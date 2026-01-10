<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人信息 - 智慧社区</title>

    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            font-family: 'Microsoft YaHei', sans-serif;
            min-height: 100vh;
        }

        /* 导航栏 */
        .navbar {
            box-shadow: 0 2px 20px rgba(0,0,0,0.1);
            background: #fff;
            backdrop-filter: blur(10px);
        }
        .navbar-brand {
            font-weight: bold;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        /* 页面头部 */
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px 0;
            margin-bottom: 40px;
            border-radius: 0 0 30px 30px;
            box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
            position: relative;
            overflow: hidden;
        }
        .page-header::before {
            content: '';
            position: absolute;
            top: -50%;
            right: -10%;
            width: 300px;
            height: 300px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
        }
        .page-header::after {
            content: '';
            position: absolute;
            bottom: -30%;
            left: -5%;
            width: 200px;
            height: 200px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
        }
        .page-header .container {
            position: relative;
            z-index: 1;
        }

        /* 卡片样式 */
        .card-box {
            background: #fff;
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 20px rgba(0,0,0,0.08);
            margin-bottom: 25px;
            transition: all 0.3s ease;
            overflow: hidden;
        }
        .card-box:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
        }

        .card-box .card-body {
            padding: 30px;
        }

        /* 信息行样式 */
        .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 18px 0;
            border-bottom: 1px dashed #e9ecef;
            transition: background 0.2s;
        }
        .info-row:hover {
            background: #f8f9fa;
            padding-left: 10px;
            padding-right: 10px;
            margin-left: -10px;
            margin-right: -10px;
            border-radius: 8px;
        }
        .info-row:last-child { border-bottom: none; }

        .info-label {
            color: #6c757d;
            font-weight: 500;
            display: flex;
            align-items: center;
            font-size: 14px;
        }
        .info-label i {
            width: 20px;
            margin-right: 8px;
            color: #667eea;
        }
        .info-value {
            color: #333;
            font-weight: 600;
            font-size: 15px;
        }

        /* 按钮样式 */
        .btn-gradient {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 10px 25px;
            border-radius: 25px;
            font-weight: 500;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }
        .btn-gradient:hover {
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
        }

        /* 表单样式 */
        .form-control {
            border-radius: 10px;
            border: 2px solid #e9ecef;
            padding: 12px 15px;
            transition: all 0.3s;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        /* 安全提示卡片 */
        .security-card {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 15px;
            border-left: 4px solid #28a745;
        }
        .security-card ul li {
            padding: 5px 0;
            position: relative;
            padding-left: 20px;
        }
        .security-card ul li::before {
            content: '✓';
            position: absolute;
            left: 0;
            color: #28a745;
            font-weight: bold;
        }

        /* 头像样式 */
        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            border: 5px solid #fff;
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
            margin: 0 auto 20px;
            display: block;
        }

        /* 标题装饰 */
        .section-title {
            position: relative;
            padding-bottom: 15px;
            margin-bottom: 25px;
        }
        .section-title::after {
            content: '';
            position: absolute;
            left: 0;
            bottom: 0;
            width: 50px;
            height: 3px;
            background: linear-gradient(90deg, #667eea, #764ba2);
            border-radius: 3px;
        }

        /* 模态框样式 */
        .modal-content {
            border-radius: 15px;
            border: none;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        .modal-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px 15px 0 0;
            border: none;
        }
        .modal-header .close {
            color: white;
            opacity: 0.8;
        }
        .modal-header .close:hover {
            opacity: 1;
        }

        /* 加载动画 */
        .loading-container {
            text-align: center;
            padding: 60px 0;
        }
        .loading-spinner {
            width: 50px;
            height: 50px;
            border: 4px solid #f3f3f3;
            border-top: 4px solid #667eea;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* 徽章样式 */
        .info-badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        /* 响应式 */
        @media (max-width: 768px) {
            .page-header {
                padding: 30px 0;
            }
            .card-box .card-body {
                padding: 20px;
            }
            .info-row {
                flex-direction: column;
                align-items: flex-start;
            }
            .info-value {
                margin-top: 5px;
            }
        }
    </style>
</head>
<body>

<!-- 导航栏 -->
<nav class="navbar navbar-expand-lg navbar-light fixed-top">
    <div class="container">
        <a class="navbar-brand" href="#"><i class="fas fa-building mr-2"></i>智慧社区</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item"><a class="nav-link" href="index.jsp"><i class="fas fa-home mr-1"></i>首页</a></li>
                <li class="nav-item"><a class="nav-link" href="payment.jsp"><i class="fas fa-wallet mr-1"></i>缴费中心</a></li>
                <li class="nav-item"><a class="nav-link" href="repair.jsp"><i class="fas fa-tools mr-1"></i>在线报修</a></li>
                <li class="nav-item"><a class="nav-link" href="complaint.jsp"><i class="fas fa-comment-dots mr-1"></i>投诉建议</a></li>
                <li class="nav-item"><a class="nav-link" href="announcement.jsp"><i class="fas fa-bullhorn mr-1"></i>社区公告</a></li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown">
                        <img src="https://ui-avatars.com/api/?name=${sessionScope.user.realName}&background=667eea&color=fff" class="rounded-circle mr-1" width="30">
                        ${sessionScope.user.realName}
                    </a>
                    <div class="dropdown-menu dropdown-menu-right">
                        <a class="dropdown-item active" href="profile.jsp">
                            <i class="fas fa-user mr-2"></i>个人信息
                        </a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/login?action=logout">
                            <i class="fas fa-sign-out-alt mr-2"></i>退出登录
                        </a>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- 页面头部 -->
<div class="page-header" style="margin-top: 56px;">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h2 class="mb-2"><i class="fas fa-user-circle mr-2"></i>个人信息管理</h2>
                <p class="mb-0 opacity-80">查看和修改您的个人资料，保护账户安全</p>
            </div>
            <div class="col-md-4 text-right d-none d-md-block">
                <i class="fas fa-user-shield" style="font-size: 80px; opacity: 0.2;"></i>
            </div>
        </div>
    </div>
</div>

<div class="container pb-5">
    <div class="row">
        <!-- 左侧: 个人信息 -->
        <div class="col-lg-8">
            <div class="card-box">
                <div class="card-body">
                    <!-- 头像和姓名 -->
                    <div class="text-center mb-4 pb-4" style="border-bottom: 2px dashed #e9ecef;">
                        <img src="https://ui-avatars.com/api/?name=${sessionScope.user.realName}&background=667eea&color=fff&size=120&bold=true"
                             class="profile-avatar" alt="头像">
                        <h4 class="mb-1 font-weight-bold" id="displayName">${sessionScope.user.realName}</h4>
                        <span class="info-badge" id="displayOwnerId">${sessionScope.user.username}</span>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="mb-0 section-title">
                            <i class="fas fa-id-card text-primary mr-2"></i>基本信息
                        </h5>
                        <button class="btn btn-sm btn-gradient" onclick="showEditModal()">
                            <i class="fas fa-edit mr-1"></i>编辑资料
                        </button>
                    </div>

                    <div id="ownerInfo">
                        <div class="loading-container">
                            <div class="loading-spinner"></div>
                            <p class="text-muted">加载中...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 右侧: 修改密码 -->
        <div class="col-lg-4">
            <!-- 修改密码卡片 -->
            <div class="card-box">
                <div class="card-body">
                    <h5 class="mb-4 section-title">
                        <i class="fas fa-key text-warning mr-2"></i>修改密码
                    </h5>

                    <form id="passwordForm">
                        <div class="form-group">
                            <label class="font-weight-500">
                                <i class="fas fa-lock mr-1 text-muted"></i>原密码
                                <span class="text-danger">*</span>
                            </label>
                            <input type="password" class="form-control" id="oldPassword"
                                   placeholder="请输入原密码" required>
                        </div>
                        <div class="form-group">
                            <label class="font-weight-500">
                                <i class="fas fa-key mr-1 text-muted"></i>新密码
                                <span class="text-danger">*</span>
                            </label>
                            <input type="password" class="form-control" id="newPassword"
                                   placeholder="请输入新密码" required minlength="8">
                            <small class="form-text text-muted">
                                <i class="fas fa-info-circle mr-1"></i>8位以上，含字母和数字
                            </small>
                        </div>
                        <div class="form-group">
                            <label class="font-weight-500">
                                <i class="fas fa-check-circle mr-1 text-muted"></i>确认新密码
                                <span class="text-danger">*</span>
                            </label>
                            <input type="password" class="form-control" id="confirmPassword"
                                   placeholder="请再次输入新密码" required>
                        </div>
                        <button type="submit" class="btn btn-gradient btn-block">
                            <i class="fas fa-shield-alt mr-1"></i>确认修改
                        </button>
                    </form>
                </div>
            </div>

            <!-- 安全提示 -->
            <div class="card-box security-card">
                <div class="card-body">
                    <h6 class="font-weight-bold mb-3">
                        <i class="fas fa-shield-alt text-success mr-2"></i>密码安全规则
                    </h6>
                    <ul class="small text-muted mb-0 pl-3" style="list-style: none;">
                        <li>密码长度必须 <strong>8位以上</strong></li>
                        <li>必须同时包含 <strong>字母和数字</strong></li>
                        <li>不要使用过于简单的密码</li>
                        <li>不要将密码告诉他人</li>
                        <li>修改密码后需要重新登录</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 编辑资料模态框 -->
<div class="modal fade" id="editModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-edit mr-2"></i>编辑个人资料
                </h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="editForm">
                    <div class="form-group">
                        <label class="font-weight-500">
                            <i class="fas fa-phone mr-1 text-primary"></i>手机号码
                        </label>
                        <input type="tel" class="form-control" id="editPhone"
                               placeholder="请输入11位手机号码" pattern="^1[3-9]\d{9}$">
                        <small class="form-text text-muted">
                            <i class="fas fa-info-circle mr-1"></i>请输入11位手机号码
                        </small>
                    </div>
                    <div class="form-group">
                        <label class="font-weight-500">
                            <i class="fas fa-envelope mr-1 text-primary"></i>电子邮箱
                        </label>
                        <input type="email" class="form-control" id="editEmail"
                               placeholder="请输入电子邮箱">
                        <small class="form-text text-muted">
                            <i class="fas fa-info-circle mr-1"></i>用于接收重要通知
                        </small>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times mr-1"></i>取消
                </button>
                <button type="button" class="btn btn-gradient" onclick="saveInfo()">
                    <i class="fas fa-save mr-1"></i>保存修改
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>

<script>
    var contextPath = '${pageContext.request.contextPath}';

    $(function() {
        loadOwnerInfo();
    });

    // 加载业主信息
    function loadOwnerInfo() {
        $.ajax({
            url: contextPath + '/owner/info',
            type: 'GET',
            data: { action: 'detail' },
            dataType: 'json',
            success: function(res) {
                console.log('✅ 业主信息:', res);

                if((res.success || res.code === 200) && res.data) {
                    var data = res.data;

                    // 更新头部显示
                    $('#displayName').text(data.ownerName || '-');
                    $('#displayOwnerId').text(data.ownerId || '-');

                    // 身份证脱敏
                    var idCard = data.idCard || '';
                    var maskedIdCard = idCard ?
                        (idCard.length >= 18 ?
                                idCard.substring(0, 6) + '********' + idCard.substring(14) :
                                idCard.substring(0, 3) + '***' + idCard.substring(idCard.length - 2)
                        ) : '未设置';

                    var html =
                        '<div class="info-row">' +
                        '<span class="info-label">' +
                        '<i class="fas fa-id-card"></i>业主编号' +
                        '</span>' +
                        '<span class="info-value">' + (data.ownerId || '-') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">' +
                        '<i class="fas fa-user"></i>姓名' +
                        '</span>' +
                        '<span class="info-value">' + (data.ownerName || '-') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">' +
                        '<i class="fas fa-phone"></i>手机号码' +
                        '</span>' +
                        '<span class="info-value">' + (data.phone || '<span class="text-muted">未设置</span>') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">' +
                        '<i class="fas fa-id-card-alt"></i>身份证号' +
                        '</span>' +
                        '<span class="info-value">' + maskedIdCard + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">' +
                        '<i class="fas fa-envelope"></i>电子邮箱' +
                        '</span>' +
                        '<span class="info-value">' + (data.email || '<span class="text-muted">未设置</span>') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">' +
                        '<i class="fas fa-home"></i>房屋数量' +
                        '</span>' +
                        '<span class="info-value">' +
                        '<span class="badge badge-primary badge-pill">' + (data.houseCount || 0) + ' 套</span>' +
                        '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">' +
                        '<i class="fas fa-users"></i>家庭成员' +
                        '</span>' +
                        '<span class="info-value">' + (data.memberCount || 0) + ' 人</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">' +
                        '<i class="fas fa-calendar-check"></i>注册日期' +
                        '</span>' +
                        '<span class="info-value">' + formatDate(data.registerDate) + '</span>' +
                        '</div>';

                    $('#ownerInfo').html(html);

                    // 保存到全局变量,供编辑使用
                    window.currentOwner = data;
                } else {
                    $('#ownerInfo').html(
                        '<div class="alert alert-danger">' +
                        '<i class="fas fa-exclamation-triangle mr-2"></i>' +
                        '加载失败: ' + (res.msg || res.message || '未知错误') +
                        '</div>'
                    );
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 加载失败:', error);
                $('#ownerInfo').html(
                    '<div class="alert alert-danger">' +
                    '<i class="fas fa-times-circle mr-2"></i>加载失败，请刷新重试' +
                    '</div>'
                );
            }
        });
    }

    // 显示编辑模态框
    function showEditModal() {
        if (window.currentOwner) {
            $('#editPhone').val(window.currentOwner.phone || '');
            $('#editEmail').val(window.currentOwner.email || '');
            $('#editModal').modal('show');
        }
    }

    // 保存信息
    function saveInfo() {
        var phone = $('#editPhone').val().trim();
        var email = $('#editEmail').val().trim();

        // 验证手机号
        if (phone && !/^1[3-9]\d{9}$/.test(phone)) {
            alert('❌ 手机号格式不正确');
            $('#editPhone').focus();
            return;
        }

        // 验证邮箱
        if (email && !/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(email)) {
            alert('❌ 邮箱格式不正确');
            $('#editEmail').focus();
            return;
        }

        $.ajax({
            url: contextPath + '/owner/info',
            type: 'POST',
            data: {
                action: 'updateInfo',
                phone: phone,
                email: email
            },
            dataType: 'json',
            success: function(res) {
                if (res.success || res.code === 200) {
                    alert('✅ 保存成功！');
                    $('#editModal').modal('hide');
                    loadOwnerInfo();
                } else {
                    alert('❌ 保存失败: ' + (res.msg || res.message || '未知错误'));
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 保存失败:', error);
                alert('❌ 保存失败，请重试');
            }
        });
    }

    // 🔥 修改密码 - 带完整验证
    $('#passwordForm').submit(function(e) {
        e.preventDefault();

        var oldPassword = $('#oldPassword').val().trim();
        var newPassword = $('#newPassword').val().trim();
        var confirmPassword = $('#confirmPassword').val().trim();

        // 1. 基础验证
        if (!oldPassword) {
            alert('❌ 请输入原密码');
            $('#oldPassword').focus();
            return;
        }

        if (!newPassword) {
            alert('❌ 请输入新密码');
            $('#newPassword').focus();
            return;
        }

        // 2. 密码长度验证
        if (newPassword.length < 8) {
            alert('❌ 新密码长度不能少于8位');
            $('#newPassword').focus();
            return;
        }

        // 3. 🔥 密码强度验证(必须包含字母和数字)
        var hasLetter = /[a-zA-Z]/.test(newPassword);
        var hasNumber = /[0-9]/.test(newPassword);

        if (!hasLetter || !hasNumber) {
            alert('❌ 新密码必须同时包含字母和数字');
            $('#newPassword').focus();
            return;
        }

        // 4. 确认密码验证
        if (newPassword !== confirmPassword) {
            alert('❌ 两次输入的新密码不一致');
            $('#confirmPassword').focus();
            return;
        }

        // 5. 新旧密码不能相同
        if (oldPassword === newPassword) {
            alert('❌ 新密码不能与原密码相同');
            $('#newPassword').focus();
            return;
        }

        if (!confirm('🔐 确定要修改密码吗？修改后需要重新登录。')) {
            return;
        }

        // 提交请求
        $.ajax({
            url: contextPath + '/owner/info',
            type: 'POST',
            data: {
                action: 'updatePassword',
                oldPassword: oldPassword,
                newPassword: newPassword,
                confirmPassword: confirmPassword
            },
            dataType: 'json',
            success: function(res) {
                if (res.success || res.code === 200) {
                    alert('✅ 密码修改成功，请重新登录！');
                    window.location.href = contextPath + '/login.jsp';
                } else {
                    alert('❌ 修改失败: ' + (res.msg || res.message || '未知错误'));
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ 修改失败:', error);
                alert('❌ 修改失败，请重试');
            }
        });
    });

    // 日期格式化
    function formatDate(timestamp) {
        if (!timestamp) return '-';
        try {
            var date = new Date(timestamp);
            if (isNaN(date.getTime())) return '-';

            var year = date.getFullYear();
            var month = String(date.getMonth() + 1).padStart(2, '0');
            var day = String(date.getDate()).padStart(2, '0');

            return year + '年' + month + '月' + day + '日';
        } catch(e) {
            return '-';
        }
    }
</script>

</body>
</html>
