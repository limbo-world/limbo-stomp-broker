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

package org.limbo.stomp.server.starter.config;

import org.limbo.stomp.server.broker.user.BrokerUserResolverFactory;
import org.limbo.stomp.server.broker.user.CachedBrokerUserResolverFactory;
import org.limbo.stomp.server.protocol.server.StompClientChannelInitializer;
import org.limbo.stomp.server.protocol.server.StompServer;
import org.limbo.stomp.server.protocol.server.StompServerConfig;
import org.limbo.stomp.server.starter.StompServerStarter;
import org.limbo.stomp.server.starter.broker.PropertiesBrokerClientProvider;
import org.limbo.stomp.server.starter.config.props.StompBrokerProperties;
import org.limbo.stomp.server.starter.config.props.StompBrokerServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Brozen
 * @since 2021-04-06
 */
@Configuration
@EnableConfigurationProperties(StompBrokerProperties.class)
public class StompBrokerAutoConfiguration {

    @Autowired
    StompBrokerProperties brokerProperties;

    /**
     * StompBrokerServer的启动器Bean，在SpringBean的声明周期中管理StompBrokerServer的声明周期
     */
    @Bean
    public StompServerStarter stompServerStarter(StompServer server) {
        return new StompServerStarter(server);
    }

    /**
     * 配置一个StompBrokerServer
     */
    @Bean
    public StompServer stompServer(StompClientChannelInitializer clientChannelInitializer) {
        StompBrokerServerProperties serverProperties = brokerProperties.getServer();
        StompServer stompServer = new StompServer(
                StompServerConfig.builder()
                        .port(serverProperties.getPort())
                        .acceptorCount(serverProperties.getAcceptorCount())
                        .workerCount(serverProperties.getWorkerCount())
                        .build()
        );

        stompServer.setChannelInitializer(clientChannelInitializer);
        return stompServer;
    }

    /**
     * 声明一个客户端连接初始化器，会注册STOMP协议的解包、封包处理器
     */
    @Bean
    public StompClientChannelInitializer brokerClientChannelInitializer(
            BrokerUserResolverFactory brokerUserResolverFactory) {
        StompClientChannelInitializer channelInitializer = new StompClientChannelInitializer();

        PropertiesBrokerClientProvider brokerClientProvider =
                new PropertiesBrokerClientProvider(brokerProperties.getClients(), brokerUserResolverFactory);
        channelInitializer.setBrokerClientProvider(brokerClientProvider);

        return channelInitializer;
    }

    /**
     * 用户解析器工厂，当使用starter的服务未指定一个解析器工厂时，会使用默认的这个
     */
    @Bean
    @ConditionalOnMissingBean(BrokerUserResolverFactory.class)
    public BrokerUserResolverFactory brokerUserResolverFactory() {
        return new CachedBrokerUserResolverFactory();
    }

}
