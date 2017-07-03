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
margin: 80px 28%;
background:none;
font-weight:600 !important
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
margin: 100px 28%;
font-size: small;
font-weight:600 !important
}
</style>

<script>
drawDonutChart();
</script>

<div id="JSFiddle" style="position:relative">
<div id="donutchart" style="width: 95%; height: 90%;"></div>
<div>
<div id="incompleteTransValue" class="overlay-incomplete-trans" ></div>
<div class="overlay-label-trans">Transactions</div>
</div>
</div>