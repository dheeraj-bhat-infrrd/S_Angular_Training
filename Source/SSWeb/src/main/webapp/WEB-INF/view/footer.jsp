<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="footer-main-wrapper">
    <div class="container text-center footer-text">
    	<spring:message code="label.copyright.key"/> &copy; <span id="ss-cc-year"></span> <spring:message code="label.footer.socialsurvey.key"/><span class="center-dot">.</span> <spring:message code="label.allrightscopyright.key"/>
    </div>
</div>

<script>
	$(document).ready(function(){
		var curDate = new Date();
		$('#ss-cc-year').html(curDate.getFullYear());
	});
</script>
</body>
</html>