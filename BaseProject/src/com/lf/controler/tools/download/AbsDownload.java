package com.lf.controler.tools.download;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.lf.controler.tools.NetWorkManager;
import com.lf.controler.tools.StringUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 下载线程的基类
 *
 * @author LinChen
 */
public abstract class AbsDownload extends Thread {
    protected DownloadListener downloadListener;
    protected DownloadTask downloadTask;
    protected RunOverListener runOverListener;

    protected String downloadPath;

    protected long downloadLength = 0l;
    private boolean isPause;

    protected volatile boolean isRunning;

    protected Context context;

    private HttpURLConnection connection;

    private static Object cookieLock = new Object();

    public AbsDownload(Context context, DownloadTask task, DownloadListener downloadListener,
                       RunOverListener runOverListener) {
        this.context = context.getApplicationContext();
        String name = "";
        if (task.mIsBreakPoint) {
            name = "断点下载线程";
        } else {
            if (task.mIsSimple) {
                name = "简单下载线程";
            } else {
                name = "复杂下载线程";
            }
        }
        setName(name);
        this.downloadTask = task;
        this.downloadListener = downloadListener;
        this.runOverListener = runOverListener;

        downloadPath = downloadTask.mPath;
        if (downloadPath != null) {
            // 添加一个缓存路径，以后还没有下载完成就去获取下载的内容
            downloadPath = downloadPath + ".bu";
        }
    }

