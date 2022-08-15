package com.duxinglangzi.canal.starter.mode;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.io.Serializable;

/**
 * 监听事件的 返回信息, 主要将 CanalEntry 里不同层级的常用数据组装到同一层级使用.<p>
 * headers 是为了方便获取其他数据，比如
 *
 * @author wuqiong 2022/8/15
 */
public class CanalMessage implements Serializable {
    private static final long serialVersionUID = 730485362580815032L;

    /**
     * 数据库名
     */
    private String dataBaseName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 发生变化的 事件类型
     */
    private CanalEntry.EventType eventType;

    /**
     * 发生变化的数据
     */
    private CanalEntry.RowData rowData;

    /**
     * 头信息， 包含: sql执行时间、数据库日志文件名、数据库日志文件偏移量 等信息
     */
    private CanalEntry.Header entryHeader;

    /**
     * 构造返回数据信息
     *
     * @param entryHeader 头信息
     * @param eventType   事件类型
     * @param rowData     变化的数据
     * @author wuqiong 2022/8/15 16:18
     */
    public CanalMessage(CanalEntry.Header entryHeader, CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        this.entryHeader = entryHeader;
        this.eventType = eventType;
        this.rowData = rowData;
        this.dataBaseName = getEntryHeader().getSchemaName();
        this.tableName = getEntryHeader().getTableName();
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public CanalEntry.EventType getEventType() {
        return eventType;
    }

    public CanalEntry.RowData getRowData() {
        return rowData;
    }

    public CanalEntry.Header getEntryHeader() {
        return entryHeader;
    }
}
