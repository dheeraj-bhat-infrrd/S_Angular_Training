//javascript for populating the graphs in reporting dashboard overview

var monthNamesList = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];	
var overviewData=getOverviewData();
var socialMediaList = new Array();

function drawSpsStatsGraph(){
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
					async : false,
					url : "/fetchreportingspsstats.do",
					type : "GET",
					cache : false,
					dataType : "json",
					success : function(data) {
						if (data.status == 200) {
							$.ajax({
										url : data.url,
										type : "GET",
										cache : false,
										dataType : "json",
										success : function(response) {
											chartData = JSON.parse(response);

											if (chartData.length == 0) {
												drawEmptyChart();
											} else {
												var spsChartData = new Array(
														chartData.length + 1);
												for (var k = 0; k <= chartData.length; k++) {
													spsChartData[k] = new Array(
															4);
												}
												spsChartData[0] = [ 'SPS',
														'Detractors',
														'Passives', 'Promoters' ];

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
												}

												var data = google.visualization
														.arrayToDataTable(spsChartData);

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
										error : function(e) {

											if (e.status == 504) {
												redirectToLoginPageOnSessionTimeOut(e.status);
												return;
											}
											drawEmptyChart();
										}
									});
						}else{
							drawEmptyChart();
						}
					},
					error : function(e) {
						if (e.status == 504) {
							redirectToLoginPageOnSessionTimeOut(e.status);
							return;
						}
						drawEmptyChart();
					}
				});
	}
}

function drawCompletionRateGraph(){
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
					async : false,
					url : "/fetchreportingcompletionrate.do",
					type : "GET",
					cache : false,
					dataType : "json",
					success : function(data) {
						if (data.status == 200) {
							$.ajax({
										url : data.url,
										type : "GET",
										cache : false,
										dataType : "json",
										success : function(response) {
											chartData = JSON.parse(response);

											if (chartData.length == 0) {
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
													console.log(yearStr.match(/.{1,2}/g)[1]);
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
										error : function(e) {

											if (e.status == 504) {
												redirectToLoginPageOnSessionTimeOut(e.status);
												return;
											}
											drawEmptyChart();
										}
									});
						}else{
							drawEmptyChart();
						}
					},
					error : function(e) {
						if (e.status == 504) {
							redirectToLoginPageOnSessionTimeOut(e.status);
							return;
						}
						drawEmptyChart();
					}
				});
	}
}

function getOverviewMonthData(month,year) {

	// var overviewData;
	$.ajax({
		async : false,
		url : "/fetchmonthdataforoverview.do?month="+month+"&year="+year,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			if (data.status == 200) {
				$.ajax({
					async : false,
					url : data.url,
					type : "GET",
					cache : false,
					dataType : "json",
					success : function(response) {
						if (response.length == 0) {
							overviewMonthData = null;
						} else {
							if(response == "{}"){
								overviewMonthData = null;
							}else{
								overviewMonthData = JSON.parse(response);
							}
						}
					},
					error : function(e) {
						if (e.status == 504) {
							redirectToLoginPageOnSessionTimeOut(e.status);
							return;
						}
						overviewMonthData = null;
					}
				});
			}else{
				overviewMonthData = null;
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			overviewMonthData = null;
		}
	});
	
	return overviewMonthData;

}


function getoverviewYearData(year) {

// var overviewData;
$.ajax({
	async : false,
	url : "/fetchyeardataforoverview.do?year="+year,
	type : "GET",
	cache : false,
	dataType : "json",
	success : function(data) {
		if (data.status == 200) {
			$.ajax({
				async : false,
				url : data.url,
				type : "GET",
				cache : false,
				dataType : "json",
				success : function(response) {
					if (response.length == 0) {
						overviewYearData = null;
					} else {
						if(response == "{}"){
							overviewYearData = null;
						}else{
							overviewYearData = JSON.parse(response);
						}
						
					}
				},
				error : function(e) {
					if (e.status == 504) {
						redirectToLoginPageOnSessionTimeOut(e.status);
						return;
					}
					overviewYearData = null;
				}
			});
		}else{
			overviewYearData = null;
		}
	},
	error : function(e) {
		if (e.status == 504) {
			redirectToLoginPageOnSessionTimeOut(e.status);
			return;
		}
		overviewYearData = null;
	}
});

return overviewYearData;

}

