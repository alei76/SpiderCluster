package com.omartech.spiderClient.task;

import java.io.File;

/**
 * Created by OmarTech on 15-3-15.
 */
public class TaskResults {
    private File file;

    public TaskResults(File file) {
        this.file = file;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
