###Spider-Cluster

该爬虫采用中心节点模式工作，服务器端负责分配任务，客户端负责抓取，然后将任务结果文件发送回服务器端，服务器端将对应的任务删除掉。


Version 2.0 -- plan

* 对文件进行压缩
* 支持在线添加批量任务 √
* 支持POST任务
* 服务器端支持列表任务（添加列表页及其jquery表达式，由客户端直接抓取内容页） √
* 提供Docker安装文件 √
* 提供服务器端批量入种子方式 √
* 提供标准文件处理handler √
* 变更存储文件时间戳为yyyyMMddhhmmss格式 √



Version 1.0 -- done

* 支持GET请求 √
* 服务器端可派发任务 √
* 服务器端可接收任务结果文件 √
* 服务器端可查看客户端状态 √
* 客户端可获取任务并抓取 √
* 客户端可使用代理 √
* 客户端可发送任务结果文件回服务器 √

###结果文件
    按行存储 HtmlObject的json格式

###使用说明

1. 常规GET任务

按照SampleV1进行

2. 下载列表页的任务

按照SampleV2进行


###其他依赖(不是很重要，自己实现就可)

omartech-utils

    基本的支持函数库

proxy-client

    简单的代理客户端，便于获取http代理

###Dockerfile相关说明
路径：docker/client，docker/server

使用：./docker_gen.sh

1. 基镜像为ubuntu+jdk8的版本，只是更改了apt-get的源为163的而已，可替换
2. 生成的镜像版本根据当前jar的版本而命名
3. 若更改端口等操作，到对应client或者server的文件夹中将`run.sh`脚本内容修改即可，暂时不支持用docker宏命令修改
4. server端暂时采用外联mysql的方式，需在run中修改

启动server:

docker run -d -p 7154:7154 -v /tmp/localstore:/spiderstore  omartech/spider-server:2.1

启动client:

docker run --env SERVER=127.0.0.1 --env PORT=7154 --env TIMESPAN=1 -d omartech/spider-client:2.1 

###Future

1. 服务端提供任务和接收文件。
    
    * 发送任务 √
    * 接收文件 √
    * 任务区分优先级
    * 接收error任务并删掉相应任务 

2. 任务按照不同的名字区分。 √

3. 任务的属性：

    * 设定是否使用代理 √
    * 设定抓取间隔
    * 设定header √
    * 设定cookie √

4. 任务的执行：

    * 不同名字的任务用不同的分组执行
    * 分组间相互独立



###协议

    Licensed under the Apache license.

