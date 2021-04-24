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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public class MemoryBrokerClientCredentialRepository implements BrokerClientCredentialRepository {

    /**
     * 全部登录凭据
     */
    private final Map<String, BrokerClientCredential> credentials;

    public MemoryBrokerClientCredentialRepository() {
        this.credentials = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     * @param credential 包括登录用户名、登录凭据
     * @return
     */
    @Override
    public BrokerClientCredential addCredential(BrokerClientCredential credential) {
        Objects.requireNonNull(credential, "credential");

        String loginName = credential.getLoginName();
        return credentials.compute(credential.getLoginName(), (_ln, prev) -> prev == null ? credential : prev);
    }

    /**
     * {@inheritDoc}
     * @param loginName 登录用户名
     * @return
     */
    @Override
    public Optional<BrokerClientCredential> getCredential(String loginName) {
        return Optional.ofNullable(credentials.get(loginName));
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Iterator<BrokerClientCredential> iterator() {
        return new ArrayList<>(credentials.values()).iterator();
    }

}
