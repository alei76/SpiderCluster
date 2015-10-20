#!/bin/bash
#check the docker command
command -v docker>/dev/null 2>&1 || { echo >&2 "Docker is not installed in this machine"; exit 1; }

command -v mvn>/dev/null 2>&1 || { echo >&2 "Maven is not installed in this machine"; exit 1; }

#1. 编译项目
(cd spider-client && mvn clean assembly:assembly -Dmaven.test.skip=true -U)

#2. 检查target下是否有正确的jar包
if [ -d "spider-client/target" ]; then
	echo ""
else
	echo "mvn没有成功，检查mvn结果"
	exit
fi

client_name=`ls spider-client/target/ | grep dependencies.jar`
#echo $client_name
if [ ${#client_name} -eq 0 ]; then
  echo "client_name为空, 请检查mvn的结果, client端没有正确生成jar包"
  exit
fi
client_version=${client_name/-jar-with-dependencies.jar/}
client_version=${client_version/spider-client-/}
client_version=${client_version/-SNAPSHOT/}
echo "spider client version: "$client_version
#3. 如果client_name不为空，复制该文件到docker/client/下，检测是否复制成功
cp spider-client/target/$client_name docker/client/
client_path=docker/client/$client_name
echo "目标地址：$client_path"
if [ -f $client_path ]; then
  echo "$client_path 复制成功!"
else
  echo "$client_path 复制失败"
fi

#4. 在docker/client目录下执行生成命令
#cd docker/client/ 
docker_command="docker build -t omartech/spider-client:"$client_version" ."
echo "docker 命令："$docker_command
(cd docker/client/ ; exec $docker_command)

#5. 判断上面命令是否正确执行
if [[ $? -ne 0 ]]; then #异常退出
  echo "docker命令异常退出"
fi

echo "spider-client部分完成"

##spider-server
#0. 编译项目
(cd spider-server && mvn clean assembly:assembly -Dmaven.test.skip=true -U)

#1. 复制spidercluster.sql到目标路径
sql_file="spidercluster.sql"
if [ -f $sql_file ]; then
  echo $sql_file"数据库文件正常"
  cp spidercluster.sql docker/server/
else
  echo $sql_file"文件不存在，运行thift_gen.sh"
  exit 1
fi

#2. 检查target下是否有正确的jar包
if [ -d "spider-server/target" ]; then
	echo ""
else
	echo "mvn没有成功，检查mvn结果"
	exit
fi
echo `pwd`
server_name=`ls spider-server/target/ | grep dependencies.jar`
#echo $server_name
if [[ ${#server_name} -eq 0 ]]; then
  echo "server_name为空, 请检查mvn的结果，server端并没有正确生成jar包"
  exit
fi
server_version=${server_name/-jar-with-dependencies.jar/}
server_version=${server_version/spider-server-/}
server_version=${server_version/-SNAPSHOT/}
echo "spider server version: "$server_version
#3. 如果server_name不为空，复制该文件到docker/server/下，检测是否复制成功
cp spider-server/target/$server_name docker/server/
server_path=docker/server/$server_name
echo "目标地址：$server_path"
if [ -f $server_path ]; then
  echo "$server_path 复制成功!"
else
  echo "$server_path 复制失败"
fi

#4. 在docker/server目录下执行生成命令
docker_command="docker build -t omartech/spider-server:"$server_version" ."
echo "docker 命令："$docker_command
(cd docker/server/ ; exec $docker_command)
#(cd docker/server/ && `docker build -t "omartech/$server_version" .`)

#5. 判断上面命令是否正确执行
if [[ $? -ne 0 ]]; then #异常退出
  echo "docker命令异常退出"
fi

