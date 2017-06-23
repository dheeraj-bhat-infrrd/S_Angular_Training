<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript">
google.charts.load("current", {packages:["corechart"]});
google.charts.setOnLoadCallback(drawChart);
function drawChart() {
  var data = google.visualization.arrayToDataTable
      ([['X', 'Y'],
        ['Jan', 24000],
        ['Feb', 25000],
        ['Mar', 26000],
        ['Apr', 25000],
        ['May', 26000],
        ['Jun', 25000],
        ['Jul', 24000],
        ['Aug', 23000],
        ['Sep', 25000],
        ['Oct', 26000],
        ['Nov', 27000],
        ['Dec', 23000],
  ]);

  var options = {
  	title:'Average Rating',
    legend: 'none',
    height:300,
    width:1000,
    hAxis: {  maxValue: 9 },
    vAxis: { minValue:21000, maxValue:28000},
    colors: ['009fe0'],
    pointSize: 5
 };

  var chart = new google.visualization.LineChart(document.getElementById('average_chart_div'));
  chart.draw(data, options);
}
</script>

  <div id="average_chart_div" style="width:100%"></div>