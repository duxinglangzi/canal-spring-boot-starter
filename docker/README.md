# Canal 服务端部署

### 此处采用 docker-compose 方式部署

### 附 docker-compose.yaml 文件内容
```yaml

version: "3.7"
services:
  canal-server:
    image: canal/canal-server:v1.1.5
    container_name: canal-server
    ports:
      - "11111:11111"
    environment:
      - canal.instance.mysql.slaveId=112          # slaveId 不与其他重复即可
      - canal.auto.scan=true                      # 自动扫描
      - canal.destinations=my-example             #  client 需要指定此 dest
      - canal.instance.master.address=127.0.0.1:3306   # mysql 地址
      - canal.instance.dbUsername=canal                # mysql username
      - canal.instance.dbPassword=canal                # mysql 密码
    volumes:
    # - ./canal-server/conf/:/home/admin/canal-server/conf/
      - ./canal-server/logs/:/home/admin/canal-server/logs/


```




