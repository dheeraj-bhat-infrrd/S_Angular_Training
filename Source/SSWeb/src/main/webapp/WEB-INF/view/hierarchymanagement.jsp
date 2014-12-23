<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left"><spring:message code="label.buildcompanyhierarchy.key"/></div>
            <div class="float-right hm-header-row-right"><spring:message code="label.editcompany.key"/></div>
        </div>
    </div>
</div>

<div id="hm-main-content-wrapper" class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container">
        <c:choose>
	     	<c:when test="${isBranchAdditionAllowed && not isRegionAdditionAllowed}">   
		        <div class="hm-content clearfix padding-001">
		            <div class="hm-top-panel padding-001">
		                <div class="hm-top-panel-header">
		                    <div class="hm-item-header padding-0150 clearfix">
		                        <div class="float-left hm-header-dd-icn dd-icn-type2 dd-icn hide"></div>
		                        <div class="hm-item-header-left float-left"><spring:message code="label.createbranch.key"/></div>
		                        <div class="hm-item-header-right icn-plus float-right"></div>
		                    </div>
		                </div>
		                <div class="create-branch-dd">
		                	<form id="add-branch-form"> 
			                    <div class="hm-top-panel-content clearfix">
			                        <div class="clearfix">
			                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
			                                <div class="hm-item-row clearfix">
			                                    <div class="hm-item-row-left text-right"><spring:message code="label.branchname.key"/></div>
			                                    <div class="hm-item-row-right">
			                                        <input type="text" name="branchName" class="hm-item-row-txt" placeholder='<spring:message code="label.newbranch.key"/>'>
			                                    </div>
			                                </div>
			                            </div>
			                            <div class="clearfix">
			                                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
			                                    <div class="hm-item-row clearfix">
			                                        <div class="hm-item-row-left text-right"><spring:message code="label.address1.key"/></div>
			                                        <div class="hm-item-row-right">
			                                            <input type="text" name= "branchAddress1" class="hm-item-row-txt" placeholder='<spring:message code="label.address1.key"/>'>
			                                        </div>
			                                    </div>
			                                </div>
			                                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
			                                    <div class="hm-item-row clearfix">
			                                        <div class="hm-item-row-left text-right"><spring:message code="label.address2.key"/></div>
			                                        <div class="hm-item-row-right">
			                                            <input type="text" name= "branchAddress2" class="hm-item-row-txt" placeholder='<spring:message code="label.address2.key"/>'>
			                                        </div>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                        <div class="hm-btn-outer margin-bottom-25 margin-top-5 clearfix hm-item-row-right-btn-save">
			                            <div class="clearfix hm-btn-wrapper hm-btn-wrapper-fix margin-0-auto">
			                                <div class="float-left add-curve-btn cursor-pointer"><spring:message code="label.addbranchadmin.key"/></div>
			                                <div id="branch-save-icon" class="float-left icn-save cursor-pointer margin-right-0"></div>
			                            </div>
			                        </div>
			                    </div>
		                    </form>
	                    	<div id="existing-branches">
	               				<!-- Existing branches get populated here through ajax from existingbranches.jsp  -->
	                    	</div>
		                </div>
		            </div>			            		            
		        </div>
	        </c:when>
	         <c:when test="${isRegionAdditionAllowed && isBranchAdditionAllowed}">   
        		<input type="hidden" id = "show-regions-flag" value="true">
		        <div class="hm-content clearfix padding-001 margin-top-2-resp">
		            <div class="float-left hm-content-left padding-right-0 col-lg-6 col-md-6 col-sm-6 col-xs-12">
		                <div class="hm-left-item">
		                    <div class="hm-item-header clearfix">
		                        <div class="float-left hm-header-dd-icn dd-icn hide"></div>
		                        <div class="hm-item-header-left float-left"><spring:message code="label.createregion.key"/></div>
		                        <div class="hm-item-header-right icn-plus float-right"></div>
		                    </div>
		                    <div class="create-branch-dd">
		                    	<form id="add-region-form">
			                        <div class="hm-item-content">
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.newregion.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" name="regionName" placeholder='<spring:message code="label.newregion.key"/>'>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.address1.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" name="regionAddress1" placeholder='<spring:message code="label.address1.key"/>'>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.address2.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" name="regionAddress2" placeholder='<spring:message code="label.address2.key"/>'>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-right hm-item-row-right-btn-save">
			                                    <div class="clearfix hm-btn-wrapper">
			                                        <div class="float-left add-curve-btn cursor-pointer"><spring:message code="label.addregionadmin.key"/></div>
			                                        <div class="float-left icn-save cursor-pointer" id="region-save-icon"></div>
			                                    </div>
			                                </div>
			                            </div>
			                  		</div> 
			                  	</form>
			                        <div id="existing-regions">                   
				                        <!-- Regions get populated here through ajax from existingenterpriseregions.jsp  -->
			                        </div>
		                        </div>
		                    </div>
		                </div>
		                <div class="float-left hm-content-right col-lg-6 col-md-6 col-sm-6 col-xs-12">
		                <div class="hm-right-item">
		                    <div class="hm-item-header clearfix">
		                        <div class="float-left hm-header-dd-icn dd-icn hide"></div>
		                        <div class="hm-item-header-left float-left"><spring:message code="label.createbranch.key"/></div>
		                        <div class="hm-item-header-right icn-plus float-right"></div>
		                    </div>
		                    <div class="create-branch-dd">
		                    	<form id="add-branch-form">
			                        <div class="hm-item-content">
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.branchname.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" name="branchName" placeholder='<spring:message code="label.newbranch.key"/>'>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.selectregion.key"/></div>
			                                <div class="hm-item-row-right pos-relative">
			                                    <div class="hm-item-row-txt cursor-pointer" id="hm-item-dd-bottom"></div>
			                                    <div class="hm-dd-wrapper hide" id="hm-dd-wrapper-bottom">
			                                        <div class="hm-dd-item">Region 1</div>
			                                        <div class="hm-dd-item">Region 2</div>
			                                        <div class="hm-dd-item">Region 3</div>
			                                        <div class="hm-dd-item">Region 4</div>
			                                    </div>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.address1.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" name="branchAddress1" placeholder='<spring:message code="label.address1.key"/>'>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.address2.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" name="branchAddress2" placeholder='<spring:message code="label.address2.key"/>'>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-right hm-item-row-right-btn-save">
			                                    <div class="clearfix hm-btn-wrapper">
			                                        <div class="float-left add-curve-btn cursor-pointer"><spring:message code="label.addbranchadmin.key"/></div>
			                                        <div class="float-left icn-save cursor-pointer" id="branch-save-icon"></div>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                    </form>
		                        <div id="existing-branches">
		                       		<!-- Branch list is populated here through ajax from existingenterprisebranches.jsp -->
		                    	</div>
		                </div>
		                <div class="hm-right-item hm-left-item-bottom no-border padding-left-25">
		                    
		                </div>
		            </div>
		        </div>
		                <div class="hm-left-item hm-left-item-bottom no-border">
		                    
		                </div>
		            </div>
		            
		            
       		</c:when>
        </c:choose>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/hierarchymanagement.js"></script>
<script>
	$(document).ready(function() {
		showBranches();
		if($("#show-regions-flag").length > 0) {
			showRegions();
		}
		
		$('#hm-dd-item').click(function(){
			   $('#hm-dd-wrapper-bottom').slideToggle(200);
		});

		$('#hm-item-dd-bottom').click(function(){
		   $('#hm-dd-wrapper-bottom').slideToggle(200);
		});
	});
	$("#branch-save-icon").click(function(e){
		addBranch("add-branch-form");
	});
	$("#region-save-icon").click(function(e) {
		addRegion("add-region-form");
	});
</script>