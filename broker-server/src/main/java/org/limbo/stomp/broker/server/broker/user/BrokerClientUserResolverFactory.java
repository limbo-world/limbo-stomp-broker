package org.limbo.stomp.broker.server.broker.user;

import io.netty.channel.Channel;
import org.limbo.stomp.server.broker.user.BrokerUserResolver;
import org.limbo.stomp.server.broker.user.CachedBrokerUserResolverFactory;

/**
 * @author Brozen
 * @since 2021-04-26
 */
public class BrokerClientUserResolverFactory extends CachedBrokerUserResolverFactory {

    @Override
    protected BrokerUserResolver doCreateBrokerUserResolver(Channel channel) {
        return new BrokerClientUserResolver(channel);
    }
}
