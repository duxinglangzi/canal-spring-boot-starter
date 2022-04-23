# Canal Spring Boot Starter 使用实例

### 在spring boot 项目配置文件 application.yml内增加以下内容
```yaml
spring:
  canal:
    instances:
      example:                  # 拉取 example 目标的数据
        host: 192.168.10.179    # canal 所在机器的ip
        port: 11111             # canal 默认暴露端口
        user-name: canal        # canal 用户名
        password: canal         # canal 密码
        batch-size: 600         # canal 每次拉取的数据条数
        retry-count: 5          # 重试次数,如果重试5次后,仍无法连接,则断开
        cluster-enabled: false  # 是否开启集群
        zookeeper-address:      # zookeeper 地址(开启集群的情况下生效), 例: 192.168.0.1:2181,192.168.0.2:2181,192.168.0.3:2181
        acquire-interval: 1000  # 未拉取到消息情况下,获取消息的时间间隔毫秒值
        subscribe: .*\\..*      # 默认情况下拉取所有库、所有表

```

### 在spring boot 项目中的代码使用实例 
```java


import com.alibaba.otter.canal.protocol.CanalEntry;
import com.duxinglangzi.canal.starter.annotation.CanalListener;
import com.duxinglangzi.canal.starter.annotation.CanalUpdateListener;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author wuqiong 2022/4/12
 * @description
 */
@Service
public class CanalListenerTest {

    /**
     * 目前 Listener 方法的参数必须为 CanalEntry.EventType , CanalEntry.RowData 
     * 程序在启动过程中会做检查
     */

    /**
     * 监控更新操作 ，目标是 example的  books库  users表
     */
    @CanalUpdateListener(destination = "example", database = "books", table = {"users"})
    public void listenerExampleBooksUsers(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        printChange("listenerExampleBooksUsers",eventType, rowData);
    }

    /**
     * 监控更新操作 ，目标是 example的  books库  books表
     */
    @CanalUpdateListener(destination = "example", database = "books", table = {"books"})
    public void listenerExampleBooksBooks(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        printChange("listenerExampleBooksBooks",eventType, rowData);
    }

    /**
     * 监控更新操作 ，目标是 example的  books库的所有表
     */
    @CanalListener(destination = "example", database = "books", eventType = CanalEntry.EventType.UPDATE)
    public void listenerExampleBooksAll(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        printChange("listenerExampleBooksAll",eventType, rowData);
    }

    /**
     * 监控更新操作 ，目标是 example的  所有库的所有表
     */
    @CanalListener(destination = "example", eventType = CanalEntry.EventType.UPDATE)
    public void listenerExampleAll(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        printChange("listenerExampleAll",eventType, rowData);
    }

    /**
     * 监控更新、删除、新增操作 ，所有配置的目标下的所有库的所有表
     */
    @CanalListener(eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void listenerAllDml(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        printChange("listenerAllDml",eventType, rowData);
    }

    public void printChange(String method,CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        if (eventType == CanalEntry.EventType.DELETE) {
            rowData.getAfterColumnsList().stream().collect(Collectors.toList()).forEach(ele -> {
                System.out.println("[方法: "+method+" ,  delete 语句 ] --->> 字段名: " + ele.getName() + ", 删除的值为: " + ele.getValue());
            });
        }

        if (eventType == CanalEntry.EventType.INSERT) {
            rowData.getAfterColumnsList().stream().collect(Collectors.toList()).forEach(ele -> {
                System.out.println("[方法: "+method+" ,insert 语句 ] --->> 字段名: " + ele.getName() + ", 新增的值为: " + ele.getValue());
            });
        }

        if (eventType == CanalEntry.EventType.UPDATE) {
            for (int i = 0; i < rowData.getAfterColumnsList().size(); i++) {
                CanalEntry.Column afterColumn = rowData.getAfterColumnsList().get(i);
                CanalEntry.Column beforeColumn = rowData.getBeforeColumnsList().get(i);
                System.out.println("[方法: "+method+" , update 语句 ] -->> 字段名," + afterColumn.getName() +
                        " , 是否修改: " + afterColumn.getUpdated() +
                        " , 修改前的值: " + beforeColumn.getValue() +
                        " , 修改后的值: " + afterColumn.getValue());
            }
        }
    }


}



```




