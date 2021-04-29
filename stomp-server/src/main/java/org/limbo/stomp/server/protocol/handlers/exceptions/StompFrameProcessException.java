package org.limbo.stomp.server.protocol.handlers.exceptions;

/**
 * STOMP帧处理异常的基类。
 *
 * @author Brozen
 * @since 2021-04-29
 */
public class StompFrameProcessException extends RuntimeException {

    private static final long serialVersionUID = -1379037290422136622L;

    public StompFrameProcessException(String message) {
        super(message);
    }

    public StompFrameProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public StompFrameProcessException(Throwable cause) {
        super(cause);
    }
}
