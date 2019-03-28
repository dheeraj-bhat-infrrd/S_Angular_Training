<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.header.admin.dashboard.key" /></div>
			<%-- <!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include> --%>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container ad-container">
		<div id="cust-suc-loader" style="display:none"></div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="dash-sub-head float-left">Customer Success Information</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info-cont">
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 cust-suc-info-cont-left">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Customer Success Name: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val-dropdown">
							<div id="ad-customer-success-name" data-columnname="customerSuccessName" data-customersuccessid="0" data-sel-customersuccessid="" class="cust-suc-info-val cust-suc-dropdown-head"></div>
						    <div id="ad-customer-success-name-dropdown" class="ad-customer-success-name-dropdown" style="display:none" data-columnname="customerSuccessId" data-disabled=false>
							    <div class="ad-customer-success-name-item">Allied Mortgage</div>
							    <div class="ad-customer-success-name-item">Allied Mortgage</div>
							</div>
						</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Company: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-company-name" data-editable=false data-columnName="companyName"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Account Status: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-account-status" data-editable=false data-columnName="accountStatus"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">TMC Client: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-tmc-client" data-editable=true data-columnName="tmcClient" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">COMPANY_ID: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-company-id" data-editable=false data-columnName="companyId"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Survey Data Source: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-survey-data-source" data-editable=true data-columnName="surveyDataSource" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">DBA's For Company: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-company-dba" data-editable=true data-columnName="dbaForCompany" data-value=""></div>
					</div>			
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Gap Analysis Date: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable">
							<input type="hidden" id="ad-gap-analysis-date-ts" value="0">
							<input class="cust-suc-info-inp" id="ad-gap-analysis-date" data-editable=true data-columnName="gapAnalysisDate">
						</div>
					</div>		
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Services Sold: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-services-sold" data-editable=true data-columnName="servicesSold" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Tag: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-tag" data-editable=true data-columnName="tag" data-value=""></div>
					</div>
				</div>
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 cust-suc-info-cont-right">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Customer Success Owner: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-customer-success-owner" data-editable=true data-columnName="customerSuccessOwner" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">RVP: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-rvp" data-editable=true data-columnName="rvp" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Closed Date: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable">
							<input type="hidden" id="ad-closed-date-ts" value="0">
							<input class="cust-suc-info-inp" id="ad-closed-date" data-editable=true data-columnName="closedDate">
						</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Created By: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-created-by" data-editable=false data-columnName="createdBy"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Modified By: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-modified-by" data-editable=false data-columnName="modifiedBy"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Potential: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-potential" data-editable=true data-columnName="potential" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Customer Settings: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp"  id="ad-customer-settings" data-editable=true data-columnName="customerSettings" data-value=""></div>
					</div>			
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Transfer Review Policy: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-transfer-review-policy" data-editable=true data-columnName="transferReviewPolicy" data-value=""></div>
					</div>		
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Realtor Surveys: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-realtor-surveys" data-editable=true data-columnName="realtorSurveys" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Happy Workflow Setting: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-happy-workflow-setting" data-editable=true data-columnName="happyWorkflowSettings" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Profile Completion: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-profile-completion" data-editable=false data-columnName="profileCompletion"></div>
					</div>
				</div>
			</div>
		</div>	
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container ad-container">
	<div id="poc-loader" style="display:none"></div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="dash-sub-head float-left">Point of Contact Information</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info-cont">
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 cust-suc-info-cont-left">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Primary POC: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-primary-poc" data-editable=true data-columnName="primaryPoc" data-value=""></div>
					</div>	
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Email: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-email" data-editable=true data-columnName="email" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Secondary Email: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-secondary-email" data-editable=true data-columnName="secondaryEmail" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Phone: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-phone" data-editable=true data-columnName="phone" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Last Conversation Date: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable">
							<input type="hidden" id="ad-last-conv-date-ts" value="0">
							<input class="cust-suc-info-inp" id="ad-last-conv-date" data-editable=true data-columnName="lastConversationDate">
						</div>
					</div>
				</div>
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 cust-suc-info-cont-right">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">POC2: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-poc-2" data-editable=true data-columnName="poc2" data-value=""></div>
					</div>	
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Email POC 2: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-email-poc-2" data-editable=true data-columnName="emailPoc2" data-value=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Phone POC 2: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-editable"><input class="cust-suc-info-inp" id="ad-phone-poc-2" data-editable=true data-columnName="phonePoc2" data-value=""></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container ad-container">
		<div id="serv-det-loader" style="display:none"></div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="dash-sub-head float-left">Service Detail</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info-cont">
				<div class="col-lg-3 col-md-3 col-sm-12 col-xs-12 cust-suc-info-cont-left">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-7 col-md-7 col-sm-7 col-xs-7 cust-suc-info-hdr">Total Users: </div>
						<div class="col-lg-5 col-md-5 col-sm-5 col-xs-5 cust-suc-info-val cust-suc-info-val-fixed" id="ad-totaL-users" data-editable=false data-columnName="userCount"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-7 col-md-7 col-sm-7 col-xs-7 cust-suc-info-hdr">Verified Users: </div>
						<div class="col-lg-5 col-md-5 col-sm-5 col-xs-5 cust-suc-info-val cust-suc-info-val-fixed" id="ad-verified-user" data-editable=false data-columnName="verifiedUserCount"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-7 col-md-7 col-sm-7 col-xs-7 cust-suc-info-hdr">Verified %: </div>
						<div class="col-lg-5 col-md-5 col-sm-5 col-xs-5 cust-suc-info-val cust-suc-info-val-fixed" id="ad-verified-perc" data-editable=false data-columnName="verifiedPercent"></div>
					</div>
					<!-- <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-7 col-md-7 col-sm-7 col-xs-7 cust-suc-info-hdr">30 Day Trend: </div>
						<div class="col-lg-5 col-md-5 col-sm-5 col-xs-5 cust-suc-info-val cust-suc-info-val-fixed" id="ad-30-day-trend" data-editable=false data-columnName=""></div>
					</div>	 -->
				</div>
				<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-cont-right">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 cust-suc-info-hdr">Completed Survey Count: </div>
						<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-val cust-suc-info-val-fixed" id="ad-com-survey-count" data-editable=false data-columnName="completedSurveyCountAllTime"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 cust-suc-info-hdr">Completed Survey Count <br>(90 Days): </div>
						<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-val cust-suc-info-val-fixed" id="ad-com-survey-count-90-days" data-editable=false data-columnName="completedSurveyCount90Days"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 cust-suc-info-hdr">Completed Survey Count <br>(This Year): </div>
						<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-val cust-suc-info-val-fixed" id="ad-com-survey-count-this-year" data-editable=false data-columnName="completedSurveyCountThisYear"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 cust-suc-info-hdr">Completed Survey Count <br>(This Month): </div>
						<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-val cust-suc-info-val-fixed" id="ad-com-survey-count-this-month" data-editable=false data-columnName="completedSurveyCountThisMonth"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 cust-suc-info-hdr">Regions Count: </div>
						<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-val cust-suc-info-val-fixed" id="ad-regions-count" data-editable=false data-columnName="regionCount"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 cust-suc-info-hdr">Branch Count: </div>
						<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-val cust-suc-info-val-fixed" id="ad-branch-count" data-editable=false data-columnName="branchCount"></div>
					</div>
					<!-- <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 cust-suc-info-hdr">Mid-Process Surveys: </div>
						<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-val cust-suc-info-val-fixed" id="ad-mid-process-surveys" data-editable=false data-columnName=""></div>
					</div> -->
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 cust-suc-info-hdr">Company GMB: </div>
						<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 cust-suc-info-val cust-suc-info-val-fixed" id="ad-company-gmb" data-editable=false data-columnName="companyGmb"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container ad-container">
		<div id="ss-data-loader" style="display:none"></div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="dash-sub-head float-left">Social Survey Data</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info-cont">
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 cust-suc-info-cont-left">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Verified GMB For Regions: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-reg-verified-gmb" data-editable=false data-columnName="regionVerifiedGmb"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Missing GMB For Regions: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-reg-missing-gmb" data-editable=false data-columnName="regionMissingGmb"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Region GMB %: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-reg-gmb-perc" data-editable=false data-columnName="regionGmbPercent"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Verified GMB For Branches: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-bra-verified-gmb" data-editable=false data-columnName="branchVerifiedGmb"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Missing GMB For Branches: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-bra-missing-gmb" data-editable=false data-columnName="branchMissingGmb"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Branch GMB %: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-bra-gmb-perc" data-editable=false data-columnName="branchGmbPercent"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Mismatches: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-mismatches" data-editable=false data-columnName="mismatchCount"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Mismatches (90 Days): </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-mismatches-90-days" data-editable=false data-columnName="mismatchCount90Days"></div>
					</div>
					<!-- <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Missing Photos: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-missing-photos-users" data-editable=false data-columnName="missingPhotoCountForUsers"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Missing Photos %: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-missing-photos-users-perc" data-editable=false data-columnName="missingPhotoPercentForUsers"></div>
					</div> -->
					<!-- <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Missing URL's: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-missing-urls" data-editable=false data-columnName=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Missing URLs %: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-missing-urls-perc" data-editable=false data-columnName=""></div>
					</div> -->
					<!-- <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">FB Connects of Agents: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-fb-connects" data-editable=false data-columnName="facebookConnectionCount"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Facebook % of Agents: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-fb-percentage" data-editable=false data-columnName="facebookPercent"></div>
					</div> -->
				</div>
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 cust-suc-info-cont-right">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">FB Connects of Agents: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-fb-connects" data-editable=false data-columnName="facebookConnectionCount"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Facebook % of Agents: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-fb-percentage" data-editable=false data-columnName="facebookPercent"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Twitter Connects of Agents: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-twitter-connects" data-editable=false data-columnName="twitterConnectionCount"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Twitter % of Agents: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-twitter-perc" data-editable=false data-columnName="twitterPercent"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">LinkedIn Connects of Agents: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-linked-in-connects" data-editable=false data-columnName="linkedinConnectionCount"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">LinkedIn % of Agents: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-linked-in-perc" data-editable=false data-columnName="linkedInPercent"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Pixel Campaign: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-pixel-campaign" data-editable=false data-columnName="pixelCampaign"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Missing Photos: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-missing-photos-users" data-editable=false data-columnName="missingPhotoCountForUsers"></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Missing Photos %: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-missing-photos-users-perc" data-editable=false data-columnName="missingPhotoPercentForUsers"></div>
					</div>
					<!-- <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Well Check Status: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-well-check-status" data-editable=false data-columnName=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">WC Previous: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-wc-previous" data-editable=false data-columnName=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">WC Next: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-wc-next" data-editable=false data-columnName=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">GAP Q1 Date: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-gap-q1-date" data-editable=false data-columnName=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">GAP Q2 Date: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-gap-q2-date" data-editable=false data-columnName=""></div>
					</div> -->
				</div>
			</div>
		</div>	
	</div>
