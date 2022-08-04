package com.duxinglangzi.canal.starter.annotation;

import com.duxinglangzi.canal.starter.configuration.CanalAutoConfigurationProperties;
import com.duxinglangzi.canal.starter.configuration.CanalConfigurationSelector;
import com.duxinglangzi.canal.starter.listener.ApplicationReadyListener;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启 canal listener
 *
 * @author wuqiong 2022/8/4
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({CanalAutoConfigurationProperties.class, CanalConfigurationSelector.class, ApplicationReadyListener.class})
public @interface EnableCanalListener {
}
