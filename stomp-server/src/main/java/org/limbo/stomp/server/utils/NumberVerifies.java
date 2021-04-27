/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.stomp.server.utils;

import java.util.function.Supplier;

/**
 * @author Brozen
 * @since 2021-04-06
 */
public class NumberVerifies {

    /**
     * 负数或0抛出异常，正整数返回
     * @param value 待检测的值
     * @return 入参value
     */
    public static int positive(int value) {
        return positive(value, () -> "必须是一个正整数，但是传入了 " + value);
    }

    /**
     * 负数或0抛出异常，正整数返回
     * @param value 待检测的值
     * @param message 抛出异常的错误信息
     * @return 入参value
     */
    public static int positive(int value, String message) {
        return positive(value, () -> message);
    }

    /**
     * 负数或0抛出异常，正整数返回
     * @param value 待检测的值
     * @param errorMessageSupplier 抛出异常的错误信息
     * @return 入参value
     */
    public static int positive(int value, Supplier<String> errorMessageSupplier) {
        if (value <= 0) {
            throw new IllegalArgumentException(errorMessageSupplier.get());
        }

        return value;
    }

}
