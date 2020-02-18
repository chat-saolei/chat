package com.chat.core.session;

import com.chat.core.net.protocol.ProtocolData;
import io.netty.channel.Channel;
import lombok.Data;

@Data
public class Session {
    private String sessionId;
    private int userId;
    private Channel channel;
    private String address;

    public void onReq(ProtocolData data) {

    }

    public void write(ProtocolData data) {

    }
}
