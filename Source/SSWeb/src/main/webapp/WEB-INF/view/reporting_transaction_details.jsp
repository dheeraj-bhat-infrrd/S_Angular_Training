<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>


hr{
	width: 265px;
    margin-top: 15px;
    margin-bottom: 15px;
    border: 0;
    border-top: 1px solid #d4d4d3;
    box-shadow: 0 0 10px #fff;
    float: left;
    margin-left: 25px;
}

.rep-trans-graphs{
	background: #f1f0f2;
    height: 306px;
    border-left: 1px solid #d2cdcd;
    border-right: 1px solid #d2cdcd;
    box-shadow: -5px 0 5px -2px #d2cdcd, 5px 0 5px -2px #d2cdcd;
}

.unclicked-chart-icn{
	width: auto;
    height: auto !important;
    max-width: 100%;
    max-height: 100%;
    display: block;
    position: absolute;
    margin: auto;
    top: 5px;
    left: 13px;
}

</style>

<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 hm-header-main-wrapper hm-hdr-bord-bot" style="background: #2f69aa;margin-bottom: 5px;">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div id="timeFrame_container">
				<jsp:include page="reporting_timeFrame_dropdown.jsp"></jsp:include>
			</div>
		</div>
	</div>
</div>

<div id="processed-trans-div" class="col-lg-4 col-md-4 col-sm-4 col-xs-4 cursor-pointer processed-trans-div">
	<div id="processed-div" class="processed-trans-cont" style="">
		<div id="processed-details" class="inline-flex-class" style="margin-bottom: 0px; margin-top: 45px;">
			<div id="processed-background-rect" class="background-rect hide"></div>
			<div id="processed-lbl-rect" class="processed-lbl-rect-div"></div>
			<div id="processed-lbl"><span class="trans-font-style">Processed</span></div>
			<div id="processed-lbl-value"><span id="processed-lbl-span" class="trans-font-style"></span></div>
		</div>
		<hr/>
		<div id="completed-details" class="inline-flex-class">
			<div class="processed-background-rect"></div>
			<div id="completed-lbl-rect" class="completed-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="completed-lbl"><span class="trans-font-style">Completed</span></div>
			<div id="completed-lbl-value"><span id="completed-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="incompleted-details-selectable" class="inline-flex-class">
			<div class="processed-background-rect" style="margin-right: 5px;"></div>
			<div id="incompleted-lbl-rect-sel" class="incomplete-lbl-rect-div hide"></div>
			<div id="incompleted-lbl-sel" class="cursor-pointer" style="margin-right: 72px;">
			<span id="incompleted-lbl-sel-span" class="trans-font-style" style="border: 1px solid black;border-radius: 3px;padding: 2px 10px;">Incomplete</span></div>
			<div id="incompleted-lbl-value-sel"><span id="incomplete-lbl-span-sel" class="trans-font-style"></span></div>
		</div>
		<div id="incompleted-details" class="hide">
			<div class="processed-background-rect"></div>
			<div id="incompleted-lbl-rect" class="incomplete-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="incompleted-lbl"><span class="trans-font-style">Incomplete</span></div>
			<div id="incompleted-lbl-value"><span id="incomplete-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="social-posts-details" class="inline-flex-class" >
			<div class="processed-background-rect"></div>
			<div id="social-posts-lbl-rect" class="social-posts-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="social-posts-lbl"><span class="trans-font-style">Social Posts</span></div>
			<div id="social-posts-lbl-value"><span id="social-posts-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="zillow-details" class="inline-flex-class">
			<div class="processed-background-rect"></div>
			<div id="zillow-lbl-rect" class="social-posts-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="zillow-lbl"><span class="trans-font-style">Zillow Reviews</span></div>
			<div id="zillow-lbl-value"><span id="zillow-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="third-party-reviews" class="inline-flex-class">
			<div class="processed-background-rect"></div>
			<div id="third-party-lbl-rect" class="social-posts-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="third-party-lbl"><span class="trans-font-style">Third Party Reviews</span></div>
			<div id="third-party-lbl-value"><span id="third-party-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="google-reviews" class="inline-flex-class">
			<div class="processed-background-rect"></div>
			<div id="google-lbl-rect" class="social-posts-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="google-lbl"><span class="trans-font-style">Google Reviews</span></div>
			<div id="google-lbl-value"><span id="google-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="facebook-reviews" class="inline-flex-class" style="margin-bottom:30px;">
			<div class="processed-background-rect"></div>
			<div id="facebook-lbl-rect" class="social-posts-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="facebook-lbl"><span class="trans-font-style">Facebook Reviews</span></div>
			<div id="facebook-lbl-value"><span id="facebook-lbl-span" class="trans-font-style"></span></div>
		</div>
	</div>
