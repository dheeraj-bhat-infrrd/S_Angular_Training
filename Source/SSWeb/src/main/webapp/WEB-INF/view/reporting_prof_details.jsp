<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<style>
.rep-rank-rect {
	position: absolute;
    width: 180px;
    height: 90px;
    border: 7px solid #4983ca;
    background: #4983ca;
    margin-top: 20px;
    border-radius: 38px;
    padding-top: 8px;
    padding-left: 10px;
}

.rep-prof-pic-circle {
	position: absolute;
    width: 130px;
    height: 130px;
    border: 7px solid #4983ca;
    border-radius: 50%;
    margin-left: 123px;
    background: #fff;
}

.rep-rank-rect #rep-rank span, .rep-rank-rect #rep-user-score span{
	color:white;
	font-size:smaller;
}

.rep-prof-pic {
	border-radius: 50%;
	width: 100%;
    height: 100%;
}

.rep-send-survey-div{
	margin-left: -20px;
	border: 1px solid #dcdcdc;
	border-radius: 10px;
	text-align: center;
    padding-top: 10px;
    height: 130px;
}

.rep-social-media-div{
	margin-left: 10px;
	border: 1px solid #dcdcdc;
	border-radius: 10px;
	text-align: center;
    padding-top: 10px;
    height: 130px;
}

.rep-dash-btn-wrapper{
	height: 35px !important;
	width: 200px !important;
}

.rep-dash-btn{
	height: 35px !important;
    line-height: 35px !important;
    width: 200px !important;
}
</style>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.vertical}" var="companyvertical"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
</c:if>
<c:choose>
	<c:when test="${entityType == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>
<c:if test="${not empty cannonicalusersettings && not empty cannonicalusersettings.companySettings && not empty cannonicalusersettings.companySettings.vertical}">
	<c:set value="${cannonicalusersettings.companySettings.vertical}" var="verticalVal"></c:set>
</c:if>

<c:if test="${(highestrole != 1 && highestrole != 2 && highestrole != 3)}">
<div id="rep-user-details" class="col-sm-6 col-lg-6 col-md-6 col-xs-6">
	<div id="rep-rank-prof-pic" class="col-lg-6 col-md-6 col-sm-6 col-xs-6" style="height: 150px;">
		<div class="rep-rank-rect" style="display:inline-grid;">
			<div id="rep-rank" style="display:inline-flex; margin-top: 5px;">
				<span>Rank#</span>
				<span style="font-size: 23px;font-weight: bold !important;margin-left: 8px;line-height: 15px;">NA</span>
			</div>
			<div id="rep-user-score" style="display:inline-flex;">
				<span>Userscore</span>
				<span style="font-size: 23px;font-weight: bold !important;margin-left: 8px;line-height: 15px;">NA</span>
			</div>
		</div>
		<div class="rep-prof-pic-circle">
			<jsp:include page="reporting_profileimage.jsp"></jsp:include>
		</div>
	</div>
	<div id="rep-user-info" class="col-lg-6 col-md-6 col-sm-6 col-xs-6" style="margin-top: 15px;">
		<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
			<div id="name" class="prof-name prof-name-txt rep-dsh-large-text dsh-txt-1">${contactdetail.name}</div>
		</div>
		<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
			<div id="designation-nmls" class="prof-addline2 prof-name-txt rep-dsh-medium-text dsh-txt-2" >${contactdetail.title}
					| ${verticalVal}
			</div>
		</div>
		<div id="prof-rating-review-count" class="prof-rating clearfix" style="margin-top:30px">
			<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
			<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"></div>
		</div>
	</div>
</div>
</c:if>

<c:if test="${(highestrole == 1 || highestrole == 2 || highestrole == 3)}">
<div id="rep-user-details" class="col-sm-6 col-lg-6 col-md-6 col-xs-6" style="margin-left:-75px">
	<div id="rep-rank-prof-pic" class="col-lg-6 col-md-6 col-sm-6 col-xs-6" style="height: 150px;">
		<div class="rep-prof-pic-circle">
			<jsp:include page="reporting_profileimage.jsp"></jsp:include>
		</div>
	</div>
	<div id="rep-user-info" class="col-lg-6 col-md-6 col-sm-6 col-xs-6" style="margin-top: 15px;">
		<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
			<div id="name" class="prof-name prof-name-txt rep-dsh-large-text dsh-txt-1">${contactdetail.name}</div>
		</div>
		<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
			<div id="designation-nmls" class="prof-addline2 prof-name-txt rep-dsh-medium-text dsh-txt-2" >${contactdetail.title}
					| ${verticalVal}
			</div>
		</div>
		<div id="prof-rating-review-count" class="prof-rating clearfix" style="margin-top:30px">
			<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
			<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"></div>
		</div>
	</div>
</div>
</c:if>

<div id="rep-dash-btns" class="col-sm-6 col-lg-6 col-md-6 col-xs-6">
	<div id="rep-send-survey" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-send-survey-div" >
		<span>Build your online reputation by surveying each and every customer</span>
		<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix rep-dash-btn-wrapper" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}" style="">
			<div id="dsh-btn1" class="dsh-btn-complete float-left rep-dash-btn "><spring:message code="label.sendsurvey.btn.key" /></div>
		</div>	
	</div>
	<div id="rep-social-media" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-social-media-div">
		<span>Extend your social reach by connecting to all your social media accounts.</span>
		<div id="rep-pro-cmplt-stars" class="dsh-star-wrapper clearfix rep-dash-btn-wrapper" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}">
			<div id="dsh-btn0" class="dsh-btn-complete dsh-btn-red float-left  <c:if test="${not isSocialMediaExpired}">hide</c:if> ">Reconnect Social Media</div>
			<div id="dsh-btn2" class="dsh-btn-complete dsh-btn-orange float-left hide rep-dash-btn"></div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function(){
		$('#rep-pro-cmplt-stars').on('click', '#dsh-btn2', function(e) {
			e.stopPropagation();
			var buttonId = 'dsh-btn2';
			var task = $('#dsh-btn2').data('social');
			dashboardButtonAction(buttonId, task, colName, colValue);
		});
		
		$('#rep-pro-cmplt-stars').on('click', '#dsh-btn3', function(e) {
			e.stopPropagation();
			var buttonId = 'dsh-btn3';
			var task = $('#dsh-btn3').data('social');
			dashboardButtonAction(buttonId, task, colName, colValue);
		});
		
		
		
		$('#rep-pro-cmplt-stars').on('click', '#dsh-btn0', function(e) {
			e.stopPropagation();
			var buttonId = 'dsh-btn0';
			// getSocialMediaToFix
			var payload = {
					"columnName" : colName,
					"columnValue" : colValue
				};
				callAjaxGetWithPayloadData('./socialmediatofix.do', paintFixSocialMedia, payload, true);
		});
		
		$(document).on('click','#prof-company-review-count',function(e){
			e.stopPropagation();
			activaTab('reviews-tab');
		});
	});
</script>