function getoverviewAllTimeData() {

	// var overviewData;
	$.ajax({
		async : false,
		url : "/fetchalltimefromreportingoverview.do",
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			if (data.status == 200) {
				$.ajax({
					async : false,
					url : data.url,
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
					},
					error : function(e) {
						if (e.status == 504) {
							redirectToLoginPageOnSessionTimeOut(e.status);
							return;
						}
						overviewAllTimeData = null;
					}
				});
			}else{
				overviewAllTimeData = null;
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			overviewAllTimeData = null;
		}
	});

	return overviewAllTimeData;

	}

function drawUnclickedDonutChart(){
	 
	var monthYear = getTimeFrameValue();
	var overviewYearData;
	
	if(monthYear.month == 14){
		overviewYearData = getoverviewAllTimeData();
	}else if(monthYear.month == 13){
    	overviewYearData =  getoverviewYearData(monthYear.year);
    }else{
    	overviewYearData = getOverviewMonthData(monthYear.month, monthYear.year);
    }
	
	 google.charts.load("current", {packages:["corechart"]});
     google.charts.setOnLoadCallback(drawChart);
     
     var processed;
     var unprocessed;
     if(overviewYearData !=null){
    	 processed = overviewYearData.Processed;
         unprocessed = overviewYearData.Unprocessed;
     }else{
    	 processed = 0;
         unprocessed = 0;
     }
     
     function drawChart() {
    	 
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
	      	tooltip: { trigger: 'none' }
	        };

	        var chart = new google.visualization.PieChart(document.getElementById('donutchart'));
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
		        
		        var chart = new google.visualization.PieChart(document.getElementById('chart-icn-chart'));
		        
		        chart.draw(data, optionsChartIcn);
		        
		        
	      }
}

function drawProcessedDonutChart(){
	
	var monthYear = getTimeFrameValue();
	var overviewYearData;
	
	if(monthYear.month == 14){
		overviewYearData = getoverviewAllTimeData();
	}else if(monthYear.month == 13){
    	overviewYearData =  getoverviewYearData(monthYear.year);
    }else{
    	overviewYearData = getOverviewMonthData(monthYear.month, monthYear.year);
    }
    
	 google.charts.load("current", {packages:["corechart"]});
     google.charts.setOnLoadCallback(drawChart);
     
     var incomplete;
     var completed;
     
     if(overviewYearData !=null){
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
	      	tooltip: { trigger: 'none' }
	        };

	        var chart = new google.visualization.PieChart(document.getElementById('processedDonutchart'));
	        chart.draw(data, options);
	       
	      }
}

function drawUnprocessedDonutChart(){
	 google.charts.load("current", {packages:["corechart"]});
    google.charts.setOnLoadCallback(drawChart);
    
    var monthYear = getTimeFrameValue();
    var overviewYearData;
    
    if(monthYear.month == 14){
		overviewYearData = getoverviewAllTimeData();
	}else if(monthYear.month == 13){
    	overviewYearData =  getoverviewYearData(monthYear.year);
    }else{
    	overviewYearData = getOverviewMonthData(monthYear.month, monthYear.year);
    }
    
    
    var unassigned;
    var duplicate;
    var corrupted;
    var other;
    
    if(overviewYearData != null){
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
	      	tooltip: { trigger: 'none' }
	        };

	        var chart = new google.visualization.PieChart(document.getElementById('unprocessedDonutchart'));
	        chart.draw(data, options);
	      }
}

