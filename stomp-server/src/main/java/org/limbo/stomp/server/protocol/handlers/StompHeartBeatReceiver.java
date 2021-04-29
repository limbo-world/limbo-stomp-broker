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
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.limbo.stomp.server.protocol.codec.StompFrames;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * STOMP心跳帧发送
 *
 * @author Brozen
 * @since 2021-04-06
 */
public class StompHeartBeatReceiver extends ChannelInboundHandlerAdapter {

    public static final String NAME = "StompHeartBeatReceiver";

    /**
     * 心跳间隔，毫秒
     */
    private long heartBeatInterval;

    /**
     * 上次收到心跳时间戳
     */
    private long lastHeartBeatTimestamp;

    /**
     * 心跳检测定时任务的future，用于关闭定时任务
     */
    private ScheduledFuture<?> heartBeatCheckTaskFuture;

    public StompHeartBeatReceiver(long heartBeatInterval) {
        this.heartBeatInterval = heartBeatInterval;
        this.lastHeartBeatTimestamp = 0L;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO 需确认，后来加入的ChannelHandler是否能会触发channelActive
        if (heartBeatInterval > 0) {
            lastHeartBeatTimestamp = System.currentTimeMillis();
            heartBeatCheckTaskFuture = ctx.executor().scheduleAtFixedRate(() -> {
                long now = System.currentTimeMillis();
                if (heartBeatInterval + lastHeartBeatTimestamp < now) {
                    // 心跳超时，写出ERROR帧
                    ctx.writeAndFlush(StompFrames.createErrorFrame("Heartbeat Timeout"));

                    // 移除心跳接受检测ChannelHandler
                    ctx.pipeline().remove(NAME);

                    // 停止定时任务检测
                    if (!heartBeatCheckTaskFuture.isCancelled()) {
                        heartBeatCheckTaskFuture.cancel(false);
                    }

                    // 关闭连接
                    ctx.channel().close();
                }
            }, 0, heartBeatInterval, TimeUnit.MILLISECONDS);

        }


        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (heartBeatInterval > 0) {
            lastHeartBeatTimestamp = System.currentTimeMillis();
        }

        super.channelRead(ctx, msg);
    }
}
