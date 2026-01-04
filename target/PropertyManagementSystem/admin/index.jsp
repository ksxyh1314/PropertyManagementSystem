<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员首页 - 物业管理系统</title>

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
        }
        .page-header::before {
            content: ''; position: absolute; top: -50%; right: -5%;
            width: 300px; height: 300px; background: rgba(255,255,255,0.1);
            border-radius: 50%; animation: float 6s ease-in-out infinite;
            opacity: 0.6;
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

        /* 🔥 优化后的统计卡片标题 */
        .section-title {
            font-size: 20px; font-weight: 600; color: white;
            margin: 60px 0 20px 0; /* 👈 增加顶部间距 */
            padding-left: 15px;
            border-left: 5px solid white; display: flex; align-items: center;
        }
        .section-title i { margin-right: 10px; }

        /* 第一个标题不需要太大间距 */
        .section-title:first-of-type {
            margin-top: 0;
        }

        /* 移动端适配 */
        @media (max-width: 768px) {
            .section-title {
                margin: 40px 0 15px 0;
                font-size: 18px;
            }
        }


        .stats-card {
            background: white; border-radius: 15px; padding: 25px;
            margin-bottom: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            transition: all 0.3s; position: relative; overflow: hidden;
            height: 100%;
        }
        .stats-card::before {
            content: ''; position: absolute; top: 0; left: 0; right: 0;
            height: 5px; transition: height 0.3s;
        }
        .stats-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 28px rgba(0,0,0,0.2);
        }
        .stats-card:hover::before { height: 8px; }

        .stats-card.primary::before { background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); }
        .stats-card.success::before { background: linear-gradient(90deg, #56ab2f 0%, #a8e063 100%); }
        .stats-card.warning::before { background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%); }
        .stats-card.info::before { background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%); }
        .stats-card.danger::before { background: linear-gradient(90deg, #ff6b6b 0%, #ee5a6f 100%); }
        .stats-card.orange::before { background: linear-gradient(90deg, #fa709a 0%, #fee140 100%); }

        .stats-icon {
            width: 65px; height: 65px; border-radius: 15px;
            display: flex; align-items: center; justify-content: center;
            font-size: 30px; float: left; margin-right: 20px;
            color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        .stats-card.primary .stats-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .stats-card.success .stats-icon { background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%); }
        .stats-card.warning .stats-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
        .stats-card.info .stats-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
        .stats-card.danger .stats-icon { background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); }
        .stats-card.orange .stats-icon { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }

        .stats-info { overflow: hidden; }
        .stats-info h3 {
            font-size: 36px; font-weight: 700; margin: 0 0 5px 0;
            color: #2c3e50;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .stats-info p { margin: 0; color: #7f8c8d; font-size: 14px; font-weight: 500; }
        .stats-info small {
            color: #95a5a6; font-size: 11px; display: block;
            line-height: 1.4; white-space: nowrap;
            overflow: hidden; text-overflow: ellipsis;
        }

        /* 🔥 图表与表格卡片（统一样式） */
        .chart-card, .table-card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 20px;
            margin-top: 30px; /* 👈 添加顶部间距 */
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            transition: all 0.3s;
        }

        .chart-card:hover, .table-card:hover {
            box-shadow: 0 8px 24px rgba(0,0,0,0.15);
        }

        .chart-card h4, .table-card h4 {
            margin: 0 0 25px 0;
            font-size: 20px;
            font-weight: 600;
            color: #2c3e50;
            padding-bottom: 15px;
            border-bottom: 3px solid #f0f0f0;
            position: relative;
        }

        .chart-card h4::before, .table-card h4::before {
            content: '';
            position: absolute;
            bottom: -3px;
            left: 0;
            width: 60px;
            height: 3px;
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        }


        /* 🔥 优化表格样式 */
        .table-responsive {
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
            position: relative;
        }
        .table-responsive::after {
            content: '← 左右滑动查看更多 →';
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            text-align: center;
            font-size: 11px;
            color: #999;
            background: rgba(255,255,255,0.9);
            padding: 5px;
            display: none;
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

        .label {
            display: inline-block; padding: 6px 14px; border-radius: 20px;
            font-size: 12px; font-weight: 600; white-space: nowrap;
        }
        .label-danger { background-color: #ffebee; color: #d32f2f; }
        .label-warning { background-color: #fff3e0; color: #f57c00; }
        .label-success { background-color: #e8f5e9; color: #388e3c; }
        .label-info { background-color: #e3f2fd; color: #1976d2; }
        .label-default { background-color: #f5f5f5; color: #616161; }

        .btn-action {
            padding: 6px 12px; font-size: 12px; margin: 2px;
            border-radius: 20px; transition: all 0.3s;
            white-space: nowrap;
        }
        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        }

        /* 🔥 美化模态框 */
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

        /* 🔥 表单样式 */
        .form-group label {
            font-weight: 600; color: #495057; margin-bottom: 8px;
            font-size: 14px;
        }
        .form-control {
            border-radius: 8px; border: 2px solid #e9ecef;
            padding: 10px 15px; transition: all 0.3s;
        }
        .form-control:focus {
            border-color: #667eea; box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        /* 投诉详情样式 */
        .detail-card { padding: 0; }
        .detail-header {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white; padding: 25px; border-radius: 12px; margin-bottom: 20px;
            box-shadow: 0 4px 12px rgba(240, 147, 251, 0.3);
        }
        .detail-header h4 { margin: 0; font-size: 22px; font-weight: 600; }
        .detail-header p { margin: 8px 0 0 0; opacity: 0.95; font-size: 14px; }

        .detail-section {
            background: #f8f9fa; padding: 20px; border-radius: 10px; margin-bottom: 15px;
        }
        .detail-grid {
            display: grid; grid-template-columns: 1fr 1fr; gap: 20px;
        }
        .detail-item { margin-bottom: 12px; }
        .detail-item small {
            color: #6c757d; display: block; margin-bottom: 6px;
            font-weight: 600; font-size: 12px;
        }
        .detail-item .value { font-weight: 600; color: #212529; font-size: 15px; }

        .content-box {
            background: white; padding: 18px; border-radius: 10px;
            border-left: 5px solid #f093fb; margin-top: 10px;
            line-height: 1.8; box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }
        .reply-box {
            background: #e8f5e9; padding: 18px; border-radius: 10px;
            border-left: 5px solid #4caf50; margin-top: 10px;
            line-height: 1.8; box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }

        /* 类型标签 */
        .type-badge {
            padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight: 600;
        }
        .type-service { background-color: #e3f2fd; color: #1976d2; }
        .type-environment { background-color: #e8f5e9; color: #388e3c; }
        .type-facility { background-color: #fff3e0; color: #f57c00; }
        .type-fee { background-color: #fce4ec; color: #c2185b; }
        .type-other { background-color: #f3e5f5; color: #7b1fa2; }

        /* 状态标签 */
        .status-badge {
            padding: 6px 14px; border-radius: 20px; font-size: 12px; font-weight: 600;
        }
        .status-pending { background-color: #fff3e0; color: #f57c00; }
        .status-processing { background-color: #e3f2fd; color: #1976d2; }
        .status-resolved { background-color: #e8f5e9; color: #388e3c; }
        .status-closed { background-color: #f5f5f5; color: #757575; }

        /* 响应式 */
        @media (max-width: 768px) {
            .sidebar { transform: translateX(-260px); }
            .sidebar-toggle { left: 20px; z-index: 1002; }
            .main-content { margin-left: 0; padding: 15px; }
            .detail-grid { grid-template-columns: 1fr; }
            .stats-info h3 { font-size: 28px; }
            .page-header { padding: 30px 20px; }
            .page-header h2 { font-size: 24px; }
            .page-header::before { display: none; }

            .stats-card {
                margin-bottom: 15px;
                padding: 20px 15px;
            }

            .stats-icon {
                width: 50px;
                height: 50px;
                font-size: 24px;
            }

            .stats-info h3 {
                font-size: 24px;
            }

            .table-responsive::after {
                display: block;
            }

            .modal-dialog {
                margin: 10px;
                max-width: calc(100% - 20px);
            }

            .modal-body {
                max-height: 60vh;
                overflow-y: auto;
                padding: 20px 15px;
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
            <a href="${pageContext.request.contextPath}/admin/index.jsp" class="active">
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
        <h2><i class="fas fa-tachometer-alt"></i> 系统概览</h2>
        <p>欢迎回来！这里是您的工作台，查看系统运行状态和待处理事项</p>
    </div>

    <!-- 🔥 基础数据统计 -->
    <div class="section-title">
        <i class="fas fa-database"></i> 基础数据统计
    </div>
    <div class="row">
        <div class="col-lg-3 col-md-6">
            <div class="stats-card primary">
                <div class="stats-icon"><i class="fas fa-building"></i></div>
                <div class="stats-info">
                    <h3 id="totalHouses">-</h3>
                    <p>房屋总数</p>
                    <small>已入住: <span id="occupiedHouses">-</span> | 空置: <span id="vacantHouses">-</span></small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card success">
                <div class="stats-icon"><i class="fas fa-users"></i></div>
                <div class="stats-info">
                    <h3 id="totalOwners">-</h3>
                    <p>业主总数</p>
                    <small>活跃业主数量统计</small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card orange">
                <div class="stats-icon"><i class="fas fa-dollar-sign"></i></div>
                <div class="stats-info">
                    <h3 id="monthlyIncome">-</h3>
                    <p>本月收入（元）</p>
                    <small>本月已缴费金额</small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card info">
                <div class="stats-icon"><i class="fas fa-percentage"></i></div>
                <div class="stats-info">
                    <h3 id="paymentRate">-%</h3>
                    <p>本月收缴率</p>
                    <small>已缴: <span id="paidCount">-</span> | 未缴: <span id="unpaidCount">-</span></small>
                </div>
            </div>
        </div>
    </div>

    <!-- 🔥 报修管理统计 -->
    <div class="section-title">
        <i class="fas fa-tools"></i> 报修管理统计
    </div>
    <div class="row">
        <div class="col-lg-3 col-md-6">
            <div class="stats-card danger">
                <div class="stats-icon"><i class="fas fa-clock"></i></div>
                <div class="stats-info">
                    <h3 id="pendingRepairs">-</h3>
                    <p>待处理报修</p>
                    <small>需要立即处理</small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card info">
                <div class="stats-icon"><i class="fas fa-cog"></i></div>
                <div class="stats-info">
                    <h3 id="processingRepairs">-</h3>
                    <p>处理中报修</p>
                    <small>正在维修处理</small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card success">
                <div class="stats-icon"><i class="fas fa-check-circle"></i></div>
                <div class="stats-info">
                    <h3 id="completedRepairs">-</h3>
                    <p>已完成报修</p>
                    <small>本月完成数量</small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card warning">
                <div class="stats-icon"><i class="fas fa-star"></i></div>
                <div class="stats-info">
                    <h3 id="avgRating">-</h3>
                    <p>平均满意度</p>
                    <small>业主评价平均分</small>
                </div>
            </div>
        </div>
    </div>

    <!-- 🔥 投诉管理统计 -->
    <div class="section-title">
        <i class="fas fa-comments"></i> 投诉管理统计
    </div>
    <div class="row">
        <div class="col-lg-3 col-md-6">
            <div class="stats-card danger">
                <div class="stats-icon"><i class="fas fa-exclamation-circle"></i></div>
                <div class="stats-info">
                    <h3 id="pendingComplaints">-</h3>
                    <p>待处理投诉</p>
                    <small>需要及时响应</small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card info">
                <div class="stats-icon"><i class="fas fa-spinner"></i></div>
                <div class="stats-info">
                    <h3 id="processingComplaints">-</h3>
                    <p>处理中投诉</p>
                    <small>正在跟进处理</small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card success">
                <div class="stats-icon"><i class="fas fa-check-double"></i></div>
                <div class="stats-info">
                    <h3 id="resolvedComplaints">-</h3>
                    <p>已解决投诉</p>
                    <small>本月解决数量</small>
                </div>
            </div>
        </div>
        <div class="col-lg-3 col-md-6">
            <div class="stats-card warning">
                <div class="stats-icon"><i class="fas fa-chart-line"></i></div>
                <div class="stats-info">
                    <h3 id="complaintResolveRate">-%</h3>
                    <p>投诉解决率</p>
                    <small>已解决/总投诉</small>
                </div>
            </div>
        </div>
    </div>

    <!-- 🔥 图表区域（修复高度） -->
    <div class="row">
        <div class="col-md-4">
            <div class="chart-card">
                <h4><i class="fas fa-chart-pie"></i> 房屋状态分布</h4>
                <div id="houseChart" style="height: 380px;"></div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="chart-card">
                <h4><i class="fas fa-chart-pie"></i> 报修状态分布</h4>
                <div id="repairChart" style="height: 380px;"></div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="chart-card">
                <h4><i class="fas fa-chart-pie"></i> 投诉状态分布</h4>
                <div id="complaintChart" style="height: 380px;"></div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="chart-card">
                <h4><i class="fas fa-chart-line"></i> 收费趋势（最近6个月）</h4>
                <div id="trendChart" style="height: 350px;"></div>
            </div>
        </div>
    </div>

    <!-- 待处理事项 -->
    <div class="row">
        <!-- 待处理报修 -->
        <div class="col-md-6">
            <div class="table-card">
                <h4><i class="fas fa-clipboard-list"></i> 待处理报修（最近5条）</h4>
                <div class="table-responsive">
                    <table class="table table-hover" id="pendingRepairTable">
                        <thead>
                        <tr>
                            <th>报修ID</th>
                            <th>业主</th>
                            <th>房屋</th>
                            <th>类型</th>
                            <th>优先级</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr><td colspan="6" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 待处理投诉 -->
        <div class="col-md-6">
            <div class="table-card">
                <h4><i class="fas fa-comments"></i> 待处理投诉（最近5条）</h4>
                <div class="table-responsive">
                    <table class="table table-hover" id="pendingComplaintTable">
                        <thead>
                        <tr>
                            <th>投诉ID</th>
                            <th>业主</th>
                            <th>标题</th>
                            <th>类型</th>
                            <th>提交时间</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr><td colspan="6" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 🔥 报修详情模态框 -->
<div class="modal fade" id="repairDetailModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-tools"></i> 报修详情</h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" id="repairDetailContent">
                <div class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 关闭
                </button>
                <button type="button" class="btn btn-success" id="btnAcceptRepair">
                    <i class="fas fa-check"></i> 受理报修
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 🔥 受理报修模态框 -->
<div class="modal fade" id="acceptRepairModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-hand-paper"></i> 受理报修</h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="acceptRepairForm">
                    <div class="form-group">
                        <label for="handlerName"><i class="fas fa-user"></i> 处理人姓名</label>
                        <input type="text" class="form-control" id="handlerName" required placeholder="请输入处理人姓名">
                    </div>
                    <div class="form-group">
                        <label for="handlerPhone"><i class="fas fa-phone"></i> 联系电话</label>
                        <input type="tel" class="form-control" id="handlerPhone" required
                               placeholder="请输入11位手机号" pattern="^1[3-9]\d{9}$" maxlength="11">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-success" onclick="confirmAcceptRepair()">
                    <i class="fas fa-check"></i> 确认受理
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 🔥 投诉详情模态框 -->
<div class="modal fade" id="complaintDetailModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-comments"></i> 投诉详情</h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" id="complaintDetailContent">
                <div class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 关闭
                </button>
                <button type="button" class="btn btn-success" id="btnAcceptComplaint">
                    <i class="fas fa-check"></i> 受理投诉
                </button>
                <button type="button" class="btn btn-primary" id="btnReplyComplaint">
                    <i class="fas fa-reply"></i> 回复投诉
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 🔥 受理投诉模态框 -->
<div class="modal fade" id="acceptComplaintModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-hand-paper"></i> 受理投诉</h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i> 确认要受理此投诉吗？受理后将由您负责跟进处理。
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-success" onclick="confirmAcceptComplaint()">
                    <i class="fas fa-check"></i> 确认受理
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 🔥 回复投诉模态框 -->
<div class="modal fade" id="replyComplaintModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-reply"></i> 回复投诉</h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="replyComplaintForm">
                    <div class="form-group">
                        <label for="replyContent"><i class="fas fa-comment-dots"></i> 回复内容</label>
                        <textarea class="form-control" id="replyContent" rows="5" required
                                  placeholder="请输入回复内容，详细说明处理情况"></textarea>
                    </div>
                    <div class="form-group">
                        <label for="newStatus"><i class="fas fa-tasks"></i> 处理后状态</label>
                        <select class="form-control" id="newStatus">
                            <option value="processing">处理中</option>
                            <option value="resolved" selected>已解决</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-primary" onclick="confirmReplyComplaint()">
                    <i class="fas fa-paper-plane"></i> 提交回复
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 引入 JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/layer.js"></script>

<script>
    var currentRepairId = null;
    var currentComplaintId = null;
    var currentUserId = <c:out value="${sessionScope.currentUser.userId}" default="1" />;
    var allCharts = []; // 🔥 统一管理所有图表

    $(function() {
        console.log('🚀 系统初始化...');
        loadDashboardData();
        loadPendingRepairs();
        loadComplaintStatistics();
        loadPendingComplaints();

        // 检查屏幕宽度，移动端默认隐藏侧边栏
        if ($(window).width() <= 768) {
            toggleSidebar();
        }

        // 🔥 统一监听窗口大小变化
        window.addEventListener('resize', function() {
            allCharts.forEach(function(chart) {
                if (chart && !chart.isDisposed()) {
                    chart.resize();
                }
            });
        });
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

    // 🔥 加载仪表盘数据（已修复）
    function loadDashboardData() {
        console.log('📊 加载仪表盘数据...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/statistics',
            type: 'GET',
            data: { method: 'dashboard' },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 仪表盘数据:', result);
                if (result.success) {
                    var data = result.data;

                    // 基础数据
                    $('#totalHouses').text(data.totalHouses || 0);
                    $('#occupiedHouses').text(data.occupiedHouses || 0);
                    $('#vacantHouses').text(data.vacantHouses || 0);
                    $('#totalOwners').text(data.totalOwners || 0);

                    // 缴费数据
                    var monthlyIncome = data.monthlyIncome || 0;
                    $('#monthlyIncome').text(monthlyIncome.toLocaleString());
                    var rate = data.paymentRate || 0;
                    $('#paymentRate').text((typeof rate === 'number' ? rate.toFixed(2) : '0.00') + '%');
                    $('#paidCount').text(data.paidCount || 0);
                    $('#unpaidCount').text(data.unpaidCount || 0);

                    // 🔥 报修数据（修复）
                    $('#pendingRepairs').text(data.pendingRepairs || 0);
                    $('#processingRepairs').text(data.processingRepairs || 0);
                    $('#completedRepairs').text(data.completedRepairs || 0);
                    var avgRating = data.avgRating || 0;
                    $('#avgRating').text((typeof avgRating === 'number' ? avgRating.toFixed(1) : '0.0'));

                    drawHouseChart(data);
                    drawRepairChart(data);
                } else {
                    console.error('❌ 仪表盘数据加载失败:', result.message);
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 仪表盘请求失败:', {
                    status: status,
                    error: error,
                    response: xhr.responseText
                });
                // 设置默认值
                $('#totalHouses, #totalOwners, #monthlyIncome, #pendingRepairs, #processingRepairs, #completedRepairs').text('0');
                $('#paymentRate, #avgRating').text('0.0');
            }
        });

        // 加载收费趋势
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/statistics',
            type: 'GET',
            data: { method: 'paymentTrend' },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 收费趋势数据:', result);
                if (result.success) {
                    drawTrendChart(result.data);
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 收费趋势请求失败:', error);
            }
        });
    }

    // 🔥 加载投诉统计数据（已修复）
    function loadComplaintStatistics() {
        console.log('📊 加载投诉统计...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/statistics',
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                console.log('✅ 投诉统计数据:', result);
                if (result.success) {
                    // 🔥 修复：兼容 result.data 和 result.overall
                    var data = result.data || result.overall || {};

                    // 🔥 兼容驼峰和下划线命名
                    $('#pendingComplaints').text(data.pending_count || data.pendingCount || 0);
                    $('#processingComplaints').text(data.processing_count || data.processingCount || 0);
                    $('#resolvedComplaints').text(data.resolved_count || data.resolvedCount || 0);
                    var resolveRate = data.resolve_rate || data.resolveRate || 0;
                    $('#complaintResolveRate').text((typeof resolveRate === 'number' ? resolveRate.toFixed(2) : '0.00') + '%');

                    drawComplaintChart(data);
                } else {
                    console.error('❌ 投诉统计加载失败:', result.message);
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 投诉统计请求失败:', {
                    status: status,
                    error: error,
                    response: xhr.responseText
                });
                // 设置默认值
                $('#pendingComplaints, #processingComplaints, #resolvedComplaints').text('0');
                $('#complaintResolveRate').text('0.00%');
            }
        });
    }

    // 🔥 绘制房屋状态图表（修复图例和标签）
    function drawHouseChart(data) {
        if (typeof echarts === 'undefined') return;
        var chartDom = document.getElementById('houseChart');
        if (!chartDom) return;

        var chart = echarts.init(chartDom);
        allCharts.push(chart); // 添加到数组

        var chartData = [
            {value: data.occupiedHouses || 0, name: '已入住', itemStyle: {color: '#667eea'}},
            {value: data.vacantHouses || 0, name: '空置', itemStyle: {color: '#91cc75'}}
        ].filter(item => item.value > 0); // 🔥 过滤掉值为0的数据

        var option = {
            tooltip: {
                trigger: 'item',
                formatter: '{b}: {c} ({d}%)'
            },
            legend: {
                orient: 'horizontal', // 🔥 改为水平排列
                bottom: '5%',         // 🔥 放到底部
                left: 'center'        // 🔥 居中显示
            },
            series: [{
                type: 'pie',
                radius: ['40%', '65%'],
                center: ['50%', '42%'], // 🔥 饼图居中偏上
                data: chartData,
                label: {
                    formatter: '{b}\n{c}',
                    fontSize: 12,
                    fontWeight: 'bold',
                    position: 'outside',
                    alignTo: 'labelLine',
                    distanceToLabelLine: 5
                },
                labelLine: {
                    length: 15,
                    length2: 10
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }]
        };
        chart.setOption(option);
    }

    // 🔥 绘制报修状态图表（修复：添加已取消状态）
    function drawRepairChart(data) {
        if (typeof echarts === 'undefined') return;
        var chartDom = document.getElementById('repairChart');
        if (!chartDom) return;

        var chart = echarts.init(chartDom);
        allCharts.push(chart); // 添加到数组

        var chartData = [
            {value: data.pendingRepairs || 0, name: '待处理', itemStyle: {color: '#fac858'}},
            {value: data.processingRepairs || 0, name: '处理中', itemStyle: {color: '#5470c6'}},
            {value: data.completedRepairs || 0, name: '已完成', itemStyle: {color: '#91cc75'}},
            {value: data.cancelledRepairs || 0, name: '已取消', itemStyle: {color: '#ee6666'}}  // ✅ 添加已取消
        ].filter(item => item.value > 0); // 🔥 过滤掉值为0的数据

        var option = {
            tooltip: {
                trigger: 'item',
                formatter: '{b}: {c} ({d}%)'
            },
            legend: {
                orient: 'horizontal', // 🔥 改为水平排列
                bottom: '5%',         // 🔥 放到底部
                left: 'center'        // 🔥 居中显示
            },
            series: [{
                type: 'pie',
                radius: ['40%', '65%'],
                center: ['50%', '42%'], // 🔥 饼图居中偏上
                data: chartData,
                label: {
                    formatter: '{b}\n{c}',
                    fontSize: 12,
                    fontWeight: 'bold',
                    position: 'outside',
                    alignTo: 'labelLine',
                    distanceToLabelLine: 5
                },
                labelLine: {
                    length: 15,
                    length2: 10
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }]
        };
        chart.setOption(option);
    }


    // 🔥 绘制投诉状态分布图表（修复图例和标签）
    function drawComplaintChart(data) {
        if (typeof echarts === 'undefined') return;
        var chartDom = document.getElementById('complaintChart');
        if (!chartDom) return;

        var chart = echarts.init(chartDom);
        allCharts.push(chart); // 添加到数组

        // 🔥 兼容驼峰和下划线命名，并过滤掉值为0的数据
        var chartData = [
            {value: data.pending_count || data.pendingCount || 0, name: '待处理', itemStyle: {color: '#ff6b6b'}},
            {value: data.processing_count || data.processingCount || 0, name: '处理中', itemStyle: {color: '#4facfe'}},
            {value: data.resolved_count || data.resolvedCount || 0, name: '已解决', itemStyle: {color: '#91cc75'}},
            {value: data.closed_count || data.closedCount || 0, name: '已关闭', itemStyle: {color: '#9e9e9e'}}
        ].filter(item => item.value > 0); // 🔥 过滤掉值为0的数据

        var option = {
            tooltip: {
                trigger: 'item',
                formatter: '{b}: {c} ({d}%)'
            },
            legend: {
                orient: 'horizontal', // 🔥 改为水平排列
                bottom: '5%',         // 🔥 放到底部
                left: 'center'        // 🔥 居中显示
            },
            series: [{
                type: 'pie',
                radius: ['40%', '65%'],
                center: ['50%', '42%'], // 🔥 饼图居中偏上
                data: chartData,
                label: {
                    formatter: '{b}\n{c}',
                    fontSize: 12,
                    fontWeight: 'bold',
                    position: 'outside',
                    alignTo: 'labelLine',
                    distanceToLabelLine: 5
                },
                labelLine: {
                    length: 15,
                    length2: 10
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }]
        };
        chart.setOption(option);
    }

    // 🔥 绘制收费趋势图表（修复Y轴刻度）
    function drawTrendChart(data) {
        if (typeof echarts === 'undefined') return;
        var chartDom = document.getElementById('trendChart');
        if (!chartDom) return;

        var chart = echarts.init(chartDom);
        allCharts.push(chart); // 添加到数组

        var months = [], totalAmounts = [], paidAmounts = [];

        if (data.months) {
            months = data.months;
            totalAmounts = data.totalAmounts;
            paidAmounts = data.paidAmounts;
        } else if (Array.isArray(data)) {
            data.forEach(function(item) {
                months.push(item.month);
                totalAmounts.push(item.totalAmount);
                paidAmounts.push(item.paidAmount);
            });
        }

        var option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: { type: 'shadow' },
                formatter: function(params) {
                    var result = params[0].name + '<br/>';
                    params.forEach(function(item) {
                        result += item.marker + item.seriesName + ': ¥' + item.value.toLocaleString() + '<br/>';
                    });
                    return result;
                }
            },
            legend: { data: ['应收金额', '实收金额'], top: 10 },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: {
                type: 'category',
                data: months,
                axisLabel: { rotate: 30, fontSize: 12 }
            },
            yAxis: {
                type: 'value',
                name: '金额（元）',
                axisLabel: {
                    formatter: function(value) {
                        // 🔥 修复：优化金额显示格式
                        if (value >= 10000) {
                            return '¥' + (value / 10000).toFixed(1) + 'w';
                        } else if (value >= 1000) {
                            return '¥' + (value / 1000).toFixed(1) + 'k';
                        }
                        return '¥' + value.toFixed(0);
                    }
                }
            },
            series: [
                {
                    name: '应收金额',
                    type: 'line',
                    data: totalAmounts,
                    smooth: true,
                    itemStyle: { color: '#667eea' },
                    areaStyle: {
                        color: {
                            type: 'linear',
                            x: 0, y: 0, x2: 0, y2: 1,
                            colorStops: [
                                {offset: 0, color: 'rgba(102, 126, 234, 0.3)'},
                                {offset: 1, color: 'rgba(102, 126, 234, 0.05)'}
                            ]
                        }
                    }
                },
                {
                    name: '实收金额',
                    type: 'line',
                    data: paidAmounts,
                    smooth: true,
                    itemStyle: { color: '#91cc75' },
                    areaStyle: {
                        color: {
                            type: 'linear',
                            x: 0, y: 0, x2: 0, y2: 1,
                            colorStops: [
                                {offset: 0, color: 'rgba(145, 204, 117, 0.3)'},
                                {offset: 1, color: 'rgba(145, 204, 117, 0.05)'}
                            ]
                        }
                    }
                }
            ]
        };
        chart.setOption(option);
    }

    // 🔥 加载待处理投诉列表（已修复）
    function loadPendingComplaints() {
        console.log('📋 加载待处理投诉...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/',
            type: 'GET',
            data: { complaintStatus: 'pending', pageSize: 5 },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 待处理投诉数据:', result);
                if (result.success || result.list) {
                    var tbody = $('#pendingComplaintTable tbody');
                    tbody.empty();
                    var complaints = result.list || [];

                    if (complaints.length === 0) {
                        tbody.append('<tr><td colspan="6" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无待处理投诉</td></tr>');
                        return;
                    }

                    complaints.forEach(function(complaint) {
                        // 🔥 兼容驼峰和下划线命名
                        var complaintId = complaint.complaintId || complaint.complaint_id;
                        var complaintType = complaint.complaintType || complaint.complaint_type;
                        var ownerName = complaint.ownerName || complaint.owner_name;
                        var isAnonymous = complaint.isAnonymous || complaint.is_anonymous;
                        var submitTime = complaint.submitTime || complaint.submit_time;

                        var typeClass = 'type-' + complaintType;
                        var typeName = getComplaintTypeName(complaintType);
                        var ownerInfo = isAnonymous === 1 ? '匿名用户' : (ownerName || '-');

                        var tr = '<tr>' +
                            '<td>' + complaintId + '</td>' +
                            '<td>' + ownerInfo + '</td>' +
                            '<td style="max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="' + (complaint.title || '') + '">' + (complaint.title || '') + '</td>' +
                            '<td><span class="type-badge ' + typeClass + '">' + typeName + '</span></td>' +
                            '<td>' + formatDate(submitTime) + '</td>' +
                            '<td>' +
                            '<button class="btn btn-sm btn-info btn-action" onclick="viewComplaint(' + complaintId + ')" title="查看详情"><i class="fas fa-eye"></i></button>' +
                            '</td>' +
                            '</tr>';
                        tbody.append(tr);
                    });
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 待处理投诉请求失败:', error);
                $('#pendingComplaintTable tbody').html('<tr><td colspan="6" class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</td></tr>');
            }
        });
    }


    // 🔥 受理投诉（使用模态框）
    $('#btnAcceptComplaint').click(function() {
        if (!currentComplaintId) return;
        $('#complaintDetailModal').modal('hide');
        $('#acceptComplaintModal').modal('show');
    });

    function confirmAcceptComplaint() {
        console.log('✅ 受理投诉:', currentComplaintId);
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/accept',
            type: 'POST',
            data: { complaintId: currentComplaintId, handlerId: currentUserId },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 受理结果:', result);
                if (result.success) {
                    layer.msg('受理成功', {icon: 1});
                    $('#acceptComplaintModal').modal('hide');
                    loadPendingComplaints();
                    loadComplaintStatistics();
                } else {
                    layer.msg(result.message || '受理失败', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 受理请求失败:', error);
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    // 🔥 回复投诉（使用模态框）
    $('#btnReplyComplaint').click(function() {
        if (!currentComplaintId) return;
        $('#complaintDetailModal').modal('hide');
        $('#replyComplaintForm')[0].reset();
        $('#replyComplaintModal').modal('show');
    });

    function confirmReplyComplaint() {
        var form = $('#replyComplaintForm')[0];
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        var reply = $('#replyContent').val();
        var newStatus = $('#newStatus').val();

        console.log('💬 回复投诉:', {complaintId: currentComplaintId, reply: reply, newStatus: newStatus});

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/reply',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                complaintId: currentComplaintId,
                handlerId: currentUserId,
                reply: reply,
                newStatus: newStatus
            }),
            dataType: 'json',
            success: function(result) {
                console.log('✅ 回复结果:', result);
                if (result.success) {
                    layer.msg('回复成功', {icon: 1});
                    $('#replyComplaintModal').modal('hide');
                    loadPendingComplaints();
                    loadComplaintStatistics();
                } else {
                    layer.msg(result.message || '回复失败', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 回复请求失败:', error);
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    // 🔥 加载待处理报修（已修复）
    function loadPendingRepairs() {
        console.log('📋 加载待处理报修...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'GET',
            data: { method: 'findPending', pageSize: 5 },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 待处理报修数据:', result);
                if (result.success) {
                    var tbody = $('#pendingRepairTable tbody');
                    tbody.empty();
                    if (!result.data || result.data.length === 0) {
                        tbody.append('<tr><td colspan="6" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无待处理报修</td></tr>');
                        return;
                    }
                    result.data.forEach(function(repair) {
                        // 🔥 兼容驼峰和下划线命名
                        var repairId = repair.repairId || repair.repair_id;
                        var ownerName = repair.ownerName || repair.owner_name;
                        var houseId = repair.houseId || repair.house_id;
                        var repairType = repair.repairType || repair.repair_type;
                        var priority = repair.priority;

                        var priorityClass = priority === 'emergency' ? 'label-danger' :
                            priority === 'urgent' ? 'label-warning' : 'label-default';
                        var priorityText = priority === 'emergency' ? '紧急' :
                            priority === 'urgent' ? '加急' : '普通';
                        var tr = '<tr>' +
                            '<td>' + repairId + '</td>' +
                            '<td>' + (ownerName || '-') + '</td>' +
                            '<td>' + (houseId || '-') + '</td>' +
                            '<td>' + getRepairTypeName(repairType) + '</td>' +
                            '<td><span class="label ' + priorityClass + '">' + priorityText + '</span></td>' +
                            '<td><button class="btn btn-sm btn-info btn-action" onclick="viewRepair(' + repairId + ')" title="查看详情"><i class="fas fa-eye"></i></button></td>' +
                            '</tr>';
                        tbody.append(tr);
                    });
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 待处理报修请求失败:', error);
                $('#pendingRepairTable tbody').html('<tr><td colspan="6" class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</td></tr>');
            }
        });
    }
    /**
     * ✨ 查看投诉详情（只优化追加内容显示）
     */
    function viewComplaint(complaintId) {
        console.log('👁️ 查看投诉详情:', complaintId);
        currentComplaintId = complaintId;
        $('#complaintDetailModal').modal('show');
        $('#complaintDetailContent').html('<div class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/detail/' + complaintId,
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                console.log('✅ 投诉详情数据:', result);
                if (result.success && result.data) {
                    var complaint = result.data;

                    // 🔥 兼容驼峰和下划线命名
                    var complaintId = complaint.complaintId || complaint.complaint_id;
                    var ownerName = complaint.ownerName || complaint.owner_name;
                    var ownerPhone = complaint.ownerPhone || complaint.owner_phone;
                    var isAnonymous = complaint.isAnonymous || complaint.is_anonymous;
                    var complaintType = complaint.complaintType || complaint.complaint_type;
                    var complaintStatus = complaint.complaintStatus || complaint.complaint_status;
                    var handlerName = complaint.handlerName || complaint.handler_name;
                    var submitTime = complaint.submitTime || complaint.submit_time;
                    var replyTime = complaint.replyTime || complaint.reply_time;
                    var responseHours = complaint.responseHours || complaint.response_hours;

                    var ownerInfo = isAnonymous === 1 ? '匿名用户' : (ownerName || '-');
                    var phoneInfo = isAnonymous === 1 ? '***' : (ownerPhone || '-');

                    var html = '<div class="detail-card">' +
                        '<div class="detail-header">' +
                        '<h4><i class="fas fa-comment-dots"></i> ' + (complaint.title || '投诉详情') + '</h4>' +
                        '<p><i class="fas fa-hashtag"></i> 投诉编号：' + complaintId + ' | <i class="fas fa-clock"></i> 提交时间：' + formatDateTime(submitTime) + '</p>' +
                        '</div>' +

                        '<div class="detail-section">' +
                        '<h6 style="margin-bottom: 15px; font-weight: 600;"><i class="fas fa-info-circle"></i> 基本信息</h6>' +
                        '<div class="detail-grid">' +
                        '<div class="detail-item"><small><i class="fas fa-user"></i> 业主姓名</small><div class="value">' + ownerInfo + '</div></div>' +
                        '<div class="detail-item"><small><i class="fas fa-phone"></i> 联系电话</small><div class="value">' + phoneInfo + '</div></div>' +
                        '<div class="detail-item"><small><i class="fas fa-tag"></i> 投诉类型</small><div class="value"><span class="type-badge type-' + complaintType + '">' + getComplaintTypeName(complaintType) + '</span></div></div>' +
                        '<div class="detail-item"><small><i class="fas fa-tasks"></i> 当前状态</small><div class="value"><span class="status-badge status-' + complaintStatus + '">' + getComplaintStatusName(complaintStatus) + '</span></div></div>' +
                        '<div class="detail-item"><small><i class="fas fa-user-tie"></i> 处理人</small><div class="value">' + (handlerName || '未分配') + '</div></div>' +
                        '<div class="detail-item"><small><i class="fas fa-user-secret"></i> 是否匿名</small><div class="value">' + (isAnonymous === 1 ? '<span class="badge badge-warning">是</span>' : '<span class="badge badge-info">否</span>') + '</div></div>' +
                        '</div>' +
                        '</div>';

                    // 🔥 投诉内容区域（时间线样式 - 只优化这一部分）
                    html += '<div class="detail-item">' +
                        '<small><i class="fas fa-align-left"></i> 投诉内容</small>' +
                        '<div style="position: relative; padding: 20px 0;">';

                    // 时间线左侧线条
                    html += '<div style="position: absolute; left: 20px; top: 0; bottom: 0; width: 2px; background: linear-gradient(180deg, #f093fb 0%, #f5576c 100%);"></div>';

                    // 解析内容 (分离原始内容和追加内容)
                    var fullContent = complaint.content || '';
                    var contentParts = fullContent.split(/【.*?追加】/);
                    var timeMatches = fullContent.match(/【(.*?)追加】/g);

                    // 原始投诉内容
                    html += '<div style="position: relative; padding-left: 60px; margin-bottom: 25px;">' +
                        '<div style="position: absolute; left: 10px; top: 0; width: 24px; height: 24px; border-radius: 50%; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; display: flex; align-items: center; justify-content: center; font-size: 12px; box-shadow: 0 2px 8px rgba(240, 147, 251, 0.4); z-index: 2;">' +
                        '<i class="fas fa-file-alt"></i>' +
                        '</div>' +
                        '<div style="background: white; border: 2px solid #f093fb; border-radius: 10px; padding: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: all 0.3s ease;">' +
                        '<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; padding-bottom: 10px; border-bottom: 1px solid #f0f0f0;">' +
                        '<span style="font-weight: 600; color: #495057; font-size: 14px;"><i class="fas fa-comment" style="margin-right: 5px; color: #f093fb;"></i> 原始投诉</span>' +
                        '<span style="font-size: 12px; color: #999;">' + formatDateTime(submitTime) + '</span>' +
                        '</div>' +
                        '<div style="line-height: 1.8; color: #333; font-size: 14px; white-space: pre-wrap; word-break: break-word;">' + (contentParts[0] || '无') + '</div>' +
                        '</div>' +
                        '</div>';

                    // ✅ 追加内容 (如果有)
                    if (contentParts.length > 1) {
                        for (var i = 1; i < contentParts.length; i++) {
                            var appendTime = timeMatches && timeMatches[i-1] ? timeMatches[i-1].replace(/【|追加】/g, '').trim() : '未知时间';
                            html += '<div style="position: relative; padding-left: 60px; margin-bottom: 25px;">' +
                                '<div style="position: absolute; left: 10px; top: 0; width: 24px; height: 24px; border-radius: 50%; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; display: flex; align-items: center; justify-content: center; font-size: 12px; box-shadow: 0 2px 8px rgba(79, 172, 254, 0.4); z-index: 2;">' +
                                '<i class="fas fa-plus-circle"></i>' +
                                '</div>' +
                                '<div style="background: white; border: 2px solid #4facfe; border-radius: 10px; padding: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: all 0.3s ease;">' +
                                '<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; padding-bottom: 10px; border-bottom: 1px solid #f0f0f0;">' +
                                '<span style="font-weight: 600; color: #495057; font-size: 14px;"><i class="fas fa-edit" style="margin-right: 5px; color: #4facfe;"></i> 追加说明 #' + i + '</span>' +
                                '<span style="font-size: 12px; color: #999;">' + appendTime + '</span>' +
                                '</div>' +
                                '<div style="line-height: 1.8; color: #333; font-size: 14px; white-space: pre-wrap; word-break: break-word;">' + contentParts[i].trim() + '</div>' +
                                '</div>' +
                                '</div>';
                        }
                    }

                    html += '</div></div>'; // 结束时间线

                    // 回复内容（保持原样）
                    if (complaint.reply) {
                        html += '<div class="detail-item">' +
                            '<small><i class="fas fa-reply"></i> 回复内容</small>' +
                            '<div class="reply-box"><i class="fas fa-check-circle"></i> ' + complaint.reply + '</div>' +
                            '</div>' +
                            '<div class="detail-item">' +
                            '<small><i class="fas fa-calendar-check"></i> 回复时间</small>' +
                            '<div class="value">' + formatDateTime(replyTime) + '</div>' +
                            '</div>';
                    }

                    // 响应时长（保持原样）
                    if (responseHours !== undefined && responseHours !== null) {
                        html += '<div class="detail-item">' +
                            '<small><i class="fas fa-hourglass-half"></i> 响应时长</small>' +
                            '<div class="value"><span class="badge badge-primary">' + responseHours + ' 小时</span></div>' +
                            '</div>';
                    }

                    html += '</div>';

                    $('#complaintDetailContent').html(html);

                    // 根据状态显示按钮
                    if (complaintStatus === 'pending') {
                        $('#btnAcceptComplaint').show();
                        $('#btnReplyComplaint').hide();
                    } else if (complaintStatus === 'processing') {
                        $('#btnAcceptComplaint').hide();
                        $('#btnReplyComplaint').show();
                    } else {
                        $('#btnAcceptComplaint').hide();
                        $('#btnReplyComplaint').hide();
                    }
                } else {
                    $('#complaintDetailContent').html('<div class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</div>');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 投诉详情请求失败:', error);
                $('#complaintDetailContent').html('<div class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</div>');
            }
        });
    }

    // 🔥 查看报修详情（已修复）
    function viewRepair(repairId) {
        console.log('👁️ 查看报修详情:', repairId);
        currentRepairId = repairId;
        $('#repairDetailModal').modal('show');
        $('#repairDetailContent').html('<div class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'GET',
            data: { method: 'detail', repairId: repairId },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 报修详情数据:', result);
                if (result.success && result.data) {
                    var repair = result.data;

                    // 🔥 兼容驼峰和下划线命名
                    var repairId = repair.repairId || repair.repair_id;
                    var ownerId = repair.ownerId || repair.owner_id;
                    var ownerName = repair.ownerName || repair.owner_name;
                    var ownerPhone = repair.ownerPhone || repair.owner_phone;
                    var houseId = repair.houseId || repair.house_id;
                    var repairType = repair.repairType || repair.repair_type;
                    var repairStatus = repair.repairStatus || repair.repair_status;
                    var submitTime = repair.submitTime || repair.submit_time;
                    var acceptTime = repair.acceptTime || repair.accept_time;
                    var completeTime = repair.completeTime || repair.complete_time;
                    var handlerPhone = repair.handlerPhone || repair.handler_phone;
                    var repairResult = repair.repairResult || repair.repair_result;
                    var satisfactionRating = repair.satisfactionRating || repair.satisfaction_rating;

                    var typeIcon = getRepairTypeIcon(repairType);
                    var typeName = getRepairTypeName(repairType);

                    var content =
                        '<div style="padding: 0; font-family: Microsoft YaHei, Arial, sans-serif;">' +
                        '<div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 25px; border-radius: 12px; margin-bottom: 20px; box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);">' +
                        '<h4 style="margin: 0 0 10px 0; font-size: 22px; font-weight: 600;"><i class="fas fa-tools"></i> 报修详情</h4>' +
                        '<p style="margin: 0; opacity: 0.95; font-size: 14px;">报修编号：' + repairId + ' | 提交时间：' + formatDateTime(submitTime) + '</p>' +
                        '</div>' +

                        '<div style="background: #f8f9fa; padding: 20px; border-radius: 10px; margin-bottom: 15px;">' +
                        '<h6 style="margin-bottom: 15px; font-weight: 600; color: #2c3e50;"><i class="fas fa-info-circle"></i> 基本信息</h6>' +
                        '<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">' +

                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-user" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">业主信息</div>' +
                        '<div style="font-weight: 600; color: #333;">' + (ownerName || ownerId) + '</div>' +
                        (ownerPhone ? '<div style="font-size: 12px; color: #666;">' + ownerPhone + '</div>' : '') +
                        '</div>' +
                        '</div>' +

                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-home" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">房屋编号</div>' +
                        '<div style="font-weight: 600; color: #333;">' + (houseId || '-') + '</div>' +
                        '</div>' +
                        '</div>' +

                        '<div style="display: flex; align-items: center;">' +
                        typeIcon +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">报修类型</div>' +
                        '<div style="font-weight: 600; color: #333;">' + typeName + '</div>' +
                        '</div>' +
                        '</div>' +

                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-flag" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">优先级</div>' +
                        '<div style="font-weight: 600; color: #333;">' + getPriorityText(repair.priority) + '</div>' +
                        '</div>' +
                        '</div>' +

                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-info-circle" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">状态</div>' +
                        '<div style="font-weight: 600; color: #333;">' + getRepairStatusName(repairStatus) + '</div>' +
                        '</div>' +
                        '</div>' +

                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-clock" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">提交时间</div>' +
                        '<div style="font-weight: 600; color: #333;">' + formatDateTime(submitTime) + '</div>' +
                        '</div>' +
                        '</div>' +

                        '</div>' +
                        '</div>';

                    if (repair.description) {
                        content +=
                            '<div style="background: #fff3cd; border-left: 5px solid #ffc107; padding: 18px; border-radius: 10px; margin-bottom: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">' +
                            '<div style="font-size: 12px; color: #856404; margin-bottom: 8px; font-weight: 600;"><i class="fas fa-comment-dots"></i> 问题描述</div>' +
                            '<div style="color: #856404; line-height: 1.8;">' + repair.description + '</div>' +
                            '</div>';
                    }

                    if (repair.handler) {
                        content +=
                            '<div style="background: #d1ecf1; border-left: 5px solid #17a2b8; padding: 18px; border-radius: 10px; margin-bottom: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">' +
                            '<div style="font-size: 12px; color: #0c5460; margin-bottom: 8px; font-weight: 600;"><i class="fas fa-user-tie"></i> 处理信息</div>' +
                            '<div style="color: #0c5460; line-height: 1.6;">' +
                            '<div><strong>处理人：</strong>' + repair.handler + '</div>' +
                            (handlerPhone ? '<div><strong>联系电话：</strong>' + handlerPhone + '</div>' : '') +
                            (acceptTime ? '<div><strong>受理时间：</strong>' + formatDateTime(acceptTime) + '</div>' : '') +
                            '</div>' +
                            '</div>';
                    }

                    if (repairResult) {
                        content +=
                            '<div style="background: #d4edda; border-left: 5px solid #28a745; padding: 18px; border-radius: 10px; margin-bottom: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">' +
                            '<div style="font-size: 12px; color: #155724; margin-bottom: 8px; font-weight: 600;"><i class="fas fa-check-circle"></i> 处理结果</div>' +
                            '<div style="color: #155724; line-height: 1.8;">' + repairResult + '</div>' +
                            (completeTime ? '<div style="margin-top: 10px; color: #155724;"><strong>完成时间：</strong>' + formatDateTime(completeTime) + '</div>' : '') +
                            '</div>';
                    }

                    if (satisfactionRating) {
                        var stars = '';
                        for (var j = 1; j <= 5; j++) {
                            if (j <= satisfactionRating) {
                                stars += '<i class="fas fa-star" style="color: #ffc107; margin-right: 3px;"></i>';
                            } else {
                                stars += '<i class="far fa-star" style="color: #ffc107; margin-right: 3px;"></i>';
                            }
                        }

                        content +=
                            '<div style="background: #f8d7da; border-left: 5px solid #dc3545; padding: 18px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">' +
                            '<div style="font-size: 12px; color: #721c24; margin-bottom: 8px; font-weight: 600;"><i class="fas fa-star"></i> 评价信息</div>' +
                            '<div style="color: #721c24; line-height: 1.6;">' +
                            '<div style="margin-bottom: 10px;"><strong>满意度：</strong>' + stars + ' (' + satisfactionRating + '分)</div>' +
                            (repair.feedback ? '<div><strong>反馈：</strong>' + repair.feedback + '</div>' : '') +
                            '</div>' +
                            '</div>';
                    }

                    content += '</div>';

                    $('#repairDetailContent').html(content);

                    if (repairStatus === 'pending') {
                        $('#btnAcceptRepair').show();
                    } else {
                        $('#btnAcceptRepair').hide();
                    }
                } else {
                    $('#repairDetailContent').html('<div class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</div>');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 报修详情请求失败:', error);
                $('#repairDetailContent').html('<div class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</div>');
            }
        });
    }

    // 🔥 受理报修（使用模态框）
    $('#btnAcceptRepair').click(function() {
        if (!currentRepairId) return;
        $('#repairDetailModal').modal('hide');
        $('#acceptRepairForm')[0].reset();
        $('#acceptRepairModal').modal('show');
    });

    function confirmAcceptRepair() {
        var form = $('#acceptRepairForm')[0];
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        var handler = $('#handlerName').val();
        var handlerPhone = $('#handlerPhone').val();

        console.log('✅ 受理报修:', {repairId: currentRepairId, handler: handler, handlerPhone: handlerPhone});

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'POST',
            data: {
                method: 'accept',
                repairId: currentRepairId,
                handler: handler,
                handlerPhone: handlerPhone
            },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 受理结果:', result);
                if (result.success) {
                    layer.msg('受理成功', {icon: 1});
                    $('#acceptRepairModal').modal('hide');
                    loadPendingRepairs();
                    loadDashboardData();
                } else {
                    layer.msg(result.message || '受理失败', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 受理请求失败:', error);
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    // 🔥 辅助函数
    function getComplaintTypeName(type) {
        var types = {
            'environment': '环境卫生',
            'facility': '设施维护',
            'service': '服务态度',
            'fee': '费用问题',
            'other': '其他'
        };
        return types[type] || type;
    }

    function getComplaintStatusName(status) {
        var statuses = {
            'pending': '待处理',
            'processing': '处理中',
            'resolved': '已解决',
            'closed': '已关闭'
        };
        return statuses[status] || status;
    }

    function getRepairTypeName(type) {
        var types = {
            'plumbing': '水电维修',
            'electrical': '电路维修',
            'door_window': '门窗维修',
            'public_facility': '公共设施',
            'other': '其他'
        };
        return types[type] || type;
    }

    function getRepairStatusName(status) {
        var statuses = {
            'pending': '待处理',
            'processing': '处理中',
            'completed': '已完成',
            'cancelled': '已取消'
        };
        return statuses[status] || status;
    }

    function getPriorityText(priority) {
        var priorities = {
            'normal': '普通',
            'urgent': '加急',
            'emergency': '紧急'
        };
        return priorities[priority] || priority;
    }

    function getRepairTypeIcon(type) {
        var icons = {
            'plumbing': '<i class="fas fa-tint" style="color: #667eea; width: 30px; font-size: 16px;"></i>',
            'electrical': '<i class="fas fa-bolt" style="color: #667eea; width: 30px; font-size: 16px;"></i>',
            'door_window': '<i class="fas fa-door-open" style="color: #667eea; width: 30px; font-size: 16px;"></i>',
            'public_facility': '<i class="fas fa-building" style="color: #667eea; width: 30px; font-size: 16px;"></i>',
            'other': '<i class="fas fa-wrench" style="color: #667eea; width: 30px; font-size: 16px;"></i>'
        };
        return icons[type] || icons['other'];
    }

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
                String(date.getMinutes()).padStart(2, '0');
        } catch (e) {
            console.error('日期格式化错误:', e);
            return dateTime;
        }
    }

    function formatDate(dateStr) {
        return formatDateTime(dateStr);
    }
</script>

</body>
</html>
