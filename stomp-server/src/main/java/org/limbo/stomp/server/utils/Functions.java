package org.limbo.stomp.server.utils;

import java.util.function.Consumer;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public class Functions {

    /**
     * 一个空的消费者方法
     */
    @SuppressWarnings("rawtypes")
    public static final Consumer EMPTY_CONSUMER = o -> {};

    /**
     * 返回一个什么都不执行的空消费方法
     * @see Functions#EMPTY_CONSUMER
     */
    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> emptyConsumer() {
        return (Consumer<T>) EMPTY_CONSUMER;
    }


}
