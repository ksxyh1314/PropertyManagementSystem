<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>公告管理 - 智慧社区管理系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        body {
            background-color: #f5f7fa;
            font-family: 'Microsoft YaHei', sans-serif;
        }

        /* ✅ 超级美化的页面标题 */
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 0;
            margin-bottom: 30px;
            box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
            position: relative;
            overflow: hidden;
        }

        .page-header::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -10%;
            width: 400px;
            height: 400px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
        }

        .page-header::after {
            content: '';
            position: absolute;
            bottom: -30%;
            left: -5%;
            width: 300px;
            height: 300px;
            background: rgba(255, 255, 255, 0.08);
            border-radius: 50%;
        }

        .page-header-content {
            position: relative;
            z-index: 1;
            padding: 35px 0 25px;
        }

        .page-header-title {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
        }

        .page-header-icon {
            width: 50px;
            height: 50px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            font-size: 24px;
            backdrop-filter: blur(10px);
        }

        .page-header h2 {
            margin: 0;
            font-size: 28px;
            font-weight: 600;
            letter-spacing: 1px;
        }

        .page-header-desc {
            margin: 0;
            opacity: 0.95;
            font-size: 14px;
            padding-left: 65px;
        }

        .toolbar {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }

        .notice-card {
            cursor: pointer;
            transition: all 0.3s;
            border: none;
            border-left: 4px solid transparent;
            margin-bottom: 20px;
            background: white;
        }
        .notice-card:hover {
            transform: translateX(5px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.15);
        }
        .notice-card.type-emergency { border-left-color: #dc3545; }
        .notice-card.type-notice { border-left-color: #007bff; }
        .notice-card.type-payment_reminder { border-left-color: #ffc107; }
        .notice-card.type-maintenance { border-left-color: #17a2b8; }

        .priority-badge { font-size: 12px; padding: 3px 8px; }
        .priority-urgent { background: #dc3545; color: white; }
        .priority-important { background: #ff6b6b; color: white; }
        .priority-normal { background: #6c757d; color: white; }

        .status-badge {
            font-size: 12px;
            padding: 3px 8px;
        }
        .status-published { background: #28a745; color: white; }
        .status-draft { background: #6c757d; color: white; }

        .action-btn {
            padding: 5px 12px;
            font-size: 13px;
            margin-right: 5px;
            margin-bottom: 5px;
        }

        .search-box input {
            border-radius: 4px 0 0 4px;
        }
        .search-box button {
            border-radius: 0 4px 4px 0;
        }
        .view-count {
            color: #6c757d;
            font-size: 13px;
        }
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        .empty-state i {
            font-size: 64px;
            margin-bottom: 20px;
            opacity: 0.3;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
        }
        .required::after {
            content: '*';
            color: #dc3545;
            margin-left: 4px;
        }

        .stat-card {
            background: white;
            border-radius: 10px;
            padding: 25px 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 15px rgba(0,0,0,0.08);
            transition: all 0.3s;
            position: relative;
            overflow: hidden;
        }
        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, var(--card-color) 0%, var(--card-color-light) 100%);
        }
        .stat-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        .stat-card .stat-icon {
            font-size: 42px;
            opacity: 0.9;
            margin-bottom: 10px;
        }
        .stat-card .stat-number {
            font-size: 32px;
            font-weight: bold;
            margin: 10px 0 5px;
            font-family: 'Arial', sans-serif;
        }
        .stat-card .stat-label {
            color: #6c757d;
            font-size: 14px;
            font-weight: 500;
        }
        .stat-card.card-primary {
            --card-color: #007bff;
            --card-color-light: #4da3ff;
        }
        .stat-card.card-danger {
            --card-color: #dc3545;
            --card-color-light: #ff6b7a;
        }
        .stat-card.card-warning {
            --card-color: #ffc107;
            --card-color-light: #ffd451;
        }
        .stat-card.card-info {
            --card-color: #17a2b8;
            --card-color-light: #4ec3d6;
        }

        .detail-modal .modal-dialog {
            max-width: 750px;
        }
        .detail-modal .modal-content {
            border: none;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            overflow: hidden;
        }
        .detail-modal .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 25px;
            border: none;
        }
        .detail-modal .modal-title {
            font-size: 18px;
            font-weight: 600;
            display: flex;
            align-items: center;
        }
        .detail-modal .modal-title i {
            margin-right: 10px;
            font-size: 20px;
        }

        .info-cards {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 12px;
            margin-bottom: 20px;
        }
        .info-card {
            background: linear-gradient(135deg, var(--info-color) 0%, var(--info-color-light) 100%);
            padding: 15px;
            border-radius: 10px;
            color: white;
            text-align: center;
            box-shadow: 0 3px 12px rgba(0,0,0,0.1);
            transition: all 0.3s;
        }
        .info-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.15);
        }
        .info-card i {
            font-size: 26px;
            margin-bottom: 8px;
            opacity: 0.9;
        }
        .info-card .info-value {
            font-size: 20px;
            font-weight: bold;
            margin: 6px 0;
        }
        .info-card .info-label {
            font-size: 12px;
            opacity: 0.9;
        }
        .info-card.card-time {
            --info-color: #667eea;
            --info-color-light: #764ba2;
        }
        .info-card.card-view {
            --info-color: #f093fb;
            --info-color-light: #f5576c;
        }
        .info-card.card-type {
            --info-color: #4facfe;
            --info-color-light: #00f2fe;
        }

        .detail-info-section {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 15px;
        }
        .detail-info-row {
            display: flex;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e9ecef;
        }
        .detail-info-row:last-child {
            border-bottom: none;
        }
        .detail-info-icon {
            width: 36px;
            height: 36px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 16px;
            margin-right: 12px;
        }
        .detail-info-content {
            flex: 1;
        }
        .detail-info-label {
            font-size: 11px;
            color: #6c757d;
            margin-bottom: 2px;
        }
        .detail-info-value {
            font-size: 14px;
            color: #333;
            font-weight: 600;
        }

        /* ✅ 超级美化的内容区域 */
        .content-section {
            background: white;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
            position: relative;
        }

        .content-section::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 4px;
            background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
            border-radius: 12px 0 0 12px;
        }

        .content-section-title {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e9ecef;
            display: flex;
            align-items: center;
            position: relative;
        }

        .content-section-title::after {
            content: '';
            position: absolute;
            left: 0;
            bottom: -2px;
            width: 60px;
            height: 2px;
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        }

        .content-section-title i {
            margin-right: 10px;
            color: #667eea;
            font-size: 18px;
        }

        /* ✅ 超级美化的内容文本 */
        .content-text {
            font-size: 15px;
            line-height: 1.9;
            color: #2c3e50;
            white-space: pre-wrap;
            word-wrap: break-word;
            max-height: 350px;
            overflow-y: auto;
            padding: 15px;
            font-family: 'Microsoft YaHei', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
            letter-spacing: 0.3px;
            text-align: left;
            background: linear-gradient(to bottom, #fafbfc 0%, #f5f7fa 100%);
            border-radius: 8px;
            border: 1px solid #e9ecef;
        }

        /* ✅ 美化滚动条 */
        .content-text::-webkit-scrollbar {
            width: 8px;
        }

        .content-text::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 4px;
        }

        .content-text::-webkit-scrollbar-thumb {
            background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
            border-radius: 4px;
        }

        .content-text::-webkit-scrollbar-thumb:hover {
            background: linear-gradient(180deg, #764ba2 0%, #667eea 100%);
        }

        /* ✅ 段落样式 */
        .content-text p {
            margin-bottom: 12px;
        }

        .content-text p:last-child {
            margin-bottom: 0;
        }

        /* ✅ 强调文本 */
        .content-text strong,
        .content-text b {
            color: #667eea;
            font-weight: 600;
        }

        /* ✅ 链接样式 */
        .content-text a {
            color: #667eea;
            text-decoration: none;
            border-bottom: 1px dashed #667eea;
            transition: all 0.3s;
        }

        .content-text a:hover {
            color: #764ba2;
            border-bottom-color: #764ba2;
        }

        /* ✅ 落款样式 */
        .content-signature {
            text-align: right;
            margin-top: 20px;
            padding-top: 15px;
            border-top: 1px dashed #dee2e6;
            color: #6c757d;
            font-size: 14px;
            line-height: 1.8;
        }

        .content-signature .signature-name {
            font-weight: 600;
            color: #495057;
        }

        .content-signature .signature-date {
            margin-top: 5px;
            font-style: italic;
            color: #868e96;
        }

        .filter-group {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 15px;
        }
        .filter-group label {
            font-size: 13px;
            color: #6c757d;
            margin-bottom: 5px;
            font-weight: 600;
        }
        .filter-group select {
            font-size: 14px;
        }

        .notice-card input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
            margin-right: 10px;
        }

        .select-all-wrapper {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            padding: 10px 15px;
            background: #e3f2fd;
            border-radius: 6px;
        }
        .select-all-wrapper input[type="checkbox"] {
            width: 20px;
            height: 20px;
            cursor: pointer;
            margin-right: 10px;
        }
        .select-all-wrapper label {
            margin: 0;
            font-weight: 600;
            color: #1976d2;
            cursor: pointer;
        }
    </style>
</head>
<body>

<!-- ✅ 超级美化的页面标题 -->
<div class="page-header">
    <div class="container-fluid">
        <div class="page-header-content">
            <div class="page-header-title">
                <div class="page-header-icon">
                    <i class="fas fa-bullhorn"></i>
                </div>
                <h2>公告管理</h2>
            </div>
            <p class="page-header-desc">发布、编辑、删除社区公告，实时推送重要通知</p>
        </div>
    </div>
</div>

<div class="container-fluid">
    <!-- 统计卡片 -->
    <div class="row mb-3" id="statisticsCards">
        <!-- JS 动态填充 -->
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
        <div class="row align-items-center mb-3">
            <div class="col-md-12">
                <button class="btn btn-primary" onclick="showAddModal()">
                    <i class="fas fa-plus mr-1"></i> 添加公告
                </button>
                <button class="btn btn-success ml-2" onclick="batchPublish()">
                    <i class="fas fa-check mr-1"></i> 批量发布
                </button>
                <button class="btn btn-danger ml-2" onclick="batchDelete()">
                    <i class="fas fa-trash mr-1"></i> 批量删除
                </button>
            </div>
        </div>

        <!-- 筛选器 -->
        <div class="filter-group">
            <div class="row">
                <div class="col-md-3">
                    <label><i class="fas fa-tag mr-1"></i>公告类型</label>
                    <select class="form-control" id="filterType" onchange="loadData(1)">
                        <option value="">全部类型</option>
                        <option value="notice">普通通知</option>
                        <option value="emergency">紧急通知</option>
                        <option value="payment_reminder">缴费提醒</option>
                        <option value="maintenance">维修公告</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label><i class="fas fa-exclamation-circle mr-1"></i>优先级</label>
                    <select class="form-control" id="filterPriority" onchange="loadData(1)">
                        <option value="">全部优先级</option>
                        <option value="normal">普通</option>
                        <option value="important">重要</option>
                        <option value="urgent">紧急</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label><i class="fas fa-toggle-on mr-1"></i>发布状态</label>
                    <select class="form-control" id="filterStatus" onchange="loadData(1)">
                        <option value="">全部状态</option>
                        <option value="1">已发布</option>
                        <option value="0">草稿</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label><i class="fas fa-search mr-1"></i>关键词搜索</label>
                    <div class="input-group">
                        <input type="text" class="form-control" id="keyword" placeholder="搜索标题..."
                               onkeypress="if(event.keyCode==13) loadData(1);">
                        <div class="input-group-append">
                            <button class="btn btn-outline-secondary" onclick="clearSearch()" title="清空">
                                <i class="fas fa-times"></i>
                            </button>
                            <button class="btn btn-primary" onclick="loadData(1)">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 全选框 -->
    <div class="select-all-wrapper">
        <input type="checkbox" id="selectAll" onchange="toggleSelectAll()">
        <label for="selectAll">全选 / 取消全选</label>
        <span class="ml-3 text-muted" id="selectedCount">(已选择 0 项)</span>
    </div>

    <!-- 公告列表 -->
    <div class="row" id="noticeList">
        <!-- JS 动态填充 -->
    </div>

    <!-- 分页 -->
    <div class="row mt-4">
        <div class="col-12">
            <nav>
                <ul class="pagination justify-content-center" id="pagination">
                    <!-- JS 动态填充 -->
                </ul>
            </nav>
        </div>
    </div>
</div>

<!-- 添加/编辑公告模态框 -->
<div class="modal fade" id="editModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="editModalTitle">
                    <i class="fas fa-edit mr-2"></i>添加公告
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="editForm">
                    <input type="hidden" id="announcementId">

                    <div class="form-group">
                        <label class="form-label required">公告标题</label>
                        <input type="text" class="form-control" id="title" placeholder="请输入公告标题" maxlength="200">
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">公告类型</label>
                                <select class="form-control" id="announcementType">
                                    <option value="notice">普通通知</option>
                                    <option value="emergency">紧急通知</option>
                                    <option value="payment_reminder">缴费提醒</option>
                                    <option value="maintenance">维修公告</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">优先级</label>
                                <select class="form-control" id="priority">
                                    <option value="normal">普通</option>
                                    <option value="important">重要</option>
                                    <option value="urgent">紧急</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label required">公告内容</label>
                        <textarea class="form-control" id="content" rows="8" placeholder="请输入公告内容"></textarea>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">有效期至</label>
                                <input type="datetime-local" class="form-control" id="expiryTime">
                                <small class="text-muted">不填则长期有效</small>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">发布状态</label>
                                <select class="form-control" id="status">
                                    <option value="1">立即发布</option>
                                    <option value="0">保存为草稿</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times mr-1"></i> 取消
                </button>
                <button type="button" class="btn btn-primary" onclick="saveAnnouncement()">
                    <i class="fas fa-save mr-1"></i> 保存
                </button>
            </div>
        </div>
    </div>
</div>

<!-- ✅ 优化后的详情模态框 -->
<div class="modal fade detail-modal" id="detailModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-file-alt"></i>
                    <span id="modalTitle"></span>
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body p-3">
                <!-- 信息卡片 -->
                <div class="info-cards">
                    <div class="info-card card-time">
                        <i class="far fa-clock"></i>
                        <div class="info-value" id="modalTime"></div>
                        <div class="info-label">发布时间</div>
                    </div>
                    <div class="info-card card-view">
                        <i class="far fa-eye"></i>
                        <div class="info-value" id="modalViewCount"></div>
                        <div class="info-label">浏览次数</div>
                    </div>
                    <div class="info-card card-type">
                        <i class="fas fa-tag"></i>
                        <div class="info-value" id="modalType"></div>
                        <div class="info-label">公告类型</div>
                    </div>
                </div>

                <!-- 详细信息 -->
                <div class="detail-info-section">
                    <div class="detail-info-row">
                        <div class="detail-info-icon">
                            <i class="far fa-user"></i>
                        </div>
                        <div class="detail-info-content">
                            <div class="detail-info-label">发布人</div>
                            <div class="detail-info-value" id="modalPublisher"></div>
                        </div>
                    </div>
                    <div class="detail-info-row">
                        <div class="detail-info-icon">
                            <i class="fas fa-exclamation-circle"></i>
                        </div>
                        <div class="detail-info-content">
                            <div class="detail-info-label">优先级</div>
                            <div class="detail-info-value" id="modalPriority"></div>
                        </div>
                    </div>
                    <div class="detail-info-row">
                        <div class="detail-info-icon">
                            <i class="far fa-calendar-times"></i>
                        </div>
                        <div class="detail-info-content">
                            <div class="detail-info-label">有效期至</div>
                            <div class="detail-info-value" id="modalExpiry"></div>
                        </div>
                    </div>
                </div>

                <!-- 公告内容 -->
                <div class="content-section">
                    <div class="content-section-title">
                        <i class="fas fa-align-left"></i>
                        公告内容
                    </div>
                    <div class="content-text" id="modalContent"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times mr-1"></i> 关闭
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>

<script>
    let currentPage = 1;
    const pageSize = 10;
    let selectedIds = [];
    let allAnnouncementIds = [];

    $(function() {
        loadStatistics();
        loadData();
    });

    function loadStatistics() {
        $.ajax({
            url: '${pageContext.request.contextPath}/announcement',
            type: 'GET',
            data: { method: 'statistics' },
            dataType: 'json',
            success: function(res) {
                console.log('统计数据:', res);
                if (res.code === 200 && res.data) {
                    renderStatistics(res.data);
                }
            },
            error: function(xhr) {
                console.error('加载统计数据失败');
            }
        });
    }

    function renderStatistics(data) {
        const html =
            '<div class="col-md-3">' +
            '<div class="stat-card card-primary text-center">' +
            '<div class="stat-icon text-primary"><i class="fas fa-bell"></i></div>' +
            '<div class="stat-number text-primary">' + (data.notice || 0) + '</div>' +
            '<div class="stat-label">普通通知</div>' +
            '</div></div>' +
            '<div class="col-md-3">' +
            '<div class="stat-card card-danger text-center">' +
            '<div class="stat-icon text-danger"><i class="fas fa-exclamation-triangle"></i></div>' +
            '<div class="stat-number text-danger">' + (data.emergency || 0) + '</div>' +
            '<div class="stat-label">紧急通知</div>' +
            '</div></div>' +
            '<div class="col-md-3">' +
            '<div class="stat-card card-warning text-center">' +
            '<div class="stat-icon text-warning"><i class="fas fa-money-bill-wave"></i></div>' +
            '<div class="stat-number text-warning">' + (data.payment_reminder || 0) + '</div>' +
            '<div class="stat-label">缴费提醒</div>' +
            '</div></div>' +
            '<div class="col-md-3">' +
            '<div class="stat-card card-info text-center">' +
            '<div class="stat-icon text-info"><i class="fas fa-tools"></i></div>' +
            '<div class="stat-number text-info">' + (data.maintenance || 0) + '</div>' +
            '<div class="stat-label">维修公告</div>' +
            '</div></div>';

        $('#statisticsCards').html(html);
    }

    function loadData(page) {
        if (page) currentPage = page;
        const keyword = $('#keyword').val().trim();
        const filterType = $('#filterType').val();
        const filterPriority = $('#filterPriority').val();
        const filterStatus = $('#filterStatus').val();

        $.ajax({
            url: '${pageContext.request.contextPath}/announcement',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: keyword,
                announcementType: filterType,
                priority: filterPriority,
                status: filterStatus
            },
            dataType: 'json',
            success: function(res) {
                console.log('返回数据:', res);
                if (res.code === 200 && res.data) {
                    renderList(res.data.list || []);
                    renderPagination(res.data.total || 0);
                } else {
                    showEmpty('加载失败：' + (res.message || '未知错误'));
                }
            },
            error: function(xhr) {
                console.error('请求失败');
                if (xhr.status === 401) {
                    alert('登录已过期，请重新登录');
                    window.location.href = '${pageContext.request.contextPath}/login.jsp';
                } else {
                    showEmpty('网络错误,请稍后重试');
                }
            }
        });
    }

    function clearSearch() {
        $('#keyword').val('');
        $('#filterType').val('');
        $('#filterPriority').val('');
        $('#filterStatus').val('');
        loadData(1);
    }

    function renderList(list) {
        let html = '';
        allAnnouncementIds = [];

        if (list && list.length > 0) {
            $.each(list, function(i, item) {
                allAnnouncementIds.push(item.announcementId);

                const typeMap = {
                    'emergency': {text:'紧急通知', cls:'type-emergency', badge:'badge-danger'},
                    'notice': {text:'普通通知', cls:'type-notice', badge:'badge-primary'},
                    'payment_reminder': {text:'缴费提醒', cls:'type-payment_reminder', badge:'badge-warning'},
                    'maintenance': {text:'维修公告', cls:'type-maintenance', badge:'badge-info'}
                };
                const typeInfo = typeMap[item.announcementType] || typeMap['notice'];

                const priorityMap = {
                    'urgent': {text:'紧急', cls:'priority-urgent'},
                    'important': {text:'重要', cls:'priority-important'},
                    'normal': {text:'普通', cls:'priority-normal'}
                };
                const priorityInfo = priorityMap[item.priority] || priorityMap['normal'];

                const statusBadge = item.status === 1
                    ? '<span class="badge status-published">已发布</span>'
                    : '<span class="badge status-draft">草稿</span>';

                let actionButtons = '';
                if (item.status === 0) {
                    actionButtons = '<button class="btn btn-sm btn-success action-btn" onclick="updateStatus(' + item.announcementId + ', 1)">' +
                        '<i class="fas fa-check"></i> 发布</button>';
                } else {
                    actionButtons = '<button class="btn btn-sm btn-warning action-btn" onclick="updateStatus(' + item.announcementId + ', 0)">' +
                        '<i class="fas fa-ban"></i> 取消发布</button>';
                }

                const isChecked = selectedIds.indexOf(item.announcementId) > -1 ? 'checked' : '';

                html += '<div class="col-md-12">' +
                    '<div class="card notice-card shadow-sm ' + typeInfo.cls + '">' +
                    '<div class="card-body py-3">' +
                    '<div class="d-flex justify-content-between align-items-start">' +
                    '<div class="flex-grow-1" onclick="showDetail(' + item.announcementId + ')" style="cursor: pointer;">' +
                    '<h5 class="mb-2">' +
                    '<input type="checkbox" class="item-checkbox" data-id="' + item.announcementId + '" ' + isChecked + ' onclick="event.stopPropagation(); toggleSelect(' + item.announcementId + ')">' +
                    '<span class="badge ' + typeInfo.badge + ' mr-2">' + typeInfo.text + '</span>' +
                    '<span class="badge ' + priorityInfo.cls + ' priority-badge mr-2">' + priorityInfo.text + '</span>' +
                    statusBadge + ' ' + escapeHtml(item.title) +
                    '</h5>' +
                    '<p class="mb-2 text-muted small" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">' +
                    escapeHtml(item.content || '').substring(0, 120) + '...</p>' +
                    '<div class="d-flex justify-content-between align-items-center">' +
                    '<small class="text-muted">' +
                    '<i class="far fa-clock mr-1"></i>' + formatDate(item.publishTime) +
                    '<span class="ml-3 view-count"><i class="far fa-eye mr-1"></i>' + (item.viewCount || 0) + ' 次浏览</span>' +
                    '</small></div></div>' +
                    '<div class="ml-3" style="min-width: 220px;">' +
                    '<button class="btn btn-sm btn-info action-btn" onclick="showEditModal(' + item.announcementId + ')">' +
                    '<i class="fas fa-edit"></i> 编辑</button>' +
                    actionButtons +
                    '<button class="btn btn-sm btn-danger action-btn" onclick="deleteAnnouncement(' + item.announcementId + ')">' +
                    '<i class="fas fa-trash"></i> 删除</button>' +
                    '</div></div></div></div></div>';
            });
        } else {
            html = '<div class="col-12"><div class="empty-state">' +
                '<i class="fas fa-inbox"></i><h5>暂无公告</h5>' +
                '<p class="text-muted">点击"添加公告"按钮创建新公告</p></div></div>';
        }

        $('#noticeList').html(html);
        updateSelectAllState();
        updateSelectedCount();
    }

    function toggleSelectAll() {
        const isChecked = $('#selectAll').prop('checked');

        if (isChecked) {
            allAnnouncementIds.forEach(function(id) {
                if (selectedIds.indexOf(id) === -1) {
                    selectedIds.push(id);
                }
            });
        } else {
            allAnnouncementIds.forEach(function(id) {
                const index = selectedIds.indexOf(id);
                if (index > -1) {
                    selectedIds.splice(index, 1);
                }
            });
        }

        $('.item-checkbox').prop('checked', isChecked);
        updateSelectedCount();
        console.log('已选中:', selectedIds);
    }

    function updateSelectAllState() {
        if (allAnnouncementIds.length === 0) {
            $('#selectAll').prop('checked', false);
            return;
        }

        const allSelected = allAnnouncementIds.every(function(id) {
            return selectedIds.indexOf(id) > -1;
        });

        $('#selectAll').prop('checked', allSelected);
    }

    function updateSelectedCount() {
        $('#selectedCount').text('(已选择 ' + selectedIds.length + ' 项)');
    }

    function renderPagination(total) {
        const totalPages = Math.ceil(total / pageSize);
        let html = '';

        if (totalPages > 1) {
            const prevDisabled = currentPage === 1 ? 'disabled' : '';
            html += '<li class="page-item ' + prevDisabled + '">' +
                '<a class="page-link" href="javascript:void(0)" onclick="loadData(' + (currentPage - 1) + ')">上一页</a></li>';

            for (let i = 1; i <= totalPages; i++) {
                if (i === 1 || i === totalPages || (i >= currentPage - 2 && i <= currentPage + 2)) {
                    const activeClass = i === currentPage ? 'active' : '';
                    html += '<li class="page-item ' + activeClass + '">' +
                        '<a class="page-link" href="javascript:void(0)" onclick="loadData(' + i + ')">' + i + '</a></li>';
                } else if (i === currentPage - 3 || i === currentPage + 3) {
                    html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
                }
            }

            const nextDisabled = currentPage === totalPages ? 'disabled' : '';
            html += '<li class="page-item ' + nextDisabled + '">' +
                '<a class="page-link" href="javascript:void(0)" onclick="loadData(' + (currentPage + 1) + ')">下一页</a></li>';
        }

        $('#pagination').html(html);
    }

    function showAddModal() {
        $('#editModalTitle').html('<i class="fas fa-plus mr-2"></i>添加公告');
        $('#editForm')[0].reset();
        $('#announcementId').val('');
        $('#editModal').modal('show');
    }

    function showEditModal(id) {
        $('#editModalTitle').html('<i class="fas fa-edit mr-2"></i>编辑公告');

        $.ajax({
            url: '${pageContext.request.contextPath}/announcement',
            type: 'GET',
            data: { method: 'detail', id: id },
            dataType: 'json',
            success: function(res) {
                if (res.code === 200 && res.data) {
                    const item = res.data;
                    $('#announcementId').val(item.announcementId);
                    $('#title').val(item.title);
                    $('#announcementType').val(item.announcementType);
                    $('#priority').val(item.priority);
                    $('#content').val(item.content);
                    $('#status').val(item.status);

                    if (item.expiryTime) {
                        $('#expiryTime').val(formatDateTimeForInput(item.expiryTime));
                    }

                    $('#editModal').modal('show');
                } else {
                    alert('加载失败：' + (res.message || '未知错误'));
                }
            }
        });
    }

    function saveAnnouncement() {
        const id = $('#announcementId').val();
        const title = $('#title').val().trim();
        const announcementType = $('#announcementType').val();
        const priority = $('#priority').val();
        const content = $('#content').val().trim();
        const expiryTime = $('#expiryTime').val();
        const status = parseInt($('#status').val());

        if (!title) {
            alert('请输入公告标题');
            return;
        }
        if (!content) {
            alert('请输入公告内容');
            return;
        }

        const data = {
            title: title,
            announcementType: announcementType,
            priority: priority,
            content: content,
            status: status
        };

        if (id) {
            data.announcementId = parseInt(id);
        }

        if (expiryTime) {
            data.expiryTime = expiryTime.replace('T', ' ') + ':00';
        }

        const method = id ? 'update' : 'add';

        $.ajax({
            url: '${pageContext.request.contextPath}/announcement?method=' + method,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            dataType: 'json',
            success: function(res) {
                if (res.code === 200) {
                    alert(res.message || '操作成功');
                    $('#editModal').modal('hide');
                    loadStatistics();
                    loadData();
                } else {
                    alert(res.message || '操作失败');
                }
            },
            error: function(xhr) {
                alert('网络错误，请稍后重试');
            }
        });
    }

    function updateStatus(id, status) {
        const action = status === 1 ? '发布' : '取消发布';
        if (!confirm('确定要' + action + '该公告吗？')) return;

        $.ajax({
            url: '${pageContext.request.contextPath}/announcement?method=updateStatus&id=' + id + '&status=' + status,
            type: 'POST',
            dataType: 'json',
            success: function(res) {
                if (res.code === 200) {
                    alert(res.message || '操作成功');
                    loadStatistics();
                    loadData();
                } else {
                    alert(res.message || '操作失败');
                }
            }
        });
    }

    function deleteAnnouncement(id) {
        if (!confirm('确定要删除该公告吗？此操作不可恢复！')) return;

        $.ajax({
            url: '${pageContext.request.contextPath}/announcement?method=delete&id=' + id,
            type: 'POST',
            dataType: 'json',
            success: function(res) {
                if (res.code === 200) {
                    alert(res.message || '删除成功');
                    loadStatistics();
                    loadData();
                } else {
                    alert(res.message || '删除失败');
                }
            }
        });
    }

    function toggleSelect(id) {
        const index = selectedIds.indexOf(id);
        if (index > -1) {
            selectedIds.splice(index, 1);
        } else {
            selectedIds.push(id);
        }
        updateSelectAllState();
        updateSelectedCount();
        console.log('已选中:', selectedIds);
    }

    function batchPublish() {
        if (selectedIds.length === 0) {
            alert('请先选择要发布的公告');
            return;
        }

        if (!confirm('确定要发布选中的 ' + selectedIds.length + ' 条公告吗？')) return;

        $.ajax({
            url: '${pageContext.request.contextPath}/announcement?method=batchUpdateStatus',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ ids: selectedIds, status: 1 }),
            dataType: 'json',
            success: function(res) {
                if (res.code === 200) {
                    alert(res.message || '操作成功');
                    selectedIds = [];
                    loadStatistics();
                    loadData();
                } else {
                    alert(res.message || '操作失败');
                }
            }
        });
    }

    function batchDelete() {
        if (selectedIds.length === 0) {
            alert('请先选择要删除的公告');
            return;
        }

        if (!confirm('确定要删除选中的 ' + selectedIds.length + ' 条公告吗？此操作不可恢复！')) return;

        $.ajax({
            url: '${pageContext.request.contextPath}/announcement?method=batchDelete',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(selectedIds),
            dataType: 'json',
            success: function(res) {
                if (res.code === 200) {
                    alert(res.message || '操作成功');
                    selectedIds = [];
                    loadStatistics();
                    loadData();
                } else {
                    alert(res.message || '操作失败');
                }
            }
        });
    }

    // ✅ 查看详情（智能识别落款）
    function showDetail(id) {
        $.ajax({
            url: '${pageContext.request.contextPath}/announcement',
            type: 'GET',
            data: { method: 'detail', id: id },
            dataType: 'json',
            success: function(res) {
                if (res.code === 200 && res.data) {
                    const item = res.data;

                    const typeMap = {
                        'emergency': '紧急通知',
                        'notice': '普通通知',
                        'payment_reminder': '缴费提醒',
                        'maintenance': '维修公告'
                    };

                    const priorityMap = {
                        'urgent': '🔴 紧急',
                        'important': '🟠 重要',
                        'normal': '🟢 普通'
                    };

                    $('#modalTitle').text(item.title);
                    $('#modalTime').text(formatDate(item.publishTime));
                    $('#modalViewCount').text(item.viewCount || 0);
                    $('#modalType').text(typeMap[item.announcementType] || '普通通知');
                    $('#modalPublisher').text('管理员');
                    $('#modalPriority').text(priorityMap[item.priority] || '🟢 普通');
                    $('#modalExpiry').text(item.expiryTime ? formatDate(item.expiryTime) : '长期有效');

                    // ✅ 智能处理内容和落款
                    const content = item.content || '暂无内容';
                    const formattedContent = formatContentWithSignature(content);
                    $('#modalContent').html(formattedContent);

                    $('#detailModal').modal('show');
                }
            }
        });
    }

    // ✅ 智能格式化内容（识别落款）
    function formatContentWithSignature(content) {
        const signaturePatterns = [
            /\n\s*(.*?(?:物业|管理处|社区|居委会|办公室))\s*\n\s*(\d{4}年\d{1,2}月\d{1,2}日)\s*$/,
            /\n\s*(.*?)\s*\n\s*(\d{4}-\d{1,2}-\d{1,2})\s*$/,
            /\n\s*(.*?)\s*\n\s*(\d{4}\.\d{1,2}\.\d{1,2})\s*$/
        ];

        let mainContent = content;
        let signature = '';

        for (let pattern of signaturePatterns) {
            const match = content.match(pattern);
            if (match) {
                mainContent = content.substring(0, match.index);
                signature = '<div class="content-signature">' +
                    '<div class="signature-name">' + escapeHtml(match[1].trim()) + '</div>' +
                    '<div class="signature-date">' + escapeHtml(match[2].trim()) + '</div>' +
                    '</div>';
                break;
            }
        }

        return escapeHtml(mainContent) + signature;
    }

    function showEmpty(message) {
        $('#noticeList').html(
            '<div class="col-12"><div class="empty-state">' +
            '<i class="fas fa-exclamation-circle"></i>' +
            '<h5>' + message + '</h5></div></div>'
        );
        $('#pagination').html('');
    }

    function formatDate(timestamp) {
        if (!timestamp) return '';
        const date = new Date(timestamp);
        return date.toLocaleDateString('zh-CN');
    }

    function formatDateTime(timestamp) {
        if (!timestamp) return '';
        const date = new Date(timestamp);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function formatDateTimeForInput(timestamp) {
        if (!timestamp) return '';
        const date = new Date(timestamp);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hour = String(date.getHours()).padStart(2, '0');
        const minute = String(date.getMinutes()).padStart(2, '0');
        return year + '-' + month + '-' + day + 'T' + hour + ':' + minute;
    }

    function escapeHtml(text) {
        if (!text) return '';
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) {
            return map[m];
        });
    }
</script>
</body>
</html>