function getTimeFrameValue(){
	var currentDate =  new Date();
	var currentMonth = currentDate.getMonth();
	var currentYear = currentDate.getFullYear();
	
	var monthYear={
			month:13,
			year:2017
	};
	var timeFrame = parseInt($('#time-frame-sel').attr('data-column-value'));
	switch(timeFrame){
		case 100: monthYear.month=14;
				  monthYear.year=currentYear;
				  return monthYear;
		
		case 101: monthYear.month=currentMonth+1;
		  		monthYear.year=currentYear;
		  		return monthYear;
		
		case 102: monthYear.month=currentMonth;
  				monthYear.year=currentYear;
  				return monthYear;
  		
		case 103: monthYear.month=13;
  				monthYear.year=currentYear;
  				return monthYear;
		
		case 104: monthYear.month=13;
  				monthYear.year=currentYear+1;
  				return monthYear;
  		
		case 1: monthYear.month=1;
  				monthYear.year=currentYear;
  				return monthYear;
  				
		case 2: monthYear.month=2;
			monthYear.year=currentYear;
			return monthYear;
			
		case 3: monthYear.month=3;
			monthYear.year=currentYear;
			return monthYear;
			
		case 4: monthYear.month=4;
			monthYear.year=currentYear;
			return monthYear;
			
		case 5: monthYear.month=5;
			monthYear.year=currentYear;
			return monthYear;
			
		case 6: monthYear.month=6;
			monthYear.year=currentYear;
			return monthYear;
			
		case 7: monthYear.month=7;
			monthYear.year=currentYear;
			return monthYear;
			
		case 8: monthYear.month=8;
			monthYear.year=currentYear;
			return monthYear;
		
		case 9: monthYear.month=9;
			monthYear.year=currentYear;
			return monthYear;
			
		case 10: monthYear.month=10;
			monthYear.year=currentYear;
			return monthYear;
			
		case 11: monthYear.month=11;
			monthYear.year=currentYear;
			return monthYear;
		
		case 12: monthYear.month=12;
			monthYear.year=currentYear;
			return monthYear;
	}
}

function drawSpsGauge(){
	
	if(overviewData != null){
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
				
				if(overviewData != null){
					
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
	
	if(overviewData != null){
		
		var detractors = overviewData.DetractorPercentage;
		var passives =   overviewData.PassivesPercentage;
		var promoters =  overviewData.PromoterPercentage;
		
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
	}
}

function getOverviewData() {

	// get sps from overview data
	$.ajax({
		async : false,
		url : "/fetchspsfromreportingoverview.do",
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			if (data.status == 200) {
				$.ajax({
					async : false,
					url : data.url,
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
					error : function(e) {
						if (e.status == 504) {
							redirectToLoginPageOnSessionTimeOut(e.status);
							return;
						}
						overviewData = null;
					}
				});
			}else{
				overviewData = null;
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			overviewData = null;
		}
	});
	
	return overviewData;

}

//javascript for reporting_reports page

//Reports Page Generate report button actions
$(document).on('change', '#generate-survey-reports', function() {
	
	var selectedVal = $('#generate-survey-reports').val();
	var key = parseInt(selectedVal);
	if(key == 101 || key == 102 || key == 103 || key == 106){
		$('#date-pickers').hide();
	}else{
		$('#date-pickers').show();
	}
	
	if(key == 106){
		$('#report-time-div').removeClass('hide');
	}else{
		$('#report-time-div').addClass('hide');
	}
});

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
		break;
	}
	
	var dateTimeFrame = month+"/01/"+year;
	
	return dateTimeFrame;
}

