<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty regionSettings && not empty regionSettings.contact_details }">
	<c:set value="${regionSettings.contact_details}" var="contactDetails"></c:set>
</c:if>
<form id="edit-region-form" class="edit-region-form">
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.regionname.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" name="regionName" id="region-name-txt" value="${contactDetails.name}">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.addressline1.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" name="regionAddress1" id="region-address1-txt" value="${contactDetails.address1}">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.addressline2.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" id="region-address2-txt" name="regionAddress2" value="${contactDetails.address2}">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.country.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" id="region-country" name="regionCountry" value="${contactDetails.country}">
	         <input type="hidden" value="${contactDetails.countryCode}" name="regionCountrycode" id="region-country-code">
	     </div>
	 </div>
	 <div id="region-state-city-row" class="hide">
		 <div class="bd-hr-form-item clearfix">
		     <div class="float-left bd-frm-left"><spring:message code="label.state.key"/></div>
		     <div class="float-left bd-frm-right">
		         <select class="bd-frm-rt-txt" id="region-state-txt" name="regionState" data-value="${contactDetails.state}">
		         	<option disabled selected><spring:message code="label.select.state.key"/></option>
		         </select>
		     </div>
		 </div>
		 <div class="bd-hr-form-item clearfix">
		     <div class="float-left bd-frm-left"><spring:message code="label.city.key"/></div>
		     <div class="float-left bd-frm-right">
		         <input class="bd-frm-rt-txt" id="region-city-txt" name="regionCity" value="${contactDetails.city}">
		     </div>
		 </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.zipcode.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" id="region-zipcode-txt" name="regionZipcode" value="${contactDetails.zipcode}">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"></div>
	     <div class="float-left bd-frm-right manage-team-radio">
	         <div class="bd-frm-rad-wrapper clearfix">
	         	<input type="hidden" class="hide" name="userSelectionType">
	             <div class="float-left bd-cust-rad-item clearfix">
	                 <div data-type="single" class="float-left bd-cust-rad-img bd-cust-rad-img-checked"></div>
	                 <div class="float-left bd-cust-rad-txt"><spring:message code="label.addsingleuser.key"/></div>
	             </div>
	             <div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix">
	                 <div data-type="multiple" class="float-left bd-cust-rad-img"></div>
	                 <div class="float-left bd-cust-rad-txt"><spring:message code="label.addmultipleusers.key"/></div>
	             </div>
	         </div>
	     </div>
	 </div>
	 <input type="hidden" data-user-selection-type="single" id="user-selection-info"/>
	 <div id="bd-multiple" class="bd-hr-form-item clearfix hide">
	     <div class="float-left bd-frm-left"><spring:message code="label.addmultipleusers.key"/></div>
	     <div class="float-left bd-frm-right">
	         <textarea class="bd-frm-rt-txt-area" id="selected-user-txt-area" name="selectedUserEmailArray" placeholder='<spring:message code="label.addmultipleemailids.key"/>'></textarea>
	     </div>
	 </div>
	 <div id="bd-single" class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.chooseuserforregion.key"/></div>
	     <div class="float-left bd-frm-right pos-relative">
	         <input autocomplete="off" id="selected-user-txt" name="selectedUserEmail" class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img" placeholder='<spring:message code="label.userselector.placeholder.key"/>'/>
	         <input type="hidden" name="selectedUserId" id="selected-userid-hidden">
	         <div id="users-droplist" class="bd-frm-rt-dd-list dd-com-list ui-widget-content hide">
	         	<!-- list of users from solr gets displayed here   -->
	         </div>
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix" id="admin-privilege-div">
	     <div class="float-left bd-frm-left"></div>
	     <div class="float-left bd-frm-right">
	         <div class="bd-frm-check-wrapper clearfix bd-check-wrp">
	             <div class="float-left bd-check-img bd-check-img-checked"></div>
	             <input type="hidden" name="isAdmin" value="false" id="is-admin-chk" class="ignore-clear">
	             <div class="float-left bd-check-txt bd-check-sm"><spring:message code="label.grantadminprivileges.key"/></div>
	         </div>
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"></div>
	     <div class="float-left bd-frm-right">
	     <c:choose>
		     <c:when test="${isUpdateCall}">
		     	 <input type="hidden" name="regionId" value="${regionSettings.iden}" class="ignore-clear"/>
		     	 <div id="btn-region-update" data-regionid = "${regionSettings.iden}" class="bd-btn-save cursor-pointer"><spring:message code="label.save.key"/></div>
		     </c:when>
		     <c:otherwise>
		      	 <div id="btn-region-save" class="bd-btn-save cursor-pointer add-team-save"><spring:message code="label.save.key"/></div>
		     </c:otherwise>
	     </c:choose>
	     </div>
	 </div>
 </form>
 <script>
 $(document).ready(function() {
     attachAutocompleteCountry("region-country", "region-country-code", "region-state-txt", "region-state-city-row", "region-city-txt");
 });
</script>