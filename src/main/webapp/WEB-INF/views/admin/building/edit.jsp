<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/common/taglib.jsp" %>
<c:url var="buildingAPI" value="/api/building"/>
<html>
<head>
    <title>Add Building</title>
</head>
<body>
<div class="main-content" id="main-container">
    <div class="main-content">
        <div class="main-content-inner">
            <div class="breadcrumbs" id="breadcrumbs">
                <script type="text/javascript">
                    try {
                        ace.settings.check('breadcrumbs', 'fixed')
                    } catch (e) {
                    }
                </script>

                <ul class="breadcrumb">
                    <li>
                        <i class="ace-icon fa fa-home home-icon"></i>
                        <a href="#">Home</a>
                    </li>
                    <li class="active">Dashboard</li>
                </ul><!-- /.breadcrumb -->

                <div class="nav-search" id="nav-search">
                    <form class="form-search">
								<span class="input-icon">
									<input type="text" placeholder="Search ..." class="nav-search-input"
                                           id="nav-search-input" autocomplete="off"/>
									<i class="ace-icon fa fa-search nav-search-icon"></i>
								</span>
                    </form>
                </div><!-- /.nav-search -->
            </div>

            <div class="page-content">
                <div class="ace-settings-container" id="ace-settings-container">
                    <div class="btn btn-app btn-xs btn-warning ace-settings-btn" id="ace-settings-btn">
                        <i class="ace-icon fa fa-cog bigger-130"></i>
                    </div>

                    <div class="ace-settings-box clearfix" id="ace-settings-box">
                        <div class="pull-left width-50">
                            <div class="ace-settings-item">
                                <div class="pull-left">
                                    <select id="skin-colorpicker" class="hide">
                                        <option data-skin="no-skin" value="#438EB9">#438EB9</option>
                                        <option data-skin="skin-1" value="#222A2D">#222A2D</option>
                                        <option data-skin="skin-2" value="#C6487E">#C6487E</option>
                                        <option data-skin="skin-3" value="#D0D0D0">#D0D0D0</option>
                                    </select>
                                </div>
                                <span>&nbsp; Choose Skin</span>
                            </div>

                            <div class="ace-settings-item">
                                <input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-navbar"/>
                                <label class="lbl" for="ace-settings-navbar"> Fixed Navbar</label>
                            </div>

                            <div class="ace-settings-item">
                                <input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-sidebar"/>
                                <label class="lbl" for="ace-settings-sidebar"> Fixed Sidebar</label>
                            </div>

                            <div class="ace-settings-item">
                                <input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-breadcrumbs"/>
                                <label class="lbl" for="ace-settings-breadcrumbs"> Fixed Breadcrumbs</label>
                            </div>

                            <div class="ace-settings-item">
                                <input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-rtl"/>
                                <label class="lbl" for="ace-settings-rtl"> Right To Left (rtl)</label>
                            </div>

                            <div class="ace-settings-item">
                                <input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-add-container"/>
                                <label class="lbl" for="ace-settings-add-container">
                                    Inside
                                    <b>.container</b>
                                </label>
                            </div>
                        </div><!-- /.pull-left -->

                        <div class="pull-left width-50">
                            <div class="ace-settings-item">
                                <input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-hover"/>
                                <label class="lbl" for="ace-settings-hover"> Submenu on Hover</label>
                            </div>

                            <div class="ace-settings-item">
                                <input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-compact"/>
                                <label class="lbl" for="ace-settings-compact"> Compact Sidebar</label>
                            </div>

                            <div class="ace-settings-item">
                                <input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-highlight"/>
                                <label class="lbl" for="ace-settings-highlight"> Alt. Active Item</label>
                            </div>
                        </div><!-- /.pull-left -->
                    </div><!-- /.ace-settings-box -->
                </div><!-- /.ace-settings-container -->

                <div class="page-header">
                    <h1>Edit Building <small><i class="ace-icon fa fa-angle-double-right"></i> overview & stats</small>
                    </h1>
                </div>

                <div class="row">
                    <form:form modelAttribute="BuildingEdit" id="listForm" method="GET">
                    <div class="col-xs-12">
                        <form class="form-horizontal" role="form">
                            <div class="form-container">
                                <!-- Building Information Section -->
                                <h3 class="form-section">Thông tin cơ bản</h3>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Tên tòa nhà</label>
                                    <div class="col-xs-9">
                                        <form:input class="form-control" path="name"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Quận</label>
                                    <div class="col-xs-4">
                                        <form:select class="form-control" path="district">
                                            <form:option value="">---Chọn quận---</form:option>
                                            <form:options items="${districts}"/>
                                        </form:select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Phường</label>
                                    <div class="col-xs-9">
                                        <form:input class="form-control" path="ward"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Đường</label>
                                    <div class="col-xs-9">
                                        <form:input class="form-control" path="street"/>
                                    </div>
                                </div>

                                <!-- Structure Section -->
                                <h3 class="form-section">Thông tin cấu trúc</h3>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Số tầng hầm</label>
                                    <div class="col-xs-4">
                                        <form:input class="form-control" path="numberOfBasement"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Diện tích sàn</label>
                                    <div class="col-xs-4">
                                        <form:input class="form-control" path="floorArea"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Hướng</label>
                                    <div class="col-xs-4">
                                        <form:input class="form-control" path="direction"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Hạng</label>
                                    <div class="col-xs-4">
                                        <form:input class="form-control" path="level"/>
                                    </div>
                                </div>

                                <!-- Pricing Section -->
                                <h3 class="form-section">Thông tin giá cả</h3>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Diện tích thuê</label>
                                    <div class="col-xs-4">
                                        <form:input class="form-control" path="rentAreas"/>

                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Giá thuê</label>
                                    <div class="col-xs-4">
                                        <form:input class="form-control" path="rentPrice"/>
                                    </div>
                                </div>

                                <!-- Building Type Section -->
                                <h3 class="form-section">Loại tòa nhà</h3>

                                <div class="form-group">
                                    <label class="col-xs-3 control-label">Loại hình</label>
                                    <div class="col-xs-9">
                                        <form:checkboxes
                                                path="typeCode"
                                                items="${types}"
                                                cssClass="form-field-checkbox"
                                                cssStyle=""/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-offset-3 col-xs-9">
                                        <c:if test="${not empty BuildingEdit.id}">
                                            <button type="submit" class="btn btn-primary" id="btnAddOrUpdateBuilding">
                                                Cập nhật
                                                tòa nhà
                                            </button>
                                            <button type="reset" class="btn btn-default" id="btnCancel">Hủy</button>
                                        </c:if>
                                        <c:if test="${empty BuildingEdit.id}">
                                            <button type="submit" class="btn btn-primary" id="btnAddOrUpdateBuilding">
                                                Thêm
                                                tòa nhà
                                            </button>
                                            <button type="reset" class="btn btn-default" id="btnCancel">Hủy</button>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            <form:hidden path="id" id="buildingId"/>
                        </form>
                    </div>
                </div>
                </form:form>

                <style>
                    .form-container {
                        background: #f9f9f9;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                    }

                    .form-section {
                        color: #2c3e50;
                        border-bottom: 2px solid #3498db;
                        padding-bottom: 5px;
                        margin: 20px 0 15px 0;
                        font-family: 'Times New Roman', Times, serif;
                    }

                    .form-group {
                        margin-bottom: 20px;
                    }

                    .control-label {
                        font-weight: 600;
                        padding-top: 7px;
                        font-family: 'Times New Roman', Times, serif;
                    }

                    .form-control {
                        border-radius: 4px;
                        border: 1px solid #ddd;
                        box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
                    }

                    .form-control:focus {
                        border-color: #3498db;
                        box-shadow: 0 0 5px rgba(52, 152, 219, 0.3);
                    }

                    .checkbox-inline {
                        margin-right: 15px;
                        font-family: 'Times New Roman', Times, serif;
                    }

                    @media (max-width: 767px) {
                        .control-label {
                            margin-bottom: 5px;
                        }

                        .col-xs-3, .col-xs-9, .col-xs-4 {
                            width: 100%;
                        }
                    }
                </style>
            </div>
            <!-- Bảng danh sách -->
        </div><!-- /.page-content -->
    </div><!-- /.main-content -->
    <a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
        <i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
    </a>
</div><!-- /.main-container -->

<!-- basic scripts -->

<!--[if !IE]> -->
<script src="assets/js/jquery.2.1.1.min.js"></script>

<script>
    $('#btnAddOrUpdateBuilding').click(function (e) {
        e.preventDefault();

        var data = {};
        var typeCode = [];
        var formData = $('#listForm').serializeArray();
        $.each(formData, function (i, v) {
            if (v.name != 'typeCode') {
                data["" + v.name + ""] = v.value;
            } else {
                typeCode.push(v.value);
            }
        })
        data['typeCode'] = typeCode;

        if (typeCode.length !== 0) {
            addOrUpdateBuilding(data);
        } else {
            window.location.href = '/admin/building-edit?typeCode=required';
        }
    });

    function addOrUpdateBuilding(data) {
        $.ajax({
            type: "POST",
            url: "${buildingAPI}",
            data: JSON.stringify(data),
            contentType: "application/json",
            dataType: "JSON",
            success: function (respond) {
                window.location.href = "/admin/building-edit-" + respond.id + "?message=update_success";
            },
            error: function (respond) {
                var message = respond.responseJSON && respond.responseJSON.message
                    ? respond.responseJSON.message
                    : "Không thể lưu tòa nhà.";
                $('.page-header').after('<div class="alert alert-danger" role="alert"></div>');
                $('.alert-danger').text(message);
            }
        })
    }

    $('#btnCancel').click(function () {
        window.location.href = "/admin/building-list";
    });
</script>
</body>
</html>
