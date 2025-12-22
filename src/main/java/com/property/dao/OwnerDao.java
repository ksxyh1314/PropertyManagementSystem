package com.property.dao;

import com.property.entity.Owner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 业主DAO
 */
public class OwnerDao extends BaseDao {

    /**
     * 根据业主ID查询
     */
    public Owner findById(String ownerId) {
        String sql = "SELECT o.*, h.building_no, h.unit_no, h.floor " +
                "FROM owners o " +
                "LEFT JOIN houses h ON o.house_id = h.house_id " +
                "WHERE o.owner_id = ?";
        return queryOne(sql, this::mapOwner, ownerId);
    }

    /**
     * 查询所有业主
     */
    public List<Owner> findAll() {
        String sql = "SELECT o.*, h.building_no, h.unit_no, h.floor " +
                "FROM owners o " +
                "LEFT JOIN houses h ON o.house_id = h.house_id " +
                "ORDER BY o.register_date DESC";
        return query(sql, this::mapOwner);
    }

    /**
     * 分页查询业主
     */
    public List<Owner> findByPage(int pageNum, int pageSize, String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY o.register_date DESC) AS row_num, ");
        sql.append("    o.*, h.building_no, h.unit_no, h.floor ");
        sql.append("  FROM owners o ");
        sql.append("  LEFT JOIN houses h ON o.house_id = h.house_id ");
        sql.append("  WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("  AND (o.owner_id LIKE ? OR o.owner_name LIKE ? OR o.phone LIKE ? OR o.house_id LIKE ?) ");
        }

        sql.append(") AS temp ");
        sql.append("WHERE row_num BETWEEN ? AND ?");

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeKeyword = "%" + keyword + "%";
            return query(sql.toString(), this::mapOwner,
                    likeKeyword, likeKeyword, likeKeyword, likeKeyword, start, end);
        } else {
            return query(sql.toString(), this::mapOwner, start, end);
        }
    }

    /**
     * 统计业主总数
     */
    public long count(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM owners o WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (o.owner_id LIKE ? OR o.owner_name LIKE ? OR o.phone LIKE ? OR o.house_id LIKE ?)");
            String likeKeyword = "%" + keyword + "%";
            return queryForLong(sql.toString(), likeKeyword, likeKeyword, likeKeyword, likeKeyword);
        }

        return queryForLong(sql.toString());
    }

    /**
     * 根据房屋ID查询业主
     */
    public Owner findByHouseId(String houseId) {
        String sql = "SELECT o.*, h.building_no, h.unit_no, h.floor " +
                "FROM owners o " +
                "LEFT JOIN houses h ON o.house_id = h.house_id " +
                "WHERE o.house_id = ?";
        return queryOne(sql, this::mapOwner, houseId);
    }

    /**
     * 添加业主
     */
    public int insert(Owner owner) {
        String sql = "INSERT INTO owners (owner_id, owner_name, phone, id_card, house_id, " +
                "email, member_count, register_date, remark) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return update(sql,
                owner.getOwnerId(),
                owner.getOwnerName(),
                owner.getPhone(),
                owner.getIdCard(),
                owner.getHouseId(),
                owner.getEmail(),
                owner.getMemberCount() != null ? owner.getMemberCount() : 1,
                owner.getRegisterDate(),
                owner.getRemark()
        );
    }

    /**
     * 更新业主信息
     */
    public int update(Owner owner) {
        String sql = "UPDATE owners SET owner_name = ?, phone = ?, id_card = ?, " +
                "house_id = ?, email = ?, member_count = ?, remark = ?, update_time = GETDATE() " +
                "WHERE owner_id = ?";
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
        return update(sql, ownerId);
    }

    /**
     * 检查业主ID是否存在
     */
    public boolean existsById(String ownerId) {
        String sql = "SELECT COUNT(*) FROM owners WHERE owner_id = ?";
        return queryForLong(sql, ownerId) > 0;
    }

    /**
     * 检查身份证号是否存在
     */
    public boolean existsByIdCard(String idCard) {
        String sql = "SELECT COUNT(*) FROM owners WHERE id_card = ?";
        return queryForLong(sql, idCard) > 0;
    }

    /**
     * 生成业主ID（根据楼栋号生成）
     */
    public String generateOwnerId(String buildingNo) {
        // 格式：楼栋号(4位) + 流水号(4位)
        String sql = "SELECT MAX(CAST(RIGHT(owner_id, 4) AS INT)) " +
                "FROM owners WHERE LEFT(owner_id, 4) = ?";
        Long maxSeq = queryForLong(sql, String.format("%04d", Integer.parseInt(buildingNo)));
        int nextSeq = maxSeq != null ? maxSeq.intValue() + 1 : 1;
        return String.format("%04d%04d", Integer.parseInt(buildingNo), nextSeq);
    }

    /**
     * 查询业主欠费汇总（调用视图）
     */
    public List<Owner> findArrearsOwners() {
        String sql = "SELECT * FROM view_owner_arrears_summary ORDER BY total_arrears DESC";
        return query(sql, this::mapOwnerArrears);
    }

    /**
     * 映射结果集到Owner对象
     */
    private Owner mapOwner(ResultSet rs) throws SQLException {
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
    }

    /**
     * 映射欠费汇总结果集
     */
    private Owner mapOwnerArrears(ResultSet rs) throws SQLException {
        Owner owner = new Owner();
        owner.setOwnerId(rs.getString("owner_id"));
        owner.setOwnerName(rs.getString("owner_name"));
        owner.setPhone(rs.getString("phone"));
        owner.setHouseId(rs.getString("house_id"));
        owner.setUnpaidCount(rs.getInt("unpaid_count"));
        owner.setTotalArrears(rs.getBigDecimal("total_arrears"));
        owner.setEarliestDueDate(rs.getDate("earliest_due_date"));
        return owner;
    }
}
