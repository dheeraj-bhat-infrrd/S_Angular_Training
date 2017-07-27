<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
.inline-flex-class{
	display:inline-flex;
	 width: 72%;
	 margin-bottom:15px;
}

.processed-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #0072c2;
    border-radius: 3px;
    margin: auto 0;
}

.completed-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #79b600;
    border-radius: 3px;
    margin: auto 0;
}

.incomplete-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #f5c70a;
    border-radius: 3px;
    margin: auto 0;
}

.unprocessed-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #fa5b00;
    border-radius: 3px;
    margin: auto 0;
}

.unassigned-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #f5c70a;
    border-radius: 3px;
    margin: auto 0;
}

.duplicate-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #8445d1;
    border-radius: 3px;
    margin: auto 0;
}

.corrupted-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #ea310b;
    border-radius: 3px;
    margin: auto 0;
}

.other-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #000000;
    border-radius: 3px;
    margin: auto 0;
}

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

#processed-lbl,#unprocessed-lbl{
	margin-left: 15px;
    margin-right: 90px;
}

#completed-lbl{
	margin-left: 15px;
    margin-right: 86px;
}

#incompleted-lbl{
	margin-left: 15px;
    margin-right: 84px;
}

#social-posts-lbl{
	margin-left: 15px;
    margin-right: 81px;
}

#zillow-lbl{
	margin-left: 15px;
    margin-right: 65px;
}

#unassigned-lbl{
	margin-left: 15px;
    margin-right: 99px;
}

#duplicate-lbl{
	margin-left: 15px;
    margin-right: 114px;
}

#corrupted-lbl{
	margin-left: 15px;
    margin-right: 110px;
}

#other-lbl{
	margin-left: 15px;
    margin-right: 141px;
}

.trans-font-style{
	font-weight: 600 !important;
    font-size: 14px;
}

.social-posts-lbl-rect-div{
	width: 15px;
    height: 15px;
    background: #D2DEDF;
    border-radius: 3px;
    margin: auto 0;
}
.background-rect,.processed-background-rect,.unprocessed-background-rect{
	width: 15px;
    height: 15px;
    background: #f9f9fb;
    border-radius: 3px;
    margin: auto 0;
}

#incompleted-lbl-sel-span:hover,#unassigned-lbl-sel-span:hover{
	background:#bab8b8;
}
</style>
<div id="processed-trans-div" class="col-lg-4 col-md-4 col-sm-4 col-xs-4 cursor-pointer">
	<div id="processed-div" style="display:inline-grid; padding-left: 45px;">
		<div id="processed-details" class="inline-flex-class" style="margin-bottom: 0px; margin-top: 45px;">
			<div id="processed-background-rect" class="background-rect hide"></div>
			<div id="processed-lbl-rect" class="processed-lbl-rect-div"></div>
			<div id="processed-lbl"><span class="trans-font-style">Processed</span></div>
			<div id="processed-lbl-value"><span id="processed-lbl-span" class="trans-font-style"></span></div>
		</div>
		<hr/>
		<div id="completed-details" class="inline-flex-class">
			<div class="processed-background-rect"></div>
			<div id="completed-lbl-rect" class="completed-lbl-rect-div hide"></div>
			<div id="completed-lbl"><span class="trans-font-style">Completed</span></div>
			<div id="completed-lbl-value"><span id="completed-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="incompleted-details-selectable" class="inline-flex-class">
			<div class="processed-background-rect" style="margin-right: 5px;"></div>
			<div id="incompleted-lbl-rect-sel" class="incomplete-lbl-rect-div hide"></div>
			<div id="incompleted-lbl-sel" class="cursor-pointer" style="margin-right: 76px;">
			<span id="incompleted-lbl-sel-span" class="trans-font-style" style="border: 1px solid black;border-radius: 3px;padding: 2px 10px;">Incomplete</span></div>
			<div id="incompleted-lbl-value-sel"><span id="incomplete-lbl-span-sel" class="trans-font-style"></span></div>
		</div>
		<div id="incompleted-details" class="hide">
			<div class="processed-background-rect"></div>
			<div id="incompleted-lbl-rect" class="incomplete-lbl-rect-div hide"></div>
			<div id="incompleted-lbl"><span class="trans-font-style">Incomplete</span></div>
			<div id="incompleted-lbl-value"><span id="incomplete-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="social-posts-details" class="inline-flex-class" >
			<div class="processed-background-rect"></div>
			<div id="social-posts-lbl-rect" class="social-posts-lbl-rect-div hide"></div>
			<div id="social-posts-lbl"><span class="trans-font-style">Social Posts</span></div>
			<div id="social-posts-lbl-value"><span id="social-posts-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="zillow-details" class="inline-flex-class" style="margin-bottom:45px;">
			<div class="processed-background-rect"></div>
			<div id="zillow-lbl-rect" class="social-posts-lbl-rect-div hide"></div>
			<div id="zillow-lbl"><span class="trans-font-style">Zillow Reviews</span></div>
			<div id="zillow-lbl-value"><span id="zillow-lbl-span" class="trans-font-style"></span></div>
		</div>
			
	</div>
