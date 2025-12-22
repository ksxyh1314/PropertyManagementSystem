<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员首页 - 社区物业管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/admin.css">
    <script src="${pageContext.request.contextPath}/static/js/echarts.min.js"></script>
</head>
<body>
<div class="wrapper">
    <!-- 侧边栏 -->
    <nav id="sidebar">
        <div class="sidebar-header">
            <h3>物业管理系统</h3>
            <p>管理员：${sessionScope.currentUser.realName}</p>
        </div>
        <ul class="list-unstyled components">
            <li class="active">
                <a href="${pageContext.request.contextPath}/admin/index.jsp">
                    <i class="glyphicon glyphicon-home"></i> 首页
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/user.jsp">
                    <i class="glyphicon glyphicon-user"></i> 用户管理
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/owner.jsp">
                    <i class="glyphicon glyphicon-list-alt"></i> 业主管理
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/house.jsp">
                    <i class="glyphicon glyphicon-home"></i> 房屋管理
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/chargeItem.jsp">
                    <i class="glyphicon glyphicon-list"></i> 收费项目
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/payment.jsp">
                    <i class="glyphicon glyphicon-credit-card"></i> 缴费管理
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/repair.jsp">
                    <i class="glyphicon glyphicon-wrench"></i> 报修管理
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/statistics.jsp">
                    <i class="glyphicon glyphicon-stats"></i> 统计报表
                </a>
            </li>
        </ul>
        <div class="sidebar-footer">
            <a href="${pageContext.request.contextPath}/login?method=logout" class="btn btn-danger btn-block">
                <i class="glyphicon glyphicon-log-out"></i> 退出登录
            </a>
        </div>
    </nav>

    <!-- 主内容区 -->
    <div id="content">
        <div class="container-fluid">
            <h2 class="page-title">系统概览</h2>

            <!-- 统计卡片 -->
            <div class="row">
                <div class="col-md-3">
                    <div class="stats-card bg-primary">
                        <div class="stats-icon">
                            <i class="glyphicon glyphicon-home"></i>
                        </div>
                        <div class="stats-info">
                            <h3 id="totalHouses">0</h3>
                            <p>房屋总数</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card bg-success">
                        <div class="stats-icon">
                            <i class="glyphicon glyphicon-user"></i>
                        </div>
                        <div class="stats-info">
                            <h3 id="totalOwners">0</h3>
                            <p>业主总数</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card bg-warning">
                        <div class="stats-icon">
                            <i class="glyphicon glyphicon-wrench"></i>
                        </div>
                        <div class="stats-info">
                            <h3 id="pendingRepairs">0</h3>
                            <p>待处理报修</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card bg-info">
                        <div class="stats-icon">
                            <i class="glyphicon glyphicon-credit-card"></i>
                        </div>
                        <div class="stats-info">
                            <h3 id="paymentRate">0%</h3>
                            <p>本月收缴率</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 图表区域 -->
            <div class="row" style="margin-top: 30px;">
                <div class="col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4>房屋状态分布</h4>
                        </div>
                        <div class="panel-body">
                            <div id="houseChart" style="height: 300px;"></div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4>报修状态分布</h4>
                        </div>
                        <div class="panel-body">
                            <div id="repairChart" style="height: 300px;"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row" style="margin-top: 20px;">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4>收费趋势（最近12个月）</h4>
                        </div>
                        <div class="panel-body">
                            <div id="trendChart" style="height: 350px;"></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 待处理事项 -->
            <div class="row" style="margin-top: 20px;">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4>待处理报修</h4>
                        </div>
                        <div class="panel-body">
                            <table class="table table-striped" id="pendingRepairTable">
                                <thead>
                                <tr>
                                    <th>报修ID</th>
                                    <th>业主姓名</th>
                                    <th>房屋编号</th>
                                    <th>报修类型</th>
                                    <th>问题描述</th>
                                    <th>优先级</th>
                                    <th>提交时间</th>
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
    </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
