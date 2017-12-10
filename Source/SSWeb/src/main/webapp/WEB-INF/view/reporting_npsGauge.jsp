<style>
#nps-wrapper{
  width: 350px;
  height: 300px;
  margin-left:-70px;
  margin-top:-40px;
}
 
#nps-meter{
  width: 80%;
  height: 80%;
}
#nps-metre-needle{
	transform:rotate(20deg);
}
</style>

<div id="nps-wrapper" onload="">
<img id="nps-metre-needle" src="${initParam.resourcesPath}/resources/images/svg-meter-gauge-needle.svg" 
	style=" margin-left: 155px; margin-top: 80px; height: 80px; position:absolute; z-index:1000">
<div style="margin-left: 165px; margin-top: 180px; font-weight: bold !important; font-size: medium; position: absolute;">NPS
	<div id="npsScorebox" style="font-weight: bold !important; font-size: medium; color: white; background: #5e5e5e;
    	width: 50px; height: 35px; padding: 5px; border-radius: 7px;">
 	</div>
 </div>

    
  <svg id="nps-meter">
		<path id="nps-arc1" fill="none" stroke="#E8341F" stroke-width="32" />
		<path id="nps-arc2" fill="none" stroke="#999999" stroke-width="32" />
		<path id="nps-arc3" fill="none" stroke="#7ab400" stroke-width="32" />
		<path id="nps-arc4" fill="none" stroke="#E8341F" stroke-width="32" />
		<path id="nps-arc5" fill="none" stroke="#999999" stroke-width="32" />
		<path id="nps-arc6" fill="none" stroke="#7ab400" stroke-width="32" />
  </svg>

</div>
<script>
drawNpsGauge();
</script>
