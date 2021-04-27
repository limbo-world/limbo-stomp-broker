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

import java.util.Iterator;
import java.util.Optional;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public interface BrokerClientCredentialRepository extends Iterable<BrokerClientCredential> {

    /**
     * 新增一个Broker的客户端登录凭据，重复add不会覆盖。
     * @param credential 包括登录用户名、登录凭据
     * @return 返回是否新增成功，如果已经存在相同loginName的登录凭据，添加会失败并返回之前的credential
     */
    BrokerClientCredential addCredential(BrokerClientCredential credential);

    /**
     * 根据登录用户名查询登录凭据
     * @param loginName 登录用户名
     * @return 登录凭据，如果不存在返回null
     */
    Optional<BrokerClientCredential> getCredential(String loginName);

    /**
     * 返回所有的凭据迭代器，返回时调用方法时的所有凭据快照，可能并非预期的全部凭据。
     */
    @Override
    Iterator<BrokerClientCredential> iterator();
}
