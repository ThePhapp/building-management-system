<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <title>Đăng nhập</title>
</head>
<body>
<main class="auth-page">
    <section class="auth-card" aria-labelledby="loginTitle">
        <a class="auth-brand" href="<c:url value='/trang-chu'/>" aria-label="Building Manager - Trang công khai">
            <span class="auth-brand-mark" aria-hidden="true">
                <span></span><span></span><span></span>
            </span>
            <span class="auth-brand-copy">
                <strong>Building Manager</strong>
                <small>Quản lý vận hành</small>
            </span>
        </a>

        <header class="auth-heading">
            <h1 id="loginTitle">Đăng nhập hệ thống</h1>
            <p>Sử dụng tài khoản được quản trị viên cấp.</p>
        </header>

        <c:if test="${param.incorrectAccount != null}">
            <div class="auth-alert" role="alert" id="loginError">
                <strong>Không thể đăng nhập</strong>
                <span>Tên đăng nhập hoặc mật khẩu chưa đúng.</span>
            </div>
        </c:if>
        <c:if test="${param.accessDenied != null}">
            <div class="auth-alert" role="alert">
                <strong>Không có quyền truy cập</strong>
                <span>Tài khoản này không được phép mở trang vừa yêu cầu.</span>
            </div>
        </c:if>
        <c:if test="${param.sessionTimeout != null}">
            <div class="auth-alert" role="alert">
                <strong>Phiên làm việc đã hết hạn</strong>
                <span>Đăng nhập lại để tiếp tục.</span>
            </div>
        </c:if>

        <form action="<c:url value='/j_spring_security_check'/>" method="post" id="loginForm" class="auth-form">
            <security:csrfInput/>

            <div class="auth-field">
                <label for="userName">Tên đăng nhập</label>
                <input type="text" id="userName" name="j_username" placeholder="Nhập tên đăng nhập"
                       autocomplete="username" required autofocus>
            </div>

            <div class="auth-field">
                <label for="password">Mật khẩu</label>
                <div class="auth-password-wrap">
                    <input type="password" id="password" name="j_password" placeholder="Nhập mật khẩu"
                           autocomplete="current-password" required>
                    <button type="button" class="auth-password-toggle" data-password-toggle="password"
                            aria-controls="password" aria-label="Hiện mật khẩu">Hiện</button>
                </div>
            </div>

            <button type="submit" class="auth-submit" id="loginSubmit">
                <span class="auth-submit-label">Đăng nhập</span>
                <span class="auth-spinner" aria-hidden="true"></span>
            </button>
        </form>

        <p class="auth-note">Tài khoản được quản lý tập trung. Liên hệ quản trị viên nếu bạn chưa được cấp quyền.</p>
        <a class="auth-back-link" href="<c:url value='/trang-chu'/>">
            <span aria-hidden="true">←</span> Quay lại trang công khai
        </a>
    </section>
</main>

<script>
    (function () {
        var form = document.getElementById('loginForm');
        var submit = document.getElementById('loginSubmit');
        var toggle = document.querySelector('[data-password-toggle]');
        var password = document.getElementById('password');

        toggle.addEventListener('click', function () {
            var shouldShow = password.type === 'password';
            password.type = shouldShow ? 'text' : 'password';
            toggle.textContent = shouldShow ? 'Ẩn' : 'Hiện';
            toggle.setAttribute('aria-label', shouldShow ? 'Ẩn mật khẩu' : 'Hiện mật khẩu');
            password.focus();
        });

        form.addEventListener('submit', function () {
            if (!form.checkValidity()) {
                return;
            }
            submit.disabled = true;
            submit.setAttribute('aria-busy', 'true');
            submit.querySelector('.auth-submit-label').textContent = 'Đang đăng nhập…';
        });
    })();
</script>
</body>
</html>
