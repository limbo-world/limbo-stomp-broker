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

package org.limbo.stomp.server.broker.user;

import java.util.Optional;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public interface BrokerUserRepository {

    /**
     * 注册一个user，user的host + userId是用户的唯一标识；如果已经存在相同user，不会覆盖，返回之前的user。
     * @param user 待注册的user
     * @return 返回注册的用户，如果已经存在相同的user，则返回之前的。
     */
    BrokerUser registerBrokerUser(BrokerUser user);

    /**
     * 查询user
     * @param userId 用户ID
     * @return 用户信息
     */
    Optional<BrokerUser> getBrokerUser(String userId);

}
