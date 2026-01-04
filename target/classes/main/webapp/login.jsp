<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统登录 - 智慧社区物业管理系统</title>

    <!-- 引入 Bootstrap 4 & FontAwesome -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/animate.css/4.1.1/animate.min.css">

    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Microsoft YaHei', sans-serif;
        }

        .login-card {
            width: 100%;
            max-width: 420px;
            background: rgba(255, 255, 255, 0.98);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
            overflow: hidden;
            border: none;
        }

        .login-header {
            background: transparent;
            padding: 40px 30px 20px;
            text-align: center;
        }

        .login-header h3 {
            color: #333;
            font-weight: 800;
            letter-spacing: 1px;
            margin-bottom: 5px;
        }

        .login-header p {
            color: #888;
            font-size: 13px;
            margin: 0;
        }

        .login-body {
            padding: 20px 40px 40px;
        }

        /* 身份切换按钮组 */
        .role-group {
            display: flex;
            justify-content: space-between;
            margin-bottom: 25px;
            background: #f1f3f5;
            padding: 5px;
            border-radius: 8px;
        }

        .role-label {
            flex: 1;
            text-align: center;
            padding: 8px 0;
            cursor: pointer;
            border-radius: 6px;
            color: #666;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.3s;
            margin-bottom: 0;
        }

        .role-label:hover {
            color: #333;
        }

        /* 选中状态 */
        .role-input:checked + .role-label {
            background: white;
            color: #667eea;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .role-input {
            display: none;
        }

        /* 输入框样式 */
        .form-group {
            position: relative;
            margin-bottom: 20px;
        }

        .form-control {
            height: 50px;
            padding-left: 45px;
            border-radius: 8px;
            border: 1px solid #e1e1e1;
            background: #fcfcfc;
            font-size: 15px;
            transition: all 0.3s;
        }

        .form-control:focus {
            background: #fff;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .input-icon {
            position: absolute;
            left: 15px;
            top: 17px;
            color: #aaa;
            font-size: 16px;
            transition: color 0.3s;
        }

        .form-control:focus + .input-icon {
            color: #667eea;
        }

        /* 登录按钮 */
        .btn-login {
            height: 50px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 8px;
            color: white;
            font-weight: bold;
            font-size: 16px;
            letter-spacing: 2px;
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
            transition: all 0.3s;
        }

        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
            color: white;
        }

        .btn-login:disabled {
            opacity: 0.7;
            cursor: not-allowed;
        }

        /* 🔥 错误提示样式 */
        .alert {
            border-radius: 8px;
            border: none;
            font-size: 14px;
            padding: 12px 15px;
            margin-bottom: 20px;
        }

        .alert-danger {
            background: #fff5f5;
            color: #c53030;
            border-left: 4px solid #fc8181;
        }

        /* 🔥 警告提示样式（账号被禁用） */
        .alert-warning {
            background: #fffaf0;
            color: #c05621;
            border-left: 4px solid #ed8936;
        }

        .alert i {
            font-size: 16px;
            margin-right: 8px;
        }

        /* 底部提示 */
        .test-account-box {
            margin-top: 25px;
            padding: 12px;
            background: #fff8e1;
            border: 1px dashed #ffe082;
            border-radius: 6px;
            color: #f57c00;
            font-size: 13px;
            text-align: center;
        }

        .copyright {
            position: absolute;
            bottom: 20px;
            color: rgba(255,255,255,0.6);
            font-size: 12px;
        }

        /* 🔥 禁用账号的特殊样式 */
        .alert-warning .contact-admin {
            display: block;
            margin-top: 8px;
            font-size: 13px;
            color: #744210;
        }

        .alert-warning .contact-admin i {
            font-size: 13px;
        }
    </style>
</head>
<body>

<div class="login-card animate__animated animate__fadeInUp">
    <div class="login-header">
        <h3><i class="fas fa-city text-primary mr-2"></i>智慧物业云平台</h3>
        <p>Smart Community Management System</p>
    </div>

    <div class="login-body">
        <!-- 🔥 普通错误提示 -->
        <div id="errorAlert" class="alert alert-danger" style="display: none;">
            <i class="fas fa-exclamation-circle"></i>
            <span id="errorMsg"></span>
        </div>

        <!-- 🔥 账号被禁用提示 -->
        <div id="disabledAlert" class="alert alert-warning" style="display: none;">
            <div>
                <i class="fas fa-ban"></i>
                <span id="disabledMsg"></span>
            </div>
            <small class="contact-admin">
                <i class="fas fa-info-circle"></i> 如有疑问，请联系系统管理员
            </small>
        </div>

        <form id="loginForm">
            <!-- 身份选择 -->
            <div class="role-group">
                <input type="radio" id="role_admin" name="role" value="admin" class="role-input" checked onchange="changeRole('admin')">
                <label for="role_admin" class="role-label"><i class="fas fa-user-shield mr-1"></i> 管理员</label>

                <input type="radio" id="role_owner" name="role" value="owner" class="role-input" onchange="changeRole('owner')">
                <label for="role_owner" class="role-label"><i class="fas fa-home mr-1"></i> 业主</label>

                <input type="radio" id="role_finance" name="role" value="finance" class="role-input" onchange="changeRole('finance')">
                <label for="role_finance" class="role-label"><i class="fas fa-coins mr-1"></i> 财务</label>
            </div>

            <!-- 账号输入 -->
            <div class="form-group">
                <input type="text" class="form-control" id="username" name="username" placeholder="请输入账号" required autocomplete="off">
                <i class="fas fa-user input-icon"></i>
            </div>

            <!-- 密码输入 -->
            <div class="form-group">
                <input type="password" class="form-control" id="password" name="password" placeholder="请输入密码" required>
                <i class="fas fa-lock input-icon"></i>
            </div>

            <button type="submit" class="btn btn-block btn-login" id="loginBtn">
                立即登录
            </button>
        </form>

        <!-- 动态测试账号提示 -->
        <div class="test-account-box" id="hintBox">
            <i class="fas fa-lightbulb mr-1"></i>
            <span id="hintText">管理员账号：admin / admin123</span>
        </div>
    </div>
