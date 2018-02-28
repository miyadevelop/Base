package com.my.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lf.app.App;
import com.lf.controler.tools.Config;
import com.lf.controler.tools.download.helper.LoadParam;
import com.lf.tools.datacollect.DataCollect;
import com.lf.view.tools.EditTextHelper;
import com.lf.view.tools.FlowLayout;
import com.my.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class BaseSearchActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextView.OnEditorActionListener, FlowLayout.OnItemClickListener {


    private SearchHistory mSearchHistory;//搜索历史
    private Fragment mFragment;
    private SearchHot mSearchHot;//搜索热词

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_search);

        //输入框初始化
        EditText editText = (EditText) findViewById(App.id("edit_edit"));
        editText.setOnEditorActionListener(this);
        editText.setOnClickListener(this);
        editText.addTextChangedListener(mTextWatcher);
        String title = getIntent().getStringExtra("title");
        if (null != title && title.length() > 0)
            editText.setHint(title);
        //清理按键的响应、显隐
        EditTextHelper.init(this, findViewById(App.id("search_layout_search")));

        mSearchHot = new SearchHot();
        FlowLayout flKeyword = (FlowLayout) findViewById(R.id.search_list_hot);
        flKeyword.setFlowLayout(mSearchHot.get(), this);

        mSearchHistory = new SearchHistory();

        refreshHistroy();

        mFragment = initFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.search_layout_content, mFragment).commit();

        //初始化，获取外面传入的参数，从而加载显示的内容
        if (null != getIntent().getData())//外面有传入参数，则执行加载
        {
            handler.postDelayed(mAutoLoadRunable, 300);
            findViewById(R.id.search_layout_search_word).setVisibility(View.GONE);
            findViewById(R.id.search_layout_content).setVisibility(View.INVISIBLE);
        } else//否则显示搜索界面
        {
            showSearch();
        }
    }

    Runnable mAutoLoadRunable = new Runnable() {
        @Override
        public void run() {
            //初始化，获取外面传入的参数，从而加载显示的内容
            LoadParam loadParam = new LoadParam();
            loadParam.addParams(getIntent().getData());
            if (loadParam.getParams().size() > 0)//外面有传入参数，则执行加载
            {
                goToLoad(loadParam, true);
            }
        }
    };


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

        if (adapterView.getId() == App.id("search_list_history"))//点击了搜索历史
        {
            DataCollect.getInstance(App.mContext).addEvent(this, "click_search_history");
            String word = (String) adapterView.getAdapter().getItem(position);
            EditText editText = (EditText) findViewById(App.id("edit_edit"));
            editText.setText(word);
            //隐藏键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public void onItemClick(String content) {
        DataCollect.getInstance(App.mContext).addEvent(this, "click_search_history");
        EditText editText = (EditText) findViewById(App.id("edit_edit"));
        editText.setText(content);
        //隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
//        if (view.getId() == App.id("search_button_search"))//点搜索按钮
//        {
//            goToSearch(true);
//        } else
        if (view.getId() == App.id("edit_edit"))//点搜索框
        {
            EditText editText = (EditText) findViewById(App.id("edit_edit"));
            editText.setHint(App.string("search_search_hint"));//重置hint
            showSearch();
        }
    }


    /**
     * 清空搜索历史
     * @param view
     */
    public void clearHistory(View view) {
        mSearchHistory.clear(App.mContext);
        refreshHistroy();
    }


    /**
     * 刷新搜索历史
     */
    private void refreshHistroy() {
        View searchHistoryListView = findViewById(App.id("search_layout_history"));
        if (mSearchHistory.getHistory(App.mContext).size() == 0)
            searchHistoryListView.setAlpha(0);
        else
            searchHistoryListView.setAlpha(1);
        FlowLayout flKeyword = (FlowLayout) findViewById(R.id.search_list_history);
        flKeyword.setFlowLayout(mSearchHistory.getHistory(App.mContext), this);
    }

    /**
     * 根据输入框的内容和排序，执行搜索，并修改界面的显示
     */
    private void goToSearch(boolean hideInput) {
        EditText editText = (EditText) findViewById(App.id("edit_edit"));
        String keyWord = editText.getText().toString();

        handler.removeCallbacks(mAddHistoryRunnable);
        if (null == keyWord || keyWord.length() == 0 || keyWord.equals(" "))
            return;
        //添加搜索历史
        handler.postDelayed(mAddHistoryRunnable, 5000);

        LoadParam loadParam = new LoadParam();
//        try {
//            keyWord = URLEncoder.encode(keyWord, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        //将空格改为下划线，以免在搜索时出错
        loadParam.addParams("word", keyWord.replaceAll(" ", "_"));
        loadParam.addParams("from_where", "search");
        goToLoad(loadParam, hideInput);
    }


    /**
     * 用runnable更新搜索历史，主要是为了防止输入到一半时自动搜索的词进入到搜索历史中
     */
    private Runnable mAddHistoryRunnable = new Runnable() {

        @Override
        public void run() {
            DataCollect.getInstance(App.mContext).addEvent(BaseSearchActivity.this, "search");
            EditText editText = (EditText) findViewById(App.id("edit_edit"));
            String keyWord = editText.getText().toString();
            mSearchHistory.addHistory(App.mContext, keyWord);
        }
    };


    /**
     * 加载数据，隐藏搜索
     */
    private void goToLoad(/*String from, */LoadParam loadParam, boolean hideInput) {
        //隐藏搜索历史、搜索热词
        findViewById(R.id.search_layout_search_word).setVisibility(View.GONE);

        findViewById(R.id.search_layout_content).setVisibility(View.VISIBLE);
        ((LoadInterface) mFragment).goToLoad(loadParam);

        if (hideInput) {
            //隐藏键盘
            EditText editText = (EditText) findViewById(App.id("edit_edit"));
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            //显示键盘
            EditText editText = (EditText) findViewById(App.id("edit_edit"));
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    };


    /**
     * 显示搜索并隐藏数据的列表
     */
    private void showSearch() {

        //显示搜索历史、搜索热词
        View searchHistoryListView = findViewById(App.id("search_layout_search_word"));
        searchHistoryListView.setVisibility(View.VISIBLE);
        refreshHistroy();
        findViewById(R.id.search_layout_content).setVisibility(View.INVISIBLE);
        //显示键盘，目前测试会自动弹出键盘，手动弹出将会导致finish的时候，键盘无法关闭，所以暂时隐去
//        handler.postDelayed(runnable, 200);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //点击了键盘上的搜索按钮
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            goToSearch(true);
            return true;
        }
        ;
        return false;
    }


