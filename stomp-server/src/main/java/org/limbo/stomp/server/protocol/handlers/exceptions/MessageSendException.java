package org.limbo.stomp.server.protocol.handlers.exceptions;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public class MessageSendException extends StompFrameProcessException {

    private static final long serialVersionUID = -5546960761305217071L;

    public MessageSendException(String message) {
        super(message);
    }

}
