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
    @Option(name = "-pw", usage = "-pw set the database password")
    private String password = "";

    @Option(name = "-rbs", usage = "-rbs set the request batch size")
    int requestBatchSize = 100;

    @Option(name = "-help", usage = "show the help")
    protected boolean help = false;

    void domain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);

            if (this.help) {
                System.err.println("java {{cp}} " + this.getClass().getCanonicalName() + " [options...] arguments...");
                parser.printUsage(System.err);
                System.exit(1);
            }

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
        logger.info("request batch size : {}", requestBatchSize);
        logger.info("============================");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://" + dbIpandPort + "/spidercluster");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource.setInitialSize(2);
        dataSource.setMaxIdle(5);
        dataSource.setMinIdle(2);


        ServerProperties serverProperties = new ServerProperties();
        serverProperties.setDataSource(dataSource);
        serverProperties.setDataStorePath(storeDir);
        serverProperties.setRequestBatchSize(requestBatchSize);

        Server server = new Server(port);
        server.setHandler(new RequestHandler(serverProperties));
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
