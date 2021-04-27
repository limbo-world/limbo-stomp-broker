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

package org.limbo.stomp.server.protocol.codec;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public class StompFrameUtils {

    private static final byte[] EMPTY_BODY = new byte[0];

    /**
     * 创建ERROR帧
     * @param errorMessage error错误信息，会设置到给 {@linkplain StompHeaders#MESSAGE message} header
     * @return ERROR帧
     */
    public static StompFrame createErrorFrame(String errorMessage) {
        return createErrorFrame(errorMessage, null);
    }

    /**
     * 创建ERROR帧
     * @param errorMessage error错误信息，会设置到给 {@linkplain StompHeaders#MESSAGE message} header
     * @param errorBody payload
     * @return ERROR帧
     */
    public static StompFrame createErrorFrame(String errorMessage, String errorBody) {
        byte[] body;
        if (StringUtils.isNotBlank(errorBody)) {
            body = EMPTY_BODY;
        } else {
            body = errorBody.getBytes(StandardCharsets.UTF_8);
        }

        DefaultStompFrame frame = new DefaultStompFrame(StompCommand.ERROR, Unpooled.wrappedBuffer(body));
        StompHeaders headers = frame.headers();

        if (StringUtils.isNotBlank(errorMessage)) {
            headers.set(StompHeaders.MESSAGE, errorMessage);
        }

        if (body.length > 0) {
            headers.set(StompHeaders.CONTENT_TYPE, "text/plain");
        }

        return frame;
    }


    /**
     * TODO 创建心跳帧
     *
     * @return 心跳帧
     */
    public static StompFrame createHeartBeatFrame() {
        return null;
    }

}