$(document).on('click', '#reports-generate-report-btn', function(e) {
	var selectedValue = $('#generate-survey-reports').val();
	var key = parseInt(selectedValue);
	var startDate = $('#dsh-start-date').val();
	var endDate = $("#dsh-end-date").val();
	
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
	
	var success = false;
	var messageToDisplay;
	var payload = {
			"startDate" : startDate,
			"endDate" : endDate,
			"reportId" : key
		};
	
	showOverlay();
		$.ajax({
			url : "./savereportingdata.do?startDate="+payload.startDate+"&endDate="+payload.endDate+"&reportId="+payload.reportId,
			type : "POST",
			dataType:"TEXT",
			async:false,
			success : function(data) {
				success=true;
				messageToDisplay = data;
				showInfoForReporting(messageToDisplay);
			},
			complete : function() {	
				hideOverlay();
			},
			error : function(e) {
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
	location.reload(true);
});

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
		success : function(data) {
			if (data.status == 200) {
				$.ajax({
					async : false,
					url : data.url,
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
			}else{
				recentActivityList = null;
			}
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

function getStatusString(status){
		var statusString;
		switch(status){
		case 1: statusString='Pending';
			break;
		case 0: statusString='Download';
			break;
		case 2: statusString='Failed';
			break;
		default: statusString='Failed'
		}
		return statusString;
	}
var startIndex=0;
var batchSize=10
var tableHeaderData;
var recentActivityList;

function drawRecentActivity(start,batchSize,tableHeader){
	
	tableHeaderData=tableHeader;
	startIndex=start;
	recentActivityList = getRecentActivityList(startIndex,batchSize);
	var tableData=''; 
	for(var i=0;i<recentActivityList.length;i++){
		
		var statusString = getStatusString(recentActivityList[i][6]);
		var startDate = getDateFromDateTime(recentActivityList[i][2]);
		var endDate =getDateFromDateTime(recentActivityList[i][3]);
		
		tableData += "<tr id='recent-activity-row"+i+"' class=\"u-tbl-row user-row \">"
			+"<td class=\"v-tbl-recent-activity fetch-name hide\">"+i+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold tbl-black-text\">"+recentActivityList[i][0]+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-blue-text\">"+recentActivityList[i][1]+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-black-text "+(startDate==null?("recent-activity-date-range\">"+" "):("\">"+startDate))+" - "+(endDate==null?" ":endDate)+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold tbl-black-text\">"+recentActivityList[i][4]+" "+recentActivityList[i][5]+"</td>";
		
		if(recentActivityList[i][6]==0){	
		tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'><a id=\"downloadLink"+i+"\"class='txt-bold tbl-blue-text downloadLink cursor-pointer'>"+statusString+"</a></td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold \" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		}else if(recentActivityList[i][6]==2){
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'>"+statusString+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold\" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		}else{
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'>"+statusString+"</td>"
				+"<td class=\"v-tbl-recent-activity fetch-name txt-bold\" >  </td>"
				+"</tr>";
		}
	}
	
	var recentActivityCount = getRecentActivityCount();
	if(recentActivityCount == 0){
		tableData='';
		tableData+="</table><div style='text-align:center; margin:20px auto'><span class='incomplete-trans-span'>There are No Recent Activities</span></div>";
		$('#recent-activity-list-table').html(tableData);
	}else{
		$('#recent-activity-list-table').html(tableHeaderData+tableData+"</table>");
	}
	
}

function getDateFromDateTime(dateTime){
	if(dateTime != null){
	return dateTime.match(/[a-zA-z]{3} \d+, \d{4}/)[0];
	}
	
	return null;
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
						drawRecentActivity(startIndex-10,batchSize,tableHeaderData);
					}else if(recentActivityCount>=10){
						drawRecentActivity(startIndex,batchSize,tableHeaderData);
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

$(document).on('click','.downloadLink',function(e){
	var clickedID = this.id;
	var indexRecentActivity = clickedID.match(/\d+$/)[0];
	var downloadLink=recentActivityList[indexRecentActivity][7];
	window.location=downloadLink;
});

$(document).on('click','.recent-act-delete-x',function(e){
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
	
	var currentDate =  new Date();
	var currentMonth = currentDate.getMonth();
	var currentYear = currentDate.getFullYear();
	
	var monthYear = getTimeFrameValue();
	var overviewYearData;
	
	if(monthYear.month == 14){
		overviewYearData = getoverviewAllTimeData();
	}else if(monthYear.month == 13){
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
		
		var avgRating = overviewYearData.Rating;
		var reviewCount=  overviewYearData.TotalReview;
		paintAvgRating(avgRating);
		paintReviewCount(reviewCount);
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
		
		var avgRating = 0;
		var reviewCount=  0;
		paintAvgRating(avgRating);
		paintReviewCount(reviewCount);
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
	$('#unassigned-lbl-rect').hide();
	$('#duplicate-lbl-rect').hide();
	$('#corrupted-lbl-rect').hide();
	$('#other-lbl-rect').hide();
	
	drawUnclickedDonutChart();
	drawProcessedDonutChart();
	drawUnprocessedDonutChart();
}

function getUserRankingList(entityType,entityId,year,month,startIndex,batchSize,timeFrame){
	var userRankingList =null;	
	
	$.ajax({
		async : false,
		url : "/getuserranking.do?entityId="+entityId+"&entityType="+entityType+"&month="+month+"&year="+year+"&startIndex="+startIndex+"&batchSize="+batchSize+"&timeFrame="+timeFrame,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			if (data.status == 200) {
				$.ajax({	
							async : false,
							url : data.url,
							type : "GET",
							cache : false,
							dataType : "json",
							success : function(response) {
								userRankingList = JSON.parse(response);		
							}
				});
			}
		},	
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
	return userRankingList;
}

function getUserRankingCountForAdmins(entityType,entityId,year,month,batchSize,timeFrame){
	
	var userRankingCount=null;
	
	$.ajax({
		async : false,
		url : "/getuserrankingcount.do?entityId="+entityId+"&entityType="+entityType+"&month="+month+"&year="+year+"&batchSize="+batchSize+"&timeFrame="+timeFrame,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			if (data.status == 200) {
				$.ajax({
							async:false,
							url : data.url,
							type : "GET",
							cache : false,
							dataType : "json",
							success : function(response) {
								userRankingCount = JSON.parse(response);
							}
						});
			}
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
		success : function(data) {
			if (data.status == 200) {
				$.ajax({
							async:false,
							url : data.url,
							type : "GET",
							cache : false,
							dataType : "json",
							success : function(response) {
								userRankingCount = JSON.parse(response);
							}
						});
			}
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

function drawLeaderboardTableStructure(userRankingList,userId,profileMasterId){
	
	var tableHeaderData='<table id="leaderboard-table" class="v-um-tbl leaderboard-table">'
		+'<tr id="u-tbl-header" class="u-tbl-header">'
		+'<td class="lead-tbl-ln-of text-center">Rank</td>'
		+'<td class="v-tbl-uname lead-name-alignment">Name</td>'
		+'<td class="lead-tbl-ln-of text-center">Reviews</td>'
		+'<td class="lead-tbl-ln-of text-center">Average Score</td>'
		+'<td class="lead-tbl-ln-of text-center">SPS</td>'
		+'<td class="lead-tbl-ln-of text-center">Completion %</td>'
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
				
				profileImageUrl = getProfileImageByUserId(userRankingList[i][0]);
				if(profileImageUrl != null && profileImageUrl!=""){
					imageDiv='<img id="lead-prof-image-edit" class="prof-image prof-image-edit pos-relative leaderboard-pic-circle" src="'+profileImageUrl+'"></img>';
				}else{
					imageDiv='<div id="lead-prof-image-edit" class="prof-image prof-image-edit pers-default-big pos-relative leaderboard-pic-circle"></div>';
				}
				if(userRankingList[i][0] == userId  && profileMasterId == 4){
					tableData += '<tr class="u-tbl-row leaderboard-row selected-row " >';
					$('#rank-span').html(userRankingList[i][1]);
					$('#user-score-span').html(userRankingList[i][4]);
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
				
				profileImageUrl = getProfileImageByUserId(userRankingList[i][0]);
				if(profileImageUrl != null && profileImageUrl!=""){
					imageDiv='<img id="lead-prof-image-edit" class="prof-image prof-image-edit pos-relative leaderboard-pic-circle" src="'+profileImageUrl+'"></img>';
				}else{
					imageDiv='<div id="lead-prof-image-edit" class="prof-image prof-image-edit pers-default-big pos-relative leaderboard-pic-circle"></div>';
				}
				
				if(userRankingList[i][0] == "${userId}"){
					nonRankedTableData += '<tr class="u-tbl-row leaderboard-row selected-row " >';
					$('#rank-span').html('NR');
					$('#user-score-span').html(userRankingList[i][4]);
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
	}else{
		$('#lead-ranks-above').removeClass('hide');
		$('#lead-ranks-above').addClass('block-display');
		
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

function getProfileImageByUserId(userId){
	var profileImageUrlData=null;
	$.ajax({
		async : false,
		url : "/getuserprofimageforleaderboard.do?userId="+userId,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			profileImageUrlData = data;						
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}	
		}
	});
	
	return profileImageUrlData;
}

function saveRankingSettings(minDaysOfRegistration, minCompletedPercentage, minNoOfReviews, monthOffset, yearOffset){
	
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
		var scoreStatsChartData = [
									['Month','Rating'],
									[ '', 0 ] ];
				
			if(chartData != null && chartData.length > 0){
				scoreStatsChartData = chartData;
			}
				
			var data = google.visualization.arrayToDataTable(scoreStatsChartData);

			var options = {
							chartArea : {
							width : '95%'
							},
							width: 800,
							height : 300,
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
			
			var chart = new google.visualization.LineChart(document.getElementById(chartDiv));
			chart.draw(data, options);
	});
}

function getOverallScoreStats(entityId,entityType){
	
	var currentDate = new Date();
	var currentMonth = currentDate.getMonth();
	var currentYear = currentDate.getFullYear();
	
	var url = "/getoverallscorestats.do?entityId="+entityId+"&entityType="+entityType+"&currentMonth="+currentMonth+"&currentYear="+currentYear;
	
	var overallScoreStats=null;
	
	$.ajax({
		async : false,
		url : url,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			if (data.status == 200) {
				$.ajax({
							async:false,
							url : data.url,
							type : "GET",
							cache : false,
							dataType : "json",
							success : function(response) {
								overallScoreStats = JSON.parse(response);
							}
						});
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}	
		}
	});
	console.log(overallScoreStats);
	return overallScoreStats;
}

function getQuestionScoreStats(entityId,entityType){
	var currentDate = new Date();
	var currentMonth = currentDate.getMonth();
	var currentYear = currentDate.getFullYear();
	
	var url = "/getquestionscorestats.do?entityId="+entityId+"&entityType="+entityType+"&currentMonth="+currentMonth+"&currentYear="+currentYear;
	
	var questionScoreStats=null;
	
	$.ajax({
		async : false,
		url : url,
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			if (data.status == 200) {
				$.ajax({
							async:false,
							url : data.url,
							type : "GET",
							cache : false,
							dataType : "json",
							success : function(response) {
								questionScoreStats = JSON.parse(response);
							}
						});
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}	
		}
	});
	console.log(questionScoreStats);
	return questionScoreStats;
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

function drawOverallScoreStatsGraph(entityId, entityType){
	var overallChartDiv = "overall-rating-chart";
	var overallScoreStats = getOverallScoreStats(entityId, entityType);
	
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

function drawQuestionScoreStatsGraph(entityId,entityType){
	var questionScoreStats = getQuestionScoreStats(entityId, entityType);
	
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
		
		for(var i=0; i<questionScoreStatsArray.length ; i++){
			
			var graphDivHtml = '';
			graphDivHtml += '<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12" style="margin-top: 10px; display: inline-block; float:left; width:100%;height:350px; margin-left:15px">'
						+ '<span class="rep-sps-lbl" style="margin-top: 13px;">'+ (i+1)+') ' + questionArray[i] + '</span>'
						+ '<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"> '
						+ '<div id="question-rating-chart-'+i+'" style="width:80%; height:300px;margin: 20px 20px 20px 60px;"></div>'
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

function getReportingSocialMediaConnections(columnName,columnValue){
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
			async : false,
			cache : false,
			success : function(data){
				data = $.parseJSON(data);
				stages = data.stages;
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
		return stages;
}

function drawReportingDashButtons(columnName, columnValue){
	
	var stages = getReportingSocialMediaConnections(columnName, columnValue);
	
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
				contentToDisplay = 'Connect to Facebook';
			} else if (stages[i].profileStageKey == 'ZILLOW_PRF') {
				contentToDisplay = 'Connect to Zillow';
			} else if (stages[i].profileStageKey == 'GOOGLE_PRF') {
				contentToDisplay = 'Connect to Google+';
			} else if (stages[i].profileStageKey == 'TWITTER_PRF') {
				contentToDisplay = 'Connect to Twitter';
			} else if (stages[i].profileStageKey == 'YELP_PRF') {
				contentToDisplay = 'Connect to Yelp';
			} else if (stages[i].profileStageKey == 'LINKEDIN_PRF') {
				contentToDisplay = 'Connect to Linkedin';
			} else if (stages[i].profileStageKey == 'LICENSE_PRF') {
				contentToDisplay = 'Enter license details';
			} else if (stages[i].profileStageKey == 'HOBBIES_PRF') {
				contentToDisplay = 'Enter hobbies';
			} else if (stages[i].profileStageKey == 'ACHIEVEMENTS_PRF') {
				contentToDisplay = 'Enter achievements';
			}
			
			if (i == 0) {
				$('#rep-social-media').removeClass('hide');
				$('#empty-rep-social-media').addClass('hide');
				$('#dsh-btn2').data('social', stages[i].profileStageKey);
				$('#dsh-btn2').html(contentToDisplay);
				$('#dsh-btn2').removeClass('hide');
				updateSocialMediaList(stages, stages[i].profileStageKey);
			}
		}
		
	}else{
		$('#rep-social-media').addClass('hide');
		$('#empty-rep-social-media').removeClass('hide');
	}
	
	
}

function changeSocialMedia(columnName, columnValue){
	
	var profileStageKey = $('#dsh-btn2').data('social');
	
	var stages = getReportingSocialMediaConnections(columnName, columnValue);
	
	var max = 2;
		
	if (stages != undefined && stages.length != 0) {
		if (stages.length < max) {
			$('#rep-social-media').fadeIn(500);
			$('#empty-rep-social-media').addClass('hide');
		}else{
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
					contentToDisplay = 'Connect to Facebook';
				} else if (stages[i].profileStageKey == 'ZILLOW_PRF') {
					contentToDisplay = 'Connect to Zillow';
				} else if (stages[i].profileStageKey == 'GOOGLE_PRF') {
					contentToDisplay = 'Connect to Google+';
				} else if (stages[i].profileStageKey == 'TWITTER_PRF') {
					contentToDisplay = 'Connect to Twitter';
				} else if (stages[i].profileStageKey == 'YELP_PRF') {
					contentToDisplay = 'Connect to Yelp';
				} else if (stages[i].profileStageKey == 'LINKEDIN_PRF') {
					contentToDisplay = 'Connect to Linkedin';
				} else if (stages[i].profileStageKey == 'LICENSE_PRF') {
					contentToDisplay = 'Enter license details';
				} else if (stages[i].profileStageKey == 'HOBBIES_PRF') {
					contentToDisplay = 'Enter hobbies';
				} else if (stages[i].profileStageKey == 'ACHIEVEMENTS_PRF') {
					contentToDisplay = 'Enter achievements';
				}
				
				if(contentToDisplay != ''){
					$('#empty-rep-social-media').addClass('hide');
					$('#dsh-btn2').data('social', stages[i].profileStageKey);
					$('#dsh-btn2').html(contentToDisplay);
					$('#dsh-btn2').removeClass('hide');
					$('#rep-social-media').fadeIn(500);
					updateSocialMediaList(stages, stages[i].profileStageKey);
					break;
				}
			}
			
		}
	}else{
		$('#rep-social-media').addClass('hide');
		$('#empty-rep-social-media').removeClass('hide');	
	}
	
}

function updateSocialMediaList(stages,socialMedia){
	
	socialMediaList.push(socialMedia);
	
	if(socialMediaList.length >= stages.length){
		socialMediaList.length = 0;
	}
}