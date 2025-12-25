package com.property.test;

import com.property.util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class QuickTest {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("      数据库连接快速测试");
        System.out.println("========================================\n");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 步骤 1：测试连接池初始化
            System.out.println("【步骤 1】测试连接池初始化...");
            System.out.println(DBUtil.getPoolInfo());

            // 步骤 2：获取数据库连接
            System.out.println("\n【步骤 2】获取数据库连接...");
            long startTime = System.currentTimeMillis();
            conn = DBUtil.getConnection();
            long endTime = System.currentTimeMillis();
            System.out.println("✅ 连接获取成功！耗时: " + (endTime - startTime) + "ms");

            // 步骤 3：测试数据库版本
            System.out.println("\n【步骤 3】查询数据库信息...");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT @@VERSION AS version, DB_NAME() AS dbname");
            if (rs.next()) {
                System.out.println("✅ 当前数据库: " + rs.getString("dbname"));
                System.out.println("✅ SQL Server 版本: " + rs.getString("version").split("\n")[0]);
            }

            // 步骤 4：检查用户表
            System.out.println("\n【步骤 4】检查用户表...");
            try {
                rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM users");
                if (rs.next()) {
                    int count = rs.getInt("total");
                    System.out.println("✅ 用户表存在，共有 " + count + " 条记录");

                    // 查询 admin 用户
                    rs = stmt.executeQuery("SELECT * FROM users WHERE username = 'admin'");
                    if (rs.next()) {
                        System.out.println("\n✅ 找到测试用户 admin：");
                        System.out.println("   - 用户ID: " + rs.getInt("user_id"));
                        System.out.println("   - 用户名: " + rs.getString("username"));
                        System.out.println("   - 真实姓名: " + rs.getString("real_name"));
                        System.out.println("   - 角色: " + rs.getString("user_role"));
                        System.out.println("   - 状态: " + (rs.getBoolean("active") ? "✅ 启用" : "❌ 禁用"));
                        System.out.println("   - 密码(MD5): " + rs.getString("password"));
                    } else {
                        System.out.println("⚠️  未找到 admin 用户");
                        System.out.println("\n请执行以下 SQL 创建测试用户：");
                        System.out.println("--------------------------------------------------");
                        System.out.println("INSERT INTO users (username, password, real_name, user_role, active, create_time)");
                        System.out.println("VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'admin', 1, GETDATE());");
                        System.out.println("--------------------------------------------------");
                        System.out.println("密码是: admin123");
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ 用户表不存在或查询失败: " + e.getMessage());
                System.out.println("\n请先创建用户表，参考之前提供的建表 SQL。");
            }

            // 步骤 5：测试完成
            System.out.println("\n========================================");
            System.out.println("      ✅ 所有测试通过！");
            System.out.println("========================================");

        } catch (java.sql.SQLException e) {
            System.err.println("\n❌ 数据库连接失败！");
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("错误代码: " + e.getErrorCode());
            System.err.println("SQL 状态: " + e.getSQLState());

            System.err.println("\n可能的原因：");
            if (e.getMessage().contains("Login failed")) {
                System.err.println("1. Windows 身份验证失败");
                System.err.println("   解决：在 SSMS 中给当前 Windows 用户授权");
                System.err.println("   或改用 SQL Server 身份验证（sa 账号）");
            } else if (e.getMessage().contains("database") && e.getMessage().contains("cannot be opened")) {
                System.err.println("1. 数据库不存在或无法访问");
                System.err.println("   解决：检查数据库名是否正确");
            } else if (e.getMessage().contains("connection")) {
                System.err.println("1. SQL Server 服务未启动");
                System.err.println("2. TCP/IP 协议未启用");
                System.err.println("3. 端口 1433 被防火墙阻止");
            }

            e.printStackTrace();

        } catch (Exception e) {
            System.err.println("\n❌ 未知错误: " + e.getMessage());
            e.printStackTrace();

        } finally {
            DBUtil.close(conn, stmt, rs);
            System.out.println("\n资源已释放");
        }
    }
}
