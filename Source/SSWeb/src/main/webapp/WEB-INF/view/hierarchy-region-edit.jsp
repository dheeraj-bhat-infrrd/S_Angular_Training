<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<form id="edit-region-form">
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.regionname.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" name="regionName" id="region-name-txt">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.addressline1.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" name="regionAddress1" id="region-address1-txt">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.addressline2.key"/></div>
	     <div class="float-left bd-frm-right">
	         <input class="bd-frm-rt-txt" id="region-address2-txt" name="regionAddress2">
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"></div>
	     <div class="float-left bd-frm-right">
	         <div class="bd-frm-rad-wrapper clearfix">
	             <div class="float-left bd-cust-rad-item clearfix">
	                 <div data-type="single" class="float-left bd-cust-rad-img bd-cust-rad-img-checked"></div>
	                 <div class="float-left bd-cust-rad-txt"><spring:message code="label.addsingleuser.key"/></div>
	             </div>
	             <div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix">
	                 <div data-type="multiple" class="float-left bd-cust-rad-img"></div>
	                 <div class="float-left bd-cust-rad-txt"><spring:message code="label.addmultipleusers.key"/></div>
	             </div>
	         </div>
	     </div>
	 </div>
	 <input type="hidden" data-user-selection-type="single" id="user-selection-info"/>
	 <div id="bd-multiple" class="bd-hr-form-item clearfix hide">
	     <div class="float-left bd-frm-left"><spring:message code="label.addmultipleusers.key"/></div>
	     <div class="float-left bd-frm-right">
	         <textarea class="bd-frm-rt-txt-area" id="selected-user-txt-area" name="selectedUserEmailArray" placeholder='<spring:message code="label.addmultipleemailids.key"/>'></textarea>
	     </div>
	 </div>
	 <div id="bd-single" class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"><spring:message code="label.chooseuserforregion.key"/></div>
	     <div class="float-left bd-frm-right pos-relative">
	         <input autocomplete="off" id="selected-user-txt" name="selectedUserEmail" class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img" placeholder='<spring:message code="label.userselector.placeholder.key"/>'/>
	         <input type="hidden" name="selectedUserId" id="selected-userid-hidden">
	         <div id="users-droplist" class="bd-frm-rt-dd-list dd-com-list hide">
	         	<!-- list of users from solr gets displayed here   -->
	         </div>
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix" id="admin-privilege-div">
	     <div class="float-left bd-frm-left"></div>
	     <div class="float-left bd-frm-right">
	         <div class="bd-frm-check-wrapper clearfix">
	             <div class="float-left bd-check-img"></div>
	             <input type="hidden" name="isAdmin" value="true" id="is-admin-chk" class="ignore-clear">
	             <div class="float-left bd-check-txt"><spring:message code="label.grantadminprivileges.key"/></div>
	         </div>
	     </div>
	 </div>
	 <div class="bd-hr-form-item clearfix">
	     <div class="float-left bd-frm-left"></div>
	     <div class="float-left bd-frm-right">
	         <div id="btn-region-save" class="bd-btn-save cursor-pointer"><spring:message code="label.save.key"/></div>
	     </div>
	 </div>
 </form>