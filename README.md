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

###Run without docker

./deploy.sh

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
    * 接收error任务并删掉相应任务 √

2. 任务按照不同的名字区分。 √

3. 任务的属性：

    * 设定是否使用代理 √
    * 设定抓取间隔
    * 设定header √
    * 设定cookie √

4. 任务的执行：

    * 不同名字的任务用不同的分组执行
    * 分组间相互独立

###背景

我们做的是分布式爬虫系统

####首先是为什么要做这么个爬虫系统？
虽然网上也有一堆堆开源的工具，然而在定制自己的任务或者管理上都有着各种坑，而且它们提供了太多的功能，并没那么必要。于是决定做一个简单且实用的爬虫。

####我们要做一个什么样的分布式爬虫呢？

    1. 它要有一个主节点，负责分发任务和接收数据。
    2. 有N个从节点，负责抓取。

####那么通常爬虫遇到的痛点是什么呢？

    1. 自己公司的IP可能就几个，于是被目标网站直接屏蔽
    2. 自己爬的快，公司网络被拖慢
    3. 从节点回传数据时很慢，因为回传的文件大。一般有几百兆左右。


####下面开始介绍具体的内容：

    首先是主节点。
    主节点利用jetty启一个web server，然后提供基本的REST接口，包括：插入任务、查询任务、分发任务。

    然后是从节点
    从节点采用Httpclient来实现，实现对task的接收、查重、抓取、存储和回传。

####看一下部署方面：

    1. 实现了一个从程序到镜像的自动化脚本，也就是说：执行该脚本，就可以生成一个对应版本的镜像。
    2. 利用Docker命令进行便捷启动。


####那么如何使用呢？

    1. 提供了Task的类，只需要根据自己的抓取需求来填充Task对象即可。提供了两种Task，一种是一次性的，例如对列表页1-200页全部抓取；另一种是周期性的，例如每隔半小时对首页抓一次。

    2. 提供了Parser类，同样根据自己的需求对目标页面进行解析，也可以使用自主实现的自动提取正文的方法。

实际效果如图：

![任务完成图](https://github.com/sonyfe25cp/SpiderCluster/blob/master/images/tasks.jpg)

![image](https://github.com/sonyfe25cp/SpiderCluster/blob/master/images/status.jpg)




###协议

    Licensed under the Apache license.

