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
 * 使用 DBCP2 连接池管理数据库连接
 */
public class DBUtil {
    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
    private static BasicDataSource dataSource;

    // 静态初始化块，加载配置并初始化连接池
    static {
        try {
            // 加载配置文件
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");

            if (is == null) {
                throw new RuntimeException("无法找到 db.properties 配置文件");
            }

            props.load(is);
            is.close();

            // 初始化数据源
            dataSource = new BasicDataSource();

            // 基本连接信息
            dataSource.setDriverClassName(props.getProperty("jdbc.driver"));
            dataSource.setUrl(props.getProperty("jdbc.url"));
            dataSource.setUsername(props.getProperty("jdbc.username"));
            dataSource.setPassword(props.getProperty("jdbc.password"));

            // 连接池配置
            dataSource.setInitialSize(Integer.parseInt(props.getProperty("druid.initialSize", "5")));
            dataSource.setMaxTotal(Integer.parseInt(props.getProperty("druid.maxActive", "20")));
            dataSource.setMaxIdle(Integer.parseInt(props.getProperty("druid.maxIdle", "10")));
            dataSource.setMinIdle(Integer.parseInt(props.getProperty("druid.minIdle", "5")));
            dataSource.setMaxWaitMillis(Long.parseLong(props.getProperty("druid.maxWait", "10000")));

            // 连接验证配置
            String validationQuery = props.getProperty("druid.validationQuery", "SELECT 1");
            dataSource.setValidationQuery(validationQuery);
            dataSource.setTestOnBorrow(Boolean.parseBoolean(props.getProperty("druid.testOnBorrow", "false")));
            dataSource.setTestWhileIdle(Boolean.parseBoolean(props.getProperty("druid.testWhileIdle", "true")));
            dataSource.setTestOnReturn(Boolean.parseBoolean(props.getProperty("druid.testOnReturn", "false")));

            // 空闲连接回收配置
            dataSource.setTimeBetweenEvictionRunsMillis(
                    Long.parseLong(props.getProperty("druid.timeBetweenEvictionRunsMillis", "60000"))
            );
            dataSource.setMinEvictableIdleTimeMillis(
                    Long.parseLong(props.getProperty("druid.minEvictableIdleTimeMillis", "300000"))
            );

            // 预编译缓存
            boolean poolPreparedStatements = Boolean.parseBoolean(
                    props.getProperty("druid.poolPreparedStatements", "true")
            );
            dataSource.setPoolPreparedStatements(poolPreparedStatements);

            if (poolPreparedStatements) {
                dataSource.setMaxOpenPreparedStatements(
                        Integer.parseInt(props.getProperty("druid.maxPoolPreparedStatementPerConnectionSize", "20"))
                );
            }

            logger.info("✅ 数据库连接池初始化成功");
            logger.info("📍 数据库地址: {}", dataSource.getUrl());
            logger.info("📊 连接池配置: 初始={}, 最小={}, 最大={}",
                    dataSource.getInitialSize(), dataSource.getMinIdle(), dataSource.getMaxTotal());

        } catch (IOException e) {
            logger.error("❌ 数据库配置文件加载失败", e);
            throw new RuntimeException("数据库初始化失败", e);
        } catch (Exception e) {
            logger.error("❌ 数据库连接池初始化失败", e);
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            logger.debug("✅ 获取数据库连接成功 [活跃: {}, 空闲: {}]",
                    dataSource.getNumActive(), dataSource.getNumIdle());
            return conn;
        } catch (SQLException e) {
            logger.error("❌ 获取数据库连接失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 开启事务
     */
    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.setAutoCommit(false);
            logger.debug("✅ 事务已开启");
        }
    }

    /**
     * 提交事务
     */
    public static void commit(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.commit();
            logger.debug("✅ 事务已提交");
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.rollback();
                    logger.warn("⚠️ 事务已回滚");
                }
            } catch (SQLException e) {
                logger.error("❌ 事务回滚失败", e);
            }
        }
    }

    /**
     * 关闭数据库资源（标准顺序：ResultSet, Statement, Connection）
     * ✅ 推荐使用这个方法
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        closeResources(rs, stmt, conn);
    }

    /**
     * 关闭数据库资源（旧版顺序：Connection, Statement, ResultSet）
     * 保留此方法以兼容旧代码
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        closeResources(rs, stmt, conn);
    }

    /**
     * 实际关闭资源的内部方法
     */
    private static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        // 关闭 ResultSet
        if (rs != null) {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                    logger.debug("✅ ResultSet 已关闭");
                }
            } catch (SQLException e) {
                logger.error("❌ 关闭 ResultSet 失败", e);
            }
        }

        // 关闭 Statement
        if (stmt != null) {
            try {
                if (!stmt.isClosed()) {
                    stmt.close();
                    logger.debug("✅ Statement 已关闭");
                }
            } catch (SQLException e) {
                logger.error("❌ 关闭 Statement 失败", e);
            }
        }

        // 关闭 Connection
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    // 如果连接还在事务中，先回滚
                    if (!conn.getAutoCommit()) {
                        conn.rollback();
                        conn.setAutoCommit(true);
                        logger.debug("⚠️ 连接在事务中，已自动回滚");
                    }
                    conn.close();
                    logger.debug("✅ Connection 已关闭 [活跃: {}, 空闲: {}]",
                            dataSource.getNumActive(), dataSource.getNumIdle());
                }
            } catch (SQLException e) {
                logger.error("❌ 关闭 Connection 失败", e);
            }
        }
    }

    /**
     * 关闭数据库资源（无 ResultSet）
     */
    public static void close(Statement stmt, Connection conn) {
        closeResources(null, stmt, conn);
    }

    /**
     * 关闭数据库连接（仅 Connection）
     */
    public static void close(Connection conn) {
        closeResources(null, null, conn);
    }

    /**
     * 调用存储过程
     */
    public static CallableStatement prepareCall(Connection conn, String sql) throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("数据库连接无效");
        }
        return conn.prepareCall(sql);
    }

    /**
     * 关闭连接池（应用关闭时调用）
     */
    public static void closeDataSource() {
        if (dataSource != null) {
            try {
                dataSource.close();
                logger.info("✅ 数据库连接池已关闭");
            } catch (SQLException e) {
                logger.error("❌ 关闭连接池失败", e);
            }
        }
    }

    /**
     * 获取连接池状态信息
     */
    public static String getPoolStatus() {
        if (dataSource == null) {
            return "连接池未初始化";
        }
        return String.format("连接池状态 - 活跃连接: %d, 空闲连接: %d, 最大连接: %d",
                dataSource.getNumActive(),
                dataSource.getNumIdle(),
                dataSource.getMaxTotal());
    }

    /**
     * 获取连接池详细信息
     */
    public static String getPoolInfo() {
        if (dataSource == null) {
            return "连接池未初始化";
        }

        StringBuilder info = new StringBuilder();
        info.append("\n========== 数据库连接池信息 ==========\n");
        info.append("数据库地址: ").append(dataSource.getUrl()).append("\n");
        info.append("驱动类名: ").append(dataSource.getDriverClassName()).append("\n");
        info.append("用户名: ").append(dataSource.getUsername()).append("\n");
        info.append("活跃连接数: ").append(dataSource.getNumActive()).append("\n");
        info.append("空闲连接数: ").append(dataSource.getNumIdle()).append("\n");
        info.append("初始连接数: ").append(dataSource.getInitialSize()).append("\n");
        info.append("最小空闲数: ").append(dataSource.getMinIdle()).append("\n");
        info.append("最大连接数: ").append(dataSource.getMaxTotal()).append("\n");
        info.append("最大等待时间: ").append(dataSource.getMaxWaitMillis()).append(" ms\n");
        info.append("=======================================");

        return info.toString();
    }

    /**
     * 测试数据库连接
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            logger.info("========== 测试数据库连接 ==========");
            conn = getConnection();

            DatabaseMetaData metaData = conn.getMetaData();
            logger.info("✅ 数据库连接测试成功！");
            logger.info("数据库产品: {}", metaData.getDatabaseProductName());
            logger.info("数据库版本: {}", metaData.getDatabaseProductVersion());
            logger.info("JDBC 驱动: {}", metaData.getDriverName());
            logger.info("JDBC 版本: {}", metaData.getDriverVersion());
            logger.info("当前用户: {}", metaData.getUserName());
            logger.info("====================================");

            return true;

        } catch (SQLException e) {
            logger.error("❌ 数据库连接测试失败！", e);
            return false;
        } finally {
            close(conn);
        }
    }

    /**
     * 执行测试
     */
    public static void main(String[] args) {
        try {
            // 测试连接
            boolean success = testConnection();

            if (success) {
                // 打印连接池信息
                System.out.println(getPoolInfo());

                // 测试事务
                System.out.println("\n========== 测试事务管理 ==========");
                Connection conn = null;
                try {
                    conn = getConnection();
                    beginTransaction(conn);
                    System.out.println("✅ 事务已开启");

                    // 这里可以执行一些数据库操作
                    System.out.println("执行数据库操作...");

                    commit(conn);
                    System.out.println("✅ 事务已提交");

                } catch (Exception e) {
                    System.err.println("❌ 事务执行失败");
                    rollback(conn);
                } finally {
                    close(conn);
                }
                System.out.println("===================================");
            }

        } finally {
            // 关闭连接池
            closeDataSource();
        }
    }
}
