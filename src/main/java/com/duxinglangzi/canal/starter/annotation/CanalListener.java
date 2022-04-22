package com.duxinglangzi.canal.starter.annotation;


import com.alibaba.otter.canal.protocol.CanalEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author wuqiong 2022/4/11
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalListener {


    /**
     * 如果未进行配置，则使用配置文件里全部 destination
     */
    String destination() default "";

    /**
     * 数据库名
     */
    String database() default "";

    /**
     * 数据表名
     */
    String[] table() default "";

    /**
     * 数据变动类型，此处请注意，默认不包含 DDL
     */
    CanalEntry.EventType[] eventType();


}
