package com.duxinglangzi.canal.starter.configuration;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.duxinglangzi.canal.starter.annotation.CanalListener;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 登记员
 * @author wuqiong 2022/4/11
 */
public class CanalListenerEndpointRegistrar {

    private Object bean;
    private Map.Entry<Method, CanalListener> listenerEntry;

    /**
     * 1、目前实现的 DML 解析器仅支持两个参数 <p>
     * 2、且顺序必须为: CanalEntry.EventType 、 CanalEntry.RowData  <p>
     * 3、如果CanalListener 指定的 destination 不在配置文件内，则直接抛错 <p>
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
        if (StringUtils.isNotBlank(getListenerEntry().getValue().destination())
                && !sets.contains(getListenerEntry().getValue().destination()))
            throw new CanalClientException("@CanalListener Illegal destination , please check ");

    }


    public List<Class<?>> parameterTypes() {
        return Arrays.stream(getListenerEntry().getKey().getParameterTypes()).collect(Collectors.toList());
    }

    public boolean isContainDestination(String destination) {
        if (StringUtils.isBlank(getListenerEntry().getValue().destination())) return true;
        return getListenerEntry().getValue().destination().equals(destination);
    }

    /**
     * 过滤参数
     * @param database
     * @param tableName
     * @param eventType
     * @return java.util.function.Predicate
     * @author wuqiong 2022-04-23 20:47
     */
    public static Predicate<CanalListenerEndpointRegistrar> filterArgs(
            String database, String tableName, CanalEntry.EventType eventType) {
        Predicate<CanalListenerEndpointRegistrar> databases = e -> StringUtils.isBlank(e.getListenerEntry().getValue().database())
                || e.getListenerEntry().getValue().database().equals(database);
        Predicate<CanalListenerEndpointRegistrar> table = e -> e.getListenerEntry().getValue().table().length == 0
                || (e.getListenerEntry().getValue().table().length == 1 && "".equals(e.getListenerEntry().getValue().table()[0]))
                || Arrays.stream(e.getListenerEntry().getValue().table()).anyMatch(s -> s.equals(tableName));
        Predicate<CanalListenerEndpointRegistrar> eventTypes = e -> e.getListenerEntry().getValue().eventType().length == 0
                || Arrays.stream(e.getListenerEntry().getValue().eventType()).anyMatch(eve -> eve == eventType);
        return databases.and(table).and(eventTypes);
    }


    public CanalListenerEndpointRegistrar(Object bean, Map.Entry<Method, CanalListener> entry) {
        this.bean = bean;
        this.listenerEntry = entry;
    }

    public Map.Entry<Method, CanalListener> getListenerEntry() {
        return listenerEntry;
    }

    public Object getBean() {
        return bean;
    }
}
