<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<c:set var="accountType" value="${user.company.licenseDetails[0].accountsMaster.accountName}" />
<c:if test="${not empty branchSettings && not empty branchSettings.organizationUnitSettings && not empty branchSettings.organizationUnitSettings.contact_details}">
	<c:set var="contactDetails" value="${branchSettings.organizationUnitSettings.contact_details}"></c:set>
</c:if>
<c:if test="${not empty branchSettings && not empty branchSettings.organizationUnitSettings}">
	<c:set var="branchUnitSettings" value="${branchSettings.organizationUnitSettings}"></c:set>
</c:if>
<form id="edit-office-form" class="edit-office-form">
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.officename.key"/></div>
    <div class="float-left bd-frm-right">
        <input class="bd-frm-rt-txt" name="officeName" id="office-name-txt" value="${contactDetails.name}">
    </div>
</div>
   <c:choose>
    <c:when test="${accountType == 'Enterprise'}">
    	<div id="bd-assign-to" class="bd-hr-form-item clearfix">
  				<div class="float-left bd-frm-left"><spring:message code="label.assignto.key"/></div>
   			<div id="assign-to-selector" class="float-left bd-frm-right pos-relative">
		    	<c:choose>
			    	<c:when test="${isCompanyBranch}">
			    		<input id="assign-to-txt" data-assignto="company" value='<spring:message code="label.company.key"/>' class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img ignore-clear"/>
			    	</c:when>
			    	<c:otherwise>
			    		<input id="assign-to-txt" data-assignto="region" value='<spring:message code="label.region.key"/>' class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img ignore-clear"/>
			    	</c:otherwise>
		    	</c:choose>
	    	
	        	<div id="assign-to-droplist" class="bd-frm-rt-dd-list dd-com-list hide">
	        	<c:if test="${highestrole == 1 || highestrole == 2}">
	            	<div data-assign-to-option="region" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-assignto-options"><spring:message code="label.region.key"/></div>
	            </c:if>
	            <c:if test="${highestrole == 1}">
	            	<div data-assign-to-option="company" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-assignto-options"><spring:message code="label.company.key"/></div>
	            </c:if>
	        	</div>
        	</div>
       	</div>
       	
    </c:when>
    <c:when test="${accountType == 'Company'}">
    	<input id="assign-to-txt" data-assignto="company" value='<spring:message code="label.company.key"/>' class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img hide ignore-clear"/>
       	<div id="assign-to-droplist" class="bd-frm-rt-dd-list dd-com-list hide">
           	<div data-assign-to-option="company" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-assignto-options"><spring:message code="label.company.key"/></div>
       	</div>
    </c:when>
   </c:choose>
<div id="bd-region-selector" class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.selectregion.key"/></div>
    <div class="float-left bd-frm-right pos-relative" id="region-selector">
        <input id="selected-region-txt" value="${branchSettings.regionName}" class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img" placeholder='<spring:message code="label.regionselector.placeholder.key"/>'/>
        <input type="text" class="hide" name="regionId" id="selected-region-id-hidden" value="${branchSettings.regionId}"/>
        <div id="regions-droplist" class="bd-frm-rt-dd-list dd-com-list hide">
        	<!-- regions list get populated here  -->
        </div>
    </div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.addressline1.key"/></div>
    <div class="float-left bd-frm-right">
        <input class="bd-frm-rt-txt" name="officeAddress1" id="office-address-txt" value="${contactDetails.address1}">
    </div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.addressline2.key"/></div>
    <div class="float-left bd-frm-right">
        <input class="bd-frm-rt-txt" name="officeAddress2" value="${contactDetails.address2}"/>
    </div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.country.key"/></div>
    <div class="float-left bd-frm-right">
        <input class="bd-frm-rt-txt" id="office-country" name="officeCountry" value="${contactDetails.country}"/>
        <input type="hidden" value="${contactDetails.countryCode}" name="officeCountrycode" id="office-country-code">
    </div>
</div>
<div id="office-state-city-row" class="hide">
	<div class="bd-hr-form-item clearfix">
	    <div class="float-left bd-frm-left"><spring:message code="label.state.key"/></div>
	    <div class="float-left bd-frm-right">
	        <select class="bd-frm-rt-txt" id="office-state-txt" name="officeState" data-value="${contactDetails.state}">
	        	<option disabled selected><spring:message code="label.select.state.key"/></option>
	        </select>
	    </div>
	</div>
	<div class="bd-hr-form-item clearfix">
	    <div class="float-left bd-frm-left"><spring:message code="label.city.key"/></div>
	    <div class="float-left bd-frm-right">
	        <input class="bd-frm-rt-txt text-capitalize" id="office-city-txt" name="officeCity" value="${contactDetails.city}"/>
	    </div>
	</div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.zipcode.key"/></div>
    <div class="float-left bd-frm-right">
        <input class="bd-frm-rt-txt" name="officeZipcode" value="${contactDetails.zipcode}"/>
    </div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"></div>
    <div class="float-left bd-frm-right">
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
    <div class="float-left bd-frm-left"><spring:message code="label.chooseuserforoffice.key"/></div>
    <div class="float-left bd-frm-right pos-relative">
        <input autocomplete="off" id="selected-user-txt" name="selectedUserEmail" class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img" placeholder='<spring:message code="label.userselector.placeholder.key"/>'/>
        <input type="hidden" name="selectedUserId" id="selected-userid-hidden">
        <div id="users-droplist" class="bd-frm-rt-dd-list dd-com-list hide">
            <!-- list of users from solr gets displayed here   -->
        </div>
    </div>
