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
		         <select class="bd-frm-rt-txt" id="region-state-txt" name="regionState" data-value="${region.state}">
		         	<option disabled selected><spring:message code="label.select.state.key"/></option>
		         </select>
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
	 var stateList;
	 var cityLookupList;
	 var countryCode = $('#region-country-code').val();
	 if(countryCode == "US"){
	 	showStateCityRow();
	 }else{
		 hideStateCityRow();
	 }
	 
	 $('.bd-check-img').click(function(e) {
		 $(this).toggleClass('bd-check-img-checked');
		/**
		 * If class is "bd-check-img-checked", check box is unchecked ,
		 * hence setting the hidden value as false
		 */
		 if($(this).hasClass('bd-check-img-checked') ){
			$(this).removeClass('bd-check-img-checked');
			$(this).next(".isAdmin").val("true");
		 }
		 else {
			$(this).addClass('bd-check-img-checked');
			$(this).next(".isAdmin").val("false");
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
				showStateCityRow();
			}else{
				hideStateCityRow();
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
  	$('#region-state-txt').on('change',function(e){
  		$('#region-city-txt').val('');
  		var stateId = $(this).find(":selected").attr('data-stateid');
  		callAjaxGET("./getzipcodesbystateid.do?stateId="+stateId, function(data){
  			cityLookupList = JSON.parse(data);
  			var searchData = [];
  			for(var i=0; i<cityLookupList.length; i++){
  				searchData[i] = cityLookupList[i].cityname;
  			}
  			
  			var uniqueSearchData = searchData.filter(function(itm,i,a){
  			    return i==a.indexOf(itm);
  			});
  			initializeCityLookup(uniqueSearchData);
  		}, true);
  	});

  	$('#region-city-txt').bind('focus', function(){ 
  		if($('#region-state-txt').val() &&  $('#region-state-txt').val() != ""){
  			$(this).trigger('keydown');
  			$(this).autocomplete("search");		
  		}
  	});
  	function initializeCityLookup(searchData){
  		$('#region-city-txt').autocomplete({
  			minLength : 0,
  			source : searchData,
  			focus : function(event, ui) {
  				event.stopPropagation();
  			},
  			select : function(event, ui) {
  				event.stopPropagation();
  			},
  			open : function() {
  				$('.ui-autocomplete').perfectScrollbar({
  					suppressScrollX : true
  				});
  				$('.ui-autocomplete').perfectScrollbar('update');
  			}
  		});
  		
  	}
  	function showStateCityRow() {
  		$('#region-state-city-row').show();
  		if(!stateList){
  			callAjaxGET("./getusstatelist.do", function(data){
  				stateList = JSON.parse(data);
  				for(var i=0; i<stateList.length; i++){
  					$('#region-state-txt').append('<option data-stateid='+stateList[i].id+'>'+stateList[i].statecode+'</option>');
  				}
  			}, true);
  			var stateVal = $('#region-state-txt').attr('data-value');
  			 if(stateVal && stateVal != ""){
  			 	$('#com-state').val(stateVal);
  			 }
  		}
  	}
  	function hideStateCityRow() {
  		$('#region-state-city-row').hide();
  		$('#region-state-city-row input').val('');
  		$('#region-state-txt').val(function() {
  			return $(this).find('option[selected]').text();	
		});
  	}
});
</script>