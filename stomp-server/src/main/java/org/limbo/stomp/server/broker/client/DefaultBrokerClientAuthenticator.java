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

import java.util.Objects;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class DefaultBrokerClientAuthenticator extends MemoryBrokerClientCredentialRepository implements BrokerClientAuthenticator {

    /**
     * {@inheritDoc}
     * @param loginName 登录用户名
     * @param password 登录密码
     * @return
     */
    @Override
    public boolean authenticate(String loginName, String password) {
        return getCredential(loginName)
                .map(credential -> Objects.equals(credential.getCredential(), password))
                .orElse(false);
    }

}
