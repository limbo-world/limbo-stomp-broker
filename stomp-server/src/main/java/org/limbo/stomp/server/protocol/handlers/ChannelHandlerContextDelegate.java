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

package org.limbo.stomp.server.protocol.handlers;

import io.netty.channel.ChannelHandlerContext;
import lombok.experimental.Delegate;

/**
 * ChannelHandlerContext代理，可动态替换。
 * 多线程不安全，需保证单线程调用
 *
 * @author Brozen
 * @since 2021-04-06
 */
public class ChannelHandlerContextDelegate implements ChannelHandlerContext {

    @Delegate
    private ChannelHandlerContext delegated;

    public ChannelHandlerContextDelegate() {
    }

    /**
     * 设置代理一个ChannelHandlerContext
     * @param delegated 被代理的ChannelHandlerContext
     */
    public void delegate(ChannelHandlerContext delegated) {
        this.delegated = delegated;
    }

    /**
     * 取消对ChannelHandlerContext的代理
     * @param delegated 当前被代理的ChannelHandlerContext，如果入参不是当前被代理的，会抛出异常
     */
    public void removeDelegate(ChannelHandlerContext delegated) {
        if (this.delegated != delegated) {
            throw new IllegalStateException("当前代理的 ChannelHandlerContext 与入参不符！");
        }

        this.delegated = null;
    }

    /**
     * 断言被代理的ChannelHandlerContext存在
     */
    public void assetDelegated() {
        if (delegated == null) {
            throw new IllegalStateException("被代理的 ChannelOutBoundInvoker 是 null!");
        }
    }

}
