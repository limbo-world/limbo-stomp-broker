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

import io.netty.channel.Channel;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public interface BrokerUserResolverFactory {

    /**
     * 默认使用{@link CachedBrokerUserResolverFactory}
     */
    BrokerUserResolverFactory DEFAULT = new CachedBrokerUserResolverFactory();

    /**
     * 生成一个client用户解析器
     * @param channel 客户端通道
     * @return client用户信息解析器
     */
    BrokerUserResolver getResolver(Channel channel);

}
