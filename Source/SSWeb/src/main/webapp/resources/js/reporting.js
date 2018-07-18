//javascript for populating the graphs in reporting dashboard overview

var monthNamesList = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];	
var overviewData=null;
var socialMediaList = new Array();
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var unclickedDrawnCount=0;
var isUpdateTransStats = false;
function drawTimeFrames(){
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
	var month;
	
	if(currentMonth > 1){
		month = currentMonth - 2;
		
		while(month >= 0 && count-- > 0){
			monthJspStr += '<div class="time-frame-item" data-year="'+currentYear+'" data-column-value="' + (month+1) + '">' + monthStr[month] + ' ' + currentYear + '</div>' ;
			month--;
		}
		
		if(currentMonth == 2){
			monthJspStr += '<div class="time-frame-item" data-year="'+(currentYear-1)+'" data-column-value="12">' + monthStr[11] + ' ' + (currentYear-1) + '</div>' ;
			monthJspStr += '<div class="time-frame-item" data-year="'+(currentYear-1)+'" data-column-value="11">' + monthStr[10] + ' ' + (currentYear-1) + '</div>' ;
			monthJspStr += '<div class="time-frame-item" data-year="'+(currentYear-1)+'" data-column-value="10">' + monthStr[9] + ' ' + (currentYear-1) + '</div>' ;
		}else if(currentMonth == 3){
			monthJspStr += '<div class="time-frame-item" data-year="'+(currentYear-1)+'" data-column-value="12">' + monthStr[11] + ' ' + (currentYear-1) + '</div>' ;
			monthJspStr += '<div class="time-frame-item" data-year="'+(currentYear-1)+'" data-column-value="11">' + monthStr[10] + ' ' + (currentYear-1) + '</div>' ;
		}else if(currentMonth == 4){
			monthJspStr += '<div class="time-frame-item" data-year="'+(currentYear-1)+'" data-column-value="12">' + monthStr[11] + ' ' + (currentYear-1) + '</div>' ;
		}
		
	}else{
		if(currentMonth == 1){
			month=11;
		}else{
			month=10;
		}
			count=4;
			while(count-- > 0){
				monthJspStr += '<div class="time-frame-item" data-year="'+(currentYear-1)+'" data-column-value="' + (month+1) + '">' + monthStr[month] + ' ' + (currentYear-1) + '</div>' ;
				month--;
			}
	}
	$('#time-frame-options').append(monthJspStr);
}

function drawTransReportTimeFrames(){
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
	var month;
	var counter = 6;
	
	if(currentMonth > 1){
		month = currentMonth - 2;
		
		while(month >= 0 && count-- > 0){
			monthJspStr += '<option value="'+(counter++)+'" data-year="'+currentYear+'" data-month="' + (month+1) + '">' + monthStr[month] + ' ' + currentYear + '</option>' ;
			month--;
		}
		
		if(currentMonth == 2){
			monthJspStr += '<option value="'+(counter++)+'" data-year="'+(currentYear-1)+'" data-month="12">' + monthStr[11] + ' ' + (currentYear-1) + '</option>' ;
			monthJspStr += '<option value="'+(counter++)+'" data-year="'+(currentYear-1)+'" data-month="11">' + monthStr[10] + ' ' + (currentYear-1) + '</option>' ;
			monthJspStr += '<option value="'+(counter)+'" data-year="'+(currentYear-1)+'" data-month="10">' + monthStr[9] + ' ' + (currentYear-1) + '</option>' ;
		}else if(currentMonth == 3){
			monthJspStr += '<option value="'+(counter++)+'" data-year="'+(currentYear-1)+'" data-month="12">' + monthStr[11] + ' ' + (currentYear-1) + '</option>' ;
			monthJspStr += '<option value="'+(counter)+'" data-year="'+(currentYear-1)+'" data-month="11">' + monthStr[10] + ' ' + (currentYear-1) + '</option>' ;
		}else if(currentMonth == 4){
			monthJspStr += '<option value="'+(counter)+'" data-year="'+(currentYear-1)+'" data-month="12">' + monthStr[11] + ' ' + (currentYear-1) + '</option>' ;
		}
		
	}else{
		if(currentMonth == 1){
			month=11;
		}else{
			month=10;
		}
			count=4;
			while(count-- > 0){
				monthJspStr += '<option value="'+(counter++)+'" data-year="'+(currentYear-1)+'" data-month="' + (month+1) + '">' + monthStr[month] + ' ' + (currentYear-1) + '</div>' ;
				month--;
			}
	}
	$('#trans-report-time-selector').append(monthJspStr);
}

function cssForSafari(){
	if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1) {
		is_safari = true;
		$('.rep-dash-bar-margin').css('margin', 'auto 2px auto 10px');
	}
}

function isEmpty(obj) {
    for(var key in obj) {
        if(obj.hasOwnProperty(key))
            return false;
    }
    return true;
}

