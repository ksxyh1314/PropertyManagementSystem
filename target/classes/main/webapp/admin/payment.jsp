<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>缴费管理 - 社区物业管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/admin.css">
</head>
<body>
<div class="wrapper">
    <!-- 侧边栏 -->
    <jsp:include page="sidebar.jsp"/>

    <!-- 主内容区 -->
    <div id="content">
        <div class="container-fluid">
            <h2 class="page-title">缴费管理</h2>

            <!-- 搜索和操作栏 -->
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="row">
                        <div class="col-md-4">
                            <div class="input-group">
                                <input type="text" class="form-control" id="searchKeyword"
                                       placeholder="输入业主ID、姓名或房屋编号搜索">
                                <span class="input-group-btn">
                                        <button class="btn btn-primary" onclick="searchPayment()">
                                            <i class="glyphicon glyphicon-search"></i> 搜索
                                        </button>
                                    </span>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <select class="form-control" id="statusFilter" onchange="searchPayment()">
                                <option value="">全部状态</option>
                                <option value="unpaid">未缴费</option>
                                <option value="paid">已缴费</option>
                                <option value="overdue">已逾期</option>
                            </select>
                        </div>
                        <div class="col-md-5 text-right">
                            <button class="btn btn-success" onclick="showGenerateBillModal()">
                                <i class="glyphicon glyphicon-plus"></i> 生成账单
                            </button>
                            <button class="btn btn-info" onclick="showAddModal()">
                                <i class="glyphicon glyphicon-plus"></i> 添加记录
                            </button>
                            <button class="btn btn-warning" onclick="loadPaymentList()">
                                <i class="glyphicon glyphicon-refresh"></i> 刷新
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 缴费记录列表 -->
            <div class="panel panel-default">
                <div class="panel-body">
                    <table class="table table-striped table-hover" id="paymentTable">
                        <thead>
                        <tr>
                            <th>记录ID</th>
                            <th>业主姓名</th>
                            <th>房屋编号</th>
                            <th>收费项目</th>
                            <th>缴费期限</th>
                            <th>应缴金额</th>
                            <th>滞纳金</th>
                            <th>合计</th>
                            <th>截止日期</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>

                    <!-- 分页 -->
                    <div class="text-center">
                        <ul class="pagination" id="pagination"></ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 添加缴费记录模态框 -->
<div class="modal fade" id="paymentModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">添加缴费记录</h4>
            </div>
            <div class="modal-body">
                <form id="paymentForm">
                    <div class="form-group">
                        <label>业主 <span class="text-danger">*</span></label>
                        <select class="form-control" id="ownerId" name="ownerId" required onchange="loadOwnerHouse()">
                            <option value="">请选择业主</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>房屋编号 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="houseId" name="houseId" readonly required>
                    </div>
                    <div class="form-group">
                        <label>收费项目 <span class="text-danger">*</span></label>
                        <select class="form-control" id="itemId" name="itemId" required>
                            <option value="">请选择收费项目</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>缴费期限 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="billingPeriod" name="billingPeriod"
                               placeholder="例如：2024年1月" required>
                    </div>
                    <div class="form-group">
                        <label>应缴金额 <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="amount" name="amount"
                               step="0.01" min="0" required>
                    </div>
                    <div class="form-group">
                        <label>截止日期 <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="dueDate" name="dueDate" required>
                    </div>
                    <div class="form-group">
                        <label>备注</label>
                        <textarea class="form-control" id="remark" name="remark" rows="3"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="savePayment()">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 生成账单模态框 -->
<div class="modal fade" id="generateBillModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">生成物业费账单</h4>
            </div>
            <div class="modal-body">
                <form id="generateBillForm">
                    <div class="form-group">
                        <label>收费项目 <span class="text-danger">*</span></label>
                        <select class="form-control" id="billItemId" name="itemId" required>
                            <option value="">请选择收费项目</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>缴费期限 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="billPeriod" name="billingPeriod"
                               placeholder="例如：2024年1月" required>
                    </div>
                    <div class="form-group">
                        <label>截止日期 <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="billDueDate" name="dueDate" required>
                    </div>
                    <div class="alert alert-info">
                        <strong>提示：</strong>系统将为所有已入住房屋自动生成账单
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="generateBill()">生成</button>
            </div>
        </div>
    </div>
</div>

