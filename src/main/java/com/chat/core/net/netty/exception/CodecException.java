package com.chat.core.net.netty.exception;

import com.google.protobuf.Message;

public class CodecException extends RuntimeException {

    public final Error error;
    public final Message data;

    public CodecException(final Error e, final Message data) {
        this.error = e;
        this.data = data;
    }
}
