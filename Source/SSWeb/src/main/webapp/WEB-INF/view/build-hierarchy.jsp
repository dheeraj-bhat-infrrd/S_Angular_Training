<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<div id="hm-header-main-wrapper" class="hm-header-main-wrapper">
    <div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center">
				<spring:message code="label.buildcompanyhierarchy.key" />
			</div>
			<div class="float-right hm-header-right text-center"
			 onclick="javascript:showMainContent('./hierarchyupload.do')">
					<spring:message code="label.header.Hierarchyupload.key" />
				</div>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./viewhierarchy.do');">
				<spring:message code="label.viewcompanyhierachy.key" />
			</div>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./showusermangementpage.do')">
				<spring:message code="label.header.editteam.key" />
			</div>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./showbuildhierarchypage.do')">
				<spring:message code="label.header.buildhierarchy.key" />
			</div>
			
		</div>
	</div>
</div>
<div id="server-message" class="hide">
	<jsp:include page="messageheader.jsp"></jsp:include>
</div>
<div class="container bd-hr-container">
   <div class="bd-hr-left-panel col-lg-3 col-md-3 col-sm-3">
   <div class="bd-hr-lp-header"><spring:message code="label.ourcompany.key"/></div>
        <div id ="prof-hierarchy-container" class="hide">
        	<!-- hierarchy structure comes here  -->
        </div>
   </div>
        <div class="bd-hr-rt-panel col-lg-9 col-md-9 col-sm-9 col-xs-12">
            <div class="bd-hr-rt-header"><i><spring:message code="label.woulduliketoadd.key"/> <strong><spring:message code="label.new.key"/></strong></i></div>
            <div class="bd-hr-tabs-wrapper">
                <div class="bd-hr-tabs-header clearfix">
                	<div class="bd-hr-tabs-header-item float-left">
                        <span id="hr-individual-tab" data-tab="individual" class="bd-hdr-span"><spring:message code="label.individual.key"/></span>
                    </div>
                    <c:if test="${isBranchAdditionAllowed}">
                    <div class="bd-hr-tabs-header-item float-left">
                        <span id="hr-office-tab" data-tab="office" class="bd-hdr-span"><spring:message code="label.office.key"/></span>
                    </div>
                    </c:if>
                    <c:if test="${isRegionAdditionAllowed}">
                    <div class="bd-hr-tabs-header-item float-left">
                        <span id="hr-region-tab" data-tab="region" class="bd-hdr-span bd-hdr-active-arr bd-hdr-active"><spring:message code="label.region.key"/></span>
                    </div>
                    </c:if>
                    <%-- <div class="bd-hr-tabs-header-item hdr-txt-rt-adj float-left">
                        <span data-tab="csv" class="bd-hdr-span"><spring:message code="label.uploadcsv.key"/></span>
                    </div> --%>
                </div>
            </div>
            <div id="bd-edit-form-section" class="bd-hr-form-wrapper">
            </div>
            
        </div>
    </div>
<!-- div to temporarily store message to be displayed  -->
<div id="temp-message" class="hide"></div>
<input class="ignore-clear" type="hidden" name="isUserAuthorized" id="is-user-authorized" value="${isUserAuthorized}"/>
<input class="ignore-clear" type="hidden" id="profile-name" value="${profileName}"/>
<input class="ignore-clear" type="hidden" id="account-type" value="${user.company.licenseDetails[0].accountsMaster.accountName}"/>
<input class="ignore-clear" type="hidden" id="highest-role" value="${highestrole}"/>

<script>
$(document).ready(function() {
	hideOverlay();
	$(document).attr("title", "Build Hierarchy");
	checkUserAuthorization();
	fetchCompleteHierarchy();
	
	/**
	*	display the form according to account type and highest role
	*/
	getEditSection();
	
    $(document).on('click', 'body', function() {
        $('.dd-com-list').slideUp(200);
    });
    
    $(document).on('click','.bd-hdr-span',function(){
        hideError();
        getEditSectionFormByTab($(this).data('tab'));
    });

});
</script>