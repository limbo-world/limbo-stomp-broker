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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.limbo.stomp.server.broker.client.BrokerClientProvider;
import org.limbo.stomp.server.protocol.codec.StompFrames;
import org.limbo.stomp.server.protocol.handlers.exceptions.StompFrameProcessException;
import org.limbo.stomp.server.protocol.handlers.frames.*;
import org.limbo.stomp.server.protocol.server.StompServerConfig;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 * @since 2021-04-06
 */
@Slf4j
public class StompMessageChannelHandler extends ChannelInboundHandlerAdapter {

    /**
     * STOMP服务配置
     */
    @Setter
    private StompServerConfig serverConfig;

    /**
     * 客户端连接身份认证凭据
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


    public StompMessageChannelHandler() {
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

        // 客户端发送消息
        SendFrameHandler sendFrameHandler = new SendFrameHandler(channelHandlerContextDelegate);
        frameHandlers.put(StompCommand.SEND, sendFrameHandler);

        // 客户端订阅、取消订阅
        frameHandlers.put(StompCommand.SUBSCRIBE, new SubscribeFrameHandler(channelHandlerContextDelegate));
        frameHandlers.put(StompCommand.UNSUBSCRIBE, new UnsubscribeFrameHandler(channelHandlerContextDelegate));

        // TODO 消息确认
        // frameHandlers.put(StompCommand.ACK, null);
        // frameHandlers.put(StompCommand.NACK, null);

        // TODO 消息事务
        // frameHandlers.put(StompCommand.BEGIN, null);
        // frameHandlers.put(StompCommand.COMMIT, null);
        // frameHandlers.put(StompCommand.ABORT, null);

        // 断开连接
        frameHandlers.put(StompCommand.DISCONNECT, new DisconnectFrameHandler(channelHandlerContextDelegate));
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 设置代理ctx
        channelHandlerContextDelegate.delegate(ctx);

        StompFrame frame = (StompFrame) msg;
        log.info("收到STOMP帧 {}", frame);

        // 检测Content-Length
        StompHeaders headers = frame.headers();
        ByteBuf payload = frame.content();
        Integer contentLength = headers.getInt(StompHeaders.CONTENT_LENGTH);
        if (contentLength == null) {
            contentLength = payload.readableBytes();
        }
        if (contentLength > serverConfig.getMaxMessageContentLength()) {
            throw new StompFrameProcessException("accepted max content length is " + serverConfig.getMaxMessageContentLength());
        }

        // 解析STOMP帧处理器
        StompCommand command = frame.command();
        StompFrameHandler frameHandler = this.frameHandlers.getOrDefault(command, StompFrameHandler.UNSUPPORTED);

        // 解析payload
        String payloadString = payload.toString(StandardCharsets.UTF_8);

        // 执行处理器
        frameHandler.process(headers, payloadString);

        // 取消代理ctx
        channelHandlerContextDelegate.removeDelegate(ctx);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("[stomp.server] 未处理异常抛出 channel={} cause={}", ctx.channel(), cause);
        StompFrame errorFrame;
        if (cause instanceof StompFrameProcessException) {
            errorFrame = StompFrames.createErrorFrame(cause.getMessage());
        } else if (cause instanceof DecoderException) {
            errorFrame = StompFrames.createErrorFrame("Decode frame failed!");
        } else {
            errorFrame = StompFrames.createErrorFrame("Unknown Error: " + cause.getMessage());
        }

        ctx.writeAndFlush(errorFrame);
        ctx.disconnect();
    }

}
