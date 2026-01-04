<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>投诉管理 - 物业管理系统</title>

    <!-- 引入 CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/theme/default/layer.css">

    <style>
        body {
            background-color: #f5f7fa;
            font-family: 'Microsoft YaHei', Arial, sans-serif;
        }

        /* 渐变头部 */
        .page-header {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

        /* 搜索框区域 */
        .search-box {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
            margin-bottom: 20px;
        }

        /* 表格区域 */
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

        /* 分页区域 */
        .pagination-wrapper {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
            margin-top: 20px;
        }

        /* 模态框样式 */
        .modal-header {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

        /* 状态标签 */
        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
        }

        .status-pending {
            background-color: #fff3e0;
            color: #f57c00;
        }

        .status-processing {
            background-color: #e3f2fd;
            color: #1976d2;
        }

        .status-resolved {
            background-color: #e8f5e9;
            color: #388e3c;
        }

        .status-closed {
            background-color: #f5f5f5;
            color: #757575;
        }

        /* 🔥 已撤销状态样式 */
        .status-cancelled {
            background-color: #eeeeee;
            color: #999999;
        }

        /* 类型标签 */
        .type-badge {
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }
        .type-service { background-color: #e3f2fd; color: #1976d2; }
        .type-environment { background-color: #e8f5e9; color: #388e3c; }
        .type-facility { background-color: #fff3e0; color: #f57c00; }
        .type-fee { background-color: #fce4ec; color: #c2185b; }
        .type-other { background-color: #f3e5f5; color: #7b1fa2; }

        /* 复选框样式 */
        .checkbox-cell { width: 40px; text-align: center; }
        input[type="checkbox"] { width: 18px; height: 18px; cursor: pointer; }

        /* 详情卡片样式 */
        .detail-card {
            padding: 20px;
        }

        .detail-header {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .detail-header h4 {
            margin: 0;
            font-size: 20px;
        }

        .detail-header p {
            margin: 5px 0 0 0;
            opacity: 0.9;
            font-size: 14px;
        }

        .detail-section {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 15px;
        }

        .detail-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }

        .detail-item {
            margin-bottom: 10px;
        }

        .detail-item small {
            color: #6c757d;
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
        }

        .detail-item .value {
            font-weight: bold;
            color: #212529;
        }

        /* ==================== 时间线样式 ==================== */
        .timeline {
            position: relative;
            padding: 20px 0;
        }

        .timeline::before {
            content: '';
            position: absolute;
            left: 20px;
            top: 0;
            bottom: 0;
            width: 2px;
            background: linear-gradient(180deg, #f093fb 0%, #f5576c 100%);
        }

        .timeline-item {
            position: relative;
            padding-left: 60px;
            margin-bottom: 25px;
        }

        .timeline-item:last-child {
            margin-bottom: 0;
        }

        .timeline-marker {
            position: absolute;
            left: 10px;
            top: 0;
            width: 24px;
            height: 24px;
            border-radius: 50%;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            box-shadow: 0 2px 8px rgba(240, 147, 251, 0.4);
            z-index: 2;
        }

        .timeline-marker-append {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            box-shadow: 0 2px 8px rgba(79, 172, 254, 0.4);
        }

        .timeline-content {
            background: white;
            border: 2px solid #f093fb;
            border-radius: 10px;
            padding: 15px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            transition: all 0.3s ease;
        }

        .timeline-content:hover {
            box-shadow: 0 4px 12px rgba(240, 147, 251, 0.3);
            transform: translateY(-2px);
        }

        .timeline-content-append {
            border-color: #4facfe;
        }

        .timeline-content-append:hover {
            box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
        }

        .timeline-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 12px;
            padding-bottom: 10px;
            border-bottom: 1px solid #f0f0f0;
        }

        .timeline-title {
            font-weight: 600;
            color: #495057;
            font-size: 14px;
        }

        .timeline-title i {
            margin-right: 5px;
            color: #f093fb;
        }

        .timeline-content-append .timeline-title i {
            color: #4facfe;
        }

        .timeline-time {
            font-size: 12px;
            color: #999;
        }

        .timeline-body {
            line-height: 1.8;
            color: #333;
            font-size: 14px;
            white-space: pre-wrap;
            word-break: break-word;
        }

        /* 回复框增强样式 */
        .reply-box {
            background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
            padding: 20px;
            border-radius: 10px;
            border-left: 4px solid #4caf50;
            margin-top: 10px;
            line-height: 1.6;
            box-shadow: 0 2px 8px rgba(76, 175, 80, 0.2);
        }

        /* 🔥 撤销/驳回回复框样式 */
        .reply-box.cancelled {
            background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
            border-left-color: #999;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        /* 统计卡片样式 */
        .stat-card {
            text-align: center;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 15px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .stat-card i {
            font-size: 32px;
            margin-bottom: 10px;
        }

        .stat-card h3 {
            font-size: 32px;
            font-weight: bold;
            margin: 10px 0;
        }

        .stat-card p {
            margin: 0;
            font-size: 14px;
        }

        .stat-info { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
        .stat-warning { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; }
        .stat-primary { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
        .stat-success { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: white; }

        /* 统计表格 */
        .stat-table {
            margin-top: 20px;
        }

        .stat-table thead th {
            background-color: #f8f9fa;
            font-weight: 600;
        }

        /* ==================== 投诉详情弹窗滚动条美化 ==================== */

        /* Layer弹窗内容区域滚动条 */
        .layui-layer-content {
            overflow-y: auto !important;
            overflow-x: hidden !important;
        }

        /* 滚动条整体样式 */
        .layui-layer-content::-webkit-scrollbar {
            width: 10px;
        }

        /* 滚动条轨道 */
        .layui-layer-content::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 10px;
            margin: 5px 0;
        }

        /* 滚动条滑块 (粉色渐变 - 匹配投诉管理主题) */
        .layui-layer-content::-webkit-scrollbar-thumb {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            border-radius: 10px;
            transition: background 0.3s;
        }

        /* 滚动条滑块悬停效果 */
        .layui-layer-content::-webkit-scrollbar-thumb:hover {
            background: linear-gradient(135deg, #e082ea 0%, #e4465b 100%);
        }

        /* 滚动条滑块激活效果 */
        .layui-layer-content::-webkit-scrollbar-thumb:active {
            background: linear-gradient(135deg, #d071d9 0%, #d3354a 100%);
        }

        /* 响应式适配 */
        @media (max-width: 1366px) {
            .layui-layer-page .layui-layer-content {
                max-height: 70vh !important;
            }
        }

        @media (max-width: 1024px) {
            .layui-layer-page .layui-layer-content {
                max-height: 65vh !important;
            }
        }

        /* ==================== 🔥 分页样式（必须放在最后） ==================== */
        .pagination {
            margin: 0 !important;
            display: flex !important;
        }

        .pagination .page-item {
            margin: 0 3px !important;
        }

        .pagination .page-link {
            border-radius: 5px !important;
            color: #f093fb !important;
            border: 1px solid #dee2e6 !important;
            padding: 8px 12px !important;
            transition: all 0.3s !important;
            font-size: 14px !important;
        }

        .pagination .page-link:hover {
            background-color: #f093fb !important;
            color: white !important;
            border-color: #f093fb !important;
        }

        .pagination .page-item.active .page-link {
            background-color: #f093fb !important;
            border-color: #f093fb !important;
            color: white !important;
            font-weight: bold !important;
        }

        .pagination .page-item.disabled .page-link {
            background-color: #f8f9fa !important;
            border-color: #dee2e6 !important;
            color: #6c757d !important;
            cursor: not-allowed !important;
        }

        #pageInfo {
            line-height: 38px;
            font-size: 14px;
            color: #495057;
            font-weight: 500;
        }
    </style>
</head>
<body>

<div class="container-fluid">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-comments"></i> 投诉管理</h2>
        <p>处理业主投诉建议，及时响应并解决问题，提升物业服务质量</p>
    </div>

    <!-- 搜索和操作区 -->
    <div class="search-box">
        <div class="row mb-3">
            <div class="col-md-3">
                <input type="text" class="form-control" id="searchKeyword"
                       placeholder="搜索标题或内容">
            </div>
            <div class="col-md-2">
                <select class="form-control" id="filterType">
                    <option value="">全部类型</option>
                    <option value="service">服务</option>
                    <option value="environment">环境</option>
                    <option value="facility">设施</option>
                    <option value="fee">费用</option>
                    <option value="other">其他</option>
                </select>
            </div>
            <div class="col-md-2">
                <select class="form-control" id="filterStatus">
                    <option value="">全部状态</option>
                    <option value="pending">待处理</option>
                    <option value="processing">处理中</option>
                    <option value="resolved">已解决</option>
                    <option value="closed">已关闭</option>
                </select>
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary btn-block" onclick="searchComplaint()">
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
                <div class="d-flex" style="gap: 10px;">
                    <button class="btn btn-success" onclick="showAddModal()">
                        <i class="fas fa-plus"></i> 新增投诉
                    </button>
                    <button class="btn btn-info" onclick="showStatistics()">
                        <i class="fas fa-chart-bar"></i> 统计分析
                    </button>
                    <button class="btn btn-danger" onclick="batchDelete()">
                        <i class="fas fa-trash-alt"></i> 批量删除
                    </button>
                    <button class="btn btn-primary" onclick="batchAssign()">
                        <i class="fas fa-user-check"></i> 批量分配
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
                    <th>投诉ID</th>
                    <th>业主</th>
                    <th>类型</th>
                    <th>标题</th>
                    <th>状态</th>
                    <th>提交时间</th>
                    <th>处理人</th>
                    <th width="320">操作</th>
                </tr>
                </thead>
                <tbody id="complaintTableBody">
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

<!-- 添加投诉模态框 -->
<div class="modal fade" id="complaintModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">提交投诉</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="complaintForm">
                    <div class="form-group">
                        <label class="form-label">业主ID</label>
                        <input type="text" class="form-control" id="ownerId"
                               name="ownerId" placeholder="留空表示匿名投诉">
                    </div>

                    <div class="form-group">
                        <label class="form-label required">投诉类型</label>
                        <select class="form-control" id="complaintType" name="complaintType" required>
                            <option value="">请选择</option>
                            <option value="service">服务</option>
                            <option value="environment">环境</option>
                            <option value="facility">设施</option>
                            <option value="fee">费用</option>
                            <option value="other">其他</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label required">投诉标题</label>
                        <input type="text" class="form-control" id="title"
                               name="title" required placeholder="请输入投诉标题">
                    </div>

                    <div class="form-group">
                        <label class="form-label required">投诉内容</label>
                        <textarea class="form-control" id="content" name="content"
                                  rows="5" required placeholder="请详细描述投诉内容"></textarea>
                    </div>

                    <div class="form-check">
                        <input type="checkbox" class="form-check-input" id="isAnonymous"
                               name="isAnonymous" value="1">
                        <label class="form-check-label" for="isAnonymous">
                            匿名投诉
                        </label>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="saveComplaint()">提交</button>
            </div>
        </div>
    </div>
</div>

<!-- 回复投诉模态框 -->
<div class="modal fade" id="replyModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title">回复投诉</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="replyForm">
                    <input type="hidden" id="replyComplaintId">

                    <div class="form-group">
                        <label class="form-label required">回复内容</label>
                        <textarea class="form-control" id="replyContent"
                                  rows="5" required placeholder="请输入回复内容"></textarea>
                    </div>

                    <div class="form-group">
                        <label class="form-label required">处理后状态</label>
                        <select class="form-control" id="newStatus" required>
                            <option value="resolved" selected>已解决</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-info" onclick="confirmReply()">提交回复</button>
            </div>
        </div>
    </div>
</div>

<!-- 批量分配模态框 -->
<div class="modal fade" id="assignModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">批量分配处理人</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="assignForm">
                    <div class="form-group">
                        <label class="form-label required">选择处理人</label>
                        <select class="form-control" id="assignHandlerId" required>
                            <option value="">请选择处理人</option>
                            <option value="1">系统管理员</option>
                            <option value="2">张会计</option>
                        </select>
                    </div>
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i>
                        已选择 <strong><span id="selectedCount">0</span></strong> 条投诉
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="confirmAssign()">确认分配</button>
            </div>
        </div>
    </div>
</div>

<!-- 引入 JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/layer.js"></script>

<script>
    // 🔥 全局变量：当前用户ID
    var currentUserId = <c:out value="${sessionScope.user.userId}" default="null" />;
    if (!currentUserId) {
        currentUserId = <c:out value="${sessionScope.currentUser.userId}" default="1" />;
    }

    var currentPage = 1;
    var pageSize = 10;
    var totalCount = 0;

    // 页面加载完成后执行
    $(document).ready(function() {
        console.log('✅ 页面加载完成');
        console.log('📌 当前用户ID:', currentUserId);
        console.log('📌 Context Path:', '${pageContext.request.contextPath}');

        // 加载投诉列表
        loadComplaintList(1);

        // 回车搜索
        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) {
                searchComplaint();
            }
        });
    });

    /**
     * 加载投诉列表
     */
    function loadComplaintList(pageNum) {
        currentPage = pageNum || currentPage;
        var keyword = $('#searchKeyword').val() || '';
        var complaintType = $('#filterType').val() || '';
        var complaintStatus = $('#filterStatus').val() || '';

        console.log('🔍 加载投诉列表，参数:', {
            pageNum: currentPage,
            pageSize: pageSize,
            keyword: keyword,
            complaintType: complaintType,
            complaintStatus: complaintStatus
        });

        // 显示加载中
        $('#complaintTableBody').html('<tr><td colspan="9" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/',
            type: 'GET',
            dataType: 'json',
            data: {
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: keyword,
                complaintType: complaintType,
                complaintStatus: complaintStatus
            },
            success: function(response) {
                console.log('✅ 数据加载成功:', response);

                if (response && response.list) {
                    renderComplaintTable(response.list);
                    totalCount = response.totalCount || 0;
                    console.log('🔢 设置 totalCount =', totalCount);
                    console.log('📄 准备渲染分页...');
                    renderPagination();
                    console.log('✅ 分页渲染完成');
                } else {
                    console.error('❌ 响应数据格式错误:', response);
                    $('#complaintTableBody').html('<tr><td colspan="9" class="text-center text-danger">数据格式错误</td></tr>');
                    layer.msg('数据格式错误', {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载失败:', {xhr: xhr, status: status, error: error});
                $('#complaintTableBody').html('<tr><td colspan="9" class="text-center text-danger">加载失败，请稍后重试</td></tr>');
            }
        });
    }

    /**
     * 🔥 渲染投诉表格 (修复版)
     */
    function renderComplaintTable(complaints) {
        console.log('📋 渲染表格，记录数:', complaints ? complaints.length : 0);

        var tbody = $('#complaintTableBody');
        tbody.empty();

        if (!complaints || complaints.length === 0) {
            tbody.append('<tr><td colspan="9" class="text-center text-muted">暂无数据</td></tr>');
            return;
        }

        $.each(complaints, function(i, complaint) {
            var status = complaint.complaintStatus;
            var reply = complaint.reply;

            // 🔥 判断是否为撤销/驳回
            var isCancelled = (status === 'closed' && reply &&
                (reply.indexOf('【业主主动撤销】') > -1 || reply.indexOf('【管理员驳回】') > -1));

            // 类型样式
            var typeClass = 'type-' + complaint.complaintType;
            var typeName = complaint.complaintTypeName || getTypeName(complaint.complaintType);

            // 🔥 状态显示
            var displayStatus = isCancelled ? 'cancelled' : status;
            var statusClass = 'status-' + displayStatus;
            var statusName = isCancelled ? '已撤销' : (complaint.complaintStatusName || getStatusName(status));

            // 业主信息（匿名处理）
            var ownerInfo = complaint.isAnonymous === 1 ? '匿名用户' : (complaint.ownerName || '-');

            // ✅ 复选框添加 data-status 属性
            var row = '<tr>' +
                '<td class="checkbox-cell"><input type="checkbox" class="row-checkbox" value="' + complaint.complaintId + '" data-status="' + status + '" data-cancelled="' + isCancelled + '"></td>' +
                '<td>' + complaint.complaintId + '</td>' +
                '<td>' + ownerInfo + '</td>' +
                '<td><span class="type-badge ' + typeClass + '">' + typeName + '</span></td>' +
                '<td style="max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="' + (complaint.title || '') + '">' + (complaint.title || '') + '</td>' +
                '<td><span class="status-badge ' + statusClass + '">' + statusName + '</span></td>' +
                '<td>' + formatDate(complaint.submitTime) + '</td>' +
                '<td>' + (complaint.handlerName || '-') + '</td>' +
                '<td>' +
                '<button class="btn btn-sm btn-info btn-action" onclick="viewComplaint(' + complaint.complaintId + ')" title="查看详情"><i class="fas fa-eye"></i> 查看</button>';

            // 🔥 操作按钮逻辑

            if (isCancelled) {
                // 已撤销：只允许删除
                row += '<button class="btn btn-sm btn-danger btn-action" onclick="deleteComplaint(' + complaint.complaintId + ')" title="删除"><i class="fas fa-trash"></i> 删除</button>';
            } else {
                // 1. 待处理：可【受理】或【驳回】
                if (status === 'pending') {
                    row += '<button class="btn btn-sm btn-warning btn-action" onclick="acceptComplaint(' + complaint.complaintId + ')" title="受理"><i class="fas fa-hand-paper"></i> 受理</button>';
                    row += '<button class="btn btn-sm btn-secondary btn-action" onclick="openCancelDialog(' + complaint.complaintId + ', \'驳回\')" title="驳回"><i class="fas fa-ban"></i> 驳回</button>';
                }

                // 2. 处理中：可【回复】或【终止】
                if (status === 'processing') {
                    row += '<button class="btn btn-sm btn-primary btn-action" onclick="openReplyModal(' + complaint.complaintId + ')" title="回复"><i class="fas fa-reply"></i> 回复</button>';
                    row += '<button class="btn btn-sm btn-secondary btn-action" onclick="openCancelDialog(' + complaint.complaintId + ', \'终止\')" title="终止处理"><i class="fas fa-stop-circle"></i> 终止</button>';
                }

                // 3. 已解决：允许【删除】
                if (status === 'resolved') {
                    row += '<button class="btn btn-sm btn-danger btn-action" onclick="deleteComplaint(' + complaint.complaintId + ')" title="删除"><i class="fas fa-trash"></i> 删除</button>';
                }

                // 4. 已关闭（非撤销）：允许【删除】
                if (status === 'closed') {
                    row += '<button class="btn btn-sm btn-danger btn-action" onclick="deleteComplaint(' + complaint.complaintId + ')" title="删除"><i class="fas fa-trash"></i> 删除</button>';
                }
            }

            row += '</td></tr>';
            tbody.append(row);
        });
        console.log('✅ 表格渲染完成，totalCount =', totalCount);
    }

    // 辅助函数：类型名称映射
    function getTypeName(type) {
        var map = {
            'service': '服务',
            'environment': '环境',
            'facility': '设施',
            'fee': '费用',
            'other': '其他'
        };
        return map[type] || type;
    }

    // 辅助函数：状态名称映射
    function getStatusName(status) {
        var map = {
            'pending': '待处理',
            'processing': '处理中',
            'resolved': '已解决',
            'closed': '已关闭'
        };
        return map[status] || status;
    }

    /**
     * 渲染分页（修复版）
     */
    function renderPagination() {
        var totalPages = Math.ceil(totalCount / pageSize);

        console.log('🔢 分页参数:', {
            totalCount: totalCount,
            pageSize: pageSize,
            totalPages: totalPages,
            currentPage: currentPage
        });

        $('#pageInfo').text('共 ' + totalCount + ' 条记录');

        var pagination = $('#pagination');
        pagination.empty();

        // 🔥 即使只有 1 页也显示分页（方便用户看到总数）
        if (totalPages <= 0) {
            console.log('⚠️ 没有数据，不显示分页');
            return;
        }

        // 上一页
        var prevDisabled = currentPage === 1 ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + prevDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage > 1 ? 'loadComplaintList(' + (currentPage - 1) + ')' : 'return false') + '">上一页</a>' +
            '</li>'
        );

        // 🔥 只有 1 页时也显示页码
        if (totalPages === 1) {
            pagination.append(
                '<li class="page-item active">' +
                '<a class="page-link" href="javascript:void(0)">1</a>' +
                '</li>'
            );
        } else {
            // 多页时的逻辑
            var startPage = Math.max(1, currentPage - 2);
            var endPage = Math.min(totalPages, currentPage + 2);

            if (startPage > 1) {
                pagination.append('<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadComplaintList(1)">1</a></li>');
                if (startPage > 2) {
                    pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
                }
            }

            for (var i = startPage; i <= endPage; i++) {
                var activeClass = i === currentPage ? 'active' : '';
                pagination.append(
                    '<li class="page-item ' + activeClass + '">' +
                    '<a class="page-link" href="javascript:void(0)" onclick="loadComplaintList(' + i + ')">' + i + '</a>' +
                    '</li>'
                );
            }

            if (endPage < totalPages) {
                if (endPage < totalPages - 1) {
                    pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
                }
                pagination.append('<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadComplaintList(' + totalPages + ')">' + totalPages + '</a></li>');
            }
        }

        // 下一页
        var nextDisabled = currentPage === totalPages ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + nextDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage < totalPages ? 'loadComplaintList(' + (currentPage + 1) + ')' : 'return false') + '">下一页</a>' +
            '</li>'
        );

        console.log('✅ 分页渲染完成，HTML:', pagination.html().substring(0, 100) + '...');
    }

    /**
     * 显示添加模态框
     */
    function showAddModal() {
        $('#complaintForm')[0].reset();
        $('#modalTitle').text('提交投诉');
        $('#complaintModal').modal('show');
    }

    /**
     * 保存投诉
     */
    function saveComplaint() {
        var form = $('#complaintForm')[0];
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        var data = {
            ownerId: $('#ownerId').val() || null,
            complaintType: $('#complaintType').val(),
            title: $('#title').val(),
            content: $('#content').val(),
            isAnonymous: $('#isAnonymous').is(':checked') ? 1 : 0
        };

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/submit',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data),
            success: function(res) {
                if (res.success) {
                    layer.msg('提交成功', {icon: 1});
                    $('#complaintModal').modal('hide');
                    loadComplaintList(1);
                } else {
                    layer.msg(res.message || '提交失败', {icon: 2});
                }
            },
            error: function(xhr) {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * ✨ 查看投诉详情 (美化版 - 时间线展示追加内容 + 滚动条)
     */
    function viewComplaint(complaintId) {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/detail/' + complaintId,
            type: 'GET',
            dataType: 'json',
            success: function(res) {
                var complaint = res.data || res;
                if (!complaint) {
                    layer.msg('获取详情失败', {icon: 2});
                    return;
                }

                var status = complaint.complaintStatus;
                var reply = complaint.reply;

                // 🔥 判断是否为撤销/驳回
                var isCancelled = (status === 'closed' && reply &&
                    (reply.indexOf('【业主主动撤销】') > -1 || reply.indexOf('【管理员驳回】') > -1));

                var displayStatus = isCancelled ? 'cancelled' : status;
                var statusName = isCancelled ? '已撤销' : getStatusName(status);

                var ownerInfo = complaint.isAnonymous === 1 ? '匿名用户' : (complaint.ownerName || '-');
                var phoneInfo = complaint.isAnonymous === 1 ? '***' : (complaint.ownerPhone || '-');

                var content =
                    '<div class="detail-card">' +
                    // 头部区域
                    '<div class="detail-header">' +
                    '<h4><i class="fas fa-comment-dots"></i> ' + (complaint.title || '投诉详情') + '</h4>' +
                    '<p><i class="fas fa-hashtag"></i> 投诉编号：' + complaint.complaintId + ' | <i class="fas fa-clock"></i> 提交时间：' + formatDate(complaint.submitTime) + '</p>' +
                    '</div>' +

                    // 基本信息区域
                    '<div class="detail-section">' +
                    '<h6 style="margin-bottom: 15px; font-weight: 600;"><i class="fas fa-info-circle"></i> 基本信息</h6>' +
                    '<div class="detail-grid">' +
                    '<div class="detail-item"><small><i class="fas fa-user"></i> 业主姓名</small><div class="value">' + ownerInfo + '</div></div>' +
                    '<div class="detail-item"><small><i class="fas fa-phone"></i> 联系电话</small><div class="value">' + phoneInfo + '</div></div>' +
                    '<div class="detail-item"><small><i class="fas fa-tag"></i> 投诉类型</small><div class="value"><span class="type-badge type-' + complaint.complaintType + '">' + getTypeName(complaint.complaintType) + '</span></div></div>' +
                    '<div class="detail-item"><small><i class="fas fa-tasks"></i> 当前状态</small><div class="value"><span class="status-badge status-' + displayStatus + '">' + statusName + '</span></div></div>' +
                    '<div class="detail-item"><small><i class="fas fa-user-tie"></i> 处理人</small><div class="value">' + (complaint.handlerName || '未分配') + '</div></div>' +
                    '<div class="detail-item"><small><i class="fas fa-user-secret"></i> 是否匿名</small><div class="value">' + (complaint.isAnonymous === 1 ? '<span class="badge badge-warning">是</span>' : '<span class="badge badge-info">否</span>') + '</div></div>' +
                    '</div>' +
                    '</div>' +

                    // ✅ 投诉内容区域 (时间线样式)
                    '<div class="detail-item">' +
                    '<h6 style="margin-bottom: 15px; font-weight: 600;"><i class="fas fa-align-left"></i> 投诉内容</h6>' +
                    '<div class="timeline">';

                // 解析内容 (分离原始内容和追加内容)
                var fullContent = complaint.content || '';
                var contentParts = fullContent.split(/【.*?追加】/);
                var timeMatches = fullContent.match(/【(.*?)追加】/g);

                // 原始投诉内容
                content += '<div class="timeline-item">' +
                    '<div class="timeline-marker"><i class="fas fa-file-alt"></i></div>' +
                    '<div class="timeline-content">' +
                    '<div class="timeline-header">' +
                    '<span class="timeline-title"><i class="fas fa-comment"></i> 原始投诉</span>' +
                    '<span class="timeline-time">' + formatDate(complaint.submitTime) + '</span>' +
                    '</div>' +
                    '<div class="timeline-body">' + (contentParts[0] || '无') + '</div>' +
                    '</div>' +
                    '</div>';

                // ✅ 追加内容 (如果有)
                if (contentParts.length > 1) {
                    for (var i = 1; i < contentParts.length; i++) {
                        var appendTime = timeMatches && timeMatches[i-1] ? timeMatches[i-1].replace(/【|追加】/g, '').trim() : '未知时间';
                        content += '<div class="timeline-item">' +
                            '<div class="timeline-marker timeline-marker-append"><i class="fas fa-plus-circle"></i></div>' +
                            '<div class="timeline-content timeline-content-append">' +
                            '<div class="timeline-header">' +
                            '<span class="timeline-title"><i class="fas fa-edit"></i> 追加说明 #' + i + '</span>' +
                            '<span class="timeline-time">' + appendTime + '</span>' +
                            '</div>' +
                            '<div class="timeline-body">' + contentParts[i].trim() + '</div>' +
                            '</div>' +
                            '</div>';
                    }
                }

                content += '</div></div>'; // 结束时间线

                // 🔥 回复内容区域 (区分正常回复和撤销/驳回)
                if (reply) {
                    var replyClass = isCancelled ? 'reply-box cancelled' : 'reply-box';
                    var replyIcon = isCancelled ? 'fas fa-info-circle' : 'fas fa-reply';
                    var replyTitle = isCancelled ? '系统消息' : '处理回复';

                    content += '<div class="detail-item">' +
                        '<h6 style="margin-bottom: 15px; font-weight: 600;"><i class="' + replyIcon + '"></i> ' + replyTitle + '</h6>' +
                        '<div class="' + replyClass + '">' +
                        '<div style="display: flex; justify-content: space-between; margin-bottom: 10px;">' +
                        '<span style="font-weight: 600;"><i class="fas fa-user-tie"></i> ' + (complaint.handlerName || '系统') + '</span>' +
                        '<span style="color: #757575; font-size: 13px;"><i class="fas fa-clock"></i> ' + formatDate(complaint.replyTime) + '</span>' +
                        '</div>' +
                        '<div style="line-height: 1.8;">' + reply + '</div>' +
                        '</div>' +
                        '</div>';
                }

                content += '</div>'; // 结束 detail-card

                // 🔥 优化后的弹窗配置 (添加滚动功能)
                layer.open({
                    type: 1,
                    title: false,
                    area: ['800px', '80vh'], // 🔥 高度改为80vh,留出上下空间
                    offset: 'auto', // 🔥 自动居中
                    shadeClose: true,
                    scrollbar: true, // 🔥 允许滚动条
                    content: content,
                    success: function(layero, index) {
                        // 🔥 确保内容区域可滚动
                        $(layero).find('.layui-layer-content').css({
                            'overflow-y': 'auto',
                            'overflow-x': 'hidden',
                            'max-height': '80vh'
                        });
                    }
                });
            },
            error: function() {
                layer.msg('获取详情失败', {icon: 2});
            }
        });
    }

    /**
     * 受理投诉
     */
    function acceptComplaint(complaintId) {
        layer.confirm('确定要受理该投诉吗？', {icon: 3, title:'受理确认'}, function(index){
            $.ajax({
                url: '${pageContext.request.contextPath}/admin/complaint/accept',
                type: 'POST',
                dataType: 'json',
                data: {
                    complaintId: complaintId,
                    handlerId: currentUserId
                },
                success: function(res) {
                    if (res.success) {
                        layer.msg('受理成功', {icon: 1});
                        loadComplaintList(currentPage);
                    } else {
                        layer.msg(res.message || '受理失败', {icon: 2});
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
     * 🔥 统一的取消/驳回/终止操作 (调用 /cancel 接口)
     */
    function openCancelDialog(complaintId, actionName) {
        layer.prompt({
            formType: 2,
            title: '请输入' + actionName + '原因 (必填)',
            area: ['400px', '150px'],
            maxlength: 200
        }, function(value, index, elem){
            if (!value.trim()) {
                layer.msg('原因不能为空', {icon: 0});
                return;
            }

            $.ajax({
                url: '${pageContext.request.contextPath}/admin/complaint/cancel',
                type: 'POST',
                dataType: 'json',
                data: {
                    complaintId: complaintId,
                    reason: value
                },
                success: function(res) {
                    if (res.success) {
                        layer.msg('已' + actionName, {icon: 1});
                        layer.close(index);
                        loadComplaintList(currentPage);
                    } else {
                        layer.msg(res.message || '操作失败', {icon: 2});
                    }
                },
                error: function() {
                    layer.msg('网络错误', {icon: 2});
                }
            });
        });
    }

    /**
     * 打开回复模态框
     */
    function openReplyModal(complaintId) {
        $('#replyComplaintId').val(complaintId);
        $('#replyContent').val('');
        $('#newStatus').val('resolved');
        $('#replyModal').modal('show');
    }

    /**
     * 确认回复
     */
    function confirmReply() {
        var complaintId = $('#replyComplaintId').val();
        var reply = $('#replyContent').val();
        var newStatus = $('#newStatus').val();

        if (!reply.trim()) {
            layer.msg('回复内容不能为空', {icon: 0});
            return;
        }

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/reply',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                complaintId: parseInt(complaintId),
                handlerId: currentUserId,
                reply: reply,
                newStatus: newStatus
            }),
            success: function(res) {
                if (res.success) {
                    layer.msg('回复成功', {icon: 1});
                    $('#replyModal').modal('hide');
                    loadComplaintList(currentPage);
                } else {
                    layer.msg(res.message || '回复失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
            }
        });
    }

    /**
     * 删除投诉 (物理删除)
     */
    function deleteComplaint(complaintId) {
        layer.confirm('确定要删除该投诉吗？此操作不可恢复！', {icon: 3, title:'删除确认'}, function(index){
            $.ajax({
                url: '${pageContext.request.contextPath}/admin/complaint/delete/' + complaintId,
                type: 'DELETE',
                dataType: 'json',
                data: {
                    operatorId: currentUserId
                },
                success: function(res) {
                    if (res.success) {
                        layer.msg('删除成功', {icon: 1});
                        loadComplaintList(currentPage);
                    } else {
                        layer.msg(res.message || '删除失败', {icon: 2});
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
     * 🔥 批量删除 (带智能提示 - 修复版)
     */
    function batchDelete() {
        var checkedBoxes = $('.row-checkbox:checked');
        if (checkedBoxes.length === 0) {
            layer.msg('请先选择要删除的投诉', {icon: 0});
            return;
        }

        var ids = [];
        var ignoreCount = 0;

        checkedBoxes.each(function() {
            var status = $(this).data('status');
            var isCancelled = $(this).data('cancelled');
            var id = $(this).val();

            // ✅ 允许删除：已解决、已关闭（包括已撤销）
            if (status === 'resolved' || status === 'closed' || isCancelled) {
                ids.push(id);
            } else {
                ignoreCount++;
            }
        });

        if (ids.length === 0) {
            layer.alert('您选中的记录中没有【已解决】或【已关闭】状态的投诉。<br>为了安全起见，【待处理】和【处理中】的记录不允许直接删除。', {
                icon: 7,
                title: '无法删除'
            });
            return;
        }

        var msg = '确定要删除这 <b>' + ids.length + '</b> 条记录吗？';

        if (ignoreCount > 0) {
            msg += '<div style="margin-top:10px; color:#dc3545; font-size:13px;">' +
                '<i class="fas fa-exclamation-circle"></i> 另有 ' + ignoreCount + ' 条正在处理中的记录将被自动忽略。' +
                '</div>';
        }

        layer.confirm(msg, {icon: 3, title:'批量删除确认', area:['400px', 'auto']}, function(index){
            var completed = 0;
            var successCount = 0;
            var failCount = 0;

            $.each(ids, function(i, id) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/admin/complaint/delete/' + id,
                    type: 'DELETE',
                    dataType: 'json',
                    data: { operatorId: currentUserId },
                    success: function(res) {
                        if (res.success) successCount++;
                        else failCount++;
                    },
                    error: function() { failCount++; },
                    complete: function() {
                        completed++;
                        if(completed === ids.length) {
                            var resultMsg = '成功删除 ' + successCount + ' 条';
                            if (failCount > 0) resultMsg += '，失败 ' + failCount + ' 条';

                            layer.msg(resultMsg, {icon: 1});
                            $('#checkAll').prop('checked', false);
                            loadComplaintList(currentPage);
                        }
                    }
                });
            });
            layer.close(index);
        });
    }

    /**
     * 批量分配
     */
    function batchAssign() {
        var checkedBoxes = $('.row-checkbox:checked');
        if (checkedBoxes.length === 0) {
            layer.msg('请先选择要分配的投诉', {icon: 0});
            return;
        }
        $('#selectedCount').text(checkedBoxes.length);
        $('#assignHandlerId').val('');
        $('#assignModal').modal('show');
    }

    /**
     * 确认分配
     */
    function confirmAssign() {
        var handlerId = $('#assignHandlerId').val();
        if (!handlerId) {
            layer.msg('请选择处理人', {icon: 0});
            return;
        }

        var checkedBoxes = $('.row-checkbox:checked');
        var ids = [];
        checkedBoxes.each(function() {
            ids.push($(this).val());
        });

        var completed = 0;
        var successCount = 0;
        var failCount = 0;

        $.each(ids, function(i, id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/admin/complaint/accept',
                type: 'POST',
                dataType: 'json',
                data: {
                    complaintId: id,
                    handlerId: handlerId
                },
                success: function(res) {
                    if (res.success) successCount++;
                    else failCount++;
                },
                error: function() { failCount++; },
                complete: function() {
                    completed++;
                    if(completed === ids.length) {
                        layer.msg('成功分配 ' + successCount + ' 条，失败 ' + failCount + ' 条', {icon: 1});
                        $('#assignModal').modal('hide');
                        $('#checkAll').prop('checked', false);
                        loadComplaintList(currentPage);
                    }
                }
            });
        });
    }

    /**
     * 显示统计分析
     */
    function showStatistics() {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/complaint/statistics',
            type: 'GET',
            dataType: 'json',
            success: function(res) {
                var overall = res.overall || {};
                var byType = res.byType || [];

                var content = '<div style="padding: 20px;">' +
                    '<h5 class="mb-4" style="font-weight: 600; color: #495057;"><i class="fas fa-chart-pie"></i> 投诉统计分析</h5>' +

                    '<div class="row mb-3">' +
                    '<div class="col-md-4"><div class="stat-card stat-info"><i class="fas fa-comments"></i><h3>' + (overall.totalCount || 0) + '</h3><p>总投诉数</p></div></div>' +
                    '<div class="col-md-4"><div class="stat-card stat-warning"><i class="fas fa-clock"></i><h3>' + (overall.pendingCount || 0) + '</h3><p>待处理</p></div></div>' +
                    '<div class="col-md-4"><div class="stat-card stat-primary"><i class="fas fa-tasks"></i><h3>' + (overall.processingCount || 0) + '</h3><p>处理中</p></div></div>' +
                    '</div>' +

                    '<div class="row mb-4">' +
                    '<div class="col-md-6"><div class="stat-card stat-success"><i class="fas fa-check-circle"></i><h3>' + (overall.resolvedCount || 0) + '</h3><p>已解决</p></div></div>' +
                    '<div class="col-md-6"><div class="stat-card" style="background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%); color: white; text-align: center; padding: 20px; border-radius: 8px; margin-bottom: 15px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"><i class="fas fa-ban" style="font-size: 32px; margin-bottom: 10px;"></i><h3 style="font-size: 32px; font-weight: bold; margin: 10px 0;">' + (overall.closedCount || 0) + '</h3><p style="margin: 0; font-size: 14px;">已关闭</p></div></div>' +
                    '</div>' +

                    '<div class="alert alert-info" style="border-radius: 8px;">' +
                    '<i class="fas fa-chart-line"></i> ' +
                    '<strong>解决率：</strong>' + (overall.resolveRate || 0) + '% | ' +
                    '<strong>平均响应时长：</strong>' + (overall.avgResponseHours || 0) + ' 小时' +
                    '</div>';

                if (byType && byType.length > 0) {
                    content += '<h6 class="mt-4 mb-3" style="font-weight: 600;"><i class="fas fa-list"></i> 按类型统计</h6>' +
                        '<table class="table table-bordered table-hover stat-table"><thead><tr>' +
                        '<th style="width: 20%;">类型</th>' +
                        '<th style="width: 12%;">总数</th>' +
                        '<th style="width: 12%;">待处理</th>' +
                        '<th style="width: 12%;">处理中</th>' +
                        '<th style="width: 12%;">已解决</th>' +
                        '<th style="width: 12%;">已关闭</th>' +
                        '<th style="width: 20%;">解决率</th>' +
                        '</tr></thead><tbody>';

                    $.each(byType, function(i, item) {
                        content += '<tr>' +
                            '<td><span class="type-badge type-' + item.complaintType + '">' + item.complaintTypeName + '</span></td>' +
                            '<td><strong>' + (item.count || 0) + '</strong></td>' +
                            '<td><span class="badge badge-warning">' + (item.pendingCount || 0) + '</span></td>' +
                            '<td><span class="badge badge-primary">' + (item.processingCount || 0) + '</span></td>' +
                            '<td><span class="badge badge-success">' + (item.resolvedCount || 0) + '</span></td>' +
                            '<td><span class="badge badge-secondary">' + (item.closedCount || 0) + '</span></td>' +
                            '<td><span class="badge badge-info" style="font-size: 13px;">' + (item.resolveRate || 0) + '%</span></td>' +
                            '</tr>';
                    });
                    content += '</tbody></table>';
                }
                content += '</div>';

                layer.open({
                    type: 1,
                    title: false,
                    area: ['850px', 'auto'],
                    maxHeight: '90%',
                    shadeClose: true,
                    content: content
                });
            },
            error: function() {
                layer.msg('获取统计数据失败', {icon: 2});
            }
        });
    }

    // 全选/反选
    function toggleCheckAll() {
        var checked = $('#checkAll').prop('checked');
        $('.row-checkbox').prop('checked', checked);
    }

    // 搜索
    function searchComplaint() {
        loadComplaintList(1);
    }

    // 重置搜索
    function resetSearch() {
        $('#searchKeyword').val('');
        $('#filterType').val('');
        $('#filterStatus').val('');
        loadComplaintList(1);
    }

    // 日期格式化
    function formatDate(dateStr) {
        if (!dateStr) return '-';
        try {
            var date;
            if (typeof dateStr === 'string') {
                dateStr = dateStr.replace('T', ' ').split('.')[0];
                date = new Date(dateStr);
            } else {
                date = new Date(dateStr);
            }
            if (isNaN(date.getTime())) return dateStr;
            return date.getFullYear() + '-' +
                String(date.getMonth() + 1).padStart(2, '0') + '-' +
                String(date.getDate()).padStart(2, '0') + ' ' +
                String(date.getHours()).padStart(2, '0') + ':' +
                String(date.getMinutes()).padStart(2, '0');
        } catch (e) {
            return dateStr;
        }
    }
</script>

</body>
</html>

