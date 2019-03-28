var lastNearMeAjaxRequestToDelete = null;
var lastLOSearchAjaxRequestToDelete = null;
var lastLOSearchCountAjaxRequestToDelete = null;

function resetPageIndex() {
	$('#srch-eng-pag-data').attr('data-startIndex', 0);
	$('#srch-eng-pag-data').attr('data-pageNo', 1);
	$('.srch-eng-page-no').html(1);
}

$('#srch-eng-filter-container').on('click', '#srch-eng-adv-srch-btn', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	$('#srch-eng-adv-srch-up').toggle();
	$('#srch-eng-adv-srch-down').toggle();

	$('#srch-eng-filter-adv-container').slideToggle();
});

$(document).on('click', function(e) {
	if ($('#srch-eng-loc-dropdown').is(':visible')) {
		$('#srch-eng-loc-dropdown').slideToggle();
	}
});

$('#srch-eng-loc-inp').on('keyup', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();
	e.stopPropagation();

	var searchString = $('#srch-eng-loc-inp').val();

	if (searchString == undefined || searchString == '' || searchString == null) {
		if ($('#srch-eng-loc-dropdown').is(':visible')) {
			$('#srch-eng-loc-dropdown').slideToggle();
		}
		if (lastNearMeAjaxRequestToDelete != null) {
			lastNearMeAjaxRequestToDelete.abort();
		}

		$('#srchEngLocFromBrowser').val(false);
		$('#srch-eng-loc-inp').attr('data-lat', 0);
		$('#srch-eng-loc-inp').attr('data-lng', 0);
	} else {
		getNearMeSuggestions(searchString);
	}
});

$('#srch-eng-filter-container').on('click', '.srch-eng-filter-txt', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	$(this).parent().find('.srch-eng-filter-txt').attr('data-sel', 'false');
	$(this).attr('data-sel', "true");

	resetPageIndex();

	getLOSearchList();
});

$('#srch-eng-filter-container').on('click', '#srch-eng-srch-btn', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	resetPageIndex();
	
	getLOSearchList();
});

$('#srch-eng-cat-cont').on('click', '.srch-eng-unchecked', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	$(this).parent().attr('data-sel', 'true');
	$(this).hide();
	$(this).parent().find('.srch-eng-checked').show();

	var selFilters = $('#srch-eng-cat-cont').data('filters');
	selFilters.push($(this).parent().attr('data-filter'));
	$('#srch-eng-cat-cont').data('filters', selFilters);

	resetPageIndex();

	getLOSearchList();
});

$('#srch-eng-cat-cont').on('click', '.srch-eng-checked', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	$(this).parent().attr('data-sel', 'false');
	$(this).hide();
	$(this).parent().find('.srch-eng-unchecked').show();

	var selFilters = $('#srch-eng-cat-cont').data('filters');
	for (var i = selFilters.length - 1; i >= 0; i--) {
		if (selFilters[i] == $(this).parent().attr('data-filter')) {
			selFilters.splice(i, 1);
		}
	}
	$('#srch-eng-cat-cont').data('filters', selFilters);

	resetPageIndex();

	getLOSearchList();
});

$('#srch-eng-cat-cont').on('click', '.srch-eng-more-cat', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	$('.srch-eng-cat-item').show();

	$(this).hide();
	$('.srch-eng-less-cat').show();
});

$('#srch-eng-cat-cont').on('click', '.srch-eng-less-cat', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	$('#srch-eng-cat-list-cont').find('.srch-eng-cat-item').each(function() {
		if ($(this).data('item') > 3) {
			$(this).hide();
		}
	});

	$(this).hide();
	$('.srch-eng-more-cat').show();
});

