<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.login.title.key" /></title>

	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/datepicker3.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/jcrop/jquery.Jcrop.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/perfect-scrollbar.min.css">
</head>
<body>
<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
<nav class="navbar navbar-default col-lg-6 col-md-6 col-sm-12 col-xs-12">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand reg-ss-logo" href="#"></a>
    </div>
    <ul id="reg-nav" class="nav navbar-nav">
      <li class="active"><a href="#">Profile</a></li>
      <li><a href="#">Company</a></li>
      <li><a href="#">Survey</a></li>
      <li><a href="#">Social</a></li>
    </ul>
  </div>
</nav>
<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12" style="background-color: #326AAD;"></div>
</div>