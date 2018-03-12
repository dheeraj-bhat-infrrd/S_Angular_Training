<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

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
<img id="metre-needle" src="${initParam.resourcesPath}/resources/images/svg-meter-gauge-needle.svg" class="sps-metre-needle-img">
<div class="sps-metre-needle-text">SPS
	<div id="spsScorebox" class="sps-metre-needle-val"></div>
</div>

    
  <svg id="meter">
		<path id="arc1" fill="none" stroke="#E8341F" stroke-width="32" />
		<path id="arc2" fill="none" stroke="#999999" stroke-width="32" />
		<path id="arc3" fill="none" stroke="#7ab400" stroke-width="32" />
		<path id="arc4" fill="none" stroke="#E8341F" stroke-width="32" />
		<path id="arc5" fill="none" stroke="#999999" stroke-width="32" />
		<path id="arc6" fill="none" stroke="#7ab400" stroke-width="32" />
  </svg>

</div>