function getLOSearchList() {

	var url = getLocationOrigin() + '/searchengine/searchresults.do';

	var payload = getSearchFilters();

	$('#srch-eng-loader').show();

	lastLOSearchAjaxRequestToDelete = $.ajax({
		url : url,
		headers : {
			Accept : "text/plain; charset=utf-8"
		},
		type : "POST",
		data : {"payload" : JSON.stringify(payload)},
		dataType: 'json',
		beforeSend : function() {
			if (lastLOSearchAjaxRequestToDelete != null) {
				lastLOSearchAjaxRequestToDelete.abort();
			}
		},
		success : drawSearchResults,
		complete : function(data) {
			if ($('#srch-eng-loc-inp').val() != null && $('#srch-eng-loc-inp').val() != undefined && $('#srch-eng-loc-inp').val() != '') {
				$('.srch-eng-result-hdr-loc').html(' near ' + $('#srch-eng-loc-inp').val());
			}

			var categoryCriteria = $('#srch-eng-cat-cont').data('filters');
			if(categoryCriteria.length == 0 || categoryCriteria == null || categoryCriteria == undefined || categoryCriteria.length > 1){
				$('.srch-eng-result-hdr-vert').html('Top');
			}else if (categoryCriteria.length < 2) {
				$('.srch-eng-result-hdr-vert').html(categoryCriteria[0] + ' ');
			}

			var profileCriteria = $('#srch-eng-prof-cont').find('[data-sel="true"]').attr('data-filter');
			if (profileCriteria == undefined || profileCriteria == '' || profileCriteria == null) {
				$('.srch-eng-result-hdr-prof').html(' Professionals ');
			} else {
				$('.srch-eng-result-hdr-prof').html(profileCriteria + ' ');
			}

			getLOSearchListCount();
			
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function getSearchFilters() {

	var sortBy = getSortingOrder();
	var distanceCriteria = getDistanceCriteria();
	var ratingCriteria = getRatingCriteria();
	var reviewCountCriteria = getReviewCriteria();
	var profileFilter = getProfilesCriteria();
	var categoryFilterList = getCategoryCriteria();
	var findBasedOn = $('#srch-eng-cat-inp').val();
	var lat = getLat();
	var lng = getLng();
	var startIndex = parseInt($('#srch-eng-pag-data').attr('data-startIndex'));
	var batchSize = parseInt($('#srch-eng-pag-data').attr('data-batchSize'));
	var companyProfileName = $('#srchEngCompanyProfileName').val();
	
	if(!(lat == 0 && lng == 0)){
		$('#cityName').val("");
		$('#stateCode').val("");
		if(distanceCriteria == 0){
			if($('#srchEngLocFromBrowser').val() == 'false'){
				distanceCriteria = 25;
			}else{
				distanceCriteria = 75;
			}
		}
	}	

	var cityName = $('#cityName').val();
	var stateCode = $('#stateCode').val();
	
	var filters = {
		"startIndex" : startIndex,
		"batchSize" : batchSize,
		"sortBy" : sortBy,
		"distanceCriteria" : distanceCriteria,
		"ratingCriteria" : ratingCriteria,
		"reviewCountCriteria" : reviewCountCriteria,
		"profileFilter" : profileFilter,
		"categoryFilterList" : categoryFilterList,
		"findBasedOn" : findBasedOn,
		"lat" : lat,
		"lng" : lng,
		"companyProfileName" : companyProfileName,
		"cityName" : cityName,
		"stateCode" : stateCode
	}

	return filters;
}

function getSortingOrder() {
	return $('#srch-eng-sort-by-cont').find('[data-sel="true"]').attr('data-filter');
}

function getDistanceCriteria() {
	var disCri = parseInt($('#srch-eng-dist-cont').find('[data-sel="true"]').attr('data-order'));
	switch (disCri) {
	case 1:
		return 75;
	case 2:
		return 50;
	case 3:
		return 25;
	case 4:
		return 10;
	default:
		return 0;
	}
}

function getRatingCriteria() {
	var ratCri = parseInt($('#srch-eng-rat-cont').find('[data-sel="true"]').attr('data-order'));
	switch (ratCri) {
	case 1:
		return 5;
	case 2:
		return 4;
	case 3:
		return 3;
	case 4:
		return 0;
	default:
		return 0;
	}
}

function getReviewCriteria() {
	var ratCri = parseInt($('#srch-eng-rev-cont').find('[data-sel="true"]').attr('data-order'));
	switch (ratCri) {
	case 1:
		return 100;
	case 2:
		return 50;
	case 3:
		return 5;
	case 4:
		return 1;
	default:
		return 0;
	}
}

function getLat() {
	return $('#srch-eng-loc-inp').attr('data-lat');
}

function getLng() {
	return $('#srch-eng-loc-inp').attr('data-lng');
}

function getCategoryCriteria() {
	var categoryCriteria = $('#srch-eng-cat-cont').data('filters');
	$('#srch-eng-cat-list').val(categoryCriteria);
	/*
	 * $('#srch-eng-cat-cont').find('[data-sel="true"]').each(function(){ categoryCriteria.push($(this).attr('data-filter')); });
	 */
	return categoryCriteria;
}

function getProfilesCriteria() {
	return $('#srch-eng-prof-cont').find('[data-sel="true"]').attr('data-filter') == undefined ? "" : $('#srch-eng-prof-cont').find('[data-sel="true"]').attr('data-filter');
}

function getSearchFiltersAppSettings() {

	var url = getLocationOrigin() + '/searchengine/applosetting.do';

	$.ajax({
		url : url,
		type : "GET",
		async : true,
		cache : false,
		success : drawAdvancedOptions,
		complete : function() {

		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			if (e.status == 0) {
				return;
			}
		}
	});
}

function getNearMeSuggestions(searchString) {

	var url = getLocationOrigin() + '/searchengine/nearmesuggestions.do';

	var startIndex = 0;
	var batchSize = -1;

	var payload = {
		"searchString" : searchString,
		"startIndex" : startIndex,
		"batchSize" : batchSize
	}

	if (lastNearMeAjaxRequestToDelete != null) {
		lastNearMeAjaxRequestToDelete.abort();
		lastNearMeAjaxRequestToDelete = null;
	}

	lastNearMeAjaxRequestToDelete = $.ajax({
		url : url,
		type : "GET",
		async : true,
		cache : false,
		data : payload,
		success : showNearMeSuggestions,
		complete : function() {

		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			if (e.status == 0) {
				return;
			}
		}
	});
}

function showNearMeSuggestions(nearMe) {

	if ($('#srch-eng-loc-dropdown').is(':hidden')) {
		$('#srch-eng-loc-dropdown').slideToggle();
	}
	$('#srch-eng-loc-dropdown').html('');

	var nearMeList = JSON.parse(nearMe);

	if (nearMeList.length == 0) {
		$('#srch-eng-loc-dropdown').html('<div class="srch-eng-empty-drop">No results found</div>');
		$('#srch-eng-loc-dropdown').addClass('srch-eng-empty-loc');
		return;
	} else {
		$('#srch-eng-loc-dropdown').removeClass('srch-eng-empty-loc');
	}

	for (i = 0; i < nearMeList.length; i++) {
		var location = nearMeList[i].cityname + ', ' + nearMeList[i].stateLookup.statecode + ' ' + nearMeList[i].zipcode;
		var lat = nearMeList[i].latitude;
		var lng = nearMeList[i].longitude;

		$('#srch-eng-loc-dropdown').append('<div class="srch-eng-loc-dropdown-item cursor-pointer" data-lat=' + lat + ' data-lng=' + lng + '>' + location + '</div>');
	}

	$('#srch-eng-loc-dropdown').on('click', '.srch-eng-loc-dropdown-item', function(e) {
		e.stopImmediatePropagation();
		e.preventDefault();

		var lat = $(this).attr('data-lat');
		var lng = $(this).attr('data-lng');
		var location = $(this).html();

		$('#srch-eng-loc-inp').attr('data-lat', lat);
		$('#srch-eng-loc-inp').attr('data-lng', lng);
		$('#srch-eng-loc-inp').val(location);
		$('#srchEngLocFromBrowser').val(false);

		$('#srch-eng-loc-dropdown').slideToggle();
		
		resetPageIndex();
		
		getLOSearchList();
	});
}

function drawSearchResults(searchResultsData) {
	var searchResultCont = '<div id="srch-eng-result-item" class="container srch-eng-result-item"><input type="hidden" val="" class="srch-eng-pub-page-link-inp">' + '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-details">' + '<div class="srch-eng-result-img-cont srch-eng-pub-page-link">' + '<div class="srch-eng-result-rank-cont"></div><div class="srch-eng-result-rank"></div>' + '<div class="srch-eng-result-img"><img alt="SocialSurvey" src="" class="srch-eng-result-prof-img" onerror="$(this).parent().addClass(\'srch-eng-default-prof-img\');$(this).hide();"></div>' + '</div>' + '<div class="srch-eng-result-prof-details">' + '<div class="srch-eng-prof-details-cont col-lg-9 col-md-9 col-sm-7 col-xs-12">' + '<div class="srch-eng-result-name srch-eng-pub-page-link"></div>' + '<div class="srch-eng-result-title-nmls"></div>' + '<div class="srch-eng-result-company"></div>' + '<div class="srch-eng-result-rev-details">' + '<div class="srch-eng-result-rev-stars srch-eng-pub-page-link">' + '<div class="srch-eng-result-star srch-eng-star-1 srch-eng-nostar"></div>' + '<div class="srch-eng-result-star srch-eng-star-2 srch-eng-nostar"></div>' + '<div class="srch-eng-result-star srch-eng-star-3 srch-eng-nostar"></div>' + '<div class="srch-eng-result-star srch-eng-star-4 srch-eng-nostar"></div>' + '<div class="srch-eng-result-star srch-eng-star-5 srch-eng-nostar"></div>' + '</div>' + '<div class="srch-eng-result-rev-count-cont srch-eng-result-rat-txt srch-eng-pub-page-link">' + '<div class="srch-eng-result-rat srch-eng-result-rat-txt"></div> - ' + '<div class="srch-eng-result-rev-count srch-eng-result-rat-txt"></div>' + '<div class="srch-eng-result-rec-rev srch-eng-result-rat-txt"></div>' + '</div>' + '</div>' + '</div>' + '<div class="srch-eng-result-address col-lg-3 col-md-3 col-sm-5 col-xs-12">' + '<div class="srch-eng-result-distance"></div>' + '<div class="srch-eng-result-address1 srch-eng-addr-txt"></div>' + '<div class="srch-eng-result-address2 srch-eng-addr-txt"></div>' + '<div class="srch-eng-result-place srch-eng-addr-txt"></div>' + '<div class="srch-eng-result-contact srch-eng-addr-txt"></div>' + '</div>' + '</div>' + '</div>' + '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 srch-eng-result-review">' + '<div class="srch-eng-quote">❛</div>' + '<div class="srch-eng-quote">❛</div>' + '<div class="srch-eng-rev-text-cont">' + '<p class="srch-eng-rev-text" data-review=""></p>' + '<div class="srch-eng-read-more cursor-pointer">... read more</div>' + '</div>' + '</div>' + '</div>';

	var searchResults = searchResultsData;
	var defaultProfileImage = 'resources/images/place-holder-individual.png';

	$('.srch-eng-result-cont').html('');

	if (searchResults.length == 0 || searchResults == undefined || searchResults == null || searchResults == "null" || searchResults == "error") {

		$('.srch-eng-result-cont').html('<div class="srch-eng-result-empty container">No Results found</div>');
		return;
	}

	var startIndex = parseInt($('#srch-eng-pag-data').attr('data-startIndex'));

	for (var i = 0; i < searchResults.length; i++) {
		$('.srch-eng-result-cont').append(searchResultCont);

		$('#srch-eng-result-item').find('.srch-eng-result-rank').html(startIndex + i + 1);

		if (searchResults[i].profileImageUrlThumbnail != null && searchResults[i].profileImageUrlThumbnail != undefined && searchResults[i].profileImageUrlThumbnail.length != 0) {
			$('#srch-eng-result-item').find('.srch-eng-result-prof-img').attr('src', searchResults[i].profileImageUrlThumbnail);
		} else {
			$('#srch-eng-result-item').find('.srch-eng-result-prof-img').hide();
			$('#srch-eng-result-item').find('.srch-eng-result-img').addClass('srch-eng-default-prof-img');
		}

		$('#srch-eng-result-item').find('.srch-eng-result-name').html(searchResults[i].name);

		var title = ((searchResults[i].title != null && searchResults[i].title != undefined && searchResults[i].title != '') ? searchResults[i].title : '');
		var nmls = ((searchResults[i].nmls != null && searchResults[i].nmls != undefined && searchResults[i].nmls != '') ? ' | ' + searchResults[i].nmls : '');
		var titleNmls = title + nmls;
		$('#srch-eng-result-item').find('.srch-eng-result-title-nmls').html(titleNmls);

		$('#srch-eng-result-item').find('.srch-eng-result-company').html(searchResults[i].companyName);

		drawRatingStars($('#srch-eng-result-item'), searchResults[i].rating);

		$('#srch-eng-result-item').find('.srch-eng-result-rat').html(searchResults[i].rating.toFixed(2));

		$('#srch-eng-result-item').find('.srch-eng-result-rev-count').html(searchResults[i].numberOfReviews + ' Reviews');

		$('#srch-eng-result-item').find('.srch-eng-result-rec-rev').html('(' + searchResults[i].numberOfRecentReviews + ' Recent)');

		var lat = parseInt($('#srch-eng-loc-inp').attr('data-lat'));
		var lng = parseInt($('#srch-eng-loc-inp').attr('data-lng'));

		if (lat != 0 || lng != 0) {
			$('#srch-eng-result-item').find('.srch-eng-result-distance').html(searchResults[i].loSearchContactAndDistanceVO.distance + ' miles');
		}

		if(searchResults[i].loSearchContactAndDistanceVO.address1 != null && searchResults[i].loSearchContactAndDistanceVO.address1 != undefined && searchResults[i].loSearchContactAndDistanceVO.address1 != ""){
			$('#srch-eng-result-item').find('.srch-eng-result-address1').html(searchResults[i].loSearchContactAndDistanceVO.address1);
		}
		
		if(searchResults[i].loSearchContactAndDistanceVO.address2 != null && searchResults[i].loSearchContactAndDistanceVO.address2 != undefined && searchResults[i].loSearchContactAndDistanceVO.address2 != ""){
			$('#srch-eng-result-item').find('.srch-eng-result-address2').html(searchResults[i].loSearchContactAndDistanceVO.address2);
		}
		
		var place = "";
		
		if(searchResults[i].loSearchContactAndDistanceVO.city != null && searchResults[i].loSearchContactAndDistanceVO.city != undefined && searchResults[i].loSearchContactAndDistanceVO.city != ''){
			place +=  searchResults[i].loSearchContactAndDistanceVO.city + ', ';
		}
		
		if(searchResults[i].loSearchContactAndDistanceVO.state != null && searchResults[i].loSearchContactAndDistanceVO.state != undefined && searchResults[i].loSearchContactAndDistanceVO.state != ''){
			place +=  searchResults[i].loSearchContactAndDistanceVO.state + ' ';
		}
		
		if(searchResults[i].loSearchContactAndDistanceVO.zipcode != null && searchResults[i].loSearchContactAndDistanceVO.zipcode != undefined && searchResults[i].loSearchContactAndDistanceVO.zipcode != ''){
			place +=  searchResults[i].loSearchContactAndDistanceVO.zipcode;
		}
		
		if(place != null && place != undefined && place != ""){
			$('#srch-eng-result-item').find('.srch-eng-result-place').html(place);
		}

		if(searchResults[i].loSearchContactAndDistanceVO.contactNumber != null && searchResults[i].loSearchContactAndDistanceVO.contactNumber != undefined && searchResults[i].loSearchContactAndDistanceVO.contactNumber != ""){
			$('#srch-eng-result-item').find('.srch-eng-result-contact').html(searchResults[i].loSearchContactAndDistanceVO.contactNumber);
		}
		
		$('#srch-eng-result-item').find('.srch-eng-pub-page-link-inp').val(searchResults[i].profileUrl);
		
		if (searchResults[i].latestReview != null && searchResults[i].latestReview != undefined && searchResults[i].latestReview.length != 0) {
			$('#srch-eng-result-item').find('.srch-eng-rev-text').html(searchResults[i].latestReview);

			if(window.innerWidth < 767){
				if (searchResults[i].latestReview.length < 167) {
					$('#srch-eng-result-item').find('.srch-eng-read-more').hide();
				}
			}else{
				if (searchResults[i].latestReview.length < 276) {
					$('#srch-eng-result-item').find('.srch-eng-read-more').hide();
				}
			}
			
		} else {
			$('#srch-eng-result-item').find('.srch-eng-result-review').hide();
		}

		$('#srch-eng-result-item').attr('id', 'srch-eng-result-item-' + i);
	}
	
	$('#srch-eng-result-item-' + (searchResults.length-1)).css('border-bottom','0px');

	$('.srch-eng-rev-text-cont').on('click', '.srch-eng-read-more', function(e) {
		e.stopImmediatePropagation();
		e.preventDefault();

		$(this).hide();
		$(this).parent().find('.srch-eng-rev-text').css('max-height', 'none');
	});
	
	$(document).on('click','.srch-eng-pub-page-link',function(e){
		e.stopImmediatePropagation();
		e.preventDefault();
		
		var profileUrl =  $(this).closest('.srch-eng-result-item').find('.srch-eng-pub-page-link-inp').val();
		
		if(profileUrl == undefined || profileUrl == null || profileUrl == ''){
			redirectPageNotFoundpage();
		}else{
			var win = window.open(profileUrl, '_blank');
			if (win) {
			    //Browser has allowed it to be opened
			    win.focus();
			} else {
			    //Browser has blocked it
			    alert('Please allow popups for this website');
			}
		}
	});
}

function drawRatingStars(parent, averageRating) {

	if (Math.floor(averageRating) == averageRating) {
		switch (Math.floor(averageRating)) {
		case 1:
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			break;
		case 2:
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			break;
		case 3:
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-3').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			break;
		case 4:
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-3').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-4').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			break;
		case 5:
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-3').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-4').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-5').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			break;
		}
	} else {
		if (Math.floor(averageRating) < 1) {

			if (averageRating > 0.55) {
				parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-3quatstar');
			} else if (averageRating > 0.25) {
				parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-halfstar');
			} else {
				parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-quatstar');
			}
		} else if (Math.floor(averageRating) < 2) {
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');

			if (averageRating > 1.55) {
				parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-3quatstar');
			} else if (averageRating > 1.25) {
				parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-halfstar');
			} else {
				parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-3quatstar');
			}
		} else if (Math.floor(averageRating) < 3) {
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');

			if (averageRating > 2.55) {
				parent.find('.srch-eng-star-3').removeClass('srch-eng-nostar').addClass('srch-eng-3quatstar');
			} else if (averageRating > 2.25) {
				parent.find('.srch-eng-star-3').removeClass('srch-eng-nostar').addClass('srch-eng-halfstar');
			} else {
				parent.find('.srch-eng-star-3').removeClass('srch-eng-nostar').addClass('srch-eng-quatstar');
			}
		} else if (Math.floor(averageRating) < 4) {
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-3').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');

			if (averageRating > 3.55) {
				parent.find('.srch-eng-star-4').removeClass('srch-eng-nostar').addClass('srch-eng-3quatstar');
			} else if (averageRating > 3.25) {
				parent.find('.srch-eng-star-4').removeClass('srch-eng-nostar').addClass('srch-eng-halfstar');
			} else {
				parent.find('.srch-eng-star-4').removeClass('srch-eng-nostar').addClass('srch-eng-quatstar');
			}
		} else {
			parent.find('.srch-eng-star-1').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-2').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-3').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');
			parent.find('.srch-eng-star-4').removeClass('srch-eng-nostar').addClass('srch-eng-fullstar');

			if (averageRating > 4.55) {
				parent.find('.srch-eng-star-5').removeClass('srch-eng-nostar').addClass('srch-eng-3quatstar');
			} else if (averageRating > 4.25) {
				parent.find('.srch-eng-star-5').removeClass('srch-eng-nostar').addClass('srch-eng-halfstar');
			} else {
				parent.find('.srch-eng-star-5').removeClass('srch-eng-nostar').addClass('srch-eng-quatstar');
			}
		}
	}
}

