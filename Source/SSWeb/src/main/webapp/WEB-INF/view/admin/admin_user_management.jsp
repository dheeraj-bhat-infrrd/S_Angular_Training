<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.usermanagement.header.key" /></div>
					<div class="float-right hm-header-right text-center" onclick="javascript:showMainContent('./showaddsocialsurveyadmin.do');">Add Social Survey Admin</div>
			
		</div>
	</div>
</div>
&nbsp;

<div class="container v-hr-container">
	<div id="ss-admin-list-wrapper"></div>
</div>

<div id="temp-message" class="hide"></div>
<script>
$(document).ready(function() {
	getSocialSurveyAdminList();
});
</script>