package com.property.dao;

import com.property.entity.Announcement;
import com.property.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 公告数据访问层
 */
public class AnnouncementDao {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementDao.class);

    // ==================== 管理员端方法 ====================

    /**
     * 管理员端分页查询
     */
    public List<Announcement> findByPage(int pageNum, int pageSize, String keyword) {
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ( ");
            sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY publish_time DESC) AS row_num, ");
            sql.append("    announcement_id, title, content, announcement_type, priority, ");
            sql.append("    publisher_id, publish_time, expiry_time, view_count, status ");
            sql.append("  FROM announcements ");
            sql.append("  WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("    AND (title LIKE ? OR content LIKE ?) ");
            }

            sql.append(") AS temp ");
            sql.append("WHERE row_num BETWEEN ? AND ? ");

            pstmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
            }

            int startRow = (pageNum - 1) * pageSize + 1;
            int endRow = pageNum * pageSize;
            pstmt.setInt(paramIndex++, startRow);
            pstmt.setInt(paramIndex, endRow);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            logger.error("❌ 管理员端分页查询失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 管理员端统计总数
     */
    public long count(String keyword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM announcements WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (title LIKE ? OR content LIKE ?) ");
            }

            pstmt = conn.prepareStatement(sql.toString());

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(1, likeKeyword);
                pstmt.setString(2, likeKeyword);
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("❌ 统计总数失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return 0;
    }

    // ==================== ✅ 新增：管理员端带筛选的分页查询 ====================

    /**
     * ✅ 管理员端分页查询（支持筛选）
     */
    public List<Announcement> findByPageWithFilter(int pageNum, int pageSize, String keyword,
                                                   String announcementType, String priority, Integer status) {
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ( ");
            sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY publish_time DESC) AS row_num, ");
            sql.append("    announcement_id, title, content, announcement_type, priority, ");
            sql.append("    publisher_id, publish_time, expiry_time, view_count, status ");
            sql.append("  FROM announcements ");
            sql.append("  WHERE 1=1 ");

            // 🔥 关键词筛选
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("    AND (title LIKE ? OR content LIKE ?) ");
            }

            // 🔥 类型筛选
            if (announcementType != null && !announcementType.trim().isEmpty()) {
                sql.append("    AND announcement_type = ? ");
            }

            // 🔥 优先级筛选
            if (priority != null && !priority.trim().isEmpty()) {
                sql.append("    AND priority = ? ");
            }

            // 🔥 状态筛选
            if (status != null) {
                sql.append("    AND status = ? ");
            }

            sql.append(") AS temp ");
            sql.append("WHERE row_num BETWEEN ? AND ? ");

            logger.debug("SQL: {}", sql);

            pstmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            // 设置参数
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
            }

            if (announcementType != null && !announcementType.trim().isEmpty()) {
                pstmt.setString(paramIndex++, announcementType);
            }

            if (priority != null && !priority.trim().isEmpty()) {
                pstmt.setString(paramIndex++, priority);
            }

            if (status != null) {
                pstmt.setInt(paramIndex++, status);
            }

            int startRow = (pageNum - 1) * pageSize + 1;
            int endRow = pageNum * pageSize;
            pstmt.setInt(paramIndex++, startRow);
            pstmt.setInt(paramIndex, endRow);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

            logger.info("✅ 查询到 {} 条公告", list.size());

        } catch (SQLException e) {
            logger.error("❌ 管理员端带筛选分页查询失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * ✅ 管理员端统计总数（支持筛选）
     */
    public long countWithFilter(String keyword, String announcementType, String priority, Integer status) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM announcements WHERE 1=1 ");

            // 🔥 关键词筛选
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (title LIKE ? OR content LIKE ?) ");
            }

            // 🔥 类型筛选
            if (announcementType != null && !announcementType.trim().isEmpty()) {
                sql.append(" AND announcement_type = ? ");
            }

            // 🔥 优先级筛选
            if (priority != null && !priority.trim().isEmpty()) {
                sql.append(" AND priority = ? ");
            }

            // 🔥 状态筛选
            if (status != null) {
                sql.append(" AND status = ? ");
            }

            logger.debug("SQL: {}", sql);

            pstmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            // 设置参数
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
            }

            if (announcementType != null && !announcementType.trim().isEmpty()) {
                pstmt.setString(paramIndex++, announcementType);
            }

            if (priority != null && !priority.trim().isEmpty()) {
                pstmt.setString(paramIndex++, priority);
            }

            if (status != null) {
                pstmt.setInt(paramIndex, status);
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.info("✅ 统计总数: {}", count);
                return count;
            }

        } catch (SQLException e) {
            logger.error("❌ 统计总数失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return 0;
    }

    // ==================== 🔥 业主端方法 ====================

    /**
     * 🔥 业主端分页查询（只查询已发布的公告）
     */
    public List<Announcement> findByPageForOwner(int pageNum, int pageSize, String announcementType) {
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ( ");
            sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY publish_time DESC) AS row_num, ");
            sql.append("    announcement_id, title, content, announcement_type, priority, ");
            sql.append("    publisher_id, publish_time, expiry_time, view_count, status ");
            sql.append("  FROM announcements ");
            sql.append("  WHERE status = 1 ");  // 🔥 只查询已发布的（status=1）

            // 🔥 类型筛选
            if (announcementType != null && !announcementType.trim().isEmpty()) {
                sql.append("    AND announcement_type = ? ");
            }

            sql.append(") AS temp ");
            sql.append("WHERE row_num BETWEEN ? AND ? ");

            logger.debug("SQL: {}", sql);

            pstmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            if (announcementType != null && !announcementType.trim().isEmpty()) {
                pstmt.setString(paramIndex++, announcementType);
            }

            int startRow = (pageNum - 1) * pageSize + 1;
            int endRow = pageNum * pageSize;
            pstmt.setInt(paramIndex++, startRow);
            pstmt.setInt(paramIndex, endRow);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

            logger.info("✅ 查询到 {} 条公告", list.size());

        } catch (SQLException e) {
            logger.error("❌ 业主端分页查询失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 🔥 业主端统计总数（只统计已发布的）
     */
    public long countForOwner(String announcementType) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM announcements WHERE status = 1 ");

            if (announcementType != null && !announcementType.trim().isEmpty()) {
                sql.append(" AND announcement_type = ? ");
            }

            pstmt = conn.prepareStatement(sql.toString());

            if (announcementType != null && !announcementType.trim().isEmpty()) {
                pstmt.setString(1, announcementType);
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("❌ 业主端统计总数失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return 0;
    }

    /**
     * 🔥 增加浏览次数
     */
    public void increaseViewCount(Integer announcementId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "UPDATE announcements SET view_count = view_count + 1 WHERE announcement_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, announcementId);

            int rows = pstmt.executeUpdate();
            logger.info("✅ 浏览次数+1 成功：announcementId={}, 影响行数={}", announcementId, rows);

        } catch (SQLException e) {
            logger.error("❌ 增加浏览次数失败：announcementId={}", announcementId, e);
            // 不抛出异常，避免影响主流程
        } finally {
            DBUtil.close(null, pstmt, conn);
        }
    }

    /**
     * 根据ID查询公告
     */
    public Announcement findById(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT * FROM announcements WHERE announcement_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            logger.error("❌ 根据ID查询公告失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return null;
    }

    /**
     * 🔥 按类型查询公告
     */
    public List<Announcement> findByType(String announcementType, int limit) {
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT TOP (?) * FROM announcements " +
                    "WHERE status = 1 AND announcement_type = ? " +
                    "ORDER BY publish_time DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setString(2, announcementType);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            logger.error("❌ 按类型查询公告失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 🔥 查询最新公告
     */
    public List<Announcement> findLatest(int limit) {
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT TOP (?) * FROM announcements " +
                    "WHERE status = 1 " +
                    "ORDER BY publish_time DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            logger.error("❌ 查询最新公告失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 🔥 查询热门公告（按浏览量排序）
     */
    public List<Announcement> findPopular(int limit) {
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT TOP (?) * FROM announcements " +
                    "WHERE status = 1 " +
                    "ORDER BY view_count DESC, publish_time DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            logger.error("❌ 查询热门公告失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 🔥 按类型统计数量
     */
    public long countByType(String announcementType) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT COUNT(*) FROM announcements " +
                    "WHERE status = 1 AND announcement_type = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, announcementType);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("❌ 按类型统计数量失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return 0;
    }

    /**
     * 🔥 搜索公告
     */
    public List<Announcement> search(String keyword, int limit) {
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT TOP (?) * FROM announcements " +
                    "WHERE status = 1 " +
                    "AND (title LIKE ? OR content LIKE ?) " +
                    "ORDER BY publish_time DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            logger.error("❌ 搜索公告失败", e);
            throw new RuntimeException("数据库查询失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 插入公告
     */
    public int insert(Announcement announcement) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "INSERT INTO announcements (title, content, announcement_type, priority, " +
                    "publisher_id, publish_time, expiry_time, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setString(3, announcement.getAnnouncementType());
            pstmt.setString(4, announcement.getPriority());
            pstmt.setInt(5, announcement.getPublisherId());
            pstmt.setTimestamp(6, announcement.getPublishTime() != null ?
                    new Timestamp(announcement.getPublishTime().getTime()) : new Timestamp(System.currentTimeMillis()));
            pstmt.setTimestamp(7, announcement.getExpiryTime() != null ?
                    new Timestamp(announcement.getExpiryTime().getTime()) : null);
            pstmt.setInt(8, announcement.getStatus() != null ? announcement.getStatus() : 1);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("❌ 插入公告失败", e);
            throw new RuntimeException("数据库操作失败", e);
        } finally {
            DBUtil.close(null, pstmt, conn);
        }
    }

    /**
     * 更新公告
     */
    public int update(Announcement announcement) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "UPDATE announcements SET title=?, content=?, announcement_type=?, " +
                    "priority=?, publish_time=?, expiry_time=?, status=? " +
                    "WHERE announcement_id=?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setString(3, announcement.getAnnouncementType());
            pstmt.setString(4, announcement.getPriority());
            pstmt.setTimestamp(5, new Timestamp(announcement.getPublishTime().getTime()));
            pstmt.setTimestamp(6, announcement.getExpiryTime() != null ?
                    new Timestamp(announcement.getExpiryTime().getTime()) : null);
            pstmt.setInt(7, announcement.getStatus());
            pstmt.setInt(8, announcement.getAnnouncementId());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("❌ 更新公告失败", e);
            throw new RuntimeException("数据库操作失败", e);
        } finally {
            DBUtil.close(null, pstmt, conn);
        }
    }

    /**
     * 删除公告
     */
    public int delete(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "DELETE FROM announcements WHERE announcement_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("❌ 删除公告失败", e);
            throw new RuntimeException("数据库操作失败", e);
        } finally {
            DBUtil.close(null, pstmt, conn);
        }
    }

    /**
     * 🔥 映射 ResultSet 到 Announcement 对象
     */
    private Announcement mapResultSet(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("announcement_id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setAnnouncementType(rs.getString("announcement_type"));
        announcement.setPriority(rs.getString("priority"));
        announcement.setPublisherId(rs.getInt("publisher_id"));
        announcement.setPublishTime(rs.getTimestamp("publish_time"));
        announcement.setExpiryTime(rs.getTimestamp("expiry_time"));
        announcement.setViewCount(rs.getInt("view_count"));
        announcement.setStatus(rs.getInt("status"));
        return announcement;
    }
    /**
     * 🔥 查询已发布公告列表（支持搜索 + 时效性过滤）
     */
    public List<Announcement> getPublishedList(String type, String keyword, int pageNum, int pageSize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Announcement> list = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ( ");
            sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY publish_time DESC) AS row_num, ");
            sql.append("    announcement_id, title, content, announcement_type, priority, ");
            sql.append("    publisher_id, publish_time, expiry_time, view_count, status ");
            sql.append("  FROM announcements ");
            sql.append("  WHERE status = 1 ");

            // 🔥 添加时效性过滤：只显示未过期的公告
            sql.append("  AND (expiry_time IS NULL OR expiry_time >= GETDATE()) ");

            // 添加类型筛选
            if (type != null && !type.trim().isEmpty()) {
                sql.append("  AND announcement_type = ? ");
            }

            // 添加关键词搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("  AND (title LIKE ? OR content LIKE ?) ");
            }

            sql.append(") AS temp ");
            sql.append("WHERE row_num BETWEEN ? AND ? ");

            pstmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            // 设置类型参数
            if (type != null && !type.trim().isEmpty()) {
                pstmt.setString(paramIndex++, type);
            }

            // 设置关键词参数
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
            }

            // 设置分页参数
            int start = (pageNum - 1) * pageSize + 1;
            int end = pageNum * pageSize;
            pstmt.setInt(paramIndex++, start);
            pstmt.setInt(paramIndex, end);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 🔥 统计已发布公告数量（支持搜索 + 时效性过滤）
     */
    public int countPublished(String type, String keyword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM announcements WHERE status = 1 ");

            // 🔥 添加时效性过滤：只统计未过期的公告
            sql.append("AND (expiry_time IS NULL OR expiry_time >= GETDATE()) ");

            // 添加类型筛选
            if (type != null && !type.trim().isEmpty()) {
                sql.append("AND announcement_type = ? ");
            }

            // 添加关键词搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("AND (title LIKE ? OR content LIKE ?) ");
            }

            pstmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            // 设置类型参数
            if (type != null && !type.trim().isEmpty()) {
                pstmt.setString(paramIndex++, type);
            }

            // 设置关键词参数
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex, likeKeyword);
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return count;
    }

}
