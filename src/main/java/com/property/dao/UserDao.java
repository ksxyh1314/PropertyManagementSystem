package com.property.dao;

import com.property.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户DAO
 */
public class UserDao extends BaseDao {

    /**
     * 用户登录验证
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 1";
        return queryOne(sql, this::mapUser, username, password);
    }

    /**
     * 根据用户ID查询用户
     */
    public User findById(Integer userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return queryOne(sql, this::mapUser, userId);
    }

    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        return queryOne(sql, this::mapUser, username);
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY create_time DESC";
        return query(sql, this::mapUser);
    }

    /**
     * 根据角色查询用户
     */
    public List<User> findByRole(String role) {
        String sql = "SELECT * FROM users WHERE user_role = ? ORDER BY create_time DESC";
        return query(sql, this::mapUser, role);
    }

    /**
     * 分页查询用户
     */
    public List<User> findByPage(int pageNum, int pageSize, String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY create_time DESC) AS row_num, * ");
        sql.append("  FROM users WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("  AND (username LIKE ? OR real_name LIKE ? OR phone LIKE ?) ");
        }

        sql.append(") AS temp ");
        sql.append("WHERE row_num BETWEEN ? AND ?");

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeKeyword = "%" + keyword + "%";
            return query(sql.toString(), this::mapUser, likeKeyword, likeKeyword, likeKeyword, start, end);
        } else {
            return query(sql.toString(), this::mapUser, start, end);
        }
    }

    /**
     * 统计用户总数
     */
    public long count(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR real_name LIKE ? OR phone LIKE ?)");
            String likeKeyword = "%" + keyword + "%";
            return queryForLong(sql.toString(), likeKeyword, likeKeyword, likeKeyword);
        }

        return queryForLong(sql.toString());
    }

    /**
     * 添加用户
     */
    public Integer insert(User user) {
        String sql = "INSERT INTO users (username, password, real_name, user_role, phone, id_card, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return insertAndGetKey(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getUserRole(),
                user.getPhone(),
                user.getIdCard(),
                user.getStatus() != null ? user.getStatus() : 1
        );
    }

    /**
     * 更新用户信息
     */
    public int update(User user) {
        String sql = "UPDATE users SET real_name = ?, phone = ?, id_card = ?, status = ? " +
                "WHERE user_id = ?";
        return update(sql,
                user.getRealName(),
                user.getPhone(),
                user.getIdCard(),
                user.getStatus(),
                user.getUserId()
        );
    }

    /**
     * 修改密码
     */
    public int updatePassword(Integer userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        return update(sql, newPassword, userId);
    }

    /**
     * 更新最后登录时间
     */
    public int updateLastLogin(Integer userId) {
        String sql = "UPDATE users SET last_login = GETDATE() WHERE user_id = ?";
        return update(sql, userId);
    }

    /**
     * 删除用户
     */
    public int delete(Integer userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        return update(sql, userId);
    }

    /**
     * 启用/禁用用户
     */
    public int updateStatus(Integer userId, Integer status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        return update(sql, status, userId);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        return queryForLong(sql, username) > 0;
    }

    /**
     * 映射结果集到User对象
     */
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setUserRole(rs.getString("user_role"));
        user.setPhone(rs.getString("phone"));
        user.setIdCard(rs.getString("id_card"));
        user.setStatus(rs.getInt("status"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}
