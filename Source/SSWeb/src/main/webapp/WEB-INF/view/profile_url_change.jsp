<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>



<div class="padding-001">
	<div class="container login-container">
		<div class="row login-row">
			<div class=" padding-001 margin-top-25 margin-bottom-25 bg-fff margin-0-auto col-xs-12 col-md-10 col-sm-12 col-lg-8">
				
				<div class="text-center font-24 margin-bot-20">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
						<div>
						<form id="profileUrlEditForm" action="/profileUrlSaveInfo.do" method="post" class="profileForm"  >			
 							<div class="url-input-container clearfix popupUrl" >
								<label ><spring:message code="label.profileurlchange.key"/></label>
								<div>
									<span>${profileBaseUrl}</span>
									<div><input class="profile-url-input" name="profileUrlBlock" type="text" autofocus="autofocus" value = "${profileSettings.getProfileName()}">
									<span>/</span></div>
								</div>
							</div>
							<div class="clearfix urlPopupButton" >
						<div style="width: 335px;margin:auto">
						<div class="float-left"><div class="profile-url-sub-btn" onclick="saveProfileUrl()"><spring:message code="label.submit.key"/></div></div>
						<div class="float-left"><div class="profile-url-sub-btn" onclick="hideActiveUserLogoutOverlay();" style="margin-left: 10px;"><spring:message code="label.cancel.key"/></div></div>
						</div>
							</div>
						</form>
						</div>
						</div>
					</div>
					<div style="font-size: 11px; text-align: center;"></div>
				</div>
				<div class="footer-copyright text-center">
					<spring:message code="label.copyright.key" />&copy;
					<spring:message code="label.footer.socialsurvey.key" /><span class="center-dot">.</span>
					<spring:message code="label.allrightscopyright.key" />
				</div>
			</div>
		</div>
	</div>
</div>

<%-- <script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script type="${initParam.resourcesPath}/resources/js/common.js"></script> --%>
<script>





/* $(window).on('unload',
	function() {
			var parentWindow = null;
			if (window.opener != null && !window.opener.closed) {
				parentWindow = window.opener;
			}
	}
); */
</script>


