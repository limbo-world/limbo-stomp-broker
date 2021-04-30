package org.limbo.stomp.server.broker.messaging;

import lombok.Data;
import lombok.experimental.Accessors;
import org.limbo.stomp.server.protocol.codec.AckMode;

import java.util.List;
import java.util.Map;

/**
 * 描述一次客户端订阅
 */
@Data
@Accessors(chain = true)
public class MessageSubscription {

    /**
     * 订阅ID，同一个连接可能会订阅多次，通过订阅ID来区分是哪次订阅，后续向客户端发送的MESSAGE或UNSUBSCRIBE帧中必须有对应的订阅ID。
     */
    private Long subscribeId;

    /**
     * 订阅的destination，具体如何定义由服务实现来决定。
     */
    private String destination;

    /**
     * 一次订阅中，客户端对消息的通知方式。
     */
    private AckMode ack;

    /**
     * SUBSCRIBE帧的请求头
     */
    private Map<String, List<String>> originHeaders;

    /**
     * SUBSCRIBE帧的payload
     */
    private String originPayload;

}