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

import io.netty.channel.group.ChannelGroup;
import org.limbo.stomp.server.broker.messaging.MessageProcessor;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public interface BrokerUser extends MessageProcessor {

    /**
     * @return 获取用户所属app
     */
    String getHost();

    /**
     * @return 获取用户唯一标识
     */
    String getUserId();

    /**
     * @return 获取用户绑定的全部客户端channel
     */
    ChannelGroup channels();

}
