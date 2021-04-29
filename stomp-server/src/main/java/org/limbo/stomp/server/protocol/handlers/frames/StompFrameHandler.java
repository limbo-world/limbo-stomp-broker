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

package org.limbo.stomp.server.protocol.handlers.frames;

import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompHeaders;
import org.limbo.stomp.server.protocol.handlers.exceptions.StompFrameProcessException;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public interface StompFrameHandler {

    /**
     * 什么都不干的处理器
     */
    StompFrameHandler IDLE = (headers, payload) -> { };

    /**
     * 抛出不支持异常的处理器
     */
    StompFrameHandler UNSUPPORTED = (headers, payload) -> {
        throw new StompFrameProcessException("不支持的command");
    };

    /**
     * 获取处理器要处理的STOMP帧 command
     * @return 处理器要处理的STOMP帧 command
     */
    default StompCommand processedCommand() {
        return StompCommand.UNKNOWN;
    }

    /**
     * 处理STOMP帧。
     * @param headers 请求头
     * @param payload 负载数据
     */
    void process(StompHeaders headers, String payload);


}
