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

import io.netty.handler.codec.stomp.StompHeaders;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public interface BrokerUserResolver {

    /**
     * 从CONNECT帧或STOMP帧中解析BrokerUser，1.2版本的协议中，用STOMP帧代替CONNECT帧。
     * @param headers CONNECT帧或STOMP帧的头
     * @param payload CONNECT帧或STOMP帧的payload，如果有的话
     * @return 返回解析后的用户信息，解析失败返回null
     */
    BrokerUser resolve(StompHeaders headers, String payload);

}
