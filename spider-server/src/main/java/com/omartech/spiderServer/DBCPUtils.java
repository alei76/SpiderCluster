package com.omartech.spiderServer;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.DataSourceConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by OmarTech on 15-3-27.
 */
public class DBCPUtils {
    private static DBCPUtils dbcputils = null;
    private BasicDataSource bds = null;
    private DataSourceConnectionFactory dscf = null;

    static String driverclass = "com.mysql.jdbc.Driver";

    private DBCPUtils(String ipAndPort, String username, String password) {
        if (bds == null)
            bds = new BasicDataSource();

        bds.setUrl("jdbc:mysql://" + ipAndPort);
        bds.setUsername(username);
        bds.setPassword(password);
        bds.setDriverClassName(driverclass);

        bds.setInitialSize(2);
        bds.setMaxIdle(5);
        bds.setMinIdle(2);

        dscf = new DataSourceConnectionFactory(bds);
    }

    public synchronized static DBCPUtils getInstance(String ipAndPort, String username, String password) {
        if (dbcputils == null)
            dbcputils = new DBCPUtils(ipAndPort, username, password);
        return dbcputils;
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = (Connection) dscf.createConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            con = DBCPUtils.getInstance("127.0.0.1:3306", "root", "").getConnection();
            con.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时为:" + (end - begin) + "ms");
    }
}
