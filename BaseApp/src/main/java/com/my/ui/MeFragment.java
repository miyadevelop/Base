package com.my.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lf.app.App;
import com.lf.controler.tools.SoftwareData;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.view.tools.Bean2View;
import com.lf.view.tools.FontHelper;
import com.my.app.R;
import com.my.m.user.User;
import com.my.m.user.UserManager;

/**
 * 我的界面
 * Created by wangwei on 17/9/22.
 */
public class MeFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (UserManager.getInstance(App.mContext).isLogin()) {
            User user = UserManager.getInstance(App.mContext).getUser();
            ImageView headImage = (ImageView) getView().findViewById(R.id.icon_url);
            headImage.setImageResource(R.drawable.ic_account_circle_black_48dp);
            Bean2View.show(getContext(), user, getView().findViewById(R.id.me_layout_head));
        } else {
            ImageView headImage = (ImageView) getView().findViewById(R.id.icon_url);
            headImage.setImageResource(R.drawable.ic_account_circle_black_48dp);
            TextView nameText = (TextView) getView().findViewById(R.id.name);
            nameText.setText(R.string.me_unlogin);

        }


        TextView updateText = getView().findViewById(R.id.me_text_update);
        updateText.setText(SoftwareData.getMetaData("UMENG_CHANNEL", getContext())+ " " +SoftwareData.getVersionName(getContext()));

        IntentFilter filter = new IntentFilter();
        filter.addAction(UserManager.getInstance(App.mContext).getUpdateUserAction());
        filter.addAction(UserManager.getInstance(App.mContext).getLoginAction());
        filter.addAction(UserManager.getInstance(App.mContext).getRegistAndLoginAction());
        filter.addAction(UserManager.getInstance(App.mContext).getRegistAction());
        getContext().registerReceiver(mReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = View.inflate(getContext(), R.layout.base_activity_me, null);
        FontHelper.applyFont(getContext(), contentView, FontHelper.APP_FONT);
        return contentView;
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            boolean isSuccess = intent.getBooleanExtra(NetLoader.STATUS, false);

            if (action.equals(UserManager.getInstance(App.mContext).getUpdateUserAction())
                    || action.equals(UserManager.getInstance(App.mContext).getLoginAction())
                    || action.equals(UserManager.getInstance(App.mContext).getRegistAction())
                    || action.equals(UserManager.getInstance(App.mContext).getRegistAndLoginAction()))//登录或者更新了用户信息
            {
                if (isSuccess)//登录成功
                {
                    User user = UserManager.getInstance(App.mContext).getUser();
                    Bean2View.show(getContext(), user, getView().findViewById(R.id.me_layout_head));
                }
            }

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver);
    }

}
