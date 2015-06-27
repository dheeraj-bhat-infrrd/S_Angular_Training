<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
</head>

<body>
	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="float-left hm-header-row-left text-center">
					<spring:message code="label.header.usermanagement.key" />
				</div>
				<div class="float-right hm-header-right text-center"
					onclick="javascript:showMainContent('./viewhierarchy.do');">
					<spring:message code="label.viewcompanyhierachy.key" />
				</div>
				<div class="float-right hm-header-right text-center"
					onclick="javascript:showMainContent('./showusermangementpage.do')">
					<spring:message code="label.header.editteam.key" />
				</div>
				<div class="float-right hm-header-right text-center"
					onclick="javascript:showMainContent('./showbuildhierarchypage.do')">
					<spring:message code="label.header.buildhierarchy.key" />
				</div>
			</div>
		</div>
	</div>

	<div class="container v-um-container">
		<div class="v-um-header clearfix">
			<div class="v-um-hdr-left float-left"><spring:message code="label.usermanagement.head.browseruser.key" /></div>
			<div class="v-um-hdr-right float-right">
				<input id="search-users-key" class="v-um-inp" placeholder="<spring:message code="label.searchuser.key" />">
				<input type="hidden" id="users-count" value="${usersCount}">
			</div>
		</div>
		<div class="v-um-tbl-wrapper" id="user-list">
			<!-- Fill in the user list jsp -->
		</div>
		<div style="width: 100px; margin: 0 auto;">
			<div id="page-previous" class="float-left paginate-button"><spring:message code="label.previous.key" /></div>
			<div id="page-next" class="float-right paginate-button"><spring:message code="label.next.key" /></div>
		</div>
	</div>
	<div id="temp-message" class="hide"></div>
<script>
$(document).ready(function() {
	hideOverlay();
    $(document).attr("title", "User Management");
    initUserManagementPage();		

    doStopAjaxRequestForUsersList = false;
    if($('#server-message>div').hasClass("error-message")){
        isUserManagementAuthorized = false;
        $('#server-message').show();
        //var errorMessage = $('#server-message p').text();
    }

    $(document).on('click', '.v-tbl-icn', function(e){
        e.stopPropagation();
    });

    $(document).on('click', '.v-ed-txt-dd', function(){
        $(this).next('.v-ed-dd-wrapper').slideToggle(200);
    });

    $(document).on('click', '.v-ed-dd-item', function(e){
        e.stopPropagation();
        $(this).parent().prev('.v-ed-txt-dd').val($(this).html());
        $(this).parent().slideToggle(200);
    });
});
</script>
</body>
</html>