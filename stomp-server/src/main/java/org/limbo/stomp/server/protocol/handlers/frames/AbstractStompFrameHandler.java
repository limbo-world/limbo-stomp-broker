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

package org.limbo.stomp.server.protocol.handlers.frames;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.util.concurrent.EventExecutor;
import org.limbo.stomp.server.broker.client.BrokerClientProvider;
import org.limbo.stomp.server.broker.user.BrokerUserResolver;
import org.limbo.stomp.server.broker.user.BrokerUserResolverFactory;
import org.limbo.stomp.server.protocol.handlers.ChannelHandlerContextDelegate;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public abstract class AbstractStompFrameHandler implements StompFrameHandler {

    /**
     * Handler处理的STOMP帧command
     */
    private StompCommand processedCommand;

    /**
     * 用于发送消息
     */
    private ChannelHandlerContextDelegate channelHandlerContext;

    /**
     * 用于获取 被代理的STOMP服务器应用信息
     */
    private BrokerClientProvider brokerClientProvider;

    public AbstractStompFrameHandler(StompCommand processedCommand, ChannelHandlerContextDelegate channelHandlerContext) {
        this.processedCommand = processedCommand;
        this.channelHandlerContext = channelHandlerContext;
    }

    /**
     * @see ChannelHandlerContextDelegate
     */
    public void setChannelHandlerContext(ChannelHandlerContextDelegate channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    /**
     * @see ChannelHandlerContextDelegate
     */
    public ChannelHandlerContextDelegate getChannelHandlerContext() {
        return channelHandlerContext;
    }

    /**
     * @see BrokerClientProvider
     */
    public BrokerClientProvider getBrokerClientProvider() {
        return brokerClientProvider;
    }

    /**
     * @see BrokerClientProvider
     */
    public void setBrokerClientProvider(BrokerClientProvider brokerClientProvider) {
        this.brokerClientProvider = brokerClientProvider;
    }

    /**
     * 获取用户解析器
     * @see BrokerUserResolver
     * @return 用户解析器 {@link BrokerUserResolver}
     */
    protected BrokerUserResolver getBrokerUserResolver() {
        BrokerUserResolverFactory userResolverFactory = brokerClientProvider.getBrokerUserResolverFactory();
        return userResolverFactory.getResolver(channelHandlerContext.channel());
    }


    /**
     * 在EventLoop中执行任务，如果调用方法时不在EventLoop中，则向EventLoop提交任务执行。
     * @param task 需要执行的任务
     */
    protected void doInEventLoop(Consumer<ChannelHandlerContext> task) {
        ChannelHandlerContextDelegate ctx = getChannelHandlerContext();
        EventExecutor executor = ctx.executor();
        if (executor.inEventLoop()) {
            task.accept(ctx);
        } else {
            executor.execute(() -> task.accept(ctx));
        }
    }

    /**
     * 将{@link StompHeaders}转换为{@link Map}。
     * @param headers STOMP帧头
     */
    protected Map<String, List<String>> headersToMap(StompHeaders headers) {
        Map<String, List<String>> headersMap = new HashMap<>();
        Iterator<Map.Entry<String, String>> iterator = headers.iteratorAsString();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String k = entry.getKey();
            String v = entry.getValue();
            headersMap.computeIfAbsent(k, _k -> new ArrayList<>()).add(v);
        }
        return headersMap;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public StompCommand processedCommand() {
        return processedCommand;
    }
}
