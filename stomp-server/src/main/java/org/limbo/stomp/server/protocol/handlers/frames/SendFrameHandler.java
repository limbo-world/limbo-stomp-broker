package org.limbo.stomp.server.protocol.handlers.frames;

import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompHeaders;
import org.limbo.stomp.server.broker.messaging.SimpStompMessage;
import org.limbo.stomp.server.broker.user.BrokerUser;
import org.limbo.stomp.server.broker.user.BrokerUserResolver;
import org.limbo.stomp.server.protocol.handlers.ChannelHandlerContextDelegate;
import org.limbo.stomp.server.utils.Functions;
import org.limbo.stomp.server.utils.StringVerifies;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public class SendFrameHandler extends AbstractStompFrameHandler {

    public SendFrameHandler(ChannelHandlerContextDelegate channelHandlerContext) {
        super(StompCommand.SEND, channelHandlerContext);
    }

    /**
     * {@inheritDoc}处理SEND帧。
     * @param headers 请求头
     * @param payload 负载数据
     */
    @Override
    public void process(StompHeaders headers, String payload) {
        // 解析user
        BrokerUserResolver userResolver = getBrokerUserResolver();
        BrokerUser user = userResolver.resolve(headers, payload);

        // 解析消息发送到的destination
        String destination = headers.getAsString(StompHeaders.DESTINATION);
        StringVerifies.requireNoneBlank(destination, "destination header required");

        // 组装Message并发送
        SimpStompMessage stompMessage = new SimpStompMessage();
        stompMessage.setDestination(destination);
        stompMessage.setHeaders(headersToMap(headers));
        stompMessage.setPayload(payload);

        // TODO transaction是否在此时处理?

        // 如果发送失败，则抛出异常
        user.send(stompMessage).subscribe(
                Functions.emptyConsumer(),
                error -> doInEventLoop(ctx -> ctx.fireExceptionCaught(error))
        );

    }
}
