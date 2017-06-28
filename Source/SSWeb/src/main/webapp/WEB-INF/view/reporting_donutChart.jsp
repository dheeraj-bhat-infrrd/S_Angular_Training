<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
.overlay-incomplete-trans {
display:block;
width:100px;
height:50px;
text-align:center;
vertical-align: middle;
position: absolute;
top: 0px;  /* chartArea top  */
left: 0px; /* chartArea left */
font-size: small;
margin: 80px 25%;
background:none;
}
.overlay-label-trans{
    display:block;
width:100px;
height:50px;
text-align:center;
vertical-align: middle;
position: absolute;
top: 0px;  /* chartArea top  */
left: 0px; /* chartArea left */
margin: 100px 25%;
font-size: small;
}
</style>
<c:set value="${total_incomplete_transactions}" var="totalIncompleteTrans"></c:set>
<c:set value="${corrupted}" var="corruptedTrans"></c:set>
<c:set value="${duplicate}" var="duplicateTrans"></c:set>
<c:set value="${archieved}" var="archievedTrans"></c:set>
<c:set value="${mismatched}" var="mismatchedTrans"></c:set>

<script type="text/javascript">
      google.charts.load("current", {packages:["corechart"]});
      google.charts.setOnLoadCallback(drawChart);
      
      var totalIncompleteTransactions = '${totalIncompleteTrans}' 
      var corrupted= ('${corruptedTrans}'/100) * totalIncompleteTransactions;
      var duplicate= ('${duplicateTrans}'/100) * totalIncompleteTransactions;
      var archived= ('${archievedTrans}'/100) * totalIncompleteTransactions;
      var mismatched= ('${mismatchedTrans}'/100) * totalIncompleteTransactions;
      
      function drawChart() {
    	 
        var data = google.visualization.arrayToDataTable([
          ['Task', 'Hours per Day'],
          ['Mismatched', mismatched],
          ['Corrupted',  corrupted],
          ['Duplicate',  duplicate],
          ['Archived', archived],
        ]);

        var options = {
          pieHole: 0.5,
          legend: { 
      	    position : 'none'
      	  },
      	  pieSliceText:'none',
      	  chartArea:{
      		  width:'95%',
      		  height:'95%'
      	  }
        };

        var chart = new google.visualization.PieChart(document.getElementById('donutchart'));
        chart.draw(data, options);
      }
</script>
<div id="JSFiddle" style="position:relative">
<div id="donutchart" style="width: 95%; height: 90%;"></div>
<div id="cnt" class="overlay-incomplete-trans">${totalIncompleteTrans}</div>
<div class="overlay-label-trans">Transactions</div>
</div>