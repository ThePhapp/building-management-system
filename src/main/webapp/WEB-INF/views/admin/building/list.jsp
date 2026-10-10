<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<c:url var="buildingListUrl" value="/admin/building-list"/>
<c:url var="buildingApiUrl" value="/api/building"/>
<c:url var="buildingEditUrl" value="/admin/building-edit"/>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách tòa nhà</title>
</head>
<body>
<div class="main-content">
    <div class="main-content-inner">
        <div class="breadcrumbs" id="breadcrumbs">
            <ul class="breadcrumb">
                <li><a href="<c:url value='/admin/home'/>">Trang chủ</a></li>
                <li class="active">Tòa nhà</li>
            </ul>
        </div>

        <div class="page-content">
            <header class="app-page-header">
                <div class="app-page-heading">
                    <h1>Danh sách tòa nhà</h1>
                    <p>Tìm kiếm, cập nhật và phân công nhân viên phụ trách từng tòa nhà.</p>
                </div>
                <security:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
                    <div class="app-page-actions">
                        <a href="${buildingEditUrl}" class="btn btn-primary">
                            <i class="fa fa-plus" aria-hidden="true"></i> Thêm tòa nhà
                        </a>
                    </div>
                </security:authorize>
            </header>

            <c:if test="${not empty messageResponse}">
                <div class="app-form-status ${alert == 'success' ? 'is-success' : 'is-danger'}" role="status">
                    <c:out value="${messageResponse}"/>
                </div>
            </c:if>
            <div class="app-form-status" id="buildingListStatus" role="status" tabindex="-1" hidden></div>

            <form:form id="buildingSearchForm" modelAttribute="modelSearch" action="${buildingListUrl}"
                       method="GET" cssClass="app-building-filter">
                <div class="app-building-filter-main">
                    <div class="app-field">
                        <label for="name">Tên tòa nhà</label>
                        <form:input path="name" id="name" cssClass="form-control" maxlength="255"
                                    autocomplete="off" placeholder="Ví dụ: Landmark"/>
                    </div>
                    <div class="app-field">
                        <label for="district">Quận</label>
                        <form:select path="district" id="district" cssClass="form-control">
                            <form:option value="" label="Tất cả quận"/>
                            <form:options items="${districts}"/>
                        </form:select>
                    </div>
                    <div class="app-field">
                        <label for="street">Đường</label>
                        <form:input path="street" id="street" cssClass="form-control" maxlength="255"
                                    autocomplete="off" placeholder="Tên đường"/>
                    </div>
                    <div class="app-building-filter-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="fa fa-search" aria-hidden="true"></i> Tìm kiếm
                        </button>
                        <a href="${buildingListUrl}" class="btn btn-default">Đặt lại</a>
                    </div>
                </div>

                <details class="app-filter-details" id="advancedBuildingFilters">
                    <summary>Bộ lọc nâng cao</summary>
                    <div class="app-filter-details-body">
                        <div class="app-form-grid app-form-grid-three">
                            <div class="app-field">
                                <label for="ward">Phường</label>
                                <form:input path="ward" id="ward" cssClass="form-control" autocomplete="off"/>
                            </div>
                            <div class="app-field">
                                <label for="numberOfBasement">Số tầng hầm</label>
                                <form:input path="numberOfBasement" id="numberOfBasement" type="number"
                                            min="0" cssClass="form-control"/>
                            </div>
                            <div class="app-field">
                                <label for="floorArea">Diện tích sàn (m²)</label>
                                <form:input path="floorArea" id="floorArea" type="number" min="0"
                                            cssClass="form-control"/>
                            </div>
                            <div class="app-field">
                                <label for="rentAreaFrom">Diện tích thuê từ (m²)</label>
                                <form:input path="rentAreaFrom" id="rentAreaFrom" type="number" min="0"
                                            cssClass="form-control"/>
                            </div>
                            <div class="app-field">
                                <label for="rentAreaTo">Diện tích thuê đến (m²)</label>
                                <form:input path="rentAreaTo" id="rentAreaTo" type="number" min="0"
                                            cssClass="form-control"/>
                            </div>
                            <div class="app-field">
                                <label for="staffId">Nhân viên phụ trách</label>
                                <security:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
                                    <form:select path="staffId" id="staffId" cssClass="form-control">
                                        <form:option value="" label="Tất cả nhân viên"/>
                                        <form:options items="${staffsList}"/>
                                    </form:select>
                                </security:authorize>
                                <security:authorize access="hasRole('STAFF')">
                                    <span class="app-field-hint">Danh sách chỉ hiển thị tòa nhà được giao cho bạn.</span>
                                </security:authorize>
                            </div>
                            <div class="app-field">
                                <label for="rentPriceFrom">Giá thuê từ</label>
                                <form:input path="rentPriceFrom" id="rentPriceFrom" type="number" min="0"
                                            cssClass="form-control"/>
                            </div>
                            <div class="app-field">
                                <label for="rentPriceTo">Giá thuê đến</label>
                                <form:input path="rentPriceTo" id="rentPriceTo" type="number" min="0"
                                            cssClass="form-control"/>
                            </div>
                            <div class="app-field">
                                <label for="managerName">Tên quản lý</label>
                                <form:input path="managerName" id="managerName" cssClass="form-control"
                                            autocomplete="off"/>
                            </div>
                            <div class="app-field">
                                <label for="managerPhone">Số điện thoại quản lý</label>
                                <form:input path="managerPhone" id="managerPhone" cssClass="form-control"
                                            inputmode="tel" autocomplete="off"/>
                            </div>
                            <fieldset class="app-field app-choice-field is-full">
                                <legend class="app-field-label">Loại tòa nhà</legend>
                                <div class="app-choice-grid">
                                    <c:forEach var="typeOption" items="${typeCode}">
                                        <label class="app-choice-control" for="filterType_${typeOption.key}">
                                            <form:checkbox path="typeCode" value="${typeOption.key}"
                                                           id="filterType_${typeOption.key}"/>
                                            <span><c:out value="${typeOption.value}"/></span>
                                        </label>
                                    </c:forEach>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </details>
            </form:form>

            <div class="app-list-toolbar">
                <p class="app-list-summary">
                    <strong><c:out value="${fn:length(responseList)}"/></strong> tòa nhà phù hợp
                </p>
                <security:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
                    <div class="app-list-actions">
                        <button id="deleteSelectedBuildingsButton" type="button" class="btn btn-danger"
                                data-bulk-action disabled>
                            <i class="fa fa-trash" aria-hidden="true"></i> Xóa đã chọn
                        </button>
                    </div>
                </security:authorize>
            </div>

            <c:choose>
                <c:when test="${empty responseList}">
                    <div class="app-empty-state">
                        Không có tòa nhà nào khớp bộ lọc hiện tại.
                        <a href="${buildingListUrl}">Đặt lại bộ lọc</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive app-building-table-wrap">
                        <table id="buildingTable" class="table table-bordered table-hover app-building-table">
                            <thead>
                            <tr>
                                <security:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
                                    <th class="center select-cell">
                                        <input type="checkbox" id="checkAll" class="check-box-element"
                                               aria-label="Chọn tất cả tòa nhà trên trang">
                                    </th>
                                </security:authorize>
                                <th>Tòa nhà</th>
                                <th>Diện tích thuê</th>
                                <th>Giá thuê</th>
                                <th>Quản lý</th>
                                <security:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
                                    <th class="text-right">Thao tác</th>
                                </security:authorize>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="item" items="${responseList}">
                                <tr data-building-id="${item.id}">
                                    <security:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
                                        <td class="center select-cell" data-label="Chọn">
                                            <input type="checkbox" name="checkList" value="${item.id}"
                                                   id="building_${item.id}" class="check-box-element"
                                                   aria-label="Chọn ${fn:escapeXml(item.name)}">
                                        </td>
                                    </security:authorize>
                                    <td data-label="Tòa nhà">
                                        <div class="app-building-cell">
                                            <strong class="app-building-name"><c:out value="${item.name}"/></strong>
                                            <span><c:out value="${item.address}" default="Chưa cập nhật địa chỉ"/></span>
                                        </div>
                                    </td>
                                    <td data-label="Diện tích thuê">
                                        <c:choose>
                                            <c:when test="${not empty item.rentArea}"><c:out value="${item.rentArea}"/> m²</c:when>
                                            <c:otherwise><span class="text-muted">Chưa cập nhật</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td data-label="Giá thuê">
                                        <c:choose>
                                            <c:when test="${not empty item.rentPrice}"><c:out value="${item.rentPrice}"/></c:when>
                                            <c:otherwise><span class="text-muted">Liên hệ</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td data-label="Quản lý">
                                        <div class="app-building-manager">
                                            <c:choose>
                                                <c:when test="${not empty item.managerName}">
                                                    <strong><c:out value="${item.managerName}"/></strong>
                                                    <span><c:out value="${item.managerPhone}" default="Chưa có số điện thoại"/></span>
                                                </c:when>
                                                <c:otherwise><span class="text-muted">Chưa phân công quản lý</span></c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <security:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
                                        <td class="text-right" data-label="Thao tác">
                                            <div class="app-table-actions">
                                                <button type="button" class="app-icon-action js-assign-building"
                                                        data-building-id="${item.id}" title="Phân công nhân viên"
                                                        aria-label="Phân công nhân viên cho ${fn:escapeXml(item.name)}">
                                                    <i class="fa fa-users" aria-hidden="true"></i>
                                                </button>
                                                <a class="app-icon-action" href="<c:url value='/admin/building-edit-${item.id}'/>"
                                                   title="Chỉnh sửa tòa nhà"
                                                   aria-label="Chỉnh sửa ${fn:escapeXml(item.name)}">
                                                    <i class="fa fa-pencil" aria-hidden="true"></i>
                                                </a>
                                                <button type="button" class="app-icon-action is-danger js-delete-building"
                                                        data-building-id="${item.id}" title="Xóa tòa nhà"
                                                        aria-label="Xóa ${fn:escapeXml(item.name)}">
                                                    <i class="fa fa-trash" aria-hidden="true"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </security:authorize>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<security:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
    <div class="modal fade app-assignment-modal" id="assignmentBuildingModal" tabindex="-1" role="dialog"
         aria-modal="true" aria-labelledby="assignmentBuildingTitle">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Đóng">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h2 class="modal-title" id="assignmentBuildingTitle">Phân công nhân viên</h2>
                    <p id="assignmentBuildingSubtitle">Chọn nhân viên phụ trách tòa nhà.</p>
                </div>
                <div class="modal-body">
                    <div class="app-modal-loading" id="assignmentLoading" role="status">
                        <span class="app-loading-spinner" aria-hidden="true"></span>
                        Đang tải danh sách nhân viên…
                    </div>
                    <div class="app-form-status is-danger" id="assignmentError" role="alert" hidden></div>
                    <div class="app-assignment-empty" id="assignmentEmpty" hidden>
                        Chưa có nhân viên đang hoạt động. Lưu để gỡ toàn bộ phân công hiện tại.
                    </div>
                    <div class="app-assignment-list" id="staffChoices" role="group"
                         aria-label="Danh sách nhân viên" hidden></div>
                    <input type="hidden" id="assignmentBuildingId">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary" id="saveAssignmentButton" disabled>
                        <span class="app-button-spinner" aria-hidden="true"></span>
                        <span data-button-label>Lưu phân công</span>
                    </button>
                </div>
            </div>
        </div>
    </div>
