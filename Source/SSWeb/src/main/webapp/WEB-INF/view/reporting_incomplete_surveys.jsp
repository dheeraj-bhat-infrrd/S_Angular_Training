<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
.dash-lp-txt{
	    font-size: 15px;
}

</style>
<div id="rep-dash-survey-incomplete" class="dash-panel-left col-lg-4 col-md-4 col-sm-4 col-xs-12" style="width: 100%; padding-top:0; margin-top: -10px; margin-bottom: 20px;">
	<div class="dash-lp-header clearfix" id="incomplete-survey-header">
		<div class="float-left" style="font-size: 20px;"><spring:message code="label.incompletesurveys.key" /></div>
		<div class="float-right dash-sur-link" onclick="showIncompleteSurveyListPopup(event)" style="font-size: 15pxpx;"></div>
	</div>
	<div class="welcome-popup-body-wrapper clearfix">
		<div id="rep-icn-sur-popup-cont" data-start="0" data-total="0" data-batch="5" class="icn-sur-popup-cont"></div>
		<div class="mult-sur-icn-wrapper">
			<div id="rep-resend-mult-sur-icn" class="mult-sur-icn resend-mult-sur-icn float-left" title="Resend"></div>
			<div id="rep-del-mult-sur-icn" class="mult-sur-icn del-mult-sur-icn float-right" title="Delete"></div>
		</div>
	</div>
	<div class="paginate-buttons-survey clearfix">
		<div id="rep-sur-previous" class="float-left sur-paginate-btn">&lt; Prev</div>
		<div class="paginate-sel-box float-left">
			<input id="rep-sel-page" type="text" pattern="[0-9]*" class="sel-page"/>
			<span class="paginate-divider">/</span>
			<span id="rep-paginate-total-pages" class="paginate-total-pages"></span>
		</div>
		<div id="rep-sur-next" class="float-right sur-paginate-btn">Next &gt;</div>
	</div>
</div>
<script>
$(document).ready(function() {
	
	var currentProfileName = $('#rep-prof-container').attr('data-column-name');
	var currentProfileValue = $('#rep-prof-container').attr('data-column-value');
	
	getIncompleteSurveyCountForNewDashboard(currentProfileName, currentProfileValue);
	
	$('#rep-icn-sur-popup-cont').attr("data-start", 0);
	paintIncompleteSurveyListForNewDashboard(0,currentProfileName,currentProfileValue);
});
</script>