<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.title.registeruser.key"/></title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>

<body>
    <div class="hdr-wrapper">
        <div class="container hdr-container clearfix">
            <div class="float-left hdr-logo"></div>
            <div class="float-right clearfix hdr-btns-wrapper">
                <div class="float-left hdr-log-btn hdr-log-reg-btn"><spring:message code="label.signin.key" /></div>
                <div class="float-left hdr-reg-btn hdr-log-reg-btn"><spring:message code="label.joinus.key" /></div>
            </div>
        </div>
    </div>
    <div class="hm-header-main-wrapper">
        <div class="container">
            <div class="hm-header-row hm-header-row-main clearfix">
                <div class="float-left hm-header-row-left text-center">View Company Hierarchy</div>
                <div class="float-right hm-header-right text-center">Build Hierarchy</div>
            </div>
        </div>
    </div>

    <div class="container v-hr-container">
        <div class="v-hr-header">Providential Utah Elite Estate</div>
        
        <div class="v-hr-tbl-wrapper">
            <table class="v-hr-tbl">
                <tr class="v-tbl-header">
                    <td class="v-tbl-line"></td>
                    <td class="v-tbl-name">Name</td>
                    <td class="v-tbl-add">Address</td>
                    <td class="v-tbl-role">Role</td>
                    <td class="v-tbl-btns"></td>
                    <td class="v-tbl-spacer"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row v-tbl-row-sel">
                    <td class="v-tbl-line">
                        <div class="v-line-cmp"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role"></td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row v-tbl-row-rgn">
                    <td class="v-tbl-line">
                        <div class="v-line-rgn"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role"></td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer v-tbl-no-bd"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row v-tbl-row-ind">
                    <td class="v-tbl-line">
                        <div class="v-line-ind"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role">Admin</td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer v-tbl-no-bd"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row v-tbl-row-ind">
                    <td class="v-tbl-line">
                        <div class="v-line-ind"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role">User</td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer v-tbl-no-bd"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row v-tbl-row-ind">
                    <td class="v-tbl-line">
                        <div class="v-line-ind"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role">Admin, User</td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer v-tbl-no-bd"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row v-tbl-row-rgn">
                    <td class="v-tbl-line">
                        <div class="v-line-rgn"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role"></td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer v-tbl-no-bd"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row">
                    <td class="v-tbl-line">
                        <div class="v-line-cmp"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role"></td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row">
                    <td class="v-tbl-line">
                        <div class="v-line-cmp"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role"></td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer"></td>
                </tr>
                <tr clicked="false" class="v-tbl-row">
                    <td class="v-tbl-line">
                        <div class="v-line-cmp"></div>
                    </td>
                    <td class="v-tbl-name">Northern Providential</td>
                    <td class="v-tbl-add">34th floor, New York, NY 10118-3299</td>
                    <td class="v-tbl-role"></td>
                    <td class="v-tbl-btns">
                        <div class="clearfix v-tbl-icn-wraper">
                            <div class="float-left v-tbl-icn">A</div>
                            <div class="float-left v-tbl-icn">B</div>
                        </div>
                    </td>
                    <td class="v-tbl-spacer"></td>
                </tr>
            </table>
        </div>
        
    </div>


<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script>
    $(document).ready(function() {
        
        $(document).on('click','.v-tbl-row',function(){
            if($(this).attr('clicked') == "false"){
                var cmpForm = $('<tr class="v-tbl-add-frm">');
                    var cmpFormTD = $('<td colspan="6">').append('test');
                cmpForm.append(cmpFormTD);
                $(this).after(cmpForm);
                $(this).next('.v-tbl-add-frm').slideDown(200);   
                $(this).attr('clicked','true').addClass('v-tbl-row-edit');
            }else{
                $(this).next('.v-tbl-add-frm').slideUp(200);   
                $(this).attr('clicked','false').removeClass('v-tbl-row-edit');
            }
        });
        
    });
</script>

</body>
</html>