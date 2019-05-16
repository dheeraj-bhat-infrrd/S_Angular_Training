<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<c:set var="accountType" value="${user.company.licenseDetails[0].accountsMaster.accountName}" />
<c:set var="parentLock" value="${parentLock}"/>
<div id="prof-message-header" class="hide"></div>

<div class="v-um-edit-wrapper clearfix">
	<form id="user-assignment-form">
		<div class="v-edit-lft col-lg-6 col-md-6 col-sm-12 col-xs-12">
			<div class="v-ed-row v-ed-img-row">
				<c:choose>
					<c:when test="${not empty profilePicUrlThumbnail}">
						<c:set var="qEProfDefImgDisplay" value="display:none"/>
						<c:set var="qEProfImgDisplay" value=""/>
					</c:when>
					<c:otherwise>
						<c:set var="qEProfDefImgDisplay" value=""/>
						<c:set var="qEProfImgDisplay" value="display:none"/>
					</c:otherwise>
				</c:choose> 
				<div class="v-ed-lbl v-ed-img-lbl">Profile Photo</div>
		        <div class="v-ed-img-cont" id="selected-user-prof-img-cont">
		    		<img id="selected-user-prof-img" class="v-ed-img" src="${profilePicUrlThumbnail}" style="${qEProfImgDisplay}">
		    		<img id="qe-default-prof-img" class="v-ed-img" src="${initParam.resourcesPath}/resources/images/place-holder-individual.png" style="${qEProfDefImgDisplay}">
		        	<input type="file" id="v-ed-prof-image" class="hidden">
		        </div>
		        <div id="selected-user-prof-img-edit" class="edit-prof-img-icn v-ed-edit-icn v-ed-click-item"></div>
		    	<div id="selected-user-prof-img-del" class="prof-img-del-icn v-ed-del-icn v-ed-click-item"></div>
		    </div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code="label.um.username.key"/></div>
				<div class="v-ed-inp-cont col-lg-5 col-md-5 col-sm-5 col-xs-6">
					<input class="v-ed-txt-item" id="selected-user-first-name" placeholder="<spring:message code='label.um.firstname.key'/>" data-editable="true" data-value="${firstName}" value="${firstName}" >
				</div>
				<div class="v-ed-inp-cont col-lg-5 col-md-5 col-sm-5 col-xs-6">
					<input class="v-ed-txt-item" id="selected-user-last-name" placeholder="<spring:message code='label.um.lastname.key'/>" data-editable="true" data-value="${lastName}" value="${lastName}" >
				</div>
				<div class="v-ed-invalid-input">* First Name cannot be blank!</div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.emailid.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-email" name="selectedUserEmail" data-locked="${parentLock.isWorkEmailLocked}" data-editable="true" placeholder="<spring:message code='label.um.emailid.key'/>" data-value="${emailId}" value="${emailId}">
				</div>
				<div class="v-ed-invalid-input">* Enter a valid email address!</div>
				<input type="hidden" name="selectedUserId" id="selected-userid-hidden" value='${userId}'/>
				<c:set var="emailLockStatus" value=""/>
				<c:choose>
					<c:when test="${parentLock.isWorkEmailLocked == true}">
						<c:set var="emailLockStatus" value="v-ed-locked"></c:set>				
					</c:when>
					<c:when test="${parentLock.isWorkEmailLocked == false}">
						<c:set var="emailLockStatus" value="v-ed-unlocked"></c:set>				
					</c:when>
				</c:choose>
				<div class="v-ed-lock-icn ${emailLockStatus}"></div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.title.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-title" name="selectedUserTitle" data-editable="true" placeholder="<spring:message code='label.um.title.key'/>" data-value="${title}" value="${title}" >
				</div>
			</div>
			<div class="v-ed-row">
				<c:set var="qeProfileUrlExists" value=""/>
				<c:choose>
					<c:when test="${profileUrlExists ==  true}">
						<c:set var="qeProfileUrlExists" value="true"/>
					</c:when>
					<c:otherwise>
						<c:set var="qeProfileUrlExists" value="false"/>
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="qe-profile-exists" value="${qeProfileUrlExists}">

				<input type="hidden" id="originalUrl" data-value="${profileUrl}" value="${profileUrl}"/>
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.profileurl.key'/></div>
				<div id="v-ed-app-base-url" class="v-ed-prof-url-txt">${applicationBaseUrl}pages/</div>
				<div class="v-ed-prof-url-inp-cont">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-profileurl" name="selectedUserProfileUrl" data-editable="true" placeholder="<spring:message code='label.um.profileurl.key'/>" data-value="${profileUrl}" value="${profileUrl}" >
				</div>
				<div class="v-ed-invalid-input">* Enter a Profile url!</div>
				<div id="v-ed-profurl-open" class="v-ed-openlink-icn"></div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.address1.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-address1" name="selectedUserAdress1" data-editable="true" placeholder="<spring:message code='label.um.address1.placeholder.key'/>" data-value="${address1}" value="${address1}" >
				</div>
				<div class="v-ed-invalid-input">* Please enter an address!</div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.address2.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-address2" name="selectedUserAdress2" data-editable="true" placeholder="<spring:message code='label.um.address2.placeholder.key'/>" data-value="${address2}" value="${address2}" >
				</div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.country.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-country" name="selectedUserCountry" data-editable="true" placeholder="<spring:message code='label.um.country.placeholder.key'/>" data-value="${country}" value="${country}">
					<input type="hidden" id="selected-user-country-code" data-value="${countryCode}" value="${countryCode}">
				</div>
				<div class="v-ed-invalid-input"></div>
			</div>
			<div id="v-ed-state-city-row">
				<div class="v-ed-row">
					<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.state.key'/></div>
					<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12" data-value="${state}">
						<select class="v-ed-txt-item" id="selected-user-state" name="selectedUserState" data-value="${state}" disabled>
							<option disabled selected><spring:message code="label.select.state.key"/></option>
						</select>
					</div>
					<div class="v-ed-invalid-input">* Please select a state!</div>
				</div>
				<div class="v-ed-row">
					<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.city.key'/></div>
					<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
						<input class="v-ed-txt-item" autocomplete="off" id="selected-user-city" name="selectedUserCity" data-editable="true" placeholder="<spring:message code='label.um.city.placeholder.key'/>" data-value="${city}" value="${city}" >
					</div>
					<div class="v-ed-invalid-input">* Please enter a city!</div>
				</div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.zip.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-zip" name="selectedUserZip" data-editable="true" placeholder="<spring:message code='label.um.zip.placeholder.key'/>" data-value="${zipcode}" value="${zipcode}" >
				</div>
				<div class="v-ed-invalid-input">* Enter a valid Zip code!</div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.phone.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-phone" name="selectedUserPhone" data-locked="${parentLock.isWorkPhoneLocked}" data-editable="true" placeholder="<spring:message code='label.um.phone.placeholder.key'/>" data-value="${contactNumber}">
				</div>
				<div class="v-ed-invalid-input">* Enter a valid phone number!</div>
				<c:set var="phoneLockStatus" value=""/>
				<c:choose>
					<c:when test="${parentLock.isWorkPhoneLocked == true}">
						<c:set var="phoneLockStatus" value="v-ed-locked"></c:set>				
					</c:when>
					<c:when test="${parentLock.isWorkPhoneLocked == false}">
						<c:set var="phoneLockStatus" value="v-ed-unlocked"></c:set>				
					</c:when>
				</c:choose>
				<div class="v-ed-lock-icn ${phoneLockStatus}"></div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code='label.um.website.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
					<input class="v-ed-txt-item" autocomplete="off" id="selected-user-website" name="selectedUserWebsite" data-locked="${parentLock.isWebAddressLocked}" data-editable="true" placeholder="<spring:message code='label.um.website.placeholder.key'/>" data-value="${webUrl}" value="${webUrl}" >
				</div>
				<div id="v-ed-webadd-open" class="v-ed-openlink-icn" style="right: -40px"></div>
				<c:set var="websiteLockStatus" value=""/>
				<c:choose>
					<c:when test="${parentLock.isWebAddressLocked == true}">
						<c:set var="websiteLockStatus" value="v-ed-locked"></c:set>				
					</c:when>
					<c:when test="${parentLock.isWebAddressLocked == false}">
						<c:set var="websiteLockStatus" value="v-ed-unlocked"></c:set>				
					</c:when>
				</c:choose>
				<div class="v-ed-lock-icn ${websiteLockStatus}"></div>
			</div>
			
			<c:set value="${fn:escapeXml(aboutMe)}" var="aboutMeTxt"></c:set>
			<c:set value="${fn:escapeXml(disclaimer)}" var="disclaimerTxt"></c:set>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2 v-ed-txtarea-lbl"><spring:message code='label.um.aboutme.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
	            	<textarea class="v-ed-textarea" rows="5" placeholder="<spring:message code='label.um.aboutme.key'/>" id="selected-user-aboutme" name="selectedUserAboutMe" data-editable="true" data-value="${aboutMeTxt}">${aboutMeTxt}</textarea>
				</div>
			</div>
			<div class="v-ed-row">
				<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2 v-ed-txtarea-lbl"><spring:message code='label.um.disclaimer.key'/></div>
				<div class="v-ed-inp-cont col-lg-10 col-md-10 col-sm-10 col-xs-12">
	            	<textarea class="v-ed-textarea" rows="5" placeholder="<spring:message code='label.um.disclaimer.key'/>" id="selected-user-disclaimer" name="selectedUserDisclaimer" data-editable="true" data-value="${disclaimerTxt}">${disclaimerTxt}</textarea>
				</div>
			</div>
		</div>
		<div class="v-edit-rt col-lg-6 col-md-6 col-sm-12 col-xs-12">
			<div class="v-ed-row v-ed-img-row">
				<c:choose>
					<c:when test="${not empty logoThumbnail}">
						<c:set var="qELogoDefImgDisplay" value="display:none"/>
						<c:set var="qELogoDisplay" value=""/>
					</c:when>
					<c:otherwise>
						<c:set var="qELogoDefImgDisplay" value=""/>
						<c:set var="qELogoDisplay" value="display:none"/>
					</c:otherwise>
				</c:choose> 
			   	<div class="v-ed-lbl v-ed-img-lbl">Company Logo</div>
		      	<div class="v-ed-img-cont" id="selected-user-logo-cont" data-locked="${parentLock.isLogoLocked}">
		    		<img id="selected-user-logo" class="v-ed-img" src="${logoThumbnail}" style="${qELogoDisplay}">
		    		<img id="qe-default-user-logo" class="v-ed-img" src="${initParam.resourcesPath}/resources/images/place-holder-Company.png" style="${qELogoDefImgDisplay}">
		        	<input type="file" id="v-ed-prof-logo" class="hidden">
		        </div>
		        <c:set var="logoLockStatus" value=""/>
				<c:choose>
					<c:when test="${parentLock.isLogoLocked == true}">
						<c:set var="logoLockStatus" value="v-ed-locked"></c:set>				
					</c:when>
					<c:when test="${parentLock.isLogoLocked == false}">
						<c:set var="logoLockStatus" value="v-ed-unlocked"></c:set>				
					</c:when>
				</c:choose>
				<div class="v-ed-img-lock v-ed-lock-icn ${logoLockStatus}"></div>
		        <div id="selected-user-logo-edit" class="edit-prof-img-icn v-ed-edit-icn v-ed-click-item"></div>
		    	<div id="selected-user-logo-del" class="prof-img-del-icn v-ed-del-icn v-ed-click-item"></div>
		    </div>
			<div class="v-ed-row">
				<div class="v-ed-lbl">Minimum score to post on social networks</div>
			</div>
			<div class="v-ed-row v-ed-minrat-row">
				<div class="v-ed-minrat-star-cont" id="v-ed-minrat-star-cont">
					<div class="rating-image float-left  star-rating-0.00" title="0.0/5.0"></div>
   					<div class="v-ed-minrat-star-text">0.0</div>
				</div>
				<div class="v-ed-minrat-drop-cont">
					<input class="v-ed-dropdown-inp dd-arrow-dn v-ed-click-item" id="selected-user-autopost" data-value="${autoPostScore}" value="${autoPostScore}" readonly>
					<div class="v-ed-rat-dropdown" style="display:none;">
						<div class="v-ed-rat-drop-item" style="border-top: 1px solid #ececec;">5</div>
						<div class="v-ed-rat-drop-item">4.5</div>
						<div class="v-ed-rat-drop-item">4.0</div>
						<div class="v-ed-rat-drop-item">3.5</div>
						<div class="v-ed-rat-drop-item">3.0</div>
						<div class="v-ed-rat-drop-item">2.5</div>
						<div class="v-ed-rat-drop-item">2.0</div>
						<div class="v-ed-rat-drop-item">1.5</div>
						<div class="v-ed-rat-drop-item">1.0</div>
						<div class="v-ed-rat-drop-item">0.5</div>
					</div>
				</div>
			</div>
			<div class="v-ed-row">
				<c:set var="allowAutoPostClass" value=""></c:set>
				<c:choose>
					<c:when test="${allowAutoPost == true}">
						<c:set var="allowAutoPostClass" value="v-ed-checkbox-checked"></c:set>				
					</c:when>
					<c:when test="${allowAutoPost == false}">
						<c:set var="allowAutoPostClass" value="v-ed-checkbox-unchecked"></c:set>				
					</c:when>
				</c:choose>					
				<div class="v-ed-checkbox ${allowAutoPostClass} v-ed-click-item"></div>
				<input type="hidden" id="selected-user-autopost-enabled" class="v-ed-checkbox-inp" data-value="${allowAutoPost}" value="${allowAutoPost}">
				<div class="v-ed-row-txt">Allow user to autopost</div>
			</div>
			
			<input type="hidden" id="v-ed-ss-admin" value="${isRealTechOrSSAdmin}">
			<c:if test="${isRealTechOrSSAdmin == true or isRealTechOrSSAdmin == 'true'}">
				<div class="v-ed-row" style="margin-top:30px;">
					<div class="v-ed-lbl">Customized Feature Setting</div>
				</div>
			
				<div class="v-ed-row">
					<div class="v-ed-ss-admin-set ss-admin-comp-settings">
						<c:set var="hidePublicPageCheckClass" value=""></c:set>
						<c:choose>
							<c:when test="${hidePublicPage == true}">
								<c:set var="hidePublicPageCheckClass" value="v-ed-checkbox-checked"></c:set>				
							</c:when>
							<c:when test="${hidePublicPage == false}">
								<c:set var="hidePublicPageCheckClass" value="v-ed-checkbox-unchecked"></c:set>				
							</c:when>	
						</c:choose>
						<div id="v-ed-hide-pp-chk-box" class="float-left v-ed-checkbox ${hidePublicPageCheckClass} v-ed-click-item"></div>
						<input type="hidden" id="v-ed-hide-public-page" name="hidepublicpage" data-value="${hidePublicPage}" value="${hidePublicPage}" class="v-ed-checkbox-inp">
						<div class="float-left v-ed-row-txt">Hide public page</div>
						<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
					</div>	
				</div>

			</c:if>
			
				<c:if test="${partnerSurveyAllowedForCompany}">
					<div class="v-ed-row">
					<c:set var="partnerSurveyCheckClass" value=""></c:set>
						<c:choose>
							<c:when test="${partnerSurveyAllowedForUser == true}">
								<c:set var="partnerSurveyCheckClass" value="v-ed-checkbox-checked"></c:set>				
							</c:when>
							<c:when test="${partnerSurveyAllowedForUser == false}">
								<c:set var="partnerSurveyCheckClass" value="v-ed-checkbox-unchecked"></c:set>				
							</c:when>	
						</c:choose>
						<div id="v-ed-hide-ps-chk-box" class="float-left v-ed-checkbox ${partnerSurveyCheckClass} v-ed-click-item"></div>
						<input type="hidden" id="at-pst-cb" name="allowpartnersurveyuser" data-value="${partnerSurveyAllowedForUser}" value="${partnerSurveyAllowedForUser}" class="v-ed-checkbox-inp">
						<div class="float-left v-ed-row-txt">Allow partner survey</div>
					</div>
				</c:if>				
			
			<c:if test="${isSocialMonitorEnabled}">
				<div class="v-ed-row">
					<c:set var="smAdminCheckClass" value=""></c:set>
						<c:choose>
							<c:when test="${isSocialMonitorAdmin == true}">
								<c:set var="smAdminCheckClass" value="v-ed-checkbox-checked"></c:set>				
							</c:when>
							<c:when test="${isSocialMonitorAdmin == false}">
								<c:set var="smAdminCheckClass" value="v-ed-checkbox-unchecked"></c:set>				
							</c:when>	
						</c:choose>
						<div id="v-ed-hide-ps-chk-box" class="float-left v-ed-checkbox ${smAdminCheckClass} v-ed-click-item"></div>
						<input type="hidden" id="is-soc-mon-admin-chk" name="isSocialMonitorAdmin" data-value="${isSocialMonitorAdmin}" value="${isSocialMonitorAdmin}" class="v-ed-checkbox-inp">
						<div class="float-left v-ed-row-txt"><spring:message code="label.grantsocialmonitoradminprivileges.key" /></div>
					</div>
			</c:if>	
							
			<div id="profile-tbl-wrapper-${userId}" class="v-edt-tbl-wrapper">
				<table class="v-edt-tbl">
					<tr class="v-edt-tbl-header">
						<td class="v-edt-tbl-assign-to"><spring:message code="label.assignedto.key" /></td>
						<td class="v-edt-tbl-role"><spring:message code="label.role.key" /></td>
						<%-- <td class="v-edt-tbl-status"><spring:message code="label.status.key" /></td> --%>
						<td class="v-edt-tbl-rem"><spring:message code="label.action.key" /></td>
					</tr>
					
					<c:choose>
						<c:when test="${not empty profiles}">
							<c:forEach var="profile" items="${profiles}">
								<tr class="v-edt-tbl-row" id="v-edt-tbl-row-${profile.profileId}" data-profile-id="${profile.profileId}">
									<td class="v-edt-tbl-assign-to">${profile.entityName}</td>
									<td class="v-edt-tbl-role">${profile.role}</td>
									<%-- <c:choose>
										<c:when test="${profile.status == 1}">
											<td class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-on" title="<spring:message code="label.active.key" />"></td>
										</c:when>
										<c:otherwise>
											<td class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-off" title="<spring:message code="label.inactive.key" />"></td>
										</c:otherwise>
									</c:choose> --%>
									<td class="v-edt-tbl-rem v-edt-tbl-icn v-icn-rem-userprofile"></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr class="v-edt-tbl-row"><spring:message code="label.nouserprofilesfound.key" /></tr>
						</c:otherwise>
					</c:choose>
				</table>
			</div>
			<div id="user-assignment-cont" class="hide v-ed-assign-cont">
				<c:choose>
				    <c:when test="${accountType == 'Enterprise'}">
				    <div class="v-ed-row clearfix">
						<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code="label.assignto.key"/></div>
						<div id="assign-to-selector" class="float-left v-ed-txt pos-relative v-ed-margin-left10" data-profile="individual">
						<input id="assign-to-txt" data-assignto="office" class="v-ed-txt-item v-ed-txt-dd ignore-clear" value='<spring:message code="label.office.key"/>'>
						<div id="assign-to-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
								<div data-assign-to-option="office" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.office.key"/></div>
							<c:if test="${highestrole == 1 || highestrole == 2}">
								<div data-assign-to-option="region" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.region.key"/></div>
							</c:if>
							<c:if test="${highestrole == 1}">
								<div data-assign-to-option="company" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.company.key"/></div>
							</c:if>
						</div>
						</div>
					</div>
					</c:when>
					<c:when test="${accountType == 'Company'}">
					<div class="v-ed-row clearfix">
						<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code="label.assignto.key"/></div>
						<div id="assign-to-selector" class="float-left v-ed-txt pos-relative v-ed-margin-left10" data-profile="individual">
						<input id="assign-to-txt" data-assignto="office" class="v-ed-txt-item v-ed-txt-dd ignore-clear" value='<spring:message code="label.office.key"/>'>
						<div id="assign-to-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
								<div data-assign-to-option="office" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.office.key"/></div>
							<c:if test="${highestrole == 1}">
								<div data-assign-to-option="company" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.company.key"/></div>
							</c:if>
						</div>
						</div>
					</div>
					</c:when>
					<c:when test="${accountType == 'Team'}">
						<input id="assign-to-txt" data-assignto="company" class="v-ed-txt-item v-ed-txt-dd hide ignore-clear" value='<spring:message code="label.team.key"/>'>
						<div id="assign-to-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
							<c:if test="${highestrole == 1}">
								<div data-assign-to-option="company" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.team.key"/></div>
							</c:if>
						</div>
					</c:when>
				</c:choose>
				<div id="bd-region-selector" class="v-ed-row clearfix hide">
					<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code="label.selectregion.key"/></div>
					<div class="float-left v-ed-txt pos-relative v-ed-margin-left10" id="region-selector">
						<input id="selected-region-txt" class="v-ed-txt-item v-ed-txt-dd" placeholder='<spring:message code="label.regionselector.placeholder.key"/>'/>
						<input type="hidden" name="regionId" id="selected-region-id-hidden"/>
						<div id="regions-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
							<!-- regions list get populated here -->
						</div>
					</div>
				</div>
				<div id="bd-office-selector" class="v-ed-row clearfix">
					<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2"><spring:message code="label.selectoffice.key"/></div>
					<div class="float-left v-ed-txt pos-relative v-ed-margin-left10" id="office-selector">
						<input id="selected-office-txt" class="v-ed-txt-item v-ed-txt-dd" placeholder='<spring:message code="label.officeselector.placeholder.key"/>'/>
						<input type="hidden" name="officeId" id="selected-office-id-hidden"/>
						<div id="offices-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
							<!-- offices list get populated here -->
						</div>
					</div>
				</div>
				<c:if test="${accountType == 'Company' || accountType == 'Enterprise'}">
					<div class="v-ed-row clearfix" id="admin-privilege-div">
						<div class="v-ed-lbl col-lg-2 col-md-2 col-sm-2 col-xs-2" style="color: transparent;">'</div>
						<div class="float-left v-ed-txt pos-relative v-ed-margin-left10">
							<div class="bd-frm-check-wrapper clearfix">
								<div class="float-left bd-check-img bd-check-img-checked"></div>
								<input type="hidden" name="isAdmin" value="false" id="is-admin-chk">
				            	<div class="float-left bd-check-txt"><spring:message code="label.grantadminprivileges.key"/></div>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</form>
	
	<div id="user-edit-btn-row" class="user-edit-btn-row clearfix">
		<div class="user-edit-btn-col float-left">
			<div id="user-edit-btn" class="user-edit-btn float-right">Edit</div>
		</div>
		<div class="user-edit-btn-col float-left">
			<div id="user-assign-btn" class="user-edit-btn float-left">Assign</div>
		</div>
	</div>
	<div id="btn-save-user-assignment" class="user-edit-btn-row clearfix hide">
		<div class="user-edit-btn-col float-left">
			<div id="user-edit-save" class="user-edit-btn float-right">Save</div>
		</div>
		<div class="user-edit-btn-col float-left">
			<div id="user-edit-cancel" class="user-edit-btn float-left">Cancel</div>
		</div>
	</div>
	<%-- <div id="btn-save-user-assignment" class="v-edt-btn-sav hide"><spring:message code="label.savechanges.key" /></div> --%>
</div>

<script>
	$(document).ready(function(){
		
		var applicationBaseUrl = "${applicationBaseUrl}";
		initializeQuickEditsPage(applicationBaseUrl);
		
		initializeQuickEditsAutoPostScore();
		
		updateEventsQuickEditsAddress();
		
		var contactNumber  = "${contactNumber}";
		updateEventsQuickEditsPhone(contactNumber);
		
		disableQuickEdits();
	});
</script>