</security:authorize>

<script>
    (function ($) {
        var listUrl = '${buildingListUrl}';
        var apiUrl = '${buildingApiUrl}';
        var $pageStatus = $('#buildingListStatus');
        var $advancedFilters = $('#advancedBuildingFilters');

        $('#buildingSearchForm').on('submit', function () {
            $(this).find('input[type="hidden"][name^="_"]').remove();
            $(this).find('input, select').filter(function () {
                return this.name && this.type !== 'checkbox' && !this.value;
            }).remove();
        });

        if ($advancedFilters.find('input:not([type="hidden"]), select').filter(function () {
            return this.type === 'checkbox' ? this.checked : Boolean($(this).val());
        }).length) {
            $advancedFilters.prop('open', true);
        }

        function responseMessage(xhr, fallback) {
            return xhr && xhr.responseJSON && xhr.responseJSON.message
                ? xhr.responseJSON.message
                : fallback;
        }

        function selectedBuildingIds() {
            return $('#buildingTable tbody input[name="checkList"]:checked').map(function () {
                return Number($(this).val());
            }).get();
        }

        function deleteBuildings(ids, description) {
            if (!ids.length) {
                return;
            }
            swal({
                title: ids.length === 1 ? 'Xóa tòa nhà?' : 'Xóa các tòa nhà đã chọn?',
                text: description || (ids.length + ' tòa nhà sẽ bị xóa cùng dữ liệu diện tích thuê và phân công liên quan.'),
                type: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Xóa',
                cancelButtonText: 'Hủy'
            }).then(function (result) {
                if (!result.value) {
                    return;
                }
                $('#deleteSelectedBuildingsButton').prop('disabled', true).attr('aria-busy', 'true');
                $.ajax({
                    url: apiUrl + '/' + ids.join(','),
                    type: 'DELETE',
                    success: function () {
                        window.location.href = listUrl + '?message=building_delete_success';
                    },
                    error: function (xhr) {
                        swal('Chưa thể xóa', responseMessage(xhr, 'Không thể xóa tòa nhà.'), 'error');
                        $('#deleteSelectedBuildingsButton').removeAttr('aria-busy');
                        syncBulkActionState();
                    }
                });
            });
        }

        $('#deleteSelectedBuildingsButton').on('click', function () {
            deleteBuildings(selectedBuildingIds());
        });

        $('.js-delete-building').on('click', function () {
            var $row = $(this).closest('tr');
            var name = $.trim($row.find('.app-building-name').text());
            deleteBuildings([Number($(this).attr('data-building-id'))],
                '“' + name + '” sẽ bị xóa cùng dữ liệu diện tích thuê và phân công liên quan.');
        });

        var $modal = $('#assignmentBuildingModal');
        var $loading = $('#assignmentLoading');
        var $error = $('#assignmentError');
        var $empty = $('#assignmentEmpty');
        var $choices = $('#staffChoices');
        var $saveButton = $('#saveAssignmentButton');

        function resetAssignmentModal() {
            $loading.prop('hidden', false);
            $error.prop('hidden', true).text('');
            $empty.prop('hidden', true);
            $choices.prop('hidden', true).empty();
            $saveButton.prop('disabled', true).removeAttr('aria-busy');
        }

        function renderStaffChoices(staffs) {
            $choices.empty();
            $.each(staffs, function (_, staff) {
                var inputId = 'assignment_staff_' + staff.staffId;
                var $checkbox = $('<input>', {
                    type: 'checkbox',
                    id: inputId,
                    value: staff.staffId
                }).prop('checked', staff.checked === 'checked');
                var $label = $('<label>', {
                    'class': 'app-assignment-option',
                    'for': inputId
                }).append($checkbox).append($('<span>').text(staff.fullName || 'Nhân viên chưa có tên'));
                $choices.append($label);
            });
            $empty.prop('hidden', staffs.length !== 0);
            $choices.prop('hidden', staffs.length === 0);
        }

        $('.js-assign-building').on('click', function () {
            var $row = $(this).closest('tr');
            var buildingId = Number($(this).attr('data-building-id'));
            var buildingName = $.trim($row.find('.app-building-name').text());
            resetAssignmentModal();
            $('#assignmentBuildingId').val(buildingId);
            $('#assignmentBuildingSubtitle').text('Chọn nhân viên phụ trách “' + buildingName + '”.');
            $modal.modal('show');

            $.ajax({
                url: apiUrl + '/' + buildingId + '/staffs',
                type: 'GET',
                dataType: 'json',
                success: function (response) {
                    renderStaffChoices($.isArray(response.data) ? response.data : []);
                    $saveButton.prop('disabled', false);
                },
                error: function (xhr) {
                    $error.text(responseMessage(xhr, 'Không thể tải danh sách nhân viên.')).prop('hidden', false);
                },
                complete: function () {
                    $loading.prop('hidden', true);
                }
            });
        });

        $saveButton.on('click', function () {
            var payload = {
                buildingId: Number($('#assignmentBuildingId').val()),
                staffs: $choices.find('input[type="checkbox"]:checked').map(function () {
                    return Number($(this).val());
                }).get()
            };
            $error.prop('hidden', true).text('');
            BmsForms.setBusy($saveButton, true, 'Đang lưu…');
            $.ajax({
                url: apiUrl + '/assignment',
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(payload),
                success: function () {
                    $modal.modal('hide');
                    BmsForms.showStatus($pageStatus, 'Đã cập nhật phân công nhân viên.', 'success');
                    $pageStatus.trigger('focus');
                },
                error: function (xhr) {
                    $error.text(responseMessage(xhr, 'Không thể lưu phân công nhân viên.')).prop('hidden', false);
                },
                complete: function () {
                    BmsForms.setBusy($saveButton, false, '');
                }
            });
        });
    })(jQuery);
</script>
</body>
</html>
