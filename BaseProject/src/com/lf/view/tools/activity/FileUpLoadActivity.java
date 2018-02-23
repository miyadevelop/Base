package com.lf.view.tools.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lf.controler.tools.BitmapUtils;
import com.lf.controler.tools.FileUtils;
import com.lf.controler.tools.JSONObjectTool;
import com.lf.controler.tools.StringUtil;
import com.lf.controler.tools.download.DownloadCenter;
import com.lf.controler.tools.download.DownloadListener;
import com.lf.controler.tools.download.DownloadTask;
import com.lf.controler.tools.download.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * 文件上传界面，外界提供上传的网址，文件类型，本地文件的路径(默认是让用户自行通过文件选择器来选择)
 * Activity会显示上传进度，并且在上传成功后，通过onActivityResult来返回结果
 *
 * @author wangwei
 */
public class FileUpLoadActivity extends AppCompatActivity {

    private String mUrl;//上传网址
    //    private String mPath;//本地路径，默认通过文件浏览器选择文件
    private ArrayList<String> mPaths = new ArrayList<>();//本地路径，默认通过文件浏览器选择文件
    private String mFileType;//文件类型，默认是图片文件
    private int mWigth;//图片宽度，默认-1，不截取长宽
    private int mHeight;//图片长度，默认-1，不截取长宽
    private Boolean mISNeedScale;//图片是否要压缩

    public static final String EXTRA_URL = "URL";//上传的服务端接口地址
    public static final String EXTRA_PATH = "PATH";//单个文件上传时，要上传的文件的本地路径
    public static final String EXTRA_PATHS = "PATHS";//多个文件上传时，要上传的文件的本地路径
    public static final String EXTRA_FILE_TYPE = "FILE_TYPE";//文件类型
    public static final String EXTRA_IMAGE_WIGTH = "IMAGE_WIGTH";//图片文件要求的宽
    public static final String EXTRA_IMAGE_HEIGHT = "IMAGE_HEIGHT";//图片文件要求的高
    public static final String EXTRA_NEED_SCALE = "NEED_SCALE";//如果是图片，是否要压缩

    public static final int REQUEST_CODE = 2139;//随意定的一个返回结果的整数值，避免和别的返回值冲突，外界也可以自定义返回值
    public static final String RESULT_URL = "url";//返回结果时，上传的文件的服务器网址将会放到intent的extra中返回，url是extra的key
    public static final String RESULT_URLS = "urls";//多个返回结果时，上传的文件的服务器网址将会放到intent的extra中返回，url是extra的key
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(0x55000000);
        //设置布局
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        ProgressBar bar = new ProgressBar(this);
        bar.setId(1);
        bar.setVisibility(View.INVISIBLE);
        layout.addView(bar, lp);
        //目前底层暂时不支持传进度
        //		mProgressTextView = new TextView(this);
        //		mProgressTextView.setTextColor(0xffffffff);
        //		layout.addView(mProgressTextView,lp);
        setContentView(layout);

        mUrl = getIntent().getStringExtra(EXTRA_URL);
        if (null != getIntent().getStringArrayListExtra(EXTRA_PATHS))
            mPaths = getIntent().getStringArrayListExtra(EXTRA_PATHS);
        if (mPaths.size() == 0 && null != getIntent().getStringExtra(EXTRA_PATH))
            mPaths.add(getIntent().getStringExtra(EXTRA_PATH));
        mFileType = getIntent().getStringExtra(EXTRA_FILE_TYPE);
        if (null == mFileType)
            mFileType = "image/jpeg";
        mWigth = getIntent().getIntExtra(EXTRA_IMAGE_WIGTH, -1);
        mHeight = getIntent().getIntExtra(EXTRA_IMAGE_HEIGHT, -1);
        mISNeedScale = getIntent().getBooleanExtra(EXTRA_NEED_SCALE, false);

