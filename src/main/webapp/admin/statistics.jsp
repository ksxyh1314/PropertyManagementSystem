<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>数据统计 - 物业管理系统</title>

    <!-- 引入 CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            background: #f5f7fa;
        }

        /* 侧边栏样式 */
        .sidebar {
            position: fixed;
            left: 0;
            top: 0;
            bottom: 0;
            width: 250px;
            background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
            color: white;
            overflow-y: auto;
            z-index: 1000;
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
        }

        .sidebar-header {
            padding: 30px 20px;
            background: rgba(0,0,0,0.2);
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }

        .sidebar-header h3 {
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .sidebar-header p {
            font-size: 14px;
            opacity: 0.8;
            margin: 0;
        }

        .sidebar-menu {
            list-style: none;
            padding: 20px 0;
        }

        .sidebar-menu li {
            margin-bottom: 5px;
        }

        .sidebar-menu a {
            display: flex;
            align-items: center;
            padding: 15px 25px;
            color: rgba(255,255,255,0.8);
            text-decoration: none;
            transition: all 0.3s;
        }

        .sidebar-menu a:hover {
            background: rgba(255,255,255,0.1);
            color: white;
            padding-left: 30px;
        }

        .sidebar-menu a.active {
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-left: 4px solid #fff;
        }

        .sidebar-menu i {
            width: 25px;
            margin-right: 15px;
            font-size: 16px;
        }

        .sidebar-footer {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            padding: 20px;
            background: rgba(0,0,0,0.2);
        }

        /* 主内容区 */
        .main-content {
            margin-left: 250px;
            padding: 30px;
            min-height: 100vh;
        }

        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .page-header h2 {
            margin: 0;
            font-size: 28px;
            font-weight: 600;
        }

        .page-header p {
            margin: 10px 0 0 0;
            opacity: 0.9;
        }

        /* 统计卡片 */
        .stats-card {
            background: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            transition: transform 0.3s, box-shadow 0.3s;
            position: relative;
            overflow: hidden;
        }

        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .stats-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
        }

        .stats-card.primary::before {
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        }

        .stats-card.success::before {
            background: linear-gradient(90deg, #56ab2f 0%, #a8e063 100%);
        }

        .stats-card.warning::before {
            background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
        }

        .stats-card.info::before {
            background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
        }

        .stats-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            float: left;
            margin-right: 20px;
        }

        .stats-card.primary .stats-icon {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .stats-card.success .stats-icon {
            background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
            color: white;
        }

        .stats-card.warning .stats-icon {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
        }

        .stats-card.info .stats-icon {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
        }

        .stats-info {
            overflow: hidden;
        }

        .stats-info h3 {
            font-size: 32px;
            font-weight: 700;
            margin: 0 0 5px 0;
            color: #2c3e50;
        }

        .stats-info p {
            margin: 0;
            color: #7f8c8d;
            font-size: 14px;
        }

        /* 图表卡片 */
        .chart-card {
            background: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .chart-card h4 {
            margin: 0 0 20px 0;
            font-size: 18px;
            font-weight: 600;
            color: #2c3e50;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
            display: flex;
            align-items: center;
        }

        .chart-card h4 i {
            margin-right: 10px;
            color: #667eea;
        }

        /* 加载动画 */
        .loading {
            text-align: center;
            padding: 50px;
            color: #999;
        }

        .loading i {
            font-size: 32px;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }

        /* 响应式 */
        @media (max-width: 768px) {
            .sidebar {
                transform: translateX(-250px);
            }

            .main-content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>

<!-- 侧边栏 -->
<div class="sidebar">
    <div class="sidebar-header">
        <h3><i class="fas fa-building"></i> 物业管理系统</h3>
        <p><i class="fas fa-user-shield"></i> 管理员：${sessionScope.currentUser.realName}</p>
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
            <a href="${pageContext.request.contextPath}/admin/statistics.jsp" class="active">
                <i class="fas fa-chart-bar"></i> 数据统计
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
<div class="main-content">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-chart-line"></i> 数据统计分析</h2>
        <p>查看系统运营数据，分析收费趋势和业务状况</p>
    </div>

    <!-- 统计卡片 -->
    <div class="row">
        <div class="col-md-3">
            <div class="stats-card primary">
                <div class="stats-icon">
                    <i class="fas fa-building"></i>
                </div>
                <div class="stats-info">
                    <h3 id="totalHouses">-</h3>
                    <p>房屋总数</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stats-card success">
                <div class="stats-icon">
                    <i class="fas fa-users"></i>
                </div>
                <div class="stats-info">
                    <h3 id="totalOwners">-</h3>
                    <p>业主总数</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stats-card warning">
                <div class="stats-icon">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                <div class="stats-info">
                    <h3 id="totalUnpaid">-</h3>
                    <p>未缴费账单</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stats-card info">
                <div class="stats-icon">
                    <i class="fas fa-yen-sign"></i>
                </div>
                <div class="stats-info">
                    <h3 id="totalAmount">-</h3>
                    <p>本月收入（元）</p>
                </div>
            </div>
        </div>
    </div>

    <!-- 图表区域 -->
    <div class="row">
        <div class="col-md-6">
            <div class="chart-card">
                <h4><i class="fas fa-chart-bar"></i> 月度收费统计</h4>
                <div id="monthlyChart" style="height: 400px;">
                    <div class="loading">
                        <i class="fas fa-spinner fa-spin"></i>
                        <p>加载中...</p>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="chart-card">
                <h4><i class="fas fa-chart-pie"></i> 缴费状态分布</h4>
                <div id="statusChart" style="height: 400px;">
                    <div class="loading">
                        <i class="fas fa-spinner fa-spin"></i>
                        <p>加载中...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="chart-card">
                <h4><i class="fas fa-chart-area"></i> 楼栋缴费率排行</h4>
                <div id="buildingChart" style="height: 400px;">
                    <div class="loading">
                        <i class="fas fa-spinner fa-spin"></i>
                        <p>加载中...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 引入 JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>

<script>
    console.log('=== 库加载检测 ===');
    console.log('jQuery:', typeof jQuery !== 'undefined' ? '✅ v' + jQuery.fn.jquery : '❌ 未加载');
    console.log('Bootstrap:', typeof $.fn.modal !== 'undefined' ? '✅ 已加载' : '❌ 未加载');
    console.log('ECharts:', typeof echarts !== 'undefined' ? '✅ 已加载' : '❌ 未加载');

    if (typeof echarts === 'undefined') {
        alert('图表库加载失败，请检查网络连接或刷新页面重试');
    }

    $(function() {
        console.log('页面加载完成，开始加载数据...');
        loadStatistics();
        loadMonthlyChart();
        loadStatusChart();
        loadBuildingChart();
    });

    // 加载概览统计
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
            }
        });
    }

    // 加载月度图表
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
                    $('#monthlyChart .loading').remove();

                    var chart = echarts.init(document.getElementById('monthlyChart'));
                    var option = {
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: {
                                type: 'shadow',
                                shadowStyle: {
                                    color: 'rgba(0, 0, 0, 0.1)'
                                }
                            }
                        },
                        legend: {
                            data: ['应收金额', '实收金额'],
                            top: 10,
                            textStyle: {
                                fontSize: 14
                            }
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
                            axisLabel: {
                                rotate: 45,
                                fontSize: 12
                            }
                        },
                        yAxis: {
                            type: 'value',
                            name: '金额（元）',
                            axisLabel: {
                                formatter: '{value}'
                            }
                        },
                        series: [
                            {
                                name: '应收金额',
                                type: 'bar',
                                data: result.data.totalAmounts || [],
                                itemStyle: {
                                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                        { offset: 0, color: '#667eea' },
                                        { offset: 1, color: '#764ba2' }
                                    ])
                                },
                                barWidth: '35%'
                            },
                            {
                                name: '实收金额',
                                type: 'bar',
                                data: result.data.paidAmounts || [],
                                itemStyle: {
                                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                        { offset: 0, color: '#56ab2f' },
                                        { offset: 1, color: '#a8e063' }
                                    ])
                                },
                                barWidth: '35%'
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
                $('#monthlyChart .loading').html('<p style="color: #e74c3c;">加载失败</p>');
            }
        });
    }

    // 加载状态图表
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
                    $('#statusChart .loading').remove();

                    var chart = echarts.init(document.getElementById('statusChart'));
                    var option = {
                        tooltip: {
                            trigger: 'item',
                            formatter: '{a} <br/>{b}: {c} ({d}%)'
                        },
                        legend: {
                            orient: 'vertical',
                            left: 'left',
                            top: 'middle',
                            textStyle: {
                                fontSize: 14
                            }
                        },
                        series: [{
                            name: '缴费状态',
                            type: 'pie',
                            radius: ['40%', '70%'],
                            center: ['60%', '50%'],
                            avoidLabelOverlap: false,
                            itemStyle: {
                                borderRadius: 10,
                                borderColor: '#fff',
                                borderWidth: 2
                            },
                            label: {
                                show: true,
                                formatter: '{b}\n{c} ({d}%)',
                                fontSize: 12
                            },
                            emphasis: {
                                label: {
                                    show: true,
                                    fontSize: 14,
                                    fontWeight: 'bold'
                                }
                            },
                            data: [
                                {
                                    value: result.data.paid || 0,
                                    name: '已缴费',
                                    itemStyle: {
                                        color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                                            { offset: 0, color: '#56ab2f' },
                                            { offset: 1, color: '#a8e063' }
                                        ])
                                    }
                                },
                                {
                                    value: result.data.unpaid || 0,
                                    name: '未缴费',
                                    itemStyle: {
                                        color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                                            { offset: 0, color: '#f093fb' },
                                            { offset: 1, color: '#f5576c' }
                                        ])
                                    }
                                },
                                {
                                    value: result.data.overdue || 0,
                                    name: '已逾期',
                                    itemStyle: {
                                        color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                                            { offset: 0, color: '#ee6666' },
                                            { offset: 1, color: '#ff9a9e' }
                                        ])
                                    }
                                }
                            ]
                        }]
                    };
                    chart.setOption(option);

                    window.addEventListener('resize', function() {
                        chart.resize();
                    });
                }
            },
            error: function(xhr, status, error) {
                console.error('加载状态数据失败:', error);
                $('#statusChart .loading').html('<p style="color: #e74c3c;">加载失败</p>');
            }
        });
    }

    // 加载楼栋图表
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
                    $('#buildingChart .loading').remove();

                    var chart = echarts.init(document.getElementById('buildingChart'));
                    var option = {
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: { type: 'shadow' },
                            formatter: '{b}<br/>缴费率: {c}%'
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
                            axisLabel: {
                                rotate: 45,
                                fontSize: 12
                            }
                        },
                        yAxis: {
                            type: 'value',
                            name: '缴费率（%）',
                            max: 100,
                            axisLabel: {
                                formatter: '{value}%'
                            }
                        },
                        series: [{
                            name: '缴费率',
                            type: 'bar',
                            data: result.data.rates || [],
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#4facfe' },
                                    { offset: 1, color: '#00f2fe' }
                                ]),
                                borderRadius: [5, 5, 0, 0]
                            },
                            barWidth: '50%',
                            label: {
                                show: true,
                                position: 'top',
                                formatter: '{c}%',
                                fontSize: 12
                            }
                        }]
                    };
                    chart.setOption(option);

                    window.addEventListener('resize', function() {
                        chart.resize();
                    });
                }
            },
            error: function(xhr, status, error) {
                console.error('加载楼栋数据失败:', error);
                $('#buildingChart .loading').html('<p style="color: #e74c3c;">加载失败</p>');
            }
        });
    }
</script>

</body>
</html>
