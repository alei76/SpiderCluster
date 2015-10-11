import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omartech.spider.gen.Task;
import com.omartech.spider.gen.TaskType;
import com.omartech.spiderClient.core.SpiderWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-12.
 */
public class TestClient {


    public static void main(String[] args) {

        List<Task> hongguanjingji = hongguanjingji();
        String local = "/tmp/tmpSpider";
        for (Task task : hongguanjingji) {
            SpiderWorker spiderWorker = new SpiderWorker(task, local);
            spiderWorker.run();
        }
    }


    static Gson gson = new Gson();

    static List<Task> hongguanjingji() {//宏观经济
        List<Task> tasks = new ArrayList<>();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headerMap.put("Accept-Encoding", "gzip,deflate,sdch");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
        headerMap.put("Cache-Control", "max-age=0");
        headerMap.put("Connection", "close");
//        headerMap.put("Host", "roll.finance.sina.com.cn");
        headerMap.put("Referer",
                "http://finance.sina.com.cn/chanjing/");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        String url = "http://finance.sina.com.cn/chanjing/rsbd/20150608/204422377874.shtml";
        String refer = "http://www.baidu.com";

        Task task = new Task();

        task.setType(TaskType.Get);
        task.setHeaderJson(gson.toJson(headerMap, new TypeToken<Map<String, String>>() {
        }.getType()));
        task.setRefer(refer);
        task.setName("sina-hongguanjingji");
        task.setUrl(url);

        task.setRecursive(false);
        task.setParseRegex("#Main .listBlk li a");

        tasks.add(task);
//        }
        return tasks;
    }
}
