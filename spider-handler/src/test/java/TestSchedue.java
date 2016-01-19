import com.google.gson.reflect.TypeToken;
import com.omartech.spider.gen.Task;
import com.omartech.spider.gen.TaskType;
import com.omartech.spiderHandler.seed.ASeedWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestSchedue extends ASeedWorker {
    static Logger logger = LoggerFactory.getLogger(TestSchedue.class);

    @Override
    public void prepare() {
        //Observation
        List<SeedTask> all = allNews();
        addSeedTasks(all);
    }

    List<SeedTask> allNews() {
        List<SeedTask> tasks = new ArrayList<>();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headerMap.put("Accept-Encoding", "gzip,deflate,sdch");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
        headerMap.put("Connection", "close");
        headerMap.put("Host", "wms.mofcom.gov.cn");
        headerMap.put("Referer",
                "http://wms.mofcom.gov.cn/");
        headerMap
                .put("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");

        for (int i = 1; i <= 5; i++) {
            String url, refer;
            if (i == 1) {
                url = "http://wms.mofcom.gov.cn/article/xxfb/";
                refer = "http://wms.mofcom.gov.cn/";
            } else if (i == 2) {
                url = "http://wms.mofcom.gov.cn/article/xxfb/?2";
                refer = "http://wms.mofcom.gov.cn/article/xxfb/";
            } else {
                url = "http://wms.mofcom.gov.cn/article/xxfb/?" + i;
                refer = "http://wms.mofcom.gov.cn/article/xxfb/?" + (i - 1);
            }

            Task task = new Task();

            task.setType(TaskType.Get);
            task.setHeaderJson(gson.toJson(headerMap, new TypeToken<Map<String, String>>() {
            }.getType()));
            task.setRefer(refer);
            task.setName("mofcom-Policy");
            task.setUrl(url);

            task.setRecursive(true);


            task.setParseRegex(".alist a");

            OnceTask onceTask = new OnceTask(task);
            tasks.add(onceTask);

            if (i == 1) {//定时抓前5页
                SchedulateTask schedulateTask = new SchedulateTask(task, 3, TimeUnit.SECONDS);
                tasks.add(schedulateTask);
            }
        }
        logger.info("{} seeds is ok", "mofcom");

        return tasks;
    }

    public static void main(String[] args) {
        TestSchedue testSchedue = new TestSchedue();
        testSchedue.dbIpAndPort = "127.0.0.1:3306";
        testSchedue.username = "root";
        testSchedue.password = "";
        testSchedue.run(args);
    }
}
