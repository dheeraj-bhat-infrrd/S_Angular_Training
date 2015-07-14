<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
 <div id="hm-header-main-wrapper" class="hm-header-main-wrapper">
     <div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center">
				<spring:message code="label.viewcompanyhierachy.key" />
			</div>
			<div class="clearfix float-right">
				<div class="float-left dash-sel-lbl">Company</div>
				<div class="dsh-inp-wrapper float-left">
					<input id="hr-comp-sel" class="dash-sel-item" type="text"
						placeholder="Start typing..." onkeyup="searchAdminCompanies(this)">
					<div id="hr-comp-res" class="dsh-sb-dd hide"></div>
				</div>
			</div>
		</div>
	</div>
 </div>
 <div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
<div id="comp-hierarchy-cont" class="container v-hr-container">
</div>
 <div id="temp-message" class="hide"></div>
