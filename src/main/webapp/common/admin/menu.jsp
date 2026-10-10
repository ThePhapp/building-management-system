<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<c:set var="requestPath" value="${pageContext.request.requestURI}"/>

<aside id="sidebar" class="app-sidebar" aria-label="Điều hướng chính" aria-hidden="false">
    <button type="button" class="sr-only" id="appSidebarClose">Đóng menu điều hướng</button>

    <nav class="app-sidebar-nav">
        <div class="app-nav-group">
            <span class="app-nav-label">Tổng quan</span>
            <a href="<c:url value='/admin/home'/>"
               class="app-nav-link ${fn:contains(requestPath, '/admin/home') ? 'is-active' : ''}"
               aria-current="${fn:contains(requestPath, '/admin/home') ? 'page' : 'false'}">
                <i class="fa fa-th-large" aria-hidden="true"></i>
                <span>Tổng quan vận hành</span>
            </a>
        </div>

        <div class="app-nav-group">
            <span class="app-nav-label">Danh mục quản lý</span>
            <a href="<c:url value='/admin/building-list'/>"
               class="app-nav-link ${fn:contains(requestPath, '/admin/building') ? 'is-active' : ''}"
               aria-current="${fn:contains(requestPath, '/admin/building') ? 'page' : 'false'}">
                <i class="fa fa-building-o" aria-hidden="true"></i>
                <span>Tòa nhà</span>
            </a>

            <security:authorize access="hasRole('ADMIN')">
                <a href="<c:url value='/admin/user-list'/>"
                   class="app-nav-link ${fn:contains(requestPath, '/admin/user') ? 'is-active' : ''}"
                   aria-current="${fn:contains(requestPath, '/admin/user') ? 'page' : 'false'}">
                    <i class="fa fa-users" aria-hidden="true"></i>
                    <span>Người dùng &amp; nhân viên</span>
                </a>
            </security:authorize>
        </div>
    </nav>

    <div class="app-sidebar-footer">
        <a href="<c:url value='/trang-chu'/>" class="app-nav-link">
            <i class="fa fa-external-link" aria-hidden="true"></i>
            <span>Xem trang công khai</span>
        </a>
        <p>Phiên bản nội bộ · Java 8</p>
    </div>
</aside>
