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

package org.limbo.stomp.server.starter.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Brozen
 * @since 2021-04-06
 */
@Data
@ConfigurationProperties("broker")
public class StompBrokerProperties {

    /**
     * Broker服务器配置
     */
    private StompBrokerServerProperties server;

    /**
     * 被代理的client配置
     */
    private List<StompBrokerClientsProperties> clients;

    /**
     * Broker kafka相关的配置
     */
    private StompBrokerKafkaProperties broker;

}