        if (mPaths.size() == 0)//外界没有指定上传的文件路径，需要用户手动选择文件
        {
            showFileChooser();
        } else//外界指定了上传的文件
        {
            if (mPaths.size() == 1 && null != mFileType && mFileType.startsWith("image/") && mWigth > 0 && mHeight > 0)//图片类型，需要裁剪
            {
                //裁剪图片之后，再上传
                startPhotoZoom(Uri.parse(mPaths.get(0)));
            } else {
                //直接上传图片
                uploadFile(mPaths);
                findViewById(1).setVisibility(View.VISIBLE);
            }
            findViewById(1).setVisibility(View.VISIBLE);
        }

    }


    /**
     * 上传用户的头像，可以按照指定尺寸保存
     */
    private void uploadFile(/*final String path*/ final ArrayList<String> paths) {

        new Thread() {
            @Override
            public void run() {
                super.run();

                DownloadTask task = new DownloadTask();
                RequestParams p = new RequestParams();
                p.addBodyParameter("default", "default");
                p.setRequestMethod(RequestParams.METHOD_POST);
                task.requestParams = p;
                task.mUrl = mUrl;
                task.mTag = "uploadUserPortrait" + System.currentTimeMillis();
                task.mId = "uploadUserPortrait" + System.currentTimeMillis();

                for (String path : paths) {
                    try {
                        File file = FileUtils.createFile(path);
                        ;
                        if (mISNeedScale) {//图片文件需要进行压缩
                            Bitmap bitmap = BitmapUtils.getBitmapFromSD(path, null);
                            file = FileUtils.createFile(getFilesDir().getAbsolutePath() + "/tempImage/" + file.getName());
                            if(null != bitmap) {
                                //压缩尺寸
                                if (bitmap.getWidth() > 720)
                                    bitmap = BitmapUtils.scaleWithWH(bitmap, 720, bitmap.getHeight() * 720 / bitmap.getWidth());
                                //压缩质量
                                BitmapUtils.scaleToSize(bitmap, 100, file.getAbsolutePath());
                            }
                        }
//                        else//不压缩，上传源文件
//                        {
//                            file = FileUtils.createFile(path);
//                        }
                        p.addBodyParameter(path, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                DownloadListener listener = new DownloadListener() {

                    public void onDownloadStart(DownloadTask task) {
                    }

                    public void onDownloadRefresh(DownloadTask task, final int progress) {
                    }

                    public void onDownloadPause(DownloadTask task) {
                    }

                    @Override
                    public void onDownloadOver(int flag, DownloadTask task, InputStream is) {

                        for (String path : paths) {

                            FileUtils.deleteFileOrFolder(new File(getFilesDir().getAbsolutePath() + "/tempImage/" + new File(path).getName()));
                        }
                        try {
                            if (flag != DownloadListener.SUCCESS)//上传失败
                            {
                                throw new Exception("Upload Failed!");
                            }

                            String content = StringUtil.from(is);
                            JSONObjectTool jsonObject = new JSONObjectTool(content);
                            if (!"ok".equals(jsonObject.getString("status")))//服务器返回失败
                            {
                                throw new Exception(jsonObject.getString("message"));
                            }

                            //解析上传后得到的对应各文件的网址
                            ArrayList<String> paths = new ArrayList<String>();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                if ("ok".equals(jsonObject1.getString("status"))) {
                                    jsonObject = new JSONObjectTool(jsonObject1.getJSONObject("data"));
                                    paths.add(jsonObject.getString("path"));
                                }
                            }

                            Intent intent = new Intent();
                            intent.putExtra(RESULT_URLS, paths);
                            if (paths.size() > 0)
                                intent.putExtra(RESULT_URL, paths.get(0));
                            //返回文件网址
                            setResult(RESULT_OK, intent);
                        } catch (final Exception e) {

                            mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(FileUpLoadActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //关闭界面
                        finish();
                    }
                };
                DownloadCenter.getInstance(FileUpLoadActivity.this).start(task, listener);


            }
        }.start();


    }


    private static final int FILE_SELECT_CODE = 1;

    /**
     * 选择文件
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mFileType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //		intent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            startActivityForResult(intent, FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE: //文件选择的结果
                if (resultCode == RESULT_OK) {
                    // 用户选择的图片文件
                    Uri uri = data.getData();
                    mPaths.add(uri.getPath());
                    if (null != mFileType && mFileType.startsWith("image/") && mWigth > 0 && mHeight > 0)//图片类型，需要裁剪
                    {
                        //裁剪图片之后，再上传
                        startPhotoZoom(uri);
                    } else {   //直接上传图片
                        uploadFile(mPaths);
                        findViewById(1).setVisibility(View.VISIBLE);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    setResult(RESULT_CANCELED, new Intent().putExtra(RESULT_URL, "cancel"));
                    finish();
                } else {
                    setResult(RESULT_CANCELED, new Intent().putExtra(RESULT_URL, "choose_file_failed"));
                    finish();
                }
                break;

            case PHOTO_REQUEST_CUT://获取到了截图的结果
                if (resultCode == RESULT_OK) {
//                    Bitmap bitmap = data.getParcelableExtra("data");
                    //上传图片
                    ArrayList<String> paths = new ArrayList<String>();
                    paths.add(getShortCutPath());
                    uploadFile(paths);
                    findViewById(1).setVisibility(View.VISIBLE);
                } else if (resultCode == RESULT_CANCELED) {
                    setResult(RESULT_CANCELED, new Intent().putExtra(RESULT_URL, "cancel"));
                    finish();
                } else {
                    setResult(RESULT_CANCELED, new Intent().putExtra(RESULT_URL, "cut_image_failed"));
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private static final int PHOTO_REQUEST_CUT = 2;

    /**
     * 执行截图
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        //		intent.setDataAndType(Uri.fromFile(new File(uri.getPath())), "image/*");

        try {
            if (uri.toString().startsWith("content"))//content://转为file://，解决部分手机上再截图时，无法记载图片的问题
            {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor actualimagecursor = this.managedQuery(uri, proj, null, null, null);
                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                actualimagecursor.moveToFirst();
                String img_path = actualimagecursor.getString(actual_image_column_index);
                File file = new File(img_path);
                uri = Uri.fromFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是裁剪时宽高的比例
        intent.putExtra("aspectX", mWigth);
        intent.putExtra("aspectY", mHeight);

        // outputX,outputY 是剪裁后保存的图片的宽高
        intent.putExtra("outputX", mWigth);
        intent.putExtra("outputY", mHeight);
        //以下这句在小米系统中不能调用，调用后会无法返回图片，所以使用下面将图片保存到本地的方式获取截取的图片
//        intent.putExtra("return-data", true);

        try {
            File file = new File(getShortCutPath());
            //如果文件夹不存在，则要先创建文件夹，否则会保存失败
            file.getParentFile().mkdirs();
        } catch (Exception e) {

        }

        Uri tempShortCup = Uri.parse("file://" + "/" + getShortCutPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempShortCup);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    /**
     * 根据mPath生成一个临时存储截图的地址
     */
    private String getShortCutPath() {
        String path;//创建一个临时存储截图的路径
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + getPackageName() + "/images/" + new File(mPaths.get(0)).getName();
        } else {
            String del = getFilesDir().toString().substring(0, mPaths.get(0).lastIndexOf("/"));
            path = del + "/images/" + new File(mPaths.get(0)).getName();
        }
        return path;
    }


    //{后缀名，MIME类型}
    //	{".3gp", "video/3gpp"},
    //	{".apk", "application/vnd.android.package-archive"},
    //	{".asf", "video/x-ms-asf"},
    //	{".avi", "video/x-msvideo"},
    //	{".bin", "application/octet-stream"},
    //	{".bmp", "image/bmp"},
    //	{".c", "text/plain"},
    //	{".class", "application/octet-stream"},
    //	{".conf", "text/plain"},
    //	{".cpp", "text/plain"},
    //	{".doc", "application/msword"},
    //	{".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
    //	{".xls", "application/vnd.ms-excel"},
    //	{".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
    //	{".exe", "application/octet-stream"},
    //	{".gif", "image/gif"},
    //	{".gtar", "application/x-gtar"},
    //	{".gz", "application/x-gzip"},
    //	{".h", "text/plain"},
    //	{".htm", "text/html"},
    //	{".html", "text/html"},
    //	{".jar", "application/java-archive"},
    //	{".java", "text/plain"},
    //	{".jpeg", "image/jpeg"},
    //	{".jpg", "image/jpeg"},
    //	{".js", "application/x-javascript"},
    //	{".log", "text/plain"},
    //	{".m3u", "audio/x-mpegurl"},
    //	{".m4a", "audio/mp4a-latm"},
    //	{".m4b", "audio/mp4a-latm"},
    //	{".m4p", "audio/mp4a-latm"},
    //	{".m4u", "video/vnd.mpegurl"},
    //	{".m4v", "video/x-m4v"},
    //	{".mov", "video/quicktime"},
    //	{".mp2", "audio/x-mpeg"},
    //	{".mp3", "audio/x-mpeg"},
    //	{".mp4", "video/mp4"},
    //	{".mpc", "application/vnd.mpohun.certificate"},
    //	{".mpe", "video/mpeg"},
    //	{".mpeg", "video/mpeg"},
    //	{".mpg", "video/mpeg"},
    //	{".mpg4", "video/mp4"},
    //	{".mpga", "audio/mpeg"},
    //	{".msg", "application/vnd.ms-outlook"},
    //	{".ogg", "audio/ogg"},
    //	{".pdf", "application/pdf"},
    //	{".png", "image/png"},
    //	{".pps", "application/vnd.ms-powerpoint"},
    //	{".ppt", "application/vnd.ms-powerpoint"},
    //	{".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
    //	{".prop", "text/plain"},
    //	{".rc", "text/plain"},
    //	{".rmvb", "audio/x-pn-realaudio"},
    //	{".rtf", "application/rtf"},
    //	{".sh", "text/plain"},
    //	{".tar", "application/x-tar"},
    //	{".tgz", "application/x-compressed"},
    //	{".txt", "text/plain"},
    //	{".wav", "audio/x-wav"},
    //	{".wma", "audio/x-ms-wma"},
    //	{".wmv", "audio/x-ms-wmv"},
    //	{".wps", "application/vnd.ms-works"},
    //	{".xml", "text/plain"},
    //	{".z", "application/x-compress"},
    //	{".zip", "application/x-zip-compressed"},
    //	{"", "*/*"}
}
