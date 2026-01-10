<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>业主中心 - 智慧社区</title>

    <!-- 引入 Bootstrap 4 & FontAwesome -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
            font-family: 'Microsoft YaHei', sans-serif;
        }

        /* 导航栏 */
        .navbar {
            box-shadow: 0 2px 15px rgba(0,0,0,0.08);
            background: #fff;
        }
        .navbar-brand {
            font-weight: bold;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        /* 顶部欢迎区 */
        .welcome-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px 0;
            margin-bottom: 30px;
            border-radius: 0 0 20px 20px;
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.3);
            position: relative;
            overflow: hidden;
        }
        .welcome-section::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -5%;
            width: 200px;
            height: 200px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
        }
        .welcome-section::after {
            content: '';
            position: absolute;
            bottom: -30%;
            left: -3%;
            width: 150px;
            height: 150px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
        }
        .welcome-section .container {
            position: relative;
            z-index: 1;
        }

        /* 卡片通用样式 */
        .card-box {
            background: #fff;
            border-radius: 12px;
            border: none;
            box-shadow: 0 2px 15px rgba(0,0,0,0.05);
            margin-bottom: 25px;
            transition: all 0.3s ease;
        }
        .card-box:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 25px rgba(0,0,0,0.1);
        }
        .card-header {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-bottom: 2px solid #e9ecef;
            font-weight: bold;
            padding: 15px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-radius: 12px 12px 0 0 !important;
        }

        /* 统计小卡片 */
        .stat-card {
            display: flex;
            align-items: center;
            padding: 20px;
            cursor: pointer;
        }
        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-right: 15px;
            color: white;
            box-shadow: 0 3px 10px rgba(0,0,0,0.15);
        }
        .bg-orange { background: linear-gradient(135deg, #ff9966, #ff5e62); }
        .bg-blue { background: linear-gradient(135deg, #56ccf2, #2f80ed); }
        .bg-green { background: linear-gradient(135deg, #11998e, #38ef7d); }
        .bg-purple { background: linear-gradient(135deg, #834d9b, #d04ed6); }

        .stat-info h3 {
            margin: 0;
            font-weight: bold;
            color: #333;
        }
        .stat-info p {
            margin: 0;
            color: #888;
            font-size: 13px;
        }

        /* 表格样式 */
        .table thead th {
            border-top: none;
            border-bottom: 2px solid #e9ecef;
            color: #666;
            font-weight: 600;
            background: #f8f9fa;
        }
        .table td {
            vertical-align: middle;
        }
        .table-hover tbody tr:hover {
            background: #f8f9fa;
        }

        /* 公告列表 */
        .notice-list .list-group-item {
            border: none;
            border-bottom: 1px dashed #e9ecef;
            padding: 12px 0;
            transition: all 0.2s;
        }
        .notice-list .list-group-item:hover {
            background: #f8f9fa;
            padding-left: 10px;
            border-left: 3px solid #667eea;
        }
        .notice-list .list-group-item:last-child {
            border-bottom: none;
        }
        .notice-badge {
            font-size: 11px;
            padding: 4px 10px;
            border-radius: 10px;
            margin-right: 8px;
            font-weight: 600;
        }

        /* 个人信息 */
        .profile-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;
            transition: all 0.2s;
        }
        .profile-row:hover {
            background: #f8f9fa;
            padding-left: 10px;
            padding-right: 10px;
            margin-left: -10px;
            margin-right: -10px;
            border-radius: 8px;
        }
        .profile-row:last-child {
            border-bottom: none;
        }
        .profile-label {
            color: #888;
            font-size: 13px;
            flex-shrink: 0;
            width: 100px;
        }
        .profile-val {
            font-weight: 600;
            color: #333;
            text-align: right;
            word-break: break-all;
        }

        /* 房屋卡片样式 */
        .house-card {
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 12px;
            margin-bottom: 10px;
            transition: all 0.3s;
            cursor: pointer;
            background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
        }
        .house-card:hover {
            border-color: #667eea;
            box-shadow: 0 3px 15px rgba(102, 126, 234, 0.2);
            transform: translateX(5px);
            background: #fff;
        }
        .house-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
        }
        .house-number {
            font-size: 16px;
            font-weight: bold;
            color: #667eea;
        }
        .house-info-row {
            display: flex;
            justify-content: space-between;
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }

        /* 快捷服务图标 */
        .service-icon {
            cursor: pointer;
            transition: all 0.3s;
            display: block;
            text-decoration: none;
            padding: 10px;
            border-radius: 10px;
        }
        .service-icon:hover {
            transform: scale(1.05);
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            text-decoration: none;
        }
        .service-icon:hover .h4,
        .service-icon:hover .small {
            color: white !important;
        }

        /* Tab 样式 */
        .nav-tabs {
            border-bottom: 2px solid #e9ecef;
        }
        .nav-tabs .nav-link {
            border: none;
            color: #666;
            font-weight: 500;
            border-bottom: 3px solid transparent;
        }
        .nav-tabs .nav-link:hover {
            border-color: transparent;
            color: #667eea;
        }
        .nav-tabs .nav-link.active {
            color: #667eea;
            border-bottom: 3px solid #667eea;
            background: transparent;
            font-weight: 600;
        }

        /* 按钮美化 */
        .btn-light {
            background: white;
            border: 2px solid white;
        }
        .btn-light:hover {
            background: rgba(255,255,255,0.9);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        .btn-outline-light:hover {
            background: rgba(255,255,255,0.2);
            border-color: white;
            transform: translateY(-2px);
        }

        /* 徽章美化 */
        .badge {
            padding: 5px 10px;
            border-radius: 10px;
            font-weight: 600;
        }

        /* 模态框美化 */
        .modal-content {
            border-radius: 15px;
            border: none;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px 15px 0 0;
            border: none;
        }
        .modal-header .close {
            color: white;
            opacity: 0.8;
        }
        .modal-header .close:hover {
            opacity: 1;
        }

        /* 加载动画 */
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
</head>
<body>

<!-- 导航栏 -->
<nav class="navbar navbar-expand-lg navbar-light fixed-top">
    <div class="container">
        <a class="navbar-brand" href="#"><i class="fas fa-building mr-2"></i>智慧社区</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item active"><a class="nav-link font-weight-bold" href="index.jsp">首页</a></li>
                <li class="nav-item"><a class="nav-link" href="payment.jsp">缴费中心</a></li>
                <li class="nav-item"><a class="nav-link" href="repair.jsp">在线报修</a></li>
                <li class="nav-item"><a class="nav-link" href="complaint.jsp">投诉建议</a></li>
                <li class="nav-item"><a class="nav-link" href="announcement.jsp">社区公告</a></li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown">
                        <img src="https://ui-avatars.com/api/?name=${sessionScope.user.realName}&background=667eea&color=fff" class="rounded-circle mr-1" width="30">
                        ${sessionScope.user.realName}
                    </a>
                    <div class="dropdown-menu dropdown-menu-right">
                        <a class="dropdown-item" href="profile.jsp">
                            <i class="fas fa-user mr-2"></i>个人信息
                        </a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/login?action=logout">
                            <i class="fas fa-sign-out-alt mr-2"></i>退出登录
                        </a>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- 顶部欢迎区 -->
<div class="welcome-section" style="margin-top: 56px;">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h2 class="mb-2"><i class="fas fa-hand-peace mr-2"></i>欢迎回家，${sessionScope.user.realName}</h2>
                <p class="mb-0 opacity-80">今天是 <span id="currentDate"></span>，祝您生活愉快！</p>
            </div>
            <div class="col-md-4 text-right d-none d-md-block">
                <button class="btn btn-light text-primary font-weight-bold shadow-sm" onclick="location.href='payment.jsp'">
                    <i class="fas fa-wallet mr-1"></i> 快速缴费
                </button>
                <button class="btn btn-outline-light font-weight-bold ml-2" onclick="location.href='repair.jsp'">
                    <i class="fas fa-tools mr-1"></i> 一键报修
                </button>
            </div>
        </div>
    </div>
</div>

<div class="container pb-4">
    <!-- 1. 核心数据统计卡片 -->
    <div class="row">
        <div class="col-md-3 col-sm-6">
            <div class="card-box stat-card">
                <div class="stat-icon bg-orange"><i class="fas fa-yen-sign"></i></div>
                <div class="stat-info">
                    <h3 id="unpaidAmount">0.00</h3>
                    <p>待缴费金额</p>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6">
            <div class="card-box stat-card">
                <div class="stat-icon bg-blue"><i class="fas fa-tools"></i></div>
                <div class="stat-info">
                    <h3 id="repairCount">0</h3>
                    <p>进行中的报修</p>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6">
            <div class="card-box stat-card">
                <div class="stat-icon bg-green"><i class="fas fa-comment-dots"></i></div>
                <div class="stat-info">
                    <h3 id="complaintCount">0</h3>
                    <p>我的投诉/建议</p>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6">
            <div class="card-box stat-card">
                <div class="stat-icon bg-purple"><i class="fas fa-bullhorn"></i></div>
                <div class="stat-info">
                    <h3 id="noticeCount">0</h3>
                    <p>最新公告</p>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- 左侧主要内容 -->
        <div class="col-lg-8">
            <!-- 最新公告 -->
            <div class="card-box">
                <div class="card-header">
                    <span><i class="fas fa-bullhorn text-primary mr-2"></i>最新社区公告</span>
                    <a href="announcement.jsp" class="small text-primary font-weight-bold">查看全部 <i class="fas fa-arrow-right ml-1"></i></a>
                </div>
                <div class="card-body pt-0">
                    <div class="list-group notice-list" id="noticeList">
                        <div class="text-center py-3 text-muted small">
                            <i class="fas fa-spinner fa-spin mr-2"></i>加载中...
                        </div>
                    </div>
                </div>
            </div>

            <!-- 待缴费账单 -->
            <div class="card-box">
                <div class="card-header">
                    <span><i class="fas fa-file-invoice-dollar text-warning mr-2"></i>待缴费账单</span>
                    <a href="payment.jsp" class="small text-primary font-weight-bold">去缴费 <i class="fas fa-arrow-right ml-1"></i></a>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover" id="unpaidTable">
                            <thead>
                            <tr>
                                <th>项目</th>
                                <th>账期</th>
                                <th>金额</th>
                                <th>滞纳金</th>
                                <th>截止日期</th>
                                <th>状态</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr><td colspan="6" class="text-center text-muted small py-3">
                                <i class="fas fa-spinner fa-spin mr-2"></i>加载中...
                            </td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Tab: 最近报修 & 我的投诉 -->
            <div class="card-box">
                <div class="card-header border-0 pb-0">
                    <ul class="nav nav-tabs card-header-tabs" id="myTab" role="tablist">
                        <li class="nav-item">
                            <a class="nav-link active" id="repair-tab" data-toggle="tab" href="#repair" role="tab">
                                <i class="fas fa-wrench mr-1"></i>最近报修
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="complaint-tab" data-toggle="tab" href="#complaint" role="tab">
                                <i class="fas fa-comment-alt mr-1"></i>我的投诉
                            </a>
                        </li>
                    </ul>
                </div>
                <div class="card-body">
                    <div class="tab-content" id="myTabContent">
                        <!-- 报修列表 -->
                        <div class="tab-pane fade show active" id="repair" role="tabpanel">
                            <table class="table table-sm table-hover" id="repairTable">
                                <thead><tr><th>类型</th><th>描述</th><th>状态</th><th>时间</th></tr></thead>
                                <tbody><tr><td colspan="4" class="text-center text-muted small py-3">
                                    <i class="fas fa-spinner fa-spin mr-2"></i>加载中...
                                </td></tr></tbody>
                            </table>
                        </div>
                        <!-- 投诉列表 -->
                        <div class="tab-pane fade" id="complaint" role="tabpanel">
                            <table class="table table-sm table-hover" id="complaintTable">
                                <thead><tr><th>类型</th><th>标题</th><th>状态</th><th>时间</th></tr></thead>
                                <tbody><tr><td colspan="4" class="text-center text-muted small py-3">
                                    <i class="fas fa-spinner fa-spin mr-2"></i>加载中...
                                </td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 右侧侧边栏 -->
        <div class="col-lg-4">
            <!-- 个人信息卡片 -->
            <div class="card-box">
                <div class="card-header">
                    <span><i class="fas fa-id-card text-info mr-2"></i>我的档案</span>
                    <a href="profile.jsp" class="small text-primary font-weight-bold">编辑 <i class="fas fa-edit ml-1"></i></a>
                </div>
                <div class="card-body">
                    <!-- 头像区域 -->
                    <div class="text-center mb-3 pb-3" style="border-bottom: 2px dashed #e9ecef;">
                        <img src="https://ui-avatars.com/api/?name=${sessionScope.user.realName}&background=667eea&color=fff&size=80&bold=true"
                             class="rounded-circle mb-2" width="80" height="80"
                             style="border: 3px solid #667eea; box-shadow: 0 3px 15px rgba(102, 126, 234, 0.3);">
                        <h5 class="mb-1 font-weight-bold" id="ownerNameDisplay">${sessionScope.user.realName}</h5>
                        <span class="badge badge-primary" id="ownerIdBadge">${sessionScope.user.username}</span>
                    </div>

                    <!-- 基本信息 -->
                    <div class="profile-row">
                        <span class="profile-label"><i class="fas fa-id-card mr-1 text-muted"></i> 业主编号</span>
                        <span class="profile-val" id="ownerId">${sessionScope.user.username}</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label"><i class="fas fa-phone mr-1 text-muted"></i> 联系电话</span>
                        <span class="profile-val" id="phone">-</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label"><i class="fas fa-id-card-alt mr-1 text-muted"></i> 身份证号</span>
                        <span class="profile-val" id="idCard">-</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label"><i class="fas fa-home mr-1 text-muted"></i> 房屋数量</span>
                        <span class="profile-val">
                            <span class="badge badge-primary badge-pill" id="houseCount">0</span> 套
                        </span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label"><i class="fas fa-users mr-1 text-muted"></i> 家庭成员</span>
                        <span class="profile-val"><span id="memberCount">0</span> 人</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label"><i class="fas fa-envelope mr-1 text-muted"></i> 电子邮箱</span>
                        <span class="profile-val" id="email">-</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label"><i class="fas fa-calendar-check mr-1 text-muted"></i> 注册日期</span>
                        <span class="profile-val" id="registerDate">-</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label"><i class="fas fa-info-circle mr-1 text-muted"></i> 账号状态</span>
                        <span class="profile-val"><span class="badge badge-success" id="accountStatus">正常</span></span>
                    </div>

                    <!-- 房屋列表（折叠显示） -->
                    <div class="mt-3 pt-3" style="border-top: 2px dashed #e9ecef;">
                        <a class="btn btn-sm btn-outline-primary btn-block" data-toggle="collapse" href="#houseListCollapse">
                            <i class="fas fa-building mr-1"></i> 我的房屋列表 (<span id="houseCountBadge">0</span>) <i class="fas fa-chevron-down float-right mt-1"></i>
                        </a>
                        <div class="collapse mt-2" id="houseListCollapse">
                            <div id="houseListDetail">
                                <div class="text-center text-muted small py-2">
                                    <i class="fas fa-spinner fa-spin mr-2"></i>加载中...
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 快捷服务 -->
            <div class="card-box">
                <div class="card-header">
                    <span><i class="fas fa-th-large text-secondary mr-2"></i>快捷服务</span>
                </div>
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-4 mb-3">
                            <a href="payment.jsp" class="text-decoration-none text-dark service-icon">
                                <div class="h4 text-warning mb-1"><i class="fas fa-file-invoice-dollar"></i></div>
                                <div class="small">在线缴费</div>
                            </a>
                        </div>
                        <div class="col-4 mb-3">
                            <a href="repair.jsp" class="text-decoration-none text-dark service-icon">
                                <div class="h4 text-primary mb-1"><i class="fas fa-tools"></i></div>
                                <div class="small">在线报修</div>
                            </a>
                        </div>
                        <div class="col-4 mb-3">
                            <a href="complaint.jsp" class="text-decoration-none text-dark service-icon">
                                <div class="h4 text-danger mb-1"><i class="fas fa-comment-dots"></i></div>
                                <div class="small">投诉建议</div>
                            </a>
                        </div>
                        <div class="col-4">
                            <a href="announcement.jsp" class="text-decoration-none text-dark service-icon">
                                <div class="h4 text-info mb-1"><i class="fas fa-bullhorn"></i></div>
                                <div class="small">社区公告</div>
                            </a>
                        </div>
                        <div class="col-4">
                            <a href="javascript:void(0)" onclick="showContact()" class="text-decoration-none text-dark service-icon">
                                <div class="h4 text-success mb-1"><i class="fas fa-phone-volume"></i></div>
                                <div class="small">联系物业</div>
                            </a>
                        </div>
                        <div class="col-4">
                            <a href="payment.jsp" class="text-decoration-none text-dark service-icon">
                                <div class="h4 text-secondary mb-1"><i class="fas fa-history"></i></div>
                                <div class="small">缴费记录</div>
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 物业联系方式 -->
            <div class="card-box" style="background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-left: 4px solid #667eea;">
                <div class="card-body">
                    <h6 class="font-weight-bold mb-3">
                        <i class="fas fa-building mr-2 text-primary"></i>物业服务中心
                    </h6>
                    <p class="small mb-2">
                        <i class="fas fa-phone-alt mr-2 text-success"></i>
                        <strong>24小时热线：</strong>010-88888888
                    </p>
                    <p class="small mb-2">
                        <i class="fas fa-clock mr-2 text-warning"></i>
                        <strong>服务时间：</strong>周一至周日 8:00-18:00
                    </p>
                    <p class="small mb-0">
                        <i class="fas fa-map-marker-alt mr-2 text-danger"></i>
                        <strong>地址：</strong>小区正门东侧办公楼101
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 🔥 房屋详情模态框 -->
<div class="modal fade" id="houseDetailModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-home mr-2"></i>房屋详细信息</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" id="houseDetailContent">
                <div class="text-center py-4">
                    <i class="fas fa-spinner fa-spin fa-2x text-primary"></i>
                    <p class="mt-2 text-muted">加载中...</p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times mr-1"></i>关闭
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>

<script>
    var contextPath = '${pageContext.request.contextPath}';
    var ownerId = '${sessionScope.user.username}';

    $(function() {
        console.log('========================================');
        console.log('🏠 业主首页初始化开始');
        console.log('业主ID:', ownerId);
        console.log('contextPath:', contextPath);
        console.log('========================================');

        // 设置当前日期
        var now = new Date();
        var weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
        $('#currentDate').text(now.getFullYear() + '年' + (now.getMonth()+1) + '月' + now.getDate() + '日 ' + weekDays[now.getDay()]);

        // 加载各模块数据
        console.log('开始加载各模块数据...');
        loadOwnerInfo();
        loadOwnerDetail();
        loadUnpaidSummary();
        loadUnpaidPayments();
        loadRecentRepairs();
        loadAnnouncements();
        loadComplaints();
    });

    // ==================== 1. 加载业主房屋信息 ====================
    function loadOwnerInfo() {
        console.log('📥 [1/7] 开始加载业主房屋信息...');

        $.ajax({
            url: contextPath + '/owner/repair',
            type: 'GET',
            data: { method: 'myHouses' },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 业主房屋信息返回成功', res);

                if((res.success || res.code === 200) && res.data) {
                    var houses = res.data;
                    $('#houseCount').text(houses.length);
                    $('#houseCountBadge').text(houses.length);
                    console.log('房屋数量:', houses.length);

                    // 🔥 渲染房屋列表
                    var houseListDiv = $('#houseListDetail');
                    houseListDiv.empty();

                    if(houses.length > 0) {
                        var firstHouse = houses[0];
                        if(firstHouse.phone) {
                            $('#phone').text(firstHouse.phone);
                            console.log('联系电话:', firstHouse.phone);
                        }

                        // 渲染每套房屋
                        $.each(houses, function(index, house) {
                            var houseId = house.houseId || house.house_id || '';
                            var buildingNo = house.buildingNo || house.building_no || '';
                            var unitNo = house.unitNo || house.unit_no || '';
                            var floor = house.floor || '';
                            var area = house.area || '-';
                            var layout = house.layout || house.houseType || house.house_type || '-';
                            var houseStatus = house.houseStatus || house.house_status || 'occupied';
                            var saleStatus = house.saleStatus || house.sale_status || 'sold';

                            // 格式化房屋编号：0101201 -> 1栋1单元201
                            var displayHouseNo = formatHouseNumber(houseId, buildingNo, unitNo, floor);

                            // 状态徽章
                            var statusBadge = getHouseStatusBadge(houseStatus);
                            var saleBadge = getSaleStatusBadge(saleStatus);

                            var houseHtml =
                                '<div class="house-card" onclick="showHouseDetail(\'' + houseId + '\')">' +
                                '<div class="house-card-header">' +
                                '<div class="house-number">' +
                                '<i class="fas fa-home mr-1"></i>' + displayHouseNo +
                                '</div>' +
                                '<div>' + statusBadge + ' ' + saleBadge + '</div>' +
                                '</div>' +
                                '<div class="house-info-row">' +
                                '<span><i class="fas fa-ruler-combined mr-1"></i>' + area + 'm²</span>' +
                                '<span><i class="fas fa-door-open mr-1"></i>' + layout + '</span>' +
                                '<span class="text-primary"><i class="fas fa-info-circle mr-1"></i>查看详情</span>' +
                                '</div>' +
                                '</div>';
                            houseListDiv.append(houseHtml);
                        });
                    } else {
                        houseListDiv.html('<div class="text-center text-muted small py-2">暂无房屋信息</div>');
                    }
                } else {
                    console.warn('⚠️ 业主房屋信息返回格式异常');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载业主房屋信息失败', error);
            }
        });
    }

    // ==================== 格式化房屋编号 ====================
    function formatHouseNumber(houseId, buildingNo, unitNo, floor) {
        if (!houseId || houseId.length !== 7) {
            return houseId || '-';
        }

        var building = parseInt(houseId.substring(0, 2)) || parseInt(buildingNo);
        var unit = parseInt(houseId.substring(2, 3)) || parseInt(unitNo);
        var floorNum = parseInt(houseId.substring(3, 5)) || parseInt(floor);
        var roomNum = houseId.substring(5, 7);

        return building + '栋' + unit + '单元' + floorNum + roomNum + '号';
    }

    // ==================== 房屋状态徽章 ====================
    function getHouseStatusBadge(status) {
        var badges = {
            'vacant': '<span class="badge badge-secondary badge-sm">空置</span>',
            'occupied': '<span class="badge badge-success badge-sm">已入住</span>',
            'rented': '<span class="badge badge-info badge-sm">出租中</span>'
        };
        return badges[status] || '<span class="badge badge-secondary badge-sm">' + status + '</span>';
    }

    // ==================== 销售状态徽章 ====================
    function getSaleStatusBadge(status) {
        var badges = {
            'for_sale': '<span class="badge badge-warning badge-sm">待售</span>',
            'sold': '<span class="badge badge-primary badge-sm">已售</span>',
            'leased': '<span class="badge badge-info badge-sm">已租</span>'
        };
        return badges[status] || '<span class="badge badge-secondary badge-sm">' + status + '</span>';
    }

    // ==================== 显示房屋详情 ====================
    function showHouseDetail(houseId) {
        console.log('🏠 查看房屋详情:', houseId);

        $('#houseDetailModal').modal('show');
        $('#houseDetailContent').html(
            '<div class="text-center py-4">' +
            '<i class="fas fa-spinner fa-spin fa-2x text-primary"></i>' +
            '<p class="mt-2 text-muted">加载中...</p>' +
            '</div>'
        );

        $.ajax({
            url: contextPath + '/owner/info',
            type: 'GET',
            data: {
                action: 'houseDetail',
                houseId: houseId
            },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 房屋详情返回:', res);

                if ((res.success || res.code === 200) && res.data) {
                    var house = res.data;
                    renderHouseDetail(house);
                } else {
                    $('#houseDetailContent').html(
                        '<div class="alert alert-warning text-center">' +
                        '<i class="fas fa-exclamation-triangle mr-2"></i>' +
                        (res.message || '加载失败') +
                        '</div>'
                    );
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载房屋详情失败:', error);
                $('#houseDetailContent').html(
                    '<div class="alert alert-danger text-center">' +
                    '<i class="fas fa-times-circle mr-2"></i>网络错误，请稍后重试' +
                    '</div>'
                );
            }
        });
    }

    // ==================== 渲染房屋详情 ====================
    function renderHouseDetail(house) {
        var houseId = house.houseId || house.house_id || '';
        var buildingNo = house.buildingNo || house.building_no || '';
        var unitNo = house.unitNo || house.unit_no || '';
        var floor = house.floor || '';
        var layout = house.layout || '-';
        var area = house.area || '-';
        var pricePerSqm = house.pricePerSqm || house.price_per_sqm || 0;
        var houseStatus = house.houseStatus || house.house_status || 'vacant';
        var saleStatus = house.saleStatus || house.sale_status || 'for_sale';
        var createTime = house.createTime || house.create_time;
        var updateTime = house.updateTime || house.update_time;

        var displayHouseNo = formatHouseNumber(houseId, buildingNo, unitNo, floor);
        var totalPrice = (parseFloat(area) * parseFloat(pricePerSqm)).toFixed(2);

        var html =
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<div class="alert" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; color: white;">' +
            '<h4 class="mb-0"><i class="fas fa-home mr-2"></i>' + displayHouseNo + '</h4>' +
            '</div>' +
            '</div>' +
            '</div>' +

            '<div class="row">' +
            '<div class="col-md-6">' +
            '<table class="table table-bordered table-sm">' +
            '<tr><th width="120"><i class="fas fa-hashtag mr-1 text-primary"></i>房屋编号</th><td>' + houseId + '</td></tr>' +
            '<tr><th><i class="fas fa-building mr-1 text-primary"></i>楼栋号</th><td>' + parseInt(buildingNo) + '栋</td></tr>' +
            '<tr><th><i class="fas fa-door-closed mr-1 text-primary"></i>单元号</th><td>' + parseInt(unitNo) + '单元</td></tr>' +
            '<tr><th><i class="fas fa-layer-group mr-1 text-primary"></i>楼层</th><td>' + parseInt(floor) + '层</td></tr>' +
            '<tr><th><i class="fas fa-door-open mr-1 text-primary"></i>户型</th><td>' + layout + '</td></tr>' +
            '</table>' +
            '</div>' +
            '<div class="col-md-6">' +
            '<table class="table table-bordered table-sm">' +
            '<tr><th width="120"><i class="fas fa-ruler-combined mr-1 text-primary"></i>建筑面积</th><td><strong class="text-primary">' + area + ' m²</strong></td></tr>' +
            '<tr><th><i class="fas fa-dollar-sign mr-1 text-primary"></i>单价</th><td>¥' + parseFloat(pricePerSqm).toFixed(2) + '/m²</td></tr>' +
            '<tr><th><i class="fas fa-coins mr-1 text-primary"></i>总价</th><td><strong class="text-danger">¥' + totalPrice + '</strong></td></tr>' +
            '<tr><th><i class="fas fa-info-circle mr-1 text-primary"></i>入住状态</th><td>' + getHouseStatusBadge(houseStatus) + '</td></tr>' +
            '<tr><th><i class="fas fa-tag mr-1 text-primary"></i>销售状态</th><td>' + getSaleStatusBadge(saleStatus) + '</td></tr>' +
            '</table>' +
            '</div>' +
            '</div>' +

            '<div class="row mt-3">' +
            '<div class="col-md-12">' +
            '<div class="card" style="background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border: none;">' +
            '<div class="card-body">' +
            '<h6 class="font-weight-bold mb-2"><i class="fas fa-clock mr-2 text-primary"></i>时间信息</h6>' +
            '<p class="mb-1 small"><strong>创建时间：</strong>' + formatDateTime(createTime) + '</p>' +
            '<p class="mb-0 small"><strong>更新时间：</strong>' + formatDateTime(updateTime) + '</p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';

        $('#houseDetailContent').html(html);
    }

    // ==================== 2. 加载业主详细信息 ====================
    function loadOwnerDetail() {
        console.log('📥 [2/7] 开始加载业主详细信息...');

        $.ajax({
            url: contextPath + '/owner/info',
            type: 'GET',
            data: { action: 'detail' },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 业主详细信息返回成功', res);

                if((res.success || res.code === 200) && res.data) {
                    var data = res.data;

                    if(data.ownerName || data.owner_name) {
                        var name = data.ownerName || data.owner_name;
                        $('#ownerNameDisplay').text(name);
                    }

                    if(data.ownerId || data.owner_id) {
                        var id = data.ownerId || data.owner_id;
                        $('#ownerId').text(id);
                        $('#ownerIdBadge').text(id);
                    }

                    if(data.phone) {
                        $('#phone').text(data.phone);
                    }

                    if(data.idCard || data.id_card) {
                        var idCard = data.idCard || data.id_card;
                        var maskedIdCard = idCard.length >= 18 ?
                            idCard.substring(0, 6) + '********' + idCard.substring(14) :
                            idCard.substring(0, 3) + '***' + idCard.substring(idCard.length - 2);
                        $('#idCard').text(maskedIdCard);
                    }

                    if(data.email) {
                        $('#email').text(data.email);
                    }

                    if(data.memberCount || data.member_count) {
                        var count = data.memberCount || data.member_count;
                        $('#memberCount').text(count);
                    }

                    if(data.registerDate || data.register_date) {
                        var date = data.registerDate || data.register_date;
                        $('#registerDate').text(formatDate(date));
                    }

                    if(data.status) {
                        var statusText = data.status === 'active' ? '正常' : '停用';
                        var statusClass = data.status === 'active' ? 'badge-success' : 'badge-danger';
                        $('#accountStatus').removeClass('badge-success badge-danger').addClass(statusClass).text(statusText);
                    }
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载业主详细信息失败', error);
            }
        });
    }

    // ==================== 3. 加载欠费汇总 ====================
    function loadUnpaidSummary() {
        console.log('📥 [3/7] 开始加载欠费汇总...');

        $.ajax({
            url: contextPath + '/owner/payment',
            type: 'GET',
            data: {
                action: 'summary',
                ownerId: ownerId
            },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 欠费汇总返回成功', res);

                if((res.success || res.code === 200) && res.data) {
                    var unpaidAmount = parseFloat(res.data.unpaid_amount || res.data.unpaidAmount || 0);
                    var overdueAmount = parseFloat(res.data.overdue_amount || res.data.overdueAmount || 0);
                    var total = unpaidAmount + overdueAmount;

                    $('#unpaidAmount').text(total.toFixed(2));
                } else {
                    $('#unpaidAmount').text('0.00');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载欠费汇总失败', error);
                $('#unpaidAmount').text('0.00');
            }
        });
    }

    // ==================== 4. 加载未缴费账单 ====================
    function loadUnpaidPayments() {
        console.log('📥 [4/7] 开始加载未缴费账单...');

        $.ajax({
            url: contextPath + '/owner/payment',
            type: 'GET',
            data: {
                action: 'list',
                ownerId: ownerId,
                paymentStatus: 'unpaid,overdue',
                pageNum: 1,
                pageSize: 5
            },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 未缴费账单返回成功', res);

                var tbody = $('#unpaidTable tbody');
                tbody.empty();

                var list = [];
                if((res.success || res.code === 200) && res.data) {
                    list = res.data.list || [];
                }

                if(list.length > 0) {
                    $.each(list, function(i, item) {
                        var itemName = item.itemName || item.item_name || '-';
                        var billingPeriod = item.billingPeriod || item.billing_period || '-';
                        var amount = parseFloat(item.amount || 0);
                        var lateFee = parseFloat(item.lateFee || item.late_fee || 0);
                        var dueDate = item.dueDate || item.due_date;
                        var paymentStatus = item.paymentStatus || item.payment_status;
                        var overdueDays = item.overdueDays || item.overdue_days || 0;

                        var statusHtml = '';
                        if(paymentStatus === 'overdue') {
                            var badgeClass = overdueDays > 60 ? 'badge-danger font-weight-bold' :
                                (overdueDays > 30 ? 'badge-danger' : 'badge-warning');
                            statusHtml = '<span class="badge ' + badgeClass + '">逾期 ' + overdueDays + ' 天</span>';
                        } else {
                            statusHtml = '<span class="badge badge-info">待缴费</span>';
                        }

                        var tr = '<tr>' +
                            '<td>' + itemName + '</td>' +
                            '<td>' + billingPeriod + '</td>' +
                            '<td class="text-primary font-weight-bold">¥' + amount.toFixed(2) + '</td>' +
                            '<td class="text-danger font-weight-bold">' + (lateFee > 0 ? '¥' + lateFee.toFixed(2) : '-') + '</td>' +
                            '<td class="small text-muted">' + formatDate(dueDate) + '</td>' +
                            '<td>' + statusHtml + '</td>' +
                            '</tr>';
                        tbody.append(tr);
                    });
                } else {
                    tbody.html('<tr><td colspan="6" class="text-center text-success py-3">' +
                        '<i class="fas fa-check-circle mr-2"></i>太棒了，所有账单已结清！</td></tr>');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载未缴费账单失败', error);
                $('#unpaidTable tbody').html('<tr><td colspan="6" class="text-center text-danger py-3">加载失败</td></tr>');
            }
        });
    }

    // ==================== 5. 加载最近报修 ====================
    function loadRecentRepairs() {
        console.log('📥 [5/7] 开始加载报修记录...');

        $.ajax({
            url: contextPath + '/owner/repair',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: 1,
                pageSize: 5
            },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 报修记录返回成功', res);

                var tbody = $('#repairTable tbody');
                tbody.empty();
                var ongoingCount = 0;

                var list = [];
                if((res.success || res.code === 200) && res.data) {
                    list = res.data.list || [];
                }

                if(list.length > 0) {
                    $.each(list, function(i, item) {
                        var repairType = item.repairType || item.repair_type;
                        var description = item.description || '-';
                        var repairStatus = item.repairStatus || item.repair_status;
                        var submitTime = item.submitTime || item.submit_time;

                        if(repairStatus === 'pending' || repairStatus === 'processing') {
                            ongoingCount++;
                        }

                        var statusBadge = getRepairStatusBadge(repairStatus);
                        var tr = '<tr>' +
                            '<td>' + getRepairTypeName(repairType) + '</td>' +
                            '<td class="text-truncate" style="max-width: 200px;" title="' + description + '">' +
                            (description.length > 20 ? description.substring(0, 20) + '...' : description) + '</td>' +
                            '<td>' + statusBadge + '</td>' +
                            '<td class="small text-muted">' + formatDate(submitTime) + '</td>' +
                            '</tr>';
                        tbody.append(tr);
                    });
                } else {
                    tbody.html('<tr><td colspan="4" class="text-center text-muted small py-3">暂无报修记录</td></tr>');
                }

                $('#repairCount').text(ongoingCount);
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载报修记录失败', error);
                $('#repairTable tbody').html('<tr><td colspan="4" class="text-center text-danger small">加载失败</td></tr>');
            }
        });
    }

    // ==================== 6. 加载最新公告 ====================
    function loadAnnouncements() {
        console.log('📥 [6/7] 开始加载公告列表...');

        $.ajax({
            url: contextPath + '/owner/announcement',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: 1,
                pageSize: 5
            },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 公告列表返回成功', res);

                var listDiv = $('#noticeList');
                listDiv.empty();

                var list = [];
                var total = 0;
                if((res.success || res.code === 200) && res.data) {
                    list = res.data.list || [];
                    total = res.data.totalCount || res.data.total || list.length;
                }

                if(list.length > 0) {
                    $('#noticeCount').text(total);

                    $.each(list, function(i, item) {
                        var announcementType = item.announcementType || item.announcement_type || 'notice';
                        var title = item.title || '无标题';
                        var publishTime = item.publishTime || item.publish_time;

                        var typeClass = 'badge-primary';
                        var typeName = '通知';

                        if(announcementType === 'emergency') {
                            typeClass = 'badge-danger';
                            typeName = '紧急';
                        } else if(announcementType === 'payment_reminder') {
                            typeClass = 'badge-warning';
                            typeName = '缴费';
                        } else if(announcementType === 'maintenance') {
                            typeClass = 'badge-info';
                            typeName = '维修';
                        }

                        var html = '<a href="announcement.jsp" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">' +
                            '<div class="text-truncate">' +
                            '<span class="badge ' + typeClass + ' notice-badge">' + typeName + '</span>' +
                            title +
                            '</div>' +
                            '<small class="text-muted ml-2">' + formatDate(publishTime) + '</small>' +
                            '</a>';
                        listDiv.append(html);
                    });
                } else {
                    listDiv.html('<div class="text-center text-muted small py-3">暂无公告</div>');
                    $('#noticeCount').text(0);
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载公告列表失败', error);
                $('#noticeList').html('<div class="text-center text-danger small py-3">加载失败</div>');
            }
        });
    }

    // ==================== 7. 加载我的投诉 ====================
    function loadComplaints() {
        console.log('📥 [7/7] 开始加载投诉记录...');

        $.ajax({
            url: contextPath + '/owner/complaint',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: 1,
                pageSize: 5
            },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 投诉记录返回成功', res);

                var tbody = $('#complaintTable tbody');
                tbody.empty();

                var list = [];
                var total = 0;
                if((res.success || res.code === 200) && res.data) {
                    list = res.data.list || [];
                    total = res.data.total || list.length;
                }

                if(list.length > 0) {
                    $('#complaintCount').text(total);

                    $.each(list, function(i, item) {
                        var complaintType = item.complaintType || item.complaint_type;
                        var title = item.title || '-';
                        var complaintStatus = item.complaintStatus || item.complaint_status;
                        var submitTime = item.submitTime || item.submit_time;

                        var statusBadge = getComplaintStatusBadge(complaintStatus);
                        var typeName = getComplaintTypeName(complaintType);

                        var tr = '<tr>' +
                            '<td>' + typeName + '</td>' +
                            '<td class="text-truncate" style="max-width: 200px;" title="' + title + '">' +
                            (title.length > 20 ? title.substring(0, 20) + '...' : title) + '</td>' +
                            '<td>' + statusBadge + '</td>' +
                            '<td class="small text-muted">' + formatDate(submitTime) + '</td>' +
                            '</tr>';
                        tbody.append(tr);
                    });
                } else {
                    tbody.html('<tr><td colspan="4" class="text-center text-muted small py-3">暂无投诉记录</td></tr>');
                    $('#complaintCount').text(0);
                }

                console.log('========================================');
                console.log('✅ 所有数据加载完成!');
                console.log('========================================');
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载投诉记录失败', error);
                $('#complaintTable tbody').html('<tr><td colspan="4" class="text-center text-danger small">加载失败</td></tr>');
            }
        });
    }

    // ==================== 工具函数 ====================

    function getRepairTypeName(type) {
        var map = {
            'plumbing': '水暖',
            'electrical': '电路',
            'door_window': '门窗',
            'public_facility': '公共设施',
            'other': '其他'
        };
        return map[type] || type || '-';
    }

    function getRepairStatusBadge(status) {
        var map = {
            'pending': '<span class="badge badge-secondary">待处理</span>',
            'processing': '<span class="badge badge-warning text-white">处理中</span>',
            'completed': '<span class="badge badge-success">已完成</span>',
            'cancelled': '<span class="badge badge-dark">已取消</span>'
        };
        return map[status] || '<span class="badge badge-secondary">未知</span>';
    }

    function getComplaintTypeName(type) {
        var map = {
            'service': '服务',
            'environment': '环境',
            'facility': '设施',
            'fee': '费用',
            'other': '其他'
        };
        return map[type] || type || '-';
    }

    function getComplaintStatusBadge(status) {
        var map = {
            'pending': '<span class="badge badge-secondary">待处理</span>',
            'processing': '<span class="badge badge-warning text-white">处理中</span>',
            'resolved': '<span class="badge badge-success">已解决</span>',
            'closed': '<span class="badge badge-dark">已关闭</span>'
        };
        return map[status] || '<span class="badge badge-secondary">未知</span>';
    }

    function formatDate(timestamp) {
        if(!timestamp) return '-';

        try {
            var date = new Date(timestamp);
            if(isNaN(date.getTime())) return '-';

            var now = new Date();
            var year = date.getFullYear();
            var month = String(date.getMonth() + 1).padStart(2, '0');
            var day = String(date.getDate()).padStart(2, '0');

            if(year === now.getFullYear()) {
                return parseInt(month) + '月' + parseInt(day) + '日';
            } else {
                return year + '年' + parseInt(month) + '月' + parseInt(day) + '日';
            }
        } catch(error) {
            console.error('日期格式化出错:', error);
            return '-';
        }
    }

    function formatDateTime(timestamp) {
        if(!timestamp) return '-';

        try {
            var date = new Date(timestamp);
            if(isNaN(date.getTime())) return '-';

            var year = date.getFullYear();
            var month = String(date.getMonth() + 1).padStart(2, '0');
            var day = String(date.getDate()).padStart(2, '0');
            var hour = String(date.getHours()).padStart(2, '0');
            var minute = String(date.getMinutes()).padStart(2, '0');

            return year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
        } catch(error) {
            console.error('日期时间格式化出错:', error);
            return '-';
        }
    }

    function showContact() {
        // 使用美化的模态框显示联系方式
        var modalHtml =
            '<div class="modal fade" id="contactModal" tabindex="-1">' +
            '<div class="modal-dialog modal-dialog-centered">' +
            '<div class="modal-content" style="border-radius: 15px; border: none; box-shadow: 0 10px 40px rgba(0,0,0,0.2);">' +
            '<div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 15px 15px 0 0; border: none;">' +
            '<h5 class="modal-title"><i class="fas fa-phone-volume mr-2"></i>物业服务中心</h5>' +
            '<button type="button" class="close text-white" data-dismiss="modal" style="opacity: 0.8;"><span>&times;</span></button>' +
            '</div>' +
            '<div class="modal-body p-4">' +
            '<div class="text-center mb-4">' +
            '<div style="width: 80px; height: 80px; margin: 0 auto 15px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center;">' +
            '<i class="fas fa-building fa-3x text-white"></i>' +
            '</div>' +
            '<h5 class="font-weight-bold mb-1">智慧社区物业管理中心</h5>' +
            '<p class="text-muted small mb-0">为您提供优质的物业服务</p>' +
            '</div>' +
            '<div class="list-group list-group-flush">' +
            '<div class="list-group-item border-0 px-0 py-3" style="background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 10px; margin-bottom: 10px;">' +
            '<i class="fas fa-phone-alt text-success mr-3 fa-lg"></i>' +
            '<strong>24小时热线：</strong><span class="text-primary font-weight-bold ml-2">010-88888888</span>' +
            '</div>' +
            '<div class="list-group-item border-0 px-0 py-3" style="background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 10px; margin-bottom: 10px;">' +
            '<i class="fas fa-clock text-warning mr-3 fa-lg"></i>' +
            '<strong>服务时间：</strong><span class="ml-2">周一至周日 8:00-18:00</span>' +
            '</div>' +
            '<div class="list-group-item border-0 px-0 py-3" style="background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 10px; margin-bottom: 10px;">' +
            '<i class="fas fa-map-marker-alt text-danger mr-3 fa-lg"></i>' +
            '<strong>地址：</strong><span class="ml-2">小区正门东侧办公楼101</span>' +
            '</div>' +
            '<div class="list-group-item border-0 px-0 py-3" style="background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 10px;">' +
            '<i class="fas fa-envelope text-info mr-3 fa-lg"></i>' +
            '<strong>邮箱：</strong><span class="ml-2">service@community.com</span>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer border-0">' +
            '<button type="button" class="btn btn-primary btn-block" data-dismiss="modal" style="border-radius: 10px;">' +
            '<i class="fas fa-check mr-2"></i>知道了' +
            '</button>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';

        // 移除旧的模态框（如果存在）
        $('#contactModal').remove();

        // 添加新的模态框并显示
        $('body').append(modalHtml);
        $('#contactModal').modal('show');

        // 模态框关闭后移除
        $('#contactModal').on('hidden.bs.modal', function() {
            $(this).remove();
        });
    }
</script>

</body>
</html>

