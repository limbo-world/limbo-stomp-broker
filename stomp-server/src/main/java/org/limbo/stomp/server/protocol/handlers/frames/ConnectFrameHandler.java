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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.limbo.stomp.server.broker.client.BrokerClient;
import org.limbo.stomp.server.broker.client.BrokerClientProvider;
import org.limbo.stomp.server.broker.user.BrokerUser;
import org.limbo.stomp.server.broker.user.BrokerUserResolver;
import org.limbo.stomp.server.broker.user.BrokerUserResolverFactory;
import org.limbo.stomp.server.protocol.codec.StompFrameUtils;
import org.limbo.stomp.server.protocol.handlers.ChannelHandlerContextDelegate;
import org.limbo.stomp.server.protocol.handlers.StompHeartBeatReceiver;
import org.limbo.stomp.server.utils.ChannelUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Broker收到CONNECT帧后，执行如下流程：
 * 1. 校验CONNECT帧中的鉴权参数，login和passcode，通过返回CONNECTED帧，否则返回ERROR帧拒绝连接
 * 2. 注册此连接代表的用户到BrokerUserRepository，用于处理
 *
 * 注意：STOMP1.2版本后，使用STOMP帧来代替CONNECT帧，但STOMP服务器可以同时解析STOMP和CONNECT帧，来兼容1.2之前的协议版本。
 *
 * @author Brozen
 * @since 2021-04-06
 */
@Slf4j
public class ConnectFrameHandler extends AbstractStompFrameHandler {

    /**
     * 可处理的STOMP协议版本
     */
    private static final Set<String> ACCEPT_VERSIONS = Set.of("1.1", "1.2");

    /**
     * 无心跳
     */
    private static final Tuple2<Long, Long> NO_HEART_BEAT = Tuples.of(0L, 0L);

    /**
     * 默认心跳速率，10秒发送，10秒接收
     */
    private static final Tuple2<Long, Long> DEFAULT_HEART_BEAT = Tuples.of(10000L, 10000L);

    /**
     * 用于获取 被代理的STOMP服务器应用信息
     */
    @Setter
    private BrokerClientProvider brokerClientProvider;

    public ConnectFrameHandler(ChannelHandlerContextDelegate channelHandlerContext) {
        super(channelHandlerContext);
    }

    /**
     * @see StompFrameHandler#processedCommand()
     */
    @Override
    public StompCommand processedCommand() {
        return StompCommand.CONNECT;
    }

    /**
     * {@inheritDoc}
     * @param headers 请求头
     * @param payload 负载数据
     */
    @Override
    public void process(StompHeaders headers, String payload) {
        StompFrame returnFrame;

        // 检测STOMP协议版本是否支持
        List<String> acceptVersions = parseAcceptVersionHeader(headers);
        if (acceptVersions.stream().noneMatch(ACCEPT_VERSIONS::contains)) {

            // 不接受的版本号
            returnFrame = StompFrameUtils.createErrorFrame("version " + acceptVersions + " is not supported!");

        } else {

            // 正常处理CONNECT帧
            returnFrame = processConnectFrame(headers, payload);
        }

        ChannelHandlerContext context = getChannelHandlerContext();
        // 认证成功，启动心跳定时任务，设置返回header
        if (returnFrame.command() == StompCommand.CONNECTED) {

            // 应用心跳
            applyHeartBeat(headers, returnFrame);

            // 发送CONNECTED帧
            context.writeAndFlush(returnFrame);

            if (log.isDebugEnabled()) {
                log.debug("STOMP连接建立，response={}", returnFrame);
            }

            return;
        }

        // 认证失败，发送ERROR帧，关闭连接
        context.writeAndFlush(returnFrame);
        context.channel().close();
    }


    /**
     * 从帧头中解析accept-version，如果不存在则返回空列表；多个版本号之间使用英文逗号分隔。
     * @param headers 帧头部
     * @return 返回accept-version头中指定的STOMP协议版本
     */
    protected List<String> parseAcceptVersionHeader(StompHeaders headers) {
        String acceptVersion = headers.getAsString(StompHeaders.ACCEPT_VERSION);
        if (StringUtils.isBlank(acceptVersion)) {
            return Collections.emptyList();
        }

        return Arrays.asList(acceptVersion.split(","));
    }

    /**
     * 处理连接帧
     * @param headers STOMP帧头
     * @param payload STOMP帧payload
     * @return 返回给client的帧
     */
    protected StompFrame processConnectFrame(StompHeaders headers, String payload) {
        // 校验CONNECT帧中的鉴权参数，login和passcode，通过返回CONNECTED帧，否则返回DISCONNECT
        String host = headers.getAsString(StompHeaders.HOST);

        // 进行登录认证：首先根据host头查询Client；然后根据Client提供的鉴权数据进行认证；
        // 认证通过则根据BrokerUserResolver解析被代理的用户，注册到repository，并返回CONNECTED帧；
        // 认证失败则返回ERROR帧；
        return Optional.ofNullable(host)
                .flatMap(hs -> brokerClientProvider.getBrokerClient(hs))
                .map(brokerClient -> doProcessConnectFrame(headers, payload, brokerClient))
                .orElseGet(() -> StompFrameUtils.createErrorFrame("Authentication Failed"));
    }

