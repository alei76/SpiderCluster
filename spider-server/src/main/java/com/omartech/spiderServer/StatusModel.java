package com.omartech.spiderServer;

import com.omartech.spider.gen.TaskStatus;

/**
 * Created by OmarTech on 15-3-15.
 */
public class StatusModel {

    private String ip;
    private String taskName;
    private String lasttime;
    private TaskStatus taskStatus;
    private int count;

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "StatusModel{" +
                "ip='" + ip + '\'' +
                ", taskName='" + taskName + '\'' +
                ", lasttime='" + lasttime + '\'' +
                ", taskStatus=" + taskStatus +
                ", count=" + count +
                '}';
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
