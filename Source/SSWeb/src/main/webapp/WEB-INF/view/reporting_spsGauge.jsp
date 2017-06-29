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
    border-radius: 7px;">${spsScore}</div></div>

    
  <svg id="meter">
<path id="arc1" fill="none" stroke="#E8341F" stroke-width="40" />
<path id="arc2" fill="none" stroke="#999999" stroke-width="40" />
<path id="arc3" fill="none" stroke="#7ab400" stroke-width="40" />
<path id="arc4" fill="none" stroke="#E8341F" stroke-width="40" />
<path id="arc5" fill="none" stroke="#999999" stroke-width="40" />
<path id="arc6" fill="none" stroke="#7ab400" stroke-width="40" />
  </svg>

</div>
<script>
	
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
		var detractors = '${detractors}';
		var passives =   '${passives}';
		var promoters =  '${promoters}';
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

	$(document).ready(function() {
		var spsScore = '${spsScore}';
		
		
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
</script>
