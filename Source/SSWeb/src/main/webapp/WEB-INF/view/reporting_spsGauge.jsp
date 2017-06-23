<style>
#wrapper{
  width: 350px;
  height: 300px;
  margin-left:-70px;
  margin-top:10px;
}
 
#meter{
  width: 80%;
  height: 80%;
  transform: rotateX(180deg);
}
#metre-needle{
	transform:rotate(20deg);
}
</style>

<div id="wrapper">
<img id="metre-needle" src="${initParam.resourcesPath}/resources/images/svg-meter-gauge-needle.svg" style="
    margin-left: 150px;
    margin-top: 30px;
    height: 130px;
    position:absolute;
    z-index:1000">
<div style="margin-left: 155px;
    margin-top: 180px;
    font-weight: bold !important;
    font-size: medium;    
    position: absolute;">SPS
<div style="font-weight: bold !important;
    font-size: medium;
    color: white;
    background: black;
    width: 40px;
    height: 30px;
    padding: 2px;
    border-radius: 3px;">32</div></div>

    
  <svg id="meter">
<circle id="low" r="100" cx="50%" cy="50%" stroke="#dc3912"
stroke-width="35" stroke-dasharray="371, 200" fill="none">
</circle>
<circle id="avg" r="100" cx="50%" cy="50%" stroke="#a7abb2"
stroke-width="35" stroke-dasharray="120, 943" fill="none">
</circle>
 <circle id="high" r="100" cx="50%" cy="50%" stroke="#109618"
stroke-width="35" stroke-dasharray="70, 495" fill="none">
</circle>
  </svg>

</div>