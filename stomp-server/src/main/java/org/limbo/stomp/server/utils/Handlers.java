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

package org.limbo.stomp.server.utils;

import io.netty.channel.*;

/**
 * @author Brozen
 * @since 2021-04-07
 */
public class Handlers {

    /**
     * 提供简单的方式，代理{@link ChannelInboundHandlerAdapter}中的实现
     */
    public static class Inbounds {

        /**
         * @param callback 回调函数，回调函数正常执行完成后，会调用{@link ChannelHandlerContext#fireChannelInactive()}，抛出异常时不会。
         * @see ChannelInboundHandlerAdapter#channelInactive(ChannelHandlerContext)
         */
        public static ChannelHandler whenInactive(ChannelHandlerContextConsumer callback) {
            return new ChannelInboundHandlerAdapter() {
                @Override
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                    callback.consume(ctx);
                    ctx.fireChannelInactive();
                }
            };
        }

    }


    /**
     * JDK自带Consumer不能抛出异常，重新定义
     */
    @FunctionalInterface
    public interface ChannelHandlerContextConsumer {
        void consume(ChannelHandlerContext context) throws Exception;
    }

}
