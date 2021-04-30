package org.limbo.stomp.server.protocol.handlers.exceptions;

/**
 * 处理ACK、NACK帧时发生异常，抛出此类。
 *
 * @author Brozen
 * @since 2021-04-30
 */
public class AcknowledgeException extends StompFrameProcessException {

    private static final long serialVersionUID = -225247786339706131L;

    public AcknowledgeException(String message) {
        super(message);
    }

}
