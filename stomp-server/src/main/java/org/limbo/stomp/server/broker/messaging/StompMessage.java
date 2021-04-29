package org.limbo.stomp.server.broker.messaging;

import java.util.List;
import java.util.Map;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public interface StompMessage {

    /**
     * 获取消息发送的目的地
     */
    String getDestination();

    /**
     * 获取此消息被重新投递次数
     */
    int getRedeliveryTimes();

    /**
     * 获取STOMP消息的headers
     */
    Map<String, List<String>> getHeaders();

    /**
     * 获取STOMP消息的payload
     */
    String getPayload();

}