</div>

<div class="dash-wrapper-main hide">
	<div class="dash-container container ad-container">
		<div id="zoho-crm-loader" style="display:none"></div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="dash-sub-head float-left">Zoho CRM - Customer Success Details</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info-cont">
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 cust-suc-info-cont-left">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">API Reviews: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-api-reviews" data-editable=false data-columnName="">Yes</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">API Widget: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-api-widget" data-editable=false data-columnName="">Yes</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Awards Event: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-awards-event" data-editable=false data-columnName="">Yes</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Awards Status: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-awards-status" data-editable=false data-columnName="">No</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Awards Month: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-awards-month" data-editable=false data-columnName="">June, January</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Awards Date: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-api-reviews" data-editable=false data-columnName=""></div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Complaint Resolution Workflow: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-api-reviews" data-editable=false data-columnName="">Yes</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Complaint Resolution Email: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-api-reviews" data-editable=false data-columnName="">compliance@alliedmg.com</div>
					</div>
				</div>
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 cust-suc-info-cont-right">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">GAP Q3 Date: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-q3-date" data-editable=false data-columnName="">Nov 1,2018</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">GAP Q4 Date: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-q4-date" data-editable=false data-columnName="">Dec 1,2018</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">Onboarding : </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-onboarding" data-editable=false data-columnName="">No</div>
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-info">
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-hdr">SocialSurvey Widget: </div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 cust-suc-info-val cust-suc-info-val-fixed" id="ad-socialsurvey-widget" data-editable=false data-columnName="">No</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="dash-wrapper-main hide">
	<div class="dash-container container ad-container">
		<div id="notes-loader"></div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="dash-sub-head float-left" style="display:none">Notes</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes-cont">
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes-hdr">CSM Notes</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes-body">
						They've had a few issues with Encompass where some transactions were not surveyed. This issue was fixed but need to keep a close eye. Bill Matthews [bill@alliedmg.com] is the Encompass contact.
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes-info">Customer Success - Allied Mortgage • Aug 13 6:36 PM by Cristine Blanco</div>
				</div>
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes-hdr"></div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes-body">
						May 31 - SocialMonitor Demo with Lauren & Scott. Need to send follow up details for	approval for summit					
					</div>
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes-info">Customer Success - Allied Mortgage • May 31 02:42 PM by Cristine Blanco</div>
				</div>
			</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 cust-suc-notes-pagination">
				<div class="cust-suc-notes-prev-notes">View Previous Notes</div>
				<div class="cust-suc-notes-count">
					<div class="cust-suc-notes-cur-count">3</div>
					<div class="cust-suc-notes-count-of">of</div>
					<div class="cust-suc-notes-total-count">8</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
 $(document).ready(function(){
	 bindDatePickerForAdminDashboard();
	 fetchCompanyStatistics();
	 fetchCustomerSuccessInfo();
	 getSocialSurveyAdmins();
	 maskPhoneInputs();
 });
</script>
				