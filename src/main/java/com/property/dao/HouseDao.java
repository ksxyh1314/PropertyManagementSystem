package com.property.dao;

import com.property.entity.House;
import com.property.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房屋DAO
 */
public class HouseDao extends BaseDao {

    /**
     * 根据房屋ID查询
     */
    public House findById(String houseId) {
        String sql = "SELECT h.*, o.owner_name, o.phone AS owner_phone " +
                "FROM houses h " +
                "LEFT JOIN owners o ON h.owner_id = o.owner_id " +
                "WHERE h.house_id = ?";
        return queryOne(sql, this::mapHouse, houseId);
    }

    /**
     * 查询所有房屋
     */
    public List<House> findAll() {
        String sql = "SELECT h.*, o.owner_name, o.phone AS owner_phone " +
                "FROM houses h " +
                "LEFT JOIN owners o ON h.owner_id = o.owner_id " +
                "ORDER BY h.building_no, h.unit_no, h.floor";
        return query(sql, this::mapHouse);
    }

    /**
     * 分页查询房屋
     */
    public List<House> findByPage(int pageNum, int pageSize, String keyword, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY h.building_no, h.unit_no, h.floor) AS row_num, ");
        sql.append("    h.house_id, h.building_no, h.unit_no, h.floor, h.layout, h.area, ");
        sql.append("    h.price_per_sqm, h.house_status, h.sale_status, h.owner_id, ");
        sql.append("    h.create_time, h.update_time, o.owner_name, o.phone AS owner_phone ");
        sql.append("  FROM houses h ");
        sql.append("  LEFT JOIN owners o ON h.owner_id = o.owner_id ");
        sql.append("  WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        if (hasKeyword) {
            sql.append("  AND (h.house_id LIKE ? OR h.building_no LIKE ? OR o.owner_name LIKE ?) ");
        }
        if (hasStatus) {
            sql.append("  AND h.house_status = ? ");
        }

        sql.append(") AS temp ");
        sql.append("WHERE row_num BETWEEN ? AND ?");

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;

        List<Object> params = new java.util.ArrayList<>();
        if (hasKeyword) {
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (hasStatus) {
            params.add(status);
        }
        params.add(start);
        params.add(end);

        logger.info("分页查询 SQL: {}", sql);
        logger.info("参数: {}", params);

        return query(sql.toString(), this::mapHouse, params.toArray());
    }

