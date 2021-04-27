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

import org.limbo.stomp.server.broker.user.BrokerUserRepository;
import org.limbo.stomp.server.broker.user.MemoryBrokerUserRepository;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class DefaultBrokerClient extends DefaultBrokerClientAuthenticator implements BrokerClient {

    /**
     * client登录broker时的host
     */
    private String host;

    /**
     * 连接到broker的用户中，归属于此client的用户repository
     */
    private BrokerUserRepository brokerUserRepository;

    public DefaultBrokerClient(String host) {
        this(host, new MemoryBrokerUserRepository());
    }

    public DefaultBrokerClient(String host, BrokerUserRepository brokerUserRepository) {
        this.host = host;
        this.brokerUserRepository = brokerUserRepository;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getHost() {
        return host;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public BrokerUserRepository getBrokerUserRepository() {
        return brokerUserRepository;
    }

}
