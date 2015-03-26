package com.omartech.spiderServer.sample;

import cn.omartech.spider.gen.SubTask;
import cn.omartech.spider.gen.Task;
import cn.omartech.spider.gen.TaskType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omartech.spiderServer.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-3-15.
 */
public class SampleV2 {
    static Logger logger = LoggerFactory.getLogger(SampleV2.class);

    public static void main(String[] args) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headerMap.put("Accept-Encoding", "gzip,deflate,sdch");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
        headerMap.put("Cache-Control", "max-age=0");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Host", "roll.finance.sina.com.cn");
        headerMap.put("Referer",
                "http://roll.finance.sina.com.cn/finance/sh2/xfylc-bgt/index_1.shtml");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
        Gson gson = new Gson();

        List<Task> tasks = new ArrayList<>();

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


            tasks.add(task);

        }

        Connection connection = con.get();
        try {
            DBService.insertTasks(connection, tasks);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    static ThreadLocal<Connection> con = new InheritableThreadLocal<Connection>() {
        @Override
        protected Connection initialValue() {
            Connection conn = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/spidercluster", "root", "");
                logger.info("new connection to weixin");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    };
}
