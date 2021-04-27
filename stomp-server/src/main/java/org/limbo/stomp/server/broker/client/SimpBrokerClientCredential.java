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

/**
 * 使用用户名、密码的client登录凭据
 *
 * @author Brozen
 * @since 2021-04-06
 */
public class SimpBrokerClientCredential implements BrokerClientCredential {

    /**
     * client登录用户名
     */
    private final String username;

    /**
     * client登录密码
     */
    private final String password;

    public SimpBrokerClientCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getLoginName() {
        return username;
    }

    @Override
    public Object getCredential() {
        return password;
    }
}
