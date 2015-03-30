package com.omartech.spiderHandler.seed;

import cn.omartech.spider.gen.Task;
import cn.omartech.spider.gen.TaskType;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by OmarTech on 15-3-27.
 */
public class SinaSeed extends ASeedWorker {

    static Logger logger = LoggerFactory.getLogger(SinaSeed.class);

    public void prepare() {

        List<SeedTask> chanjings = chanjings();
        addSeedTasks(chanjings);

    }

    List<SeedTask> chanjings() {//产经新闻
        List<SeedTask> tasks = new ArrayList<>();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headerMap.put("Accept-Encoding", "gzip,deflate,sdch");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
        headerMap.put("Cache-Control", "max-age=0");
        headerMap.put("Connection", "close");
        headerMap.put("Host", "roll.finance.sina.com.cn");
        headerMap.put("Referer",
                "http://roll.finance.sina.com.cn/finance/sh2/xfylc-bgt/index_1.shtml");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        for (int i = 1; i < 87; i++) {
            String url = "http://roll.finance.sina.com.cn/finance/sh2/qyzh/index_" + i
                    + ".shtml";
            String refer = "http://roll.finance.sina.com.cn/finance/sh2/qyzh/index_" + (i - 1)
                    + ".shtml";

            Task task = new Task();

            task.setType(TaskType.Get);
            task.setHeaderJson(gson.toJson(headerMap, new TypeToken<Map<String, String>>() {
            }.getType()));
            task.setRefer(refer);
            task.setName("qiyezhaohui");
            task.setUrl(url);

            task.setRecursive(true);
            task.setParseRegex("#Main .listBlk li a");

            OnceTask onceTask = new OnceTask(task);
            tasks.add(onceTask);

            if (i < 5) {//定时抓前5页
                SchedulateTask schedulateTask = new SchedulateTask(task, 5, TimeUnit.HOURS);
                tasks.add(schedulateTask);
            }
        }
        logger.info("chanjing seeds is ok");
        return tasks;
    }


    public static void main(String[] args) {

        SinaSeed sinaSeed = new SinaSeed();
        sinaSeed.run(args);

    }


}
