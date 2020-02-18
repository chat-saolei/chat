package com.chat.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class NettyUtil {
    private static final String SESSION_ID = "SESSION_ID";

    public static String getAddress(Channel channel) {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        return address.getAddress().getHostAddress();
    }

    public static String getSessionId(Channel channel) {
        Attribute attribute = channel.attr(AttributeKey.valueOf(SESSION_ID));
        return (String) attribute.get();
    }

    public static String setSessionId(Channel channel, String sessionId) {
        Attribute attribute = channel.attr(AttributeKey.valueOf(SESSION_ID));
        attribute.set(sessionId);
        return sessionId;
    }
}
