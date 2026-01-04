<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户管理 - 物业管理系统</title>

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

        /* 角色标签 */
        .role-badge {
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }
        .role-admin { background-color: #e3f2fd; color: #1976d2; }
        .role-finance { background-color: #fff3e0; color: #f57c00; }
        .role-owner { background-color: #f3e5f5; color: #7b1fa2; }

        /* 复选框样式 */
        .checkbox-cell { width: 40px; text-align: center; }
        input[type="checkbox"] { width: 18px; height: 18px; cursor: pointer; }
    </style>
</head>
<body>

<div class="container-fluid">
    <!-- 页面标题 -->
    <div class="page-header">
        <h2><i class="fas fa-users-cog"></i> 用户管理</h2>
        <p>管理系统后台用户及业主账号，包括权限分配、状态控制及密码重置</p>
    </div>

    <!-- 搜索和操作区 -->
    <div class="search-box">
        <div class="row mb-3">
            <div class="col-md-3">
                <input type="text" class="form-control" id="searchKeyword"
                       placeholder="搜索用户名、真实姓名或手机号">
            </div>
            <!-- 🔥 新增：角色筛选下拉框 -->
            <div class="col-md-2">
                <select class="form-control" id="searchRole">
                    <option value="">全部角色</option>
                    <option value="admin">系统管理员</option>
                    <option value="finance">财务人员</option>
                    <option value="owner">业主</option>
                </select>
            </div>
            <!-- 🔥 新增：状态筛选下拉框 -->
            <div class="col-md-2">
                <select class="form-control" id="searchStatus">
                    <option value="">全部状态</option>
                    <option value="1">启用</option>
                    <option value="0">禁用</option>
                </select>
            </div>
            <div class="col-md-2">
                <button class="btn btn-primary btn-block" onclick="searchUser()">
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
                        <i class="fas fa-user-plus"></i> 添加用户
                    </button>
                    <button class="btn btn-danger" onclick="batchDelete()">
                        <i class="fas fa-trash-alt"></i> 批量删除
                    </button>
                    <!-- 🔥 新增：快速筛选按钮 -->
                    <button class="btn btn-outline-primary" onclick="quickFilter('admin')">
                        <i class="fas fa-user-shield"></i> 管理员
                    </button>
                    <button class="btn btn-outline-warning" onclick="quickFilter('finance')">
                        <i class="fas fa-calculator"></i> 财务人员
                    </button>
                    <button class="btn btn-outline-info" onclick="quickFilter('owner')">
                        <i class="fas fa-users"></i> 业主
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
                    <th>ID</th>
                    <th>用户名</th>
                    <th>真实姓名</th>
                    <th>角色</th>
                    <th>手机号</th>
                    <th>状态</th>
                    <th>创建时间</th>
                    <th width="280">操作</th>
                </tr>
                </thead>
                <tbody id="userTableBody">
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

<!-- 添加/编辑用户模态框 -->
<div class="modal fade" id="userModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">添加用户</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="userForm">
                    <input type="hidden" id="formMethod" value="add">
                    <input type="hidden" id="userId">

                    <div class="form-group">
                        <label class="form-label required">用户名</label>
                        <input type="text" class="form-control" id="username"
                               name="username" required placeholder="请输入登录账号">
                    </div>

                    <div class="form-group" id="passwordGroup">
                        <label class="form-label required">登录密码</label>
                        <input type="password" class="form-control" id="password"
                               name="password" placeholder="请输入密码">
                        <small class="text-muted">默认建议: 123456</small>
                    </div>

                    <div class="form-group">
                        <label class="form-label required">真实姓名</label>
                        <input type="text" class="form-control" id="realName"
                               name="realName" required placeholder="请输入真实姓名">
                    </div>

                    <div class="form-group">
                        <label class="form-label required">用户角色</label>
                        <select class="form-control" id="userRole" name="userRole" required>
                            <option value="">请选择角色</option>
                            <option value="admin">系统管理员</option>
                            <option value="finance">财务人员</option>
                            <option value="owner">业主</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">手机号</label>
                        <input type="text" class="form-control" id="phone"
                               name="phone" placeholder="请输入11位手机号" pattern="^1[3-9]\d{9}$">
                    </div>

                    <div class="form-group">
                        <label class="form-label">身份证号</label>
                        <input type="text" class="form-control" id="idCard"
                               name="idCard" placeholder="请输入身份证号">
                    </div>

                    <!-- 编辑模式下显示状态选择 -->
                    <div class="form-group" id="statusGroup" style="display:none;">
                        <label class="form-label">账号状态</label>
                        <select class="form-control" id="status" name="status">
                            <option value="1">启用</option>
                            <option value="0">禁用</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="saveUser()">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 重置密码模态框 -->
<div class="modal fade" id="pwdModal" tabindex="-1">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header bg-warning text-white">
                <h5 class="modal-title">重置密码</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="pwdForm">
                    <input type="hidden" id="pwdUserId">
                    <div class="form-group">
                        <label class="form-label">新密码</label>
                        <input type="text" class="form-control" id="newPassword" value="123456">
                        <small class="text-muted">默认重置为: 123456</small>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-warning" onclick="confirmResetPwd()">确认重置</button>
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

    // 页面加载完成后执行
    $(document).ready(function() {
        loadUserList(1);

        // 回车搜索
        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) {
                searchUser();
            }
        });
    });

    /**
     * 🔥 加载用户列表（支持角色和状态筛选）
     */
    function loadUserList(pageNum) {
        currentPage = pageNum || currentPage;
        var keyword = $('#searchKeyword').val();
        var role = $('#searchRole').val();      // 🔥 获取角色筛选
        var status = $('#searchStatus').val();  // 🔥 获取状态筛选

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/user',
            type: 'GET',
            data: {
                method: 'list',
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: keyword,
                userRole: role,    // 🔥 传递角色参数
                status: status     // 🔥 传递状态参数
            },
            success: function(response) {
                console.log("后端返回数据:", response);

                if (response.code === 0 || response.code === 200) {
                    var userList = response.data;
                    totalCount = response.count || response.total || 0;

                    renderUserTable(userList);
                    renderPagination();
                } else {
                    layer.msg(response.msg || '加载失败', {icon: 2});
                }
            },
            error: function() {
                layer.msg('网络错误', {icon: 2});
                $('#userTableBody').html('<tr><td colspan="9" class="text-center text-danger">加载失败，请检查网络或服务器日志</td></tr>');
            }
        });
    }

    /**
     * 渲染用户表格
     */
    function renderUserTable(users) {
        var tbody = $('#userTableBody');
        tbody.empty();

        if (!users || users.length === 0) {
            tbody.append('<tr><td colspan="9" class="text-center text-muted">暂无数据</td></tr>');
            return;
        }

        $.each(users, function(i, user) {
            // 角色样式
            var roleClass = 'role-' + user.userRole;
            var roleName = getRoleName(user.userRole);

            // 状态样式
            var statusBadge = user.status === 1
                ? '<span class="status-badge status-active">启用</span>'
                : '<span class="status-badge status-inactive">禁用</span>';

            // 状态切换按钮
            var toggleBtn = user.status === 1
                ? '<button class="btn btn-sm btn-outline-secondary btn-action" onclick="updateStatus(' + user.userId + ', 0)" title="禁用"><i class="fas fa-ban"></i></button>'
                : '<button class="btn btn-sm btn-outline-success btn-action" onclick="updateStatus(' + user.userId + ', 1)" title="启用"><i class="fas fa-check"></i></button>';

            var row = '<tr>' +
                '<td class="checkbox-cell"><input type="checkbox" class="row-checkbox" value="' + user.userId + '"></td>' +
                '<td>' + user.userId + '</td>' +
                '<td>' + (user.username || '-') + '</td>' +
                '<td>' + (user.realName || '-') + '</td>' +
                '<td><span class="role-badge ' + roleClass + '">' + roleName + '</span></td>' +
                '<td>' + (user.phone || '-') + '</td>' +
                '<td>' + statusBadge + '</td>' +
                '<td>' + formatDate(user.createTime) + '</td>' +
                '<td>' +
                '<button class="btn btn-sm btn-info btn-action" onclick="viewUser(' + user.userId + ')" title="查看"><i class="fas fa-eye"></i></button>' +
                '<button class="btn btn-sm btn-primary btn-action" onclick="editUser(' + user.userId + ')" title="编辑"><i class="fas fa-edit"></i></button>' +
                '<button class="btn btn-sm btn-warning btn-action" onclick="openPwdModal(' + user.userId + ')" title="重置密码"><i class="fas fa-key"></i></button>' +
                toggleBtn +
                '<button class="btn btn-sm btn-danger btn-action" onclick="deleteUser(' + user.userId + ')" title="删除"><i class="fas fa-trash"></i></button>' +
                '</td>' +
                '</tr>';
            tbody.append(row);
        });
    }

    // 辅助函数：角色名称映射
    function getRoleName(role) {
        if (!role) return '未知';
        switch(role) {
            case 'admin': return '系统管理员';
            case 'finance': return '财务人员';
            case 'owner': return '业主';
            default: return role;
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
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage > 1 ? 'loadUserList(' + (currentPage - 1) + ')' : 'return false') + '">上一页</a>' +
            '</li>'
        );

        // 简单页码逻辑
        for (var i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || (i >= currentPage - 2 && i <= currentPage + 2)) {
                var activeClass = i === currentPage ? 'active' : '';
                pagination.append(
                    '<li class="page-item ' + activeClass + '">' +
                    '<a class="page-link" href="javascript:void(0)" onclick="loadUserList(' + i + ')">' + i + '</a>' +
                    '</li>'
                );
            } else if (i === currentPage - 3 || i === currentPage + 3) {
                pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
            }
        }

        // 下一页
        var nextDisabled = currentPage === totalPages ? 'disabled' : '';
        pagination.append(
            '<li class="page-item ' + nextDisabled + '">' +
            '<a class="page-link" href="javascript:void(0)" onclick="' + (currentPage < totalPages ? 'loadUserList(' + (currentPage + 1) + ')' : 'return false') + '">下一页</a>' +
            '</li>'
        );
    }

    /**
     * 🔥 快速筛选（点击按钮快速筛选角色）
     */
    function quickFilter(role) {
        $('#searchRole').val(role);
        $('#searchKeyword').val('');
        $('#searchStatus').val('');
        loadUserList(1);
    }

    /**
     * 显示添加模态框
     */
    function showAddModal() {
        $('#modalTitle').text('添加用户');
        $('#formMethod').val('add');
        $('#userForm')[0].reset();

        $('#username').prop('readonly', false);
        $('#passwordGroup').show();
        $('#password').prop('required', true);
        $('#statusGroup').hide();

        $('#userModal').modal('show');
    }

    /**
     * 编辑用户
     */
    function editUser(userId) {
        $('#modalTitle').text('编辑用户');
        $('#formMethod').val('update');
        $('#userId').val(userId);

        $('#passwordGroup').hide();
        $('#password').prop('required', false);
        $('#statusGroup').show();

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/user',
            type: 'GET',
            data: { method: 'findById', userId: userId },
            success: function(res) {
                var user = res.data || res;

                if(user) {
                    $('#username').val(user.username).prop('readonly', true);
                    $('#realName').val(user.realName);
                    $('#userRole').val(user.userRole);
                    $('#phone').val(user.phone);
                    $('#idCard').val(user.idCard);
                    $('#status').val(user.status);
                    $('#userModal').modal('show');
                }
            }
        });
    }

    /**
     * 保存用户
     */
    function saveUser() {
        var form = $('#userForm')[0];
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        var method = $('#formMethod').val();
        var data = $('#userForm').serialize();

        if(method === 'update') {
            data += '&userId=' + $('#userId').val();
        }

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/user?method=' + method,
            type: 'POST',
            data: data,
            success: function(res) {
                if (res.code === 0 || res.code === 200 || res.success) {
                    layer.msg('操作成功', {icon: 1});
                    $('#userModal').modal('hide');
                    loadUserList(currentPage);
                } else {
                    layer.msg(res.msg || res.message || '操作失败', {icon: 2});
                }
            }
        });
    }

    /**
     * 查看用户详情
     */
    function viewUser(userId) {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/user',
            type: 'GET',
            data: { method: 'findById', userId: userId },
            success: function(res) {
                var user = res.data || res;
                if(user) {
                    var roleName = getRoleName(user.userRole);
                    var content =
                        '<div style="padding: 20px;">' +
                        '<div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px;">' +
                        '<h4 style="margin: 0;"><i class="fas fa-user-circle"></i> ' + (user.realName || '未命名') + '</h4>' +
                        '<p style="margin: 5px 0 0 0; opacity: 0.9;">@' + user.username + '</p>' +
                        '</div>' +
                        '<div style="background: #f8f9fa; padding: 15px; border-radius: 8px;">' +
                        '<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">' +
                        '<div><small class="text-muted">用户角色</small><div class="font-weight-bold">' + roleName + '</div></div>' +
                        '<div><small class="text-muted">联系电话</small><div class="font-weight-bold">' + (user.phone || '-') + '</div></div>' +
                        '<div><small class="text-muted">身份证号</small><div class="font-weight-bold">' + (user.idCard || '-') + '</div></div>' +
                        '<div><small class="text-muted">创建时间</small><div class="font-weight-bold">' + formatDate(user.createTime) + '</div></div>' +
                        '<div><small class="text-muted">最后登录</small><div class="font-weight-bold">' + formatDate(user.lastLogin) + '</div></div>' +
                        '<div><small class="text-muted">账号状态</small><div class="font-weight-bold">' + (user.status === 1 ? '<span class="text-success">正常</span>' : '<span class="text-danger">禁用</span>') + '</div></div>' +
                        '</div>' +
                        '</div>' +
                        '</div>';

                    layer.open({
                        type: 1,
                        title: false,
                        area: ['500px', 'auto'],
                        shadeClose: true,
                        content: content
                    });
                }
            }
        });
    }

    /**
     * 打开重置密码模态框
     */
    function openPwdModal(userId) {
        $('#pwdUserId').val(userId);
        $('#newPassword').val('123456');
        $('#pwdModal').modal('show');
    }

    /**
     * 确认重置密码
     */
    function confirmResetPwd() {
        var userId = $('#pwdUserId').val();
        var newPwd = $('#newPassword').val();

        if(!newPwd) {
            layer.msg('密码不能为空', {icon: 0});
            return;
        }

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/user',
            type: 'POST',
            data: { method: 'resetPassword', userId: userId, newPassword: newPwd },
            success: function(res) {
                if (res.code === 0 || res.code === 200 || res.success) {
                    layer.msg('密码重置成功', {icon: 1});
                    $('#pwdModal').modal('hide');
                } else {
                    layer.msg(res.msg || res.message || '重置失败', {icon: 2});
                }
            }
        });
    }

    /**
     * 更新状态
     */
    function updateStatus(userId, status) {
        var action = status === 1 ? '启用' : '禁用';
        layer.confirm('确定要' + action + '该用户吗？', {icon: 3, title:'提示'}, function(index){
            $.ajax({
                url: '${pageContext.request.contextPath}/admin/user',
                type: 'POST',
                data: { method: 'updateStatus', userId: userId, status: status },
                success: function(res) {
                    if (res.code === 0 || res.code === 200 || res.success) {
                        layer.msg('已' + action, {icon: 1});
                        loadUserList(currentPage);
                    } else {
                        layer.msg('操作失败', {icon: 2});
                    }
                }
            });
            layer.close(index);
        });
    }

    /**
     * 删除用户
     */
    function deleteUser(userId) {
        layer.confirm('确定要删除该用户吗？此操作不可恢复！', {icon: 3, title:'删除确认'}, function(index){
            $.ajax({
                url: '${pageContext.request.contextPath}/admin/user',
                type: 'POST',
                data: { method: 'delete', userId: userId },
                success: function(res) {
                    if (res.code === 0 || res.code === 200 || res.success) {
                        layer.msg('删除成功', {icon: 1});
                        loadUserList(currentPage);
                    } else {
                        layer.msg(res.msg || res.message || '删除失败', {icon: 2});
                    }
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
            layer.msg('请先选择要删除的用户', {icon: 0});
            return;
        }

        layer.confirm('确定要删除选中的 ' + checkedBoxes.length + ' 个用户吗？', {icon: 3}, function(index){
            var ids = [];
            checkedBoxes.each(function() {
                ids.push($(this).val());
            });

            var completed = 0;
            $.each(ids, function(i, id) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/admin/user',
                    type: 'POST',
                    data: { method: 'delete', userId: id },
                    complete: function() {
                        completed++;
                        if(completed === ids.length) {
                            layer.msg('批量操作完成', {icon: 1});
                            $('#checkAll').prop('checked', false);
                            loadUserList(currentPage);
                        }
                    }
                });
            });
            layer.close(index);
        });
    }

    // 全选/反选
    function toggleCheckAll() {
        $('.row-checkbox').prop('checked', $('#checkAll').prop('checked'));
    }

    // 搜索
    function searchUser() {
        loadUserList(1);
    }

    // 🔥 重置搜索（清空所有筛选条件）
    function resetSearch() {
        $('#searchKeyword').val('');
        $('#searchRole').val('');
        $('#searchStatus').val('');
        loadUserList(1);
    }

    // 日期格式化
    function formatDate(dateStr) {
        if (!dateStr) return '-';
        var date = new Date(dateStr);
        if (isNaN(date.getTime())) return '-';

        var year = date.getFullYear();
        var month = String(date.getMonth() + 1).padStart(2, '0');
        var day = String(date.getDate()).padStart(2, '0');
        var hour = String(date.getHours()).padStart(2, '0');
        var minute = String(date.getMinutes()).padStart(2, '0');

        return year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
    }
</script>

</body>
</html>
