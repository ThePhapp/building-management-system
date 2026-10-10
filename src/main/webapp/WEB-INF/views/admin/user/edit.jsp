<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/common/taglib.jsp" %>
<c:url var="userApiUrl" value="/api/user"/>
<c:url var="userListUrl" value="/admin/user-list"/>
<c:url var="userEditBaseUrl" value="/admin/user-edit-"/>
<c:set var="isEditing" value="${not empty model.id}"/>
<html lang="vi">
<head>
    <title>${isEditing ? 'Chỉnh sửa người dùng' : 'Thêm người dùng'}</title>
</head>
<body>
<div class="main-content">
    <div class="main-content-inner">
        <div class="breadcrumbs" id="breadcrumbs">
            <ul class="breadcrumb">
                <li><a href="<c:url value='/admin/home'/>">Trang chủ</a></li>
                <li><a href="${userListUrl}">Người dùng</a></li>
                <li class="active">${isEditing ? 'Chỉnh sửa' : 'Thêm mới'}</li>
            </ul>
        </div>

        <div class="page-content">
            <header class="app-page-header">
                <div class="app-page-heading">
                    <h1>${isEditing ? 'Chỉnh sửa người dùng' : 'Thêm người dùng'}</h1>
                    <p>${isEditing ? 'Cập nhật thông tin và vai trò của tài khoản.' : 'Tạo tài khoản mới và cấp vai trò phù hợp.'}</p>
                </div>
            </header>

            <div class="app-account-layout">
                <c:if test="${not empty messageResponse}">
                    <div class="app-form-status ${alert == 'success' ? 'is-success' : 'is-danger'}" role="status">
                        <c:out value="${messageResponse}"/>
                    </div>
                </c:if>
                <div class="app-form-status" id="userFormStatus" role="alert" hidden></div>

                <form:form id="userForm" modelAttribute="model" novalidate="novalidate">
                    <section class="app-panel" aria-labelledby="userPanelTitle">
                        <div class="app-panel-header">
                            <h2 id="userPanelTitle">Thông tin tài khoản</h2>
                            <p>Các trường có dấu <span class="app-required">*</span> là bắt buộc.</p>
                        </div>
                        <div class="app-panel-body">
                            <div class="app-form-grid">
                                <div class="app-field">
                                    <label for="roleCode">Vai trò <span class="app-required" aria-hidden="true">*</span></label>
                                    <form:select path="roleCode" id="roleCode" cssClass="form-control"
                                                 required="required" aria-describedby="roleCodeError">
                                        <form:option value="" label="Chọn vai trò"/>
                                        <form:options items="${model.roleDTOs}"/>
                                    </form:select>
                                    <span class="app-field-error" id="roleCodeError" data-error-for="roleCode"></span>
                                </div>

                                <div class="app-field">
                                    <label for="userName">Tên đăng nhập <span class="app-required" aria-hidden="true">*</span></label>
                                    <c:choose>
                                        <c:when test="${isEditing}">
                                            <form:input path="userName" id="userName" cssClass="form-control"
                                                        readonly="true" autocomplete="username" aria-describedby="userNameHelp"/>
                                        </c:when>
                                        <c:otherwise>
                                            <form:input path="userName" id="userName" cssClass="form-control"
                                                        required="required" minlength="3" maxlength="50"
                                                        pattern="[A-Za-z0-9._-]{3,50}" autocomplete="username"
                                                        aria-describedby="userNameHelp userNameError"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="app-field-hint" id="userNameHelp">
                                        ${isEditing ? 'Tên đăng nhập không thể thay đổi.' : '3–50 ký tự; dùng chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.'}
                                    </span>
                                    <span class="app-field-error" id="userNameError" data-error-for="userName"></span>
                                </div>

                                <div class="app-field">
                                    <label for="fullName">Tên đầy đủ <span class="app-required" aria-hidden="true">*</span></label>
                                    <form:input path="fullName" id="fullName" cssClass="form-control"
                                                required="required" maxlength="255" autocomplete="name"
                                                aria-describedby="fullNameError"/>
                                    <span class="app-field-error" id="fullNameError" data-error-for="fullName"></span>
                                </div>

                                <div class="app-field">
                                    <label for="email">Email <span class="app-field-optional">Không bắt buộc</span></label>
                                    <form:input path="email" id="email" type="email" cssClass="form-control"
                                                maxlength="255" autocomplete="email" aria-describedby="emailError"/>
                                    <span class="app-field-error" id="emailError" data-error-for="email"></span>
                                </div>

                                <c:if test="${not isEditing}">
                                    <div class="app-field is-full">
                                        <label for="password">Mật khẩu ban đầu <span class="app-required" aria-hidden="true">*</span></label>
                                        <div class="app-password-wrap">
                                            <form:password path="password" id="password" cssClass="form-control"
                                                           required="required" minlength="8" autocomplete="new-password"
                                                           aria-describedby="passwordHelp passwordError"/>
                                            <button type="button" class="app-password-toggle" data-password-toggle="password"
                                                    aria-controls="password" aria-label="Hiện mật khẩu">Hiện</button>
                                        </div>
                                        <span class="app-field-hint" id="passwordHelp">Ít nhất 8 ký tự.</span>
                                        <span class="app-field-error" id="passwordError" data-error-for="password"></span>
                                        <form:hidden path="confirmPassword" id="confirmPassword"/>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <div class="app-panel-footer">
                            <a href="${userListUrl}" class="btn btn-default">Hủy</a>
                            <button type="submit" class="btn btn-primary" id="saveUserButton">
                                <span class="app-button-spinner" aria-hidden="true"></span>
                                <span data-button-label>${isEditing ? 'Lưu thay đổi' : 'Tạo người dùng'}</span>
                            </button>
                        </div>
                    </section>
                    <form:hidden path="id" id="userId"/>
                </form:form>

                <c:if test="${isEditing}">
                    <form id="resetPasswordForm" novalidate>
                        <input type="text" name="username" value="${fn:escapeXml(model.userName)}"
                               class="sr-only" tabindex="-1" autocomplete="username" readonly aria-hidden="true">
                        <section class="app-panel" aria-labelledby="resetPasswordTitle">
                            <div class="app-panel-header">
                                <h2 id="resetPasswordTitle">Đặt mật khẩu mới</h2>
                                <p>Thay mật khẩu cho người dùng này mà không cần biết mật khẩu hiện tại.</p>
                            </div>
                            <div class="app-panel-body">
                                <div class="app-field is-full">
                                    <label for="resetPassword">Mật khẩu mới <span class="app-required" aria-hidden="true">*</span></label>
                                    <div class="app-password-wrap">
                                        <input type="password" class="form-control" id="resetPassword" name="newPassword"
                                               required minlength="8" autocomplete="new-password"
                                               aria-describedby="resetPasswordHelp resetPasswordError">
                                        <button type="button" class="app-password-toggle" data-password-toggle="resetPassword"
                                                aria-controls="resetPassword" aria-label="Hiện mật khẩu mới">Hiện</button>
                                    </div>
                                    <span class="app-field-hint" id="resetPasswordHelp">Ít nhất 8 ký tự. Người dùng sẽ dùng mật khẩu này ở lần đăng nhập tiếp theo.</span>
                                    <span class="app-field-error" id="resetPasswordError" data-error-for="newPassword"></span>
                                </div>
                            </div>
                            <div class="app-panel-footer">
                                <button type="submit" class="btn btn-default" id="resetPasswordButton">
                                    <span class="app-button-spinner" aria-hidden="true"></span>
                                    <span data-button-label>Cập nhật mật khẩu</span>
                                </button>
                            </div>
                        </section>
                    </form>
                </c:if>
            </div>
        </div>
    </div>
