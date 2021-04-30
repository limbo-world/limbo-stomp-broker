package org.limbo.stomp.server.broker.messaging;

/**
 * 消息确认
 *
 * @author Brozen
 * @since 2021-04-30
 */
public interface MessageAcknowledge {

    /**
     * ack的消息ID
     */
    long getId();

    /**
     * ack的事务，可以不存在。
     */
    String getTransaction();

}
