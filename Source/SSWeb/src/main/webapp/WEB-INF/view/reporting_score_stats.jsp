<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="columnName" value="${columnName}"></c:set>
<c:set var="columnValue" value="${columnValue}"></c:set>

<style>
	.rep-sps-lbl{
     	font-size: 20px;
    	position: absolute;
    	left: 0;
    	z-index: 1000;
    	float: left;
    	margin-top: -20px;
   		margin-left: 50px;
    	
     }
</style>

<div id="overall-ratings-div" class="col-md-12 col-lg-12 col-sm-12 col-xs-12" style="margin-top: 10px; display: inline-block; float:left; width:100%;height:350px; margin-left:15px">
	<span class="rep-sps-lbl" style="margin-top: 13px;">Overall Rating</span>
	<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"> 
		<div id="overall-rating-chart" style="width:80%; height:300px;margin: 20px 20px 20px 60px;"></div>
	</div>
</div>

<div id="question-ratings-div" class="col-md-12 col-lg-12 col-sm-12 col-xs-12" style="margin-top: 10px; float:left; width:100%; margin-left:15px">
	
</div>
<div id="empty-questions-div" class="hide">
	<div style="text-align: center; margin: 5% auto">
		<span class="incomplete-trans-span" style="font-size: large">Sorry!!!</span>
		<div style="clear: both">
			<span class="incomplete-trans-span" style="font-size: large">No Questions found for your account</span>
		</div>
	</div>
</div>