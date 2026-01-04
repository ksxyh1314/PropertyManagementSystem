<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的缴费 - 智慧社区</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/theme/default/layer.css">

    <style>
        body {
            background-color: #f5f7fa;
            font-family: 'Microsoft YaHei', sans-serif;
            padding-bottom: 50px;
        }

        .header-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px 20px 50px 20px;
            border-radius: 0 0 30px 30px;
            box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
            margin-bottom: 20px;
        }

        .search-card {
            background: #fff;
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0,0,0,0.05);
            margin-top: -40px;
            margin-bottom: 20px;
        }

        .search-input-wrapper {
            position: relative;
        }
        .search-clear-btn {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #999;
            cursor: pointer;
            padding: 5px;
            display: none;
            z-index: 10;
        }
        .search-clear-btn:hover {
            color: #666;
        }
        .search-clear-btn.show {
            display: block;
        }

        .bill-card {
            background: #fff;
            border: none;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.03);
            transition: all 0.2s;
            margin-bottom: 15px;
            overflow: hidden;
            cursor: pointer;
        }
        .bill-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }

        .bill-header {
            padding: 12px 20px;
            border-bottom: 1px solid #f8f9fa;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .bill-body {
            padding: 15px 20px;
        }

        .amount-lg {
            font-size: 22px;
            font-weight: 800;
            color: #333;
        }

        .badge-soft-danger {
            background-color: #ffe5e5;
            color: #ff6b6b;
            padding: 5px 10px;
            border-radius: 6px;
        }
        .badge-soft-success {
            background-color: #e5ffe9;
            color: #2ecc71;
            padding: 5px 10px;
            border-radius: 6px;
        }
        .badge-soft-warning {
            background-color: #fff3cd;
            color: #ff9800;
            padding: 5px 10px;
            border-radius: 6px;
        }

        .nav-pills .nav-link {
            border-radius: 10px;
            color: #6c757d;
            font-weight: 600;
        }
        .nav-pills .nav-link.active {
            background-color: #667eea;
            color: #fff;
            box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
        }

        .detail-modal .modal-dialog {
            max-width: 700px;
        }

        .detail-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            font-size: 14px;
            padding-bottom: 10px;
            border-bottom: 1px dashed #eee;
        }
        .detail-row:last-child {
            border-bottom: none;
        }

        .detail-label {
            color: #666;
            display: flex;
            align-items: center;
        }

        .detail-label i {
            width: 20px;
            margin-right: 8px;
            color: #667eea;
        }

        .detail-value {
            font-weight: 600;
            color: #333;
        }

        .total-highlight {
            font-size: 30px;
            color: #667eea;
            font-weight: bold;
            text-align: center;
            margin: 12px 0;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        .empty-state img {
            width: 120px;
            opacity: 0.6;
            margin-bottom: 20px;
        }

        .payment-method-btn {
            cursor: pointer;
            transition: all 0.3s;
            border: 2px solid #e0e0e0 !important;
            background-color: #fff !important;
            position: relative;
        }
        .payment-method-btn:hover {
            border-color: #667eea !important;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }
        .payment-method-btn.active {
            border-color: #667eea !important;
            background-color: #f0f3ff !important;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
        }
        .payment-method-btn.active::after {
            content: '\f00c';
            font-family: 'Font Awesome 5 Free';
            font-weight: 900;
            position: absolute;
            top: 5px;
            right: 5px;
            color: #667eea;
            font-size: 14px;
            background: white;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .payment-method-btn i {
            transition: all 0.3s;
        }
        .payment-method-btn.active i {
            color: #667eea;
            transform: scale(1.1);
        }

        .item-code-badge {
            background-color: #e8eaf6;
            color: #5c6bc0;
            font-family: 'Courier New', monospace;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 11px;
            margin-left: 5px;
            font-weight: 600;
        }

        .overdue-alert {
            background-color: #fff3cd;
            border-left: 4px solid #ff9800;
            padding: 12px 15px;
            margin: 15px 0;
            border-radius: 4px;
        }

        .overdue-alert i {
            color: #ff9800;
            margin-right: 8px;
        }

        .detail-section {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 15px;
        }
        .detail-section-title {
            font-size: 14px;
            font-weight: 600;
            color: #495057;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .detail-content {
            background: white;
            padding: 12px;
            border-radius: 6px;
            line-height: 1.8;
            color: #333;
        }

        .overdue-badge {
            background-color: #ffe5e5;
            color: #ff6b6b;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 11px;
            margin-left: 5px;
        }

        .late-fee-info {
            background-color: #fff9e6;
            border-left: 3px solid #ffc107;
            padding: 10px 12px;
            margin: 10px 0;
            border-radius: 4px;
            font-size: 13px;
            color: #856404;
        }
        .late-fee-info i {
            color: #ffc107;
            margin-right: 6px;
        }
    </style>
</head>
<body>

<!-- 顶部概览 -->
<div class="header-card text-center">
    <h4 class="mb-4"><i class="fas fa-wallet mr-2"></i>缴费中心</h4>
    <div class="row">
        <div class="col-6 border-right border-light">
            <small class="opacity-75">待缴费总额</small>
            <div class="h2 font-weight-bold mt-1" id="totalUnpaid">¥0.00</div>
        </div>
        <div class="col-6">
            <small class="opacity-75">逾期金额</small>
            <div class="h2 font-weight-bold mt-1" id="totalOverdue">¥0.00</div>
        </div>
    </div>
</div>

<div class="container">
    <!-- 搜索筛选栏 -->
    <div class="card search-card">
        <div class="card-body py-3 px-3">
            <div class="form-row align-items-center">
                <div class="col-md-6 col-12 mb-2 mb-md-0">
                    <div class="input-group search-input-wrapper">
                        <div class="input-group-prepend">
                            <span class="input-group-text bg-light border-0">
                                <i class="fas fa-search text-muted"></i>
                            </span>
                        </div>
                        <input type="text" id="searchKeyword" class="form-control bg-light border-0"
                               placeholder="搜索项目名称或账期">
                        <button type="button" class="search-clear-btn" id="clearSearchBtn">
                            <i class="fas fa-times-circle"></i>
                        </button>
                    </div>
                </div>
                <div class="col-md-3 col-6 mb-2 mb-md-0">
                    <select id="searchItemId" class="form-control bg-light border-0">
                        <option value="">全部项目</option>
                        <option value="01">物业管理费</option>
                        <option value="02">停车费</option>
                        <option value="06">取暖费</option>
                        <option value="07">垃圾清运费</option>
                        <option value="08">电梯维护费</option>
                    </select>
                </div>
                <div class="col-md-3 col-12 mb-2 mb-md-0 text-right">
                    <button class="btn btn-primary btn-sm px-3" onclick="doSearch()">
                        <i class="fas fa-filter"></i> 筛选
                    </button>
                    <button class="btn btn-light btn-sm px-3 ml-1" onclick="resetSearch()">
                        <i class="fas fa-redo"></i> 重置
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- 选项卡 -->
    <div class="card shadow-sm border-0 mb-3" style="border-radius: 15px;">
        <div class="card-body p-2">
            <ul class="nav nav-pills nav-fill" id="pills-tab">
                <li class="nav-item">
                    <a class="nav-link active" id="tab-unpaid" data-toggle="pill" href="#pills-unpaid">
                        <i class="fas fa-exclamation-circle"></i> 待缴费
                        <span class="badge badge-danger ml-1" id="unpaidCount">0</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="tab-history" data-toggle="pill" href="#pills-history">
                        <i class="fas fa-history"></i> 缴费记录
                        <span class="badge badge-secondary ml-1" id="historyCount">0</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>

    <div class="tab-content" id="pills-tabContent">
        <!-- 待缴费列表 -->
        <div class="tab-pane fade show active" id="pills-unpaid">
            <div id="unpaidList">
                <div class="text-center py-5 text-muted">
                    <i class="fas fa-spinner fa-spin fa-2x"></i>
                    <p class="mt-3">加载中...</p>
                </div>
            </div>
        </div>

        <!-- 历史记录列表 -->
        <div class="tab-pane fade" id="pills-history">
            <div id="historyList">
                <div class="text-center py-5 text-muted">
                    <i class="fas fa-spinner fa-spin fa-2x"></i>
                    <p class="mt-3">加载中...</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 详情模态框 -->
<div class="modal fade detail-modal" id="detailModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header bg-light">
                <h5 class="modal-title"><i class="fas fa-file-invoice-dollar mr-2"></i>账单详情</h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body p-4" id="detailContent">
                <!-- 动态内容 -->
            </div>
            <div class="modal-footer" id="detailActions">
                <!-- 动态按钮 -->
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/layer.js"></script>

<script>
    // 🔥 关键修改：获取业主编号和用户ID
    var ownerId = '${sessionScope.username}';  // 业主编号（如：00010003）
    var userId = '${sessionScope.userId}';      // 🔥 用户ID（如：31）

    // 🔥 如果 Session 中没有 userId，尝试使用 ownerId 作为 fallback
    if(!userId || userId === '' || userId === 'null') {
        console.warn('⚠️ Session 中没有 userId，将尝试使用其他方式');
        // 可以通过 AJAX 查询用户ID，这里暂时使用 null
        userId = null;
    }

    // 宽限期配置
    var GRACE_PERIOD_CONFIG = {
        '01': 30,
        '02': 15,
        '06': 30,
        '07': 30,
        '08': 30
    };

    function getGracePeriodByItemId(itemId) {
        return GRACE_PERIOD_CONFIG[itemId] || 30;
    }

    // 格式化滞纳金比例显示
    function formatLateFeeRate(rate) {
        if(!rate || rate === 0) return '0%';
        return (rate * 100).toFixed(2) + '%';
    }

    $(function() {
        console.log('========================================');
        console.log('🔥 页面加载');
        console.log('  业主编号 (ownerId):', ownerId);
        console.log('  用户ID (userId):', userId);
        console.log('========================================');

        if(!ownerId || ownerId === '' || ownerId === 'null') {
            layer.msg('请先登录', {icon: 2, time: 2000}, function() {
                window.location.href = '${pageContext.request.contextPath}/login.jsp';
            });
            return;
        }

        loadSummary();
        doSearch();

        $('#searchKeyword').on('input', function() {
            if($(this).val().trim().length > 0) {
                $('#clearSearchBtn').addClass('show');
            } else {
                $('#clearSearchBtn').removeClass('show');
            }
        });

        $('#clearSearchBtn').click(function() {
            $('#searchKeyword').val('');
            $(this).removeClass('show');
            $('#searchKeyword').focus();
            doSearch();
        });
    });

    function doSearch() {
        var keyword = $('#searchKeyword').val().trim();
        var itemId = $('#searchItemId').val();
        loadUnpaid(keyword, itemId);
        loadHistory(keyword, itemId);
    }

    function resetSearch() {
        $('#searchKeyword').val('');
        $('#searchItemId').val('');
        $('#clearSearchBtn').removeClass('show');
        doSearch();
    }

    function loadSummary() {
        $.get('${pageContext.request.contextPath}/owner/payment', {
            action: 'summary',
            ownerId: ownerId
        }, function(res) {
            console.log('📊 汇总数据响应:', res);
            if(res.code === 200 && res.data) {
                var unpaidAmount = res.data.unpaid_amount || res.data.unpaidAmount || 0;
                var overdueAmount = res.data.overdue_amount || res.data.overdueAmount || 0;
                $('#totalUnpaid').text('¥' + parseFloat(unpaidAmount).toFixed(2));
                $('#totalOverdue').text('¥' + parseFloat(overdueAmount).toFixed(2));
            } else {
                $('#totalUnpaid').text('¥0.00');
                $('#totalOverdue').text('¥0.00');
            }
        }, 'json').fail(function(xhr) {
            console.error('❌ 加载汇总失败:', xhr);
            $('#totalUnpaid').text('¥0.00');
            $('#totalOverdue').text('¥0.00');
        });
    }

    function loadUnpaid(keyword, itemId) {
        $('#unpaidList').html('<div class="text-center py-5 text-muted"><i class="fas fa-spinner fa-spin fa-2x"></i><p class="mt-3">加载中...</p></div>');

        $.get('${pageContext.request.contextPath}/owner/payment', {
            action: 'list',
            ownerId: ownerId,
            paymentStatus: 'unpaid,overdue',
            keyword: keyword,
            itemId: itemId
        }, function(res) {
            var html = '';
            if(res.code === 200 && res.data && res.data.list && res.data.list.length > 0) {
                var list = res.data.list;
                $('#unpaidCount').text(list.length);

                $.each(list, function(i, item) {
                    var recordId = item.record_id || item.recordId || item.id || item.ID;
                    if(!recordId) return true;

                    var itemName = item.item_name || item.itemName || '未知项目';
                    var itemCode = item.item_id || item.itemId;
                    var billingPeriod = item.billing_period || item.billingPeriod || '-';
                    var amount = parseFloat(item.amount) || 0;
                    var lateFee = parseFloat(item.late_fee || item.lateFee) || 0;
                    var dueDate = item.due_date || item.dueDate;
                    var overdueDays = parseInt(item.overdue_days || item.overdueDays) || 0;

                    var isOverdue = overdueDays > 0;
                    var statusBadge = isOverdue
                        ? '<span class="badge-soft-danger"><i class="fas fa-exclamation-circle"></i> 已逾期 ' + overdueDays + '天</span>'
                        : '<span class="badge-soft-warning"><i class="fas fa-clock"></i> 待缴费</span>';

                    var lateFeeHtml = '';
                    if(lateFee > 0) {
                        lateFeeHtml = '<div class="text-danger small mt-1"><i class="fas fa-exclamation-triangle"></i> 滞纳金: ¥' + lateFee.toFixed(2) + '</div>';
                    }

                    var itemCodeBadge = itemCode ? '<span class="item-code-badge">' + itemCode + '</span>' : '';

                    html += '<div class="bill-card" data-record-id="' + recordId + '">' +
                        '<div class="bill-header">' +
                        '<span class="font-weight-bold text-dark">' +
                        '<i class="fas fa-file-invoice-dollar text-primary mr-2"></i>' + itemName +
                        itemCodeBadge +
                        '</span>' + statusBadge +
                        '</div>' +
                        '<div class="bill-body">' +
                        '<div class="d-flex justify-content-between align-items-center mb-2">' +
                        '<div><div class="text-muted small">账单周期</div>' +
                        '<div class="font-weight-bold text-dark">' + billingPeriod + '</div></div>' +
                        '<div class="text-right"><div class="text-muted small">应缴金额</div>' +
                        '<div class="amount-lg text-primary">¥' + amount.toFixed(2) + '</div>' +
                        lateFeeHtml + '</div></div>' +
                        '<div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">' +
                        '<small class="text-muted"><i class="far fa-calendar-times mr-1"></i>截止: ' + formatDate(dueDate) + '</small>' +
                        '<small class="text-primary"><i class="fas fa-hand-pointer mr-1"></i>点击查看详情</small>' +
                        '</div></div></div>';
                });
            } else {
                $('#unpaidCount').text(0);
                html = '<div class="empty-state">' +
                    '<img src="https://img.icons8.com/clouds/100/000000/checked.png">' +
                    '<p class="text-muted">没有找到相关待缴账单</p>' +
                    '<small class="text-muted">所有账单已缴清或暂无账单</small></div>';
            }

            $('#unpaidList').html(html);
            $('#unpaidList').off('click', '.bill-card').on('click', '.bill-card', function() {
                var recordId = $(this).data('record-id');
                if(recordId) showDetail(recordId);
            });
        }, 'json').fail(function(xhr) {
            console.error('❌ 加载待缴费失败:', xhr);
            $('#unpaidList').html('<div class="text-center py-5 text-danger"><i class="fas fa-exclamation-triangle fa-2x mb-3"></i><p>加载失败,请稍后重试</p></div>');
        });
    }

    function loadHistory(keyword, itemId) {
        $('#historyList').html('<div class="text-center py-5 text-muted"><i class="fas fa-spinner fa-spin fa-2x"></i><p class="mt-3">加载中...</p></div>');

        $.get('${pageContext.request.contextPath}/owner/payment', {
            action: 'history',
            ownerId: ownerId,
            keyword: keyword,
            itemId: itemId
        }, function(res) {
            var html = '';
            if(res.code === 200 && res.data && res.data.list && res.data.list.length > 0) {
                var list = res.data.list;
                $('#historyCount').text(list.length);

                $.each(list, function(i, item) {
                    var recordId = item.record_id || item.recordId || item.id || item.ID;
                    if(!recordId) return true;

                    var itemName = item.item_name || item.itemName || '未知项目';
                    var itemCode = item.item_id || item.itemId;
                    var billingPeriod = item.billing_period || item.billingPeriod || '-';
                    var amount = parseFloat(item.amount) || 0;
                    var lateFee = parseFloat(item.late_fee || item.lateFee) || 0;
                    var totalAmount = parseFloat(item.total_amount || item.totalAmount) || (amount + lateFee);
                    var paymentDate = item.payment_date || item.paymentDate;
                    var receiptNo = item.receipt_no || item.receiptNo;
                    var overdueDays = parseInt(item.overdue_days || item.overdueDays) || 0;

                    var itemCodeBadge = itemCode ? '<span class="badge badge-light ml-1">' + itemCode + '</span>' : '';
                    var overdueBadge = '';
                    if(overdueDays > 0) {
                        overdueBadge = '<span class="overdue-badge ml-1">逾期' + overdueDays + '天后缴费</span>';
                    }

                    html += '<div class="bill-card" data-record-id="' + recordId + '">' +
                        '<div class="bill-body py-3">' +
                        '<div class="d-flex justify-content-between align-items-center">' +
                        '<div><div class="font-weight-bold text-dark">' + itemName + itemCodeBadge + overdueBadge +
                        '<span class="text-muted small ml-1">(' + billingPeriod + ')</span></div>' +
                        '<small class="text-muted"><i class="far fa-check-circle text-success mr-1"></i>' +
                        formatTime(paymentDate) + ' 支付</small>' +
                        '<div class="mt-1"><span class="badge badge-light">' +
                        '<i class="fas fa-receipt mr-1"></i>' + (receiptNo || '-') + '</span></div></div>' +
                        '<div class="text-right"><div class="font-weight-bold text-dark">¥' + totalAmount.toFixed(2) + '</div>' +
                        '<span class="badge-soft-success small">已完成</span></div></div></div></div>';
                });
            } else {
                $('#historyCount').text(0);
                html = '<div class="empty-state">' +
                    '<img src="https://img.icons8.com/clouds/100/000000/time-machine.png">' +
                    '<p class="text-muted">没有找到相关记录</p>' +
                    '<small class="text-muted">暂无历史缴费记录</small></div>';
            }

            $('#historyList').html(html);
            $('#historyList').off('click', '.bill-card').on('click', '.bill-card', function() {
                var recordId = $(this).data('record-id');
                if(recordId) showDetail(recordId);
            });
        }, 'json').fail(function(xhr) {
            console.error('❌ 加载历史记录失败:', xhr);
            $('#historyList').html('<div class="text-center py-5 text-danger"><i class="fas fa-exclamation-triangle fa-2x mb-3"></i><p>加载失败,请稍后重试</p></div>');
        });
    }

    function showDetail(recordId) {
        if(!recordId) {
            layer.msg('账单ID无效', {icon: 2});
            return;
        }

        console.log('🔍 查看详情，recordId:', recordId);

        $('#detailModal').modal('show');
        $('#detailContent').html('<div class="text-center py-5"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>');
        $('#detailActions').html('');

        $.get('${pageContext.request.contextPath}/owner/payment', {
            action: 'detail',
            recordId: recordId,
            ownerId: ownerId
        }, function(res) {
            console.log('📥 详情响应:', res);
            if(res.code === 200 && res.data) {
                var record = res.data.record || res.data;
                renderDetail(record);
            } else {
                $('#detailContent').html('<div class="alert alert-danger">' + (res.msg || '加载失败') + '</div>');
            }
        }, 'json').fail(function(xhr) {
            console.error('❌ 加载详情失败:', xhr);
            $('#detailContent').html('<div class="alert alert-danger">加载失败,请稍后重试</div>');
        });
    }

    // 🔥 渲染详情内容
    function renderDetail(record) {
        var recordId = record.record_id || record.recordId;
        var amount = parseFloat(record.amount) || 0;
        var lateFee = parseFloat(record.late_fee || record.lateFee) || 0;
        var totalAmount = parseFloat(record.total_amount || record.totalAmount) || (amount + lateFee);
        var overdueDays = parseInt(record.overdue_days || record.overdueDays) || 0;

        var itemName = record.item_name || record.itemName || '物业费';
        var itemCode = record.item_id || record.itemId || '-';
        var billingPeriod = record.billing_period || record.billingPeriod || '-';
        var dueDate = record.due_date || record.dueDate;
        var paymentStatus = record.payment_status || record.paymentStatus;
        var paymentDate = record.payment_date || record.paymentDate;
        var paymentMethod = record.payment_method || record.paymentMethod;
        var receiptNo = record.receipt_no || record.receiptNo;

        // 从后端数据获取滞纳金比例和宽限期
        var lateFeeRate = parseFloat(record.late_fee_rate || record.lateFeeRate) || 0.0005;
        var gracePeriod = parseInt(record.grace_period || record.gracePeriod) || getGracePeriodByItemId(itemCode);

        console.log('📊 账单详情数据:');
        console.log('  recordId:', recordId);
        console.log('  paymentStatus:', paymentStatus);
        console.log('  滞纳金比例:', lateFeeRate);
        console.log('  宽限期:', gracePeriod);

        // 🔥 关键：将 recordId 存储到模态框的 data 属性中
        $('#detailModal').data('recordId', recordId);
        console.log('✅ recordId 已存储到 modal.data:', $('#detailModal').data('recordId'));

        var html = '';

        // 金额显示
        html += '<div class="text-center mb-4">';
        html += '<p class="text-muted mb-0">应缴总额</p>';
        html += '<div class="total-highlight">¥' + totalAmount.toFixed(2) + '</div>';
        if(lateFee > 0) {
            html += '<div class="badge badge-soft-danger px-3 py-2">';
            html += '<i class="fas fa-exclamation-circle"></i> ';
            html += '含滞纳金: ¥' + lateFee.toFixed(2) + ' (逾期 ' + overdueDays + ' 天)';
            html += '</div>';
        }
        html += '</div>';

        // 基本信息
        html += '<div class="detail-section">';
        html += '<div class="detail-section-title"><i class="fas fa-info-circle text-primary"></i>基本信息</div>';
        html += '<div class="detail-content">';
        html += '<div class="detail-row">';
        html += '<span class="detail-label"><i class="fas fa-tag"></i>收费项目</span>';
        html += '<span class="detail-value">' + itemName + '</span>';
        html += '</div>';
        html += '<div class="detail-row">';
        html += '<span class="detail-label"><i class="fas fa-barcode"></i>项目编号</span>';
        html += '<span class="detail-value">' + itemCode + '</span>';
        html += '</div>';
        html += '<div class="detail-row">';
        html += '<span class="detail-label"><i class="fas fa-calendar-alt"></i>账单周期</span>';
        html += '<span class="detail-value">' + billingPeriod + '</span>';
        html += '</div>';
        html += '</div>';
        html += '</div>';

        // 时间信息
        html += '<div class="detail-section">';
        html += '<div class="detail-section-title"><i class="fas fa-clock text-info"></i>时间信息</div>';
        html += '<div class="detail-content">';
        html += '<div class="detail-row">';
        html += '<span class="detail-label"><i class="fas fa-calendar-times"></i>截止日期</span>';
        html += '<span class="detail-value">' + formatDate(dueDate) + '</span>';
        html += '</div>';

        var graceDate = addDays(dueDate, gracePeriod);
        html += '<div class="detail-row">';
        html += '<span class="detail-label"><i class="fas fa-calendar-check"></i>宽限日期</span>';
        html += '<span class="detail-value">' + formatDate(graceDate) + '</span>';
        html += '</div>';
        html += '<div class="detail-row">';
        html += '<span class="detail-label"><i class="fas fa-hourglass-half"></i>宽限期</span>';
        html += '<span class="detail-value">' + gracePeriod + ' 天</span>';
        html += '</div>';
        html += '</div>';
        html += '</div>';

        // 费用明细
        html += '<div class="detail-section">';
        html += '<div class="detail-section-title"><i class="fas fa-money-bill-wave text-success"></i>费用明细</div>';
        html += '<div class="detail-content">';
        html += '<div class="detail-row">';
        html += '<span class="detail-label"><i class="fas fa-dollar-sign"></i>应缴金额</span>';
        html += '<span class="detail-value text-primary">¥' + amount.toFixed(2) + '</span>';
        html += '</div>';
        if(lateFee > 0) {
            html += '<div class="detail-row">';
            html += '<span class="detail-label"><i class="fas fa-exclamation-triangle"></i>滞纳金</span>';
            html += '<span class="detail-value text-danger">¥' + lateFee.toFixed(2) + '</span>';
            html += '</div>';
        }
        html += '</div>';
        html += '</div>';

        // 滞纳金计算说明
        if(paymentStatus !== 'paid') {
            html += '<div class="late-fee-info">';
            html += '<i class="fas fa-info-circle"></i>';
            html += '<strong>滞纳金说明：</strong>';
            html += '逾期后按应缴金额的 ' + formatLateFeeRate(lateFeeRate) + ' /天计算滞纳金';
            html += '</div>';
        }

        // 逾期警告
        if(paymentStatus !== 'paid' && overdueDays > 0) {
            html += '<div class="overdue-alert">';
            html += '<i class="fas fa-exclamation-triangle"></i>';
            html += '<strong>逾期提醒:</strong> 该账单已逾期 ' + overdueDays + ' 天,请尽快缴费!';
            html += '</div>';
        }

        // 支付方式选择(仅待缴费显示)
        if(paymentStatus !== 'paid') {
            html += '<div class="detail-section">';
            html += '<div class="detail-section-title"><i class="fas fa-wallet text-warning"></i>选择支付方式</div>';
            html += '<div class="row text-center mt-3">';
            html += '<div class="col-4">';
            html += '<label class="btn btn-block py-3 payment-method-btn active shadow-sm" style="border-radius: 10px;">';
            html += '<input type="radio" name="payMethod" value="wechat" checked hidden>';
            html += '<i class="fab fa-weixin fa-2x mb-2 text-success"></i><br>';
            html += '<small class="font-weight-bold">微信支付</small>';
            html += '</label>';
            html += '</div>';
            html += '<div class="col-4">';
            html += '<label class="btn btn-block py-3 payment-method-btn shadow-sm" style="border-radius: 10px;">';
            html += '<input type="radio" name="payMethod" value="alipay" hidden>';
            html += '<i class="fab fa-alipay fa-2x mb-2 text-primary"></i><br>';
            html += '<small class="font-weight-bold">支付宝</small>';
            html += '</label>';
            html += '</div>';
            html += '<div class="col-4">';
            html += '<label class="btn btn-block py-3 payment-method-btn shadow-sm" style="border-radius: 10px;">';
            html += '<input type="radio" name="payMethod" value="online" hidden>';
            html += '<i class="fas fa-credit-card fa-2x mb-2 text-info"></i><br>';
            html += '<small class="font-weight-bold">银联支付</small>';
            html += '</label>';
            html += '</div>';
            html += '</div>';
            html += '</div>';
        }

        // 缴费记录(仅已缴费显示)
        if(paymentStatus === 'paid') {
            html += '<div class="detail-section">';
            html += '<div class="detail-section-title"><i class="fas fa-receipt text-success"></i>缴费记录</div>';
            html += '<div class="detail-content">';
            html += '<div class="detail-row">';
            html += '<span class="detail-label"><i class="fas fa-calendar-check"></i>缴费时间</span>';
            html += '<span class="detail-value">' + formatTime(paymentDate) + '</span>';
            html += '</div>';
            html += '<div class="detail-row">';
            html += '<span class="detail-label"><i class="fas fa-credit-card"></i>支付方式</span>';
            html += '<span class="detail-value">' + getPaymentMethodName(paymentMethod) + '</span>';
            html += '</div>';
            html += '<div class="detail-row">';
            html += '<span class="detail-label"><i class="fas fa-file-invoice"></i>收据号</span>';
            html += '<span class="detail-value">' + (receiptNo || '-') + '</span>';
            html += '</div>';
            html += '</div>';
            html += '</div>';
        }

        $('#detailContent').html(html);

        // 重新绑定支付方式点击事件
        $('.payment-method-btn').off('click').on('click', function() {
            $('.payment-method-btn').removeClass('active');
            $(this).addClass('active');
            $(this).find('input[type="radio"]').prop('checked', true);
        });

        // 动态按钮
        var actions = '<button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>';
        if(paymentStatus !== 'paid') {
            actions += '<button type="button" class="btn btn-primary btn-lg font-weight-bold" onclick="submitPaymentFromDetail()" style="border-radius: 10px;">';
            actions += '<i class="fas fa-check-circle mr-2"></i>立即支付';
            actions += '</button>';
        }
        $('#detailActions').html(actions);
    }

    // 🔥 关键修改：提交支付（使用业主自己的 userId）
    function submitPaymentFromDetail() {
        // 从模态框的 data 属性获取 recordId
        var recordId = $('#detailModal').data('recordId');
        var method = $('input[name="payMethod"]:checked').val();

        console.log('========================================');
        console.log('💰 准备提交支付:');
        console.log('  recordId:', recordId);
        console.log('  method:', method);
        console.log('  ownerId:', ownerId);
        console.log('  userId:', userId);
        console.log('========================================');

        if(!recordId) {
            layer.msg('账单ID无效，请重新打开详情', {icon: 2});
            return;
        }

        if(!method) {
            layer.msg('请选择支付方式', {icon: 2});
            return;
        }

        // 🔥 关键：使用业主的 userId 作为 operatorId
        var operatorId = userId;

        // 🔥 如果 userId 为空，提示用户重新登录
        if(!operatorId || operatorId === '' || operatorId === 'null') {
            console.error('❌ 用户ID为空，无法提交支付');
            layer.msg('用户信息异常，请重新登录', {icon: 2, time: 2000}, function() {
                window.location.href = '${pageContext.request.contextPath}/login.jsp';
            });
            return;
        }

        var loadIdx = layer.load(1, {shade: [0.5,'#000']});

        $.post('${pageContext.request.contextPath}/owner/payment', {
            action: 'pay',
            recordId: recordId,
            ownerId: ownerId,
            paymentMethod: method,
            operatorId: operatorId  // 🔥 使用业主的 userId
        }, function(res) {
            layer.close(loadIdx);

            console.log('========================================');
            console.log('📥 支付响应:', res);
            console.log('========================================');

            if(res.code === 200) {
                $('#detailModal').modal('hide');
                layer.msg('支付成功!', {
                    icon: 1,
                    time: 2000,
                    end: function() {
                        loadSummary();
                        doSearch();
                    }
                });
            } else {
                layer.msg(res.msg || '支付失败', {icon: 2});
            }
        }, 'json').fail(function(xhr) {
            layer.close(loadIdx);
            console.error('========================================');
            console.error('❌ 支付请求失败:', xhr);
            console.error('  状态码:', xhr.status);
            console.error('  响应:', xhr.responseText);
            console.error('========================================');
            layer.msg('支付失败,请稍后重试', {icon: 2});
        });
    }

    function getPaymentMethodName(method) {
        var map = {
            'wechat': '微信支付',
            'alipay': '支付宝',
            'cash': '现金',
            'bank_transfer': '银行转账',
            'online': '银联支付'
        };
        return map[method] || method || '-';
    }

    function addDays(dateStr, days) {
        if(!dateStr) return '-';
        var date = new Date(dateStr);
        date.setDate(date.getDate() + days);
        var year = date.getFullYear();
        var month = ('0' + (date.getMonth() + 1)).slice(-2);
        var day = ('0' + date.getDate()).slice(-2);
        return year + '-' + month + '-' + day;
    }

    function formatDate(str) {
        if(!str) return '-';
        return str.substring(0, 10);
    }

    function formatTime(str) {
        if(!str) return '-';
        return str.substring(0, 16).replace('T', ' ');
    }
</script>
</body>
</html>
