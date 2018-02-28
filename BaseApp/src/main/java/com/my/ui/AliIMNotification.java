package com.my.ui;

import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMNotification;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;

/**
 * Created by wangwei on 18/1/19.
 */
public class AliIMNotification extends IMNotification{

    public AliIMNotification(Pointcut pointcut) {
        super(pointcut);
    }

    @Override
    public boolean needSound(YWConversation conversation, YWMessage message) {
        return true;
    }
}
