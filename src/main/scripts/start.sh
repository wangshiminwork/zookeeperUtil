#!/bin/sh
Time=$(date +%Y%m%d)
PWDPATH=$(
  cd $(dirname $0)
  pwd
)
CLSPATH="$PWDPATH/.."
cd $CLSPATH
export SERVER_NAME=${artifactId}-${version}.jar

YAML_PATH=$CLSPATH/conf/application.yaml
JAR_PATH=$CLSPATH/jar/$SERVER_NAME
INFO_PATH=$CLSPATH/logs/info.log

#可指定jdk路径
export JAVA_HOME=

JAVA_OPT="${JAVA_HOME}java"
JAVA_OPT="${JAVA_OPT} -Xms1g -Xmx4g -Xss256k"
JAVA_OPT="${JAVA_OPT} -Dspring.config.location=${YAML_PATH} "

#检测pid
function getPid() {
  echo "检测状态---------------------------------------------"
  pid=$(ps -ef | grep -n ${SERVER_NAME} | grep -v grep | awk '{print $2}')
  if [ ${pid} ]; then
    echo "运行pid：${pid}"
  else
    echo "未运行"
  fi
}

#启动程序
function start() {
  #启动前，先停止之前的
  stop
  if [ ${pid} ]; then
    echo "停止程序失败，无法启动"
  else
    echo "启动程序---------------------------------------------"
    echo "请输入集群模式 cluster|single|codis"
    read input_model
    if [ -z $input_model ]; then
      echo "集群模式必填"
      exit
    fi
    echo "请输入操作类型 dbSize|select|selectBySuffix|delete|deleteBySuffix|import|length|pressure|deleteByRule|writePreKey|writeNoPreKey"
    read input_operation
    if [ -z $input_operation ]; then
      echo "操作类型必填"
      exit
    fi
    echo "请输入key"
    read input_key
    if [ -z $input_key ]; then
      input_key=" "
    else
      input_key="-k ${input_key} "
    fi

    echo "集群模式 ${input_model} ; 操作类型 ${input_operation} ; 执行key ${input_key}"
    echo "确定要执行输入y. [y/n]"
    read commit
    if [ $commit != "y" ]; then
      exit
    fi

    JAVA_CMD="nohup ${JAVA_OPT} -jar ${JAR_PATH} -m ${input_model} -o ${input_operation} ${input_key} >>/dev/null 2>&1 &"
    echo $JAVA_CMD
    eval $JAVA_CMD
    #查询是否有启动进程
    getPid
    if [ ${pid} ]; then
      echo "已启动"
    else
      echo "启动失败"
    fi
  fi
}

#停止程序
function stop() {
  getPid
  if [ ${pid} ]; then
    echo "停止程序---------------------------------------------"
    kill -9 ${pid}
    sleep 5
    getPid
    if [ ${pid} ]; then
      echo "停止失败"
    else
      echo "停止成功"
    fi
  fi
}
#查看日志tailf方式
function taillog() {
  tailf ${INFO_PATH}
}

#启动时带参数，根据参数执行
if [ ${#} -ge 1 ]; then
  case ${1} in
  "start")
    start
    ;;
  "restart")
    start
    ;;
  "status")
    getPid
    ;;
  "taillog")
    taillog
    ;;
  "stop")
    stop
    ;;
  *)
    echo "${1}无任何操作"
    ;;
  esac
else
  echo "
    command如下命令：
    start：启动
    status：状态
    stop：停止进程
    restart：重启
    taillog：查看日志（tailf方式）
    "
fi
