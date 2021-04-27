package org.limbo.stomp.broker.server.broker.user;

import io.netty.channel.Channel;
import lombok.Getter;
import org.limbo.stomp.server.broker.user.SimpBrokerUser;

/**
 * client系统连接用户
 *
 * @author Brozen
 * @since 2021-04-26
 */
public class BrokerClientUser extends SimpBrokerUser {

    /**
     * client系统连接
     */
    @Getter
    private Channel channel;

    public BrokerClientUser(String host, String userId, Channel channel) {
        super(host, userId, channel.eventLoop());
        this.channel = channel;
    }

}
