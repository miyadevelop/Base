package com.my.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMChattingPageOperateion;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.kit.contact.YWContactHeadLoadHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.my.app.R;

import org.json.JSONObject;

/**
 * 聊天界面(单聊和群聊界面)的定制点(根据需要实现相应的接口来达到自定义聊天界面)，不设置则使用openIM默认的实现
 * 1.CustomChattingTitleAdvice 自定义聊天窗口标题 2. OnUrlClickChattingAdvice 自定义聊天窗口中
 * 当消息是url是点击的回调。用于isv处理url的打开处理。不处理则用第三方浏览器打开 如果需要定制更多功能，需要实现更多开放的接口
 * 需要.继承BaseAdvice .实现相应的接口
 * <p/>
 * 另外需要在Application中绑定
 * AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_POINTCUT,
 * ChattingOperationCustomSample.class);
 *
 * @author jing.huai
 */
public class AliChattingOperationCustomSample extends IMChattingPageOperateion {


    public class CustomMessageType {
        private static final String CUSTOM_MSG = "custom_msg";
    }


    // 默认写法
    public AliChattingOperationCustomSample(Pointcut pointcut) {
        super(pointcut);
    }


//    /**
//     * 发送群自定义消息
//     */
//    public static void sendTribeCustomMessage(YWConversation conversation) {
//        // 创建自定义消息的messageBody对象
//        YWCustomMessageBody messageBody = new YWCustomMessageBody();
//
//        // 请注意这里不一定要是JSON格式，这里纯粹是为了演示的需要
//        JSONObject object = new JSONObject();
//        try {
//            object.put("customizeMessageType", CustomMessageType.GREETING);
//        } catch (JSONException e) {
//        }
//
//        messageBody.setContent(object.toString());// 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
//        messageBody.setSummary("您收到一个招呼");// 可以理解为消息的标题，用于显示会话列表和消息通知栏
//        //创建群聊自定义消息，创建单聊自定义消息和群聊自定义消息的接口不是同一个，切记不要用错！！
//        YWMessage message =  YWMessageChannel.createTribeCustomMessage(messageBody);
//        //发送群聊自定义消息
//        conversation.getMessageSender().sendMessage(message,120, null);
//    }
//
//    /**
//     * 发送单聊自定义消息
//     * @param userId
//     */
//    public static void sendP2PCustomMessage(String userId) {
//        //创建自定义消息的messageBody对象
//        YWCustomMessageBody messageBody = new YWCustomMessageBody();
//
//        //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
//        JSONObject object = new JSONObject();
//        try {
//            object.put("customizeMessageType", CustomMessageType.CARD);
//            object.put("personId", userId);
//        } catch (JSONException e) {
//
//        }
//
//        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
//        messageBody.setSummary("[名片]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
//        //创建单聊自定义消息，创建单聊自定义消息和群聊自定义消息的接口不是同一个，切记不要用错！！
//        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
//        //发送单聊自定义消息
//        mConversation.getMessageSender().sendMessage(message, 120, null);
//    }
//
//    /**
//     * 发送单聊地理位置消息
//     */
//    public static void sendGeoMessage(YWConversation conversation) {
//        conversation.getMessageSender().sendMessage(
//                YWMessageChannel.createGeoMessage(30.2743790000,
//                        120.1422530000, "浙江省杭州市西湖区"), 120, null);
//    }

    /**
     * 定制点击消息事件, 每一条消息的点击事件都会回调该方法，开发者根据消息类型，对不同类型的消息设置不同的点击事件
     *
     * @param fragment 聊天窗口fragment对象
     * @param message  被点击的消息
     * @return true:使用用户自定义的消息点击事件，false：使用默认的消息点击事件
     */
    @Override
    public boolean onMessageClick(final Fragment fragment, final YWMessage message) {
        if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS || message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_TRIBE_CUS) {
            String msgType = null;
            try {
                String content = message.getMessageBody().getContent();
                JSONObject object = new JSONObject(content);
                msgType = object.getString("do_what");

                if (!TextUtils.isEmpty(msgType)) {
                    //通用消息
                    if (msgType.equals(CustomMessageType.CUSTOM_MSG)) {

                        String url = object.getString("intent");
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(url));
                        fragment.getContext().startActivity(intent);
                    }
                }
            } catch (Exception e) {

            }
            return true;
        }
        return false;
    }


    @Override
    public int getFastReplyResId(YWConversation conversation) {
        return R.drawable.aliwx_reply_bar_face_bg;
    }

    @Override
    public boolean onFastReplyClick(Fragment pointcut, YWConversation ywConversation) {
        return false;
    }

    @Override
    public int getRecordResId(YWConversation conversation) {
        return 0;
    }

    @Override
    public boolean onRecordItemClick(Fragment pointcut, YWConversation ywConversation) {
        return false;
    }


//    /**
//     * 获取url对应的自定义view,当openIM发送或者接收到url消息时会回调该方法获取该url的自定义view。若开发者实现了该方法并且返回了一个view对象，openIM将会使用该view展示对应的url消息。
//     * @param fragment  可以通过 fragment.getActivity拿到Context
//     * @param message   url所属的message
//     * @param url       url
//     * @param ywConversation message所属的conversion
//     * @return  自定义Url view
//     */
//    @Override
//    public View getCustomUrlView(Fragment fragment, YWMessage message, String url, YWConversation ywConversation) {
//        if (url.equals("https://www.baidu.com/ ")) {
//            LinearLayout layout = (LinearLayout) View.inflate(
//                    DemoApplication.getContext(),
//                    R.layout.demo_custom_tribe_msg_layout, null);
//            TextView textView = (TextView) layout.findViewById(R.id.msg_content);
//            textView.setText("I'm from getCustomUrlView!");
//            return layout;
//        }
//        return null;
//    }

