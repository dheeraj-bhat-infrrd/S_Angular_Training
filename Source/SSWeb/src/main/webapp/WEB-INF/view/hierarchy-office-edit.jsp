<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<form id="edit-office-form">
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.officename.key"/></div>
    <div class="float-left bd-frm-right">
        <input class="bd-frm-rt-txt" name="officeName" id="office-name-txt">
    </div>
</div>
<div id="bd-assign-to" class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.assignto.key"/></div>
    <div id="assign-to-selector" class="float-left bd-frm-right pos-relative">
        <input id="assign-to-txt" data-assignto="region" value='<spring:message code="label.region.key"/>' class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img ignore-clear"/>
        <div id="assign-to-droplist" class="bd-frm-rt-dd-list dd-com-list hide">
            <div data-assign-to-option="region" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-assignto-options"><spring:message code="label.region.key"/></div>
            <div data-assign-to-option="company" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-assignto-options"><spring:message code="label.company.key"/></div>
        </div>
    </div>
</div>
<div id="bd-region-selector" class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.selectregion.key"/></div>
    <div class="float-left bd-frm-right pos-relative" id="region-selector">
        <input id="selected-region-txt" class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img"/>
        <input type="hidden" name="regionId" id="selected-region-id-hidden"/>
        <div id="regions-droplist" class="bd-frm-rt-dd-list dd-com-list hide">
        	<!-- regions list get populated here  -->
        </div>
    </div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.addressline1.key"/></div>
    <div class="float-left bd-frm-right">
        <input class="bd-frm-rt-txt" name="officeAddress1" id="office-address-txt">
    </div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.addressline2.key"/></div>
    <div class="float-left bd-frm-right">
        <input class="bd-frm-rt-txt" name="officeAddress2">
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
        <textarea class="bd-frm-rt-txt-area" id="selected-user-txt-area" name="selectedUserEmailArray"></textarea>
    </div>
</div>
<div id="bd-single" class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"><spring:message code="label.chooseuserforoffice.key"/></div>
    <div class="float-left bd-frm-right pos-relative">
        <input id="selected-user-txt" name="selectedUserEmail" class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img">
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
            <input type="hidden" name="isAdmin" value="true" id="is-admin-chk">
            <div class="float-left bd-check-txt"><spring:message code="label.grantadminprivileges.key"/></div>
        </div>
    </div>
</div>
<div class="bd-hr-form-item clearfix">
    <div class="float-left bd-frm-left"></div>
    <div class="float-left bd-frm-right">
        <div class="bd-btn-save cursor-pointer" id="btn-office-save"><spring:message code="label.save.key"/></div>
    </div>
</div>
</form>