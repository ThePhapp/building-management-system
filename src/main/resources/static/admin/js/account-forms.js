(function ($, window) {
    'use strict';

    function clearErrors($form) {
        $form.find('.app-field').removeClass('has-error');
        $form.find('[aria-invalid="true"]').removeAttr('aria-invalid');
        $form.find('[data-error-for]').text('');
    }

    function setFieldError($form, fieldName, message) {
        var $field = $form.find('[name="' + fieldName + '"]').first();
        var $error = $form.find('[data-error-for="' + fieldName + '"]').first();
        if (!$field.length || !$error.length) {
            return false;
        }
        $field.attr('aria-invalid', 'true').closest('.app-field').addClass('has-error');
        $error.text(message);
        return true;
    }

    function showStatus($status, message, type) {
        if (!$status || !$status.length) {
            return;
        }
        $status.removeClass('is-danger is-success').addClass(type === 'success' ? 'is-success' : 'is-danger');
        $status.text(message).prop('hidden', false);
    }

    function hideStatus($status) {
        if ($status && $status.length) {
            $status.prop('hidden', true).text('').removeClass('is-danger is-success');
        }
    }

    function setBusy($button, busy, busyText) {
        var $label = $button.find('[data-button-label]');
        if (!$button.data('default-label')) {
            $button.data('default-label', $label.text());
        }
        $button.prop('disabled', busy).attr('aria-busy', String(busy));
        $label.text(busy ? busyText : $button.data('default-label'));
    }

    function friendlyMessage(message, fallback) {
        var messages = {
            change_password_fail: 'Mật khẩu hiện tại chưa đúng.',
            password_policy_fail: 'Mật khẩu mới cần ít nhất 8 ký tự.',
            error_system: 'Hệ thống chưa thể xử lý yêu cầu.'
        };
        return messages[message] || message || fallback;
    }

    function applyApiErrors($form, $status, xhr, fallback) {
        clearErrors($form);
        var body = xhr && xhr.responseJSON ? xhr.responseJSON : {};
        var details = $.isArray(body.details) ? body.details : [];
        var mapped = 0;

        $.each(details, function (_, detail) {
            var match = /^([^:]+):\s*(.+)$/.exec(detail || '');
            if (match && setFieldError($form, match[1], match[2])) {
                mapped++;
            }
        });

        if (mapped > 0) {
            showStatus($status, 'Vui lòng kiểm tra các trường được đánh dấu.', 'danger');
            $form.find('[aria-invalid="true"]').first().trigger('focus');
            return;
        }

        showStatus($status, friendlyMessage(body.message, fallback), 'danger');
    }

    $(function () {
        $(document).on('click', '[data-password-toggle]', function () {
            var $button = $(this);
            var input = document.getElementById($button.attr('data-password-toggle'));
            if (!input) {
                return;
            }
            var shouldShow = input.type === 'password';
            input.type = shouldShow ? 'text' : 'password';
            $button.text(shouldShow ? 'Ẩn' : 'Hiện');
            $button.attr('aria-label', shouldShow ? 'Ẩn mật khẩu' : 'Hiện mật khẩu');
            input.focus();
        });

        $(document).on('input change', '.app-field [name]', function () {
            var $field = $(this);
            var $form = $field.closest('form');
            $field.removeAttr('aria-invalid').closest('.app-field').removeClass('has-error');
            $form.find('[data-error-for="' + $field.attr('name') + '"]').text('');
        });
    });

    window.BmsForms = {
        clearErrors: clearErrors,
        setFieldError: setFieldError,
        showStatus: showStatus,
        hideStatus: hideStatus,
        setBusy: setBusy,
        applyApiErrors: applyApiErrors
    };
})(jQuery, window);
