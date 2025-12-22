package com.property.dao;

import com.property.entity.House;
import com.property.util.DBUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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
        sql.append("    h.*, o.owner_name, o.phone AS owner_phone ");
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

        // 动态构建参数
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

        return queryForLong(sql.toString(), params.toArray());
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

            while (rs.next()) {
                map.put(rs.getString("house_status"), rs.getLong("cnt"));
            }
        } catch (SQLException e) {
            logger.error("统计失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return map;
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
        house.setSaleStatus(rs.getString("sale_status"));
        house.setOwnerId(rs.getString("owner_id"));
        house.setCreateTime(rs.getTimestamp("create_time"));
        house.setUpdateTime(rs.getTimestamp("update_time"));
        return house;
    }
}
