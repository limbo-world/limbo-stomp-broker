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
public interface BrokerClientAuthenticator extends BrokerClientCredentialRepository {

    /**
     * 进行登录鉴权
     * @param loginName 登录用户名
     * @param password 登录密码
     * @return 是否登录成功
     */
    boolean authenticate(String loginName, String password);

}
