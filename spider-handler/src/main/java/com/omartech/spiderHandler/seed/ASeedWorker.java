package com.omartech.spiderHandler.seed;

import com.google.gson.Gson;
import com.omartech.spider.gen.Task;
import com.omartech.spider.gen.TaskStatus;
import com.omartech.spiderServer.DBService;
import org.apache.commons.dbcp2.BasicDataSource;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by OmarTech on 15-3-27.
 */
public abstract class ASeedWorker {

    @Option(name = "-dbp", usage = "-dbp set the database ip and port, like 127.0.0.1:3306")
    protected String dbIpAndPort = "10.1.0.171:3311";
    @Option(name = "-u", usage = "-u set the database username")
    protected String username = "root";
    @Option(name = "-pw", usage = "-pw set the database password")
    protected String password = "spiderserver";
    @Option(name = "-once", usage = "-once is force insert the once task again, default is false")
    protected boolean forceOnce = false;

    @Option(name = "-help", usage = "-help ")
    protected boolean help = false;

    private static Logger logger = LoggerFactory.getLogger(ASeedWorker.class);

    public abstract void prepare();//add seeds

    public void run(String[] args) {

        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
        }
        parser.setUsageWidth(80);
        if (!help) {
            logger.info("============================");
            logger.info("server runs with args blow:");
            logger.info("database ip and port : {}", dbIpAndPort);
            logger.info("database username : {}", username);
            logger.info("database password : {}", password);
            logger.info("insert once task: {}", forceOnce);
            logger.info("============================");
            prepare();
            beginToWork();
        } else {
            logger.info("show all the params:");
            logger.info("-help help");
            logger.info("-dbp set the database ip and port, like 127.0.0.1:3306");
            logger.info("-u set the database username");
            logger.info("-pw set the database password");
            logger.info("-once is force insert the once task again, default is false");
        }
    }

    private List<SeedTask> tasks = new ArrayList<>();


    private void addScheduleTask(SchedulateTask schedulateTask) {
        Task task = schedulateTask.task;
        int timespan = schedulateTask.timespan;
        TimeUnit timeUnit = schedulateTask.timeUnit;
        SchedulateTask bigTask = new SchedulateTask(task, timespan, timeUnit);
        tasks.add(bigTask);
        logger.info("add schedule task {} with every {} {}", new String[]{task.getName(), timespan + "", timeUnit.toString()});
    }

    private void addOnceTask(OnceTask onceTask) {
        OnceTask bigTask = new OnceTask(onceTask.task);
        tasks.add(bigTask);
    }

    public void addSeedTasks(List<SeedTask> tasks) {
        for (SeedTask seedTask : tasks) {
            if (seedTask instanceof SchedulateTask) {
                SchedulateTask schedulateTask = (SchedulateTask) seedTask;
                addScheduleTask(schedulateTask);
            } else if (seedTask instanceof OnceTask) {
                OnceTask onceTask = (OnceTask) seedTask;
                addOnceTask(onceTask);
            } else {
                logger.error("wrong task type");
            }
        }
    }


    protected Gson gson = new Gson();

    private void beginToWork() {
        if (tasks.size() == 0) {
            logger.info("no seedwork in the list.");
            return;
        }
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://" + dbIpAndPort + "/spidercluster");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource.setInitialSize(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(2);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        for (SeedTask bigTask : tasks) {

            if (bigTask instanceof SchedulateTask) {
                SchedulateTask schedulateTask = (SchedulateTask) bigTask;
                SeedRunner seedRunner = new SeedRunner(schedulateTask.task, dataSource);
                service.scheduleAtFixedRate(seedRunner, 1, schedulateTask.timespan, schedulateTask.timeUnit);
            } else if (bigTask instanceof OnceTask) {
                if (forceOnce) {
                    OnceTask onceTask = (OnceTask) bigTask;
                    service.submit(new SeedRunner(onceTask.task, dataSource));
                }
            }
        }
    }

    private class SeedRunner implements Runnable {

        private Task task;
        private DataSource dataSource;

        public SeedRunner(Task task, DataSource dataSource) {
            this.task = task;
            this.dataSource = dataSource;
        }

        @Override
        public void run() {
            logger.info("insert task : {}", task);
            try (Connection connection = dataSource.getConnection()) {
                DBService.insertTask(connection, task);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public interface SeedTask {

    }

    public class OnceTask implements SeedTask {
        Task task;

        public OnceTask(Task task) {
            checkTask(task);
            this.task = task;
        }
    }

    public class SchedulateTask implements SeedTask {
        Task task;
        int timespan;
        TimeUnit timeUnit;

        public SchedulateTask(Task task, int timespan, TimeUnit timeUnit) {
            checkTask(task);
            this.task = task;
            this.timespan = timespan;
            this.timeUnit = timeUnit;
        }

        @Override
        public String toString() {
            return "SchedulateTask{" +
                    "task=" + task +
                    ", timespan=" + timespan +
                    ", timeUnit=" + timeUnit +
                    '}';
        }
    }

    public static boolean checkTask(Task task) {
        TaskStatus taskStatus = task.getTaskStatus();
        boolean flag = true;
        if (taskStatus == null) {
            logger.error("taskStatus is null");
        }
        return flag;
    }
}
