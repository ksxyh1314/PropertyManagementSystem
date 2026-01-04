package com.property.service;

import com.property.util.DBUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * 统计服务类
 */
public class StatisticsService {

    /**
     * 获取仪表盘统计数据
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try (Connection conn = DBUtil.getConnection()) {
            // 总业主数
            String sql1 = "SELECT COUNT(*) as total FROM owners";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next()) {
                stats.put("totalOwners", rs1.getInt("total"));
            }
            rs1.close();
            pstmt1.close();

            // 总房屋数
            String sql2 = "SELECT COUNT(*) as total FROM houses";
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next()) {
                stats.put("totalHouses", rs2.getInt("total"));
            }
            rs2.close();
            pstmt2.close();

            // 已缴费数
            String sql3 = "SELECT COUNT(*) as count FROM payment_records WHERE payment_status = 'paid'";
            PreparedStatement pstmt3 = conn.prepareStatement(sql3);
            ResultSet rs3 = pstmt3.executeQuery();
            if (rs3.next()) {
                stats.put("paidCount", rs3.getInt("count"));
            }
            rs3.close();
            pstmt3.close();

            // 未缴费数
            String sql4 = "SELECT COUNT(*) as count FROM payment_records WHERE payment_status = 'unpaid'";
            PreparedStatement pstmt4 = conn.prepareStatement(sql4);
            ResultSet rs4 = pstmt4.executeQuery();
            if (rs4.next()) {
                stats.put("unpaidCount", rs4.getInt("count"));
            }
            rs4.close();
            pstmt4.close();

            // 逾期数
            String sql5 = "SELECT COUNT(*) as count FROM payment_records WHERE payment_status = 'overdue'";
            PreparedStatement pstmt5 = conn.prepareStatement(sql5);
            ResultSet rs5 = pstmt5.executeQuery();
            if (rs5.next()) {
                stats.put("overdueCount", rs5.getInt("count"));
            }
            rs5.close();
            pstmt5.close();

            // 本月收入
            String sql6 = "SELECT COALESCE(SUM(amount), 0) as total " +
                    "FROM payment_records " +
                    "WHERE payment_status = 'paid' " +
                    "AND YEAR(payment_date) = YEAR(GETDATE()) " +
                    "AND MONTH(payment_date) = MONTH(GETDATE())";
            PreparedStatement pstmt6 = conn.prepareStatement(sql6);
            ResultSet rs6 = pstmt6.executeQuery();
            if (rs6.next()) {
                stats.put("monthlyIncome", rs6.getDouble("total"));
            }
            rs6.close();
            pstmt6.close();

            // 总欠费金额
            String sql7 = "SELECT COALESCE(SUM(total_amount), 0) as total " +
                    "FROM payment_records " +
                    "WHERE payment_status IN ('unpaid', 'overdue')";
            PreparedStatement pstmt7 = conn.prepareStatement(sql7);
            ResultSet rs7 = pstmt7.executeQuery();
            if (rs7.next()) {
                stats.put("totalArrears", rs7.getDouble("total"));
            }
            rs7.close();
            pstmt7.close();

            // ========== ✅ 新增：首页仪表盘需要的数据 ==========

            // 已入住房屋数
            String sql8 = "SELECT COUNT(*) as count FROM houses WHERE house_status = 'occupied'";
            PreparedStatement pstmt8 = conn.prepareStatement(sql8);
            ResultSet rs8 = pstmt8.executeQuery();
            if (rs8.next()) {
                stats.put("occupiedHouses", rs8.getInt("count"));
            }
            rs8.close();
            pstmt8.close();

            // 空置房屋数
            String sql9 = "SELECT COUNT(*) as count FROM houses WHERE house_status = 'vacant'";
            PreparedStatement pstmt9 = conn.prepareStatement(sql9);
            ResultSet rs9 = pstmt9.executeQuery();
            if (rs9.next()) {
                stats.put("vacantHouses", rs9.getInt("count"));
            }
            rs9.close();
            pstmt9.close();

            // 待处理报修数
            String sql10 = "SELECT COUNT(*) as count FROM repair_records WHERE repair_status = 'pending'";
            PreparedStatement pstmt10 = conn.prepareStatement(sql10);
            ResultSet rs10 = pstmt10.executeQuery();
            if (rs10.next()) {
                stats.put("pendingRepairs", rs10.getInt("count"));
            }
            rs10.close();
            pstmt10.close();

            // 处理中报修数
            String sql11 = "SELECT COUNT(*) as count FROM repair_records WHERE repair_status = 'processing'";
            PreparedStatement pstmt11 = conn.prepareStatement(sql11);
            ResultSet rs11 = pstmt11.executeQuery();
            if (rs11.next()) {
                stats.put("processingRepairs", rs11.getInt("count"));
            }
            rs11.close();
            pstmt11.close();

            // 已完成报修数
            String sql12 = "SELECT COUNT(*) as count FROM repair_records WHERE repair_status = 'completed'";
            PreparedStatement pstmt12 = conn.prepareStatement(sql12);
            ResultSet rs12 = pstmt12.executeQuery();
            if (rs12.next()) {
                stats.put("completedRepairs", rs12.getInt("count"));
            }
            rs12.close();
            pstmt12.close();

            // 本月收缴率
            String sql13 = "SELECT " +
                    "    CAST(COUNT(CASE WHEN payment_status = 'paid' THEN 1 END) * 100.0 / " +
                    "    NULLIF(COUNT(*), 0) AS DECIMAL(5,2)) as paymentRate " +
                    "FROM payment_records " +
                    "WHERE YEAR(due_date) = YEAR(GETDATE()) " +
                    "AND MONTH(due_date) = MONTH(GETDATE())";
            PreparedStatement pstmt13 = conn.prepareStatement(sql13);
            ResultSet rs13 = pstmt13.executeQuery();
            if (rs13.next()) {
                stats.put("paymentRate", rs13.getDouble("paymentRate"));
            } else {
                stats.put("paymentRate", 0.0);
            }
            rs13.close();
            pstmt13.close();

            // ========== 🔥 新增：已取消报修数 ==========
            String sql14 = "SELECT COUNT(*) as count FROM repair_records WHERE repair_status = 'cancelled'";
            PreparedStatement pstmt14 = conn.prepareStatement(sql14);
            ResultSet rs14 = pstmt14.executeQuery();
            if (rs14.next()) {
                stats.put("cancelledRepairs", rs14.getInt("count"));
            }
            rs14.close();
            pstmt14.close();

            // ========== 🔥 新增：平均满意度 ==========
            String sql15 = "SELECT AVG(CAST(satisfaction_rating AS FLOAT)) as avgRating " +
                    "FROM repair_records " +
                    "WHERE satisfaction_rating IS NOT NULL";
            PreparedStatement pstmt15 = conn.prepareStatement(sql15);
            ResultSet rs15 = pstmt15.executeQuery();
            if (rs15.next()) {
                double avgRating = rs15.getDouble("avgRating");
                if (!rs15.wasNull()) {
                    stats.put("avgRating", Math.round(avgRating * 10.0) / 10.0); // 保留1位小数
                } else {
                    stats.put("avgRating", 0.0);
                }
            } else {
                stats.put("avgRating", 0.0);
            }
            rs15.close();
            pstmt15.close();

            // ========== ✅✅✅ 新增：投诉统计（包含已关闭状态）==========

            // 总投诉数
            String sql16 = "SELECT COUNT(*) as count FROM complaints";
            PreparedStatement pstmt16 = conn.prepareStatement(sql16);
            ResultSet rs16 = pstmt16.executeQuery();
            if (rs16.next()) {
                stats.put("totalComplaints", rs16.getInt("count"));
            }
            rs16.close();
            pstmt16.close();

            // 待处理投诉数
            String sql17 = "SELECT COUNT(*) as count FROM complaints WHERE complaint_status = 'pending'";
            PreparedStatement pstmt17 = conn.prepareStatement(sql17);
            ResultSet rs17 = pstmt17.executeQuery();
            if (rs17.next()) {
                stats.put("pendingComplaints", rs17.getInt("count"));
            }
            rs17.close();
            pstmt17.close();

            // 处理中投诉数
            String sql18 = "SELECT COUNT(*) as count FROM complaints WHERE complaint_status = 'processing'";
            PreparedStatement pstmt18 = conn.prepareStatement(sql18);
            ResultSet rs18 = pstmt18.executeQuery();
            if (rs18.next()) {
                stats.put("processingComplaints", rs18.getInt("count"));
            }
            rs18.close();
            pstmt18.close();

            // 已解决投诉数
            String sql19 = "SELECT COUNT(*) as count FROM complaints WHERE complaint_status = 'resolved'";
            PreparedStatement pstmt19 = conn.prepareStatement(sql19);
            ResultSet rs19 = pstmt19.executeQuery();
            if (rs19.next()) {
                stats.put("resolvedComplaints", rs19.getInt("count"));
            }
            rs19.close();
            pstmt19.close();

            // ✅ 已关闭投诉数（新增）
            String sql20 = "SELECT COUNT(*) as count FROM complaints WHERE complaint_status = 'closed'";
            PreparedStatement pstmt20 = conn.prepareStatement(sql20);
            ResultSet rs20 = pstmt20.executeQuery();
            if (rs20.next()) {
                stats.put("closedComplaints", rs20.getInt("count"));
            }
            rs20.close();
            pstmt20.close();

            // 打印日志
            System.out.println("========== 仪表盘数据 ==========");
            System.out.println("总房屋数: " + stats.get("totalHouses"));
            System.out.println("总业主数: " + stats.get("totalOwners"));
            System.out.println("已入住: " + stats.get("occupiedHouses"));
            System.out.println("空置: " + stats.get("vacantHouses"));
            System.out.println("待处理报修: " + stats.get("pendingRepairs"));
            System.out.println("处理中报修: " + stats.get("processingRepairs"));
            System.out.println("已完成报修: " + stats.get("completedRepairs"));
            System.out.println("已取消报修: " + stats.get("cancelledRepairs"));
            System.out.println("平均满意度: " + stats.get("avgRating"));
            System.out.println("本月收缴率: " + stats.get("paymentRate") + "%");

            // ✅ 投诉统计日志
            System.out.println("---------- 投诉统计 ----------");
            System.out.println("总投诉数: " + stats.get("totalComplaints"));
            System.out.println("待处理投诉: " + stats.get("pendingComplaints"));
            System.out.println("处理中投诉: " + stats.get("processingComplaints"));
            System.out.println("已解决投诉: " + stats.get("resolvedComplaints"));
            System.out.println("已关闭投诉: " + stats.get("closedComplaints"));
            System.out.println("===============================");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("获取仪表盘统计数据失败: " + e.getMessage());
        }

        return stats;
    }

    /**
     * 获取收费趋势数据（最近6个月）
     */
    public List<Map<String, Object>> getPaymentTrend() {
        List<Map<String, Object>> trendData = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT " +
                    "    FORMAT(due_date, 'yyyy-MM') as month, " +
                    "    COALESCE(SUM(amount), 0) as totalAmount, " +
                    "    COALESCE(SUM(CASE WHEN payment_status = 'paid' THEN amount ELSE 0 END), 0) as paidAmount " +
                    "FROM payment_records " +
                    "WHERE due_date >= DATEADD(MONTH, -6, GETDATE()) " +
                    "GROUP BY FORMAT(due_date, 'yyyy-MM') " +
                    "ORDER BY FORMAT(due_date, 'yyyy-MM')";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("month", rs.getString("month"));
                data.put("totalAmount", rs.getDouble("totalAmount"));
                data.put("paidAmount", rs.getDouble("paidAmount"));
                trendData.add(data);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("获取收费趋势数据失败: " + e.getMessage());
        }

