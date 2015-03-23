<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.title.registeruser.key"/></title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>

<body>
    <div class="hm-header-main-wrapper">
        <div class="container">
            <div class="hm-header-row hm-header-row-main clearfix">
                <div class="float-left hm-header-row-left text-center"><spring:message code="label.header.usermanagement.key" /></div>
            </div>
        </div>
    </div>

    <div class="container v-um-container">
        <div class="v-um-header clearfix">
            <div class="v-um-hdr-left float-left"><spring:message code="label.usermanagement.head.browseruser.key" /></div>
            <div class="v-um-hdr-right float-right">
                <input class="v-um-inp" placeholder="Search User" onkeyup="searchUsersByNameEmailLoginId(this.value)">
            </div>
        </div>
        <div class="v-um-tbl-wrapper" id="user-list">
        	<!-- Fill in the user list jsp -->
        </div>
    </div>


<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script>
    $(document).ready(function() {
    	$(document).ready(function(){
    		doStopAjaxRequestForUsersList = false;
    		if($('#server-message>div').hasClass("error-message")){
    			isUserManagementAuthorized = false;
    			$('#server-message').show();
    			//var errorMessage = $('#server-message p').text();
    		}
    		initUserManagementPage();		
    	});
        $(document).on('click','.v-tbl-icn',function(e){
            e.stopPropagation();
        });
        
        $(document).on('click','.v-ed-txt-dd',function(){
            $(this).next('.v-ed-dd-wrapper').slideToggle(200);
        });
        
        $(document).on('click','.v-ed-dd-item',function(e){
            e.stopPropagation();
            $(this).parent().prev('.v-ed-txt-dd').val($(this).html());
            $(this).parent().slideToggle(200);
        });
        
        $(document).on('click','.u-tbl-row',function(){
            if($(this).hasClass('u-tbl-row-sel')){
                $(this).removeClass('u-tbl-row-sel');
                $(this).next('.u-tbl-row').hide();
            }else{
                var editRow = $('<tr class="u-tbl-row u-tbl-row-sel">');
                $(this).after(editRow);
                $(this).addClass('u-tbl-row-sel');
            }
        });
        
    });
</script>

</body>
</html>