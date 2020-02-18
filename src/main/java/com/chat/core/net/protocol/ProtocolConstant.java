package com.chat.core.net.protocol;

public class ProtocolConstant {
    public static final short MAGIC_NUMBER = (short) 0xE04A;
    public static final int PROTOCOL_HEADER_LEN = 13;


    public static final int REQUEST_TYPE = 0x1;
    public static final int RESPONSE_TYPE = 0x2;
    public static final int NOTIF_TYPE = 0x3;
}
