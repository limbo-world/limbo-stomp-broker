package org.limbo.stomp.server.utils;

import io.netty.channel.Channel;

import java.util.Objects;

/**
 * @author Brozen
 * @since 2021-04-27
 */
public class ChannelUtils {


    /**
     * 返回Channel的ID，长字符串，并移除中连接线
     * @param channel netty连接
     * @return netty连接的ID
     */
    public static String id(Channel channel) {
        return Objects.requireNonNull(channel, "channel")
                .id().asLongText()
                .replaceAll("-", "");
    }


}
