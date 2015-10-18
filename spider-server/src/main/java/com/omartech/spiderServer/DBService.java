package com.omartech.spiderServer;

import com.omartech.spider.gen.Task;
import com.omartech.spider.gen.TaskStatus;
import com.omartech.spider.gen.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public static List<Task> findUnDoTasks(Connection connection, int offset, int limit) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE taskStatus = " + TaskStatus.UnDo.getValue() + " LIMIT ?, ?";
//        logger.info("find undo task : {}", sql);
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
                    boolean useProxy = resultSet.getBoolean("useProxy");

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
                    task.setUseProxy(useProxy);
                    task.setTaskStatus(TaskStatus.UnDo);
                    tasks.add(task);
                }
            }
        }
        return tasks;

    }

    public static List<Task> findDoingTasksWithIp(Connection connection, String ip, int offset, int limit) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE taskStatus = " + TaskStatus.Doing.getValue() + " and workerIp = ? LIMIT ?, ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, ip);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);
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
                    boolean useProxy = resultSet.getBoolean("useProxy");

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
                    task.setUseProxy(useProxy);
                    task.setTaskStatus(TaskStatus.Doing);
                    tasks.add(task);
                }
            }
        }
        return tasks;

    }

    public static void updateTaskStatus(Connection connection, long taskId, TaskStatus taskStatus, String workerIp) throws SQLException {
        String sql = "UPDATE tasks SET taskStatus = ?, workerIp = ? WHERE id = ?";
        if (taskStatus != null && taskId != 0) {
            try (PreparedStatement psmt = connection.prepareStatement(sql)) {
                psmt.setInt(1, taskStatus.getValue());
                psmt.setString(2, workerIp);
                psmt.setLong(3, taskId);
                psmt.executeUpdate();
            }
        } else {
            logger.error("something wrong about update taskStatus");
        }
    }


    public static void insertTasks(Connection connection, List<Task> tasks) throws SQLException {
        String sql = "INSERT INTO tasks(name, url, cookies, headers, parameters, refer, type, recursive, parseRegex, subTaskJson, taskStatus, useProxy) VALUES(?,?,?,?,?,?,?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            PreparedStatement psmt = connection.prepareStatement(sql);
            for (Task task : tasks) {
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
                psmt.setInt(11, task.getTaskStatus().getValue());
                psmt.setBoolean(12, task.isUseProxy());
                psmt.addBatch();
            }
            psmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            connection.setAutoCommit(true);
            for (Task task : tasks) {
                insertTask(connection, task);
            }
        }
        connection.setAutoCommit(true);
    }


    public static void insertTask(Connection connection, Task task) throws SQLException {
        String sql = "INSERT INTO tasks(name, url, cookies, headers, parameters, refer, type, recursive, parseRegex, subTaskJson, taskStatus, useProxy) VALUES(?,?,?,?,?,?,?, ?, ?, ?, ?, ?)";
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
            psmt.setInt(11, task.getTaskStatus().getValue());
            psmt.setBoolean(12, task.isUseProxy());
            psmt.executeUpdate();
        }
    }

    public static Map<String, Integer> fetchSchedule(Connection connection) throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT name, count(1) c FROM tasks GROUP BY name ORDER BY name";
        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = psmt.executeQuery();) {
                String name = resultSet.getString("name");
                int c = resultSet.getInt("c");
                map.put(name, c);
            }
        }
        return map;
    }

    public static List<StatusModel> fetchTasksInDB(Connection connection) throws SQLException {
        List<StatusModel> list = new ArrayList<>();
        String sql = "SELECT name, date(createdAt) createdAt, count(1) c FROM tasks GROUP BY name, date(createdAt) ORDER BY date(createdAt) DESC";
        try (PreparedStatement psmt = connection.prepareStatement(sql);
             ResultSet resultSet = psmt.executeQuery();) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String createdAt = resultSet.getString("createdAt");
                int c = resultSet.getInt("c");
                StatusModel statusModel = new StatusModel();
                statusModel.setTaskName(name);
                statusModel.setLasttime(createdAt);
                statusModel.setCount(c);
                list.add(statusModel);
            }
        }
        return list;
    }

}
