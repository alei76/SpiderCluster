package com.omartech.spiderServer.handler;

import cn.omartech.spider.gen.HtmlObject;
import cn.omartech.spider.gen.Task;
import cn.omartech.spider.gen.TaskResponse;
import com.google.gson.Gson;
import com.omartech.spiderServer.DBService;
import com.omartech.spiderServer.StatusModel;
import com.techwolf.omartech_utils.DBUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by OmarTech on 15-3-14.
 */
public class RequestHandler extends AbstractHandler {
    static Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    static Map<String, StatusModel> statusMap = new HashMap<>();

    @Override
    public void handle(String path, Request request,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);

        String ipAddress = getIpAddress(httpServletRequest);
        logger.info("{} request {}", ipAddress, path);

        try {
            if (!StringUtils.isEmpty(path)) {
                switch (path) {
                    case "/fetchtasks":
                        fetchTasks(httpServletRequest, response);
                        break;
                    case "/sendresults":
                        try {
                            int count = receiveResults(httpServletRequest, response);
                            StatusModel model = statusMap.get(ipAddress);
                            if (model == null) {
                                model = new StatusModel();
                                model.setCount(count);
                                model.setIp(ipAddress);
                                model.setLasttime(DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss"));
                            } else {
                                int count1 = model.getCount();
                                model.setCount(count1 + count);
                            }
                            statusMap.put(ipAddress, model);
                        } catch (FileUploadException e) {
                            e.printStackTrace();
                            response.getWriter().write(e.getMessage());
                        }
                        break;
                    default:
                        showStatus(response);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.getWriter().println("<p>©2015 OmarTech</p>");
    }

    private int receiveResults(HttpServletRequest request, HttpServletResponse response) throws FileUploadException {
        int count = 0;
        File tmpFolder = new File("/tmp/spider-server-temp");
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }
        DiskFileItemFactory factory = new DiskFileItemFactory(1024 * 1024 * 1024, tmpFolder);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(request);
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();
                if (item.isFormField()) {
                    processFormField(item);
                } else {
                    count = processUploadedFile(item);
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private int processUploadedFile(FileItem item) throws Exception {
        String fileName = item.getName();
        String contentType = item.getContentType();
        logger.info("fileName : {}, contentType : {}", fileName, contentType);

        File folder = new File("spider-server-store");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String newFileName = System.currentTimeMillis() + "";
        File uploadedFile = new File("spider-server-store/" + newFileName);
        item.write(uploadedFile);

        List<String> lines = FileUtils.readLines(uploadedFile);
        List<Long> ids = new ArrayList<>();
        for (String line : lines) {
            HtmlObject object = gson.fromJson(line, HtmlObject.class);
            long taskId = object.getTaskId();
            ids.add(taskId);
        }
        try (Connection connection = fetchConnection("spidercluster");) {
            DBService.delete(connection, ids);
        }
        return ids.size();
    }


    private void processFormField(FileItem item) {
        logger.error("no form field is accepted");
    }

    void fetchTasks(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        //find tasks in the db limit 100
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = fetchConnection("spidercluster");) {
            tasks = DBService.findTasks(connection, 0, 10);
        }
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTasks(tasks);

        String json = gson.toJson(taskResponse);
//        logger.info("json : {}", json);
        response.setContentType("application/Json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
        writer.close();

    }


    static Gson gson = new Gson();

    void showStatus(HttpServletResponse response) throws IOException {
        List<StatusModel> models = new ArrayList<>();
        for (Map.Entry<String, StatusModel> entry : statusMap.entrySet()) {
            StatusModel status = entry.getValue();
            models.add(status);
        }
        Collections.sort(models, new Comparator<StatusModel>() {
            @Override
            public int compare(StatusModel o1, StatusModel o2) {
                String lasttime = o1.getLasttime();
                String o2Lasttime = o2.getLasttime();
                return lasttime.compareTo(o2Lasttime);
            }
        });
        PrintWriter writer = response.getWriter();
        writer.write("<h3>任务记录</h3>");
        writer.write("<table border='1'>");
        writer.write("<tr>");
        writer.write("<td>");
        writer.write("客户端IP地址");
        writer.write("</td>");
        writer.write("<td>");
        writer.write("已完成任务数");
        writer.write("</td>");
        writer.write("<td>");
        writer.write("最后更新时间");
        writer.write("</td>");
        writer.write("</tr>");
        for (StatusModel model : models) {
            logger.info(model.toString());
            writer.write("<tr>");
            writer.write("<td>");
            writer.write(model.getIp());
            writer.write("</td>");
            writer.write("<td>");
            writer.write(model.getCount() + "");
            writer.write("</td>");
            writer.write("<td>");
            writer.write(model.getLasttime());
            writer.write("</td>");
            writer.write("</tr>");
        }
        writer.write("</table>");
        writer.flush();
    }

    public final static String getIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isDebugEnabled()) {
            logger.debug("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (logger.isDebugEnabled()) {
                    logger.debug("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (logger.isDebugEnabled()) {
                    logger.debug("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (logger.isDebugEnabled()) {
                    logger.debug("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (logger.isDebugEnabled()) {
                    logger.debug("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (logger.isDebugEnabled()) {
                    logger.debug("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    private static Connection fetchConnection(String dbname) {
        Connection connection = null;
        boolean flag = false;
        do {
            connection = con.get();
            flag = DBUtils.verifyConnection(connection, "select id from tasks limit 1");
            if (!flag) {
                con.remove();
            }
        } while (!flag);
        return connection;
    }

    static ThreadLocal<Connection> con = new InheritableThreadLocal<Connection>() {
        @Override
        protected Connection initialValue() {
            Connection conn = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/spidercluster", "root", "");
                logger.info("new connection to spidercluster");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    };
}
