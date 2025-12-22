package com.property.util;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库工具类 - SQL Server Windows 身份验证版本
 */
public class DBUtil {

    private static BasicDataSource dataSource;

    // SQL Server 数据库配置（Windows 身份验证）
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    // Windows 身份验证连接字符串
    private static final String URL = "jdbc:sqlserver://localhost:1433;" +
            "databaseName=PropertyManagement;" +
            "integratedSecurity=true;" +
            "encrypt=false;" +
            "trustServerCertificate=true";

    // Windows 身份验证不需要用户名和密码
    // private static final String USERNAME = "";
    // private static final String PASSWORD = "";

    // 连接池配置
    private static final int INITIAL_SIZE = 5;
    private static final int MAX_TOTAL = 20;
    private static final int MAX_IDLE = 10;
    private static final int MIN_IDLE = 5;
    private static final long MAX_WAIT_MILLIS = 10000;

    // 静态代码块，初始化连接池
    static {
        try {
            Class.forName(DRIVER);
            System.out.println("✅ SQL Server 驱动加载成功");

            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(DRIVER);
            dataSource.setUrl(URL);

            // Windows 身份验证不需要设置用户名和密码
            // dataSource.setUsername(USERNAME);
            // dataSource.setPassword(PASSWORD);

            dataSource.setInitialSize(INITIAL_SIZE);
            dataSource.setMaxTotal(MAX_TOTAL);
            dataSource.setMaxIdle(MAX_IDLE);
            dataSource.setMinIdle(MIN_IDLE);
            dataSource.setMaxWaitMillis(MAX_WAIT_MILLIS);

            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery("SELECT 1");

            System.out.println("✅ 数据库连接池初始化成功（Windows 身份验证）");
            System.out.println("📍 数据库地址：" + URL);

        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQL Server 驱动加载失败");
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            System.out.println("✅ 获取数据库连接成功（Windows 身份验证）");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ 获取数据库连接失败：" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 开启事务
     */
    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(false);
            System.out.println("✅ 事务已开启");
        }
    }

    /**
     * 提交事务
     */
    public static void commit(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
            System.out.println("✅ 事务已提交");
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                System.out.println("⚠️ 事务已回滚");
            } catch (SQLException e) {
                System.err.println("❌ 事务回滚失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭资源
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                // 如果连接还在事务中，先回滚
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
                conn.close();
                System.out.println("✅ 数据库连接已关闭");
            }
        } catch (SQLException e) {
            System.err.println("❌ 关闭资源失败");
            e.printStackTrace();
        }
    }

    /**
     * 关闭资源（无ResultSet）
     */
    public static void close(Connection conn, PreparedStatement ps) {
        close(conn, ps, null);
    }

    /**
     * 关闭连接（单独）
     */
    public static void close(Connection conn) {
        close(conn, null, null);
    }

    /**
     * 关闭连接池
     */
    public static void closeDataSource() {
        try {
            if (dataSource != null) {
                dataSource.close();
                System.out.println("✅ 数据库连接池已关闭");
            }
        } catch (SQLException e) {
            System.err.println("❌ 关闭连接池失败");
            e.printStackTrace();
        }
    }

    /**
     * 测试数据库连接
     */
    public static void main(String[] args) {
        Connection conn = null;
        try {
            System.out.println("========== 测试 SQL Server 连接（Windows 身份验证）==========");
            conn = getConnection();
            System.out.println("✅ 数据库连接测试成功！");
            System.out.println("数据库产品：" + conn.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本：" + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("当前用户：" + conn.getMetaData().getUserName());

            // 测试事务
            System.out.println("\n========== 测试事务管理 ==========");
            beginTransaction(conn);
            System.out.println("执行一些数据库操作...");
            commit(conn);

        } catch (SQLException e) {
            System.err.println("❌ 数据库连接测试失败！");
            e.printStackTrace();
            rollback(conn);
        } finally {
            close(conn);
        }
    }
}
