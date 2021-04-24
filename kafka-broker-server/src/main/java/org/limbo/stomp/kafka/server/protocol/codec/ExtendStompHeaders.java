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

package org.limbo.stomp.kafka.server.protocol.codec;

import io.netty.util.AsciiString;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public interface ExtendStompHeaders {

    /**
     * 帧所代表用户的userId
     */
    AsciiString BROKERED_USER = AsciiString.cached("x-brokered-user");

    /**
     * 帧所代表用户的sessionId
     */
    AsciiString ORIGIN_SESSION = AsciiString.cached("x-brokered-session");

    /**
     * 解析字符串格式的headerName为AsciiString
     */
    static AsciiString headerName(String headerName) {
        return AsciiString.cached(headerName);
    }

}
