<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
	<c:when test="${columnName == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>

<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.help.key" /></div>
			<c:if test="${not empty assignments}">
				
			</c:if>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container">
		<div id="prof-container" data-profile-master-id="${profileMasterId}"
			data-column-name="${columnName}" data-account-type="${accounttype}"
			data-column-value="${columnValue}" class="dash-top-info dash-prof-wrapper">
			<div id="dash-profile-detail-circles" class="row row-dash-top-adj">
				<!-- Populated by dashboard_profiledetail.jsp -->
			</div>
		</div>

		

		<div class="dash-stats-wrapper bord-bot-dc clearfix help">
		
		<form action="" method="post" class="contact-form"  name=f
enctype="multipart/form-data"
onsubmit="return check();">
								<p class="half">
				<div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left">Name</div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" name="regionName" id="region-name-txt" placeholder="Write your name" value="">
	     </div>
	 </div>
	 
	 
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left">Subject</div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" name="regionAddress1" id="region-address1-txt" placeholder="Write the subject" value="">
	     </div>
	 </div>
	 
	 
	 <div id="bd-multiple" class="bd-hr-form-item clearfix hide" style="display: block;">
	     <div class="float-left bd-frm-left">Message</div>
	     <div class="float-left bd-frm-right">
	         <textarea class="bd-frm-rt-txt-area" id="selected-user-txt-area" name="selectedUserEmailArray" placeholder="Type your message here"></textarea>
	     </div>
	     
	    
	 </div>
	 
	 
	   
	

	 
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"></div>
	    <!-- <input class ="" type="file"  size="40"
accept="" name="file[]" multiple> -->

<input type="file" name="attachment" id="attachment" onchange="document.getElementById('moreUploadsLink').style.display = 'block';" />
<div id="moreUploads"></div>
<div id="moreUploadsLink" style="display:none;"><a href="javascript:addFileInput();">Attach another File</a></div>

		      	 <div id="send-button" style="margin:0 auto ; margin-top: 20px"  class="bd-btn-save cursor-pointer "
		      	 >Send message</div>

 </div>
	 
	 </form>
	 
			
			
			
			
		</div>

		
	</div>
	

</div>

<!-- <script type="text/javascript" language="JavaScript">
function check() {
  var ext = document.f.pic.value;
  ext = ext.substring(ext.length-3,ext.length);
  ext = ext.toLowerCase();
  if(ext != 'jpg') {
    alert('You selected a .'+ext+
          ' file; please select a .jpg file instead!');
    return false; }
  else
    return true; }
</script> -->
<script type="text/javascript" >
var upload_number = 2;
function addFileInput() {
 	var d = document.createElement("div");
 	var file = document.createElement("input");
 	file.setAttribute("type", "file");
 	file.setAttribute("name", "attachment"+upload_number);
 	d.appendChild(file);
 	document.getElementById("moreUploads").appendChild(d);
 	upload_number++;
}
</script>