package com.omartech.spiderServer;

import com.omartech.spiderServer.handler.RequestHandler;
import org.eclipse.jetty.server.Server;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by OmarTech on 15-3-14.
 */
public class SpiderServer {

    static Logger logger = LoggerFactory.getLogger(SpiderServer.class);
    @Option(name = "-d", usage = "-d set the default dir for store")
    private String dir = "/tmp";

    @Option(name = "-p", usage = "-p set the port")
    private int port = 7154;

    void domain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);
        logger.info("============================");
        logger.info("server runs with args blow:");
        logger.info("port : {}", port);
        logger.info("savefolder : {}", dir);
        logger.info("============================");

        Server server = new Server(port);
        server.setHandler(new RequestHandler());
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
