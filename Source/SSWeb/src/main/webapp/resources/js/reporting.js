//javascript for populating the graphs in reporting dashboard overview

var monthNamesList = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];	
var overviewData=getOverviewData();

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
						title : 'SPS Stats',
						legend : {
							position : 'none'
						},
						bar : {
							groupWidth : '40%'
						},
						isStacked : true,
						height : 300,
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
													spsChartData[i][0] = monthName
															+ "/"
															+ chartData[i - 1][0];
													spsChartData[i][1] = chartData[i - 1][2];
													spsChartData[i][2] = chartData[i - 1][3];
													spsChartData[i][3] = chartData[i - 1][4];
												}

												var data = google.visualization
														.arrayToDataTable(spsChartData);

												var options = {
													title : 'SPS Stats',
													legend : {
														position : 'none'
													},
													bar : {
														groupWidth : '40%'
													},
													isStacked : true,
													height : 300,
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

function drawAvgRatingsGraph(){
	
	google.charts.load("current", {packages:["corechart"]});
	google.charts.setOnLoadCallback(drawChart);
	
	function drawEmptyChart(){
		var spsChartData = [ [ 'X', 'Y' ],
								[ '', 0 ] ];

						var data = google.visualization
								.arrayToDataTable(spsChartData);

						var options = {
							legend : 'none',
							height : 300,
							width : 1000,
							vAxis : {
								title : 'Average Rating',
								minValue : 0,
								maxValue : 6,
								gridlines : {
									count : 7
								}
							},
							colors : [ '009fe0' ],
							pointSize : 5
						};

						var chart = new google.visualization.ColumnChart(
								document
										.getElementById('average_chart_div'));
						chart.draw(data, options);
	}
	
	function drawChart() {
		$.ajax({
					async : false,
					url : "/fetchaveragereportingrating.do",
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
												var avgRatingChartData = new Array(
														chartData.length + 1);
												for (var k = 0; k <= chartData.length; k++) {
													avgRatingChartData[k] = new Array(
															2);
												}
												avgRatingChartData[0] = [ 'X',
														'Y' ];

												for (var i = 1; i <= chartData.length; i++) {
													var monthName = monthNamesList[(chartData[i - 1][1]) - 1];
													avgRatingChartData[i][0] = monthName
															+ "/"
															+ chartData[i - 1][0];
													avgRatingChartData[i][1] = chartData[i - 1][2];
												}

												var data = google.visualization
														.arrayToDataTable(avgRatingChartData);

												var options = {
													legend : 'none',
													height : 300,
													width : 1000,
													vAxis : {
														title : 'Average Rating',
														minValue : 0,
														maxValue : 6,
														gridlines : {
															count : 7
														}
													},
													colors : [ '009fe0' ],
													pointSize : 5
												};

												var chart = new google.visualization.LineChart(
														document
																.getElementById('average_chart_div'));
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
						title : 'Completion Rate',
						height : 300,
						width : 1100,
						chartArea : {
							width : '78%'
						},
						vAxis : {
							minValue : 0,
							maxValue : 10,
							gridlines : {
								count : 6
							}
						},
						legend : {
							position : 'right',
							alignment : 'center',
							maxLines : 2
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
													compRateChartData[i][0] = monthName
															+ "/"
															+ chartData[i - 1][0];
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
													maxVAxisValue = maxTransactionValue + 10;
												}

												var data = google.visualization
														.arrayToDataTable(compRateChartData);

												var options = {
													title : 'Completion Rate',
													height : 300,
													width : 1100,
													chartArea : {
														width : '78%'
													},
													vAxis : {
														minValue : 0,
														maxValue : maxVAxisValue,
														gridlines : {
															count : 6
														}
													},
													legend : {
														position : 'right',
														alignment : 'center',
														maxLines : 2
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

function drawDonutChart(){
	
	//var overviewData = getOverviewData();
	
	if(overviewData != null){
	      google.charts.load("current", {packages:["corechart"]});
	      google.charts.setOnLoadCallback(drawChart);
	      
	      var totalIncompleteTransactions = overviewData.TotalIncompleteTransactions;
	      $('#incompleteTransValue').html(totalIncompleteTransactions);
	      
	      var corruptedTrans = overviewData.CorruptedPercentage;
	      var duplicateTrans = overviewData.DuplicatePercentage;
	      var archivedTrans = overviewData.ArchievedPercentage;
	      var mismatchedTrans = overviewData.MismatchedPercentage;
	      
	      var corrupted= (corruptedTrans/100) * totalIncompleteTransactions;
	      var duplicate= (duplicateTrans/100) * totalIncompleteTransactions;
	      var archived= (archivedTrans/100) * totalIncompleteTransactions;
	      var mismatched= (mismatchedTrans/100) * totalIncompleteTransactions;
	      var totalOfCorDupArcMis = corrupted + duplicate + archived + mismatched;
	      var other = totalIncompleteTransactions - totalOfCorDupArcMis;
	      
	      function drawChart() {
	    	 
	        var data = google.visualization.arrayToDataTable([
	          ['Task', 'Hours per Day'],
	          ['Mismatched', mismatched],
	          ['Corrupted', corrupted],
	          ['Duplicate', duplicate],
	          ['Archived', archived],
	          ['Other', other]
	        ]);

	        var options = {
	          pieHole: 0.5,
	          legend: { 
	      	    position : 'none'
	      	  },
	      	  pieSliceText:'none',
	      	  chartArea:{
	      		  width:'95%',
	      		  height:'97%'
	      	  },
	      	slices: [{color : '#009fe0'},{color: '#E8341F'}, {color: '#985698'}, {color: '#7ab400'}, {color: '#f4ad42'}]
	        };

	        var chart = new google.visualization.PieChart(document.getElementById('donutchart'));
	        chart.draw(data, options);
	      }
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
					document.getElementById("arc1").setAttribute("d", describeArc(150, 150, 70, detractorStartAngle, 0));
					document.getElementById("arc4").setAttribute("d", describeArc(150, 150, 70, 0, detractorEndAngle));
				}else{
					document.getElementById("arc1").setAttribute("d", describeArc(150, 150, 70, detractorStartAngle, detractorEndAngle));
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
						document.getElementById("arc2").setAttribute("d", describeArc(150, 150, 70, passivesStartAngle, passivesEndAngle));
					}else{
						document.getElementById("arc2").setAttribute("d", describeArc(150, 150, 70, passivesStartAngle, 0));
						document.getElementById("arc5").setAttribute("d", describeArc(150, 150, 70, 0, passivesEndAngle));
					}
				}else{
					document.getElementById("arc5").setAttribute("d", describeArc(150, 150, 70, passivesStartAngle, passivesEndAngle));
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
						document.getElementById("arc3").setAttribute("d", describeArc(150, 150, 70, promotersStartAngle, promotersEndAngle));
					}else{
						document.getElementById("arc3").setAttribute("d", describeArc(150, 150, 70, promotersStartAngle, 0));
						document.getElementById("arc6").setAttribute("d", describeArc(150, 150, 70, 0, promotersEndAngle));
					}
				}else{
					document.getElementById("arc6").setAttribute("d", describeArc(150, 150, 70, promotersStartAngle, promotersEndAngle));
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
							$("#metre-needle").css("margin-left",marginLeft-marginNeedle+5+'px');
						}else{
							$("#metre-needle").css("margin-left",marginLeft-marginNeedle-5+'px');
						}
						$("#metre-needle").css("margin-top",marginTop+marginNeedle-20+'px');
						
				}else if(spsScore > 15){
					
					needleDegree = Math.abs(spsScore)*1.1;
					if(spsScore > 87){
						$("#metre-needle").css("margin-left",marginLeft+marginNeedle-10+'px');
					}else{
						$("#metre-needle").css("margin-left",marginLeft+marginNeedle+'px');
					}
					$("#metre-needle").css("margin-top",marginTop+marginNeedle+'px');
					
				}else if(spsScore > 7 && spsScore <=15){
					
					needleDegree = Math.abs(spsScore)*1.1;
					$("#metre-needle").css("margin-left",marginLeft-5+'px');
					
				}else if(spsScore == 0 || (spsScore > 0 && spsScore <= 7)){
					needleDegree = Math.abs(spsScore)*1.1;
					$("#metre-needle").css("margin-left",marginLeft-13+'px');
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
			
			//donutChart checks
			var totalIncompleteTransactions = overviewData.TotalIncompleteTransactions;
			
			if(totalIncompleteTransactions > 0){
				$('#donutChartSuccess').show();
				$('#donutChartFailure').hide();
			}else{
				$('#donutChartSuccess').hide();
				$('#donutChartFailure').show();
			}
			
			//surveys sent, surveys completed, social posts and zillow reviews values
			var surveysSent = overviewData.TotalSurveySent;
			var surveysCompleted = overviewData.TotalSurveyCompleted;
			var socialPosts = overviewData.TotalSocialPost;
			var zillowReviews = overviewData.TotalZillowReviews;
			
			$('#surveysSentValue').html(surveysSent);
			$('#surveysCompValue').html(surveysCompleted);
			$('#socialPostsValue').html(socialPosts);
			$('#zillowReviewsValue').html(zillowReviews);
					
	}
}

function getOverviewData() {

	// var overviewData;
	$.ajax({
		async : false,
		url : "/fetchreportingoverview.do",
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