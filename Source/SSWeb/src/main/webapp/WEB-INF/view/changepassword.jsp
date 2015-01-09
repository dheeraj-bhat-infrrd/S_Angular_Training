<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left"><spring:message code="label.changepassword.key"/></div>
        </div>
    </div>
</div>

<div id="hm-main-content-wrapper" class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container">
        <div class="hm-content clearfix padding-001">
            <div class="hm-top-panel padding-001" id="company-branch">
                <div class="hm-top-panel-header">
	                <!--Use this container to input all the messages from server-->
	                <jsp:include page="messageheader.jsp"/>
                </div>
                <div class="create-branch-dd">
                    <form id="change-password-form" method="POST" action="./changepassword.do">
                        <div class="hm-top-panel-content clearfix">
                            <div class="clearfix">
                                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
                                    <div class="hm-item-row clearfix">
                                        <div class="hm-item-row-left text-right"><spring:message code="label.currentpassword.key"/></div>
                                        <div class="hm-item-row-right">
                                            <input type="password" name="oldpassword" id="" class="hm-item-row-txt" placeholder=''>
                                            <div id="" class="input-error-2 error-msg"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="clearfix">
                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
                                        <div class="hm-item-row clearfix">
                                            <div class="hm-item-row-left text-right"><spring:message code="label.newpassword.key"/></div>
                                            <div class="hm-item-row-right">
                                                <input type="password" name="newpassword" id="" class="hm-item-row-txt" placeholder=''>
                                                <div id="" class="input-error-2 error-msg"></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
                                        <div class="hm-item-row clearfix">
                                            <div class="hm-item-row-left text-right"><spring:message code="label.confirmnewpassword.key"/></div>
                                            <div class="hm-item-row-right">
                                                <input type="password" name="confirmnewpassword" id="" class="hm-item-row-txt" placeholder=''>
                                                <div id="" class="input-error-2 error-msg"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="hm-btn-outer margin-bottom-25 margin-top-5 clearfix hm-item-row-right-btn-save">
                                <div class="clearfix hm-btn-wrapper hm-btn-wrapper-fix margin-0-auto">
                                    <div class="btn-payment-sel" id="save-pwd">
                                    <spring:message code="label.save.key"/></div>
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
		
	function submitChangepassword() {
		console.log("submitting change password");
			$('#change-password-form').submit();
			
		}
	function updatechangepassword(formid) {
		var url = "./changepassword.do";
		callAjaxFormSubmit(url, updatechangepasswordCallBack, formid);
	}
	function updatechangepasswordCallBack(response) {
		$("#main-content").html(response);
	}
	$('#save-pwd').click(function(e) {
		updatechangepassword('change-password-form');
	});
	
});
</script>