package com.quarkdata.yunpan.api.aspect.isEnoughSpace;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/2.
 * 上传文档之前检查空间是否足够
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface IsEnoughSpace {
    String type(); //文档类型 0-组织文件 1-个人文件
    String docSize(); // 要上传的文档大小
}

