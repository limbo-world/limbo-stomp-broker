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

package org.limbo.stomp.kafka.server.broker;

import io.netty.channel.ChannelId;
import org.limbo.stomp.kafka.server.protocol.handlers.ChannelHandlerContextDelegate;
import org.limbo.stomp.kafka.server.utils.Handlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class CachedBrokerUserResolverFactory implements BrokerUserResolverFactory {

    private static final Map<ChannelId, BrokerUserResolver> RESOLVER_CACHE;
    static {
        RESOLVER_CACHE = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     * @param context 客户端通道处理上下文代理
     * @return
     */
    @Override
    public BrokerUserResolver getResolver(ChannelHandlerContextDelegate context) {
        return RESOLVER_CACHE.computeIfAbsent(context.channel().id(), cid -> createBrokerUserResolver(context));
    }

    /**
     * 创建一个新的BrokerUserResolver，并注册在通道关闭时从缓存中移除，该方法调用时并发安全，它在ConcurrentHashMap的computeIfAbsent中执行。
     */
    private BrokerUserResolver createBrokerUserResolver(ChannelHandlerContextDelegate context) {
        BrokerUserResolver resolver = doCreateBrokerUserResolver(context);
        context.pipeline().addLast(Handlers.Inbounds.whenInactive(ctx -> RESOLVER_CACHE.remove(ctx.channel().id())));
        return resolver;
    }

    /**
     * 创建一个新的BrokerUserResolver，子类可以覆盖实现
     */
    protected BrokerUserResolver doCreateBrokerUserResolver(ChannelHandlerContextDelegate context) {
        return new HeaderBrokerUserResolver(context);
    }

}
