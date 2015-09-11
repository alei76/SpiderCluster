package com.omartech.spiderClient.task;

import com.omartech.spider.gen.TaskResponse;
import com.google.gson.Gson;
import com.omartech.utils.spider.DefetcherUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by OmarTech on 15-3-14.
 */
public class TaskMonitor {
    static Logger logger = LoggerFactory.getLogger(TaskMonitor.class);

    public TaskMonitor(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
        this.FETCHTASKURL = "http://" + serverIp + ":" + port + "/fetchtasks";
        this.SENDRESULTSURL = "http://" + serverIp + ":" + port + "/sendresults";
    }

    private int port = 7154;
    private String serverIp;
    private String FETCHTASKURL;
    private String SENDRESULTSURL;

    private static Gson gson = new Gson();

    public boolean sendResults(TaskResults taskResults) {

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(SENDRESULTSURL);
        boolean flag = false;

        FileBody file = new FileBody(taskResults.getFile());

        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("file", file)
                .build();

        post.setEntity(reqEntity);

        try {
            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200:
                    String result = DefetcherUtils.toString(response);
                    logger.info(result);
                    flag = true;
                    break;
                default:
                    logger.error("send result back to server , code : {}", statusCode);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }

    public TaskResponse fetchTasks() {
        TaskResponse taskResponse = new TaskResponse();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(FETCHTASKURL);
        try {
            HttpResponse response = client.execute(get);
            int statusLine = response.getStatusLine().getStatusCode();
            switch (statusLine) {
                case 200:
                    HttpEntity entity = response.getEntity();

                    String json = DefetcherUtils.toString(response);
                    if (!StringUtils.isEmpty(json)) {
                        try {
                            taskResponse = gson.fromJson(json, TaskResponse.class);
                            logger.info("fetch tasks, task size:{}", taskResponse.getTasks().size());
                        } catch (Exception e) {
                            logger.error("error : {}", json);
                        }
                    }
                    EntityUtils.consumeQuietly(entity);
                    break;
                default:
                    logger.info("the server is somthing wrong : {}", FETCHTASKURL);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskResponse;
    }

}
