<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>社区公告 - 智慧社区</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            font-family: 'Microsoft YaHei', 'Segoe UI', sans-serif;
            min-height: 100vh;
        }

        /* ========== 页面头部 ========== */
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 50px 0 30px;
            margin-bottom: 40px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.15);
            position: relative;
            overflow: hidden;
        }

        .page-header::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -10%;
            width: 300px;
            height: 300px;
            background: rgba(255,255,255,0.1);
            border-radius: 50%;
        }

        .page-header::after {
            content: '';
            position: absolute;
            bottom: -30%;
            left: -5%;
            width: 200px;
            height: 200px;
            background: rgba(255,255,255,0.08);
            border-radius: 50%;
        }

        .page-header h2 {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 10px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
            position: relative;
            z-index: 1;
        }

        .page-header p {
            font-size: 1.1rem;
            opacity: 0.95;
            position: relative;
            z-index: 1;
        }

        /* ========== 筛选区域 ========== */
        .filter-section {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
        }

        .filter-tabs {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            margin-bottom: 20px;
        }

        .filter-tab {
            padding: 8px 20px;
            border-radius: 25px;
            border: 2px solid #e0e0e0;
            background: white;
            color: #666;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 14px;
            font-weight: 500;
        }

        .filter-tab:hover {
            border-color: #667eea;
            color: #667eea;
            transform: translateY(-2px);
        }

        .filter-tab.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #667eea;
            color: white;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }

        /* ========== 搜索框 ========== */
        .search-box {
            background: white;
            border-radius: 50px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            overflow: hidden;
            display: flex;
        }

        .search-box input {
            border: none;
            height: 55px;
            padding: 0 25px;
            font-size: 15px;
            flex: 1;
        }

        .search-box input:focus {
            outline: none;
            box-shadow: none;
        }

        .search-box .input-group-append {
            display: flex;
        }

        .search-box .btn-clear {
            height: 55px;
            padding: 0 15px;
            border: none;
            background: #f8f9fa;
            color: #6c757d;
            transition: all 0.3s;
            border-radius: 0;
        }

        .search-box .btn-clear:hover {
            background: #e9ecef;
            color: #dc3545;
        }

        .search-box .btn-search {
            height: 55px;
            padding: 0 30px;
            border: none;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            font-weight: 600;
            transition: all 0.3s;
            border-radius: 0 50px 50px 0;
        }

        .search-box .btn-search:hover {
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
            transform: scale(1.02);
        }

        /* ========== 公告卡片 ========== */
        .notice-card {
            cursor: pointer;
            transition: all 0.3s ease;
            border: none;
            border-left: 5px solid transparent;
            margin-bottom: 20px;
            background: white;
            border-radius: 12px;
            overflow: hidden;
        }

        .notice-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.1);
        }

        .notice-card.type-emergency {
            border-left-color: #dc3545;
            background: linear-gradient(to right, rgba(220, 53, 69, 0.05) 0%, white 10%);
        }
        .notice-card.type-notice {
            border-left-color: #007bff;
            background: linear-gradient(to right, rgba(0, 123, 255, 0.05) 0%, white 10%);
        }
        .notice-card.type-payment_reminder {
            border-left-color: #ffc107;
            background: linear-gradient(to right, rgba(255, 193, 7, 0.05) 0%, white 10%);
        }
        .notice-card.type-maintenance {
            border-left-color: #17a2b8;
            background: linear-gradient(to right, rgba(23, 162, 184, 0.05) 0%, white 10%);
        }

        .notice-card .card-body {
            padding: 25px;
        }

        .notice-card h5 {
            font-size: 1.2rem;
            font-weight: 600;
            margin-bottom: 12px;
            color: #2c3e50;
            line-height: 1.5;
        }

        /* ========== 徽章样式 ========== */
        .priority-badge {
            font-size: 11px;
            padding: 4px 10px;
            border-radius: 12px;
            font-weight: 600;
            letter-spacing: 0.5px;
        }
        .priority-urgent {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(220, 53, 69, 0.3);
        }
        .priority-important {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
        }
        .priority-normal {
            background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
            color: white;
        }

        .badge {
            font-size: 12px;
            padding: 5px 12px;
            border-radius: 15px;
            font-weight: 600;
        }

        /* ========== 浏览次数样式 ========== */
        .view-count {
            color: #6c757d;
            font-size: 13px;
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .view-count i {
            color: #007bff;
        }

        .view-count.hot {
            color: #dc3545;
            font-weight: 600;
        }

        .view-count.hot i {
            color: #dc3545;
            animation: pulse 1.5s infinite;
        }

        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.2); }
        }

        /* ========== 详情模态框 ========== */
        .modal-content {
            border: none;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }

        .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px 35px;
            border: none;
            position: relative;
        }

        .modal-header::before {
            content: '';
            position: absolute;
            top: 0;
            right: 0;
            width: 200px;
            height: 200px;
            background: rgba(255,255,255,0.1);
            border-radius: 50%;
            transform: translate(30%, -30%);
        }

        .modal-header .modal-title {
            font-size: 1.5rem;
            font-weight: 700;
            position: relative;
            z-index: 1;
            line-height: 1.4;
        }

        .modal-header .close {
            color: white;
            opacity: 0.9;
            text-shadow: none;
            font-size: 2rem;
            position: relative;
            z-index: 1;
            transition: all 0.3s;
        }

        .modal-header .close:hover {
            opacity: 1;
            transform: rotate(90deg);
        }

        .modal-body {
            padding: 35px;
            background: #f8f9fa;
        }

        /* 信息卡片 */
        .modal-info-box {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.05);
            border: 1px solid #e9ecef;
        }

        .modal-info-row {
            display: flex;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px dashed #e9ecef;
        }

        .modal-info-row:last-child {
            border-bottom: none;
            padding-bottom: 0;
        }

        .modal-info-row:first-child {
            padding-top: 0;
        }

        .modal-info-icon {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            font-size: 18px;
        }

        .modal-info-icon.time {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .modal-info-icon.user {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
        }

        .modal-info-icon.view {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
        }

        .modal-info-icon.calendar {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            color: white;
        }

        .modal-info-content {
            flex: 1;
        }

        .modal-info-label {
            font-size: 13px;
            color: #6c757d;
            margin-bottom: 3px;
            font-weight: 500;
        }

        .modal-info-value {
            font-size: 15px;
            color: #2c3e50;
            font-weight: 600;
        }

        /* 浏览次数特殊样式 */
        .view-count-display {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-weight: 700;
            font-size: 16px;
        }

        .view-count-display.hot {
            color: #dc3545;
        }

        .view-count-display.hot i {
            color: #dc3545;
            animation: fire-pulse 1.5s infinite;
        }

        .view-count-display.popular {
            color: #ff6b6b;
        }

        .view-count-display.popular i {
            color: #ff6b6b;
        }

        .view-count-display.normal {
            color: #28a745;
        }

        .view-count-display.normal i {
            color: #28a745;
        }

        @keyframes fire-pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.3); }
        }

        /* 内容区域 */
        .notice-content-modal {
            background: white;
            border-radius: 15px;
            padding: 30px;
            font-size: 16px;
            line-height: 2;
            white-space: pre-wrap;
            word-wrap: break-word;
            color: #2c3e50;
            min-height: 200px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.05);
            border: 1px solid #e9ecef;
            position: relative;
        }

        .notice-content-modal::before {
            content: '\f10d';
            font-family: 'Font Awesome 5 Free';
            font-weight: 900;
            position: absolute;
            top: 15px;
            left: 15px;
            font-size: 30px;
            color: #e9ecef;
            opacity: 0.5;
        }

        .content-title {
            font-size: 14px;
            color: #6c757d;
            font-weight: 600;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .content-title i {
            color: #667eea;
        }

        /* 模态框底部 */
        .modal-footer {
            background: white;
            border-top: 1px solid #e9ecef;
            padding: 20px 35px;
        }

        .modal-footer .btn {
            padding: 10px 30px;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s;
        }

        .modal-footer .btn-secondary {
            background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
            border: none;
        }

        .modal-footer .btn-secondary:hover {
            background: linear-gradient(135deg, #5a6268 0%, #6c757d 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(108, 117, 125, 0.4);
        }

        /* ========== 分页美化 ========== */
        .pagination {
            gap: 5px;
        }

        .page-item .page-link {
            border: 2px solid #e0e0e0;
            color: #667eea;
            border-radius: 8px;
            margin: 0 3px;
            font-weight: 600;
            transition: all 0.3s;
            min-width: 40px;
            text-align: center;
        }

        .page-item .page-link:hover {
            background: #667eea;
            color: white;
            border-color: #667eea;
            transform: translateY(-2px);
        }

        .page-item.active .page-link {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #667eea;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }

        .page-item.disabled .page-link {
            border-color: #e0e0e0;
            color: #ccc;
            background: #f8f9fa;
        }

        /* ========== 分页信息 ========== */
        .pagination-info {
            text-align: center;
            color: #6c757d;
            font-size: 14px;
            margin-top: 15px;
            padding: 10px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }

        .pagination-info strong {
            color: #667eea;
            font-weight: 700;
        }

        /* ========== 空状态 ========== */
        .empty-state {
            text-align: center;
            padding: 80px 20px;
            color: #999;
        }

        .empty-state i {
            font-size: 80px;
            margin-bottom: 25px;
            opacity: 0.3;
            color: #667eea;
        }

        .empty-state h5 {
            font-size: 1.3rem;
            color: #6c757d;
            margin-bottom: 10px;
        }

        /* ========== 加载动画 ========== */
        .loading-spinner {
            text-align: center;
            padding: 60px 20px;
        }

        .loading-spinner i {
            font-size: 3rem;
            color: #667eea;
        }

        /* ========== 热门标签 ========== */
        .hot-badge {
            display: inline-block;
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
            color: white;
            font-size: 10px;
            padding: 2px 8px;
            border-radius: 10px;
            font-weight: 700;
            margin-left: 5px;
            animation: bounce 1s infinite;
        }

        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-3px); }
        }

        /* ========== 时间标签 ========== */
        .time-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            color: #6c757d;
            font-size: 13px;
        }

        .time-badge.new {
            color: #28a745;
            font-weight: 600;
        }

        .time-badge.new i {
            color: #28a745;
        }

        /* ========== 响应式 ========== */
        @media (max-width: 768px) {
            .page-header h2 {
                font-size: 1.8rem;
            }

            .filter-tabs {
                justify-content: center;
            }

            .notice-card:hover {
                transform: translateY(-3px);
            }

            .modal-body {
                padding: 25px 20px;
            }

            .modal-info-box {
                padding: 20px 15px;
            }

            .notice-content-modal {
                padding: 20px;
            }

            .pagination-info {
                font-size: 12px;
            }
        }
    </style>
