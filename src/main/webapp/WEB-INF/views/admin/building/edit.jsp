<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<c:url var="buildingApiUrl" value="/api/building"/>
<c:url var="buildingListUrl" value="/admin/building-list"/>
<c:url var="buildingEditBaseUrl" value="/admin/building-edit-"/>
<c:set var="isEditing" value="${not empty BuildingEdit.id}"/>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${isEditing ? 'Chỉnh sửa tòa nhà' : 'Thêm tòa nhà'}</title>
</head>
<body>
<div class="main-content">
    <div class="main-content-inner">
        <div class="breadcrumbs" id="breadcrumbs">
            <ul class="breadcrumb">
                <li><a href="<c:url value='/admin/home'/>">Trang chủ</a></li>
                <li><a href="${buildingListUrl}">Tòa nhà</a></li>
                <li class="active">${isEditing ? 'Chỉnh sửa' : 'Thêm mới'}</li>
            </ul>
        </div>

        <div class="page-content">
            <header class="app-page-header">
                <div class="app-page-heading">
                    <h1>${isEditing ? 'Chỉnh sửa tòa nhà' : 'Thêm tòa nhà'}</h1>
                    <p>${isEditing ? 'Cập nhật thông tin vận hành, giá thuê và người liên hệ.' : 'Khai báo đầy đủ thông tin để đưa tòa nhà vào danh sách quản lý.'}</p>
                </div>
            </header>

            <div class="app-building-form-layout">
                <c:if test="${not empty messageResponse}">
                    <div class="app-form-status ${alert == 'success' ? 'is-success' : 'is-danger'}" role="status">
                        <c:out value="${messageResponse}"/>
                    </div>
                </c:if>
                <div class="app-form-status" id="buildingFormStatus" role="alert" tabindex="-1" hidden></div>

                <form:form id="buildingForm" modelAttribute="BuildingEdit" novalidate="novalidate">
                    <section class="app-panel" aria-labelledby="buildingIdentityTitle">
                        <div class="app-panel-header">
                            <h2 id="buildingIdentityTitle">Thông tin cơ bản</h2>
                            <p>Các trường có dấu <span class="app-required">*</span> là bắt buộc.</p>
                        </div>
                        <div class="app-panel-body">
                            <div class="app-form-grid">
                                <div class="app-field is-full">
                                    <label for="name">Tên tòa nhà <span class="app-required" aria-hidden="true">*</span></label>
                                    <form:input path="name" id="name" cssClass="form-control" required="required"
                                                maxlength="255" autocomplete="organization"
                                                aria-describedby="nameError"/>
                                    <span class="app-field-error" id="nameError" data-error-for="name"></span>
                                </div>
                                <div class="app-field">
                                    <label for="district">Quận <span class="app-required" aria-hidden="true">*</span></label>
                                    <form:select path="district" id="district" cssClass="form-control"
                                                 required="required" aria-describedby="districtError">
                                        <form:option value="" label="Chọn quận"/>
                                        <form:options items="${districts}"/>
                                    </form:select>
                                    <span class="app-field-error" id="districtError" data-error-for="district"></span>
                                </div>
                                <div class="app-field">
                                    <label for="ward">Phường <span class="app-field-optional">Không bắt buộc</span></label>
                                    <form:input path="ward" id="ward" cssClass="form-control" maxlength="255"
                                                autocomplete="address-level3"/>
                                </div>
                                <div class="app-field is-full">
                                    <label for="street">Đường <span class="app-required" aria-hidden="true">*</span></label>
                                    <form:input path="street" id="street" cssClass="form-control" required="required"
                                                maxlength="255" autocomplete="street-address"
                                                aria-describedby="streetError"/>
                                    <span class="app-field-error" id="streetError" data-error-for="street"></span>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section class="app-panel" aria-labelledby="buildingStructureTitle">
                        <div class="app-panel-header">
                            <h2 id="buildingStructureTitle">Quy mô và loại hình</h2>
                            <p>Thông tin giúp nhân viên tra cứu và tư vấn mặt bằng phù hợp.</p>
                        </div>
                        <div class="app-panel-body">
                            <div class="app-form-grid">
                                <div class="app-field">
                                    <label for="numberOfBasement">Số tầng hầm</label>
                                    <form:input path="numberOfBasement" id="numberOfBasement" type="number" min="0"
                                                cssClass="form-control" aria-describedby="numberOfBasementError"/>
                                    <span class="app-field-error" id="numberOfBasementError" data-error-for="numberOfBasement"></span>
                                </div>
                                <div class="app-field">
                                    <label for="floorArea">Diện tích sàn (m²)</label>
                                    <form:input path="floorArea" id="floorArea" type="number" min="0"
                                                cssClass="form-control" aria-describedby="floorAreaError"/>
                                    <span class="app-field-error" id="floorAreaError" data-error-for="floorArea"></span>
                                </div>
                                <div class="app-field">
                                    <label for="structure">Kết cấu</label>
                                    <form:input path="structure" id="structure" cssClass="form-control"
                                                placeholder="Ví dụ: 2 hầm, 15 tầng"/>
                                </div>
                                <div class="app-field">
                                    <label for="direction">Hướng</label>
                                    <form:input path="direction" id="direction" cssClass="form-control"
                                                placeholder="Ví dụ: Đông Nam"/>
                                </div>
                                <div class="app-field">
                                    <label for="level">Hạng tòa nhà</label>
                                    <form:input path="level" id="level" cssClass="form-control"
                                                placeholder="Ví dụ: Hạng A"/>
                                </div>
                                <fieldset class="app-field app-choice-field is-full">
                                    <legend class="app-field-label">Loại tòa nhà <span class="app-required" aria-hidden="true">*</span></legend>
                                    <div class="app-choice-grid" aria-describedby="typeCodeError">
                                        <c:forEach var="typeOption" items="${types}">
                                            <label class="app-choice-control" for="buildingType_${typeOption.key}">
                                                <form:checkbox path="typeCode" value="${typeOption.key}"
                                                               id="buildingType_${typeOption.key}"/>
                                                <span><c:out value="${typeOption.value}"/></span>
                                            </label>
                                        </c:forEach>
                                    </div>
                                    <span class="app-field-error" id="typeCodeError" data-error-for="typeCode"></span>
                                </fieldset>
                            </div>
                        </div>
                    </section>

                    <section class="app-panel" aria-labelledby="buildingPriceTitle">
                        <div class="app-panel-header">
                            <h2 id="buildingPriceTitle">Diện tích và chi phí</h2>
                            <p>Nhập nhiều diện tích thuê bằng dấu phẩy, ví dụ: 100, 200, 350.</p>
                        </div>
                        <div class="app-panel-body">
                            <div class="app-form-grid">
                                <div class="app-field is-full">
                                    <label for="rentAreas">Diện tích thuê (m²) <span class="app-required" aria-hidden="true">*</span></label>
                                    <form:input path="rentAreas" id="rentAreas" cssClass="form-control"
                                                required="required" inputmode="numeric" placeholder="100, 200, 350"
                                                aria-describedby="rentAreasHint rentAreasError"/>
                                    <span class="app-field-hint" id="rentAreasHint">Mỗi diện tích phải là số nguyên lớn hơn 0.</span>
                                    <span class="app-field-error" id="rentAreasError" data-error-for="rentAreas"></span>
                                </div>
                                <div class="app-field">
                                    <label for="rentPrice">Giá thuê</label>
                                    <form:input path="rentPrice" id="rentPrice" type="number" min="0"
                                                cssClass="form-control" aria-describedby="rentPriceError"/>
                                    <span class="app-field-error" id="rentPriceError" data-error-for="rentPrice"></span>
                                </div>
                                <div class="app-field">
                                    <label for="rentPriceDescription">Mô tả giá thuê</label>
                                    <form:input path="rentPriceDescription" id="rentPriceDescription" cssClass="form-control"
                                                placeholder="Ví dụ: Chưa gồm VAT"/>
                                </div>
                                <div class="app-field">
                                    <label for="serviceFee">Phí dịch vụ</label>
                                    <form:input path="serviceFee" id="serviceFee" cssClass="form-control"/>
                                </div>
                                <div class="app-field">
                                    <label for="brokerageFee">Phí môi giới</label>
                                    <form:input path="brokerageFee" id="brokerageFee" type="number" min="0" step="0.01"
                                                cssClass="form-control" aria-describedby="brokerageFeeError"/>
                                    <span class="app-field-error" id="brokerageFeeError" data-error-for="brokerageFee"></span>
                                </div>
                                <div class="app-field">
                                    <label for="motoFee">Phí gửi xe máy</label>
                                    <form:input path="motoFee" id="motoFee" cssClass="form-control"/>
                                </div>
                                <div class="app-field">
                                    <label for="carFee">Phí gửi ô tô</label>
                                    <form:input path="carFee" id="carFee" cssClass="form-control"/>
                                </div>
                                <div class="app-field">
                                    <label for="electricityFee">Phí điện</label>
                                    <form:input path="electricityFee" id="electricityFee" cssClass="form-control"/>
                                </div>
                                <div class="app-field">
                                    <label for="overTimeFee">Phí ngoài giờ</label>
                                    <form:input path="overTimeFee" id="overTimeFee" cssClass="form-control"/>
                                </div>
                                <div class="app-field">
                                    <label for="deposit">Tiền đặt cọc</label>
                                    <form:input path="deposit" id="deposit" cssClass="form-control"/>
                                </div>
                                <div class="app-field">
                                    <label for="payment">Kỳ thanh toán</label>
                                    <form:input path="payment" id="payment" cssClass="form-control"/>
                                </div>
                                <div class="app-field">
                                    <label for="rentTime">Thời hạn thuê</label>
                                    <form:input path="rentTime" id="rentTime" cssClass="form-control"/>
                                </div>
                                <div class="app-field">
                                    <label for="decorationTime">Thời gian thi công</label>
                                    <form:input path="decorationTime" id="decorationTime" cssClass="form-control"/>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section class="app-panel" aria-labelledby="buildingContactTitle">
                        <div class="app-panel-header">
                            <h2 id="buildingContactTitle">Liên hệ và ghi chú</h2>
                            <p>Thông tin nội bộ phục vụ quá trình vận hành và tư vấn.</p>
                        </div>
                        <div class="app-panel-body">
                            <div class="app-form-grid">
                                <div class="app-field">
                                    <label for="managerName">Tên quản lý</label>
                                    <form:input path="managerName" id="managerName" cssClass="form-control"
                                                autocomplete="name"/>
                                </div>
                                <div class="app-field">
                                    <label for="managerPhone">Số điện thoại quản lý</label>
                                    <form:input path="managerPhone" id="managerPhone" cssClass="form-control"
                                                inputmode="tel" autocomplete="tel"/>
                                </div>
                                <div class="app-field is-full">
                                    <label for="note">Ghi chú</label>
                                    <form:textarea path="note" id="note" cssClass="form-control app-textarea" rows="4"/>
                                </div>
                            </div>
                        </div>
                        <div class="app-panel-footer">
                            <a href="${buildingListUrl}" class="btn btn-default">Hủy</a>
                            <button type="submit" class="btn btn-primary" id="saveBuildingButton">
                                <span class="app-button-spinner" aria-hidden="true"></span>
                                <span data-button-label>${isEditing ? 'Lưu thay đổi' : 'Tạo tòa nhà'}</span>
                            </button>
                        </div>
                    </section>
                    <form:hidden path="id" id="buildingId"/>
                    <form:hidden path="image"/>
                </form:form>
            </div>
        </div>
    </div>
