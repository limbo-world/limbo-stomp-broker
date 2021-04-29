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
import java.util.Objects;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public class StompFrames {

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
     * 创建RECEIPT帧，在收到客户端DISCONNECT帧之后发送，发送完成后断开连接。
     * @param receiptId DISCONNECT确认ID
     * @return RECEIPT帧
     */
    public static StompFrame createReceiptFrame(Long receiptId) {
        Objects.requireNonNull(receiptId, "receiptId cannot be null");

        DefaultStompFrame frame = new DefaultStompFrame(StompCommand.RECEIPT);
        frame.headers().set(StompHeaders.RECEIPT_ID, receiptId.toString());

        return frame;
    }

}
