package com.omartech.spiderServer;

/**
 * Created by OmarTech on 15-3-15.
 */
public class StatusModel {

    private String ip;
    private String lasttime;
    private int count;

    @Override
    public String toString() {
        return "StatusModel{" +
                "ip='" + ip + '\'' +
                ", lasttime='" + lasttime + '\'' +
                ", count=" + count +
                '}';
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