<!-- 缴费模态框 -->
<div class="modal fade" id="payModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">处理缴费</h4>
            </div>
            <div class="modal-body">
                <form id="payForm">
                    <input type="hidden" id="payRecordId">
                    <div class="form-group">
                        <label>业主姓名</label>
                        <input type="text" class="form-control" id="payOwnerName" readonly>
                    </div>
                    <div class="form-group">
                        <label>房屋编号</label>
                        <input type="text" class="form-control" id="payHouseId" readonly>
                    </div>
                    <div class="form-group">
                        <label>收费项目</label>
                        <input type="text" class="form-control" id="payItemName" readonly>
                    </div>
                    <div class="form-group">
                        <label>应缴金额</label>
                        <input type="text" class="form-control" id="payAmount" readonly>
                    </div>
                    <div class="form-group">
                        <label>滞纳金</label>
                        <input type="text" class="form-control" id="payLateFee" readonly>
                    </div>
                    <div class="form-group">
                        <label>合计金额</label>
                        <input type="text" class="form-control" id="payTotalAmount" readonly>
                    </div>
                    <div class="form-group">
                        <label>缴费方式 <span class="text-danger">*</span></label>
                        <select class="form-control" id="paymentMethod" required>
                            <option value="">请选择缴费方式</option>
                            <option value="cash">现金</option>
                            <option value="wechat">微信</option>
                            <option value="alipay">支付宝</option>
                            <option value="bank_transfer">银行转账</option>
                            <option value="online">在线支付</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-success" onclick="processPay()">确认缴费</button>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