    /**
     * 统计房屋总数
     */
    public long count(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM houses h ");
        sql.append("LEFT JOIN owners o ON h.owner_id = o.owner_id WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        List<Object> params = new java.util.ArrayList<>();

        if (hasKeyword) {
            sql.append("AND (h.house_id LIKE ? OR h.building_no LIKE ? OR o.owner_name LIKE ?) ");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (hasStatus) {
            sql.append("AND h.house_status = ? ");
            params.add(status);
        }

        long result = queryForLong(sql.toString(), params.toArray());
        logger.info("统计房屋数量 - SQL: {}, 参数: {}, 结果: {}", sql, params, result);
        return result;
    }

    /**
     * 根据楼栋查询房屋
     */
    public List<House> findByBuilding(String buildingNo) {
        String sql = "SELECT h.*, o.owner_name, o.phone AS owner_phone " +
                "FROM houses h " +
                "LEFT JOIN owners o ON h.owner_id = o.owner_id " +
                "WHERE h.building_no = ? " +
                "ORDER BY h.unit_no, h.floor";
        return query(sql, this::mapHouse, buildingNo);
    }

    /**
     * 查询空置房屋
     */
    public List<House> findVacantHouses() {
        String sql = "SELECT * FROM houses WHERE house_status = 'vacant' " +
                "ORDER BY building_no, unit_no, floor";
        return query(sql, this::mapHouse);
    }

    /**
     * 添加房屋
     */
    public int insert(House house) {
        String sql = "INSERT INTO houses (house_id, building_no, unit_no, floor, layout, " +
                "area, price_per_sqm, house_status, sale_status, owner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return update(sql,
                house.getHouseId(),
                house.getBuildingNo(),
                house.getUnitNo(),
                house.getFloor(),
                house.getLayout(),
                house.getArea(),
                house.getPricePerSqm(),
                house.getHouseStatus() != null ? house.getHouseStatus() : "vacant",
                house.getSaleStatus() != null ? house.getSaleStatus() : "for_sale",
                house.getOwnerId()
        );
    }

    /**
     * 更新房屋信息
     */
    public int update(House house) {
        String sql = "UPDATE houses SET building_no = ?, unit_no = ?, floor = ?, layout = ?, " +
                "area = ?, price_per_sqm = ?, house_status = ?, sale_status = ?, " +
                "owner_id = ?, update_time = GETDATE() " +
                "WHERE house_id = ?";
        return update(sql,
                house.getBuildingNo(),
                house.getUnitNo(),
                house.getFloor(),
                house.getLayout(),
                house.getArea(),
                house.getPricePerSqm(),
                house.getHouseStatus(),
                house.getSaleStatus(),
                house.getOwnerId(),
                house.getHouseId()
        );
    }

    /**
     * 分配业主
     */
    public int assignOwner(String houseId, String ownerId) {
        String sql = "UPDATE houses SET owner_id = ?, house_status = 'occupied', " +
                "update_time = GETDATE() WHERE house_id = ?";
        return update(sql, ownerId, houseId);
    }

    /**
     * 删除房屋
     */
    public int delete(String houseId) {
        String sql = "DELETE FROM houses WHERE house_id = ?";
        return update(sql, houseId);
    }

    /**
     * 检查房屋ID是否存在
     */
    public boolean existsById(String houseId) {
        String sql = "SELECT COUNT(*) FROM houses WHERE house_id = ?";
        return queryForLong(sql, houseId) > 0;
    }

    /**
     * 统计各状态房屋数量
     */
    public java.util.Map<String, Long> countByStatus() {
        String sql = "SELECT house_status, COUNT(*) AS cnt FROM houses GROUP BY house_status";
        java.util.Map<String, Long> map = new java.util.HashMap<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            logger.info("执行 SQL: {}", sql);

            while (rs.next()) {
                String status = rs.getString("house_status");
                long count = rs.getLong("cnt");
                map.put(status, count);
                logger.info("房屋状态: {}, 数量: {}", status, count);
            }

            logger.info("统计各状态房屋数量结果: {}", map);
        } catch (SQLException e) {
            logger.error("统计失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return map;
    }

    /**
     * 根据条件查询房屋（不分页,用于导出）
     */
    public List<House> findByCondition(String keyword, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT h.*, o.owner_name, o.phone AS owner_phone ");
        sql.append("FROM houses h ");
        sql.append("LEFT JOIN owners o ON h.owner_id = o.owner_id ");
        sql.append("WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        List<Object> params = new java.util.ArrayList<>();

        if (hasKeyword) {
            sql.append("AND (h.house_id LIKE ? OR h.building_no LIKE ? OR o.owner_name LIKE ?) ");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (hasStatus) {
            sql.append("AND h.house_status = ? ");
            params.add(status);
        }

        sql.append("ORDER BY h.building_no, h.unit_no, h.floor");

        logger.info("根据条件查询房屋 SQL: {}", sql);
        logger.info("参数: {}", params);

        return query(sql.toString(), this::mapHouse, params.toArray());
    }

    /**
     * 根据ID列表查询房屋（用于导出选中数据）
     */
    public List<House> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT h.*, o.owner_name, o.phone AS owner_phone ");
        sql.append("FROM houses h ");
        sql.append("LEFT JOIN owners o ON h.owner_id = o.owner_id ");
        sql.append("WHERE h.house_id IN (");

        // 构建 IN 子句的占位符
        for (int i = 0; i < ids.size(); i++) {
            sql.append("?");
            if (i < ids.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(") ORDER BY h.building_no, h.unit_no, h.floor");

        logger.info("根据ID列表查询房屋 SQL: {}", sql);
        logger.info("参数: {}", ids);

        return query(sql.toString(), this::mapHouse, ids.toArray());
    }

    /**
     * 🔧 查询所有已入住的房屋（用于生成账单）
     */
    public List<House> findOccupiedHouses() {
        String sql = "SELECT h.*, o.owner_name, o.phone AS owner_phone " +
                "FROM houses h " +
                "LEFT JOIN owners o ON h.owner_id = o.owner_id " +
                "WHERE h.house_status = 'occupied' AND h.owner_id IS NOT NULL " +
                "ORDER BY h.building_no, h.unit_no, h.floor";

        logger.info("查询已入住房屋 SQL: {}", sql);

        try {
            List<House> houses = query(sql, this::mapHouse);
            logger.info("查询到 {} 套已入住房屋", houses.size());
            return houses;
        } catch (Exception e) {
            logger.error("查询已入住房屋失败", e);
            throw new RuntimeException("查询已入住房屋失败", e);
        }
    }

    /**
     * 映射结果集到House对象
     */
    private House mapHouse(ResultSet rs) throws SQLException {
        House house = new House();
        house.setHouseId(rs.getString("house_id"));
        house.setBuildingNo(rs.getString("building_no"));
        house.setUnitNo(rs.getString("unit_no"));
        house.setFloor(rs.getString("floor"));
        house.setLayout(rs.getString("layout"));
        house.setArea(rs.getBigDecimal("area"));
        house.setPricePerSqm(rs.getBigDecimal("price_per_sqm"));
        house.setHouseStatus(rs.getString("house_status"));
        house.setOwnerId(rs.getString("owner_id"));
        house.setCreateTime(rs.getTimestamp("create_time"));
        house.setUpdateTime(rs.getTimestamp("update_time"));

        // ✅ 添加 sale_status
        try {
            house.setSaleStatus(rs.getString("sale_status"));
        } catch (SQLException e) {
            // 如果查询中没有 sale_status 字段，忽略
        }

        // ✅ 关联查询的业主信息（重要！前端需要显示）
        try {
            house.setOwnerName(rs.getString("owner_name"));
            house.setOwnerPhone(rs.getString("owner_phone"));
        } catch (SQLException e) {
            // 如果查询中没有关联业主信息，忽略
        }

        return house;
    }
    /**
     * 获取房屋总数
     */
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM houses";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("获取房屋总数失败", e);
        }
        return 0;
    }

    /**
     * 获取已入住房屋数量
     */
    public int getOccupiedCount() {
        String sql = "SELECT COUNT(*) FROM houses WHERE house_status = 'occupied'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("获取已入住房屋数量失败", e);
        }
        return 0;
    }

    /**
     * 获取空置房屋数量
     */
    public int getVacantCount() {
        String sql = "SELECT COUNT(*) FROM houses WHERE house_status = 'vacant'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("获取空置房屋数量失败", e);
        }
        return 0;
    }
    /**
     * 根据业主ID查询房屋列表（业主端使用）
     * @param ownerId 业主ID
     * @return 房屋列表
     */
    public List<House> findByOwnerId(String ownerId) {
        logger.info(">>> DAO: 查询业主房屋，ownerId: {}", ownerId);

        String sql = "SELECT h.*, o.owner_name, o.phone AS owner_phone " +
                "FROM houses h " +
                "LEFT JOIN owners o ON h.owner_id = o.owner_id " +
                "WHERE h.owner_id = ? " +
                "ORDER BY h.building_no, h.unit_no, h.floor";

        try {
            List<House> houses = query(sql, this::mapHouse, ownerId);
            logger.info("✅ DAO: 查询到 {} 套房屋", houses.size());
            return houses;
        } catch (Exception e) {
            logger.error("❌ DAO 查询业主房屋失败", e);
            return new ArrayList<>();
        }
    }
    /**
     * 获取楼栋列表（从 house_id 动态提取）
     */
    public List<Map<String, Object>> listBuildings() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // ✅ 从 house_id 提取前2位作为楼栋编号
            String sql =
                    "SELECT " +
                            "    LEFT(house_id, 2) AS building_id, " +
                            "    LEFT(house_id, 2) + N'号楼' AS building_name, " +
                            "    COUNT(*) AS total_houses, " +
                            "    SUM(CASE WHEN house_status = 'occupied' THEN 1 ELSE 0 END) AS occupied_count " +
                            "FROM houses " +
                            "GROUP BY LEFT(house_id, 2) " +
                            "ORDER BY LEFT(house_id, 2)";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            List<Map<String, Object>> buildings = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> building = new HashMap<>();
                building.put("buildingId", rs.getString("building_id"));
                building.put("buildingName", rs.getString("building_name"));
                building.put("totalHouses", rs.getInt("total_houses"));
                building.put("occupiedCount", rs.getInt("occupied_count"));
                buildings.add(building);
            }

