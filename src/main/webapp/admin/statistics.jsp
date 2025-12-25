<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>数据统计 - 社区物业管理系统</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/admin.css">
</head>
<body>
<div class="wrapper">
    <jsp:include page="sidebar.jsp"/>

    <div id="content">
        <div class="container-fluid">
            <h2 class="page-title">数据统计</h2>

            <!-- 统计卡片 -->
            <div class="row">
                <div class="col-md-3">
                    <div class="panel panel-primary">
                        <div class="panel-body">
                            <h3 id="totalHouses">-</h3>
                            <p>房屋总数</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="panel panel-success">
                        <div class="panel-body">
                            <h3 id="totalOwners">-</h3>
                            <p>业主总数</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="panel panel-warning">
                        <div class="panel-body">
                            <h3 id="totalUnpaid">-</h3>
                            <p>未缴费账单</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="panel panel-info">
                        <div class="panel-body">
                            <h3 id="totalAmount">-</h3>
                            <p>本月收入（元）</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 图表 -->
            <div class="row">
                <div class="col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4>月度收费统计</h4>
                        </div>
                        <div class="panel-body">
                            <div id="monthlyChart" style="height: 400px;"></div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4>缴费状态分布</h4>
                        </div>
                        <div class="panel-body">
                            <div id="statusChart" style="height: 400px;"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4>楼栋缴费率排行</h4>
                        </div>
                        <div class="panel-body">
                            <div id="buildingChart" style="height: 400px;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- ✅ 1. 先加载 jQuery -->
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

<!-- ✅ 2. 再加载 Bootstrap -->
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/3.4.1/js/bootstrap.min.js"></script>

<!-- ✅ 3. 加载 ECharts -->
<script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>

<!-- ✅ 4. 检测库加载（放在库加载之后） -->
<script>
    console.log('=== 库加载检测 ===');
    console.log('jQuery:', typeof jQuery !== 'undefined' ? '✅ v' + jQuery.fn.jquery : '❌ 未加载');
    console.log('Bootstrap:', typeof $.fn.modal !== 'undefined' ? '✅ 已加载' : '❌ 未加载');
    console.log('ECharts:', typeof echarts !== 'undefined' ? '✅ 已加载' : '❌ 未加载');

    if (typeof echarts === 'undefined') {
        alert('图表库加载失败，请检查网络连接或刷新页面重试');
    }
</script>

<!-- ✅ 5. 业务逻辑代码 -->
<script>
    $(function() {
        console.log('页面加载完成，开始加载数据...');
        loadStatistics();
        loadMonthlyChart();
        loadStatusChart();
        loadBuildingChart();
    });

    function loadStatistics() {
        console.log('正在加载概览统计数据...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/statistics',
            type: 'GET',
            data: { method: 'overview' },
            dataType: 'json',
            success: function(result) {
                console.log('概览数据返回:', result);
                if (result.success && result.data) {
                    $('#totalHouses').text(result.data.totalHouses || 0);
                    $('#totalOwners').text(result.data.totalOwners || 0);
                    $('#totalUnpaid').text(result.data.unpaidCount || 0);
                    var totalAmount = result.data.currentMonthIncome || 0;
                    $('#totalAmount').text(typeof totalAmount === 'number' ? totalAmount.toFixed(2) : '0.00');
                } else {
                    console.error('概览数据格式错误:', result);
                }
            },
            error: function(xhr, status, error) {
                console.error('加载概览数据失败:', status, error);
                console.error('响应内容:', xhr.responseText);
            }
        });
    }

    function loadMonthlyChart() {
        console.log('正在加载月度图表...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/statistics',
            type: 'GET',
            data: { method: 'monthly' },
            dataType: 'json',
            success: function(result) {
                console.log('月度数据返回:', result);
                if (result.success && result.data) {
                    var chart = echarts.init(document.getElementById('monthlyChart'));
                    var option = {
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: { type: 'shadow' }
                        },
                        legend: {
                            data: ['应收金额', '实收金额'],
                            top: 10
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true
                        },
                        xAxis: {
                            type: 'category',
                            data: result.data.months || [],
                            axisLabel: { rotate: 45 }
                        },
                        yAxis: {
                            type: 'value',
                            name: '金额（元）'
                        },
                        series: [
                            {
                                name: '应收金额',
                                type: 'bar',
                                data: result.data.totalAmounts || [],
                                itemStyle: { color: '#5470c6' }
                            },
                            {
                                name: '实收金额',
                                type: 'bar',
                                data: result.data.paidAmounts || [],
                                itemStyle: { color: '#91cc75' }
                            }
                        ]
                    };
                    chart.setOption(option);

                    window.addEventListener('resize', function() {
                        chart.resize();
                    });
                }
            },
            error: function(xhr, status, error) {
                console.error('加载月度数据失败:', error);
            }
        });
    }

    function loadStatusChart() {
        console.log('正在加载状态图表...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/statistics',
            type: 'GET',
            data: { method: 'status' },
            dataType: 'json',
            success: function(result) {
                console.log('状态数据返回:', result);
                if (result.success && result.data) {
                    var chart = echarts.init(document.getElementById('statusChart'));
                    var option = {
                        tooltip: {
                            trigger: 'item',
                            formatter: '{a} <br/>{b}: {c} ({d}%)'
                        },
                        legend: {
                            orient: 'vertical',
                            left: 'left',
                            top: 'middle'
                        },
                        series: [{
                            name: '缴费状态',
                            type: 'pie',
                            radius: ['40%', '70%'],
                            data: [
                                {
                                    value: result.data.paid || 0,
                                    name: '已缴费',
                                    itemStyle: { color: '#91cc75' }
                                },
                                {
                                    value: result.data.unpaid || 0,
                                    name: '未缴费',
                                    itemStyle: { color: '#fac858' }
                                },
                                {
                                    value: result.data.overdue || 0,
                                    name: '已逾期',
                                    itemStyle: { color: '#ee6666' }
                                }
                            ]
                        }]
                    };
                    chart.setOption(option);

                    window.addEventListener('resize', function() {
                        chart.resize();
                    });
                }
            }
        });
    }

    function loadBuildingChart() {
        console.log('正在加载楼栋图表...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/statistics',
            type: 'GET',
            data: { method: 'building' },
            dataType: 'json',
            success: function(result) {
                console.log('楼栋数据返回:', result);
                if (result.success && result.data) {
                    var chart = echarts.init(document.getElementById('buildingChart'));
                    var option = {
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: { type: 'shadow' }
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true
                        },
                        xAxis: {
                            type: 'category',
                            data: result.data.buildings || [],
                            axisLabel: { rotate: 45 }
                        },
                        yAxis: {
                            type: 'value',
                            name: '缴费率（%）',
                            max: 100
                        },
                        series: [{
                            name: '缴费率',
                            type: 'bar',
                            data: result.data.rates || [],
                            itemStyle: { color: '#5470c6' }
                        }]
                    };
                    chart.setOption(option);

                    window.addEventListener('resize', function() {
                        chart.resize();
                    });
                }
            }
        });
    }
</script>
</body>
</html>
