package org.limbo.stomp.broker.server.broker.user;

import io.netty.channel.Channel;
import io.netty.handler.codec.stomp.StompHeaders;
import lombok.Getter;
import org.limbo.stomp.server.broker.user.BrokerUser;
import org.limbo.stomp.server.broker.user.HeaderBrokerUserResolver;
import org.limbo.stomp.server.utils.ChannelUtils;

/**
 * @author Brozen
 * @since 2021-04-26
 */
public class BrokerClientUserResolver extends HeaderBrokerUserResolver {

    /**
     * client服务器的系统连接
     */
    @Getter
    private Channel channel;

    public BrokerClientUserResolver(Channel channel) {
        super(channel.eventLoop());
        this.channel = channel;
    }

    /**
     * {@inheritDoc}<br/>
     *
     * 当调用父类方法无法解析出用户时，则说明是BrokerClient的连接
     *
     * @param headers CONNECT帧或STOMP帧的头
     * @param payload CONNECT帧或STOMP帧的payload，如果有的话
     * @return
     */
    @Override
    public BrokerUser resolve(StompHeaders headers, String payload) {
        BrokerUser brokerUser = super.resolve(headers, payload);

        if (brokerUser == null) {
            String host = headers.getAsString(StompHeaders.HOST);
            brokerUser = new BrokerClientUser(host, "client#" + ChannelUtils.id(channel), channel);
        }

        return brokerUser;
    }
}
