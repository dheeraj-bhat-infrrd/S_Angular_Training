<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"> 
	<div id="completion_chart_div" style="width:100%; height:300px; margin-top:20px; "></div>
</div>
<script type="text/javascript">
	$(window).resize(function(){
		if($('#completion_chart_div').length>0){
			drawCompletionRateGraph();
		}
	});
</script>