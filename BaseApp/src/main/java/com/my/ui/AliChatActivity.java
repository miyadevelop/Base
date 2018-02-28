package com.my.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMChattingPageUI;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.alibaba.mobileim.conversation.YWP2PConversationBody;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lf.view.tools.FontHelper;
import com.lf.view.tools.QMUIStatusBarHelper;
import com.my.app.R;
import com.my.m.im.ImManager;

import org.json.JSONObject;

/**
 * Created by wangwei on 17/9/22.
 */
public class AliChatActivity extends IMChattingPageUI {


    public final static String EXTRA_CUSTOM_MSG_TO_SEND = "custom_msg";

    public AliChatActivity(Pointcut pointcut) {
        super(pointcut);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState, final Fragment fragment, final YWConversation conversation) {
        super.onActivityCreated(savedInstanceState, fragment, conversation);
        QMUIStatusBarHelper.setStatusBarLightMode(fragment.getActivity());
    }

    /**
     * isv需要返回自定义的view. openIMSDK会回调这个方法，获取用户设置的view. Fragment 聊天界面的fragment
     */
    @Override
    public View getCustomTitleView(final Fragment fragment,
                                   final Context context, LayoutInflater inflater,
                                   final YWConversation conversation) {
        // 单聊和群聊都会使用这个方法，所以这里需要做一下区分
        // 本demo示例是处理单聊，如果群聊界面也支持自定义，请去掉此判断
        //TODO 重要：必须以该形式初始化view---［inflate(R.layout.**, new RelativeLayout(context),false)］------，以让inflater知道父布局的类型，否则布局**中的高度和宽度无效，均变为wrap_content
        View view = inflater.inflate(R.layout.base_layout_title, new RelativeLayout(context), false);
        String title = null;
        if (conversation.getConversationType() == YWConversationType.P2P) {
            YWP2PConversationBody conversationBody = (YWP2PConversationBody) conversation
                    .getConversationBody();
            if (!TextUtils.isEmpty(conversationBody.getContact().getShowName())) {
                title = conversationBody.getContact().getShowName();
            } else {

                YWIMKit imKit = ImManager.getInstance().getIMKit();
                IYWContact contact = imKit.getContactService().getContactProfileInfo(conversationBody.getContact().getUserId(), conversationBody.getContact().getAppKey());
                //生成showName，According to id。
                if (contact != null && !TextUtils.isEmpty(contact.getShowName())) {
                    title = contact.getShowName();
                }
            }
            //如果标题为空，那么直接使用Id
            if (TextUtils.isEmpty(title)) {
                title = conversationBody.getContact().getUserId();
            }
        }

        TextView textTitle = (TextView) view.findViewById(R.id.titilebar_text_title);
        textTitle.setText(title);


        View backView = view.findViewById(R.id.titlebar_btn_left);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fragment.getActivity().finish();
            }
        });


        //添加发送通用消息布局
        final String content = fragment.getActivity().getIntent().getStringExtra(EXTRA_CUSTOM_MSG_TO_SEND);
        if (content != null) {//如果外界有传入content，则显示通用消息布局

            final View customMsgView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.base_layout_chat_send_custom_msg, null);
            String title1 = null;
            String msg = null;
            String icon = null;
            try {
                //获取消息内容
                JSONObject object = new JSONObject(content).getJSONObject("content");
                title1 = object.getString("title");
                msg = object.getString("msg");
                icon = object.getString("icon");
            } catch (Exception e) {
            }

            ((TextView) (customMsgView.findViewById(R.id.title))).setText(title1);
            ((TextView) (customMsgView.findViewById(R.id.msg))).setText(msg);
            if (null != icon)
                Glide.with(fragment.getContext()).load(Uri.parse(icon)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(((ImageView) (customMsgView.findViewById(R.id.icon))));

            customMsgView.findViewById(R.id.chat_button_send_custom_msg).setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    //点击后发送消息，并将此消息布局置为不可见
                    customMsgView.setVisibility(View.GONE);
                    YWCustomMessageBody body = new YWCustomMessageBody();
                    body.setContent(content);
                    YWMessage message = YWMessageChannel.createCustomMessage(body);
                    conversation.getMessageSender().sendMessage(message, 10000, null);

                }
            });

            //将标题栏和消息发送布局合二为一，添加到linearLayout中
            LinearLayout linearLayout = new LinearLayout(fragment.getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(view);
            linearLayout.addView(customMsgView);
            view = linearLayout;
        }

        FontHelper.applyFont(fragment.getContext(), view, "app_font.ttf");
        return view;
    }
}
