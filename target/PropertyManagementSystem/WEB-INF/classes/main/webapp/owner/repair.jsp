<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>在线报修 - 智慧社区</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        body { background-color: #f5f7fa; font-family: 'Microsoft YaHei', sans-serif; }
        .page-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px 0 20px; margin-bottom: 30px; }
        .card-box { background: #fff; border-radius: 10px; border: none; box-shadow: 0 2px 15px rgba(0,0,0,0.03); margin-bottom: 20px; }

        /* 🔥 简化的报修卡片 */
        .repair-card {
            background: #fff;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 15px;
            border-left: 4px solid #eee;
            transition: all 0.3s;
            cursor: pointer;
            position: relative;
        }
        .repair-card:hover {
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            transform: translateY(-2px);
        }
        .repair-card.pending { border-left-color: #6c757d; }
        .repair-card.processing { border-left-color: #17a2b8; }
        .repair-card.completed { border-left-color: #28a745; }
        .repair-card.cancelled { border-left-color: #dc3545; }

        /* 卡片头部 */
        .repair-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        .repair-title {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin: 0;
        }
        .repair-status {
            font-size: 12px;
            padding: 4px 10px;
            border-radius: 12px;
            white-space: nowrap;
        }
        .status-pending { background: #6c757d; color: white; }
        .status-processing { background: #17a2b8; color: white; }
        .status-completed { background: #28a745; color: white; }
        .status-cancelled { background: #dc3545; color: white; }

        /* 卡片内容 */
        .repair-card-body {
            font-size: 14px;
            color: #666;
            line-height: 1.6;
        }
        .repair-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            font-size: 13px;
            color: #999;
            margin-top: 10px;
        }
        .repair-meta-item {
            display: flex;
            align-items: center;
            gap: 5px;
        }
        .repair-meta-item i {
            width: 16px;
            text-align: center;
        }

        /* 优先级标记 */
        .priority-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;
        }
        .priority-normal { background: #e8f5e9; color: #2e7d32; }
        .priority-urgent { background: #fff3e0; color: #e65100; }
        .priority-emergency { background: #ffebee; color: #c62828; }

        /* 删除按钮 */
        .btn-delete-card {
            position: absolute;
            top: 10px;
            right: 10px;
            opacity: 0;
            transition: all 0.3s;
        }
        .repair-card:hover .btn-delete-card {
            opacity: 1;
        }

        /* 筛选区域 */
        .filter-box { background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 20px; }
        .filter-box .form-control, .filter-box .btn { height: 38px; }
        .filter-box label { font-size: 13px; font-weight: 600; color: #495057; margin-bottom: 5px; }

        /* 空状态 */
        .empty-state { padding: 60px 20px; text-align: center; color: #adb5bd; }
        .empty-state i { font-size: 48px; margin-bottom: 15px; opacity: 0.5; }

        /* 🔥 详情模态框样式 */
        .detail-modal .modal-dialog { max-width: 700px; }
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
        .timeline-item {
            padding: 8px 0;
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 14px;
        }
        .timeline-item i {
            width: 20px;
            text-align: center;
        }
        .timeline-item strong {
            color: #333;
        }
        .rating-stars-detail {
            display: inline-flex;
            gap: 3px;
            font-size: 18px;
        }
        .feedback-box {
            background: #fff9e6;
            padding: 12px;
            border-radius: 6px;
            border-left: 3px solid #ffc107;
            margin-top: 10px;
        }
        .result-box {
            background: #e8f5e9;
            padding: 12px;
            border-radius: 6px;
            border-left: 3px solid #4caf50;
            margin-top: 10px;
        }

        /* 🔥 取消信息样式优化 */
        .cancel-type-badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 8px 12px;
            background: #fff5f5;
            border-radius: 6px;
            border-left: 3px solid #dc3545;
            margin-bottom: 10px;
        }
        .cancel-type-badge i {
            font-size: 16px;
            color: #dc3545;
        }
        .cancel-type-badge strong {
            color: #721c24;
            font-size: 14px;
        }
        .cancel-reason-text {
            padding: 10px 15px;
            background: white;
            border-radius: 6px;
            color: #721c24;
            line-height: 1.6;
        }
    </style>
</head>
<body>

<div class="page-header">
    <div class="container">
        <h2><i class="fas fa-tools mr-2"></i>在线报修</h2>
        <p class="mb-0 opacity-80">设施故障、居家维修，一键直达物业</p>
    </div>
</div>

<div class="container">
    <div class="row">
        <!-- 左侧：提交表单 -->
        <div class="col-lg-4">
            <div class="card-box p-4">
                <h5 class="font-weight-bold mb-4">📝 填写报修单</h5>
                <form id="repairForm">
                    <input type="hidden" name="method" value="submit">

                    <div class="form-group">
                        <label>选择房屋 <span class="text-danger">*</span></label>
                        <select class="form-control" name="houseId" id="houseSelect" required>
                            <option value="">请选择房屋</option>
                        </select>
                        <small class="text-muted">请选择需要维修的房屋</small>
                    </div>

                    <div class="form-group">
                        <label>报修类型 <span class="text-danger">*</span></label>
                        <select class="form-control" name="repairType" required>
                            <option value="plumbing">🚰 水暖管道</option>
                            <option value="electrical">⚡ 电路电器</option>
                            <option value="door_window">🚪 门窗维修</option>
                            <option value="public_facility">🌳 公共设施</option>
                            <option value="other">🔧 其他问题</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>紧急程度</label>
                        <div class="btn-group btn-group-toggle w-100" data-toggle="buttons">
                            <label class="btn btn-outline-secondary active">
                                <input type="radio" name="priority" value="normal" checked> 普通
                            </label>
                            <label class="btn btn-outline-warning">
                                <input type="radio" name="priority" value="urgent"> 加急
                            </label>
                            <label class="btn btn-outline-danger">
                                <input type="radio" name="priority" value="emergency"> 紧急
                            </label>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>问题描述 <span class="text-danger">*</span></label>
                        <textarea class="form-control" name="description" rows="5" placeholder="请详细描述故障情况、发生位置等..." required maxlength="500"></textarea>
                        <small class="text-muted">最多500字</small>
                    </div>

                    <button type="button" class="btn btn-primary btn-block font-weight-bold py-2" onclick="submitRepair()">
                        <i class="fas fa-paper-plane mr-1"></i> 提交申请
                    </button>
                </form>
            </div>
        </div>

        <!-- 右侧：报修记录 -->
        <div class="col-lg-8">
            <div class="card-box p-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="font-weight-bold mb-0">📋 我的报修记录</h5>
                    <button class="btn btn-sm btn-outline-primary" onclick="loadRepairs(1)">
                        <i class="fas fa-sync"></i> 刷新
                    </button>
                </div>

                <!-- 筛选区域 -->
                <div class="filter-box">
                    <div class="row">
                        <div class="col-md-4">
                            <label><i class="fas fa-filter mr-1"></i>报修状态</label>
                            <select class="form-control form-control-sm" id="filterStatus">
                                <option value="">全部状态</option>
                                <option value="pending">待处理</option>
                                <option value="processing">处理中</option>
                                <option value="completed">已完成</option>
                                <option value="cancelled">已取消</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label><i class="fas fa-tag mr-1"></i>报修类型</label>
                            <select class="form-control form-control-sm" id="filterType">
                                <option value="">全部类型</option>
                                <option value="plumbing">水暖管道</option>
                                <option value="electrical">电路电器</option>
                                <option value="door_window">门窗维修</option>
                                <option value="public_facility">公共设施</option>
                                <option value="other">其他问题</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label>&nbsp;</label>
                            <div class="d-flex">
                                <button class="btn btn-primary btn-sm flex-fill mr-2" onclick="applyFilter()">
                                    <i class="fas fa-search"></i> 查询
                                </button>
                                <button class="btn btn-secondary btn-sm flex-fill" onclick="resetFilter()">
                                    <i class="fas fa-redo"></i> 重置
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="repairList">
                    <div class="text-center py-5"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>
                </div>

                <div class="mt-3 d-flex justify-content-between align-items-center" id="paginationBox" style="display:none;">
                    <small class="text-muted">共 <span id="totalCount">0</span> 条记录</small>
                    <div>
                        <button class="btn btn-sm btn-light mr-2" onclick="changePage(-1)" id="btnPrev">
                            <i class="fas fa-chevron-left"></i> 上一页
                        </button>
                        <span id="pageInfo" class="mx-2 text-muted">1/1</span>
                        <button class="btn btn-sm btn-light ml-2" onclick="changePage(1)" id="btnNext">
                            下一页 <i class="fas fa-chevron-right"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 🔥 详情模态框 -->
<div class="modal fade detail-modal" id="detailModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header bg-light">
                <h5 class="modal-title"><i class="fas fa-info-circle mr-2"></i>报修详情</h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body" id="detailContent">
                <div class="text-center py-5"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>
            </div>
            <div class="modal-footer" id="detailActions">
                <!-- 动态按钮 -->
            </div>
        </div>
    </div>
</div>

<!-- 评价模态框 -->
<div class="modal fade" id="rateModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-light">
                <h5 class="modal-title"><i class="fas fa-star text-warning mr-2"></i>服务评价</h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="rateRepairId">
                <div class="form-group text-center">
                    <label class="font-weight-bold">请为本次服务打分</label>
                    <div class="h2 my-3" style="cursor: pointer;">
                        <i class="fas fa-star text-warning rate-star" onclick="setStar(1)"></i>
                        <i class="fas fa-star text-warning rate-star" onclick="setStar(2)"></i>
                        <i class="fas fa-star text-warning rate-star" onclick="setStar(3)"></i>
                        <i class="fas fa-star text-warning rate-star" onclick="setStar(4)"></i>
                        <i class="fas fa-star text-warning rate-star" onclick="setStar(5)"></i>
                    </div>
                    <input type="hidden" id="ratingScore" value="5">
                    <small class="text-muted">点击星星选择评分（默认5星）</small>
                </div>
                <div class="form-group">
                    <label>评价内容（选填）</label>
                    <textarea class="form-control" id="feedbackContent" rows="4" placeholder="请描述您对本次维修服务的评价..." maxlength="500"></textarea>
                    <small class="text-muted">最多500字，可以不填写</small>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-warning" onclick="submitRating()">
                    <i class="fas fa-check"></i> 提交评价
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/layer.js"></script>

<script>
    var contextPath = '${pageContext.request.contextPath}';
    var currentPage = 1;
    var pageSize = 8;
    var totalPages = 1;
    var totalCount = 0;

    $(function() {
        loadMyHouses();
        loadRepairs(1);
    });

    /**
     * ✅ 加载当前业主的房屋列表（优化版 - 支持多套房产详细显示）
     */
    function loadMyHouses() {
        console.log('📥 开始加载业主房屋列表...');

        $.ajax({
            url: contextPath + '/owner/repair',
            type: 'GET',
            data: { method: 'myHouses' },
            dataType: 'json',
            success: function(res) {
                console.log('📦 房屋列表响应:', res);

                if(res.success || res.code === 200) {
                    var houses = res.data || [];
                    var options = '<option value="">请选择房屋</option>';

                    // ========== 无房屋处理 ==========
                    if(houses.length === 0) {
                        console.warn('⚠️ 业主名下暂无房屋');
                        options = '<option value="">您名下暂无房屋</option>';
                        $('#houseSelect').html(options).prop('disabled', true);
                        $('button[onclick="submitRepair()"]').prop('disabled', true).text('暂无房屋，无法报修');
                        return;
                    }

                    // ========== 构建房屋选项（详细信息） ==========
                    console.log('✅ 检测到业主拥有 ' + houses.length + ' 套房产');

                    $.each(houses, function(i, house) {
                        var label = '';

                        // 1️⃣ 房屋编号（必选）
                        if(house.houseId) {
                            label += house.houseId + ' - ';
                        }

                        // 2️⃣ 楼栋单元楼层（核心信息）
                        var location = '';
                        if(house.buildingNo) {
                            location += house.buildingNo + '栋';
                        }
                        if(house.unitNo) {
                            location += house.unitNo + '单元';
                        }
                        if(house.floor) {
                            location += house.floor + '层';
                        }
                        label += location;

                        // 3️⃣ 户型（如果有）
                        if(house.layout && house.layout.trim() !== '') {
                            label += ' (' + house.layout + ')';
                        }

                        // 4️⃣ 面积（如果有）
                        if(house.area && house.area > 0) {
                            label += ' ' + house.area + '㎡';
                        }

                        // 5️⃣ 房屋状态标识（可选）
                        if(house.houseStatus) {
                            var statusMap = {
                                'occupied': ' [已入住]',
                                'rented': ' [出租中]',
                                'vacant': ' [空置]',
                                'renovating': ' [装修中]'
                            };
                            var statusText = statusMap[house.houseStatus] || '';
                            if(statusText) {
                                label += statusText;
                            }
                        }

                        options += '<option value="'+house.houseId+'">'+label+'</option>';

                        console.log('  房产 '+(i+1)+':', label);
                    });

                    $('#houseSelect').html(options).prop('disabled', false);
                    $('button[onclick="submitRepair()"]').prop('disabled', false).html('<i class="fas fa-paper-plane mr-1"></i> 提交申请');

                    // ========== 多套房产提示 ==========
                    if(houses.length > 1) {
                        console.log('💡 提示：业主拥有多套房产，请仔细选择需要报修的房屋');
                    }

                    // ========== 记住上次选择的房屋 ==========
                    var lastHouse = localStorage.getItem('lastSelectedHouse_' + contextPath);
                    if(lastHouse && $('#houseSelect option[value="'+lastHouse+'"]').length > 0) {
                        $('#houseSelect').val(lastHouse);
                        console.log('✅ 自动选中上次报修的房屋:', lastHouse);
                    }

                    console.log('✅ 房屋列表加载完成，共 ' + houses.length + ' 套');

                } else {
                    console.error('❌ 加载房屋列表失败:', res.message);
                    layer.msg(res.message || '加载房屋列表失败', {icon: 2});
                    $('#houseSelect').html('<option value="">加载失败</option>').prop('disabled', true);
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 网络请求失败:', error);
                console.error('响应状态:', xhr.status);
                console.error('响应内容:', xhr.responseText);
                layer.msg('网络请求失败', {icon: 2});
                $('#houseSelect').html('<option value="">加载失败</option>').prop('disabled', true);
            }
        });
    }

    /**
     * ✅ 提交报修（优化版 - 记住选择的房屋）
     */
    function submitRepair() {
        var houseId = $('select[name="houseId"]').val();
        var desc = $('textarea[name="description"]').val();

        if(!houseId) {
            layer.msg('请选择房屋', {icon: 0});
            return;
        }
        if(!desc || desc.trim() === '') {
            layer.msg('请填写问题描述', {icon: 0});
            return;
        }

        var loadIdx = layer.load(1);
        $.ajax({
            url: contextPath + '/owner/repair',
            type: 'POST',
            data: $('#repairForm').serialize(),
            dataType: 'json',
            success: function(res) {
                layer.close(loadIdx);
                if(res.success || res.code === 200) {
                    layer.msg('提交成功', {icon: 1});

                    // ✅ 保存本次选择的房屋（下次自动选中）
                    localStorage.setItem('lastSelectedHouse_' + contextPath, houseId);
                    console.log('✅ 已保存选择的房屋:', houseId);

                    // 重置表单
                    $('#repairForm')[0].reset();
                    $('input[name="method"]').val('submit');
                    $('.btn-group label').removeClass('active');
                    $('.btn-group label:first').addClass('active');
                    $('input[name="priority"][value="normal"]').prop('checked', true);

                    // 重新加载列表
                    loadRepairs(1);

                    // ✅ 恢复上次选择的房屋
                    setTimeout(function() {
                        $('#houseSelect').val(houseId);
                    }, 100);
                } else {
                    layer.msg(res.message || '提交失败', {icon: 2});
                }
            },
            error: function(xhr) {
                layer.close(loadIdx);
                var msg = '网络请求失败';
                try {
                    var res = JSON.parse(xhr.responseText);
                    msg = res.message || msg;
                } catch(e) {}
                layer.msg(msg, {icon: 2});
            }
        });
    }

    /**
     * 应用筛选
     */
    function applyFilter() {
        loadRepairs(1);
    }

    /**
     * 重置筛选
     */
    function resetFilter() {
        $('#filterStatus').val('');
        $('#filterType').val('');
        loadRepairs(1);
    }

    /**
     * 加载报修列表
     */
    function loadRepairs(page) {
        currentPage = page;
        $('#repairList').html('<div class="text-center py-5"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>');

        var filterStatus = $('#filterStatus').val();
        var filterType = $('#filterType').val();

        $.ajax({
            url: contextPath + '/owner/repair',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: currentPage,
                pageSize: pageSize,
                repairStatus: filterStatus,
                repairType: filterType
            },
            dataType: 'json',
            success: function(res) {
                if(res.success || res.code === 200) {
                    var data = res.data || res;
                    var list = data.list || [];
                    var total = data.total || 0;

                    totalCount = total;
                    totalPages = Math.ceil(total / pageSize);
                    if(totalPages === 0) totalPages = 1;

                    renderList(list);
                    updatePagination();
                } else {
                    $('#repairList').html('<div class="text-center text-danger py-5">'+res.message+'</div>');
                }
            },
            error: function() {
                $('#repairList').html('<div class="text-center text-danger py-5">加载失败，请刷新重试</div>');
            }
        });
    }

    /**
     * 🔥 渲染简化的卡片列表（完整优化版）
     */
    function renderList(list) {
        if(list.length === 0) {
            $('#repairList').html(
                '<div class="empty-state">' +
                '<i class="fas fa-inbox"></i>' +
                '<p class="mb-0">暂无报修记录</p>' +
                '</div>'
            );
            return;
        }

        var html = '';
        $.each(list, function(i, item) {
            var statusMap = {
                'pending': {text:'待处理', cls:'status-pending'},
                'processing': {text:'处理中', cls:'status-processing'},
                'completed': {text:'已完成', cls:'status-completed'},
                'cancelled': {text:'已取消', cls:'status-cancelled'}
            };
            var st = statusMap[item.repairStatus] || statusMap['pending'];

            var typeMap = {
                'plumbing':'🚰 水暖管道',
                'electrical':'⚡ 电路电器',
                'door_window':'🚪 门窗维修',
                'public_facility':'🌳 公共设施',
                'other':'🔧 其他问题'
            };
            var typeName = typeMap[item.repairType] || item.repairType;

            var priorityMap = {'normal':'普通','urgent':'加急','emergency':'紧急'};
            var priorityText = priorityMap[item.priority] || '普通';
            var priorityCls = 'priority-' + (item.priority || 'normal');

            var houseInfo = item.houseInfo || item.houseNumber || item.houseName || '';

            html += '<div class="repair-card '+item.repairStatus+'" onclick="showDetail('+item.repairId+')">';

            // 删除按钮（仅已取消）
            if(item.repairStatus === 'cancelled') {
                html += '<button class="btn btn-sm btn-outline-danger btn-delete-card" onclick="event.stopPropagation(); deleteRepair('+item.repairId+')" title="删除记录">' +
                    '<i class="fas fa-trash-alt"></i>' +
                    '</button>';
            }

            // 头部
            html += '<div class="repair-card-header">';
            html += '<h6 class="repair-title">'+typeName+'</h6>';
            html += '<span class="repair-status '+st.cls+'">'+st.text+'</span>';
            html += '</div>';

            // 描述（截断）
            var desc = item.description || '';
            if(desc.length > 50) desc = desc.substring(0, 50) + '...';
            html += '<div class="repair-card-body">'+desc+'</div>';

            // 元信息
            html += '<div class="repair-meta">';
            html += '<div class="repair-meta-item"><span class="priority-badge '+priorityCls+'">'+priorityText+'</span></div>';
            if(houseInfo) {
                html += '<div class="repair-meta-item"><i class="fas fa-home"></i>'+houseInfo+'</div>';
            }
            html += '<div class="repair-meta-item"><i class="far fa-clock"></i>'+formatTime(item.submitTime)+'</div>';

            // 🔥 已取消状态显示取消原因摘要
            if(item.repairStatus === 'cancelled') {
                var cancelHint = '';
                var reason = item.cancelReason || item.repairResult || '';

                if(reason && reason.trim() !== '' && reason !== '<null>' && reason !== 'null') {
                    reason = reason.replace(/^取消原因[：:]\s*/g, '');

                    var match = reason.match(/^\[([^\]]+)\](.*)$/);
                    if(match) {
                        cancelHint = match[1]; // 只显示类型
                    } else {
                        cancelHint = reason.length > 15 ? reason.substring(0, 15) + '...' : reason;
                    }

                    html += '<div class="repair-meta-item text-danger">';
                    html += '<i class="fas fa-info-circle"></i>' + cancelHint;
                    html += '</div>';
                }
            }

            html += '</div>';

            html += '</div>';
        });

        $('#repairList').html(html);
    }

    /**
     * 🔥 显示详情模态框
     */
    function showDetail(id) {
        $('#detailModal').modal('show');
        $('#detailContent').html('<div class="text-center py-5"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>');
        $('#detailActions').html('');

        $.ajax({
            url: contextPath + '/owner/repair',
            type: 'GET',
            data: { method: 'detail', repairId: id },
            dataType: 'json',
            success: function(res) {
                if(res.success || res.code === 200) {
                    var item = res.data;
                    renderDetail(item);
                } else {
                    $('#detailContent').html('<div class="alert alert-danger">'+res.message+'</div>');
                }
            },
            error: function() {
                $('#detailContent').html('<div class="alert alert-danger">加载失败</div>');
            }
        });
    }

    /**
     * 🔥 渲染详情内容（完整优化版）
     */
    function renderDetail(item) {
        var statusMap = {
            'pending': {text:'待处理', badge:'badge-secondary', icon:'clock'},
            'processing': {text:'处理中', badge:'badge-info', icon:'tools'},
            'completed': {text:'已完成', badge:'badge-success', icon:'check-circle'},
            'cancelled': {text:'已取消', badge:'badge-danger', icon:'ban'}
        };
        var st = statusMap[item.repairStatus] || statusMap['pending'];

        var typeMap = {
            'plumbing':'🚰 水暖管道',
            'electrical':'⚡ 电路电器',
            'door_window':'🚪 门窗维修',
            'public_facility':'🌳 公共设施',
            'other':'🔧 其他问题'
        };
        var typeName = typeMap[item.repairType] || item.repairType;

        var priorityMap = {'normal':'普通','urgent':'加急','emergency':'紧急'};
        var priorityText = priorityMap[item.priority] || '普通';

        var html = '';

        // 基本信息
        html += '<div class="detail-section">';
        html += '<div class="detail-section-title"><i class="fas fa-info-circle text-primary"></i>基本信息</div>';
        html += '<div class="detail-content">';
        html += '<div class="mb-2"><strong>报修编号：</strong>#'+item.repairId+'</div>';
        html += '<div class="mb-2"><strong>报修类型：</strong>'+typeName+'</div>';
        html += '<div class="mb-2"><strong>紧急程度：</strong>'+priorityText+'</div>';
        html += '<div class="mb-2"><strong>当前状态：</strong><span class="badge '+st.badge+' ml-2"><i class="fas fa-'+st.icon+' mr-1"></i>'+st.text+'</span></div>';
        var houseInfo = item.houseInfo || item.houseNumber || item.houseName || '';
        if(houseInfo) {
            html += '<div class="mb-2"><strong>报修房屋：</strong>'+houseInfo+'</div>';
        }
        html += '<div><strong>提交时间：</strong>'+formatTime(item.submitTime)+'</div>';
        html += '</div>';
        html += '</div>';

        // 问题描述
        html += '<div class="detail-section">';
        html += '<div class="detail-section-title"><i class="fas fa-file-alt text-warning"></i>问题描述</div>';
        html += '<div class="detail-content">'+item.description+'</div>';
        html += '</div>';

        // 处理进度
        if(item.repairStatus !== 'pending' && item.repairStatus !== 'cancelled') {
            html += '<div class="detail-section">';
            html += '<div class="detail-section-title"><i class="fas fa-tasks text-info"></i>处理进度</div>';
            html += '<div class="detail-content">';

            var handlerName = item.handlerName || item.handler || item.workerName || '';
            var handlerPhone = item.handlerPhone || item.phone || item.workerPhone || '';

            if(handlerName) {
                html += '<div class="timeline-item">';
                html += '<i class="fas fa-user-tie text-primary"></i>';
                html += '<div><strong>维修人员：</strong>'+handlerName;
                if(handlerPhone) {
                    html += ' <a href="tel:'+handlerPhone+'" class="text-success ml-2"><i class="fas fa-phone-alt"></i> '+handlerPhone+'</a>';
                }
                html += '</div></div>';

                var acceptTime = item.acceptTime || item.receiveTime || '';
                if(acceptTime) {
                    html += '<div class="timeline-item">';
                    html += '<i class="fas fa-check text-success"></i>';
                    html += '<div><strong>接单时间：</strong>'+formatTime(acceptTime)+'</div>';
                    html += '</div>';
                }

                if(item.repairStatus === 'completed') {
                    var completeTime = item.completeTime || item.finishTime || '';
                    if(completeTime) {
                        html += '<div class="timeline-item">';
                        html += '<i class="fas fa-flag-checkered text-success"></i>';
                        html += '<div><strong>完成时间：</strong>'+formatTime(completeTime)+'</div>';
                        html += '</div>';
                    }
                }
            }

            html += '</div>';
            html += '</div>';
        }

        // 维修结果
        if(item.repairStatus === 'completed' && item.repairResult &&
            item.repairResult.trim() !== '' && item.repairResult !== '<null>' && item.repairResult !== 'null') {
            html += '<div class="detail-section">';
            html += '<div class="detail-section-title"><i class="fas fa-wrench text-success"></i>维修结果</div>';
            html += '<div class="result-box">'+item.repairResult+'</div>';
            html += '</div>';
        }

        // 我的评价
        if(item.repairStatus === 'completed' && item.satisfactionRating) {
            html += '<div class="detail-section">';
            html += '<div class="detail-section-title"><i class="fas fa-star text-warning"></i>我的评价</div>';
            html += '<div class="detail-content">';

            var stars = '';
            for(var k=0; k<5; k++) {
                stars += k < item.satisfactionRating ? '<i class="fas fa-star text-warning"></i>' : '<i class="far fa-star text-warning"></i>';
            }
            html += '<div class="rating-stars-detail mb-2">'+stars+'</div>';

            if(item.feedback && item.feedback.trim() !== '' && item.feedback !== '<null>' && item.feedback !== 'null') {
                html += '<div class="feedback-box">'+item.feedback+'</div>';
            }

            html += '</div>';
            html += '</div>';
        }

        // 🔥 取消信息（完整优化版）
        if(item.repairStatus === 'cancelled') {
            var cancelReason = '';
            var cancelBy = ''; // 取消人/类型

            // 1. 优先从 cancelReason 字段获取
            if(item.cancelReason && item.cancelReason.trim() !== '' &&
                item.cancelReason !== '<null>' && item.cancelReason !== 'null') {
                cancelReason = item.cancelReason;
            }

            // 2. 如果没有，尝试从 repairResult 获取
            if(!cancelReason && item.repairResult && item.repairResult.trim() !== '' &&
                item.repairResult !== '<null>' && item.repairResult !== 'null') {
                cancelReason = item.repairResult;
            }

            // 3. 解析和格式化
            if(cancelReason) {
                // 移除可能的多余前缀
                cancelReason = cancelReason.replace(/^取消原因[：:]\s*/g, '');

                // 解析格式：[管理员驳回]1111111
                var match = cancelReason.match(/^\[([^\]]+)\](.*)$/);
                if(match) {
                    cancelBy = match[1].trim(); // 管理员驳回
                    cancelReason = match[2].trim(); // 1111111
                }

                html += '<div class="detail-section">';
                html += '<div class="detail-section-title"><i class="fas fa-ban text-danger"></i>取消信息</div>';
                html += '<div class="alert alert-danger mb-0">';

                // 显示取消类型（如果有）
                if(cancelBy) {
                    var iconMap = {
                        '管理员驳回': 'fa-user-shield',
                        '业主取消': 'fa-user',
                        '系统取消': 'fa-robot',
                        '超时取消': 'fa-clock'
                    };
                    var icon = iconMap[cancelBy] || 'fa-info-circle';
                    html += '<div class="cancel-type-badge">';
                    html += '<i class="fas ' + icon + '"></i>';
                    html += '<strong>' + cancelBy + '</strong>';
                    html += '</div>';
                }

                // 显示取消原因
                if(cancelReason && cancelReason !== '') {
                    html += '<div class="cancel-reason-text">' + cancelReason + '</div>';
                } else if(cancelBy) {
                    html += '<div class="cancel-reason-text text-muted"><i class="fas fa-exclamation-triangle mr-1"></i>未填写具体原因</div>';
                }

                html += '</div>';
                html += '</div>';
            } else {
                // 没有任何取消信息
                html += '<div class="detail-section">';
                html += '<div class="detail-section-title"><i class="fas fa-ban text-danger"></i>取消信息</div>';
                html += '<div class="alert alert-warning mb-0">';
                html += '<i class="fas fa-info-circle mr-2"></i>该报修已取消，但未记录取消原因';
                html += '</div>';
                html += '</div>';
            }
        }

        $('#detailContent').html(html);

        // 🔥 动态按钮
        var actions = '<button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>';

        if(item.repairStatus === 'pending') {
            actions += '<button type="button" class="btn btn-danger" onclick="cancelRepair('+item.repairId+')">取消申请</button>';
        } else if(item.repairStatus === 'completed' && !item.satisfactionRating) {
            actions += '<button type="button" class="btn btn-warning" onclick="openRate('+item.repairId+')"><i class="fas fa-star"></i> 评价</button>';
        } else if(item.repairStatus === 'cancelled') {
            actions += '<button type="button" class="btn btn-danger" onclick="deleteRepair('+item.repairId+')"><i class="fas fa-trash-alt"></i> 删除</button>';
        }

        $('#detailActions').html(actions);
    }

    /**
     * 更新分页按钮状态
     */
    function updatePagination() {
        $('#totalCount').text(totalCount);

        if(totalPages <= 1) {
            $('#paginationBox').hide();
        } else {
            $('#paginationBox').show();
            $('#pageInfo').text(currentPage + ' / ' + totalPages);
            $('#btnPrev').prop('disabled', currentPage === 1);
            $('#btnNext').prop('disabled', currentPage === totalPages);
        }
    }

    function changePage(delta) {
        var newPage = currentPage + delta;
        if (newPage >= 1 && newPage <= totalPages) {
            loadRepairs(newPage);
        }
    }

    /**
     * 取消报修
     */
    function cancelRepair(id) {
        $('#detailModal').modal('hide');
        layer.prompt({
            formType: 2,
            value: '',
            title: '请输入取消原因',
            area: ['350px', '150px']
        }, function(value, index, elem){
            if(!value || value.trim() === '') {
                layer.msg('取消原因不能为空', {icon: 0});
                return;
            }

            var loadIdx = layer.load(1);
            $.post(contextPath + '/owner/repair', {
                method: 'cancel',
                repairId: id,
                cancelReason: value.trim()
            }, function(res) {
                layer.close(loadIdx);
                if(res.success || res.code === 200) {
                    layer.msg('已取消', {icon: 1});
                    layer.close(index);
                    loadRepairs(currentPage);
                } else {
                    layer.msg(res.message || '取消失败', {icon: 2});
                }
            }, 'json').fail(function() {
                layer.close(loadIdx);
                layer.msg('网络请求失败', {icon: 2});
            });
        });
    }

    /**
     * 删除报修记录
     */
    function deleteRepair(id) {
        $('#detailModal').modal('hide');
        layer.confirm('确定要删除这条报修记录吗？', {
            icon: 3,
            title: '确认删除',
            btn: ['确定', '取消']
        }, function(index) {
            var loadIdx = layer.load(1);
            $.post(contextPath + '/owner/repair', {
                method: 'delete',
                repairId: id
            }, function(res) {
                layer.close(loadIdx);
                if(res.success || res.code === 200) {
                    layer.msg('删除成功', {icon: 1});
                    layer.close(index);
                    loadRepairs(currentPage);
                } else {
                    layer.msg(res.message || '删除失败', {icon: 2});
                }
            }, 'json').fail(function() {
                layer.close(loadIdx);
                layer.msg('网络请求失败', {icon: 2});
            });
        });
    }

    /**
     * 打开评价模态框
     */
    function openRate(id) {
        $('#detailModal').modal('hide');
        $('#rateRepairId').val(id);
        setStar(5);
        $('#feedbackContent').val('');
        $('#rateModal').modal('show');
    }

    /**
     * 设置星星样式
     */
    function setStar(n) {
        $('#ratingScore').val(n);
        $('.rate-star').each(function(index) {
            if(index < n) {
                $(this).removeClass('far').addClass('fas');
            } else {
                $(this).removeClass('fas').addClass('far');
            }
        });
    }

    /**
     * 提交评价
     */
    function submitRating() {
        var rating = $('#ratingScore').val();
        var feedback = $('#feedbackContent').val().trim();

        if(!rating || rating < 1 || rating > 5) {
            layer.msg('请选择评分', {icon: 0});
            return;
        }

        var loadIdx = layer.load(1);
        $.post(contextPath + '/owner/repair', {
            method: 'rate',
            repairId: $('#rateRepairId').val(),
            rating: rating,
            feedback: feedback
        }, function(res) {
            layer.close(loadIdx);
            if(res.success || res.code === 200) {
                layer.msg('评价成功', {icon: 1});
                $('#rateModal').modal('hide');
                loadRepairs(currentPage);
            } else {
                layer.msg(res.message || '评价失败', {icon: 2});
            }
        }, 'json').fail(function() {
            layer.close(loadIdx);
            layer.msg('网络请求失败', {icon: 2});
        });
    }

    /**
     * 时间格式化
     */
    function formatTime(ts) {
        if(!ts) return '';
        var d = new Date(ts);
        var month = (d.getMonth() + 1).toString().padStart(2, '0');
        var day = d.getDate().toString().padStart(2, '0');
        var hour = d.getHours().toString().padStart(2, '0');
        var minute = d.getMinutes().toString().padStart(2, '0');
        return d.getFullYear() + '-' + month + '-' + day + ' ' + hour + ':' + minute;
    }
</script>
</body>
</html>
