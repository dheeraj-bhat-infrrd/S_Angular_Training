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
<div id="temp-message" class="hide"></div>
<script>
    $(document).ready(function() {
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

        // remove user
        $(document).on('click', '.v-icn-rem-user', function(){
        	if ($(this).hasClass('v-tbl-icn-disabled')) {
        		return;
        	}

        	var userId = $(this).parent().find('.fetch-name').attr('data-user-id');
            var adminId = '${user.userId}';
            confirmDeleteUser(userId, adminId);
        });

        // resend verification mail
        $(document).on('click', '.v-icn-fmail', function(){
        	if ($(this).hasClass('v-tbl-icn-disabled')) {
        		return;
        	}

        	var firstName = $(this).parent().find('.fetch-name').attr('data-first-name');
            var lastName = $(this).parent().find('.fetch-name').attr('data-last-name');
            var emailId = $(this).parent().find('.fetch-email').html();
            reinviteUser(firstName, lastName, emailId);
        });

        // de-activate user profile
        $(document).on('click', '.tbl-switch-on', function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 0);
        });

        // activate user profile
        $(document).on('click', '.tbl-switch-off', function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 1);
        });
    });
</script>

</body>
</html>