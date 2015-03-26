package com.omartech.spiderServer;

import cn.omartech.spider.gen.Task;
import cn.omartech.spider.gen.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OmarTech on 15-3-14.
 */
public class DBService {
    static Logger logger = LoggerFactory.getLogger(DBService.class);

    public static void delete(Connection conection, List<Long> ids) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        for (long id : ids) {
            try (PreparedStatement psmt = conection.prepareStatement(sql)) {
                psmt.setLong(1, id);
                psmt.executeUpdate();
            }
        }
    }


    public static List<Task> findTasks(Connection connection, int offset, int limit) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks LIMIT ?, ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setInt(1, offset);
            preparedStatement.setInt(2, limit);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                logger.debug(resultSet.getStatement().toString());
                while (resultSet.next()) {
                    String taskName = resultSet.getString("name");
                    String url = resultSet.getString("url");
                    String headers = resultSet.getString("headers");
                    String cookies = resultSet.getString("cookies");
                    String type = resultSet.getString("type");
                    long id = resultSet.getLong("id");
                    String parameters = resultSet.getString("parameters");
                    boolean recursive = resultSet.getBoolean("recursive");
                    String parseRegex = resultSet.getString("parseRegex");
                    String subTaskJson = resultSet.getString("subTaskJson");

                    Task task = new Task();
                    task.setName(taskName);
                    task.setId(id);
                    task.setUrl(url);
                    task.setCookie(cookies);
                    task.setHeaderJson(headers);
                    task.setType(TaskType.valueOf(type));
                    task.setParameterJson(parameters);
                    task.setRecursive(recursive);
                    task.setParseRegex(parseRegex);
                    task.setSubTaskJson(subTaskJson);
                    tasks.add(task);
                }
            }
        }
        return tasks;

    }

    public static void insertTasks(Connection connection, List<Task> tasks) throws SQLException {
        String sql = "INSERT INTO tasks(name, url, cookies, headers, parameters, refer, type, recursive, parseRegex, subTaskJson) VALUES(?,?,?,?,?,?,?, ?, ?, ?)";
        for (Task task : tasks) {
            try (PreparedStatement psmt = connection.prepareStatement(sql)) {
                psmt.setString(1, task.getName());
                psmt.setString(2, task.getUrl());
                psmt.setString(3, task.getCookie());
                psmt.setString(4, task.getHeaderJson());
                psmt.setString(5, task.getParameterJson());
                psmt.setString(6, task.refer);
                psmt.setString(7, task.getType().toString());
                psmt.setBoolean(8, task.isRecursive());
                psmt.setString(9, task.getParseRegex());
                psmt.setString(10, task.getSubTaskJson());
                psmt.executeUpdate();
            }
        }
    }
}
