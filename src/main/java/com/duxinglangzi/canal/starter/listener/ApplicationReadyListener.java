package com.duxinglangzi.canal.starter.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wuqiong 2022/4/16
 */
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    public static final AtomicBoolean START_LISTENER_CONTAINER = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 确保程序启动之后，再放行所有的 canal transponder
        if (!START_LISTENER_CONTAINER.get()) START_LISTENER_CONTAINER.set(true);
    }
}
