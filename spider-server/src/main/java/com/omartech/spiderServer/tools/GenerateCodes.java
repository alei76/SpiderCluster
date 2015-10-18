package com.omartech.spiderServer.tools;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by OmarTech on 15-10-17.
 */
public class GenerateCodes {
    public static void main(String[] args) {
        File file = new File("spider-server/pages/form.html");
        try {
            List<String> list = FileUtils.readLines(file);
            for (String tmp : list) {
                tmp = tmp.replace("\"", "\\\"");
                System.out.println("writer.write(\"" + tmp + "\");");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
