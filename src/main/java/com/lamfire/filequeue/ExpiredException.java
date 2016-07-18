package com.lamfire.filequeue;

import java.io.IOException;

public class ExpiredException extends IOException {

    private static final long serialVersionUID = 9127711830636757848L;

    public ExpiredException() {
    }

    public ExpiredException(String message) {
        super(message);
    }

    public ExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredException(Throwable cause) {
        super(cause);
    }
}
