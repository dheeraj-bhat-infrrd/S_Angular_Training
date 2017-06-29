<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${detractor}" var="detractors"></c:set>
<c:set value="${passives}" var="passives"></c:set>
<c:set value="${promoters}" var="promoters"></c:set>

<script>
	google.charts.load('current', {packages: ['corechart', 'bar']});
	google.charts.setOnLoadCallback(drawStacked);
	
	function drawStacked() {
		var data = google
					.visualization.arrayToDataTable([
		                ['SPS', 'Detractors', 'Passives', 'Promoters',{ role: 'annotation' } ],
		                ['Jan', 20, 30, 40,''],
		                ['Feb', 40, 20, 40,''],
		                ['Mar', 40, 20, 50,''],
		                ['Apr', 30, 20, 30,''],
		                ['May', 40, 20, 50,''],
		                ['Jun', 60, 30, 50,''],
		                ['Jul', 70, 40, 50,''],
		                ['Aug', 100,20, 30,''],
		                ['Sep', 70, 30, 100,''],
		                ['Oct', 90, 20, 120,''],
		                ['Nov', 60, 40, 50,''],
		                ['Dec', 50, 20, 90,''],
		               ]);

		var options = {
		   title:'SPS Stats',
		   legend: { position: 'none' },
		   bar: { groupWidth: '50%' },
		   isStacked: true,
		   height:300,
	       vAxis: { gridlines: { count: 14 } },
	       colors:['#E8341F','#999999','#7ab400']
		};

      var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
      chart.draw(data, options);
    }
</script>
 <div id="chart_div" style="width:100%"></div>