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

package org.limbo.stomp.server.broker.client;

import org.limbo.stomp.server.broker.user.BrokerUserResolverFactory;

import java.util.Optional;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public interface BrokerClientProvider {

    /**
     * 获取被代理的STOMP服务器应用信息
     * @param host 应用host
     */
    Optional<BrokerClient> getBrokerClient(String host);

    /**
     * 获取代理用户解析器工厂
     */
    BrokerUserResolverFactory getBrokerUserResolverFactory();

}
