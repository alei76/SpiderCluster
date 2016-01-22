import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omartech.spider.gen.ContentType;
import com.omartech.spider.gen.Task;
import com.omartech.spider.gen.TaskType;
import com.omartech.spiderClient.core.SpiderWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by omar on 16/1/22.
 */
public class TestSinaChanye {
    static Logger logger = LoggerFactory.getLogger(TestSinaChanye.class);

    public static void main(String[] args) {

        List<Task> hongguanjingji = chanyexinwen();
        String local = "/tmp/tmpSpider";
        for (Task task : hongguanjingji) {
            SpiderWorker spiderWorker = new SpiderWorker(task, local);
            spiderWorker.run();
        }
    }

    static Gson gson = new Gson();

    public static List<Task> chanyexinwen() {//产业新闻
        List<Task> tasks = new ArrayList<>();
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

        for (int i = 1; i < 2; i++) {
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
            task.setContentType(ContentType.GBK);
            task.setRecursive(true);
            task.setParseRegex("#Main .listBlk li a");
            tasks.add(task);
        }
        logger.info("产业新闻 seeds is ok");
        return tasks;
    }

}