</div>

<script>
    (function ($) {
        var isEditing = ${isEditing};
        var $form = $('#buildingForm');
        var $status = $('#buildingFormStatus');
        var $saveButton = $('#saveBuildingButton');

        function setError(name, message) {
            return BmsForms.setFieldError($form, name, message);
        }

        function invalidNonNegative(fieldName, label) {
            var value = $.trim($form.find('[name="' + fieldName + '"]').val());
            if (value !== '' && (!isFinite(Number(value)) || Number(value) < 0)) {
                setError(fieldName, label + ' không được âm.');
                return true;
            }
            return false;
        }

        function validateBuildingForm() {
            BmsForms.clearErrors($form);
            BmsForms.hideStatus($status);
            var hasError = false;
            var name = $.trim($('#name').val());
            var district = $('#district').val();
            var street = $.trim($('#street').val());
            var rentAreas = $.trim($('#rentAreas').val());
            var types = $form.find('input[name="typeCode"]:checked');

            if (!name) {
                hasError = setError('name', 'Chưa nhập tên tòa nhà.') || hasError;
            }
            if (!district) {
                hasError = setError('district', 'Chưa chọn quận.') || hasError;
            }
            if (!street) {
                hasError = setError('street', 'Chưa nhập tên đường.') || hasError;
            }
            if (!rentAreas) {
                hasError = setError('rentAreas', 'Chưa nhập diện tích thuê.') || hasError;
            } else {
                var values = rentAreas.split(',');
                var validAreas = values.every(function (value) {
                    return /^\s*\d+\s*$/.test(value) && Number($.trim(value)) > 0;
                });
                if (!validAreas) {
                    hasError = setError('rentAreas', 'Nhập các số nguyên lớn hơn 0, ngăn cách bằng dấu phẩy.') || hasError;
                }
            }
            if (!types.length) {
                hasError = setError('typeCode', 'Chọn ít nhất một loại tòa nhà.') || hasError;
            }
            hasError = invalidNonNegative('numberOfBasement', 'Số tầng hầm') || hasError;
            hasError = invalidNonNegative('floorArea', 'Diện tích sàn') || hasError;
            hasError = invalidNonNegative('rentPrice', 'Giá thuê') || hasError;
            hasError = invalidNonNegative('brokerageFee', 'Phí môi giới') || hasError;

            if (hasError) {
                BmsForms.showStatus($status, 'Vui lòng kiểm tra các trường được đánh dấu.', 'danger');
                $form.find('[aria-invalid="true"]').first().trigger('focus');
                return false;
            }
            return true;
        }

        function formPayload() {
            var data = {};
            var typeCodes = [];
            $.each($form.serializeArray(), function (_, field) {
                if (field.name.indexOf('_') === 0) {
                    return;
                }
                if (field.name === 'typeCode') {
                    typeCodes.push(field.value);
                } else {
                    data[field.name] = typeof field.value === 'string' ? $.trim(field.value) : field.value;
                }
            });
            data.typeCode = typeCodes;
            $.each(['id', 'numberOfBasement', 'floorArea', 'rentPrice'], function (_, fieldName) {
                data[fieldName] = data[fieldName] === '' ? null : Number(data[fieldName]);
            });
            return data;
        }

        $form.on('submit', function (event) {
            event.preventDefault();
            if (!validateBuildingForm()) {
                return;
            }

            BmsForms.setBusy($saveButton, true, isEditing ? 'Đang lưu…' : 'Đang tạo…');
            $.ajax({
                url: '${buildingApiUrl}',
                type: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify(formPayload()),
                success: function (building) {
                    window.location.href = '${buildingEditBaseUrl}' + building.id
                        + '?message=' + (isEditing ? 'update_success' : 'insert_success');
                },
                error: function (xhr) {
                    BmsForms.applyApiErrors($form, $status, xhr,
                        isEditing ? 'Không thể cập nhật tòa nhà.' : 'Không thể tạo tòa nhà.');
                    $status.trigger('focus');
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
