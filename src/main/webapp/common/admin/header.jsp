<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<header class="app-topbar" id="navbar">
    <div class="app-brand-area">
        <button type="button" class="app-icon-button app-menu-button" id="appSidebarToggle"
                aria-controls="sidebar" aria-expanded="false" aria-label="Mở menu điều hướng">
            <i class="fa fa-bars" aria-hidden="true"></i>
        </button>
        <a href="<c:url value='/admin/home'/>" class="app-brand" aria-label="Building Manager - Tổng quan">
            <span class="app-brand-mark" aria-hidden="true"><i class="fa fa-building"></i></span>
            <span class="app-brand-copy">
                <strong>Building Manager</strong>
                <small>Quản lý vận hành</small>
            </span>
        </a>
    </div>

    <div class="app-topbar-main">
        <div class="app-page-context">
            <span class="app-page-eyebrow">Không gian quản lý</span>
            <span class="app-page-title" id="appHeaderTitle">Tổng quan</span>
        </div>

        <div class="app-account dropdown">
            <button type="button" class="app-account-trigger dropdown-toggle" data-toggle="dropdown"
                    aria-haspopup="true" aria-expanded="false">
                <span class="app-avatar" aria-hidden="true"><i class="fa fa-user"></i></span>
                <span class="app-account-copy">
                    <small>Tài khoản</small>
                    <strong><security:authentication property="principal.fullName" htmlEscape="true"/></strong>
                </span>
                <i class="fa fa-angle-down app-account-chevron" aria-hidden="true"></i>
            </button>

            <ul class="dropdown-menu dropdown-menu-right app-account-menu" role="menu">
                <li class="app-menu-heading">
                    <span>Đang đăng nhập với</span>
                    <strong><security:authentication property="principal.username" htmlEscape="true"/></strong>
                </li>
                <li class="divider"></li>
                <li>
                    <a href="<c:url value='/admin/profile'/>"><i class="fa fa-user" aria-hidden="true"></i>Hồ sơ cá nhân</a>
                </li>
                <li>
                    <a href="<c:url value='/admin/profile-password'/>"><i class="fa fa-key" aria-hidden="true"></i>Đổi mật khẩu</a>
                </li>
                <li class="divider"></li>
                <li class="app-logout-item">
                    <form action="<c:url value='/logout'/>" method="post">
                        <security:csrfInput/>
                        <button type="submit"><i class="fa fa-sign-out" aria-hidden="true"></i>Đăng xuất</button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
</header>
