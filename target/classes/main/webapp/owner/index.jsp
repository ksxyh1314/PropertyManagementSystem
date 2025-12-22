<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>业主首页 - 社区物业管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/owner.css">
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">社区物业管理系统</a>
        </div>
        <ul class="nav navbar-nav">
            <li class="active"><a href="${pageContext.request.contextPath}/owner/index.jsp">首页</a></li>
            <li><a href="${pageContext.request.contextPath}/owner/payment.jsp">我的缴费</a></li>
            <li><a href="${pageContext.request.contextPath}/owner/repair.jsp">我的报修</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="#"><i class="glyphicon glyphicon-user"></i> ${sessionScope.currentUser.realName}</a></li>
            <li><a href="${pageContext.request.contextPath}/login?method=logout">
                <i class="glyphicon glyphicon-log-out"></i> 退出
            </a></li>
        </ul>
    </div>
</nav>

<div class="container" style="margin-top: 80px;">
    <div class="row">
        <!-- 个人信息卡片 -->
        <div class="col-md-4">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h4><i class="glyphicon glyphicon-user"></i> 个人信息</h4>
                </div>
                <div class="panel-body" id="ownerInfo">
                    <p><strong>业主ID：</strong><span id="ownerId"></span></p>
                    <p><strong>姓名：</strong><span id="ownerName"></span></p>
                    <p><strong>电话：</strong><span id="phone"></span></p>
                    <p><strong>房屋编号：</strong><span id="houseId"></span></p>
                    <p><strong>家庭人数：</strong><span id="memberCount"></span></p>
                </div>
            </div>
        </div>

        <!-- 欠费统计卡片 -->
        <div class="col-md-4">
            <div class="panel panel-warning">
                <div class="panel-heading">
                    <h4><i class="glyphicon glyphicon-credit-card"></i> 欠费统计</h4>
                </div>
                <div class="panel-body text-center">
                    <h2 class="text-danger" id="unpaidAmount">¥0.00</h2>
                    <p>待缴费金额</p>
                    <button class="btn btn-success btn-block" onclick="location.href='payment.jsp'">
                        立即缴费
                    </button>
                </div>
            </div>
        </div>

        <!-- 报修统计卡片 -->
        <div class="col-md-4">
            <div class="panel panel-info">
                <div class="panel-heading">
                    <h4><i class="glyphicon glyphicon-wrench"></i> 报修统计</h4>
                </div>
                <div class="panel-body text-center">
                    <h2 class="text-info" id="repairCount">0</h2>
                    <p>待处理报修</p>
                    <button class="btn btn-primary btn-block" onclick="location.href='repair.jsp'">
                        我要报修
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- 未缴费账单 -->
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4><i class="glyphicon glyphicon-list-alt"></i> 未缴费账单</h4>
                </div>
                <div class="panel-body">
                    <table class="table table-striped" id="unpaidTable">
                        <thead>
                        <tr>
                            <th>收费项目</th>
                            <th>缴费期限</th>
                            <th>应缴金额</th>
                            <th>滞纳金</th>
                            <th>合计</th>
                            <th>截止日期</th>
                            <th>逾期天数</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- 最近报修记录 -->
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4><i class="glyphicon glyphicon-wrench"></i> 最近报修记录</h4>
                </div>
                <div class="panel-body">
                    <table class="table table-striped" id="repairTable">
                        <thead>
                        <tr>
                            <th>报修ID</th>
                            <th>报修类型</th>
                            <th>问题描述</th>
                            <th>状态</th>
                            <th>提交时间</th>
                            <th>处理人</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
