<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left"><spring:message code="label.buildcompanyhierarchy.key"/></div>
            <a class="float-right hm-header-row-right hm-rt-btn-lnk" href="javascript:showMainContent('./showcompanysettings.do')">
            	<spring:message code="label.editcompany.key"/>
            </a>
        </div>
    </div>
</div>

<div id="hm-main-content-wrapper" class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container">
        <div class="hm-content clearfix padding-001">
            <div class="hm-top-panel padding-001" id="company-branch">
                <div class="hm-top-panel-header">
                    <div class="hm-item-header padding-0150 clearfix">
                        <div class="hm-item-header-left float-left">Change Password</div>
                    </div>
                </div>
                <div class="create-branch-dd">
                    <form id="add-branch-form"> 
                        <div class="hm-top-panel-content clearfix">
                            <div class="clearfix">
                                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
                                    <div class="hm-item-row clearfix">
                                        <div class="hm-item-row-left text-right">Current Password</div>
                                        <div class="hm-item-row-right">
                                            <input type="password" name="" id="" class="hm-item-row-txt" placeholder=''>
                                            <div id="" class="input-error-2 error-msg"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="clearfix">
                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
                                        <div class="hm-item-row clearfix">
                                            <div class="hm-item-row-left text-right">New Password</div>
                                            <div class="hm-item-row-right">
                                                <input type="password" name="" id="" class="hm-item-row-txt" placeholder=''>
                                                <div id="" class="input-error-2 error-msg"></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
                                        <div class="hm-item-row clearfix">
                                            <div class="hm-item-row-left text-right">Confirn New Password</div>
                                            <div class="hm-item-row-right">
                                                <input type="password" name="" id="" class="hm-item-row-txt" placeholder=''>
                                                <div id="" class="input-error-2 error-msg"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="hm-btn-outer margin-bottom-25 margin-top-5 clearfix hm-item-row-right-btn-save">
                                <div class="clearfix hm-btn-wrapper hm-btn-wrapper-fix margin-0-auto">
                                    <div class=".btn-payment-sel">Save</div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>			            		            
        </div>
	</div>
</div>

<script>
	$(document).ready(function() {
		
	});
</script>