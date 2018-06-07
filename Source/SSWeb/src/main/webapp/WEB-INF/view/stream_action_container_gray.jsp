<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="act-cont" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 float-left stream-action-container hide">
	<div class="action-icn icn-cont col-lg-1 col-md-1 col-sm-1 col-xs-1 float-left stream-action-icn hide">
		<img src="${initParam.resourcesPath}/resources/images/verified-gray.png" class="hide float-left action-res-icn">
		<img src="${initParam.resourcesPath}/resources/images/escalated-gray.png" class="hide float-left action-esc-icn">
		<img src="${initParam.resourcesPath}/resources/images/flag-gray.png" class="hide float-left action-flag-icn">
	</div>
	<div class="act-action-date act-details col-lg-11 col-md-11 col-sm-11 col-xs-11 float-right">
		<div class="act-details-date row stream-action-date">
						
		</div>
	</div>
	<div class="act-action act-details col-lg-11 col-md-11 col-sm-11 col-xs-11 float-left hide">
		<div class=" act-details-text row stream-action-text">
			
		</div>
	</div>
	<div class="act-details-msg-type col-lg-12 col-md-12 col-sm-12 col-xs-12 float-left hide"></div>
	<div class="action-msg-icn icn-cont col-lg-1 col-md-1 col-sm-1 col-xs-1 float-left stream-action-icn hide">
		<img src="${initParam.resourcesPath}/resources/images/email-18px.png" class="hide float-left action-mail-icn">
	</div>
	<div class="act-action-msg act-details col-lg-11 col-md-11 col-sm-11 col-xs-11 float-right hide">
		<div class="act-details-msg-text row">
		
		</div>
	</div>	
</div>
						