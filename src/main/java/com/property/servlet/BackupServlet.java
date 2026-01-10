package com.property.servlet;

import com.property.util.DBUtil;
import com.property.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/backup")
public class BackupServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BackupServlet.class);

    // 备份文件存储路径
    private static final String BACKUP_PATH = "D:\\MySql\\backup\\";

    // 数据库名称
    private static final String DB_NAME = "PropertyManagementSystem";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String method = req.getParameter("method");

        if ("list".equals(method)) {
            list(req, resp);
        } else if ("download".equals(method)) {
            download(req, resp);
        } else {
            ResponseUtil.writeJson(resp, ResponseUtil.error("无效的请求方法"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String method = req.getParameter("method");

        if ("backup".equals(method)) {
            backup(req, resp);
        } else if ("restore".equals(method)) {
            restore(req, resp);
        } else if ("delete".equals(method)) {
            delete(req, resp);
        } else if ("setAutoBackup".equals(method)) {
            setAutoBackup(req, resp);
        } else {
            ResponseUtil.writeJson(resp, ResponseUtil.error("无效的请求方法"));
        }
    }

    /**
     * 获取备份列表
     */
    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "select * from backup_records where status = 1 order by backup_time desc";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<Map<String, Object>> backupList = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> backup = new HashMap<>();
                backup.put("backupId", rs.getInt("backup_id"));
                backup.put("fileName", rs.getString("file_name"));
                backup.put("filePath", rs.getString("file_path"));
                backup.put("fileSize", rs.getLong("file_size"));
                backup.put("backupTime", rs.getTimestamp("backup_time"));
                backup.put("backupUser", rs.getString("backup_user"));
                backup.put("backupType", rs.getString("backup_type"));
                backup.put("status", rs.getInt("status"));
                backup.put("remark", rs.getString("remark"));
                backupList.add(backup);
            }

            ResponseUtil.writeJson(resp, ResponseUtil.success("查询成功", backupList));

        } catch (Exception e) {
            logger.error("获取备份列表失败", e);
            ResponseUtil.writeJson(resp, ResponseUtil.error("获取备份列表失败：" + e.getMessage()));
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 备份数据库（调用存储过程）
     */
    private void backup(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            // 获取当前用户
            HttpSession session = req.getSession();
            Object userObj = session.getAttribute("user");
            String username = "admin"; // 默认值

            // 如果 session 中有用户信息，可以获取
            // if (userObj != null) {
            //     username = ((User) userObj).getUsername();
            // }

            // 确保备份目录存在并可写
            File backupDir = new File(BACKUP_PATH);
            if (!backupDir.exists()) {
                boolean created = backupDir.mkdirs();
                if (!created) {
                    logger.error("创建备份目录失败: {}", BACKUP_PATH);
                    ResponseUtil.writeJson(resp, ResponseUtil.error("创建备份目录失败"));
                    return;
                }
                logger.info("创建备份目录: {}", BACKUP_PATH);
            }

            if (!backupDir.canWrite()) {
                logger.error("备份目录不可写: {}", BACKUP_PATH);
                ResponseUtil.writeJson(resp, ResponseUtil.error("备份目录没有写入权限"));
                return;
            }

            conn = DBUtil.getConnection();

            // 调用存储过程
            String sql = "{call sp_backup_database(?, ?, ?, ?, ?, ?)}";
            cstmt = conn.prepareCall(sql);

            // 设置输入参数
            cstmt.setString(1, BACKUP_PATH);
            cstmt.setString(2, username);
            cstmt.setString(3, "manual");
            cstmt.setString(4, "手动备份");

            // 注册输出参数
            cstmt.registerOutParameter(5, Types.INTEGER);
            cstmt.registerOutParameter(6, Types.NVARCHAR);

            // 执行存储过程
            cstmt.execute();

            // 获取输出参数
            int result = cstmt.getInt(5);
            String message = cstmt.getString(6);

            logger.info("备份结果: result={}, message={}", result, message);

            if (result == 1) {
                ResponseUtil.writeJson(resp, ResponseUtil.success(message));
            } else {
                ResponseUtil.writeJson(resp, ResponseUtil.error(message));
            }

        } catch (Exception e) {
            logger.error("备份数据库失败", e);
            ResponseUtil.writeJson(resp, ResponseUtil.error("备份失败：" + e.getMessage()));
        } finally {
            if (cstmt != null) {
                try { cstmt.close(); } catch (SQLException e) { }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { }
            }
        }
    }
    /**
     * 恢复数据库（调用存储过程）
     */
    private void restore(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            String backupFile = req.getParameter("backupFile");

            // 安全检查
            if (backupFile == null || backupFile.contains("..") ||
                    backupFile.contains("/") || backupFile.contains("\\")) {
                ResponseUtil.writeJson(resp, ResponseUtil.error("非法的文件名"));
                return;
            }

            String fullPath = BACKUP_PATH + backupFile;

            // 检查文件是否存在
            File file = new File(fullPath);
            if (!file.exists()) {
                logger.warn("备份文件不存在: {}", fullPath);
                ResponseUtil.writeJson(resp, ResponseUtil.error("备份文件不存在"));
                return;
            }

            logger.info("准备恢复数据库，连接到 master 数据库...");

            // ⚠️ 重要：连接到 master 数据库，而不是 PropertyManagementSystem
            String masterUrl = "jdbc:sqlserver://localhost;instanceName=SQL2022_DEV;databaseName=master;encrypt=false;trustServerCertificate=true";
            String username = "sa";  // 使用 sa 账户
            String password = "123456";  // 你的 sa 密码

            conn = DriverManager.getConnection(masterUrl, username, password);
            logger.info("✅ 已连接到 master 数据库");

            logger.info("正在执行数据库恢复...");

            // 调用存储过程（在 master 数据库中）
            String sql = "{call sp_restore_database(?, ?, ?)}";
            cstmt = conn.prepareCall(sql);

            // 设置输入参数
            cstmt.setString(1, fullPath);

            // 注册输出参数
            cstmt.registerOutParameter(2, Types.INTEGER);
            cstmt.registerOutParameter(3, Types.NVARCHAR);

            // 执行存储过程
            cstmt.execute();

            // 获取输出参数
            int result = cstmt.getInt(2);
            String message = cstmt.getString(3);

            logger.info("恢复结果: result={}, message={}", result, message);

            if (result == 1) {
                ResponseUtil.writeJson(resp, ResponseUtil.success(message));
            } else {
                ResponseUtil.writeJson(resp, ResponseUtil.error(message));
            }

        } catch (Exception e) {
            logger.error("恢复数据库失败", e);
            ResponseUtil.writeJson(resp, ResponseUtil.error("恢复失败：" + e.getMessage()));
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                    logger.info("CallableStatement 已关闭");
                } catch (SQLException e) {
                    logger.error("关闭 CallableStatement 失败", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                    logger.info("Connection 已关闭");
                } catch (SQLException e) {
                    logger.error("关闭 Connection 失败", e);
                }
            }
        }
    }


    /**
     * 删除备份（调用存储过程）
     */
    private void delete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Connection conn = null;
        CallableStatement cstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            int backupId = Integer.parseInt(req.getParameter("backupId"));

            conn = DBUtil.getConnection();

            // 先获取文件信息
            String selectSql = "select file_name, file_path from backup_records where backup_id = ?";
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setInt(1, backupId);
            rs = pstmt.executeQuery();

            String fileName = null;
            String filePath = null;

            if (rs.next()) {
                fileName = rs.getString("file_name");
                filePath = rs.getString("file_path");
            }

            // 调用存储过程
            String sql = "{call sp_delete_backup(?, ?, ?)}";
            cstmt = conn.prepareCall(sql);

            // 设置输入参数
            cstmt.setInt(1, backupId);

            // 注册输出参数
            cstmt.registerOutParameter(2, Types.INTEGER);
            cstmt.registerOutParameter(3, Types.NVARCHAR);

            // 执行存储过程
            cstmt.execute();

            // 获取输出参数
            int result = cstmt.getInt(2);
            String message = cstmt.getString(3);

            // 删除物理文件
            if (result == 1 && fileName != null && filePath != null) {
                File file = new File(filePath + fileName);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        logger.info("删除物理文件成功: {}", fileName);
                    } else {
                        logger.warn("删除物理文件失败: {}", fileName);
                    }
                } else {
                    logger.warn("物理文件不存在: {}", fileName);
                }
            }

            logger.info("删除结果: result={}, message={}", result, message);

            if (result == 1) {
                ResponseUtil.writeJson(resp, ResponseUtil.success(message));
            } else {
                ResponseUtil.writeJson(resp, ResponseUtil.error(message));
            }

        } catch (Exception e) {
            logger.error("删除备份失败", e);
            ResponseUtil.writeJson(resp, ResponseUtil.error("删除失败：" + e.getMessage()));
        } finally {
            DBUtil.close(rs, pstmt, conn);
            if (cstmt != null) {
                try { cstmt.close(); } catch (SQLException e) { }
            }
        }
    }

    /**
     * 下载备份文件
     */
    private void download(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        FileInputStream fis = null;
        OutputStream os = null;

        try {
            String fileName = req.getParameter("fileName");

            // 安全检查：防止路径遍历攻击
            if (fileName == null || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                ResponseUtil.writeJson(resp, ResponseUtil.error("非法的文件名"));
                return;
            }

            String fullPath = BACKUP_PATH + fileName;

            File file = new File(fullPath);
            if (!file.exists()) {
                logger.warn("下载文件不存在: {}", fullPath);
                ResponseUtil.writeJson(resp, ResponseUtil.error("文件不存在"));
                return;
            }

            // 设置响应头
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition",
                    "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            resp.setContentLengthLong(file.length());

            // 输出文件
            fis = new FileInputStream(file);
            os = resp.getOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();

            logger.info("下载备份文件：{}", fileName);

        } catch (Exception e) {
            logger.error("下载备份文件失败", e);
            ResponseUtil.writeJson(resp, ResponseUtil.error("下载失败：" + e.getMessage()));
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException e) { }
            }
            if (os != null) {
                try { os.close(); } catch (IOException e) { }
            }
        }
    }

    /**
     * 设置自动备份
     */
    private void setAutoBackup(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            String frequency = req.getParameter("frequency");
            String time = req.getParameter("time");

            logger.info("设置自动备份：频率={}, 时间={}", frequency, time);

            ResponseUtil.writeJson(resp, ResponseUtil.success("设置成功（演示功能）"));

        } catch (Exception e) {
            logger.error("设置自动备份失败", e);
            ResponseUtil.writeJson(resp, ResponseUtil.error("设置失败：" + e.getMessage()));
        }
    }
}
