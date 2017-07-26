<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
.dash-lp-txt{
	    font-size: 15px;
}

</style>
<div id="dash-survey-incomplete" class="dash-panel-left col-lg-4 col-md-4 col-sm-4 col-xs-12" style="margin-left: 30px; width: 100%; padding-top:0; margin-top: -10px; margin-bottom: 20px;">
	<div class="dash-lp-header clearfix" id="incomplete-survey-header">
		<div class="float-left" style="font-size: 20px;"><spring:message code="label.incompletesurveys.key" /></div>
		<div class="float-right dash-sur-link" onclick="showIncompleteSurveyListPopup(event)" style="font-size: 15pxpx;">View All</div>
	</div>
	<div id="dsh-inc-srvey" class="dash-lp-item-grp clearfix" data-total="0" style="text-align:left">
		<!-- Populated with dashboard_incompletesurveys.jsp -->
	</div>
	<%-- <div id="dsh-inc-dwnld" class="dash-btn-sur-data hide"><spring:message code="label.incompletesurveydata.key" /></div> --%>
</div>
<script>
$(document).ready(function() {
	
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');
	
	var scrollContainer = document.getElementById('dsh-inc-srvey');
	scrollContainer.onscroll = function() {
		if (scrollContainer.scrollTop >= ((scrollContainer.scrollHeight * 0.75) - scrollContainer.clientHeight)) {
			if(!doStopIncompleteSurveyPostAjaxRequest || $('#dsh-inc-srvey>div.dsh-icn-sur-item.hide').length > 0) {
					fetchIncompleteSurvey(false);
					$('#dsh-inc-srvey').perfectScrollbar('update');
			}
		}
	};
	
	getIncompleteSurveyCount(currentProfileName, currentProfileValue);
});
</script>