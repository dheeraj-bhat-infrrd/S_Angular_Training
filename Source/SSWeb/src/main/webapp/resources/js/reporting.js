//javascript for populating the graphs in reporting dashboard overview

var monthNamesList = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];	

function drawSpsStatsGraph(){
	var chartData;
	var spsChartData = [[ 'SPS', 'Detractors', 'Passives', 'Promoters'],[]];
	google.charts.load('current', {	packages : [ 'corechart', 'bar' ]});
	google.charts.setOnLoadCallback(drawStacked);
	
	function drawStacked() {
		
		$.ajax({
			url : "/fetchreportingspsstats.do",
			type : "GET",
			cache : false,
			dataType : "json",
			success : function(data) {
				chartData = data;
				
				var spsChartData = new Array(chartData.length+1);
				for (var k = 0; k <= chartData.length; k++) {
					  spsChartData[k] = new Array(4);
				}
				spsChartData[0] = [ 'SPS', 'Detractors', 'Passives', 'Promoters'];
				
				for(var i=1;i<=chartData.length;i++){
					var monthName = monthNamesList[(chartData[i-1][1])-1];
					spsChartData[i][0] = monthName + "/" + chartData[i-1][0];
					spsChartData[i][1] = chartData[i-1][2];	
					spsChartData[i][2] = chartData[i-1][3];
					spsChartData[i][3] = chartData[i-1][4];
				}
				
				var data = google.visualization.arrayToDataTable(spsChartData);

				var options = { title : 'SPS Stats',
				                legend : {position : 'none'},
				                bar : {groupWidth : '40%'},
				                isStacked : true,
				                height : 300,
				                vAxis : {
				                         	gridlines : {
				                                  			count : 14
				                                  		}
				                        },
				                colors : [ '#E8341F', '#999999', '#7ab400' ]
				               };

				var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
				chart.draw(data, options);
			},
			error : function(e) {
				
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				var spsChartData = [[ 'SPS', 'Detractors', 'Passives', 'Promoters'],['',0,0,0]];
				
				var data = google.visualization.arrayToDataTable(spsChartData);

				var options = { title : 'SPS Stats',
				                legend : {position : 'none'},
				                bar : {groupWidth : '40%'},
				                isStacked : true,
				                height : 300,
				                vAxis : {	
				                			minValue:0,
				                			maxValue:10,
				                         	gridlines : {
				                                  			count : 5
				                                  		}
				                        },
				                colors : [ '#E8341F', '#999999', '#7ab400' ]
				               };

				var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
				chart.draw(data, options);
			}
		});
	}
}

function drawAvgRatingsGraph(){
	
	google.charts.load("current", {packages:["corechart"]});
	google.charts.setOnLoadCallback(drawChart);

	function drawChart() {
		
		$.ajax({
			url : "/fetchaveragereportingrating.do",
			type : "GET",
			cache : false,
			dataType : "json",
			success : function(data) {
				chartData = data;
				
				var avgRatingChartData = new Array(chartData.length+1);
				for (var k = 0; k <= chartData.length; k++) {
					avgRatingChartData[k] = new Array(2);
				}
				avgRatingChartData[0] = ['X','Y'];
				
				for(var i=1;i<=chartData.length;i++){
					var monthName = monthNamesList[(chartData[i-1][1])-1];
					avgRatingChartData[i][0] = monthName + "/" + chartData[i-1][0];
					avgRatingChartData[i][1] = chartData[i-1][2];	
				}
				
				
	  		var data = google.visualization.arrayToDataTable(avgRatingChartData);

	  		var options = {
	  			legend: 'none',
	    		height:300,
	    		width:1000,
	    		vAxis: { 
	    				title:'Average Rating',
	    				minValue:0, 
	    				maxValue:6,
	    				gridlines : {
	      					count : 7
	      				}
	  		   		},
	    		colors: ['009fe0'],
	    		pointSize: 5
	 		};

	  		var chart = new google.visualization.LineChart(document.getElementById('average_chart_div'));
	  		chart.draw(data, options);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
	var spsChartData = [['X','Y'],['',0]];
			
			var data = google.visualization.arrayToDataTable(spsChartData);

			var options = { 
							legend: 'none',
		    				height:300,
		    				width:1000,
		    				vAxis: { 
		        				title:'Average Rating',
		        				minValue:0, 
		        				maxValue:6,
		        				gridlines : {
		          					count : 7
		          				}
		      		   		},
		        			colors: ['009fe0'],
		        			pointSize: 5
			               };

			var chart = new google.visualization.ColumnChart(document.getElementById('average_chart_div'));
			chart.draw(data, options);
		}
		});
	}

}

function drawCompletionRateGraph(){
	google.charts.load('current', {'packages':['corechart']});
	google.charts.setOnLoadCallback(drawChart);

	function drawChart() {
		
		$.ajax({
			url : "/fetchreportingcompletionrate.do",
			type : "GET",
			cache : false,
			dataType : "json",
			success : function(data) {
				chartData = data;
				var maxTransactionValue = 0;
				var compRateChartData = new Array(chartData.length+1);
				for (var k = 0; k <= chartData.length; k++) {
					compRateChartData[k] = new Array(3);
				}
				compRateChartData[0] = ['Month', 'Completed Transactions ', 'Incomplete Transactions '];
				
				for(var i=1;i<=chartData.length;i++){
					var monthName = monthNamesList[(chartData[i-1][1])-1];
					compRateChartData[i][0] = monthName + "/" + chartData[i-1][0];
					compRateChartData[i][1] = chartData[i-1][2];
					compRateChartData[i][2] = chartData[i-1][3];
					
					if(compRateChartData[i][1] > maxTransactionValue){
						maxTransactionValue =  compRateChartData[i][1];
					}
					
					if(compRateChartData[i][2] > maxTransactionValue){
						maxTransactionValue =  compRateChartData[i][2];
					}
				}
			
				var maxVAxisValue = 0;
				
				if(maxTransactionValue<10){
					maxVAxisValue = maxTransactionValue + 5;
				}else{
					maxVAxisValue = maxTransactionValue + 10;
				}
				
	  		var data = google.visualization.arrayToDataTable(compRateChartData);

	  		var options = {
	    		title: 'Completion Rate',
	    		height:300,
	    		width:1100,
	    		chartArea:{width:'78%'},
	    		vAxis: { minValue:0, maxValue: maxVAxisValue ,gridlines : {count : 6	}},
	    		legend: { position: 'right',alignment:'center',maxLines:2},
	    		pointSize:5
	  		};

	 		 var chart = new google.visualization.LineChart(document.getElementById('completion_chart_div'));

	  		chart.draw(data, options);
			},
		error : function(e) {
			
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			var spsChartData = [['Month', 'Completed Transactions ', 'Incomplete Transactions '],['',0,0]];
			
			var data = google.visualization.arrayToDataTable(spsChartData);

			var options = {
							title: 'Completion Rate',
		    				height:300,
		    				width:1100,
		    				chartArea:{width:'78%'},
		    				vAxis: { minValue:0, maxValue: 10 ,gridlines : {count : 6	}},
		    				legend: { position: 'right',alignment:'center',maxLines:2},
		    				pointSize:5
			               };

			var chart = new google.visualization.ColumnChart(document.getElementById('completion_chart_div'));
			chart.draw(data, options);
		}
		});
	}
}

function drawDonutChart(){
	
	var overviewData = getOverviewData();
	
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
function getOverviewData(){
	
	var overviewData;
	$.ajax({
		async:false,
		url : "/showreportingoverview.do",
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
				if(data.length==0){
					overviewData = null;
				}else{
					overviewData = data;
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