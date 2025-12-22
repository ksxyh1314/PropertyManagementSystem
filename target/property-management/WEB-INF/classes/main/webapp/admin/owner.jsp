<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>业主管理 - 社区物业管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/admin.css">
</head>
<body>
<div class="wrapper">
    <!-- 侧边栏（同index.jsp） -->
    <jsp:include page="sidebar.jsp"/>

    <!-- 主内容区 -->
    <div id="content">
        <div class="container-fluid">
            <h2 class="page-title">业主管理</h2>

            <!-- 搜索和操作栏 -->
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="input-group">
                                <input type="text" class="form-control" id="searchKeyword"
                                       placeholder="输入业主ID、姓名、电话或房屋编号搜索">
                                <span class="input-group-btn">
                                        <button class="btn btn-primary" onclick="searchOwner()">
                                            <i class="glyphicon glyphicon-search"></i> 搜索
                                        </button>
                                    </span>
                            </div>
                        </div>
                        <div class="col-md-6 text-right">
                            <button class="btn btn-success" onclick="showAddModal()">
                                <i class="glyphicon glyphicon-plus"></i> 添加业主
                            </button>
                            <button class="btn btn-warning" onclick="loadOwnerList()">
                                <i class="glyphicon glyphicon-refresh"></i> 刷新
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 业主列表 -->
            <div class="panel panel-default">
                <div class="panel-body">
                    <table class="table table-striped table-hover" id="ownerTable">
                        <thead>
                        <tr>
                            <th>业主ID</th>
                            <th>姓名</th>
                            <th>电话</th>
                            <th>身份证号</th>
                            <th>房屋编号</th>
                            <th>家庭人数</th>
                            <th>登记日期</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>

                    <!-- 分页 -->
                    <div class="text-center">
                        <ul class="pagination" id="pagination"></ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 添加/编辑业主模态框 -->
<div class="modal fade" id="ownerModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title" id="modalTitle">添加业主</h4>
            </div>
            <div class="modal-body">
                <form id="ownerForm">
                    <input type="hidden" id="ownerId" name="ownerId">
                    <div class="form-group">
                        <label>业主姓名 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="ownerName" name="ownerName" required>
                    </div>
                    <div class="form-group">
                        <label>联系电话 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="phone" name="phone"
                               pattern="^1[3-9]\d{9}$" required>
                    </div>
                    <div class="form-group">
                        <label>身份证号 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="idCard" name="idCard"
                               pattern="^\d{17}[\dXx]$" required>
                    </div>
                    <div class="form-group">
                        <label>房屋编号 <span class="text-danger">*</span></label>
                        <select class="form-control" id="houseId" name="houseId" required>
                            <option value="">请选择房屋</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>电子邮箱</label>
                        <input type="email" class="form-control" id="email" name="email">
                    </div>
                    <div class="form-group">
                        <label>家庭人数</label>
                        <input type="number" class="form-control" id="memberCount" name="memberCount"
                               min="1" value="1">
                    </div>
                    <div class="form-group">
                        <label>登记日期</label>
                        <input type="date" class="form-control" id="registerDate" name="registerDate">
                    </div>
                    <div class="form-group" id="passwordGroup">
                        <label>登录密码 <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" id="password" name="password">
                        <small class="text-muted">密码必须8位以上，且包含字母和数字</small>
                    </div>
                    <div class="form-group">
                        <label>备注</label>
                        <textarea class="form-control" id="remark" name="remark" rows="3"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="saveOwner()">保存</button>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
