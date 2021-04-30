package org.limbo.stomp.server.broker.messaging;

import lombok.Getter;
import org.limbo.stomp.server.protocol.codec.AckMode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.UnicastProcessor;

/**
 * 抽象消息源，消息来自上游
 *
 * @author Brozen
 * @since 2021-04-30
 */
public abstract class AbstractDestinationMessageSource implements DestinationMessageSource {

    /**
     * 订阅ID
     */
    @Getter
    private final Long subscribeId;

    /**
     * 订阅的destination
     */
    @Getter
    private final String destination;

    /**
     * 消息确认模式
     */
    @Getter
    private final AckMode ackMode;

    /**
     * 用于实现消息订阅。
     */
    private Flux<StompMessage> messageSource;

    /**
     * 用于实现消息的发射。
     */
    private FluxSink<StompMessage> messageSourceSink;

    /**
     * 创建一个消息源。
     * @param subscribeId 订阅ID
     * @param destination 订阅destination
     * @param ackMode 消息确认模式
     */
    public AbstractDestinationMessageSource(Long subscribeId, String destination, AckMode ackMode) {
        this.subscribeId = subscribeId;
        this.destination = destination;
        this.ackMode = ackMode;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Flux<StompMessage> getPublisher() {
        // FIXME 此次需保证单线程调用或多线程并发安全，考虑使用netty的executor线程绑定
        if (messageSource == null) {
            messageSource = createMessageSource();
        }

        return this.messageSource;
    }

    /**
     * 创建消息发布的UnicastProcessor
     */
    protected Flux<StompMessage> createMessageSource() {
        Flux<StompMessage> upstream = createUpstreamMessageSource();
        return UnicastProcessor.create(sink -> {
            this.messageSourceSink = sink;
            upstream.subscribe(sink::next, sink::error, sink::complete);
        });
    }

    /**
     * 创建上游消息数据源。
     */
    protected abstract Flux<StompMessage> createUpstreamMessageSource();

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public FluxSink<StompMessage> getPublisherSink() {
        // FIXME 此次需保证单线程调用或多线程并发安全，考虑使用netty的executor线程绑定
        if (messageSource == null) {
            messageSource = createMessageSource();
        }
        return messageSourceSink;
    }
}
