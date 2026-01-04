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
        body { background-color: #f5f7fa; font-family: 'Microsoft YaHei', sans-serif; }
        .navbar { box-shadow: 0 2px 10px rgba(0,0,0,0.05); background: #fff; }
        .navbar-brand { font-weight: bold; color: #667eea !important; }

        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; padding: 30px 0; margin-bottom: 30px;
            border-radius: 0 0 20px 20px;
        }

        .card-box {
            background: #fff; border-radius: 10px; border: none;
            box-shadow: 0 2px 15px rgba(0,0,0,0.05); margin-bottom: 25px;
        }

        .info-row {
            display: flex; justify-content: space-between;
            padding: 15px 0; border-bottom: 1px solid #f0f0f0;
        }
        .info-row:last-child { border-bottom: none; }
        .info-label { color: #888; font-weight: 500; }
        .info-value { color: #333; font-weight: 600; }

        .btn-gradient {
            background: linear-gradient(45deg, #667eea, #764ba2);
            border: none; color: white;
        }
        .btn-gradient:hover {
            background: linear-gradient(45deg, #764ba2, #667eea);
            color: white;
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
                <li class="nav-item"><a class="nav-link" href="index.jsp">首页</a></li>
                <li class="nav-item"><a class="nav-link" href="payment.jsp">缴费中心</a></li>
                <li class="nav-item"><a class="nav-link" href="repair.jsp">在线报修</a></li>
                <li class="nav-item"><a class="nav-link" href="complaint.jsp">投诉建议</a></li>
                <li class="nav-item"><a class="nav-link" href="announcement.jsp">社区公告</a></li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown">
                        <img src="https://ui-avatars.com/api/?name=${sessionScope.user.realName}&background=random" class="rounded-circle mr-1" width="30">
                        ${sessionScope.user.realName}
                    </a>
                    <div class="dropdown-menu dropdown-menu-right">
                        <a class="dropdown-item active" href="profile.jsp"><i class="fas fa-user mr-2"></i>个人信息</a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt mr-2"></i>退出登录</a>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- 页面头部 -->
<div class="page-header" style="margin-top: 56px;">
    <div class="container">
        <h2><i class="fas fa-user-circle mr-2"></i>个人信息</h2>
        <p class="mb-0">查看和修改您的个人资料</p>
    </div>
</div>

<div class="container">
    <div class="row">
        <!-- 左侧:个人信息 -->
        <div class="col-lg-8">
            <div class="card-box">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="mb-0"><i class="fas fa-id-card text-primary mr-2"></i>基本信息</h5>
                        <button class="btn btn-sm btn-gradient" onclick="showEditModal()">
                            <i class="fas fa-edit mr-1"></i>编辑资料
                        </button>
                    </div>

                    <div id="ownerInfo">
                        <div class="text-center py-4">
                            <i class="fas fa-spinner fa-spin fa-2x text-muted"></i>
                            <p class="text-muted mt-2">加载中...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 右侧:修改密码 -->
        <div class="col-lg-4">
            <div class="card-box">
                <div class="card-body">
                    <h5 class="mb-4"><i class="fas fa-key text-warning mr-2"></i>修改密码</h5>

                    <form id="passwordForm">
                        <div class="form-group">
                            <label>原密码 <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" id="oldPassword" required>
                        </div>
                        <div class="form-group">
                            <label>新密码 <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" id="newPassword" required minlength="8">
                            <small class="form-text text-muted">密码长度8位以上,必须包含字母和数字</small>
                        </div>
                        <div class="form-group">
                            <label>确认新密码 <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" id="confirmPassword" required>
                        </div>
                        <button type="submit" class="btn btn-gradient btn-block">
                            <i class="fas fa-lock mr-1"></i>确认修改
                        </button>
                    </form>
                </div>
            </div>

            <!-- 安全提示 -->
            <div class="card-box bg-light">
                <div class="card-body">
                    <h6 class="font-weight-bold mb-3">
                        <i class="fas fa-shield-alt text-success mr-2"></i>密码安全规则
                    </h6>
                    <ul class="small text-muted mb-0 pl-3">
                        <li>密码长度必须<strong>8位以上</strong></li>
                        <li>必须同时包含<strong>字母和数字</strong></li>
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
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-edit mr-2"></i>编辑个人资料</h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="editForm">
                    <div class="form-group">
                        <label>手机号码</label>
                        <input type="tel" class="form-control" id="editPhone" pattern="^1[3-9]\d{9}$">
                        <small class="form-text text-muted">请输入11位手机号码</small>
                    </div>
                    <div class="form-group">
                        <label>电子邮箱</label>
                        <input type="email" class="form-control" id="editEmail">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-gradient" onclick="saveInfo()">
                    <i class="fas fa-save mr-1"></i>保存
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
                console.log('业主信息:', res);

                if((res.success || res.code === 200) && res.data) {
                    var data = res.data;
                    var html = '<div class="info-row">' +
                        '<span class="info-label">业主编号</span>' +
                        '<span class="info-value">' + (data.ownerId || '-') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">姓名</span>' +
                        '<span class="info-value">' + (data.ownerName || '-') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">手机号码</span>' +
                        '<span class="info-value">' + (data.phone || '未设置') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">身份证号</span>' +
                        '<span class="info-value">' + (data.idCard || '未设置') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">电子邮箱</span>' +
                        '<span class="info-value">' + (data.email || '未设置') + '</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">家庭成员</span>' +
                        '<span class="info-value">' + (data.memberCount || 0) + ' 人</span>' +
                        '</div>' +
                        '<div class="info-row">' +
                        '<span class="info-label">注册日期</span>' +
                        '<span class="info-value">' + formatDate(data.registerDate) + '</span>' +
                        '</div>';

                    $('#ownerInfo').html(html);

                    // 保存到全局变量,供编辑使用
                    window.currentOwner = data;
                } else {
                    $('#ownerInfo').html('<div class="alert alert-danger">加载失败: ' + (res.msg || res.message || '未知错误') + '</div>');
                }
            },
            error: function(xhr, status, error) {
                console.error('加载失败:', error);
                $('#ownerInfo').html('<div class="alert alert-danger">加载失败,请刷新重试</div>');
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
            alert('手机号格式不正确');
            return;
        }

        // 验证邮箱
        if (email && !/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(email)) {
            alert('邮箱格式不正确');
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
                    alert('保存成功!');
                    $('#editModal').modal('hide');
                    loadOwnerInfo();
                } else {
                    alert('保存失败: ' + (res.msg || res.message || '未知错误'));
                }
            },
            error: function(xhr, status, error) {
                console.error('保存失败:', error);
                alert('保存失败,请重试');
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
            alert('请输入原密码');
            return;
        }

        if (!newPassword) {
            alert('请输入新密码');
            return;
        }

        // 2. 密码长度验证
        if (newPassword.length < 8) {
            alert('新密码长度不能少于8位');
            return;
        }

        // 3. 🔥 密码强度验证(必须包含字母和数字)
        var hasLetter = /[a-zA-Z]/.test(newPassword);
        var hasNumber = /[0-9]/.test(newPassword);

        if (!hasLetter || !hasNumber) {
            alert('新密码必须同时包含字母和数字');
            return;
        }

        // 4. 确认密码验证
        if (newPassword !== confirmPassword) {
            alert('两次输入的新密码不一致');
            return;
        }

        // 5. 新旧密码不能相同
        if (oldPassword === newPassword) {
            alert('新密码不能与原密码相同');
            return;
        }

        if (!confirm('确定要修改密码吗?修改后需要重新登录。')) {
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
                    alert('密码修改成功,请重新登录!');
                    window.location.href = contextPath + '/login.jsp';
                } else {
                    alert('修改失败: ' + (res.msg || res.message || '未知错误'));
                }
            },
            error: function(xhr, status, error) {
                console.error('修改失败:', error);
                alert('修改失败,请重试');
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

            return year + '-' + month + '-' + day;
        } catch(e) {
            return '-';
        }
    }
</script>

</body>
</html>
