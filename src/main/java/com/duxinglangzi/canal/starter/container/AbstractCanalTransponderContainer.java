package com.duxinglangzi.canal.starter.container;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.duxinglangzi.canal.starter.listener.ApplicationReadyListener;
import org.springframework.context.SmartLifecycle;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的canal transponder ,实现SmartLifecycle接口,声明周期由spring进行管理
 * @author wuqiong 2022/4/11
 */
public abstract class AbstractCanalTransponderContainer implements SmartLifecycle {
    protected boolean isRunning = false;
    protected final Long SLEEP_TIME_MILLI_SECONDS = 1000L;
    protected List<CanalEntry.EntryType> IGNORE_ENTRY_TYPES =
            Arrays.asList(CanalEntry.EntryType.TRANSACTIONBEGIN,
                    CanalEntry.EntryType.TRANSACTIONEND,
                    CanalEntry.EntryType.HEARTBEAT);

    protected abstract void doStart();
    protected abstract void initConnect();
    protected abstract void disconnect();


    @Override
    public void start() {
        new Thread(() -> {
            // spring 启动后 才会进行canal数据拉取
            while (!ApplicationReadyListener.START_LISTENER_CONTAINER.get())
                sleep(5L * SLEEP_TIME_MILLI_SECONDS);
            initConnect();
            while (isRunning() && !Thread.currentThread().isInterrupted()) doStart();
            disconnect(); // 线程被终止或者容器已经停止
        }).start();
        setRunning(true);
    }

    @Override
    public void stop() {
        setRunning(false);
    }

    @Override
    public void stop(Runnable callback) {
        callback.run();
        setRunning(false);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    protected void setRunning(boolean bool){
        isRunning = bool;
    }

    protected void sleep(long sleepTimeMilliSeconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(sleepTimeMilliSeconds);
            if (!isRunning()) Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
        }
    }


}