</div>
<div id="rep-trans-graphs" class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
	<div id="unclicked-trans-graph" style="margin-top:35px" >
		<div id="unclicked-graph-div" style="position:relative">
			<div id="donutchart" style="width: 100%; height: 90%;"></div>
		</div>	
	</div>
	<div id="processed-trans-graph" style="margin-top:35px" class="hide">
		<div id="processed-graph-div" style="position:relative">
			<div id="processedDonutchart" style="width: 100%; height: 90%;"></div>
		</div>
	</div>
	<div id="unprocessed-trans-graph" style="margin-top:35px" class="hide">
		<div id="unprocessed-graph-div" style="position:relative">
			<div id="unprocessedDonutchart" style="width: 100%; height: 90%;"></div>
		</div>
	</div>
	<div id="empty-rep-chart-div" class="hide" style="margin: 124px 0;">
		<div style="text-align:center; margin:5% auto">
			<span class="incomplete-trans-span" style="font-size:large">You have no</span>
			<div style="clear: both">
				<span class="incomplete-trans-span" style="font-size:larger">Transactions for your account</span> 
			</div>
		</div>
	</div>
</div>
<div id="unprocessed-trans-div" class="col-lg-4 col-md-4 col-sm-4 col-xs-4 cursor-pointer">
	<div id="unprocessed-div" style="display:inline-grid; padding-left: 45px; width: 100%;">
		<div id="unprocessed-details" class="inline-flex-class" style="margin-bottom: 0px; margin-top: 45px;">
			<div id="unprocessed-background-rect" class="background-rect hide"></div>
			<div id="unprocessed-lbl-rect" class="unprocessed-lbl-rect-div"></div>
			<div id="unprocessed-lbl"><span class="trans-font-style">Unprocessed</span></div>
			<div id="unprocessed-lbl-value"><span id="unprocessed-lbl-span" class="trans-font-style"></span></div>
		</div>
		<hr/>
		
		<div id="unassigned-details-selectable" class="inline-flex-class">
			<div class="unprocessed-background-rect" style="margin-right: 5px;"></div>
			<div id="unassigned-lbl-rect-sel" class="unassigned-lbl-rect-div hide"></div>
			<div id="unassigned-lbl-sel" class="cursor-pointer" style="margin-right: 87px;">
			<span id="unassigned-lbl-sel-span" class="trans-font-style" style="border: 1px solid black;border-radius: 3px;padding: 2px 10px;">Unassigned</span></div>
			<div id="unassigned-lbl-value-sel"><span id="unassigned-lbl-span-sel" class="trans-font-style"></span></div>
		</div>
		
		<div id="unassigned-details" class="hide">
			<div class="unprocessed-background-rect"></div>
			<div id="unassigned-lbl-rect" class="unassigned-lbl-rect-div hide"></div>
			<div id="unassigned-lbl"><span class="trans-font-style">Unassigned</span></div>
			<div id="unassigned-lbl-value"><span id="unassigned-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="duplicate-details" class="inline-flex-class">
			<div class="unprocessed-background-rect"></div>
			<div id="duplicate-lbl-rect" class="duplicate-lbl-rect-div hide"></div>
			<div id="duplicate-lbl"><span class="trans-font-style">Duplicate</span></div>
			<div id="duplicate-lbl-value"><span id="duplicate-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="corrupted-details" class="inline-flex-class" >
			<div class="unprocessed-background-rect"></div>
			<div id="corrupted-lbl-rect" class="corrupted-lbl-rect-div hide"></div>
			<div id="corrupted-lbl"><span class="trans-font-style">Corrupted</span></div>
			<div id="corrupted-lbl-value"><span id="corrupted-lbl-span" class="trans-font-style"></span></div>
		</div>
		<div id="other-details" class="inline-flex-class" style="margin-bottom:85px;">
			<div class="unprocessed-background-rect"></div>
			<div id="other-lbl-rect" class="other-lbl-rect-div hide"></div>
			<div id="other-lbl"><span class="trans-font-style">Other</span></div>
			<div id="other-lbl-value"><span id="other-lbl-span" class="trans-font-style">26</span></div>
		</div>
			
	</div>
