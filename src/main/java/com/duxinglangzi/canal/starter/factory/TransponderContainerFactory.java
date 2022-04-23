package com.duxinglangzi.canal.starter.factory;

import com.alibaba.otter.canal.client.CanalConnector;
import com.duxinglangzi.canal.starter.configuration.CanalAutoConfigurationProperties;
import com.duxinglangzi.canal.starter.configuration.CanalListenerEndpointRegistrar;
import com.duxinglangzi.canal.starter.container.DmlMessageTransponderContainer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wuqiong 2022/4/18
 * @description
 */
public class TransponderContainerFactory {

    private static final String CONTAINER_ID_PREFIX = "com.duxinglangzi.canal.starter.container.MessageTransponderContainer#";


    /**
     * 将所有待注册的端点，注册到spring中
     * @param beanFactory
     * @param canalConfig
     * @param registrars
     * @return void
     * @author wuqiong 2022-04-23 20:34
     */
    public static void registerListenerContainer(
            ConfigurableListableBeanFactory beanFactory, CanalAutoConfigurationProperties canalConfig,
            Set<CanalListenerEndpointRegistrar> registrars) {
        if (registrars == null || registrars.isEmpty()) return;
        if (canalConfig == null || canalConfig.getInstances().isEmpty()) return;

        for (Map.Entry<String, CanalAutoConfigurationProperties.EndpointInstance> endpointInstance : canalConfig.getInstances().entrySet()) {
            if (beanFactory.containsBean(getContainerID(endpointInstance.getKey()))) continue; // 如果已经存在则不在创建
            List<CanalListenerEndpointRegistrar> registrarList = new ArrayList<>();
            for (CanalListenerEndpointRegistrar registrar : registrars) {
                registrar.checkParameter(canalConfig.getInstances().keySet());
                if (!registrar.isContainDestination(endpointInstance.getKey())) continue;
                registrarList.add(registrar);
            }
            if (registrarList.isEmpty()) continue;
            registerTransponderContainer(
                    endpointInstance.getKey(), endpointInstance.getValue(), beanFactory, registrarList);
        }

    }

    private static void registerTransponderContainer(
            String destination, CanalAutoConfigurationProperties.EndpointInstance endpointInstance,
            ConfigurableListableBeanFactory beanFactory, List<CanalListenerEndpointRegistrar> registrarList) {
        CanalConnector connector = CanalConnectorFactory.createConnector(destination, endpointInstance);
        beanFactory.registerSingleton(getContainerID(destination),
                new DmlMessageTransponderContainer(connector, registrarList, endpointInstance));
    }


    private static String getContainerID(String destination) {
        return CONTAINER_ID_PREFIX + "#" + destination;
    }


}
