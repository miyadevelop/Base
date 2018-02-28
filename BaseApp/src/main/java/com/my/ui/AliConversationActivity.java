package com.my.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMConversationListUI;
import com.alibaba.mobileim.channel.util.WXUtil;
import com.alibaba.mobileim.conversation.IYWConversationListener;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationCreater;
import com.alibaba.mobileim.lib.model.message.Message;
import com.alibaba.mobileim.ui.IYWConversationFragment;
import com.lf.view.tools.FontHelper;
import com.my.app.R;
import com.my.m.im.ImManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 17/9/22.
 */
public class AliConversationActivity extends IMConversationListUI {

    Fragment mFragment;

    @Override
    public void onActivityCreated(Bundle savedInstanceState, Fragment fragment) {
        super.onActivityCreated(savedInstanceState, fragment);

        mFragment = fragment;

        final YWIMKit mIMKit = ImManager.getInstance().getIMKit();

        if (null != mIMKit)
            mIMKit.getIMCore().getConversationService().addConversationListener(mListener);

    }

    public AliConversationActivity(Pointcut pointcut) {
        super(pointcut);
    }


    /**
     * 返回会话列表的自定义标题
     *
     * @param fragment
     * @param context
     * @param inflater
     * @return
     */
    @Override
    public View getCustomConversationListTitle(final Fragment fragment,
                                               final Context context, LayoutInflater inflater) {
        //TODO 重要：必须以该形式初始化customView---［inflate(R.layout.**, new RelativeLayout(context),false)］------，以让inflater知道父布局的类型，否则布局xml**中定义的高度和宽度无效，均被默认的wrap_content替代
        RelativeLayout customView = (RelativeLayout) inflater
                .inflate(R.layout.base_layout_title_2, new RelativeLayout(context), false);
        TextView title = (TextView) customView.findViewById(R.id.titilebar_text_title);
        title.setText(R.string.chat_conversation_title);
//        final YWIMKit mIMKit = ImManager.getInstance().getIMKit();
//
//        if (null == mIMKit) {
//            title.setText("未登录");
//        } else {
//            final String loginUserId = mIMKit.getIMCore().getLoginUserId();
//            final String appKey = mIMKit.getIMCore().getAppKey();
//            if (TextUtils.isEmpty(loginUserId) || TextUtils.isEmpty(appKey)) {
//                title.setText("未登录");
//            }
//        }

        FontHelper.applyFont(fragment.getContext(), customView, "app_font.ttf");
        return customView;
    }


    @Override
    public boolean isNeedRoundRectHead() {
        return true;
    }


    private IYWConversationListener mListener = new IYWConversationListener() {
        @Override
        public void onItemUpdated() {
            try {

                YWIMKit mIMKit = ImManager.getInstance().getIMKit();
                if (null != mIMKit) {

                    //发送默认消息
                    sendFeedbackMesssage();

                    List<YWConversation> conversations = mIMKit.getConversationService().getConversationList();
                    if (null == conversations || 0 == conversations.size()) {
                        mFragment.getView().findViewById(com.alibaba.sdk.android.R.id.no_message).setVisibility(View.VISIBLE);
                    } else {
                        mFragment.getView().findViewById(com.alibaba.sdk.android.R.id.no_message).setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {

            }
        }
    };


    @Override
    public void onInitFinished(IYWConversationFragment fragment) {
        super.onInitFinished(fragment);
        try {

            YWIMKit mIMKit = ImManager.getInstance().getIMKit();
            if (null != mIMKit) {
                List<YWConversation> conversations = mIMKit.getConversationService().getConversationList();
                if (null == conversations || 0 == conversations.size()) {
                    mFragment.getView().findViewById(com.alibaba.sdk.android.R.id.no_message).setVisibility(View.VISIBLE);
                } else {
                    mFragment.getView().findViewById(com.alibaba.sdk.android.R.id.no_message).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {

        }
    }


    /*
    查看当前的会话，判断四个客服会话是否存在，不在创建（发送默认消息）
    问题反馈 白菜券app
     */
    private void sendFeedbackMesssage() {

        if(null == ImManager.getInstance().getIMKit())
            return;

        // 获取会话管理类
        List<String> needSend = new ArrayList<>();
        needSend.add("1500000000000001");
        IYWConversationService conversationService = ImManager.getInstance().getIMKit().getConversationService();
        //获取最近会话列表
        List<YWConversation> list = conversationService.getConversationList();
        for (YWConversation coner : list) {
            for (String s : needSend) {
                if (coner.getConversationId().contains(s)) {
                    needSend.remove(s);
                    break;
                }
            }
        }

        for (String s : needSend) {
            Message message = new Message();
            message.setMsgId(WXUtil.getUUID());
            message.setSubType(-3);//警示消息的常量值
            message.setContent("Hello！欢迎来到这里！周一到周五的9:00-17:00，任何问题都可以在这里提问哟。");
            message.setNeedSave(true);
            message.setIsLocal(true);//设置为本地消息

            final YWConversationCreater conversationCreater =
                    ImManager.getInstance().getIMKit().getConversationService().getConversationCreater();

//            EServiceContact contact = new EServiceContact("白菜券app", 0);
            YWConversation conversation;
//            if (s.equals("白菜券app"))
//                conversation = conversationCreater.createConversationIfNotExist(contact);
//            else
            conversation = conversationCreater.createConversationIfNotExist(s);
            //发送消息
            conversation.getMessageSender().sendMessage(message, 120, null);

        }
    }
}