function drawTransactionDetailsTab(){
	showDashOverlay('#trans-stats-dash');
	$.ajax({
		url : './reportingtransactiondetails.do',
		type : "GET",
		cache : false,
		success : function(data){
			$('#reporting-trans-details').append(data);
			paintForReportingDash();
		},
		complete: function(){
			hideDashOverlay('#trans-stats-dash');
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function paintForReportingDash() {
	
	showDashOverlay('#unclicked-graph-dash');
	var monthYear = getTimeFrameValue();
	var overviewYearData = null;
	
	if(monthYear.month == 14){
		getoverviewAllTimeData();
	}else if(monthYear.month == 13){
    	getoverviewYearData(monthYear.year);
    }else{
    	getOverviewMonthData(monthYear.month, monthYear.year);
    }
	
}

function drawLeaderboardPage(columnName, columnId,profileMasterId,userId,companyId){
	showDashOverlay('#leaderboard-dash');
	var batchSize = 10;
	var startIndex=0;
	var count=0;
	var timeFrame = 1;
	var currentDate = new Date();
	var currentYear = currentDate.getFullYear();
	var currentMonth = currentDate.getMonth()+1;
	var userRankingCount =null;
	
	if(profileMasterId != 4){
		userRankingCount = getUserRankingCountForAdmins(columnName, columnId, currentYear, currentMonth, batchSize, timeFrame)
		if(userRankingCount != null){
			startIndex= 0;
			count=userRankingCount.Count;
		}
	}else{
		userRankingCount = getUserRankingCount("companyId", companyId, currentYear, currentMonth, batchSize, timeFrame);
		if(userRankingCount != null){
			startIndex= userRankingCount.startIndex;
			count=userRankingCount.Count;
		}
	}
	
	$('#rank-count').html('/'+count);
	
	var userRankingList = null;
	if(profileMasterId != 4){
		getUserRankingList(columnName,columnId, currentYear, currentMonth, startIndex, batchSize, timeFrame);
	}else{
		getUserRankingList("companyId",companyId, currentYear, currentMonth, startIndex, batchSize, timeFrame);
	}
	
	showHideRankPaginateBtns(startIndex, count);
	$('#user-ranking-data').attr('data-start-index',startIndex);
	$('#user-ranking-data').attr('data-count',count);

}

function leaderboardPageStructure(userRankingList){
	var tableData='';
	var userIdStr = $('#reporting-data-div').attr('data-user-id');
	var userId = parseInt(userIdStr);
	var profileMasterIdStr = $('#reporting-data-div').attr('data-profile-master-id');
	var profileMasterId = parseInt(profileMasterIdStr);
	
	if(userRankingList != null && userRankingList.length != 0){
		tableData=drawLeaderboardTableStructure(userRankingList, userId,profileMasterId);
		$('#leaderboard-list').removeClass('hide');
		$('#leaderboard-tbl').html(tableData);
		$('#leaderboard-empty-list-msg-div').addClass('hide');
	}else{
		$('#leaderboard-list').addClass('hide');
		$('#leaderboard-empty-list-msg-div').removeClass('hide');
	}
}

//draw sps graphs and completion rate graphs functions
function drawSpsStatsGraph(){
	showDashOverlay('#sps-dash');
	var chartData;
	var spsChartData = [[ 'SPS', 'Detractors', 'Passives', 'Promoters'],[]];
	google.charts.load('current', {	packages : [ 'corechart', 'bar' ]});
	google.charts.setOnLoadCallback(drawStacked);
	
	function drawEmptyChart(){
		var spsChartData = [
							[ 'SPS', 'Detractors','Passives','Promoters' ],
							[ '', 0, 0, 0 ] ];

					var data = google.visualization
							.arrayToDataTable(spsChartData);

					var options = {
						legend : {
							position : 'none'
						},
						isStacked : true,
						vAxis : {
							minValue : 0,
							maxValue : 10,
							gridlines : {
								count : 5
							}
						},
						colors : [ '#E8341F',
								'#999999', '#7ab400' ]
					};

					var chart = new google.visualization.ColumnChart(
							document
									.getElementById('chart_div'));
					chart.draw(data, options);
	}
	
	function drawStacked() {
		$.ajax({
					url : "/fetchreportingspsstats.do",
					type : "GET",
					cache : false,
					dataType : "json",
					success : function(response) {
											chartData = JSON.parse(response);

											if (chartData.length == 0 || chartData == null) {
												drawEmptyChart();
											} else {
												var spsChartData = new Array(
														chartData.length + 1);
												for (var k = 0; k <= chartData.length; k++) {
													spsChartData[k] = new Array(
															4);
												}
												spsChartData[0] = [ 'SPS','Detractors',
														'Passives','Promoters',{ role: 'annotation' } ];

												for (var i = 1; i <= chartData.length; i++) {
													var monthName = monthNamesList[(chartData[i - 1][1]) - 1];
													var yearStr = (chartData[i-1][0]).toString();
													var yearValue = yearStr.match(/.{1,2}/g)[1];
													spsChartData[i][0] = monthName
															+ " "
															+ yearValue;
													spsChartData[i][1] = chartData[i - 1][2];
													spsChartData[i][2] = chartData[i - 1][3];
													spsChartData[i][3] = chartData[i - 1][4];
													
													var totalTransactions = chartData[i - 1][2] + chartData[i - 1][3] + chartData[i - 1][4];
													var promoters = chartData[i - 1][4];
													var detractors = chartData[i - 1][2];
													var spsScore = 0;
													if(totalTransactions != 0 && totalTransactions != undefined && totalTransactions != null){
														spsScore = ((promoters - detractors)*100)/totalTransactions;
													}
													
													spsChartData[i][4] = 'SPS: ' + spsScore.toFixed(2);
												}

												var data = google.visualization
														.arrayToDataTable(spsChartData);
												
												var windowSize = $( window ).width();
												var groupWidth = '60';
												if(windowSize <= 500){
													groupWidth = '30';
												}
												
												var options = {
													legend : {
														position : 'none'
													},
													isStacked : true,
													chartArea:{width:'85%'},
													vAxis : {
														gridlines : {
															count : 14
														}
													},
													bar: { groupWidth: groupWidth },
													annotations: {
														   alwaysOutside:true,
														   style: 'point',
														          highContrast: 'true',
														          textStyle: {
														            align: 'center !important',
														            fontSize:13,
														            color:'#000000',
														            bold: 'true'
														          },
														          stem:{length:2}
															},
													colors : [ '#E8341F',
															'#999999',
															'#7ab400' ]
												};

												var chart = new google.visualization.ColumnChart(
														document
																.getElementById('chart_div'));
												chart.draw(data, options);
											}
					},
					complete:function(){
						hideDashOverlay('#sps-dash');
					},
					error : function(e) {
						if (e.status == 504) {
							redirectToLoginPageOnSessionTimeOut(e.status);
							return;
						}
						drawEmptyChart();
						hideDashOverlay('#sps-dash')
					}
				});
	}
}

function drawNpsStatsGraph(entityId,entityType){
	showDashOverlay('#nps-dash');
	var chartData;
	google.charts.load('current', {	packages : [ 'corechart', 'bar' ]});
	google.charts.setOnLoadCallback(drawNpsStacked);
	
	function drawEmptyNpsChart(){
		var npsChartData = [
							[ 'NPS', 'Detractors','Passives','Promoters' ],
							[ '', 0, 0, 0 ] ];

					var data = google.visualization
							.arrayToDataTable(npsChartData);

					var options = {
						legend : {
							position : 'none'
						},
						isStacked : true,
						vAxis : {
							minValue : 0,
							maxValue : 10,
							gridlines : {
								count : 5
							}
						},
						colors : [ '#E8341F',
								'#999999', '#7ab400' ]
					};

					var chart = new google.visualization.ColumnChart(
							document
									.getElementById('nps_chart_div'));
					chart.draw(data, options);
	}
	
	function drawNpsStacked() {
		
		var currentDate = new Date();
		var currentYear = currentDate.getFullYear();
		var currentMonth = currentDate.getMonth()+1;
		
		var payload = {
				"entityId" : entityId,
				"entityType" : entityType,
				"currentMonth" : currentMonth,
				"currentYear" : currentYear
		}
		$.ajax({
					url : "/reporting/npsgraph.do",
					type : "GET",
					data : payload,
					cache : false,
					dataType : "json",
					success : function(response) {
											chartData = JSON.parse(response);

											if (chartData.length == 0 || chartData == null) {
												drawEmptyNpsChart();
											} else {
												var npsChartData = new Array(
														chartData.length + 1);
												for (var k = 0; k <= chartData.length; k++) {
													npsChartData[k] = new Array(
															4);
												}
												npsChartData[0] = [ 'NPS','Detractors',
														'Passives', 'Promoters',{ role: 'annotation' } ];

												for (var i = 1; i <= chartData.length; i++) {
													var monthName = monthNamesList[(chartData[i - 1][1]) - 1];
													var yearStr = (chartData[i-1][0]).toString();
													var yearValue = yearStr.match(/.{1,2}/g)[1];
													npsChartData[i][0] = monthName
															+ " "
															+ yearValue;
													npsChartData[i][1] = chartData[i - 1][2];
													npsChartData[i][2] = chartData[i - 1][3];
													npsChartData[i][3] = chartData[i - 1][4];
													
													var totalTransactions = chartData[i - 1][2] + chartData[i - 1][3] + chartData[i - 1][4];
													var promoters = chartData[i - 1][4];
													var detractors = chartData[i - 1][2];
													var npsScore = 0;
													if(totalTransactions != 0 && totalTransactions != undefined && totalTransactions != null){
														npsScore = ((promoters - detractors)*100)/totalTransactions;
													}
													
													npsChartData[i][4] = 'NPS: ' + npsScore.toFixed(2);
												}

												var data = google.visualization
														.arrayToDataTable(npsChartData);

												var windowSize = $( window ).width();
												var groupWidth = '60';
												if(windowSize <= 500){
													groupWidth = '30';
												}
												
												var options = {
													legend : {
														position : 'none'
													},
													isStacked : true,
													chartArea:{width:'85%'},
													vAxis : {
														gridlines : {
															count : 14
														}
													},
													bar: { groupWidth: groupWidth },
													annotations: {
														   alwaysOutside:true,
														   style: 'point',
														          highContrast: 'true',
														          textStyle: {
														            align: 'center !important',
														            fontSize:13,
														            color:'#000000',
														            bold: 'true'
														          },
														          stem:{length:2}
															},
													colors : [ '#E8341F',
															'#999999',
															'#7ab400' ]
												};

												var chart = new google.visualization.ColumnChart(
														document
																.getElementById('nps_chart_div'));
												chart.draw(data, options);
											}
					},
					complete:function(){
						hideDashOverlay('#nps-dash');
					},
					error : function(e) {
						if (e.status == 504) {
							redirectToLoginPageOnSessionTimeOut(e.status);
							return;
						}
						drawEmptyChart();
						hideDashOverlay('#nps-dash')
					}
				});
	}
}

function drawCompletionRateGraph(){
	showDashOverlay('#completion-graph-dash');
	google.charts.load('current', {'packages':['corechart']});
	google.charts.setOnLoadCallback(drawChart);

	function drawEmptyChart(){
		var spsChartData = [
							['Month','Completed Transactions ','Incomplete Transactions '],
							[ '', 0, 0 ] ];

					var data = google.visualization
							.arrayToDataTable(spsChartData);

					var options = {
						chartArea : {
							width : '85%'
						},
						vAxis : {
							minValue : 0,
							maxValue : 10,
							gridlines : {
								count : 6
							}
						},
						legend : {
							position : 'bottom',
							alignment : 'center'
						},
						pointSize : 5
					};

					var chart = new google.visualization.ColumnChart(
							document
									.getElementById('completion_chart_div'));
					chart.draw(data, options);
	}
	
	function drawChart() {
		$.ajax({
					url : "/fetchreportingcompletionrate.do",
					type : "GET",
					cache : false,
					dataType : "json",
					success : function(response) {
											chartData = JSON.parse(response);

											if (chartData.length == 0 || chartData == null) {
												drawEmptyChart();
											} else {
												var maxTransactionValue = 0;
												var compRateChartData = new Array(
														chartData.length + 1);
												for (var k = 0; k <= chartData.length; k++) {
													compRateChartData[k] = new Array(
															3);
												}
												compRateChartData[0] = [
														'Month',
														'Completed Transactions ',
														'Incomplete Transactions ' ];

												for (var i = 1; i <= chartData.length; i++) {
													var monthName = monthNamesList[(chartData[i - 1][1]) - 1];
													var yearStr = (chartData[i-1][0]).toString();
													var yearValue = yearStr.match(/.{1,2}/g)[1];
													compRateChartData[i][0] = monthName
															+ " "
															+ yearValue;
													compRateChartData[i][1] = chartData[i - 1][2];
													compRateChartData[i][2] = chartData[i - 1][3];

													if (compRateChartData[i][1] > maxTransactionValue) {
														maxTransactionValue = compRateChartData[i][1];
													}

													if (compRateChartData[i][2] > maxTransactionValue) {
														maxTransactionValue = compRateChartData[i][2];
													}
												}

												var maxVAxisValue = 0;

												if (maxTransactionValue < 10) {
													maxVAxisValue = maxTransactionValue + 5;
												} else {
													maxVAxisValue = maxTransactionValue + 5;
												}

												var data = google.visualization
														.arrayToDataTable(compRateChartData);

												var options = {
													chartArea : {
														width : '85%'
													},
													vAxis : {
														minValue : 0,
														maxValue : maxVAxisValue,
														gridlines : {
															count : 6
														}
													},
													legend : {
														position : 'bottom',
														alignment : 'center'
													},
													pointSize : 5
												};

												var chart = new google.visualization.LineChart(
														document
																.getElementById('completion_chart_div'));

												chart.draw(data, options);
											}
					},
					complete: function(){
						hideDashOverlay('#completion-graph-dash');
					},
					error : function(e) {
						if (e.status == 504) {
							redirectToLoginPageOnSessionTimeOut(e.status);
							return;
						}
						drawEmptyChart();
						hideDashOverlay('#completion-graph-dash');
					}
				});
	}
}

//draw Unclicked, Processed and Unprocessed Pie charts functions
function drawUnclickedDonutChart(overviewYearData){
	 
	unclickedDrawnCount=0;
	var monthYear = getTimeFrameValue();
	
	 google.charts.load("current", {packages:["corechart"]});
     google.charts.setOnLoadCallback(drawChart);
     
     var processed;
     var unprocessed;
     
     if(overviewYearData !=null && !isEmpty(overviewYearData)){
    	 processed = overviewYearData.Processed;
         unprocessed = overviewYearData.Unprocessed;
     }else{
    	 processed = 0;
         unprocessed = 0;
     }
     var chart;
     function drawChart() {
    	 	
    	 	unclickedDrawnCount++;
    	 	var switchable = $('#reporting-trans-details').attr('data-switch');
    	 	var isSwitchable=true;
    	 	if(switchable == 'true' || switchable == true){
    	 		isSwitchable=true;
    	 	}else{
    	 		isSwitchable=false;
    	 	}
    	 		
    	 	if(unclickedDrawnCount==1 && !isUpdateTransStats && isSwitchable){
	        	var profilemasterid=$('#rep-prof-container').attr('data-profile-master-id');
	 	    	if(profilemasterid == 4){
	 	    		activaTab('leaderboard-tab');
	 	        }
	        }
	        
	        var data = google.visualization.arrayToDataTable([
	          ['Transaction', 'Number#'],
	          ['Processed', processed],
	          ['Unprocessed', unprocessed]
	        ]);

	        var options = {
	          pieStartAngle: 130,
	          backgroundColor: '#f1f0f2',
	          pieHole: 0.5,
	          legend: { 
	      	    position : 'none'
	      	  },
	      	  pieSliceText:'none',
	      	  chartArea:{
	      		  width:'100%',
	      		  height:'65%'
	      	  },
	      	slices: [{color : '#0072c2'},{color: '#fa5b00'}],
	      	 legend: {
	             position: 'labeled',
	             labeledValueText: 'none',
	             textStyle: {
	                 color: 'black', 
	                 fontSize: 15,
	                 bold: true
	             }
	      	 },
	      	tooltip: { trigger: 'none' },
	      	sliceVisibilityThreshold: 0
	        };

	        chart = new google.visualization.PieChart(document.getElementById('donutchart'));
	        
	        function selectHandler(){
	        	 var selectedItem = chart.getSelection()[0];
	             if (selectedItem) {
	               var slice = data.getValue(selectedItem.row, 0);

	               if( slice == 'Processed'){
	            	   clickProcessedDiv();
	               }else if( slice == 'Unprocessed'){
	            	   clickUnprocessedDiv();
	               }
	             }
	        }
	        
	        google.visualization.events.addListener(chart, 'select', selectHandler);
	        chart.draw(data, options);
	       
	        var optionsChartIcn = {
		  	          pieStartAngle: 130,
			          backgroundColor: '#f1f0f2',
			          pieHole: 0.3,
			          legend: { 
			      	    position : 'none'
			      	  },
			      	  pieSliceText:'none',
			      	  width:32,
			      	  height:32,
			      	  slices: [{color : '#0072c2'},{color: '#fa5b00'}],
			      	  legend: 'none',
			      	  tooltip: { trigger: 'none' },
			      	  enableInteractivity: false
			        };
		        
		        var icnChart = new google.visualization.PieChart(document.getElementById('chart-icn-chart'));
		        
		        icnChart.draw(data, optionsChartIcn);
		       
		        isUpdateTransStats=false;
		        
		        var switchable = $('#reporting-trans-details').attr('data-switch',true);
		        
		        hideDashOverlay('#unclicked-graph-dash');
	      }
}

function drawProcessedDonutChart(overviewYearData){
	
	$('#processed-trans-graph').removeClass('hide');
	
	var monthYear = getTimeFrameValue();
	    
	 google.charts.load("current", {packages:["corechart"]});
     google.charts.setOnLoadCallback(drawChart);
     
     var incomplete;
     var completed;
     
     if(overviewYearData !=null && !isEmpty(overviewYearData)){
    	 incomplete = overviewYearData.Incomplete;
    	 completed = overviewYearData.Completed;
     }else{
    	 incomplete = 0;
    	 completed = 0;
     }
     
     function drawChart() {
    	 
	        var data = google.visualization.arrayToDataTable([
	          ['Transaction', 'Number#'],
	          ['Incomplete', incomplete],
	          ['Completed', completed]
	        ]);

	        var options = {
	          pieStartAngle: 130,
	          backgroundColor: '#f1f0f2',
	          pieHole: 0.5,
	          legend: { 
	      	    position : 'none'
	      	  },
	      	  pieSliceText:'none',
	      	  width:360,
	      	  chartArea:{
	      		  width:'100%',
	      		  height:'65%'
	      	  },
	      	slices: [{color : '#f5c70a'},{color: '#79b600'}],
	      	 legend: {
	             position: 'labeled',
	             labeledValueText: 'none',
	             textStyle: {
	                 color: 'black', 
	                 fontSize: 15,
	                 bold: true
	             }
	      	 },
	      	tooltip: { trigger: 'none' },
	      	sliceVisibilityThreshold: 0
	        };

	        var chart = new google.visualization.PieChart(document.getElementById('processedDonutchart'));
	        chart.draw(data, options);
	       
	        $('#processed-trans-graph').addClass('hide');
	      }
}

function drawUnprocessedDonutChart(overviewYearData){
	
	$('#unprocessed-trans-graph').removeClass('hide');
	
	 google.charts.load("current", {packages:["corechart"]});
    google.charts.setOnLoadCallback(drawChart);
    
    var unassigned;
    var duplicate;
    var corrupted;
    var other;
    
    if(overviewYearData != null && !isEmpty(overviewYearData)){
    	unassigned = overviewYearData.Unassigned;
        duplicate = overviewYearData.Duplicate;
        corrupted = overviewYearData.Corrupted;
        other= overviewYearData.Unprocessed - (overviewYearData.Unassigned + overviewYearData.Duplicate + overviewYearData.Corrupted);
    }else{
    	unassigned = 0;
        duplicate = 0;
        corrupted = 0;
        other=0;
    }
    
    function drawChart() {
   	 
	        var data = google.visualization.arrayToDataTable([
	          ['Transaction', 'Number#'],
	          ['Unassigned', unassigned],
	          ['duplicate', duplicate],
	          ['corrupted', corrupted],
	          ['other',other]
	        ]);

	        var options = {
	          pieStartAngle: 90,
	          backgroundColor: '#f1f0f2',
	          pieHole: 0.5,
	          legend: { 
	      	    position : 'none'
	      	  },
	      	  pieSliceText:'none',
	      	  width:360,
	      	  chartArea:{
	      		  width:'100%',
	      		  height:'65%'
	      	  },
	      	slices: [{color : '#f5c70a'},{color: '#7e36c2'},{color:'#ea310b'},{color:'#000000'}],
	      	 legend: {
	             position: 'labeled',
	             labeledValueText: 'none',
	             textStyle: {
	                 color: 'black', 
	                 fontSize: 15,
	                 bold: true
	             }
	      	 },
	      	tooltip: { trigger: 'none' },
	      	sliceVisibilityThreshold: 0
	        };

	        var chart = new google.visualization.PieChart(document.getElementById('unprocessedDonutchart'));
	        chart.draw(data, options);
	        
	        $('#unprocessed-trans-graph').addClass('hide');
	      }
}

//Get time frame value function
function getTimeFrameValue(){
	var currentDate =  new Date();
	var currentMonth = currentDate.getMonth();
	var currentYear = currentDate.getFullYear();
	
	var monthYear={
			month:13,
			year:2017
	};
	var timeFrame = parseInt($('#time-frame-sel').attr('data-column-value'));
	var yearTimeFrame = parseInt($('#time-frame-sel').attr('data-year'));
	
	switch(timeFrame){
		case 100: monthYear.month=14;
				  monthYear.year=currentYear;
				  return monthYear;
		
		case 101: monthYear.month=currentMonth+1;
		  		monthYear.year=currentYear;
		  		return monthYear;
		
		case 102:monthYear.month=currentMonth;
				 monthYear.year=currentYear;
				 if(currentMonth == 0){
  					monthYear.month=12;
  					monthYear.year=currentYear-1;
				 }
  				return monthYear;
  		
		case 103: monthYear.month=13;
  				monthYear.year=currentYear;
  				return monthYear;
		
		case 104: monthYear.month=13;
  				monthYear.year=currentYear-1;
  				return monthYear;
  		
		case 1: monthYear.month=1;
  				monthYear.year=yearTimeFrame;
  				return monthYear;
  				
		case 2: monthYear.month=2;
			monthYear.year=yearTimeFrame;
			return monthYear;
			
		case 3: monthYear.month=3;
			monthYear.year=yearTimeFrame;
			return monthYear;
			
		case 4: monthYear.month=4;
			monthYear.year=yearTimeFrame;
			return monthYear;
			
		case 5: monthYear.month=5;
			monthYear.year=yearTimeFrame;
			return monthYear;
			
		case 6: monthYear.month=6;
			monthYear.year=yearTimeFrame;
			return monthYear;
			
		case 7: monthYear.month=7;
			monthYear.year=yearTimeFrame;
			return monthYear;
			
		case 8: monthYear.month=8;
			monthYear.year=yearTimeFrame;
			return monthYear;
		
		case 9: monthYear.month=9;
			monthYear.year=yearTimeFrame;
			return monthYear;
			
		case 10: monthYear.month=10;
			monthYear.year=yearTimeFrame;
			return monthYear;
			
		case 11: monthYear.month=11;
			monthYear.year=yearTimeFrame;
			return monthYear;
		
		case 12: monthYear.month=12;
			monthYear.year=yearTimeFrame;
			return monthYear;
	}
}

function drawNpsGauge(){
	
	if(overviewData != null && !isEmpty(overviewData)){
		var detractorEndAngle;
		var passivesEndAngle;
		var promotersEndAngle;
		var detractorStartAngle;
		var passivesStartAngle;
		var promotersStartAngle;
		var gaugeStartAngle = 250;
		var gaugeEndAngle = 110;

			function npsPolarToCartesian(centerX, centerY, radius, angleInDegrees) {
				var angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0;

				return {
					x : centerX + (radius * Math.cos(angleInRadians)),
					y : centerY + (radius * Math.sin(angleInRadians))
				};
			}

			function npsDescribeArc(x, y, radius, startAngle, endAngle) {

				var start = npsPolarToCartesian(x, y, radius, endAngle);
				var end = npsPolarToCartesian(x, y, radius, startAngle);

				var largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

				var d = [ "M", start.x, start.y, "A", radius, radius, 0, largeArcFlag,
						0, end.x, end.y ].join(" ");

				return d;
			}
			
			function getNpsGaugeEndAngles(){
						
				var npsScore = overviewData.NpsScore;
				
				if(overviewData != null && !isEmpty(overviewData)){
					
					$('#npsScorebox').html(npsScore);
					var detractors = overviewData.NpsDetractorPercentage;
					var passives =   overviewData.NpsPassivesPercentage;
					var promoters =  overviewData.NpsPromoterPercentage;
					var totalDegree = (360-gaugeStartAngle)+gaugeEndAngle;
					var degRequired = 0;
				
				//Detractor Start And End Angles
				detractorStartAngle = gaugeStartAngle;
				if(detractors == 0){
					detractorEndAngle = detractorStartAngle;
				}else if(detractors == 50){
					detractorEndAngle = 0;
				}else{
					degRequired = (detractors/100)*totalDegree;
					detractorEndAngle = (detractorStartAngle + degRequired)%360;
				}
				
				if(detractors > 50){
					document.getElementById("nps-arc1").setAttribute("d", npsDescribeArc(150, 150, 55, detractorStartAngle, 0));
					document.getElementById("nps-arc4").setAttribute("d", npsDescribeArc(150, 150, 55, 0, detractorEndAngle));
				}else{
					document.getElementById("nps-arc1").setAttribute("d", npsDescribeArc(150, 150, 55, detractorStartAngle, detractorEndAngle));
				}
				
				//Passives Start and End Angles
				if(detractors==0){
					passivesStartAngle = gaugeStartAngle;
				}else{
					passivesStartAngle = detractorEndAngle + 2;
				}
				
				if(passives == 0){
					passivesEndAngle = passivesStartAngle;
				}else{
					degRequired = (passives/100)*totalDegree;
					passivesEndAngle = (passivesStartAngle + degRequired)%360;
				}
				
				if(passivesStartAngle >= gaugeStartAngle){
					if(passivesEndAngle >= gaugeStartAngle){
						document.getElementById("nps-arc2").setAttribute("d", npsDescribeArc(150, 150, 55, passivesStartAngle, passivesEndAngle));
					}else{
						document.getElementById("nps-arc2").setAttribute("d", npsDescribeArc(150, 150, 55, passivesStartAngle, 0));
						document.getElementById("nps-arc5").setAttribute("d", npsDescribeArc(150, 150, 55, 0, passivesEndAngle));
					}
				}else{
					document.getElementById("nps-arc5").setAttribute("d", npsDescribeArc(150, 150, 55, passivesStartAngle, passivesEndAngle));
				}
				
				//Promoters Start and End Angles
				promotersEndAngle =  gaugeEndAngle;
				
				if(promoters == 0){
					promotersStartAngle = promotersEndAngle;
				}else if(detractors == 0 && passives == 0){
					promotersStartAngle = gaugeStartAngle;
				}else{
					promotersStartAngle = passivesEndAngle +2;
				}
				
				if(promotersStartAngle >= gaugeStartAngle){
					if(promotersEndAngle >= gaugeStartAngle){
						document.getElementById("nps-arc3").setAttribute("d", npsDescribeArc(150, 150, 55, promotersStartAngle, promotersEndAngle));
					}else{
						document.getElementById("nps-arc3").setAttribute("d", npsDescribeArc(150, 150, 55, promotersStartAngle, 0));
						document.getElementById("nps-arc6").setAttribute("d", npsDescribeArc(150, 150, 55, 0, promotersEndAngle));
					}
				}else{
					document.getElementById("nps-arc6").setAttribute("d", npsDescribeArc(150, 150, 55, promotersStartAngle, promotersEndAngle));
				}
			}
		}

			$(document).ready(function() {
				var npsScore = overviewData.NpsScore;
				
				
				var marginLeft = parseInt($("#nps-metre-needle").css("margin-left"));
				var marginTop = parseInt($("#nps-metre-needle").css("margin-top"));
			
				var needleDegree;
				var marginNeedle = Math.abs(npsScore - 20)/2;
				
				if(npsScore < 0){
					
						needleDegree = 360-(Math.abs(npsScore)*1.1);
						if(npsScore < -87){
							$("#nps-metre-needle").css("margin-left",marginLeft-marginNeedle+12+'px');
						}else{
							$("#nps-metre-needle").css("margin-left",marginLeft-marginNeedle-5+'px');
						}
						$("#nps-metre-needle").css("margin-top",marginTop+marginNeedle-20+'px');
						
				}else if(npsScore > 15){
					
					needleDegree = Math.abs(npsScore)*1.1;
					if(npsScore > 87){
						$("#nps-metre-needle").css("margin-left",marginLeft+marginNeedle-20+'px');
					}else{
						$("#nps-metre-needle").css("margin-left",marginLeft+marginNeedle+'px');
					}
					$("#nps-metre-needle").css("margin-top",marginTop+marginNeedle+'px');
					
				}else if(npsScore > 7 && npsScore <=15){
					
					needleDegree = Math.abs(npsScore)*1.1;
					$("#nps-metre-needle").css("margin-left",marginLeft-5+'px');
					
				}else if(npsScore == 0 || (npsScore > 0 && npsScore <= 7)){
					needleDegree = Math.abs(npsScore)*1.1;
					$("#nps-metre-needle").css("margin-left",marginLeft-8+'px');
				}
				
				$('#nps-metre-needle').css({'transform':'rotate(' + needleDegree + 'deg)'});
					
				getNpsGaugeEndAngles();
				
				//document.getElementById("arc1").setAttribute("d", describeArc(150, 150, 70, detractorStartAngle, detractorEndAngle));
				//document.getElementById("arc2").setAttribute("d", describeArc(150, 150, 70, passivesStartAngle, passivesEndAngle));
				//document.getElementById("arc3").setAttribute("d", describeArc(150, 150, 70, promotersStartAngle, promotersEndAngle));
			});
		}
}

function drawSpsGauge(){

	if(overviewData != null && !isEmpty(overviewData)){
		var detractorEndAngle;
		var passivesEndAngle;
		var promotersEndAngle;
		var detractorStartAngle;
		var passivesStartAngle;
		var promotersStartAngle;
		var gaugeStartAngle = 250;
		var gaugeEndAngle = 110;

			function polarToCartesian(centerX, centerY, radius, angleInDegrees) {
				var angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0;

				return {
					x : centerX + (radius * Math.cos(angleInRadians)),
					y : centerY + (radius * Math.sin(angleInRadians))
				};
			}

			function describeArc(x, y, radius, startAngle, endAngle) {

				var start = polarToCartesian(x, y, radius, endAngle);
				var end = polarToCartesian(x, y, radius, startAngle);

				var largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

				var d = [ "M", start.x, start.y, "A", radius, radius, 0, largeArcFlag,
						0, end.x, end.y ].join(" ");

				return d;
			}
			
			function getGaugeEndAngles(){
						
				var spsScore = overviewData.SpsScore;
				
				if(overviewData != null && !isEmpty(overviewData)){
					
					$('#spsScorebox').html(spsScore);
					var detractors = overviewData.DetractorPercentage;
					var passives =   overviewData.PassivesPercentage;
					var promoters =  overviewData.PromoterPercentage;
					var totalDegree = (360-gaugeStartAngle)+gaugeEndAngle;
					var degRequired = 0;
				
				//Detractor Start And End Angles
				detractorStartAngle = gaugeStartAngle;
				if(detractors == 0){
					detractorEndAngle = detractorStartAngle;
				}else if(detractors == 50){
					detractorEndAngle = 0;
				}else{
					degRequired = (detractors/100)*totalDegree;
					detractorEndAngle = (detractorStartAngle + degRequired)%360;
				}
				
				if(detractors > 50){
					document.getElementById("arc1").setAttribute("d", describeArc(150, 150, 55, detractorStartAngle, 0));
					document.getElementById("arc4").setAttribute("d", describeArc(150, 150, 55, 0, detractorEndAngle));
				}else{
					document.getElementById("arc1").setAttribute("d", describeArc(150, 150, 55, detractorStartAngle, detractorEndAngle));
				}
				
				//Passives Start and End Angles
				if(detractors==0){
					passivesStartAngle = gaugeStartAngle;
				}else{
					passivesStartAngle = detractorEndAngle + 2;
				}
				
				if(passives == 0){
					passivesEndAngle = passivesStartAngle;
				}else{
					degRequired = (passives/100)*totalDegree;
					passivesEndAngle = (passivesStartAngle + degRequired)%360;
				}
				
				if(passivesStartAngle >= gaugeStartAngle){
					if(passivesEndAngle >= gaugeStartAngle){
						document.getElementById("arc2").setAttribute("d", describeArc(150, 150, 55, passivesStartAngle, passivesEndAngle));
					}else{
						document.getElementById("arc2").setAttribute("d", describeArc(150, 150, 55, passivesStartAngle, 0));
						document.getElementById("arc5").setAttribute("d", describeArc(150, 150, 55, 0, passivesEndAngle));
					}
				}else{
					document.getElementById("arc5").setAttribute("d", describeArc(150, 150, 55, passivesStartAngle, passivesEndAngle));
				}
				
				//Promoters Start and End Angles
				promotersEndAngle =  gaugeEndAngle;
				
				if(promoters == 0){
					promotersStartAngle = promotersEndAngle;
				}else if(detractors == 0 && passives == 0){
					promotersStartAngle = gaugeStartAngle;
				}else{
					promotersStartAngle = passivesEndAngle +2;
				}
				
				if(promotersStartAngle >= gaugeStartAngle){
					if(promotersEndAngle >= gaugeStartAngle){
						document.getElementById("arc3").setAttribute("d", describeArc(150, 150, 55, promotersStartAngle, promotersEndAngle));
					}else{
						document.getElementById("arc3").setAttribute("d", describeArc(150, 150, 55, promotersStartAngle, 0));
						document.getElementById("arc6").setAttribute("d", describeArc(150, 150, 55, 0, promotersEndAngle));
					}
				}else{
					document.getElementById("arc6").setAttribute("d", describeArc(150, 150, 55, promotersStartAngle, promotersEndAngle));
				}
			}
		}

			$(document).ready(function() {
				var spsScore = overviewData.SpsScore;
				
				
				var marginLeft = parseInt($("#metre-needle").css("margin-left"));
				var marginTop = parseInt($("#metre-needle").css("margin-top"));
			
				var needleDegree;
				var marginNeedle = Math.abs(spsScore - 20)/2;
				
				if(spsScore < 0){
					
						needleDegree = 360-(Math.abs(spsScore)*1.1);
						if(spsScore < -87){
							$("#metre-needle").css("margin-left",marginLeft-marginNeedle+12+'px');
						}else{
							$("#metre-needle").css("margin-left",marginLeft-marginNeedle-5+'px');
						}
						$("#metre-needle").css("margin-top",marginTop+marginNeedle-20+'px');
						
				}else if(spsScore > 15){
					
					needleDegree = Math.abs(spsScore)*1.1;
					if(spsScore > 87){
						$("#metre-needle").css("margin-left",marginLeft+marginNeedle-20+'px');
					}else{
						$("#metre-needle").css("margin-left",marginLeft+marginNeedle+'px');
					}
					$("#metre-needle").css("margin-top",marginTop+marginNeedle+'px');
					
				}else if(spsScore > 7 && spsScore <=15){
					
					needleDegree = Math.abs(spsScore)*1.1;
					$("#metre-needle").css("margin-left",marginLeft-5+'px');
					
				}else if(spsScore == 0 || (spsScore > 0 && spsScore <= 7)){
					needleDegree = Math.abs(spsScore)*1.1;
					$("#metre-needle").css("margin-left",marginLeft-8+'px');
				}
				
				$('#metre-needle').css({'transform':'rotate(' + needleDegree + 'deg)'});
					
				getGaugeEndAngles();
				
				//document.getElementById("arc1").setAttribute("d", describeArc(150, 150, 70, detractorStartAngle, detractorEndAngle));
				//document.getElementById("arc2").setAttribute("d", describeArc(150, 150, 70, passivesStartAngle, passivesEndAngle));
				//document.getElementById("arc3").setAttribute("d", describeArc(150, 150, 70, promotersStartAngle, promotersEndAngle));
			});
		}
}

function drawOverviewPage(){
	
	getOverviewData();
	
	var detractors; 
	var passives;
	var promoters;
	var npsDetractors;
	var npsPassives;
	var npsPromoters;
	
	if(overviewData != null && !isEmpty(overviewData)){
		
		detractors = overviewData.DetractorPercentage;
		passives =   overviewData.PassivesPercentage;
		promoters =  overviewData.PromoterPercentage;
		npsDetractors = overviewData.NpsDetractorPercentage;
		npsPassives =   overviewData.NpsPassivesPercentage;
		npsPromoters =  overviewData.NpsPromoterPercentage;
		
			//spsGauge checks
			if(detractors == 0 && promoters == 0 && passives == 0){
				$('#spsGaugeSuccess').hide();
				$('#spsGaugeFailure').show();
			}else{
				$('#spsGaugeFailure').hide();
				$('#spsGaugeSuccess').show();
			}
			
			//detractors,passives and promoters bar and values assignment
			$('#detractorsBar').css('width',detractors+'%');
			$('#detractorsValue').html(detractors+'%');
			$('#passivesBar').css('width',passives+'%');
			$('#passivesValue').html(passives+'%');
			$('#promotersBar').css('width',promoters+'%');
			$('#promotersValue').html(promoters+'%');	
		
			if(npsDetractors == 0 && npsPassives == 0 && npsPromoters == 0){
				$('#nps-row').hide();
				$('#npsGaugeSuccess').hide();
				$('#npsGaugeFailure').show();
			}else{
				$('#nps-row').show();
				$('#npsGaugeFailure').hide();
				$('#npsGaugeSuccess').show();
			}
			
			//detractors,passives and promoters bar and values assignment
			$('#npsDetractorsBar').css('width',npsDetractors+'%');
			$('#npsDetractorsValue').html(npsDetractors+'%');
			$('#npsPassivesBar').css('width',npsPassives+'%');
			$('#npsPassivesValue').html(npsPassives+'%');
			$('#npsPromotersBar').css('width',npsPromoters+'%');
			$('#npsPromotersValue').html(npsPromoters+'%');
			
			
	}else{
			detractors = 0;
			passives =   0;
			promoters =  0;
		
			$('#spsGaugeSuccess').hide();
			$('#spsGaugeFailure').show();
			
			//detractors,passives and promoters bar and values assignment
			$('#detractorsBar').css('width',detractors+'%');
			$('#detractorsValue').html(detractors+'%');
			$('#passivesBar').css('width',passives+'%');
			$('#passivesValue').html(passives+'%');
			$('#promotersBar').css('width',promoters+'%');
			$('#promotersValue').html(promoters+'%');
			
			$('#nps-row').hide();
			
	}
	
	drawSpsGauge();
	
	drawNpsGauge();
	
	
	if (overviewData == null) {
		$('#overviewSuccess').hide();
		$('#overviewFailure').show();
	} else {
		$('#overviewSuccess').show();
		$('#overviewFailure').hide();
	}
}

//javascript for reporting_reports page

//Reports Page Generate report button actions
$(document).on('change', '#generate-survey-reports', function() {
	
	var selectedVal = $('#generate-survey-reports').val();
	var key = parseInt(selectedVal);
	if(key == 101 || key == 102 || key == 103 || key == 106 || key == 110 || key == 112 || key == 200 || key == 1001 || key==105){
		$('#date-pickers').hide();
		if( $('#date-pickers').hasClass( 'display-inline-grid' ) ){
			$('#date-pickers').removeClass( 'display-inline-grid' )
		}
		if( $('#date-pickers').hasClass( 'display-inline-grid-imp' ) ){
			$('#date-pickers').removeClass( 'display-inline-grid-imp' )
		}
	}else{
		$('#date-pickers').show();
		if( !$('#date-pickers').hasClass( 'display-inline-grid' ) ){
			$('#date-pickers').addClass( 'display-inline-grid' )
		}
		if( !$('#date-pickers').hasClass( 'display-inline-grid-imp' ) ){
			$('#date-pickers').addClass( 'display-inline-grid-imp' )
		}
	}
	
	if(key == 302){
		$('#sm-keywords').removeClass('hide');
	}else{
		$('#sm-keywords').addClass('hide');
	}
	
	if(key==105){
		$('#trans-report-time-div').show();	
	}else{
		$('#trans-report-time-div').hide();	
	}
	
	if(key == 106 || key == 112){
		$('#report-time-div').removeClass('hide');
	}else{
		$('#report-time-div').addClass('hide');
	}
	
	if(key == 110){
		$('#nps-report-time-div').removeClass('hide');
		setNpsTimeFrames();
	}else{
		$('#nps-report-time-div').addClass('hide');
	}
	
	if( key == 200 ){
		$('#digest-time-div').removeClass('hide');
	} else {
		$('#digest-time-div').addClass('hide');
	}
	

	if(key == 1001){
		$('#email-rep-time-div').removeClass('hide');
	}else{
		$('#email-rep-time-div').addClass('hide');
	}
});

function setNpsTimeFrames(){
	var currentDate = new Date();
	var currentMonth = currentDate.getMonth()+1;
	var currentYear = currentDate.getFullYear();
	var lastMonth = (currentMonth-1)==0?12:currentMonth-1;
	var lastYear = currentYear-1;
	var lastMonthYear = lastMonth == 12 ? currentYear-1 : currentYear;
	
	var initialOptions = '<option value=1 data-report="thisWeek">This Week</option>'
		   +'<option value=2 data-report="lastWeek">Last Week</option>'
		   +'<option value=3 data-report="'+(currentMonth+'/01/'+currentYear)+'">This Month</option>'
		   +'<option value=4 data-report="'+(lastMonth+'/01/'+lastMonthYear)+'">Last Month</option>';

	$('#nps-report-time-selector').html(initialOptions);

	
	var monthStr = new Array();
	monthStr[0] = ""
		monthStr[1] = "Jan";
	monthStr[2] = "Feb";
	monthStr[3] = "Mar";
	monthStr[4] = "Apr";
	monthStr[5] = "May";
	monthStr[6] = "Jun";
	monthStr[7] = "Jul";
	monthStr[8] = "Aug";
	monthStr[9] = "Sep";
	monthStr[10] = "Oct";
	monthStr[11] = "Nov";
	monthStr[12] = "Dec";

	var lastMonth;
	var lastYear;
	var index = 5;
	if(currentMonth == 1){
		lastMonth = 12;
		lastYear = currentYear-1;
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('11/01/'+lastYear) + '">' + (monthStr[11]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('10/01/'+lastYear) + '">' + (monthStr[10]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('9/01/'+lastYear) + '">' + (monthStr[9]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('8/01'+lastYear) + '">' + (monthStr[8]+' '+lastYear)+'</option>');
	}else if(currentMonth == 2){
		lastMonth = 12;
		lastYear = currentYear-1;
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('12/01/'+lastYear) + '">' + (monthStr[12]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('11/01/'+lastYear) + '">' + (monthStr[11]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('10/01/'+lastYear) + '">' + (monthStr[10]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('9/01/'+lastYear) + '">' + (monthStr[9]+' '+lastYear)+'</option>');
	}else if(currentMonth == 3){
		lastMonth = 12;
		lastYear = currentYear-1;
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('1/01/'+currentYear) + '">' + (monthStr[1]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('12/01/'+lastYear) + '">' + (monthStr[12]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('11/01/'+lastYear) + '">' + (monthStr[11]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('10/01/'+lastYear) + '">' + (monthStr[10]+' '+lastYear)+'</option>');
	}else if(currentMonth == 4){
		lastMonth = 12;
		lastYear = currentYear-1;
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('2/01/'+currentYear) + '">' + (monthStr[2]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('1/01/'+currentYear) + '">' + (monthStr[1]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('12/01/'+lastYear) + '">' + (monthStr[12]+' '+lastYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('11/01/'+lastYear) + '">' + (monthStr[11]+' '+lastYear)+'</option>');
	}else if(currentMonth == 5){
		lastMonth = 12;
		lastYear = currentYear-1;
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('3/01/'+currentYear) + '">' + (monthStr[3]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('2/01/'+currentYear) + '">' + (monthStr[2]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('1/01/'+currentYear) + '">' + (monthStr[1]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ('12/01/'+lastYear) + '">' + (monthStr[12]+' '+lastYear)+'</option>');
	}else{
		var thisMonth = currentMonth-2;
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + (thisMonth+'/01/'+currentYear) + '">' + (monthStr[thisMonth]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ((thisMonth-1)+'/01/'+currentYear) + '">' + (monthStr[thisMonth-1]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ((thisMonth-2)+'/01/'+currentYear) + '">' + (monthStr[thisMonth-2]+' '+currentYear)+'</option>');
		$('#nps-report-time-selector').append('<option value='+ (index++) + ' data-report="' + ((thisMonth-3)+'/01/'+currentYear) + '">' + (monthStr[thisMonth-3]+' '+currentYear)+'</option>');
	}
}

function getTimeFrameForUserRankingReport(){
	var currentDate = new Date();
	var currentYear = currentDate.getFullYear();
	var currentMonth = currentDate.getMonth()+1;
	
	var year = currentYear;
	var month = currentMonth;
	
	var timeFrameStr = $('#report-time-selector').val();
	timeFrame = parseInt(timeFrameStr);
	
	switch(timeFrame){
	case 1: year = currentYear;
		break;
	case 2: year = currentYear;
		month = currentMonth;
		break;
	case 3: year = currentYear - 1;
		break;
	case 4: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		break;
	}
	
	var dateTimeFrame = month+"/01/"+year;
	
	return dateTimeFrame;
}

function getTimeFrameForEmailReport(){
	var currentDate = new Date();
	var currentYear = currentDate.getFullYear();
	var currentMonth = currentDate.getMonth()+1;
	
	var year = currentYear;
	var month = currentMonth;
	
	var dateTimeFrame = '';
	
	var timeFrameStr = $('#email-rep-selector').val();
	timeFrame = parseInt(timeFrameStr);
	
	switch(timeFrame){
	case 2: year = currentYear;
		month = currentMonth;
		dateTimeFrame = month+"/01/"+year;
		break;
	
	case 3: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		dateTimeFrame = month+"/01/"+year;
		break;
	}
		
	return dateTimeFrame;
}

function getTimeFrameForTransReport(){
	var currentDate = new Date();
	var currentYear = currentDate.getFullYear();
	var currentMonth = currentDate.getMonth()+1;
	
	var year;
	var month;
	
	var dateTimeFrame = '';
	
	var timeFrameStr = $('#trans-report-time-selector').val();
	timeFrame = parseInt(timeFrameStr);
	
	switch(timeFrame){
	case 2: year = currentYear;
		month = currentMonth;
		dateTimeFrame = month+"/01/"+year;
		break;
	
	case 3: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		dateTimeFrame = month+"/01/"+year;
		break;
	
	case 4: year = currentYear;
		dateTimeFrame = "01/01/"+year;
		break;
	
	case 5: year = currentYear-1;
		dateTimeFrame = "01/01/"+year;
		break;
	}
	
	if(timeFrame>5){
		year = $('#trans-report-time-selector').find(':selected').data('year');
		month = $('#trans-report-time-selector').find(':selected').data('month');
		dateTimeFrame = month+"/01/"+year;
	}
		
	return dateTimeFrame;
}

function getStartAndEndDateForNps(npsTimeFrame){
	var currentDate = new Date;
	var currentMonth = currentDate.getMonth()+1;
	var currentYear = currentDate.getFullYear();
	var npsDates = new Object();
	npsDates.endDate = "";
	var curr = new Date;
	var firstDayOfWeek = curr.getDate() - curr.getDay();
	firstDayOfWeek = firstDayOfWeek + 1
	
	if(npsTimeFrame == 1){
		var thisMonday = new Date(curr.setDate(firstDayOfWeek));
		var thisMondayDay = thisMonday.getDate();
		if(thisMondayDay < 10){
			thisMondayDay = '0'+thisMondayDay;
		}
		var thisMondayMonth = thisMonday.getMonth() + 1;
		var thisMondayYear = thisMonday.getFullYear();
		npsDates.startDate = thisMondayMonth+'/'+thisMondayDay+'/'+thisMondayYear;
	}else if(npsTimeFrame == 2){
		var thisMonday = new Date(curr.setDate(firstDayOfWeek-7));
		var thisMondayDay = thisMonday.getDate();
		if(thisMondayDay < 10){
			thisMondayDay = '0'+thisMondayDay;
		}
		var thisMondayMonth = thisMonday.getMonth() + 1;
		var thisMondayYear = thisMonday.getFullYear();
		npsDates.startDate = thisMondayMonth+'/'+thisMondayDay+'/'+thisMondayYear;
	}else{
		npsDates.startDate = $("#nps-report-time-selector option[value="+npsTimeFrame+"]").attr('data-report');
	}
	
	return npsDates;
}

function getStartAndEndDateForDigest(timeFrame){
	
	var date = new Date();
	date.setMonth(date.getMonth() - timeFrame);
	
	var digestDates = new Object();
	digestDates.startDate = formatDateForDigest( new Date(date.getFullYear(), date.getMonth(), 1) );
	
	date.setMonth( date.getMonth() + 1 );
	date = new Date(date.getFullYear(), date.getMonth(), 1);
	date.setSeconds( date.getSeconds() - 1 ); 
	digestDates.endDate = formatDateForDigest(date);
	return digestDates;
}

function formatDateForDigest( date ){
	var year = date.getFullYear();
	var month = date.getMonth() < 10 ? '0' + (  date.getMonth() + 1 ) : ( date.getMonth() + 1 );
	var date = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
	return month + '/' + date + '/' + year;
}

$(document).on('click', '#reports-generate-report-btn', function(e) {
	var selectedValue = $('#generate-survey-reports').val();
	var key = parseInt(selectedValue);
	var startDate = $('#dsh-start-date').val();
	var endDate = $("#dsh-end-date").val();
	var digestMonthValue = 0;
	var npsTimeFrame = parseInt($('#nps-report-time-selector').val());
	var d = new Date();
	var clientTimeZone = d.getTimezoneOffset();
	var keywordStr = document.getElementById('sm-keywords-selector');
	var keyword = keywordStr.options[keywordStr.selectedIndex].text;
	var keywordValue = parseInt($('#sm-keywords-selector').val());
	
	if(isNaN(keywordValue) && key == 302){
		$('#overlay-toast').html("Please select a keyword");
		showToast();
		return;
	}
	
	if( key == 200 ){
		digestMonthValue = $('#digest-time-selector').val()
		var digestDates = getStartAndEndDateForDigest(parseInt(digestMonthValue));
		startDate = digestDates.startDate;
		endDate = digestDates.endDate;
	}
	
	if(key == 106){
		startDate = getTimeFrameForUserRankingReport();
		var timeFrameStr = $('#report-time-selector').val();
		timeFrame = parseInt(timeFrameStr);
				
		switch(timeFrame){
			case 1: key = 107;
				break;
			case 2: key = 106;
				break;
			case 3: key = 107;
				break;
			case 4: key = 106;
				break;
		}
		
	}
	
	if(key == 112){
		startDate = getTimeFrameForUserRankingReport();
		timeFrameStr = $('#report-time-selector').val();
		timeFrame = parseInt(timeFrameStr);
		
		switch(timeFrame){
		case 1: key = 113;
			break;
		case 2: key = 112;
			break;
		case 3: key = 113;
			break;
		case 4: key = 112;
			break;
		}
	}
	
	if(key == 110){
		if(npsTimeFrame == 1 || npsTimeFrame == 2){
			key = 110;
		}else{
			key = 111;
		}
		var npsDates = getStartAndEndDateForNps(npsTimeFrame);
		startDate = npsDates.startDate;
		endDate = npsDates.endDate;
	}
	
	if(key == 1001){
		startDate = getTimeFrameForEmailReport();
	}
	
	if(key==105){
		startDate = getTimeFrameForTransReport();
	}
	
	var success = false;
	var messageToDisplay;
	var payload = {
			"startDate" : startDate,
			"endDate" : endDate,
			"reportId" : key,
			"clientTimeZone": clientTimeZone,
			"keyword": keyword,
		};
	
	showOverlay();	
		$.ajax({
			url : "./savereportingdata.do",
			type : "POST",
			data: payload,
			dataType:"TEXT",
			async:false,
			success : function(data) {
				success=true;
				messageToDisplay = data;
				showInfoForReporting(messageToDisplay);
			},
			complete : function() {	
				hideOverlay();
				
				var recentActivityCount=getRecentActivityCount();
				drawRecentActivity(0,batchSize,tableHeaderData,recentActivityCount);
				showHidePaginateButtons(0, recentActivityCount);
				
				if(recentActivityCount == 0){
					var tableData='';
					tableData+="</table><div style='text-align:center; margin:20px auto'><span class='incomplete-trans-span'>There are No Recent Activities</span></div>";
					$('#recent-activity-list-table').html(tableData);
				}
			},
			error : function(e) {
				showError("Your request could not be processed at the moment. Please try again later!");
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
			}
		});
});

$(document).on('click', '.err-close-rep', function() {
	hideError();
	hideInfo();
});


function getStatusString(status){
		var statusString;
		switch(status){
		case 1: statusString='Pending';
			break;
		case 0: statusString='Download';
			break;
		case 2: statusString='Pending';
			break;
		case 4: statusString='Failed';
			break;
		case 5: statusString='View';
			break;
		default: statusString='Failed'
		}
		return statusString;
	}
var startIndex=0;
var batchSize=10
var tableHeaderData;
var recentActivityList;

function drawRecentActivity(start,batchSize,tableHeader,recentActivityCount){
	
	tableHeaderData=tableHeader;
	startIndex=start;
	recentActivityList = getRecentActivityList(startIndex,batchSize);
	var tableData=''; 
	
	var curDate = new Date();
	var curYear = curDate.getFullYear();
	
	for(var i=0;i<recentActivityList.length;i++){
		
		var statusString = getStatusString(recentActivityList[i][6]);
		var startDate = getDateFromDateTime(recentActivityList[i][2]);
		var endDate =getDateFromDateTime(recentActivityList[i][3]);
		var monthStartDate = getMonthFromDateTime(recentActivityList[i][2]);
		var reportType = recentActivityList[i][9];
		
		tableData += "<tr id='recent-activity-row"+i+"' class=\"u-tbl-row user-row \">"
			+"<td class=\"v-tbl-recent-activity fetch-name hide\">"+i+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold tbl-black-text\">"+recentActivityList[i][0]+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-blue-text\">"+recentActivityList[i][1]+"</td>";
			
			if(reportType == 107){
				var yearOfReport = parseInt(startDate.split(",")[1]);
				if(yearOfReport < curYear){
					tableData += "<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-black-text \">Last Year</td>";
				}else{
					tableData += "<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-black-text \">This Year</td>";
				}
			}else if(recentActivityList[i][1] == 'NPS Report for Week'){
				tableData += "<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-black-text \">"+findReportWeek(startDate)+"</td>";
			} else if(recentActivityList[i][1] == 'Survey Invitation Email Report'){
				tableData += '<td class="v-tbl-recent-activity fetch-email txt-bold tbl-black-text ">';
				if(startDate==null && endDate==null){
					tableData += "All Time till date";
				} else if(endDate==null){
					tableData += "30 days starting "+startDate;
				} else if(startDate==null){
					tableData += "30 days ending "+ endDate;
				} else {
					tableData += startDate + ' - ' + endDate;
				}
				tableData += "</td>";
			} else if(recentActivityList[i][1] == 'Social Monitor Date based Report' || recentActivityList[i][1] == 'Social Monitor Date Report with keyword'){
				tableData += '<td class="v-tbl-recent-activity fetch-email txt-bold tbl-black-text ">';
				tableData += startDate + ' - ' + endDate;
				tableData += "</td>";
			} else{
				tableData += "<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-black-text "+(startDate==null?("\">"+"All Time till date "):("\">"+(endDate==null?monthStartDate:startDate)))+(endDate==null?" ":" - "+endDate)+"</td>";
			}
		tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold tbl-black-text\">"+recentActivityList[i][4]+" "+recentActivityList[i][5]+"</td>";
		
		if(recentActivityList[i][6]==0){	
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'><a id=\"downloadLink"+i+"\"class='txt-bold tbl-blue-text downloadLink cursor-pointer'>"+statusString+"</a></td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold \" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		}else if(recentActivityList[i][6]==3){
		  	tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'> No records found </td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold \" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		  }else if(recentActivityList[i][6]==4){
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'>"+statusString+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold\" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		} else if(recentActivityList[i][6]==5){	
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'><a id=\"viewLink"+i+"\"class='txt-bold tbl-blue-text downloadLink cursor-pointer'>"+statusString+"</a></td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold \" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		}else{
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'>"+statusString+"</td>"
				+"<td class=\"v-tbl-recent-activity fetch-name txt-bold\" >  </td>"
				+"</tr>";
		}
	}
	
	if(recentActivityCount == 0){
		tableData='';
		tableData+="</table><div style='text-align:center; margin:20px auto'><span class='incomplete-trans-span'>There are No Recent Activities</span></div>";
		$('#recent-activity-list-table').html(tableData);
	}else{
		$('#recent-activity-list-table').html(tableHeaderData+tableData+"</table>");
	}
	
	$('#rec-act-start-index').attr('data-start-index',startIndex);
	
}

function findReportWeek(startDate){
	var currentDate = new Date();
	var currentDay = currentDate.getDate();
	
	var startDateDay = parseInt(startDate.split(" ")[1].split(",")[0]);
	var startDateYear = parseInt(startDate.split(" ")[2]);
	var startDateMonth = getMonthNumberFromName(startDate.split(" ")[0]);
	var startingDate = new Date(startDateYear,startDateMonth,startDateDay);
	
	var timeDiff = Math.abs(currentDate.getTime() - startingDate.getTime());
	var dayDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));
	
	var weekName;
	
	weekName = startDate;
	var weekEnd;
	if(dayDiff<7){
		weekEnd = getDateFromDate(currentDate);
		weekName += ' - '+weekEnd;
	}else{
		var weekEndDate = new Date(startDateYear,startDateMonth,startDateDay);
		weekEndDate.setDate(weekEndDate.getDate()+6);
		weekEnd = getDateFromDate(weekEndDate);
		weekName += ' - '+weekEnd;
	}
	return weekName;	
}

function getDateFromDate(date){
	var day = date.getDate();
	var year =  date.getFullYear();
	var month = date.getMonth()+1;
	var monthStr = "Jan";
	
	switch(month){
	case 1: monthStr = "Jan";
		break;
	case 2: monthStr = "Feb";
		break;
	case 3: monthStr = "Mar";
		break;
	case 4: monthStr = "Apr";
		break;
	case 5: monthStr = "May";
		break;
	case 6: monthStr = "Jun";
		break;
	case 7: monthStr = "Jul";
		break;
	case 8: monthStr = "Aug";
		break;
	case 9: monthStr = "Sep";
		break;
	case 10: monthStr = "Oct";
		break;
	case 11: monthStr = "Nov";
		break;
	case 12: monthStr = "Dec";
		break;
	}
	
	return monthStr+' '+day+', '+year;
}

function getMonthNumberFromName(monthName){
	var month = 0;
	
	if(monthName == "Jan"){
		month = 0;
	}else if(monthName == "Feb"){
		month = 1;
	}else if(monthName == "Mar"){
		month = 2;
	}else if(monthName == "Apr"){
		month = 3;
	}else if(monthName == "May"){
		month = 4;
	}else if(monthName == "Jun"){
		month = 5;
	}else if(monthName == "Jul"){
		month = 6;
	}else if(monthName == "Aug"){
		month = 7;
	}else if(monthName == "Sep"){
		month = 8;
	}else if(monthName == "Oct"){
		month = 9;
	}else if(monthName == "Nov"){
		month = 10;
	}else if(monthName == "Dec"){
		month = 11;
	}
	return month;
}

function getDateFromDateTime(dateTime){
	if(dateTime != null){
	return dateTime.match(/[a-zA-z]{3} \d+, \d{4}/)[0];
	}
	
	return null;
}

function getMonthFromDateTime(dateTime){
	if(dateTime != null){
		return (dateTime.match(/[a-zA-z]{3}/)[0] + " " + dateTime.match(/\d{4}/)[0]);
		}
		
		return null;
}

$(document).on('click','.downloadLink',function(e){
	var clickedID = this.id;
	var indexRecentActivity = clickedID.match(/\d+$/)[0];
	var downloadLink=recentActivityList[indexRecentActivity][7];
	
	// open digest in new tab
	if( recentActivityList[indexRecentActivity][1] == "Monthly Digest" ){
		window.open(downloadLink,'_blank');
	} else {
		window.location=downloadLink;	
	}
});

$(document).on('click','.recent-act-delete-x',function(e){
	showOverlay();
	var clickedID = this.id;
	var indexRecentActivity = clickedID.match(/\d+$/)[0];
	var fileUploadId=recentActivityList[indexRecentActivity][8];
	deleteRecentActivity(fileUploadId, indexRecentActivity);
});

function getStartIndex(){
	return startIndex;
}

function getTableHeader(){
	return tableHeaderData;
}

function showHidePaginateButtons(startIndex,recentActivityCount){

	if(startIndex == 0){
		$('#rec-act-page-previous').hide();
	}else{
		$('#rec-act-page-previous').show();
	}
	
	if((recentActivityCount-startIndex)<=10){
		$('#rec-act-page-next').hide();
	}else{
		$('#rec-act-page-next').show();
	}
	
	if(recentActivityCount == 0){
		$('#rec-act-page-previous').hide();
		$('#rec-act-page-next').hide();
	}
}

function activaTab(tab){
    $('.nav-tabs a[href="#' + tab + '"]').tab('show');
};

function updateReportingDashboard(){
	
	$('#reporting-trans-details').attr('data-switch',false);
	var currentDate =  new Date();
	var currentMonth = currentDate.getMonth()+1;
	var currentYear = currentDate.getFullYear();
	isUpdateTransStats=true;
	
	var monthYear = getTimeFrameValue();
	var overviewYearData;
	
	if(monthYear.month == 14){
		getoverviewAllTimeData();
	}else if(monthYear.month == 13){
    	getoverviewYearData(monthYear.year);
    }else{
    	getOverviewMonthData(monthYear.month, monthYear.year);
    }
	
	$('#processed-trans-div').css('opacity','1.0');
	$('#unprocessed-trans-div').css('opacity','1.0');
	$('#incompleted-details-selectable').show();
	$('#incompleted-details').addClass('hide');
	$('#unassigned-details-selectable').show();
	$('#unassigned-details').addClass('hide');
	$('.processed-background-rect').hide();
	$('#processed-background-rect').hide();
	$('#processed-lbl-rect').show();
	$('#unprocessed-lbl-rect').show();
	$('#completed-lbl-rect').hide();
	$('#incompleted-lbl-rect').hide();
	$('#social-posts-lbl-rect').hide();
	$('#zillow-lbl-rect').hide();
	$('#third-party-lbl-rect').hide();
	$('#unassigned-lbl-rect').hide();
	$('#duplicate-lbl-rect').hide();
	$('#corrupted-lbl-rect').hide();
	$('#other-lbl-rect').hide();
	$('#unsubscribed-lbl-rect').hide();
}

function drawLeaderboardTableStructure(userRankingList,userId,profileMasterId){
	var tableHeaderData='<table id="leaderboard-table" class="v-um-tbl leaderboard-table col-lg-12 col-md-12 col-sm-12 col-xs-12">'
		+'<tr id="u-tbl-header" class="u-tbl-header">'
		+'<td class="lead-tbl-ln-of text-center">Rank</td>'
		+'<td class="v-tbl-uname lead-name-alignment">Name</td>'
		+'<td class="lead-tbl-ln-of text-center lead-tbl-hdr">Reviews</td>'
		+'<td class="lead-tbl-ln-of text-center lead-tbl-hdr">Average Score</td>'
		+'<td class="lead-tbl-ln-of text-center lead-tbl-hdr">SPS</td>'
		+'<td class="lead-tbl-ln-of text-center lead-tbl-hdr">Completion %</td>'
		+'</tr>';
	
	var tableData = '';
	var nonRankedTableData = '';
	var profileImageUrl="";
	var imageDiv="";
	
	if(userRankingList.length>0 && userRankingList != null){
		for(var i=0;i<userRankingList.length;i++){
			var rank='NR';
			
			if(userRankingList[i][9] == 1){
				rank='#' + userRankingList[i][1];
				
				profileImageUrl = userRankingList[i][10];
				if(profileImageUrl != null && profileImageUrl!="" && profileImageUrl != 'null'){
					imageDiv='<img id="lead-prof-image-edit" class="prof-image-edit pos-relative leaderboard-pic-circle" src="'+profileImageUrl+'"></img>';
				}else{
					imageDiv='<div id="lead-prof-image-edit" class="prof-image-edit pers-default-big pos-relative leaderboard-pic-circle"></div>';
				}
				if(userRankingList[i][0] == userId  && profileMasterId == 4){
					tableData += '<tr class="u-tbl-row leaderboard-row selected-row " >';
					$('#rank-span').html(userRankingList[i][1]);
					$('#user-score-span').html(userRankingList[i][6]);
				}else{
					tableData+='<tr class="u-tbl-row leaderboard-row">';
				}
				
				tableData+= '<td class="lead-tbl-ln-of">'+rank+'</td>'
				+'<td class="v-tbl-uname fetch-name">'+'<div class="leaderboard-name-div">'
				+'<div class="lead-img-div">'+imageDiv+'</div>'
				+'<span class="lead-name-span" >'+userRankingList[i][2]+' '+userRankingList[i][3]+'</span></div>'
				+'</td>'
				+'<td class="lead-tbl-ln-of">'+userRankingList[i][5]+'</td>'
				+'<td class="lead-tbl-ln-of">'+userRankingList[i][6]+'</td>'
				+'<td class="lead-tbl-ln-of">'+userRankingList[i][7]+'</td>'
				+'<td class="lead-tbl-ln-of">'+userRankingList[i][8]+'</td>'
				+'</tr>'
			}else{
				
				profileImageUrl = userRankingList[i][10];
				if(profileImageUrl != null && profileImageUrl!="" && profileImageUrl != 'null'){
					imageDiv='<img id="lead-prof-image-edit" class="prof-image-edit pos-relative leaderboard-pic-circle" src="'+profileImageUrl+'"></img>';
				}else{
					imageDiv='<div id="lead-prof-image-edit" class="prof-image-edit pers-default-big pos-relative leaderboard-pic-circle"></div>';
				}
				
				if(userRankingList[i][0] == userId  && profileMasterId == 4){
					nonRankedTableData += '<tr class="u-tbl-row leaderboard-row selected-row " >';
					$('#rank-span').html('NR');
					$('#user-score-span').html(userRankingList[i][6]);
				}else{
					nonRankedTableData+='<tr class="u-tbl-row leaderboard-row">';
				}
				nonRankedTableData+= '<td class="lead-tbl-ln-of">'+rank+'</td>'
				+'<td class="v-tbl-uname fetch-name">'+'<div class="leaderboard-name-div">'
				+'<div class="lead-img-div">'+imageDiv+'</div>'
				+'<span class="lead-name-span" >'+userRankingList[i][2]+' '+userRankingList[i][3]+'</span></div>'
				+'</td>'
				+'<td class="lead-tbl-ln-of">'+userRankingList[i][5]+'</td>'
				+'<td class="lead-tbl-ln-of">'+userRankingList[i][6]+'</td>'
				+'<td class="lead-tbl-ln-of">'+userRankingList[i][7]+'</td>'
				+'<td class="lead-tbl-ln-of">'+userRankingList[i][8]+'</td>'
				+'</tr>'
			}
			
		}
	}
	
	return (tableHeaderData+tableData+nonRankedTableData+'</table>');
}

function showHideRankPaginateBtns(startIndex,count){
	if(startIndex == 0 || count<11){
		if(!($('#lead-ranks-above').hasClass('hide'))){
			$('#lead-ranks-above').addClass('hide');
			$('#lead-ranks-above').removeClass('block-display');
		}
		if(!($('#top-ten-ranks').hasClass('hide'))){
			$('#top-ten-ranks').addClass('hide');
			$('#top-ten-ranks').removeClass('block-display');
		}	
	}else{
		$('#lead-ranks-above').removeClass('hide');
		$('#lead-ranks-above').addClass('block-display');
		$('#top-ten-ranks').removeClass('hide');
		$('#top-ten-ranks').addClass('block-display');

	}
	
	if(count<11 || startIndex>count-10){
		if(!($('#lead-ranks-below').hasClass('hide'))){
			$('#lead-ranks-below').addClass('hide');
			$('#lead-ranks-below').removeClass('block-display');
		}
	}else{
		$('#lead-ranks-below').removeClass('hide');
		$('#lead-ranks-below').addClass('block-display');
	}
	
}

function getAndSaveRankingSettingsVal(columnName,isRealTechOrSSAdmin,monthOff,yearOff){
	var minDaysOfRegistration = $('#days-registration').attr('placeholder');
	var minCompletedPercentage = $('#survey-completion').attr('placeholder');
	var	minNoOfReviews = $('#minimum-reviews').attr('placeholder');
	var	monthOffset = monthOff;
	var yearOffset = yearOff;
	
	if($('#days-registration').val() != ""){
		minDaysOfRegistration = $('#days-registration').val();
	}
	
	if($('#survey-completion').val() != ""){
		minCompletedPercentage = $('#survey-completion').val();	
	}
	
	if($('#minimum-reviews').val() != ""){
		minNoOfReviews = $('#minimum-reviews').val();
	}
	
	if((isRealTechOrSSAdmin == true || isRealTechOrSSAdmin == 'true')){
		
		if($('#month-offset').val() != ""){
			monthOffset = $('#month-offset').val();
		}else{
			monthOffset = $('#month-offset').attr('placeholder');
		}
		
		if($('#year-offset').val() != ""){
			yearOffset = $('#year-offset').val();
		}else{
			yearOffset = $('#year-offset').attr('placeholder');
		}
		
	}
	
	var message = saveRankingSettings(minDaysOfRegistration, minCompletedPercentage, minNoOfReviews, monthOffset, yearOffset);
	
	return message;
}

function drawLineGraphForScoreStats(chartDiv,chartData){
	google.charts.load('current', {'packages':['corechart']});
	google.charts.setOnLoadCallback(function(){
		
		var windowSize = $(window).width();
		var graphWidth = windowSize - 200;
		
		var scoreStatsChartData = [
									['Month','Rating'],
									[ '', 0 ] ];
				
			if(chartData != null && chartData.length > 0){
				scoreStatsChartData = chartData;
			}
				
			var data = google.visualization.arrayToDataTable(scoreStatsChartData);

			var options = {
							chartArea : {
							width : '90%'
							},
							vAxis : {
								minValue : 0,
								maxValue : 5,
								gridlines : {
									count : 6
								}
							},
							width: windowSize,
							height: 300,
							pointSize : 5,
							legend: {position:'none'}
						};
			
			if(windowSize > 1100){
				 options = {
							chartArea : {
							width : '90%'
							},
							width: graphWidth,
							height: 300,
							vAxis : {
								minValue : 0,
								maxValue : 5,
								gridlines : {
									count : 6
								}
							},
							pointSize : 5,
							legend: {position:'none'}
						};
			}
			var chart = new google.visualization.LineChart(document.getElementById(chartDiv));
			chart.draw(data, options);
	});
}

function splitAndEditDate(monthYear){
	
	var splitStr = monthYear.split('/');
	
	var month = parseInt(splitStr[0]);
	
	var monthStr = "Jan"
		
	switch(month){
	case 1: monthStr = "Jan";
		break;
	case 2: monthStr = "Feb";
		break;
	case 3: monthStr = "Mar";
		break;
	case 4: monthStr = "Apr";
		break;
	case 5: monthStr = "May";
		break;
	case 6: monthStr = "Jun";
		break;
	case 7: monthStr = "Jul";
		break;
	case 8: monthStr = "Aug";
		break;
	case 9: monthStr = "Sep";
		break;
	case 10: monthStr = "Oct";
		break;
	case 11: monthStr = "Nov";
		break;
	case 12: monthStr = "Dec";
		break;
	}
	
	var year = splitStr[1];
	
	var yearStr = year.match(/.{1,2}/g)[1];
	
	return (monthStr + " " + yearStr);
}

function drawOverallScoreStatsGraph(overallScoreStats){
		
	var overallChartDiv = "overall-rating-chart";
	
	if(overallScoreStats != null && overallScoreStats.length != 0){
		for(var i=0; i<overallScoreStats.length; i++){
			var monthYear = overallScoreStats[i][0];
		
			overallScoreStats[i][0] = splitAndEditDate(monthYear);
		}

		var overallChartData = new Array(overallScoreStats.length + 1);
		for(var i=0; i<overallChartData.length; i++){
			overallChartData[i] = new Array(2);
		}
	
		overallChartData[0]=['Month','Rating'];
	
		for(var i=0; i<overallScoreStats.length; i++){
			overallChartData[i+1] = overallScoreStats[i];
		}
	
		drawLineGraphForScoreStats(overallChartDiv, overallChartData);
	}else{
		var emptyChartData = [['Month','Ratings'],['',0]];
		drawLineGraphForScoreStats(overallChartDiv, emptyChartData);
	}
}

function drawQuestionScoreStatsGraph(questionScoreStats){
	
	if(questionScoreStats != null && questionScoreStats.length != 0){
		
		$('#question-ratings-div').removeClass('hide');
		$('#empty-questions-div').addClass('hide');
		
		var questionIdArray = new Array();
		var questionArray = new Array();
		
		var questionIterator = 0;
		for(var i=0; i<questionScoreStats.length; i++){
			if(!isContainsQuestion(questionIdArray, questionScoreStats[i][0])){
				questionIdArray[questionIterator] = questionScoreStats[i][0];
				questionArray[questionIterator++] = questionScoreStats[i][1];
			}
		}
		
		var questionScoreStatsArray = new Array();
		var questionScoreStatsIndex = 0;
				
		for(var i=0; i<questionIdArray.length; i++){
			var count = 0;
			for(var j=0; j<questionScoreStats.length; j++){
				if(questionScoreStats[j][0] == questionIdArray[i]){
					count++;
				}
			}
			var scoreStatsArray = new Array(count+1);
			var index = 1;
			scoreStatsArray[0]=['Month','Rating'];
			for(var j=0; j<questionScoreStats.length; j++){
				if(questionScoreStats[j][0] == questionIdArray[i]){
					monthYear = splitAndEditDate(questionScoreStats[j][2]);
					scoreStatsArray[index++] = [monthYear,questionScoreStats[j][3]];
				}
			}
			
			questionScoreStatsArray[questionScoreStatsIndex++] = scoreStatsArray;
		}
		
		$('#question-ratings-div').html('');
		for(var i=0; i<questionScoreStatsArray.length ; i++){
			
			var graphDivHtml = '';
			graphDivHtml += '<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12  score-stats-graph-con score-stats-ques-graph-con">'
						+ '<span class="score-stats-lbl" >'+ (i+1)+') ' + questionArray[i] + '</span>'
						+ '<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"> '
						+ '<div id="question-rating-chart-'+i+'" style="width: 100%; height: 300px"></div>'
						+ '</div></div>';
			
			$('#question-ratings-div').append(graphDivHtml);
			drawLineGraphForScoreStats("question-rating-chart-"+i, questionScoreStatsArray[i]);
		}
	}else{
		$('#question-ratings-div').addClass('hide');
		$('#empty-questions-div').removeClass('hide');
	}
	
}

function isContainsQuestion(array,questionId){
	for(var i=0; i<array.length; i++){
		if(array[i] == questionId){
			return true;
		}
	}
	
	return false;
}

function drawReportingDashButtons(columnName, columnValue){
	
	var payload = {
			"columnName" : columnName,
			"columnValue" : columnValue
		};
		
		var stages = null;
		$.ajax({
			url : './dashboardbuttonsorder.do',
			headers: {          
	            Accept : "text/plain; charset=utf-8"   
			},
			type : "GET",
			data : payload,
			cache : false,
			success : function(data){
				data = $.parseJSON(data);
				stages = data.stages;
				reportingSocialMediaButtons(stages,columnName,columnValue)
			},
			complete: function(){
				hideOverlay();
				hideDashOverlay('#mid-dash');
				hideDashOverlay('#top-dash');
				hideDashOverlay('#latest-post-ep');
			},
			error : function(e) {
				if(e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				if(e.status == 0) {
					return;
				}
				redirectErrorpage();
			}
		});
}

function reportingSocialMediaButtons(stages,columnName,columnValue){
	var max = 1;
	
	if(columnName == null){
		return;
	}
	
	if (stages != undefined && stages.length != 0) {
		if (stages.length < max) {
			$('#rep-social-media').addClass('hide');
			$('#empty-rep-social-media').removeClass('hide');
			max = stages.length;
		}
		for (var i = 0; i < max; i++) {
			var contentToDisplay = '';
			if (stages[i].profileStageKey == 'FACEBOOK_PRF') {
				contentToDisplay = '<div class="rep-fb-btn"></div>';
			} else if (stages[i].profileStageKey == 'ZILLOW_PRF') {
				contentToDisplay = '<div class="rep-zillow-btn"></div>';
			} else if (stages[i].profileStageKey == 'GOOGLE_PRF') {
				contentToDisplay = '<div class="rep-google-btn"></div>';
			} else if (stages[i].profileStageKey == 'TWITTER_PRF') {
				contentToDisplay = '<div class="rep-twitter-btn"></div>';
			} else if (stages[i].profileStageKey == 'YELP_PRF') {
				contentToDisplay = '<div class="rep-yelp-btn"></div>';
			} else if (stages[i].profileStageKey == 'LINKEDIN_PRF') {
				contentToDisplay = '<div class="rep-linked-in-btn"></div>';
			} else if (stages[i].profileStageKey == 'LICENSE_PRF') {
				contentToDisplay = 'Enter license details';
			} else if (stages[i].profileStageKey == 'HOBBIES_PRF') {
				contentToDisplay = 'Enter hobbies';
			} else if (stages[i].profileStageKey == 'ACHIEVEMENTS_PRF') {
				contentToDisplay = 'Enter achievements';
			} else if (stages[i].profileStageKey == 'INSTAGRAM_PRF') {
				contentToDisplay = '<div class="rep-instagram-btn"></div>';
			}
			
			if (i == 0) {
				$('#rep-social-media').removeClass('hide');
				$('#empty-rep-social-media').addClass('hide');
				$('#dsh-btn2').data('social', stages[i].profileStageKey);
				$('#dsh-btn2').html(contentToDisplay);
				$('#dsh-btn2').removeClass('hide');
				updateSocialMediaList(stages[i].profileStageKey);
			}
		}
		
	}else{
		$('#rep-social-media').addClass('hide');
		$('#empty-rep-social-media').removeClass('hide');
	}
}


function changeSocialMedia(columnName, columnValue){
	
		var payload = {
			"columnName" : columnName,
			"columnValue" : columnValue
		};
		
		var stages = null;
		$.ajax({
			url : './dashboardbuttonsorder.do',
			headers: {          
	            Accept : "text/plain; charset=utf-8"   
			},
			type : "GET",
			data : payload,
			cache : false,
			success : function(data){
				data = $.parseJSON(data);
				stages = data.stages;
				changeReportingSocialMediaButtons(stages);
			},
			complete: function(){
				hideOverlay();
				hideDashOverlay('#mid-dash');
				hideDashOverlay('#top-dash');
				hideDashOverlay('#latest-post-ep');
			},
			error : function(e) {
				if(e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				if(e.status == 0) {
					return;
				}
				redirectErrorpage();
			}
		});
}


function changeReportingSocialMediaButtons(stages){
var profileStageKey = $('#dsh-btn2').data('social');
	
	var max = 2;
		
	if (stages != undefined && stages.length != 0) {
		if (stages.length >= max) {
			max = stages.length;
			
			for (var i = 0; i < max; i++) {
				var contentToDisplay = '';
				var isContainsSocialMedia = false;
				for(var j=0; j<socialMediaList.length; j++){
					if(stages[i].profileStageKey == socialMediaList[j]){
						isContainsSocialMedia = true;
					}
				}
				
				if(isContainsSocialMedia){
					continue;
				}
				
				if (stages[i].profileStageKey == 'FACEBOOK_PRF') {
					contentToDisplay = '<div class="rep-fb-btn"></div>';
				} else if (stages[i].profileStageKey == 'ZILLOW_PRF') {
					contentToDisplay = '<div class="rep-zillow-btn"></div>';
				} else if (stages[i].profileStageKey == 'GOOGLE_PRF') {
					contentToDisplay = '<div class="rep-google-btn"></div>';
				} else if (stages[i].profileStageKey == 'TWITTER_PRF') {
					contentToDisplay = '<div class="rep-twitter-btn"></div>';
				} else if (stages[i].profileStageKey == 'YELP_PRF') {
					contentToDisplay = '<div class="rep-yelp-btn"></div>';
				} else if (stages[i].profileStageKey == 'LINKEDIN_PRF') {
					contentToDisplay = '<div class="rep-linked-in-btn"></div>';
				} else if (stages[i].profileStageKey == 'LICENSE_PRF') {
					contentToDisplay = 'Enter license details';
				} else if (stages[i].profileStageKey == 'HOBBIES_PRF') {
					contentToDisplay = 'Enter hobbies';
				} else if (stages[i].profileStageKey == 'ACHIEVEMENTS_PRF') {
					contentToDisplay = 'Enter achievements';
				} else if (stages[i].profileStageKey == 'INSTAGRAM_PRF') {
					contentToDisplay = '<div class="rep-instagram-btn"></div>';
				}
				
				if(contentToDisplay != ''){
					$('#empty-rep-social-media').addClass('hide');
					$('#dsh-btn2').data('social', stages[i].profileStageKey);
					$('#dsh-btn2').html(contentToDisplay);
					$('#dsh-btn2').removeClass('hide');
					$('#rep-social-media').fadeIn(500);
					updateSocialMediaList(stages[i].profileStageKey);
					break;
				}
			}
			
		}
	}else{
		$('#rep-social-media').addClass('hide');
		$('#empty-rep-social-media').removeClass('hide');	
	}
}

function updateSocialMediaList(socialMedia){
	
	socialMediaList.push(socialMedia);
	
}

function clickProcessedDiv(){
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
	$('#third-party-lbl-rect').show();
	$('#unassigned-lbl-rect').hide();
	$('#duplicate-lbl-rect').hide();
	$('#corrupted-lbl-rect').hide();
	$('#other-lbl-rect').hide();
	$('#unsubscribed-lbl-rect').hide();
	$('#processed-trans-div').fadeTo('fast','1.0');
	$('#unprocessed-trans-div').fadeTo('fast','0.2');
	
	var processed=parseInt($('#processed-lbl-span').html());
	if(processed != 0){
		$('#unclicked-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#processed-trans-graph').removeClass('hide');
		$('#empty-rep-chart-div').addClass('hide');
	}else{
		$('#unclicked-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
	}
}

function clickUnprocessedDiv(){
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
	$('#third-party-lbl-rect').hide();
	$('#unassigned-lbl-rect').show();
	$('#duplicate-lbl-rect').show();
	$('#corrupted-lbl-rect').show();
	$('#other-lbl-rect').show();
	$('#unsubscribed-lbl-rect').show();
	$('#unprocessed-trans-div').fadeTo('fast','1.0');
	$('#processed-trans-div').fadeTo('fast','0.2');
	
	var unprocessed=parseInt($('#unprocessed-lbl-span').html());
	if(unprocessed != 0){
		$('#unclicked-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').removeClass('hide');
		$('#empty-rep-chart-div').addClass('hide');
	}else{
		$('#unclicked-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
	}
}

//api calls functions

function drawTransactionStats(overviewYearData){
	
	if(overviewYearData != null && !isEmpty(overviewYearData)){
		var avgRating = overviewYearData.Rating;
		var reviewCount=  overviewYearData.TotalReview;
		paintAvgRating(avgRating);
		paintReviewCount(reviewCount);
	
	}else{
		var avgRating = 0;
		var reviewCount=  0;
		paintAvgRating(avgRating);
		paintReviewCount(reviewCount);
	}
	
	//Paint the transactions section of the reporting the dashboard
	if(overviewYearData!=null && !isEmpty(overviewYearData)){
		if($('#donutchart').length > 0 ){
	 		drawUnclickedDonutChart(overviewYearData);
	 	}
	 	if($('#processedDonutchart').length > 0 ){
	 		drawProcessedDonutChart(overviewYearData);
	 	}
		
	 	if($('#unprocessedDonutchart').length > 0 ){
	 		drawUnprocessedDonutChart(overviewYearData);
	 	}
		$('#processed-lbl-span').html(overviewYearData.Processed);
		$('#completed-lbl-span').html(overviewYearData.Completed+' ('+overviewYearData.CompletePercentage+'%)');
		$('#incomplete-lbl-span').html(overviewYearData.Incomplete+' ('+overviewYearData.IncompletePercentage+'%)');
		$('#incomplete-lbl-span-sel').html(overviewYearData.Incomplete+' ('+overviewYearData.IncompletePercentage+'%)');
		$('#social-posts-lbl-span').html(overviewYearData.SocialPosts);
		$('#zillow-lbl-span').html(overviewYearData.ZillowReviews);
		$('#third-party-lbl-span').html(overviewYearData.ThirdParty);
		$('#unprocessed-lbl-span').html(overviewYearData.Unprocessed);
		$('#unassigned-lbl-span').html(overviewYearData.Unassigned);
		$('#duplicate-lbl-span').html(overviewYearData.Duplicate);
		$('#unassigned-lbl-span-sel').html(overviewYearData.Unassigned);
		$('#corrupted-lbl-span').html(overviewYearData.Corrupted);
		var other = overviewYearData.Unprocessed - (overviewYearData.Unassigned + overviewYearData.Duplicate + overviewYearData.Corrupted);
		$('#other-lbl-span').html(other);
		$('#unsubscribed-lbl-span').html(overviewYearData.Unsubscribed);
		$('#unclicked-trans-graph').removeClass('hide');
		$('#processed-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#empty-rep-chart-div').addClass('hide');
	}else{
		if($('#donutchart').length > 0 ){
	 		drawUnclickedDonutChart(overviewYearData);
	 	}
	 	if($('#processedDonutchart').length > 0 ){
	 		drawProcessedDonutChart(overviewYearData);
	 	}
		
	 	if($('#unprocessedDonutchart').length > 0 ){
	 		drawUnprocessedDonutChart(overviewYearData);
	 	}
		$('#processed-lbl-span').html(0);
		$('#completed-lbl-span').html(0+' ('+0+'%)');
		$('#incomplete-lbl-span').html(0+' ('+0+'%)');
		$('#incomplete-lbl-span-sel').html(0);
		$('#social-posts-lbl-span').html(0);
		$('#zillow-lbl-span').html(0);
		$('#third-party-lbl-span').html(0);
		$('#unprocessed-lbl-span').html(0);
		$('#unassigned-lbl-span').html(0);
		$('#duplicate-lbl-span').html(0);
		$('#unassigned-lbl-span-sel').html(0);
		$('#corrupted-lbl-span').html(0);
		$('#other-lbl-span').html(0);
		$('#unsubscribed-lbl-span').html(0);
		$('#unclicked-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#empty-rep-chart-div').removeClass('hide');
	}	
	
	var processed=parseInt($('#processed-lbl-span').html());
	var unprocessed=parseInt($('#unprocessed-lbl-span').html());
	if(processed != 0 || unprocessed != 0){
		$('#unclicked-trans-graph').removeClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
		if(!($('#empty-rep-chart-div').hasClass('hide'))){
			$('#empty-rep-chart-div').addClass('hide');
		}	
	}else{
		$('#unclicked-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
		if($('#empty-rep-chart-div').hasClass('hide')){
			$('#empty-rep-chart-div').removeClass('hide');
		}	
	}
}

function getOverviewMonthData(month,year) {

	$.ajax({
		url : "/fetchmonthdataforoverview.do?month="+month+"&year="+year,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
						if (response.length == 0 || response == null) {
							overviewMonthData = null;
						} else {
							if(response == "{}"){
								overviewMonthData = null;
							}else{
								overviewMonthData = JSON.parse(response);
							}
						}
						drawTransactionStats(overviewMonthData);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			overviewMonthData = null;
			drawTransactionStats(overviewMonthData);
		}
	});

}


function getoverviewYearData(year) {

$.ajax({
	url : "/fetchyeardataforoverview.do?year="+year,
	type : "GET",
	cache : false,
	dataType : "json",
	success : function(response) {
					if (response.length == 0 || response == null) {
						overviewYearData = null;
					} else {
						if(response == "{}"){
							overviewYearData = null;
						}else{
							overviewYearData = JSON.parse(response);
						}
						
					}
					drawTransactionStats(overviewYearData);
	},
	error : function(e) {
		if (e.status == 504) {
			redirectToLoginPageOnSessionTimeOut(e.status);
			return;
		}
		overviewYearData = null;
		drawTransactionStats(overviewYearData);
	}
});

}

function getoverviewAllTimeData() {

	$.ajax({
		url : "/fetchalltimefromreportingoverview.do",
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
						if (response.length == 0) {
							overviewAllTimeData = null;
						} else {
							if(response == "{}"){
								overviewAllTimeData = null;
							}else{
								overviewAllTimeData = JSON.parse(response);
							}
							
						}
						
						drawTransactionStats(overviewAllTimeData);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			overviewAllTimeData = null;
			drawTransactionStats(overviewAllTimeData);
		}
	});

	}

function getOverviewData() {

	// get sps from overview data
	$.ajax({
		async : false,
		url : "/fetchspsfromreportingoverview.do",
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
						if (response.length == 0) {
							overviewData = null;
						} else {
							overviewData = JSON.parse(response);
						}
					},
		complete:function(){
			hideOverlay();
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			overviewData = null;
		}
	});
}

function getRecentActivityList(startIndex,batchSize){
	var recentActivityList=null;
	var payload={
			"startIndex" : startIndex,
			"batchSize" : batchSize
	}
	$.ajax({
		async : false,
		url : "/fetchrecentactivities.do?startIndex="+payload.startIndex+"&batchSize="+payload.batchSize,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
						recentActivityList = JSON.parse(response);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			recentActivityList = null;
		}
	});	
	return recentActivityList;
}

function getRecentActivityCount(){
	var recentActivityCount=0;
	$.ajax({
		async : false,
		url : "/fetchrecentactivitiescount.do",
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
			recentActivityCount = parseInt(response);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			recentActivityCount = 0;
		}
	});
	
	return recentActivityCount;
}

function deleteRecentActivity(fileUploadId,idIndex){
	showOverlay();
	$.ajax({
		url : "./deletefromrecentactivities.do?fileUploadId="+fileUploadId,
		type : "POST",
		dataType:"TEXT",
		async:false,
		success : function(data) {
			success=true;
			messageToDisplay = data;
			
		},
		complete : function() {	
			hideOverlay();
			
			var recentActivityCount=getRecentActivityCount();
			$('#recent-activity-row'+idIndex).fadeOut(500)
				.promise()
				.done(function(){
					if(recentActivityCount <= startIndex){
						drawRecentActivity(startIndex-10,batchSize,tableHeaderData,recentActivityCount);
					}else if(recentActivityCount>=10){
						drawRecentActivity(startIndex,batchSize,tableHeaderData,recentActivityCount);
					}
					showHidePaginateButtons(startIndex, recentActivityCount);
					
					if(recentActivityCount == 0){
						var tableData='';
						tableData+="</table><div style='text-align:center; margin:20px auto'><span class='incomplete-trans-span'>There are No Recent Activities</span></div>";
						$('#recent-activity-list-table').html(tableData);
					}
				});
			},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			messageToDisplay="Sorry! Failed to delete the activity. Please try again later";
			showError(messageToDisplay);
		}
	});
}

function getOverallScoreStats(entityId,entityType){
	
	var currentDate = new Date();
	var currentMonth = currentDate.getMonth()+1;
	var currentYear = currentDate.getFullYear();
	
	var url = "/getoverallscorestats.do?entityId="+entityId+"&entityType="+entityType+"&currentMonth="+currentMonth+"&currentYear="+currentYear;
	
	var overallScoreStats=null;
	
	$.ajax({
		url : url,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
			 overallScoreStats = JSON.parse(response);
			 drawOverallScoreStatsGraph(overallScoreStats);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}	
			drawOverallScoreStatsGraph(overallScoreStats);
		}
	});
}

function getQuestionScoreStats(entityId,entityType){
	var currentDate = new Date();
	var currentMonth = currentDate.getMonth()+1;
	var currentYear = currentDate.getFullYear();
	
	var url = "/getquestionscorestats.do?entityId="+entityId+"&entityType="+entityType+"&currentMonth="+currentMonth+"&currentYear="+currentYear;
	
	var questionScoreStats=null;
	
	$.ajax({
		url : url,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
					questionScoreStats = JSON.parse(response);
					drawQuestionScoreStatsGraph(questionScoreStats);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}	
			drawQuestionScoreStatsGraph(questionScoreStats);
		}
	});
	return questionScoreStats;
}

function saveRankingSettings(minDaysOfRegistration, minCompletedPercentage, minNoOfReviews, monthOffset, yearOffset){
	
	if(minCompletedPercentage>100 || minCompletedPercentage<0){
		return "Invalid Value for Completion Percentage. Setting for Ranking Could not be Saved";
	}
	
	var url = "/saverankingsettings.do?minDaysOfRegistration="+minDaysOfRegistration
		+"&minCompletedPercentage="+minCompletedPercentage
		+"&minNoOfReviews="+minNoOfReviews
		+"&monthOffset="+monthOffset
		+"&yearOffset="+yearOffset;
	$.ajax({
		async : false,
		url : url,
		type : "PUT",
		cache : false,
		dataType : "text",
		success : function(data) {
			message = data;						
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			message = "Setting for Ranking Could not be Saved"; 
		}
	});
	
	return message;
}

function getUserRankingList(entityType,entityId,year,month,startIndex,batchSize,timeFrame){
	var userRankingList =null;	
	
	$.ajax({
		url : "/getuserranking.do?entityId="+entityId+"&entityType="+entityType+"&month="+month+"&year="+year+"&startIndex="+startIndex+"&batchSize="+batchSize+"&timeFrame="+timeFrame,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
					userRankingList = JSON.parse(response);	
					leaderboardPageStructure(userRankingList)
		},
		complete: function(){
			hideOverlay();
			hideDashOverlay('#leaderboard-dash');
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			leaderboardPageStructure(userRankingList)
		}
	});
}

function getUserRankingCountForAdmins(entityType,entityId,year,month,batchSize,timeFrame){
	
	var userRankingCount=null;
	
	$.ajax({
		async : false,
		url : "/getuserrankingcount.do?entityId="+entityId+"&entityType="+entityType+"&month="+month+"&year="+year+"&batchSize="+batchSize+"&timeFrame="+timeFrame,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(response) {
					userRankingCount = JSON.parse(response);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}	
		}
	});
	return userRankingCount;
	
}

function getUserRankingCount(entityType,entityId,year,month,batchSize,timeFrame){
	
	var userRankingCount=null;
	
	$.ajax({
		async : false,
		url : "/getuserrankingrankandcount.do?entityId="+entityId+"&entityType="+entityType+"&month="+month+"&year="+year+"&batchSize="+batchSize+"&timeFrame="+timeFrame,
		type : "GET",
		cache : false,
		dataType : "json",
		success :function(response) {
					userRankingCount = JSON.parse(response);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}	
		}
	});
	return userRankingCount;
}

function recalculateUserRanking(){
	$('.recalculate-usr-rank-btn-active').toggle();
	$('.recalculate-usr-rank-btn-inactive').toggle();
	
	$.ajax({
		url : "/recalranking.do",
		type : "GET",
		success :function(response) {
			if(response == -1){
				$('#overlay-toast').html("ETL is already running. Please try again later.");
				showToast();
			}else{
				$('#overlay-toast').html("User ranking is being Re-Calculated. Please wait.");
				showToast();
			}	
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}	
		}
	});
}

function getIncompleteSurveyCountForNewDashboard(colName, colValue) {

	var payload = {
		"columnName" : colName,
		"columnValue" : colValue
	};
	callAjaxGetWithPayloadData("./fetchdashboardincompletesurveycount.do", function(data) {
		$('#rep-icn-sur-popup-cont').attr("data-total", data);
		var totalCount = parseInt(data);
		
		if (totalCount == 0) {
			$("#incomplete-survey-header").addClass("hide");
			$("#inc-survey-cont").addClass("hide");
			$("#paginate-buttons-survey").addClass("hide");
			$("#rep-nil-dash-survey-incomplete").removeClass("hide");
			return;
		}
		
		var batchSize = parseInt($('#rep-icn-sur-popup-cont').attr("data-batch"));
		var numPages = 0;
		if (parseInt(totalCount % batchSize) == 0) {
			numPages = parseInt(totalCount / batchSize);
		} else {
			numPages = parseInt(parseInt(totalCount / batchSize) + 1);
		}
		$('#rep-paginate-total-pages').html(numPages);

	}, payload, true);
}

function paintIncompleteSurveyListForNewDashboard(incompleteSurveystartIndex,colmName,colmValue) {
	var incompleteSurveyBatchSize = parseInt($('#rep-icn-sur-popup-cont').attr("data-batch"));
	$('#rep-sel-page').val((incompleteSurveystartIndex / incompleteSurveyBatchSize) + 1);
	var payload = {
		"columnName" : colmName,
		"columnValue" : colmValue,
		"startIndex" : incompleteSurveystartIndex,
		"batchSize" : $('#rep-icn-sur-popup-cont').attr("data-batch"),
		"origin" : "newDashboard"
	};
	callAjaxGetWithPayloadData("./fetchincompletesurveypopup.do", function(data) {
		$('#rep-icn-sur-popup-cont').html(data);
		if (parseInt(incompleteSurveystartIndex) > 0) {
			$('#rep-sur-previous').addClass('paginate-button');
		} else {
			$('#rep-sur-previous').removeClass('paginate-button');
		}
		
		// move back a page if this page has no entry
		if( parseInt($('#rep-icn-sur-popup-cont').children('.dash-lp-item').size()) < 1 && parseInt(incompleteSurveystartIndex) > 0 ){
			paintIncompleteSurveyListForNewDashboard((parseInt(incompleteSurveystartIndex) - incompleteSurveyBatchSize),colmName,colmValue);
			return;
		}
		
		incompleteSurveystartIndex = parseInt(incompleteSurveystartIndex) + parseInt($('#rep-icn-sur-popup-cont').children('.dash-lp-item').size());
		var totalSurveysCount = parseInt($('#rep-icn-sur-popup-cont').attr("data-total"));
		if (incompleteSurveystartIndex < totalSurveysCount) {
			$('#rep-sur-next').addClass('paginate-button');
		} else {
			$('#rep-sur-next').removeClass('paginate-button');
		}
	}, payload, true);
}

$(document).on('click', '#rep-resend-mult-sur-icn.mult-sur-icn-active', function() {
	var selectedSurveys = $('#rep-icn-sur-popup-cont').data('selected-survey');
	resendMultipleIncompleteSurveyRequestsForNewDashboard(selectedSurveys);
});

function resendMultipleIncompleteSurveyRequestsForNewDashboard(incompleteSurveyIds) {
	showOverlay();
	callAjaxPOSTWithTextData("/resendmultipleincompletesurveyrequest.do?surveysSelected=" + incompleteSurveyIds, function(data) {
		data = JSON.parse(data);
		if (data.errMsg == undefined || data.errMsg == "") {
			// unselect all the options after deleting
			$('#rep-icn-sur-popup-cont').data('selected-survey', []);

			var toastmsg = data.success;
			$('#overlay-toast').html(toastmsg);
			showToastLong();
			
			$('#rep-del-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#rep-resend-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#rep-icn-sur-popup-cont').data('selected-survey', []);
			$('.rep-sur-icn-checkbox').addClass('sb-q-chk-yes').removeClass('sb-q-chk-no');

			// update the page
			var incompleteSurveyStartIndex = parseInt($('#rep-icn-sur-popup-cont').attr("data-start"));
			paintIncompleteSurveyListForNewDashboard(incompleteSurveyStartIndex,$('#rep-prof-container').attr('data-column-name'),$('#rep-prof-container').attr('data-column-value'));
		}else{
			var toastmsg = data.errMsg;
			$('#overlay-toast').html(errCode);
			showToastLong();
		}
	}, true, {});
}


$(document).on('click', '#rep-del-mult-sur-icn.mult-sur-icn-active', function() {
	var selectedSurveys = $('#rep-icn-sur-popup-cont').data('selected-survey');
	removeMultipleIncompleteSurveyRequestForNewDashboard(selectedSurveys);
});


function removeMultipleIncompleteSurveyRequestForNewDashboard(incompleteSurveyIds) {
	callAjaxPOSTWithTextData("/deletemultipleincompletesurveyrequest.do?surveySetToDelete=" + incompleteSurveyIds, function(data) {
		if (data == "success") {

			// unselect all the options after deleting
			$('#rep-icn-sur-popup-cont').data('selected-survey', []);

			var totalIncSurveys = $('#rep-icn-sur-popup-cont').attr('data-total');
			totalIncSurveys = totalIncSurveys - incompleteSurveyIds.length;
			$('#rep-icn-sur-popup-cont').attr('data-total', totalIncSurveys);
			var batchSize = parseInt($('#rep-icn-sur-popup-cont').attr('data-batch'));
			var newTotalPages = 0;
			if (totalIncSurveys % batchSize == 0) {
				newTotalPages = totalIncSurveys / batchSize;
			} else {
				newTotalPages = parseInt(totalIncSurveys / batchSize) + 1;
			}
			$('#rep-paginate-total-pages').html(newTotalPages);
			for (var i = 0; i < incompleteSurveyIds.length; i++) {
				$('div[data-iden="sur-pre-' + incompleteSurveyIds[i] + '"]').remove();
			}

			$('#overlay-toast').html('Survey reminder request deleted successfully');
			showToast();

			// update the page
			var incompleteSurveyStartIndex = parseInt($('#rep-icn-sur-popup-cont').attr("data-start"));
			paintIncompleteSurveyListForNewDashboard(incompleteSurveyStartIndex,$('#rep-prof-container').attr('data-column-name'),$('#rep-prof-container').attr('data-column-value'));

			// Update the incomplete survey on dashboard
			getIncompleteSurveyCountForNewDashboard($('#rep-prof-container').attr('data-column-name'),$('#rep-prof-container').attr('data-column-value'));

			$('#rep-del-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#rep-resend-mult-sur-icn').removeClass('mult-sur-icn-active');
		}
	}, true, {});
}


$(document).on('click', '#rep-sur-next.paginate-button', function() {
	var incompleteSurveyStartIndex = parseInt($('#rep-icn-sur-popup-cont').attr("data-start"));
	var incompleteSurveyBatchSize = parseInt($('#rep-icn-sur-popup-cont').attr("data-batch"));
	incompleteSurveyStartIndex = incompleteSurveyStartIndex + incompleteSurveyBatchSize;
	$('#rep-icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListForNewDashboard(incompleteSurveyStartIndex,$('#rep-prof-container').attr('data-column-name'),$('#rep-prof-container').attr('data-column-value'));
});

$(document).on('click', '#rep-sur-previous.paginate-button', function() {
	var incompleteSurveyStartIndex = parseInt($('#rep-icn-sur-popup-cont').attr("data-start"));
	var incompleteSurveyBatchSize = parseInt($('#rep-icn-sur-popup-cont').attr("data-batch"));
	if (incompleteSurveyStartIndex % incompleteSurveyBatchSize == 0) {
		incompleteSurveyStartIndex = parseInt(incompleteSurveyStartIndex / incompleteSurveyBatchSize) - 1;
	} else {
		incompleteSurveyStartIndex = parseInt(incompleteSurveyStartIndex / incompleteSurveyBatchSize);
	}
	incompleteSurveyStartIndex = incompleteSurveyStartIndex * incompleteSurveyBatchSize;
	$('#rep-icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListForNewDashboard(incompleteSurveyStartIndex,$('#rep-prof-container').attr('data-column-name'),$('#rep-prof-container').attr('data-column-value'));
});

$(document).on('keypress', '#rep-sel-page', function(e) {
	// if the letter is not digit then don't type anything
	if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
		return false;
	}
	var batchSize = parseInt($('#rep-icn-sur-popup-cont').attr("data-batch"));
	var total = parseInt($('#rep-icn-sur-popup-cont').attr("data-total"));
	var prevPageNoVal = parseInt($('#rep-sel-page').val());
	if (prevPageNoVal == NaN) {
		prevPageNoVal = 0;
	}
	var pageNo = prevPageNoVal + String.fromCharCode(e.which);
	pageNo = parseInt(pageNo);
	var incompleteSurveyStartIndex = parseInt(pageNo - 1) * batchSize;
	if (incompleteSurveyStartIndex >= total || incompleteSurveyStartIndex <= 0) {
		return false;
	}
});

function paginateIncompleteSurveyForNewDashboard() {
	$('#rep-sel-page').blur();
	var pageNo = parseInt($('#rep-sel-page').val());
	if (pageNo == NaN || pageNo <= 0) {
		return false;
	}
	var incompleteSurveyStartIndex = 0;
	var batchSize = parseInt($('#rep-icn-sur-popup-cont').attr("data-batch"));
	incompleteSurveyStartIndex = parseInt(pageNo - 1) * batchSize;

	$('#rep-icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListForNewDashboard(incompleteSurveyStartIndex,$('#rep-prof-container').attr('data-column-name'),$('#rep-prof-container').attr('data-column-value'));
}

$(document).on('keyup', '#rep-sel-page', function(e) {
	if (e.which == 13) {
		paginateIncompleteSurveyForNewDashboard();
	}
});

$(document).on('change', '#rep-sel-page', function(e) {
	delay(function() {
		paginateIncompleteSurveyForNewDashboard();
	}, 100);
});

function showOverviewTab(){
	
	var entityId = $('#rep-prof-container').attr('data-column-value');
	var entityType = $('#rep-prof-container').attr('data-column-name');
	activaTab('overview-tab');
	$('#overview-tab').addClass('active');
	 drawCompletionRateGraph();
 	drawSpsStatsGraph();
	drawNpsStatsGraph(entityId,entityType);
}

function autoRefresh(tableHeaderData){
	
	setTimeout(function(){
		
		var startIndexStr = $('#rec-act-start-index').attr('data-start-index');
		var startIndex = parseInt(startIndexStr);
		if($('#reports_page_container').length<=0){
			return;
		}
		
		var recentActivityCount=getRecentActivityCount();
		drawRecentActivity(startIndex,10,tableHeaderData,recentActivityCount);
		showHidePaginateButtons(startIndex, recentActivityCount);
		
		if(recentActivityCount == 0){
			var tableData='';
			tableData+="</table><div style='text-align:center; margin:20px auto'><span class='incomplete-trans-span'>There are No Recent Activities</span></div>";
			$('#recent-activity-list-table').html(tableData);
		}
		
		autoRefresh(tableHeaderData);
	}, 30000);
}

function drawPhraseList(){
	var startIndex = 0;
	var batchSize = 100;

	var payload = {
			"startIndex" : startIndex,
			"batchSize" : batchSize,
			"monitorType" : '',
			"text" : ''
	}

	$.ajax({
		url : "/getmonitorslistbytype.do",
		type : "GET",
		data : payload,
		cache : false,
		dataType : "json",
		success : function(response) {
			var monitorData = response.filterKeywords;
			var phraseList = [];
			if(monitorData != undefined && monitorData != null){
				for(var i = 0; i<monitorData.length; i++ ){
					if(monitorData[i].status==1){
						phraseList.push(monitorData[i].phrase);
					}
				}
				var uniquePhraseList = phraseList.filter(function(elem, index, self) {
				    return index === self.indexOf(elem);
				})
				populatePhrases(uniquePhraseList);
			}
		},
		error : function(e){
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}


function populatePhrases(uniquePhraseList){
	for(var i=0;i<uniquePhraseList.length;i++){
		var option = '<option value="'+i+'">'+uniquePhraseList[i]+'</option>';
		$('#sm-keywords-selector').append(option);
	}
}

// new dashboard event handlers
$(document).on('click','#prof-company-review-count',function(e){
	e.stopPropagation();
	activaTab('reviews-tab');
	delay(function(){
		$(window).scrollTop($('#rep-reviews-container').offset().top);
	},300);
});

$(document).on('click','#incompleted-lbl-sel',function(e){
	e.stopPropagation();
	activaTab('incomplete-surveys-tab');
	
	delay(function(){
		$(window).scrollTop($('#rep-dash-survey-incomplete').offset().top);
	},300);
	
});

$(document).on('click','#unassigned-lbl-sel-span',function(e){
	e.stopPropagation();
	showMainContent('./showapps.do');
});

$(document).on('click','#unprocessed-trans-div',function(e){
	
	clickUnprocessedDiv();
});

$(document).on('click','#processed-trans-div',function(e){
	clickProcessedDiv();
});

$(document).on('click','#chart-icn-btn',function(e){
	$('#incompleted-details-selectable').show();
	$('#incompleted-details').addClass('hide');
	$('#incompleted-details').removeClass('inline-flex-class');
	$('#unassigned-details-selectable').show();
	$('#unassigned-details').addClass('hide');
	$('#unassigned-details').removeClass('inline-flex-class');
	$('.processed-background-rect').show();
	$('#processed-background-rect').hide();
	$('.unprocessed-background-rect').show();
	$('#unprocessed-background-rect').hide();
	$('#unprocessed-lbl-rect').show();
	$('#processed-lbl-rect').show();
	$('#completed-lbl-rect').hide();
	$('#incompleted-lbl-rect').hide();
	$('#social-posts-lbl-rect').hide();
	$('#zillow-lbl-rect').hide();
	$('#third-party-lbl-rect').hide();
	$('#unassigned-lbl-rect').hide();
	$('#duplicate-lbl-rect').hide();
	$('#corrupted-lbl-rect').hide();
	$('#other-lbl-rect').hide();
	$('#unsubscribed-lbl-rect').hide();
	
	$('#unprocessed-trans-div').fadeTo('fast','1.0');
	$('#processed-trans-div').fadeTo('fast','1.0');
	
	var processed=parseInt($('#processed-lbl-span').html());
	var unprocessed=parseInt($('#unprocessed-lbl-span').html());
	if(processed != 0 || unprocessed != 0){
		$('#unclicked-trans-graph').removeClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
		$('#empty-rep-chart-div').addClass('hide');
	}else{
		$('#unclicked-trans-graph').addClass('hide');
		$('#unprocessed-trans-graph').addClass('hide');
		$('#processed-trans-graph').addClass('hide');
	}
});