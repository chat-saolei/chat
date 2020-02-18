package com.chat.core.net.protocol;

import lombok.Data;

@Data
public class ProtocolData {
    private byte flag;
    private short code;
    private int seq;
    private byte[] data;

    public boolean isRequest() {
        return flag == ProtocolConstant.REQUEST_TYPE;
    }

    public boolean isResponse() {
        return flag == ProtocolConstant.RESPONSE_TYPE;
    }

    public boolean isSuccess() {
        return flag == ProtocolConstant.RESPONSE_TYPE && code == 0;
    }

    public boolean isNotif() {
        return flag == ProtocolConstant.NOTIF_TYPE;
    }

    public static ProtocolData create(byte flag, short code, int seq, byte[] data) {
        ProtocolData protocolData = new ProtocolData();
        protocolData.setFlag(flag);
        protocolData.setCode(code);
        protocolData.setSeq(seq);
        protocolData.setData(data);
        return protocolData;
    }

    public static ProtocolData createForRequest(short code, int seq, byte[] data) {
        ProtocolData protocolData = new ProtocolData();
        protocolData.setFlag((byte) ProtocolConstant.REQUEST_TYPE);
        protocolData.setCode(code);
        protocolData.setSeq(seq);
        protocolData.setData(data);
        return protocolData;
    }

    public static ProtocolData createForError(int error, int seq, byte[] data) {
        ProtocolData protocolData = new ProtocolData();
        protocolData.setFlag((byte) ProtocolConstant.RESPONSE_TYPE);
        protocolData.setCode((short) error);
        protocolData.setSeq(seq);
        protocolData.setData(data);
        return protocolData;
    }

    public static ProtocolData createForNotif(int eventType, int seq, byte[] data) {
        ProtocolData protocolData = new ProtocolData();
        protocolData.setFlag((byte) ProtocolConstant.NOTIF_TYPE);
        protocolData.setCode((short) eventType);
        protocolData.setSeq(seq);
        protocolData.setData(data);
        return protocolData;
    }

    public static ProtocolData createForNotif(int eventType, byte[] data) {
        ProtocolData protocolData = new ProtocolData();
        protocolData.setFlag((byte) ProtocolConstant.NOTIF_TYPE);
        protocolData.setCode((short) eventType);
        protocolData.setData(data);
        return protocolData;
    }

}
