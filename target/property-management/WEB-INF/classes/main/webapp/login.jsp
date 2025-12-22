<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - 社区物业管理系统</title>

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">

    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .login-card {
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            border-radius: 10px;
            overflow: hidden;
            background: white;
        }
        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 30px;
        }
        .card-body {
            padding: 40px;
        }
        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 12px;
            font-size: 16px;
            font-weight: bold;
            color: white;
        }
        .btn-login:hover {
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
            color: white;
        }
        .btn-login:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card login-card">
                <div class="card-header text-center text-white">
                    <h3 class="mb-0">🏘️ 社区物业管理系统</h3>
                    <p class="mb-0 mt-2">Community Property Management System</p>
                </div>
                <div class="card-body">
                    <!-- 错误提示 -->
                    <div id="errorAlert" class="alert alert-danger alert-dismissible fade show" style="display: none;">
                        <strong>❌ 登录失败！</strong>
                        <span id="errorMessage"></span>
                        <button type="button" class="close" onclick="$('#errorAlert').hide()">
                            <span>&times;</span>
                        </button>
                    </div>

                    <!-- 登录表单 -->
                    <form id="loginForm">
                        <div class="form-group">
                            <label for="username">
                                <i class="fas fa-user"></i> 👤 用户名
                            </label>
                            <input type="text"
                                   class="form-control"
                                   id="username"
                                   name="username"
                                   placeholder="请输入用户名"
                                   autocomplete="off"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="password">
                                <i class="fas fa-lock"></i> 🔒 密码
                            </label>
                            <input type="password"
                                   class="form-control"
                                   id="password"
                                   name="password"
                                   placeholder="请输入密码"
                                   autocomplete="off"
                                   required>
                        </div>

                        <button type="submit" class="btn btn-primary btn-block btn-login" id="loginBtn">
                            登录
                        </button>
                    </form>

                    <hr class="my-4">

                    <div class="text-center text-muted">
                        <small>💡 测试账号：admin / admin123</small>
                    </div>
                </div>
            </div>

            <!-- 版权信息 -->
            <div class="text-center mt-3 text-white">
                <small>&copy; 2024 社区物业管理系统</small>
            </div>
        </div>
    </div>
</div>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    $(document).ready(function() {
        console.log('登录页面加载完成');

        // 表单提交
        $('#loginForm').on('submit', function(e) {
            e.preventDefault(); // 阻止默认提交

            var username = $('#username').val().trim();
            var password = $('#password').val().trim();

            console.log('准备登录，用户名：' + username);

            // 验证
            if(!username) {
                showError('请输入用户名！');
                return;
            }
            if(!password) {
                showError('请输入密码！');
                return;
            }

            // 禁用按钮，防止重复提交
            $('#loginBtn').prop('disabled', true).html('<span class="spinner-border spinner-border-sm mr-2"></span>登录中...');
            $('#errorAlert').hide();

            // AJAX 提交
            $.ajax({
                url: '${pageContext.request.contextPath}/login?action=login',
                type: 'POST',
                data: {
                    username: username,
                    password: password
                },
                dataType: 'json',
                timeout: 10000, // 10秒超时
                success: function(response) {
                    console.log('服务器响应：', response);

                    if(response.success) {
                        // 登录成功
                        console.log('登录成功！');
                        $('#errorAlert').hide();

                        // 显示成功提示
                        $('#loginBtn').html('✅ 登录成功！').removeClass('btn-primary').addClass('btn-success');

                        // 延迟跳转
                        setTimeout(function() {
                            if(response.data) {
                                console.log('跳转到：' + response.data);
                                window.location.href = '${pageContext.request.contextPath}/' + response.data;
                            } else {
                                console.log('跳转到首页');
                                window.location.href = '${pageContext.request.contextPath}/index.jsp';
                            }
                        }, 500);

                    } else {
                        // 登录失败
                        console.error('登录失败：' + response.message);
                        showError(response.message || '登录失败，请检查用户名和密码');
                        resetButton();
                    }
                },
                error: function(xhr, status, error) {
                    console.error('请求失败：', xhr, status, error);
                    console.error('状态码：', xhr.status);
                    console.error('响应内容：', xhr.responseText);

                    if(xhr.status === 404) {
                        showError('登录接口不存在（404），请检查服务器配置');
                    } else if(xhr.status === 500) {
                        showError('服务器内部错误（500），请查看后台日志');
                    } else if(status === 'timeout') {
                        showError('请求超时，请检查网络连接');
                    } else {
                        showError('网络错误，请稍后重试');
                    }

                    resetButton();
                }
            });
        });

        // 显示错误信息
        function showError(message) {
            $('#errorMessage').text(message);
            $('#errorAlert').fadeIn();
        }

        // 重置按钮
        function resetButton() {
            $('#loginBtn').prop('disabled', false).html('登录').removeClass('btn-success').addClass('btn-primary');
        }

        // 回车登录
        $('#username, #password').on('keypress', function(e) {
            if(e.which === 13) {
                $('#loginForm').submit();
            }
        });
    });
</script>
</body>
</html>
