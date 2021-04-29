package org.limbo.stomp.server.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/**
 * @author Brozen
 * @since 2021-04-29
 */
public class StringVerifies {


    /**
     * 校验入参字符串是否为空串，null或空字符串或字符串中仅包含空白字符，均视为空串。
     * @param str 待校验字符串
     * @return 返回待校验字符串
     * @see StringUtils#isBlank(CharSequence)
     */
    public static String requireNoneBlank(String str) {
        return requireNoneBlank(str, "param cannot be blank string.");
    }


    /**
     * 校验入参字符串是否为空串，null或空字符串或字符串中仅包含空白字符，均视为空串。
     * @param str 待校验字符串
     * @param errorMsg 校验不通过时错误信息。
     * @return 返回待校验字符串
     * @see StringUtils#isBlank(CharSequence)
     */
    public static String requireNoneBlank(String str, String errorMsg) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException(errorMsg);
        }

        return str;
    }

    /**
     * 校验入参字符串是否为空串，null或空字符串或字符串中仅包含空白字符，均视为空串。
     * @param str 待校验字符串
     * @param errorMsgSupplier 校验不通过时错误信息提供者。
     * @return 返回待校验字符串
     * @see StringUtils#isBlank(CharSequence)
     */
    public static String requireNoneBlank(String str, Supplier<String> errorMsgSupplier) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException(errorMsgSupplier.get());
        }

        return str;
    }


}
