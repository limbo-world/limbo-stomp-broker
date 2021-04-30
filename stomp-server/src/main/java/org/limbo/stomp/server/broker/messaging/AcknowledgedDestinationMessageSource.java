package org.limbo.stomp.server.broker.messaging;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.stomp.StompHeaders;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.limbo.stomp.server.protocol.codec.AckMode;
import org.limbo.stomp.server.protocol.handlers.exceptions.AcknowledgeException;
import org.limbo.stomp.server.protocol.handlers.exceptions.SubscribeException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * 可确认的消息源。未确认的消息会根据{@link AckMode}来决定如何处理。
 *
 * <p>
 * {@link AckMode#AUTO} 模式下，{@link #ack(MessageAcknowledge)} 和 {@link #nack(MessageAcknowledge)} 方法不执行任何操作。
 * </p>
 *
 * <p>
 * {@link AckMode#CLIENT} 模式下，{@link #ack(MessageAcknowledge)} 调用会确认收到ACK之前发出的全部消息；
 * {@link #nack(MessageAcknowledge)}调用不执行任何操作。
 * </p>
 *
 * <p>
 * {@link AckMode#CLIENT_INDIVIDUAL}模式下，{@link #ack(MessageAcknowledge)} 会确认指定ID的消息，
 * {@link #nack(MessageAcknowledge)}调用不执行任何操作。
 * </p>
 *
 * @author Brozen
 * @since 2021-04-30
 */
public abstract class AcknowledgedDestinationMessageSource extends AbstractDestinationMessageSource {

    /**
     * 待确认的帧。key是MESSAGE帧的message-id头，value是待确认的消息。
     */
    private ConcurrentSkipListMap<Long, StompMessage> notAcknowledgedMessage;

    /**
     * 未确认消息重新投递的时间间隔，毫秒。默认100毫秒，则100毫秒内消息未确认会重新投递给客户端。
     */
    @Setter
    private long redeliverIntervalMillis = 100;

    /**
     * 检查未确认消息是否需要重新投递的任务执行间隔，毫秒。默认50毫秒。
     * 为防止一次重新投递遍历耗时过久，任务执行间隔可以设置短一些，单次检查消息数量可以少一些。
     */
    @Setter
    private long redeliverTaskIntervalMillis = 50;

    /**
     * 每次重新投递时，从待确认消息列表中检查的消息数量。默认100条。
     * 为防止一次重新投递遍历耗时过久，任务执行间隔可以设置短一些，单次检查消息数量可以少一些。
     */
    @Setter
    private int redeliverBatchSize = 100;

    /**
     * 重新投递任务的执行线程池
     */
    @Setter
    private EventLoop executor;

    /**
     * 创建一个消息源。
     * @param subscribeId 订阅ID
     * @param destination 订阅destination
     * @param ackMode 消息确认模式
     */
    public AcknowledgedDestinationMessageSource(Long subscribeId, String destination, AckMode ackMode) {
        super(subscribeId, destination, ackMode);
        this.notAcknowledgedMessage = new ConcurrentSkipListMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Flux<StompMessage> createUpstreamMessageSource() {
        Flux<StompMessage> messageSource = doCreateUpstreamMessageSource();

        switch (getAckMode()) {
            case AUTO:
                return messageSource;

            case CLIENT:
            case CLIENT_INDIVIDUAL:
                return messageSource.doOnNext(message -> {
                    Map<String, List<String>> headers = message.getHeaders();

                    // 解析msgId
                    List<String> msgIdValue = headers.get(StompHeaders.MESSAGE_ID.toString());
                    String msgIdString = null;
                    if (CollectionUtils.isEmpty(msgIdValue) || !NumberUtils.isCreatable((msgIdString = msgIdValue.get(0)))) {
                        throw new SubscribeException("Message id invalid: " + msgIdString);
                    }

                    // 校验msgId是否重复
                    Long msgId = Long.parseLong(msgIdString);
                    StompMessage msg = notAcknowledgedMessage.computeIfAbsent(msgId, _mid -> message);
                    if (msg != message) {
                        throw new SubscribeException("Message id duplicate: " + msgId);
                    }

                    // 设置消息投递时间
                }).doOnError(error -> {
                    // 清理资源
                    notAcknowledgedMessage.clear();
                });

            default:
                return Flux.error(new SubscribeException("Unknown ack mode: " + getAckMode()));
        }
    }

    protected abstract Flux<StompMessage> doCreateUpstreamMessageSource();

    @Override
    public void ack(MessageAcknowledge ack) {
        switch (getAckMode()) {
            case AUTO:
                return;

            case CLIENT:
                // 之前的待确认消息全部确认
                notAcknowledgedMessage.clear();

            case CLIENT_INDIVIDUAL:
                // 仅确认一个
                notAcknowledgedMessage.remove(ack.getId());

            default:
                throw new AcknowledgeException("Unknown ack mode: " + ack);
        }
    }

    /**
     * 检查未确认消息列表，
     */
    protected void checkAndRedeliverMessage() {
        // TODO
    }

}
