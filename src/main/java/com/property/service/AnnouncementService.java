package com.property.service;

import com.property.dao.AnnouncementDao;
import com.property.entity.Announcement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告服务层
 */
public class AnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementService.class);
    private final AnnouncementDao announcementDao = new AnnouncementDao();

    // ========== 🔥 业主端专用方法 ==========

    /**
     * ✅ 获取已发布的公告列表（业主端）
     */
    public List<Announcement> getPublishedAnnouncements(String announcementType, int pageNum, int pageSize) {
        logger.info("========== 业主端查询已发布公告 ==========");
        logger.info("类型: {}, 页码: {}, 每页: {}", announcementType, pageNum, pageSize);

        try {
            // 🔥 调用 DAO 的业主端方法
            List<Announcement> list = announcementDao.findByPageForOwner(pageNum, pageSize, announcementType);
            logger.info("✅ 查询成功，共 {} 条", list.size());
            return list;
        } catch (Exception e) {
            logger.error("❌ 查询已发布公告失败", e);
            throw new RuntimeException("查询公告失败", e);
        }
    }

    /**
     * ✅ 获取已发布公告总数（业主端）
     */
    public int getPublishedAnnouncementCount(String announcementType) {
        logger.info("========== 查询已发布公告总数 ==========");
        logger.info("类型: {}", announcementType);

        try {
            // 🔥 调用 DAO 的业主端统计方法
            long count = announcementDao.countForOwner(announcementType);
            logger.info("✅ 总数: {}", count);
            return (int) count;
        } catch (Exception e) {
            logger.error("❌ 查询公告总数失败", e);
            throw new RuntimeException("查询公告总数失败", e);
        }
    }

    /**
     * ✅ 获取公告详情并增加浏览次数（业主端）
     */
    public Announcement getAnnouncementDetailAndIncreaseView(int announcementId) {
        logger.info("========== 查询公告详情并增加浏览次数 ==========");
        logger.info("公告ID: {}", announcementId);

        try {
            // 1. 查询详情
            Announcement announcement = announcementDao.findById(announcementId);

            if (announcement == null) {
                logger.warn("❌ 公告不存在: {}", announcementId);
                return null;
            }

            // 2. 增加浏览次数
            try {
                announcementDao.increaseViewCount(announcementId);
                logger.info("✅ 浏览次数已增加");
                // 更新对象中的浏览次数
                announcement.setViewCount(announcement.getViewCount() + 1);
            } catch (Exception e) {
                logger.warn("⚠️ 浏览次数更新失败，但不影响主流程", e);
            }

            return announcement;

        } catch (Exception e) {
            logger.error("❌ 查询公告详情失败", e);
            throw new RuntimeException("查询公告详情失败", e);
        }
    }