//
//    /**
//     * 开发者可以根据用户操作设置该值
//     */
//    private static boolean mUserInCallMode = false;
//
//    /**
//     * 当打开聊天窗口时，自动发送该字符串给对方
//     * @param fragment      聊天窗口fragment
//     * @param conversation  当前会话
//     * @return 自动发送的内容（注意，内容empty则不自动发送）
//     */
//    @Override
//    public String messageToSendWhenOpenChatting(Fragment fragment, YWConversation conversation) {
//        //p2p、客服和店铺会话处理，否则不处理，
//        int mCvsType = conversation.getConversationType().getValue();
//        if (mCvsType == YWConversationType.P2P.getValue() || mCvsType == YWConversationType.SHOP.getValue()) {
////            return "你好";
//            return null;
//        } else {
//            return null;
//        }
//
//    }
//
//    /**
//     * 当打开聊天窗口时，自动发送该消息给对方
//     * @param fragment      聊天窗口fragment
//     * @param conversation  当前会话
//     * @param isConversationFirstCreated  是否是首次创建会话
//     * @return 自动发送的消息（注意，内容empty则不自动发送）
//     */
//    @Override
//    public YWMessage ywMessageToSendWhenOpenChatting(Fragment fragment, YWConversation conversation, boolean isConversationFirstCreated) {
////        YWMessageBody messageBody = new YWMessageBody();
////        messageBody.setSummary("WithoutHead");
////        messageBody.setContent("hi，我是单聊自定义消息之好友名片");
////        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
////        return message;
//
//        //与客服的会话
//        if(conversation.getConversationId().contains("openim官方客服")){
//            //首次进入会话
////            if(isConversationFirstCreated){
//
//                final SharedPreferences defalultSprefs = fragment.getActivity().getSharedPreferences(
//                    "ywPrefsTools", Context.MODE_PRIVATE);
//
//                long lastSendTime = defalultSprefs.getLong("lastSendTime_"+conversation.getConversationId(), -1);
//                //24小时后再次发送本地隐藏消息
//                if(System.currentTimeMillis()-lastSendTime>24*60*60*1000){
//
//                    YWMessage textMessage = YWMessageChannel.createTextMessage("你好");
//                    //添加发送的消息不显示在对方界面上的本地标记（todo 仅支持本地隐藏。当用户切换手机或清楚数据后，会漫游消息下这些消息并出现在用户的聊天界面上！！）
//                    textMessage.setLocallyHideMessage(true);
//
//                    //保存发送时间戳
//                    SharedPreferences.Editor edit = defalultSprefs.edit();
//                    edit.putLong("lastSendTime_"+conversation.getConversationId(),System.currentTimeMillis());
//                    edit.commit();
//
//                    return textMessage;
//                }
//
////            }
//
//        }
//        //返回null,则不发送
//        return null;
//
//    }


    /*****************
     * 以下是定制自定义消息view的示例代码
     ****************/

    //自定义消息view的种类数
    private final int typeCount = 1;

    private final int type_custom_msg = 0;


    /**
     * 自定义消息view的种类数
     *
     * @return 自定义消息view的种类数
     */
    @Override
    public int getCustomViewTypeCount() {
        return typeCount;
    }

    /**
     * 自定义消息view的类型，开发者可以根据自己的需求定制多种自定义消息view，这里需要根据消息返回view的类型
     *
     * @param message 需要自定义显示的消息
     * @return 自定义消息view的类型
     */
    @Override
    public int getCustomViewType(YWMessage message) {

        if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS || message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_TRIBE_CUS) {
            String msgType = null;
            try {
                String content = message.getMessageBody().getContent();
                JSONObject object = new JSONObject(content);
                msgType = object.getString("do_what");
            } catch (Exception e) {

            }
            if (!TextUtils.isEmpty(msgType)) {
                //通用消息
                if (msgType.equals(CustomMessageType.CUSTOM_MSG)) {
                    return type_custom_msg;
                }
            }
        }
        return super.getCustomViewType(message);
    }


    /**
     * 根据viewType获取自定义view
     *
     * @param fragment       聊天窗口fragment
     * @param message        当前需要自定义view的消息
     * @param convertView    自定义view
     * @param viewType       自定义view的类型
     * @param headLoadHelper 头像加载管理器，用户可以调用该对象的方法加载头像
     * @return 自定义view
     */
    @Override
    public View getCustomView(Fragment fragment, YWMessage message, View convertView, int viewType, YWContactHeadLoadHelper headLoadHelper) {

        if (viewType == type_custom_msg) { //通用消息
            String title = null;
            String msg = null;
            String icon = null;
            try {
                //获取消息内容
                String content = message.getMessageBody().getContent();
                JSONObject object = new JSONObject(content).getJSONObject("content");
                title = object.getString("title");
                msg = object.getString("msg");
                icon = object.getString("icon");
            } catch (Exception e) {
            }

            ViewHolder2 holder;
            if (convertView == null) {
                holder = new ViewHolder2();
                convertView = View.inflate(fragment.getActivity(), R.layout.base_item_chat_custom_msg, null);
                holder.icon = convertView.findViewById(R.id.icon);
                holder.title = convertView.findViewById(R.id.title);
                holder.msg = convertView.findViewById(R.id.msg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder2) convertView.getTag();
            }
            holder.title.setText(title);
            holder.msg.setText(msg);
            if (null != icon)
                Glide.with(convertView.getContext()).load(Uri.parse(icon)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.icon);
            return convertView;
        }
        return super.getCustomView(fragment, message, convertView, viewType, headLoadHelper);
    }


    public class ViewHolder2 {
        ImageView icon;
        TextView title;
        TextView msg;
    }


    /**************** 以上是定制自定义消息view的示例代码 ****************/


}