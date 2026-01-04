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
        body { background-color: #f5f7fa; font-family: 'Microsoft YaHei', sans-serif; }

        /* 导航栏 */
        .navbar { box-shadow: 0 2px 10px rgba(0,0,0,0.05); background: #fff; }
        .navbar-brand { font-weight: bold; color: #667eea !important; }

        /* 顶部欢迎区 */
        .welcome-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; padding: 30px 0; margin-bottom: 30px;
            border-radius: 0 0 20px 20px; box-shadow: 0 4px 15px rgba(118, 75, 162, 0.3);
        }

        /* 卡片通用样式 */
        .card-box {
            background: #fff; border-radius: 10px; border: none;
            box-shadow: 0 2px 15px rgba(0,0,0,0.03); margin-bottom: 25px;
            transition: transform 0.2s;
        }
        .card-box:hover { transform: translateY(-3px); box-shadow: 0 5px 20px rgba(0,0,0,0.08); }
        .card-header {
            background: transparent; border-bottom: 1px solid #f0f0f0;
            font-weight: bold; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center;
        }

        /* 统计小卡片 */
        .stat-card { display: flex; align-items: center; padding: 20px; }
        .stat-icon {
            width: 50px; height: 50px; border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            font-size: 24px; margin-right: 15px; color: white;
        }
        .bg-orange { background: linear-gradient(45deg, #ff9966, #ff5e62); }
        .bg-blue { background: linear-gradient(45deg, #56ccf2, #2f80ed); }
        .bg-green { background: linear-gradient(45deg, #11998e, #38ef7d); }
        .bg-purple { background: linear-gradient(45deg, #834d9b, #d04ed6); }

        .stat-info h3 { margin: 0; font-weight: bold; color: #333; }
        .stat-info p { margin: 0; color: #888; font-size: 13px; }

        /* 表格样式 */
        .table thead th { border-top: none; border-bottom: 2px solid #eee; color: #666; font-weight: 600; }
        .table td { vertical-align: middle; }

        /* 公告列表 */
        .notice-list .list-group-item { border: none; border-bottom: 1px dashed #eee; padding: 12px 0; }
        .notice-list .list-group-item:last-child { border-bottom: none; }
        .notice-badge { font-size: 12px; padding: 3px 8px; border-radius: 4px; margin-right: 8px; }

        /* 个人信息 */
        .profile-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f9f9f9; }
        .profile-label { color: #888; }
        .profile-val { font-weight: 600; color: #333; }

        /* 快捷服务图标 */
        .service-icon { cursor: pointer; transition: transform 0.2s; }
        .service-icon:hover { transform: scale(1.1); }
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
                        <img src="https://ui-avatars.com/api/?name=${sessionScope.user.realName}&background=random" class="rounded-circle mr-1" width="30">
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
                <h2 class="mb-2">欢迎回家,${sessionScope.user.realName} 👋</h2>
                <p class="mb-0 opacity-80">今天是 <span id="currentDate"></span>,祝您生活愉快!</p>
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

<div class="container">
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
                    <a href="announcement.jsp" class="small text-muted">查看全部 ></a>
                </div>
                <div class="card-body pt-0">
                    <div class="list-group notice-list" id="noticeList">
                        <div class="text-center py-3 text-muted small">加载中...</div>
                    </div>
                </div>
            </div>

            <!-- 待缴费账单 -->
            <div class="card-box">
                <div class="card-header">
                    <span><i class="fas fa-file-invoice-dollar text-warning mr-2"></i>待缴费账单</span>
                    <a href="payment.jsp" class="small text-muted">去缴费 ></a>
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
                            <tr><td colspan="6" class="text-center text-muted small py-3">加载中...</td></tr>
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
                            <a class="nav-link active" id="repair-tab" data-toggle="tab" href="#repair" role="tab"><i class="fas fa-wrench mr-1"></i>最近报修</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="complaint-tab" data-toggle="tab" href="#complaint" role="tab"><i class="fas fa-comment-alt mr-1"></i>我的投诉</a>
                        </li>
                    </ul>
                </div>
                <div class="card-body">
                    <div class="tab-content" id="myTabContent">
                        <!-- 报修列表 -->
                        <div class="tab-pane fade show active" id="repair" role="tabpanel">
                            <table class="table table-sm" id="repairTable">
                                <thead><tr><th>类型</th><th>描述</th><th>状态</th><th>时间</th></tr></thead>
                                <tbody><tr><td colspan="4" class="text-center text-muted small">加载中...</td></tr></tbody>
                            </table>
                        </div>
                        <!-- 投诉列表 -->
                        <div class="tab-pane fade" id="complaint" role="tabpanel">
                            <table class="table table-sm" id="complaintTable">
                                <thead><tr><th>类型</th><th>标题</th><th>状态</th><th>时间</th></tr></thead>
                                <tbody><tr><td colspan="4" class="text-center text-muted small">加载中...</td></tr></tbody>
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
                </div>
                <div class="card-body">
                    <div class="profile-row">
                        <span class="profile-label">业主姓名</span>
                        <span class="profile-val" id="ownerName">${sessionScope.user.realName}</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label">联系电话</span>
                        <span class="profile-val" id="phone">-</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label">房屋数量</span>
                        <span class="profile-val"><span id="houseCount">0</span> 套</span>
                    </div>
                    <div class="profile-row">
                        <span class="profile-label">家庭成员</span>
                        <span class="profile-val"><span id="memberCount">0</span> 人</span>
                    </div>
                    <div class="profile-row border-0">
                        <span class="profile-label">注册日期</span>
                        <span class="profile-val" id="registerDate">-</span>
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
            <div class="card-box bg-light">
                <div class="card-body">
                    <h6 class="font-weight-bold mb-3"><i class="fas fa-building mr-2 text-primary"></i>物业服务中心</h6>
                    <p class="small text-muted mb-2"><i class="fas fa-phone-alt mr-2 text-success"></i>24小时热线:<strong>010-88888888</strong></p>
                    <p class="small text-muted mb-2"><i class="fas fa-clock mr-2 text-warning"></i>服务时间:周一至周日 8:00-18:00</p>
                    <p class="small text-muted mb-0"><i class="fas fa-map-marker-alt mr-2 text-danger"></i>地址:小区正门东侧办公楼101</p>
                </div>
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
                    console.log('房屋数量:', houses.length);

                    if(houses.length > 0) {
                        var firstHouse = houses[0];
                        if(firstHouse.phone) {
                            $('#phone').text(firstHouse.phone);
                            console.log('联系电话:', firstHouse.phone);
                        }
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

                    // 更新家庭成员数量
                    if(data.memberCount) {
                        $('#memberCount').text(data.memberCount);
                        console.log('家庭成员:', data.memberCount, '人');
                    }

                    // 更新注册日期
                    if(data.registerDate) {
                        $('#registerDate').text(formatDate(data.registerDate));
                        console.log('注册日期:', formatDate(data.registerDate));
                    }

                    // 更新联系电话(如果之前没获取到)
                    if(data.phone && $('#phone').text() === '-') {
                        $('#phone').text(data.phone);
                        console.log('联系电话:', data.phone);
                    }
                } else {
                    console.warn('⚠️ 业主详细信息返回格式异常');
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
                    console.log('待缴金额: ¥' + total.toFixed(2));
                } else {
                    console.warn('⚠️ 欠费汇总返回格式异常');
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

                console.log('账单数量:', list.length);

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
                        '<i class="fas fa-check-circle mr-2"></i>太棒了,所有账单已结清!</td></tr>');
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

                console.log('报修数量:', list.length);

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
                console.log('进行中的报修:', ongoingCount);
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
                pageSize: 4
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

                console.log('公告数量:', list.length);

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

                console.log('投诉数量:', list.length);

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

    function showContact() {
        alert('物业服务中心\n\n24小时热线:010-88888888\n服务时间:周一至周日 8:00-18:00\n地址:小区正门东侧办公楼101');
    }
</script>

</body>
</html>
