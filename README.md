### redis工具使用说明

1.配置文件

```
#基本配置
base:
  config:
  	#分割符，在查询全部数据时 依据改字段进行分割
    divider: ':'
    #scan 数量(过大会影响生产使用，自行调整)
    oneCount: 2000
    #启动的线程数(根据服务器性能调整)
    threadSize: 10
    #导入文件时 选择文件编码格式
    encoding: 'utf-8'
    #导入文件时 选择生成的key前缀
    prefix: 'member_'
    
#redis连接配置
redis:
  #自动连接
  auto-reconnect: true
  #是否启用默认压缩
  compression: fasle
  #定时更新集群状态 cluster集群可用
  enable-periodic-refresh: false
  #激活连接前ping
  ping-before-activate-connection: true
  #扫描时间间隔
  refresh-period: 1000
  #超时时间
  timeout: 30
  #连接池配置（可不用修改）
  pool:
    max-idle: 100
    max-total: 100
    min-idle: 0
    test-on-accquire: true
    test-on-create: false
    test-on-release: false
  #cluster集群配置
  cluster:
    host: 192.168.78.160:16380,192.168.78.160:16381
    password: qdzw&OTN!(2020)
  #单节点配置 codis集群可配置对应节点
  single:
    host: 192.168.78.146:16380,192.168.78.146:16381,192.168.78.146:16382
    password: qdzw&OTN!(2020)
  #codis集群配置 
  codis:
    host: 192.168.78.146:2181
    password: qdzw&OTN!(2020)
    zkDir: /jodis/codis-test
    #需确定codis group信息存储节点  通常目录为/codis/{name}/group
    groupPath: /codis3/codis-test/group

# metric计数器打印日志间隔时间（秒）
metric:
  sleep: 5
```

2.上传工具jar包

```
代码在 https://gitlab.gzky.com/nicholas.qiao/redisclustertool.git
打包   mvn clean package -Dmaven.test.skip=true
```

3.执行启动命令

```
完成cluster single的多个功能重构
1.统计总key数量 -o dbSize
2.根据分隔符查询key的数量 -o select
3.根据一个或多个前缀查询key的数量 -o select -k
4.根据一个或多个后缀查询key的数量 -o selectBySuffix -k
5.根据一个或多个前缀删除key -o delete -k
6.根据一个或多个后缀删除key -o deleteBySuffix -k
7.根据文件导入数据 -o import -k {filePath}
8.根据文件删除key -o deleteByFile -k {filePath}
9.根据一个或多个前缀打印key -o writePreKey -k  
10根据一个或多个后缀打印key -o writeNoPreKey -k  
11根据一个或多个后缀统计key长度 -o length -k  
---多个key参数使用','分割


例 删除cluster集群后缀为 fix1和fix2 的key
仅需修改base.config参数以及redis.cluster.host和redis.cluster.password  其他值无需修改
nohup java -jar -Dspring.config.location=application.yaml redisClusterTool-0.0.1-SNAPSHOT.jar -m cluster -o deleteBySuffix -k fix1,fix2

导入文件需要文件内容固定格式
{"type":"string","key":"1","value":"2","field":{"3":"4"},"list":["5"],"expire":-1}
type 类型 可选string|hash|list|set|
key redis key 必填
value 参数  string类型参数
feild 参数  hash类型参数  Map<String,String>类型
list  参数  list set类型参数 List<String>类型
expire 过期时间  仅string类型下生效，默认-1永不过期 单位毫秒
```

