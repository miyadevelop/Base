package com.lf.view.tools.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lf.base.R;
import com.lf.controler.tools.download.RemoteDownloadHandle;
import com.lf.tools.log.MyLog;
import com.lf.tools.share.IShareListener;
import com.lf.tools.share.ShareBean;
import com.lf.tools.share.ShareImage;
import com.lf.tools.share.ShareManager;
import com.lf.tools.share.SharePlatform;

import java.util.Arrays;
import java.util.List;

/**
 * 网页界面，传入网址、设置标题
 * Created by wangwei on 17/11/20.
 */
public class WebActivity extends AppCompatActivity {

    public static final String EXTRA_URI = "showUri";
    protected WebView mWebView;
    private ProgressBar mProgressBar;
    private String mUri;    //需要跳转的网页地址
    private boolean isUseHtmlTitle = false;//是否显示网页的标题

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_web);

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {

        }
        mProgressBar = (ProgressBar) findViewById(R.id.web_progressbar);

        //初始化webView
        mWebView = (WebView) findViewById(R.id.web_webview);
        //dom存储是否可用，默认false，这里设置解决部分显示网页白屏的问题
        mWebView.getSettings().setDomStorageEnabled(true);
        //是否支持插件，默认的不支持
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //为了上webView自适应网页的显示，避免类似显示过大的现象
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        //设置当前浏览器的标识，以便网页端能识别当前浏览器是app内部浏览器
        mWebView.getSettings().setUserAgentString("Inner");
        //设置js支持
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaScriptObject(WebActivity.this), "myObject");
        //设置默认编码
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");

        mWebView.setBackgroundColor(0); // 设置背景色
