package com.quarkdata.yunpan.api.aspect.isLock;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by yanyq1129@thundersoft.com on 2018/9/6.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface IsLock {
    String ids(); // 文档ID
}
