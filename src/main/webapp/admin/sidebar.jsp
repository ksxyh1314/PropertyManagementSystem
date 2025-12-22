<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav id="sidebar">
    <div class="sidebar-header">
        <h3>物业管理系统</h3>
        <p>管理员：${sessionScope.currentUser.realName}</p>
    </div>
    <ul class="list-unstyled components">
        <li id="menu-index">
            <a href="${pageContext.request.contextPath}/admin/index.jsp">
                <i class="glyphicon glyphicon-home"></i> 首页
            </a>
        </li>
        <li id="menu-user">
            <a href="${pageContext.request.contextPath}/admin/user.jsp">
                <i class="glyphicon glyphicon-user"></i> 用户管理
            </a>
        </li>
        <li id="menu-owner">
            <a href="${pageContext.request.contextPath}/admin/owner.jsp">
                <i class="glyphicon glyphicon-list-alt"></i> 业主管理
            </a>
        </li>
        <li id="menu-house">
            <a href="${pageContext.request.contextPath}/admin/house.jsp">
                <i class="glyphicon glyphicon-home"></i> 房屋管理
            </a>
        </li>
        <li id="menu-chargeItem">
            <a href="${pageContext.request.contextPath}/admin/chargeItem.jsp">
                <i class="glyphicon glyphicon-list"></i> 收费项目
            </a>
        </li>
        <li id="menu-payment">
            <a href="${pageContext.request.contextPath}/admin/payment.jsp">
                <i class="glyphicon glyphicon-credit-card"></i> 缴费管理
            </a>
        </li>
        <li id="menu-repair">
            <a href="${pageContext.request.contextPath}/admin/repair.jsp">
                <i class="glyphicon glyphicon-wrench"></i> 报修管理
            </a>
        </li>
        <li id="menu-statistics">
            <a href="${pageContext.request.contextPath}/admin/statistics.jsp">
                <i class="glyphicon glyphicon-stats"></i> 统计报表
            </a>
        </li>
    </ul>
    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/login?method=logout" class="btn btn-danger btn-block">
            <i class="glyphicon glyphicon-log-out"></i> 退出登录
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
