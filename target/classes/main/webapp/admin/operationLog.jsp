<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>操作日志 - 物业管理系统</title>

    <!-- 引入 CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/theme/default/layer.css">

    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }

        /* 侧边栏样式 */
        .sidebar {
            position: fixed; left: 0; top: 0; bottom: 0; width: 260px;
            background: linear-gradient(180deg, #1e3c72 0%, #2a5298 100%);
            color: white; overflow-y: auto; z-index: 1000;
            box-shadow: 4px 0 20px rgba(0,0,0,0.3);
            transition: transform 0.3s ease;
        }

        .sidebar.collapsed { transform: translateX(-260px); }

        .sidebar-header {
            padding: 30px 20px;
            background: rgba(0,0,0,0.3);
            border-bottom: 1px solid rgba(255,255,255,0.1);
            text-align: center;
        }

        .sidebar-header .logo {
            width: 60px; height: 60px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%; display: flex; align-items: center; justify-content: center;
            margin: 0 auto 15px; font-size: 28px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        }

        .sidebar-header h3 { font-size: 18px; font-weight: 600; margin-bottom: 8px; }
        .sidebar-header p { font-size: 13px; opacity: 0.8; margin: 0; }

        .sidebar-menu { list-style: none; padding: 20px 0; }
        .sidebar-menu li { margin-bottom: 3px; }
        .sidebar-menu a {
            display: flex; align-items: center; padding: 14px 25px;
            color: rgba(255,255,255,0.85); text-decoration: none;
            transition: all 0.3s; position: relative;
        }
        .sidebar-menu a::before {
            content: ''; position: absolute; left: 0; top: 0; bottom: 0;
            width: 4px; background: #667eea; transform: scaleY(0);
            transition: transform 0.3s;
        }
        .sidebar-menu a:hover {
            background: rgba(255,255,255,0.1); color: white; padding-left: 30px;
        }
        .sidebar-menu a:hover::before { transform: scaleY(1); }
        .sidebar-menu a.active {
            background: linear-gradient(90deg, rgba(102, 126, 234, 0.3) 0%, transparent 100%);
            color: white; border-left: 4px solid #667eea;
        }
        .sidebar-menu i { width: 22px; margin-right: 12px; font-size: 15px; }

        .sidebar-footer {
            position: absolute; bottom: 0; left: 0; right: 0;
            padding: 20px; background: rgba(0,0,0,0.3);
        }

        /* 侧边栏切换按钮 */
        .sidebar-toggle {
            position: fixed; left: 270px; top: 20px; z-index: 1002;
            width: 45px; height: 45px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; border: none; border-radius: 50%;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            cursor: pointer; display: flex; align-items: center; justify-content: center;
            transition: all 0.3s ease; font-size: 18px;
        }
        .sidebar-toggle:hover {
            transform: scale(1.1) rotate(90deg);
            box-shadow: 0 6px 16px rgba(0,0,0,0.4);
        }
        .sidebar-toggle.collapsed { left: 20px; }

        /* 主内容区 */
        .main-content {
            margin-left: 260px; padding: 30px; min-height: 100vh;
            transition: margin-left 0.3s ease;
        }
        .main-content.expanded { margin-left: 0; }

        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; padding: 40px 30px; border-radius: 15px;
            margin-bottom: 30px; box-shadow: 0 8px 24px rgba(0,0,0,0.2);
            position: relative; overflow: hidden;
            z-index: 1;
        }
        .page-header::before {
            content: ''; position: absolute; top: -50%; right: -5%;
            width: 300px; height: 300px; background: rgba(255,255,255,0.1);
            border-radius: 50%; animation: float 6s ease-in-out infinite;
        }
        @keyframes float {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }
        .page-header h2 {
            margin: 0; font-size: 32px; font-weight: 700;
            position: relative; z-index: 1;
        }
        .page-header p {
            margin: 12px 0 0 0; opacity: 0.95; font-size: 15px;
            position: relative; z-index: 1;
        }

        /* 搜索卡片 */
        .search-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            position: relative;
            z-index: 100;
        }

        /* 表格卡片 */
        .table-card {
            background: white; border-radius: 15px; padding: 30px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            position: relative;
            z-index: 1;
        }

        .table-responsive {
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
        }
        .table {
            margin-bottom: 0;
            white-space: nowrap;
        }
        .table thead th {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; border: none; font-weight: 600;
            padding: 15px 12px; font-size: 14px;
            position: sticky; top: 0; z-index: 10;
        }
        .table tbody td {
            padding: 14px 12px; vertical-align: middle;
            font-size: 13px;
        }
        .table tbody tr { transition: all 0.3s; }
        .table tbody tr:hover {
            background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, transparent 100%);
            transform: scale(1.01);
        }

        /* 标签样式 */
        .label {
            display: inline-block; padding: 6px 14px; border-radius: 20px;
            font-size: 12px; font-weight: 600; white-space: nowrap;
        }
        .label-success { background-color: #e8f5e9; color: #388e3c; }
        .label-info { background-color: #e3f2fd; color: #1976d2; }
        .label-warning { background-color: #fff3e0; color: #f57c00; }
        .label-danger { background-color: #ffebee; color: #d32f2f; }
        .label-primary { background-color: #e8eaf6; color: #3f51b5; }
        .label-default { background-color: #f5f5f5; color: #616161; }

        /* 按钮样式 */
        .btn-action {
            padding: 6px 12px; font-size: 12px; margin: 2px;
            border-radius: 20px; transition: all 0.3s;
            white-space: nowrap;
        }
        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        }

        /* 表单样式 */
        .form-group label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 8px;
            font-size: 14px;
            white-space: nowrap;
        }

        .form-control {
            border-radius: 8px;
            border: 2px solid #e9ecef;
            padding: 10px 15px;
            transition: all 0.3s;
            background-color: white;
            color: #495057;
            font-size: 14px;
            line-height: 1.5;
            position: relative;
            z-index: 1;
        }

        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
            outline: none;
            z-index: 200;
        }

        /* 修复 select 下拉框样式 */
        select.form-control {
            appearance: auto;
            -webkit-appearance: auto;
            -moz-appearance: auto;
            cursor: pointer;
            background-color: white;
            color: #495057;
            font-size: 14px;
            padding-right: 30px;
            height: auto;
            min-height: 42px;
        }

        /* 下拉选项样式 */
        select.form-control option {
            padding: 10px 15px;
            background-color: white;
            color: #212529;
            font-size: 14px;
            line-height: 1.5;
        }

        /* 确保表单内联元素正确显示 */
        .form-inline {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
        }

        .form-inline .form-group {
            display: flex;
            align-items: center;
            margin-right: 15px;
            margin-bottom: 10px;
        }

        .form-inline .form-group label {
            margin-bottom: 0;
            margin-right: 8px;
        }

        .form-inline .form-control {
            display: inline-block;
            width: auto;
            vertical-align: middle;
        }

        /* 分页样式 */
        .pagination {
            margin-top: 20px;
            display: flex;
            justify-content: center;
        }
        .pagination .page-link {
            color: #667eea;
            border: 1px solid #dee2e6;
            margin: 0 3px;
            border-radius: 8px;
        }
        .pagination .page-item.active .page-link {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #667eea;
        }

        /* 模态框样式 */
        .modal-content {
            border-radius: 15px; overflow: hidden;
            border: none; box-shadow: 0 10px 40px rgba(0,0,0,0.3);
        }
        .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; padding: 25px 30px; border: none;
        }
        .modal-header .modal-title {
            font-size: 22px; font-weight: 600;
        }
        .modal-header .close {
            color: white; opacity: 0.9; text-shadow: none;
            font-size: 32px; font-weight: 300;
        }
        .modal-header .close:hover { opacity: 1; }
        .modal-body { padding: 30px; max-height: 70vh; overflow-y: auto; }
        .modal-footer {
            padding: 20px 30px; background: #f8f9fa;
            border-top: 1px solid #e9ecef;
        }

        /* 详情样式 */
        .detail-item {
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #f0f0f0;
        }
        .detail-item:last-child {
            border-bottom: none;
        }
        .detail-item label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 8px;
            display: block;
            font-size: 14px;
        }
        .detail-item .value {
            color: #212529;
            font-size: 15px;
            line-height: 1.6;
        }

        /* 响应式 */
        @media (max-width: 768px) {
            .sidebar { transform: translateX(-260px); }
            .sidebar-toggle { left: 20px; z-index: 1002; }
            .main-content { margin-left: 0; padding: 15px; }
            .page-header { padding: 30px 20px; }
            .page-header h2 { font-size: 24px; }
            .search-card, .table-card { padding: 20px 15px; }

            /* 移动端表单样式 */
            .form-inline {
                flex-direction: column;
                align-items: stretch;
            }
            .form-inline .form-group {
                width: 100%;
                margin-right: 0;
            }
            .form-inline .form-control {
                width: 100%;
            }
        }
    </style>
</head>
<body>

<!-- 侧边栏切换按钮 -->
<button class="sidebar-toggle" id="sidebarToggle" onclick="toggleSidebar()">
    <i class="fas fa-bars"></i>
</button>

<!-- 侧边栏 -->
<div class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <div class="logo">
            <i class="fas fa-building"></i>
        </div>
        <h3>物业管理系统</h3>
        <p><i class="fas fa-user-shield"></i> ${sessionScope.currentUser.realName}</p>
    </div>

    <ul class="sidebar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/admin/index.jsp">
                <i class="fas fa-home"></i> 系统首页
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/user.jsp">
                <i class="fas fa-users-cog"></i> 用户管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/owner.jsp">
                <i class="fas fa-users"></i> 业主管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/house.jsp">
                <i class="fas fa-building"></i> 房屋管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/chargeItem.jsp">
                <i class="fas fa-list-alt"></i> 收费项目
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/payment.jsp">
                <i class="fas fa-credit-card"></i> 缴费管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/repair.jsp">
                <i class="fas fa-tools"></i> 报修管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/complaint.jsp">
                <i class="fas fa-comments"></i> 投诉管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/announcement.jsp">
                <i class="fas fa-bullhorn"></i> 公告管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/statistics.jsp">
                <i class="fas fa-chart-bar"></i> 统计报表
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/operationLog.jsp" class="active">
                <i class="fas fa-history"></i> 操作日志
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/backup.jsp">
                <i class="fas fa-database"></i> 备份管理
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/login?method=logout" class="btn btn-danger btn-block">
            <i class="fas fa-sign-out-alt"></i> 退出登录
        </a>
    </div>
</div>

<!-- 主内容区 -->
<div class="main-content" id="mainContent">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-history"></i> 操作日志</h2>
        <p>查看系统操作记录，追踪用户行为</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-card">
        <form id="searchForm" class="form-inline">
            <div class="form-group mr-3 mb-2">
                <label class="mr-2">关键词：</label>
                <input type="text" class="form-control" id="keyword" name="keyword" placeholder="用户名/描述" style="width: 180px;">
            </div>

            <div class="form-group mr-3 mb-2">
                <label class="mr-2">功能模块：</label>
                <select class="form-control" id="module" name="module" style="width: 150px;">
                    <option value="">全部模块</option>
                    <option value="login">用户认证</option>
                    <option value="owner">业主管理</option>
                    <option value="house">房屋管理</option>
                    <option value="payment">收费管理</option>
                    <option value="charge_item">收费项目</option>
                    <option value="repair">报修管理</option>
                    <option value="complaint">投诉管理</option>
                    <option value="announcement">公告管理</option>
                    <option value="user">用户管理</option>
                    <option value="backup">系统管理</option>
                </select>
            </div>

            <div class="form-group mr-3 mb-2">
                <label class="mr-2">时间范围：</label>
                <select class="form-control" id="timeRange" name="timeRange" onchange="setTimeRange(this.value)" style="width: 130px;">
                    <option value="">自定义</option>
                    <option value="today">今天</option>
                    <option value="yesterday">昨天</option>
                    <option value="week">最近7天</option>
                    <option value="month">最近30天</option>
                </select>
            </div>

            <div class="form-group mr-3 mb-2">
                <label class="mr-2">开始日期：</label>
                <input type="date" class="form-control" id="startDate" name="startDate" style="width: 160px;">
            </div>

            <div class="form-group mr-3 mb-2">
                <label class="mr-2">结束日期：</label>
                <input type="date" class="form-control" id="endDate" name="endDate" style="width: 160px;">
            </div>

            <button type="button" class="btn btn-primary mb-2 mr-2" onclick="searchLogs()">
                <i class="fas fa-search"></i> 查询
            </button>
            <button type="button" class="btn btn-secondary mb-2 mr-2" onclick="resetSearch()">
                <i class="fas fa-redo"></i> 重置
            </button>
            <button type="button" class="btn btn-success mb-2" onclick="exportLogs()">
                <i class="fas fa-file-excel"></i> 导出
            </button>
        </form>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
        <div class="table-responsive">
            <table class="table table-hover" id="logTable">
                <thead>
                <tr>
                    <th>日志ID</th>
                    <th>用户名</th>
                    <th>功能模块</th>
                    <th>操作描述</th>
                    <th>IP地址</th>
                    <th>操作时间</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <tr><td colspan="7" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>
                </tbody>
            </table>
        </div>

        <!-- 分页 -->
        <nav>
            <ul class="pagination" id="pagination"></ul>
        </nav>
    </div>
</div>

<!-- 日志详情模态框 -->
<div class="modal fade" id="logDetailModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-info-circle"></i> 日志详情</h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" id="logDetailContent">
                <div class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 关闭
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 引入 JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/layer.js"></script>

<script>
    var currentPage = 1;
    var pageSize = 10;

    $(function() {
        console.log('🚀 操作日志页面初始化...');
        loadLogs(1);

        // 检查屏幕宽度，移动端默认隐藏侧边栏
        if ($(window).width() <= 768) {
            toggleSidebar();
        }
    });

    // 切换侧边栏
    function toggleSidebar() {
        var sidebar = $('#sidebar');
        var mainContent = $('#mainContent');
        var toggleBtn = $('#sidebarToggle');

        sidebar.toggleClass('collapsed');
        mainContent.toggleClass('expanded');
        toggleBtn.toggleClass('collapsed');

        var icon = toggleBtn.find('i');
        if (sidebar.hasClass('collapsed')) {
            icon.removeClass('fa-times').addClass('fa-bars');
        } else {
            icon.removeClass('fa-bars').addClass('fa-times');
        }
    }

    // 时间范围快捷选择
    function setTimeRange(range) {
        var today = new Date();
        var startDate = new Date();
        var endDate = new Date();

        switch(range) {
            case 'today':
                startDate = today;
                endDate = today;
                break;
            case 'yesterday':
                startDate.setDate(today.getDate() - 1);
                endDate.setDate(today.getDate() - 1);
                break;
            case 'week':
                startDate.setDate(today.getDate() - 7);
                break;
            case 'month':
                startDate.setDate(today.getDate() - 30);
                break;
            default:
                $('#startDate').val('');
                $('#endDate').val('');
                return;
        }

        $('#startDate').val(formatDateInput(startDate));
        $('#endDate').val(formatDateInput(endDate));
    }

    // 格式化日期为 YYYY-MM-DD
    function formatDateInput(date) {
        return date.getFullYear() + '-' +
            String(date.getMonth() + 1).padStart(2, '0') + '-' +
            String(date.getDate()).padStart(2, '0');
    }

    // 加载日志列表
    function loadLogs(pageNum) {
        currentPage = pageNum;
        var keyword = $('#keyword').val();
        var module = $('#module').val();
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();

        console.log('📋 加载日志列表:', {pageNum, keyword, module, startDate, endDate});

        $.ajax({
            url: '${pageContext.request.contextPath}/operationLog',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: pageNum,
                pageSize: pageSize,
                keyword: keyword,
                module: module,
                startDate: startDate,
                endDate: endDate
            },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 完整响应数据:', result);

                // 兼容多种响应格式
                var success = false;
                var list = [];
                var total = 0;

                // 格式1: {code: 200, data: {list: [...], total: 741}}
                if (result.code === 200 && result.data) {
                    success = true;
                    list = result.data.list || [];
                    total = result.data.total || 0;
                }
                // 格式2: {success: true, data: {list: [...], total: 741}}
                else if (result.success && result.data) {
                    success = true;
                    list = result.data.list || [];
                    total = result.data.total || 0;
                }
                // 格式3: {list: [...], total: 741}
                else if (result.list) {
                    success = true;
                    list = result.list;
                    total = result.total || 0;
                }

                console.log('📊 解析结果:', {success, listLength: list.length, total});

                if (success && list.length > 0) {
                    console.log('📊 第一条数据:', list[0]);
                    renderTable(list);
                    renderPagination(total, pageNum);
                } else if (success && list.length === 0) {
                    $('#logTable tbody').html('<tr><td colspan="7" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无数据</td></tr>');
                    $('#pagination').empty();
                } else {
                    layer.msg(result.msg || result.message || '加载失败', {icon: 2});
                    $('#logTable tbody').html('<tr><td colspan="7" class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</td></tr>');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载失败:', {status, error});
                console.error('❌ 响应文本:', xhr.responseText);
                layer.msg('网络错误', {icon: 2});
                $('#logTable tbody').html('<tr><td colspan="7" class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</td></tr>');
            }
        });
    }

    // 渲染表格
    function renderTable(logs) {
        var tbody = $('#logTable tbody');
        tbody.empty();

        console.log('📊 开始渲染表格，数据条数:', logs.length);

        if (!logs || logs.length === 0) {
            tbody.append('<tr><td colspan="7" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无数据</td></tr>');
            return;
        }

        logs.forEach(function(log, index) {
            // 兼容驼峰命名和下划线命名
            var logId = log.logId || log.log_id;
            var username = log.username || log.user_name;
            var operationType = log.operationType || log.operation_type;
            var operationDesc = log.operationDesc || log.operation_desc;
            var ipAddress = log.ipAddress || log.ip_address;
            var operationTime = log.operationTime || log.operation_time;

            // 获取模块名称和样式
            var moduleName = getModuleName(operationType);
            var moduleClass = getModuleClass(operationType);

            var tr = '<tr>' +
                '<td>' + (logId || '-') + '</td>' +
                '<td><i class="fas fa-user"></i> ' + (username || '-') + '</td>' +
                '<td><span class="label ' + moduleClass + '">' + moduleName + '</span></td>' +
                '<td style="max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="' + (operationDesc || '') + '">' +
                (operationDesc || '-') +
                '</td>' +
                '<td><i class="fas fa-map-marker-alt"></i> ' + (ipAddress || '-') + '</td>' +
                '<td>' + formatDateTime(operationTime) + '</td>' +
                '<td>' +
                '<button class="btn btn-sm btn-info btn-action" onclick="viewLog(' + logId + ')" title="查看详情"><i class="fas fa-eye"></i></button>' +
                '</td>' +
                '</tr>';
            tbody.append(tr);
        });

        console.log('✅ 表格渲染完成，共 ' + logs.length + ' 条记录');
    }

    // 渲染分页
    function renderPagination(total, currentPage) {
        var totalPages = Math.ceil(total / pageSize);
        var pagination = $('#pagination');
        pagination.empty();

        console.log('📄 渲染分页: total=' + total + ', currentPage=' + currentPage + ', totalPages=' + totalPages);

        if (totalPages <= 1) return;

        // 上一页
        var prevClass = currentPage === 1 ? 'disabled' : '';
        pagination.append('<li class="page-item ' + prevClass + '"><a class="page-link" href="javascript:void(0)" onclick="loadLogs(' + (currentPage - 1) + ')">上一页</a></li>');

        // 页码
        var startPage = Math.max(1, currentPage - 2);
        var endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            pagination.append('<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadLogs(1)">1</a></li>');
            if (startPage > 2) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
        }

        for (var i = startPage; i <= endPage; i++) {
            var activeClass = i === currentPage ? 'active' : '';
            pagination.append('<li class="page-item ' + activeClass + '"><a class="page-link" href="javascript:void(0)" onclick="loadLogs(' + i + ')">' + i + '</a></li>');
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
            pagination.append('<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadLogs(' + totalPages + ')">' + totalPages + '</a></li>');
        }

        // 下一页
        var nextClass = currentPage === totalPages ? 'disabled' : '';
        pagination.append('<li class="page-item ' + nextClass + '"><a class="page-link" href="javascript:void(0)" onclick="loadLogs(' + (currentPage + 1) + ')">下一页</a></li>');
    }

    // 查询日志
    function searchLogs() {
        loadLogs(1);
    }

    // 重置搜索
    function resetSearch() {
        $('#searchForm')[0].reset();
        loadLogs(1);
    }

    // 导出日志
    function exportLogs() {
        var keyword = $('#keyword').val();
        var module = $('#module').val();
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();

        var params = 'method=export';
        if (keyword) params += '&keyword=' + encodeURIComponent(keyword);
        if (module) params += '&module=' + encodeURIComponent(module);
        if (startDate) params += '&startDate=' + encodeURIComponent(startDate);
        if (endDate) params += '&endDate=' + encodeURIComponent(endDate);

        window.location.href = '${pageContext.request.contextPath}/operationLog?' + params;
    }

    // 查看日志详情
    function viewLog(logId) {
        console.log('👁️ 查看日志详情:', logId);
        $('#logDetailModal').modal('show');
        $('#logDetailContent').html('<div class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>');

        $.ajax({
            url: '${pageContext.request.contextPath}/operationLog',
            type: 'GET',
            data: { method: 'findById', logId: logId },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 日志详情:', result);

                var success = result.code === 200 || result.success;
                var log = result.data || result;

                if (success && log) {
                    var logId = log.logId || log.log_id;
                    var username = log.username || log.user_name;
                    var operationType = log.operationType || log.operation_type;
                    var operationDesc = log.operationDesc || log.operation_desc;
                    var ipAddress = log.ipAddress || log.ip_address;
                    var operationTime = log.operationTime || log.operation_time;

                    var moduleName = getModuleName(operationType);
                    var moduleClass = getModuleClass(operationType);

                    var html = '<div class="detail-item">' +
                        '<label><i class="fas fa-hashtag"></i> 日志ID</label>' +
                        '<div class="value">' + (logId || '-') + '</div>' +
                        '</div>' +
                        '<div class="detail-item">' +
                        '<label><i class="fas fa-user"></i> 用户名</label>' +
                        '<div class="value">' + (username || '-') + '</div>' +
                        '</div>' +
                        '<div class="detail-item">' +
                        '<label><i class="fas fa-tag"></i> 功能模块</label>' +
                        '<div class="value"><span class="label ' + moduleClass + '">' + moduleName + '</span></div>' +
                        '</div>' +
                        '<div class="detail-item">' +
                        '<label><i class="fas fa-align-left"></i> 操作描述</label>' +
                        '<div class="value">' + (operationDesc || '-') + '</div>' +
                        '</div>' +
                        '<div class="detail-item">' +
                        '<label><i class="fas fa-map-marker-alt"></i> IP地址</label>' +
                        '<div class="value">' + (ipAddress || '-') + '</div>' +
                        '</div>' +
                        '<div class="detail-item">' +
                        '<label><i class="fas fa-clock"></i> 操作时间</label>' +
                        '<div class="value">' + formatDateTime(operationTime) + '</div>' +
                        '</div>';
                    $('#logDetailContent').html(html);
                } else {
                    $('#logDetailContent').html('<div class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</div>');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载详情失败:', error);
                $('#logDetailContent').html('<div class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</div>');
            }
        });
    }

    // 根据操作类型获取模块名称
    function getModuleName(type) {
        if (!type) return '未知模块';

        // 用户认证
        if (type.includes('login') || type.includes('logout') || type.includes('register') || type.includes('password')) {
            return '用户认证';
        }
        // 业主管理
        else if (type.includes('owner')) {
            return '业主管理';
        }
        // 房屋管理
        else if (type.includes('house')) {
            return '房屋管理';
        }
        // 收费管理
        else if (type.includes('payment')) {
            return '收费管理';
        }
        // 收费项目
        else if (type.includes('charge_item')) {
            return '收费项目';
        }
        // 报修管理
        else if (type.includes('repair')) {
            return '报修管理';
        }
        // 投诉管理
        else if (type.includes('complaint')) {
            return '投诉管理';
        }
        // 公告管理
        else if (type.includes('announcement')) {
            return '公告管理';
        }
        // 用户管理
        else if (type.includes('user')) {
            return '用户管理';
        }
        // 系统管理
        else if (type.includes('backup')) {
            return '系统管理';
        }

        return '其他操作';
    }

    // 根据操作类型获取样式类
    function getModuleClass(type) {
        if (!type) return 'label-default';

        if (type.includes('login') || type.includes('logout')) return 'label-info';
        if (type.includes('add') || type.includes('register')) return 'label-success';
        if (type.includes('delete')) return 'label-danger';
        if (type.includes('update') || type.includes('process')) return 'label-warning';
        if (type.includes('payment') || type.includes('charge')) return 'label-primary';

        return 'label-default';
    }

    // 格式化日期时间
    function formatDateTime(dateTime) {
        if (!dateTime) return '-';
        try {
            var date;
            if (typeof dateTime === 'string') {
                dateTime = dateTime.replace('T', ' ').split('.')[0];
                date = new Date(dateTime);
            } else {
                date = new Date(dateTime);
            }
            if (isNaN(date.getTime())) return dateTime;
            return date.getFullYear() + '-' +
                String(date.getMonth() + 1).padStart(2, '0') + '-' +
                String(date.getDate()).padStart(2, '0') + ' ' +
                String(date.getHours()).padStart(2, '0') + ':' +
                String(date.getMinutes()).padStart(2, '0') + ':' +
                String(date.getSeconds()).padStart(2, '0');
        } catch (e) {
            console.error('日期格式化错误:', e);
            return dateTime;
        }
    }
</script>

</body>
</html>
