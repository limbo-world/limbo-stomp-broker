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
import org.limbo.stomp.server.protocol.server.StompBrokerClientChannelInitializer;
import org.limbo.stomp.server.protocol.server.StompBrokerServer;
import org.limbo.stomp.server.starter.StompServerStarter;
import org.limbo.stomp.server.starter.broker.PropertiesBrokerClientProvider;
import org.limbo.stomp.server.starter.config.props.StompBrokerProperties;
import org.limbo.stomp.server.starter.config.props.StompBrokerServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ServiceLoader;

/**
 * @author Brozen
 * @since 2021-04-06
 */
@Configuration
@EnableConfigurationProperties(StompBrokerProperties.class)
public class StompBrokerAutoConfiguration {

    /**
     * 配置一个StompBrokerServer
     */
    @Bean
    public StompBrokerServer stompServer(StompBrokerProperties brokerProperties) {
        StompBrokerServerProperties serverProperties = brokerProperties.getServer();
        StompBrokerServer stompBrokerServer = new StompBrokerServer(
                serverProperties.getPort(),
                serverProperties.getAcceptorCount(),
                serverProperties.getWorkerCount()
        );

        stompBrokerServer.setChannelInitializer(brokerClientChannelInitializer(brokerProperties));
        return stompBrokerServer;
    }

    /**
     * 声明一个客户端连接初始化器，会注册STOMP协议的解包、封包处理器
     */
    public StompBrokerClientChannelInitializer brokerClientChannelInitializer(StompBrokerProperties brokerProperties) {
        StompBrokerClientChannelInitializer channelInitializer = new StompBrokerClientChannelInitializer();

        // 通过SPI加载BrokerUserResolverFactory
        ServiceLoader<BrokerUserResolverFactory> serviceLoader = ServiceLoader.load(BrokerUserResolverFactory.class);
        BrokerUserResolverFactory brokerUserResolverFactory = serviceLoader.findFirst()
                .orElseGet(CachedBrokerUserResolverFactory::new);
        channelInitializer.setBrokerClientProvider(new PropertiesBrokerClientProvider(brokerProperties.getClients(), brokerUserResolverFactory));

        return channelInitializer;
    }

    /**
     * StompBrokerServer的启动器Bean，在SpringBean的声明周期中管理StompBrokerServer的声明周期
     */
    @Bean
    public StompServerStarter stompServerStarter(StompBrokerServer server) {
        return new StompServerStarter(server);
    }

}
