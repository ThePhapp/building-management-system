<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/common/taglib.jsp" %>
<c:url var="profileApiUrl" value="/api/user/profile"/>
<html lang="vi">
<head>
    <title>Hồ sơ cá nhân</title>
</head>
<body>
<div class="main-content">
    <div class="main-content-inner">
        <div class="breadcrumbs" id="breadcrumbs">
            <ul class="breadcrumb">
                <li><a href="<c:url value='/admin/home'/>">Trang chủ</a></li>
                <li class="active">Hồ sơ cá nhân</li>
            </ul>
        </div>

        <div class="page-content">
            <header class="app-page-header">
                <div class="app-page-heading">
                    <h1>Hồ sơ cá nhân</h1>
                    <p>Cập nhật tên hiển thị và địa chỉ email của tài khoản đang đăng nhập.</p>
                </div>
            </header>

            <div class="app-account-layout">
                <c:if test="${not empty messageResponse}">
                    <div class="app-form-status ${alert == 'success' ? 'is-success' : 'is-danger'}" role="status">
                        <c:out value="${messageResponse}"/>
                    </div>
                </c:if>
                <div class="app-form-status" id="profileStatus" role="alert" hidden></div>

                <form:form id="profileForm" modelAttribute="model" novalidate="novalidate">
                    <section class="app-panel" aria-labelledby="profilePanelTitle">
                        <div class="app-panel-header">
                            <h2 id="profilePanelTitle">Thông tin tài khoản</h2>
                            <p>Tên đăng nhập do quản trị viên quản lý và không thể thay đổi tại đây.</p>
                        </div>
                        <div class="app-panel-body">
                            <div class="app-form-grid">
                                <div class="app-field is-full">
                                    <label for="userName">Tên đăng nhập</label>
                                    <form:input path="userName" id="userName" cssClass="form-control"
                                                readonly="true" autocomplete="username" aria-describedby="userNameHint"/>
                                    <span class="app-field-hint" id="userNameHint">Dùng tên này khi đăng nhập hệ thống.</span>
                                </div>

                                <div class="app-field is-full">
                                    <label for="fullName">Tên đầy đủ <span class="app-required" aria-hidden="true">*</span></label>
                                    <form:input path="fullName" id="fullName" cssClass="form-control"
                                                required="required" maxlength="255" autocomplete="name"
                                                aria-describedby="fullNameError"/>
                                    <span class="app-field-error" id="fullNameError" data-error-for="fullName"></span>
                                </div>

                                <div class="app-field is-full">
                                    <label for="email">Email <span class="app-field-optional">Không bắt buộc</span></label>
                                    <form:input path="email" id="email" type="email" cssClass="form-control"
                                                maxlength="255" autocomplete="email" aria-describedby="emailError"/>
                                    <span class="app-field-error" id="emailError" data-error-for="email"></span>
                                </div>
                            </div>
                        </div>
                        <div class="app-panel-footer">
                            <button type="submit" class="btn btn-primary" id="saveProfileButton">
                                <span class="app-button-spinner" aria-hidden="true"></span>
                                <span data-button-label>Lưu thay đổi</span>
                            </button>
                        </div>
                    </section>
                    <form:hidden path="id" id="userId"/>
                </form:form>
            </div>
        </div>
    </div>
</div>

<script>
    (function ($) {
        var $form = $('#profileForm');
        var $status = $('#profileStatus');
        var $button = $('#saveProfileButton');

        $form.on('submit', function (event) {
            event.preventDefault();
            BmsForms.clearErrors($form);
            BmsForms.hideStatus($status);

            var fullName = $.trim($('#fullName').val());
            var email = $.trim($('#email').val());
            var hasError = false;

            if (!fullName) {
                hasError = BmsForms.setFieldError($form, 'fullName', 'Chưa nhập tên đầy đủ.') || hasError;
            }
            if (email && !document.getElementById('email').checkValidity()) {
                hasError = BmsForms.setFieldError($form, 'email', 'Email phải có định dạng hợp lệ.') || hasError;
            }
            if (hasError) {
                $form.find('[aria-invalid="true"]').first().trigger('focus');
                return;
            }

            BmsForms.setBusy($button, true, 'Đang lưu…');
            $.ajax({
                url: '${profileApiUrl}',
                type: 'PUT',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({fullName: fullName, email: email}),
                success: function () {
                    BmsForms.showStatus($status, 'Hồ sơ đã được cập nhật.', 'success');
                },
                error: function (xhr) {
                    BmsForms.applyApiErrors($form, $status, xhr, 'Không thể cập nhật hồ sơ.');
                },
                complete: function () {
                    BmsForms.setBusy($button, false, 'Đang lưu…');
                }
            });
        });
    })(jQuery);
</script>
</body>
</html>
