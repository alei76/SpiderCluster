package com.omartech.spiderClient.core;

import cn.omartech.spider.gen.HtmlObject;
import cn.omartech.spider.gen.Task;
import cn.omartech.spider.gen.TaskType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omartech.proxy.proxy_client.ProxyClient;
import com.techwolf.omartech_utils.spider.DefetcherUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by OmarTech on 15-3-14.
 */
public class SpiderWorker implements Runnable {
    static Logger logger = LoggerFactory.getLogger(SpiderWorker.class);
    private Task task;

    public SpiderWorker(Task task) {

        this.task = task;
    }

    private static final int MaxRetry = 3;

    @Override
    public void run() {
        int times = 0;
        String html = null;
        try {
            do {
                if (task.useProxy) {
                    ProxyClient proxyClient = new ProxyClient();
                    HttpHost proxy = proxyClient.fetchOne();
                    html = work(proxy);
                } else {
                    html = work(null);
                }
                times++;
            } while (StringUtils.isEmpty(html) && times < MaxRetry);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("task :{},  url : {} is over", task.getName(), task.getUrl());
    }

    private String work(HttpHost proxy) throws InterruptedException {
        long id = task.getId();
        String cookie = task.getCookie();
        String headerJson = task.getHeaderJson();
        String parameterJson = task.getParameterJson();
        String url = task.getUrl();
        TaskType type = task.getType();
        String localStoreFile = task.getLocalStoreFile();
        String refer = task.getRefer();
        String name = task.getName();
        if (StringUtils.isEmpty(localStoreFile)) {
            logger.error("please set the local file to each task!");
            return null;
        }
        String html = null;

        HttpClient client = HttpClientBuilder.create().build();

        switch (type) {
            case Get:
                HttpGet get = new HttpGet(url);
                if (!StringUtils.isEmpty(headerJson)) {
                    Map<String, String> headers = decodeMap(headerJson);
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        get.setHeader(entry.getKey(), entry.getValue());
                        logger.debug("header: {} -- {}", entry.getKey(), entry.getValue());
                    }
                }
                if (!StringUtils.isEmpty(cookie)) {
                    logger.debug("Cookie : {}", cookie);
                    get.setHeader("Cookie", cookie);
                }
                if (!StringUtils.isEmpty(parameterJson)) {
                    Map<String, String> parameters = decodeMap(parameterJson);
                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        BasicNameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    }
                }
                RequestConfig.Builder config = RequestConfig.custom().setSocketTimeout(40000)
                        .setConnectTimeout(15000).setRedirectsEnabled(false);
                if (task.useProxy) {
                    get.setHeader("Proxy-Connection", "keep-alive");
                    config.setProxy(proxy);
                }
                get.setHeader("Referer", refer);
                get.setConfig(config.build());
                try {
                    HttpResponse httpResponse = client.execute(get);
                    int statusCode = httpResponse.getStatusLine().getStatusCode();
                    switch (statusCode) {
                        case 200:
                            html = DefetcherUtils.toString(httpResponse);
                            HtmlObject object = new HtmlObject();
                            object.setUrl(url);
                            object.setTaskName(name);
                            object.setHtml(html);
                            object.setTaskId(id);
                            String toJson = gson.toJson(object);
                            FileUtils.write(new File(localStoreFile), toJson + "\n", true);
                            break;
                        default:
                            logger.info("url: {}, status code:{}, with proxy :{}", new String[]{url, statusCode + "", proxy == null ? "null" : proxy.toHostString()});
                            break;
                    }
                } catch (UnknownHostException e) {
                    logger.error("dns broke down, sleep 5min");
                    Thread.sleep(5 * 1000 * 60);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case Post:
                break;
            default:
                break;
        }
        return html;
    }

    private static Gson gson = new Gson();

    private static Map<String, String> decodeMap(String json) {
        Map<String, String> map = gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
        return map;
    }
}