    /**
     * 下载结束后的操作，将备份文件还原
     */
    protected void runOver(int overFlag) {
        if (overFlag == DownloadListener.SUCCESS) {
            if (downloadPath != null) {
                // 将原来缓存的文件还原
                File srcFile = new File(downloadPath);
                File toFile = new File(downloadPath.substring(0, downloadPath.length() - 3));

                if (srcFile.exists()) {
                    srcFile.renameTo(toFile);
                }
                downloadPath = toFile.toString();
            }
            if (((downloadTask.cookieStatus & DownloadTask.COOKIE_WRITEABLE) == DownloadTask.COOKIE_WRITEABLE)
                    && downloadTask.cookiePath != null) {
                List<String> cookies = connection.getHeaderFields().get("set-cookie");

                if (cookies != null && cookies.size() > 0) {
                    synchronized (cookieLock) {
                        try {
                            File cookieFile = new File(downloadTask.cookiePath);
                            cookieFile.getParentFile().mkdirs();
                            FileWriter writer = new FileWriter(cookieFile);
                            for (int i = 0; i < cookies.size(); ++i) {
                                writer.write(cookies.get(i));
                                if (i != (cookies.size() - 1)) {
                                    writer.write("; ");
                                }
                            }
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //以下代码可以将cookie传递给webview
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        CookieSyncManager.createInstance(context);
                    }
                    String cookie = connection.getHeaderField("set-cookie");
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.setCookie("http://" + connection.getURL().getHost(), cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        CookieSyncManager.getInstance().sync();
                    }
                }
            }
        }
    }

    @Override
    public synchronized void start() {
        if (NetWorkManager.getInstance(context).isConnect()) {
            isRunning = true;
            super.start();
        } else {
            downloadListener.onDownloadOver(DownloadListener.DOWNLOAD_ERR, downloadTask, null);
            runOverListener.onRunOver(this);
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        isPause = true;
    }

    /**
     * 暂停后继续
     */
    public void keepOn() {
        synchronized (this) {
            isPause = false;
            this.notifyAll();
        }
    }

    /**
     * 中断
     */
    public void cancel() {
        interrupt();
    }

    public boolean isPause() {
        return isPause;
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected synchronized HttpURLConnection getConn() throws IOException {

        // javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        // javax.net.ssl.TrustManager tm = new SSLTrustManager();
        // trustAllCerts[0] = tm;
        // javax.net.ssl.SSLContext sc = null;
        // try {
        // sc = javax.net.ssl.SSLContext.getInstance("SSL");
        // sc.init(null, trustAllCerts, null);
        // } catch (NoSuchAlgorithmException e) {
        // e.printStackTrace();
        // } catch (KeyManagementException e) {
        // e.printStackTrace();
        // }
        // if (sc != null) {
        // javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // }
        // HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier) tm);

        String url = downloadTask.mUrl;
        RequestParams requestParams = downloadTask.requestParams;
        if (url != null && !"".equals(url.trim())) {
            URL res = null;
            if (requestParams != null
                    && requestParams.getContentType() != null
                    && requestParams.getContentType().startsWith(
                    RequestParams.CONTENT_TYPE_UPLOAD_FILE)) {// 在上传文件的时候改一下url（最好是能在上传的时候把参数加上，暂时不知道如何实现）
                if (url.indexOf("?") == -1) {
                    url += "?" + requestParams.getQueryString();
                } else {
                    url += "&" + requestParams.getQueryString();
                }
            }
            res = new URL(url);
            connection = (HttpURLConnection) res.openConnection();
            connection.setConnectTimeout((int) downloadTask.mTimeout);
            connection.setReadTimeout((int) downloadTask.mTimeout);
            connection.setRequestProperty("Charset", "utf-8");
            if (((downloadTask.cookieStatus & DownloadTask.COOKIE_READABLE) == DownloadTask.COOKIE_READABLE)
                    && downloadTask.cookiePath != null) {
                File file = new File(downloadTask.cookiePath);
                if (file.exists()) {
                    synchronized (cookieLock) {
                        String cookie = StringUtil.from(file);
                        connection.setRequestProperty("Cookie", cookie);
                    }
                }
            }
            //ww 修改于 16.10.12，添加了以下一个判断及其括号中的内容用于给下载添加头部参数
            if (null != downloadTask.head && downloadTask.head.size() > 0) {
                Iterator<Entry<String, String>> iter = downloadTask.head.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, String> entry = iter.next();
                    String key = entry.getKey();
                    String value = entry.getValue();
                    connection.setRequestProperty(key, value);
                }
            }

            // 如果有requestParams，优先
            if (requestParams != null) {
                String contentType = requestParams.getContentType();
                if (contentType != null) {
                    connection.setRequestProperty("Content-Type", contentType);
                }
                connection.setRequestMethod(requestParams.getRequestMethod());
                if (contentType != null
                        && contentType.startsWith(RequestParams.CONTENT_TYPE_UPLOAD_FILE)) {// 上传文件，目前只支持上传一个文件
                    String PREFIX = "--", LINE_END = "\r\n";
                    if (!requestParams.getFileMap().isEmpty()) {
                        connection.setDoOutput(true);
                        //=======旧代码注释掉开始=======
//						OutputStream os = connection.getOutputStream();
//						StringBuilder sb = new StringBuilder();
//						sb.append(PREFIX);
//						sb.append(requestParams.getBoundary());
//						sb.append(LINE_END);
//						/**
//						 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的
//						 * 比如:abc.png
//						 */
//						Entry<String, File> entry = requestParams.getFileMap().entrySet()
//								.iterator().next();
//						sb.append("Content-Disposition: form-data; name=\"" + entry.getKey()
//								+ "\"; filename=\"");
//						sb.append(entry.getValue().getName());
//						sb.append("\"");
//						sb.append(LINE_END);
//						sb.append("Content-Type: application/octet-stream; charset=");
//						sb.append("utf-8").append(LINE_END).append(LINE_END);
//						os.write(sb.toString().getBytes());
//
//						DataInputStream dis = new DataInputStream(new FileInputStream(
//								entry.getValue()));
//						byte[] buffer = new byte[1024];
//						int len = 0;
//						while ((len = dis.read(buffer)) != -1) {
//							os.write(buffer, 0, len);
//						}
//						dis.close();
//
//						os.write(LINE_END.getBytes());
//						os.write(LINE_END.getBytes());
//						byte[] end_data = (PREFIX + requestParams.getBoundary() + PREFIX + LINE_END)
//								.getBytes();
//						os.write(end_data);
//						os.flush();
//						os.close();
//=======旧代码注释掉结束=======
                        //============多文件上传开始===========

                        DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
//						outStream.write(sb.toString().getBytes());
                        // 发送文件数据
                        if (!requestParams.getFileMap().isEmpty())
                            for (Map.Entry<String, File> file : requestParams.getFileMap().entrySet()) {
                                StringBuilder sb1 = new StringBuilder();
                                sb1.append(PREFIX);
                                sb1.append(requestParams.getBoundary());
                                sb1.append(LINE_END);
                                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                                        + file.getValue().getName() + "\"" + LINE_END);
//                    sb1.append("Content-Disposition: form-data; name=\"" + "file" + "\"" + LINEND);
                                sb1.append("Content-Type: application/octet-stream; charset=");
                                sb1.append("utf-8").append(LINE_END).append(LINE_END);
                                outStream.write(sb1.toString().getBytes());
                                InputStream is = new FileInputStream(file.getValue());
                                byte[] buffer = new byte[1024];
                                int len = 0;
                                while ((len = is.read(buffer)) != -1) {
                                    outStream.write(buffer, 0, len);
                                }
                                is.close();
                                outStream.write(LINE_END.getBytes());
                            }
                        // 请求结束标志
                        byte[] end_data = (PREFIX + requestParams.getBoundary() + PREFIX + LINE_END).getBytes();
                        outStream.write(end_data);
                        outStream.flush();
                        outStream.close();
                        //============多文件上传结束===========

                    }
                } else {
                    OutputStream os = connection.getOutputStream();
                    os.write(requestParams.getQueryString().getBytes());
                    os.flush();
                    os.close();
                }

            } else {
                if (downloadTask.isPost
                        || RequestParams.METHOD_POST.equals(downloadTask.requestMethod)) {
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    OutputStream os = connection.getOutputStream();
                    os.write(downloadTask.queryString.getBytes());
                    os.flush();
                    os.close();
                } else {
                    if (downloadTask.requestMethod != null) {
                        connection.setRequestMethod(downloadTask.requestMethod);
                    }
                }
            }
            return connection;
        } else {
            return null;
        }
    }

    /**
     * 监听线程的下载量
     *
     * @param length 当前线程的总下载量
     */
    protected void onDownloadLength(long length) {
    }

    /**
     * 下载文件的存储
     *
     * @param is       原始流
     * @param file     保存的位置，可以为null
     * @param isReuse  是否需要返回InputStream
     * @param isAppend 是否在是有文件的基础上追加内容，用于断点下载
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    protected InputStream save(InputStream is, File file, boolean isReuse, boolean isAppend)
            throws IOException, InterruptedException {
        if (is == null)
            return null;
        byte[] buffer = new byte[1 * 1024];
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (file != null) {
            fos = new FileOutputStream(file, isAppend);
            bos = new BufferedOutputStream(fos);
        }
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            if (isInterrupted()) {
                throw new InterruptedException();
            }
            synchronized (this) {
                if (isPause) {
                    downloadListener.onDownloadPause(downloadTask);
                    this.wait();
                }
                downloadLength += len;
                onDownloadLength(downloadLength);
                if (file != null) {
                    bos.write(buffer, 0, len);
                }
                if (isReuse)
                    baos.write(buffer, 0, len);
            }
        }
        if (file != null) {
            bos.flush();
            bos.close();
            fos.flush();
            fos.close();
        }
        if (isReuse) {
            byte[] content = baos.toByteArray();
            baos.close();
            return new ByteArrayInputStream(content);
        } else {
            return null;
        }

    }

    public interface RunOverListener {
        public void onRunOver(AbsDownload thread);
    }

    public DownloadTask getTask() {
        return downloadTask;
    }

    public void release() {
        downloadTask.release();
    }

    public class SSLTrustManager implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager, HostnameVerifier {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        @Override
        public boolean verify(String urlHostName, SSLSession session) { // 允许所有主机
            return true;
        }
    }

}
