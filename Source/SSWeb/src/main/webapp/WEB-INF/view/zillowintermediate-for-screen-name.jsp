<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty accountSettings}">
	<!-- <c:set var = "profile" value = "${ accountSettings }"></c:set> -->
	<c:set var = "nmlsId" value = "${ nmls-id }"></c:set>
</c:if>
<c:if test="${empty accountSettings}">
	<c:if test="${ not empty profileSettings }">
		<!-- <c:set var = "profile" value = "${ profileSettings }"></c:set> -->
		<c:set var = "nmlsId" value = "${ nmls-id }"></c:set>
	</c:if>
</c:if>




<div class=" padding-001 ">
	<div class="container login-container">
		<div class="row login-row">
			
			<div class=" padding-001 margin-top-25 margin-bottom-25 bg-fff margin-0-auto col-xs-12 col-md-12 col-sm-12 col-lg-12">
				<div class="text-center font-24">
					<div style="padding: 0px 20px;" class="clearfix">
								<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
									<div>
										<form id="zillowForm1">
										</form>
									</div>
								</div>
					</div>				
					<div style="font-size: 11px; text-align: center;"></div>	
				</div>				
			</div>
		</div>
	</div>
</div>
