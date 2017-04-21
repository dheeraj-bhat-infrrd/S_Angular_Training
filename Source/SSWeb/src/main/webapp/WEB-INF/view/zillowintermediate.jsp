<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty accountSettings}">
	<c:set var = "profile" value = "${ accountSettings }"></c:set>
</c:if>
<c:if test="${empty accountSettings}">
	<c:if test="${ not empty profileSettings }">
		<c:set var = "profile" value = "${ profileSettings }"></c:set>
	</c:if>
</c:if>

${ profile }

<div class=" padding-001 ">
	<div class="container login-container">
		<div class="row login-row">
			
			<div class=" padding-001 margin-top-25 margin-bottom-25 bg-fff margin-0-auto col-xs-12 col-md-10 col-sm-12 col-lg-8">
			<div class="text-center font-24">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
						<div>
							<form id="zillowForm">
	 							<div class="zillow-input-container clearfix popupUrl">
									<label class="zillow-input-label"><spring:message code="label.zillowconnect.key"/></label>
									<div class="zillow-input-cont">
										<c:choose>
											  <c:when test="${profile.vertical == 'Mortgage'}">
											    
											  </c:when>
											  <c:otherwise>
											  	<span><spring:message code="label.zillowconnect.link.key"/></span>
												<input class="zillow-input" name="zillowProfileName" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">
												<span>/</span>
											  </c:otherwise>
										</c:choose>
									</div>
								</div>
							</form>
							<div class="zillow-example-cont popupUrl">
								<div class="zillow-exm-url">
									<span class="zillow-url"><spring:message code="label.zillow.exampleurl.key" /></span>
								</div>
								<div class="zillow-exm-profile">
									<span><spring:message code="label.zillow.exampleprofilename.text.key" /></span> 
									<span class="zillow-exm-profilename"><spring:message code="label.zillow.exampleprofilename.key" /></span>
								</div>
							</div>
						</div>
						</div>
					</div>
					<div style="font-size: 11px; text-align: center;"></div>
				</div>
				
			</div>
		</div>
	</div>
</div>