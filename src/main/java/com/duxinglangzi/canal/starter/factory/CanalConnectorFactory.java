package com.duxinglangzi.canal.starter.factory;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.duxinglangzi.canal.starter.configuration.CanalAutoConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;


public class CanalConnectorFactory {

    /**
     * 创建 CanalConnector
     * @param destination
     * @param endpointInstance
     * @return CanalConnector
     * @author wuqiong 2022-04-23 20:36
     */
    public static synchronized CanalConnector createConnector(
            String destination, CanalAutoConfigurationProperties.EndpointInstance endpointInstance) {
        Assert.isTrue(StringUtils.hasText(destination), "destination is null , please check ");
        Assert.isTrue(endpointInstance != null, "endpoint instance is null , please check ");
        CanalConnector connector;
        if (endpointInstance.isClusterEnabled()) {
            if (!StringUtils.hasText(endpointInstance.getZookeeperAddress()))
                throw new CanalClientException("zookeeper address is null");
            List<SocketAddress> addresses = new ArrayList<>();
            for (String s : endpointInstance.getZookeeperAddress().split(",")) {
                String[] split = s.split(":");
                if (split.length != 2)
                    throw new CanalClientException("error parsing zookeeper address:" + s);
                addresses.add(new InetSocketAddress(split[0], Integer.parseInt(split[1])));
            }
            connector = CanalConnectors.newClusterConnector(
                    addresses, destination, endpointInstance.getUserName(), endpointInstance.getPassword());
        } else {
            connector = CanalConnectors.newSingleConnector(
                    new InetSocketAddress(endpointInstance.getHost(), endpointInstance.getPort()),
                    destination, endpointInstance.getUserName(), endpointInstance.getPassword());
        }
        return connector;
    }
}
