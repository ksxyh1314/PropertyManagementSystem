package com.property.dao;

import com.property.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO 基类
 * 封装常用的数据库操作方法
 */
public abstract class BaseDao {
    protected static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

    /**
     * 查询单个对象
     */
    protected <E> E queryOne(String sql, RowMapper<E> rowMapper, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rowMapper.mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("查询单个对象失败: " + sql, e);
            throw new RuntimeException("查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);  // ✅ 修改：正确的参数顺序
        }
    }

    /**
     * 查询列表
     */
    protected <E> List<E> query(String sql, RowMapper<E> rowMapper, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<E> list = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                E entity = rowMapper.mapRow(rs);
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            logger.error("查询列表失败: " + sql, e);
            throw new RuntimeException("查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);  // ✅ 修改：正确的参数顺序
        }
    }

    /**
     * 更新操作 (INSERT, UPDATE, DELETE)
     */
    protected int update(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            int rows = pstmt.executeUpdate();
            return rows;
        } catch (SQLException e) {
            logger.error("更新失败: " + sql, e);
            throw new RuntimeException("更新失败", e);
        } finally {
            DBUtil.close(pstmt, conn);  // ✅ 修改：正确的参数顺序
        }
    }

    /**
     * 插入并返回自增主键
     */
    protected Integer insertAndGetKey(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParameters(pstmt, params);
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        } catch (SQLException e) {
            logger.error("插入并获取主键失败: " + sql, e);
            throw new RuntimeException("插入失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);  // ✅ 修改：正确的参数顺序
        }
    }

    /**
     * 查询单个值 (COUNT, SUM, MAX 等)
     */
    protected Long queryForLong(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                long value = rs.getLong(1);
                return value;
            }
            return 0L;
        } catch (SQLException e) {
            logger.error("查询单个值失败: " + sql, e);
            throw new RuntimeException("查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);  // ✅ 修改：正确的参数顺序
        }
    }

    /**
     * 调用存储过程
     */
    protected void callProcedure(String sql, ProcedureCallback callback) {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = DBUtil.getConnection();
            cstmt = conn.prepareCall(sql);
            callback.doInCallableStatement(cstmt);
        } catch (SQLException e) {
            logger.error("调用存储过程失败: " + sql, e);
            throw new RuntimeException("调用存储过程失败", e);
        } finally {
            DBUtil.close(cstmt, conn);  // ✅ 修改：使用 DBUtil.close()
        }
    }

    /**
     * 设置 PreparedStatement 参数
     */
    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param == null) {
                    pstmt.setNull(i + 1, Types.NULL);
                } else if (param instanceof java.util.Date) {
                    // 处理日期类型
                    java.util.Date date = (java.util.Date) param;
                    if (param instanceof java.sql.Date) {
                        pstmt.setDate(i + 1, (java.sql.Date) param);
                    } else if (param instanceof java.sql.Timestamp) {
                        pstmt.setTimestamp(i + 1, (java.sql.Timestamp) param);
                    } else {
                        pstmt.setTimestamp(i + 1, new java.sql.Timestamp(date.getTime()));
                    }
                } else {
                    pstmt.setObject(i + 1, param);
                }
            }
        }
    }

    /**
     * 行映射器接口
     */
    @FunctionalInterface
    protected interface RowMapper<E> {
        E mapRow(ResultSet rs) throws SQLException;
    }

    /**
     * 存储过程回调接口
     */
    @FunctionalInterface
    protected interface ProcedureCallback {
        void doInCallableStatement(CallableStatement cstmt) throws SQLException;
    }

    /**
     * 批量更新操作
     */
    protected int[] batchUpdate(String sql, List<Object[]> paramsList) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            pstmt = conn.prepareStatement(sql);

            for (Object[] params : paramsList) {
                setParameters(pstmt, params);
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            conn.commit(); // 提交事务

            logger.info("批量更新成功，共 {} 条记录", paramsList.size());
            return results;

        } catch (SQLException e) {
            logger.error("批量更新失败: " + sql, e);
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException ex) {
                logger.error("事务回滚失败", ex);
            }
            throw new RuntimeException("批量更新失败", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                logger.error("恢复自动提交失败", e);
            }
            DBUtil.close(pstmt, conn);  // ✅ 修改：正确的参数顺序
        }
    }

    /**
     * 执行事务
     */
    protected void executeTransaction(TransactionCallback callback) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            DBUtil.beginTransaction(conn);

            callback.doInTransaction(conn);

            DBUtil.commit(conn);
            logger.info("事务提交成功");

        } catch (Exception e) {
            logger.error("事务执行失败", e);
            DBUtil.rollback(conn);
            throw new RuntimeException("事务执行失败", e);
        } finally {
            DBUtil.close(conn);  // ✅ 修改：只关闭 Connection
        }
    }

    /**
     * 事务回调接口
     */
    @FunctionalInterface
    protected interface TransactionCallback {
        void doInTransaction(Connection conn) throws Exception;
    }
}
