<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
	.time-frame-item{
		cursor: pointer;
		font-size: 12px;
		line-height: 15px;
		padding: 7px 5px;
		border-bottom: 1px solid #eee;
	}
	
	.time-frame-wrapper{
		z-index: 999;
   		border: 1px solid #dcdcdc;
    	position: absolute;
    	background-color: #fff;
    	border-radius: 3px;
    	width: 129px;
    	color: #666;
    	text-align: left;
    	top: 110px;
    	left: 234px;
    	max-height: 400px;
    	box-shadow: 3px 3px 30px #444;
	}
	
	.time-frame-prof-sel{
		border: 1px solid #d2cdcd;
   		background: #ffffff;
    	height: 40px;
    	border-radius: 3px;
	}
	
	.time-frame-item:hover{
		color: #fff;
		background-color: #5cc7ef;
	}
</style>
<div id="time-frame-wrapper" class="float-left clearfix hr-dsh-adj-lft hdr-prof-sel time-frame-prof-sel">
	<div id="time-frame-sel" class="float-left hr-txt2 cursor-pointer" style="height:90%; line-height:40px !important;">This Month</div>	
	<div id="time-frame-options" class="time-frame-wrapper hide">
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
		
		if(currentMonth > 1){
			var month = currentMonth - 2;
			
			while(month >= 0){
				monthJspStr += '<div class="time-frame-item" data-column-value="' + month + '">' + monthStr[month] + ' ' + currentYear + '</div>' ;
				month--;
			}
			
			$('#time-frame-options').append(monthJspStr);
		}
		
		$(document).on('click', '#time-frame-sel', function(e) {
			e.stopPropagation();
			$('#time-frame-options').slideToggle(200);
		});
	});
</script>