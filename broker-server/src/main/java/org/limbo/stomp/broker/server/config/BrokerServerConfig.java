package org.limbo.stomp.broker.server.config;

import org.limbo.stomp.broker.server.broker.user.BrokerClientUserResolverFactory;
import org.limbo.stomp.server.broker.user.BrokerUserResolverFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Brozen
 * @since 2021-04-29
 */
@Configuration
public class BrokerServerConfig {

    /**
     * 用户解析器工厂，可以解析BrokerClient的系统连接
     */
    @Bean
    public BrokerUserResolverFactory brokerUserResolverFactory() {
        return new BrokerClientUserResolverFactory();
    }

}