<script>
    var currentPage = 1;
    var pageSize = 10;
    var isEdit = false;

    $(function() {
        loadOwnerList();
        loadVacantHouses();

        // 设置默认日期为今天
        $('#registerDate').val(new Date().toISOString().split('T')[0]);
    });

    // 加载业主列表
    function loadOwnerList(page) {
        if (page) currentPage = page;

        var keyword = $('#searchKeyword').val();

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner?method=list',
            type: 'GET',
            data: {
                pageNum: currentPage,
                pageSize: pageSize,
                keyword: keyword
            },
            dataType: 'json',
            success: function(result) {
                var tbody = $('#ownerTable tbody');
                tbody.empty();

                if (result.list.length === 0) {
                    tbody.append('<tr><td colspan="8" class="text-center">暂无数据</td></tr>');
                    return;
                }

                result.list.forEach(function(owner) {
                    var tr = '<tr>' +
                        '<td>' + owner.ownerId + '</td>' +
                        '<td>' + owner.ownerName + '</td>' +
                        '<td>' + owner.phone + '</td>' +
                        '<td>' + owner.idCard + '</td>' +
                        '<td>' + owner.houseId + '</td>' +
                        '<td>' + owner.memberCount + '</td>' +
                        '<td>' + formatDate(owner.registerDate) + '</td>' +
                        '<td>' +
                        '<button class="btn btn-sm btn-info" onclick="viewOwner(\'' + owner.ownerId + '\')">查看</button> ' +
                        '<button class="btn btn-sm btn-warning" onclick="editOwner(\'' + owner.ownerId + '\')">编辑</button> ' +
                        '<button class="btn btn-sm btn-danger" onclick="deleteOwner(\'' + owner.ownerId + '\')">删除</button>' +
                        '</td>' +
                        '</tr>';
                    tbody.append(tr);
                });

                // 渲染分页
                renderPagination(result.pageNum, result.totalPages);
            }
        });
    }

    // 加载空置房屋
    function loadVacantHouses() {
        $.ajax({
            url: '${pageContext.request.contextPath}/admin/house?method=findVacant',
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var select = $('#houseId');
                    select.find('option:not(:first)').remove();

                    result.data.forEach(function(house) {
                        select.append('<option value="' + house.houseId + '">' +
                            house.houseId + ' (' + house.layout + ', ' + house.area + '㎡)</option>');
                    });
                }
            }
        });
    }

    // 搜索业主
    function searchOwner() {
        currentPage = 1;
        loadOwnerList();
    }

    // 显示添加模态框
    function showAddModal() {
        isEdit = false;
        $('#modalTitle').text('添加业主');
        $('#ownerForm')[0].reset();
        $('#ownerId').val('');
        $('#passwordGroup').show();
        $('#password').prop('required', true);
        $('#registerDate').val(new Date().toISOString().split('T')[0]);
        loadVacantHouses();
        $('#ownerModal').modal('show');
    }

    // 编辑业主
    function editOwner(ownerId) {
        isEdit = true;
        $('#modalTitle').text('编辑业主');
        $('#passwordGroup').hide();
        $('#password').prop('required', false);

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner?method=findById',
            type: 'GET',
            data: {ownerId: ownerId},
            dataType: 'json',
            success: function(result) {
                if (result.success) {
                    var owner = result.data;
                    $('#ownerId').val(owner.ownerId);
                    $('#ownerName').val(owner.ownerName);
                    $('#phone').val(owner.phone);
                    $('#idCard').val(owner.idCard);
                    $('#houseId').val(owner.houseId);
                    $('#email').val(owner.email);
                    $('#memberCount').val(owner.memberCount);
                    $('#registerDate').val(formatDate(owner.registerDate));
                    $('#remark').val(owner.remark);

                    $('#ownerModal').modal('show');
                }
            }
        });
    }

    // 保存业主
    function saveOwner() {
        var form = $('#ownerForm')[0];
        if (!form.checkValidity()) {
            alert('请填写完整信息');
            return;
        }

        var method = isEdit ? 'update' : 'add';
        var data = {
            ownerId: $('#ownerId').val(),
            ownerName: $('#ownerName').val(),
            phone: $('#phone').val(),
            idCard: $('#idCard').val(),
            houseId: $('#houseId').val(),
            email: $('#email').val(),
            memberCount: $('#memberCount').val(),
            registerDate: $('#registerDate').val(),
            remark: $('#remark').val()
        };

        if (!isEdit) {
            data.password = $('#password').val();
        }

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner?method=' + method,
            type: 'POST',
            data: data,
            dataType: 'json',
            success: function(result) {
                alert(result.message);
                if (result.success) {
                    $('#ownerModal').modal('hide');
                    loadOwnerList();
                }
            }
        });
    }

    // 删除业主
    function deleteOwner(ownerId) {
        if (!confirm('确定要删除该业主吗？删除后将无法恢复！')) {
            return;
        }

        $.ajax({
            url: '${pageContext.request.contextPath}/admin/owner?method=delete',
            type: 'POST',
            data: {ownerId: ownerId},
            dataType: 'json',
            success: function(result) {
                alert(result.message);
                if (result.success) {
                    loadOwnerList();
                }
            }
        });
    }

    // 渲染分页
    function renderPagination(current, total) {
        var pagination = $('#pagination');
        pagination.empty();

        if (total <= 1) return;

        // 上一页
        if (current > 1) {
            pagination.append('<li><a href="javascript:loadOwnerList(' + (current - 1) + ')">«</a></li>');
        }

        // 页码
        for (var i = 1; i <= total; i++) {
            var activeClass = i === current ? 'active' : '';
            pagination.append('<li class="' + activeClass + '"><a href="javascript:loadOwnerList(' + i + ')">' + i + '</a></li>');
        }

        // 下一页
        if (current < total) {
            pagination.append('<li><a href="javascript:loadOwnerList(' + (current + 1) + ')">»</a></li>');
        }
    }

    // 格式化日期
    function formatDate(date) {
        if (!date) return '';
        var d = new Date(date);
        return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate());
    }

    function pad(num) {
        return num < 10 ? '0' + num : num;
    }
</script>
</body>
</html>
