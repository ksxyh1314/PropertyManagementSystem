package com.property.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库工具类
 * 使用DBCP2连接池管理数据库连接
 */
public class DBUtil {
    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
    private static BasicDataSource dataSource;

    static {
        try {
            // 加载配置文件
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                throw new RuntimeException("无法找到db.properties配置文件");
            }
            props.load(is);

            // 初始化连接池
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(props.getProperty("db.driver"));
            dataSource.setUrl(props.getProperty("db.url"));
            dataSource.setUsername(props.getProperty("db.username"));
            dataSource.setPassword(props.getProperty("db.password"));

            // 连接池配置
            dataSource.setInitialSize(Integer.parseInt(props.getProperty("db.initialSize", "5")));
            dataSource.setMaxTotal(Integer.parseInt(props.getProperty("db.maxTotal", "20")));
            dataSource.setMaxIdle(Integer.parseInt(props.getProperty("db.maxIdle", "10")));
            dataSource.setMinIdle(Integer.parseInt(props.getProperty("db.minIdle", "5")));
            dataSource.setMaxWaitMillis(Long.parseLong(props.getProperty("db.maxWaitMillis", "10000")));

            // 连接验证
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setTestOnBorrow(true);
            dataSource.setTestWhileIdle(true);

            logger.info("数据库连接池初始化成功");
        } catch (IOException e) {
            logger.error("数据库配置文件加载失败", e);
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭资源
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            logger.error("关闭ResultSet失败", e);
        }

        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            logger.error("关闭Statement失败", e);
        }

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("关闭Connection失败", e);
        }
    }

    /**
     * 关闭资源（无ResultSet）
     */
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    /**
     * 开启事务
     */
    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }

    /**
     * 提交事务
     */
    public static void commit(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.commit();
            }
        } catch (SQLException e) {
            logger.error("事务提交失败", e);
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            logger.error("事务回滚失败", e);
        }
    }

    /**
     * 调用存储过程
     */
    public static CallableStatement prepareCall(Connection conn, String sql) throws SQLException {
        return conn.prepareCall(sql);
    }

    /**
     * 获取连接池状态信息
     */
    public static String getPoolStatus() {
        return String.format("连接池状态 - 活跃连接: %d, 空闲连接: %d",
                dataSource.getNumActive(), dataSource.getNumIdle());
    }
}
