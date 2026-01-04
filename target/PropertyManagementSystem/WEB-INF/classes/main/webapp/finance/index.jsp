<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.property.entity.User" %>
<%
    System.out.println("========================================");
    System.out.println("【finance/index.jsp 开始加载】");
    System.out.println("请求时间: " + new java.util.Date());
    System.out.println("请求 URI: " + request.getRequestURI());
    System.out.println("========================================");

    // 检查登录
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        System.out.println("❌ 用户未登录，重定向到登录页");
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // 检查权限
    String userRole = currentUser.getUserRole();
    System.out.println("✅ 用户: " + currentUser.getUsername() + " (" + userRole + ")");

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
    <title>财务工作台 - 社区物业管理系统</title>

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

        /* ========== 页面头部 ========== */
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px 0;
            margin-bottom: 30px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.15);
        }

        .page-header h2 {
            margin: 0;
            font-size: 32px;
            font-weight: bold;
        }

        .page-header p {
            margin: 10px 0 0 0;
            opacity: 0.95;
            font-size: 16px;
        }

        /* ========== 统计卡片 ========== */
        .stat-card {
            background: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
            transition: all 0.3s ease;
            cursor: pointer;
            border-left: 4px solid transparent;
        }

        .stat-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }

        .stat-card.primary {
            border-left-color: #667eea;
        }

        .stat-card.danger {
            border-left-color: #f5576c;
        }

        .stat-card.warning {
            border-left-color: #fcb69f;
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

        .stat-card.primary .stat-icon {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        .stat-card.danger .stat-icon {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        .stat-card.warning .stat-icon {
            background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
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
            margin-bottom: 5px;
        }

        .stat-card .stat-desc {
            font-size: 13px;
            color: #666;
        }

        /* ========== 快捷入口 ========== */
        .quick-menu {
            background: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
        }

        .quick-menu h5 {
            margin-bottom: 20px;
            font-weight: bold;
            color: #333;
            font-size: 18px;
        }

        .menu-item {
            display: block;
            padding: 15px 20px;
            margin-bottom: 10px;
            background: #f8f9fa;
            border-radius: 8px;
            color: #333;
            text-decoration: none;
            transition: all 0.3s;
            border-left: 3px solid transparent;
        }

        .menu-item:hover {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            transform: translateX(5px);
            text-decoration: none;
            border-left-color: #764ba2;
        }

        .menu-item i {
            width: 30px;
            text-align: center;
            margin-right: 10px;
        }

        /* ========== 最近动态 ========== */
        .activity-box {
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.08);
        }

        .activity-box h5 {
            margin-bottom: 20px;
            font-weight: bold;
            color: #333;
            font-size: 18px;
        }

        .activity-item {
            padding: 15px 0;
            border-bottom: 1px solid #f0f0f0;
            transition: all 0.3s;
        }

        .activity-item:last-child {
            border-bottom: none;
        }

        .activity-item:hover {
            background: #f8f9fa;
            padding-left: 10px;
            border-radius: 8px;
        }

        .activity-time {
            color: #999;
            font-size: 12px;
            display: block;
            margin-top: 5px;
        }

        .activity-item i {
            font-size: 18px;
            margin-right: 10px;
        }

        .activity-item strong {
            color: #333;
            font-size: 15px;
        }

        .activity-item p {
            color: #666;
            font-size: 14px;
            margin: 5px 0 0 28px;
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

            .page-header h2 {
                font-size: 24px;
            }

            .page-header p {
                font-size: 14px;
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
                    <a class="nav-link active" href="<%=request.getContextPath()%>/finance/index.jsp">
                        <i class="fas fa-home"></i> 工作台
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%=request.getContextPath()%>/finance/finance.jsp">
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
                            <i class="fas fa-user-circle"></i> <%=currentUser.getUsername()%>
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

<!-- 页面头部 -->
<div class="page-header">
    <div class="container-fluid">
        <h2><i class="fas fa-tachometer-alt"></i> 财务工作台</h2>
        <p>欢迎回来，<%=currentUser.getRealName()%>！今天是 <span id="currentDate"></span></p>
    </div>
</div>

<!-- 主内容区 -->
<div class="container-fluid" style="padding: 0 30px 30px;">
    <!-- 统计卡片 -->
    <div class="row">
        <div class="col-lg-3 col-md-6">
            <div class="stat-card primary" onclick="location.href='finance.jsp'">
                <div class="stat-icon">
                    <i class="fas fa-money-bill-wave"></i>
                </div>
                <div class="stat-content">
                    <div class="stat-title">本月收入</div>
                    <div class="stat-value" id="monthlyIncome">-</div>
                    <div class="stat-desc">较上月 <span id="incomeChange">-</span></div>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stat-card danger" onclick="location.href='finance.jsp?tab=arrears'">
                <div class="stat-icon">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                <div class="stat-content">
                    <div class="stat-title">逾期账单</div>
                    <div class="stat-value" id="overdueCount">-</div>
                    <div class="stat-desc">需要及时处理</div>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stat-card warning" onclick="location.href='finance.jsp?tab=arrears'">
                <div class="stat-icon">
                    <i class="fas fa-clock"></i>
                </div>
                <div class="stat-content">
                    <div class="stat-title">待缴金额</div>
                    <div class="stat-value" id="unpaidAmount">-</div>
                    <div class="stat-desc">本金 + 滞纳金</div>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stat-card success" onclick="location.href='payment.jsp'">
                <div class="stat-icon">
                    <i class="fas fa-check-circle"></i>
                </div>
                <div class="stat-content">
                    <div class="stat-title">今日缴费</div>
                    <div class="stat-value" id="todayPayment">-</div>
                    <div class="stat-desc">共 <span id="todayCount">-</span> 笔</div>
                </div>
            </div>
        </div>
    </div>

    <!-- 快捷入口和最近动态 -->
    <div class="row">
        <!-- 快捷入口 -->
        <div class="col-lg-4 col-md-12">
            <div class="quick-menu">
                <h5><i class="fas fa-th-large"></i> 快捷入口</h5>
                <a href="finance.jsp" class="menu-item">
                    <i class="fas fa-chart-line"></i> 财务统计分析
                </a>
                <a href="finance.jsp?tab=arrears" class="menu-item">
                    <i class="fas fa-users"></i> 欠费业主管理
                </a>
                <a href="finance.jsp?tab=building" class="menu-item">
                    <i class="fas fa-building"></i> 楼栋收缴统计
                </a>
                <a href="finance.jsp?tab=monthly" class="menu-item">
                    <i class="fas fa-calendar-alt"></i> 月度收缴报表
                </a>
                <a href="payment.jsp" class="menu-item">
                    <i class="fas fa-credit-card"></i> 缴费记录查询
                </a>
            </div>
        </div>

        <!-- 最近动态 -->
        <div class="col-lg-8 col-md-12">
            <div class="activity-box">
                <h5><i class="fas fa-bell"></i> 最近动态</h5>
                <div id="activityList">
                    <div class="text-center" style="padding: 40px;">
                        <i class="fas fa-spinner fa-spin fa-2x text-muted"></i>
                        <p class="text-muted mt-3">加载中...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>

<script>
    const contextPath = '<%=request.getContextPath()%>';

    $(document).ready(function() {
        // 显示当前日期
        showCurrentDate();

        // 加载统计数据
        loadStatistics();

        // 加载最近动态
        loadRecentActivities();
    });

    // 显示当前日期
    function showCurrentDate() {
        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth() + 1;
        const day = now.getDate();
        const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
        const weekday = weekdays[now.getDay()];
        const dateStr = year + '年' + month + '月' + day + '日 ' + weekday;
        $('#currentDate').text(dateStr);
    }

    // 加载统计数据
    function loadStatistics() {
        // 加载本月收入
        $.ajax({
            url: contextPath + '/finance',
            type: 'GET',
            data: { action: 'getMonthlyIncome' },
            dataType: 'json',
            success: function(res) {
                if (res.code === 200 && res.data) {
                    $('#monthlyIncome').text('¥' + formatMoney(res.data.income || 0));
                    const change = res.data.changeRate || 0;
                    const changeHtml = change >= 0
                        ? '<span class="text-success">↑ ' + change + '%</span>'
                        : '<span class="text-danger">↓ ' + Math.abs(change) + '%</span>';
                    $('#incomeChange').html(changeHtml);
                }
            },
            error: function() {
                $('#monthlyIncome').text('加载失败');
            }
        });

        // 加载逾期统计
        $.ajax({
            url: contextPath + '/finance',
            type: 'GET',
            data: { action: 'getOverdueStatistics' },
            dataType: 'json',
            success: function(res) {
                if (res.code === 200 && res.data) {
                    $('#overdueCount').text(res.data.overdueCount || 0);
                    $('#unpaidAmount').text('¥' + formatMoney(res.data.overdueAmount || 0));
                }
            },
            error: function() {
                $('#overdueCount').text('加载失败');
                $('#unpaidAmount').text('加载失败');
            }
        });

        // 加载今日缴费
        $.ajax({
            url: contextPath + '/finance',
            type: 'GET',
            data: { action: 'getTodayPayment' },
            dataType: 'json',
            success: function(res) {
                if (res.code === 200 && res.data) {
                    $('#todayPayment').text('¥' + formatMoney(res.data.amount || 0));
                    $('#todayCount').text(res.data.count || 0);
                }
            },
            error: function() {
                $('#todayPayment').text('加载失败');
                $('#todayCount').text('-');
            }
        });
    }

    // 加载最近动态
    function loadRecentActivities() {
        $.ajax({
            url: contextPath + '/finance',
            type: 'GET',
            data: { action: 'getRecentActivities', limit: 10 },
            dataType: 'json',
            success: function(res) {
                if (res.code === 200 && res.data && res.data.length > 0) {
                    let html = '';
                    res.data.forEach(function(item) {
                        const iconClass = item.type === 'payment' ? 'fa-credit-card text-success'
                            : item.type === 'overdue' ? 'fa-exclamation-triangle text-danger'
                                : 'fa-info-circle text-info';

                        html += '<div class="activity-item">' +
                            '<i class="fas ' + iconClass + '"></i>' +
                            '<strong>' + item.title + '</strong>' +
                            '<p>' + item.content + '</p>' +
                            '<span class="activity-time">' + item.time + '</span>' +
                            '</div>';
                    });
                    $('#activityList').html(html);
                } else {
                    $('#activityList').html(
                        '<div class="text-center" style="padding: 40px;">' +
                        '<i class="fas fa-inbox fa-3x text-muted"></i>' +
                        '<p class="text-muted mt-3">暂无动态</p>' +
                        '</div>'
                    );
                }
            },
            error: function() {
                $('#activityList').html(
                    '<div class="text-center" style="padding: 40px;">' +
                    '<i class="fas fa-exclamation-circle fa-3x text-danger"></i>' +
                    '<p class="text-muted mt-3">加载失败</p>' +
                    '</div>'
                );
            }
        });
    }

    // 格式化金额
    function formatMoney(amount) {
        if (!amount) return '0.00';
        return parseFloat(amount).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }
</script>
</body>
</html>
