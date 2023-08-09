package com.duxinglangzi.canal.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Canal连接的配置类
 *
 * @author wuqiong 2022/4/11
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "spring.canal")
public class CanalAutoConfigurationProperties {

    private Map<String, EndpointInstance> instances = new LinkedHashMap<>();

    public static class EndpointInstance {

        /**
         * 是否开启 cluster
         */
        private boolean clusterEnabled;

        /**
         * zookeeper 地址, 例: 192.168.0.1:2181,192.168.0.2:2181,192.168.0.3:2181
         */
        private String zookeeperAddress;

        /**
         * 默认 127.0.0.1
         */
        private String host = "127.0.0.1";

        /**
         * 端口 , 默认: 11111
         */
        private int port = 11111;

        /**
         * 用户名
         */
        private String userName = "";

        /**
         * 密码
         */
        private String password = "";

        /**
         * 每次获取数据条数 , 默认: 200
         */
        private int batchSize = 200;

        /**
         * 发生错误时重试次数 , 默认: 5
         */
        private int retryCount = 5;

        /**
         * mysql 数据解析关注的表，Perl正则表达式.
         * <p>
         * <p>
         * 多个正则之间以逗号(,)分隔，转义符需要双斜杠(\\)
         * <p>
         * <p>
         * 常见例子： <p>
         * 1.  所有库表：.*   or  .*\\..* <p>
         * 2.  canal_db 下所有表：    canal_db\\..* <p>
         * 3.  canal_db 下的以canal打头的表：   canal_db\\.canal.* <p>
         * 4.  canal_db 下的一张表：  canal_db\\.test1 <p>
         * 5.  多个规则组合使用：canal_db\\..*,mysql_db.test1,mysql.test2 (逗号分隔) <p>
         * <p>
         * 默认: 全库全表(.*\\..*)
         */
        private String subscribe = ".*";

        /**
         * 未拉取到消息情况下,获取消息的时间间隔毫秒值 , 默认: 1000
         */
        private long acquireInterval = 1000;

        /**
         * 当canal连接中断时对外发送通知,通知地址和内容自定义
         */
        private String connectionInterruptionNotificationURL = "";

        public EndpointInstance() {
        }

        public boolean isClusterEnabled() {
            return clusterEnabled;
        }

        public void setClusterEnabled(boolean clusterEnabled) {
            this.clusterEnabled = clusterEnabled;
        }

        public String getZookeeperAddress() {
            return zookeeperAddress;
        }

        public void setZookeeperAddress(String zookeeperAddress) {
            this.zookeeperAddress = zookeeperAddress;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public long getAcquireInterval() {
            return acquireInterval;
        }

        public void setAcquireInterval(long acquireInterval) {
            this.acquireInterval = acquireInterval;
        }

        public String getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(String subscribe) {
            this.subscribe = subscribe;
        }

        public String getConnectionInterruptionNotificationURL() {
            return connectionInterruptionNotificationURL;
        }

        public void setConnectionInterruptionNotificationURL(String connectionInterruptionNotificationURL) {
            this.connectionInterruptionNotificationURL = connectionInterruptionNotificationURL;
        }
    }

    public Map<String, EndpointInstance> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, EndpointInstance> instances) {
        this.instances = instances;
    }
}
