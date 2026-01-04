<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>收费项目管理 - 物业管理系统</title>

    <!-- 引入 CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/theme/default/layer.css">

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

        .search-box {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
            margin-bottom: 20px;
        }

        .data-table {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .table {
            margin-bottom: 0;
        }

        .table thead th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
            color: #495057;
            font-weight: 600;
            padding: 15px;
            white-space: nowrap;
        }

        .table tbody td {
            padding: 12px 15px;
            vertical-align: middle;
        }

        .btn-action {
            margin: 2px;
            padding: 5px 12px;
            font-size: 13px;
        }

        .pagination-wrapper {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
            margin-top: 20px;
        }

        .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 8px;
        }

        .required::after {
            content: " *";
            color: #dc3545;
        }

        .checkbox-cell {
            width: 40px;
            text-align: center;
        }

        input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        .btn-group-custom {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
        }

        .status-active {
            background-color: #e8f5e9;
            color: #388e3c;
        }

        .status-inactive {
            background-color: #ffebee;
            color: #d32f2f;
        }

        /* 收费周期标签 */
        .cycle-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
        }

        .cycle-monthly {
            background-color: #e3f2fd;
            color: #1976d2;
        }

        .cycle-quarterly {
            background-color: #f3e5f5;
            color: #7b1fa2;
        }

        .cycle-yearly {
            background-color: #e0f2f1;
            color: #00796b;
        }

        .cycle-once {
            background-color: #fff3e0;
            color: #f57c00;
        }

        .help-text {
            font-size: 12px;
            color: #6c757d;
            margin-top: 5px;
        }

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
            <a href="${pageContext.request.contextPath}/admin/chargeItem.jsp" class="active">
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
            <a href="${pageContext.request.contextPath}/admin/statistics.jsp">
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
        <h2><i class="fas fa-list-alt"></i> 收费项目管理</h2>
        <p>管理物业收费项目，包括物业费、停车费、水电费等各类收费标准</p>
    </div>

    <!-- 搜索和操作区 -->
    <div class="search-box">
        <div class="row mb-3">
            <div class="col-md-4">
                <input type="text" class="form-control" id="searchKeyword"
                       placeholder="搜索项目名称或编号">
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary btn-block" onclick="searchItem()">
                    <i class="fas fa-search"></i> 搜索
                </button>
            </div>
            <div class="col-md-2">
                <button class="btn btn-secondary btn-block" onclick="resetSearch()">
                    <i class="fas fa-redo"></i> 重置
                </button>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <div class="btn-group-custom">
                    <button class="btn btn-success" onclick="showAddModal()">
                        <i class="fas fa-plus"></i> 添加收费项目
                    </button>
                    <button class="btn btn-danger" onclick="batchDelete()">
                        <i class="fas fa-trash-alt"></i> 批量删除
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-table">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th class="checkbox-cell">
                        <input type="checkbox" id="checkAll" onclick="toggleCheckAll()">
                    </th>
                    <th>项目编号</th>
                    <th>项目名称</th>
                    <th>收费周期</th>
                    <th>计算类型</th>
                    <th>收费标准</th>
                    <th>状态</th>
                    <th>创建时间</th>
                    <th width="220">操作</th>
                </tr>
                </thead>
                <tbody id="itemTableBody">
                <tr>
                    <td colspan="9" class="text-center">
                        <i class="fas fa-spinner fa-spin"></i> 加载中...
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
        <div class="row">
            <div class="col-md-6">
                <div id="pageInfo">共 0 条记录</div>
            </div>
            <div class="col-md-6">
                <nav>
                    <ul class="pagination justify-content-end" id="pagination">
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>