</div>

<div class="copyright">
    &copy; 2025 社区物业管理系统 | Designed by Developer
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    // 页面加载时初始化
    $(document).ready(function() {
        // 默认聚焦用户名
        $('#username').focus();

        // 监听回车
        $(document).keypress(function(e) {
            if(e.which == 13) {
                $('#loginBtn').click();
            }
        });

        // 登录表单提交
        $('#loginForm').on('submit', function(e) {
            e.preventDefault();

            var username = $('#username').val().trim();
            var password = $('#password').val().trim();
            var role = $('input[name="role"]:checked').val();

            if(!username || !password) {
                showError('请输入账号和密码');
                return;
            }

            // 按钮加载状态
            var $btn = $('#loginBtn');
            var originalText = $btn.text();
            $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> 登录中...');

            // 隐藏所有提示
            $('#errorAlert, #disabledAlert').slideUp();

            // 发送 AJAX 请求
            $.ajax({
                url: '${pageContext.request.contextPath}/login?action=login',
                type: 'POST',
                data: {
                    username: username,
                    password: password,
                    role: role
                },
                dataType: 'json',
                success: function(res) {
                    console.log('登录响应:', res);

                    if(res.success || res.code === 200) {
                        $btn.html('<i class="fas fa-check"></i> 登录成功').removeClass('btn-login').addClass('btn-success');

                        // 延迟跳转
                        setTimeout(function() {
                            if(res.data) {
                                window.location.href = '${pageContext.request.contextPath}/' + res.data;
                            } else {
                                if(role === 'admin') window.location.href = '${pageContext.request.contextPath}/admin/index.jsp';
                                else if(role === 'owner') window.location.href = '${pageContext.request.contextPath}/owner/index.jsp';
                                else if(role === 'finance') window.location.href = '${pageContext.request.contextPath}/finance/index.jsp';
                                else window.location.href = '${pageContext.request.contextPath}/index.jsp';
                            }
                        }, 800);
                    } else {
                        // 🔥 判断是否是账号被禁用
                        var errorMsg = res.message || '登录失败';

                        if(errorMsg.indexOf('已被禁用') !== -1 ||
                            errorMsg.indexOf('已禁用') !== -1 ||
                            errorMsg.indexOf('被禁用') !== -1 ||
                            res.code === 403) {
                            // 显示禁用警告
                            showDisabled(errorMsg);
                        } else {
                            // 显示普通错误
                            showError(errorMsg);
                        }

                        $btn.prop('disabled', false).text(originalText);
                    }
                },
                error: function(xhr) {
                    console.error('请求失败:', xhr);
                    showError('服务器连接失败，请检查网络');
                    $btn.prop('disabled', false).text(originalText);
                }
            });
        });
    });

    // 切换角色时更新提示文案
    function changeRole(role) {
        var hint = '';
        var user = '';
        var pass = '';

        if(role === 'admin') {
            user = 'admin'; pass = 'admin123';
            hint = '管理员账号：' + user + ' / ' + pass;
        } else if(role === 'owner') {
            user = '00010001'; pass = '123456';
            hint = '业主账号(ID)：' + user + ' / ' + pass;
        } else if(role === 'finance') {
            user = 'finance01'; pass = '123456';
            hint = '财务账号：' + user + ' / ' + pass;
        }

        // 动画切换提示
        $('#hintBox').fadeOut(200, function() {
            $('#hintText').text(hint);
            $(this).fadeIn(200);
        });
    }

    // 🔥 显示普通错误（红色）
    function showError(msg) {
        $('#disabledAlert').slideUp();
        $('#errorMsg').text(msg);
        $('#errorAlert').slideDown();
    }

    // 🔥 显示禁用警告（橙色）
    function showDisabled(msg) {
        $('#errorAlert').slideUp();
        $('#disabledMsg').text(msg);
        $('#disabledAlert').slideDown();
    }
</script>
</body>
</html>
