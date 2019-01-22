<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty companyList}">
	<c:forEach items="${companyList}" var="companyItem">
		<div id="tr-comp-${companyItem.iden}" clicked="false"
			class="v-tbl-row v-tbl-row-sel comp-row cursor-pointer"
			data-iden="${companyItem.iden}">
			<div class="v-tbl-line">
				<div class="v-line-comp"></div>
				<i id="tr-spinner-${companyItem.iden}"  class="fa fa-spinner fa-pulse fa-2x fa-fw" aria-hidden="true" style="display:none;margin:7px 0px;"></i> 
			</div>
			<%-- <div class="v-tbl-img">
           		<c:choose>
    				<c:when test="${not empty companyItem.profileImageUrl}">
        				<div  class="margin-top-5 float-left profile-image-display" style="background: url(${companyItem.profileImageUrl}) 50% 50% / cover no-repeat;">
							<span></span>
						</div> 
    				</c:when>    
    				<c:otherwise>
        				<div id="" class="margin-top-5 float-left profile-image-display" style="">
							<span id="">${fn:substring(companyItem.profileName, 0, 1)}</span>
						</div> 
    				</c:otherwise>
				</c:choose>
           	</div> --%>
			<div class="v-tbl-name">
				<c:if
					test="${not empty companyItem.contact_details && not empty companyItem.contact_details.name }">
					${companyItem.contact_details.name}
				</c:if>
			</div>
			<div class="v-tbl-add">
				<c:if
					test="${not empty companyItem.contact_details && not empty companyItem.contact_details.address1 }">
					${companyItem.contact_details.address1}
				</c:if>
				&nbsp;
				<c:if
					test="${not empty companyItem.contact_details && not empty companyItem.contact_details.address2 }">
					${companyItem.contact_details.address2}
				</c:if>
			</div>
			<div class="v-tbl-role"></div>
			 
			<div class="v-tbl-btns">
				<div class="clearfix v-tbl-icn-wraper">

					<c:if test="${companyStatus ne 'incomplete' and companyStatus ne 'inactive'}">
						<div class="float-right v-tbl-icn v-icn-login user-login-icn" data-company-iden="${companyItem.iden}" title="login as"></div>
					</c:if>
					<c:if test="${companyStatus eq 'incomplete'}">
						<div class="float-right v-tbl-icn v-icn-close comp-del-icn" data-iden="${companyItem.iden}"></div>
					</c:if>
					<c:if test="${(companyStatus eq 'inactive')  && (isSuperAdmin == true)}">
						<div class="float-right v-tbl-icn v-icn-close comp-del-icn" data-iden="${companyItem.iden}"></div>
					</c:if>
				</div>
			</div>
			
			<div class="v-tbl-spacer"></div>
		</div>
		<div data-iden="${companyItem.iden}" class="hide comp-hr-cont"></div>
	</c:forEach>
</c:if>
<script type="text/javascript">
	var globalStrings = new Array();
	globalStrings['label.admin.key'] = "<spring:theme code='label.admin.key' text='Admin' javaScriptEscape='true' />";
	globalStrings['label.companyadmin.key'] = "<spring:theme code='label.companyadmin.key' text='Company Admin' javaScriptEscape='true' />";
	globalStrings['label.regionadmin.key'] = "<spring:theme code='label.regionadmin.key' text='Region Admin' javaScriptEscape='true' />";
	globalStrings['label.branchadmin.key'] = "<spring:theme code='label.branchadmin.key' text='Branch Admin' javaScriptEscape='true' />";
	globalStrings['label.user.key'] = "<spring:theme code='label.user.key' text='User' javaScriptEscape='true' />";
	bindUserLoginEvent();
</script>
