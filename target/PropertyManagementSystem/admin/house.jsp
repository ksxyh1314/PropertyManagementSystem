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
            padding: 15px;
            white-space: nowrap;
        }

        .table tbody td {
            padding: 12px 15px;
            vertical-align: middle;
        }

        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
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

        /* 按钮组样式 */
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

        /* 🔥 自定义可搜索下拉框样式（美化版） */
        .owner-select-wrapper {
            position: relative;
        }

        .owner-search-input {
            width: 100%;
            padding: 8px 65px 8px 12px;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            font-size: 14px;
            transition: border-color 0.15s ease-in-out;
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
            transition: background-color 0.2s;
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

        /* 🔥 美化业主信息显示 */
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

        /* 🔥 清除按钮样式 */
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

        /* 🔥 下拉箭头图标 */
        .owner-dropdown-arrow {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            color: #999;
            pointer-events: none;
            font-size: 14px;
        }
    </style>
</head>
<body>

<div class="container-fluid">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-building"></i> 房屋管理</h2>
        <p>管理小区房屋信息,包括添加、编辑、删除和分配业主</p>
    </div>

    <!-- 搜索和操作区 -->
    <div class="search-box">
        <div class="row mb-3">
            <div class="col-md-3">
                <input type="text" class="form-control" id="searchKeyword"
                       placeholder="搜索房屋编号/楼栋/业主">
                <!-- 🔥 搜索提示 -->
                <small class="search-hint">
                    <i class="fas fa-lightbulb"></i>
                    支持：1栋、2单元、5楼、姓名、电话
                </small>
            </div>
            <div class="col-md-2">
                <select class="form-control" id="searchStatus">
                    <option value="">全部状态</option>
                    <option value="vacant">空置</option>
                    <option value="occupied">已入住</option>
                    <option value="rented">出租</option>
                </select>
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary btn-block" onclick="loadHouseList(1)">
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
                    <th width="250">操作</th>
                </tr>
                </thead>
                <tbody id="houseTableBody">
                <tr>
                    <td colspan="11" class="text-center">
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

<!-- 添加/编辑房屋模态框 -->
<div class="modal fade" id="houseModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">添加房屋</h5>
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
                                    <!-- 🔥 清除按钮 -->
                                    <button type="button" class="clear-owner-btn" id="clearOwnerBtn" title="清除选择">
                                        <i class="fas fa-times-circle"></i>
                                    </button>
                                    <!-- 🔥 下拉箭头 -->
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
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="saveHouse()">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 分配业主模态框 -->
<div class="modal fade" id="assignOwnerModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">分配业主</h5>
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
                        <!-- 🔥 清除按钮 -->
                        <button type="button" class="clear-owner-btn" id="clearAssignOwnerBtn" title="清除选择">
                            <i class="fas fa-times-circle"></i>
                        </button>
                        <!-- 🔥 下拉箭头 -->
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
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="confirmAssignOwner()">确定</button>
            </div>
        </div>
    </div>
</div>

<!-- 引入 JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    // 如果 jQuery 加载失败，使用备用源
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
    var allOwners = []; // 存储所有业主数据
    var allHouses = []; // 🔥 新增：存储所有房屋数据（用于前端过滤）

    // 页面加载完成后执行
    $(document).ready(function() {
        console.log('房屋管理页面加载完成');
        loadHouseList(1);
        loadOwnerList();

        // 回车搜索
        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) {
                loadHouseList(1);
            }
        });

        // 🔥 初始化业主搜索框
        initOwnerSearch('ownerSearchInput', 'ownerDropdown', 'selectedOwnerId', 'clearOwnerBtn');
        initOwnerSearch('assignOwnerSearchInput', 'assignOwnerDropdown', 'assignSelectedOwnerId', 'clearAssignOwnerBtn');

        // 点击页面其他地方关闭下拉框
        $(document).click(function(e) {
            if (!$(e.target).closest('.owner-select-wrapper').length) {
                $('.owner-dropdown').removeClass('show');
            }
        });
    });

    /**
     * 🔥 智能解析搜索关键词（完整版）
     */
    function parseSearchKeyword(keyword) {
        if (!keyword) return {type: 'all', value: '', display: ''};

        keyword = keyword.trim();

        // 匹配 "数字+栋" 格式 → 楼栋号
        var buildingMatch = keyword.match(/^(\d+)栋$/);
        if (buildingMatch) {
            var num = parseInt(buildingMatch[1]);
            var value = num < 10 ? '0' + num : num.toString();
            return {
                type: 'building',
                value: value,
                display: value + '栋'
            };
        }

        // 匹配 "数字+单元" 格式 → 单元号
        var unitMatch = keyword.match(/^(\d+)单元$/);
        if (unitMatch) {
            return {
                type: 'unit',
                value: unitMatch[1],
                display: unitMatch[1] + '单元'
            };
        }

        // 匹配 "数字+楼" 或 "数字+层" 格式 → 楼层
        var floorMatch = keyword.match(/^(\d+)[楼层]$/);
        if (floorMatch) {
            var num = parseInt(floorMatch[1]);
            var value = num < 10 ? '0' + num : num.toString();
            return {
                type: 'floor',
                value: value,
                display: value + '楼'
            };
        }

        // 其他情况：全字段模糊搜索
        return {type: 'all', value: keyword, display: keyword};
    }

    /**
     * 🔥 前端精确过滤房屋数据
     */
    function filterHousesByKeyword(houses, keyword) {
        if (!keyword) return houses;

        var parsed = parseSearchKeyword(keyword);

        if (parsed.type === 'all') {
            // 全字段模糊搜索
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

        // 精确字段匹配
        return houses.filter(function(house) {
            switch(parsed.type) {
                case 'building':
                    return house.buildingNo === parsed.value;
                case 'unit':
                    return house.unitNo === parsed.value;
                case 'floor':
                    return house.floor === parsed.value;
                default:
                    return true;
            }
        });
    }

    /**
     * 🔥 按状态过滤房屋
     */
    function filterHousesByStatus(houses, status) {
        if (!status) return houses;
        return houses.filter(function(house) {
            return house.houseStatus === status;
        });
    }

    /**
     * 🔥 加载房屋列表（完整优化版 - 纯前端过滤）
     */
    function loadHouseList(pageNum) {
        currentPage = pageNum;
        var keyword = $('#searchKeyword').val();
        var status = $('#searchStatus').val();

        // 解析关键词
        var parsed = parseSearchKeyword(keyword);

        // 显示解析提示
        if (keyword && parsed.type !== 'all') {
            console.log('🔍 搜索解析: "' + keyword + '" → ' + parsed.type + ': "' + parsed.value + '"');
        }

        $('#houseTableBody').html('<tr><td colspan="11" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>');

        // 🔥 如果已有缓存数据且只是翻页，直接使用缓存
        if (allHouses.length > 0 && pageNum > 1) {
            processHouseData(allHouses, keyword, status, pageNum);
            return;
        }

        // 🔥 从后端获取所有数据（不带过滤条件）
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: 1,
                pageSize: 9999,  // 🔥 获取所有数据
                keyword: '',     // 🔥 不传关键词
                status: ''       // 🔥 不传状态
            },
            success: function(response) {
                console.log('✅ 后端返回数据:', response);
                if (response.success) {
                    allHouses = response.data.list || [];  // 🔥 缓存所有数据
                    processHouseData(allHouses, keyword, status, pageNum);
                } else {
                    layer.msg(response.message || '加载失败', {icon: 2});
                    $('#houseTableBody').html('<tr><td colspan="11" class="text-center text-danger">加载失败</td></tr>');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 请求失败:', error);
                layer.msg('网络错误', {icon: 2});
                $('#houseTableBody').html('<tr><td colspan="11" class="text-center text-danger">网络错误</td></tr>');
            }
        });
    }

    /**
     * 🔥 处理房屋数据（过滤 + 分页）
     */
    function processHouseData(houses, keyword, status, pageNum) {
        // 1. 按关键词过滤
        var filteredHouses = filterHousesByKeyword(houses, keyword);

        // 2. 按状态过滤
        filteredHouses = filterHousesByStatus(filteredHouses, status);

        // 3. 计算总数
        totalCount = filteredHouses.length;

        // 4. 前端分页
        var start = (pageNum - 1) * pageSize;
        var end = start + pageSize;
        var pagedHouses = filteredHouses.slice(start, end);

        // 5. 渲染表格
        renderHouseTable(pagedHouses);
        renderPagination();

        // 6. 显示搜索结果提示
        if (keyword && totalCount === 0) {
            var parsed = parseSearchKeyword(keyword);
            layer.msg('未找到 "' + parsed.display + '" 的相关房屋', {icon: 0, time: 2000});
        }

        console.log('📊 过滤结果: 共 ' + totalCount + ' 条，当前显示第 ' + pageNum + ' 页');
    }

    /**
     * 渲染房屋表格
     */
    function renderHouseTable(houses) {
        var tbody = $('#houseTableBody');
        tbody.empty();

        if (!houses || houses.length === 0) {
            tbody.append(
                '<tr><td colspan="11" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无数据</td></tr>'
            );
            return;
        }

        for (var i = 0; i < houses.length; i++) {
            var house = houses[i];
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

            var ownerInfo = house.ownerName || '-';
            var phoneInfo = house.ownerPhone || '-';
            var layoutInfo = house.layout || '-';

            tbody.append(
                '<tr>' +
                '<td class="checkbox-cell"><input type="checkbox" class="row-checkbox" value="' + house.houseId + '"></td>' +
                '<td><strong>' + (house.houseId || '-') + '</strong></td>' +
                '<td>' + (house.buildingNo || '-') + '</td>' +
                '<td>' + (house.unitNo || '-') + '</td>' +
                '<td>' + (house.floor || '-') + '</td>' +
                '<td>' + (house.area || '-') + '</td>' +
                '<td>' + layoutInfo + '</td>' +
                '<td>' + ownerInfo + '</td>' +
                '<td>' + phoneInfo + '</td>' +
                '<td><span class="status-badge ' + statusClass + '">' + statusText + '</span></td>' +
                '<td>' +
                '<button class="btn btn-sm btn-info btn-action" onclick="showEditModal(\'' + house.houseId + '\')" title="编辑">' +
                '<i class="fas fa-edit"></i> 编辑' +
                '</button>' +
                '<button class="btn btn-sm btn-warning btn-action" onclick="showAssignOwnerModal(\'' + house.houseId + '\')" title="分配业主">' +
                '<i class="fas fa-user-plus"></i> 分配' +
                '</button>' +
                '<button class="btn btn-sm btn-danger btn-action" onclick="deleteHouse(\'' + house.houseId + '\')" title="删除">' +
                '<i class="fas fa-trash"></i> 删除' +
                '</button>' +
                '</td>' +
                '</tr>'
            );
        }
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
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage > 1 ? 'loadHouseList(' + (currentPage - 1) + ')' : 'return false') + '">上一页</a>' +
            '</li>'
        );

        // 页码
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

        // 下一页
        var nextDisabled = currentPage === totalPages ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + nextDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage < totalPages ? 'loadHouseList(' + (currentPage + 1) + ')' : 'return false') + '">下一页</a>' +
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
     * 🔥 重置搜索（清空缓存）
     */
    function resetSearch() {
        $('#searchKeyword').val('');
        $('#searchStatus').val('');
        allHouses = [];  // 🔥 清空缓存，强制重新加载
        loadHouseList(1);
    }

    /**
     * 🔥 刷新数据（清空缓存）
     */
    function refreshData() {
        allHouses = [];  // 🔥 清空缓存
        loadHouseList(currentPage);
    }

    /**
     * 显示添加模态框
     */
    function showAddModal() {
        $('#modalTitle').text('添加房屋');
        $('#formMethod').val('add');
        $('#houseForm')[0].reset();
        $('#houseId').prop('readonly', false);

        // 🔥 重置业主选择
        $('#ownerSearchInput').val('');
        $('#selectedOwnerId').val('');
        $('#clearOwnerBtn').removeClass('show');
        $('#ownerDropdown').removeClass('show');

        $('#houseModal').modal('show');
    }

    /**
     * 显示编辑模态框
     */
    function showEditModal(houseId) {
        $('#modalTitle').text('编辑房屋');
        $('#formMethod').val('update');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'GET',
            data: {
                method: 'findById',
                houseId: houseId
            },
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

                    // 🔥 设置业主
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

    /**
     * 保存房屋
     */
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

        // 表单验证
        if (!formData.houseId || !formData.buildingNo || !formData.unitNo ||
            !formData.floor || !formData.layout || !formData.area || !formData.pricePerSqm) {
            layer.msg('请填写所有必填项', {icon: 0});
            return;
        }

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'POST',
            data: formData,
            success: function(response) {
                if (response.success) {
                    layer.msg(response.message || '保存成功', {icon: 1});
                    $('#houseModal').modal('hide');
                    refreshData();  // 🔥 刷新数据
                } else {
                    layer.msg(response.message || '保存失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * 删除房屋
     */
    function deleteHouse(houseId) {
        layer.confirm('确定要删除该房屋吗？<br><small class="text-danger">注意：只有无任何关联记录（缴费、报修）的房屋才能被删除。</small>', {
            icon: 3,
            title: '确认删除'
        }, function(index) {
            $.ajax({
                url: '${pageContext.request.contextPath}/admin/house',
                type: 'POST',
                data: {
                    method: 'delete',
                    houseId: houseId
                },
                success: function(response) {
                    if (response.success) {
                        layer.msg('删除成功', {icon: 1});
                        refreshData();  // 🔥 刷新数据
                    } else {
                        layer.alert(response.message || '删除失败', {
                            icon: 2,
                            title: '无法删除',
                            closeBtn: 0
                        });
                    }
                },
                error: function() {
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
            layer.msg('请先选择要删除的房屋', {icon: 0});
            return;
        }

        var ids = [];
        checkedBoxes.each(function() {
            ids.push($(this).val());
        });

        layer.confirm('确定要删除选中的 ' + ids.length + ' 条记录吗？<br><small class="text-muted">系统将自动跳过包含历史数据的房屋。</small>', {
            icon: 3,
            title: '确认批量删除'
        }, function(index) {
            var loading = layer.load(1, {shade: [0.3, '#000']});
            var deleteCount = 0;
            var failCount = 0;
            var completed = 0;
            var errorDetails = [];

            // 逐个删除
            $.each(ids, function(i, id) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/admin/house',
                    type: 'POST',
                    data: {
                        method: 'delete',
                        houseId: id
                    },
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
                                var reportHtml = '<div style="text-align: left;">' +
                                    '<p class="text-success"><i class="fas fa-check-circle"></i> 成功删除: ' + deleteCount + ' 条</p>' +
                                    '<p class="text-danger"><i class="fas fa-times-circle"></i> 删除失败: ' + failCount + ' 条</p>' +
                                    '<hr>' +
                                    '<div style="max-height: 150px; overflow-y: auto; font-size: 13px; color: #666; background: #f8f9fa; padding: 10px; border-radius: 4px;">' +
                                    errorDetails.join('<br>') +
                                    '</div>' +
                                    '</div>';

                                layer.alert(reportHtml, {
                                    icon: 2,
                                    title: '批量删除结果',
                                    area: ['450px', 'auto']
                                });
                            } else {
                                layer.msg('全部删除成功', {icon: 1});
                            }

                            $('#checkAll').prop('checked', false);
                            refreshData();  // 🔥 刷新数据
                        }
                    }
                });
            });

            layer.close(index);
        });
    }

    /**
     * 🔥 初始化业主搜索框
     */
    function initOwnerSearch(inputId, dropdownId, hiddenInputId, clearBtnId) {
        var $input = $('#' + inputId);
        var $dropdown = $('#' + dropdownId);
        var $hiddenInput = $('#' + hiddenInputId);
        var $clearBtn = $('#' + clearBtnId);

        // 输入事件
        $input.on('input', function() {
            var keyword = $(this).val().trim().toLowerCase();

            // 显示/隐藏清除按钮
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

        // 获得焦点时显示下拉框
        $input.on('focus', function() {
            if (allOwners.length > 0) {
                var keyword = $(this).val().trim();
                if (keyword === '') {
                    renderOwnerDropdown(allOwners, dropdownId, inputId, hiddenInputId, clearBtnId);
                } else {
                    $input.trigger('input');
                }
                $dropdown.addClass('show');
            }
        });

        // 清除按钮
        $clearBtn.on('click', function(e) {
            e.stopPropagation();
            $input.val('');
            $hiddenInput.val('');
            $clearBtn.removeClass('show');
            $dropdown.removeClass('show');
            $input.focus();
        });

        // 点击输入框区域显示下拉框
        $('.owner-select-wrapper').on('click', function(e) {
            if (!$(e.target).hasClass('clear-owner-btn') && !$(e.target).closest('.clear-owner-btn').length) {
                $input.focus();
            }
        });
    }

    /**
     * 🔥 渲染业主下拉列表（美化版）
     */
    function renderOwnerDropdown(owners, dropdownId, inputId, hiddenInputId, clearBtnId) {
        var $dropdown = $('#' + dropdownId);
        $dropdown.empty();

        if (owners.length === 0) {
            $dropdown.html(
                '<div class="owner-dropdown-empty">' +
                '<i class="fas fa-inbox"></i> 未找到匹配的业主<br>' +
                '<small style="color: #999; margin-top: 5px; display: block;">试试输入：姓名、电话或业主ID</small>' +
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

    /**
     * 🔥 加载业主列表
     */
    function loadOwnerList() {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'GET',
            data: { method: 'owners' },
            success: function(response) {
                if (response.success && response.data) {
                    allOwners = response.data;
                    console.log('✅ 业主列表加载完成，共 ' + allOwners.length + ' 个业主');
                }
            },
            error: function() {
                console.error('❌ 加载业主列表失败');
            }
        });
    }

    /**
     * 显示分配业主模态框
     */
    function showAssignOwnerModal(houseId) {
        $('#assignHouseId').val(houseId);

        // 🔥 重置业主选择
        $('#assignOwnerSearchInput').val('');
        $('#assignSelectedOwnerId').val('');
        $('#clearAssignOwnerBtn').removeClass('show');
        $('#assignOwnerDropdown').removeClass('show');

        $('#assignOwnerModal').modal('show');
    }

    /**
     * 确认分配业主
     */
    function confirmAssignOwner() {
        var houseId = $('#assignHouseId').val();
        var ownerId = $('#assignSelectedOwnerId').val();

        if (!ownerId) {
            layer.msg('请选择业主', {icon: 0});
            return;
        }

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'POST',
            data: {
                method: 'assignOwner',
                houseId: houseId,
                ownerId: ownerId
            },
            success: function(response) {
                if (response.success) {
                    layer.msg('分配成功', {icon: 1});
                    $('#assignOwnerModal').modal('hide');
                    refreshData();  // 🔥 刷新数据
                } else {
                    layer.msg(response.message || '分配失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * 导出当前筛选条件的数据
     */
    function exportAllData() {
        var keyword = $('#searchKeyword').val();
        var status = $('#searchStatus').val();

        var url = '${pageContext.request.contextPath}/export/house?method=export&exportType=all';
        if (keyword) {
            url += '&keyword=' + encodeURIComponent(keyword);
        }
        if (status) {
            url += '&status=' + encodeURIComponent(status);
        }

        window.location.href = url;
        layer.msg('正在导出数据，请稍候...', {icon: 16, time: 2000});
    }

    /**
     * 导出选中的数据
     */
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

        var url = '${pageContext.request.contextPath}/export/house?method=export&exportType=selected&selectedIds=' + ids.join(',');
        window.location.href = url;
        layer.msg('正在导出选中数据，请稍候...', {icon: 16, time: 2000});
    }
</script>
</body>
</html>