</head>
<body>

<!-- 页面头部 -->
<div class="page-header">
    <div class="container">
        <h2><i class="fas fa-bullhorn mr-3"></i>社区公告</h2>
        <p class="mb-0">最新通知 · 停水停电 · 紧急消息 · 缴费提醒</p>
    </div>
</div>

<div class="container">
    <!-- 筛选区域 -->
    <div class="filter-section">
        <div class="row align-items-center">
            <div class="col-md-12 mb-3">
                <h6 class="mb-3" style="color: #6c757d; font-weight: 600;">
                    <i class="fas fa-filter mr-2"></i>公告类型
                </h6>
                <div class="filter-tabs">
                    <div class="filter-tab active" data-type="" onclick="filterByType(this, '')">
                        <i class="fas fa-th-large mr-1"></i> 全部
                    </div>
                    <div class="filter-tab" data-type="notice" onclick="filterByType(this, 'notice')">
                        <i class="fas fa-info-circle mr-1"></i> 普通通知
                    </div>
                    <div class="filter-tab" data-type="emergency" onclick="filterByType(this, 'emergency')">
                        <i class="fas fa-exclamation-triangle mr-1"></i> 紧急通知
                    </div>
                    <div class="filter-tab" data-type="payment_reminder" onclick="filterByType(this, 'payment_reminder')">
                        <i class="fas fa-credit-card mr-1"></i> 缴费提醒
                    </div>
                    <div class="filter-tab" data-type="maintenance" onclick="filterByType(this, 'maintenance')">
                        <i class="fas fa-tools mr-1"></i> 维修公告
                    </div>
                </div>
            </div>
            <div class="col-md-12">
                <div class="input-group search-box">
                    <input type="text" class="form-control" id="keyword" placeholder="🔍 搜索公告标题或内容..."
                           onkeypress="if(event.keyCode==13) searchData();">
                    <div class="input-group-append">
                        <button class="btn btn-clear" onclick="clearSearch()" id="clearBtn" style="display: none;">
                            <i class="fas fa-times"></i>
                        </button>
                        <button class="btn btn-search" onclick="searchData()">
                            <i class="fas fa-search mr-2"></i>搜索
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 公告列表 -->
    <div class="row" id="noticeList">
        <div class="col-12">
            <div class="loading-spinner">
                <i class="fas fa-spinner fa-spin"></i>
                <p class="text-muted mt-3">加载中...</p>
            </div>
        </div>
    </div>

    <!-- 分页 -->
    <div class="row mt-4">
        <div class="col-12">
            <nav>
                <ul class="pagination justify-content-center" id="pagination"></ul>
            </nav>
            <!-- 🔥 分页信息 -->
            <div class="pagination-info" id="paginationInfo" style="display: none;"></div>
        </div>
    </div>
