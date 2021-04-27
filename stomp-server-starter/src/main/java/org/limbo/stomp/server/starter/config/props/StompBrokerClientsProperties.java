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

import java.util.List;

/**
 * @author Brozen
 * @since 2021-04-07
 */
@Data
public class StompBrokerClientsProperties {

    /**
     * client的host，每个client拥有一个host，该host唯一。多个被代理的STOMP服务器可以使用同一个host进行连接
     */
    private String host;

    /**
     * client连接到Broker使用的凭据
     */
    private List<Credential> credentials;

    /**
     * client认证凭据
     */
    @Data
    public static class Credential {
        /**
         * 认证登录用户名
         */
        private String username;

        /**
         * 认证登录密码
         */
        private String password;

        /**
         * 认证凭据类型
         */
        private CredentialType type = CredentialType.USER;
    }

    public enum CredentialType {
        /**
         * 被代理的STOMP服务器登录凭据
         */
        CLIENT,

        /**
         * 被代理的用户登录凭据
         */
        USER
    }

}
