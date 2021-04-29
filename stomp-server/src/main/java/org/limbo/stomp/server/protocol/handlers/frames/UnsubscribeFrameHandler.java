package org.limbo.stomp.server.protocol.handlers.frames;

import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompHeaders;
import org.limbo.stomp.server.broker.user.BrokerUser;
import org.limbo.stomp.server.broker.user.BrokerUserResolver;
import org.limbo.stomp.server.protocol.handlers.ChannelHandlerContextDelegate;
import org.limbo.stomp.server.utils.Functions;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public class UnsubscribeFrameHandler extends AbstractStompFrameHandler {

    public UnsubscribeFrameHandler(ChannelHandlerContextDelegate channelHandlerContext) {
        super(StompCommand.UNSUBSCRIBE, channelHandlerContext);
    }

    /**
     * {@inheritDoc}处理取消订阅帧UNSUBSCRIBE。
     * @param headers 请求头
     * @param payload 负载数据
     */
    @Override
    public void process(StompHeaders headers, String payload) {

        // 解析brokerUser
        BrokerUserResolver userResolver = getBrokerUserResolver();
        BrokerUser user = userResolver.resolve(headers, payload);

        // 订阅ID
        Long id = headers.getLong(StompHeaders.ID);

        // 取消订阅
        user.unsubscribe(id).subscribe(
                Functions.emptyConsumer(),
                error -> this.doInEventLoop(ctx -> ctx.fireExceptionCaught(error))
        );
    }
}
