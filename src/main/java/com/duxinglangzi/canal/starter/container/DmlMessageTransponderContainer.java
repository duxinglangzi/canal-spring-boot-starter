package com.duxinglangzi.canal.starter.container;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.duxinglangzi.canal.starter.configuration.CanalAutoConfigurationProperties;
import com.duxinglangzi.canal.starter.configuration.CanalListenerEndpointRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * DML 数据拉取、解析
 *
 * @author wuqiong 2022/4/11
 */
public class DmlMessageTransponderContainer extends AbstractCanalTransponderContainer {

    private final static Logger logger = LoggerFactory.getLogger(DmlMessageTransponderContainer.class);

    private CanalConnector connector;
    private CanalAutoConfigurationProperties.EndpointInstance endpointInstance;
    private List<CanalListenerEndpointRegistrar> registrars = new ArrayList<>();
    private Set<CanalEntry.EventType> support_all_types = new HashSet<>();
    private Integer local_retry_count;


    public void initConnect() {
        try {
            // init supportAllTypes
            registrars.forEach(e -> support_all_types.addAll(Arrays.asList(e.getEventType())));
            connector.connect();
            connector.subscribe(endpointInstance.getSubscribe());
            connector.rollback();
            // 初始化本地重试次数
            local_retry_count = endpointInstance.getRetryCount();
        } catch (Exception e) {
            logger.error("[DmlMessageTransponderContainer_initConnect] canal client connect error .", e);
            setRunning(false);
        }

    }

    public void disconnect() {
        // 关闭连接
        connector.disconnect();
    }


    public void doStart() {
        Message message = null;
        try {
            message = connector.getWithoutAck(endpointInstance.getBatchSize()); // 获取指定数量的数据
        } catch (Exception clientException) {
            logger.error("[DmlMessageTransponderContainer] error msg : ", clientException);
            if (local_retry_count > 0) {
                // 重试次数减一
                local_retry_count = local_retry_count - 1;
                sleep(endpointInstance.getAcquireInterval());
            } else {
                // 重试次数 <= 0 时,直接终止线程
                logger.error("[DmlMessageTransponderContainer] retry count is zero ,  " +
                                "thread interrupt , current connector host: {} , port: {} ",
                        endpointInstance.getHost(), endpointInstance.getPort());
                Thread.currentThread().interrupt();
            }
            return;
        }
        // 如果重试次数小于设置的,则修改
        if (local_retry_count < endpointInstance.getRetryCount()) local_retry_count = endpointInstance.getRetryCount();
        List<CanalEntry.Entry> entries = message.getEntries();
        if (message.getId() == -1 || entries.isEmpty()) {
            sleep(endpointInstance.getAcquireInterval());
            return;
        }
        for (CanalEntry.Entry entry : entries) {
            try {
                consumer(entry);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("[DmlMessageTransponderContainer_doStart] CanalEntry.Entry consumer error ", e);
                // connector.rollback(message.getId()); // 目前先不处理失败, 无需回滚数据
                // return;
            }
        }
        connector.ack(message.getId()); // 提交确认
    }

    public DmlMessageTransponderContainer(
            CanalConnector connector, List<CanalListenerEndpointRegistrar> registrars,
            CanalAutoConfigurationProperties.EndpointInstance endpointInstance) {
        this.connector = connector;
        this.registrars.addAll(registrars);
        this.endpointInstance = endpointInstance;
    }

    private void consumer(CanalEntry.Entry entry) {
        if (IGNORE_ENTRY_TYPES.contains(entry.getEntryType())) return;
        CanalEntry.RowChange rowChange = null;
        try {
            rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (Exception e) {
            logger.error("[DmlMessageTransponderContainer_consumer] RowChange parse has an error ", e);
            throw new RuntimeException("RowChange parse has an error , data:" + entry.toString(), e);
        }

        // 忽略 ddl 语句
        if (rowChange.hasIsDdl() && rowChange.getIsDdl()) return;
        CanalEntry.EventType eventType = rowChange.getEventType();
        if (!support_all_types.contains(eventType)) return;
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            registrars
                    .stream()
                    .filter(CanalListenerEndpointRegistrar.filterArgs(
                            entry.getHeader().getSchemaName(),
                            entry.getHeader().getTableName(),
                            eventType))
                    .forEach(element -> {
                        try {
                            element.getMethod().invoke(element.getBean(), eventType, rowData);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            logger.error("[DmlMessageTransponderContainer_consumer] RowData Callback Method invoke error message", e);
                            throw new RuntimeException("RowData Callback Method invoke error message： " + e.getMessage(), e);
                        }
                    });
        }
    }

}
