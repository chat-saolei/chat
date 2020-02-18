package com.chat.core.net.netty.boot;

import com.chat.common.config.ApplicationConfig;
import com.chat.core.net.netty.handler.ProtoMessageHandler;
import com.chat.core.net.netty.handler.codec.TcpCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpBoot {
    private static Channel channel;
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static ServerBootstrap bootstrap;

    public static void start() {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("netty-tcp-boss"));
        workerGroup = new NioEventLoopGroup(10, new DefaultThreadFactory("netty-tcp-worker"));
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //当处理线程占满后，保留完成3次握手的请求数，默认为50
                .option(ChannelOption.SO_BACKLOG, Integer.valueOf(100))
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("tcp-decoder", new TcpCodec(3 * 1024 * 1024));
                        p.addLast(new IdleStateHandler(30, 30, 30, TimeUnit.SECONDS));
                        p.addLast(new ProtoMessageHandler());
                    }
                });

        try {
            channel = bootstrap.bind(ApplicationConfig.port).sync().channel();
            log.info("Tcp server start at {}", ApplicationConfig.port);
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("Tcp server start error:", e);
            System.exit(-1);
        }
    }

    public static void close() {
        try {
            if (channel != null && channel.isActive()) {
                channel.close();
            }
        } catch (Exception e) {
            log.error("Channel close error:", e);
        }

        try {
            if (bootstrap != null) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            log.error("EventLoopGroup close error:", e);
        }
        log.info("Tcp server closed");
    }
}
