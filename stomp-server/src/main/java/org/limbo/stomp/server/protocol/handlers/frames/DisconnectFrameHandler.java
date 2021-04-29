package org.limbo.stomp.server.protocol.handlers.frames;

import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompHeaders;
import org.limbo.stomp.server.protocol.codec.StompFrames;
import org.limbo.stomp.server.protocol.handlers.ChannelHandlerContextDelegate;

import java.util.Objects;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public class DisconnectFrameHandler extends AbstractStompFrameHandler {

    public DisconnectFrameHandler(ChannelHandlerContextDelegate channelHandlerContext) {
        super(StompCommand.DISCONNECT, channelHandlerContext);
    }

    /**
     * {@inheritDoc}处理断开连接帧。
     * @param headers 请求头
     * @param payload 负载数据
     */
    @Override
    public void process(StompHeaders headers, String payload) {

        Long receiptId = headers.getLong(StompHeaders.RECEIPT);
        Objects.requireNonNull(receiptId, "receiptId cannot be null.");

        ChannelHandlerContextDelegate ctx = getChannelHandlerContext();
        ctx.write(StompFrames.createReceiptFrame(receiptId));

        ctx.disconnect();
    }
}
