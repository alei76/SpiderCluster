package com.omartech.spiderClient;

import com.omartech.spider.gen.Task;
import com.omartech.spider.gen.TaskResponse;
import com.google.gson.Gson;
import com.omartech.spiderClient.core.SpiderWorker;
import com.omartech.spiderClient.task.TaskMonitor;
import com.omartech.spiderClient.task.TaskResults;
import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by OmarTech on 15-3-14.
 */
public class SpiderClient {
    static Logger logger = LoggerFactory.getLogger(SpiderClient.class);

    @Option(name = "-s", usage = "-s set the server address")
    private String server = "127.0.0.1";

    @Option(name = "-p", usage = "-p set the port")
    private int port = 7154;

    @Option(name = "-d", usage = "-d set the default localstore folder")
    private String localstore = "localstore";

    @Option(name = "-t", usage = "-t set the time span of loop, default = 5 min")
    private int timeSpan = 5;

    @Option(name = "-nd", usage = "-nd debug model, mv files to /test-spider-client/")
    private boolean notDelete = false;


    public static void main(String[] args) {
        new SpiderClient().domain(args);
    }

    void domain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(120);
        try {
            parser.parseArgument(args);
            logger.info("=============================");
            logger.info("client runs with args blow:");
            logger.info("port : {}", port);
            logger.info("server : {}", server);
            logger.info("savefolder : {}", localstore);
            logger.info("timeSpan : {}min", timeSpan);
            logger.info("debug : {}", notDelete);
            logger.info("=============================");
            File folder = new File(localstore);
            if (folder.exists()) {
                if (!folder.isDirectory()) {
                    FileUtils.deleteQuietly(folder);
                    folder.mkdirs();
                }
            } else {
                folder.mkdirs();
            }
            taskMonitor = new TaskMonitor(server, port);
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CmdLineException e) {
            e.printStackTrace();
        }
    }


    String localTaskFile = "__spider-client__task";

    private TaskMonitor taskMonitor;

    void run() throws InterruptedException, IOException {

        logger.info("spider client is working");

        long start = System.currentTimeMillis();
        int taskCount = 0;
        while (true) {
            File file = new File(localTaskFile);
            if (file.exists()) {
                String old = FileUtils.readFileToString(file);
                TaskResponse taskLeft = gson.fromJson(old, TaskResponse.class);
                for (File tmp : new File(localstore).listFiles()) {
                    FileUtils.deleteQuietly(tmp);//删掉未完成的那部分
                }

                if (taskLeft != null) {
                    logger.info("some task is left");
                    consumeTaskResponse(taskLeft);
                    logger.info("task left if over");
                }
            }
            start = System.currentTimeMillis();
            TaskResponse response = taskMonitor.fetchTasks();
            if (response.getTasksSize() > 0) {
                consumeTaskResponse(response);
                logger.info("task {} finish", ++taskCount);
            }
            long end = System.currentTimeMillis();
            if (response.getTasksSize() == 0) {
                logger.info("sleep {} min", timeSpan);
                Thread.sleep(timeSpan * 1000 * 60);
            }

        }
    }

    private void consumeTaskResponse(TaskResponse response) throws InterruptedException {
        List<Task> tasks = response.getTasks();
        if (tasks.size() > 0) {

            recordTask(response);
            for (Task task : tasks) {
                logger.info(task.toString());
                try {
                    new SpiderWorker(task, localstore).run();
//                    new Thread(new SpiderWorker(task, localstore)).start();
                } catch (Exception e) {
                    logger.error("{} is wrong", task.getUrl());
                }
            }
            logger.info("task size : {}, finish", response.getTasksSize());

            FileUtils.deleteQuietly(new File(localTaskFile));
            File folder = new File(localstore);
            int fileCount = 0;
            for (File tmp : folder.listFiles()) {
                TaskResults taskResults = new TaskResults(tmp);
                boolean failed = true;
                do {
                    boolean status = taskMonitor.sendResults(taskResults);
                    if (status) {
                        logger.info("数据发送完毕, 删掉文件");
                        if (notDelete) {
                            String tmpPath = "test-spider-client";
                            File testFolder = new File(tmpPath);
                            if (!testFolder.exists()) {
                                testFolder.mkdir();
                            }
                            String name = tmp.getName()+"-"+new Random().nextInt();
                            File newFile = new File(tmpPath+File.separator+name);
                            try {
                                FileUtils.copyFile(tmp, newFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        FileUtils.deleteQuietly(tmp);
                        failed = false;
                    } else {
                        logger.info("发送数据失败，休息{}min", timeSpan);
                        Thread.sleep(timeSpan * 1000 * 60);
                        failed = true;
                    }
                } while (failed);
                fileCount++;
            }
            logger.info("本次任务共计{}个文件。", fileCount);
        }
    }

    Gson gson = new Gson();

    protected void recordTask(TaskResponse response) {
        try {
            File file = new File(localTaskFile);
            String toJson = gson.toJson(response);
            FileUtils.write(file, toJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