//    @Override
//    public void onPageSelected(int position) {
//        super.onPageSelected(position);
//    }


    /**
     * 监听输入框文字变化，自动进行搜索，无需点击搜索按钮
     */
    public TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            try {
                //删除输入框中的表情符号
                EditText editText = (EditText) findViewById(App.id("edit_edit"));
                Editable edit = editText.getText();
                while (edit.length() > 0 && isEmojiCharacter(edit.charAt(edit.length() - 1))) {
                    edit.delete(edit.length() - 1, edit.length());
                }
            } catch (Exception e) {
            }

            handler.removeCallbacks(mAutoSearchRunnable);
            handler.postDelayed(mAutoSearchRunnable, 500);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    Runnable mAutoSearchRunnable = new Runnable() {

        @Override
        public void run() {
            goToSearch(false);
        }
    };


    /**
     * 执行搜索的时候，会调用LoadInterface.goToLoad,子类只需将Fragment实现这个接口LoadInterface
     */
    public interface LoadInterface {
        public void goToLoad(LoadParam loadParam);
    }


    /**
     * 初始化展示搜索内容的fragment
     * 这里要注意：fragment的onCreateView中 inflater.inflate的参数viewGroup要设置为null，否则会报错
     *
     * @return
     */
    public abstract Fragment initFragment();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(mAddHistoryRunnable);
        handler.removeCallbacks(mAutoSearchRunnable);
    }


    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }


    /**
     * 搜索记录
     *
     * @author wangwei
     */
    public static class SearchHistory {


        public ArrayList<String> mWords;
        private static final String TAG = "search_history";

        public void addHistory(Context context, String word) {
            if (null == mWords)
                mWords = getDataList(context, TAG);

            if (mWords.contains(word))//将该搜索词放在第一个
                mWords.remove(word);
            mWords.add(0, word);

            setDataList(context, TAG, mWords);
        }


        public ArrayList<String> getHistory(Context context) {
            if (null == mWords)
                mWords = getDataList(context, TAG);
            return mWords;
        }


        public void clear(Context context) {
            if (null == mWords)
                mWords = new ArrayList<String>();
            else
                mWords.clear();
            setDataList(context, TAG, mWords);
        }


        /**
         * 保存List
         *
         * @param tag
         * @param datalist
         */
        public static <T> void setDataList(Context context, String tag, ArrayList<T> datalist) {
            if (null == datalist)
                return;
            SharedPreferences.Editor editor = context.getSharedPreferences(tag, Context.MODE_PRIVATE).edit();
            Gson gson = new Gson();
            //转换成json数据，再保存
            String strJson = gson.toJson(datalist);
            editor.clear();
            editor.putString(tag, strJson);
            editor.commit();

        }

        /**
         * 获取List
         *
         * @param tag
         * @return
         */
        public static <T> ArrayList<T> getDataList(Context context, String tag) {
            ArrayList<T> datalist = new ArrayList<T>();
            try {
                SharedPreferences preferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
                String strJson = preferences.getString(tag, null);
                if (null == strJson) {
                    return datalist;
                }
                Gson gson = new Gson();
                datalist = gson.fromJson(strJson, new TypeToken<ArrayList<T>>() {
                }.getType());
            } catch (Exception e) {

            }
            return datalist;

        }
    }


    /**
     * 搜索记录
     *
     * @author wangwei
     */
    public static class SearchHot {


        public List<String> get()
        {
            String words = Config.getConfig().getString("hot_words","AJ,Kobe,Nike,Zoom");
            String[] arr = words.split(",");
            return Arrays.asList(arr);
        }

    }

}
