package org.limbo.stomp.server.broker.messaging;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Brozen
 * @since 2021-04-29
 */
@Data
public class SimpStompMessage implements StompMessage {

    /**
     * 消息发送目的地
     */
    private String destination;

    /**
     * 此消息被重新投递次数
     */
    private int redeliveryTimes;

    /**
     * 消息上次被投递的时间戳
     */
    private long emitTimestamp;

    /**
     * STOMP消息头
     */
    private Map<String, List<String>> headers;

    /**
     * STOMP消息payload
     */
    private String payload;

}