// 🔥🔥🔥 新增：带关键词搜索的方法 🔥🔥🔥

    /**
     * ✅ 获取已发布的公告列表（业主端 - 支持搜索）
     */
    public List<Announcement> getPublishedAnnouncements(String announcementType, String keyword, int pageNum, int pageSize) {
        logger.info("========== 业主端查询已发布公告（带搜索） ==========");
        logger.info("类型: {}, 关键词: {}, 页码: {}, 每页: {}", announcementType, keyword, pageNum, pageSize);

        try {
            // 🔥 调用 DAO 的搜索方法
            List<Announcement> list = announcementDao.getPublishedList(announcementType, keyword, pageNum, pageSize);
            logger.info("✅ 查询成功，共 {} 条", list.size());
            return list;
        } catch (Exception e) {
            logger.error("❌ 查询已发布公告失败", e);
            throw new RuntimeException("查询公告失败", e);
        }
    }

    /**
     * ✅ 获取已发布公告总数（业主端 - 支持搜索）
     */
    public int getPublishedAnnouncementCount(String announcementType, String keyword) {
        logger.info("========== 查询已发布公告总数（带搜索） ==========");
        logger.info("类型: {}, 关键词: {}", announcementType, keyword);

        try {
            // 🔥 调用 DAO 的统计方法
            int count = announcementDao.countPublished(announcementType, keyword);
            logger.info("✅ 总数: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("❌ 查询公告总数失败", e);
            throw new RuntimeException("查询公告总数失败", e);
        }
    }

    // ========== 🔥 业主端扩展方法 ==========

    /**
     * 按类型查询公告
     */
    public List<Announcement> getAnnouncementsByType(String announcementType, int limit) {
        logger.info("按类型查询公告：type={}, limit={}", announcementType, limit);
        try {
            return announcementDao.findByType(announcementType, limit);
        } catch (Exception e) {
            logger.error("❌ 按类型查询公告失败", e);
            throw new RuntimeException("查询公告失败", e);
        }
    }

    /**
     * 查询最新公告
     */
    public List<Announcement> getLatestAnnouncements(int limit) {
        logger.info("查询最新公告：limit={}", limit);
        try {
            return announcementDao.findLatest(limit);
        } catch (Exception e) {
            logger.error("❌ 查询最新公告失败", e);
            throw new RuntimeException("查询公告失败", e);
        }
    }

    /**
     * 查询热门公告
     */
    public List<Announcement> getPopularAnnouncements(int limit) {
        logger.info("查询热门公告：limit={}", limit);
        try {
            return announcementDao.findPopular(limit);
        } catch (Exception e) {
            logger.error("❌ 查询热门公告失败", e);
            throw new RuntimeException("查询公告失败", e);
        }
    }

    /**
     * 搜索公告
     */
    public List<Announcement> searchAnnouncements(String keyword, int limit) {
        logger.info("搜索公告：keyword={}, limit={}", keyword, limit);
        try {
            return announcementDao.search(keyword, limit);
        } catch (Exception e) {
            logger.error("❌ 搜索公告失败", e);
            throw new RuntimeException("搜索公告失败", e);
        }
    }

    /**
     * 按类型统计数量
     */
    public long countByType(String announcementType) {
        logger.info("按类型统计数量：type={}", announcementType);
        try {
            return announcementDao.countByType(announcementType);
        } catch (Exception e) {
            logger.error("❌ 按类型统计数量失败", e);
            throw new RuntimeException("统计失败", e);
        }
    }

    // ========== 管理员端方法（保留原有的） ==========

    /**
     * ✅ 分页查询公告（管理员端 - 支持筛选）
     */
    public Map<String, Object> getAnnouncements(String keyword, String announcementType,
                                                String priority, Integer status,
                                                int pageNum, int pageSize) {
        logger.info("========== 管理员端查询公告 ==========");
        logger.info("关键词: {}, 类型: {}, 优先级: {}, 状态: {}, 页码: {}, 每页: {}",
                keyword, announcementType, priority, status, pageNum, pageSize);

        try {
            // 🔥 调用 DAO 的筛选方法
            List<Announcement> list = announcementDao.findByPageWithFilter(
                    pageNum, pageSize, keyword, announcementType, priority, status
            );

            // 🔥 调用 DAO 的统计方法
            long total = announcementDao.countWithFilter(keyword, announcementType, priority, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", total);

            logger.info("✅ 查询成功，共 {} 条，总数: {}", list.size(), total);
            return result;
        } catch (Exception e) {
            logger.error("❌ 查询公告失败", e);
            throw new RuntimeException("查询公告失败", e);
        }
    }

    /**
     * 获取公告详情（管理员端）
     */
    public Announcement getAnnouncementById(int announcementId) {
        logger.info("========== 查询公告详情 ==========");
        logger.info("公告ID: {}", announcementId);

        try {
            Announcement announcement = announcementDao.findById(announcementId);
            if (announcement == null) {
                logger.warn("❌ 公告不存在: {}", announcementId);
            }
            return announcement;
        } catch (Exception e) {
            logger.error("❌ 查询公告详情失败", e);
            throw new RuntimeException("查询公告详情失败", e);
        }
    }

    /**
     * 添加公告
     */
    public boolean addAnnouncement(Announcement announcement) {
        logger.info("========== 添加公告 ==========");
        logger.info("标题: {}", announcement.getTitle());

        try {
            int rows = announcementDao.insert(announcement);
            if (rows > 0) {
                logger.info("✅ 添加公告成功");
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("❌ 添加公告失败", e);
            throw new RuntimeException("添加公告失败", e);
        }
    }

    /**
     * 更新公告
     */
    public boolean updateAnnouncement(Announcement announcement) {
        logger.info("========== 更新公告 ==========");
        logger.info("公告ID: {}, 标题: {}", announcement.getAnnouncementId(), announcement.getTitle());

        try {
            int rows = announcementDao.update(announcement);
            if (rows > 0) {
                logger.info("✅ 更新公告成功");
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("❌ 更新公告失败", e);
            throw new RuntimeException("更新公告失败", e);
        }
    }

    /**
     * 删除公告
     */
    public boolean deleteAnnouncement(int announcementId) {
        logger.info("========== 删除公告 ==========");
        logger.info("公告ID: {}", announcementId);

        try {
            int rows = announcementDao.delete(announcementId);
            if (rows > 0) {
                logger.info("✅ 删除公告成功");
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("❌ 删除公告失败", e);
            throw new RuntimeException("删除公告失败", e);
        }
    }

    /**
     * 更新公告状态
     */
    public boolean updateAnnouncementStatus(int announcementId, int status) {
        logger.info("========== 更新公告状态 ==========");
        logger.info("公告ID: {}, 状态: {}", announcementId, status);

        try {
            // 先查询公告
            Announcement announcement = announcementDao.findById(announcementId);
            if (announcement == null) {
                logger.warn("❌ 公告不存在: {}", announcementId);
                return false;
            }

            // 更新状态
            announcement.setStatus(status);
            int rows = announcementDao.update(announcement);

            if (rows > 0) {
                logger.info("✅ 更新公告状态成功");
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("❌ 更新公告状态失败", e);
            throw new RuntimeException("更新公告状态失败", e);
        }
    }

    /**
     * 批量更新状态
     */
    public boolean batchUpdateStatus(List<Integer> ids, int status) {
        logger.info("========== 批量更新公告状态 ==========");
        logger.info("公告数量: {}, 状态: {}", ids.size(), status);

        try {
            int successCount = 0;
            for (Integer id : ids) {
                if (updateAnnouncementStatus(id, status)) {
                    successCount++;
                }
            }

            logger.info("✅ 批量更新完成，成功: {}/{}", successCount, ids.size());
            return successCount == ids.size();
        } catch (Exception e) {
            logger.error("❌ 批量更新状态失败", e);
            throw new RuntimeException("批量更新状态失败", e);
        }
    }

    /**
     * 批量删除
     */
    public boolean batchDelete(List<Integer> ids) {
        logger.info("========== 批量删除公告 ==========");
        logger.info("公告数量: {}", ids.size());

        try {
            int successCount = 0;
            for (Integer id : ids) {
                if (deleteAnnouncement(id)) {
                    successCount++;
                }
            }

            logger.info("✅ 批量删除完成，成功: {}/{}", successCount, ids.size());
            return successCount == ids.size();
        } catch (Exception e) {
            logger.error("❌ 批量删除失败", e);
            throw new RuntimeException("批量删除失败", e);
        }
    }

    /**
     * ✅ 获取统计数据（修复类型名称）
     */
    public Map<String, Object> getStatistics() {
        logger.info("========== 查询公告统计数据 ==========");

        try {
            Map<String, Object> stats = new HashMap<>();

            // 统计各类型数量（修正类型名称）
            stats.put("notice", announcementDao.countByType("notice"));
            stats.put("emergency", announcementDao.countByType("emergency"));
            stats.put("payment_reminder", announcementDao.countByType("payment_reminder"));
            stats.put("maintenance", announcementDao.countByType("maintenance"));

            // 总数
            stats.put("total", announcementDao.countForOwner(null));

            logger.info("✅ 统计数据查询成功: {}", stats);
            return stats;
        } catch (Exception e) {
            logger.error("❌ 查询统计数据失败", e);
            throw new RuntimeException("查询统计数据失败", e);
        }
    }
}
