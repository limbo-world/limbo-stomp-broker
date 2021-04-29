/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.stomp.server.broker.user;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.util.concurrent.EventExecutor;
import org.limbo.stomp.server.broker.messaging.StompMessage;
import org.limbo.stomp.server.broker.messaging.MessageProcessor;
import org.limbo.stomp.server.broker.messaging.MessageSubscription;
import org.limbo.stomp.server.protocol.handlers.exceptions.MessageSendException;
import org.limbo.stomp.server.protocol.handlers.exceptions.SubscribeException;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class SimpBrokerUser implements BrokerUser {

    protected final String host;

    protected final String userId;

    protected final ChannelGroup userChannels;

    /**
     * 代理MessageProcessor
     */
    protected MessageProcessor messageProcessor;

    public SimpBrokerUser(String host, String userId, EventExecutor executor) {
        this.host = host;
        this.userId = userId;

        String groupName = String.format("userChannels-%s-%s", host, userId);
        this.userChannels = new DefaultChannelGroup(groupName, executor);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getHost() {
        return this.host;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getUserId() {
        return this.userId;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public ChannelGroup channels() {
        return this.userChannels;
    }

    // --------------------- 以下实现MessageProcessor接口

    /**
     * {@inheritDoc}
     * @param subscription 客户端订阅参数
     * @return
     */
    @Override
    public Mono<Publisher<StompFrame>> subscribe(MessageSubscription subscription) {
        if (this.messageProcessor == null) {
            return Mono.error(new SubscribeException(String.format(
                    "Cannot subscribe %s %s, SUBSCRIBE frame is not supported!",
                    subscription.getSubscribeId(), subscription.getDestination())));
        }

        return this.messageProcessor.subscribe(subscription);
    }

    /**
     * {@inheritDoc}
     * @param id UNSUBSCRIBE帧的ID头。
     * @return
     */
    @Override
    public Mono<Void> unsubscribe(Long id) {
        if (this.messageProcessor == null) {
            return Mono.error(new SubscribeException(String.format(
                    "Cannot unsubscribe %s, UNSUBSCRIBE frame is not supported!", id)));
        }

        return this.messageProcessor.unsubscribe(id);
    }

    /**
     * {@inheritDoc}
     * @param message 待发送的消息
     * @return
     */
    @Override
    public Mono<Void> send(StompMessage message) {
        if (this.messageProcessor == null) {
            return Mono.error(new MessageSendException("Cannot send message, SEND frame is not supported!"));
        }

        return this.messageProcessor.send(message);
    }
}