        return trendData;
    }

    /**
     * 获取待处理报修列表
     */
    public List<Map<String, Object>> getPendingRepairs() {
        List<Map<String, Object>> repairs = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT TOP 10 " +
                    "    rr.repair_id, " +
                    "    rr.owner_id, " +
                    "    o.owner_name, " +
                    "    rr.house_id, " +
                    "    rr.repair_type, " +
                    "    rr.description, " +
                    "    rr.priority, " +
                    "    rr.submit_time " +
                    "FROM repair_records rr " +
                    "INNER JOIN owners o ON rr.owner_id = o.owner_id " +
                    "WHERE rr.repair_status = 'pending' " +
                    "ORDER BY rr.submit_time DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> repair = new HashMap<>();
                repair.put("repairId", rs.getInt("repair_id"));
                repair.put("ownerId", rs.getString("owner_id"));
                repair.put("ownerName", rs.getString("owner_name"));
                repair.put("houseId", rs.getString("house_id"));
                repair.put("repairType", rs.getString("repair_type"));
                repair.put("description", rs.getString("description"));
                repair.put("priority", rs.getString("priority"));
                repair.put("submitTime", rs.getTimestamp("submit_time"));
                repairs.add(repair);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("获取待处理报修列表失败: " + e.getMessage());
        }

        return repairs;
    }

    /**
     * 获取楼栋缴费状态
     */
    public List<Map<String, Object>> getBuildingPaymentStatus() {
        List<Map<String, Object>> buildingStats = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT " +
                    "    h.building_no as buildingNo, " +
                    "    COUNT(pr.record_id) as totalRecords, " +
                    "    SUM(CASE WHEN pr.payment_status = 'paid' THEN 1 ELSE 0 END) as paidRecords, " +
                    "    CAST(SUM(CASE WHEN pr.payment_status = 'paid' THEN 1 ELSE 0 END) * 100.0 / " +
                    "         NULLIF(COUNT(pr.record_id), 0) AS DECIMAL(5,2)) as paymentRate " +
                    "FROM houses h " +
                    "LEFT JOIN payment_records pr ON h.house_id = pr.house_id " +
                    "GROUP BY h.building_no " +
                    "ORDER BY h.building_no";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("buildingNo", rs.getString("buildingNo"));
                stat.put("totalRecords", rs.getInt("totalRecords"));
                stat.put("paidRecords", rs.getInt("paidRecords"));
                stat.put("paymentRate", rs.getDouble("paymentRate"));
                buildingStats.add(stat);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("获取楼栋缴费状态失败: " + e.getMessage());
        }

        return buildingStats;
    }

    /**
     * 获取物业收费统计
     */
    public List<Map<String, Object>> getPaymentStatistics(String startMonth, String endMonth) {
        List<Map<String, Object>> stats = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT " +
                    "    FORMAT(pr.due_date, 'yyyy-MM') as month, " +
                    "    ci.item_name, " +
                    "    COUNT(*) as totalBills, " +
                    "    SUM(CASE WHEN pr.payment_status = 'paid' THEN 1 ELSE 0 END) as paidBills, " +
                    "    SUM(pr.amount) as totalAmount, " +
                    "    SUM(CASE WHEN pr.payment_status = 'paid' THEN pr.amount ELSE 0 END) as paidAmount " +
                    "FROM payment_records pr " +
                    "INNER JOIN charge_items ci ON pr.item_id = ci.item_id " +
                    "WHERE FORMAT(pr.due_date, 'yyyy-MM') >= ? " +
                    "  AND FORMAT(pr.due_date, 'yyyy-MM') <= ? " +
                    "GROUP BY FORMAT(pr.due_date, 'yyyy-MM'), ci.item_name " +
                    "ORDER BY FORMAT(pr.due_date, 'yyyy-MM')";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, startMonth);
            pstmt.setString(2, endMonth);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("month", rs.getString("month"));
                stat.put("itemName", rs.getString("item_name"));
                stat.put("totalBills", rs.getInt("totalBills"));
                stat.put("paidBills", rs.getInt("paidBills"));
                stat.put("totalAmount", rs.getDouble("totalAmount"));
                stat.put("paidAmount", rs.getDouble("paidAmount"));
                stats.add(stat);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("获取物业收费统计失败: " + e.getMessage());
        }

        return stats;
    }

    /**
     * 生成财务报表
     */
    public Map<String, Object> generateFinancialReport(Date startDate, Date endDate) {
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("startDate", startDate);
        reportData.put("endDate", endDate);

        try (Connection conn = DBUtil.getConnection()) {
            // 期间统计
            Map<String, Object> periodStats = new HashMap<>();

            String sql1 = "SELECT " +
                    "    COUNT(*) as totalCount, " +
                    "    SUM(CASE WHEN payment_status = 'paid' THEN 1 ELSE 0 END) as paidCount, " +
                    "    SUM(amount) as totalAmount, " +
                    "    SUM(CASE WHEN payment_status = 'paid' THEN amount ELSE 0 END) as paidAmount, " +
                    "    SUM(late_fee) as totalLateFee " +
                    "FROM payment_records " +
                    "WHERE due_date >= ? AND due_date <= ?";

            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt1.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs1 = pstmt1.executeQuery();

            if (rs1.next()) {
                periodStats.put("totalCount", rs1.getInt("totalCount"));
                periodStats.put("paidCount", rs1.getInt("paidCount"));
                periodStats.put("totalAmount", rs1.getDouble("totalAmount"));
                periodStats.put("paidAmount", rs1.getDouble("paidAmount"));
                periodStats.put("totalLateFee", rs1.getDouble("totalLateFee"));
            }

            rs1.close();
            pstmt1.close();

            reportData.put("periodStats", periodStats);

            // 楼栋统计
            List<Map<String, Object>> buildingStats = new ArrayList<>();

            String sql2 = "SELECT " +
                    "    h.building_no as buildingNo, " +
                    "    COUNT(pr.record_id) as totalRecords, " +
                    "    SUM(CASE WHEN pr.payment_status = 'paid' THEN 1 ELSE 0 END) as paidRecords, " +
                    "    SUM(pr.amount) as totalAmount, " +
                    "    SUM(CASE WHEN pr.payment_status = 'paid' THEN pr.amount ELSE 0 END) as paidAmount, " +
                    "    SUM(CASE WHEN pr.payment_status != 'paid' THEN pr.amount ELSE 0 END) as unpaidAmount, " +
                    "    SUM(pr.late_fee) as totalLateFee, " +
                    "    CAST(SUM(CASE WHEN pr.payment_status = 'paid' THEN 1 ELSE 0 END) * 100.0 / " +
                    "         NULLIF(COUNT(pr.record_id), 0) AS DECIMAL(5,2)) as paymentRate " +
                    "FROM houses h " +
                    "LEFT JOIN payment_records pr ON h.house_id = pr.house_id " +
                    "WHERE pr.due_date >= ? AND pr.due_date <= ? " +
                    "GROUP BY h.building_no " +
                    "ORDER BY h.building_no";

            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt2.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs2 = pstmt2.executeQuery();

            while (rs2.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("buildingNo", rs2.getString("buildingNo"));
                stat.put("totalRecords", rs2.getInt("totalRecords"));
                stat.put("paidRecords", rs2.getInt("paidRecords"));
                stat.put("totalAmount", rs2.getDouble("totalAmount"));
                stat.put("paidAmount", rs2.getDouble("paidAmount"));
                stat.put("unpaidAmount", rs2.getDouble("unpaidAmount"));
                stat.put("totalLateFee", rs2.getDouble("totalLateFee"));
                stat.put("paymentRate", rs2.getDouble("paymentRate"));
                buildingStats.add(stat);
            }

            rs2.close();
            pstmt2.close();

            reportData.put("buildingStats", buildingStats);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("生成财务报表失败: " + e.getMessage());
        }

        return reportData;
    }

    /**
     * 获取指定月份的统计数据（修复版）
     */
    public Map<String, Object> getMonthlyStatistics(Date startDate, Date endDate) {
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = DBUtil.getConnection()) {

            // 查询应收金额（按 due_date，所有状态）
            String sql1 = "SELECT COALESCE(SUM(amount), 0) as totalAmount " +
                    "FROM payment_records " +
                    "WHERE due_date >= ? AND due_date <= ?";

            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt1.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs1 = pstmt1.executeQuery();

            double totalAmount = 0.0;
            if (rs1.next()) {
                totalAmount = rs1.getDouble("totalAmount");
            }
            rs1.close();
            pstmt1.close();

            // 查询实收金额（按 due_date，只统计已缴费）
            String sql2 = "SELECT COALESCE(SUM(amount), 0) as paidAmount " +
                    "FROM payment_records " +
                    "WHERE due_date >= ? AND due_date <= ? " +
                    "AND payment_status = 'paid'";

            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt2.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs2 = pstmt2.executeQuery();

            double paidAmount = 0.0;
            if (rs2.next()) {
                paidAmount = rs2.getDouble("paidAmount");
            }
            rs2.close();
            pstmt2.close();

            result.put("totalAmount", totalAmount);
            result.put("paidAmount", paidAmount);

            // 添加日志
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("========== 月度统计查询 ==========");
            System.out.println("查询时间范围: " + sdf.format(startDate) + " 至 " + sdf.format(endDate));
            System.out.println("应收金额 (totalAmount): " + totalAmount);
            System.out.println("实收金额 (paidAmount): " + paidAmount);
            System.out.println("================================");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("查询月度统计数据失败: " + e.getMessage());
        }

        return result;
    }
    /**
     * ✅ 获取筛选后的统计数据（支持按收费项目筛选）
     * @param keyword 关键词（业主ID、姓名、房屋编号）
     * @param status 缴费状态（paid/unpaid/overdue）
     * @param itemId 收费项目ID
     * @return 统计数据
     */
    public Map<String, Object> getFilteredStatistics(String keyword, String status, String itemId) {
        Map<String, Object> stats = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // ✅ 构建 WHERE 条件
            StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                whereClause.append(" AND (pr.owner_id LIKE ? OR pr.house_id LIKE ? OR o.owner_name LIKE ?) ");
                String keywordPattern = "%" + keyword.trim() + "%";
                params.add(keywordPattern);
                params.add(keywordPattern);
                params.add(keywordPattern);
            }

            if (status != null && !status.trim().isEmpty()) {
                whereClause.append(" AND pr.payment_status = ? ");
                params.add(status);
            }

            if (itemId != null && !itemId.trim().isEmpty()) {
                whereClause.append(" AND pr.item_id = ? ");
                params.add(itemId);
            }

            // ✅ 1. 统计总记录数、已缴费、未缴费、逾期
            String countSql =
                    "SELECT " +
                            "  COUNT(*) as total_count, " +
                            "  SUM(CASE WHEN payment_status = 'paid' THEN 1 ELSE 0 END) as paid_count, " +
                            "  SUM(CASE WHEN payment_status = 'unpaid' THEN 1 ELSE 0 END) as unpaid_count, " +
                            "  SUM(CASE WHEN payment_status = 'overdue' THEN 1 ELSE 0 END) as overdue_count " +
                            "FROM payment_records pr " +
                            "LEFT JOIN owners o ON pr.owner_id = o.owner_id " +
                            whereClause.toString();

            pstmt = conn.prepareStatement(countSql);
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            rs = pstmt.executeQuery();

            if (rs.next()) {
                stats.put("totalRecords", rs.getInt("total_count"));
                stats.put("totalCount", rs.getInt("total_count"));  // 兼容前端
                stats.put("paidCount", rs.getInt("paid_count"));
                stats.put("unpaidCount", rs.getInt("unpaid_count"));
                stats.put("overdueCount", rs.getInt("overdue_count"));
            }
            rs.close();
            pstmt.close();

            // ✅ 2. 统计金额
            String amountSql =
                    "SELECT " +
                            "  COALESCE(SUM(amount), 0) as total_amount, " +
                            "  COALESCE(SUM(CASE WHEN payment_status = 'paid' THEN amount ELSE 0 END), 0) as paid_amount, " +
                            "  COALESCE(SUM(CASE WHEN payment_status != 'paid' THEN amount ELSE 0 END), 0) as unpaid_amount, " +
                            "  COALESCE(SUM(CASE WHEN payment_status = 'overdue' THEN amount ELSE 0 END), 0) as overdue_amount, " +
                            "  COALESCE(SUM(late_fee), 0) as total_late_fee " +
                            "FROM payment_records pr " +
                            "LEFT JOIN owners o ON pr.owner_id = o.owner_id " +
                            whereClause.toString();

            pstmt = conn.prepareStatement(amountSql);
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            rs = pstmt.executeQuery();

            if (rs.next()) {
                stats.put("totalAmount", rs.getBigDecimal("total_amount"));
                stats.put("paidAmount", rs.getBigDecimal("paid_amount"));
                stats.put("unpaidAmount", rs.getBigDecimal("unpaid_amount"));
                stats.put("overdueAmount", rs.getBigDecimal("overdue_amount"));
                stats.put("totalLateFee", rs.getBigDecimal("total_late_fee"));
            }
            rs.close();
            pstmt.close();

            // ✅ 3. 按收费项目分组统计（费用类型统计）
            String feeTypeSql =
                    "SELECT " +
                            "  ci.item_name, " +
                            "  COUNT(*) as total_count, " +
                            "  COALESCE(SUM(pr.amount), 0) as total_amount, " +
                            "  COALESCE(SUM(CASE WHEN pr.payment_status = 'paid' THEN pr.amount ELSE 0 END), 0) as paid_amount " +
                            "FROM payment_records pr " +
                            "LEFT JOIN charge_items ci ON pr.item_id = ci.item_id " +
                            "LEFT JOIN owners o ON pr.owner_id = o.owner_id " +
                            whereClause.toString() +
                            " GROUP BY ci.item_id, ci.item_name " +
                            "ORDER BY total_amount DESC";

            pstmt = conn.prepareStatement(feeTypeSql);
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            rs = pstmt.executeQuery();

            List<Map<String, Object>> feeTypeStats = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("itemName", rs.getString("item_name"));
                item.put("totalCount", rs.getInt("total_count"));
                item.put("totalAmount", rs.getBigDecimal("total_amount"));
                item.put("paidAmount", rs.getBigDecimal("paid_amount"));
                feeTypeStats.add(item);
            }
            stats.put("feeTypeStats", feeTypeStats);

            System.out.println("========== 筛选统计完成 ==========");
            System.out.println("筛选条件 - keyword: " + keyword + ", status: " + status + ", itemId: " + itemId);
            System.out.println("总记录: " + stats.get("totalCount"));
            System.out.println("已缴: " + stats.get("paidCount"));
            System.out.println("未缴: " + stats.get("unpaidCount"));
            System.out.println("逾期: " + stats.get("overdueCount"));
            System.out.println("费用类型数: " + feeTypeStats.size());
            System.out.println("================================");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ 获取筛选统计失败: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return stats;
    }
}
