package org.limbo.stomp.server.protocol.handlers.exceptions;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public class SubscribeException extends StompFrameProcessException {

    private static final long serialVersionUID = 3829422609357788408L;

    public SubscribeException(String message) {
        super(message);
    }

}