<script>
    var ownerId = '${sessionScope.currentUser.username}';

    $(function() {
        loadOwnerInfo();
        loadUnpaidAmount();
        loadUnpaidPayments();
        loadRecentRepairs();
    });

    // 加载业主信息
    function loadOwnerInfo() {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner?method=findById',
            type: 'GET',
            data: {ownerId: ownerId},
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var owner = result.data;
                    $('#ownerId').text(owner.ownerId);
                    $('#ownerName').text(owner.ownerName);
                    $('#phone').text(owner.phone);
                    $('#houseId').text(owner.houseId);
                    $('#memberCount').text(owner.memberCount);
                }
            }
        });
    }

    // 加载欠费总额
    function loadUnpaidAmount() {
        $.ajax({
            url: '${pageContext.request.contextPath}/payment?method=sumUnpaid',
            type: 'GET',
            data: {ownerId: ownerId},
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    $('#unpaidAmount').text('¥' + result.data.toFixed(2));
                }
            }
        });
    }

    // 加载未缴费账单
    function loadUnpaidPayments() {
        $.ajax({
            url: '${pageContext.request.contextPath}/payment?method=findUnpaid',
            type: 'GET',
            data: {ownerId: ownerId},
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var tbody = $('#unpaidTable tbody');
                    tbody.empty();

                    if (result.data.length === 0) {
                        tbody.append('<tr><td colspan="8" class="text-center">暂无未缴费账单</td></tr>');
                        return;
                    }

                    result.data.forEach(function(payment) {
                        var overdueClass = payment.overdueDays > 0 ? 'text-danger' : '';

                        var tr = '<tr>' +
                            '<td>' + payment.itemName + '</td>' +
                            '<td>' + payment.billingPeriod + '</td>' +
                            '<td>¥' + payment.amount.toFixed(2) + '</td>' +
                            '<td class="text-danger">¥' + payment.lateFee.toFixed(2) + '</td>' +
                            '<td><strong>¥' + payment.totalAmount.toFixed(2) + '</strong></td>' +
                            '<td>' + formatDate(payment.dueDate) + '</td>' +
                            '<td class="' + overdueClass + '">' + payment.overdueDays + '天</td>' +
                            '<td>' +
                            '<button class="btn btn-sm btn-success" onclick="payNow(' + payment.recordId + ')">立即缴费</button>' +
                            '</td>' +
                            '</tr>';
                        tbody.append(tr);
                    });
                }
            }
        });
    }

    // 加载最近报修记录
    function loadRecentRepairs() {
        $.ajax({
            url: '${pageContext.request.contextPath}/repair?method=findByOwner',
            type: 'GET',
            data: {ownerId: ownerId},
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var tbody = $('#repairTable tbody');
                    tbody.empty();

                    var pendingCount = 0;
                    var records = result.data.slice(0, 5); // 只显示最近5条

                    if (records.length === 0) {
                        tbody.append('<tr><td colspan="7" class="text-center">暂无报修记录</td></tr>');
                        return;
                    }

                    records.forEach(function(repair) {
                        if (repair.repairStatus === 'pending' || repair.repairStatus === 'processing') {
                            pendingCount++;
                        }

                        var statusClass = repair.repairStatus === 'completed' ? 'label-success' :
                            repair.repairStatus === 'processing' ? 'label-warning' : 'label-default';
                        var statusText = repair.repairStatus === 'completed' ? '已完成' :
                            repair.repairStatus === 'processing' ? '处理中' : '待处理';

                        var actionBtn = repair.repairStatus === 'completed' && !repair.satisfactionRating ?
                            '<button class="btn btn-sm btn-primary" onclick="rateRepair(' + repair.repairId + ')">评价</button>' : '';

                        var tr = '<tr>' +
                            '<td>' + repair.repairId + '</td>' +
                            '<td>' + getRepairTypeName(repair.repairType) + '</td>' +
                            '<td>' + repair.description + '</td>' +
                            '<td><span class="label ' + statusClass + '">' + statusText + '</span></td>' +
                            '<td>' + formatDateTime(repair.submitTime) + '</td>' +
                            '<td>' + (repair.handler || '-') + '</td>' +
                            '<td>' + actionBtn + '</td>' +
                            '</tr>';
                        tbody.append(tr);
                    });

                    $('#repairCount').text(pendingCount);
                }
            }
        });
    }

    // 立即缴费
    function payNow(recordId) {
        location.href = 'payment.jsp?recordId=' + recordId;
    }

    // 获取报修类型名称
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

    // 格式化日期
    function formatDate(date) {
        if (!date) return '';
        var d = new Date(date);
        return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate());
    }

    // 格式化日期时间
    function formatDateTime(dateTime) {
        if (!dateTime) return '';
        var date = new Date(dateTime);
        return date.getFullYear() + '-' +
            pad(date.getMonth() + 1) + '-' +
            pad(date.getDate()) + ' ' +
            pad(date.getHours()) + ':' +
            pad(date.getMinutes());
    }

    function pad(num) {
        return num < 10 ? '0' + num : num;
    }
</script>
</body>
</html>
