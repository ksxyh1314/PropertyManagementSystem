<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.property.entity.User" %>
<%
    System.out.println("========================================");
    System.out.println("【finance.jsp 开始加载】");
    System.out.println("请求时间: " + new java.util.Date());

    // 检查 Session
    HttpSession currentSession = request.getSession(false);
    System.out.println("Session 是否存在: " + (currentSession != null));

    if (currentSession != null) {
        System.out.println("Session ID: " + currentSession.getId());
        System.out.println("Session 创建时间: " + new java.util.Date(currentSession.getCreationTime()));
        System.out.println("Session 最后访问时间: " + new java.util.Date(currentSession.getLastAccessedTime()));
    }

    if (currentSession == null) {
        System.out.println("❌ Session 不存在,重定向到登录页");
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // 检查用户(尝试两种 key)
    User currentUser = (User) currentSession.getAttribute("user");
    if (currentUser == null) {
        currentUser = (User) currentSession.getAttribute("currentUser");
    }

    System.out.println("User 对象: " + currentUser);

    if (currentUser == null) {
        System.out.println("❌ 用户未登录,重定向到登录页");
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // 检查权限
    String userRole = currentUser.getUserRole();
    System.out.println("用户信息:");
    System.out.println("  - 用户名: " + currentUser.getUsername());
    System.out.println("  - 真实姓名: " + currentUser.getRealName());
    System.out.println("  - 角色: " + userRole);

    if (!"admin".equals(userRole) && !"finance".equals(userRole)) {
        System.out.println("❌ 权限不足: " + userRole);
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    System.out.println("✅ 权限验证通过");
    System.out.println("========================================");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>财务管理 - 物业管理系统</title>
    <!-- CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background: #f5f7fa;
            font-family: "Microsoft YaHei", Arial, sans-serif;
        }

        /* ========== 导航栏 ========== */
        .navbar {
            background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%) !important;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 12px 0;
        }

        .navbar-brand {
            font-weight: bold;
            font-size: 22px;
            color: white !important;
        }

        .navbar-brand:hover {
            color: #3498db !important;
        }

        .navbar-nav .nav-link {
            color: rgba(255,255,255,0.8) !important;
            padding: 8px 16px !important;
            margin: 0 5px;
            border-radius: 5px;
            transition: all 0.3s;
        }

        .navbar-nav .nav-link:hover {
            background: rgba(255,255,255,0.1);
            color: white !important;
        }

        .navbar-nav .nav-link.active {
            background: rgba(52, 152, 219, 0.8);
            color: white !important;
        }

        /* ========== 页面标题 ========== */
        .finance-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 35px;
            border-radius: 12px;
            margin-bottom: 30px;
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }

        .finance-header h2 {
            margin: 0;
            font-size: 32px;
            font-weight: bold;
        }

        .finance-header p {
            margin: 12px 0 0 0;
            opacity: 0.95;
            font-size: 15px;
        }

        /* ========== 统计卡片 ========== */
        .stat-card {
            background: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
            transition: all 0.3s ease;
            border-left: 4px solid transparent;
        }

        .stat-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }

        .stat-card.danger {
            border-left-color: #f5576c;
        }

        .stat-card.warning {
            border-left-color: #fcb69f;
        }

        .stat-card.info {
            border-left-color: #4facfe;
        }

        .stat-card.success {
            border-left-color: #43e97b;
        }

        .stat-card .stat-icon {
            width: 70px;
            height: 70px;
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 32px;
            color: white;
            float: left;
            margin-right: 20px;
        }

        .stat-card.danger .stat-icon {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        .stat-card.warning .stat-icon {
            background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
        }

        .stat-card.info .stat-icon {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        .stat-card.success .stat-icon {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }

        .stat-card .stat-content {
            overflow: hidden;
            padding-top: 5px;
        }

        .stat-card .stat-title {
            font-size: 14px;
            color: #999;
            margin-bottom: 8px;
            font-weight: 500;
        }

        .stat-card .stat-value {
            font-size: 32px;
            font-weight: bold;
            color: #333;
            line-height: 1.2;
        }

        .stat-card .stat-desc {
            font-size: 13px;
            color: #666;
            margin-top: 8px;
        }

        /* ========== Tab 导航 ========== */
        .nav-tabs {
            border-bottom: 2px solid #e9ecef;
            margin-bottom: 25px;
        }

        .nav-tabs .nav-link {
            border: none;
            color: #666;
            font-weight: 600;
            padding: 14px 28px;
            transition: all 0.3s;
            border-radius: 8px 8px 0 0;
            margin-right: 5px;
        }

        .nav-tabs .nav-link:hover {
            color: #667eea;
            background: #f8f9fa;
        }

        .nav-tabs .nav-link.active {
            color: #667eea;
            background: white;
            border-bottom: 3px solid #667eea;
        }

        /* ========== 搜索框 ========== */
        .search-box {
            background: white;
            padding: 25px;
            border-radius: 12px;
            margin-bottom: 25px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
        }

        .search-box .form-group {
            margin-bottom: 0;
        }

        .search-box label {
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            font-size: 14px;
        }

        .search-box .form-control {
            height: 42px;
            border-radius: 6px;
            border: 1px solid #ddd;
            transition: all 0.3s;
        }

        .search-box .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        /* ========== 表格 ========== */
        .table-container {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
        }

        .table {
            margin-bottom: 0;
        }

        .table thead th {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            color: #333;
            font-weight: 600;
            border-bottom: 2px solid #dee2e6;
            padding: 14px 12px;
            font-size: 14px;
            white-space: nowrap;
        }

        .table tbody td {
            padding: 14px 12px;
            vertical-align: middle;
            font-size: 14px;
        }

        .table tbody tr {
            transition: background-color 0.2s;
        }

        .table tbody tr:hover {
            background: #f8f9fa;
        }

        /* 汇总行样式 */
        .table tbody tr.summary-row {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            font-weight: bold;
            border-top: 3px solid #667eea;
            border-bottom: 3px solid #667eea;
        }

        .table tbody tr.summary-row td {
            color: #333;
            font-size: 15px;
        }

        /* ========== 徽章 ========== */
        .badge-overdue {
            background: linear-gradient(135deg, #f5576c 0%, #e74c3c 100%);
            color: white;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .badge-warning {
            background: linear-gradient(135deg, #fcb69f 0%, #f39c12 100%);
            color: white;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .badge-normal {
            background: linear-gradient(135deg, #43e97b 0%, #27ae60 100%);
            color: white;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        /* ========== 按钮 ========== */
        .btn {
            border-radius: 6px;
            padding: 10px 20px;
            font-weight: 600;
            transition: all 0.3s;
        }

        .btn-gradient {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
        }

        .btn-gradient:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
            color: white;
        }

        .btn-export {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            border: none;
            color: white;
        }

        .btn-export:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(67, 233, 123, 0.4);
            color: white;
        }

        .btn-primary {
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            border: none;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(52, 152, 219, 0.4);
        }

        /* 周期按钮组 */
        .cycle-buttons {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }

        .cycle-btn {
            flex: 1;
            padding: 12px;
            border: 2px solid #e9ecef;
            background: white;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            text-align: center;
            font-weight: 600;
            color: #666;
        }

        .cycle-btn:hover {
            border-color: #667eea;
            color: #667eea;
            transform: translateY(-2px);
        }

        .cycle-btn.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #667eea;
            color: white;
        }

        .cycle-btn i {
            margin-right: 8px;
            font-size: 18px;
        }

        /* ========== 分页 ========== */
        .pagination {
            margin-top: 25px;
            justify-content: center;
        }

        .pagination .page-link {
            color: #667eea;
            border: 1px solid #dee2e6;
            margin: 0 3px;
            border-radius: 6px;
            padding: 8px 14px;
            transition: all 0.3s;
        }

        .pagination .page-link:hover {
            background: #667eea;
            color: white;
            border-color: #667eea;
            transform: translateY(-2px);
        }

        .pagination .page-item.active .page-link {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #667eea;
        }

        /* ========== 空数据 ========== */
        .empty-data {
            text-align: center;
            padding: 80px 20px;
            color: #999;
        }

        .empty-data i {
            font-size: 80px;
            color: #ddd;
            margin-bottom: 20px;
        }

        .empty-data p {
            font-size: 16px;
            margin-top: 15px;
        }

        /* ========== 图表容器 ========== */
        .chart-container {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
            margin-bottom: 25px;
            height: 420px;
        }

        /* ========== 加载动画 ========== */
        .loading-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.6);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 9999;
        }

        .loading-overlay.show {
            display: flex;
        }

        .loading-spinner {
            background: white;
            padding: 40px;
            border-radius: 12px;
            text-align: center;
            box-shadow: 0 10px 40px rgba(0,0,0,0.3);
        }

        .loading-spinner i {
            font-size: 50px;
            color: #667eea;
            animation: spin 1s linear infinite;
        }

        .loading-spinner p {
            margin-top: 20px;
            color: #666;
            font-size: 16px;
            font-weight: 600;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* ========== 响应式 ========== */
        @media (max-width: 768px) {
            .stat-card .stat-icon {
                float: none;
                margin: 0 auto 15px;
            }

            .stat-card .stat-content {
                text-align: center;
            }

            .finance-header h2 {
                font-size: 24px;
            }

            .table-container {
                overflow-x: auto;
            }

            .cycle-buttons {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<!-- 导航栏 -->
<nav class="navbar navbar-expand-lg navbar-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/finance/index.jsp">
            <i class="fas fa-building"></i> 物业管理系统
        </a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item">
                    <a class="nav-link" href="<%=request.getContextPath()%>/finance/index.jsp">
                        <i class="fas fa-home"></i> 工作台
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="<%=request.getContextPath()%>/finance/finance.jsp">
                        <i class="fas fa-chart-line"></i> 财务管理
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%=request.getContextPath()%>/finance/payment.jsp">
                        <i class="fas fa-credit-card"></i> 缴费记录
                    </a>
                </li>
                <li class="nav-item">
                        <span class="nav-link">
                            <i class="fas fa-user-circle"></i> <%=currentUser.getRealName()%>
                        </span>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%=request.getContextPath()%>/login?action=logout">
                        <i class="fas fa-sign-out-alt"></i> 退出
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- 主内容区 -->
<div class="container-fluid" style="padding: 30px;">
    <!-- 页面标题 -->
    <div class="finance-header">
        <h2><i class="fas fa-chart-line"></i> 财务管理中心</h2>
        <p>欠费统计 · 收缴分析 · 催缴管理 · 数据报表</p>
    </div>

    <!-- 统计卡片 -->
    <div class="row">
        <div class="col-lg-3 col-md-6">
            <div class="stat-card danger">
                <div class="stat-icon">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                <div class="stat-content">
                    <div class="stat-title">逾期账单数</div>
                    <div class="stat-value" id="overdueCount">-</div>
                    <div class="stat-desc">需要及时处理</div>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stat-card warning">
                <div class="stat-icon">
                    <i class="fas fa-money-bill-wave"></i>
                </div>
                <div class="stat-content">
                    <div class="stat-title">逾期金额</div>
                    <div class="stat-value" id="overdueAmount">-</div>
                    <div class="stat-desc">本金 + 滞纳金</div>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stat-card info">
                <div class="stat-icon">
                    <i class="fas fa-clock"></i>
                </div>
                <div class="stat-content">
                    <div class="stat-title">平均逾期天数</div>
                    <div class="stat-value" id="avgOverdueDays">-</div>
                    <div class="stat-desc">需加强催缴</div>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stat-card success">
                <div class="stat-icon">
                    <i class="fas fa-hand-holding-usd"></i>
                </div>
                <div class="stat-content">
                    <div class="stat-title">滞纳金总额</div>
                    <div class="stat-value" id="totalLateFee">-</div>
                    <div class="stat-desc">已产生滞纳金</div>
                </div>
            </div>
        </div>
    </div>

    <!-- Tab 导航 -->
    <ul class="nav nav-tabs" id="financeTabs" role="tablist">
        <li class="nav-item">
            <a class="nav-link active" id="arrears-tab" data-toggle="tab" href="#arrears" role="tab">
                <i class="fas fa-users"></i> 欠费业主列表
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" id="period-tab" data-toggle="tab" href="#period" role="tab">
                <i class="fas fa-chart-bar"></i> 时间段统计
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" id="building-tab" data-toggle="tab" href="#building" role="tab">
                <i class="fas fa-building"></i> 楼栋收缴统计
            </a>
        </li>
    </ul>

    <!-- Tab 内容 -->
    <div class="tab-content">
        <!-- ========== Tab 1: 欠费业主列表 ========== -->
        <div class="tab-pane fade show active" id="arrears" role="tabpanel">
            <!-- 搜索框 -->
            <div class="search-box">
                <div class="row">
                    <div class="col-md-2">
                        <div class="form-group">
                            <label><i class="fas fa-user"></i> 业主姓名/电话</label>
                            <input type="text" class="form-control" id="keyword"
                                   placeholder="输入姓名或电话">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="form-group">
                            <label><i class="fas fa-dollar-sign"></i> 最低欠费金额</label>
                            <input type="number" class="form-control" id="minAmount"
                                   placeholder="如:500" value="0">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="form-group">
                            <label>&nbsp;</label>
                            <button class="btn btn-gradient btn-block" onclick="searchArrears()">
                                <i class="fas fa-search"></i> 查询
                            </button>
                        </div>
                    </div>
                    <div class="col-md-6 text-right">
                        <div class="form-group">
                            <label>&nbsp;</label>
                            <div>
                                <button class="btn btn-export" onclick="exportArrears()">
                                    <i class="fas fa-file-excel"></i> 导出Excel
                                </button>
                                <button class="btn btn-gradient" onclick="batchGenerateReminders()">
                                    <i class="fas fa-bell"></i> 批量催缴
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 表格 -->
            <div class="table-container">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th width="50">
                                <input type="checkbox" id="selectAll" onclick="toggleSelectAll()">
                            </th>
                            <th>业主ID</th>
                            <th>业主姓名</th>
                            <th>联系电话</th>
                            <th>房屋编号</th>
                            <th>位置</th>
                            <th>未缴笔数</th>
                            <th>欠费金额</th>
                            <th>滞纳金</th>
                            <th>欠费总额</th>
                            <th>最大逾期天数</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody id="arrearsTableBody">
                        <tr>
                            <td colspan="12" class="text-center">
                                <i class="fas fa-spinner fa-spin"></i> 加载中...
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>

                <!-- 分页 -->
                <nav>
                    <ul class="pagination" id="arrearsPagination"></ul>
                </nav>
            </div>
        </div>

        <!-- ========== Tab 2: 时间段统计 ========== -->
        <div class="tab-pane fade" id="period" role="tabpanel">
            <!-- 周期选择按钮 -->
            <div class="cycle-buttons">
                <div class="cycle-btn active" onclick="selectCycle('monthly')" id="btn-monthly">
                    <i class="fas fa-calendar-day"></i>
                    <div>月度统计</div>
                    <small>按月查看收缴率</small>
                </div>
                <div class="cycle-btn" onclick="selectCycle('quarterly')" id="btn-quarterly">
                    <i class="fas fa-calendar-week"></i>
                    <div>季度统计</div>
                    <small>按季度查看收缴率</small>
                </div>
                <div class="cycle-btn" onclick="selectCycle('yearly')" id="btn-yearly">
                    <i class="fas fa-calendar"></i>
                    <div>年度统计</div>
                    <small>按年度查看收缴率</small>
                </div>
            </div>

            <!-- 时间选择器 -->
            <div class="search-box">
                <div class="row">
                    <!-- 月度选择 -->
                    <div id="monthly-selector" style="display: flex; width: 100%;">
                        <div class="col-md-2">
                            <div class="form-group">
                                <label><i class="fas fa-calendar"></i> 年份</label>
                                <select class="form-control" id="periodYear"></select>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <label><i class="fas fa-calendar-day"></i> 月份</label>
                                <select class="form-control" id="periodMonth">
                                    <option value="1">1月</option>
                                    <option value="2">2月</option>
                                    <option value="3">3月</option>
                                    <option value="4">4月</option>
                                    <option value="5">5月</option>
                                    <option value="6">6月</option>
                                    <option value="7">7月</option>
                                    <option value="8">8月</option>
                                    <option value="9">9月</option>
                                    <option value="10">10月</option>
                                    <option value="11">11月</option>
                                    <option value="12">12月</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <label>&nbsp;</label>
                                <button class="btn btn-gradient btn-block" onclick="searchPeriod()">
                                    <i class="fas fa-search"></i> 查询
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- 季度选择 -->
                    <div id="quarterly-selector" style="display: none; width: 100%;">
                        <div class="col-md-2">
                            <div class="form-group">
                                <label><i class="fas fa-calendar"></i> 年份</label>
                                <select class="form-control" id="quarterYear"></select>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <label><i class="fas fa-calendar-week"></i> 季度</label>
                                <select class="form-control" id="quarter">
                                    <option value="1">第一季度(1-3月)</option>
                                    <option value="2">第二季度(4-6月)</option>
                                    <option value="3">第三季度(7-9月)</option>
                                    <option value="4">第四季度(10-12月)</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <label>&nbsp;</label>
                                <button class="btn btn-gradient btn-block" onclick="searchPeriod()">
                                    <i class="fas fa-search"></i> 查询
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- 年度选择 -->
                    <div id="yearly-selector" style="display: none; width: 100%;">
                        <div class="col-md-2">
                            <div class="form-group">
                                <label><i class="fas fa-calendar"></i> 年份</label>
                                <select class="form-control" id="yearOnly"></select>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group">
                                <label>&nbsp;</label>
                                <button class="btn btn-gradient btn-block" onclick="searchPeriod()">
                                    <i class="fas fa-search"></i> 查询
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 统计表格 -->
            <div class="table-container">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th>收费项目</th>
                            <th>计费周期</th>
                            <th>统计周期</th>
                            <th>总账单数</th>
                            <th>已缴数</th>
                            <th>未缴数</th>
                            <th>逾期数</th>
                            <th>应收金额</th>
                            <th>实收金额</th>
                            <th>已收滞纳金</th>
                            <th>收缴率</th>
                        </tr>
                        </thead>
                        <tbody id="periodTableBody">
                        <tr>
                            <td colspan="11" class="text-center">
                                <i class="fas fa-info-circle"></i> 请选择时间段后点击查询
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- ========== Tab 3: 楼栋收缴统计 ========== -->
        <div class="tab-pane fade" id="building" role="tabpanel">
            <div class="row">
                <!-- 图表 -->
                <div class="col-lg-8">
                    <div class="chart-container">
                        <canvas id="buildingChart"></canvas>
                    </div>
                </div>
                <!-- 表格 -->
                <div class="col-lg-4">
                    <div class="table-container" style="max-height: 420px; overflow-y: auto;">
                        <table class="table table-sm">
                            <thead>
                            <tr>
                                <th>楼栋</th>
                                <th>收缴率</th>
                            </tr>
                            </thead>
                            <tbody id="buildingTableBody">
                            <tr>
                                <td colspan="2" class="text-center">
                                    <i class="fas fa-spinner fa-spin"></i> 加载中...
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 加载动画 -->
<div class="loading-overlay" id="loadingOverlay">
    <div class="loading-spinner">
        <i class="fas fa-spinner fa-spin"></i>
        <p>处理中,请稍候...</p>
    </div>
</div>

<!-- JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
    // 全局变量
    const contextPath = '<%=request.getContextPath()%>';
    let currentPage = 1;
    let pageSize = 10;
    let selectedOwnerIds = [];
    let buildingChartInstance = null;
    let currentCycle = 'monthly'; // 当前选择的周期

    // 页面加载
    $(document).ready(function() {
        console.log('========================================');
        console.log('【finance.jsp 页面加载】');
        console.log('contextPath:', contextPath);
        console.log('========================================');

        initYearSelectors();
        loadOverdueStatistics();
        loadArrearsOwners(1);

        // Tab切换事件
        $('#period-tab').on('shown.bs.tab', function() {
            console.log('✅ 切换到时间段统计 Tab');
            searchPeriod();
        });

        $('#building-tab').on('shown.bs.tab', function() {
            console.log('✅ 切换到楼栋统计 Tab');
            loadBuildingStatistics();
        });
    });

    // 初始化所有年份选择器
    function initYearSelectors() {
        const currentYear = new Date().getFullYear();
        const currentMonth = new Date().getMonth() + 1;
        const currentQuarter = Math.ceil(currentMonth / 3);

        let yearHtml = '';
        for (let year = currentYear; year >= currentYear - 5; year--) {
            yearHtml += '<option value="' + year + '">' + year + '年</option>';
        }

        // 只初始化时间段统计需要的选择器
        $('#periodYear').html(yearHtml);
        $('#quarterYear').html(yearHtml);
        $('#yearOnly').html(yearHtml);

        $('#periodMonth').val(currentMonth);
        $('#quarter').val(currentQuarter);

        console.log('✅ 年份选择器初始化完成');
    }

    // 选择统计周期
    function selectCycle(cycle) {
        currentCycle = cycle;
        console.log('✅ 选择周期:', cycle);

        // 更新按钮状态
        $('.cycle-btn').removeClass('active');
        $('#btn-' + cycle).addClass('active');

        // 显示对应的选择器
        $('#monthly-selector').hide();
        $('#quarterly-selector').hide();
        $('#yearly-selector').hide();

        if (cycle === 'monthly') {
            $('#monthly-selector').show();
        } else if (cycle === 'quarterly') {
            $('#quarterly-selector').show();
        } else if (cycle === 'yearly') {
            $('#yearly-selector').show();
        }
    }

    // 查询时间段统计
    function searchPeriod() {
        let year, month, quarter;
        let periodText = '';

        if (currentCycle === 'monthly') {
            year = $('#periodYear').val();
            month = $('#periodMonth').val();
            periodText = year + '年' + month + '月';
        } else if (currentCycle === 'quarterly') {
            year = $('#quarterYear').val();
            quarter = $('#quarter').val();
            periodText = year + '年第' + quarter + '季度';
        } else if (currentCycle === 'yearly') {
            year = $('#yearOnly').val();
            periodText = year + '年';
        }

        console.log('========================================');
        console.log('【查询时间段统计】');
        console.log('周期类型:', currentCycle);
        console.log('统计周期:', periodText);
        console.log('年份:', year, '月份:', month, '季度:', quarter);
        console.log('========================================');

        $.ajax({
            url: contextPath + '/finance',
            type: 'GET',
            data: {
                action: 'getPeriodPaymentStatistics',
                cycle: currentCycle,
                year: year,
                month: month,
                quarter: quarter
            },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 查询响应:', res);
                if (res.code === 200 && res.data) {
                    renderPeriodTable(res.data, periodText);
                } else {
                    showError('查询失败: ' + (res.message || '未知错误'));
                }
            },
            error: function(xhr) {
                console.error('❌ 查询失败:', xhr);
                showError('请求失败,请检查网络连接');
            }
        });
    }

    // 渲染时间段统计表格（包含汇总行）
    function renderPeriodTable(data, periodText) {
        let html = '';
        if (!data || data.length === 0) {
            html = '<tr>' +
                '<td colspan="11" class="empty-data">' +
                '<i class="fas fa-inbox"></i>' +
                '<p>该时间段暂无数据</p>' +
                '</td>' +
                '</tr>';
        } else {
            data.forEach(function(item) {
                const rate = parseFloat(item.collectionRate) || 0;
                const rateClass = rate >= 90 ? 'text-success' : rate >= 70 ? 'text-warning' : 'text-danger';

                // 判断是否为汇总行
                const isSummary = item.isSummary === true || item.itemName === '【汇总】';
                const rowClass = isSummary ? 'summary-row' : '';

                html += '<tr class="' + rowClass + '">' +
                    '<td>' + (item.itemName || '-') + '</td>' +
                    '<td>' + (item.chargeCycle || '-') + '</td>' +
                    '<td><span class="badge badge-info">' + periodText + '</span></td>' +
                    '<td>' + (item.totalBills || 0) + '</td>' +
                    '<td class="text-success">' + (item.paidBills || 0) + '</td>' +
                    '<td class="text-warning">' + (item.unpaidBills || 0) + '</td>' +
                    '<td class="text-danger">' + (item.overdueBills || 0) + '</td>' +
                    '<td>¥' + formatMoney(item.totalAmount || 0) + '</td>' +
                    '<td class="text-success">¥' + formatMoney(item.collectedAmount || 0) + '</td>' +
                    '<td class="text-warning">¥' + formatMoney(item.collectedLateFee || 0) + '</td>' +
                    '<td class="' + rateClass + '"><strong>' + rate.toFixed(2) + '%</strong></td>' +
                    '</tr>';
            });
        }
        $('#periodTableBody').html(html);
    }

    // 1. 加载逾期统计
    function loadOverdueStatistics() {
        console.log('========================================');
        console.log('【开始加载逾期统计】');
        console.log('请求 URL:', contextPath + '/finance');
        console.log('请求参数:', { action: 'getOverdueStatistics' });
        console.log('========================================');

        $.ajax({
            url: contextPath + '/finance',
            type: 'GET',
            data: { action: 'getOverdueStatistics' },
            dataType: 'json',
            timeout: 10000,
            success: function(res) {
                console.log('✅ 逾期统计响应:', res);
                if (res.code === 200 && res.data) {
                    $('#overdueCount').text(res.data.overdueCount || 0);
                    $('#overdueAmount').text('¥' + formatMoney(res.data.overdueAmount || 0));
                    $('#avgOverdueDays').text(Math.round(res.data.avgOverdueDays || 0) + '天');
                    $('#totalLateFee').text('¥' + formatMoney(res.data.totalLateFee || 0));
                } else {
                    alert(res.message || '加载逾期统计失败');
                }
            },
            error: function(xhr) {
                console.error('❌ 请求失败:', xhr);
                if (xhr.status === 401) {
                    alert('未登录或登录已过期,请重新登录');
                    setTimeout(function() {
                        window.location.href = contextPath + '/login.jsp';
                    }, 2000);
                } else {
                    alert('加载逾期统计失败');
                }
            }
        });
    }

    // 2. 加载欠费业主列表
    function loadArrearsOwners(page) {
        currentPage = page;
        const minAmount = $('#minAmount').val() || 0;
        const keyword = $('#keyword').val() || '';

        $.ajax({
            url: contextPath + '/finance',
            type: 'GET',
            data: {
                action: 'getArrearsOwners',
                pageNum: page,
                pageSize: pageSize,
                minAmount: minAmount,
                keyword: keyword
            },
            dataType: 'json',
            success: function(res) {
                if (res.code === 200 && res.data) {
                    renderArrearsTable(res.data.list || []);
                    renderPagination(res.data, 'arrearsPagination', loadArrearsOwners);
                } else {
                    showError('加载失败: ' + (res.message || '未知错误'));
                }
            },
            error: function(xhr) {
                showError('请求失败,请检查网络连接');
            }
        });
    }

    // 渲染欠费业主表格
    function renderArrearsTable(list) {
        let html = '';
        if (!list || list.length === 0) {
            html = '<tr>' +
                '<td colspan="12" class="empty-data">' +
                '<i class="fas fa-inbox"></i>' +
                '<p>暂无欠费数据</p>' +
                '</td>' +
                '</tr>';
        } else {
            list.forEach(function(item) {
                const location = (item.buildingNo || '-') + '栋' +
                    (item.unitNo || '-') + '单元' +
                    (item.floor || '-') + '层';
                const overdueDays = item.maxOverdueDays || 0;

                let overdueBadge;
                if (overdueDays > 90) {
                    overdueBadge = '<span class="badge-overdue">' + overdueDays + '天</span>';
                } else if (overdueDays > 30) {
                    overdueBadge = '<span class="badge-warning">' + overdueDays + '天</span>';
                } else {
                    overdueBadge = '<span class="badge-normal">' + overdueDays + '天</span>';
                }

                html += '<tr>' +
                    '<td>' +
                    '<input type="checkbox" class="owner-checkbox" ' +
                    'value="' + item.ownerId + '" ' +
                    'onchange="updateSelectedOwners()">' +
                    '</td>' +
                    '<td>' + (item.ownerId || '-') + '</td>' +
                    '<td>' + (item.ownerName || '-') + '</td>' +
                    '<td>' + (item.ownerPhone || '-') + '</td>' +
                    '<td>' + (item.houseId || '-') + '</td>' +
                    '<td>' + location + '</td>' +
                    '<td><span class="badge badge-warning">' + (item.unpaidCount || 0) + '</span></td>' +
                    '<td class="text-danger"><strong>¥' + formatMoney(item.unpaidAmount || 0) + '</strong></td>' +
                    '<td class="text-warning">¥' + formatMoney(item.totalLateFee || 0) + '</td>' +
                    '<td class="text-danger"><strong>¥' + formatMoney(item.totalArrears || 0) + '</strong></td>' +
                    '<td>' + overdueBadge + '</td>' +
                    '<td>' +
                    '<button class="btn btn-sm btn-primary" ' +
                    'onclick="generateSingleReminder(\'' + item.ownerId + '\', \'' + item.ownerName + '\')">' +
                    '<i class="fas fa-bell"></i> 催缴' +
                    '</button>' +
                    '</td>' +
                    '</tr>';
            });
        }
        $('#arrearsTableBody').html(html);
        selectedOwnerIds = [];
        $('#selectAll').prop('checked', false);
    }

    // 3. 搜索欠费业主
    function searchArrears() {
        loadArrearsOwners(1);
    }

    // 4. 导出欠费业主列表
    function exportArrears() {
        const minAmount = $('#minAmount').val() || 0;
        const keyword = $('#keyword').val() || '';

        let url = contextPath + '/finance?action=exportArrearsOwners&minAmount=' + minAmount;
        if (keyword) {
            url += '&keyword=' + encodeURIComponent(keyword);
        }

        window.location.href = url;
    }

    // 5. 全选/取消全选
    function toggleSelectAll() {
        const checked = $('#selectAll').prop('checked');
        $('.owner-checkbox').prop('checked', checked);
        updateSelectedOwners();
    }

    function updateSelectedOwners() {
        selectedOwnerIds = [];
        $('.owner-checkbox:checked').each(function() {
            selectedOwnerIds.push($(this).val());
        });
    }

    // 6. 单个催缴
    function generateSingleReminder(ownerId, ownerName) {
        if (!confirm('确定要向业主 ' + ownerName + ' 发送催缴通知吗?')) {
            return;
        }
        showLoading();
        $.ajax({
            url: contextPath + '/finance',
            type: 'POST',
            data: {
                action: 'generatePaymentReminder',
                ownerIds: ownerId
            },
            dataType: 'json',
            success: function(res) {
                hideLoading();
                if (res.code === 200) {
                    alert('催缴通知发送成功!');
                } else {
                    alert('发送失败:' + (res.message || '未知错误'));
                }
            },
            error: function() {
                hideLoading();
                alert('请求失败,请稍后重试');
            }
        });
    }

    // 7. 批量催缴
    function batchGenerateReminders() {
        if (selectedOwnerIds.length === 0) {
            alert('请先选择要催缴的业主');
            return;
        }
        if (!confirm('确定要向 ' + selectedOwnerIds.length + ' 位业主发送催缴通知吗?')) {
            return;
        }
        showLoading();
        $.ajax({
            url: contextPath + '/finance',
            type: 'POST',
            data: {
                action: 'generatePaymentReminder',
                ownerIds: selectedOwnerIds.join(',')
            },
            dataType: 'json',
            success: function(res) {
                hideLoading();
                if (res.code === 200) {
                    alert('催缴通知发送完成!\n成功:' + (res.data.successCount || 0) + ' 条\n失败:' + (res.data.failCount || 0) + ' 条');
                    loadArrearsOwners(currentPage);
                } else {
                    alert('发送失败:' + (res.message || '未知错误'));
                }
            },
            error: function() {
                hideLoading();
                alert('请求失败,请稍后重试');
            }
        });
    }

    // 8. 加载楼栋统计
    function loadBuildingStatistics() {
        $.ajax({
            url: contextPath + '/finance',
            type: 'GET',
            data: { action: 'getBuildingPaymentStatistics' },
            dataType: 'json',
            success: function(res) {
                if (res.code === 200 && res.data) {
                    renderBuildingChart(res.data);
                    renderBuildingTable(res.data);
                }
            },
            error: function() {
                console.error('加载楼栋统计失败');
            }
        });
    }

    // 渲染楼栋图表
    function renderBuildingChart(data) {
        const labels = data.map(function(item) {
            return item.buildingName || item.buildingNo + '栋';
        });
        const rates = data.map(function(item) {
            return parseFloat(item.collectionRate) || 0;
        });

        const ctx = document.getElementById('buildingChart').getContext('2d');
        if (buildingChartInstance) {
            buildingChartInstance.destroy();
        }

        buildingChartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: '收缴率 (%)',
                    data: rates,
                    backgroundColor: 'rgba(102, 126, 234, 0.7)',
                    borderColor: 'rgba(102, 126, 234, 1)',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        ticks: {
                            callback: function(value) {
                                return value + '%';
                            }
                        }
                    }
                },
                plugins: {
                    legend: { display: false },
                    title: {
                        display: true,
                        text: '楼栋收缴率统计',
                        font: { size: 18, weight: 'bold' }
                    }
                }
            }
        });
    }

    // 渲染楼栋表格
    function renderBuildingTable(data) {
        let html = '';
        if (!data || data.length === 0) {
            html = '<tr><td colspan="2" class="text-center">暂无数据</td></tr>';
        } else {
            data.forEach(function(item) {
                const rate = parseFloat(item.collectionRate) || 0;
                const rateClass = rate >= 90 ? 'text-success' : rate >= 70 ? 'text-warning' : 'text-danger';
                html += '<tr>' +
                    '<td>' + (item.buildingName || item.buildingNo + '栋') + '</td>' +
                    '<td class="' + rateClass + '"><strong>' + rate.toFixed(2) + '%</strong></td>' +
                    '</tr>';
            });
        }
        $('#buildingTableBody').html(html);
    }

    // 工具函数 - 格式化金额
    function formatMoney(amount) {
        if (!amount || amount === 0) return '0.00';

        // 先转换为数字
        const num = parseFloat(amount);
        if (isNaN(num)) return '0.00';

        // 格式化为两位小数
        const fixed = num.toFixed(2);

        // 分割整数和小数部分
        const parts = fixed.split('.');

        // 为整数部分添加千分位分隔符
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');

        // 重新组合
        return parts.join('.');
    }

    function renderPagination(data, elementId, callback) {
        const total = data.total || 0;
        const pageNum = data.pageNum || 1;
        const pages = data.pages || 1;
        let html = '';

        if (pageNum > 1) {
            html += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="' + callback.name + '(' + (pageNum - 1) + ')">上一页</a></li>';
        }

        const startPage = Math.max(1, pageNum - 2);
        const endPage = Math.min(pages, pageNum + 2);

        if (startPage > 1) {
            html += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="' + callback.name + '(1)">1</a></li>';
            if (startPage > 2) {
                html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            const activeClass = i === pageNum ? 'active' : '';
            html += '<li class="page-item ' + activeClass + '"><a class="page-link" href="javascript:void(0)" onclick="' + callback.name + '(' + i + ')">' + i + '</a></li>';
        }

        if (endPage < pages) {
            if (endPage < pages - 1) {
                html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
            html += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="' + callback.name + '(' + pages + ')">' + pages + '</a></li>';
        }

        if (pageNum < pages) {
            html += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="' + callback.name + '(' + (pageNum + 1) + ')">下一页</a></li>';
        }

        $('#' + elementId).html(html);
    }

    function showLoading() {
        $('#loadingOverlay').addClass('show');
    }

    function hideLoading() {
        $('#loadingOverlay').removeClass('show');
    }

    function showError(msg) {
        alert(msg);
    }
</script>

</body>
</html>
