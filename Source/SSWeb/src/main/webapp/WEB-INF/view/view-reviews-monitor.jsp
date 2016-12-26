<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="user"
	value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<body>
	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="float-left hm-header-row-left text-center">
					<spring:message code="label.reviewsmonitor.key" />
				</div>
			</div>
		</div>
	</div>
	<div class="vendasta-container">
		<div id="vendasta-loader" class="overlay-vendasta"></div>
		<iframe id="vendasta-iframe" height="600px" width="100%" class="hide">
			Vendasta Integration </iframe>
		<div id="error-msg-vendasta" class="access-cont hide">You do not
			have access to this resource.</div>
	</div>
	<script>
		var url = "";
		$(document).ready(function() {
			$(document).attr("title", "Reviews Monitor");
			var iframe = document.getElementById('vendasta-iframe');
			iframe.addEventListener("load", function() {
				showFrame();
			});
			loadVendastaIframe();
		});

		//load Reviews monitor Iframe with Vendasta product URL
		function loadVendastaIframe() {
			url = fetchVendastaUrl();
			if (url == "" || url == undefined) {
				document.getElementById("vendasta-loader").style.display = "none";
				document.getElementById("vendasta-iframe").style.display = "none";
				document.getElementById("error-msg-vendasta").style.display = "block";
			} else {
				var payload = {
						"url" : url
				};
				callAjaxGetWithPayloadData("/testvendastaurl.do", function(data) {
					if ( data == "success") {
						loadVendastaUrl(url);
					}
					else {
						loadVendastaUrl("/vendastaError.do");
					}
				}, payload, false);
			}
		}

		function fetchVendastaUrl() {
			var payload = {};
			callAjaxGetWithPayloadData("/fetchvendastaurl.do", function(data) {
				map = JSON.parse(data);
				if (map.status == "success") {
					if (map.ssoToken != undefined || mao.ssoToken != "") {
						url = map.url + "?sso_token=" + map.ssoToken;
					}
				}
			}, payload, false);
			return url;
		}
		
		function loadVendastaUrl(url){
			$("#vendasta-iframe").attr("src", url);
		}
		
		function showFrame() {
			document.getElementById("vendasta-loader").style.display = "none";
			document.getElementById("error-msg-vendasta").style.display = "none";
			document.getElementById("vendasta-iframe").style.display = "block";
		}
	</script>