<script>
    var currentPage = 1;
    var pageSize = 10;

    $(function() {
        loadPaymentList();
        loadOwners();
        loadChargeItems();
    });

    // 加载缴费记录列表
    function loadPaymentList(page) {
        if (page) currentPage = page;

        var keyword = $('#searchKeyword').val();
        var status = $('#statusFilter').val();

        $.ajax({
            url: '${pageContext.request.contextPath}/payment?method=list',
            type: 'GET',
            data: {
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: keyword,
                status: status
            },
            dataType: 'json',
            success: function(result) {
                var tbody = $('#paymentTable tbody');
                tbody.empty();

                if (result.list.length === 0) {
                    tbody.append('<tr><td colspan="11" class="text-center">暂无数据</td></tr>');
                    return;
                }

                result.list.forEach(function(payment) {
                    var statusClass = payment.paymentStatus === 'paid' ? 'label-success' :
                        payment.paymentStatus === 'overdue' ? 'label-danger' : 'label-warning';
                    var statusText = payment.paymentStatus === 'paid' ? '已缴费' :
                        payment.paymentStatus === 'overdue' ? '已逾期' : '未缴费';

                    var actionBtn = payment.paymentStatus === 'paid' ?
                        '<button class="btn btn-sm btn-info" onclick="viewReceipt(' + payment.recordId + ')">查看收据</button>' :
                        '<button class="btn btn-sm btn-success" onclick="showPayModal(' + payment.recordId + ')">缴费</button>';

                    var tr = '<tr>' +
                        '<td>' + payment.recordId + '</td>' +
                        '<td>' + payment.ownerName + '</td>' +
                        '<td>' + payment.houseId + '</td>' +
                        '<td>' + payment.itemName + '</td>' +
                        '<td>' + payment.billingPeriod + '</td>' +
                        '<td>¥' + payment.amount.toFixed(2) + '</td>' +
                        '<td>¥' + payment.lateFee.toFixed(2) + '</td>' +
                        '<td><strong>¥' + payment.totalAmount.toFixed(2) + '</strong></td>' +
                        '<td>' + formatDate(payment.dueDate) + '</td>' +
                        '<td><span class="label ' + statusClass + '">' + statusText + '</span></td>' +
                        '<td>' + actionBtn + '</td>' +
                        '</tr>';
                    tbody.append(tr);
                });

                // 渲染分页
                renderPagination(result.pageNum, result.totalPages);
            }
        });
    }

    // 加载业主列表
    function loadOwners() {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner?method=findAll',
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var select = $('#ownerId');
                    select.find('option:not(:first)').remove();

                    result.data.forEach(function(owner) {
                        select.append('<option value="' + owner.ownerId + '" data-house="' + owner.houseId + '">' +
                            owner.ownerName + ' (' + owner.ownerId + ')</option>');
                    });
                }
            }
        });
    }

    // 加载收费项目
    function loadChargeItems() {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/chargeItem?method=findActive',
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var select1 = $('#itemId');
                    var select2 = $('#billItemId');
                    select1.find('option:not(:first)').remove();
                    select2.find('option:not(:first)').remove();

                    result.data.forEach(function(item) {
                        var option = '<option value="' + item.itemId + '">' + item.itemName + '</option>';
                        select1.append(option);
                        select2.append(option);
                    });
                }
            }
        });
    }

    // 加载业主房屋
    function loadOwnerHouse() {
        var selected = $('#ownerId option:selected');
        var houseId = selected.data('house');
        $('#houseId').val(houseId);
    }

    // 搜索缴费记录
    function searchPayment() {
        currentPage = 1;
        loadPaymentList();
    }

    // 显示添加模态框
    function showAddModal() {
        $('#paymentForm')[0].reset();
        $('#paymentModal').modal('show');
    }

    // 保存缴费记录
    function savePayment() {
        var form = $('#paymentForm')[0];
        if (!form.checkValidity()) {
            alert('请填写完整信息');
            return;
        }

        var data = {
            ownerId: $('#ownerId').val(),
            houseId: $('#houseId').val(),
            itemId: $('#itemId').val(),
            billingPeriod: $('#billingPeriod').val(),
            amount: $('#amount').val(),
            dueDate: $('#dueDate').val(),
            remark: $('#remark').val()
        };

        $.ajax({
            url: '${pageContext.request.contextPath}/payment?method=add',
            type: 'POST',
            data: data,
            dataType: 'json',
            success: function(result) {
                alert(result.message);
                if (result.success) {
                    $('#paymentModal').modal('hide');
                    loadPaymentList();
                }
            }
        });
    }

    // 显示生成账单模态框
    function showGenerateBillModal() {
        $('#generateBillForm')[0].reset();

        // 设置默认期限为下个月
        var now = new Date();
        var nextMonth = new Date(now.getFullYear(), now.getMonth() + 1, 1);
        var period = nextMonth.getFullYear() + '年' + (nextMonth.getMonth() + 1) + '月';
        $('#billPeriod').val(period);

        // 设置默认截止日期为下个月15号
        var dueDate = new Date(nextMonth.getFullYear(), nextMonth.getMonth(), 15);
        $('#billDueDate').val(dueDate.toISOString().split('T')[0]);

        $('#generateBillModal').modal('show');
    }

    // 生成账单
    function generateBill() {
        var form = $('#generateBillForm')[0];
        if (!form.checkValidity()) {
            alert('请填写完整信息');
            return;
        }

        if (!confirm('确定要生成账单吗？')) {
            return;
        }

        var data = {
            itemId: $('#billItemId').val(),
            billingPeriod: $('#billPeriod').val(),
            dueDate: $('#billDueDate').val()
        };

        $.ajax({
            url: '${pageContext.request.contextPath}/payment?method=generateBill',
            type: 'POST',
            data: data,
            dataType: 'json',
            success: function(result) {
                alert(result.message);
                if (result.success) {
                    $('#generateBillModal').modal('hide');
                    loadPaymentList();
                }
            }
        });
    }

    // 显示缴费模态框
    function showPayModal(recordId) {
        $.ajax({
            url: '${pageContext.request.contextPath}/payment?method=list',
            type: 'GET',
            data: {
                pageNum: 1,
                pageSize: 1000
            },
            dataType: 'json',
            success: function(result) {
                var payment = result.list.find(p => p.recordId === recordId);
                if (payment) {
                    $('#payRecordId').val(payment.recordId);
                    $('#payOwnerName').val(payment.ownerName);
                    $('#payHouseId').val(payment.houseId);
                    $('#payItemName').val(payment.itemName);
                    $('#payAmount').val('¥' + payment.amount.toFixed(2));
                    $('#payLateFee').val('¥' + payment.lateFee.toFixed(2));
                    $('#payTotalAmount').val('¥' + payment.totalAmount.toFixed(2));
                    $('#paymentMethod').val('');

                    $('#payModal').modal('show');
                }
            }
        });
    }

    // 处理缴费
    function processPay() {
        var paymentMethod = $('#paymentMethod').val();
        if (!paymentMethod) {
            alert('请选择缴费方式');
            return;
        }

        var recordId = $('#payRecordId').val();

        $.ajax({
            url: '${pageContext.request.contextPath}/payment?method=pay',
            type: 'POST',
            data: {
                recordId: recordId,
                paymentMethod: paymentMethod
            },
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    alert('缴费成功！收据号：' + result.receiptNo);
                    $('#payModal').modal('hide');
                    loadPaymentList();
                } else {
                    alert(result.message);
                }
            }
        });
    }

    // 渲染分页
    function renderPagination(current, total) {
        var pagination = $('#pagination');
        pagination.empty();

        if (total <= 1) return;

        if (current > 1) {
            pagination.append('<li><a href="javascript:loadPaymentList(' + (current - 1) + ')">«</a></li>');
        }

        for (var i = 1; i <= total; i++) {
            var activeClass = i === current ? 'active' : '';
            pagination.append('<li class="' + activeClass + '"><a href="javascript:loadPaymentList(' + i + ')">' + i + '</a></li>');
        }

        if (current < total) {
            pagination.append('<li><a href="javascript:loadPaymentList(' + (current + 1) + ')">»</a></li>');
        }
    }

    // 格式化日期
    function formatDate(date) {
        if (!date) return '';
        var d = new Date(date);
        return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate());
    }

    function pad(num) {
        return num < 10 ? '0' + num : num;
    }
</script>
</body>
</html>