</div>
<div class="bd-hr-form-item clearfix" id="admin-privilege-div">
    <div class="float-left bd-frm-left"></div>
    <div class="float-left bd-frm-right">
        <div class="bd-frm-check-wrapper clearfix">
            <div class="float-left bd-check-img bd-check-img-checked"></div>
            <input type="hidden" name="isAdmin" value="false" id="is-admin-chk" class="ignore-clear">
            <div class="float-left bd-check-txt"><spring:message code="label.grantadminprivileges.key"/></div>
        </div>
    </div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"></div>
    <div class="float-left bd-frm-right">
    <c:choose>
	     <c:when test="${isUpdateCall}">
	     	 <input type="hidden" name="branchId" value="${branchUnitSettings.iden}" class="ignore-clear"/>
	     	 <div id="btn-office-update" data-branchid = "${branchUnitSettings.iden}" class="bd-btn-save cursor-pointer"><spring:message code="label.save.key"/></div>
	     </c:when>
	     <c:otherwise>
	      	 <div id="btn-office-save" class="bd-btn-save cursor-pointer"><spring:message code="label.save.key"/></div>
	     </c:otherwise>
     </c:choose>
    </div>
</div>
</form>
<script>
var selectedCountryRegEx = "";
$(document).ready(function(){
	var countryCode = "US";
	
	if($('#office-country-code').val() != undefined && $('#office-country-code').val() != ""){
		countryCode = $('#office-country-code').val();	 
	}
	if(countryCode == "US"){
		showStateCityRow("office-state-city-row", "office-state-txt");
		if( $('#office-country').val() == null || $('#office-country').val() == "" ){
			$('#office-country').val("United States");
			$('#office-country-code').val(countryCode);
		}
		selectedCountryRegEx = "^" + "\\b\\d{5}\\b(?:[- ]{1}\\d{4})?" + "$";
		selectedCountryRegEx = new RegExp(selectedCountryRegEx);
	}else{
		hideStateCityRow("office-state-city-row", "office-state-txt");
	}
	 
	$('.bd-check-img').click(function(e) {
		 $(this).toggleClass('bd-check-img-checked');
		/**
		 * If class is "bd-check-img-checked", check box is unchecked ,
		 * hence setting the hidden value as false
		 */
		 if($(this).hasClass('bd-check-img-checked') ){
			$(this).next("#is-admin-chk").val("false");
		 }
		 else {
			$(this).next("#is-admin-chk").val("true");
		 }
	 });
	$("#office-country").autocomplete({
		minLength: 1,
		source: countryData,
		delay : 0,
		autoFocus : true,
		open : function(event, ui) {
			$( "#office-country-code" ).val("");
		},
		select: function(event, ui) {
			$("#office-country").val(ui.item.label);
			$("#office-country-code").val(ui.item.code);
			for (var i = 0; i < postCodeRegex.length; i++) {
				if (postCodeRegex[i].code == ui.item.code) {
					selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
					selectedCountryRegEx = new RegExp(selectedCountryRegEx);
					break;
				}
			}
			if(ui.item.code=="US"){
				showStateCityRow("office-state-city-row", "office-state-txt");
			}else{
				hideStateCityRow("office-state-city-row", "office-state-txt");
			}
			return false;
		},
		close: function(event, ui) {},
		create: function(event, ui) {
	        $('.ui-helper-hidden-accessible').remove();
		}
	}).autocomplete("instance")._renderItem = function(ul, item) {
		return $("<li>").append(item.label).appendTo(ul);
	};
	/* $("#office-country").keydown(function(e){
  	    if( e.keyCode != $.ui.keyCode.TAB) return; 
  	    
   	   e.keyCode = $.ui.keyCode.DOWN;
   	   $(this).trigger(e);

   	   e.keyCode = $.ui.keyCode.ENTER;
   	   $(this).trigger(e);
   	});
 */	
	$('#office-state-txt').on('change',function(e){
  		var stateId = $(this).find(":selected").attr('data-stateid');
  		callAjaxGET("./getzipcodesbystateid.do?stateId="+stateId, function(data){
  			var uniqueSearchData = getUniqueCitySearchData(data);
  			initializeCityLookup(uniqueSearchData, "office-city-txt");
  		}, true);
  	});

  	$('#office-city-txt').bind('focus', function(){ 
  		if($('#office-state-txt').val() &&  $('#office-state-txt').val() != ""){
  			$(this).trigger('keydown');
  			$(this).autocomplete("search");		
  		}
  	});
	  	
});
</script>