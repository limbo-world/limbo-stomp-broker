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

import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.EventExecutor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.limbo.stomp.server.protocol.codec.ExtendStompHeaders;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class HeaderBrokerUserResolver implements BrokerUserResolver {

    /**
     * 用于创建ChannelGroup
     */
    @Getter
    private EventExecutor executor;

    /**
     * 代表userId的header
     */
    private AsciiString userIdHeader = ExtendStompHeaders.BROKERED_USER;

    public HeaderBrokerUserResolver(EventExecutor executor) {
        this.executor = executor;
    }

    /**
     * 设置 代表userId的header
     */
    public void setUserIdHeader(String userIdHeader) {
        this.userIdHeader = AsciiString.cached(userIdHeader);
    }

    /**
     * {@inheritDoc}
     * @param headers CONNECT帧或STOMP帧的头
     * @param payload CONNECT帧或STOMP帧的payload，如果有的话
     * @return
     */
    @Override
    public BrokerUser resolve(StompHeaders headers, String payload) {
        String host = headers.getAsString(StompHeaders.HOST);
        String userId = headers.getAsString(userIdHeader);

        if (StringUtils.isBlank(host)) {
            throw new IllegalStateException("未知的host！");
        }

        if (StringUtils.isBlank(userId)) {
            return null;
        }

        return new SimpBrokerUser(host, userId, executor);
    }
}
