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

import org.limbo.utils.verifies.Verifies;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class MemoryBrokerUserRepository implements BrokerUserRepository {

    /**
     * 全部用户信息，key: host, value: Map{key: userId, value: BrokerUser}
     */
    private final Map<String, BrokerUser> users;

    public MemoryBrokerUserRepository() {
        this.users = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     * @param user 待注册的user
     * @return
     */
    @Override
    public BrokerUser registerBrokerUser(BrokerUser user) {
        Objects.requireNonNull(user, "user");
        Verifies.notBlank(user.getHost(), "user.host");
        Verifies.notBlank(user.getUserId(), "user.userId");

        return users.compute(user.getUserId(), (_uid, prev) -> prev == null ? user : prev);
    }

    /**
     * {@inheritDoc}
     * @param userId 用户ID
     * @return
     */
    @Override
    public Optional<BrokerUser> getBrokerUser(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

}

