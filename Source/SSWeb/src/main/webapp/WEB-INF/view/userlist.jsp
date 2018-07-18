<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Account masters 1=Individual, 2=Team, 3=Company,4=Enterprise,5=Free Account -->
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<c:set var="accountTypeId" value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" />

<table class="v-um-tbl">
	<tr id="u-tbl-header" class="u-tbl-header" data-num-found="${numFound}">
		<td class="v-tbl-uname mng-tbl-name"><spring:message code="label.usermanagement.username.key" /></td>
		<td class="v-tbl-email mng-tbl-email"><spring:message code="label.emailid.key" /></td>
		<td class="v-tbl-email mng-tbl-soc-conn">Social Connections</td>
		<td class="v-tbl-rgn-adm text-center mng-tbl-ticks">
			<c:if test="${accountTypeId == 4}">
				<spring:message code="label.region.key" /><br />
				<spring:message code="label.admin.key" />
			</c:if>
		</td>
		<td class="v-tbl-of-adm text-center mng-tbl-ticks">
			<c:if test="${accountTypeId == 4 || accountTypeId == 3}">
				<spring:message code="label.office.key" /><br />
				<spring:message code="label.admin.key" />
			</c:if>
		</td>
		<td class="v-tbl-ln-of text-center mng-tbl-ticks"><spring:message code="label.individual.key" /></td>
		<!-- <td class="v-tbl-mail"></td>
		<td class="v-tbl-wid"></td>
		<td class="v-tbl-online"></td>
		<td class="v-tbl-rem"></td>
		<td class="v-tbl-edit"></td> -->
		<td class="v-tbl-spacer"></td>
	</tr>
	<c:choose>
		<c:when test="${not empty userslist}">
			<c:forEach var="userfromsearch" items="${userslist}">
				<!-- For Region admin -->
				<c:if test="${accountTypeId == 4}">
					<c:set var="regionadmintickclass" value="" />
					<c:if test="${userfromsearch.isRegionAdmin != null && userfromsearch.isRegionAdmin}">
						<c:set var="regionadmintickclass" value="v-icn-tick" />
					</c:if>
				</c:if>

				<!-- For Branch admin -->
				<c:if test="${accountTypeId == 4 || accountTypeId == 3}">
					<c:set var="branchadmintickclass" value="" />
					<c:if test="${userfromsearch.isBranchAdmin != null && userfromsearch.isBranchAdmin}">
						<c:set var="branchadmintickclass" value="v-icn-tick" />
					</c:if>
				</c:if>

				<!-- For Agent -->
				<c:set var="agenttickclass" value="" />
				<c:if test="${userfromsearch.isAgent != null && userfromsearch.isAgent}">
					<c:set var="agenttickclass" value="v-icn-tick" />
				</c:if>

				<!-- If status is 2, then user has not acted on invitation -->
				<c:set var="regstatustickclass" value="" />
				<c:set var="userstatustickclass" value="v-icn-verified" />
				<c:if test="${userfromsearch.status == 2}">
					<c:set var="regstatustickclass" value="v-icn-fmail" />
					<c:set var="userstatustickclass" value="v-icn-notverified" />
				</c:if>

				<!-- if admin can edit -->
				<c:choose>
					<c:when test="${userfromsearch.canEdit}">
						<c:set var="admincaneditclass" value="v-tbl-icn" />
					</c:when>
					<c:otherwise>
						<c:set var="admincaneditclass" value="v-tbl-icn-disabled" />
					</c:otherwise>
				</c:choose>

				<c:choose>
					<c:when test="${userfromsearch.canEdit && user.userId != userfromsearch.userId}">
						<c:set var="admincanremoveclass" value="v-tbl-icn" />
					</c:when>
					<c:otherwise>
						<c:set var="admincanremoveclass" value="v-tbl-icn-disabled" />
					</c:otherwise>
				</c:choose>

				<tr class="u-tbl-row user-row" id="user-row-${userfromsearch.userId}" data-editable="${userfromsearch.canEdit}">
					<td class="v-tbl-uname fetch-name mng-tbl-name" data-first-name="${userfromsearch.firstName}" data-last-name="${userfromsearch.lastName}"
						data-user-id="${userfromsearch.userId}">${userfromsearch.displayName}</td>
					<td class="v-tbl-email fetch-email mng-tbl-email"><div class="mng-tbl-email-div" title="${userfromsearch.emailId}">${userfromsearch.emailId}</div></td>
					<td class="v-tbl-email mng-tbl-soc-conn">
						<c:forEach var="socialMediaVO" items="${userfromsearch.socialMediaVOs}">
							<c:set var="status" value="${socialMediaVO.status}" />
	               		  	<c:set var="socialMedia" value="${socialMediaVO.socialMedia}" />
	               		  	<c:choose>
	               		  		<c:when test="${socialMedia == 'facebook'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="fbClass" value="mng-fb-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="fbClass" value="mng-fb-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="fbClass" value="mng-fb-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'instagram'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="instaClass" value="mng-insta-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="instaClass" value="mng-insta-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="instaClass" value="mng-insta-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'facebookPixel'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="fbpClass" value="mng-fbp-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="fbpClass" value="mng-fbp-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="fbpClass" value="mng-fbp-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'google'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="gpClass" value="mng-gp-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="gpClass" value="mng-gp-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="gpClass" value="mng-gp-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'lendingtree'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="ltClass" value="mng-lt-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="ltClass" value="mng-lt-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="ltClass" value="mng-lt-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'linkedin'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="linClass" value="mng-lin-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="linClass" value="mng-lin-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="linClass" value="mng-lin-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'realtor'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="rtClass" value="mng-rt-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="rtClass" value="mng-rt-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="rtClass" value="mng-rt-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'twitter'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="twClass" value="mng-tw-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="twClass" value="mng-tw-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="twClass" value="mng-tw-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'yelp'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="ypClass" value="mng-yp-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="ypClass" value="mng-yp-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="ypClass" value="mng-yp-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  		<c:when test="${socialMedia == 'zillow'}">
	               		  			<c:choose>
	               		  				<c:when test="${status == 'NOT_CONNECTED'}">
	               		  					<c:set var="ziClass" value="mng-zi-gray" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'CONNECTED'}">
	               		  					<c:set var="ziClass" value="mng-zi-conn" />
	               		  				</c:when>
	               		  				<c:when test="${status == 'EXPIRED'}">
	               		  					<c:set var="ziClass" value="mng-zi-disc" />
	               		  				</c:when>
	               		  			</c:choose>
	               		  		</c:when>
	               		  	</c:choose>
                		</c:forEach>
                		<div class="${fbClass} mgn-tbl-conn-icn"></div>
						<div class="${gpClass} mgn-tbl-conn-icn"></div>
						<div class="${ltClass} mgn-tbl-conn-icn"></div>
						<div class="${linClass} mgn-tbl-conn-icn"></div>
						<div class="${rtClass} mgn-tbl-conn-icn"></div>
						<div class="${ziClass} mgn-tbl-conn-icn"></div>
						<div class="${ypClass} mgn-tbl-conn-icn"></div>
						<div class="${twClass} mgn-tbl-conn-icn"></div>
						<div class="${fbpClass} mgn-tbl-conn-icn"></div>
						<div class="${instaClass} mgn-tbl-conn-icn"></div> 
					</td>
					<td class="v-tbl-rgn-adm mng-tbl-ticks ${regionadmintickclass}"></td>
					<td class="v-tbl-of-adm mng-tbl-ticks ${branchadmintickclass}"></td>
					<td class="v-tbl-ln-of mng-tbl-ticks ${agenttickclass}"></td>
					<td class="v-tbl-btns v-tbl-btns-um mng-tbl-btns">
						<div class="v-tbn-icn-dropdown hide"></div>
						<div class="clearfix v-tbl-icn-wraper v-um-tbl-icn-wraper">
							<c:choose>
								<c:when test="${not empty regstatustickclass}">
									<div class="v-tbl-mail ${admincaneditclass} ${regstatustickclass} v-tbl-icn-sm"
										title="<spring:message code="label.resendmail.key" />">Resend</div>
								</c:when>
								<c:otherwise>
									<div class="v-tbl-mail ${admincaneditclass}"></div>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${userfromsearch.isAgent != null && userfromsearch.isAgent}">
									<div class="v-tbl-wid v-icn-wid ${admincaneditclass} v-tbl-icn-sm"
										title="<spring:message code="label.widget.key" />"
										onclick="generateWidget($(this),${ userfromsearch.userId }, 'individual');">Widget</div>
								</c:when>
								<c:otherwise>
									<div class="v-tbl-spacer" ></div>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${userfromsearch.status == 2}">
									<div class="v-tbl-online v-tbl-icn ${userstatustickclass}" title="<spring:message code="label.notverified.key" />"></div>
								</c:when>
								<c:otherwise>
									<div class="v-tbl-online v-tbl-icn ${userstatustickclass}" title="<spring:message code="label.verified.key" />"></div>
								</c:otherwise>
							</c:choose>
							<div class="v-tbl-rem ${admincanremoveclass} v-icn-rem-user v-tbl-icn-sm" title="<spring:message code="label.remove.key" />">Delete</div>
							<div class="v-tbl-edit ${admincaneditclass} v-icn-edit-user edit-user v-tbl-icn-sm" title="<spring:message code="label.edit.key" />">Edit</div>
							<c:choose>
							 <c:when test="${user.userId != userfromsearch.userId}">
						   		<div class="v-tbl-online v-tbl-icn v-icn-login user-login-icn v-tbl-icn-sm" data-iden="${userfromsearch.userId}" title="login as">Login</div>
						   </c:when>
						   <c:otherwise>
									<div class="v-tbl-spacer" ></div>
								</c:otherwise>
						   </c:choose>
					   </div>
				   </td>
				</tr>
				<tr class="u-tbl-row u-tbl-row-sel hide user-assignment-edit-row">
					<td id="user-details-and-assignments-${userfromsearch.userId}" class="u-tbl-edit-td user-assignment-edit-div" colspan="7">
						<!-- data populated from um-edit-row.jsp -->
					</td>
				</tr>
				
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr class="u-tbl-row"><spring:message code="label.nousersfound.key" /></tr>
		</c:otherwise>
	</c:choose>
</table>
<script>
	bindAppUserLoginEvent();
</script>