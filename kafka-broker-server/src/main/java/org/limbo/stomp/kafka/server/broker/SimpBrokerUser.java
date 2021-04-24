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

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class SimpBrokerUser implements BrokerUser {

    protected final String host;

    protected final String userId;

    protected final ChannelGroup userChannels;

    public SimpBrokerUser(String host, String userId, EventExecutor executor) {
        this.host = host;
        this.userId = userId;

        String groupName = String.format("userChannels-%s-%s", host, userId);
        this.userChannels = new DefaultChannelGroup(groupName, executor);
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public ChannelGroup channels() {
        return this.userChannels;
    }
}