function drawAdvancedOptions(filtersList) {

	$('#srch-eng-loc-dropdown').width($('#srch-eng-loc-inp').width() - 15);

	var filters = JSON.parse(filtersList);

	var profilesCriteriaCont = '<div data-order=1 data-filter="" data-sel="false" class="srch-eng-prof-item">' + '<img alt="unchecked" src="/resources/images/checkbox-blank-outline.png" class="srch-eng-chk-box srch-eng-unchecked">' + '<img alt="checked" src="/resources/images/checkbox-marked-outline.png" class="srch-eng-chk-box srch-eng-checked" style="display:none">' + '<div class="srch-eng-prof-txt">Professionals</div>' + '</div>';

	var categoryCriteriaCont = '<div data-item=1 data-vertId="" data-filter="" data-sel="false" class="srch-eng-cat-item">' + '<img alt="unchecked" src="/resources/images/checkbox-blank-outline.png" class="srch-eng-chk-box srch-eng-unchecked">' + '<img alt="checked" src="/resources/images/checkbox-marked-outline.png" class="srch-eng-chk-box srch-eng-checked" style="display:none">' + '<div class="srch-eng-cat-txt">Mortgage</div>' + '</div>';
	var sortBy = filters.sortingOrder;
	var distanceCriteria = filters.distanceCriteria;
	var ratingCriteria = filters.ratingCriteria;
	var reviewCriteria = filters.reviewCriteria;
	var profilesList = filters.profilesCriteria;
	var categoryList = filters.verticals;

	for (i = 0; i < sortBy.length; i++) {
		$('#srch-eng-sort-by-cont').find('.srch-eng-filter-txt').eq(i).attr('data-order', sortBy[i].displayOrder);
		$('#srch-eng-sort-by-cont').find('.srch-eng-filter-txt').eq(i).html(sortBy[i].displayName);
		$('#srch-eng-sort-by-cont').find('.srch-eng-filter-txt').eq(i).attr('data-filter', sortBy[i].displayName);
		if (sortBy[i].defaultSet == true || sortBy[i].defaultSet == "true") {
			$('#srch-eng-sort-by-cont').find('.srch-eng-filter-txt').eq(i).attr('data-sel', sortBy[i].defaultSet);
		}
	}

	for (i = 0; i < distanceCriteria.length; i++) {
		$('#srch-eng-dist-cont').find('.srch-eng-filter-txt').eq(i).attr('data-order', distanceCriteria[i].displayOrder);
		$('#srch-eng-dist-cont').find('.srch-eng-filter-txt').eq(i).html(distanceCriteria[i].displayName);
		$('#srch-eng-dist-cont').find('.srch-eng-filter-txt').eq(i).attr('data-filter', distanceCriteria[i].displayName);
		if (distanceCriteria[i].defaultSet == true || distanceCriteria[i].defaultSet == "true") {
			$('#srch-eng-dist-cont').find('.srch-eng-filter-txt').eq(i).attr('data-sel', distanceCriteria[i].defaultSet);
		}
	}

	for (i = 0; i < ratingCriteria.length; i++) {
		$('#srch-eng-rat-cont').find('.srch-eng-filter-txt').eq(i).attr('data-order', ratingCriteria[i].displayOrder);
		$('#srch-eng-rat-cont').find('.srch-eng-filter-txt').eq(i).html(ratingCriteria[i].displayName);
		$('#srch-eng-rat-cont').find('.srch-eng-filter-txt').eq(i).attr('data-filter', ratingCriteria[i].displayName);
		if (ratingCriteria[i].defaultSet == true || ratingCriteria[i].defaultSet == "true") {
			$('#srch-eng-rat-cont').find('.srch-eng-filter-txt').eq(i).attr('data-sel', ratingCriteria[i].defaultSet);
		}
	}

	for (i = 0; i < reviewCriteria.length; i++) {
		$('#srch-eng-rev-cont').find('.srch-eng-filter-txt').eq(i).attr('data-order', reviewCriteria[i].displayOrder);
		$('#srch-eng-rev-cont').find('.srch-eng-filter-txt').eq(i).html(reviewCriteria[i].displayName);
		$('#srch-eng-rev-cont').find('.srch-eng-filter-txt').eq(i).attr('data-filter', reviewCriteria[i].displayName);
		if (reviewCriteria[i].defaultSet == true || reviewCriteria[i].defaultSet == "true") {
			$('#srch-eng-rev-cont').find('.srch-eng-filter-txt').eq(i).attr('data-sel', reviewCriteria[i].defaultSet);
		}
	}

	for (i = 0; i < profilesList.length; i++) {
		$('#srch-eng-prof-cont').find('.srch-eng-filter-txt').eq(i).attr('data-order', profilesList[i].displayOrder);
		$('#srch-eng-prof-cont').find('.srch-eng-filter-txt').eq(i).html(profilesList[i].displayName);
		$('#srch-eng-prof-cont').find('.srch-eng-filter-txt').eq(i).attr('data-filter', profilesList[i].displayName);
		if (profilesList[i].defaultSet == true || profilesList[i].defaultSet == "true") {
			$('#srch-eng-prof-cont').find('.srch-eng-filter-txt').eq(i).attr('data-sel', profilesList[i].defaultSet);
		}
	}

	if (categoryList.length != 0) {
		$('#srch-eng-cat-list-cont').html('');
		for (i = 0; i < categoryList.length; i++) {
			$('#srch-eng-cat-list-cont').append(categoryCriteriaCont);
			$('#srch-eng-cat-list-cont').find('.srch-eng-cat-item').eq(i).data('item', i + 1);
			$('#srch-eng-cat-list-cont').find('.srch-eng-cat-item').eq(i).attr('data-vertId', categoryList[i].verticalsMasterId);
			$('#srch-eng-cat-list-cont').find('.srch-eng-cat-item').eq(i).attr('data-filter', categoryList[i].verticalName);
			$('#srch-eng-cat-list-cont').find('.srch-eng-cat-item').eq(i).find('.srch-eng-cat-txt').html(categoryList[i].verticalName);
			if (categoryList[i].defaultSet == true || categoryList[i].defaultSet == "true") {
				$('#srch-eng-cat-list-cont').find('.srch-eng-cat-item').eq(i).find('.srch-eng-chk-box').toggle();
				var categorySel = $('#srch-eng-cat-cont').data('filters');
				if (categorySel == undefined || categorySel == "" || categorySel == null) {
					categorySel = new Array();
				}
				categorySel.push(categoryList[i].verticalName);
				$('#srch-eng-cat-cont').data('filters', categorySel);
			}
			$('#srch-eng-cat-list-cont').find('.srch-eng-cat-item').eq(i).attr('data-sel', categoryList[i].defaultSet);

			if (i > 2) {
				$('#srch-eng-cat-list-cont').find('.srch-eng-cat-item').eq(i).hide();
			}
		}
		if (categoryList.length > 2) {
			$('#srch-eng-cat-cont').find('.srch-eng-more-cat').show();
		}
	}

}

