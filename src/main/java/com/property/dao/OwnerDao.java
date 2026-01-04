package com.property.dao;

import com.property.entity.Owner;
import com.property.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业主数据访问层
 */
public class OwnerDao extends BaseDao {
    private static final Logger logger = LoggerFactory.getLogger(OwnerDao.class);

    /**
     * RowMapper：将 ResultSet 映射为 Owner 对象
     */
    private final RowMapper<Owner> ownerRowMapper = rs -> {
        Owner owner = new Owner();
        owner.setOwnerId(rs.getString("owner_id"));
        owner.setOwnerName(rs.getString("owner_name"));
        owner.setPhone(rs.getString("phone"));
        owner.setIdCard(rs.getString("id_card"));
        owner.setHouseId(rs.getString("house_id"));
        owner.setEmail(rs.getString("email"));
        owner.setMemberCount(rs.getInt("member_count"));
        owner.setRegisterDate(rs.getDate("register_date"));
        owner.setRemark(rs.getString("remark"));
        owner.setCreateTime(rs.getTimestamp("create_time"));
        owner.setUpdateTime(rs.getTimestamp("update_time"));
        return owner;
    };

    /**
     * 根据ID查询业主
     */
    public Owner findById(String ownerId) {
        String sql = "SELECT * FROM owners WHERE owner_id = ?";
        return queryOne(sql, ownerRowMapper, ownerId);
    }

    /**
     * 查询所有业主
     */
    public List<Owner> findAll() {
        String sql = "SELECT * FROM owners ORDER BY register_date DESC";
        return query(sql, ownerRowMapper);
    }

    /**
     * 分页查询业主
     */
    public List<Owner> findByPage(int pageNum, int pageSize, String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( ");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY register_date DESC) AS row_num, ");
        sql.append("    owner_id, owner_name, phone, id_card, house_id, email, ");
        sql.append("    member_count, register_date, remark, create_time, update_time ");
        sql.append("  FROM owners ");
        sql.append("  WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (owner_id LIKE ? OR owner_name LIKE ? OR phone LIKE ? OR house_id LIKE ?) ");
            String keywordPattern = "%" + keyword + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
        }

        sql.append(") AS temp ");
        sql.append("WHERE row_num BETWEEN ? AND ?");

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;
        params.add(start);
        params.add(end);

        logger.info("分页查询业主 - pageNum: {}, pageSize: {}, keyword: {}", pageNum, pageSize, keyword);

