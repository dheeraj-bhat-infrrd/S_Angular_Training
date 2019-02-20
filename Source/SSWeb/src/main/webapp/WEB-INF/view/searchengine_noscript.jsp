<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>${title}</title>
	<meta name="keywords" content="agents, top, best, ${vertical}, ${entity}, near, ${location} ${stateCode} , professional, online, reputation, social, survey, reviews, rating, SocialSurvey">
	<meta name="description" content="top agents ${vertical} ${entity} near ${location} ${stateCode} , Best agents ${vertical} ${entity} in ${location} ${stateCode}, Find professional reviews, ratings, reputation, and contact information on SocialSurvey">
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/search-engine.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<meta property="og:description" content="Find the top ${vertical} ${entity} near ${location}" />
	<meta property="og:profileLevel" content="${entity}" />
	<meta property="og:title" content="${title}" />
</head>
<body>
	<div class="overlay-loader hide"></div>
	<div class="body-wrapper">
		<div class="hdr-wrapper search-eng-hdr">
			<div class="container hdr-container clearfix">
				<div class="float-left hdr-logo"></div>
				<div class="float-right clearfix hdr-btns-wrapper">
					<div class="float-left hdr-log-btn hdr-log-reg-btn">
						<spring:message code="label.signin.key" />
					</div>
				</div>
			</div>
		</div>
		<div id="err-nw-wrapper" class="err-nw-wrapper">
			<span class="err-new-close"></span>
			<span id="err-nw-txt"></span>
		</div>
		<div id="srch-eng-filter-container" class="srch-eng-filter-container">
			<div class="container srch-eng-filter-container-div">
				<div class="srch-eng-filter-txt-container">
					<div class="srch-eng-find-cont col-lg-6 col-md-6 col-sm-6 col-xs-12">
						<div class="srch-eng-txt-lbl srch-eng-inp-container srch-eng-txt-lbl-bord">Find</div>
						<input id="srch-eng-cat-inp" placeholder="mortgage brokers, real estate agents,car rentals" class="srch-eng-cat-inp srch-eng-inp-container">
					</div>
					<div class="srch-eng-loc-cont  col-lg-6 col-md-6 col-sm-6 col-xs-12">
						<div class="srch-eng-txt-lbl srch-eng-inp-container">Near</div>
						<div id="srch-eng-loc-inp-cont" class="srch-eng-loc-inp-cont">
							<input id="srch-eng-loc-inp" placeholder="" class="srch-eng-cat-inp srch-eng-inp-container" data-lat=0 data-lng=0>
							<div id="srch-eng-loc-dropdown" class="srch-eng-loc-dropdown">
								<div class="srch-eng-loc-dropdown-item cursor-pointer" data-lat=0 data-lng=0>Addison, Abcde</div>
								<div class="srch-eng-loc-dropdown-item cursor-pointer" data-lat=0 data-lng=0>Addison, Abcde</div>
								<div class="srch-eng-loc-dropdown-item cursor-pointer" data-lat=0 data-lng=0>Addison, Abcde</div>
							</div>
						</div>
					</div>
					<div id="srch-eng-srch-btn" class="srch-eng-srch-img-cont cursor-pointer">
	    				<img src="/resources/images/search_white.png" alt="search" class="srch-eng-srch-img">
	    			</div>
				</div>
				<div class="srch-eng-filter-adv-btn-container">
					<div id="srch-eng-adv-srch-btn" class="srch-eng-adv-srch-btn cursor-pointer">
						<img id="srch-eng-adv-srch-up" class="srch-eng-adv-srch-icon" alt="^" src="${initParam.resourcesPath}/resources/images/chevron-up-srch.png" >
						<img id="srch-eng-adv-srch-down" class="srch-eng-adv-srch-icon" alt="^" src="${initParam.resourcesPath}/resources/images/chevron-down-srch.png" style="display:none">
						<div class="srch-eng-adv-srch-txt">Advanced Search</div>
					</div>
				</div>
				<div id="srch-eng-filter-adv-container" class="srch-eng-filter-adv-container">
					<div id="srch-eng-sort-by-cont" class="srch-eng-filter-col col-lg-2 col-md-2 col-sm-4 col-xs-6">
						<div class="srch-eng-filter-hdr">Sort By</div>
						<div data-order=1 data-filter="Best Match" data-sel="true" class="srch-eng-filter-txt">Best Match</div>
						<div data-order=2 data-filter="Highest Rated" data-sel="false" class="srch-eng-filter-txt">Highest Rated</div>
						<div data-order=3 data-filter="Most Reviewed" data-sel="false" class="srch-eng-filter-txt">Most Reviewed</div>
						<div data-order=4 data-filter="Distance" data-sel="false" class="srch-eng-filter-txt">Distance</div>
					</div>
					<div id="srch-eng-dist-cont" class="srch-eng-filter-col col-lg-2 col-md-2 col-sm-4 col-xs-6">
						<div class="srch-eng-filter-hdr">Distance</div>
						<div data-order=1 data-filter="" data-sel="false" class="srch-eng-filter-txt">75 miles</div>
						<div data-order=2 data-filter="" data-sel="false" class="srch-eng-filter-txt">50 miles</div>
						<div data-order=3 data-filter="" data-sel="false" class="srch-eng-filter-txt">25 miles</div>
						<div data-order=4 data-filter="" data-sel="false" class="srch-eng-filter-txt">10 miles</div>
					</div>
					<div id="srch-eng-rat-cont" class="srch-eng-filter-col col-lg-2 col-md-2 col-sm-4 col-xs-6">
						<div class="srch-eng-filter-hdr">Rating</div>
						<div data-order=1 data-filter="" data-sel="false" class="srch-eng-filter-txt">>100</div>
						<div data-order=2 data-filter="" data-sel="false" class="srch-eng-filter-txt">>50</div>
						<div data-order=3 data-filter="" data-sel="false" class="srch-eng-filter-txt">>5</div>
						<div data-order=4 data-filter="" data-sel="false" class="srch-eng-filter-txt">1 or more</div>
					</div>
					<div id="srch-eng-rev-cont" class="srch-eng-filter-col col-lg-2 col-md-2 col-sm-4 col-xs-6">
						<div class="srch-eng-filter-hdr">Reviews</div>
						<div data-order=1 data-filter="" data-sel="false" class="srch-eng-filter-txt">Best Match</div>
						<div data-order=2 data-filter="" data-sel="false" class="srch-eng-filter-txt">Highest Rated</div>
						<div data-order=3 data-filter="" data-sel="false" class="srch-eng-filter-txt">Most Reviewed</div>
						<div data-order=4 data-filter="" data-sel="false" class="srch-eng-filter-txt">Distance</div>
					</div>
					<div id="srch-eng-cat-cont" class="srch-eng-filter-col col-lg-2 col-md-2 col-sm-4 col-xs-6" data-filters=[]>
						<input id="srch-eng-cat-list" type="hidden" val=>
						<div class="srch-eng-filter-hdr">Category</div>
						<div id="srch-eng-cat-list-cont" class="srch-eng-cat-list-cont"></div>
						<div class="srch-eng-filter-txt srch-eng-more-cat" style="display:none">More Categories</div>
						<div class="srch-eng-filter-txt srch-eng-less-cat" style="display:none">Less Categories</div>
					</div>
					<div id="srch-eng-prof-cont" class="srch-eng-filter-col col-lg-2 col-md-2 col-sm-4 col-xs-6">
						<div class="srch-eng-filter-hdr">Profiles</div>
						<div data-order=1 data-filter="" data-sel="false" class="srch-eng-filter-txt">Professionals</div>
						<div data-order=2 data-filter="" data-sel="false" class="srch-eng-filter-txt">Loan Offices</div>
						<div data-order=3 data-filter="" data-sel="false" class="srch-eng-filter-txt">Companies</div>
					</div>
				</div>
			</div>
		</div>
		<div class="srch-eng-result-hdr">
			<div class="container srch-eng-result-hdr-txt">
				<input id="srch-eng-pag-data" type="hidden" data-startIndex=0 data-batchSize=10 data-pageNo=1 data-count=0>
				<h4><span class="srch-eng-hdr-txt"><span class="srch-eng-result-hdr-vert">Top </span><span class="srch-eng-result-hdr-prof">Professionals </span><span class="srch-eng-result-hdr-loc"></span></span></h4>
				<div class="srch-eng-hdr-pagination">
					  <div class="srch-eng-pag-icon srch-eng-pag-prev cursor-pointer" style="display:none">❮</div>
					  <div class="srch-eng-page-no">1</div>
					  <div class="srch-eng-pag-icon srch-eng-pag-next cursor-pointer" style="display:none">❯</div>
				</div>
			</div>
		</div>
		<div id="srch-eng-loader" style="display:none"></div>
		<div class="srch-eng-result-cont">
			<!-- Search results -->
			<div id="srch-eng-result-item" class="container srch-eng-result-item">
				<input type="hidden" val="" class="srch-eng-pub-page-link-inp"> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-details"> 
					<div class="srch-eng-result-img-cont srch-eng-pub-page-link"> 
						<div class="srch-eng-result-rank-cont"></div>
						<div class="srch-eng-result-rank">1</div> 
						<div class="srch-eng-result-img">
							<img alt="SocialSurvey" src="" class="srch-eng-default-prof-img">
						</div> 
					</div> 
					<div class="srch-eng-result-prof-details"> 
						<div class="srch-eng-prof-details-cont col-lg-9 col-md-9 col-sm-7 col-xs-12"> 
							<div class="srch-eng-result-name srch-eng-pub-page-link">Name1</div> 
							<div class="srch-eng-result-title-nmls">Title-nmls1</div> 
							<div class="srch-eng-result-company">company1</div> 
							<div class="srch-eng-result-rev-details"> 
								<div class="srch-eng-result-rev-stars srch-eng-pub-page-link"> 
									<div class="srch-eng-result-star srch-eng-star-1 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-2 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-3 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-4 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-5 srch-eng-3quatstar"></div> 
								</div> 
								<div class="srch-eng-result-rev-count-cont srch-eng-result-rat-txt srch-eng-pub-page-link"> 
									<div class="srch-eng-result-rat srch-eng-result-rat-txt">4.55</div> -  
									<div class="srch-eng-result-rev-count srch-eng-result-rat-txt">234 Reviews</div> 
									<div class="srch-eng-result-rec-rev srch-eng-result-rat-txt">Recent review</div> 
								</div> 
							</div> 
						</div> 
						<div class="srch-eng-result-address col-lg-3 col-md-3 col-sm-5 col-xs-12"> 
							<div class="srch-eng-result-distance">0.27 miles</div> 
							<div class="srch-eng-result-address1 srch-eng-addr-txt">Address text1-1</div> 
							<div class="srch-eng-result-address2 srch-eng-addr-txt">Address text1-2</div> 
							<div class="srch-eng-result-place srch-eng-addr-txt">U.S.A</div> 
							<div class="srch-eng-result-contact srch-eng-addr-txt">Contact1</div> 
						</div> 
					</div> 
				</div> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-review"> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-rev-text-cont"> 
						<p class="srch-eng-rev-text" data-review="">Review by customer xyz</p> 
						<div class="srch-eng-read-more cursor-pointer">... read more</div> 
					</div> 
				</div> 
			</div>
			
			<div id="srch-eng-result-item" class="container srch-eng-result-item">
				<input type="hidden" val="" class="srch-eng-pub-page-link-inp"> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-details"> 
					<div class="srch-eng-result-img-cont srch-eng-pub-page-link"> 
						<div class="srch-eng-result-rank-cont"></div>
						<div class="srch-eng-result-rank">2</div> 
						<div class="srch-eng-result-img">
							<img alt="SocialSurvey" src="" class="srch-eng-default-prof-img">
						</div> 
					</div> 
					<div class="srch-eng-result-prof-details"> 
						<div class="srch-eng-prof-details-cont col-lg-9 col-md-9 col-sm-7 col-xs-12"> 
							<div class="srch-eng-result-name srch-eng-pub-page-link">Name2</div> 
							<div class="srch-eng-result-title-nmls">Title-nmls2</div> 
							<div class="srch-eng-result-company">company2</div> 
							<div class="srch-eng-result-rev-details"> 
								<div class="srch-eng-result-rev-stars srch-eng-pub-page-link"> 
									<div class="srch-eng-result-star srch-eng-star-1 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-2 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-3 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-4 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-5 srch-eng-nostar"></div> 
								</div> 
								<div class="srch-eng-result-rev-count-cont srch-eng-result-rat-txt srch-eng-pub-page-link"> 
									<div class="srch-eng-result-rat srch-eng-result-rat-txt">4.0</div> -  
									<div class="srch-eng-result-rev-count srch-eng-result-rat-txt">196 Reviews</div> 
									<div class="srch-eng-result-rec-rev srch-eng-result-rat-txt">Recent review</div> 
								</div> 
							</div> 
						</div> 
						<div class="srch-eng-result-address col-lg-3 col-md-3 col-sm-5 col-xs-12"> 
							<div class="srch-eng-result-distance">0.79 miles</div> 
							<div class="srch-eng-result-address1 srch-eng-addr-txt">Address text1-1</div> 
							<div class="srch-eng-result-address2 srch-eng-addr-txt">Address text1-2</div> 
							<div class="srch-eng-result-place srch-eng-addr-txt">U.S.A</div> 
							<div class="srch-eng-result-contact srch-eng-addr-txt">Contact2</div> 
						</div> 
					</div> 
				</div> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-review"> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-rev-text-cont"> 
						<p class="srch-eng-rev-text" data-review="">Review by customer XYZ-2</p> 
						<div class="srch-eng-read-more cursor-pointer">... read more</div> 
					</div> 
				</div> 
			</div>
			
			<div id="srch-eng-result-item" class="container srch-eng-result-item">
				<input type="hidden" val="" class="srch-eng-pub-page-link-inp"> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-details"> 
					<div class="srch-eng-result-img-cont srch-eng-pub-page-link"> 
						<div class="srch-eng-result-rank-cont"></div>
						<div class="srch-eng-result-rank">3</div> 
						<div class="srch-eng-result-img">
							<img alt="SocialSurvey" src="" class="srch-eng-default-prof-img">
						</div> 
					</div> 
					<div class="srch-eng-result-prof-details"> 
						<div class="srch-eng-prof-details-cont col-lg-9 col-md-9 col-sm-7 col-xs-12"> 
							<div class="srch-eng-result-name srch-eng-pub-page-link">Name1</div> 
							<div class="srch-eng-result-title-nmls">Title-nmls1</div> 
							<div class="srch-eng-result-company">company1</div> 
							<div class="srch-eng-result-rev-details"> 
								<div class="srch-eng-result-rev-stars srch-eng-pub-page-link"> 
									<div class="srch-eng-result-star srch-eng-star-1 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-2 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-3 srch-eng-3quatstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-4 srch-eng-nostar"></div> 
									<div class="srch-eng-result-star srch-eng-star-5 srch-eng-nostar"></div> 
								</div> 
								<div class="srch-eng-result-rev-count-cont srch-eng-result-rat-txt srch-eng-pub-page-link"> 
									<div class="srch-eng-result-rat srch-eng-result-rat-txt">2.55</div> -  
									<div class="srch-eng-result-rev-count srch-eng-result-rat-txt">110 Reviews</div> 
									<div class="srch-eng-result-rec-rev srch-eng-result-rat-txt">Recent review</div> 
								</div> 
							</div> 
						</div> 
						<div class="srch-eng-result-address col-lg-3 col-md-3 col-sm-5 col-xs-12"> 
							<div class="srch-eng-result-distance">1.7 miles</div> 
							<div class="srch-eng-result-address1 srch-eng-addr-txt">Address text3-1</div> 
							<div class="srch-eng-result-address2 srch-eng-addr-txt">Address text3-2</div> 
							<div class="srch-eng-result-place srch-eng-addr-txt">U.S.A</div> 
							<div class="srch-eng-result-contact srch-eng-addr-txt">Contact3</div> 
						</div> 
					</div> 
				</div> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-review"> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-rev-text-cont"> 
						<p class="srch-eng-rev-text" data-review="">Review by customer xyz3</p> 
						<div class="srch-eng-read-more cursor-pointer">... read more</div> 
					</div> 
				</div> 
			</div>
			
			<div id="srch-eng-result-item" class="container srch-eng-result-item">
				<input type="hidden" val="" class="srch-eng-pub-page-link-inp"> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-details"> 
					<div class="srch-eng-result-img-cont srch-eng-pub-page-link"> 
						<div class="srch-eng-result-rank-cont"></div>
						<div class="srch-eng-result-rank">4</div> 
						<div class="srch-eng-result-img">
							<img alt="SocialSurvey" src="" class="srch-eng-default-prof-img">
						</div> 
					</div> 
					<div class="srch-eng-result-prof-details"> 
						<div class="srch-eng-prof-details-cont col-lg-9 col-md-9 col-sm-7 col-xs-12"> 
							<div class="srch-eng-result-name srch-eng-pub-page-link">Name4</div> 
							<div class="srch-eng-result-title-nmls">Title-nmls4</div> 
							<div class="srch-eng-result-company">company4</div> 
							<div class="srch-eng-result-rev-details"> 
								<div class="srch-eng-result-rev-stars srch-eng-pub-page-link"> 
									<div class="srch-eng-result-star srch-eng-star-1 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-2 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-3 srch-eng-nostar"></div> 
									<div class="srch-eng-result-star srch-eng-star-4 srch-eng-nostar"></div> 
									<div class="srch-eng-result-star srch-eng-star-5 srch-eng-nostar"></div> 
								</div> 
								<div class="srch-eng-result-rev-count-cont srch-eng-result-rat-txt srch-eng-pub-page-link"> 
									<div class="srch-eng-result-rat srch-eng-result-rat-txt">2.0</div> -  
									<div class="srch-eng-result-rev-count srch-eng-result-rat-txt">24 Reviews</div> 
									<div class="srch-eng-result-rec-rev srch-eng-result-rat-txt">Recent review</div> 
								</div> 
							</div> 
						</div> 
						<div class="srch-eng-result-address col-lg-3 col-md-3 col-sm-5 col-xs-12"> 
							<div class="srch-eng-result-distance">2.2 miles</div> 
							<div class="srch-eng-result-address1 srch-eng-addr-txt">Address text4-1</div> 
							<div class="srch-eng-result-address2 srch-eng-addr-txt">Address text4-2</div> 
							<div class="srch-eng-result-place srch-eng-addr-txt">U.S.A</div> 
							<div class="srch-eng-result-contact srch-eng-addr-txt">Contact1</div> 
						</div> 
					</div> 
				</div> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-review"> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-rev-text-cont"> 
						<p class="srch-eng-rev-text" data-review="">Review by customer xyz-4</p> 
						<div class="srch-eng-read-more cursor-pointer">... read more</div> 
					</div> 
				</div> 
			</div>
			
			<div id="srch-eng-result-item" class="container srch-eng-result-item">
				<input type="hidden" val="" class="srch-eng-pub-page-link-inp"> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-details"> 
					<div class="srch-eng-result-img-cont srch-eng-pub-page-link"> 
						<div class="srch-eng-result-rank-cont"></div>
						<div class="srch-eng-result-rank">5</div> 
						<div class="srch-eng-result-img">
							<img alt="SocialSurvey" src="" class="srch-eng-default-prof-img">
						</div> 
					</div> 
					<div class="srch-eng-result-prof-details"> 
						<div class="srch-eng-prof-details-cont col-lg-9 col-md-9 col-sm-7 col-xs-12"> 
							<div class="srch-eng-result-name srch-eng-pub-page-link">Name5</div> 
							<div class="srch-eng-result-title-nmls">Title-nmls5</div> 
							<div class="srch-eng-result-company">company5</div> 
							<div class="srch-eng-result-rev-details"> 
								<div class="srch-eng-result-rev-stars srch-eng-pub-page-link"> 
									<div class="srch-eng-result-star srch-eng-star-1 srch-eng-fullstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-2 srch-eng-3quatstar"></div> 
									<div class="srch-eng-result-star srch-eng-star-3 srch-eng-nostar"></div> 
									<div class="srch-eng-result-star srch-eng-star-4 srch-eng-nostar"></div> 
									<div class="srch-eng-result-star srch-eng-star-5 srch-eng-nostar"></div> 
								</div> 
								<div class="srch-eng-result-rev-count-cont srch-eng-result-rat-txt srch-eng-pub-page-link"> 
									<div class="srch-eng-result-rat srch-eng-result-rat-txt">1.65</div> -  
									<div class="srch-eng-result-rev-count srch-eng-result-rat-txt">4 Reviews</div> 
									<div class="srch-eng-result-rec-rev srch-eng-result-rat-txt">Recent review</div> 
								</div> 
							</div> 
						</div> 
						<div class="srch-eng-result-address col-lg-3 col-md-3 col-sm-5 col-xs-12"> 
							<div class="srch-eng-result-distance">1.1 miles</div> 
							<div class="srch-eng-result-address1 srch-eng-addr-txt">Address text5-1</div> 
							<div class="srch-eng-result-address2 srch-eng-addr-txt">Address text5-2</div> 
							<div class="srch-eng-result-place srch-eng-addr-txt">U.S.A</div> 
							<div class="srch-eng-result-contact srch-eng-addr-txt">Contact5</div> 
						</div> 
					</div> 
				</div> 
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-review"> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-quote">❛</div> 
					<div class="srch-eng-rev-text-cont"> 
						<p class="srch-eng-rev-text" data-review="">Review by customer xyz-5</p> 
						<div class="srch-eng-read-more cursor-pointer">... read more</div> 
					</div> 
				</div> 
			</div>
			
		</div>
		<div class="srch-eng-result-hdr">
			<div class="container srch-eng-result-hdr-txt">
				<div class="srch-eng-hdr-pagination">
					  <div class="srch-eng-pag-icon srch-eng-pag-prev cursor-pointer" style="display:none">❮</div>
					  <div class="srch-eng-page-no">1</div>
					  <div class="srch-eng-pag-icon srch-eng-pag-next cursor-pointer" style="">❯</div>
				</div>
			</div>
		</div>
	</div>
	<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/search-engine.js"></script>
</body>
</html>