</div>

<script>
    (function ($) {
        var isEditing = ${isEditing};
        var $form = $('#userForm');
        var $status = $('#userFormStatus');
        var $saveButton = $('#saveUserButton');

        function validateUserForm() {
            BmsForms.clearErrors($form);
            BmsForms.hideStatus($status);
            var hasError = false;
            var roleCode = $('#roleCode').val();
            var userName = $.trim($('#userName').val());
            var fullName = $.trim($('#fullName').val());
            var email = $.trim($('#email').val());
            var password = isEditing ? '' : $('#password').val();

            if (!roleCode) {
                hasError = BmsForms.setFieldError($form, 'roleCode', 'Chưa chọn vai trò.') || hasError;
            }
            if (!isEditing && !/^[A-Za-z0-9._-]{3,50}$/.test(userName)) {
                hasError = BmsForms.setFieldError($form, 'userName', 'Tên đăng nhập cần 3–50 ký tự đúng định dạng.') || hasError;
            }
            if (!fullName) {
                hasError = BmsForms.setFieldError($form, 'fullName', 'Chưa nhập tên đầy đủ.') || hasError;
            }
            if (email && !document.getElementById('email').checkValidity()) {
                hasError = BmsForms.setFieldError($form, 'email', 'Email phải có định dạng hợp lệ.') || hasError;
            }
            if (!isEditing && password.length < 8) {
                hasError = BmsForms.setFieldError($form, 'password', 'Mật khẩu cần ít nhất 8 ký tự.') || hasError;
            }

            if (hasError) {
                $form.find('[aria-invalid="true"]').first().trigger('focus');
                return false;
            }
            return true;
        }

        $form.on('submit', function (event) {
            event.preventDefault();
            if (!validateUserForm()) {
                return;
            }

            var data = {};
            $.each($form.serializeArray(), function (_, field) {
                data[field.name] = field.value;
            });
            data.fullName = $.trim(data.fullName);
            data.email = $.trim(data.email);
            if (!isEditing) {
                data.confirmPassword = data.password;
            }

            BmsForms.setBusy($saveButton, true, isEditing ? 'Đang lưu…' : 'Đang tạo…');
            $.ajax({
                url: '${userApiUrl}' + (isEditing ? '/' + $('#userId').val() : ''),
                type: isEditing ? 'PUT' : 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify(data),
                success: function (user) {
                    window.location.href = '${userEditBaseUrl}' + user.id + '?message=' + (isEditing ? 'update_success' : 'insert_success');
                },
                error: function (xhr) {
                    BmsForms.applyApiErrors($form, $status, xhr, isEditing ? 'Không thể cập nhật người dùng.' : 'Không thể tạo người dùng.');
                },
                complete: function () {
                    BmsForms.setBusy($saveButton, false, '');
                }
            });
        });

        var $resetForm = $('#resetPasswordForm');
        var $resetButton = $('#resetPasswordButton');
        $resetForm.on('submit', function (event) {
            event.preventDefault();
            BmsForms.clearErrors($resetForm);
            BmsForms.hideStatus($status);
            var password = $('#resetPassword').val();
            if (password.length < 8) {
                BmsForms.setFieldError($resetForm, 'newPassword', 'Mật khẩu mới cần ít nhất 8 ký tự.');
                $('#resetPassword').trigger('focus');
                return;
            }

            BmsForms.setBusy($resetButton, true, 'Đang cập nhật…');
            $.ajax({
                url: '${userApiUrl}/' + $('#userId').val() + '/password',
                type: 'PUT',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({newPassword: password, confirmPassword: password}),
                success: function (user) {
                    window.location.href = '${userEditBaseUrl}' + user.id + '?message=reset_password_success';
                },
                error: function (xhr) {
                    BmsForms.applyApiErrors($resetForm, $status, xhr, 'Không thể cập nhật mật khẩu.');
                },
                complete: function () {
                    BmsForms.setBusy($resetButton, false, '');
                }
            });
        });
    })(jQuery);
</script>
</body>
</html>
