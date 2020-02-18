package com.chat.core.session;

import com.chat.util.NettyUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SessionManager {
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();
    private static AtomicLong SESSION_ID = new AtomicLong(1);

    public static void onCreate(Channel channel) {
        String sessionId = SESSION_ID.getAndIncrement() + "";
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setChannel(channel);
        session.setAddress(NettyUtil.getAddress(channel));
        sessionMap.put(sessionId, session);
        NettyUtil.setSessionId(channel, sessionId);
        log.info("session create:{},{}", sessionId, channel);
    }

    public static void onClose(Channel channel) {
        String sessionId = NettyUtil.getSessionId(channel);
        if (sessionId == null) {
            log.info("session not found:" + channel);
        }
        sessionMap.remove(sessionId);
    }

    public static Session getSession(Channel channel) {
        String sessionId = NettyUtil.getSessionId(channel);
        if (sessionId == null) {
            log.info("session not found:" + channel);
        }
        return sessionMap.get(sessionId);
    }


}
