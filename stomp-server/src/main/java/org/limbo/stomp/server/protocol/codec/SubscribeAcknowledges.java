package org.limbo.stomp.server.protocol.codec;

import org.apache.commons.lang3.StringUtils;
import org.limbo.stomp.server.protocol.handlers.exceptions.StompFrameProcessException;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public enum SubscribeAcknowledges {

    AUTO("auto"),
    CLIENT("client"),
    CLIENT_INDIVIDUAL("client-individual"),
    ;

    public final String value;


    SubscribeAcknowledges(String value) {
        this.value = value;
    }


    /**
     * 解析ack头，如果入参为空则返回 {@link SubscribeAcknowledges#AUTO}。如果无法解析则抛出 {@link StompFrameProcessException} 异常。
     * @param ack SUBSCRIBE帧中的ack头
     * @return 返回解析出的ack值。
     */
    public static SubscribeAcknowledges parse(String ack) {
        if (StringUtils.isBlank(ack)) {
            return SubscribeAcknowledges.AUTO;
        }

        for (SubscribeAcknowledges acknowledge : values()) {
            if (acknowledge.value.equalsIgnoreCase(ack)) {
                return acknowledge;
            }
        }

        throw new StompFrameProcessException("cannot parse SUBSCRIBE ack header:" + ack);
    }

}
