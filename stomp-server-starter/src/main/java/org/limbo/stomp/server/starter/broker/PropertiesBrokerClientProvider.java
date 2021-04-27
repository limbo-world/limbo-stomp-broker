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

import lombok.experimental.Delegate;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.stomp.server.broker.client.BrokerClientProvider;
import org.limbo.stomp.server.broker.client.DefaultBrokerClient;
import org.limbo.stomp.server.broker.client.SimpBrokerClientCredential;
import org.limbo.stomp.server.broker.user.BrokerUserResolverFactory;
import org.limbo.stomp.server.starter.config.props.StompBrokerClientsProperties;

import java.util.List;
import java.util.Objects;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class PropertiesBrokerClientProvider implements BrokerClientProvider {

    /**
     * 被代理的BrokerClientProvider
     */
    @Delegate
    private ConfigurableBrokerClientProvider delegated;

    /**
     * 从{@linkplain StompBrokerClientsProperties}中解析出BrokerClient配置，设置给被代理的{@link ConfigurableBrokerClientProvider}
     */
    public PropertiesBrokerClientProvider(List<StompBrokerClientsProperties> clients, BrokerUserResolverFactory userResolverFactory) {
        Objects.requireNonNull(userResolverFactory);

        // 创建代理对象
        this.delegated = new ConfigurableBrokerClientProvider();

        // 解析 StompBrokerClientsProperties
        clients.forEach(client -> {
            DefaultBrokerClient brokerClient = new DefaultBrokerClient(client.getHost());

            // 解析client登录认证凭据
            List<StompBrokerClientsProperties.Credential> credentials = client.getCredentials();
            if (CollectionUtils.isNotEmpty(credentials)) {
                for (StompBrokerClientsProperties.Credential cred : credentials) {
                    brokerClient.addCredential(new SimpBrokerClientCredential(cred.getUsername(), cred.getPassword()));
                }
            }
            delegated.addBrokerClient(client.getHost(), brokerClient);
        });

        this.delegated.setBrokerUserResolverFactory(userResolverFactory);
    }
}
