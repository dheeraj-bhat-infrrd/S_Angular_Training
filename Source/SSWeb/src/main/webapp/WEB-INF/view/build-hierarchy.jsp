<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row hm-header-row-main clearfix">
            <div class="float-left hm-header-row-left text-center"><spring:message code="label.buildcompanyhierarchy.key"/></div>
        </div>
    </div>
</div>
<div class="container bd-hr-container">
        <div class="bd-hr-left-panel col-lg-3 col-md-3 col-sm-3">
            <div class="bd-hr-lp-header"><spring:message code="label.ourcompany.key"/></div>
            <div class="bd-hr-lp-content-wrapper">
                <div class="bd-hr-item-l1">
                    <div class="bd-hr-item bd-lt-l1 clearfix">
                        <div class="bd-hr-txt">Sample test</div>
                    </div>
                    <div class="bd-hr-item-l2">
                        <div class="bd-hr-item bd-lt-l2 clearfix">
                            <div class="bd-hr-txt">Sample test</div>
                        </div>
                    </div>
                    <div class="bd-hr-item-l2">
                        <div class="bd-hr-item bd-lt-l2 clearfix">
                            <div class="bd-hr-txt">Sample test</div>
                        </div>
                    </div>
                    <div class="bd-hr-item-l2">
                        <div class="bd-hr-item bd-lt-l2 clearfix">
                            <div class="bd-hr-txt">Sample test</div>
                        </div>
                        <div class="bd-hr-item-l3">
                            <div class="bd-hr-item bd-lt-l3 clearfix">
                                <div class="float-left bd-hr-img"></div>
                                <div class="bd-hr-txt">Sample test</div>
                            </div>
                        </div>
                        <div class="bd-hr-item-l3">
                            <div class="bd-hr-item bd-lt-l3 clearfix">
                                <div class="float-left bd-hr-img"></div>
                                <div class="bd-hr-txt">Sample test</div>
                            </div>
                        </div>
                        <div class="bd-hr-item-l3">
                            <div class="bd-hr-item bd-lt-l3 clearfix">
                                <div class="float-left bd-hr-img"></div>
                                <div class="bd-hr-txt">Sample test</div>
                            </div>
                        </div>
                    </div>
                    <div class="bd-hr-item-l2">
                        <div class="bd-hr-item bd-lt-l2 clearfix">
                            <div class="bd-hr-txt">Sample test</div>
                        </div>
                    </div>
                </div>
                <div class="bd-hr-item-l1">
                    <div class="bd-hr-item bd-lt-l1 clearfix">
                        <div class="bd-hr-txt">Sample test</div>
                    </div>
                </div>
                <div class="bd-hr-item-l1">
                    <div class="bd-hr-item bd-lt-l1 clearfix">
                        <div class="bd-hr-txt">Sample test</div>
                    </div>
                </div>
                <div class="bd-hr-item-l1">
                    <div class="bd-hr-item bd-lt-l1 clearfix">
                        <div class="bd-hr-txt">Sample test</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="bd-hr-rt-panel col-lg-9 col-md-9 col-sm-9 col-xs-12">
            <div class="bd-hr-rt-header"><i><spring:message code="label.woulduliketoadd.key"/> <strong><spring:message code="label.new.key"/></strong></i></div>
            <div class="bd-hr-tabs-wrapper">
                <div class="bd-hr-tabs-header clearfix">
                    <div class="bd-hr-tabs-header-item float-left">
                        <span data-tab="region" class="bd-hdr-span bd-hdr-active-arr bd-hdr-active"><spring:message code="label.region.key"/></span>
                    </div>
                    <div class="bd-hr-tabs-header-item float-left">
                        <span data-tab="office" class="bd-hdr-span"><spring:message code="label.office.key"/></span>
                    </div>
                    <div class="bd-hr-tabs-header-item float-left">
                        <span data-tab="individual" class="bd-hdr-span"><spring:message code="label.individual.key"/></span>
                    </div>
                    <div class="bd-hr-tabs-header-item hdr-txt-rt-adj float-left">
                        <span data-tab="csv" class="bd-hdr-span"><spring:message code="label.uploadcsv.key"/></span>
                    </div>
                </div>
            </div>
            <c:if test="${isRegionAdditionAllowed}">
	            <div id="bd-form-region" class="bd-hr-form-wrapper">
	                <jsp:include page="hierarchy-region-edit.jsp"/>
	            </div>
            </c:if>
            <c:if test="${isBranchAdditionAllowed}">
	            <div id="bd-form-office" class="bd-hr-form-wrapper hide">
	            	<jsp:include page="hierarchy-office-edit.jsp"/>
	            </div>
            </c:if>
            <div id="bd-form-individual" class="bd-hr-form-wrapper hide">
            	<jsp:include page="hierarchy-individual-edit.jsp"/>
            </div>
            <div id="bd-form-csv" class="bd-hr-form-wrapper hide">csv</div>
        </div>
    </div>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/hierarchy-management.js"></script>
<script>
$(document).ready(function() {
	$(document).attr("title", "Build Hierarchy");
    $(document).on('click', '.dd-com-main', function(e) {
        e.stopPropagation();
        $(this).next('.dd-com-list').slideToggle(200);
    });
    
    $(document).on('click', 'body', function() {
        $('.dd-com-list').slideUp(200);
    });
    
    $(document).on('click', '.dd-com-item', function(e) {
        $(this).parent().prev('.dd-com-main').html($(this).data('option'));
    });
    
    $(document).on('click', '.bd-check-img', function(e) {
        $(this).toggleClass('bd-check-img-checked');
    });
    
    $(document).on('click', '.bd-cust-rad-img', function(e) {
        $('.bd-cust-rad-img').removeClass('bd-cust-rad-img-checked');
        $(this).toggleClass('bd-cust-rad-img-checked');
        if($(this).data('type') == "single"){
            $('#bd-single').show();
            $('#bd-multiple').hide();
        }else if($(this).data('type') == "multiple"){
            $('#bd-single').hide();
            $('#bd-multiple').show();
        }
    });
    
    $(document).on('click','.bd-hdr-span',function(){
        $('.bd-hdr-span').removeClass('bd-hdr-active');
        $('.bd-hdr-span').removeClass('bd-hdr-active-arr');
        $(this).addClass('bd-hdr-active');
        $(this).addClass('bd-hdr-active-arr');
        $('.bd-hr-form-wrapper').hide();
        switch($(this).data('tab')){
            case 'region': 
                $('#bd-form-region').show();
                break;
            case 'office': 
                $('#bd-form-office').show();
                break;
            case 'individual': 
                $('#bd-form-individual').show();
                break;
            case 'csv': 
                $('#bd-form-csv').show();
                break;
            default: 
                $('#bd-form-region').show();
                break;
        }
    });

});
</script>