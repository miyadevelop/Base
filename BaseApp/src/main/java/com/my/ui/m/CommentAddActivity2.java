package com.my.ui.m;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lf.app.App;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.view.tools.WaitDialog;
import com.my.app.R;
import com.my.m.comment.Comment;
import com.my.m.comment.CommentAddLoader;
import com.my.ui.BaseActivity;

/**
 * 文章等内容的发表评论界面，背景透明
 * Created by wangwei on 17/11/14.
 */
public class CommentAddActivity2 extends BaseActivity implements TextView.OnEditorActionListener {


    private Comment mComment = new Comment();
    public final static String EXTRA_CALLBACK_NAME = "name";
    public final static String EXTRA_CALLBACK_ID = "id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_comment_add2);

        EditText editText = (EditText) findViewById(R.id.comment_add_edit_text);
        editText.setOnEditorActionListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(CommentAddLoader.getInstance().getAction());
        registerReceiver(mReceiver, filter);

    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            addComment(null);
            return true;
        }
        return false;
    }


    /**
     * 提交评论
     */
    public void addComment(View view) {

        mComment.src_id = getIntent().getStringExtra(EXTRA_CALLBACK_NAME) + "_" + getIntent().getStringExtra(EXTRA_CALLBACK_ID);

        EditText editText = (EditText) findViewById(R.id.comment_add_edit_text);
        String text = editText.getText().toString();
        if (TextUtils.isEmpty(text))//允许评论为空
        {
            mComment.text = "null";
        } else
            mComment.text = text;
        WaitDialog.show(this, "", true);
        CommentAddLoader.getInstance().commitComment(mComment, String.valueOf(System.currentTimeMillis()), getIntent().getStringExtra(EXTRA_CALLBACK_NAME), getIntent().getStringExtra(EXTRA_CALLBACK_ID));
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isSuccess = intent.getBooleanExtra(NetLoader.STATUS, false);
            if (action.equals(CommentAddLoader.getInstance().getAction())) {
                WaitDialog.cancel(CommentAddActivity2.this);

                if (isSuccess) {
                    Toast.makeText(App.mContext, R.string.comment_add_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else
                    Snackbar.make(findViewById(R.id.root), intent.getStringExtra(NetLoader.MESSAGE), Snackbar.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.base_slide_bottom_out);
    }


    public void finish(View view)
    {
        finish();
    }
}
