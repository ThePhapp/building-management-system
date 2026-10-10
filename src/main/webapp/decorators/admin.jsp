<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="_csrf" content="<c:out value='${_csrf.token}'/>"/>
    <meta name="_csrf_header" content="<c:out value='${_csrf.headerName}'/>"/>
    <title><dec:title default="Tổng quan"/> | Building Manager</title>
    <dec:head/>

    <link rel="stylesheet" href="<c:url value='/admin/assets/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/admin/font-awesome/4.5.0/css/font-awesome.min.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/admin/assets/css/ace.min.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/admin/assets/sweetalert2/sweetalert2.min.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/admin/css/app.css'/>"/>

    <script src="<c:url value='/admin/js/2.1.4/jquery.min.js'/>"></script>
</head>
<body class="bms-app no-skin">
<%@ include file="/common/admin/header.jsp" %>

<div class="main-container" id="main-container">
    <%@ include file="/common/admin/menu.jsp" %>
    <button type="button" class="app-sidebar-backdrop" id="appSidebarBackdrop"
            aria-label="Đóng menu" tabindex="-1"></button>

    <dec:body/>

    <%@ include file="/common/admin/footer.jsp" %>
</div>

<section class="app-toast-region" aria-live="polite" aria-atomic="true"></section>

<script src="<c:url value='/admin/assets/js/bootstrap.min.js'/>"></script>
<script src="<c:url value='/admin/assets/js/jquery.validate.min.js'/>"></script>
<script src="<c:url value='/admin/assets/sweetalert2/sweetalert2.min.js'/>"></script>
<script src="<c:url value='/admin/assets/js/ace.min.js'/>"></script>
<script src="<c:url value='/admin/js/global_admin_script.js'/>"></script>
<script src="<c:url value='/admin/js/app-shell.js'/>"></script>

<script>
    function showAlertBeforeDelete(callback) {
        swal({
            title: 'Xác nhận thao tác',
            text: 'Bạn có chắc muốn thực hiện với các dòng đã chọn?',
            type: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Xác nhận',
            cancelButtonText: 'Hủy'
        }).then(function (result) {
            if (result.value) {
                callback();
            }
        });
    }
</script>
</body>
</html>