            return buildings;

        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }

    /**
     * 统计已入住房屋数量
     */
    public int countOccupied(String buildingId) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql =
                    "SELECT COUNT(*) AS cnt " +
                            "FROM houses " +
                            "WHERE house_status = 'occupied' " +
                            "  AND owner_id IS NOT NULL";

            // ✅ 如果指定楼栋，从 house_id 前2位匹配
            if (buildingId != null && !buildingId.trim().isEmpty()) {
                sql += " AND LEFT(house_id, 2) = ?";
            }

            stmt = conn.prepareStatement(sql);

            if (buildingId != null && !buildingId.trim().isEmpty()) {
                stmt.setString(1, buildingId);
            }

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("cnt");
            }

            return 0;

        } finally {
            DBUtil.close(rs, stmt, conn);
        }
    }
    /**
     * ✅ 新增：根据楼栋ID查询已入住的房屋 (适配 PaymentService)
     * 逻辑：匹配 house_id 的前2位作为楼栋ID，且状态为 occupied
     */
    public List<House> findByBuildingId(String buildingId) {
        // 注意：这里使用 LEFT(h.house_id, 2) 是因为你的 listBuildings 方法就是这样定义楼栋ID的
        String sql = "SELECT h.*, o.owner_name, o.phone AS owner_phone " +
                "FROM houses h " +
                "LEFT JOIN owners o ON h.owner_id = o.owner_id " +
                "WHERE LEFT(h.house_id, 2) = ? " +
                "AND h.house_status = 'occupied' " +
                "ORDER BY h.unit_no, h.floor";

        logger.info("根据楼栋ID查询已入住房屋 SQL: {}, 参数: {}", sql, buildingId);

        return query(sql, this::mapHouse, buildingId);
    }

}