<!-- 添加/编辑收费项目模态框 -->
<div class="modal fade" id="itemModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">添加收费项目</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="itemForm">
                    <input type="hidden" id="formMethod" value="add">
                    <input type="hidden" id="originalItemId">

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">项目编号</label>
                                <input type="text" class="form-control" id="itemId"
                                       name="itemId" required placeholder="2位数字，如：01"
                                       maxlength="2" pattern="[0-9]{2}">
                                <small class="help-text">必须为2位数字</small>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">项目名称</label>
                                <input type="text" class="form-control" id="itemName"
                                       name="itemName" required placeholder="请输入项目名称">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">收费周期</label>
                                <select class="form-control" id="chargeCycle" name="chargeCycle" required>
                                    <option value="">请选择</option>
                                    <option value="monthly">按月</option>
                                    <option value="quarterly">按季度</option>
                                    <option value="yearly">按年</option>
                                    <option value="once">一次性</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">计算类型</label>
                                <select class="form-control" id="calculationType" name="calculationType" required>
                                    <option value="">请选择</option>
                                    <option value="fixed">固定金额</option>
                                    <option value="area_based">按面积计算</option>
                                </select>
                                <small class="help-text">固定金额：每次收取固定费用；按面积：根据房屋面积计算</small>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group" id="amountGroup">
                                <label class="form-label required" id="amountLabel">固定金额（元）</label>
                                <input type="number" class="form-control" id="fixedAmount"
                                       name="fixedAmount" placeholder="请输入金额"
                                       step="0.01" min="0.01" max="999999.99">
                                <small class="help-text" id="amountHelp">每次收取的固定金额</small>
                            </div>
                        </div>
                        <div class="col-md-6" id="formulaGroup" style="display: none;">
                            <div class="form-group">
                                <label class="form-label">计算公式</label>
                                <input type="text" class="form-control" id="formula"
                                       name="formula" placeholder="例如: 面积 * 2.5">
                                <small class="help-text">按面积计算时使用，如：面积 * 单价</small>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">宽限期（天）</label>
                                <input type="number" class="form-control" id="gracePeriod"
                                       name="gracePeriod" placeholder="逾期宽限天数"
                                       min="0" max="365" value="30">
                                <small class="help-text">超过缴费期限后的宽限天数，默认30天</small>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">滞纳金比例</label>
                                <input type="number" class="form-control" id="lateFeeRate"
                                       name="lateFeeRate" placeholder="例如：0.0005"
                                       step="0.0001" min="0" max="1" value="0.0005">
                                <small class="help-text">每日滞纳金比例，如 0.0005 表示万分之五</small>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label class="form-label">项目说明</label>
                                <textarea class="form-control" id="description" name="description"
                                          rows="3" placeholder="请输入项目说明"></textarea>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">状态</label>
                                <select class="form-control" id="status" name="status">
                                    <option value="1">启用</option>
                                    <option value="0">停用</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="saveItem()">
                    <i class="fas fa-save"></i> 保存
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 引入 JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/layer.js"></script>