</div>
<script>
$(document).ready(function(){
	$(document).on('click','#incompleted-lbl-sel',function(e){
		e.stopPropagation();
		activaTab('incomplete-surveys-tab');
	});
	
	$(document).on('click','#unassigned-lbl-sel',function(e){
		e.stopPropagation();
		activaTab('incomplete-surveys-tab');
	});
	
	$(document).on('click','#unprocessed-trans-div',function(e){
		e.stopPropagation();
		$(this).css('opacity','1.0');
		$('#incompleted-details-selectable').hide();
		$('#incompleted-details').removeClass('hide');
		$('#incompleted-details').addClass('inline-flex-class');
		$('#unassigned-details-selectable').hide();
		$('#unassigned-details').removeClass('hide');
		$('#unassigned-details').addClass('inline-flex-class');
		$('.processed-background-rect').show();
		$('#processed-background-rect').show();
		$('#processed-lbl-rect').hide();
		$('#completed-lbl-rect').hide();
		$('#incompleted-lbl-rect').hide();
		$('#social-posts-lbl-rect').hide();
		$('#zillow-lbl-rect').hide();
		$('#unassigned-lbl-rect').show();
		$('#duplicate-lbl-rect').show();
		$('#corrupted-lbl-rect').show();
		$('#other-lbl-rect').show();
		$('#processed-trans-div').fadeTo('fast','0.2');
		$('#unclicked-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').removeClass('hide');
		
	});
	
	$(document).on('click','#processed-trans-div',function(e){
		e.stopPropagation();
		$(this).css('opacity','1.0');
		$('#incompleted-details-selectable').hide();
		$('#incompleted-details').removeClass('hide');
		$('#incompleted-details').addClass('inline-flex-class');
		$('#unassigned-details-selectable').hide();
		$('#unassigned-details').removeClass('hide');
		$('#unassigned-details').addClass('inline-flex-class');
		$('.unprocessed-background-rect').show();
		$('#unprocessed-background-rect').show();
		$('#unprocessed-lbl-rect').hide();
		$('#completed-lbl-rect').show();
		$('#incompleted-lbl-rect').show();
		$('#social-posts-lbl-rect').show();
		$('#zillow-lbl-rect').show();
		$('#unassigned-lbl-rect').hide();
		$('#duplicate-lbl-rect').hide();
		$('#corrupted-lbl-rect').hide();
		$('#other-lbl-rect').hide();
		$('#unprocessed-trans-div').fadeTo('fast','0.2');
		$('#unclicked-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#processed-trans-graph').removeClass('hide');
		
	});
	
	drawUnclickedDonutChart();
	drawProcessedDonutChart();
	drawUnprocessedDonutChart();
	
	var currentDate =  new Date();
	var currentMonth = currentDate.getMonth();
	var currentYear = currentDate.getFullYear();
	
	var monthYear = getTimeFrameValue();
	var overviewYearData;
	
    if(monthYear.month == 13){
    	overviewYearData =  getoverviewYearData(monthYear.year);
    }else{
    	overviewYearData = getOverviewMonthData(monthYear.month, monthYear.year);
    }
	
	if(overviewYearData!=null){
		$('#processed-lbl-span').html(overviewYearData.Processed);
		$('#completed-lbl-span').html(overviewYearData.Completed+' ('+overviewYearData.CompletePercentage+'%)');
		$('#incomplete-lbl-span').html(overviewYearData.Incomplete+' ('+overviewYearData.IncompletePercentage+'%)');
		$('#incomplete-lbl-span-sel').html(overviewYearData.Incomplete);
		$('#social-posts-lbl-span').html(overviewYearData.SocialPosts);
		$('#zillow-lbl-span').html(overviewYearData.ZillowReviews);
		$('#unprocessed-lbl-span').html(overviewYearData.Unprocessed);
		$('#unassigned-lbl-span').html(overviewYearData.Unassigned);
		$('#duplicate-lbl-span').html(overviewYearData.Duplicate);
		$('#unassigned-lbl-span-sel').html(overviewYearData.Unassigned);
		$('#corrupted-lbl-span').html(overviewYearData.Corrupted);
		var other = overviewYearData.Unprocessed - (overviewYearData.Unassigned + overviewYearData.Duplicate + overviewYearData.Corrupted);
		$('#other-lbl-span').html(other);
		$('#unclicked-trans-graph').removeClass('hide');
		$('#processed-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#empty-rep-chart-div').addClass('hide');
	}else{
		$('#processed-lbl-span').html(0);
		$('#completed-lbl-span').html(0+' ('+0+'%)');
		$('#incomplete-lbl-span').html(0+' ('+0+'%)');
		$('#incomplete-lbl-span-sel').html(0);
		$('#social-posts-lbl-span').html(0);
		$('#zillow-lbl-span').html(0);
		$('#unprocessed-lbl-span').html(0);
		$('#unassigned-lbl-span').html(0);
		$('#duplicate-lbl-span').html(0);
		$('#unassigned-lbl-span-sel').html(0);
		$('#corrupted-lbl-span').html(0);
		$('#other-lbl-span').html(0);
		$('#unclicked-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#empty-rep-chart-div').removeClass('hide');
	}
	
	
	console.log(overviewYearData);	
	
	
});
</script>