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

package org.limbo.stomp.server.starter.broker;

import org.limbo.stomp.server.broker.client.BrokerClient;
import org.limbo.stomp.server.broker.client.BrokerClientProvider;
import org.limbo.stomp.server.broker.user.BrokerUserResolverFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class ConfigurableBrokerClientProvider implements BrokerClientProvider {

    /**
     * Broker代理的全部STOMP服务器信息，每个STOMP服务器都属于一个BrokerClient，有自己的host。
     * key: host，value: BrokerClient。
     */
    private Map<String, BrokerClient> brokerClients;

    /**
     * 用户解析器工厂
     */
    private BrokerUserResolverFactory brokerUserResolverFactory;

    public ConfigurableBrokerClientProvider() {
        this(Collections.emptyMap());
    }

    /**
     * @param brokerClients 初始化的BrokerClient
     */
    public ConfigurableBrokerClientProvider(Map<String, BrokerClient> brokerClients) {
        this.brokerClients = new ConcurrentHashMap<>();
        this.brokerClients.putAll(brokerClients);
    }

    /**
     * 添加一个BrokerClient定义
     */
    public void addBrokerClient(String host, BrokerClient brokerClient) {
        this.brokerClients.putIfAbsent(host, brokerClient);
    }

    /**
     * 设置用户解析器工厂
     */
    public void setBrokerUserResolverFactory(BrokerUserResolverFactory brokerUserResolverFactory) {
        this.brokerUserResolverFactory = brokerUserResolverFactory;
    }

    /**
     * {@inheritDoc}
     * @param host 应用host
     * @return
     */
    @Override
    public Optional<BrokerClient> getBrokerClient(String host) {
        return Optional.ofNullable(brokerClients.get(host));
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public BrokerUserResolverFactory getBrokerUserResolverFactory() {
        return brokerUserResolverFactory;
    }
}