//        mWebView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255

        //支持网页隐式打开界面
        mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                MyLog.i("Web: mUri:" + mUri);
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return false;
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                    }
                    return true;
                }
            }

        });

        // 网页加载进度
        mWebView.setWebChromeClient(new android.webkit.WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (isUseHtmlTitle) {
                    if (null != getSupportActionBar())
                        getSupportActionBar().setTitle(title);
                }
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 98)
                    mProgressBar.setVisibility(View.GONE);
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        //设置标题
        String title = getIntent().getStringExtra("title");
        // title==null则不显示标题栏 title为""则标题栏显示网页的标题，如果title既不为空也不为""，则显示title
        if (title == null) {

        } else if (title.equals("")) {
            isUseHtmlTitle = true;
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(" ");
        } else {
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(title);
        }

        mUri = getIntent().getStringExtra("showUri");
        mWebView.loadUrl(mUri);
        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                RemoteDownloadHandle.notifyDownload(WebActivity.this, url);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else
                finish();
            return true;
        } else {
            mWebView.loadUrl("javascript:onMenuClick(" + "'" + mMenu + "'" + ")");
            return true;
        }
    }


    /**
     * 和网页交互的接口
     * Created by ww
     */
    public class JavaScriptObject implements IShareListener {
        private Context mContext;

        public JavaScriptObject(Context context) {
            mContext = context;
        }


        //17以上需要调用这个方法，服务器才可以访问到本地
        @JavascriptInterface
        /**
         * 供WebView中的js回调本地的,发送广播
         * @params name 广播的名称
         * @params params 广播中的参数。样例 A:a,B:b
         */
        public void execSendBroadCast(String name, String params) {
            if (TextUtils.isEmpty(name)) {//没有广播的名称，不处理
                return;
            }
            Intent intent = new Intent();
            intent.setAction(name);
            //添加广播中的参数
            try {
                if (!TextUtils.isEmpty(params)) {
                    String[] paramsStr = params.split(",");
                    List<String> paramsArr = Arrays.asList(paramsStr);
                    for (String itemStr : paramsArr) {//遍历每一项，把值添加近广播中
                        String[] keyAndValue = itemStr.split(":");
                        intent.putExtra(keyAndValue[0], keyAndValue[1]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mContext.sendBroadcast(intent);
        }

        /**
         * WebView通过js回调本地，打开某个Acticity
         *
         * @param scheme 隐式打开的url
         */
        @JavascriptInterface
        public void execOpenActivity(final String scheme) {
            //这里需要同步到ui线程进行处理，否则会崩溃
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(scheme)) {//没有广播的名称，不处理
                        return;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
                    mContext.startActivity(intent);
                }
            });
        }


        Handler mHandler = new Handler();

        /**
         * 执行分享
         *
         * @param title    标题
         * @param imageUrl 分享的图片的网址
         * @param url      点击分享内容跳转的网址
         * @param content  副标题
         */
        @JavascriptInterface
        public void share(final String title, final String imageUrl, final String url,
                          final String content) {
            //这里需要同步到ui线程进行处理，否则会崩溃
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ShareBean bean = new ShareBean();
                    bean.setUrl(url);
                    bean.setImage(new ShareImage(mContext, imageUrl));
                    bean.setContent(content);
                    bean.setTitle(title);
                    bean.setContent(content);
                    ShareManager.getInstance((Activity) mContext).openShare((Activity) mContext, bean, JavaScriptObject.this);
                }
            });
        }


        /**
         * 关闭WebActivity
         *
         * @return
         */
        @JavascriptInterface
        public void closeWeb() {
            finish();
        }


        /**
         * 拷贝文字内容
         *
         * @param text 要复制的文字
         */
        @JavascriptInterface
        public void copy(String text) {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            ClipData mClipData = ClipData.newPlainText("Label", text);
            cm.setPrimaryClip(mClipData);
            Toast.makeText(WebActivity.this, "Copy Success !", Toast.LENGTH_LONG).show();
        }


        /**
         * 获取浏览器名称
         *
         * @return
         */
        @JavascriptInterface
        public String getExplorerName() {
            return "Inner";
        }


        /**
         * 获取浏览器版本
         *
         * @return
         */
        @JavascriptInterface
        public String getExplorerVersion() {
            return "1.0";
        }


        /**
         * 打开登录界面
         */
        @JavascriptInterface
        public void login() {
            Intent intent = new Intent();
            intent.setData(Uri.parse("my://www.doubiapp.com/login.json?from=web"));
            startActivity(intent);
        }


        /**
         * 弹出文字提示
         */
        @JavascriptInterface
        public void alert(String text) {
            Toast.makeText(WebActivity.this, text, Toast.LENGTH_LONG).show();
        }


        /**
         * 开始分享
         */
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(SharePlatform platform) {
            mWebView.loadUrl("javascript:onShareSuccess(" + "'" + platform.name() + "'" + ")");
        }

        @Override
        public void onFail(SharePlatform platform) {
            mWebView.loadUrl("javascript:onShareFailed(" + "'" + platform.name() + "'" + ")");
        }


        /**
         * 隐藏标题栏
         *
         * @param hide true隐藏，false显示
         */
        @JavascriptInterface
        public void hideActionBar(final boolean hide) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (null == getSupportActionBar())
                        return;
                    if (hide)
                        getSupportActionBar().hide();
                    else
                        getSupportActionBar().show();
                }
            });
        }


        /**
         * 设置右上角图标
         *
         * @param menu 右上角图标值，支持的值：share、more、camera、send、search、help、add
         */
        @JavascriptInterface
        public void setMenu(final String menu) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mMenu = menu;
                    WebActivity.this.invalidateOptionsMenu();
                }
            });
        }
    }


    String mMenu;

    @TargetApi(26)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if ("share".equals(mMenu))
            menu.add(0, 0, 0, "").setIcon(android.R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        else if ("more".equals(mMenu))
            menu.add(0, 0, 0, "").setIcon(android.R.drawable.ic_menu_more).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        else if ("camera".equals(mMenu))
            menu.add(0, 0, 0, "").setIcon(android.R.drawable.ic_menu_camera).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        else if ("send".equals(mMenu))
            menu.add(0, 0, 0, "").setIcon(android.R.drawable.ic_menu_send).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        else if ("search".equals(mMenu))
            menu.add(0, 0, 0, "").setIcon(android.R.drawable.ic_menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        else if ("help".equals(mMenu))
            menu.add(0, 0, 0, "").setIcon(android.R.drawable.ic_menu_help).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        else if ("add".equals(mMenu))
            menu.add(0, 0, 0, "").setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mWebView.loadUrl("javascript:onResume()");
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();//返回上一页面
        } else
            super.onBackPressed();
    }
}
