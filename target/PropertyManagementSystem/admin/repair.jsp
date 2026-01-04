<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>报修管理 - 物业管理系统</title>

    <!-- 引入 CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/theme/default/layer.css">

    <style>
        /* ==================== 基础样式 ==================== */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Microsoft YaHei', Arial, sans-serif; background: #f5f7fa; }

        /* ==================== 侧边栏样式 ==================== */
        .sidebar { position: fixed; left: 0; top: 0; bottom: 0; width: 250px; background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%); color: white; overflow-y: auto; z-index: 1000; box-shadow: 2px 0 10px rgba(0,0,0,0.1); transition: transform 0.3s ease; }
        .sidebar.collapsed { transform: translateX(-250px); }
        .sidebar-header { padding: 30px 20px; background: rgba(0,0,0,0.2); border-bottom: 1px solid rgba(255,255,255,0.1); }
        .sidebar-header h3 { font-size: 20px; font-weight: 600; margin-bottom: 10px; }
        .sidebar-header p { font-size: 14px; opacity: 0.8; margin: 0; }
        .sidebar-menu { list-style: none; padding: 20px 0; }
        .sidebar-menu li { margin-bottom: 5px; }
        .sidebar-menu a { display: flex; align-items: center; padding: 15px 25px; color: rgba(255,255,255,0.8); text-decoration: none; transition: all 0.3s; }
        .sidebar-menu a:hover { background: rgba(255,255,255,0.1); color: white; padding-left: 30px; }
        .sidebar-menu a.active { background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); color: white; border-left: 4px solid #fff; }
        .sidebar-menu i { width: 25px; margin-right: 15px; font-size: 16px; }
        .sidebar-footer { position: absolute; bottom: 0; left: 0; right: 0; padding: 20px; background: rgba(0,0,0,0.2); }

        /* 侧边栏切换按钮 */
        .sidebar-toggle { position: fixed; left: 260px; top: 20px; z-index: 1001; width: 40px; height: 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 50%; box-shadow: 0 4px 6px rgba(0,0,0,0.2); cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.3s ease; }
        .sidebar-toggle:hover { transform: scale(1.1); box-shadow: 0 6px 12px rgba(0,0,0,0.3); }
        .sidebar-toggle.collapsed { left: 10px; }

        /* ==================== 主内容区 ==================== */
        .main-content { margin-left: 250px; padding: 30px; min-height: 100vh; transition: margin-left 0.3s ease; }
        .main-content.expanded { margin-left: 0; }
        .page-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .page-header h2 { margin: 0; font-size: 28px; font-weight: 600; }
        .page-header p { margin: 10px 0 0 0; opacity: 0.9; }

        /* ==================== 统计卡片 ==================== */
        .stats-container { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .stat-card { background: white; border-radius: 10px; padding: 25px; box-shadow: 0 2px 4px rgba(0,0,0,0.08); transition: transform 0.3s, box-shadow 0.3s; }
        .stat-card:hover { transform: translateY(-5px); box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
        .stat-card .icon { width: 50px; height: 50px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-bottom: 15px; }
        .stat-card.pending .icon { background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%); color: #856404; }
        .stat-card.processing .icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: #004085; }
        .stat-card.completed .icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: #155724; }
        .stat-card.cancelled .icon { background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); color: #721c24; }
        .stat-card.total .icon { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); color: #721c24; }
        .stat-card h3 { font-size: 32px; font-weight: 700; margin: 0 0 5px 0; color: #333; }
        .stat-card p { margin: 0; color: #666; font-size: 14px; }

        /* ==================== 搜索和表格 ==================== */
        .search-box { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.08); margin-bottom: 20px; }
        .data-table { background: white; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.08); overflow: hidden; }
        .table { margin-bottom: 0; }
        .table thead th { background-color: #f8f9fa; border-bottom: 2px solid #dee2e6; color: #495057; font-weight: 600; padding: 15px; white-space: nowrap; }
        .table tbody td { padding: 12px 15px; vertical-align: middle; }
        .btn-action { margin: 2px; padding: 5px 12px; font-size: 13px; }
        .pagination-wrapper { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.08); margin-top: 20px; }

        .modal-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
        .form-label { font-weight: 600; color: #495057; margin-bottom: 8px; }
        .required::after { content: " *"; color: #dc3545; }
        .checkbox-cell { width: 40px; text-align: center; }
        input[type="checkbox"] { width: 18px; height: 18px; cursor: pointer; }
        .btn-group-custom { display: flex; gap: 10px; flex-wrap: wrap; }

        /* ==================== 状态和优先级标签 ==================== */
        .status-badge { padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
        .status-pending { background-color: #fff3cd; color: #856404; }
        .status-processing { background-color: #d1ecf1; color: #0c5460; }
        .status-completed { background-color: #d4edda; color: #155724; }
        .status-cancelled { background-color: #f8d7da; color: #721c24; }

        .priority-badge { padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
        .priority-normal { background-color: #e3f2fd; color: #1976d2; }
        .priority-urgent { background-color: #fff3e0; color: #f57c00; }
        .priority-emergency { background-color: #ffebee; color: #d32f2f; }

        .rating-stars { color: #ffc107; }
        .rating-stars i { font-size: 14px; }

        /* ==================== 报修详情弹窗样式 (紫色主题) ==================== */
        .repair-detail-card { font-family: 'Microsoft YaHei', Arial, sans-serif; }

        .repair-detail-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 15px;
        }

        .repair-detail-section h6 {
            margin-bottom: 15px;
            font-weight: 600;
            color: #2c3e50;
        }

        .repair-detail-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .repair-detail-item {
            margin-bottom: 15px;
        }

        .repair-detail-item small {
            display: block;
            color: #666;
            font-size: 12px;
            margin-bottom: 5px;
        }

        .repair-detail-item small i {
            margin-right: 5px;
            color: #667eea;
        }

        .repair-detail-item .value {
            font-weight: 600;
            color: #333;
            font-size: 14px;
        }

        /* 问题描述框 (黄色) */
        .repair-content-box {
            background: #fff3cd;
            border-left: 5px solid #ffc107;
            padding: 18px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            line-height: 1.8;
            color: #856404;
            white-space: pre-wrap;
        }

        /* 处理信息框 (蓝色) */
        .repair-handler-box {
            background: #d1ecf1;
            border-left: 5px solid #17a2b8;
            padding: 18px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            line-height: 1.6;
            color: #0c5460;
        }

        /* 处理结果框 (绿色) */
        .repair-result-box {
            background: #d4edda;
            border-left: 5px solid #28a745;
            padding: 18px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            line-height: 1.8;
            color: #155724;
            white-space: pre-wrap;
        }

        /* 业主反馈框 (金色渐变) */
        .repair-feedback-box {
            background: linear-gradient(135deg, #fff9e6 0%, #ffe8cc 100%);
            border-left: 5px solid #ff9800;
            padding: 18px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(255, 152, 0, 0.2);
            color: #e65100;
        }

        .repair-feedback-box .feedback-content {
            background: white;
            padding: 15px;
            border-radius: 8px;
            line-height: 1.8;
            white-space: pre-wrap;
            color: #333;
            border: 1px solid #ffe0b2;
        }

        /* 取消原因框 (红色) */
        .repair-cancel-box {
            background: #f8d7da;
            border-left: 5px solid #dc3545;
            padding: 18px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            line-height: 1.8;
            color: #721c24;
            white-space: pre-wrap;
        }

        /* 未评价提示框 (灰色) */
        .repair-no-feedback-box {
            background: #f5f5f5;
            border-left: 5px solid #9e9e9e;
            padding: 18px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            text-align: center;
            color: #757575;
        }

        /* 评分星星 */
        .repair-rating-stars {
            margin-bottom: 12px;
        }

        .repair-rating-stars i {
            color: #ffc107;
            margin-right: 3px;
            font-size: 18px;
        }

        /* 评分星星动画 */
        @keyframes starPulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.1); }
        }

        .repair-rating-stars .fas.fa-star {
            animation: starPulse 1.5s ease-in-out infinite;
        }

        .repair-rating-stars .fas.fa-star:nth-child(2) { animation-delay: 0.1s; }
        .repair-rating-stars .fas.fa-star:nth-child(3) { animation-delay: 0.2s; }
        .repair-rating-stars .fas.fa-star:nth-child(4) { animation-delay: 0.3s; }
        .repair-rating-stars .fas.fa-star:nth-child(5) { animation-delay: 0.4s; }

        /* 引用样式 */
        .repair-feedback-box .fa-quote-left,
        .repair-feedback-box .fa-quote-right {
            opacity: 0.3;
            font-size: 12px;
        }

        /* 模态框滚动条美化 (紫色主题) */
        #repairDetailModal .modal-body::-webkit-scrollbar {
            width: 10px;
        }

        #repairDetailModal .modal-body::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 10px;
        }

        #repairDetailModal .modal-body::-webkit-scrollbar-thumb {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 10px;
            transition: background 0.3s;
        }

        #repairDetailModal .modal-body::-webkit-scrollbar-thumb:hover {
            background: linear-gradient(135deg, #5568d3 0%, #6a3f8f 100%);
        }

        /* ==================== 响应式 ==================== */
        @media (max-width: 768px) {
            .sidebar { transform: translateX(-250px); }
            .sidebar-toggle { left: 10px; }
            .main-content { margin-left: 0; }
            .stats-container { grid-template-columns: 1fr 1fr; }
            .repair-detail-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

<!-- 侧边栏切换按钮 -->
<button class="sidebar-toggle" id="sidebarToggle" onclick="toggleSidebar()">
    <i class="fas fa-bars"></i>
</button>

<!-- 侧边栏 -->
<div class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <h3><i class="fas fa-building"></i> 物业管理系统</h3>
        <p><i class="fas fa-user-shield"></i> 管理员：${sessionScope.currentUser.realName}</p>
    </div>

    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/index.jsp"><i class="fas fa-home"></i> 系统首页</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/user.jsp"><i class="fas fa-users-cog"></i> 用户管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/owner.jsp"><i class="fas fa-users"></i> 业主管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/house.jsp"><i class="fas fa-building"></i> 房屋管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/chargeItem.jsp"><i class="fas fa-list-alt"></i> 收费项目</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/payment.jsp"><i class="fas fa-credit-card"></i> 缴费管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/repair.jsp" class="active"><i class="fas fa-tools"></i> 报修管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistics.jsp"><i class="fas fa-chart-bar"></i> 数据统计</a></li>
    </ul>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/login?method=logout" class="btn btn-danger btn-block">
            <i class="fas fa-sign-out-alt"></i> 退出登录
        </a>
    </div>
</div>

<!-- 主内容区 -->
<div class="main-content" id="mainContent">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-tools"></i> 报修管理</h2>
        <p>管理业主报修记录，包括受理、处理、完成等全流程管理</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
        <div class="stat-card pending">
            <div class="icon"><i class="fas fa-clock"></i></div>
            <h3 id="pendingCount">0</h3>
            <p>待处理报修</p>
        </div>
        <div class="stat-card processing">
            <div class="icon"><i class="fas fa-cog"></i></div>
            <h3 id="processingCount">0</h3>
            <p>处理中报修</p>
        </div>
        <div class="stat-card completed">
            <div class="icon"><i class="fas fa-check-circle"></i></div>
            <h3 id="completedCount">0</h3>
            <p>已完成报修</p>
        </div>
        <div class="stat-card cancelled">
            <div class="icon"><i class="fas fa-ban"></i></div>
            <h3 id="cancelledCount">0</h3>
            <p>已取消报修</p>
        </div>
        <div class="stat-card total">
            <div class="icon"><i class="fas fa-clipboard-list"></i></div>
            <h3 id="totalCount">0</h3>
            <p>报修总数</p>
        </div>
    </div>

    <!-- 搜索和操作区 -->
    <div class="search-box">
        <div class="row mb-3">
            <div class="col-md-3">
                <input type="text" class="form-control" id="searchKeyword" placeholder="搜索业主姓名、房屋编号">
            </div>
            <div class="col-md-2">
                <select class="form-control" id="searchStatus">
                    <option value="">全部状态</option>
                    <option value="pending">待处理</option>
                    <option value="processing">处理中</option>
                    <option value="completed">已完成</option>
                    <option value="cancelled">已取消</option>
                </select>
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary btn-block" onclick="searchRepair()">
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
                    <button class="btn btn-info" onclick="loadPendingRepairs()">
                        <i class="fas fa-clock"></i> 待处理报修
                    </button>
                    <button class="btn btn-success" onclick="showSubmitModal()">
                        <i class="fas fa-plus"></i> 代客报修
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
                    <th class="checkbox-cell"><input type="checkbox" id="checkAll" onclick="toggleCheckAll()"></th>
                    <th>报修编号</th>
                    <th>业主信息</th>
                    <th>房屋编号</th>
                    <th>报修类型</th>
                    <th>优先级</th>
                    <th>状态</th>
                    <th>提交时间</th>
                    <th>处理人</th>
                    <th>评分</th>
                    <th width="280">操作</th>
                </tr>
                </thead>
                <tbody id="repairTableBody">
                <tr><td colspan="11" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
        <div class="row">
            <div class="col-md-6"><div id="pageInfo">共 0 条记录</div></div>
            <div class="col-md-6"><nav><ul class="pagination justify-content-end" id="pagination"></ul></nav></div>
        </div>
    </div>
</div>

<!-- ==================== 报修详情模态框 (Bootstrap Modal) ==================== -->
<div class="modal fade" id="repairDetailModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document" style="max-width: 800px;">
        <div class="modal-content">
            <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none;">
                <h5 class="modal-title"><i class="fas fa-tools"></i> 报修详情</h5>
                <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" style="max-height: 70vh; overflow-y: auto; padding: 20px;">
                <div id="repairDetailContent">
                    <div class="text-center">
                        <i class="fas fa-spinner fa-spin"></i> 加载中...
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="border-top: 1px solid #dee2e6;">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 关闭
                </button>
            </div>
        </div>
    </div>
</div>

<!-- 提交报修模态框 (管理员代客报修) -->
<div class="modal fade" id="submitModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">代客报修</h5>
                <button type="button" class="close text-white" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <div class="modal-body">
                <form id="submitForm">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">业主ID</label>
                                <input type="text" class="form-control" id="submitOwnerId" name="ownerId" required placeholder="8位业主编号">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">房屋编号</label>
                                <input type="text" class="form-control" id="submitHouseId" name="houseId" required placeholder="如：01010101">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">报修类型</label>
                                <select class="form-control" id="submitRepairType" name="repairType" required>
                                    <option value="">请选择</option>
                                    <option value="plumbing">水管维修</option>
                                    <option value="electrical">电路维修</option>
                                    <option value="door_window">门窗维修</option>
                                    <option value="public_facility">公共设施</option>
                                    <option value="other">其他</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label required">优先级</label>
                                <select class="form-control" id="submitPriority" name="priority" required>
                                    <option value="normal">普通</option>
                                    <option value="urgent">紧急</option>
                                    <option value="emergency">特急</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label class="form-label required">问题描述</label>
                                <textarea class="form-control" id="submitDescription" name="description" rows="4" required placeholder="请详细描述报修问题"></textarea>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="submitRepair()">提交</button>
            </div>
        </div>
    </div>
</div>

<!-- 受理报修模态框 -->
<div class="modal fade" id="acceptModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">受理报修</h5>
                <button type="button" class="close text-white" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <div class="modal-body">
                <form id="acceptForm">
                    <input type="hidden" id="acceptRepairId">
                    <div class="form-group">
                        <label class="form-label required">处理人</label>
                        <input type="text" class="form-control" id="acceptHandler" name="handler" required placeholder="请输入处理人姓名">
                    </div>
                    <div class="form-group">
                        <label class="form-label required">联系电话</label>
                        <input type="text" class="form-control" id="acceptHandlerPhone" name="handlerPhone" required placeholder="请输入11位手机号" pattern="^1[3-9]\d{9}$" maxlength="11">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="acceptRepair()">确认受理</button>
            </div>
        </div>
    </div>
</div>

<!-- 完成报修模态框 -->
<div class="modal fade" id="completeModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">完成报修</h5>
                <button type="button" class="close text-white" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <div class="modal-body">
                <form id="completeForm">
                    <input type="hidden" id="completeRepairId">
                    <div class="form-group">
                        <label class="form-label required">处理结果</label>
                        <textarea class="form-control" id="completeRepairResult" name="repairResult" rows="4" required placeholder="请输入处理结果和说明"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="completeRepair()">确认完成</button>
            </div>
        </div>
    </div>
</div>

<!-- 取消/驳回报修模态框 -->
<div class="modal fade" id="cancelModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">取消/驳回报修</h5>
                <button type="button" class="close text-white" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <div class="modal-body">
                <form id="cancelForm">
                    <input type="hidden" id="cancelRepairId">
                    <div class="form-group">
                        <label class="form-label required">取消/驳回原因</label>
                        <textarea class="form-control" id="cancelReason" name="cancelReason" rows="4" required placeholder="请输入取消或驳回的原因，将通知业主"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-danger" onclick="cancelRepair()">确认取消</button>
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
    var currentKeyword = '';
    var currentStatus = '';

    $(document).ready(function() {
        console.log('📋 报修管理页面加载完成');
        loadRepairList(1);
        loadStatistics();

        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) searchRepair();
        });

        if ($(window).width() <= 768) toggleSidebar();
    });

    // ==================== 侧边栏切换 ====================
    function toggleSidebar() {
        var sidebar = $('#sidebar');
        var mainContent = $('#mainContent');
        var toggleBtn = $('#sidebarToggle');
        sidebar.toggleClass('collapsed');
        mainContent.toggleClass('expanded');
        toggleBtn.toggleClass('collapsed');
        var icon = toggleBtn.find('i');
        if (sidebar.hasClass('collapsed')) {
            icon.removeClass('fa-times').addClass('fa-bars');
        } else {
            icon.removeClass('fa-bars').addClass('fa-times');
        }
    }

    // ==================== 加载统计数据 ====================
    function loadStatistics() {
        console.log('📊 开始加载统计数据...');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'GET',
            data: { method: 'countByStatus' },
            success: function(response) {
                console.log('✅ 统计数据返回:', response);

                var stats = response.data || response;

                if (stats) {
                    var pending = parseInt(stats.pendingCount || stats.pending) || 0;
                    var processing = parseInt(stats.processingCount || stats.processing) || 0;
                    var completed = parseInt(stats.completedCount || stats.completed) || 0;
                    var cancelled = parseInt(stats.cancelledCount || stats.cancelled) || 0;
                    var total = parseInt(stats.totalCount) || (pending + processing + completed + cancelled);

                    $('#pendingCount').text(pending);
                    $('#processingCount').text(processing);
                    $('#completedCount').text(completed);
                    $('#cancelledCount').text(cancelled);
                    $('#totalCount').text(total);

                    console.log('✅ 统计数据更新完成');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 统计数据加载失败:', error);
            }
        });
    }

    // ==================== 加载报修列表 ====================
    function loadRepairList(pageNum) {
        currentPage = pageNum || currentPage;
        $('#repairTableBody').html('<tr><td colspan="11" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: currentKeyword,
                status: currentStatus
            },
            success: function(response) {
                var data = response.data || response;
                if (data && data.list) {
                    renderRepairTable(data.list);
                    totalCount = data.total || 0;
                    renderPagination();
                    loadStatistics();
                } else {
                    $('#repairTableBody').html('<tr><td colspan="11" class="text-center text-muted">暂无数据</td></tr>');
                    totalCount = 0;
                    renderPagination();
                }
            },
            error: function() {
                $('#repairTableBody').html('<tr><td colspan="11" class="text-center text-danger">加载失败，请刷新重试</td></tr>');
            }
        });
    }

    // ==================== 渲染表格 ====================
    function renderRepairTable(repairs) {
        var tbody = $('#repairTableBody');
        tbody.empty();

        if (!repairs || repairs.length === 0) {
            tbody.append('<tr><td colspan="11" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无数据</td></tr>');
            return;
        }

        $.each(repairs, function(i, repair) {
            var statusClass = 'status-' + repair.repairStatus;
            var statusText = getStatusName(repair.repairStatus);
            var priorityClass = 'priority-' + repair.priority;
            var priorityText = getPriorityName(repair.priority);
            var typeIcon = getRepairTypeIcon(repair.repairType);
            var typeName = getRepairTypeName(repair.repairType);

            var ownerInfo = (repair.ownerName || repair.ownerId);
            if (repair.ownerPhone) ownerInfo += '<br><small class="text-muted">' + repair.ownerPhone + '</small>';

            var handlerInfo = repair.handler || '-';
            if (repair.handlerPhone) handlerInfo += '<br><small class="text-muted">' + repair.handlerPhone + '</small>';

            var ratingHtml = '-';
            if (repair.satisfactionRating) {
                ratingHtml = '<div class="rating-stars">';
                for (var j = 1; j <= 5; j++) {
                    ratingHtml += (j <= repair.satisfactionRating) ? '<i class="fas fa-star"></i>' : '<i class="far fa-star"></i>';
                }
                ratingHtml += '</div>';
            }

            // 按钮逻辑
            var actions = '<button class="btn btn-sm btn-info btn-action" onclick="viewRepair(' + repair.repairId + ')" title="查看详情">' +
                '<i class="fas fa-eye"></i> 查看</button>';

            if (repair.repairStatus === 'pending') {
                actions += '<button class="btn btn-sm btn-success btn-action" onclick="showAcceptModal(' + repair.repairId + ')" title="受理">' +
                    '<i class="fas fa-hand-paper"></i> 受理</button>';
                actions += '<button class="btn btn-sm btn-warning btn-action" onclick="showCancelModal(' + repair.repairId + ')" title="取消">' +
                    '<i class="fas fa-ban"></i> 取消</button>';
            }

            if (repair.repairStatus === 'processing') {
                actions += '<button class="btn btn-sm btn-primary btn-action" onclick="showCompleteModal(' + repair.repairId + ')" title="完成">' +
                    '<i class="fas fa-check"></i> 完成</button>';
                actions += '<button class="btn btn-sm btn-warning btn-action" onclick="showCancelModal(' + repair.repairId + ')" title="驳回/终止">' +
                    '<i class="fas fa-ban"></i> 驳回</button>';
            }

            if (repair.repairStatus === 'cancelled' || repair.repairStatus === 'completed') {
                actions += '<button class="btn btn-sm btn-danger btn-action" onclick="deleteRepair(' + repair.repairId + ')" title="删除">' +
                    '<i class="fas fa-trash"></i> 删除</button>';
            }

            var row = '<tr>' +
                '<td class="checkbox-cell"><input type="checkbox" class="row-checkbox" value="' + repair.repairId + '"></td>' +
                '<td><strong>' + repair.repairId + '</strong></td>' +
                '<td>' + ownerInfo + '</td>' +
                '<td>' + (repair.houseId || '-') + '</td>' +
                '<td><div class="repair-type-icon">' + typeIcon + ' ' + typeName + '</div></td>' +
                '<td><span class="priority-badge ' + priorityClass + '">' + priorityText + '</span></td>' +
                '<td><span class="status-badge ' + statusClass + '">' + statusText + '</span></td>' +
                '<td>' + formatDateTime(repair.submitTime) + '</td>' +
                '<td>' + handlerInfo + '</td>' +
                '<td>' + ratingHtml + '</td>' +
                '<td>' + actions + '</td>' +
                '</tr>';
            tbody.append(row);
        });
    }

    // ==================== 渲染分页 ====================
    function renderPagination() {
        var totalPages = Math.ceil(totalCount / pageSize);
        $('#pageInfo').text('共 ' + totalCount + ' 条记录');
        var pagination = $('#pagination');
        pagination.empty();
        if (totalPages <= 1) return;

        var prevDisabled = currentPage === 1 ? 'disabled' : '';
        pagination.append('<li class="page-item ' + prevDisabled + '"><a class="page-link" href="javascript:void(0)" onclick="' + (currentPage > 1 ? 'loadRepairList(' + (currentPage - 1) + ')' : 'return false') + '">上一页</a></li>');

        var startPage = Math.max(1, currentPage - 2);
        var endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            pagination.append('<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadRepairList(1)">1</a></li>');
            if (startPage > 2) pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
        }

        for (var i = startPage; i <= endPage; i++) {
            var activeClass = i === currentPage ? 'active' : '';
            pagination.append('<li class="page-item ' + activeClass + '"><a class="page-link" href="javascript:void(0)" onclick="loadRepairList(' + i + ')">' + i + '</a></li>');
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            pagination.append('<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="loadRepairList(' + totalPages + ')">' + totalPages + '</a></li>');
        }

        var nextDisabled = currentPage === totalPages ? 'disabled' : '';
        pagination.append('<li class="page-item ' + nextDisabled + '"><a class="page-link" href="javascript:void(0)" onclick="' + (currentPage < totalPages ? 'loadRepairList(' + (currentPage + 1) + ')' : 'return false') + '">下一页</a></li>');
    }

    // ==================== 复选框全选 ====================
    function toggleCheckAll() {
        $('.row-checkbox').prop('checked', $('#checkAll').prop('checked'));
    }

    // ==================== 搜索功能 ====================
    function searchRepair() {
        currentKeyword = $('#searchKeyword').val().trim();
        currentStatus = $('#searchStatus').val();
        currentPage = 1;
        loadRepairList(1);
    }

    function resetSearch() {
        $('#searchKeyword').val('');
        $('#searchStatus').val('');
        currentKeyword = '';
        currentStatus = '';
        currentPage = 1;
        loadRepairList(1);
    }

    function loadPendingRepairs() {
        $('#searchKeyword').val('');
        $('#searchStatus').val('pending');
        currentKeyword = '';
        currentStatus = 'pending';
        currentPage = 1;
        loadRepairList(1);
    }

    // ==================== 代客报修 ====================
    function showSubmitModal() {
        $('#submitForm')[0].reset();
        $('#submitModal').modal('show');
    }

    function submitRepair() {
        var form = $('#submitForm')[0];
        if (!form.checkValidity()) { form.reportValidity(); return; }
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'POST',
            data: {
                method: 'submit',
                ownerId: $('#submitOwnerId').val().trim(),
                houseId: $('#submitHouseId').val().trim(),
                repairType: $('#submitRepairType').val(),
                priority: $('#submitPriority').val(),
                description: $('#submitDescription').val().trim()
            },
            success: function(response) {
                if (response.success || response.code === 200) {
                    layer.msg('提交成功', {icon: 1});
                    $('#submitModal').modal('hide');
                    loadRepairList(currentPage);
                } else {
                    layer.msg(response.message || '提交失败', {icon: 2});
                }
            },
            error: function() { layer.msg('网络错误', {icon: 2}); }
        });
    }

    // ==================== ✨ 查看报修详情 (Bootstrap Modal版本) ====================
    function viewRepair(repairId) {
        console.log('👁️ 查看报修详情:', repairId);
        $('#repairDetailModal').modal('show');
        $('#repairDetailContent').html('<div class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>');

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'GET',
            data: { method: 'findById', repairId: repairId },
            dataType: 'json',
            success: function(response) {
                console.log('✅ 报修详情数据:', response);
                var repair = response.data || response;

                if (!repair || !repair.repairId) {
                    $('#repairDetailContent').html('<div class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</div>');
                    return;
                }

                // 🔥 兼容驼峰和下划线命名
                var repairId = repair.repairId || repair.repair_id;
                var ownerId = repair.ownerId || repair.owner_id;
                var ownerName = repair.ownerName || repair.owner_name;
                var ownerPhone = repair.ownerPhone || repair.owner_phone;
                var houseId = repair.houseId || repair.house_id;
                var repairType = repair.repairType || repair.repair_type;
                var repairStatus = repair.repairStatus || repair.repair_status;
                var priority = repair.priority;
                var submitTime = repair.submitTime || repair.submit_time;
                var acceptTime = repair.acceptTime || repair.accept_time;
                var completeTime = repair.completeTime || repair.complete_time;
                var handler = repair.handler;
                var handlerPhone = repair.handlerPhone || repair.handler_phone;
                var repairResult = repair.repairResult || repair.repair_result;
                var satisfactionRating = repair.satisfactionRating || repair.satisfaction_rating;
                var feedback = repair.feedback;
                var feedbackTime = repair.feedbackTime || repair.feedback_time;
                var cancelReason = repair.cancelReason || repair.cancel_reason;

                var typeIcon = getRepairTypeIcon(repairType);
                var typeName = getRepairTypeName(repairType);

                var html = '<div class="repair-detail-card">' +
                    // 头部信息
                    '<div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);">' +
                    '<p style="margin: 0; opacity: 0.95; font-size: 14px;"><i class="fas fa-hashtag"></i> 报修编号：' + repairId + ' | <i class="fas fa-clock"></i> 提交时间：' + formatDateTime(submitTime) + '</p>' +
                    '</div>' +

                    // 基本信息
                    '<div class="repair-detail-section">' +
                    '<h6><i class="fas fa-info-circle"></i> 基本信息</h6>' +
                    '<div class="repair-detail-grid">' +
                    '<div class="repair-detail-item"><small><i class="fas fa-user"></i> 业主信息</small><div class="value">' + (ownerName || ownerId) + (ownerPhone ? '<br><span style="font-size: 12px; color: #666;">' + ownerPhone + '</span>' : '') + '</div></div>' +
                    '<div class="repair-detail-item"><small><i class="fas fa-home"></i> 房屋编号</small><div class="value">' + (houseId || '-') + '</div></div>' +
                    '<div class="repair-detail-item"><small>' + typeIcon + ' 报修类型</small><div class="value">' + typeName + '</div></div>' +
                    '<div class="repair-detail-item"><small><i class="fas fa-flag"></i> 优先级</small><div class="value">' + getPriorityName(priority) + '</div></div>' +
                    '<div class="repair-detail-item"><small><i class="fas fa-info-circle"></i> 状态</small><div class="value">' + getStatusName(repairStatus) + '</div></div>' +
                    '<div class="repair-detail-item"><small><i class="fas fa-clock"></i> 提交时间</small><div class="value">' + formatDateTime(submitTime) + '</div></div>' +
                    '</div>' +
                    '</div>';

                // 问题描述
                if (repair.description) {
                    html += '<div class="repair-detail-item">' +
                        '<small><i class="fas fa-comment-dots"></i> 问题描述</small>' +
                        '<div class="repair-content-box">' + repair.description + '</div>' +
                        '</div>';
                }

                // 处理信息
                if (handler) {
                    html += '<div class="repair-detail-item">' +
                        '<small><i class="fas fa-user-tie"></i> 处理信息</small>' +
                        '<div class="repair-handler-box">' +
                        '<div><strong>处理人：</strong>' + handler + '</div>' +
                        (handlerPhone ? '<div><strong>联系电话：</strong>' + handlerPhone + '</div>' : '') +
                        (acceptTime ? '<div><strong>受理时间：</strong>' + formatDateTime(acceptTime) + '</div>' : '') +
                        '</div>' +
                        '</div>';
                }

                // 处理结果
                if (repairResult) {
                    html += '<div class="repair-detail-item">' +
                        '<small><i class="fas fa-check-circle"></i> 处理结果</small>' +
                        '<div class="repair-result-box">' + repairResult +
                        (completeTime ? '<div style="margin-top: 10px;"><strong>完成时间：</strong>' + formatDateTime(completeTime) + '</div>' : '') +
                        '</div>' +
                        '</div>';
                }

                // 业主反馈
                if (repairStatus === 'completed' && (satisfactionRating || feedback)) {
                    html += '<div class="repair-detail-item">' +
                        '<small><i class="fas fa-star"></i> 业主反馈</small>' +
                        '<div class="repair-feedback-box">';

                    // 评分
                    if (satisfactionRating) {
                        html += '<div class="repair-rating-stars"><strong>满意度评分：</strong>';
                        for (var j = 1; j <= 5; j++) {
                            html += (j <= satisfactionRating) ?
                                '<i class="fas fa-star"></i>' :
                                '<i class="far fa-star"></i>';
                        }
                        html += ' <span style="font-weight: bold; font-size: 16px;">(' + satisfactionRating + ' / 5 分)</span></div>';
                    }

                    // 反馈意见
                    if (feedback && feedback.trim()) {
                        html += '<div style="margin-bottom: 8px;"><strong>反馈意见：</strong></div>' +
                            '<div class="feedback-content">' +
                            '<i class="fas fa-quote-left" style="color: #ff9800; margin-right: 5px;"></i>' +
                            feedback +
                            '<i class="fas fa-quote-right" style="color: #ff9800; margin-left: 5px;"></i>' +
                            '</div>';
                    }

                    // 评价时间
                    if (feedbackTime) {
                        html += '<div style="margin-top: 10px; text-align: right; font-size: 12px;"><i class="fas fa-clock"></i> 评价时间：' + formatDateTime(feedbackTime) + '</div>';
                    }

                    html += '</div></div>';
                } else if (repairStatus === 'completed') {
                    // 已完成但未评价
                    html += '<div class="repair-detail-item">' +
                        '<small><i class="fas fa-star"></i> 业主反馈</small>' +
                        '<div class="repair-no-feedback-box">' +
                        '<i class="fas fa-hourglass-half" style="font-size: 32px; margin-bottom: 10px; opacity: 0.3;"></i>' +
                        '<div style="font-size: 14px;">业主暂未评价</div>' +
                        '</div>' +
                        '</div>';
                }

                // 取消原因
                if (repairStatus === 'cancelled' && cancelReason) {
                    html += '<div class="repair-detail-item">' +
                        '<small><i class="fas fa-ban"></i> 取消原因</small>' +
                        '<div class="repair-cancel-box">' + cancelReason + '</div>' +
                        '</div>';
                }

                html += '</div>';

                $('#repairDetailContent').html(html);
            },
            error: function(xhr, status, error) {
                console.error('❌ 报修详情请求失败:', error);
                $('#repairDetailContent').html('<div class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</div>');
            }
        });
    }

    // ==================== 受理报修 ====================
    function showAcceptModal(repairId) {
        $('#acceptRepairId').val(repairId);
        $('#acceptForm')[0].reset();
        $('#acceptModal').modal('show');
    }

    function acceptRepair() {
        var form = $('#acceptForm')[0];
        if (!form.checkValidity()) { form.reportValidity(); return; }
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'POST',
            data: {
                method: 'accept',
                repairId: $('#acceptRepairId').val(),
                handler: $('#acceptHandler').val().trim(),
                handlerPhone: $('#acceptHandlerPhone').val().trim()
            },
            success: function(response) {
                if (response.success || response.code === 200) {
                    layer.msg('受理成功', {icon: 1});
                    $('#acceptModal').modal('hide');
                    loadRepairList(currentPage);
                } else { layer.msg(response.message || '受理失败', {icon: 2}); }
            }
        });
    }

    // ==================== 完成报修 ====================
    function showCompleteModal(repairId) {
        $('#completeRepairId').val(repairId);
        $('#completeForm')[0].reset();
        $('#completeModal').modal('show');
    }

    function completeRepair() {
        var form = $('#completeForm')[0];
        if (!form.checkValidity()) { form.reportValidity(); return; }
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'POST',
            data: {
                method: 'complete',
                repairId: $('#completeRepairId').val(),
                repairResult: $('#completeRepairResult').val().trim()
            },
            success: function(response) {
                if (response.success || response.code === 200) {
                    layer.msg('完成成功', {icon: 1});
                    $('#completeModal').modal('hide');
                    loadRepairList(currentPage);
                } else { layer.msg(response.message || '操作失败', {icon: 2}); }
            }
        });
    }

    // ==================== 取消/驳回报修 ====================
    function showCancelModal(repairId) {
        $('#cancelRepairId').val(repairId);
        $('#cancelForm')[0].reset();
        $('#cancelModal').modal('show');
    }

    function cancelRepair() {
        var form = $('#cancelForm')[0];
        if (!form.checkValidity()) { form.reportValidity(); return; }
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/repair',
            type: 'POST',
            data: {
                method: 'cancel',
                repairId: $('#cancelRepairId').val(),
                cancelReason: $('#cancelReason').val().trim()
            },
            success: function(response) {
                if (response.success || response.code === 200) {
                    layer.msg('操作成功', {icon: 1});
                    $('#cancelModal').modal('hide');
                    loadRepairList(currentPage);
                } else { layer.msg(response.message || '操作失败', {icon: 2}); }
            }
        });
    }

    // ==================== 删除报修 ====================
    function deleteRepair(repairId) {
        layer.confirm('确定要删除该报修记录吗？', {icon: 3, title: '确认删除'}, function(index) {
            $.ajax({
                url: '${pageContext.request.contextPath}/admin/repair',
                type: 'POST',
                data: { method: 'delete', repairId: repairId },
                success: function(response) {
                    if (response.success || response.code === 200) {
                        layer.msg('删除成功', {icon: 1});
                        loadRepairList(currentPage);
                    } else { layer.msg(response.message || '删除失败', {icon: 2}); }
                }
            });
            layer.close(index);
        });
    }

    // ==================== 批量删除 ====================
    function batchDelete() {
        var checkedBoxes = $('.row-checkbox:checked');
        if (checkedBoxes.length === 0) {
            layer.msg('请先选择要删除的记录', {icon: 0});
            return;
        }

        var ids = [];
        checkedBoxes.each(function() {
            ids.push($(this).val());
        });

        layer.confirm('确定要删除选中的 ' + ids.length + ' 条记录吗？<br><small class="text-danger">注意：处理中的工单无法删除</small>', {icon: 3}, function(index) {
            var loading = layer.load(1);
            var completed = 0, successCount = 0;

            $.each(ids, function(i, id) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/admin/repair',
                    type: 'POST',
                    data: { method: 'delete', repairId: id },
                    success: function(res) {
                        if(res.success || res.code === 200) successCount++;
                    },
                    complete: function() {
                        completed++;
                        if (completed === ids.length) {
                            layer.close(loading);
                            layer.msg('成功删除 ' + successCount + ' 条，失败 ' + (ids.length - successCount) + ' 条', {icon: 1});
                            $('#checkAll').prop('checked', false);
                            loadRepairList(currentPage);
                        }
                    }
                });
            });
            layer.close(index);
        });
    }

    // ==================== 工具函数 ====================
    function getStatusName(status) {
        var map = {
            'pending': '待处理',
            'processing': '处理中',
            'completed': '已完成',
            'cancelled': '已取消'
        };
        return map[status] || status;
    }

    function getPriorityName(p) {
        var map = {
            'normal': '普通',
            'urgent': '紧急',
            'emergency': '特急'
        };
        return map[p] || p;
    }

    function getRepairTypeName(t) {
        var map = {
            'plumbing': '水管维修',
            'electrical': '电路维修',
            'door_window': '门窗维修',
            'public_facility': '公共设施',
            'other': '其他'
        };
        return map[t] || t;
    }

    function getRepairTypeIcon(t) {
        var icons = {
            'plumbing': '<i class="fas fa-tint text-primary"></i>',
            'electrical': '<i class="fas fa-bolt text-warning"></i>',
            'door_window': '<i class="fas fa-door-open text-info"></i>',
            'public_facility': '<i class="fas fa-building text-secondary"></i>',
            'other': '<i class="fas fa-wrench text-dark"></i>'
        };
        return icons[t] || icons['other'];
    }

    function formatDateTime(s) {
        if (!s) return '-';
        var d = new Date(s);
        return d.getFullYear() + '-' +
            String(d.getMonth()+1).padStart(2,'0') + '-' +
            String(d.getDate()).padStart(2,'0') + ' ' +
            String(d.getHours()).padStart(2,'0') + ':' +
            String(d.getMinutes()).padStart(2,'0');
    }
</script>
</body>
</html>

