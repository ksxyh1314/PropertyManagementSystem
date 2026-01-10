<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>备份管理 - 物业管理系统</title>

    <!-- 引入 CSS -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/layer/3.5.1/theme/default/layer.css">

    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }

        /* 侧边栏样式 */
        .sidebar {
            position: fixed; left: 0; top: 0; bottom: 0; width: 260px;
            background: linear-gradient(180deg, #1e3c72 0%, #2a5298 100%);
            color: white; overflow-y: auto; z-index: 1000;
            box-shadow: 4px 0 20px rgba(0,0,0,0.3);
            transition: transform 0.3s ease;
        }

        .sidebar.collapsed { transform: translateX(-260px); }

        .sidebar-header {
            padding: 30px 20px;
            background: rgba(0,0,0,0.3);
            border-bottom: 1px solid rgba(255,255,255,0.1);
            text-align: center;
        }

        .sidebar-header .logo {
            width: 60px; height: 60px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%; display: flex; align-items: center; justify-content: center;
            margin: 0 auto 15px; font-size: 28px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        }

        .sidebar-header h3 { font-size: 18px; font-weight: 600; margin-bottom: 8px; }
        .sidebar-header p { font-size: 13px; opacity: 0.8; margin: 0; }

        .sidebar-menu { list-style: none; padding: 20px 0; }
        .sidebar-menu li { margin-bottom: 3px; }
        .sidebar-menu a {
            display: flex; align-items: center; padding: 14px 25px;
            color: rgba(255,255,255,0.85); text-decoration: none;
            transition: all 0.3s; position: relative;
        }
        .sidebar-menu a::before {
            content: ''; position: absolute; left: 0; top: 0; bottom: 0;
            width: 4px; background: #667eea; transform: scaleY(0);
            transition: transform 0.3s;
        }
        .sidebar-menu a:hover {
            background: rgba(255,255,255,0.1); color: white; padding-left: 30px;
        }
        .sidebar-menu a:hover::before { transform: scaleY(1); }
        .sidebar-menu a.active {
            background: linear-gradient(90deg, rgba(102, 126, 234, 0.3) 0%, transparent 100%);
            color: white; border-left: 4px solid #667eea;
        }
        .sidebar-menu i { width: 22px; margin-right: 12px; font-size: 15px; }

        .sidebar-footer {
            position: absolute; bottom: 0; left: 0; right: 0;
            padding: 20px; background: rgba(0,0,0,0.3);
        }

        /* 侧边栏切换按钮 */
        .sidebar-toggle {
            position: fixed; left: 270px; top: 20px; z-index: 1002;
            width: 45px; height: 45px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; border: none; border-radius: 50%;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            cursor: pointer; display: flex; align-items: center; justify-content: center;
            transition: all 0.3s ease; font-size: 18px;
        }
        .sidebar-toggle:hover {
            transform: scale(1.1) rotate(90deg);
            box-shadow: 0 6px 16px rgba(0,0,0,0.4);
        }
        .sidebar-toggle.collapsed { left: 20px; }

        /* 主内容区 */
        .main-content {
            margin-left: 260px; padding: 30px; min-height: 100vh;
            transition: margin-left 0.3s ease;
        }
        .main-content.expanded { margin-left: 0; }

        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; padding: 40px 30px; border-radius: 15px;
            margin-bottom: 30px; box-shadow: 0 8px 24px rgba(0,0,0,0.2);
            position: relative; overflow: hidden;
        }
        .page-header::before {
            content: ''; position: absolute; top: -50%; right: -5%;
            width: 300px; height: 300px; background: rgba(255,255,255,0.1);
            border-radius: 50%; animation: float 6s ease-in-out infinite;
        }
        @keyframes float {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }
        .page-header h2 {
            margin: 0; font-size: 32px; font-weight: 700;
            position: relative; z-index: 1;
        }
        .page-header p {
            margin: 12px 0 0 0; opacity: 0.95; font-size: 15px;
            position: relative; z-index: 1;
        }

        /* 功能卡片 */
        .function-card {
            background: white; border-radius: 15px; padding: 30px;
            margin-bottom: 25px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        .function-card h4 {
            color: #667eea; font-size: 20px; font-weight: 600;
            margin-bottom: 20px; padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
        }

        .function-card h4 i {
            margin-right: 10px;
        }

        /* 备份操作区 */
        .backup-actions {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 30px;
        }

        .backup-btn {
            flex: 1;
            min-width: 200px;
            padding: 20px;
            border-radius: 12px;
            border: 2px solid #e9ecef;
            background: white;
            text-align: center;
            transition: all 0.3s;
            cursor: pointer;
        }

        .backup-btn:hover {
            border-color: #667eea;
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.2);
        }

        .backup-btn i {
            font-size: 36px;
            color: #667eea;
            margin-bottom: 15px;
        }

        .backup-btn h5 {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
        }

        .backup-btn p {
            font-size: 13px;
            color: #999;
            margin: 0;
        }

        /* 表格卡片 */
        .table-card {
            background: white; border-radius: 15px; padding: 30px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        .table-responsive {
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
        }
        .table {
            margin-bottom: 0;
            white-space: nowrap;
        }
        .table thead th {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; border: none; font-weight: 600;
            padding: 15px 12px; font-size: 14px;
        }
        .table tbody td {
            padding: 14px 12px; vertical-align: middle;
            font-size: 13px;
        }
        .table tbody tr { transition: all 0.3s; }
        .table tbody tr:hover {
            background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, transparent 100%);
            transform: scale(1.01);
        }

        /* 标签样式 */
        .label {
            display: inline-block; padding: 6px 14px; border-radius: 20px;
            font-size: 12px; font-weight: 600; white-space: nowrap;
        }
        .label-success { background-color: #e8f5e9; color: #388e3c; }
        .label-danger { background-color: #ffebee; color: #d32f2f; }

        /* 按钮样式 */
        .btn-action {
            padding: 6px 12px; font-size: 12px; margin: 2px;
            border-radius: 20px; transition: all 0.3s;
            white-space: nowrap;
        }
        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        }

        /* 信息提示框 */
        .info-box {
            background: #e3f2fd;
            border-left: 4px solid #2196f3;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .info-box i {
            color: #2196f3;
            margin-right: 10px;
        }

        .info-box p {
            margin: 0;
            color: #1976d2;
            font-size: 14px;
        }

        /* 响应式 */
        @media (max-width: 768px) {
            .sidebar { transform: translateX(-260px); }
            .sidebar-toggle { left: 20px; z-index: 1002; }
            .main-content { margin-left: 0; padding: 15px; }
            .page-header { padding: 30px 20px; }
            .page-header h2 { font-size: 24px; }
            .function-card, .table-card { padding: 20px 15px; }
            .backup-actions { flex-direction: column; }
            .backup-btn { min-width: 100%; }
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
        <div class="logo">
            <i class="fas fa-building"></i>
        </div>
        <h3>物业管理系统</h3>
        <p><i class="fas fa-user-shield"></i> ${sessionScope.currentUser.realName}</p>
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
            <a href="${pageContext.request.contextPath}/admin/chargeItem.jsp">
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
            <a href="${pageContext.request.contextPath}/admin/complaint.jsp">
                <i class="fas fa-comments"></i> 投诉管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/announcement.jsp">
                <i class="fas fa-bullhorn"></i> 公告管理
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/statistics.jsp">
                <i class="fas fa-chart-bar"></i> 统计报表
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/operationLog.jsp">
                <i class="fas fa-history"></i> 操作日志
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/backup.jsp" class="active">
                <i class="fas fa-database"></i> 备份管理
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
<div class="main-content" id="mainContent">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-database"></i> 备份管理</h2>
        <p>数据库备份与恢复，保障数据安全</p>
    </div>

    <!-- 信息提示 -->
    <div class="info-box">
        <i class="fas fa-info-circle"></i>
        <p><strong>温馨提示：</strong>建议定期备份数据库，以防数据丢失。备份文件存储在服务器 backup 目录下。</p>
    </div>

    <!-- 备份操作区 -->
    <div class="function-card">
        <h4><i class="fas fa-tools"></i> 备份操作</h4>
        <div class="backup-actions">
            <div class="backup-btn" onclick="backupDatabase()">
                <i class="fas fa-download"></i>
                <h5>立即备份</h5>
                <p>备份当前数据库</p>
            </div>
            <div class="backup-btn" onclick="showRestoreDialog()">
                <i class="fas fa-upload"></i>
                <h5>恢复数据</h5>
                <p>从备份文件恢复</p>
            </div>
            <div class="backup-btn" onclick="autoBackupSetting()">
                <i class="fas fa-clock"></i>
                <h5>自动备份</h5>
                <p>设置定时备份</p>
            </div>
        </div>
    </div>

    <!-- 备份记录 -->
    <div class="table-card">
        <h4 style="color: #667eea; font-size: 20px; font-weight: 600; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 2px solid #f0f0f0;">
            <i class="fas fa-list"></i> 备份记录
        </h4>
        <div class="table-responsive">
            <table class="table table-hover" id="backupTable">
                <thead>
                <tr>
                    <th>备份ID</th>
                    <th>备份文件名</th>
                    <th>文件大小</th>
                    <th>备份时间</th>
                    <th>备份人</th>
                    <th>状态</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <tr><td colspan="7" class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- 恢复数据模态框 -->
<div class="modal fade" id="restoreModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content" style="border-radius: 15px; overflow: hidden; border: none;">
            <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 25px 30px; border: none;">
                <h5 class="modal-title" style="font-size: 22px; font-weight: 600;">
                    <i class="fas fa-upload"></i> 恢复数据
                </h5>
                <button type="button" class="close" data-dismiss="modal" style="color: white; opacity: 0.9; text-shadow: none; font-size: 32px; font-weight: 300;">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body" style="padding: 30px;">
                <form id="restoreForm">
                    <div class="form-group">
                        <label><i class="fas fa-file"></i> 选择备份文件</label>
                        <select class="form-control" id="backupFileSelect" name="backupFile" required>
                            <option value="">请选择备份文件</option>
                        </select>
                    </div>
                    <div class="alert alert-warning" style="border-radius: 8px;">
                        <i class="fas fa-exclamation-triangle"></i>
                        <strong>警告：</strong>恢复数据将覆盖当前数据库，请谨慎操作！建议先备份当前数据。
                    </div>
                </form>
            </div>
            <div class="modal-footer" style="padding: 20px 30px; background: #f8f9fa; border-top: 1px solid #e9ecef;">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> 取消
                </button>
                <button type="button" class="btn btn-danger" onclick="restoreDatabase()">
                    <i class="fas fa-upload"></i> 确认恢复
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
    $(function() {
        console.log('🚀 备份管理页面初始化...');
        loadBackupList();

        // 检查屏幕宽度，移动端默认隐藏侧边栏
        if ($(window).width() <= 768) {
            toggleSidebar();
        }
    });

    /**
     * 切换侧边栏
     */
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

    /**
     * 加载备份列表
     */
    function loadBackupList() {
        $.ajax({
            url: '${pageContext.request.contextPath}/backup',
            type: 'GET',
            data: { method: 'list' },
            dataType: 'json',
            success: function(result) {
                console.log('✅ 备份列表:', result);

                var success = result.code === 200 || result.success;
                var list = result.data || result.list || [];

                if (success && list.length > 0) {
                    renderTable(list);
                    updateBackupFileSelect(list);
                } else if (success && list.length === 0) {
                    $('#backupTable tbody').html('<tr><td colspan="7" class="text-center text-muted"><i class="fas fa-inbox"></i> 暂无备份记录</td></tr>');
                } else {
                    layer.msg(result.msg || result.message || '加载失败', {icon: 2});
                    $('#backupTable tbody').html('<tr><td colspan="7" class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</td></tr>');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载失败:', error);
                layer.msg('网络错误', {icon: 2});
                $('#backupTable tbody').html('<tr><td colspan="7" class="text-center text-danger"><i class="fas fa-exclamation-triangle"></i> 加载失败</td></tr>');
            }
        });
    }

    /**
     * 渲染表格
     */
    function renderTable(backups) {
        var tbody = $('#backupTable tbody');
        tbody.empty();

        backups.forEach(function(backup) {
            var backupId = backup.backupId || backup.backup_id;
            var fileName = backup.fileName || backup.file_name;
            var fileSize = backup.fileSize || backup.file_size;
            var backupTime = backup.backupTime || backup.backup_time;
            var backupUser = backup.backupUser || backup.backup_user;
            var status = backup.status;

            var statusLabel = status === 1 ? '<span class="label label-success">正常</span>' : '<span class="label label-danger">已删除</span>';

            var tr = '<tr>' +
                '<td>' + (backupId || '-') + '</td>' +
                '<td><i class="fas fa-file-archive"></i> ' + (fileName || '-') + '</td>' +
                '<td>' + formatFileSize(fileSize) + '</td>' +
                '<td>' + formatDateTime(backupTime) + '</td>' +
                '<td><i class="fas fa-user"></i> ' + (backupUser || '-') + '</td>' +
                '<td>' + statusLabel + '</td>' +
                '<td>';

            if (status === 1) {
                tr += '<button class="btn btn-sm btn-success btn-action" onclick="downloadBackup(\'' + fileName + '\')" title="下载"><i class="fas fa-download"></i></button> ' +
                    '<button class="btn btn-sm btn-danger btn-action" onclick="deleteBackup(' + backupId + ')" title="删除"><i class="fas fa-trash"></i></button>';
            } else {
                tr += '<span class="text-muted">-</span>';
            }

            tr += '</td></tr>';
            tbody.append(tr);
        });
    }

    /**
     * 更新恢复对话框的备份文件下拉框
     */
    function updateBackupFileSelect(backups) {
        var select = $('#backupFileSelect');
        select.find('option:not(:first)').remove();

        backups.forEach(function(backup) {
            if (backup.status === 1) {
                var fileName = backup.fileName || backup.file_name;
                var backupTime = backup.backupTime || backup.backup_time;
                select.append('<option value="' + fileName + '">' + fileName + ' (' + formatDateTime(backupTime) + ')</option>');
            }
        });
    }

    /**
     * 立即备份
     */
    function backupDatabase() {
        layer.confirm('确定要备份当前数据库吗？', {
            icon: 3,
            title: '确认备份',
            btn: ['确定', '取消']
        }, function(index) {
            layer.close(index);

            var loadingIndex = layer.load(1, { shade: [0.3, '#000'] });

            $.ajax({
                url: '${pageContext.request.contextPath}/backup',
                type: 'POST',
                data: { method: 'backup' },
                dataType: 'json',
                success: function(result) {
                    layer.close(loadingIndex);
                    console.log('✅ 备份结果:', result);

                    if (result.code === 200 || result.success) {
                        layer.msg('✅ 备份成功！', {icon: 1}, function() {
                            loadBackupList();
                        });
                    } else {
                        layer.msg('❌ ' + (result.msg || result.message || '备份失败'), {icon: 2});
                    }
                },
                error: function(xhr, status, error) {
                    layer.close(loadingIndex);
                    console.error('❌ 备份失败:', error);
                    layer.msg('❌ 备份失败：' + error, {icon: 2});
                }
            });
        });
    }

    /**
     * 显示恢复对话框
     */
    function showRestoreDialog() {
        // 检查是否有备份文件
        if ($('#backupFileSelect option').length <= 1) {
            layer.msg('⚠️ 暂无可用的备份文件', {icon: 0});
            return;
        }
        $('#restoreModal').modal('show');
    }

    /**
     * 恢复数据库
     */
    function restoreDatabase() {
        var backupFile = $('#backupFileSelect').val();

        if (!backupFile) {
            layer.msg('⚠️ 请选择备份文件', {icon: 0});
            return;
        }

        layer.confirm(
            '<div style="padding:20px;">' +
            '<h3 style="color:#f56c6c;margin-bottom:15px;">⚠️ 危险操作警告</h3>' +
            '<p style="line-height:1.8;margin-bottom:10px;">恢复数据库将会：</p>' +
            '<ul style="text-align:left;line-height:2;color:#666;">' +
            '<li>🔴 断开所有用户连接</li>' +
            '<li>🔴 覆盖当前所有数据</li>' +
            '<li>🔴 需要重新登录系统</li>' +
            '</ul>' +
            '<p style="margin-top:15px;color:#f56c6c;font-weight:bold;">此操作不可撤销！</p>' +
            '<p style="margin-top:10px;">备份文件：<span style="color:#409eff;">' + backupFile + '</span></p>' +
            '</div>',
            {
                icon: 0,
                title: '确认恢复数据库',
                btn: ['确定恢复', '取消'],
                btn1: function(index) {
                    layer.close(index);
                    $('#restoreModal').modal('hide');

                    // 显示加载提示
                    var loadIndex = layer.load(2, {
                        shade: [0.3, '#000'],
                        content: '正在恢复数据库，请稍候...',
                        success: function(layero) {
                            layero.find('.layui-layer-content').css({
                                'padding-top': '40px',
                                'width': '250px'
                            });
                        }
                    });

                    $.ajax({
                        url: '${pageContext.request.contextPath}/backup',
                        type: 'POST',
                        data: {
                            method: 'restore',
                            backupFile: backupFile
                        },
                        dataType: 'json',
                        timeout: 60000,
                        success: function(result) {
                            layer.close(loadIndex);
                            console.log('✅ 恢复结果:', result);

                            if (result.code === 200 || result.success) {
                                layer.alert('✅ 恢复成功！<br><br>页面将在 3 秒后跳转到登录页面。', {
                                    icon: 1,
                                    time: 3000
                                }, function(){
                                    window.location.href = '${pageContext.request.contextPath}/login.jsp';
                                });

                                setTimeout(function(){
                                    window.location.href = '${pageContext.request.contextPath}/login.jsp';
                                }, 3000);
                            } else {
                                layer.alert('❌ 恢复失败：' + (result.msg || result.message || '未知错误'), {icon: 2});
                            }
                        },
                        error: function(xhr, status, error) {
                            layer.close(loadIndex);
                            console.error('❌ 恢复失败:', error);
                            if (status === 'timeout') {
                                layer.alert('⏱️ 恢复超时，请稍后检查数据库状态', {icon: 2});
                            } else {
                                layer.alert('❌ 恢复失败：网络错误', {icon: 2});
                            }
                        }
                    });
                }
            }
        );
    }

    /**
     * 下载备份文件
     */
    function downloadBackup(fileName) {
        console.log('📥 下载备份文件:', fileName);
        window.location.href = '${pageContext.request.contextPath}/backup?method=download&fileName=' + encodeURIComponent(fileName);
    }

    /**
     * 删除备份
     */
    function deleteBackup(backupId) {
        layer.confirm('确定要删除这个备份吗？', {
            icon: 0,
            title: '确认删除',
            btn: ['确定', '取消']
        }, function(index) {
            layer.close(index);

            var loadingIndex = layer.load(1, { shade: [0.3, '#000'] });

            $.ajax({
                url: '${pageContext.request.contextPath}/backup',
                type: 'POST',
                data: {
                    method: 'delete',
                    backupId: backupId
                },
                dataType: 'json',
                success: function(result) {
                    layer.close(loadingIndex);
                    console.log('✅ 删除结果:', result);

                    if (result.code === 200 || result.success) {
                        layer.msg('✅ 删除成功！', {icon: 1}, function() {
                            loadBackupList();
                        });
                    } else {
                        layer.msg('❌ ' + (result.msg || result.message || '删除失败'), {icon: 2});
                    }
                },
                error: function(xhr, status, error) {
                    layer.close(loadingIndex);
                    console.error('❌ 删除失败:', error);
                    layer.msg('❌ 删除失败：' + error, {icon: 2});
                }
            });
        });
    }

    /**
     * 自动备份设置
     */
    function autoBackupSetting() {
        layer.open({
            type: 1,
            title: '<i class="fas fa-clock"></i> 自动备份设置',
            area: ['500px', '400px'],
            content: '<div style="padding: 30px;">' +
                '<div class="form-group">' +
                '<label><i class="fas fa-calendar-alt"></i> 备份频率</label>' +
                '<select class="form-control" id="backupFrequency">' +
                '<option value="daily">每天备份</option>' +
                '<option value="weekly">每周备份</option>' +
                '<option value="monthly">每月备份</option>' +
                '</select>' +
                '</div>' +
                '<div class="form-group">' +
                '<label><i class="fas fa-clock"></i> 备份时间</label>' +
                '<input type="time" class="form-control" id="backupTime" value="02:00">' +
                '</div>' +
                '<div class="alert alert-info" style="border-radius: 8px;">' +
                '<i class="fas fa-info-circle"></i> 自动备份将在指定时间执行，请确保服务器正常运行。' +
                '</div>' +
                '<button class="btn btn-primary btn-block" onclick="saveAutoBackupSetting()" style="padding: 12px; font-size: 16px; border-radius: 8px;">' +
                '<i class="fas fa-save"></i> 保存设置' +
                '</button>' +
                '</div>',
            btn: false
        });
    }

    /**
     * 保存自动备份设置
     */
    function saveAutoBackupSetting() {
        var frequency = $('#backupFrequency').val();
        var time = $('#backupTime').val();

        var loadingIndex = layer.load(1, { shade: [0.3, '#000'] });

        $.ajax({
            url: '${pageContext.request.contextPath}/backup',
            type: 'POST',
            data: {
                method: 'setAutoBackup',
                frequency: frequency,
                time: time
            },
            dataType: 'json',
            success: function(result) {
                layer.close(loadingIndex);
                console.log('✅ 设置结果:', result);

                if (result.code === 200 || result.success) {
                    layer.msg('✅ 设置成功！', {icon: 1}, function() {
                        layer.closeAll();
                    });
                } else {
                    layer.msg('❌ ' + (result.msg || result.message || '设置失败'), {icon: 2});
                }
            },
            error: function(xhr, status, error) {
                layer.close(loadingIndex);
                console.error('❌ 设置失败:', error);
                layer.msg('❌ 设置失败：' + error, {icon: 2});
            }
        });
    }

    /**
     * 格式化文件大小
     */
    function formatFileSize(bytes) {
        if (!bytes || bytes === 0) return '0 B';
        var k = 1024;
        var sizes = ['B', 'KB', 'MB', 'GB'];
        var i = Math.floor(Math.log(bytes) / Math.log(k));
        return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
    }

    /**
     * 格式化日期时间
     */
    function formatDateTime(dateTime) {
        if (!dateTime) return '-';
        try {
            var date;
            if (typeof dateTime === 'string') {
                dateTime = dateTime.replace('T', ' ').split('.')[0];
                date = new Date(dateTime);
            } else {
                date = new Date(dateTime);
            }
            if (isNaN(date.getTime())) return dateTime;
            return date.getFullYear() + '-' +
                String(date.getMonth() + 1).padStart(2, '0') + '-' +
                String(date.getDate()).padStart(2, '0') + ' ' +
                String(date.getHours()).padStart(2, '0') + ':' +
                String(date.getMinutes()).padStart(2, '0') + ':' +
                String(date.getSeconds()).padStart(2, '0');
        } catch (e) {
            console.error('日期格式化错误:', e);
            return dateTime;
        }
    }
</script>

</body>
</html>
