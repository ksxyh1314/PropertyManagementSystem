<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav id="sidebar">
    <div class="sidebar-header">
        <div class="logo">
            <i class="glyphicon glyphicon-home"></i>
        </div>
        <h3>物业管理系统</h3>
        <div class="user-info">
            <div class="avatar">
                <i class="glyphicon glyphicon-user"></i>
            </div>
            <p class="user-name">${sessionScope.currentUser.realName}</p>
            <p class="user-role">系统管理员</p>
        </div>
    </div>

    <ul class="list-unstyled components">
        <li id="menu-index">
            <a href="${pageContext.request.contextPath}/admin/index.jsp">
                <i class="glyphicon glyphicon-dashboard"></i>
                <span>系统首页</span>
            </a>
        </li>
        <li id="menu-user">
            <a href="${pageContext.request.contextPath}/admin/user.jsp">
                <i class="glyphicon glyphicon-user"></i>
                <span>用户管理</span>
            </a>
        </li>
        <li id="menu-owner">
            <a href="${pageContext.request.contextPath}/admin/owner.jsp">
                <i class="glyphicon glyphicon-list-alt"></i>
                <span>业主管理</span>
            </a>
        </li>
        <li id="menu-house">
            <a href="${pageContext.request.contextPath}/admin/house.jsp">
                <i class="glyphicon glyphicon-home"></i>
                <span>房屋管理</span>
            </a>
        </li>
        <li id="menu-chargeItem">
            <a href="${pageContext.request.contextPath}/admin/chargeItem.jsp">
                <i class="glyphicon glyphicon-list"></i>
                <span>收费项目</span>
            </a>
        </li>
        <li id="menu-payment">
            <a href="${pageContext.request.contextPath}/admin/payment.jsp">
                <i class="glyphicon glyphicon-credit-card"></i>
                <span>缴费管理</span>
            </a>
        </li>
        <li id="menu-repair">
            <a href="${pageContext.request.contextPath}/admin/repair.jsp">
                <i class="glyphicon glyphicon-wrench"></i>
                <span>报修管理</span>
            </a>
        </li>
        <li id="menu-statistics">
            <a href="${pageContext.request.contextPath}/admin/statistics.jsp">
                <i class="glyphicon glyphicon-stats"></i>
                <span>统计报表</span>
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/login?method=logout" class="btn btn-logout">
            <i class="glyphicon glyphicon-log-out"></i>
            <span>退出登录</span>
        </a>
    </div>
</nav>

<script>
    // 根据当前页面高亮菜单
    $(function() {
        var currentPage = window.location.pathname;
        var fileName = currentPage.substring(currentPage.lastIndexOf('/') + 1).replace('.jsp', '');

        if (fileName === 'index' || fileName === '') {
            $('#menu-index').addClass('active');
        } else {
            $('#menu-' + fileName).addClass('active');
        }
    });
</script>
