<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div id="time-frame-wrapper" class="float-left clearfix hr-dsh-adj-lft hdr-prof-sel time-frame-prof-sel">
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
		var currentDate = new Date();
		var currentMonth = currentDate.getMonth();
		var currentYear = currentDate.getFullYear();
		
		var monthStr = new Array();
		monthStr[0] = "Jan";
		monthStr[1] = "Feb";
		monthStr[2] = "Mar";
		monthStr[3] = "Apr";
		monthStr[4] = "May";
		monthStr[5] = "Jun";
		monthStr[6] = "Jul";
		monthStr[7] = "Aug";
		monthStr[8] = "Sep";
		monthStr[9] = "Oct";
		monthStr[10] = "Nov";
		monthStr[11] = "Dec";
		
		var monthJspStr='';
		var count=4;
		
		if(currentMonth > 1){
			var month = currentMonth - 2;
			
			while(month >= 0 && count-- > 0){
				monthJspStr += '<div class="time-frame-item" data-column-value="' + (month+1) + '">' + monthStr[month] + ' ' + currentYear + '</div>' ;
				month--;
			}
			
			$('#time-frame-options').append(monthJspStr);
		}
		
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
			hideOverlay();
			
		});
	});
</script>