<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.title.vendastaproductsettings.key" />
			</div>
		</div>
	</div>
</div>

<div class="container">
	<form class="vendasta-form" method="post">
		<div class="vms-txt vms-resp-txt">
			<div class="vms-item-row-left vms-resp">
				<spring:message code="label.account.iden.key" />
			</div>
		</div>
		<div class="vms-input vms-resp-input">
			<input id="account-iden" name="account-iden"
				class="vendasta-account-id resp-vms-acnt"
				placeholder="<spring:message code="label.vendasta.account.identifier.key" />">
		</div>
		<div id="vndsta-form-submit"
			class="bd-vms-btn-save cursor-pointer vms-resp-submit">Save</div>
	</form>
</div>
<script>
	$(document).ready(function() {
		if("${accountId}" != ""){
			$('#account-iden').val('${accountId}');
		}		
		$(document).on('click', '#vndsta-form-submit', function() {
			var formData = { "accountId": $('#account-iden').val() };
			callAjaxPostWithPayloadData("/updatevendastasettings.do", function(data) {
				$('#overlay-toast').html(data);
				showToast();
			}, formData, true, '#vndsta-form-submit');
		});
	});
</script>