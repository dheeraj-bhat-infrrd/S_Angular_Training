<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
 <div id="bd-multiple" class="bd-hr-form-item clearfix hide">
     <div class="float-left bd-frm-left"><spring:message code="label.addmultipleusers.key"/></div>
     <div class="float-left bd-frm-right">
         <textarea class="bd-frm-rt-txt-area"></textarea>
     </div>
 </div>
 <div id="bd-single" class="bd-hr-form-item clearfix">
     <div class="float-left bd-frm-left"><spring:message code="label.chooseuserforregion.key"/></div>
     <div class="float-left bd-frm-right pos-relative">
         <input class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img">
         <div class="bd-frm-rt-dd-list dd-com-list hide">
             <div data-option="test1" class="bd-frm-rt-dd-item dd-com-item">test1</div>
             <div data-option="test2" class="bd-frm-rt-dd-item dd-com-item">test2</div>
             <div data-option="test3" class="bd-frm-rt-dd-item dd-com-item">test3</div>
             <div data-option="test4" class="bd-frm-rt-dd-item dd-com-item">test4</div>
         </div>
     </div>
 </div>
 <div class="bd-hr-form-item clearfix">
     <div class="float-left bd-frm-left"></div>
     <div class="float-left bd-frm-right">
         <div class="bd-frm-check-wrapper clearfix">
             <div class="float-left bd-check-img"></div>
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