package org.limbo.stomp.server.broker.messaging;

import io.netty.handler.codec.stomp.StompFrame;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public interface MessageSubscriber {

    /**
     * 订阅一个destination，SUBSCRIBE帧的相关请求头都在参数中。
     * @param subscription 客户端订阅参数
     * @return 如果订阅成功，返回订阅消息的事件源，可以通过该事件源订阅后续的MESSAGE帧（如果无法返回MESSAGE帧，需要触发Publisher的onError）。
     * 如果订阅失败，则直接触发Mono的onError。
     */
    Mono<Publisher<StompFrame>> subscribe(MessageSubscription subscription);

    /**
     * 取消订阅destination，根据UNSUBSCRIBE帧的ID头来取消订阅，此ID对应着订阅时的SUBSCRIBE帧ID头。
     * @param id UNSUBSCRIBE帧的ID头。
     * @return 返回是否取消订阅成功。
     */
    Mono<Void> unsubscribe(Long id);

}
