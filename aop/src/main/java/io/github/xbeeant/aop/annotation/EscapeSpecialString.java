package io.github.xbeeant.aop.annotation;

import java.lang.annotation.*;

/**
 * 铭感字符串过滤
 *
 * @author xiaobiao
 * @version 2020/2/16
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EscapeSpecialString {
}