function getLOSearchListCount() {

	var url = getLocationOrigin() + '/searchengine/searchresults/count.do';

	var payload = getSearchFilters();

	lastLOSearchCountAjaxRequestToDelete = $.ajax({
		url : url,
		headers : {
			Accept : "text/plain; charset=utf-8"
		},
		type : "POST",
		data : {"payload" : JSON.stringify(payload)},
		dataType: 'json',
		beforeSend : function() {
			if (lastLOSearchCountAjaxRequestToDelete != null) {
				lastLOSearchCountAjaxRequestToDelete.abort();
			}
		},
		success : paginateSearchPage,
		complete : function() {
			$('#srch-eng-loader').hide();
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function paginateSearchPage(data) {
	var count = JSON.parse(data);

	var startIndex = parseInt($('#srch-eng-pag-data').attr('data-startIndex'));
	var batchSize = parseInt($('#srch-eng-pag-data').attr('data-batchSize'));

	if (startIndex <= 0) {
		$('.srch-eng-pag-prev').hide();
	} else {
		$('.srch-eng-pag-prev').show();
	}

	if (startIndex + batchSize >= count) {
		$('.srch-eng-pag-next').hide();
	} else {
		$('.srch-eng-pag-next').show()
	}
}

$('.srch-eng-hdr-pagination').on('click', '.srch-eng-pag-prev', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	var startIndex = parseInt($('#srch-eng-pag-data').attr('data-startIndex'));
	var batchSize = parseInt($('#srch-eng-pag-data').attr('data-batchSize'));
	var pageNo = parseInt($('#srch-eng-pag-data').attr('data-pageNo'));

	if ((startIndex - batchSize) >= 0) {
		startIndex -= batchSize;
	} else {
		startIndex = 0;
	}

	if (pageNo - 1 != 0) {
		$('#srch-eng-pag-data').attr('data-pageNo', --pageNo);
		$('.srch-eng-page-no').html(pageNo);
	}

	$('#srch-eng-pag-data').attr('data-startIndex', startIndex);

	getLOSearchList();
});

$('.srch-eng-hdr-pagination').on('click', '.srch-eng-pag-next', function(e) {
	e.stopImmediatePropagation();
	e.preventDefault();

	var startIndex = parseInt($('#srch-eng-pag-data').attr('data-startIndex'));
	var batchSize = parseInt($('#srch-eng-pag-data').attr('data-batchSize'));
	var pageNo = parseInt($('#srch-eng-pag-data').attr('data-pageNo'));

	$('#srch-eng-pag-data').attr('data-startIndex', startIndex += batchSize);

	$('#srch-eng-pag-data').attr('data-pageNo', ++pageNo);
	$('.srch-eng-page-no').html(pageNo);

	getLOSearchList();
});

function getSearchResultsForCurrentLocation() {
	resetPageIndex();

	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(initializeLatLong, resetLatLongForNoLocation);
	}
}

function initializeLatLong(position) {
	var lat = position.coords.latitude;
	var lng = position.coords.longitude;

	$('#srch-eng-loc-inp').attr('data-lat', lat);
	$('#srch-eng-loc-inp').attr('data-lng', lng);
	$('#srchEngLocFromBrowser').val(true);
	
	getLOSearchList();
	$('.srch-eng-result-hdr-vert').html('Top');
	$('.srch-eng-result-hdr-loc').html(' near you');
}

function resetLatLongForNoLocation(error) {
	$('#srch-eng-loc-inp').attr('data-lat', 0);
	$('#srch-eng-loc-inp').attr('data-lng', 0);
	$('#srchEngLocFromBrowser').val(false);
	
	getLOSearchList();
	
	$('.srch-eng-result-hdr-vert').html('Top');
}

$('#srch-eng-cat-inp').on('keyup',function(e){
	if(e.keyCode == 13){
		resetPageIndex();
		
		getLOSearchList();
	}
});
$(document).on('click','#srch-eng-pub-page-srch-btn',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	$('.overlay-loader').show();
	searchFromPubPage();
	
});

function searchFromPubPage(){
	
	var basedOn = $('#srch-eng-pub-page-inp').val();
	var companyProfileName = $('#company-profile-name').val();
	
	var payload = {
		"basedOn" : basedOn,
		"companyProfileName" : companyProfileName
	}
	
	var url = getLocationOrigin() + "/showsearchenginepage.do?basedOn="+ basedOn + "&companyProfileName=" + companyProfileName;
	
	window.location = url;
	
	/*$.ajax({
		url : url,
		type : "GET",
		async: true,
		data : payload,
		success : function(data) {
			$("#profile-main-content-div").html(data);
		},
		complete: function(){
			$('.overlay-loader').hide();
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});*/
}

function findProfileOrder(profileName){
		switch(profileName){
		case "Professionals" : return 1;
		case "Loan Offices" : return 2;
		case "Companies" : return 3;
		default : return 1;
		}
	}