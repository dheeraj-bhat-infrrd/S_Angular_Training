<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div id="payment-section" class="payment-section">
<div class="ol-header" ><spring:message code="label.upgrade.confirmation.header" /></div>
        <div class="ol-content">
            <div class="ol-txt"><spring:message code="label.upgrade.confirmation.content" /></div>
            <div class="ol-txt"><spring:message code="label.upgrade.balance.header" /> ${balanceAmount}</div>
            <div class="clearfix">
                <div class="float-left ol-btn-wrapper">
                    <div id="ol-btn-upgrade" class="ol-btn" onclick="javascript:upgradeAccountType(${accounttype});"><spring:message code="label.upgrade.proceed.key" /></div>
                </div>
                <div class="float-left ol-btn-wrapper">
                    <div id="ol-btn-cancel" class="ol-btn" onclick="upgradePlan();"><spring:message code="label.cancel.key" /></div>
                </div>
            </div>
        </div>
</div>
 