<script>
    var currentPage = 1;
    var pageSize = 10;
    var totalCount = 0;

    $(document).ready(function() {
        console.log('收费项目管理页面加载完成');
        loadItemList(1);

        // 🔧 计算类型改变时的处理（修复版）
        $('#calculationType').change(function() {
            var type = $(this).val();
            console.log('计算类型改变:', type);

            if (type === 'fixed') {
                // 固定金额
                $('#amountLabel').html('固定金额（元） <span style="color: red;">*</span>');
                $('#amountHelp').text('每次收取的固定金额');
                $('#fixedAmount').prop('required', true).attr('placeholder', '请输入固定金额');
                $('#amountGroup').show();
                $('#formulaGroup').hide();
                $('#formula').prop('required', false).val('');
            } else if (type === 'area_based') {
                // 按面积计算
                $('#amountLabel').html('单价（元/平米） <span style="color: red;">*</span>');
                $('#amountHelp').text('每平米的收费单价');
                $('#fixedAmount').prop('required', true).attr('placeholder', '请输入单价');
                $('#amountGroup').show();
                $('#formulaGroup').show();
                $('#formula').prop('required', false).attr('placeholder', '可选，如：面积 * 单价');
            } else {
                $('#amountGroup').hide();
                $('#formulaGroup').hide();
                $('#fixedAmount').prop('required', false);
                $('#formula').prop('required', false);
            }
        });

        // 回车搜索
        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) {
                searchItem();
            }
        });

        // 🔧 项目编号输入限制（只允许数字）
        $('#itemId').on('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '').substring(0, 2);
        });
    });

    /**
     * 加载收费项目列表
     */
    function loadItemList(pageNum) {
        currentPage = pageNum || currentPage;
        var keyword = $('#searchKeyword').val();

        console.log('正在加载收费项目列表，页码:', currentPage);

        // 显示加载状态
        $('#itemTableBody').html('<tr><td colspan="9" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/chargeItem',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: keyword
            },
            success: function(response) {
                console.log('收费项目列表响应:', response);

                // 兼容不同的响应格式
                var data = response;
                if (response.data) {
                    data = response.data;
                }

                if (data && data.list) {
                    renderItemTable(data.list);
                    totalCount = data.total || 0;
                    renderPagination();
                } else {
                    $('#itemTableBody').html('<tr><td colspan="9" class="text-center text-muted">暂无数据</td></tr>');
                    totalCount = 0;
                    renderPagination();
                }
            },
            error: function(xhr, status, error) {
                console.error('加载失败:', error);
                $('#itemTableBody').html('<tr><td colspan="9" class="text-center text-danger"><i class="fas fa-exclamation-circle"></i> 加载失败，请刷新重试</td></tr>');
            }
        });
    }

    /**
     * 🔧 渲染收费项目表格（修复版）
     */
    function renderItemTable(items) {
        console.log('开始渲染表格，数据条数:', items ? items.length : 0);

        var tbody = $('#itemTableBody');
        tbody.empty();

        if (!items || items.length === 0) {
            tbody.append('<tr><td colspan="9" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无数据</td></tr>');
            return;
        }

        $.each(items, function(i, item) {
            var cycleName = getCycleName(item.chargeCycle);
            var cycleClass = 'cycle-' + item.chargeCycle;
            var typeName = getCalculationTypeName(item.calculationType);

            var statusClass = item.status === 1 ? 'status-active' : 'status-inactive';
            var statusText = item.status === 1 ? '启用' : '停用';

            // 🔧 显示金额（修复）
            var amount = '-';
            if (item.calculationType === 'fixed') {
                amount = '<strong>' + (item.fixedAmount || 0) + '</strong> 元';
            } else if (item.calculationType === 'area_based') {
                amount = '<strong>' + (item.fixedAmount || 0) + '</strong> 元/平米';
                if (item.formula && item.formula.trim() !== '') {
                    amount += '<br><small class="text-muted">' + item.formula + '</small>';
                }
            }

            var row = '<tr>' +
                '<td class="checkbox-cell"><input type="checkbox" class="row-checkbox" value="' + (item.itemId || '') + '"></td>' +
                '<td>' + (item.itemId || '-') + '</td>' +
                '<td><strong>' + (item.itemName || '-') + '</strong></td>' +
                '<td><span class="cycle-badge ' + cycleClass + '">' + cycleName + '</span></td>' +
                '<td>' + typeName + '</td>' +
                '<td>' + amount + '</td>' +
                '<td><span class="status-badge ' + statusClass + '">' + statusText + '</span></td>' +
                '<td>' + formatDate(item.createTime) + '</td>' +
                '<td>' +
                '<button class="btn btn-sm btn-info btn-action" onclick="viewItem(\'' + item.itemId + '\')" title="查看详情">' +
                '<i class="fas fa-eye"></i> 查看' +
                '</button>' +
                '<button class="btn btn-sm btn-warning btn-action" onclick="editItem(\'' + item.itemId + '\')" title="编辑">' +
                '<i class="fas fa-edit"></i> 编辑' +
                '</button>' +
                '<button class="btn btn-sm btn-danger btn-action" onclick="deleteItem(\'' + item.itemId + '\')" title="删除">' +
                '<i class="fas fa-trash"></i> 删除' +
                '</button>' +
                '</td>' +
                '</tr>';
            tbody.append(row);
        });

        console.log('✅ 表格渲染完成，共', items.length, '行');
    }

    /**
     * 渲染分页
     */
    function renderPagination() {
        var totalPages = Math.ceil(totalCount / pageSize);
        $('#pageInfo').text('共 ' + totalCount + ' 条记录');

        var pagination = $('#pagination');
        pagination.empty();

        if (totalPages <= 1) return;

        // 上一页
        var prevDisabled = currentPage === 1 ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + prevDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage > 1 ? 'loadItemList(' + (currentPage - 1) + ')' : 'return false') + '">上一页</a>' +
            '</li>'
        );

        // 页码
        var startPage = Math.max(1, currentPage - 2);
        var endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            pagination.append(
                '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadItemList(1)">1</a></li>'
            );
            if (startPage > 2) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
        }

        for (var i = startPage; i <= endPage; i++) {
            var activeClass = i === currentPage ? 'active' : '';
            pagination.append(
                '<li class="page-item ' + activeClass + '">' +
                '<a class="page-link" href="javascript:void(0)" onclick="loadItemList(' + i + ')">' + i + '</a>' +
                '</li>'
            );
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
            pagination.append(
                '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadItemList(' + totalPages + ')">' + totalPages + '</a></li>'
            );
        }

        // 下一页
        var nextDisabled = currentPage === totalPages ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + nextDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage < totalPages ? 'loadItemList(' + (currentPage + 1) + ')' : 'return false') + '">下一页</a>' +
            '</li>'
        );
    }

    /**
     * 全选/取消全选
     */
    function toggleCheckAll() {
        var checked = $('#checkAll').prop('checked');
        $('.row-checkbox').prop('checked', checked);
    }

    /**
     * 搜索收费项目
     */
    function searchItem() {
        currentPage = 1;
        loadItemList(1);
    }

    /**
     * 重置搜索
     */
    function resetSearch() {
        $('#searchKeyword').val('');
        currentPage = 1;
        loadItemList(1);
    }

    /**
     * 🔧 显示添加模态框（修复版）
     */
    function showAddModal() {
        $('#modalTitle').text('添加收费项目');
        $('#formMethod').val('add');
        $('#itemForm')[0].reset();

        // 设置默认值
        $('#status').val('1');
        $('#gracePeriod').val('30');
        $('#lateFeeRate').val('0.0005');
        $('#calculationType').val('fixed').trigger('change');

        // 启用项目编号输入
        $('#itemId').prop('readonly', false);

        $('#itemModal').modal('show');
    }

    /**
     * 🔧 查看收费项目详情（修复版）
     */
    function viewItem(itemId) {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/chargeItem',
            type: 'GET',
            data: {
                method: 'findById',
                itemId: itemId
            },
            success: function(response) {
                var item = response.data || response;

                if (item && item.itemId) {
                    var amount = '-';
                    if (item.calculationType === 'fixed') {
                        amount = (item.fixedAmount || 0) + ' 元';
                    } else if (item.calculationType === 'area_based') {
                        amount = (item.fixedAmount || 0) + ' 元/平米';
                        if (item.formula && item.formula.trim() !== '') {
                            amount += '<br><small>' + item.formula + '</small>';
                        }
                    }

                    var content =
                        '<div style="padding: 20px; font-family: Microsoft YaHei, Arial, sans-serif;">' +
                        '<div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px;">' +
                        '<h4 style="margin: 0 0 10px 0;"><i class="fas fa-list-alt"></i> ' + (item.itemName || '-') + '</h4>' +
                        '<p style="margin: 0; opacity: 0.9;">项目编号：' + (item.itemId || '-') + '</p>' +
                        '</div>' +
                        '<div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 15px;">' +
                        '<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-calendar-alt" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">收费周期</div>' +
                        '<div style="font-weight: 600; color: #333;">' + getCycleName(item.chargeCycle) + '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-calculator" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">计算类型</div>' +
                        '<div style="font-weight: 600; color: #333;">' + getCalculationTypeName(item.calculationType) + '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-yen-sign" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">收费标准</div>' +
                        '<div style="font-weight: 600; color: #333;">' + amount + '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-clock" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">宽限期</div>' +
                        '<div style="font-weight: 600; color: #333;">' + (item.gracePeriod || 0) + ' 天</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-percentage" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">滞纳金比例</div>' +
                        '<div style="font-weight: 600; color: #333;">' + ((item.lateFeeRate || 0) * 100) + '%（日）</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-toggle-on" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">状态</div>' +
                        '<div style="font-weight: 600; color: #333;">' + (item.status === 1 ? '启用' : '停用') + '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>';

                    if (item.description && item.description.trim() !== '') {
                        content +=
                            '<div style="background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; border-radius: 5px;">' +
                            '<div style="font-size: 12px; color: #856404; margin-bottom: 5px;"><i class="fas fa-info-circle"></i> 项目说明</div>' +
                            '<div style="color: #856404; line-height: 1.6;">' + item.description + '</div>' +
                            '</div>';
                    }

                    content += '</div>';

                    layer.open({
                        type: 1,
                        title: '<i class="fas fa-list-alt"></i> 收费项目详情',
                        area: ['600px', 'auto'],
                        shade: 0.5,
                        shadeClose: true,
                        content: content,
                        btn: ['<i class="fas fa-edit"></i> 编辑', '<i class="fas fa-times"></i> 关闭'],
                        yes: function(index, layero) {
                            layer.close(index);
                            editItem(itemId);
                        },
                        btn2: function(index, layero) {
                            layer.close(index);
                        }
                    });
                } else {
                    layer.msg('查询失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * 🔧 编辑收费项目（修复版）
     */
    function editItem(itemId) {
        $('#modalTitle').text('编辑收费项目');
        $('#formMethod').val('update');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/chargeItem',
            type: 'GET',
            data: {
                method: 'findById',
                itemId: itemId
            },
            success: function(response) {
                var item = response.data || response;

                if (item && item.itemId) {
                    $('#originalItemId').val(item.itemId);
                    $('#itemId').val(item.itemId).prop('readonly', true); // 编辑时不允许修改ID
                    $('#itemName').val(item.itemName);
                    $('#chargeCycle').val(item.chargeCycle);
                    $('#calculationType').val(item.calculationType).trigger('change');
                    $('#fixedAmount').val(item.fixedAmount);
                    $('#formula').val(item.formula);
                    $('#gracePeriod').val(item.gracePeriod);
                    $('#lateFeeRate').val(item.lateFeeRate);
                    $('#description').val(item.description);
                    $('#status').val(item.status);

                    $('#itemModal').modal('show');
                } else {
                    layer.msg('查询失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * 🔧 保存收费项目（修复版）
     */
    function saveItem() {
        var form = $('#itemForm')[0];
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        var method = $('#formMethod').val();

        // 🔧 获取项目ID
        var itemId = '';
        if (method === 'update') {
            itemId = $('#originalItemId').val();
        } else {
            itemId = $('#itemId').val().trim();
            if (!itemId || itemId.length !== 2 || !/^[0-9]{2}$/.test(itemId)) {
                layer.msg('项目编号必须为2位数字', {icon: 0});
                return;
            }
        }

        var data = {
            method: method,
            itemId: itemId,
            itemName: $('#itemName').val().trim(),
            chargeCycle: $('#chargeCycle').val(),
            calculationType: $('#calculationType').val(),
            fixedAmount: $('#fixedAmount').val(),
            formula: $('#formula').val().trim(),
            gracePeriod: $('#gracePeriod').val() || 30,
            lateFeeRate: $('#lateFeeRate').val() || 0.0005,
            description: $('#description').val().trim(),
            status: $('#status').val()
        };

        console.log('提交数据:', data);

        // 🔧 验证必填字段
        if (!data.itemName) {
            layer.msg('请输入项目名称', {icon: 0});
            return;
        }
        if (!data.chargeCycle) {
            layer.msg('请选择收费周期', {icon: 0});
            return;
        }
        if (!data.calculationType) {
            layer.msg('请选择计算类型', {icon: 0});
            return;
        }
        if (data.calculationType === 'fixed' || data.calculationType === 'area_based') {
            if (!data.fixedAmount || parseFloat(data.fixedAmount) <= 0) {
                layer.msg('请输入有效的金额', {icon: 0});
                return;
            }
        }

        var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/chargeItem',
            type: 'POST',
            data: data,
            success: function(response) {
                layer.close(loadingIndex);
                console.log('保存响应:', response);

                if (response.success || response.code === 200) {
                    layer.msg(response.message || '保存成功', {icon: 1});
                    $('#itemModal').modal('hide');
                    loadItemList(currentPage);
                } else {
                    layer.msg(response.message || '保存失败', {icon: 2});
                }
            },
            error: function(xhr) {
                layer.close(loadingIndex);
                console.error('保存失败:', xhr);

                var errorMsg = '网络错误';
                try {
                    var response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {}

                layer.msg(errorMsg, {icon: 2, time: 3000});
            }
        });
    }

    /**
     * 删除收费项目
     */
    function deleteItem(itemId) {
        layer.confirm('确定要删除该收费项目吗？删除后将无法恢复！', {
            icon: 3,
            title: '确认删除',
            btn: ['确定', '取消']
        }, function(index) {
            var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

            $.ajax({
                url: '${pageContext.request.contextPath}/admin/chargeItem',
                type: 'POST',
                data: {
                    method: 'delete',
                    itemId: itemId
                },
                success: function(response) {
                    layer.close(loadingIndex);
                    if (response.success || response.code === 200) {
                        layer.msg('删除成功', {icon: 1});
                        loadItemList(currentPage);
                    } else {
                        layer.msg(response.message || '删除失败', {icon: 2});
                    }
                },
                error: function() {
                    layer.close(loadingIndex);
                    layer.msg('网络错误', {icon: 2});
                }
            });
            layer.close(index);
        });
    }

    /**
     * 批量删除
     */
    function batchDelete() {
        var checkedBoxes = $('.row-checkbox:checked');
        if (checkedBoxes.length === 0) {
            layer.msg('请先选择要删除的收费项目', {icon: 0});
            return;
        }

        var ids = [];
        checkedBoxes.each(function() {
            ids.push($(this).val());
        });

        layer.confirm('确定要删除选中的 ' + ids.length + ' 条记录吗？', {
            icon: 3,
            title: '确认批量删除',
            btn: ['确定', '取消']
        }, function(index) {
            var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

            var deleteCount = 0;
            var failCount = 0;
            var completed = 0;

            $.each(ids, function(i, id) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/admin/chargeItem',
                    type: 'POST',
                    data: {
                        method: 'delete',
                        itemId: id
                    },
                    success: function(response) {
                        if (response.success || response.code === 200) {
                            deleteCount++;
                        } else {
                            failCount++;
                        }
                    },
                    error: function() {
                        failCount++;
                    },
                    complete: function() {
                        completed++;
                        if (completed === ids.length) {
                            layer.close(loadingIndex);
                            layer.msg('成功删除 ' + deleteCount + ' 条记录' +
                                (failCount > 0 ? '，失败 ' + failCount + ' 条' : ''), {icon: 1});
                            $('#checkAll').prop('checked', false);
                            loadItemList(currentPage);
                        }
                    }
                });
            });

            layer.close(index);
        });
    }

    /**
     * 获取周期名称
     */
    function getCycleName(cycle) {
        var cycles = {
            'monthly': '按月',
            'quarterly': '按季度',
            'yearly': '按年',
            'once': '一次性'
        };
        return cycles[cycle] || cycle;
    }

    /**
     * 🔧 获取计算类型名称（修复）
     */
    function getCalculationTypeName(type) {
        var types = {
            'fixed': '固定金额',
            'area_based': '按面积计算'
        };
        return types[type] || type;
    }

    /**
     * 格式化日期
     */
    function formatDate(dateStr) {
        if (!dateStr) return '-';

        try {
            var date = new Date(dateStr);
            if (isNaN(date.getTime())) return '-';

            var year = date.getFullYear();
            var month = String(date.getMonth() + 1).padStart(2, '0');
            var day = String(date.getDate()).padStart(2, '0');

            return year + '-' + month + '-' + day;
        } catch (e) {
            console.error('日期格式化失败:', dateStr, e);
            return '-';
        }
    }
</script>

</body>
</html>
