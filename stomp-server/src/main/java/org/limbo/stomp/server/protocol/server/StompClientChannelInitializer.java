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

package org.limbo.stomp.server.protocol.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.stomp.StompSubframeAggregator;
import io.netty.handler.codec.stomp.StompSubframeDecoder;
import io.netty.handler.codec.stomp.StompSubframeEncoder;
import lombok.Setter;
import org.limbo.stomp.server.broker.client.BrokerClientProvider;
import org.limbo.stomp.server.protocol.handlers.StompMessageChannelHandler;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class StompClientChannelInitializer extends ChannelInitializer<Channel> {

    /**
     * Broker代理的STOMP服务器信息
     */
    @Setter
    private BrokerClientProvider brokerClientProvider;


    @Override
    protected void initChannel(Channel ch) {
        StompMessageChannelHandler brokerChannelHandler = new StompMessageChannelHandler();
        brokerChannelHandler.setBrokerClientProvider(brokerClientProvider);

        ch.pipeline()
                .addLast(new StompSubframeDecoder())
                .addLast(new StompSubframeAggregator(8132))
                .addLast(new StompSubframeEncoder())
                .addLast(brokerChannelHandler);
    }

}
