<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="clearfix um-panel-content">
	<div class="row">
		<div class="um-top-row cleafix">
			<div class="clearfix um-top-form-wrapper">
				<!-- set encompass details -->
				<c:if test="${accountSettings != null && accountSettings.crm_info != null}">
					<c:set var="dotloopapi"
						value="${accountSettings.crm_info.api}" />
				</c:if>
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
					<div class="hm-item-row item-row-OR clearfix float-left">
						<div class="um-item-row-left text-right">
							API
						</div>
						<div class="clearfix float-right st-username-icons">
							<div class="um-item-row-icon margin-left-0"></div>
							<div class="um-item-row-icon margin-left-0"></div>
						</div>
						<div
							class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
							<input id="encompass-username" type="text"
								class="um-item-row-txt um-item-row-txt-OR"
								placeholder="Api" name="dotloop-api"
								value="${dotloopapi}">
							<div id="dotloop-api" class="hm-item-err-2"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>