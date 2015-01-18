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
        <c:choose>
	     	<c:when test="${isBranchAdditionAllowed && not isRegionAdditionAllowed}">   
		        <input type="hidden" id="enable-branches-form" value="true">
		        <div class="hm-content clearfix padding-001">
		            <div class="hm-top-panel padding-001" id="company-branch">
		                <div class="hm-top-panel-header">
		                    <div class="hm-item-header padding-0150 clearfix">
		                        <div class="float-left hm-header-dd-icn dd-icn-type2 dd-icn hide"></div>
		                        <div class="hm-item-header-left float-left"><spring:message code="label.createbranch.key"/></div>
		                        <div class="hm-item-header-right icn-plus dd-icn-plus float-right hm-dd-pls-btn"></div>
								<div class="hm-item-header-right icn-remove dd-icn-minus hide float-right hm-dd-pls-btn"></div>
		                    </div>
		                </div>
		                <div class="create-branch-dd hm-dd-main-content">
		                	<form id="add-branch-form"> 
			                    <div class="hm-top-panel-content clearfix">
			                        <div class="clearfix">
			                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
			                                <div class="hm-item-row clearfix">
			                                    <div class="hm-item-row-left text-right"><spring:message code="label.branchname.key"/></div>
			                                    <div class="hm-item-row-right">
			                                        <input type="text" name="branchName" id="branch-name-txt" class="hm-item-row-txt" placeholder='<spring:message code="label.newbranch.key"/>'>
			                                        <div id="branch-name-error" class="input-error-2 error-msg"></div>
			                                    </div>
			                                </div>
			                            </div>
			                            <div class="clearfix">
			                                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
			                                    <div class="hm-item-row clearfix">
			                                        <div class="hm-item-row-left text-right"><spring:message code="label.address1.key"/></div>
			                                        <div class="hm-item-row-right">
			                                            <input type="text" name="branchAddress1" id="branch-address1-txt" class="hm-item-row-txt" placeholder='<spring:message code="label.address1.key"/>'>
			                                        	<div id="branch-address1-error" class="input-error-2 error-msg"></div>
			                                        </div>
			                                    </div>
			                                </div>
			                                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
			                                    <div class="hm-item-row clearfix">
			                                        <div class="hm-item-row-left text-right"><spring:message code="label.address2.key"/></div>
			                                        <div class="hm-item-row-right">
			                                            <input type="text" name="branchAddress2" id="branch-address2-txt" class="hm-item-row-txt" placeholder='<spring:message code="label.address2.key"/>'>
			                                        	<div id="branch-address2-error" class="input-error-2 error-msg"></div>
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
			                     <input type="hidden" name="branchId" id="branch-id-hidden"/>
			                     <input type="hidden" id="account-type" account-type="company"/>
		                    </form>
		                    <div class="hm-bottom-panel margin-bottom-25">
							    <div class="hm-bottom-header">
							        <div class="hm-sub-header clearfix">
							            <div class="float-left hm-sub-header-left"><spring:message code="label.existingbranch.key"/></div>
							            <div class="float-right mobile-search-panel">
							                <div class="clearfix hm-sub-search-wrapper">
							                    <div class="float-left">
							                        <input id = "search-company-branch-txt" class="hm-sub-search-txt" placeholder='<spring:message code="label.searchbranch.key"/>'> 
							                    </div>
							                    <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
							                </div>
							            </div>
							        </div>
							    </div>
							    <div class="hm-bottom-panel-content clearfix">
                    				<div id="existing-branches">
                    			
               						<!-- Existing branches get populated here from solr  -->
                    				</div>
                    			</div>
	                    	</div>
		                </div>
		            </div>			            		            
		        </div>
	        </c:when>
	         <c:when test="${isRegionAdditionAllowed && isBranchAdditionAllowed}">   
        		<input type="hidden" id="show-regions-flag" value="true">
		        <div class="hm-content clearfix padding-001 margin-top-2-resp" id="enterprise-branch-region">
		            <div class="float-left hm-content-left padding-right-0 col-lg-6 col-md-6 col-sm-6 col-xs-12">
		                <div class="hm-left-item">
		                    <div class="hm-item-header clearfix">
		                        <div class="float-left hm-header-dd-icn dd-icn hide"></div>
		                        <div class="hm-item-header-left float-left"><spring:message code="label.createregion.key"/></div>
		                        <div class="hm-item-header-right icn-plus dd-icn-plus float-right hm-dd-pls-btn"></div>
								<div class="hm-item-header-right icn-remove dd-icn-minus hide float-right hm-dd-pls-btn"></div>
		                    </div>
		                    <div class="create-branch-dd hm-dd-main-content">
		                    	<form id="add-region-form">
			                        <div class="hm-item-content">
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.newregion.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" name="regionName" id="region-name-txt" placeholder='<spring:message code="label.newregion.key"/>'>
				                                <div id="region-name-error" class="input-error-2 error-msg"></div>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.address1.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" id="region-address1-txt" name="regionAddress1" placeholder='<spring:message code="label.address1.key"/>'>
				                                <div id="region-address1-error" class="input-error-2 error-msg"></div>
			                                </div>
			                            </div>
			                            <div class="hm-item-row clearfix">
			                                <div class="hm-item-row-left text-right"><spring:message code="label.address2.key"/></div>
			                                <div class="hm-item-row-right">
			                                    <input type="text" class="hm-item-row-txt" id="region-address2-txt" name="regionAddress2" placeholder='<spring:message code="label.address2.key"/>'>
				                                <div id="region-address2-error" class="input-error-2 error-msg"></div>
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
			                  		<input type="hidden" name="regionId" id="region-id-hidden">
			                  	</form>
				                    <div class="hm-sub-header clearfix">
	    								<div class="float-left hm-sub-header-left">
	    									<spring:message code="label.existingregion.key"/>
	    								</div>
									    <div class="float-right">
									        <div class="clearfix hm-sub-search-wrapper">
									            <div class="float-left">
									                <input id = "search-region-txt" class="hm-sub-search-txt" placeholder='<spring:message code="label.searchregion.key"/>'> 
									            </div>
									            <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
									        </div>
									    </div>
									</div>
									<div id="existing-regions">
										<!--existing regions are populated here through ajax after fetching from solr  -->
									</div>
		                    	</div>
	                    	</div>
	                	</div>
	                	<div class="float-left hm-content-right col-lg-6 col-md-6 col-sm-6 col-xs-12">
	               		<div class="hm-right-item">
		                    <div class="hm-item-header clearfix">
		                        <div class="float-left hm-header-dd-icn dd-icn hide"></div>
		                        <div class="hm-item-header-left float-left"><spring:message code="label.createbranch.key"/></div>
		                        <div class="hm-item-header-right icn-plus dd-icn-plus float-right hm-dd-pls-btn"></div>
								<div class="hm-item-header-right icn-remove dd-icn-minus hide float-right hm-dd-pls-btn"></div>
		                    </div>
	                    <div class="create-branch-dd hm-dd-main-content">
	                    	<form id="add-branch-form">
		                        <div class="hm-item-content">
		                            <div class="hm-item-row clearfix">
		                                <div class="hm-item-row-left text-right"><spring:message code="label.branchname.key"/></div>
		                                <div class="hm-item-row-right">
		                                    <input type="text" id="branch-name-txt" class="hm-item-row-txt" name="branchName" placeholder='<spring:message code="label.newbranch.key"/>'>
			                                <div id="branch-name-error" class="input-error-2 error-msg"></div>
		                                </div>
		                            </div>
		                            <div class="hm-item-row clearfix">
		                                <div class="hm-item-row-left text-right"><spring:message code="label.selectregion.key"/></div>
		                                <div class="hm-item-row-right pos-relative">
		                                    <input class="hm-item-row-txt cursor-pointer" type="text" id="selected-region-txt" autocomplete="off">
		                                    <input type="hidden" name="regionId" id="selected-region-id-hidden"/>
		                                    <input type="hidden" name="branchId" id="branch-id-hidden"/>
		                                    <div class="hm-dd-wrapper hide" id="hm-dd-wrapper-bottom">
		                                        <!-- Regions are populated here through ajax from regionsautocomplete.jsp -->
		                                    </div>
		                                </div>
		                            </div>
		                            <div class="hm-item-row clearfix">
		                                <div class="hm-item-row-left text-right"><spring:message code="label.address1.key"/></div>
		                                <div class="hm-item-row-right">
		                                    <input type="text" class="hm-item-row-txt" id="branch-address1-txt" name="branchAddress1" placeholder='<spring:message code="label.address1.key"/>'>
			                                <div id="branch-address1-error" class="input-error-2 error-msg"></div>
		                                </div>
		                            </div>
		                            <div class="hm-item-row clearfix">
		                                <div class="hm-item-row-left text-right"><spring:message code="label.address2.key"/></div>
		                                <div class="hm-item-row-right">
		                                    <input type="text" class="hm-item-row-txt" id="branch-address2-txt" name="branchAddress2" placeholder='<spring:message code="label.address2.key"/>'>
			                                <div id="branch-address2-error" class="input-error-2 error-msg"></div>
		                                </div>
		                            </div>
		                            <div class="hm-item-row clearfix">
		                                <div class="hm-item-row-right hm-item-row-right-btn-save">
		                                    <div class="clearfix hm-btn-wrapper" id="branch-actions">
		                                        <div class="float-left add-curve-btn cursor-pointer"><spring:message code="label.addbranchadmin.key"/></div>
		                                        <div class="float-left icn-save cursor-pointer" id="branch-save-icon"></div>
		                                    </div>
		                                </div>
		                            </div>
		                        </div>
		                    </form>
		                    <div class="hm-sub-header clearfix">
     							<div class="float-left hm-sub-header-left">
     								<spring:message code="label.existingbranch.key"/>
     							</div>
							     <div class="float-right">
							         <div class="clearfix hm-sub-search-wrapper">
							             <div class="float-left">
							                 <input id="search-branch-txt" class="hm-sub-search-txt" placeholder='<spring:message code="label.searchbranch.key"/>'> 
							             </div>
							             <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
							         </div>
							     </div>
 							</div>
	                        <div id="existing-branches">
	                       		<!-- Branch list is populated here after fetching from solr -->
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
		$(document).attr("title", "Build Hierarchy");
		
		//for enterprise account type
		if($("#show-regions-flag").length > 0) {
			showRegions();
			showBranches();
		}
		//company account type
		else {
			searchBranchesForCompany("");
		}
	});
</script>