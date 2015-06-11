<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
</c:if>

<div id="prof-address-edit-container" class="prof-user-address prof-edit-icn">
	<form id="prof-edit-address-form">
		<input id="prof-address1" name="address1" class="pu-edit-fields" value="${contactdetail.address1}" placeholder='<spring:message code="label.address.address1.key"/>'>
		<input id="prof-address2" name="address2" class="pu-edit-fields" value="${contactdetail.address2}" placeholder='<spring:message code="label.address.address2.key"/>'>
		<input id="prof-country" name="country" class="pu-edit-fields" value="${contactdetail.country}" placeholder='<spring:message code="label.address.country.key"/>'>
		<input type="hidden" name="countryCode" id="prof-country-code" class="pu-edit-fields" value="${contactdetail.countryCode}">
		<select id="prof-state" name="state" class="pu-edit-fields" data-value="${contactdetail.state}">
			<option disabled selected><spring:message code="label.select.state.key"/></option>
		</select>
		<input id="prof-city" name="city" class="pu-edit-fields" value="${contactdetail.city}" placeholder='<spring:message code="label.address.city.key"/>'>
		<input id="prof-zipcode" name="zipCode" class="pu-edit-fields" value="${contactdetail.zipcode}" placeholder='<spring:message code="label.address.zipcode.key"/>'>
	</form>
</div>
<script>
$(document).ready(function(){
	var stateList;
	var cityLookupList;
	
	var countryCode = $('#prof-country-code').val();
	 if(countryCode == "US"){
	 	showStateCityRow();
	 }else{
		hideStateCityRow();
	 }
	 
	$("#prof-country").autocomplete({
		minLength: 1,
		source: countryData,
		delay : 0,
		open : function(event, ui) {
			$( "#prof-country-code" ).val("");
		},
		focus: function(event, ui) {
			$( "#prof-country" ).val(ui.item.label);
			return false;
		},
		select: function(event, ui) {
			$("#prof-country").val(ui.item.label);
			$("#prof-country-code").val(ui.item.code);
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
	$('#prof-state').on('change',function(e){
  		$('#prof-city').val('');
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

  	$('#prof-city').bind('focus', function(){ 
  		if($('#prof-state').val() &&  $('#prof-state').val() != ""){
  			$(this).trigger('keydown');
  			$(this).autocomplete("search");		
  		}
  	});
	  	
  	function initializeCityLookup(searchData){
  		$('#prof-city').autocomplete({
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
  		$('#prof-state').show();
		$('#prof-city').show();
  		if(!stateList){
  			callAjaxGET("./getusstatelist.do", function(data){
  				stateList = JSON.parse(data);
  				for(var i=0; i<stateList.length; i++){
  					$('#prof-state').append('<option data-stateid='+stateList[i].id+'>'+stateList[i].statecode+'</option>');
  				}
  				var stateVal = $('#prof-state').attr('data-value');
  				 if(stateVal && stateVal != ""){
  				 	$('#prof-state').val(stateVal);
  				 }
  			}, true);
  		}
  	}
  	function hideStateCityRow() {
  		$('#prof-state').hide().val('');
		$('#prof-city').hide();
  		$('#prof-state').val(function() {
  			return $(this).find('option[selected]').text();
  	    });
  	}
});
</script>