</div>
<div id="rep-trans-graphs" class="col-lg-4 col-md-4 col-sm-4 col-xs-4 rep-trans-graphs" style="height: 341px;">
	<div id="unclicked-graph-dash" class="hide" ></div>
	<div id="chart-icn-btn" style="display: -webkit-box; display:inline-flex; margin-left: -10px;" class="cursor-pointer">
		<!--  img id="unclicked-chart-icn" class="unclicked-chart-icn prof-image-edit pos-relative cursor-pointer" src="${initParam.resourcesPath}/resources/images/unclicked-chart-icon-small.png"></img-->
		<div id="chart-icn-graph-div" style="position:relative">
			<div id="chart-icn-chart" style=""></div>
		</div>
		<div class="prof-addline2 prof-name-txt rep-dsh-medium-text dsh-txt-2" style="padding-left: 7px; line-height: 18px; text-decoration: underline; border-left: 1px solid #d2cdcd;margin-top: 7px; height: 20px; font-size: small;">Transactions</div>
	</div>
	<div id="empty-rep-chart-div" class="hide" style="margin: 124px 0;">
		<div style="text-align:center; margin:5% auto">
			<span class="incomplete-trans-span" style="font-size:large">You have no</span>
			<div style="clear: both">
				<span class="incomplete-trans-span" style="font-size:large">Transactions for your account</span> 
			</div>
		</div>
	</div>
	<div id="unclicked-trans-graph" class="col-lg-12 col-md-12 col-sm-12 col-xs-12" style="margin-top:40px; padding:0" >
		<div id="unclicked-graph-div" style="position:relative">
			<div id="donutchart" style="width: 100%; height: 90%;"></div>
		</div>	
	</div>
	<div id="processed-trans-graph" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 hide" style="margin-top:40px; padding:0">
		<div id="processed-graph-div" style="position:relative">
			<div id="processedDonutchart" style="width: 100%; height: 90%;"></div>
		</div>
	</div>
	<div id="unprocessed-trans-graph" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 hide" style="margin-top:40px; padding:0">
		<div id="unprocessed-graph-div" style="position:relative">
			<div id="unprocessedDonutchart" style="width: 100%; height: 90%;"></div>
		</div>
	</div>
</div>
<div id="unprocessed-trans-div" class="col-lg-4 col-md-4 col-sm-4 col-xs-4 cursor-pointer unprocessed-trans-div">
	<div id="unprocessed-div" class="processed-trans-cont">
		<div id="unprocessed-details" class="inline-flex-class" style="margin-bottom: 0px; margin-top: 45px;">
			<div id="unprocessed-background-rect" class="background-rect hide"></div>
			<div id="unprocessed-lbl-rect" class="unprocessed-lbl-rect-div"></div>
			<div id="unprocessed-lbl"><span class="trans-font-style">Unprocessed</span></div>
			<div id="unprocessed-lbl-value"><span id="unprocessed-lbl-span" class="trans-font-style"></span></div>
		</div>
		<hr/>
		
		<div id="unassigned-details-selectable" class="inline-flex-class">
			<div class="unprocessed-background-rect" style="margin-right: 5px;"></div>
			<div id="unassigned-lbl-rect-sel" class="unassigned-lbl-rect-div hide" ></div>
			<div id="unassigned-lbl-sel" class="cursor-pointer" style="margin-right: 87px;">
			<span id="unassigned-lbl-sel-span" class="trans-font-style" style="border: 1px solid black;border-radius: 3px;padding: 2px 10px;">Unassigned</span></div>
			<div id="unassigned-lbl-value-sel"><span id="unassigned-lbl-span-sel" class="trans-font-style"></span></div>
		</div>
		
		<div id="unassigned-details" class="hide">
			<div class="unprocessed-background-rect"></div>
			<div id="unassigned-lbl-rect" class="unassigned-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="unassigned-lbl"><span class="trans-font-style">Unassigned</span></div>
			<div id="unassigned-lbl-value"><span id="unassigned-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="duplicate-details" class="inline-flex-class">
			<div class="unprocessed-background-rect"></div>
			<div id="duplicate-lbl-rect" class="duplicate-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="duplicate-lbl"><span class="trans-font-style">Duplicate</span></div>
			<div id="duplicate-lbl-value"><span id="duplicate-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="corrupted-details" class="inline-flex-class" >
			<div class="unprocessed-background-rect"></div>
			<div id="corrupted-lbl-rect" class="corrupted-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="corrupted-lbl"><span class="trans-font-style">Corrupted</span></div>
			<div id="corrupted-lbl-value"><span id="corrupted-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="other-details" class="inline-flex-class">
			<div class="unprocessed-background-rect"></div>
			<div id="other-lbl-rect" class="other-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="other-lbl"><span class="trans-font-style">Other</span></div>
			<div id="other-lbl-value"><span id="other-lbl-span" class="trans-font-style">26</span></div>
		</div>
		<div id="unsubscribed-count" class="inline-flex-class" style="margin-bottom:75px;">
			<div class="unprocessed-background-rect"></div>
			<div id="unsubscribed-lbl-rect" class="social-posts-lbl-rect-div hide" style="margin-left: -15px;"></div>
			<div id="unsubscribed-lbl"><span class="trans-font-style">Unsubscribed</span></div>
			<div id="unsubscribed-lbl-value"><span id="unsubscribed-lbl-span" class="trans-font-style"></span></div>
		</div>
	</div>
</div>