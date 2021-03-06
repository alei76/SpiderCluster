package com.omartech.spiderClient.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omartech.proxy.proxy_client.ProxyClient;
import com.omartech.spider.gen.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-3-14.
 */
public class SpiderWorker {
    static Logger logger = LoggerFactory.getLogger(SpiderWorker.class);
    private Task task;

    private String localstore;

    public SpiderWorker(Task task, String localstore) {
        this.task = task;
        this.localstore = localstore;
    }

    private static final int MaxRetry = 3;

    private List<HtmlObject> objects = new ArrayList<>();
    private List<Long> errors = new ArrayList<>();

    void doTask(Task task) {
        int times = 0;
        FetchResponse response = null;
        try {
            do {
                if (task.useProxy) {
                    ProxyClient proxyClient = new ProxyClient();
                    HttpHost proxy = proxyClient.fetchOne();
                    response = work(task, proxy);
                } else {
                    response = work(task, null);
                }
                times++;

                int statusCode = response.statusCode;
                if (statusCode != 200) {
                    long taskId = task.getId();
                    errors.add(taskId);
                }

            } while (retryLogic(response, times));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("task :{},  url : {} is over， objects size:{}", new String[]{task.getName(), task.getUrl(), objects.size() + ""});
    }

    boolean retryLogic(FetchResponse response, int times) {
        boolean flag = false;

        int statusCode = response.statusCode;

        if (StringUtils.isEmpty(response.html)) {
            if (statusCode == 200 || statusCode == 0) {
                flag = true;
            }
        }
        return flag && (times < MaxRetry);
    }


    public static boolean isSameSite(String url1, String url2) {
        boolean same = false;
        if (url1.startsWith("http") && url2.startsWith("http")) {
            String host = URLRefiner.findHost(url1);
            String host2 = URLRefiner.findHost(url2);
            if (host.contains(".") && host2.contains(".")) {
                host = host.substring(host.indexOf("."));
                host2 = host2.substring(host2.indexOf("."));
            }
            if (host.equals(host2)) {
                same = true;
            }
        }
        if (!same) {
            logger.warn("{} is not same to {}", url1, url2);
        }
        return same;
    }

    public void run() {
        if (task.recursive) {//recursive
            doTask(task);
            if (objects.size() > 0) {
                HtmlObject object = objects.get(0);
                objects.clear();
                String html = object.getHtml();
                Document document = Jsoup.parse(html);
                Elements elements = document.select(task.getParseRegex());
                String subTaskJson = task.getSubTaskJson();
                SubTask subTask = gson.fromJson(subTaskJson, SubTask.class);
                subTask = subTask == null ? new SubTask() : subTask;
                List<Task> newTasks = new ArrayList<>();
                if (elements != null && elements.size() > 0) {
                    for (Element ele : elements) {
                        String href = ele.attr("href");
                        if (!href.startsWith("http")) {
                            href = URLRefiner.refineURL(task.getUrl(), href);
                        }
                        Task newTask = new Task();
                        newTask.setUrl(href);
                        newTask.setCookie(subTask.getCookie());
                        if (isSameSite(task.getUrl(), href)) {
                            newTask.setHeaderJson(subTask.getHeaderJson());
                        }
                        newTask.setId(task.getId());
                        newTask.setUseProxy(subTask.useProxy);
                        newTask.setRefer(task.getUrl());
                        newTask.setName(task.getName());
                        newTasks.add(newTask);
                    }
                    for (Task tmp : newTasks) {
                        doTask(tmp);
                    }
                } else {//fix bugs when no content in the regex, the spider will not stop
                    HtmlObject object1 = new HtmlObject();
                    object1.setTaskId(task.getId());
                    object1.setTaskName(task.getName());
                    objects.add(object1);
                }
            }
        } else {
            doTask(task);
        }

        for (HtmlObject object : objects) {
            String taskName = object.getTaskName();
            String json = gson.toJson(object);
            try {
                FileUtils.write(new File(localstore + File.separator + taskName), json + "\n", "UTF-8", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (long taskId : errors) {
            try {
                FileUtils.write(new File(localstore + File.separator + "error_tasks"), taskId + "\n","UTF-8", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private FetchResponse work(Task task, HttpHost proxy) throws InterruptedException {
        long id = task.getId();
        String cookie = task.getCookie();
        String headerJson = task.getHeaderJson();
        String parameterJson = task.getParameterJson();
        String url = task.getUrl();
        TaskType type = task.getType();
        String refer = task.getRefer();
        String name = task.getName();
        String html = null;

        FetchResponse fetchResponse = new FetchResponse();

        try (CloseableHttpClient client = HttpClientBuilder.create().build();) {
            switch (type) {
                case Get:
                    HttpGet get = null;
                    try {
                        get = new HttpGet(url);
                    } catch (Exception e) {
                        get.abort();
                        break;
                    }
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
                        fetchResponse.statusCode = statusCode;
                        switch (statusCode) {
                            case 200:
                                ContentType contentType = task.getContentType();
                                if (contentType == null) {
                                    html = DefetcherUtils.toString(httpResponse);
                                } else {
                                    html = DefetcherUtils.toString(httpResponse, contentType.name());
                                }
                                HtmlObject object = new HtmlObject();
                                object.setUrl(url);
                                object.setTaskName(name);
                                object.setHtml(html);
                                object.setTaskId(id);
                                objects.add(object);
                                break;
                            case 302:
                                Header[] allHeaders = httpResponse.getAllHeaders();
                                for (Header header : allHeaders) {
//                                    logger.info("header, key: {}, value: {}", header.getName(), header.getValue());
                                    if (header.getName().equals("Location")) {
                                        logger.info("302, from {} to {}", url, header.getValue());
                                    }
                                }
                                break;
                            default:
                                logger.info("url: {}, status code:{}, with proxy :{}", new String[]{url, statusCode + "", proxy == null ? "null" : proxy.toHostString()});
                                EntityUtils.consumeQuietly(httpResponse.getEntity());
                                break;
                        }
                    } catch (EOFException e) {
                        logger.error("eof broke down, sleep 10s, url:{}", url);
                        Thread.sleep(10 * 1000);
                    } catch (UnknownHostException e) {
                        logger.error("dns broke down, sleep 10s, {}", url);
                        Thread.sleep(10 * 1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        get.releaseConnection();
                    }
                    break;
                case Post:
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fetchResponse.html = html;
        return fetchResponse;
    }

    private static Gson gson = new Gson();

    private static Map<String, String> decodeMap(String json) {
        Map<String, String> map = gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
        return map;
    }
}

class FetchResponse {
    String html;
    int statusCode = NULL;
    public static int NULL = 0;
}
