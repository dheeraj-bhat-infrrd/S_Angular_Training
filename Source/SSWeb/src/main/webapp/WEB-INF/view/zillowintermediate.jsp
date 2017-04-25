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

<div class=" padding-001 ">
	<div class="container login-container">
		<div class="row login-row">
			
			<div class=" padding-001 margin-top-25 margin-bottom-25 bg-fff margin-0-auto col-xs-12 col-md-12 col-sm-12 col-lg-12">
			<div class="text-center font-24">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
							<div>
								<form id="zillowForm">
									<input type="hidden" id="profileType" value="${profile.vertical}" ></input>
									<div id="main-container">
			 							<div class="zillow-input-container clearfix popupUrl">
											<label class="zillow-input-label"><spring:message code="label.zillowconnect.key"/></label>
											<div class="zillow-input-cont">
												<c:choose>
													  <c:when test="${profile.vertical == 'Mortgage'}">
													    <span>
														    <spring:message code="label.zillowconnect.nmls.header.key" />
															<input class="zillow-input" name="nmlsId" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.nmls.key"/>' value = "${ profile.socialMediaTokens.zillowToken.lenderRef.nmlsId }">
														</span>
													  </c:when>
													  <c:otherwise>
													  	<div class="zillow-example-cont popupUrl">
															<div class="zillow-exm-url">
																<span class="zillow-url"><spring:message code="label.zillow.exampleurl.key" /></span>
															</div>
															<div class="zillow-exm-profile">
																<span><spring:message code="label.zillow.exampleprofilename.text.key" /></span> 
																<span class="zillow-exm-profilename"><spring:message code="label.zillow.exampleprofilename.key" /></span>
															</div>
														</div>
													  
													  
													  	<span><spring:message code="label.zillowconnect.link.key"/></span>
														<input class="zillow-input zillowProfileName" name="zillowProfileName" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">
														<span>/</span>
													  </c:otherwise>
												</c:choose>
											</div>
										</div>
									</div>
									<div id="screen-name-found-container">
										<div class="zillow-input-container clearfix popupUrl">
											<div>
												<span><spring:message code="label.zillowconnect.screen.name.found.key"/></span>
											</div>
											<div class="zillow-exm-profile">
												<spring:message code="label.zillowconnect.link.key" /><span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span>
											</div>
										</div>
									</div>
									<div id="no-screen-name-container">
										<div class="zillow-input-container clearfix popupUrl">
											<div>
												<span><spring:message code="label.zillowconnect.no.screen.name.key"/></span>
												<!-- <span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span> -->
											</div>
											
											<div>
												<span><spring:message code="label.zillowconnect.screen.name.key"/></span>											
												<input class="zillow-input zillowProfileName" name="zillowProfileNameNoScreenForNMLS" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">												
											</div>							
											
											<div class="zillow-exm-profile">
												<span><spring:message code="label.zillow.exampleprofilename.sample.key" /></span> <br/>
												<span class="zillow-exm-profilename">
													<spring:message code="label.zillowconnect.link.key" /><span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span>
												</span>
											</div>
											<div>* You can find your screen name while logged into Zillow by hovering over "My Zillow" and clicking the "Profile" button. 
												Once you're on your profile, click on "Edit your profile" then you will be directed to the page where you can view and edit your "Screen name."
											</div>
										</div>
									</div>
									<div id="by-screen-name-container">
										<div class="zillow-input-container clearfix popupUrl">
											<div>
												<span><spring:message code="label.zillowconnect.by.screen.name.key"/></span>
											</div>
											
											<div>
												<span><spring:message code="label.zillowconnect.screen.name.key"/></span>											
												<input class="zillow-input zillowProfileName" name="zillowProfileNameForNmls" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">												
											</div>				
											
											<div class="zillow-exm-profile">
												<span><spring:message code="label.zillow.exampleprofilename.sample.key" /></span> <br/>
												<span class="zillow-exm-profilename">
													<spring:message code="label.zillowconnect.link.key" /><span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span>
												</span>
											</div>
											<div>* You can find your screen name while logged into Zillow by hovering over "My Zillow" and clicking the "Profile" button. 
												Once you're on your profile, click on "Edit your profile" then you will be directed to the page where you can view and edit your "Screen name."
											</div>
										</div>
									</div>
								</form>
								
							</div>
						</div>
					</div>
					<div style="font-size: 11px; text-align: center;"></div>
				</div>
				
			</div>
		</div>
	</div>
</div>
