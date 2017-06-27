<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${detractor}" var="detractors"></c:set>
<c:set value="${passives}" var="passives"></c:set>
<c:set value="${promoters}" var="promoters"></c:set>
<c:set value="${SPS_score}" var="spsScore"></c:set>

<style>
#wrapper{
  width: 350px;
  height: 300px;
  margin-left:-70px;
  margin-top:-40px;
}
 
#meter{
  width: 80%;
  height: 80%;
}
#metre-needle{
	transform:rotate(20deg);
}
</style>

<div id="wrapper" onload="">
<img id="metre-needle" src="${initParam.resourcesPath}/resources/images/svg-meter-gauge-needle.svg" style="
    margin-left: 155px;
    margin-top: 70px;
    height: 100px;
    position:absolute;
    z-index:1000">
<div style="margin-left: 165px;
    margin-top: 180px;
    font-weight: bold !important;
    font-size: medium;    
    position: absolute;">SPS
<div style="font-weight: bold !important;
    font-size: medium;
    color: white;
    background: #5e5e5e;
    width: 50px;
    height: 35px;
    padding: 5px;
    border-radius: 7px;">32</div></div>

    
  <svg id="meter">
<path id="arc1" fill="none" stroke="#dc3912" stroke-width="40" />
<path id="arc2" fill="none" stroke="#a7abb2" stroke-width="40" />
<path id="arc3" fill="none" stroke="#109618" stroke-width="40" />
  </svg>

</div>
<script>

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

	$(document).ready(function() {
		var detractorEndAngle = ('${detractors}'/100)*110;
		var passivesEndAngle = (('${passives}'/100)*110)+detractorEndAngle;
		var promotersEndAngle = (('${promoters}'/100)*110)+passivesEndAngle;
		var spsScore = '${spsScore}';
		
		var marginLeft = parseInt($("#metre-needle").css("margin-left"));
		var marginTop = parseInt($("#metre-needle").css("margin-top"));
		
		$('#metre-needle').css({'transform':'rotate(' + spsScore + 'deg)'});
		var needleDegree = Math.abs(spsScore - 20)/2;
		
		$("#metre-needle").css("margin-left",marginLeft+needleDegree+'px');
		$("#metre-needle").css("margin-top",marginTop+needleDegree+'px');
		
		document.getElementById("arc1").setAttribute("d", describeArc(150, 150, 70, 250, detractorEndAngle));
		document.getElementById("arc2").setAttribute("d", describeArc(150, 150, 70, detractorEndAngle+2, passivesEndAngle));
		document.getElementById("arc3").setAttribute("d", describeArc(150, 150, 70, passivesEndAngle+2, promotersEndAngle));
	});
</script>