</div>

<!-- 详情模态框 -->
<div class="modal fade" id="detailModal" tabindex="-1">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle"></h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <!-- 信息卡片 -->
                <div class="modal-info-box">
                    <div class="modal-info-row">
                        <div class="modal-info-icon time">
                            <i class="far fa-clock"></i>
                        </div>
                        <div class="modal-info-content">
                            <div class="modal-info-label">发布时间</div>
                            <div class="modal-info-value" id="modalTime"></div>
                        </div>
                    </div>
                    <div class="modal-info-row">
                        <div class="modal-info-icon user">
                            <i class="far fa-user"></i>
                        </div>
                        <div class="modal-info-content">
                            <div class="modal-info-label">发布人</div>
                            <div class="modal-info-value" id="modalPublisher"></div>
                        </div>
                    </div>
                    <div class="modal-info-row">
                        <div class="modal-info-icon view">
                            <i class="far fa-eye"></i>
                        </div>
                        <div class="modal-info-content">
                            <div class="modal-info-label">浏览次数</div>
                            <div class="modal-info-value" id="modalViewCount"></div>
                        </div>
                    </div>
                    <div class="modal-info-row">
                        <div class="modal-info-icon calendar">
                            <i class="far fa-calendar-check"></i>
                        </div>
                        <div class="modal-info-content">
                            <div class="modal-info-label">有效期至</div>
                            <div class="modal-info-value" id="modalExpiry"></div>
                        </div>
                    </div>
                </div>

                <!-- 内容区域 -->
                <div class="content-title">
                    <i class="fas fa-align-left"></i>
                    <span>公告内容</span>
                </div>
                <div class="notice-content-modal" id="modalContent"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times mr-2"></i>关闭
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
    let currentType = '';
    let currentKeyword = '';
    let totalCount = 0;  // 🔥 新增：总记录数

    $(function() {
        console.log('📱 页面加载完成');
        loadData();

        // 监听输入框变化
        $('#keyword').on('input', function() {
            const val = $(this).val().trim();
            if (val) {
                $('#clearBtn').fadeIn(200);
            } else {
                $('#clearBtn').fadeOut(200);
            }
        });
    });

    // ==================== 类型筛选 ====================
    function filterByType(element, type) {
        $('.filter-tab').removeClass('active');
        $(element).addClass('active');
        currentType = type;
        currentPage = 1;
        loadData();
    }

    // ==================== 搜索功能 ====================
    function searchData() {
        const keyword = $('#keyword').val().trim();
        console.log('🔍 搜索关键词:', keyword);

        currentKeyword = keyword;
        currentPage = 1;
        loadData();
    }

    // ==================== 清空搜索 ====================
    function clearSearch() {
        console.log('🗑️ 清空搜索');
        $('#keyword').val('');
        $('#clearBtn').fadeOut(200);
        currentKeyword = '';
        currentPage = 1;
        loadData();
    }

    // ==================== 加载公告列表 ====================
    function loadData(page, silent) {
        if (page) currentPage = page;

        const params = {
            method: 'list',
            pageNum: currentPage,
            pageSize: pageSize
        };

        if (currentKeyword) {
            params.keyword = currentKeyword;
        }

        if (currentType) {
            params.announcementType = currentType;
        }

        console.log('📤 请求参数:', params);

        $.ajax({
            url: '${pageContext.request.contextPath}/owner/announcement',
            type: 'GET',
            data: params,
            dataType: 'json',
            success: function(res) {
                console.log('✅ 返回数据:', res);

                if (res.code === 200) {
                    const list = res.data.list || [];
                    totalCount = res.data.totalCount || res.data.total || 0;  // 🔥 保存总记录数
                    renderList(list, silent);
                    renderPagination(totalCount);
                    renderPaginationInfo(totalCount);  // 🔥 渲染分页信息
                } else {
                    if (!silent) {
                        showEmpty('加载失败：' + (res.msg || '未知错误'));
                    }
                }
            },
            error: function(xhr) {
                console.error('❌ 请求失败:', xhr);
                if (!silent) {
                    showEmpty('网络错误，请稍后重试');
                }
            }
        });
    }

    // ==================== 渲染公告列表 ====================
    function renderList(list, silent) {
        console.log('🎨 渲染列表，数量:', list ? list.length : 0, '静默模式:', silent);
        let html = '';

        if (list && list.length > 0) {
            $.each(list, function(i, item) {
                const typeMap = {
                    'emergency': {text:'紧急通知', cls:'type-emergency', badge:'badge-danger', icon:'fa-exclamation-triangle'},
                    'notice': {text:'普通通知', cls:'type-notice', badge:'badge-primary', icon:'fa-info-circle'},
                    'payment_reminder': {text:'缴费提醒', cls:'type-payment_reminder', badge:'badge-warning', icon:'fa-credit-card'},
                    'maintenance': {text:'维修公告', cls:'type-maintenance', badge:'badge-info', icon:'fa-tools'}
                };
                const typeInfo = typeMap[item.announcementType] || typeMap['notice'];

                const priorityMap = {
                    'urgent': {text:'紧急', cls:'priority-urgent'},
                    'important': {text:'重要', cls:'priority-important'},
                    'normal': {text:'普通', cls:'priority-normal'}
                };
                const priorityInfo = priorityMap[item.priority] || priorityMap['normal'];

                const viewCount = item.viewCount || 0;
                let viewHtml = '';
                if (viewCount >= 100) {
                    viewHtml = '<span class="view-count hot">' +
                        '<i class="fas fa-fire"></i>' +
                        '<strong>' + viewCount + '</strong> 次浏览' +
                        '<span class="hot-badge">HOT</span>' +
                        '</span>';
                } else if (viewCount >= 50) {
                    viewHtml = '<span class="view-count" style="color: #ff6b6b; font-weight: 600;">' +
                        '<i class="fas fa-eye" style="color: #ff6b6b;"></i>' +
                        '<strong>' + viewCount + '</strong> 次浏览' +
                        '</span>';
                } else {
                    viewHtml = '<span class="view-count">' +
                        '<i class="far fa-eye"></i>' +
                        viewCount + ' 次浏览' +
                        '</span>';
                }

                const isNew = isNewAnnouncement(item.publishTime);
                const timeHtml = isNew ?
                    '<span class="time-badge new"><i class="fas fa-star"></i>NEW</span>' :
                    '<span class="time-badge"><i class="far fa-clock"></i>' + formatDate(item.publishTime) + '</span>';

                const dataStr = encodeURIComponent(JSON.stringify(item));

                html += '<div class="col-md-12">' +
                    '<div class="card notice-card shadow-sm ' + typeInfo.cls + '" onclick="showDetail(\'' + dataStr + '\')">' +
                    '<div class="card-body">' +
                    '<div class="d-flex justify-content-between align-items-start">' +
                    '<div class="flex-grow-1">' +
                    '<h5 class="mb-3">' +
                    '<i class="fas ' + typeInfo.icon + ' mr-2" style="color: ' + getBorderColor(item.announcementType) + ';"></i>' +
                    '<span class="badge ' + typeInfo.badge + ' mr-2">' + typeInfo.text + '</span>' +
                    '<span class="badge ' + priorityInfo.cls + ' priority-badge mr-2">' + priorityInfo.text + '</span>' +
                    escapeHtml(item.title) +
                    '</h5>' +
                    '<p class="mb-3 text-muted" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.6;">' +
                    escapeHtml(item.content || '').substring(0, 150) + '...' +
                    '</p>' +
                    '<div class="d-flex justify-content-between align-items-center">' +
                    '<div>' +
                    timeHtml +
                    '<span class="ml-3">' + viewHtml + '</span>' +
                    '</div>' +
                    '<small class="text-primary font-weight-bold">点击查看详情 <i class="fas fa-chevron-right ml-1"></i></small>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>';
            });
        } else {
            let emptyMessage = '暂无公告';
            let emptyDesc = '当前没有符合条件的公告信息';

            if (currentKeyword) {
                emptyMessage = '未找到相关公告';
                emptyDesc = '搜索 "' + escapeHtml(currentKeyword) + '" 没有找到相关内容';
            }

            html = '<div class="col-12">' +
                '<div class="empty-state">' +
                '<i class="fas fa-inbox"></i>' +
                '<h5>' + emptyMessage + '</h5>' +
                '<p class="text-muted">' + emptyDesc + '</p>' +
                '</div>' +
                '</div>';
        }

        $('#noticeList').html(html);
    }

    // ==================== 渲染分页 ====================
    function renderPagination(total) {
        const totalPages = Math.ceil(total / pageSize);
        let html = '';

        if (totalPages > 1) {
            // 上一页
            if (currentPage > 1) {
                html += '<li class="page-item">' +
                    '<a class="page-link" href="javascript:void(0)" onclick="loadData(' + (currentPage - 1) + ')">' +
                    '<i class="fas fa-chevron-left"></i> 上一页</a></li>';
            } else {
                html += '<li class="page-item disabled">' +
                    '<span class="page-link"><i class="fas fa-chevron-left"></i> 上一页</span></li>';
            }

            // 页码
            for (let i = 1; i <= totalPages; i++) {
                const activeClass = i === currentPage ? 'active' : '';
                html += '<li class="page-item ' + activeClass + '">' +
                    '<a class="page-link" href="javascript:void(0)" onclick="loadData(' + i + ')">' + i + '</a></li>';
            }

            // 下一页
            if (currentPage < totalPages) {
                html += '<li class="page-item">' +
                    '<a class="page-link" href="javascript:void(0)" onclick="loadData(' + (currentPage + 1) + ')">' +
                    '下一页 <i class="fas fa-chevron-right"></i></a></li>';
            } else {
                html += '<li class="page-item disabled">' +
                    '<span class="page-link">下一页 <i class="fas fa-chevron-right"></i></span></li>';
            }
        }

        $('#pagination').html(html);
    }

    // ==================== 🔥 渲染分页信息 ====================
    function renderPaginationInfo(total) {
        if (total > 0) {
            const totalPages = Math.ceil(total / pageSize);
            const start = (currentPage - 1) * pageSize + 1;
            const end = Math.min(currentPage * pageSize, total);

            const infoHtml = '显示第 <strong>' + start + '</strong> 到 <strong>' + end + '</strong> 条，' +
                '共 <strong>' + total + '</strong> 条记录，' +
                '第 <strong>' + currentPage + '</strong> / <strong>' + totalPages + '</strong> 页';

            $('#paginationInfo').html(infoHtml).show();
        } else {
            $('#paginationInfo').hide();
        }
    }

    // ==================== 显示详情 ====================
    function showDetail(dataStr) {
        const item = JSON.parse(decodeURIComponent(dataStr));

        $('#modalTitle').html('<i class="fas fa-spinner fa-spin mr-2"></i>加载中...');
        $('#modalTime').text('--');
        $('#modalPublisher').text('--');
        $('#modalViewCount').html('--');
        $('#modalExpiry').text('--');
        $('#modalContent').html('<div class="text-center py-5">' +
            '<i class="fas fa-spinner fa-spin fa-3x text-muted mb-3"></i>' +
            '<p class="text-muted">正在加载公告内容...</p>' +
            '</div>');
        $('#detailModal').modal('show');

        $.ajax({
            url: '${pageContext.request.contextPath}/owner/announcement',
            type: 'GET',
            data: {
                method: 'detail',
                id: item.announcementId
            },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 详情返回:', res);

                if (res.code === 200 && res.data) {
                    const detail = res.data;

                    $('#modalTitle').text(detail.title);
                    $('#modalTime').text(formatDateTime(detail.publishTime));
                    $('#modalPublisher').text(detail.publisherName || '物业中心');

                    const viewCount = detail.viewCount || 0;
                    let viewCountHtml = '';
                    if (viewCount >= 100) {
                        viewCountHtml = '<span class="view-count-display hot">' +
                            '<i class="fas fa-fire"></i>' +
                            '<strong>' + viewCount + '</strong> 次' +
                            '</span>';
                    } else if (viewCount >= 50) {
                        viewCountHtml = '<span class="view-count-display popular">' +
                            '<i class="fas fa-eye"></i>' +
                            '<strong>' + viewCount + '</strong> 次' +
                            '</span>';
                    } else {
                        viewCountHtml = '<span class="view-count-display normal">' +
                            '<i class="far fa-eye"></i>' +
                            '<strong>' + viewCount + '</strong> 次' +
                            '</span>';
                    }
                    $('#modalViewCount').html(viewCountHtml);

                    $('#modalExpiry').text(detail.expiryTime ? formatDate(detail.expiryTime) : '长期有效');
                    $('#modalContent').text(detail.content || '暂无内容');

                    $('#detailModal').one('hidden.bs.modal', function() {
                        console.log('🔄 模态框关闭，静默刷新列表');
                        loadData(currentPage, true);
                    });

                } else {
                    $('#modalTitle').html('<i class="fas fa-exclamation-triangle text-warning mr-2"></i>加载失败');
                    $('#modalContent').html('<div class="alert alert-warning mb-0">无法加载公告内容：' + (res.msg || '未知错误') + '</div>');
                }
            },
            error: function(xhr) {
                console.error('❌ 请求详情失败:', xhr);
                $('#modalTitle').html('<i class="fas fa-times-circle text-danger mr-2"></i>网络错误');
                $('#modalContent').html('<div class="alert alert-danger mb-0">无法连接服务器，请检查网络后重试</div>');
            }
        });
    }

    // ==================== 显示空状态 ====================
    function showEmpty(message) {
        $('#noticeList').html(
            '<div class="col-12">' +
            '<div class="empty-state">' +
            '<i class="fas fa-exclamation-circle"></i>' +
            '<h5>' + message + '</h5>' +
            '</div>' +
            '</div>'
        );
        $('#pagination').html('');
        $('#paginationInfo').hide();
    }

    // ==================== 工具函数 ====================
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

    function isNewAnnouncement(publishTime) {
        if (!publishTime) return false;
        const now = new Date().getTime();
        const publish = new Date(publishTime).getTime();
        const diff = now - publish;
        return diff < 24 * 60 * 60 * 1000;
    }

    function getBorderColor(type) {
        const colorMap = {
            'emergency': '#dc3545',
            'notice': '#007bff',
            'payment_reminder': '#ffc107',
            'maintenance': '#17a2b8'
        };
        return colorMap[type] || '#007bff';
    }
</script>
</body>
</html>
