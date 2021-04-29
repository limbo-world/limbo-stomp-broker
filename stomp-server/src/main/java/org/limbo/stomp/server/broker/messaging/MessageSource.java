package org.limbo.stomp.server.broker.messaging;

import reactor.core.publisher.Mono;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public interface MessageSource {

    /**
     * 发送一条消息。如果发送失败，则Mono应触发onError，来让服务器知道消息发送失败，从而给客户端返回ERROR帧并关闭连接。
     * @param message 待发送的消息
     * @return 返回消息发送是否成功
     */
    Mono<Void> send(StompMessage message);

}
