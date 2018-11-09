package com.quarkdata.yunpan.api.aspect.isShare;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 与我共享相关操作controller层添加此注解,用于取消与我共享未刷新页面操作检查
 * Created by yanyq1129@thundersoft.com on 2018/7/2.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface IsShare {
    String documentIds() default ""; // 文档ID
    String shareIds() default "";// 共享记录ID
}