<script>
    $(function() {
        // 加载统计数据
        loadDashboardData();

        // 加载待处理报修
        loadPendingRepairs();
    });

    // 加载仪表盘数据
    function loadDashboardData() {
        $.ajax({
            url: '${pageContext.request.contextPath}/statistics?method=dashboard',
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var data = result.data;

                    // 更新统计卡片
                    $('#totalHouses').text(data.totalHouses);
                    $('#totalOwners').text(data.totalOwners);
                    $('#pendingRepairs').text(data.pendingRepairs);
                    $('#paymentRate').text(data.paymentRate + '%');

                    // 绘制房屋状态图表
                    drawHouseChart(data);

                    // 绘制报修状态图表
                    drawRepairChart(data);
                }
            }
        });

        // 加载收费趋势
        $.ajax({
            url: '${pageContext.request.contextPath}/statistics?method=paymentTrend',
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    drawTrendChart(result.data);
                }
            }
        });
    }

    // 绘制房屋状态图表
    function drawHouseChart(data) {
        var chart = echarts.init(document.getElementById('houseChart'));
        var option = {
            tooltip: {
                trigger: 'item',
                formatter: '{b}: {c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left'
            },
            series: [{
                type: 'pie',
                radius: '60%',
                data: [
                    {value: data.occupiedHouses, name: '已入住'},
                    {value: data.vacantHouses, name: '空置'}
                ],
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

    // 绘制报修状态图表
    function drawRepairChart(data) {
        var chart = echarts.init(document.getElementById('repairChart'));
        var option = {
            tooltip: {
                trigger: 'item',
                formatter: '{b}: {c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left'
            },
            series: [{
                type: 'pie',
                radius: '60%',
                data: [
                    {value: data.pendingRepairs, name: '待处理'},
                    {value: data.processingRepairs, name: '处理中'},
                    {value: data.completedRepairs, name: '已完成'}
                ],
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

    // 绘制收费趋势图表
    function drawTrendChart(data) {
        var chart = echarts.init(document.getElementById('trendChart'));

        var months = [];
        var totalAmounts = [];
        var paidAmounts = [];

        data.forEach(function(item) {
            months.push(item.month);
            totalAmounts.push(item.totalAmount);
            paidAmounts.push(item.paidAmount);
        });

        var option = {
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['应收金额', '实收金额']
            },
            xAxis: {
                type: 'category',
                data: months
            },
            yAxis: {
                type: 'value',
                name: '金额（元）'
            },
            series: [
                {
                    name: '应收金额',
                    type: 'line',
                    data: totalAmounts,
                    smooth: true
                },
                {
                    name: '实收金额',
                    type: 'line',
                    data: paidAmounts,
                    smooth: true
                }
            ]
        };
        chart.setOption(option);
    }

    // 加载待处理报修
    function loadPendingRepairs() {
        $.ajax({
            url: '${pageContext.request.contextPath}/repair?method=findPending',
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var tbody = $('#pendingRepairTable tbody');
                    tbody.empty();

                    if (result.data.length === 0) {
                        tbody.append('<tr><td colspan="8" class="text-center">暂无待处理报修</td></tr>');
                        return;
                    }

                    result.data.forEach(function(repair) {
                        var priorityClass = repair.priority === 'emergency' ? 'label-danger' :
                            repair.priority === 'urgent' ? 'label-warning' : 'label-default';
                        var priorityText = repair.priority === 'emergency' ? '紧急' :
                            repair.priority === 'urgent' ? '加急' : '普通';

                        var tr = '<tr>' +
                            '<td>' + repair.repairId + '</td>' +
                            '<td>' + repair.ownerName + '</td>' +
                            '<td>' + repair.houseId + '</td>' +
                            '<td>' + getRepairTypeName(repair.repairType) + '</td>' +
                            '<td>' + repair.description + '</td>' +
                            '<td><span class="label ' + priorityClass + '">' + priorityText + '</span></td>' +
                            '<td>' + formatDateTime(repair.submitTime) + '</td>' +
                            '<td>' +
                            '<button class="btn btn-sm btn-primary" onclick="acceptRepair(' + repair.repairId + ')">受理</button>' +
                            '</td>' +
                            '</tr>';
                        tbody.append(tr);
                    });
                }
            }
        });
    }

    // 受理报修
    function acceptRepair(repairId) {
        var handler = prompt('请输入处理人姓名：');
        if (!handler) return;

        var handlerPhone = prompt('请输入处理人电话：');
        if (!handlerPhone) return;

        $.ajax({
            url: '${pageContext.request.contextPath}/repair?method=accept',
            type: 'POST',
            data: {
                repairId: repairId,
                handler: handler,
                handlerPhone: handlerPhone
            },
            dataType: 'json',
            success: function(result) {
                alert(result.message);
                if (result.success) {
                    loadPendingRepairs();
                    loadDashboardData();
                }
            }
        });
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
