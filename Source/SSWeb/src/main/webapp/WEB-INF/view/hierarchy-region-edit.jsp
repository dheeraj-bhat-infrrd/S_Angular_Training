<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="edit-region-form" class="edit-region-form">
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.regionname.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" name="regionName" id="region-name-txt" value="${region.regionName}">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.addressline1.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" name="regionAddress1" id="region-address1-txt" value="${region.address1}">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.addressline2.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" id="region-address2-txt" name="regionAddress2" value="${region.address2}">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.country.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" id="region-country" name="regionCountry" value="${region.country}">
	         <input type="hidden" value="${region.countryCode}" name="regionCountrycode" id="region-country-code">
	     </div>
	 </div>
	 <div id="region-state-city-row" class="hide">
		 <div class="bd-hr-form-item clearfix">
		     <div class="float-left bd-frm-left"><spring:message code="label.state.key"/></div>
		     <div class="float-left bd-frm-right">
		         <input class="bd-frm-rt-txt" id="region-state-txt" name="regionState" value="${region.state}">
		     </div>
		 </div>
		 <div class="bd-hr-form-item clearfix">
		     <div class="float-left bd-frm-left"><spring:message code="label.city.key"/></div>
		     <div class="float-left bd-frm-right">
		         <input class="bd-frm-rt-txt" id="region-city-txt" name="regionCity" value="${region.city}">
		     </div>
		 </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.zipcode.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" id="region-zipcode-txt" name="regionZipcode" value="${region.zipcode}">
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
	     <div class="float-left bd-frm-left"><spring:message code="label.chooseuserforregion.key"/></div>
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
	             <input type="hidden" name="isAdmin" value="true" id="false" class="ignore-clear">
	             <div class="float-left bd-check-txt"><spring:message code="label.grantadminprivileges.key"/></div>
	         </div>
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"></div>
	     <div class="float-left bd-frm-right">
	     <c:choose>
		     <c:when test="${isUpdateCall}">
		     	 <input type="hidden" name="regionId" value="${region.regionId}" class="ignore-clear"/>
		     	 <div id="btn-region-update" data-regionid = "${region.regionId}" class="bd-btn-save cursor-pointer"><spring:message code="label.save.key"/></div>
		     </c:when>
		     <c:otherwise>
		      	 <div id="btn-region-save" class="bd-btn-save cursor-pointer"><spring:message code="label.save.key"/></div>
		     </c:otherwise>
	     </c:choose>
	     </div>
	 </div>
 </form>
 <script>
 $(document).ready(function(){
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
	 $("#region-country").autocomplete({
			minLength: 1,
			source: countryData,
			delay : 0,
			open : function(event, ui) {
				$( "#region-country-code" ).val("");
			},
			focus: function(event, ui) {
				$( "#region-country" ).val(ui.item.label);
				return false;
			},
			select: function(event, ui) {
				$("#region-country").val(ui.item.label);
				$("#region-country-code").val(ui.item.code);
				for (var i = 0; i < postCodeRegex.length; i++) {
					if (postCodeRegex[i].code == ui.item.code) {
						selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
						selectedCountryRegEx = new RegExp(selectedCountryRegEx);
						break;
					}
				}
				if(ui.item.code=="US"){
					$('#region-state-city-row').show();
					/* callAjaxGET("", function(data){
						
					}, true); */
				}else{
					$('#region-state-city-row').hide();
					$('#region-state-city-row input').val('');
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
});
</script>