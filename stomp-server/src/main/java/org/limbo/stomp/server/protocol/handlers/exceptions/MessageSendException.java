package org.limbo.stomp.server.protocol.handlers.exceptions;

/**
 * SEND帧处理发生异常时，抛出此异常。
 *
 * @author Brozen
 * @since 2021-04-29
 */
public class MessageSendException extends StompFrameProcessException {

    private static final long serialVersionUID = -5546960761305217071L;

    public MessageSendException(String message) {
        super(message);
    }

}
