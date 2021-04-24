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

package org.limbo.stomp.kafka.server.protocol.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.limbo.stomp.kafka.server.protocol.handlers.frames.ConnectFrameHandler;
import org.limbo.stomp.kafka.server.protocol.handlers.frames.StompFrameHandler;
import org.limbo.stomp.kafka.server.broker.BrokerClientProvider;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 * @since 2021-04-06
 */
@Slf4j
public class StompBrokerMessageChannelHandler extends ChannelInboundHandlerAdapter {

    /**
     * Broker的客户端连接身份认证凭据
     */
    @Setter
    private BrokerClientProvider brokerClientProvider;

    /**
     * STOMP帧处理器
     */
    private Map<StompCommand, StompFrameHandler> frameHandlers;

    /**
     * ChannelHandlerContext代理
     */
    private ChannelHandlerContextDelegate channelHandlerContextDelegate;


    public StompBrokerMessageChannelHandler() {
        this.frameHandlers = new ConcurrentHashMap<>();
        this.channelHandlerContextDelegate = new ChannelHandlerContextDelegate();
    }

    /**
     * 在STOMP服务器连接到Broker的时候再注册帧解析器
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        registerDefaultFrameHandlers();

        super.channelActive(ctx);
    }

    /**
     * 注册内置STOMP帧处理器
     */
    private void registerDefaultFrameHandlers() {
        ConnectFrameHandler connectFrameHandler = new ConnectFrameHandler(channelHandlerContextDelegate);
        connectFrameHandler.setBrokerClientProvider(brokerClientProvider);
        frameHandlers.put(StompCommand.STOMP, connectFrameHandler);
        frameHandlers.put(StompCommand.CONNECT, connectFrameHandler);

        // TODO
        // frameHandlers.put(StompCommand.SEND, null);
        // frameHandlers.put(StompCommand.SUBSCRIBE, null);
        // frameHandlers.put(StompCommand.UNSUBSCRIBE, null);
        // frameHandlers.put(StompCommand.ACK, null);
        // frameHandlers.put(StompCommand.NACK, null);
        // frameHandlers.put(StompCommand.DISCONNECT, null);
        // frameHandlers.put(StompCommand.MESSAGE, null);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 设置代理ctx
        channelHandlerContextDelegate.delegate(ctx);

        StompFrame frame = (StompFrame) msg;
        log.info("收到STOMP帧 {}", frame);

        // 解析STOMP帧处理器
        StompCommand command = frame.command();
        StompFrameHandler frameHandler = this.frameHandlers.getOrDefault(command, StompFrameHandler.IDLE);

        // 解析payload
        ByteBuf payload = frame.content();
        String payloadString = payload.toString(StandardCharsets.UTF_8);

        // 执行处理器
        frameHandler.process(frame.headers(), payloadString);

        // 取消代理ctx
        channelHandlerContextDelegate.removeDelegate(ctx);

        super.channelRead(ctx, msg);
    }
}
