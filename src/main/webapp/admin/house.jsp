<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>房屋管理 - 物业管理系统</title>

    <!-- 引入 CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/theme/default/layer.css">

    <style>
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
            padding: 15px 10px;
            white-space: nowrap;
            font-size: 14px;
        }

        .table tbody td {
            padding: 12px 10px;
            vertical-align: middle;
            font-size: 14px;
        }

        /* 🔥 状态徽章样式优化 */
        .status-badge {
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
            display: inline-block;
            white-space: nowrap;
        }

        .status-vacant {
            background-color: #e3f2fd;
            color: #1976d2;
        }

        .status-occupied {
            background-color: #e8f5e9;
            color: #388e3c;
        }

        .status-rented {
            background-color: #fff3e0;
            color: #f57c00;
        }

        /* 🔥 销售状态徽章 */
        .sale-badge {
            padding: 3px 8px;
            border-radius: 10px;
            font-size: 11px;
            font-weight: 500;
            margin-left: 5px;
            display: inline-block;
        }

        .sale-for_sale {
            background-color: #fff3e0;
            color: #f57c00;
        }

        .sale-sold {
            background-color: #e8f5e9;
            color: #388e3c;
        }

        .sale-leased {
            background-color: #e3f2fd;
            color: #1976d2;
        }

        /* 🔥 操作按钮优化 */
        .btn-action {
            margin: 2px;
            padding: 4px 10px;
            font-size: 12px;
            white-space: nowrap;
        }

        .btn-action i {
            margin-right: 3px;
        }

        /* 🔥 操作列宽度固定 */
        .action-column {
            width: 280px;
            min-width: 280px;
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
            font-size: 14px;
        }

        .required::after {
            content: " *";
            color: #dc3545;
        }

        /* 复选框样式 */
        .checkbox-cell {
            width: 40px;
            text-align: center;
        }

        input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        /* 🔥 按钮组样式优化 */
        .btn-group-custom {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .btn-export {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border: none;
        }

        .btn-export:hover {
            background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
            color: white;
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(245, 87, 108, 0.3);
        }

        /* 🔥 搜索提示样式 */
        .search-hint {
            margin-top: 5px;
            font-size: 12px;
            color: #6c757d;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .search-hint i {
            color: #667eea;
            font-size: 13px;
        }

        /* 🔥 业主搜索框样式 */
        .owner-select-wrapper {
            position: relative;
        }

        .owner-search-input {
            width: 100%;
            padding: 8px 65px 8px 12px;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            font-size: 14px;
            transition: all 0.3s;
        }

        .owner-search-input:focus {
            border-color: #667eea;
            outline: none;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .owner-dropdown {
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            max-height: 300px;
            overflow-y: auto;
            background: white;
            border: 1px solid #ced4da;
            border-top: none;
            border-radius: 0 0 0.25rem 0.25rem;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            z-index: 1000;
            display: none;
        }

        .owner-dropdown.show {
            display: block;
        }

        .owner-dropdown-item {
            padding: 10px 12px;
            cursor: pointer;
            border-bottom: 1px solid #f0f0f0;
            transition: all 0.2s;
        }

        .owner-dropdown-item:hover {
            background-color: #f8f9fa;
        }

        .owner-dropdown-item.active {
            background-color: #667eea;
            color: white;
        }

        .owner-dropdown-item:last-child {
            border-bottom: none;
        }

        .owner-dropdown-empty {
            padding: 20px;
            text-align: center;
            color: #999;
        }

        .owner-info-name {
            font-weight: 600;
            color: #333;
            display: flex;
            align-items: center;
        }

        .owner-info-name i {
            margin-right: 5px;
            color: #667eea;
        }

        .owner-dropdown-item.active .owner-info-name {
            color: white;
        }

        .owner-dropdown-item.active .owner-info-name i {
            color: white;
        }

        .owner-info-detail {
            font-size: 12px;
            color: #666;
            margin-top: 4px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .owner-info-detail i {
            color: #999;
            width: 14px;
        }

        .owner-dropdown-item.active .owner-info-detail {
            color: rgba(255,255,255,0.9);
        }

        .owner-dropdown-item.active .owner-info-detail i {
            color: rgba(255,255,255,0.8);
        }

        .clear-owner-btn {
            position: absolute;
            right: 35px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #999;
            cursor: pointer;
            font-size: 18px;
            padding: 5px;
            width: 24px;
            height: 24px;
            display: none;
            z-index: 10;
            transition: color 0.2s;
        }

        .clear-owner-btn:hover {
            color: #dc3545;
        }

        .clear-owner-btn.show {
            display: block;
        }

        .owner-dropdown-arrow {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            color: #999;
            pointer-events: none;
            font-size: 14px;
        }

        /* 🔥 表格滚动优化 */
        .table-responsive {
            overflow-x: auto;
        }

        /* 🔥 加载动画 */
        .loading-spinner {
            text-align: center;
            padding: 40px;
            color: #667eea;
        }

        .loading-spinner i {
            font-size: 32px;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* 🔥 空状态样式 */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }

        .empty-state i {
            font-size: 48px;
            color: #ddd;
            margin-bottom: 15px;
        }

        /* 🔥 分页样式优化 */
        .pagination .page-link {
            color: #667eea;
            border-color: #dee2e6;
        }

        .pagination .page-item.active .page-link {
            background-color: #667eea;
            border-color: #667eea;
        }

        .pagination .page-link:hover {
            color: #764ba2;
            background-color: #f8f9fa;
        }

        /* 🔥 响应式优化 */
        @media (max-width: 768px) {
            .page-header h2 {
                font-size: 22px;
            }

            .btn-action {
                padding: 3px 8px;
                font-size: 11px;
            }

            .action-column {
                width: auto;
                min-width: auto;
            }
        }
    </style>
</head>
<body>

<div class="container-fluid">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-building"></i> 房屋管理</h2>
        <p>管理小区房屋信息，包括添加、编辑、删除和分配业主</p>
    </div>

    <!-- 搜索和操作区 -->
    <div class="search-box">
        <!-- 🔥 搜索条件区 -->
        <div class="row mb-3">
            <div class="col-md-3 mb-2">
                <input type="text" class="form-control" id="searchKeyword"
                       placeholder="搜索房屋编号/楼栋/业主">
                <small class="search-hint">
                    <i class="fas fa-lightbulb"></i>
                    支持：1栋、2单元、5楼、姓名、电话
                </small>
            </div>
            <div class="col-md-2 mb-2">
                <select class="form-control" id="searchStatus">
                    <option value="">全部入住状态</option>
                    <option value="vacant">空置</option>
                    <option value="occupied">已入住</option>
                    <option value="rented">出租</option>
                </select>
            </div>
            <!-- 🔥 新增：销售状态筛选 -->
            <div class="col-md-2 mb-2">
                <select class="form-control" id="searchSaleStatus">
                    <option value="">全部销售状态</option>
                    <option value="for_sale">待售</option>
                    <option value="sold">已售</option>
                    <option value="leased">已租</option>
                </select>
            </div>
            <div class="col-md-2 mb-2">
                <button class="btn btn-primary btn-block" onclick="loadHouseList(1)">
                    <i class="fas fa-search"></i> 搜索
                </button>
            </div>
            <div class="col-md-2 mb-2">
                <button class="btn btn-secondary btn-block" onclick="resetSearch()">
                    <i class="fas fa-redo"></i> 重置
                </button>
            </div>
        </div>

        <!-- 🔥 操作按钮区（添加房屋移到这里） -->
        <div class="row">
            <div class="col-md-12">
                <div class="btn-group-custom">
                    <button class="btn btn-success" onclick="showAddModal()">
                        <i class="fas fa-plus"></i> 添加房屋
                    </button>
                    <button class="btn btn-export" onclick="exportAllData()">
                        <i class="fas fa-file-export"></i> 导出当前数据
                    </button>
                    <button class="btn btn-warning" onclick="exportSelectedData()">
                        <i class="fas fa-check-square"></i> 导出选中数据
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
                    <th>房屋编号</th>
                    <th>楼栋</th>
                    <th>单元</th>
                    <th>楼层</th>
                    <th>面积(㎡)</th>
                    <th>户型</th>
                    <th>业主</th>
                    <th>联系电话</th>
                    <th>状态</th>
                    <th class="action-column">操作</th>
                </tr>
                </thead>
                <tbody id="houseTableBody">
                <tr>
                    <td colspan="11" class="loading-spinner">
                        <i class="fas fa-spinner fa-spin"></i>
                        <p class="mt-2">加载中...</p>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
        <div class="row align-items-center">
            <div class="col-md-6">
                <div id="pageInfo">共 0 条记录</div>
            </div>
            <div class="col-md-6">
                <nav>
                    <ul class="pagination justify-content-end mb-0" id="pagination">
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>

<!-- 添加/编辑房屋模态框 -->
<div class="modal fade" id="houseModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">
                    <i class="fas fa-home"></i> 添加房屋
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="houseForm">
                    <input type="hidden" id="formMethod" value="add">
                    <input type="hidden" id="originalHouseId">
                    <input type="hidden" id="selectedOwnerId">

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">房屋编号</label>
                                <input type="text" class="form-control" id="houseId"
                                       name="houseId" required placeholder="如：01010101">
                                <small class="form-text text-muted">格式：楼栋(2位)+单元(1位)+楼层(2位)+房号(2位)</small>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">楼栋号</label>
                                <input type="text" class="form-control" id="buildingNo"
                                       name="buildingNo" required placeholder="如：01、02" maxlength="2">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">单元号</label>
                                <input type="text" class="form-control" id="unitNo"
                                       name="unitNo" required placeholder="如：1、2" maxlength="1">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">楼层</label>
                                <input type="text" class="form-control" id="floor"
                                       name="floor" required placeholder="如：01、06" maxlength="2">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">建筑面积(㎡)</label>
                                <input type="number" class="form-control" id="area"
                                       name="area" required step="0.1" min="0" placeholder="如：89.5">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">户型</label>
                                <select class="form-control" id="layout" name="layout" required>
                                    <option value="">请选择户型</option>
                                    <option value="一室一厅">一室一厅</option>
                                    <option value="两室一厅">两室一厅</option>
                                    <option value="两室两厅">两室两厅</option>
                                    <option value="三室两厅">三室两厅</option>
                                    <option value="四室两厅">四室两厅</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">物业费单价(元/㎡/月)</label>
                                <input type="number" class="form-control" id="pricePerSqm"
                                       name="pricePerSqm" required step="0.01" min="0" placeholder="如：3.00">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">房屋状态</label>
                                <select class="form-control" id="houseStatus" name="houseStatus" required>
                                    <option value="vacant">空置</option>
                                    <option value="occupied">已入住</option>
                                    <option value="rented">出租</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">销售状态</label>
                                <select class="form-control" id="saleStatus" name="saleStatus">
                                    <option value="for_sale">待售</option>
                                    <option value="sold">已售</option>
                                    <option value="leased">已租</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">业主</label>
                                <div class="owner-select-wrapper">
                                    <input type="text"
                                           class="form-control owner-search-input"
                                           id="ownerSearchInput"
                                           placeholder="输入姓名、电话或业主ID搜索..."
                                           autocomplete="off">
                                    <button type="button" class="clear-owner-btn" id="clearOwnerBtn" title="清除选择">
                                        <i class="fas fa-times-circle"></i>
                                    </button>
                                    <span class="owner-dropdown-arrow">
                                        <i class="fas fa-chevron-down"></i>
                                    </span>
                                    <div class="owner-dropdown" id="ownerDropdown"></div>
                                </div>
                                <small class="form-text text-muted">
                                    <i class="fas fa-lightbulb"></i> 可输入姓名、电话或业主ID搜索
                                </small>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-primary" onclick="saveHouse()">
                    <i class="fas fa-save"></i> 保存
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 🔥 出售房屋模态框 -->
<div class="modal fade" id="sellModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title">
                    <i class="fas fa-dollar-sign"></i> 出售房屋
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="sellHouseId">
                <input type="hidden" id="sellSelectedOwnerId">

                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i>
                    选择业主后，房屋将自动标记为<strong>【已售 + 已入住】</strong>状态
                </div>

                <div class="form-group">
                    <label class="form-label required">选择业主</label>
                    <div class="owner-select-wrapper">
                        <input type="text"
                               class="form-control owner-search-input"
                               id="sellOwnerSearchInput"
                               placeholder="输入姓名、电话或业主ID搜索..."
                               autocomplete="off">
                        <button type="button" class="clear-owner-btn" id="clearSellOwnerBtn" title="清除选择">
                            <i class="fas fa-times-circle"></i>
                        </button>
                        <span class="owner-dropdown-arrow">
                            <i class="fas fa-chevron-down"></i>
                        </span>
                        <div class="owner-dropdown" id="sellOwnerDropdown"></div>
                    </div>
                    <small class="form-text text-muted">
                        <i class="fas fa-lightbulb"></i> 可输入姓名、电话或业主ID搜索
                    </small>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-success" onclick="confirmSell()">
                    <i class="fas fa-check"></i> 确定出售
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 🔥 出租房屋模态框 -->
<div class="modal fade" id="leaseModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title">
                    <i class="fas fa-key"></i> 出租房屋
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="leaseHouseId">
                <input type="hidden" id="leaseSelectedOwnerId">

                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i>
                    选择租户后，房屋将自动标记为<strong>【已租 + 出租中】</strong>状态
                </div>

                <div class="form-group">
                    <label class="form-label required">选择租户</label>
                    <div class="owner-select-wrapper">
                        <input type="text"
                               class="form-control owner-search-input"
                               id="leaseOwnerSearchInput"
                               placeholder="输入姓名、电话或租户ID搜索..."
                               autocomplete="off">
                        <button type="button" class="clear-owner-btn" id="clearLeaseOwnerBtn" title="清除选择">
                            <i class="fas fa-times-circle"></i>
                        </button>
                        <span class="owner-dropdown-arrow">
                            <i class="fas fa-chevron-down"></i>
                        </span>
                        <div class="owner-dropdown" id="leaseOwnerDropdown"></div>
                    </div>
                    <small class="form-text text-muted">
                        <i class="fas fa-lightbulb"></i> 可输入姓名、电话或租户ID搜索
                    </small>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-info" onclick="confirmLease()">
                    <i class="fas fa-check"></i> 确定出租
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 分配业主模态框 -->
<div class="modal fade" id="assignOwnerModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-user-plus"></i> 分配业主
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="assignHouseId">
                <input type="hidden" id="assignSelectedOwnerId">
                <div class="form-group">
                    <label class="form-label required">选择业主</label>
                    <div class="owner-select-wrapper">
                        <input type="text"
                               class="form-control owner-search-input"
                               id="assignOwnerSearchInput"
                               placeholder="输入姓名、电话或业主ID搜索..."
                               autocomplete="off">
                        <button type="button" class="clear-owner-btn" id="clearAssignOwnerBtn" title="清除选择">
                            <i class="fas fa-times-circle"></i>
                        </button>
                        <span class="owner-dropdown-arrow">
                            <i class="fas fa-chevron-down"></i>
                        </span>
                        <div class="owner-dropdown" id="assignOwnerDropdown"></div>
                    </div>
                    <small class="form-text text-muted">
                        <i class="fas fa-lightbulb"></i> 可输入姓名、电话或业主ID搜索
                    </small>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-primary" onclick="confirmAssignOwner()">
                    <i class="fas fa-check"></i> 确定
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

<script>
    var currentPage = 1;
    var pageSize = 10;
    var totalCount = 0;
    var allOwners = [];
    var allHouses = [];

    $(document).ready(function() {
        console.log('房屋管理页面加载完成');
        loadHouseList(1);
        loadOwnerList();

        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) {
                loadHouseList(1);
            }
        });

        // 🔥 初始化所有业主搜索框
        initOwnerSearch('ownerSearchInput', 'ownerDropdown', 'selectedOwnerId', 'clearOwnerBtn');
        initOwnerSearch('assignOwnerSearchInput', 'assignOwnerDropdown', 'assignSelectedOwnerId', 'clearAssignOwnerBtn');
        initOwnerSearch('sellOwnerSearchInput', 'sellOwnerDropdown', 'sellSelectedOwnerId', 'clearSellOwnerBtn');
        initOwnerSearch('leaseOwnerSearchInput', 'leaseOwnerDropdown', 'leaseSelectedOwnerId', 'clearLeaseOwnerBtn');

        $(document).click(function(e) {
            if (!$(e.target).closest('.owner-select-wrapper').length) {
                $('.owner-dropdown').removeClass('show');
            }
        });
    });

    function parseSearchKeyword(keyword) {
        if (!keyword) return {type: 'all', value: '', display: ''};
        keyword = keyword.trim();

        var buildingMatch = keyword.match(/^(\d+)栋$/);
        if (buildingMatch) {
            var num = parseInt(buildingMatch[1]);
            var value = num < 10 ? '0' + num : num.toString();
            return {type: 'building', value: value, display: value + '栋'};
        }

        var unitMatch = keyword.match(/^(\d+)单元$/);
        if (unitMatch) {
            return {type: 'unit', value: unitMatch[1], display: unitMatch[1] + '单元'};
        }

        var floorMatch = keyword.match(/^(\d+)[楼层]$/);
        if (floorMatch) {
            var num = parseInt(floorMatch[1]);
            var value = num < 10 ? '0' + num : num.toString();
            return {type: 'floor', value: value, display: value + '楼'};
        }

        return {type: 'all', value: keyword, display: keyword};
    }

    /**
     * 🔥 显示出售模态框
     */
    function showSellModal(houseId) {
        $('#sellHouseId').val(houseId);
        $('#sellOwnerSearchInput').val('');
        $('#sellSelectedOwnerId').val('');
        $('#clearSellOwnerBtn').removeClass('show');
        $('#sellOwnerDropdown').removeClass('show');
        $('#sellModal').modal('show');
    }

    /**
     * 🔥 确认出售
     */
    function confirmSell() {
        var houseId = $('#sellHouseId').val();
        var ownerId = $('#sellSelectedOwnerId').val();

        if (!ownerId) {
            layer.msg('请选择业主', {icon: 0});
            return;
        }

        var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'POST',
            data: {
                method: 'markAsSold',
                houseId: houseId,
                ownerId: ownerId
            },
            success: function(response) {
                layer.close(loadingIndex);
                if (response.success) {
                    layer.msg('✅ 房屋已成功出售，状态已自动更新为【已售+已入住】', {icon: 1, time: 2000});
                    $('#sellModal').modal('hide');
                    refreshData();
                } else {
                    layer.msg(response.message || '出售失败', {icon: 2});
                }
            },
            error: function() {
                layer.close(loadingIndex);
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * 🔥 显示出租模态框
     */
    function showLeaseModal(houseId) {
        $('#leaseHouseId').val(houseId);
        $('#leaseOwnerSearchInput').val('');
        $('#leaseSelectedOwnerId').val('');
        $('#clearLeaseOwnerBtn').removeClass('show');
        $('#leaseOwnerDropdown').removeClass('show');
        $('#leaseModal').modal('show');
    }

    /**
     * 🔥 确认出租
     */
    function confirmLease() {
        var houseId = $('#leaseHouseId').val();
        var ownerId = $('#leaseSelectedOwnerId').val();

        if (!ownerId) {
            layer.msg('请选择租户', {icon: 0});
            return;
        }

        var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'POST',
            data: {
                method: 'markAsLeased',
                houseId: houseId,
                ownerId: ownerId
            },
            success: function(response) {
                layer.close(loadingIndex);
                if (response.success) {
                    layer.msg('✅ 房屋已成功出租，状态已自动更新为【已租+出租中】', {icon: 1, time: 2000});
                    $('#leaseModal').modal('hide');
                    refreshData();
                } else {
                    layer.msg(response.message || '出租失败', {icon: 2});
                }
            },
            error: function() {
                layer.close(loadingIndex);
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * 🔥 取消出售
     */
    function cancelSale(houseId) {
        layer.confirm(
            '<div style="padding: 10px;">' +
            '<div style="font-size: 16px; margin-bottom: 10px;"><i class="fas fa-exclamation-triangle" style="color: #ff9800;"></i> 确认取消出售</div>' +
            '<div style="color: #666; line-height: 1.6;">' +
            '确定要取消此房屋的出售状态吗？<br>' +
            '<span style="color: #dc3545; font-weight: 600;">房屋将重新标记为"待售"状态，业主关联将被清除。</span>' +
            '</div>' +
            '</div>',
            {
                icon: 0,
                title: false,
                closeBtn: 1,
                btn: ['<i class="fas fa-check"></i> 确定', '<i class="fas fa-times"></i> 取消']
            },
            function(index) {
                var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

                $.ajax({
                    url: '${pageContext.request.contextPath}/admin/house',
                    type: 'POST',
                    data: {
                        method: 'markAsForSale',
                        houseId: houseId
                    },
                    success: function(response) {
                        layer.close(loadingIndex);
                        if (response.success) {
                            layer.msg('✅ 已取消出售，房屋状态已恢复为【待售+空置】', {icon: 1});
                            refreshData();
                        } else {
                            layer.msg(response.message || '操作失败', {icon: 2});
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

    /**
     * 🔥 取消出租
     */
    function cancelLease(houseId) {
        layer.confirm(
            '<div style="padding: 10px;">' +
            '<div style="font-size: 16px; margin-bottom: 10px;"><i class="fas fa-exclamation-triangle" style="color: #ff9800;"></i> 确认取消出租</div>' +
            '<div style="color: #666; line-height: 1.6;">' +
            '确定要取消此房屋的出租状态吗？<br>' +
            '<span style="color: #dc3545; font-weight: 600;">房屋将重新标记为"待售"状态，租户关联将被清除。</span>' +
            '</div>' +
            '</div>',
            {
                icon: 0,
                title: false,
                closeBtn: 1,
                btn: ['<i class="fas fa-check"></i> 确定', '<i class="fas fa-times"></i> 取消']
            },
            function(index) {
                var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

                $.ajax({
                    url: '${pageContext.request.contextPath}/admin/house',
                    type: 'POST',
                    data: {
                        method: 'markAsForSale',
                        houseId: houseId
                    },
                    success: function(response) {
                        layer.close(loadingIndex);
                        if (response.success) {
                            layer.msg('✅ 已取消出租，房屋状态已恢复为【待售+空置】', {icon: 1});
                            refreshData();
                        } else {
                            layer.msg(response.message || '操作失败', {icon: 2});
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

    function filterHousesByKeyword(houses, keyword) {
        if (!keyword) return houses;
        var parsed = parseSearchKeyword(keyword);

        if (parsed.type === 'all') {
            var lowerKeyword = parsed.value.toLowerCase();
            return houses.filter(function(house) {
                return (house.houseId && house.houseId.toLowerCase().indexOf(lowerKeyword) !== -1) ||
                    (house.buildingNo && house.buildingNo.toLowerCase().indexOf(lowerKeyword) !== -1) ||
                    (house.unitNo && house.unitNo.toLowerCase().indexOf(lowerKeyword) !== -1) ||
                    (house.floor && house.floor.toLowerCase().indexOf(lowerKeyword) !== -1) ||
                    (house.ownerName && house.ownerName.toLowerCase().indexOf(lowerKeyword) !== -1) ||
                    (house.ownerPhone && house.ownerPhone.indexOf(lowerKeyword) !== -1);
            });
        }

        return houses.filter(function(house) {
            switch(parsed.type) {
                case 'building': return house.buildingNo === parsed.value;
                case 'unit': return house.unitNo === parsed.value;
                case 'floor': return house.floor === parsed.value;
                default: return true;
            }
        });
    }

    function filterHousesByStatus(houses, status) {
        if (!status) return houses;
        return houses.filter(function(house) {
            return house.houseStatus === status;
        });
    }

    // 🔥 新增：按销售状态过滤
    function filterHousesBySaleStatus(houses, saleStatus) {
        if (!saleStatus) return houses;
        return houses.filter(function(house) {
            return house.saleStatus === saleStatus;
        });
    }

    function loadHouseList(pageNum) {
        currentPage = pageNum;
        var keyword = $('#searchKeyword').val();
        var status = $('#searchStatus').val();
        var saleStatus = $('#searchSaleStatus').val();

        $('#houseTableBody').html(
            '<tr><td colspan="11" class="loading-spinner">' +
            '<i class="fas fa-spinner fa-spin"></i><p class="mt-2">加载中...</p>' +
            '</td></tr>'
        );

        if (allHouses.length > 0 && pageNum > 1) {
            processHouseData(allHouses, keyword, status, saleStatus, pageNum);
            return;
        }

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: 1,
                pageSize: 9999,
                keyword: '',
                status: ''
            },
            success: function(response) {
                if (response.success) {
                    allHouses = response.data.list || [];
                    processHouseData(allHouses, keyword, status, saleStatus, pageNum);
                } else {
                    layer.msg(response.message || '加载失败', {icon: 2});
                    $('#houseTableBody').html(
                        '<tr><td colspan="11" class="empty-state">' +
                        '<i class="fas fa-exclamation-circle"></i>' +
                        '<p>加载失败</p>' +
                        '</td></tr>'
                    );
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
                $('#houseTableBody').html(
                    '<tr><td colspan="11" class="empty-state">' +
                    '<i class="fas fa-wifi"></i>' +
                    '<p>网络错误</p>' +
                    '</td></tr>'
                );
            }
        });
    }

    function processHouseData(houses, keyword, status, saleStatus, pageNum) {
        var filteredHouses = filterHousesByKeyword(houses, keyword);
        filteredHouses = filterHousesByStatus(filteredHouses, status);
        filteredHouses = filterHousesBySaleStatus(filteredHouses, saleStatus);
        totalCount = filteredHouses.length;

        var start = (pageNum - 1) * pageSize;
        var end = start + pageSize;
        var pagedHouses = filteredHouses.slice(start, end);

        renderHouseTable(pagedHouses);
        renderPagination();

        if (keyword && totalCount === 0) {
            var parsed = parseSearchKeyword(keyword);
            layer.msg('未找到 "' + parsed.display + '" 的相关房屋', {icon: 0, time: 2000});
        }
    }

    // 🔥 修改：根据 sale_status 显示不同按钮
    function renderHouseTable(houses) {
        var tbody = $('#houseTableBody');
        tbody.empty();

        if (!houses || houses.length === 0) {
            tbody.append(
                '<tr><td colspan="11" class="empty-state">' +
                '<i class="fas fa-inbox"></i>' +
                '<p class="mt-2">暂无数据</p>' +
                '</td></tr>'
            );
            return;
        }

        for (var i = 0; i < houses.length; i++) {
            var house = houses[i];

            // 🔥 房屋状态
            var statusClass = '';
            var statusText = '';
            switch (house.houseStatus) {
                case 'vacant':
                    statusClass = 'status-vacant';
                    statusText = '空置';
                    break;
                case 'occupied':
                    statusClass = 'status-occupied';
                    statusText = '已入住';
                    break;
                case 'rented':
                    statusClass = 'status-rented';
                    statusText = '出租';
                    break;
                default:
                    statusText = '未知';
            }

            // 🔥 销售状态
            var saleClass = '';
            var saleText = '';
            switch (house.saleStatus) {
                case 'for_sale':
                    saleClass = 'sale-for_sale';
                    saleText = '待售';
                    break;
                case 'sold':
                    saleClass = 'sale-sold';
                    saleText = '已售';
                    break;
                case 'leased':
                    saleClass = 'sale-leased';
                    saleText = '已租';
                    break;
            }

            var ownerInfo = house.ownerName || '-';
            var phoneInfo = house.ownerPhone || '-';

            // 🔥 根据 sale_status 显示不同的操作按钮
            var actionButtons = '';

            if (house.saleStatus === 'for_sale') {
                // 待售状态：显示"出售"和"出租"按钮
                actionButtons += '<button class="btn btn-sm btn-success btn-action" onclick="showSellModal(\'' + house.houseId + '\')" title="出售">' +
                    '<i class="fas fa-dollar-sign"></i> 出售' +
                    '</button>';
                actionButtons += '<button class="btn btn-sm btn-info btn-action" onclick="showLeaseModal(\'' + house.houseId + '\')" title="出租">' +
                    '<i class="fas fa-key"></i> 出租' +
                    '</button>';
            } else if (house.saleStatus === 'sold') {
                // 已售状态：显示"已售"标签和"取消出售"按钮
                actionButtons += '<span class="badge badge-success mr-1" style="font-size: 11px;">✓ 已售</span>';
                actionButtons += '<button class="btn btn-sm btn-warning btn-action" onclick="cancelSale(\'' + house.houseId + '\')" title="取消出售">' +
                    '<i class="fas fa-undo"></i> 取消' +
                    '</button>';
            } else if (house.saleStatus === 'leased') {
                // 已租状态：显示"已租"标签和"取消出租"按钮
                actionButtons += '<span class="badge badge-info mr-1" style="font-size: 11px;">✓ 已租</span>';
                actionButtons += '<button class="btn btn-sm btn-warning btn-action" onclick="cancelLease(\'' + house.houseId + '\')" title="取消出租">' +
                    '<i class="fas fa-undo"></i> 取消' +
                    '</button>';
            }

            // 通用操作按钮
            actionButtons += '<button class="btn btn-sm btn-primary btn-action" onclick="showEditModal(\'' + house.houseId + '\')" title="编辑">' +
                '<i class="fas fa-edit"></i> 编辑' +
                '</button>';

            // 🔥 只有待售状态才能删除
            if (house.saleStatus === 'for_sale') {
                actionButtons += '<button class="btn btn-sm btn-danger btn-action" onclick="deleteHouse(\'' + house.houseId + '\')" title="删除">' +
                    '<i class="fas fa-trash"></i> 删除' +
                    '</button>';
            }

            tbody.append(
                '<tr>' +
                '<td class="checkbox-cell"><input type="checkbox" class="row-checkbox" value="' + house.houseId + '"></td>' +
                '<td><strong>' + (house.houseId || '-') + '</strong></td>' +
                '<td>' + (house.buildingNo || '-') + '</td>' +
                '<td>' + (house.unitNo || '-') + '</td>' +
                '<td>' + (house.floor || '-') + '</td>' +
                '<td>' + (house.area || '-') + '</td>' +
                '<td>' + (house.layout || '-') + '</td>' +
                '<td>' + ownerInfo + '</td>' +
                '<td>' + phoneInfo + '</td>' +
                '<td>' +
                '<span class="status-badge ' + statusClass + '">' + statusText + '</span>' +
                (saleText ? '<span class="sale-badge ' + saleClass + '">' + saleText + '</span>' : '') +
                '</td>' +
                '<td class="action-column">' + actionButtons + '</td>' +
                '</tr>'
            );
        }
    }

    function renderPagination() {
        var totalPages = Math.ceil(totalCount / pageSize);
        $('#pageInfo').text('共 ' + totalCount + ' 条记录');

        var pagination = $('#pagination');
        pagination.empty();

        if (totalPages <= 1) return;

        var prevDisabled = currentPage === 1 ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + prevDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage > 1 ? 'loadHouseList(' + (currentPage - 1) + ')' : 'return false') + '">上一页</a>' +
            '</li>'
        );

        var startPage = Math.max(1, currentPage - 2);
        var endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            pagination.append('<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadHouseList(1)">1</a></li>');
            if (startPage > 2) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
        }

        for (var i = startPage; i <= endPage; i++) {
            var activeClass = i === currentPage ? 'active' : '';
            pagination.append(
                '<li class="page-item ' + activeClass + '">' +
                '<a class="page-link" href="javascript:void(0)" onclick="loadHouseList(' + i + ')">' + i + '</a>' +
                '</li>'
            );
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
            pagination.append('<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadHouseList(' + totalPages + ')">' + totalPages + '</a></li>');
        }

        var nextDisabled = currentPage === totalPages ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + nextDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage < totalPages ? 'loadHouseList(' + (currentPage + 1) + ')' : 'return false') + '">下一页</a>' +
            '</li>'
        );
    }

    function toggleCheckAll() {
        var checked = $('#checkAll').prop('checked');
        $('.row-checkbox').prop('checked', checked);
    }

    function resetSearch() {
        $('#searchKeyword').val('');
        $('#searchStatus').val('');
        $('#searchSaleStatus').val('');
        allHouses = [];
        loadHouseList(1);
    }

    function refreshData() {
        allHouses = [];
        loadHouseList(currentPage);
    }

    function showAddModal() {
        $('#modalTitle').html('<i class="fas fa-home"></i> 添加房屋');
        $('#formMethod').val('add');
        $('#houseForm')[0].reset();
        $('#houseId').prop('readonly', false);
        $('#ownerSearchInput').val('');
        $('#selectedOwnerId').val('');
        $('#clearOwnerBtn').removeClass('show');
        $('#ownerDropdown').removeClass('show');
        $('#houseModal').modal('show');
    }

    function showEditModal(houseId) {
        $('#modalTitle').html('<i class="fas fa-edit"></i> 编辑房屋');
        $('#formMethod').val('update');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'GET',
            data: {method: 'findById', houseId: houseId},
            success: function(response) {
                if (response.success && response.data) {
                    var house = response.data;
                    $('#houseId').val(house.houseId).prop('readonly', true);
                    $('#originalHouseId').val(house.houseId);
                    $('#buildingNo').val(house.buildingNo);
                    $('#unitNo').val(house.unitNo);
                    $('#floor').val(house.floor);
                    $('#layout').val(house.layout);
                    $('#area').val(house.area);
                    $('#pricePerSqm').val(house.pricePerSqm);
                    $('#houseStatus').val(house.houseStatus);
                    $('#saleStatus').val(house.saleStatus);

                    if (house.ownerId && house.ownerName) {
                        $('#ownerSearchInput').val(house.ownerName + ' - ' + house.ownerPhone);
                        $('#selectedOwnerId').val(house.ownerId);
                        $('#clearOwnerBtn').addClass('show');
                    } else {
                        $('#ownerSearchInput').val('');
                        $('#selectedOwnerId').val('');
                        $('#clearOwnerBtn').removeClass('show');
                    }

                    $('#houseModal').modal('show');
                } else {
                    layer.msg('加载房屋信息失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    function saveHouse() {
        var method = $('#formMethod').val();
        var formData = {
            method: method,
            houseId: $('#houseId').val(),
            buildingNo: $('#buildingNo').val(),
            unitNo: $('#unitNo').val(),
            floor: $('#floor').val(),
            layout: $('#layout').val(),
            area: $('#area').val(),
            pricePerSqm: $('#pricePerSqm').val(),
            houseStatus: $('#houseStatus').val(),
            saleStatus: $('#saleStatus').val(),
            ownerId: $('#selectedOwnerId').val() || ''
        };

        if (!formData.houseId || !formData.buildingNo || !formData.unitNo ||
            !formData.floor || !formData.layout || !formData.area || !formData.pricePerSqm) {
            layer.msg('请填写所有必填项', {icon: 0});
            return;
        }

        var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'POST',
            data: formData,
            success: function(response) {
                layer.close(loadingIndex);
                if (response.success) {
                    layer.msg(response.message || '保存成功', {icon: 1});
                    $('#houseModal').modal('hide');
                    refreshData();
                } else {
                    layer.msg(response.message || '保存失败', {icon: 2});
                }
            },
            error: function() {
                layer.close(loadingIndex);
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    function deleteHouse(houseId) {
        layer.confirm('确定要删除该房屋吗？<br><small class="text-danger">注意：只有无任何关联记录（缴费、报修）的房屋才能被删除。</small>', {
            icon: 3,
            title: '确认删除'
        }, function(index) {
            var loadingIndex = layer.load(1, {shade: [0.3, '#000']});
            $.ajax({
                url: '${pageContext.request.contextPath}/admin/house',
                type: 'POST',
                data: {method: 'delete', houseId: houseId},
                success: function(response) {
                    layer.close(loadingIndex);
                    if (response.success) {
                        layer.msg('删除成功', {icon: 1});
                        refreshData();
                    } else {
                        layer.alert(response.message || '删除失败', {
                            icon: 2,
                            title: '无法删除'
                        });
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

    function batchDelete() {
        var checkedBoxes = $('.row-checkbox:checked');
        if (checkedBoxes.length === 0) {
            layer.msg('请先选择要删除的房屋', {icon: 0});
            return;
        }

        var ids = [];
        checkedBoxes.each(function() {
            ids.push($(this).val());
        });

        layer.confirm('确定要删除选中的 ' + ids.length + ' 条记录吗？', {
            icon: 3,
            title: '确认批量删除'
        }, function(index) {
            var loading = layer.load(1, {shade: [0.3, '#000']});
            var deleteCount = 0;
            var failCount = 0;
            var completed = 0;
            var errorDetails = [];

            $.each(ids, function(i, id) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/admin/house',
                    type: 'POST',
                    data: {method: 'delete', houseId: id},
                    success: function(response) {
                        if (response.success) {
                            deleteCount++;
                        } else {
                            failCount++;
                            errorDetails.push("【" + id + "】: " + response.message);
                        }
                    },
                    error: function() {
                        failCount++;
                        errorDetails.push("【" + id + "】: 网络请求错误");
                    },
                    complete: function() {
                        completed++;
                        if (completed === ids.length) {
                            layer.close(loading);
                            if (failCount > 0) {
                                layer.alert(
                                    '<p class="text-success">成功删除: ' + deleteCount + ' 条</p>' +
                                    '<p class="text-danger">删除失败: ' + failCount + ' 条</p>' +
                                    '<hr><div style="max-height: 150px; overflow-y: auto;">' +
                                    errorDetails.join('<br>') + '</div>',
                                    {icon: 2, title: '批量删除结果'}
                                );
                            } else {
                                layer.msg('全部删除成功', {icon: 1});
                            }
                            $('#checkAll').prop('checked', false);
                            refreshData();
                        }
                    }
                });
            });
            layer.close(index);
        });
    }

    function initOwnerSearch(inputId, dropdownId, hiddenInputId, clearBtnId) {
        var $input = $('#' + inputId);
        var $dropdown = $('#' + dropdownId);
        var $hiddenInput = $('#' + hiddenInputId);
        var $clearBtn = $('#' + clearBtnId);

        $input.on('input', function() {
            var keyword = $(this).val().trim().toLowerCase();
            if (keyword !== '') {
                $clearBtn.addClass('show');
            } else {
                $clearBtn.removeClass('show');
            }

            if (keyword === '') {
                renderOwnerDropdown(allOwners, dropdownId, inputId, hiddenInputId, clearBtnId);
            } else {
                var filtered = allOwners.filter(function(owner) {
                    return owner.ownerName.toLowerCase().indexOf(keyword) !== -1 ||
                        owner.phone.indexOf(keyword) !== -1 ||
                        owner.ownerId.toLowerCase().indexOf(keyword) !== -1;
                });
                renderOwnerDropdown(filtered, dropdownId, inputId, hiddenInputId, clearBtnId);
            }
        });

        $input.on('focus', function() {
            if (allOwners.length > 0) {
                $input.trigger('input');
                $dropdown.addClass('show');
            }
        });

        $clearBtn.on('click', function(e) {
            e.stopPropagation();
            $input.val('');
            $hiddenInput.val('');
            $clearBtn.removeClass('show');
            $dropdown.removeClass('show');
            $input.focus();
        });
    }

    function renderOwnerDropdown(owners, dropdownId, inputId, hiddenInputId, clearBtnId) {
        var $dropdown = $('#' + dropdownId);
        $dropdown.empty();

        if (owners.length === 0) {
            $dropdown.html(
                '<div class="owner-dropdown-empty">' +
                '<i class="fas fa-inbox"></i> 未找到匹配的业主' +
                '</div>'
            );
            $dropdown.addClass('show');
            return;
        }

        owners.forEach(function(owner) {
            var $item = $('<div class="owner-dropdown-item"></div>');
            $item.html(
                '<div class="owner-info-name">' +
                '<i class="fas fa-user-circle"></i>' +
                owner.ownerName +
                '</div>' +
                '<div class="owner-info-detail">' +
                '<span><i class="fas fa-phone"></i> ' + owner.phone + '</span>' +
                '<span><i class="fas fa-id-badge"></i> ' + owner.ownerId + '</span>' +
                '</div>'
            );

            $item.on('click', function() {
                $('#' + inputId).val(owner.ownerName + ' - ' + owner.phone);
                $('#' + hiddenInputId).val(owner.ownerId);
                $('#' + clearBtnId).addClass('show');
                $dropdown.removeClass('show');
            });

            $dropdown.append($item);
        });

        $dropdown.addClass('show');
    }

    function loadOwnerList() {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'GET',
            data: {method: 'owners'},
            success: function(response) {
                if (response.success && response.data) {
                    allOwners = response.data;
                    console.log('✅ 业主列表加载完成，共 ' + allOwners.length + ' 个业主');
                }
            }
        });
    }

    function showAssignOwnerModal(houseId) {
        $('#assignHouseId').val(houseId);
        $('#assignOwnerSearchInput').val('');
        $('#assignSelectedOwnerId').val('');
        $('#clearAssignOwnerBtn').removeClass('show');
        $('#assignOwnerDropdown').removeClass('show');
        $('#assignOwnerModal').modal('show');
    }

    function confirmAssignOwner() {
        var houseId = $('#assignHouseId').val();
        var ownerId = $('#assignSelectedOwnerId').val();

        if (!ownerId) {
            layer.msg('请选择业主', {icon: 0});
            return;
        }

        var loadingIndex = layer.load(1, {shade: [0.3, '#000']});

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'POST',
            data: {method: 'assignOwner', houseId: houseId, ownerId: ownerId},
            success: function(response) {
                layer.close(loadingIndex);
                if (response.success) {
                    layer.msg('分配成功', {icon: 1});
                    $('#assignOwnerModal').modal('hide');
                    refreshData();
                } else {
                    layer.msg(response.message || '分配失败', {icon: 2});
                }
            },
            error: function() {
                layer.close(loadingIndex);
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    function exportAllData() {
        var keyword = $('#searchKeyword').val();
        var status = $('#searchStatus').val();
        var saleStatus = $('#searchSaleStatus').val();
        var url = '${pageContext.request.contextPath}/admin/house?method=export';
        if (keyword) url += '&keyword=' + encodeURIComponent(keyword);
        if (status) url += '&status=' + encodeURIComponent(status);
        if (saleStatus) url += '&saleStatus=' + encodeURIComponent(saleStatus);
        window.location.href = url;
        layer.msg('正在导出数据...', {icon: 16, time: 2000});
    }

    function exportSelectedData() {
        var checkedBoxes = $('.row-checkbox:checked');
        if (checkedBoxes.length === 0) {
            layer.msg('请先选择要导出的数据', {icon: 0});
            return;
        }
        var ids = [];
        checkedBoxes.each(function() {
            ids.push($(this).val());
        });
        var url = '${pageContext.request.contextPath}/admin/house?method=exportSelected&ids=' + ids.join(',');
        window.location.href = url;
        layer.msg('正在导出选中数据...', {icon: 16, time: 2000});
    }
</script>
</body>
</html>