        return query(sql.toString(), ownerRowMapper, params.toArray());
    }

    /**
     * 统计业主数量
     */
    public int count(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM owners WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (owner_id LIKE ? OR owner_name LIKE ? OR phone LIKE ? OR house_id LIKE ?)");
            String keywordPattern = "%" + keyword + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
        }

        Long count = queryForLong(sql.toString(), params.toArray());
        return count != null ? count.intValue() : 0;
    }

    /**
     * 根据房屋ID查询业主
     */
    public Owner findByHouseId(String houseId) {
        String sql = "SELECT * FROM owners WHERE house_id = ?";
        return queryOne(sql, ownerRowMapper, houseId);
    }

    /**
     * 根据关键字搜索业主（不分页）
     */
    public List<Owner> findByKeyword(String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM owners WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (owner_id LIKE ? OR owner_name LIKE ? OR phone LIKE ? OR house_id LIKE ?) ");
            String keywordPattern = "%" + keyword + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
            params.add(keywordPattern);
        }

        sql.append(" ORDER BY register_date DESC");

        return query(sql.toString(), ownerRowMapper, params.toArray());
    }

    /**
     * 验证身份证号是否已存在
     */
    public boolean existsByIdCard(String idCard) {
        String sql = "SELECT COUNT(*) FROM owners WHERE id_card = ?";
        Long count = queryForLong(sql, idCard);
        return count != null && count > 0;
    }

    /**
     * 验证手机号是否已存在
     */
    public boolean existsByPhone(String phone) {
        String sql = "SELECT COUNT(*) FROM owners WHERE phone = ?";
        Long count = queryForLong(sql, phone);
        return count != null && count > 0;
    }
    /**
     * 🔥 生成业主编号（根据房屋编号）
     * 格式: BBBBNNNN
     * - BBBB: 楼栋号（4位，从房屋编号提取）
     * - NNNN: 该楼栋的业主流水号（4位，从0001开始）
     *
     * @param houseId 房屋编号（7位，如 0210602）
     * @return 业主编号（8位，如 00020001）
     */
    public String generateOwnerId(String houseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // 🔥 从房屋编号中提取楼栋号（前2位）
            if (houseId == null || houseId.length() < 2) {
                logger.error("房屋编号格式错误: {}", houseId);
                throw new IllegalArgumentException("房屋编号格式错误");
            }

            String buildingNo = houseId.substring(0, 2);

            // 🔥 扩展为4位楼栋号（前面补0）
            String buildingPart = String.format("%04d", Integer.parseInt(buildingNo));

            logger.info("提取楼栋号: {} → 扩展为: {}", buildingNo, buildingPart);

            // 🔥 查询该楼栋已有的最大流水号
            String sql = "SELECT MAX(CAST(SUBSTRING(owner_id, 5, 4) AS INT)) AS max_seq " +
                    "FROM owners " +
                    "WHERE owner_id LIKE ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buildingPart + "%");
            rs = pstmt.executeQuery();

            int nextSeq = 1;
            if (rs.next()) {
                int maxSeq = rs.getInt("max_seq");
                if (!rs.wasNull() && maxSeq > 0) {
                    nextSeq = maxSeq + 1;
                }
            }

            // 🔥 生成新的业主编号
            String seqPart = String.format("%04d", nextSeq);
            String newOwnerId = buildingPart + seqPart;

            logger.info("生成业主编号: {} (楼栋: {}, 流水号: {})",
                    newOwnerId, buildingNo, nextSeq);

            return newOwnerId;

        } catch (SQLException e) {
            logger.error("生成业主编号失败", e);
            throw new RuntimeException("生成业主编号失败: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            logger.error("房屋编号格式错误: {}", houseId, e);
            throw new IllegalArgumentException("房屋编号格式错误: " + houseId, e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }


    /**
     * 插入业主
     */
    public int insert(Owner owner) {
        String sql = "INSERT INTO owners (owner_id, owner_name, phone, id_card, house_id, " +
                "email, member_count, register_date, remark) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        logger.info("插入业主: {} - {}", owner.getOwnerId(), owner.getOwnerName());

        return update(sql,
                owner.getOwnerId(),
                owner.getOwnerName(),
                owner.getPhone(),
                owner.getIdCard(),
                owner.getHouseId(),
                owner.getEmail(),
                owner.getMemberCount(),
                owner.getRegisterDate(),
                owner.getRemark()
        );
    }

    /**
     * 更新业主
     */
    public int update(Owner owner) {
        String sql = "UPDATE owners SET owner_name = ?, phone = ?, id_card = ?, " +
                "house_id = ?, email = ?, member_count = ?, remark = ?, " +
                "update_time = GETDATE() " +
                "WHERE owner_id = ?";

        logger.info("更新业主: {} - {}", owner.getOwnerId(), owner.getOwnerName());

        return update(sql,
                owner.getOwnerName(),
                owner.getPhone(),
                owner.getIdCard(),
                owner.getHouseId(),
                owner.getEmail(),
                owner.getMemberCount(),
                owner.getRemark(),
                owner.getOwnerId()
        );
    }

    /**
     * 删除业主
     */
    public int delete(String ownerId) {
        String sql = "DELETE FROM owners WHERE owner_id = ?";
        logger.info("删除业主: {}", ownerId);
        return update(sql, ownerId);
    }

    /**
     * 查询欠费业主
     */
    public List<Owner> findArrearsOwners() {
        String sql = "SELECT DISTINCT o.* FROM owners o " +
                "INNER JOIN payment_records pr ON o.owner_id = pr.owner_id " +
                "WHERE pr.payment_status IN ('unpaid', 'overdue') " +
                "ORDER BY o.register_date DESC";

        return query(sql, ownerRowMapper);
    }

    /**
     * 统计本月新增业主数
     */
    public int countMonthlyNew() {
        String sql = "SELECT COUNT(*) FROM owners " +
                "WHERE YEAR(register_date) = YEAR(GETDATE()) " +
                "AND MONTH(register_date) = MONTH(GETDATE())";
        Long count = queryForLong(sql);
        return count != null ? count.intValue() : 0;
    }

    /**
     * 根据身份证号查询业主
     */
    public Owner findByIdCard(String idCard) {
        String sql = "SELECT * FROM owners WHERE id_card = ?";
        return queryOne(sql, ownerRowMapper, idCard);
    }

    /**
     * 根据手机号查询业主
     */
    public Owner findByPhone(String phone) {
        String sql = "SELECT * FROM owners WHERE phone = ?";
        return queryOne(sql, ownerRowMapper, phone);
    }

    /**
     * 批量插入业主
     */
    public int batchInsert(List<Owner> owners) {
        String sql = "INSERT INTO owners (owner_id, owner_name, phone, id_card, house_id, " +
                "email, member_count, register_date, remark) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> paramsList = new ArrayList<>();
        for (Owner owner : owners) {
            Object[] params = new Object[]{
                    owner.getOwnerId(),
                    owner.getOwnerName(),
                    owner.getPhone(),
                    owner.getIdCard(),
                    owner.getHouseId(),
                    owner.getEmail(),
                    owner.getMemberCount(),
                    owner.getRegisterDate(),
                    owner.getRemark()
            };
            paramsList.add(params);
        }

        int[] results = batchUpdate(sql, paramsList);
        logger.info("批量插入业主成功，共 {} 条", results.length);
        return results.length;
    }

    /**
     * 统计各楼栋业主数量
     */
    public List<Object[]> countByBuilding() {
        String sql = "SELECT h.building_no, COUNT(DISTINCT o.owner_id) AS owner_count " +
                "FROM houses h " +
                "LEFT JOIN owners o ON h.owner_id = o.owner_id " +
                "GROUP BY h.building_no " +
                "ORDER BY h.building_no";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Object[]> results = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getString("building_no");
                row[1] = rs.getInt("owner_count");
                results.add(row);
            }

            return results;

        } catch (SQLException e) {
            logger.error("统计楼栋业主数失败", e);
            throw new RuntimeException("统计楼栋业主数失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 更新业主密码（通过 UserDao 实现）
     */
    public boolean updatePassword(String ownerId, String encryptedPassword) {
        // 注意：密码存储在 users 表中，这里调用 UserDao
        // 或者在这里直接更新 users 表
        String sql = "UPDATE users SET password = ?, update_time = GETDATE() " +
                "WHERE username = ? AND user_role = 'owner'";

        int rows = update(sql, encryptedPassword, ownerId);
        logger.info("更新业主密码: {} - {}", ownerId, rows > 0 ? "成功" : "失败");
        return rows > 0;
    }

    /**
     * 根据业主ID列表查询业主
     */
    public List<Owner> findByIds(List<String> ownerIds) {
        if (ownerIds == null || ownerIds.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM owners WHERE owner_id IN (");
        for (int i = 0; i < ownerIds.size(); i++) {
            sql.append("?");
            if (i < ownerIds.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(") ORDER BY register_date DESC");

        return query(sql.toString(), ownerRowMapper, ownerIds.toArray());
    }

    /**
     * 查询指定日期范围内注册的业主
     */
    public List<Owner> findByRegisterDateRange(Date startDate, Date endDate) {
        String sql = "SELECT * FROM owners " +
                "WHERE register_date BETWEEN ? AND ? " +
                "ORDER BY register_date DESC";

        return query(sql, ownerRowMapper, startDate, endDate);
    }

    /**
     * 统计指定楼栋的业主数量
     */
    public int countByBuilding(String buildingNo) {
        String sql = "SELECT COUNT(*) FROM owners o " +
                "INNER JOIN houses h ON o.house_id = h.house_id " +
                "WHERE h.building_no = ?";

        Long count = queryForLong(sql, buildingNo);
        return count != null ? count.intValue() : 0;
    }

    /**
     * 查询指定楼栋的所有业主
     */
    public List<Owner> findByBuilding(String buildingNo) {
        String sql = "SELECT o.* FROM owners o " +
                "INNER JOIN houses h ON o.house_id = h.house_id " +
                "WHERE h.building_no = ? " +
                "ORDER BY h.unit_no, h.floor";

        return query(sql, ownerRowMapper, buildingNo);
    }
    /**
     * 获取业主总数
     */
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM owners";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("获取业主总数失败", e);
        }
        return 0;
    }
    /**
     * 检查业主是否有未缴费记录
     */
    public boolean hasUnpaidRecords(String ownerId) {
        String sql = "SELECT COUNT(*) FROM payment_records " +
                "WHERE owner_id = ? AND payment_status IN ('unpaid', 'overdue')";

        Long count = queryForLong(sql, ownerId);
        return count != null && count > 0;
    }
    /**
     * 查询业主拥有的所有房屋（通过 houses.owner_id）
     */
    public List<Map<String, Object>> findHousesByOwnerId(String ownerId) {
        // 🔥 删除了 room_no 字段
        String sql = "SELECT house_id, building_no, unit_no, floor, layout, area " +
                "FROM houses WHERE owner_id = ? ORDER BY building_no, unit_no, floor";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> houses = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ownerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> house = new HashMap<>();
                house.put("houseId", rs.getString("house_id"));
                house.put("buildingNo", rs.getString("building_no"));
                house.put("unitNo", rs.getString("unit_no"));
                house.put("floor", rs.getInt("floor"));
                // 🔥 不再读取 roomNo
                house.put("layout", rs.getString("layout"));
                house.put("area", rs.getDouble("area"));
                houses.add(house);
            }

            logger.info("查询业主 {} 的房屋，共 {} 套", ownerId, houses.size());
            return houses;

        } catch (SQLException e) {
            logger.error("查询业主房屋失败: {}", ownerId, e);
            throw new RuntimeException("查询业主房屋失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }


    /**
     * 统计业主拥有的房屋数量
     */
    public int countHousesByOwnerId(String ownerId) {
        String sql = "SELECT COUNT(*) FROM houses WHERE owner_id = ?";
        Long count = queryForLong(sql, ownerId);
        int houseCount = count != null ? count.intValue() : 0;
        logger.debug("业主 {} 拥有 {} 套房产", ownerId, houseCount);
        return houseCount;
    }

}
