package com.omartech.spiderHandler.seed;

import cn.omartech.spider.gen.Task;
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
public class ASeedWorker {

    @Option(name = "-dbp", usage = "-dbp set the database ip and port, like 127.0.0.1:3306")
    String dbIpAndPort = "127.0.0.1:3306";
    @Option(name = "-u", usage = "-p set the database username")
    String username = "root";
    @Option(name = "-pw", usage = "-p set the database password")
    String password = "spiderserver";

    private static Logger logger = LoggerFactory.getLogger(ASeedWorker.class);

    public void domain(String[] args) {

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
        run();
    }

    private List<BigTask> tasks = new ArrayList<>();


    public void addTask(Task task, int timespan, TimeUnit timeUnit) {
        BigTask bigTask = new BigTask(task, timespan, timeUnit);
        tasks.add(bigTask);
    }


    private void run() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://" + dbIpAndPort + "/spidercluster");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource.setInitialSize(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(2);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        for (BigTask bigTask : tasks) {
            service.scheduleAtFixedRate(new SeedRunner(bigTask.task, dataSource), 1, bigTask.timespan, bigTask.timeUnit);
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


    private class BigTask {
        Task task;
        int timespan;
        TimeUnit timeUnit;

        public BigTask(Task task, int timespan, TimeUnit timeUnit) {
            this.task = task;
            this.timespan = timespan;
            this.timeUnit = timeUnit;
        }
    }

}
