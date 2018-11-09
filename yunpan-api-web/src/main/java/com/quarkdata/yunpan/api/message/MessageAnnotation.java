package com.quarkdata.yunpan.api.message;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author liuda
 * @Description 自定义消息通知注解
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface MessageAnnotation {
}