    /**
     * 执行CONNECT帧处理逻辑，并处理异常
     * @param headers CONNECT帧头
     * @param payload CONNECT帧body
     * @param brokerClient 从host头部解析出的{@link BrokerClient}
     * @return 返回处理结果帧
     */
    private StompFrame doProcessConnectFrame(StompHeaders headers, String payload, BrokerClient brokerClient) {

        // 解析连接帧中的认证参数
        String loginName = headers.getAsString(StompHeaders.LOGIN);
        loginName = loginName == null ? "" : loginName;
        String password = headers.getAsString(StompHeaders.PASSCODE);

        if (brokerClient.authenticate(loginName, password)) {

            try {
                // 获取用户解析器
                ChannelHandlerContext context = getChannelHandlerContext();
                BrokerUserResolverFactory userResolverFactory = brokerClientProvider.getBrokerUserResolverFactory();
                BrokerUserResolver brokerUserResolver = userResolverFactory.getResolver(getChannelHandlerContext().channel());

                // 登录认证成功，注册Broker用户信息
                BrokerUser user = brokerUserResolver.resolve(headers, payload);
                user = brokerClient.getBrokerUserRepository().registerBrokerUser(user);

                // 绑定Channel
                user.channels().add(context.channel());

                // TODO 使用channelId作为session
                StompFrame connectedFrame = createConnectedFrame();
                connectedFrame.headers().set(StompHeaders.SESSION, ChannelUtils.id(context.channel()));

                // 设置Server帧
                connectedFrame.headers().set(StompHeaders.SERVER, "LimboBroker/1.0.0-Alpha");

                // 返回CONNECTED帧
                return connectedFrame;
            } catch (Exception e) {
                log.error("用户登录认证失败！", e);
                return StompFrameUtils.createErrorFrame("Authentication Failed: " + e.getMessage());
            }

        } else {
            // 认证失败，返回ERROR帧
            return StompFrameUtils.createErrorFrame("Authentication Failed");
        }
    }

    /**
     * 创建一个CONNECTED连接成功的帧
     */
    protected StompFrame createConnectedFrame() {
        return new DefaultStompFrame(StompCommand.CONNECTED, Unpooled.buffer(0));
    }


    /**
     * 应用broker与client之间的心跳。
     * @param headers client连接帧头部
     * @param connectedFrame 响应给client的{@code CONNECTED}帧
     */
    private void applyHeartBeat(StompHeaders headers, StompFrame connectedFrame) {
        Tuple2<Long, Long> clientHeartBeat = parseClientHeartBeat(headers);
        Tuple2<Long, Long> serverHeartBeat = startHeatBeatCheck(clientHeartBeat);
        connectedFrame.headers().set(StompHeaders.HEART_BEAT,
                String.format("%s,%s", serverHeartBeat.get(0), serverHeartBeat.get(1)));
    }


    /**
     * 启动心跳检测与心跳发送
     * @param heatBeat 客户端传入的心跳速率
     * @return 返回服务端实际应用的心跳速率
     */
    protected Tuple2<Long, Long> startHeatBeatCheck(Tuple2<Long, Long> heatBeat) {
        ChannelHandlerContext context = getChannelHandlerContext();
        long cx = heatBeat.getT1();
        long cy = heatBeat.getT2();
        long sx = DEFAULT_HEART_BEAT.getT1();
        long sy = DEFAULT_HEART_BEAT.getT2();

        // 启动向客户端发送心跳
        if (cy > 0 && sx >= 0) {
            sx = Math.max(cy, sx);
            // 防止网络问题或处理超时，服务端实际发送心跳速率是真实速率的2倍
            sx = sx / 2;
            context.executor().scheduleAtFixedRate(
                    () -> context.write(StompFrameUtils.createHeartBeatFrame()), sx, sx, TimeUnit.MILLISECONDS);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("不向客户端发送心跳 client={} server={}", heatBeat, DEFAULT_HEART_BEAT);
            }
            sx = 0L;
        }

        // 启动客户端心跳接收监听
        if (cx > 0 && sy > 0) {
            sy = Math.max(cx, sy);
            // 防止网络问题或处理超时，服务端实际接收心跳检测时长是真实时长的1.5倍
            sy = (long) (sy * 1.5f);
            context.pipeline().addLast(StompHeartBeatReceiver.NAME, new StompHeartBeatReceiver(sy));
        } else {
            if (log.isDebugEnabled()) {
                log.debug("不从客户端接收心跳 client={} server={}", heatBeat, DEFAULT_HEART_BEAT);
            }
            sy = 0L;
        }

        return Tuples.of(sx, sy);
    }


    /**
     * 从header中解析heart-beat
     * @param headers 帧头部
     * @return 返回解析后的心跳速率，如果没有heart-beat，按照1.2协议将视为 [0,0]
     */
    protected Tuple2<Long, Long> parseClientHeartBeat(StompHeaders headers) {

        String heatBeat = headers.getAsString(StompHeaders.HEART_BEAT);
        if (StringUtils.isBlank(heatBeat)) {
            return NO_HEART_BEAT;
        }

        // heart-beat心跳设置不合法，直接忽略
        String[] heatBeats = heatBeat.split(",");
        if (heatBeats.length != 2 || !NumberUtils.isCreatable(heatBeats[0]) || !NumberUtils.isCreatable(heatBeats[1])) {
            return NO_HEART_BEAT;
        }

        // 再次检测合法性
        Long cx = NumberUtils.createLong(heatBeats[0]);
        Long cy = NumberUtils.createLong(heatBeats[1]);
        if (cx < 0 && cy < 0) {
            return NO_HEART_BEAT;
        }

        return Tuples.of(cx, cy);
    }

}
