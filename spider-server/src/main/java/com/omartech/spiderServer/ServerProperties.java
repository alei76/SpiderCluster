package com.omartech.spiderServer;

import javax.sql.DataSource;

/**
 * Created by omar on 15/10/26.
 */
public class ServerProperties {
    private DataSource dataSource;//数据源
    private String dataStorePath;//数据存放路径
    private int requestBatchSize;//每次返回任务数

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataStorePath() {
        return dataStorePath;
    }

    public void setDataStorePath(String dataStorePath) {
        this.dataStorePath = dataStorePath;
    }

    public int getRequestBatchSize() {
        return requestBatchSize;
    }

    public void setRequestBatchSize(int requestBatchSize) {
        this.requestBatchSize = requestBatchSize;
    }
}
