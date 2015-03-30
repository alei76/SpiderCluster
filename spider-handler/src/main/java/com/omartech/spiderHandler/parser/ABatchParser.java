package com.omartech.spiderHandler.parser;

import cn.omartech.spider.gen.HtmlObject;
import com.google.gson.Gson;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by OmarTech on 15-3-18.
 */
public abstract class ABatchParser {

    static Logger logger = LoggerFactory.getLogger(ABatchParser.class);

    @Option(name = "-d", usage = "-d set the default dir")
    private String folder = "spider-server-store";

    @Option(name = "-c", usage = "-c set how many thread to work")
    private int cpu = Runtime.getRuntime().availableProcessors();

    public void domain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);
        logger.info("============================");
        logger.info("set the default store : {}", folder);
        logger.info("use {} thread to parse", cpu);
        logger.info("============================");

        try {
            List<HtmlObject> htmlObjects = parseFolder(new File(folder));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public abstract void consumeHtmlObject(HtmlObject object);


    static Gson gson = new Gson();


    ThreadPoolExecutor executor = new ThreadPoolExecutor(cpu, cpu, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(cpu * 3), new ThreadPoolExecutor.CallerRunsPolicy());


    public void work() throws IOException {
        if (cpu <= 1) {
            parseFolder(new File(folder));
        } else {
            workWithMultiThread(new File(folder));
        }
    }


    public void workWithMultiThread(File folder) {
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                executor.submit(new Worker(file));
            }
        }
    }


    class Worker implements Runnable {

        private File file;

        public Worker(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            try {
                HtmlObject object = parseFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static List<HtmlObject> parseFolder(File folder) throws IOException {
        List<HtmlObject> list = new ArrayList<>();
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                HtmlObject object = parseFile(file);
                if (object != null) {
                    list.add(object);
                }
            }
        }
        return list;
    }

    public static HtmlObject parseFile(File file) throws IOException {
        HtmlObject object = null;
        if (file.exists() && file.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file));) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    object = gson.fromJson(line, HtmlObject.class);
                }
            }
        }
        return object;
    }


}
