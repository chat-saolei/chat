package com.chat.core.net.netty.handler.codec;

import com.chat.core.net.protocol.ProtocolData;
import enigma.SystemErrorData;
import imserver.gateway.net.netty.exception.CodecException;
import imserver.gateway.net.protocol.ProtocolConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/*
    ---------------------------
     magic|flag|cmd/pushType/errorCode|seq|len|data
    ---------------------------
      2   | 1  | 2 | 4 | 4 |len
    ---------------------------

        flag
    ---------------------------------
    x x x x compress push res/error req
    ---------------------------------
*/
public class TcpCodec extends ByteToMessageCodec<ProtocolData> {
    private int maxFrameLen;

    public TcpCodec(int maxFrameLenMb) {
        maxFrameLen = maxFrameLenMb * 1024 * 1024;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolData msg, ByteBuf out) {
        out.writeShort(ProtocolConstant.MAGIC_NUMBER);
        out.writeByte(msg.getFlag());
        out.writeShort(msg.getCode());
        out.writeInt(msg.getSeq());

        byte[] data = msg.getData();
        int len = data == null ? 0 : data.length;
        out.writeInt(len);
        if (len > 0) {
            out.writeBytes(msg.getData());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Object o;
        try {
            for (; ; ) {
                o = decode(in);
                if (o == null) {
                    break;
                }
                out.add(o);
            }
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new CodecException(Error.BAD_REQUEST, SystemErrorData.newBuilder().setDesc(e.getMessage()).build());
        }
    }

    private Object decode(ByteBuf in) {
        if (in.readableBytes() > maxFrameLen) {
            throw new CodecException(Error.BAD_REQUEST, SystemErrorData.newBuilder().setDesc("too long data,len:" + in.readableBytes()).build());
        }
        if (in.readableBytes() < ProtocolConstant.PROTOCOL_HEADER_LEN) {
            return null;
        }

        in.markReaderIndex();

        int magic = in.readShort();

        if (magic != ProtocolConstant.MAGIC_NUMBER) {
            throw new CodecException(Error.BAD_REQUEST, SystemErrorData.newBuilder().setDesc("magic number error").build());
        }


        byte flag = in.readByte();
        short id = in.readShort();
        int seq = in.readInt();
        int len = in.readInt();
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return null;
        }


        byte[] data = new byte[len];
        in.readBytes(data);
        ProtocolData protocolData = ProtocolData.create(flag, id, seq, data);
        return protocolData;
    }

}
