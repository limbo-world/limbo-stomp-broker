package org.limbo.stomp.server.broker.messaging;

import org.limbo.stomp.server.protocol.codec.AckMode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * 一次SUBSCRIBE帧订阅成功后的结果抽象。代表着一个消息源，该消息源的产生是因为subscribeId代表的SUBSCRIBE帧，
 * 该消息源发射的消息均来自于同一个destination。
 *
 * @author Brozen
 * @since 2021-04-30
 */
public interface DestinationMessageSource {

    /**
     * 订阅ID
     */
    Long getSubscribeId();

    /**
     * 订阅的destination
     */
    String getDestination();

    /**
     * 订阅的消息通过Flux发射
     */
    Flux<StompMessage> getPublisher();

    /**
     * 如果需要发射新的消息，可以通过此FluxSink进行。
     */
    FluxSink<StompMessage> getPublisherSink();

    /**
     * 获取订阅的ACK模式
     */
    AckMode getAckMode();

    /**
     * 确认消息。
     */
    void ack(MessageAcknowledge ack);

    /**
     * 取消确认消息。在limbo-stomp-server中，nack不做任何处理，不会把消息转发给其他client
     */
    default void nack(MessageAcknowledge ack) {}

}
