<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home Page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>
<body>
    
    <div class="body-wrapper">
        
        <div class="hdr-wrapper">
            <div class="container hdr-container clearfix">
                <div class="float-left hdr-logo"></div>
                <div class="float-right clearfix hdr-btns-wrapper">
                    <div class="float-left hdr-log-btn hdr-log-reg-btn">sign in</div>
                    <div class="float-left hdr-reg-btn hdr-log-reg-btn">join us</div>
                </div>
            </div>
        </div>
        
        <div class="hero-wrapper">
            <div class="hero-container container">
                <div class="hr-txt">Not the one you are looking for?</div>
            </div>
        </div>
        
        <div class="fp-wrapper">
            <div class="fp-container container clearfix">
                <div class="row">
                    <div class="float-left fp-left-item">Find a professional</div>
                    <div class="float-left fp-right-item">
                        <div class="fp-wrapper clearfix">
                            <input class="fp-inp" placeholder="First Name">
                            <input class="fp-inp" placeholder="Last Name">
                            <input type="button" class="fp-inp pro-btn" value="Search">
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="ctnt-wrapper">
            <div class="ctnt-container container">
                <div class="row">
                    <div class="ctnt-left-item col-lg-9 col-md-9 col-sm-9 col-xs-12">
                        <div class="ctnt-list-header clearfix">
                            <div class="ctnt-list-header-left float-left">
                                Profiles found for <span class="srch-name">James Anderson</span>
                            </div>
                            <div class="ctnt-list-header-right float-right">
                                <span class="srch-num">180</span> Profiles found
                            </div>
                        </div>
                        <div class="ctnt-list-wrapper">
                            <div class="ctnt-list-item clearfix">
                                <div class="float-left ctnt-list-item-img"></div>
                                <div class="float-left ctnt-list-item-txt-wrap">
                                    <div class="ctnt-item-name">James Anderson</div>
                                    <div class="ctnt-item-desig">Marketting Head at Ralecon</div>
                                    <div class="ctnt-item-comment">lorem ipsum doe ir lera lorem ipsum doe ir lera lorem ipsum doe ir lera lorem ipsum doe ir lera lorem ipsum doe ir lera lorem ipsum doe ir lera lorem ipsum doe ir lera </div>
                                </div>
                                <div class="float-left ctnt-list-item-btn-wrap">
                                    <div class="ctnt-review-btn">Review</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ctnt-right-item col-lg-3 col-md-3 col-sm-3 col-xs-12"></div>
                </div>
            </div>
        </div>

    </div>
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script-1.1.js"></script>
    <script>
        $(document).ready(function(){
            
        });
    </script>
    
</body>
</html>