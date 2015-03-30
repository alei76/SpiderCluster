package com.omartech.spiderHandler.seed;

import cn.omartech.spider.gen.Task;

import java.util.concurrent.TimeUnit;

/**
 * Created by OmarTech on 15-3-27.
 */
public class SinaSeed extends ASeedWorker {


    void run() {

        Task chanjing = chanjing();
        addTask(chanjing, 1, TimeUnit.HOURS);
    }


    Task chanjing() {
        Task task = new Task();
        return task;
    }


    public static void main(String[] args) {

        SinaSeed sinaSeed = new SinaSeed();


        sinaSeed.domain(args);

    }


}
