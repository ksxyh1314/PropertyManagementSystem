<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>缴费管理 - 物业管理系统</title>

    <!-- 引入 CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/theme/default/layer.css">

    <style>
        /* ===== 缴费统计分析样式 ===== */
        .info-item {
            background: #f8f9fa;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 10px;
        }

        .info-label {
            color: #666;
            font-size: 12px;
            margin-bottom: 5px;
        }

        .info-value {
            font-size: 16px;
            font-weight: 600;
            color: #333;
        }

        .metric-card {
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            display: flex;
            align-items: center;
            transition: transform 0.3s;
        }

        .metric-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.15);
        }

        .metric-icon {
            width: 70px;
            height: 70px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            color: white;
            margin-right: 20px;
        }

        .bg-gradient-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        .bg-gradient-success {
            background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
        }

        .metric-info {
            flex: 1;
        }

        .metric-value {
            font-size: 36px;
            font-weight: bold;
            margin: 0;
            color: #2c3e50;
        }

        .metric-label {
            font-size: 14px;
            color: #7f8c8d;
            margin: 5px 0 0 0;
        }

        .detail-card {
            background: white;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .detail-card .card-title {
            font-size: 16px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
        }

        .detail-card .card-title i {
            color: #667eea;
            margin-right: 8px;
        }

        .table-bordered th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }

        .amount-text {
            font-weight: 600;
            font-size: 16px;
        }

        body {
            background-color: #f5f7fa;
            font-family: 'Microsoft YaHei', Arial, sans-serif;
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

        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
        }

        .status-paid {
            background-color: #e8f5e9;
            color: #388e3c;
        }

        .status-unpaid {
            background-color: #fff3e0;
            color: #f57c00;
        }

        .status-overdue {
            background-color: #ffebee;
            color: #d32f2f;
        }

        .btn-group-custom {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .btn-generate {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            border: none;
        }

        .btn-generate:hover {
            background: linear-gradient(135deg, #00f2fe 0%, #4facfe 100%);
            color: white;
        }

        .btn-export {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            color: white;
            border: none;
        }

        .btn-export:hover {
            background: linear-gradient(135deg, #38f9d7 0%, #43e97b 100%);
            color: white;
        }

        .stats-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
            margin-bottom: 20px;
        }

        .stats-item {
            text-align: center;
            padding: 15px;
        }

        .stats-value {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .stats-label {
            color: #666;
            font-size: 14px;
        }

        .alert-info-custom {
            background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
            border: none;
            border-radius: 8px;
            color: #00695c;
        }

        /* ✅ 复选框样式 */
        .checkbox-cell {
            width: 40px;
            text-align: center;
        }

        .checkbox-cell input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        /* ✅ 批量操作按钮组 */
        .batch-actions {
            display: none;
            padding: 15px;
            background: #fff3cd;
            border-radius: 8px;
            margin-bottom: 15px;
            border: 1px solid #ffc107;
        }

        .batch-actions.show {
            display: block;
        }

        /* ✅ 新增：全选提示样式 */
        .select-all-hint {
            background: #e3f2fd;
            padding: 10px 15px;
            border-radius: 6px;
            margin-bottom: 10px;
            border: 1px solid #2196f3;
            display: none;
        }

        .select-all-hint.show {
            display: block;
        }

        /* 收据样式 */
        .receipt-container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .receipt-header {
            text-align: center;
            border-bottom: 2px solid #667eea;
            padding-bottom: 20px;
            margin-bottom: 20px;
        }

        .receipt-title {
            color: #667eea;
            font-weight: bold;
            font-size: 24px;
            margin-bottom: 10px;
        }

        .receipt-subtitle {
            color: #666;
            font-size: 14px;
        }

        .receipt-no-box {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px;
            border-radius: 8px;
            text-align: center;
            margin-bottom: 20px;
        }

        .receipt-no-label {
            font-size: 12px;
            opacity: 0.9;
        }

        .receipt-no-value {
            font-size: 24px;
            font-weight: bold;
            letter-spacing: 2px;
            margin-top: 5px;
        }

        .info-item {
            background: #f8f9fa;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 10px;
        }

        .info-label {
            color: #666;
            font-size: 12px;
            margin-bottom: 5px;
        }

        .info-value {
            font-size: 16px;
            font-weight: 600;
            color: #333;
        }

        .amount-box {
            background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .amount-item {
            text-align: center;
        }

        .amount-label {
            color: #666;
            font-size: 12px;
            margin-bottom: 5px;
        }

        .amount-value {
            font-size: 20px;
            font-weight: bold;
        }

        .receipt-footer {
            border-top: 2px dashed #ddd;
            padding-top: 20px;
            margin-top: 20px;
        }

        .receipt-footer-text {
            color: #666;
            font-size: 12px;
        }

        @media print {
            .modal-header, .modal-footer {
                display: none;
            }
        }
    </style>
</head>

<body>

<div class="container-fluid">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-money-bill-wave"></i> 缴费管理</h2>
        <p>管理物业缴费记录,包括账单生成、缴费处理、逾期管理和统计分析</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-card">
        <div class="row">
            <div class="col-md-3">
                <div class="stats-item">
                    <div class="stats-value text-primary" id="totalRecords">0</div>
                    <div class="stats-label">总记录数</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-item">
                    <div class="stats-value text-success" id="paidCount">0</div>
                    <div class="stats-label">已缴费</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-item">
                    <div class="stats-value text-warning" id="unpaidCount">0</div>
                    <div class="stats-label">未缴费</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-item">
                    <div class="stats-value text-danger" id="overdueCount">0</div>
                    <div class="stats-label">已逾期</div>
                </div>
            </div>
        </div>
    </div>

    <!-- 搜索和操作区 -->
    <div class="search-box">
        <div class="row mb-3">
            <div class="col-md-3">
                <input type="text" class="form-control" id="searchKeyword"
                       placeholder="搜索业主ID、姓名或房屋编号">
            </div>
            <div class="col-md-2">
                <select class="form-control" id="statusFilter">
                    <option value="">全部状态</option>
                    <option value="unpaid">未缴费</option>
                    <option value="paid">已缴费</option>
                    <option value="overdue">已逾期</option>
                </select>
            </div>
            <div class="col-md-2">
                <select class="form-control" id="itemFilter">
                    <option value="">全部收费项目</option>
                </select>
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary btn-block" onclick="searchPayment()">
                    <i class="fas fa-search"></i> 搜索
                </button>
            </div>
            <div class="col-md-2">
                <button class="btn btn-secondary btn-block" onclick="resetSearch()">
                    <i class="fas fa-redo"></i> 重置
                </button>
            </div>
        </div>

        <!-- ✅ 全选所有筛选结果提示 -->
        <div class="select-all-hint" id="selectAllHint">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <i class="fas fa-info-circle text-primary"></i>
                    <span>当前页已全选 <strong id="currentPageCount">0</strong> 条记录。</span>
                    <a href="javascript:void(0)" onclick="selectAllFiltered()" class="text-primary font-weight-bold">
                        <u>选择全部 <span id="totalFilteredCount">0</span> 条筛选结果？</u>
                    </a>
                </div>
                <div class="col-md-4 text-right">
                    <button class="btn btn-sm btn-outline-secondary" onclick="cancelSelectAllFiltered()">
                        <i class="fas fa-times"></i> 取消
                    </button>
                </div>
            </div>
        </div>

        <!-- ✅ 批量操作提示 -->
        <div class="batch-actions" id="batchActions">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <i class="fas fa-check-circle text-warning"></i>
                    <strong>已选择 <span id="selectedCount">0</span> 条记录</strong>
                    <span id="selectModeHint" style="display:none;" class="text-primary ml-2">
                        （全选模式：所有筛选结果）
                    </span>
                </div>
                <div class="col-md-6 text-right">
                    <button class="btn btn-sm btn-danger" onclick="batchDelete()">
                        <i class="fas fa-trash-alt"></i> 批量删除
                    </button>
                    <button class="btn btn-sm btn-success" onclick="batchExport()">
                        <i class="fas fa-file-excel"></i> 导出选中
                    </button>
                    <button class="btn btn-sm btn-secondary" onclick="clearSelection()">
                        <i class="fas fa-times"></i> 取消选择
                    </button>
                </div>
            </div>
        </div>

        <!-- 操作按钮 -->
        <div class="row">
            <div class="col-md-12">
                <div class="btn-group-custom">
                    <button class="btn btn-generate" onclick="showGenerateBillModal()">
                        <i class="fas fa-file-invoice"></i> 生成账单
                    </button>
                    <button class="btn btn-export" onclick="exportExcel()">
                        <i class="fas fa-file-excel"></i> 导出当前筛选
                    </button>
                    <button class="btn btn-info" onclick="showStatisticsModal()">
                        <i class="fas fa-chart-bar"></i> 统计分析
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- ✅ 数据表格 -->
    <div class="data-table">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th class="checkbox-cell">
                        <input type="checkbox" id="selectAll" onchange="toggleSelectAll()">
                    </th>
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
                    <th width="200">操作</th>
                </tr>
                </thead>
                <tbody id="paymentTableBody">
                <tr>
                    <td colspan="12" class="text-center">
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

<!-- 生成账单模态框 -->
<div class="modal fade" id="generateBillModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-file-invoice"></i> 批量生成账单
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="generateBillForm">
                    <div class="form-group">
                        <label class="form-label required">收费项目</label>
                        <select class="form-control" id="billItemId" name="itemId" required onchange="onChargeItemChange()">
                            <option value="">请选择收费项目</option>
                        </select>
                        <small class="form-text text-muted" id="itemInfo"></small>
                    </div>

                    <div class="form-group">
                        <label class="form-label required">账期</label>
                        <div class="input-group">
                            <input type="text" class="form-control" id="billPeriod" name="billingPeriod"
                                   placeholder="例如:2026年2月" required>
                            <div class="input-group-append">
                                <button class="btn btn-outline-secondary" type="button" onclick="autoGeneratePeriod()">
                                    <i class="fas fa-magic"></i> 自动生成
                                </button>
                            </div>
                        </div>
                        <small class="form-text text-muted" id="periodHint">请先选择收费项目</small>
                    </div>

                    <div class="form-group">
                        <label class="form-label required">截止日期</label>
                        <input type="date" class="form-control" id="billDueDate" name="dueDate" required>
                        <small class="form-text text-muted" id="gracePeriodHint"></small>
                    </div>

                    <div class="form-group">
                        <label class="form-label required">生成范围</label>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="scope" id="scopeAll" value="all" checked onchange="onScopeChange()">
                            <label class="form-check-label" for="scopeAll">
                                <i class="fas fa-building"></i> 所有已入住房屋
                            </label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="scope" id="scopeBuilding" value="building" onchange="onScopeChange()">
                            <label class="form-check-label" for="scopeBuilding">
                                <i class="fas fa-home"></i> 指定楼栋
                            </label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="scope" id="scopeCustom" value="custom" onchange="onScopeChange()">
                            <label class="form-check-label" for="scopeCustom">
                                <i class="fas fa-list"></i> 自定义房屋列表
                            </label>
                        </div>
                    </div>

                    <div class="form-group" id="buildingSelectGroup" style="display: none;">
                        <label class="form-label required">选择楼栋</label>
                        <select class="form-control" id="billBuildingId" onchange="estimateCount()">
                            <option value="">请选择楼栋</option>
                        </select>
                    </div>

                    <div class="form-group" id="customHousesGroup" style="display: none;">
                        <label class="form-label required">房屋编号</label>
                        <textarea class="form-control" id="billHouseIds" rows="3"
                                  placeholder="输入房屋编号,多个用逗号分隔,例如:0110301,0110302,0120401"
                                  oninput="estimateCount()"></textarea>
                        <small class="form-text text-muted">支持批量输入,用逗号或换行分隔</small>
                    </div>

                    <div class="alert alert-info-custom">
                        <i class="fas fa-info-circle"></i>
                        <strong>预计生成:</strong><span id="estimatedCount" class="text-primary font-weight-bold">0</span> 个房屋的账单
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-primary" onclick="generateBill()">
                    <i class="fas fa-check"></i> 确认生成
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 缴费模态框 -->
<div class="modal fade" id="payModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">处理缴费</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="payForm">
                    <input type="hidden" id="payRecordId">

                    <div class="form-group">
                        <label class="form-label">业主姓名</label>
                        <input type="text" class="form-control" id="payOwnerName" readonly>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">房屋编号</label>
                                <input type="text" class="form-control" id="payHouseId" readonly>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">收费项目</label>
                                <input type="text" class="form-control" id="payItemName" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-4">
                            <div class="form-group">
                                <label class="form-label">应缴金额</label>
                                <input type="text" class="form-control" id="payAmount" readonly>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="form-group">
                                <label class="form-label">滞纳金</label>
                                <input type="text" class="form-control" id="payLateFee" readonly>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="form-group">
                                <label class="form-label">合计金额</label>
                                <input type="text" class="form-control amount-text" id="payTotalAmount" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label required">缴费方式</label>
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
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-success" onclick="processPay()">
                    <i class="fas fa-check"></i> 确认缴费
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 查看收据模态框 -->
<div class="modal fade" id="receiptModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <h5 class="modal-title text-white">
                    <i class="fas fa-receipt"></i> 缴费收据
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" style="background-color: #f8f9fa;">
                <div class="receipt-container">
                    <div class="receipt-header">
                        <h3 class="receipt-title">
                            <i class="fas fa-file-invoice-dollar"></i> 物业缴费收据
                        </h3>
                        <div class="receipt-subtitle">Property Payment Receipt</div>
                    </div>

                    <div class="receipt-no-box">
                        <div class="receipt-no-label">收据编号 Receipt No.</div>
                        <div class="receipt-no-value" id="receiptNo">-</div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">
                                    <i class="fas fa-user"></i> 业主姓名
                                </div>
                                <div class="info-value" id="receiptOwnerName">-</div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">
                                    <i class="fas fa-home"></i> 房屋编号
                                </div>
                                <div class="info-value" id="receiptHouseId">-</div>
                            </div>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">
                                    <i class="fas fa-list-alt"></i> 收费项目
                                </div>
                                <div class="info-value" id="receiptItemName">-</div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">
                                    <i class="fas fa-calendar-alt"></i> 缴费期限
                                </div>
                                <div class="info-value" id="receiptBillingPeriod">-</div>
                            </div>
                        </div>
                    </div>

                    <div class="amount-box">
                        <div class="row">
                            <div class="col-4">
                                <div class="amount-item">
                                    <div class="amount-label">应缴金额</div>
                                    <div class="amount-value" style="color: #1976d2;" id="receiptAmount">¥0.00</div>
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="amount-item">
                                    <div class="amount-label">滞纳金</div>
                                    <div class="amount-value" style="color: #f57c00;" id="receiptLateFee">¥0.00</div>
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="amount-item">
                                    <div class="amount-label">合计金额</div>
                                    <div class="amount-value" style="color: #e91e63; font-size: 24px;" id="receiptTotalAmount">¥0.00</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">
                                    <i class="fas fa-credit-card"></i> 缴费方式
                                </div>
                                <div class="info-value" id="receiptPaymentMethod">-</div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">
                                    <i class="fas fa-clock"></i> 缴费时间
                                </div>
                                <div class="info-value" id="receiptPaymentDate">-</div>
                            </div>
                        </div>
                    </div>

                    <div class="receipt-footer">
                        <div class="row">
                            <div class="col-6">
                                <div class="receipt-footer-text">收款单位:XX物业管理公司</div>
                                <div class="receipt-footer-text" style="margin-top: 5px;">联系电话:400-XXX-XXXX</div>
                            </div>
                            <div class="col-6 text-right">
                                <div class="receipt-footer-text">
                                    <i class="fas fa-check-circle" style="color: #4caf50;"></i> 已缴费
                                </div>
                                <div class="receipt-footer-text" style="color: #999; font-size: 11px; margin-top: 5px;">
                                    此收据仅作为缴费凭证
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 关闭
                </button>
                <button type="button" class="btn btn-primary" onclick="printReceipt()">
                    <i class="fas fa-print"></i> 打印收据
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 缴费统计分析模态框 -->
<div class="modal fade" id="statisticsModal" tabindex="-1">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <h5 class="modal-title text-white">
                    <i class="fas fa-chart-bar"></i> 缴费统计分析
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" style="max-height: 75vh; overflow-y: auto; background-color: #f8f9fa;">
                <div id="statsContent">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 关闭
                </button>
                <button type="button" class="btn btn-primary" onclick="loadPaymentStatistics()">
                    <i class="fas fa-sync-alt"></i> 刷新数据
                </button>
            </div>
        </div>
    </div>
</div>
<!-- ✅ 缴费详情模态框 -->
<div class="modal fade" id="detailModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <h5 class="modal-title text-white">
                    <i class="fas fa-info-circle"></i> 缴费记录详情
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-hashtag"></i> 记录编号</div>
                            <div class="info-value" id="detailRecordId">-</div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-calendar-check"></i> 状态</div>
                            <div class="info-value" id="detailStatus">-</div>
                        </div>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-6">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-user"></i> 业主姓名</div>
                            <div class="info-value" id="detailOwnerName">-</div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-home"></i> 房屋编号</div>
                            <div class="info-value" id="detailHouseId">-</div>
                        </div>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-12">
                        <div class="info-item" style="background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);">
                            <div class="info-label"><i class="fas fa-list-alt"></i> 收费项目</div>
                            <div class="info-value" id="detailItemName" style="font-size: 18px; color: #1976d2;">-</div>
                        </div>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-4">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-calendar-alt"></i> 缴费期限</div>
                            <div class="info-value" id="detailBillingPeriod">-</div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-clock"></i> 截止日期</div>
                            <div class="info-value" id="detailDueDate">-</div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-hourglass-half"></i> 宽限期</div>
                            <div class="info-value" id="detailGracePeriod" style="line-height: 1.8;">-</div>
                        </div>

                    </div>
                </div>

                <div class="amount-box">
                    <div class="row">
                        <div class="col-4">
                            <div class="amount-item">
                                <div class="amount-label">应缴金额</div>
                                <div class="amount-value" style="color: #1976d2;" id="detailAmount">¥0.00</div>
                            </div>
                        </div>
                        <div class="col-4">
                            <div class="amount-item">
                                <div class="amount-label">滞纳金</div>
                                <div class="amount-value" style="color: #f57c00;" id="detailLateFee">¥0.00</div>
                                <small class="text-muted" id="detailLateFeeRate" style="display: block; margin-top: 5px;">-</small>
                            </div>
                        </div>
                        <div class="col-4">
                            <div class="amount-item">
                                <div class="amount-label">合计金额</div>
                                <div class="amount-value" style="color: #e91e63; font-size: 24px;" id="detailTotalAmount">¥0.00</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row mb-3" id="detailPaymentInfo" style="display: none;">
                    <div class="col-md-6">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-credit-card"></i> 缴费方式</div>
                            <div class="info-value" id="detailPaymentMethod">-</div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-calendar-check"></i> 缴费时间</div>
                            <div class="info-value" id="detailPaymentDate">-</div>
                        </div>
                    </div>
                </div>

                <div class="row" id="detailReceiptInfo" style="display: none;">
                    <div class="col-md-12">
                        <div class="info-item" style="background: #e8f5e9;">
                            <div class="info-label"><i class="fas fa-receipt"></i> 收据编号</div>
                            <div class="info-value" style="color: #388e3c;" id="detailReceiptNo">-</div>
                        </div>
                    </div>
                </div>

                <div class="row mt-3" id="detailRemark">
                    <div class="col-md-12">
                        <div class="info-item">
                            <div class="info-label"><i class="fas fa-comment"></i> 备注</div>
                            <div class="info-value" id="detailRemarkText" style="color: #666; font-size: 14px;">-</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 关闭
                </button>
                <button type="button" class="btn btn-primary" id="detailPayBtn" style="display: none;" onclick="payFromDetail()">
                    <i class="fas fa-hand-holding-usd"></i> 立即缴费
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 引入 JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    if (typeof jQuery == 'undefined') {
        document.write('<script src="https://cdn.staticfile.org/jquery/3.6.0/jquery.min.js"><\/script>');
    }
</script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/layer.js"></script>

<script type="text/javascript">
    // ===== 全局变量 =====
    var currentPage = 1;
    var pageSize = 10;
    var totalCount = 0;
    var selectedChargeItem = null;
    var buildings = [];
    var selectedRecords = [];
    var isSelectAllMode = false;
    var currentPageRecordIds = [];

    // ===== 页面加载完成后执行 =====
    $(document).ready(function() {
        console.log('✅ 缴费管理页面加载完成');

        // 初始化数据
        loadPaymentList(1);
        loadChargeItems();
        loadStatistics();
        loadBuildings();

        // 回车搜索
        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) {
                searchPayment();
            }
        });

        // ✅ 修改：筛选条件变化时自动搜索并刷新统计
        $('#statusFilter, #itemFilter').change(function() {
            console.log('🔍 筛选条件变化:', {
                status: $('#statusFilter').val(),
                itemId: $('#itemFilter').val()
            });
            searchPayment();
            loadStatistics();  // ✅ 确保调用统计刷新
        });
    });

    function loadBuildings() {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'GET',
            data: { method: 'listBuildings' },
            dataType: 'json',
            success: function(result) {
                console.log('楼栋列表响应:', result);
                if (result.success && result.data) {
                    buildings = result.data;
                    var select = $('#billBuildingId');
                    select.find('option:not(:first)').remove();

                    for (var i = 0; i < buildings.length; i++) {
                        var building = buildings[i];
                        select.append(
                            '<option value="' + building.buildingId + '">' +
                            building.buildingName +
                            ' (' + building.totalHouses + '套房屋,已入住' + building.occupiedCount + '套)' +
                            '</option>'
                        );
                    }
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载楼栋列表失败:', error);
            }
        });
    }

    function loadPaymentList(pageNum) {
        currentPage = pageNum || currentPage;
        var keyword = $('#searchKeyword').val();
        var status = $('#statusFilter').val();
        var itemId = $('#itemFilter').val();

        console.log('📥 加载缴费列表,页码:', currentPage, '收费项目:', itemId);

        $.ajax({
            url: '${pageContext.request.contextPath}/payment',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: keyword,
                status: status,
                itemId: itemId
            },
            dataType: 'json',
            success: function(result) {
                console.log('📦 缴费列表响应:', result);
                if (result.list) {
                    renderPaymentTable(result.list);
                    totalCount = result.total || 0;
                    renderPagination(result.pageNum, result.totalPages || result.pages);

                    // ✅ 加载完列表后刷新统计
                    loadStatistics();

                    if (isSelectAllMode) {
                        $('.record-checkbox').prop('checked', true);
                        updateSelection();
                    } else {
                        clearSelection();
                    }
                } else {
                    layer.msg('加载失败', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 请求失败:', error);
                layer.msg('网络错误', {icon: 2});
                $('#paymentTableBody').html(
                    '<tr><td colspan="12" class="text-center text-danger">加载失败,请刷新重试</td></tr>'
                );
            }
        });
    }

    function renderPaymentTable(payments) {
        var tbody = $('#paymentTableBody');
        tbody.empty();
        currentPageRecordIds = [];

        if (!payments || payments.length === 0) {
            tbody.append(
                '<tr><td colspan="12" class="text-center text-muted">暂无数据</td></tr>'
            );
            return;
        }

        for (var i = 0; i < payments.length; i++) {
            var payment = payments[i];
            currentPageRecordIds.push(payment.recordId);

            var statusClass = payment.paymentStatus === 'paid' ? 'status-paid' :
                payment.paymentStatus === 'overdue' ? 'status-overdue' : 'status-unpaid';
            var statusText = payment.paymentStatus === 'paid' ? '已缴费' :
                payment.paymentStatus === 'overdue' ? '已逾期' : '未缴费';

            var checkboxHtml = '<input type="checkbox" class="record-checkbox" value="' + payment.recordId + '" onchange="updateSelection()">';

            // ✅ 修改：所有记录都显示"查看详情"按钮
            var actionBtn = '<button class="btn btn-sm btn-info btn-action" onclick="viewDetail(\'' + payment.recordId + '\')">' +
                '<i class="fas fa-info-circle"></i> 详情</button>';

            if (payment.paymentStatus === 'paid') {
                actionBtn += '<button class="btn btn-sm btn-success btn-action" onclick="viewReceipt(\'' + payment.recordId + '\')">' +
                    '<i class="fas fa-receipt"></i> 收据</button>';
            } else {
                actionBtn += '<button class="btn btn-sm btn-success btn-action" onclick="showPayModal(\'' + payment.recordId + '\')">' +
                    '<i class="fas fa-hand-holding-usd"></i> 缴费</button>' +
                    '<button class="btn btn-sm btn-danger btn-action" onclick="deleteRecord(\'' + payment.recordId + '\')">' +
                    '<i class="fas fa-trash-alt"></i> 删除</button>';
            }

            tbody.append(
                '<tr>' +
                '<td class="checkbox-cell">' + checkboxHtml + '</td>' +
                '<td>' + payment.recordId + '</td>' +
                '<td>' + (payment.ownerName || '-') + '</td>' +
                '<td>' + (payment.houseId || '-') + '</td>' +
                '<td>' + (payment.itemName || '-') + '</td>' +
                '<td>' + (payment.billingPeriod || '-') + '</td>' +
                '<td class="amount-text">¥' + (payment.amount || 0).toFixed(2) + '</td>' +
                '<td class="text-danger">¥' + (payment.lateFee || 0).toFixed(2) + '</td>' +
                '<td class="amount-text"><strong>¥' + (payment.totalAmount || 0).toFixed(2) + '</strong></td>' +
                '<td>' + formatDate(payment.dueDate) + '</td>' +
                '<td><span class="status-badge ' + statusClass + '">' + statusText + '</span></td>' +
                '<td>' + actionBtn + '</td>' +
                '</tr>'
            );
        }
    }


    function loadStatistics() {
        var keyword = $('#searchKeyword').val();
        var status = $('#statusFilter').val();
        var itemId = $('#itemFilter').val();

        console.log('📊 加载统计数据,参数:', {
            keyword: keyword,
            status: status,
            itemId: itemId
        });

        $.ajax({
            url: '${pageContext.request.contextPath}/payment',
            type: 'GET',
            data: {
                method: 'statistics',
                keyword: keyword,
                status: status,
                itemId: itemId
            },
            dataType: 'json',
            success: function(result) {
                console.log('📊 统计数据响应:', result);
                if (result.success && result.data) {
                    $('#totalRecords').text(result.data.totalRecords || result.data.totalCount || 0);
                    $('#paidCount').text(result.data.paidCount || 0);
                    $('#unpaidCount').text(result.data.unpaidCount || 0);
                    $('#overdueCount').text(result.data.overdueCount || 0);

                    if (itemId) {
                        var itemName = $('#itemFilter option:selected').text();
                        console.log('✅ 已按收费项目筛选: ' + itemName);
                    }
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 统计数据加载失败:', error);
            }
        });
    }

    function renderPagination(current, total) {
        $('#pageInfo').text('共 ' + totalCount + ' 条记录,第 ' + current + '/' + total + ' 页');

        var pagination = $('#pagination');
        pagination.empty();

        if (total <= 1) return;

        pagination.append(
            '<li class="page-item ' + (current === 1 ? 'disabled' : '') + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="loadPaymentList(' + (current - 1) + ')">上一页</a>' +
            '</li>'
        );

        var startPage = Math.max(1, current - 2);
        var endPage = Math.min(total, current + 2);

        if (endPage - startPage < 4) {
            if (current <= 3) {
                endPage = Math.min(total, 5);
            } else {
                startPage = Math.max(1, endPage - 4);
            }
        }

        if (startPage > 1) {
            pagination.append(
                '<li class="page-item">' +
                '<a class="page-link" href="javascript:void(0)" onclick="loadPaymentList(1)">1</a>' +
                '</li>'
            );
            if (startPage > 2) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
        }

        for (var i = startPage; i <= endPage; i++) {
            pagination.append(
                '<li class="page-item ' + (i === current ? 'active' : '') + '">' +
                '<a class="page-link" href="javascript:void(0)" onclick="loadPaymentList(' + i + ')">' + i + '</a>' +
                '</li>'
            );
        }

        if (endPage < total) {
            if (endPage < total - 1) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
            pagination.append(
                '<li class="page-item">' +
                '<a class="page-link" href="javascript:void(0)" onclick="loadPaymentList(' + total + ')">' + total + '</a>' +
                '</li>'
            );
        }

        pagination.append(
            '<li class="page-item ' + (current === total ? 'disabled' : '') + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="loadPaymentList(' + (current + 1) + ')">下一页</a>' +
            '</li>'
        );
    }

    function loadChargeItems() {
        console.log('📥 开始加载收费项目');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/chargeItem',
            type: 'GET',
            data: { method: 'findActive' },
            dataType: 'json',
            success: function(result) {
                console.log('📦 收费项目响应:', result);

                if (result.success && result.data) {
                    var select1 = $('#billItemId');
                    var select2 = $('#itemFilter');

                    select1.find('option:not(:first)').remove();
                    select2.find('option:not(:first)').remove();

                    for (var i = 0; i < result.data.length; i++) {
                        var item = result.data[i];

                        var option1 = '<option value="' + item.itemId + '" ' +
                            'data-cycle="' + item.chargeCycle + '" ' +
                            'data-calculation="' + item.calculationType + '" ' +
                            'data-amount="' + item.fixedAmount + '" ' +
                            'data-grace="' + item.gracePeriod + '">' +
                            item.itemName +
                            '</option>';

                        var option2 = '<option value="' + item.itemId + '">' +
                            item.itemName +
                            '</option>';

                        select1.append(option1);
                        select2.append(option2);
                    }

                    console.log('✅ 收费项目加载完成,共 ' + result.data.length + ' 个');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载收费项目失败:', error);
            }
        });
    }

    function toggleSelectAll() {
        var checked = $('#selectAll').prop('checked');
        $('.record-checkbox').prop('checked', checked);

        if (checked && totalCount > currentPageRecordIds.length) {
            $('#currentPageCount').text(currentPageRecordIds.length);
            $('#totalFilteredCount').text(totalCount);
            $('#selectAllHint').addClass('show');
        } else {
            $('#selectAllHint').removeClass('show');
        }

        updateSelection();
    }

    function selectAllFiltered() {
        isSelectAllMode = true;

        var keyword = $('#searchKeyword').val();
        var status = $('#statusFilter').val();
        var itemId = $('#itemFilter').val();

        var loadingIndex = layer.msg('正在加载所有筛选结果...', {
            icon: 16,
            time: 0,
            shade: 0.3
        });

        $.ajax({
            url: '${pageContext.request.contextPath}/payment',
            type: 'GET',
            data: {
                method: 'export',
                keyword: keyword,
                status: status,
                itemId: itemId,
                onlyIds: true
            },
            dataType: 'json',
            success: function(result) {
                layer.close(loadingIndex);

                if (result.success && result.data && result.data.ids) {
                    selectedRecords = result.data.ids;
                } else {
                    selectedRecords = ['ALL_FILTERED'];
                }

                $('#selectAllHint').removeClass('show');
                $('#selectModeHint').show();
                $('#selectedCount').text(totalCount);
                $('#batchActions').addClass('show');

                $('.record-checkbox').prop('checked', true);
                $('#selectAll').prop('checked', true);

                layer.msg('已选择全部 ' + totalCount + ' 条筛选结果', {icon: 1});
            },
            error: function() {
                layer.close(loadingIndex);
                isSelectAllMode = true;
                selectedRecords = ['ALL_FILTERED'];

                $('#selectAllHint').removeClass('show');
                $('#selectModeHint').show();
                $('#selectedCount').text(totalCount);
                $('#batchActions').addClass('show');

                $('.record-checkbox').prop('checked', true);
                $('#selectAll').prop('checked', true);

                layer.msg('已选择全部 ' + totalCount + ' 条筛选结果', {icon: 1});
            }
        });
    }

    function cancelSelectAllFiltered() {
        isSelectAllMode = false;
        $('#selectAllHint').removeClass('show');
        $('#selectAll').prop('checked', false);
        $('.record-checkbox').prop('checked', false);
        updateSelection();
    }

    function updateSelection() {
        if (!isSelectAllMode) {
            selectedRecords = [];
            $('.record-checkbox:checked').each(function() {
                selectedRecords.push($(this).val());
            });

            $('#selectedCount').text(selectedRecords.length);
            $('#selectModeHint').hide();

            if (selectedRecords.length > 0) {
                $('#batchActions').addClass('show');

                if (selectedRecords.length === currentPageRecordIds.length && totalCount > currentPageRecordIds.length) {
                    $('#currentPageCount').text(currentPageRecordIds.length);
                    $('#totalFilteredCount').text(totalCount);
                    $('#selectAllHint').addClass('show');
                } else {
                    $('#selectAllHint').removeClass('show');
                }
            } else {
                $('#batchActions').removeClass('show');
                $('#selectAllHint').removeClass('show');
            }

            var totalCheckboxes = $('.record-checkbox').length;
            var checkedCheckboxes = $('.record-checkbox:checked').length;
            $('#selectAll').prop('checked', totalCheckboxes > 0 && totalCheckboxes === checkedCheckboxes);
        } else {
            $('#selectedCount').text(totalCount);
            $('#selectModeHint').show();
            $('#batchActions').addClass('show');
        }
    }

    function clearSelection() {
        isSelectAllMode = false;
        selectedRecords = [];
        $('.record-checkbox').prop('checked', false);
        $('#selectAll').prop('checked', false);
        $('#batchActions').removeClass('show');
        $('#selectAllHint').removeClass('show');
        $('#selectModeHint').hide();
    }

    function batchDelete() {
        if (selectedRecords.length === 0 && !isSelectAllMode) {
            layer.msg('请先选择要删除的记录', {icon: 0});
            return;
        }

        var hasPaidRecords = false;
        var paidCount = 0;

        $('.record-checkbox:checked').each(function() {
            var $row = $(this).closest('tr');
            var statusBadge = $row.find('.status-badge');

            if (statusBadge.hasClass('status-paid')) {
                hasPaidRecords = true;
                paidCount++;
            }
        });

        if (hasPaidRecords) {
            layer.msg('选中的记录中包含 ' + paidCount + ' 条已缴费记录，不能删除！', {
                icon: 0,
                time: 3000
            });
            return;
        }

        var deleteCount = isSelectAllMode ? totalCount : selectedRecords.length;
        var modeText = isSelectAllMode ? '（全选模式：所有筛选结果）' : '';

        layer.confirm(
            '确定要删除选中的 <strong class="text-danger">' + deleteCount + '</strong> 条未缴费记录吗?' + modeText + '<br>' +
            '<small class="text-warning">此操作不可恢复!</small>',
            {
                icon: 3,
                title: '批量删除确认',
                btn: ['确定删除', '取消']
            },
            function(index) {
                var loadingIndex = layer.msg('正在删除,请稍候...', {
                    icon: 16,
                    time: 0,
                    shade: 0.3
                });

                var recordIds = isSelectAllMode ? 'ALL_FILTERED' : selectedRecords.join(',');
                var keyword = $('#searchKeyword').val();
                var status = $('#statusFilter').val();
                var itemId = $('#itemFilter').val();

                $.ajax({
                    url: '${pageContext.request.contextPath}/payment',
                    type: 'POST',
                    data: {
                        method: 'batchDelete',
                        recordIds: recordIds,
                        keyword: keyword,
                        status: status,
                        itemId: itemId
                    },
                    dataType: 'json',
                    success: function(result) {
                        layer.close(loadingIndex);
                        if (result.success) {
                            layer.msg('删除成功!', {icon: 1});
                            loadPaymentList(currentPage);
                            clearSelection();
                        } else {
                            layer.msg(result.message || '删除失败', {icon: 2});
                        }
                    },
                    error: function() {
                        layer.close(loadingIndex);
                        layer.msg('网络错误', {icon: 2});
                    }
                });
                layer.close(index);
            }
        );
    }

    function batchExport() {
        if (selectedRecords.length === 0 && !isSelectAllMode) {
            layer.msg('请先选择要导出的记录', {icon: 0});
            return;
        }

        var exportCount = isSelectAllMode ? totalCount : selectedRecords.length;

        if (isSelectAllMode) {
            var keyword = $('#searchKeyword').val();
            var status = $('#statusFilter').val();
            var itemId = $('#itemFilter').val();

            var params = [];
            if (keyword) params.push('keyword=' + encodeURIComponent(keyword));
            if (status) params.push('status=' + encodeURIComponent(status));
            if (itemId) params.push('itemId=' + encodeURIComponent(itemId));

            var url = '${pageContext.request.contextPath}/payment?method=export';
            if (params.length > 0) {
                url += '&' + params.join('&');
            }

            console.log('导出全部筛选结果URL:', url);
            layer.msg('正在导出全部 ' + exportCount + ' 条记录...', {icon: 16, time: 2000});

            var iframe = document.createElement('iframe');
            iframe.style.display = 'none';
            iframe.src = url;
            document.body.appendChild(iframe);

            setTimeout(function() {
                document.body.removeChild(iframe);
            }, 3000);
        } else {
            var url = '${pageContext.request.contextPath}/payment?method=export&recordIds=' + selectedRecords.join(',');

            console.log('导出选中记录URL:', url);
            layer.msg('正在导出 ' + exportCount + ' 条记录...', {icon: 16, time: 2000});

            var iframe = document.createElement('iframe');
            iframe.style.display = 'none';
            iframe.src = url;
            document.body.appendChild(iframe);

            setTimeout(function() {
                document.body.removeChild(iframe);
            }, 3000);
        }
    }

    function deleteRecord(recordId) {
        layer.confirm(
            '确定要删除这条未缴费记录吗?<br><small class="text-warning">此操作不可恢复!</small>',
            {
                icon: 3,
                title: '删除确认'
            },
            function(index) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/payment',
                    type: 'POST',
                    data: {
                        method: 'delete',
                        recordId: recordId
                    },
                    dataType: 'json',
                    success: function(result) {
                        if (result.success) {
                            layer.msg('删除成功!', {icon: 1});
                            loadPaymentList(currentPage);
                        } else {
                            layer.msg(result.message || '删除失败', {icon: 2});
                        }
                    },
                    error: function() {
                        layer.msg('网络错误', {icon: 2});
                    }
                });
                layer.close(index);
            }
        );
    }

    function searchPayment() {
        console.log('🔍 执行搜索');
        currentPage = 1;
        clearSelection();
        loadPaymentList(1);
    }

    function resetSearch() {
        console.log('🔄 重置搜索条件');
        $('#searchKeyword').val('');
        $('#statusFilter').val('');
        $('#itemFilter').val('');
        currentPage = 1;
        clearSelection();
        loadPaymentList(1);
    }

    function showGenerateBillModal() {
        $('#generateBillForm')[0].reset();
        $('#scopeAll').prop('checked', true);
        $('#buildingSelectGroup').hide();
        $('#customHousesGroup').hide();
        $('#itemInfo').text('');
        $('#periodHint').text('请先选择收费项目');
        $('#gracePeriodHint').text('');
        $('#estimatedCount').text('0');
        selectedChargeItem = null;

        var now = new Date();
        var nextMonth = new Date(now.getFullYear(), now.getMonth() + 1, 15);
        var dueDateStr = nextMonth.getFullYear() + '-' +
            pad(nextMonth.getMonth() + 1) + '-' +
            pad(nextMonth.getDate());
        $('#billDueDate').val(dueDateStr);

        $('#generateBillModal').modal('show');
        estimateCount();
    }

    function onChargeItemChange() {
        var selected = $('#billItemId option:selected');

        if (!selected.val()) {
            selectedChargeItem = null;
            $('#itemInfo').text('');
            $('#periodHint').text('请先选择收费项目');
            $('#gracePeriodHint').text('');
            $('#billPeriod').val('');
            return;
        }

        selectedChargeItem = {
            itemId: selected.val(),
            itemName: selected.text(),
            chargeCycle: selected.data('cycle'),
            calculationType: selected.data('calculation'),
            fixedAmount: selected.data('amount'),
            gracePeriod: selected.data('grace')
        };

        console.log('选中的收费项目:', selectedChargeItem);

        var calculationText = selectedChargeItem.calculationType === 'area_based' ?
            '按面积计费:' + selectedChargeItem.fixedAmount + '元/㎡' :
            '固定金额:' + selectedChargeItem.fixedAmount + '元';

        $('#itemInfo').text(
            getChargeCycleText(selectedChargeItem.chargeCycle) + ' | ' +
            calculationText + ' | ' +
            '宽限期:' + selectedChargeItem.gracePeriod + '天'
        );

        updatePeriodHint();
        $('#gracePeriodHint').text(
            '宽限期:' + selectedChargeItem.gracePeriod + '天(截止日期后开始计算)'
        );
        $('#billPeriod').val('');
    }

    function updatePeriodHint() {
        if (!selectedChargeItem) {
            $('#periodHint').text('请先选择收费项目');
            return;
        }

        var hints = {
            'monthly': '格式:YYYY年MM月,例如:2026年2月',
            'quarterly': '格式:YYYY年第Q季度,例如:2026年第1季度',
            'yearly': '格式:YYYY年度,例如:2026年度',
            'once': '格式:自定义,例如:装修押金'
        };

        $('#periodHint').text(hints[selectedChargeItem.chargeCycle] || '请输入账期');
    }

    function autoGeneratePeriod() {
        if (!selectedChargeItem) {
            layer.msg('请先选择收费项目', {icon: 0});
            return;
        }

        var now = new Date();
        var year = now.getFullYear();
        var month = now.getMonth() + 1;
        var quarter = Math.ceil(month / 3);

        var period = '';
        switch(selectedChargeItem.chargeCycle) {
            case 'monthly':
                period = year + '年' + month + '月';
                break;
            case 'quarterly':
                period = year + '年第' + quarter + '季度';
                break;
            case 'yearly':
                period = year + '年度';
                break;
            default:
                period = year + '年' + month + '月';
        }

        $('#billPeriod').val(period);
        layer.msg('已自动生成账期', {icon: 1, time: 1000});
    }

    function getChargeCycleText(cycle) {
        var map = {
            'monthly': '月度',
            'quarterly': '季度',
            'yearly': '年度',
            'once': '一次性'
        };
        return map[cycle] || cycle;
    }

    function onScopeChange() {
        var scope = $('input[name="scope"]:checked').val();

        if (scope === 'building') {
            $('#buildingSelectGroup').show();
            $('#customHousesGroup').hide();
        } else if (scope === 'custom') {
            $('#buildingSelectGroup').hide();
            $('#customHousesGroup').show();
        } else {
            $('#buildingSelectGroup').hide();
            $('#customHousesGroup').hide();
        }

        estimateCount();
    }

    function estimateCount() {
        var scope = $('input[name="scope"]:checked').val();

        if (scope === 'custom') {
            var houseIds = $('#billHouseIds').val().trim();
            if (!houseIds) {
                $('#estimatedCount').text('0');
                return;
            }

            var ids = houseIds.split(/[,\n]+/).filter(function(id) {
                return id.trim().length > 0;
            });

            $('#estimatedCount').text(ids.length);

        } else {
            var buildingId = scope === 'building' ? $('#billBuildingId').val() : '';

            $.ajax({
                url: '${pageContext.request.contextPath}/admin/house',
                type: 'GET',
                data: {
                    method: 'countOccupied',
                    buildingId: buildingId
                },
                dataType: 'json',
                success: function(result) {
                    if (result.success) {
                        $('#estimatedCount').text(result.data || 0);
                    }
                },
                error: function() {
                    $('#estimatedCount').text('?');
                }
            });
        }
    }

    function generateBill() {
        var form = $('#generateBillForm')[0];
        if (!form.checkValidity()) {
            layer.msg('请填写完整信息', {icon: 0});
            return;
        }

        var itemId = $('#billItemId').val();
        var billingPeriod = $('#billPeriod').val().trim();
        var dueDate = $('#billDueDate').val();
        var scope = $('input[name="scope"]:checked').val();
        var buildingId = scope === 'building' ? $('#billBuildingId').val() : '';
        var houseIds = scope === 'custom' ? $('#billHouseIds').val().trim().replace(/\n/g, ',') : '';

        if (!itemId) {
            layer.msg('请选择收费项目', {icon: 0});
            return;
        }
        if (!billingPeriod) {
            layer.msg('请输入账期', {icon: 0});
            return;
        }
        if (!dueDate) {
            layer.msg('请选择截止日期', {icon: 0});
            return;
        }

        if (scope === 'building' && !buildingId) {
            layer.msg('请选择楼栋', {icon: 0});
            return;
        }

        if (scope === 'custom' && !houseIds) {
            layer.msg('请输入房屋编号', {icon: 0});
            return;
        }

        var estimatedCount = $('#estimatedCount').text();

        console.log('生成账单参数:', {
            itemId: itemId,
            billingPeriod: billingPeriod,
            dueDate: dueDate,
            scope: scope,
            buildingId: buildingId,
            houseIds: houseIds,
            estimatedCount: estimatedCount
        });

        layer.confirm(
            '确定要为 <strong class="text-primary">' + estimatedCount + '</strong> 个房屋生成账单吗?<br>' +
            '<small class="text-muted">账期:' + billingPeriod + '</small>',
            {
                icon: 3,
                title: '确认生成'
            },
            function(index) {
                var loadingIndex = layer.msg('正在生成账单,请稍候...', {
                    icon: 16,
                    time: 0,
                    shade: 0.3
                });

                $.ajax({
                    url: '${pageContext.request.contextPath}/payment',
                    type: 'POST',
                    data: {
                        method: 'generateBill',
                        itemId: itemId,
                        billingPeriod: billingPeriod,
                        dueDate: dueDate,
                        buildingId: buildingId,
                        houseIds: houseIds
                    },
                    dataType: 'json',
                    success: function(result) {
                        layer.close(loadingIndex);
                        console.log('生成账单响应:', result);

                        if (result.success) {
                            var data = result.data || {};
                            var message =
                                '<div style="text-align: left; line-height: 2;">' +
                                '<p><strong>生成结果:</strong></p>' +
                                '<p>总数:<span class="text-primary">' + (data.totalCount || 0) + '</span></p>' +
                                '<p>成功:<span class="text-success">' + (data.successCount || 0) + '</span></p>' +
                                '<p>失败:<span class="text-danger">' + (data.failCount || 0) + '</span></p>' +
                                (data.failCount > 0 ? '<p class="text-warning"><small>失败原因:可能已存在相同账期的账单</small></p>' : '') +
                                '</div>';

                            layer.alert(message, {
                                icon: 1,
                                title: '生成完成',
                                btn: ['确定']
                            });

                            $('#generateBillModal').modal('hide');
                            loadPaymentList(currentPage);
                        } else {
                            layer.msg(result.message || '生成失败', {icon: 2});
                        }
                    },
                    error: function(xhr, status, error) {
                        layer.close(loadingIndex);
                        console.error('生成账单失败:', error);
                        console.error('响应内容:', xhr.responseText);
                        layer.msg('网络错误:' + error, {icon: 2});
                    }
                });
                layer.close(index);
            }
        );
    }

    function showPayModal(recordId) {
        $.ajax({
            url: '${pageContext.request.contextPath}/payment',
            type: 'GET',
            data: {
                method: 'findById',
                recordId: recordId
            },
            dataType: 'json',
            success: function(result) {
                if (result.success && result.data) {
                    var payment = result.data;
                    $('#payRecordId').val(payment.recordId);
                    $('#payOwnerName').val(payment.ownerName);
                    $('#payHouseId').val(payment.houseId);
                    $('#payItemName').val(payment.itemName);
                    $('#payAmount').val('¥' + payment.amount.toFixed(2));
                    $('#payLateFee').val('¥' + payment.lateFee.toFixed(2));
                    $('#payTotalAmount').val('¥' + payment.totalAmount.toFixed(2));
                    $('#paymentMethod').val('');

                    $('#payModal').modal('show');
                } else {
                    layer.msg('查询失败', {icon: 2});
                }
            }
        });
    }

    function processPay() {
        var paymentMethod = $('#paymentMethod').val();
        if (!paymentMethod) {
            layer.msg('请选择缴费方式', {icon: 0});
            return;
        }

        var recordId = $('#payRecordId').val();

        layer.confirm('确认收到业主缴费吗?', {
            icon: 3,
            title: '确认缴费'
        }, function(index) {
            $.ajax({
                url: '${pageContext.request.contextPath}/payment',
                type: 'POST',
                data: {
                    method: 'pay',
                    recordId: recordId,
                    paymentMethod: paymentMethod
                },
                dataType: 'json',
                success: function(result) {
                    if (result.success) {
                        var receiptNo = result.data ? result.data.receiptNo : '';
                        layer.msg('缴费成功!' + (receiptNo ? '收据号:' + receiptNo : ''), {icon: 1, time: 3000});
                        $('#payModal').modal('hide');
                        loadPaymentList(currentPage);
                    } else {
                        layer.msg(result.message || '缴费失败', {icon: 2});
                    }
                },
                error: function() {
                    layer.msg('网络错误', {icon: 2});
                }
            });
            layer.close(index);
        });
    }

    function viewReceipt(recordId) {
        $.ajax({
            url: '${pageContext.request.contextPath}/payment',
            type: 'GET',
            data: {
                method: 'findById',
                recordId: recordId
            },
            dataType: 'json',
            success: function(result) {
                if (result.success && result.data) {
                    var payment = result.data;

                    $('#receiptNo').text(payment.receiptNo || '-');
                    $('#receiptOwnerName').text(payment.ownerName || '-');
                    $('#receiptHouseId').text(payment.houseId || '-');
                    $('#receiptItemName').text(payment.itemName || '-');
                    $('#receiptBillingPeriod').text(payment.billingPeriod || '-');
                    $('#receiptAmount').text('¥' + (payment.amount || 0).toFixed(2));
                    $('#receiptLateFee').text('¥' + (payment.lateFee || 0).toFixed(2));
                    $('#receiptTotalAmount').text('¥' + (payment.totalAmount || 0).toFixed(2));
                    $('#receiptPaymentMethod').text(getPaymentMethodText(payment.paymentMethod));
                    $('#receiptPaymentDate').text(formatDateTime(payment.paymentDate));

                    $('#receiptModal').modal('show');
                } else {
                    layer.msg('查询失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    function printReceipt() {
        var printContent = $('.receipt-container').html();
        var printWindow = window.open('', '_blank');
        printWindow.document.write('<html><head><title>打印收据</title>');
        printWindow.document.write('<style>');
        printWindow.document.write('body { font-family: "Microsoft YaHei", Arial, sans-serif; padding: 20px; }');
        printWindow.document.write('.receipt-container { max-width: 800px; margin: 0 auto; }');
        printWindow.document.write('.receipt-header { text-align: center; border-bottom: 2px solid #667eea; padding-bottom: 20px; margin-bottom: 20px; }');
        printWindow.document.write('.receipt-title { color: #667eea; font-weight: bold; font-size: 24px; margin-bottom: 10px; }');
        printWindow.document.write('.receipt-subtitle { color: #666; font-size: 14px; }');
        printWindow.document.write('.receipt-no-box { background: #667eea; color: white; padding: 15px; border-radius: 8px; text-align: center; margin-bottom: 20px; }');
        printWindow.document.write('.receipt-no-label { font-size: 12px; opacity: 0.9; }');
        printWindow.document.write('.receipt-no-value { font-size: 24px; font-weight: bold; letter-spacing: 2px; margin-top: 5px; }');
        printWindow.document.write('.info-item { background: #f8f9fa; padding: 12px; border-radius: 6px; margin-bottom: 10px; }');
        printWindow.document.write('.info-label { color: #666; font-size: 12px; margin-bottom: 5px; }');
        printWindow.document.write('.info-value { font-size: 16px; font-weight: 600; color: #333; }');
        printWindow.document.write('.amount-box { background: #e3f2fd; padding: 20px; border-radius: 8px; margin-bottom: 20px; }');
        printWindow.document.write('.amount-item { text-align: center; }');
        printWindow.document.write('.amount-label { color: #666; font-size: 12px; margin-bottom: 5px; }');
        printWindow.document.write('.amount-value { font-size: 20px; font-weight: bold; }');
        printWindow.document.write('.receipt-footer { border-top: 2px dashed #ddd; padding-top: 20px; margin-top: 20px; }');
        printWindow.document.write('.receipt-footer-text { color: #666; font-size: 12px; }');
        printWindow.document.write('.row { display: flex; margin: 0 -15px; }');
        printWindow.document.write('.col-4, .col-6 { flex: 0 0 auto; padding: 0 15px; }');
        printWindow.document.write('.col-4 { width: 33.333%; }');
        printWindow.document.write('.col-6 { width: 50%; }');
        printWindow.document.write('.text-right { text-align: right; }');
        printWindow.document.write('</style>');
        printWindow.document.write('</head><body>');
        printWindow.document.write('<div class="receipt-container">' + printContent + '</div>');
        printWindow.document.write('</body></html>');
        printWindow.document.close();

        setTimeout(function() {
            printWindow.print();
        }, 250);
    }

    function exportExcel() {
        var keyword = $('#searchKeyword').val();
        var status = $('#statusFilter').val();
        var itemId = $('#itemFilter').val();

        var params = [];
        if (keyword) params.push('keyword=' + encodeURIComponent(keyword));
        if (status) params.push('status=' + encodeURIComponent(status));
        if (itemId) params.push('itemId=' + encodeURIComponent(itemId));

        var url = '${pageContext.request.contextPath}/payment?method=export';
        if (params.length > 0) {
            url += '&' + params.join('&');
        }

        console.log('导出URL:', url);
        layer.msg('正在导出数据,请稍候...', {icon: 16, time: 2000});

        var iframe = document.createElement('iframe');
        iframe.style.display = 'none';
        iframe.src = url;
        document.body.appendChild(iframe);

        setTimeout(function() {
            document.body.removeChild(iframe);
        }, 3000);
    }
    /**
     * ✅ 查看缴费记录详情（完整版）
     */
    function viewDetail(recordId) {
        console.log('📋 查看详情，记录ID:', recordId);

        $.ajax({
            url: '${pageContext.request.contextPath}/payment',
            type: 'GET',
            data: {
                method: 'findById',
                recordId: recordId
            },
            dataType: 'json',
            success: function(result) {
                console.log('📦 详情数据返回:', result);

                if (result.success && result.data) {
                    var payment = result.data;

                    console.log('📋 缴费详情数据:', payment);

                    // ========== 基本信息 ==========
                    $('#detailRecordId').text(payment.recordId || '-');
                    $('#detailOwnerName').text(payment.ownerName || '-');
                    $('#detailHouseId').text(payment.houseId || '-');
                    $('#detailItemName').text(payment.itemName || '-');
                    $('#detailBillingPeriod').text(payment.billingPeriod || '-');
                    $('#detailDueDate').text(formatDate(payment.dueDate));

                    // ========== 宽限期（从收费项目获取） ==========
                    var gracePeriod = parseInt(payment.gracePeriod || 0);
                    $('#detailGracePeriod').text(gracePeriod + ' 天');
                    console.log('✅ 宽限期:', gracePeriod, '天');

                    // ========== 金额信息 ==========
                    var amount = parseFloat(payment.amount || 0);
                    var lateFee = parseFloat(payment.lateFee || 0);
                    var totalAmount = parseFloat(payment.totalAmount || (amount + lateFee));

                    $('#detailAmount').text('¥' + amount.toFixed(2));
                    $('#detailLateFee').text('¥' + lateFee.toFixed(2));
                    $('#detailTotalAmount').text('¥' + totalAmount.toFixed(2));

                    console.log('💰 金额信息:', {
                        amount: amount,
                        lateFee: lateFee,
                        totalAmount: totalAmount
                    });

                    // ========== 滞纳金比例和逾期天数 ==========
                    var lateFeeRate = parseFloat(payment.lateFeeRate || 0);
                    var overdueDays = parseInt(payment.overdueDays || 0);

                    console.log('📊 滞纳金信息:', {
                        lateFeeRate: lateFeeRate,
                        overdueDays: overdueDays,
                        lateFee: lateFee
                    });

                    // ✅ 显示滞纳金比例和逾期天数
                    if (lateFee > 0 && lateFeeRate > 0) {
                        // 有滞纳金且有比例
                        var ratePercent = (lateFeeRate * 100).toFixed(2);
                        var lateFeeInfo = '滞纳金比例: ' + ratePercent + '% / 天';

                        if (overdueDays > 0) {
                            lateFeeInfo += ' (已逾期 ' + overdueDays + ' 天)';
                        }

                        $('#detailLateFeeRate').text(lateFeeInfo).show();
                        console.log('✅ 显示滞纳金信息:', lateFeeInfo);

                    } else if (lateFeeRate > 0) {
                        // 有比例但无滞纳金（未逾期或在宽限期内）
                        var ratePercent = (lateFeeRate * 100).toFixed(2);
                        $('#detailLateFeeRate').text('滞纳金比例: ' + ratePercent + '% / 天').show();
                        console.log('✅ 显示滞纳金比例:', ratePercent + '%');

                    } else {
                        // 无滞纳金比例
                        $('#detailLateFeeRate').text('无滞纳金').show();
                        console.log('⚠️ 无滞纳金');
                    }

                    // ========== 状态显示 ==========
                    var statusClass = payment.paymentStatus === 'paid' ? 'badge-success' :
                        payment.paymentStatus === 'overdue' ? 'badge-danger' : 'badge-warning';
                    var statusText = payment.paymentStatus === 'paid' ? '已缴费' :
                        payment.paymentStatus === 'overdue' ? '已逾期' : '未缴费';

                    var statusHtml = '<span class="badge ' + statusClass + '">' + statusText + '</span>';

                    // ✅ 如果逾期，在状态后显示逾期天数
                    if (payment.paymentStatus === 'overdue' && overdueDays > 0) {
                        statusHtml += ' <small class="text-danger">(逾期 ' + overdueDays + ' 天)</small>';
                    }

                    $('#detailStatus').html(statusHtml);

                    // ========== 宽限期结束日期（可选显示） ==========
                    if (payment.graceDueDate) {
                        var graceDueDate = formatDate(payment.graceDueDate);
                        $('#detailGracePeriod').html(
                            gracePeriod + ' 天<br>' +
                            '<small class="text-muted">宽限至: ' + graceDueDate + '</small>'
                        );
                        console.log('✅ 宽限期结束日期:', graceDueDate);
                    } else if (gracePeriod > 0 && payment.dueDate) {
                        // 如果后端没返回，前端计算
                        var dueDate = new Date(payment.dueDate);
                        dueDate.setDate(dueDate.getDate() + gracePeriod);
                        var graceDueDate = formatDate(dueDate);
                        $('#detailGracePeriod').html(
                            gracePeriod + ' 天<br>' +
                            '<small class="text-muted">宽限至: ' + graceDueDate + '</small>'
                        );
                        console.log('✅ 计算宽限期结束日期:', graceDueDate);
                    }

                    // ========== 缴费信息（仅已缴费显示） ==========
                    if (payment.paymentStatus === 'paid') {
                        $('#detailPaymentInfo').show();
                        $('#detailReceiptInfo').show();
                        $('#detailPayBtn').hide();

                        $('#detailPaymentMethod').text(getPaymentMethodText(payment.paymentMethod));
                        $('#detailPaymentDate').text(formatDateTime(payment.paymentDate));
                        $('#detailReceiptNo').text(payment.receiptNo || '-');
                    } else {
                        $('#detailPaymentInfo').hide();
                        $('#detailReceiptInfo').hide();
                        $('#detailPayBtn').show().data('recordId', recordId);
                    }

                    // ========== 备注 ==========
                    if (payment.remark && payment.remark.trim() !== '') {
                        $('#detailRemark').show();
                        $('#detailRemarkText').text(payment.remark);
                    } else {
                        $('#detailRemark').hide();
                    }

                    // ========== 显示模态框 ==========
                    $('#detailModal').modal('show');
                    console.log('✅ 详情模态框已显示');

                } else {
                    console.error('❌ 查询失败:', result.message);
                    layer.msg(result.message || '查询失败', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 查询详情失败:', error);
                console.error('响应状态:', xhr.status);
                console.error('响应内容:', xhr.responseText);
                layer.msg('网络错误', {icon: 2});
            }
        });
    }


    /**
     * ✅ 从详情页直接跳转到缴费
     */
    function payFromDetail() {
        var recordId = $('#detailPayBtn').data('recordId');
        $('#detailModal').modal('hide');
        setTimeout(function() {
            showPayModal(recordId);
        }, 300);
    }

    function showStatisticsModal() {
        $('#statisticsModal').modal('show');
        loadPaymentStatistics();
    }

    function loadPaymentStatistics() {
        var keyword = $('#searchKeyword').val();
        var status = $('#statusFilter').val();
        var itemId = $('#itemFilter').val();

        $('#statsContent').html('<div class="text-center p-5"><i class="fas fa-spinner fa-spin fa-3x text-primary"></i><p class="mt-3">正在加载统计数据...</p></div>');

        $.ajax({
            url: '${pageContext.request.contextPath}/payment',
            type: 'GET',
            data: {
                method: 'statistics',
                keyword: keyword,
                status: status,
                itemId: itemId
            },
            dataType: 'json',
            success: function(result) {
                console.log('统计数据返回:', result);

                if (itemId) {
                    var itemName = $('#itemFilter option:selected').text();
                    console.log('✅ 统计分析按收费项目筛选: ' + itemName);
                }

                if (result.success && result.data) {
                    renderPaymentStatistics(result.data);
                } else {
                    $('#statsContent').html('<div class="alert alert-danger"><i class="fas fa-exclamation-circle"></i> 加载失败: ' + (result.message || '未知错误') + '</div>');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 统计数据加载失败:', error);
                console.error('响应状态:', xhr.status);
                console.error('响应内容:', xhr.responseText);
                $('#statsContent').html('<div class="alert alert-danger"><i class="fas fa-exclamation-circle"></i> 网络错误<br><small>' + xhr.responseText + '</small></div>');
            }
        });
    }

    function renderPaymentStatistics(data) {
        console.log('=== 开始渲染统计数据 ===');
        console.log('原始数据:', data);

        var itemId = $('#itemFilter').val();
        var itemName = itemId ? $('#itemFilter option:selected').text() : '';
        var filterInfo = '';

        if (itemName) {
            filterInfo = '<div class="alert alert-info mb-3">' +
                '<i class="fas fa-filter"></i> <strong>当前筛选:</strong> ' + itemName +
                '</div>';
        }

        var totalCount = parseInt(data.totalCount || data.totalRecords || 0);
        var paidCount = parseInt(data.paidCount || 0);
        var unpaidCount = parseInt(data.unpaidCount || 0);
        var overdueCount = parseInt(data.overdueCount || 0);

        var totalAmount = parseFloat(data.totalAmount || 0);
        var paidAmount = parseFloat(data.paidAmount || 0);
        var unpaidAmount = parseFloat(data.unpaidAmount || 0);
        var overdueAmount = parseFloat(data.overdueAmount || 0);

        var paymentRate = totalCount > 0 ? ((paidCount / totalCount) * 100).toFixed(2) : '0.00';
        var collectionRate = totalAmount > 0 ? ((paidAmount / totalAmount) * 100).toFixed(2) : '0.00';

        var html = filterInfo +
            '<div class="row mb-4">' +
            '<div class="col-md-6"><div class="metric-card"><div class="metric-icon bg-gradient-primary"><i class="fas fa-percentage"></i></div>' +
            '<div class="metric-info"><h3 class="metric-value">' + paymentRate + '%</h3><p class="metric-label">账单缴费完成率</p>' +
            '<small class="text-muted">已缴 ' + paidCount + ' / 总计 ' + totalCount + ' 笔</small></div></div></div>' +
            '<div class="col-md-6"><div class="metric-card"><div class="metric-icon bg-gradient-success"><i class="fas fa-chart-line"></i></div>' +
            '<div class="metric-info"><h3 class="metric-value">' + collectionRate + '%</h3><p class="metric-label">金额收缴率</p>' +
            '<small class="text-muted">实收 ¥' + paidAmount.toFixed(2) + ' / 应收 ¥' + totalAmount.toFixed(2) + '</small></div></div></div></div>' +
            '<div class="row mb-4"><div class="col-md-12"><div class="detail-card"><h5 class="card-title"><i class="fas fa-money-bill-wave"></i> 金额统计明细</h5>' +
            '<table class="table table-bordered table-hover"><thead class="thead-light"><tr><th width="25%">统计项</th><th width="25%" class="text-right">金额(元)</th>' +
            '<th width="25%">占比</th><th width="25%" class="text-center">状态</th></tr></thead><tbody>' +
            '<tr><td><strong>应收总额</strong></td><td class="text-right amount-text">¥' + totalAmount.toFixed(2) + '</td><td>100%</td>' +
            '<td class="text-center"><span class="badge badge-primary">基准值</span></td></tr>' +
            '<tr class="table-success"><td><strong>已缴金额</strong></td><td class="text-right text-success"><strong>¥' + paidAmount.toFixed(2) + '</strong></td>' +
            '<td><strong>' + collectionRate + '%</strong></td><td class="text-center"><span class="badge badge-success"><i class="fas fa-check"></i> 已收</span></td></tr>' +
            '<tr class="table-warning"><td><strong>未缴金额</strong></td><td class="text-right text-danger"><strong>¥' + unpaidAmount.toFixed(2) + '</strong></td>' +
            '<td><strong>' + (100 - parseFloat(collectionRate)).toFixed(2) + '%</strong></td>' +
            '<td class="text-center"><span class="badge badge-warning"><i class="fas fa-clock"></i> 待收</span></td></tr>' +
            '<tr class="table-danger"><td><strong>逾期金额</strong></td><td class="text-right text-danger">¥' + overdueAmount.toFixed(2) + '</td><td>-</td>' +
            '<td class="text-center"><span class="badge badge-danger"><i class="fas fa-exclamation-triangle"></i> 逾期</span></td></tr></tbody></table></div></div></div>' +
            '<div class="row mb-4"><div class="col-md-12"><div class="detail-card"><h5 class="card-title"><i class="fas fa-chart-pie"></i> 账单状态分布</h5>' +
            '<table class="table table-bordered"><thead class="thead-light"><tr><th width="25%">状态</th><th width="25%" class="text-right">数量(笔)</th>' +
            '<th width="25%">占比</th><th width="25%">进度</th></tr></thead><tbody>' +
            '<tr><td><span class="badge badge-success">已缴费</span></td><td class="text-right"><strong>' + paidCount + '</strong></td>' +
            '<td><strong>' + paymentRate + '%</strong></td><td><div class="progress" style="height: 20px;">' +
            '<div class="progress-bar bg-success" style="width: ' + paymentRate + '%">' + paymentRate + '%</div></div></td></tr>' +
            '<tr><td><span class="badge badge-warning">未缴费</span></td><td class="text-right">' + unpaidCount + '</td>' +
            '<td>' + (totalCount > 0 ? ((unpaidCount / totalCount) * 100).toFixed(2) : '0.00') + '%</td>' +
            '<td><div class="progress" style="height: 20px;"><div class="progress-bar bg-warning" style="width: ' +
            (totalCount > 0 ? ((unpaidCount / totalCount) * 100).toFixed(2) : 0) + '%">' +
            (totalCount > 0 ? ((unpaidCount / totalCount) * 100).toFixed(2) : '0.00') + '%</div></div></td></tr>' +
            '<tr><td><span class="badge badge-danger">已逾期</span></td><td class="text-right">' + overdueCount + '</td>' +
            '<td>' + (totalCount > 0 ? ((overdueCount / totalCount) * 100).toFixed(2) : '0.00') + '%</td>' +
            '<td><div class="progress" style="height: 20px;"><div class="progress-bar bg-danger" style="width: ' +
            (totalCount > 0 ? ((overdueCount / totalCount) * 100).toFixed(2) : 0) + '%">' +
            (totalCount > 0 ? ((overdueCount / totalCount) * 100).toFixed(2) : '0.00') + '%</div></div></td></tr></tbody></table></div></div></div>';

        if (data.feeTypeStats && Array.isArray(data.feeTypeStats) && data.feeTypeStats.length > 0) {
            html += '<div class="row"><div class="col-md-12"><div class="detail-card"><h5 class="card-title"><i class="fas fa-list-alt"></i> 费用类型统计</h5>' +
                '<table class="table table-striped table-hover"><thead class="thead-light"><tr><th>收费项目</th><th class="text-right">账单数量</th>' +
                '<th class="text-right">应收金额</th><th class="text-right">实收金额</th><th class="text-center">收缴率</th></tr></thead><tbody>';

            for (var i = 0; i < data.feeTypeStats.length; i++) {
                var item = data.feeTypeStats[i];
                var itemName = item.itemName || item.item_name || '未分类';
                var itemTotalCount = parseInt(item.totalCount || item.total_count || 0);
                var itemTotalAmount = parseFloat(item.totalAmount || item.total_amount || 0);
                var itemPaidAmount = parseFloat(item.paidAmount || item.paid_amount || 0);
                var itemRate = itemTotalAmount > 0 ? ((itemPaidAmount / itemTotalAmount) * 100).toFixed(2) : '0.00';
                var badgeClass = parseFloat(itemRate) >= 80 ? 'badge-success' : parseFloat(itemRate) >= 50 ? 'badge-warning' : 'badge-danger';

                html += '<tr><td><strong>' + itemName + '</strong></td><td class="text-right">' + itemTotalCount + ' 笔</td>' +
                    '<td class="text-right">¥' + itemTotalAmount.toFixed(2) + '</td>' +
                    '<td class="text-right text-success"><strong>¥' + itemPaidAmount.toFixed(2) + '</strong></td>' +
                    '<td class="text-center"><span class="badge ' + badgeClass + '">' + itemRate + '%</span></td></tr>';
            }

            html += '</tbody></table></div></div></div>';
        }

        html += '<div class="row mt-4"><div class="col-md-12"><div class="alert alert-info">' +
            '<i class="fas fa-info-circle"></i> <strong>统计说明:</strong><ul class="mb-0 mt-2">' +
            '<li><strong>账单缴费完成率</strong>:已缴费账单数 / 总账单数</li>' +
            '<li><strong>金额收缴率</strong>:已缴费金额 / 应收总金额</li>' +
            '<li>以上数据基于当前筛选条件统计,实时更新</li></ul></div></div></div>';

        $('#statsContent').html(html);
        console.log('=== 统计数据渲染完成 ===');
    }

    function getPaymentMethodText(method) {
        var methods = {
            'cash': '现金',
            'wechat': '微信',
            'alipay': '支付宝',
            'bank_transfer': '银行转账',
            'online': '在线支付'
        };
        return methods[method] || method;
    }

    function formatDate(date) {
        if (!date) return '-';
        var d = new Date(date);
        return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate());
    }

    function formatDateTime(datetime) {
        if (!datetime) return '-';
        var d = new Date(datetime);
        return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + ' ' +
            pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds());
    }

    function pad(num) {
        return num < 10 ? '0' + num : num;
    }
</script>

</body>
</html>

