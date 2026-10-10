<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<c:url var="userListUrl" value="/admin/user-list"/>
<c:url var="userApiUrl" value="/api/user"/>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Người dùng và nhân viên</title>
</head>
<body>
<div class="main-content">
    <div class="main-content-inner">
        <div class="breadcrumbs" id="breadcrumbs">
            <ul class="breadcrumb">
                <li><a href="<c:url value='/admin/home'/>">Trang chủ</a></li>
                <li class="active">Người dùng và nhân viên</li>
            </ul>
        </div>

        <div class="page-content">
            <header class="app-page-header">
                <div class="app-page-heading">
                    <h1>Người dùng và nhân viên</h1>
                    <p>Quản lý tài khoản đang hoạt động, vai trò và quyền truy cập hệ thống.</p>
                </div>
                <div class="app-page-actions">
                    <a href="<c:url value='/admin/user-edit'/>" class="btn btn-primary">
                        <i class="fa fa-plus" aria-hidden="true"></i> Thêm người dùng
                    </a>
                </div>
            </header>

            <c:if test="${not empty messageResponse}">
                <div class="app-form-status ${alert == 'success' ? 'is-success' : 'is-danger'}" role="status">
                    <c:out value="${messageResponse}"/>
                </div>
            </c:if>

            <form:form modelAttribute="model" action="${userListUrl}" id="userSearchForm" method="GET"
                       cssClass="app-search-panel">
                <div class="app-field">
                    <label for="searchValue">Tìm người dùng</label>
                    <form:input path="searchValue" id="searchValue" cssClass="form-control"
                                autocomplete="off" aria-label="Tìm theo tên đăng nhập, họ tên hoặc email"/>
                </div>
                <button type="submit" class="btn btn-primary">
                    <i class="fa fa-search" aria-hidden="true"></i> Tìm kiếm
                </button>
                <c:if test="${not empty model.searchValue}">
                    <a href="${userListUrl}" class="btn btn-default">Xóa tìm kiếm</a>
                </c:if>
            </form:form>

            <div class="app-list-toolbar">
                <p class="app-list-summary">
                    <strong><c:out value="${model.totalItems}"/></strong> tài khoản đang hoạt động
                </p>
                <div class="app-list-actions">
                    <button id="deactivateUsersButton" type="button" disabled class="btn btn-danger" data-bulk-action>
                        <i class="fa fa-user-times" aria-hidden="true"></i> Vô hiệu hóa đã chọn
                    </button>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty model.listResult}">
                    <div class="app-empty-state">
                        <c:choose>
                            <c:when test="${not empty model.searchValue}">
                                Không có người dùng nào khớp “<c:out value="${model.searchValue}"/>”.
                                <a href="${userListUrl}">Xóa tìm kiếm</a>
                            </c:when>
                            <c:otherwise>Chưa có người dùng đang hoạt động.</c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <display:table name="model.listResult" cellspacing="0" cellpadding="0"
                                       requestURI="${userListUrl}" partialList="true" sort="external"
                                       size="${model.totalItems}" id="tableList" pagesize="${model.maxPageItems}"
                                       export="false" class="table table-bordered table-hover dataTable no-footer">
                            <display:column
                                    title="<input type='checkbox' id='checkAll' class='check-box-element' aria-label='Chọn tất cả người dùng trên trang'>"
                                    class="center select-cell" headerClass="center select-cell">
                                <input type="checkbox" name="checkList" value="${tableList.id}"
                                       id="user_${tableList.id}" class="check-box-element"
                                       aria-label="Chọn ${fn:escapeXml(tableList.fullName)}">
                            </display:column>
                            <display:column headerClass="text-left" title="Người dùng">
                                <div class="app-user-cell">
                                    <strong><c:out value="${tableList.fullName}"/></strong>
                                    <span>@<c:out value="${tableList.userName}"/></span>
                                </div>
                            </display:column>
                            <display:column headerClass="text-left" title="Email">
                                <c:choose>
                                    <c:when test="${not empty tableList.email}"><c:out value="${tableList.email}"/></c:when>
                                    <c:otherwise><span class="text-muted">Chưa cập nhật</span></c:otherwise>
                                </c:choose>
                            </display:column>
                            <display:column headerClass="text-left" title="Vai trò">
                                <span class="app-badge ${tableList.roleCode == 'ADMIN' ? 'is-admin' : ''}">
                                    <c:out value="${tableList.roleName}"/>
                                </span>
                            </display:column>
                            <display:column headerClass="text-right" class="text-right" title="Thao tác">
                                <div class="app-table-actions">
                                    <a class="app-icon-action" href="<c:url value='/admin/user-edit-${tableList.id}'/>"
                                       aria-label="Chỉnh sửa ${fn:escapeXml(tableList.fullName)}"
                                       title="Chỉnh sửa người dùng">
                                        <i class="fa fa-pencil" aria-hidden="true"></i>
                                    </a>
                                </div>
                            </display:column>
                        </display:table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script>
    (function ($) {
        var $deactivateButton = $('#deactivateUsersButton');

        function selectedUserIds() {
            return $('tbody input[name="checkList"]:checked').map(function () {
                return Number($(this).val());
            }).get();
        }

        $deactivateButton.on('click', function () {
            var ids = selectedUserIds();
            if (!ids.length) {
                return;
            }

            swal({
                title: 'Vô hiệu hóa tài khoản?',
                text: 'Các tài khoản đã chọn sẽ không thể đăng nhập cho đến khi được kích hoạt lại.',
                type: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Vô hiệu hóa',
                cancelButtonText: 'Hủy'
            }).then(function (result) {
                if (!result.value) {
                    return;
                }

                $deactivateButton.prop('disabled', true).attr('aria-busy', 'true');
                $.ajax({
                    url: '${userApiUrl}',
                    type: 'DELETE',
                    contentType: 'application/json',
                    data: JSON.stringify(ids),
                    success: function () {
                        window.location.href = '${userListUrl}?message=delete_success';
                    },
                    error: function (xhr) {
                        var message = xhr.responseJSON && xhr.responseJSON.message
                            ? xhr.responseJSON.message
                            : 'Không thể vô hiệu hóa người dùng.';
                        swal('Chưa thể thực hiện', message, 'error');
                        $deactivateButton.prop('disabled', false).removeAttr('aria-busy');
                    }
                });
            });
        });
    })(jQuery);
</script>
</body>
</html>
