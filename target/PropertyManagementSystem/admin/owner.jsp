<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>业主管理 - 物业管理系统</title>

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

        /* 状态标签 */
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

        /* 🔥 自定义可搜索下拉框样式 */
        .house-select-wrapper {
            position: relative;
        }

        .house-search-input {
            width: 100%;
            padding: 8px 65px 8px 12px;  /* 🔥 右侧留出空间 */
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            font-size: 14px;
            transition: border-color 0.15s ease-in-out;
        }

        .house-search-input:focus {
            border-color: #667eea;
            outline: none;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .house-dropdown {
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

        .house-dropdown.show {
            display: block;
        }

        .house-dropdown-item {
            padding: 10px 12px;
            cursor: pointer;
            border-bottom: 1px solid #f0f0f0;
            transition: background-color 0.2s;
        }

        .house-dropdown-item:hover {
            background-color: #f8f9fa;
        }

        .house-dropdown-item.active {
            background-color: #667eea;
            color: white;
        }

        .house-dropdown-item:last-child {
            border-bottom: none;
        }

        .house-info-id {
            font-weight: 600;
            color: #333;
        }

        .house-dropdown-item.active .house-info-id {
            color: white;
        }

        .house-info-detail {
            font-size: 12px;
            color: #666;
            margin-top: 2px;
        }

        .house-dropdown-item.active .house-info-detail {
            color: rgba(255,255,255,0.9);
        }

        .house-dropdown-empty {
            padding: 20px;
            text-align: center;
            color: #999;
        }

        /* 🔥 清除按钮样式 */
        .clear-house-btn {
            position: absolute;
            right: 35px;  /* 🔥 为下拉箭头留出空间 */
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

        .clear-house-btn:hover {
            color: #dc3545;
        }

        .clear-house-btn.show {
            display: block;
        }

        /* 🔥 下拉箭头图标 */
        .house-dropdown-arrow {
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
        <h2><i class="fas fa-users"></i> 业主管理</h2>
        <p>管理小区业主信息，包括添加、编辑、删除和查询业主资料</p>
    </div>

    <!-- 搜索和操作区 -->
    <div class="search-box">
        <div class="row mb-3">
            <div class="col-md-4">
                <input type="text" class="form-control" id="searchKeyword"
                       placeholder="搜索业主ID、姓名、电话或房屋编号">
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary btn-block" onclick="searchOwner()">
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
                        <i class="fas fa-plus"></i> 添加业主
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
                    <th>业主ID</th>
                    <th>姓名</th>
                    <th>电话</th>
                    <th>身份证号</th>
                    <th>房屋编号</th>
                    <th>邮箱</th>
                    <th>家庭人数</th>
                    <th>登记日期</th>
                    <th width="250">操作</th>
                </tr>
                </thead>
                <tbody id="ownerTableBody">
                <tr>
                    <td colspan="10" class="text-center">
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

<!-- 添加/编辑业主模态框 -->
<div class="modal fade" id="ownerModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">添加业主</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="ownerForm">
                    <input type="hidden" id="formMethod" value="add">
                    <input type="hidden" id="originalOwnerId">
                    <input type="hidden" id="selectedHouseId">

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">业主姓名</label>
                                <input type="text" class="form-control" id="ownerName"
                                       name="ownerName" required placeholder="请输入业主姓名">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">联系电话</label>
                                <input type="text" class="form-control" id="phone"
                                       name="phone" required placeholder="请输入11位手机号"
                                       pattern="^1[3-9]\d{9}$">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">身份证号</label>
                                <input type="text" class="form-control" id="idCard"
                                       name="idCard" required placeholder="请输入18位身份证号"
                                       pattern="^\d{17}[\dXx]$">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">房屋编号</label>
                                <div class="house-select-wrapper">
                                    <input type="text"
                                           class="form-control house-search-input"
                                           id="houseSearchInput"
                                           placeholder="输入房屋编号、楼栋或户型搜索..."
                                           autocomplete="off">
                                    <!-- 🔥 清除按钮 -->
                                    <button type="button" class="clear-house-btn" id="clearHouseBtn" title="清除选择">
                                        <i class="fas fa-times-circle"></i>
                                    </button>
                                    <!-- 🔥 下拉箭头 -->
                                    <span class="house-dropdown-arrow">
                                        <i class="fas fa-chevron-down"></i>
                                    </span>
                                    <div class="house-dropdown" id="houseDropdown"></div>
                                </div>
                                <small class="form-text text-muted">
                                    <i class="fas fa-lightbulb"></i> 支持输入：房屋编号、楼栋（如"1栋"、"01"）、户型（如"三室"）
                                </small>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">电子邮箱</label>
                                <input type="email" class="form-control" id="email"
                                       name="email" placeholder="请输入邮箱地址">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">家庭人数</label>
                                <input type="number" class="form-control" id="memberCount"
                                       name="memberCount" min="1" value="1">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">登记日期</label>
                                <input type="date" class="form-control" id="registerDate"
                                       name="registerDate">
                            </div>
                        </div>
                        <div class="col-md-6" id="passwordGroup">
                            <div class="form-group">
                                <label class="form-label required">登录密码</label>
                                <input type="password" class="form-control" id="password"
                                       name="password" placeholder="8位以上，包含字母和数字">
                                <small class="text-muted">密码必须8位以上，且包含字母和数字</small>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">备注</label>
                        <textarea class="form-control" id="remark" name="remark"
                                  rows="3" placeholder="请输入备注信息"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="saveOwner()">保存</button>
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
    var isEdit = false;
    var allHouses = []; // 🔥 存储所有空置房屋数据

    // 页面加载完成后执行
    $(document).ready(function() {
        console.log('业主管理页面加载完成');
        loadOwnerList(1);

        // 设置默认日期为今天
        var today = new Date().toISOString().split('T')[0];
        $('#registerDate').val(today);

        // 回车搜索
        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) {
                searchOwner();
            }
        });

        // 🔥 初始化房屋搜索框
        initHouseSearch();

        // 点击页面其他地方关闭下拉框
        $(document).click(function(e) {
            if (!$(e.target).closest('.house-select-wrapper').length) {
                $('#houseDropdown').removeClass('show');
            }
        });
    });

    /**
     * ✨ 修复: 新增辅助函数，用于格式化房屋ID。
     * 解决从后端接收的房屋ID（如 110102）因被当作数字处理而丢失前导零的问题。
     * 此函数确保ID始终为7位字符串，不足时在前面补零（例如，将 110102 转换为 '0110102'）。
     */
    function formatHouseId(houseId) {
        if (!houseId) return '-';
        // 使用 String.padStart() 方法来补全前导零
        return String(houseId).padStart(7, '0');
    }

    /**
     * 🔥 初始化房屋搜索框（支持智能解析）
     */
    function initHouseSearch() {
        var $input = $('#houseSearchInput');
        var $dropdown = $('#houseDropdown');
        var $hiddenInput = $('#selectedHouseId');
        var $clearBtn = $('#clearHouseBtn');

        // 🔥 输入事件 - 支持智能解析
        $input.on('input', function() {
            var keyword = $(this).val().trim();

            // 显示/隐藏清除按钮
            if (keyword !== '') {
                $clearBtn.addClass('show');
            } else {
                $clearBtn.removeClass('show');
            }

            if (keyword === '') {
                renderHouseDropdown(allHouses);
            } else {
                // 🔥 智能解析输入
                var parsedKeyword = parseHouseKeyword(keyword);
                console.log('原始输入:', keyword, '解析后:', parsedKeyword);

                var filtered = allHouses.filter(function(house) {
                    // ✨ 修复: 确保将房屋ID转换为字符串进行搜索，以防其为数字类型
                    var houseIdStr = String(house.houseId).toLowerCase();
                    var buildingNo = String(house.buildingNo).toLowerCase();
                    var layout = (house.layout || '').toLowerCase();

                    // 匹配原始关键词
                    var matchOriginal = houseIdStr.indexOf(keyword.toLowerCase()) !== -1 ||
                        buildingNo.indexOf(keyword.toLowerCase()) !== -1 ||
                        layout.indexOf(keyword.toLowerCase()) !== -1;

                    // 匹配解析后的关键词
                    var matchParsed = false;
                    if (parsedKeyword !== keyword.toLowerCase()) {
                        matchParsed = houseIdStr.indexOf(parsedKeyword) !== -1 ||
                            buildingNo.indexOf(parsedKeyword) !== -1 ||
                            layout.indexOf(parsedKeyword) !== -1;
                    }

                    return matchOriginal || matchParsed;
                });

                renderHouseDropdown(filtered);
            }
        });

        // 🔥 获得焦点时显示下拉框
        $input.on('focus', function() {
            if (allHouses.length > 0) {
                var keyword = $(this).val().trim();
                if (keyword === '') {
                    renderHouseDropdown(allHouses);
                } else {
                    // 触发搜索
                    $input.trigger('input');
                }
                $dropdown.addClass('show');
            } else {
                // 如果没有房屋数据，尝试加载
                loadVacantHouses();
            }
        });

        // 🔥 清除按钮
        $clearBtn.on('click', function(e) {
            e.stopPropagation();
            $input.val('');
            $hiddenInput.val('');
            $clearBtn.removeClass('show');
            $dropdown.removeClass('show');
            $input.focus();
        });

        // 🔥 点击输入框区域显示下拉框
        $('.house-select-wrapper').on('click', function(e) {
            if (!$(e.target).hasClass('clear-house-btn') && !$(e.target).closest('.clear-house-btn').length) {
                $input.focus();
            }
        });
    }

    /**
     * 🔥 智能解析房屋关键词
     * 支持：1栋 -> 01, 三室 -> 三室, 01010101 -> 01010101
     */
    function parseHouseKeyword(keyword) {
        keyword = keyword.trim().toLowerCase();

        // 1. 解析楼栋：1栋、一栋、01栋 -> 01
        var buildingMatch = keyword.match(/^(\d+|[一二三四五六七八九十]+)栋?$/);
        if (buildingMatch) {
            var num = buildingMatch[1];
            // 中文数字转阿拉伯数字
            var chineseNum = {
                '一': 1, '二': 2, '三': 3, '四': 4, '五': 5,
                '六': 6, '七': 7, '八': 8, '九': 9, '十': 10
            };
            if (chineseNum[num]) {
                num = chineseNum[num];
            }
            // 补齐为两位数
            return String(num).padStart(2, '0');
        }

        // 2. 解析单元：1单元、一单元 -> 1
        var unitMatch = keyword.match(/^(\d+|[一二三四五六七八九])单元?$/);
        if (unitMatch) {
            var num = unitMatch[1];
            var chineseNum = {
                '一': 1, '二': 2, '三': 3, '四': 4, '五': 5,
                '六': 6, '七': 7, '八': 8, '九': 9
            };
            if (chineseNum[num]) {
                num = chineseNum[num];
            }
            return String(num);
        }

        // 3. 解析楼层：1层、一层、01层 -> 01
        var floorMatch = keyword.match(/^(\d+|[一二三四五六七八九十]+)层?$/);
        if (floorMatch) {
            var num = floorMatch[1];
            var chineseNum = {
                '一': 1, '二': 2, '三': 3, '四': 4, '五': 5,
                '六': 6, '七': 7, '八': 8, '九': 9, '十': 10
            };
            if (chineseNum[num]) {
                num = chineseNum[num];
            }
            return String(num).padStart(2, '0');
        }

        // 4. 解析户型：三室、3室 -> 三室
        var layoutMatch = keyword.match(/^([一二三四五六七八九]|[1-9])室/);
        if (layoutMatch) {
            var num = layoutMatch[1];
            var arabicToChinese = {
                '1': '一', '2': '两', '3': '三', '4': '四', '5': '五',
                '6': '六', '7': '七', '8': '八', '9': '九'
            };
            if (arabicToChinese[num]) {
                return arabicToChinese[num] + '室';
            }
            return keyword;
        }

        // 5. 纯数字：自动补齐为两位
        if (/^\d+$/.test(keyword)) {
            if (keyword.length === 1) {
                return keyword.padStart(2, '0');
            }
        }

        // 6. 其他情况：返回原始关键词
        return keyword;
    }

    /**
     * 🔥 渲染房屋下拉列表
     */
    function renderHouseDropdown(houses) {
        var $dropdown = $('#houseDropdown');
        $dropdown.empty();

        if (houses.length === 0) {
            $dropdown.html(
                '<div class="house-dropdown-empty">' +
                '<i class="fas fa-inbox"></i> 未找到匹配的房屋<br>' +
                '<small style="color: #999; margin-top: 5px; display: block;">试试输入：房屋编号、楼栋（如"1栋"）、户型（如"三室"）</small>' +
                '</div>'
            );
            $dropdown.addClass('show');
            return;
        }

        houses.forEach(function(house) {
            var $item = $('<div class="house-dropdown-item"></div>');
            // ✨ 修复: 在下拉列表中显示房屋ID时，使用格式化函数
            var formattedHouseId = formatHouseId(house.houseId);
            $item.html(
                '<div class="house-info-id">' +
                '<i class="fas fa-home" style="margin-right: 5px; color: #667eea;"></i>' +
                formattedHouseId + // 使用格式化后的ID
                '</div>' +
                '<div class="house-info-detail">' +
                '<i class="fas fa-building" style="margin-right: 3px;"></i> 楼栋: ' + house.buildingNo + ' | ' +
                '<i class="fas fa-door-open" style="margin-right: 3px;"></i> 户型: ' + (house.layout || '-') + ' | ' +
                '<i class="fas fa-ruler-combined" style="margin-right: 3px;"></i> 面积: ' + house.area + '㎡' +
                '</div>'
            );
            $item.on('click', function() {
                // ✅ 判断是否有完整的房屋信息
                if (house.layout && house.layout !== '-' && house.area && house.area !== '-') {
                    // 有完整信息，显示详细信息
                    $('#houseSearchInput').val(formattedHouseId + ' (' + house.layout + ', ' + house.area + '㎡)');
                } else {
                    // 信息不完整，只显示房屋编号
                    $('#houseSearchInput').val(formattedHouseId);
                }
                $('#selectedHouseId').val(formattedHouseId);
                $('#clearHouseBtn').addClass('show');
                $dropdown.removeClass('show');
            });


            $dropdown.append($item);
        });

        $dropdown.addClass('show');
    }

    /**
     * 加载业主列表
     */
    function loadOwnerList(pageNum) {
        currentPage = pageNum || currentPage;
        var keyword = $('#searchKeyword').val();

        console.log('正在加载业主列表，页码:', currentPage);

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: keyword
            },
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            success: function(response) {
                console.log('业主列表响应:', response);
                if (response.success || response.code === 200) {
                    var data = response.data || response;
                    renderOwnerTable(data.list);
                    totalCount = data.total;
                    renderPagination();
                } else {
                    layer.msg(response.message || '加载失败', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('请求失败:', error);
                layer.msg('网络错误', {icon: 2});
                $('#ownerTableBody').html(
                    '<tr><td colspan="10" class="text-center text-danger">加载失败，请刷新重试</td></tr>'
                );
            }
        });
    }

    /**
     * 渲染业主表格
     */
    function renderOwnerTable(owners) {
        var tbody = $('#ownerTableBody');
        tbody.empty();

        if (!owners || owners.length === 0) {
            tbody.append(
                '<tr><td colspan="10" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无数据</td></tr>'
            );
            return;
        }

        $.each(owners, function(i, owner) {
            // 🔥 构建房产信息显示（主房产 + 数量）
            var houseInfo = '';

            if (owner.houseId) {
                // 🔥 解析并格式化主房产编号
                var houseId = formatHouseId(owner.houseId);
                var buildingNo = houseId.substring(0, 2);
                var unitNo = houseId.substring(2, 3);
                var floor = houseId.substring(3, 5);
                var roomNo = houseId.substring(5, 7);

                var displayName = parseInt(buildingNo) + '栋 ' +
                    parseInt(unitNo) + '单元 ' +
                    floor + roomNo + '室';

                houseInfo = '<div style="margin-bottom: 3px;">' +
                    displayName +
                    ' <span style="color: #28a745; font-size: 11px;">(主)</span>' +
                    '</div>';
            }

            // 🔥 如果有房屋数量，显示可点击的链接
            if (owner.houseCount && owner.houseCount > 0) {
                houseInfo += '<span style="color: #667eea; cursor: pointer; text-decoration: underline; font-size: 13px;" ' +
                    'onclick="viewOwnerHouses(\'' + owner.ownerId + '\')" title="点击查看所有房产">' +
                    '<i class="fas fa-home"></i> 共 ' + owner.houseCount + ' 套' +
                    '</span>';
            }

            // 如果没有任何房产信息，显示 "-"
            if (!houseInfo) {
                houseInfo = '<span style="color: #999;">-</span>';
            }

            // 🔥 构建表格行
            var row = '<tr>' +
                '<td class="checkbox-cell"><input type="checkbox" class="row-checkbox" value="' + (owner.ownerId || '') + '"></td>' +
                '<td>' + (owner.ownerId || '-') + '</td>' +
                '<td>' + (owner.ownerName || '-') + '</td>' +
                '<td>' + (owner.phone || '-') + '</td>' +
                '<td>' + (owner.idCard || '-') + '</td>' +
                '<td>' + houseInfo + '</td>' +
                '<td>' + (owner.email || '-') + '</td>' +
                '<td>' + (owner.memberCount || 0) + '</td>' +
                '<td>' + formatDate(owner.registerDate) + '</td>' +
                '<td>' +
                '<button class="btn btn-sm btn-info btn-action" onclick="viewOwner(\'' + owner.ownerId + '\')" title="查看详情">' +
                '<i class="fas fa-eye"></i> 查看' +
                '</button>' +
                '<button class="btn btn-sm btn-warning btn-action" onclick="editOwner(\'' + owner.ownerId + '\')" title="编辑">' +
                '<i class="fas fa-edit"></i> 编辑' +
                '</button>' +
                '<button class="btn btn-sm btn-danger btn-action" onclick="deleteOwner(\'' + owner.ownerId + '\')" title="删除">' +
                '<i class="fas fa-trash"></i> 删除' +
                '</button>' +
                '</td>' +
                '</tr>';

            tbody.append(row);
        });
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
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage > 1 ? 'loadOwnerList(' + (currentPage - 1) + ')' : 'return false') + '">上一页</a>' +
            '</li>'
        );

        // 页码
        var startPage = Math.max(1, currentPage - 2);
        var endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            pagination.append(
                '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadOwnerList(1)">1</a></li>'
            );
            if (startPage > 2) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
        }

        for (var i = startPage; i <= endPage; i++) {
            var activeClass = i === currentPage ? 'active' : '';
            pagination.append(
                '<li class="page-item ' + activeClass + '">' +
                '<a class="page-link" href="javascript:void(0)" onclick="loadOwnerList(' + i + ')">' + i + '</a>' +
                '</li>'
            );
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
            pagination.append(
                '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadOwnerList(' + totalPages + ')">' + totalPages + '</a></li>'
            );
        }

        // 下一页
        var nextDisabled = currentPage === totalPages ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + nextDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage < totalPages ? 'loadOwnerList(' + (currentPage + 1) + ')' : 'return false') + '">下一页</a>' +
            '</li>'
        );
    }

    function loadVacantHouses() {
        console.log('正在加载空置房屋...');
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house',
            type: 'GET',
            data: { method: 'findVacant' },
            success: function(response) {
                console.log('空置房屋响应:', response);
                if ((response.success || response.code === 200) && response.data) {
                    // ✅ 添加详细调试信息
                    console.log('原始房屋数据示例:', response.data[0]);

                    allHouses = response.data.map(function(house) {
                        // ✅ 打印每个房屋的原始数据
                        console.log('处理房屋:', house);

                        house.houseId = formatHouseId(house.houseId);
                        if (house.houseId === '-') return null;
                        return house;
                    }).filter(Boolean);

                    console.log('✅ 空置房屋加载完成，共 ' + allHouses.length + ' 个房屋');
                    console.log('处理后的房屋数据示例:', allHouses[0]);
                }
            },
            error: function() {
                console.error('❌ 加载空置房屋失败');
            }
        });
    }


    /**
     * 全选/取消全选
     */
    function toggleCheckAll() {
        var checked = $('#checkAll').prop('checked');
        $('.row-checkbox').prop('checked', checked);
    }

    /**
     * 搜索业主
     */
    function searchOwner() {
        currentPage = 1;
        loadOwnerList(1);
    }

    /**
     * 重置搜索
     */
    function resetSearch() {
        $('#searchKeyword').val('');
        currentPage = 1;
        loadOwnerList(1);
    }

    /**
     * 显示添加模态框
     */
    function showAddModal() {
        isEdit = false;
        $('#modalTitle').text('添加业主');
        $('#formMethod').val('add');
        $('#ownerForm')[0].reset();
        $('#passwordGroup').show();
        $('#password').prop('required', true);

        var today = new Date().toISOString().split('T')[0];
        $('#registerDate').val(today);
        $('#memberCount').val(1);

        // 🔥 重置房屋选择
        $('#houseSearchInput').val('');
        $('#selectedHouseId').val('');
        $('#clearHouseBtn').removeClass('show');
        $('#houseDropdown').removeClass('show');

        // 🔥 加载空置房屋
        loadVacantHouses();

        $('#ownerModal').modal('show');
    }

    /**
     * 查看业主详情
     */
    function viewOwner(ownerId) {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner',
            type: 'GET',
            data: {
                method: 'findById',
                ownerId: ownerId
            },
            success: function(response) {
                if ((response.success || response.code === 200) && response.data) {
                    var owner = response.data;

                    // 🔥 解析并格式化房屋编号
                    var houseId = formatHouseId(owner.houseId);
                    var buildingNo = houseId.substring(0, 2);
                    var unitNo = houseId.substring(2, 3);
                    var floor = houseId.substring(3, 5);
                    var roomNo = houseId.substring(5, 7);

                    var displayName = parseInt(buildingNo) + '栋 ' +
                        parseInt(unitNo) + '单元 ' +
                        floor + roomNo + '室';

                    var content =
                        '<div style="padding: 20px; font-family: Microsoft YaHei, Arial, sans-serif;">' +
                        '<div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px;">' +
                        '<h4 style="margin: 0 0 10px 0;"><i class="fas fa-user-circle"></i> ' + (owner.ownerName || '-') + '</h4>' +
                        '<p style="margin: 0; opacity: 0.9;">业主编号：' + (owner.ownerId || '-') + '</p>' +
                        '</div>' +
                        '<div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 15px;">' +
                        '<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-phone" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">联系电话</div>' +
                        '<div style="font-weight: 600; color: #333;">' + (owner.phone || '-') + '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-id-card" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">身份证号</div>' +
                        '<div style="font-weight: 600; color: #333;">' + (owner.idCard || '-') + '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-home" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">房屋地址</div>' +
                        '<div style="font-weight: 600; color: #333;">' + displayName + '</div>' +
                        '<div style="font-size: 11px; color: #999; margin-top: 2px;">(' + houseId + ')</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-envelope" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">电子邮箱</div>' +
                        '<div style="font-weight: 600; color: #333;">' + (owner.email || '未填写') + '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-users" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">家庭人数</div>' +
                        '<div style="font-weight: 600; color: #333;">' + (owner.memberCount || 0) + ' 人</div>' +
                        '</div>' +
                        '</div>' +
                        '<div style="display: flex; align-items: center;">' +
                        '<i class="fas fa-calendar-alt" style="color: #667eea; width: 30px; font-size: 16px;"></i>' +
                        '<div>' +
                        '<div style="font-size: 12px; color: #666; margin-bottom: 3px;">登记日期</div>' +
                        '<div style="font-weight: 600; color: #333;">' + formatDate(owner.registerDate) + '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>';

                    if (owner.remark && owner.remark.trim() !== '') {
                        content +=
                            '<div style="background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; border-radius: 5px;">' +
                            '<div style="font-size: 12px; color: #856404; margin-bottom: 5px;"><i class="fas fa-sticky-note"></i> 备注信息</div>' +
                            '<div style="color: #856404; line-height: 1.6;">' + owner.remark + '</div>' +
                            '</div>';
                    }

                    content += '</div>';

                    layer.open({
                        type: 1,
                        title: '<i class="fas fa-user-circle"></i> 业主详细信息',
                        area: ['600px', 'auto'],
                        shade: 0.5,
                        shadeClose: true,
                        content: content,
                        btn: ['<i class="fas fa-edit"></i> 编辑', '<i class="fas fa-times"></i> 关闭'],
                        yes: function(index, layero) {
                            layer.close(index);
                            editOwner(ownerId);
                        },
                        btn2: function(index, layero) {
                            layer.close(index);
                        }
                    });
                } else {
                    layer.msg(response.message || '查询失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }


    /**
     * 编辑业主
     */
    function editOwner(ownerId) {
        isEdit = true;
        $('#modalTitle').text('编辑业主');
        $('#formMethod').val('update');
        $('#passwordGroup').hide();
        $('#password').prop('required', false);

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner',
            type: 'GET',
            data: {
                method: 'findById',
                ownerId: ownerId
            },
            success: function(response) {
                if ((response.success || response.code === 200) && response.data) {
                    var owner = response.data;
                    $('#originalOwnerId').val(owner.ownerId);
                    $('#ownerName').val(owner.ownerName);
                    $('#phone').val(owner.phone);
                    $('#idCard').val(owner.idCard);
                    $('#email').val(owner.email);
                    $('#memberCount').val(owner.memberCount);
                    $('#registerDate').val(formatDateForInput(owner.registerDate));
                    $('#remark').val(owner.remark);

                    // ✅ 格式化房屋ID
                    var formattedHouseId = formatHouseId(owner.houseId);

                    // 🔥 先加载空置房屋，然后设置当前房屋
                    $.ajax({
                        url: '${pageContext.request.contextPath}/admin/house',
                        type: 'GET',
                        data: { method: 'findVacant' },
                        success: function(houseResponse) {
                            console.log('空置房屋响应:', houseResponse);

                            if ((houseResponse.success || houseResponse.code === 200) && houseResponse.data) {
                                // ✅ 处理房屋数据
                                allHouses = houseResponse.data.map(function(house) {
                                    return {
                                        houseId: formatHouseId(house.houseId),
                                        buildingNo: house.buildingNo || house.building_no || '-',
                                        layout: house.layout || house.houseType || house.house_type || '-',
                                        area: house.area || house.houseArea || house.house_area || '-'
                                    };
                                }).filter(function(house) {
                                    return house.houseId !== '-';
                                });

                                console.log('✅ 空置房屋加载完成，共 ' + allHouses.length + ' 个房屋');
                                console.log('处理后的房屋数据示例:', allHouses[0]);

                                // ✅ 添加当前房屋到列表（如果不在空置列表中）
                                var houseExists = allHouses.some(function(h) {
                                    return h.houseId === formattedHouseId;
                                });

                                if (!houseExists && owner.houseId) {
                                    // 🔥 尝试从后端获取当前房屋的完整信息
                                    $.ajax({
                                        url: '${pageContext.request.contextPath}/admin/house',
                                        type: 'GET',
                                        data: {
                                            method: 'findById',
                                            houseId: formattedHouseId
                                        },
                                        success: function(currentHouseResponse) {
                                            if ((currentHouseResponse.success || currentHouseResponse.code === 200) && currentHouseResponse.data) {
                                                var currentHouse = currentHouseResponse.data;
                                                allHouses.unshift({
                                                    houseId: formattedHouseId,
                                                    buildingNo: currentHouse.buildingNo || formattedHouseId.substring(0, 2),
                                                    layout: currentHouse.layout || currentHouse.houseType || '-',
                                                    area: currentHouse.area || currentHouse.houseArea || '-'
                                                });
                                            } else {
                                                // 如果获取失败，添加基本信息
                                                allHouses.unshift({
                                                    houseId: formattedHouseId,
                                                    buildingNo: formattedHouseId.substring(0, 2),
                                                    layout: '-',
                                                    area: '-'
                                                });
                                            }
                                            setHouseInput(formattedHouseId);
                                        },
                                        error: function() {
                                            // 如果请求失败，添加基本信息
                                            allHouses.unshift({
                                                houseId: formattedHouseId,
                                                buildingNo: formattedHouseId.substring(0, 2),
                                                layout: '-',
                                                area: '-'
                                            });
                                            setHouseInput(formattedHouseId);
                                        }
                                    });
                                } else {
                                    setHouseInput(formattedHouseId);
                                }
                            }
                        },
                        error: function() {
                            console.error('❌ 加载空置房屋失败');
                            // 即使加载失败，也要设置房屋信息
                            $('#houseSearchInput').val(formattedHouseId);
                            $('#selectedHouseId').val(formattedHouseId);
                            $('#clearHouseBtn').addClass('show');
                        }
                    });

                    $('#ownerModal').modal('show');
                } else {
                    layer.msg(response.message || '查询失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * 🔥 设置房屋输入框的值
     */
    function setHouseInput(houseId) {
        var selectedHouse = allHouses.find(function(h) {
            return h.houseId === houseId;
        });

        if (selectedHouse && selectedHouse.layout !== '-' && selectedHouse.area !== '-') {
            // 有完整信息，显示详细信息
            $('#houseSearchInput').val(selectedHouse.houseId + ' (' + selectedHouse.layout + ', ' + selectedHouse.area + '㎡)');
        } else {
            // 信息不完整，只显示房屋编号
            $('#houseSearchInput').val(houseId);
        }

        $('#selectedHouseId').val(houseId);
        $('#clearHouseBtn').addClass('show');
    }


    /**
     * 保存业主
     */
    function saveOwner() {
        var form = $('#ownerForm')[0];
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        // 🔥 验证房屋是否选择
        // ✨ 注释: 此处获取的 houseId 已经是通过下拉选择或编辑时设置好的、格式正确的字符串，无需再次处理
        var houseId = $('#selectedHouseId').val();
        if (!houseId) {
            layer.msg('请选择房屋编号', {icon: 0});
            $('#houseSearchInput').focus();
            return;
        }

        var method = $('#formMethod').val();
        var data = {
            method: method,
            ownerName: $('#ownerName').val().trim(),
            phone: $('#phone').val().trim(),
            idCard: $('#idCard').val().trim(),
            houseId: houseId,
            email: $('#email').val().trim(),
            memberCount: $('#memberCount').val(),
            registerDate: $('#registerDate').val(),
            remark: $('#remark').val().trim()
        };

        if (method === 'add') {
            data.password = $('#password').val();
        } else {
            data.ownerId = $('#originalOwnerId').val();
        }

        console.log('提交数据:', data);

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner',
            type: 'POST',
            data: data,
            success: function(response) {
                console.log('保存响应:', response);
                if (response.success || response.code === 200) {
                    layer.msg(response.message || '保存成功', {icon: 1});
                    $('#ownerModal').modal('hide');
                    loadOwnerList(currentPage);
                } else {
                    layer.msg(response.message || '保存失败', {icon: 2});
                }
            },
            error: function(xhr) {
                console.error('保存失败:', xhr);
                var errorMsg = '网络错误';
                try {
                    var response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {}
                layer.msg(errorMsg, {icon: 2});
            }
        });
    }
    /**
     * 删除业主
     */
    function deleteOwner(ownerId) {
        layer.confirm(
            '<div style="padding: 10px;">' +
            '<div style="font-size: 16px; margin-bottom: 10px;"><i class="fas fa-exclamation-triangle" style="color: #ff9800;"></i> 确认删除</div>' +
            '<div style="color: #666; line-height: 1.6;">' +
            '确定要删除该业主吗？<br>' +
            '<span style="color: #dc3545; font-weight: 600;">注意：如果该业主有未缴费记录，将无法删除！</span>' +
            '</div>' +
            '</div>',
            {
                icon: 3,
                title: false,
                closeBtn: 0,
                btn: ['<i class="fas fa-check"></i> 确定删除', '<i class="fas fa-times"></i> 取消'],
                btn1: function(index) {
                    // 显示加载动画
                    var loadingIndex = layer.load(1, {
                        shade: [0.3, '#000'],
                        content: '<div style="color: white;">正在删除...</div>'
                    });

                    $.ajax({
                        url: '${pageContext.request.contextPath}/admin/owner',
                        type: 'POST',
                        data: {
                            method: 'delete',
                            ownerId: ownerId
                        },
                        success: function(response) {
                            layer.close(loadingIndex);

                            if (response.success || response.code === 200) {
                                layer.msg('<i class="fas fa-check-circle"></i> 删除成功', {
                                    icon: 1,
                                    time: 2000
                                });
                                loadOwnerList(currentPage);
                            } else {
                                // 🔥 显示详细的错误信息
                                layer.alert(
                                    '<div style="padding: 15px;">' +
                                    '<div style="font-size: 16px; color: #dc3545; margin-bottom: 10px;">' +
                                    '<i class="fas fa-times-circle"></i> 删除失败' +
                                    '</div>' +
                                    '<div style="color: #666; line-height: 1.6;">' +
                                    (response.message || '操作失败，请稍后重试') +
                                    '</div>' +
                                    '</div>',
                                    {
                                        icon: 2,
                                        title: false,
                                        closeBtn: 1,
                                        btn: ['知道了']
                                    }
                                );
                            }
                        },
                        error: function(xhr) {
                            layer.close(loadingIndex);

                            var errorMsg = '网络错误，请稍后重试';
                            try {
                                var response = JSON.parse(xhr.responseText);
                                errorMsg = response.message || errorMsg;
                            } catch (e) {}

                            layer.alert(
                                '<div style="padding: 15px;">' +
                                '<div style="font-size: 16px; color: #dc3545; margin-bottom: 10px;">' +
                                '<i class="fas fa-exclamation-triangle"></i> 操作失败' +
                                '</div>' +
                                '<div style="color: #666; line-height: 1.6;">' +
                                errorMsg +
                                '</div>' +
                                '</div>',
                                {
                                    icon: 2,
                                    title: false,
                                    closeBtn: 1,
                                    btn: ['知道了']
                                }
                            );
                        }
                    });

                    layer.close(index);
                },
                btn2: function(index) {
                    layer.close(index);
                }
            }
        );
    }


    /**
     * 批量删除
     */
    function batchDelete() {
        var checkedBoxes = $('.row-checkbox:checked');
        if (checkedBoxes.length === 0) {
            layer.msg('<i class="fas fa-info-circle"></i> 请先选择要删除的业主', {icon: 0});
            return;
        }

        var ids = [];
        checkedBoxes.each(function() {
            ids.push($(this).val());
        });

        layer.confirm(
            '<div style="padding: 10px;">' +
            '<div style="font-size: 16px; margin-bottom: 10px;"><i class="fas fa-exclamation-triangle" style="color: #ff9800;"></i> 批量删除确认</div>' +
            '<div style="color: #666; line-height: 1.6;">' +
            '确定要删除选中的 <span style="color: #dc3545; font-weight: 600;">' + ids.length + '</span> 条记录吗？<br>' +
            '<span style="color: #dc3545; font-weight: 600;">注意：有未缴费记录的业主将无法删除！</span>' +
            '</div>' +
            '</div>',
            {
                icon: 3,
                title: false,
                closeBtn: 0,
                btn: ['<i class="fas fa-check"></i> 确定删除', '<i class="fas fa-times"></i> 取消'],
                btn1: function(index) {
                    var loadingIndex = layer.load(1, {
                        shade: [0.3, '#000'],
                        content: '<div style="color: white;">正在批量删除...</div>'
                    });

                    var deleteCount = 0;
                    var failCount = 0;
                    var completed = 0;
                    var failMessages = [];

                    $.each(ids, function(i, id) {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/admin/owner',
                            type: 'POST',
                            data: {
                                method: 'delete',
                                ownerId: id
                            },
                            success: function(response) {
                                if (response.success || response.code === 200) {
                                    deleteCount++;
                                } else {
                                    failCount++;
                                    failMessages.push('业主 ' + id + ': ' + (response.message || '删除失败'));
                                }
                            },
                            error: function(xhr) {
                                failCount++;
                                var errorMsg = '网络错误';
                                try {
                                    var response = JSON.parse(xhr.responseText);
                                    errorMsg = response.message || errorMsg;
                                } catch (e) {}
                                failMessages.push('业主 ' + id + ': ' + errorMsg);
                            },
                            complete: function() {
                                completed++;
                                if (completed === ids.length) {
                                    layer.close(loadingIndex);

                                    // 🔥 显示详细的批量删除结果
                                    var resultHtml = '<div style="padding: 15px;">' +
                                        '<div style="font-size: 16px; margin-bottom: 15px;">' +
                                        '<i class="fas fa-info-circle" style="color: #17a2b8;"></i> 批量删除结果' +
                                        '</div>' +
                                        '<div style="background: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 10px;">' +
                                        '<div style="color: #28a745; margin-bottom: 5px;">' +
                                        '<i class="fas fa-check-circle"></i> 成功删除：<strong>' + deleteCount + '</strong> 条' +
                                        '</div>';

                                    if (failCount > 0) {
                                        resultHtml += '<div style="color: #dc3545;">' +
                                            '<i class="fas fa-times-circle"></i> 删除失败：<strong>' + failCount + '</strong> 条' +
                                            '</div>';
                                    }

                                    resultHtml += '</div>';

                                    if (failMessages.length > 0) {
                                        resultHtml += '<div style="max-height: 200px; overflow-y: auto; background: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; border-radius: 3px;">' +
                                            '<div style="font-size: 14px; color: #856404; margin-bottom: 8px; font-weight: 600;">失败原因：</div>' +
                                            '<div style="font-size: 13px; color: #856404; line-height: 1.8;">';

                                        $.each(failMessages, function(i, msg) {
                                            resultHtml += '<div style="margin-bottom: 5px;">• ' + msg + '</div>';
                                        });

                                        resultHtml += '</div></div>';
                                    }

                                    resultHtml += '</div>';

                                    layer.alert(resultHtml, {
                                        icon: failCount > 0 ? 0 : 1,
                                        title: false,
                                        closeBtn: 1,
                                        btn: ['知道了'],
                                        yes: function(alertIndex) {
                                            layer.close(alertIndex);
                                        }
                                    });

                                    $('#checkAll').prop('checked', false);
                                    loadOwnerList(currentPage);
                                }
                            }
                        });
                    });

                    layer.close(index);
                },
                btn2: function(index) {
                    layer.close(index);
                }
            }
        );
    }


    /**
     * 导出当前筛选条件的数据
     */
    function exportAllData() {
        var keyword = $('#searchKeyword').val();
        var url = '${pageContext.request.contextPath}/admin/owner?method=export';
        if (keyword) {
            url += '&keyword=' + encodeURIComponent(keyword);
        }
        window.open(url, '_blank');
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

        var url = '${pageContext.request.contextPath}/admin/owner?method=exportSelected&ids=' + ids.join(',');
        window.open(url, '_blank');
        layer.msg('正在导出选中数据，请稍候...', {icon: 16, time: 2000});
    }

    /**
     * 格式化日期（显示用）
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

    /**
     * 格式化日期（输入框用）
     */
    function formatDateForInput(dateStr) {
        if (!dateStr) return '';

        try {
            if (/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) {
                return dateStr;
            }

            var date = new Date(dateStr);
            if (isNaN(date.getTime())) return '';

            var year = date.getFullYear();
            var month = String(date.getMonth() + 1).padStart(2, '0');
            var day = String(date.getDate()).padStart(2, '0');

            return year + '-' + month + '-' + day;
        } catch (e) {
            console.error('日期格式化失败:', dateStr, e);
            return '';
        }
    }
    /**
     * 🔥 查看业主的所有房屋
     */
    function viewOwnerHouses(ownerId) {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner',
            type: 'GET',
            data: {
                method: 'findHouses',
                ownerId: ownerId
            },
            success: function(response) {
                if ((response.success || response.code === 200) && response.data) {
                    var houses = response.data.houses || [];
                    var ownerName = response.data.ownerName || '业主';

                    if (houses.length === 0) {
                        layer.msg('该业主暂无房产', {icon: 0});
                        return;
                    }

                    var content = '<div style="padding: 20px;">' +
                        '<div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px; border-radius: 8px; margin-bottom: 20px;">' +
                        '<h5 style="margin: 0;"><i class="fas fa-home"></i> ' + ownerName + ' 的房产列表</h5>' +
                        '<p style="margin: 5px 0 0 0; opacity: 0.9;">共 ' + houses.length + ' 套房产</p>' +
                        '</div>' +
                        '<div style="max-height: 400px; overflow-y: auto;">';

                    $.each(houses, function(i, house) {
                        // 🔥 解析房屋编号：0210602 -> 02栋 1单元 06楼 02号
                        var houseId = formatHouseId(house.houseId);
                        var buildingNo = houseId.substring(0, 2);  // 楼栋：02
                        var unitNo = houseId.substring(2, 3);      // 单元：1
                        var floor = houseId.substring(3, 5);       // 楼层：06
                        var roomNo = houseId.substring(5, 7);      // 房号：02

                        // 🔥 格式化显示：2栋 1单元 6楼 602室
                        var displayName = parseInt(buildingNo) + '栋 ' +
                            parseInt(unitNo) + '单元 ' +
                            parseInt(floor) + '楼 ' +
                            floor + roomNo + '室';

                        content += '<div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 10px; border-left: 4px solid #667eea;">' +
                            '<div style="font-size: 16px; font-weight: 600; color: #333; margin-bottom: 10px;">' +
                            '<i class="fas fa-building" style="color: #667eea;"></i> ' + displayName +
                            '<span style="color: #999; font-size: 13px; font-weight: normal; margin-left: 10px;">(' + houseId + ')</span>' +
                            '</div>' +
                            '<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; font-size: 14px; color: #666;">' +
                            '<div><i class="fas fa-map-marker-alt" style="color: #667eea; width: 20px;"></i> 楼栋: ' + parseInt(buildingNo) + '栋</div>' +
                            '<div><i class="fas fa-door-open" style="color: #667eea; width: 20px;"></i> 单元: ' + parseInt(unitNo) + '单元</div>' +
                            '<div><i class="fas fa-layer-group" style="color: #667eea; width: 20px;"></i> 楼层: ' + parseInt(floor) + '楼</div>' +
                            '<div><i class="fas fa-home" style="color: #667eea; width: 20px;"></i> 房号: ' + floor + roomNo + '室</div>' +
                            '<div><i class="fas fa-th-large" style="color: #667eea; width: 20px;"></i> 户型: ' + (house.layout || '-') + '</div>' +
                            '<div><i class="fas fa-ruler-combined" style="color: #667eea; width: 20px;"></i> 面积: ' + (house.area || '-') + '㎡</div>' +
                            '</div>' +
                            '</div>';
                    });

                    content += '</div></div>';

                    layer.open({
                        type: 1,
                        title: false,
                        area: ['650px', 'auto'],
                        maxHeight: 600,
                        shade: 0.5,
                        shadeClose: true,
                        content: content,
                        btn: ['<i class="fas fa-times"></i> 关闭']
                    });
                } else {
                    layer.msg(response.message || '查询失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

</script>
</body>
</html>
