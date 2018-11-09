package com.quarkdata.yunpan.api.log;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 自定义日志注解
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface LogAnnotation {

}
