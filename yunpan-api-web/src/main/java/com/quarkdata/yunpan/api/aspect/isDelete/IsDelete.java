package com.quarkdata.yunpan.api.aspect.isDelete;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 文档相关任何操作前检查isDelete属性
 * Created by yanyq1129@thundersoft.com on 2018/6/20.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface IsDelete {
    // id
    String ids();
    // 类名
    String className() default "Document";

}
