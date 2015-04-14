<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.header.dashboard.key" /></div>
			
			<c:if test="${not empty profileList && fn:length(profileList) > 1}">
				<div class="float-right header-right clearfix hr-dsh-adj-rt" style="z-index: 9999; margin-left: 50px;">
					<div class="float-left hr-txt1"><spring:message code="label.viewas.key" /></div>
					<div id="dashboard-sel" class="float-left hr-txt2 cursor-pointer">${profileName}</div>
					<div id="da-dd-wrapper-profiles" class="hr-dd-wrapper hide">
						<c:forEach var="userprofile" items="${profileList}">
							<div class="da-dd-item" data-profile-id="${userprofile.key}"
								data-column-name="${userprofile.value.profileName}"
								data-column-value="${userprofile.value.profileValue}"
								data-profile-master-id="${userprofile.value.profilesMasterId}">${userprofile.value.userProfileName}</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container">
		<div id="prof-container" data-profile-master-id="${profileMasterId}"
			data-profile-id="${profileId}" data-column-name="${columnName}"
			data-column-value="${columnValue}" class="dash-top-info">
			<div id="dash-profile-detail-circles" class="row row-dash-top-adj">
				<!-- Populated by profile detail -->
			</div>
		</div>

		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head"><spring:message code="label.surveystatus.key" /></div>
				<div id="region-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<select id="selection-list" class="float-left dash-sel-item"></select>
				</div>
				<div id="dsh-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<div class="dsh-inp-wrapper">
						<input id="dsh-sel-item" class="dash-sel-item" type="text" placeholder="<spring:message code="label.starttyping.key" />"
							onkeyup="searchBranchRegionOrAgent(this.value, 'icons')">
						<div id="dsh-srch-res" class="dsh-sb-dd"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.duration.key" /></div>
					<select id="survey-count-days" class="float-left dash-sel-item">
						<option value="30"><spring:message code="label.duration.one.key" /></option>
						<option value="60"><spring:message code="label.duration.two.key" /></option>
						<option value="90"><spring:message code="label.duration.three.key" /></option>
						<option value="365"><spring:message code="label.duration.four.key" /></option>
					</select>
				</div>
			</div>
			<div id="dash-survey-status" >
				<!-- Populated by survey status -->
			</div>
		</div>

		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head"><spring:message code="label.utilization.key" /></div>
				<div id="graph-sel-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<select id="graph-sel-list" class="float-left dash-sel-item"></select>
				</div>
				<div id="dsh-grph-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<div class="dsh-inp-wrapper">
						<input id="dsh-grph-sel-item" class="dash-sel-item" type="text" placeholder="<spring:message code="label.starttyping.key" />"
							onkeyup="searchBranchRegionOrAgent(this.value, 'graph')">
						<div id="dsh-grph-srch-res" class="dsh-sb-dd"></div>
					</div>
				</div>
				
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.format.key" /></div>
					<select id="dsh-grph-format" class="float-left dash-sel-item">
						<option value="weekly"><spring:message code="label.format.one.key" /></option>
						<option value="monthly"><spring:message code="label.format.two.key" /></option>
						<option value="yearly"><spring:message code="label.format.three.key" /></option>
					</select>
				</div>
			</div>
			<div class="float-left stats-right stats-right-adj">
				<div class="util-graph-wrapper">
					<div id="util-gph-item" class="util-gph-item"></div>
					<div class="util-gph-legend clearfix">
						<div class="util-gph-legend-item">
							<spring:message code="label.surveyssent.key" /><span class="lgn-col-item lgn-col-grn"></span>
						</div>
						<div class="util-gph-legend-item">
							<spring:message code="label.surveysclicked.key" /><span class="lgn-col-item lgn-col-blue"></span>
						</div>
						<div class="util-gph-legend-item">
							<spring:message code="label.surveyscompleted.key" /><span class="lgn-col-item lgn-col-yel"></span>
						</div>
						<div class="util-gph-legend-item">
							<spring:message code="label.socialposts.key" /><span class="lgn-col-item lgn-col-red"></span>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="dash-panels-wrapper">
			<div class="row">
				<div id="dash-survey-incomplete" class="dash-panel-left col-lg-4 col-md-4 col-sm-4 col-xs-12">
					<!-- Populated with incomplete surveys -->
				</div>
				
				<div class="dash-panel-right col-lg-8 col-md-8 col-sm-8 col-xs-12 resp-adj">
					<div class="people-say-wrapper rt-content-main rt-content-main-adj">
						<div class="main-con-header clearfix pad-bot-10-resp">
							<div id="review-desc" class="float-left dash-ppl-say-lbl"><span class="ppl-say-txt-st">What people say</span> about Anna Thomas</div>
							<div id="dsh-cmp-dwnld" class="float-right dash-btn-dl-sd hide">Download Survey Data</div>
						</div>
						<div id="review-details" class="ppl-review-item-wrapper">
							<div class="ppl-review-item"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<style>
.dsh-dd-wrapper {
	width: 300px;
	background-color: #fff;
	padding: 8px;
	border: 1px solid #dcdcdc;
}

.dsh-rgn-brnch-agnt-list {
	position: absolute;
}

.dsh-sb-dd {
	position: absolute;
  	background-color: white;
  	top: 36px;
  	width: 215px;
  	right: 35px;
  	display: block;
  	border: 1px solid #dcdcdc;
  	line-height: 32px;
}

.dsh-inp-wrapper {
	position: relative;
}

.dsh-res-display {
	padding: 0 10px;
	cursor: pointer;
}
</style>

<script src="${pageContext.request.contextPath}/resources/js/dashboard.js"></script>
<script>
$(document).ready(function() {
	hideOverlay();
	$(document).attr("title", "Dashboard");
	
	var profileMasterId = $('#prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');

	paintDashboard(profileMasterId, currentProfileName, currentProfileValue);
});
</script>