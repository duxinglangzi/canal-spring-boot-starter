package com.duxinglangzi.canal.starter.configuration;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 登记员
 *
 * @author wuqiong 2022/4/11
 */
public class CanalListenerEndpointRegistrar {

    private Object bean;
    private Method method;

    /**
     * 如果未进行配置，则使用配置文件里全部 destination
     */
    private String destination;

    /**
     * 数据库名
     */
    private String database;

    /**
     * 数据表名
     */
    private String[] table;

    /**
     * 数据变动类型，此处请注意，默认不包含 DDL
     */
    private CanalEntry.EventType[] eventType;

    /**
     * 1、目前实现的 DML 解析器仅支持两个参数 <p>
     * 2、且顺序必须为: CanalEntry.EventType 、 CanalEntry.RowData  <p>
     * 3、如果CanalListener 指定的 destination 不在配置文件内，则直接抛错 <p>
     *
     * @param sets
     * @return void
     * @author wuqiong 2022-04-23 20:27
     */
    public void checkParameter(Set<String> sets) {
        List<Class<?>> classes = parameterTypes();
        if (classes.size() > 2
                || classes.get(1) != CanalEntry.RowData.class
                || classes.get(0) != CanalEntry.EventType.class)
            throw new IllegalArgumentException("@CanalListener Method Parameter Type Invalid, " +
                    "Need Parameter Type [CanalEntry.EventType,CanalEntry.RowData] please check ");
        if (StringUtils.isNotBlank(getDestination()) && !sets.contains(getDestination()))
            throw new CanalClientException("@CanalListener Illegal destination , please check ");

    }


    public List<Class<?>> parameterTypes() {
        return Arrays.stream(getMethod().getParameterTypes()).collect(Collectors.toList());
    }

    public boolean isContainDestination(String destination) {
        if (StringUtils.isBlank(getDestination())) return true;
        return getDestination().equals(destination);
    }

    /**
     * 过滤参数
     *
     * @param database
     * @param tableName
     * @param eventType
     * @return java.util.function.Predicate
     * @author wuqiong 2022-04-23 20:47
     */
    public static Predicate<CanalListenerEndpointRegistrar> filterArgs(
            String database, String tableName, CanalEntry.EventType eventType) {
        Predicate<CanalListenerEndpointRegistrar> databases =
                e -> StringUtils.isBlank(e.getDatabase()) || e.getDatabase().equals(database);
        Predicate<CanalListenerEndpointRegistrar> table = e -> e.getTable().length == 0
                || (e.getTable().length == 1 && "".equals(e.getTable()[0]))
                || Arrays.stream(e.getTable()).anyMatch(s -> s.equals(tableName));
        Predicate<CanalListenerEndpointRegistrar> eventTypes = e -> e.getEventType().length == 0
                || Arrays.stream(e.getEventType()).anyMatch(eve -> eve == eventType);
        return databases.and(table).and(eventTypes);
    }


    public CanalListenerEndpointRegistrar(
            Object bean, Method method, String destination,
            String database, String[] tables, CanalEntry.EventType[] eventTypes) {
        this.bean = bean;
        this.method = method;
        this.destination = destination;
        this.database = database;
        this.table = tables;
        this.eventType = eventTypes;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public String getDestination() {
        return destination;
    }

    public String getDatabase() {
        return database;
    }

    public String[] getTable() {
        return table;
    }

    public CanalEntry.EventType[] getEventType() {
        return eventType;
    }
}
