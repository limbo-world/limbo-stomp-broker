package org.limbo.stomp.broker.server;

import org.limbo.stomp.server.starter.config.StompBrokerAutoConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Brozen
 * @since 2021-04-26
 */
@SpringBootApplication
@ImportAutoConfiguration(StompBrokerAutoConfiguration.class)
public class BrokerServerBootstrap {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .web(WebApplicationType.NONE)
                .main(BrokerServerBootstrap.class)
                .sources(BrokerServerBootstrap.class)
                .build()
                .run(args);
    }

}
