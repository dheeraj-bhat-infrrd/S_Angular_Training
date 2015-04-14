<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="hm-header-main-wrapper hm-hdr-bord-bot">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left hr-dsh-adj-lft">Dashboard</div>
            
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
		<div id="prof-container" data-profile-master-id="${profileMasterId}" data-profile-id="${profileId}"
			data-column-name="${columnName}" data-column-value="${columnValue}" class="dash-top-info">

			<div class="row row-dash-top-adj">
				<div class="float-right dash-main-right col-lg-6 col-md-6 col-sm-6 col-xs-12">
					<div class="dsh-graph-wrapper">
						<div class="dsh-g-wrap dsh-g-wrap-1">
							<div class="dsh-graph-item dsh-graph-item-1">
								<div id="dg-img-1" class="dsh-graph-img"></div>
								<div id="socl-post" class="dsh-graph-num">400</div>
								<div class="dsh-graph-txt dsh-graph-txt-1">Social Post In Last 30 Days</div>
							</div>
						</div>
						<div class="dsh-g-wrap dsh-g-wrap-2">
							<div class="dsh-graph-item dsh-graph-item-1">
								<div id="dg-img-2" class="dsh-graph-img"></div>
								<div id="srv-snt-cnt" class="dsh-graph-num">3000</div>
								<div class="dsh-graph-txt dsh-graph-txt-2">Total Surveys Sent</div>
							</div>
						</div>
						<div class="dsh-g-wrap dsh-g-wrap-3">
							<div class="dsh-graph-item dsh-graph-item-1">
								<div id="dg-img-3" class="dsh-graph-img"></div>
								<div id="srv-scr" class="dsh-graph-num">4/5</div>
								<div class="dsh-graph-txt dsh-graph-txt-3">Survey Score Over Last 30 Days</div>
							</div>
						</div>
						<div class="dsh-g-wrap dsh-g-wrap-4">
							<div class="dsh-graph-item dsh-graph-item-1">
								<div id="dg-img-4" class="dsh-graph-img dsh-graph-img-4"></div>
								<div id="dsh-prsn-img" class="dsh-graph-num dsh-graph-num-4 <!-- person-img -->"></div>
								<div class="dsh-graph-txt dsh-graph-txt-4">Profile Completed</div>
								<div id="badges" class="dsg-g-rbn"></div>
							</div>
						</div>
					</div>
				</div>

				<div class="float-left dash-main-left col-lg-6 col-md-6 col-sm-6 col-xs-12">
					<div class="dash-left-txt-wrapper">
						<div class="dsh-name-wrapper">
							<div id="name" class="dsh-txt-1"></div>
							<div id="designation" class="dsh-txt-2"></div>
							<div id="company" class="dsh-txt-3"></div>
						</div>
						<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix">
							<div class="float-left dsh-star-item no-star"></div>
							<div class="float-left dsh-star-item no-star"></div>
							<div class="float-left dsh-star-item no-star"></div>
							<div class="float-left dsh-star-item no-star"></div>
							<div class="float-left dsh-star-item no-star"></div>
							<div id="profile-completed" class="float-right dsh-rating-item">3/5</div>
						</div>
						<div class="dsh-btn-complete" onclick="showMainContent('./showprofilepage.do')">Complete Your Profile</div>
					</div>
				</div>
			</div>
		</div>

		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head">Survey Status</div>
				<div id="region-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<select id="selection-list" class="float-left dash-sel-item"></select>
				</div>
				<div id="dsh-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<div class="dsh-inp-wrapper">
						<input id="dsh-sel-item" class="dash-sel-item" type="text" placeholder="Start typing..." onkeyup="searchBranchRegionOrAgent(this.value, 'icons')">
						<div id="dsh-srch-res" class="dsh-sb-dd"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Duration</div>
					<select id="survey-count-days" class="float-left dash-sel-item">
						<option value="30">30 Days</option>
						<option value="60">60 Days</option>
						<option value="90">90 Days</option>
						<option value="365">1 Year</option>
					</select>
				</div>
			</div>
			
			<div class="float-left stats-right">
				<div class="clearfix stat-icns-wrapper">
					<div class="float-left stat-icn-lbl">No. of surveys sent</div>
					<div id="all-surv-icn" class="float-left stat-icns-item clearfix"></div>
				</div>
				<div class="clearfix stat-icns-wrapper">
					<div class="float-left stat-icn-lbl">Surveys clicked</div>
					<div id="clicked-surv-icn" class="float-left stat-icns-item clearfix"></div>
				</div>
				<div class="clearfix stat-icns-wrapper">
					<div class="float-left stat-icn-lbl">Surveys completed</div>
					<div id="completed-surv-icn" class="float-left stat-icns-item clearfix"></div>
				</div>
				<div class="clearfix stat-icns-wrapper">
					<div class="float-left stat-icn-lbl">Social posts</div>
					<div id="social-post-icn" class="float-left stat-icns-item clearfix"></div>
				</div>
				<!--<div class="clearfix stat-icns-wrapper">
                    <div class="float-left stat-icn-lbl">No. of social posts</div>
                    <div class="float-left stat-icns-item clearfix">
                        <div class="progress">
                            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 40%">
                                <span class="sr-only">40% Complete (success)</span>
                            </div>
                        </div>
                        <div class="float-left stat-icn-txt-rt">80%</div>
                    </div>
                </div>-->
			</div>
		</div>

		<div class="dash-stats-wrapper bord-bot-dc clearfix">
            <div class="float-left stats-left clearfix">
                <div class="dash-sub-head">Utilization over time</div>
                <div id="graph-sel-div" class="clearfix dash-sel-wrapper">
                    <div class="float-left dash-sel-lbl">Choose</div>
                    <select id="graph-sel-list" class="float-left dash-sel-item"></select>
                </div>
                
                <div id="dsh-grph-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<div class="dsh-inp-wrapper">
						<input id="dsh-grph-sel-item" class="dash-sel-item" type="text" placeholder="Start typing..." onkeyup="searchBranchRegionOrAgent(this.value,'graph')">
						<div id="dsh-grph-srch-res" class="dsh-sb-dd"></div>
					</div>
				</div>
                
                <div class="clearfix dash-sel-wrapper">
                    <div class="float-left dash-sel-lbl">Format</div>
                    <select id="dsh-grph-format" class="float-left dash-sel-item">
                        <option value="weekly">Weekly</option>
                        <option value="monthly">Monthly</option>
                        <option value="yearly">Yearly</option>
                    </select>
                </div>
            </div>
            <div class="float-left stats-right stats-right-adj">
                <div class="util-graph-wrapper">
                    <div id="util-gph-item" class="util-gph-item">
                    </div>
                    <div class="util-gph-legend clearfix">
                        <div class="util-gph-legend-item">No of surveys sent<span class="lgn-col-item lgn-col-grn"></span></div>
                        <div class="util-gph-legend-item">No of surveys clicked<span class="lgn-col-item lgn-col-blue"></span></div>
                        <div class="util-gph-legend-item">No of surveys completed<span class="lgn-col-item lgn-col-yel"></span></div>
                        <div class="util-gph-legend-item">No of social posts<span class="lgn-col-item lgn-col-red"></span></div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="dash-panels-wrapper">
            <div class="row">
                <div id="prnt-dsh-inc-srvey" class="dash-panel-left col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="dash-lp-header" id="incomplete-survey-header">Incomplete Surveys</div>
                    <div id="dsh-inc-srvey" class="dash-lp-item-grp">
                    </div>
                    <div id="dsh-inc-dwnld" class="dash-btn-sur-data hide">Incomplete Survey Data</div>
                </div>
                <div class="dash-panel-right col-lg-8 col-md-8 col-sm-8 col-xs-12 resp-adj">
                    <div class="people-say-wrapper rt-content-main rt-content-main-adj">
                        <div class="main-con-header clearfix pad-bot-10-resp">
                            <div id="review-desc" class="float-left dash-ppl-say-lbl"><span class="ppl-say-txt-st">What people say</span> about Anna Thomas</div>
                            <div id="dsh-cmp-dwnld" class="float-right dash-btn-dl-sd hide">Download Survey Data</div>
                        </div>
                        <div id="review-details" class="ppl-review-item-wrapper">
	                        <div class="ppl-review-item">
	                           <!--  <div class="ppl-header-wrapper clearfix">
	                                <div class="float-left ppl-header-left">
	                                    <div class="ppl-head-1">Matt &amp; Gina Conelly - Lehi, UT</div>
	                                    <div class="ppl-head-2">12<sup>th</sup> Sept 2014</div>
	                                </div>
	                                <div class="float-right ppl-header-right">
	                                    <div class="st-rating-wrapper maring-0 clearfix">
	                                        <div class="rating-star icn-full-star"></div>
	                                        <div class="rating-star icn-full-star"></div>
	                                        <div class="rating-star icn-half-star"></div>
	                                        <div class="rating-star icn-no-star"></div>
	                                        <div class="rating-star icn-no-star"></div>
	                                    </div>
	                                </div>
	                            </div>
	                            <div class="ppl-content">Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las.</div>
	                            <div class="ppl-share-wrapper clearfix">
	                                <div class="float-left blue-text ppl-share-shr-txt">Share</div>
	                                <div class="float-left icn-share icn-plus-open" style="display: block;"></div>
	                                <div class="float-left clearfix ppl-share-social hide" style="display: none;">
	                                    <div class="float-left ppl-share-icns icn-fb"></div>
	                                    <div class="float-left ppl-share-icns icn-twit"></div>
	                                    <div class="float-left ppl-share-icns icn-lin"></div>
	                                    <div class="float-left ppl-share-icns icn-yelp"></div>
	                                    <div class="view-survey-lnk">View Survey</div>
	                                </div>
	                                <div class="float-left icn-share icn-remove icn-rem-size hide" style="display: none;"></div>
	                            </div> -->
	                        </div>
                        </div>
                        <!-- <div class="profile-addl-links clearfix">
                            <span class="p-a-l-item">100 additional reviews not recommended</span>
                        </div> -->
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

<script>

$(document).ready(function() {
	hideOverlay();
	$(document).attr("title", "Dashboard");
	var profileMasterId = $('#prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');
	paintDashboard(profileMasterId, currentProfileName, currentProfileValue);
	updateCircles();
});

// Dashboard View as
$('#dashboard-sel').click(function(){
	$('#da-dd-wrapper-profiles').slideToggle(200);
});

$('.da-dd-item').click(function(){
	$('#dashboard-sel').html($(this).html());
	$('#da-dd-wrapper-profiles').slideToggle(200);
	
	// update selected profile in session
	var newProfileId = $(this).attr('data-profile-id');
	updateCurrentProfile(newProfileId);

	var newProfileMasterId = $(this).attr('data-profile-master-id');
	var newProfileName = $(this).attr('data-column-name');
	var newProfileValue = $(this).attr('data-column-value');
	paintDashboard(newProfileMasterId, newProfileName, newProfileValue);
	updateCircles();
	
	// updating data
	$('#prof-container').attr('data-profile-id', newProfileId);
	$('#prof-container').attr('data-profile-master-id', newProfileMasterId);
	$('#prof-container').attr('data-column-name', newProfileName);
	$('#prof-container').attr('data-column-value', newProfileValue);
});
</script>
