package com.omartech.spiderServer;

import com.omartech.spiderServer.handler.RequestHandler;
import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.jetty.server.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by OmarTech on 15-3-14.
 */
public class SpiderServer {

    static Logger logger = LoggerFactory.getLogger(SpiderServer.class);
    @Option(name = "-d", usage = "-d set the default storeDir for store")
    private String storeDir = "spider-server-store";

    @Option(name = "-p", usage = "-p set the port")
    private int port = 7154;

    @Option(name = "-dbp", usage = "-dbp set the database ip and port, like 127.0.0.1:3306")
    private String dbIpandPort = "127.0.0.1:3306";
    @Option(name = "-u", usage = "-p set the database username")
    private String username = "root";
    @Option(name = "-pw", usage = "-p set the database password")
    private String password = "";

    void domain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
        }
        parser.setUsageWidth(80);
        logger.info("============================");
        logger.info("server runs with args blow:");
        logger.info("port : {}", port);
        logger.info("savefolder : {}", storeDir);
        logger.info("database ip and port : {}", dbIpandPort);
        logger.info("database username : {}", username);
        logger.info("database password : {}", password);
        logger.info("============================");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://" + dbIpandPort + "/spidercluster");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource.setInitialSize(2);
        dataSource.setMaxIdle(5);
        dataSource.setMinIdle(2);

        Server server = new Server(port);
        server.setHandler(new RequestHandler(dataSource, storeDir));
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SpiderServer().domain(args);
    }
}
