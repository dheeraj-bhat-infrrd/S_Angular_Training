<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<style>
.ht-txt2{
    padding-right: 49px;
}
</style>
<div id="time-frame-wrapper" class="float-right clearfix hr-dsh-adj-lft hdr-prof-sel time-frame-prof-sel" style="margin-top:7px; margin-right: 10px;">
	<div id="time-frame-sel" data-column-value="100" class="float-left hr-txt2 cursor-pointer time-frame-sel" >All Time</div>	
	<div id="time-frame-options" class="time-frame-wrapper hide">
		<div class="time-frame-item" data-column-value="100">All Time</div>
		<div class="time-frame-item" data-column-value="101">This Month</div>
		<div class="time-frame-item" data-column-value="102">Last Month</div>
		<div class="time-frame-item" data-column-value="103">This Year</div>
		<div class="time-frame-item" data-column-value="104">Last Year</div>
	</div>
</div>

<script>
	$(document).ready(function(){
		drawTimeFrames();
		
		$('#timeFrame_container').unbind('click').on('click', '#time-frame-sel', function(e) {
			e.stopPropagation();
			$('#time-frame-options').slideToggle(200);
		});
		
		$('#timeFrame_container').on('click', '.time-frame-item', function(e) {
			showOverlay();
			var time=$(this).html();
			$('#time-frame-sel').html($(this).html());
			$('#time-frame-options').slideToggle(200);
			
			var timeFrame = $(this).attr('data-column-value');
			$('#time-frame-sel').attr('data-column-value',timeFrame);

			$('#reporting-trans-details').fadeOut(500);
			$('#reporting-trans-details').fadeIn(500);
			updateReportingDashboard();
			
		});
	});
</script>