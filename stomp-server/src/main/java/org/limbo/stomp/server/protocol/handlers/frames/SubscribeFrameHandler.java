package org.limbo.stomp.server.protocol.handlers.frames;

import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompHeaders;
import org.limbo.stomp.server.broker.messaging.MessageSubscription;
import org.limbo.stomp.server.broker.user.BrokerUser;
import org.limbo.stomp.server.broker.user.BrokerUserResolver;
import org.limbo.stomp.server.protocol.codec.SubscribeAcknowledges;
import org.limbo.stomp.server.protocol.handlers.ChannelHandlerContextDelegate;
import org.limbo.stomp.server.utils.StringVerifies;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public class SubscribeFrameHandler extends AbstractStompFrameHandler {

    public SubscribeFrameHandler(ChannelHandlerContextDelegate channelHandlerContext) {
        super(StompCommand.SUBSCRIBE, channelHandlerContext);
    }

    /**
     * {@inheritDoc}处理SUBSCRIBE帧。
     * @param headers 请求头
     * @param payload 负载数据
     */
    @Override
    public void process(StompHeaders headers, String payload) {

        // 解析brokerUser
        BrokerUserResolver userResolver = getBrokerUserResolver();
        BrokerUser user = userResolver.resolve(headers, payload);

        // 解析SUBSCRIBE帧的订阅参数
        Long subscribeId = headers.getLong(StompHeaders.ID);
        String destination = headers.getAsString(StompHeaders.DESTINATION);
        SubscribeAcknowledges ack = SubscribeAcknowledges.parse(headers.getAsString(StompHeaders.ACK));

        // 校验
        Objects.requireNonNull(subscribeId, "subscribe header required");
        StringVerifies.requireNoneBlank(destination, "destination header required");

        // 将headers解析为Map
        Map<String, List<String>> headersMap = headersToMap(headers);

        // 订阅destination
        user.subscribe(new MessageSubscription()
                .setSubscribeId(subscribeId)
                .setDestination(destination)
                .setAck(ack)
                .setOriginHeaders(headersMap)
                .setOriginPayload(payload)
        ).subscribe(publisher -> {
            // 订阅成功，订阅事件源，将StompFrame写回客户端
            Flux.from(publisher)
                    .subscribe(
                            frame -> this.doInEventLoop(ctx -> ctx.write(frame)),
                            error -> this.doInEventLoop(ctx -> ctx.fireExceptionCaught(error))
                    );
        }, error -> {
            // 订阅失败，写回ERROR帧，关闭连接
            this.doInEventLoop(ctx -> ctx.fireExceptionCaught(error));
        });

    }

}
