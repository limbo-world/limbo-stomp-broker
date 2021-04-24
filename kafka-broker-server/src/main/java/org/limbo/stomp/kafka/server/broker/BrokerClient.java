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

package org.limbo.stomp.kafka.server.broker;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public interface BrokerClient extends BrokerClientAuthenticator {

    /**
     * 获取被代理的STOMP服务器的host
     */
    String getHost();

    /**
     * 获取用于 STOMP客户端登录鉴权 的repository
     */
    BrokerUserRepository getBrokerUserRepository();

}
