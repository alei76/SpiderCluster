package com.omartech.spiderHandler.seed;

import com.omartech.spider.gen.Task;
import com.google.gson.Gson;
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
    String dbIpAndPort = "10.1.0.171:3311";
    @Option(name = "-u", usage = "-p set the database username")
    String username = "root";
    @Option(name = "-pw", usage = "-p set the database password")
    String password = "spiderserver";

    private static Logger logger = LoggerFactory.getLogger(ASeedWorker.class);

    public abstract void prepare();

    public void run(String[] args) {

        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
        }
        parser.setUsageWidth(80);
        logger.info("============================");
        logger.info("server runs with args blow:");
        logger.info("database ip and port : {}", dbIpAndPort);
        logger.info("database username : {}", username);
        logger.info("database password : {}", password);
        logger.info("============================");
        prepare();
        beginToWork();
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
                service.scheduleAtFixedRate(new SeedRunner(schedulateTask.task, dataSource), 1, schedulateTask.timespan, schedulateTask.timeUnit);
            } else if (bigTask instanceof OnceTask) {
                OnceTask onceTask = (OnceTask) bigTask;
                service.submit(new SeedRunner(onceTask.task, dataSource));
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
            try (Connection connection = dataSource.getConnection();) {
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

        public OnceTask(Task tas) {
            this.task = tas;
        }
    }

    public class SchedulateTask implements SeedTask {
        Task task;
        int timespan;
        TimeUnit timeUnit;

        public SchedulateTask(Task task, int timespan, TimeUnit timeUnit) {
            this.task = task;
            this.timespan = timespan;
            this.timeUnit = timeUnit;
        }
    }

}
