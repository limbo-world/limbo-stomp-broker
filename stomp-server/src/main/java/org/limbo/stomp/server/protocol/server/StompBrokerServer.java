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

package org.limbo.stomp.server.protocol.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.limbo.stomp.server.utils.NumberVerifies;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static org.limbo.stomp.server.protocol.server.StompBrokerServer.StompBrokerServerState.*;

/**
 * @author Brozen
 * @since 2021-04-06
 */
@Slf4j
public class StompBrokerServer {

    private final int port;

    /**
     * 连接加入处理线程池大小
     */
    private final Integer acceptorCount;

    /**
     * 连接加入处理线程池
     */
    private NioEventLoopGroup acceptorGroup;

    /**
     * 数据包处理线程池大小
     */
    private final Integer workerCount;

    /**
     * 数据包处理线程池
     */
    private NioEventLoopGroup workerGroup;

    /**
     * 服务启动Future
     */
    private ChannelFuture startFuture;

    /**
     * 服务关闭Future
     */
    private ChannelFuture closeFuture;

    /**
     * 被代理STOMP服务器连接的初始化器
     */
    @Setter
    private StompBrokerClientChannelInitializer channelInitializer;

    /**
     * 代理服务器的状态
     */
    @Getter
    private volatile StompBrokerServerState state;

    private static final AtomicReferenceFieldUpdater<StompBrokerServer, StompBrokerServerState> stateUpdater
            = AtomicReferenceFieldUpdater.newUpdater(StompBrokerServer.class, StompBrokerServerState.class, "state");

    /**
     * @param port 服务启动监听的端口号
     * @param acceptorCount 连接加入处理线程池大小
     * @param workerCount 数据包处理线程池大小
     */
    public StompBrokerServer(int port, int acceptorCount, int workerCount) {
        this.port = port;
        this.acceptorCount = NumberVerifies.positive(acceptorCount);
        this.workerCount = NumberVerifies.positive(workerCount);
        this.state = INITIALIZED;
    }

    /**
     * 启动STOMP服务
     * @return 返回服务启动Future，方法调用返回时，可能没有启动成功
     */
    public ChannelFuture start() {
        // 已停止禁止再次启动
        if (state == STOP || state == TERMINATED) {
            throw new IllegalStateException("服务器已经停止 state=" + state);
        }

        // 重复启动处理
        if (!stateUpdater.compareAndSet(this, INITIALIZED, STARTING)) {
            return startFuture;
        }

        // 线程池
        acceptorGroup = new NioEventLoopGroup(acceptorCount);
        workerGroup = new NioEventLoopGroup(workerCount);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(acceptorGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(channelInitializer);

        startFuture = bootstrap.bind(port);
        startFuture.addListener(f -> {
            log.info("Server启动成功 at {}", port);
            if (!stateUpdater.compareAndSet(this, STARTING, RUNNING)) {
                log.warn("Broker服务状态异常，启动成功，state={}", state);
            }
        });

        return startFuture;
    }


    /**
     * 停止STOMP代理服务
     * @return 返回关闭服务Future，返回时可能没有关闭成功
     */
    public ChannelFuture close() {
        if (state == INITIALIZED) {
            throw new IllegalStateException("代理服务尚未启动");
        }

        // 已经停止直接返回
        if (state == STOP || state == TERMINATED) {
            return closeFuture;
        }

        try {
            // 在启动中等待启动完成后停止
            if (state == STARTING) {
                startFuture.sync();
            }

            stateUpdater.set(this, STOP);
            closeFuture = this.doClose();
            closeFuture.addListener(f -> {
                log.info("Server已停止");
                if (!stateUpdater.compareAndSet(this, STOP, TERMINATED)) {
                    log.warn("Broker服务状态异常，启动成功，state={}", state);
                }
            });

            return closeFuture;
        } catch (InterruptedException e) {
            log.error("启动STOMP服务失败！", e);
            throw new IllegalStateException("启动STOMP服务失败！", e);
        }

    }

    /**
     * 执行停止代理服务逻辑
     */
    private ChannelFuture doClose() throws InterruptedException {
        // 如果在启动流程中，先等待启动完成
        startFuture.sync();

        // 关闭channel
        Channel serverChannel = startFuture.channel();
        serverChannel.close();

        // 在关闭channel之后关闭线程池
        ChannelFuture closeFuture = serverChannel.closeFuture();
        closeFuture.addListener((ChannelFutureListener) f -> {
            if (acceptorGroup != null) {
                acceptorGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        });

        return closeFuture;
    }


    /**
     * STOMP代理服务器状态
     */
    enum StompBrokerServerState {

        /**
         * 初始化，默认状态
         */
        INITIALIZED,

        /**
         * 代理服务启动中
         */
        STARTING,

        /**
         * 代理服务启动完成，正在运行中
         */
        RUNNING,

        /**
         * 代理服务已关闭，正在销毁流程中
         */
        STOP,

        /**
         * 代理服务已停止，此时服务完全停止
         */
        TERMINATED,
    }
}
