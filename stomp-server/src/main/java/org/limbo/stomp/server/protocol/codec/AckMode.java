package org.limbo.stomp.server.protocol.codec;

import org.apache.commons.lang3.StringUtils;
import org.limbo.stomp.server.protocol.handlers.exceptions.StompFrameProcessException;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public enum AckMode {

    /**
     * 无需客户端确认，消息发送后默认客户端已收到。
     */
    AUTO("auto"),

    /**
     * 需客户端确认，服务端收到客户端ACK帧的时候，之前发送的帧全部确认完成。
     */
    CLIENT("client"),

    /**
     * 需客户端确认，且逐帧确认。
     */
    CLIENT_INDIVIDUAL("client-individual"),
    ;

    public final String value;


    AckMode(String value) {
        this.value = value;
    }


    /**
     * 解析ack头，如果入参为空则返回 {@link AckMode#AUTO}。如果无法解析则抛出 {@link StompFrameProcessException} 异常。
     * @param ack SUBSCRIBE帧中的ack头
     * @return 返回解析出的ack值。
     */
    public static AckMode parse(String ack) {
        if (StringUtils.isBlank(ack)) {
            return AckMode.AUTO;
        }

        for (AckMode acknowledge : values()) {
            if (acknowledge.value.equalsIgnoreCase(ack)) {
                return acknowledge;
            }
        }

        throw new StompFrameProcessException("cannot parse SUBSCRIBE ack header:" + ack);
    }

}
