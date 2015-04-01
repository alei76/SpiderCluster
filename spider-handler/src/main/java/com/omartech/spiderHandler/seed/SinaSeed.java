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

        List<SeedTask> qiyezhaohui = qiyezhaohui();
        List<SeedTask> gongsixinwen = gongsixinwen();
        List<SeedTask> chanyexinwen = chanyexinwen();
        List<SeedTask> renshibiandong = renshibiandong();
        List<SeedTask> jingchanguancha = jingchanguancha();
        List<SeedTask> hongguanjingji = hongguanjingji();

        addSeedTasks(hongguanjingji);
        addSeedTasks(jingchanguancha);
        addSeedTasks(renshibiandong);
        addSeedTasks(qiyezhaohui);
        addSeedTasks(chanyexinwen);
        addSeedTasks(gongsixinwen);


    }

    List<SeedTask> hongguanjingji() {//宏观经济
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
                "http://finance.sina.com.cn/chanjing/");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        for (int i = 1; i < 300; i++) {
            String url = "http://roll.finance.sina.com.cn/finance/gncj/hgjj/index_" + i
                    + ".shtml";
            String refer = "http://roll.finance.sina.com.cn/finance/gncj/hgjj/index_" + (i - 1)
                    + ".shtml";

            Task task = new Task();

            task.setType(TaskType.Get);
            task.setHeaderJson(gson.toJson(headerMap, new TypeToken<Map<String, String>>() {
            }.getType()));
            task.setRefer(refer);
            task.setName("sina-hongguanjingji");
            task.setUrl(url);

            task.setRecursive(true);
            task.setParseRegex("#Main .listBlk li a");

            OnceTask onceTask = new OnceTask(task);
            tasks.add(onceTask);

            if (i <= 1) {//定时抓前5页
                SchedulateTask schedulateTask = new SchedulateTask(task, 10, TimeUnit.HOURS);
                tasks.add(schedulateTask);
            }
        }
        logger.info("宏观经济 seeds is ok");
        return tasks;
    }

    List<SeedTask> jingchanguancha() {//经产观察
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
                "http://finance.sina.com.cn/chanjing/");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        for (int i = 1; i < 300; i++) {
            String url = "http://roll.finance.sina.com.cn/finance/pl1/jcgc/index_" + i
                    + ".shtml";
            String refer = "http://roll.finance.sina.com.cn/finance/pl1/jcgc/index_" + (i - 1)
                    + ".shtml";

            Task task = new Task();

            task.setType(TaskType.Get);
            task.setHeaderJson(gson.toJson(headerMap, new TypeToken<Map<String, String>>() {
            }.getType()));
            task.setRefer(refer);
            task.setName("sina-jingchanguancha");
            task.setUrl(url);

            task.setRecursive(true);
            task.setParseRegex("#Main .listBlk li a");

            OnceTask onceTask = new OnceTask(task);
            tasks.add(onceTask);

            if (i <= 1) {//定时抓前5页
                SchedulateTask schedulateTask = new SchedulateTask(task, 10, TimeUnit.HOURS);
                tasks.add(schedulateTask);
            }
        }
        logger.info("经产观察 seeds is ok");
        return tasks;
    }

    List<SeedTask> renshibiandong() {//人事变动
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
                "http://finance.sina.com.cn/chanjing/");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        for (int i = 1; i < 35; i++) {
            String url = "http://roll.finance.sina.com.cn/finance/cj4/rsbd/index_" + i
                    + ".shtml";
            String refer = "http://roll.finance.sina.com.cn/finance/cj4/rsbd/index_" + (i - 1)
                    + ".shtml";

            Task task = new Task();

            task.setType(TaskType.Get);
            task.setHeaderJson(gson.toJson(headerMap, new TypeToken<Map<String, String>>() {
            }.getType()));
            task.setRefer(refer);
            task.setName("sina-renshibiandong");
            task.setUrl(url);

            task.setRecursive(true);
            task.setParseRegex("#Main .listBlk li a");

            OnceTask onceTask = new OnceTask(task);
            tasks.add(onceTask);

            if (i <= 1) {//定时抓前5页
                SchedulateTask schedulateTask = new SchedulateTask(task, 20, TimeUnit.HOURS);
                tasks.add(schedulateTask);
            }
        }
        logger.info("人事变动 seeds is ok");
        return tasks;
    }

    List<SeedTask> chanyexinwen() {//产业新闻
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
                "http://finance.sina.com.cn/chanjing/");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        for (int i = 1; i < 230; i++) {
            String url = "http://roll.finance.sina.com.cn/finance/cj4/cj_cyxw/index_" + i
                    + ".shtml";
            String refer = "http://roll.finance.sina.com.cn/finance/cj4/cj_cyxw/index_" + (i - 1)
                    + ".shtml";

            Task task = new Task();

            task.setType(TaskType.Get);
            task.setHeaderJson(gson.toJson(headerMap, new TypeToken<Map<String, String>>() {
            }.getType()));
            task.setRefer(refer);
            task.setName("sina-chanyexinwen");
            task.setUrl(url);

            task.setRecursive(true);
            task.setParseRegex("#Main .listBlk li a");

            OnceTask onceTask = new OnceTask(task);
            tasks.add(onceTask);

            if (i <= 3) {//定时抓前5页
                SchedulateTask schedulateTask = new SchedulateTask(task, 5, TimeUnit.HOURS);
                tasks.add(schedulateTask);
            }
        }
        logger.info("产业新闻 seeds is ok");
        return tasks;
    }

    List<SeedTask> gongsixinwen() {//公司新闻
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
                "http://finance.sina.com.cn/chanjing/");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        for (int i = 1; i < 150; i++) {
            String url = "http://roll.finance.sina.com.cn/finance/cj4/cj_gsxw/index_" + i
                    + ".shtml";
            String refer = "http://roll.finance.sina.com.cn/finance/cj4/cj_gsxw/index_" + (i - 1)
                    + ".shtml";

            Task task = new Task();

            task.setType(TaskType.Get);
            task.setHeaderJson(gson.toJson(headerMap, new TypeToken<Map<String, String>>() {
            }.getType()));
            task.setRefer(refer);
            task.setName("sina-gongsixinwen");
            task.setUrl(url);

            task.setRecursive(true);
            task.setParseRegex("#Main .listBlk li a");

            OnceTask onceTask = new OnceTask(task);
            tasks.add(onceTask);

            if (i <= 3) {//定时抓前5页
                SchedulateTask schedulateTask = new SchedulateTask(task, 5, TimeUnit.HOURS);
                tasks.add(schedulateTask);
            }
        }
        logger.info("公司新闻 seeds is ok");
        return tasks;
    }


    List<SeedTask> qiyezhaohui() {//企业召回
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
            task.setName("sina-qiyezhaohui");
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
