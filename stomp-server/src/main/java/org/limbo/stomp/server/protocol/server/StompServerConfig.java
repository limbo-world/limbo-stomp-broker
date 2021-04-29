package org.limbo.stomp.server.protocol.server;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.limbo.stomp.server.utils.NumberVerifies;

/**
 * @author Brozen
 * @since 2021-04-29
 */
@Getter
@ToString
@EqualsAndHashCode
public class StompServerConfig {

    /**
     * STOMP服务启动监听的端口号
     */
    private final int port;

    /**
     * 连接加入处理线程池大小
     */
    private final int acceptorCount;

    /**
     * 数据包处理线程池大小
     */
    private final int workerCount;

    /**
     * STOMP消息体最大长度，单位byte。默认64KB。
     */
    private final long maxMessageContentLength;

    /**
     * 私有全参构造器，用于Builder调用
     */
    private StompServerConfig(int port, int acceptorCount, int workerCount, long maxMessageContentLength) {
        this.port = port;
        this.acceptorCount = acceptorCount;
        this.workerCount = workerCount;
        this.maxMessageContentLength = maxMessageContentLength;
    }

    /**
     * Builder模式
     */
    public static StompServerConfigBuilder builder() {
        return new StompServerConfigBuilder();
    }

    /**
     * STOMP 服务配置构建器
     */
    public static class StompServerConfigBuilder {
        private int port;
        private int acceptorCount;
        private int workerCount;
        private long maxMessageContentLength = 64 * 1024;

        StompServerConfigBuilder() {
        }

        /**
         * STOMP服务启动监听的端口号
         */
        public StompServerConfigBuilder port(int port) {
            this.port = NumberVerifies.positive(port);
            return this;
        }

        /**
         * 连接加入处理线程池大小
         */
        public StompServerConfigBuilder acceptorCount(int acceptorCount) {
            this.acceptorCount = NumberVerifies.positive(acceptorCount);
            return this;
        }

        /**
         * 数据包处理线程池大小
         */
        public StompServerConfigBuilder workerCount(int workerCount) {
            this.workerCount = NumberVerifies.positive(workerCount);
            return this;
        }

        /**
         * STOMP消息体最大长度，单位byte。默认64KB。
         */
        public StompServerConfigBuilder maxMessageContentLength(long maxMessageContentLength) {
            this.maxMessageContentLength = NumberVerifies.positive(maxMessageContentLength);
            return this;
        }

        /**
         * 生成STOMP服务配置
         */
        public StompServerConfig build() {
            return new StompServerConfig(port, acceptorCount, workerCount, maxMessageContentLength);
        }

    }
}
