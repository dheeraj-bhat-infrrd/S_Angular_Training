<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.title.registeruser.key"/></title>
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
        
        $(document).on('click','.v-icn-edit-user',function(){
            if($(this).parent().hasClass('u-tbl-row-sel')){
                $(this).parent().removeClass('u-tbl-row-sel');
                $(this).parent().next('.u-tbl-row').hide();
            }else{
                $(this).parent().next('.u-tbl-row').show();
                $(this).parent().addClass('u-tbl-row-sel');
                // make an ajax call and fetch the details of the user
                var firstName = $(this).parent().find('.fetch-name').attr('data-first-name');
                var lastName = $(this).parent().find('.fetch-name').attr('data-last-name');
                var emailId = $(this).parent().find('.fetch-email').html();
                var userId = $(this).parent().find('.fetch-name').attr('data-user-id');
                getUserAssignments(firstName, lastName, emailId, userId);
            }
        });
        
        $(document).on('click','.v-icn-rem-user', function(){
        	var userId = $(this).parent().find('.fetch-name').attr('data-user-id');
        	alert(userId);
        	deleteUser(userId);
        });
        
        $(document).on('click','.v-icn-fmail', function(){
        	 var firstName = $(this).parent().find('.fetch-name').attr('data-first-name');
             var lastName = $(this).parent().find('.fetch-name').attr('data-last-name');
             var emailId = $(this).parent().find('.fetch-email').html();
             reinviteUser(firstName, lastName, emailId);
        });
        
    });
</script>

</body>
</html>