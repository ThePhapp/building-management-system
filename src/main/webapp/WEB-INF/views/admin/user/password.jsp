<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/common/taglib.jsp" %>
<c:url var="changePasswordUrl" value="/api/user/change-password"/>
<html lang="vi">
<head>
    <title>Đổi mật khẩu</title>
</head>
<body>
<div class="main-content">
    <div class="main-content-inner">
        <div class="breadcrumbs" id="breadcrumbs">
            <ul class="breadcrumb">
                <li><a href="<c:url value='/admin/home'/>">Trang chủ</a></li>
                <li class="active">Đổi mật khẩu</li>
            </ul>
        </div>

        <div class="page-content">
            <header class="app-page-header">
                <div class="app-page-heading">
                    <h1>Đổi mật khẩu</h1>
                    <p>Tạo mật khẩu mới cho tài khoản của bạn. Mật khẩu cần có ít nhất 8 ký tự.</p>
                </div>
            </header>

            <div class="app-account-layout">
                <c:if test="${not empty messageResponse}">
                    <div class="app-form-status ${alert == 'success' ? 'is-success' : 'is-danger'}" role="status">
                        <c:out value="${messageResponse}"/>
                    </div>
                </c:if>
                <div class="app-form-status" id="passwordStatus" role="alert" hidden></div>

                <form id="passwordForm" novalidate>
                    <input type="text" name="username" value="${fn:escapeXml(model.userName)}"
                           class="sr-only" tabindex="-1" autocomplete="username" readonly aria-hidden="true">
                    <section class="app-panel" aria-labelledby="passwordPanelTitle">
                        <div class="app-panel-header">
                            <h2 id="passwordPanelTitle">Bảo mật tài khoản</h2>
                            <p>Nhập mật khẩu hiện tại để xác nhận thay đổi.</p>
                        </div>
                        <div class="app-panel-body">
                            <div class="app-form-grid">
                                <div class="app-field is-full">
                                    <label for="oldPassword">Mật khẩu hiện tại <span class="app-required" aria-hidden="true">*</span></label>
                                    <div class="app-password-wrap">
                                        <input type="password" class="form-control" id="oldPassword" name="oldPassword"
                                               required autocomplete="current-password" aria-describedby="oldPasswordError">
                                        <button type="button" class="app-password-toggle" data-password-toggle="oldPassword"
                                                aria-controls="oldPassword" aria-label="Hiện mật khẩu hiện tại">Hiện</button>
                                    </div>
                                    <span class="app-field-error" id="oldPasswordError" data-error-for="oldPassword"></span>
                                </div>

                                <div class="app-field is-full">
                                    <label for="newPassword">Mật khẩu mới <span class="app-required" aria-hidden="true">*</span></label>
                                    <div class="app-password-wrap">
                                        <input type="password" class="form-control" id="newPassword" name="newPassword"
                                               required minlength="8" autocomplete="new-password"
                                               aria-describedby="newPasswordHelp newPasswordError">
                                        <button type="button" class="app-password-toggle" data-password-toggle="newPassword"
                                                aria-controls="newPassword" aria-label="Hiện mật khẩu mới">Hiện</button>
                                    </div>
                                    <span class="app-field-hint" id="newPasswordHelp">Ít nhất 8 ký tự.</span>
                                    <span class="app-field-error" id="newPasswordError" data-error-for="newPassword"></span>
                                </div>
                            </div>

                            <div class="app-security-note">
                                <i class="fa fa-shield" aria-hidden="true"></i>
                                <span>Không chia sẻ mật khẩu. Sau khi đổi, hãy dùng mật khẩu mới cho lần đăng nhập tiếp theo.</span>
                            </div>
                        </div>
                        <div class="app-panel-footer">
                            <button type="submit" class="btn btn-primary" id="changePasswordButton">
                                <span class="app-button-spinner" aria-hidden="true"></span>
                                <span data-button-label>Đổi mật khẩu</span>
                            </button>
                        </div>
                    </section>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    (function ($) {
        var $form = $('#passwordForm');
        var $status = $('#passwordStatus');
        var $button = $('#changePasswordButton');

        $form.on('submit', function (event) {
            event.preventDefault();
            BmsForms.clearErrors($form);
            BmsForms.hideStatus($status);

            var oldPassword = $('#oldPassword').val();
            var newPassword = $('#newPassword').val();
            var hasError = false;

            if (!oldPassword) {
                hasError = BmsForms.setFieldError($form, 'oldPassword', 'Chưa nhập mật khẩu hiện tại.') || hasError;
            }
            if (!newPassword) {
                hasError = BmsForms.setFieldError($form, 'newPassword', 'Chưa nhập mật khẩu mới.') || hasError;
            } else if (newPassword.length < 8) {
                hasError = BmsForms.setFieldError($form, 'newPassword', 'Mật khẩu mới cần ít nhất 8 ký tự.') || hasError;
            }
            if (hasError) {
                $form.find('[aria-invalid="true"]').first().trigger('focus');
                return;
            }

            BmsForms.setBusy($button, true, 'Đang cập nhật…');
            $.ajax({
                url: '${changePasswordUrl}',
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({
                    oldPassword: oldPassword,
                    newPassword: newPassword,
                    confirmPassword: newPassword
                }),
                success: function () {
                    $form[0].reset();
                    BmsForms.showStatus($status, 'Mật khẩu đã được thay đổi.', 'success');
                    $('#oldPassword').trigger('focus');
                },
                error: function (xhr) {
                    var code = xhr.responseJSON && xhr.responseJSON.message;
                    if (code === 'change_password_fail') {
                        BmsForms.setFieldError($form, 'oldPassword', 'Mật khẩu hiện tại chưa đúng.');
                        $('#oldPassword').val('').trigger('focus');
                        BmsForms.showStatus($status, 'Chưa thể đổi mật khẩu. Kiểm tra trường được đánh dấu.', 'danger');
                    } else {
                        BmsForms.applyApiErrors($form, $status, xhr, 'Không thể đổi mật khẩu.');
                    }
                },
                complete: function () {
                    BmsForms.setBusy($button, false, 'Đang cập nhật…');
                }
            });
        });
    })(jQuery);
</script>
</body>
</html>
