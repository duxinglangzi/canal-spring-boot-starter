package com.duxinglangzi.canal.starter.annotation;


import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wuqiong 2022/4/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CanalListener(eventType = CanalEntry.EventType.UPDATE)
public @interface CanalUpdateListener {

    @AliasFor(annotation = CanalListener.class)
    String destination();

    @AliasFor(annotation = CanalListener.class)
    String database();

    @AliasFor(annotation = CanalListener.class)
    String[] table();

}
