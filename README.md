###Spider-Cluster

该爬虫采用中心节点模式工作，服务器端负责分配任务，客户端负责抓取，然后将任务结果文件发送回服务器端，服务器端将对应的任务删除掉。


Version 2.0 -- plan

* 支持POST任务
* 服务器端支持列表任务（添加列表页及其jquery表达式，由客户端直接抓取内容页） √
* 提供Docker安装文件
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

###协议

    Licensed under the Apache license.
