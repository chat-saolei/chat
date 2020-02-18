package com.chat.core.net.netty.handler;

import com.chat.core.net.protocol.ProtocolData;
import com.chat.core.session.Session;
import com.chat.core.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ProtoMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ProtocolData data = new ProtocolData();
        Session session = SessionManager.getSession(ctx.channel());
        if (session == null) {
            log.error("session not found:" + ctx);
            return;
        }
        session.onReq(data);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SessionManager.onCreate(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionManager.onClose(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            SessionManager.onClose(ctx.channel());
            ctx.close();
            log.info("session closed by Idle:" + evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            IOException exception = (IOException) cause;
            String msg = exception.getMessage();
            if ("Connection reset by peer".equals(msg)) {
                return;
            }
        } else if (cause instanceof DecoderException && cause.getCause() != null) {
            SessionManager.onClose(ctx.channel());
        }
        log.error("exceptionCaught", cause);
    }
}
