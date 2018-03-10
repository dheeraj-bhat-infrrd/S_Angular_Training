<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="columnName" value="${columnName}"></c:set>
<c:set var="columnValue" value="${columnValue}"></c:set>

<div id="overall-ratings-div" class="col-md-12 col-lg-12 col-sm-12 col-xs-12 score-stats-overall-con">
	<span class="score-stats-lbl">Overall Rating</span>
	<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12 score-stats-graph-con">
		<div id="overall-rating-chart" style="width: 100%; min-height: 300px"></div>
	</div>
</div>

<div id="question-ratings-div" class="col-md-12 col-lg-12 col-sm-12 col-xs-12 score-stats-ques-con">
	
</div>
<div id="empty-questions-div" class="hide">
	<div style="text-align: center; margin: 5% auto">
		<span class="incomplete-trans-span" style="font-size: large">Sorry!!!</span>
		<div style="clear: both">
			<span class="incomplete-trans-span" style="font-size: large">No Questions found for your account</span>
		</div>
	</div>
</div>