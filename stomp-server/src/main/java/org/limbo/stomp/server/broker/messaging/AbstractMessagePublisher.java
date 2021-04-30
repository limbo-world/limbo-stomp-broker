package org.limbo.stomp.server.broker.messaging;

import org.limbo.stomp.server.protocol.handlers.exceptions.SubscribeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 * @since 2021-04-30
 */
public abstract class AbstractMessagePublisher<T extends DestinationMessageSource> implements MessagePublisher {

    /**
     * 所有已订阅的SUBSCRIBE帧id。
     */
    private Map<Long, T> subscribedPublishers;

    /**
     * 所有已订阅的Publisher，key是subscribeId。
     */
    private Map<String, List<T>> destinationMessageSources;

    public AbstractMessagePublisher() {
        this.subscribedPublishers = new ConcurrentHashMap<>();
        this.destinationMessageSources = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     * @param subscription 客户端订阅参数
     * @return
     */
    @Override
    public Mono<Flux<StompMessage>> subscribe(MessageSubscription subscription) {
        // 检测重复订阅
        Long subscribeId = subscription.getSubscribeId();
        String destination = subscription.getDestination();
        if (subscribedPublishers.containsKey(subscribeId)) {
            throw new SubscribeException(String.format(
                    "Cannot subscribe %s, duplicate id: %s.", destination, subscribeId));
        }

        try {

            return Mono.create(sink -> createDestinationMessageSource(subscription).subscribe(
                    messageSource -> {
                        // 拿到Flux之后要再确认一遍是否已经订阅，double check
                        DestinationMessageSource newSource = subscribedPublishers.computeIfAbsent(subscribeId, _d -> messageSource);
                        if (newSource != messageSource) {
                            throw new SubscribeException(String.format(
                                    "Cannot subscribe %s, duplicate id: %s.", destination, subscribeId));
                        }

                        // 记录一下
                        subscribedPublishers.put(subscribeId, messageSource);
                        destinationMessageSources
                                .computeIfAbsent(destination, _d -> new LinkedList<>())
                                .add(messageSource);

                        sink.success(messageSource.getPublisher());
                    }, sink::error
            ));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }


    /**
     * 创建一个新的订阅对象
     * @param subscription 订阅参数
     * @return 指向指定destination的消息源
     */
    protected abstract Mono<T> createDestinationMessageSource(MessageSubscription subscription);


    /**
     * 查询一个destination对应的全部message source。
     */
    protected List<T> getDestinationMessageSource(String destination) {
        return destinationMessageSources.get(destination);
    }
}
