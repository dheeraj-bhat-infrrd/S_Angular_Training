var companyProfileName = $("#company-profile-name").val();
var currentProfileIden = "";
var startIndex = 0;
var numOfRows = 5;
var minScore = 0;
var publicPostStartIndex = 0;
var publicPostNumRows = 5;
var currentProfileName;
var doStopPublicPostPagination = false;
var isPublicPostAjaxRequestRunning = false;
var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
	"Sep", "Oct", "Nov", "Dec"];
var profileJson;
var isFetchReviewAjaxRequestRunning = false; //keeps checks of if the ajax request is running to fetch reviews.
var stopFetchReviewPagination = false;
var reviewsNextBatch = []; //Reviews batch to store the next reviews
var publicPostsNextBatch = []; //Posts batch to store the next posts
var isLoaderRunningPublicPosts = false; //Keeps track of loader running in social posts
var isLoaderRunningReviews = false; //Keeps track of loader running in reviews
var doFetchZillowReviews = true;
var doFetchHeirarchyIds = true;
var isZillowReviewsCallRunning = false;
var zillowCallBreak = false
var processedPermalink = 0;

$(document).ajaxStop(function () {
	adjustImageForPublicProfile();
});


function adjustImageForPublicProfile() {
	var windW = getWindowWidth();
	if (windW < 768) {
		//var imgW = $('#prof-image').width();
		//$('#prof-image').height(imgW * 0.7);
		$('.footer-main-wrapper').hide();
	} else {
		$('.lp-con-row-item').width('auto');
		$('.footer-main-wrapper').show();
		//show all the containers
		$('#reviews-container, #prof-company-intro, #prof-agent-container').show();
		$('#recent-post-container, #ppl-post-cont, #contact-wrapper, #intro-about-me').show();
	}
}

//Profile page event binding
$(document).on("click", "#prof-company-review-count", function () {
	if (window.innerWidth < 768) {
		$('.icn-star-smile').click();
	}
	$('html, body').animate({
		scrollTop: $('#reviews-container').offset().top
	}, 500);
});

//Find a pro
$(document).on('keyup', '#find-pro-form input', function (e) {
	if (e.which == 13)
		submitFindProForm();
});

$(document).on("click", '#find-pro-submit', function (e) {
	e.preventDefault();
	submitFindProForm();
});

function submitFindProForm() {
	$('#find-pro-form').submit();
	showOverlay();
}

// Contact us form validation functions
function validateMessage(elementId) {
	var message = 'Please enter your message!';
	if ($('#' + elementId).val() != "") {
		return true;
	} else {
		$('#overlay-toast').text(message);
		showToast();
		return false;
	}
}

function validateName(elementId) {
	var message = 'Please enter your valid name!';
	if ($('#' + elementId).val() != "") {
		if (nameRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			$('#overlay-toast').text(message);
			showToast();
			return false;
		}
	} else {
		$('#overlay-toast').text(message);
		showToast();
		return false;
	}
}

function validateContactUsForm() {

	// Validate form input elements
	if (!validateName('lp-input-name')) {
		$('#lp-input-name').focus();
		return false;
	}

	if (!validateEmailId('lp-input-email', true)) {
		$('#lp-input-email').focus();
		return false;
	}

	if (!validateMessage('lp-input-message')) {
		$('#lp-input-message').focus();
		return false;
	}
	return true;
}

$(document).on('click touchstart', '.icn-person', function () {
	$('.mob-icn').removeClass('mob-icn-active');
	$(this).addClass('mob-icn-active');
	$('#prof-company-intro').show();
	$('#contact-info').show();
	$('#prof-agent-container').hide();
	$('#reviews-container').hide();
	$('#recent-post-container').hide();
});

$(document).on('click touchstart', '.icn-ppl', function () {
	$('.mob-icn').removeClass('mob-icn-active');
	$(this).addClass('mob-icn-active');
	$('#recent-post-container').show();
	$('#contact-info').hide();
	$('#prof-agent-container').hide();
	$('#prof-company-intro').hide();
	$('#reviews-container').hide();
});

$(document).on('click touchstart', '.icn-star-smile', function () {
	$('.mob-icn').removeClass('mob-icn-active');
	$(this).addClass('mob-icn-active');
	$('#reviews-container').show();
	$('#contact-info').hide();
	$('#prof-agent-container').hide();
	$('#prof-company-intro').hide();
	$('#recent-post-container').hide();
});

$(document).on('click touchstart', '.inc-more', function () {
	$('.mob-icn').removeClass('mob-icn-active');
	$(this).addClass('mob-icn-active');
	$('#prof-agent-container').show();
	$('#prof-company-intro').hide();
	$('#contact-info').hide();
	$('#reviews-container').hide();
	$('#recent-post-container').hide();
});

$(document).on('click', '.bd-q-contact-us', function () {
	$('#contact-us-pu-wrapper').show();
	$('body').addClass('body-no-scroll-y');
});

$(document).on('click', '.bd-q-btn-cancel', function () {
	$('#contact-us-pu-wrapper').hide();
	$('body').removeClass('body-no-scroll-y');
});

$(document).on('click', '.lp-button', function (event) {
	if (validateContactUsForm()) {
		url = getLocationOrigin() + "/pages/profile/sendmail.do";
		data = "";
		if ($("#agent-profile-name").val() != "") {
			data += "profilename=" + $("#agent-profile-name").val();
		}
		else if ($("#region-profile-name").val() != "") {
			data += "profilename=" + $("#region-profile-name").val();
		}
		else if ($("#branch-profile-name").val() != "") {
			data += "profilename=" + $("#branch-profile-name").val();
		}
		else if ($("#company-profile-name").val() != "") {
			data += "profilename=" + $("#company-profile-name").val();
		}

		data += "&companyprofilename=" + $("#company-profile-name").val();
		data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
		data += "&name=" + $('#lp-input-name').val();
		data += "&email=" + $('#lp-input-email').val();
		data += "&message=" + $('#lp-input-message').val();
		data += "&g-recaptcha-response=" + $('#g-recaptcha-response').val();
		//data += "&recaptcha_input=" + $('#captcha-text').val();
		showOverlay();
		callAjaxPostWithPayloadData(url, showMessage, data, true);
	}
});

function showMessage(data) {
	var jsonData = JSON.parse(data);
	if (jsonData["success"] == 1) {
		showInfoMobileAndWeb(jsonData["message"]);
		$(".reg-cap-reload").click();

		// resetting contact form and captcha
		$('#prof-contact-form')[0].reset();
		var recaptchaframe = $('.g-recaptcha iframe');
		var recaptchaSoure = recaptchaframe[0].src;
		recaptchaframe[0].src = '';
		setTimeout(function () { recaptchaframe[0].src = recaptchaSoure; }, 500);
	}
	else {
		showErrorMobileAndWeb(jsonData["message"]);
		$(".reg-cap-reload").click();
	}
}

function adjustImage() {
	var windW = getWindowWidth();
	if (windW < 768) {
		var imgW = $('#prof-image').width();
		$('#prof-image').height(imgW * 0.7);
		$('.footer-main-wrapper').hide();
	} else {
		$('.lp-con-row-item').width('auto');
		$('.footer-main-wrapper').show();
		//show all the containers
		$('#reviews-container, #prof-company-intro, #prof-agent-container').show();
		$('#recent-post-container, #ppl-post-cont, #contact-wrapper, #intro-about-me').show();
	}
}


function fetchCompanyProfile() {
	var result = profileJson;
	paintProfilePage(result);
	paintIndividualDetails(result);
	fetchCompanyRegions();
	fetchCompanyBranches();
	fetchCompanyIndividuals();
	minScore = 0;
	if (result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}

	if (showAllReviews == true) {
		fetchReviewsBasedOnProfileLevel('COMPANY', result.iden, startIndex, numOfRows, 0, true);
	}
	else {
		fetchReviewsBasedOnProfileLevel('COMPANY', result.iden, startIndex, numOfRows, minScore, true);
	}

	fetchReviewsCountBasedOnProfileLevel('COMPANY', result.iden, paintHiddenReviewsCount, 0, minScore, true);
	// Commented as zillow reviews are to be fetched after the last batch of social survey reviews fetched, SS-1277
	// fetchZillowReviewsBasedOnProfile('COMPANY',result.iden);
}

function paintProfilePage(result) {
	if (result != undefined && result != "") {
		currentProfileIden = result.iden;
		currentProfileName = result.profileName;
		var contactDetails = result.contact_details;
		var profileLevel = $("#profile-fetch-info").attr("profile-level");

		// paint public  posts
		//fetchPublicPosts(false);

		var breadCrumUrl = '/rest/breadcrumb/';

		if (profileLevel == 'INDIVIDUAL') {
			breadCrumUrl += 'individual/' + profileJson.iden;
		} else if (profileLevel == 'BRANCH') {
			breadCrumUrl += '/branch/' + profileJson.iden;
		} else if (profileLevel == 'REGION') {
			breadCrumUrl += '/region/' + profileJson.iden;
		}

		if (profileLevel != 'COMPANY') {
			paintBreadCrums(breadCrumUrl);
		} else {
			var htmlContent = '<a target="_blank" class="brd-crm brd-crm-link" href="'
				+ '/findcompany.do?verticalName='
				+ profileJson.vertical
				+ '">'
				+ profileJson.vertical + '</a>';
			$('#bread-crum-cont').html(htmlContent);
		}

		if (contactDetails != undefined) {
			var addressHtml = "";

			// Company profile address
			if (profileLevel == 'INDIVIDUAL') {
				var addressData = contactDetails;
				if (!addressData.address1 && result.companyProfileData) {
					addressData = result.companyProfileData;
				}

				if (addressData.address1 != undefined) {
					addressHtml += '<div class="prof-user-addline1">' + addressData.address1 + '</div>';
				}
				if (addressData.address2 != undefined) {
					addressHtml += '<div class="prof-user-addline2">' + addressData.address2 + '</div>';
				}
				if (addressData.zipcode != undefined || addressData.state != undefined || addressData.city != undefined) {
					addressHtml += '<div class="prof-user-addline2">';
					if (addressData.city && addressData.city != "") {
						addressHtml += addressData.city + ', ';
					}
					if (addressData.state && addressData.state != "") {
						addressHtml += addressData.state + ' ';
					}
					if (addressData.zipcode && addressData.zipcode != "") {
						addressHtml += addressData.zipcode;
					}
					addressHtml += '</div>';
				}
			} else {
				if (contactDetails.address1 != undefined) {
					addressHtml += '<div class="prof-user-addline1">' + contactDetails.address1 + '</div>';
				}
				if (contactDetails.address2 != undefined) {
					addressHtml += '<div class="prof-user-addline2">' + contactDetails.address2 + '</div>';
				}
				if (contactDetails.zipcode != undefined || contactDetails.state != undefined || contactDetails.city != undefined) {
					addressHtml += '<div class="prof-user-addline2">';
					if (contactDetails.city && contactDetails.city != "") {
						addressHtml += contactDetails.city + ', ';
					}
					if (contactDetails.state && contactDetails.state != "") {
						addressHtml += contactDetails.state + ' ';
					}
					if (contactDetails.zipcode && contactDetails.zipcode != "") {
						addressHtml += contactDetails.zipcode;
					}
					addressHtml += '</div>';
				}
			}
			$("#prof-company-address").html(addressHtml);

			if (result.logo == undefined) {
				var address;
				if (profileLevel == 'INDIVIDUAL') {
					var addressData = contactDetails;
					if (!addressData.address1 && result.companyProfileData) {
						addressData = result.companyProfileData;
					}

					address = '';
					if (result.companyProfileData && result.companyProfileData.name && result.companyProfileData.name != "") {
						address += result.companyProfileData.name;
					}
					if (addressData.address1 && addressData.address1 != "") {
						address += ' ' + addressData.address1;
					}
					if (addressData.address2 && addressData.address2 != "") {
						address += ' ' + addressData.address2;
					}
					if (addressData.country && addressData.country != "") {
						address += ' ' + addressData.country;
					}
					if (addressData.zipcode && addressData.zipcode != "") {
						address += ' ' + addressData.zipcode;
					}
				} else {
					address = contactDetails.name;

					if (contactDetails.address1 != undefined) {
						address += ' ' + contactDetails.address1;
					}
					if (contactDetails.address2 != undefined) {
						address += ' ' + contactDetails.address2;
					}
					if (contactDetails.country != undefined) {
						address += ' ' + contactDetails.country;
					}
					if (contactDetails.zipcode != undefined) {
						address += ' ' + contactDetails.zipcode;
					}
				}
				address = address.replace(/,/g, "");

				$("#prof-company-logo").html('<iframe src="https://maps.google.com/maps?hl=en&q=' + address + '&ie=UTF8&t=m&z=10&iwloc=B&output=embed"></iframe>');
			}
		}

		var dataLink = $('.web-address-link').attr('data-link');
		var link = returnValidWebAddress(dataLink);
		$('#web-address-txt').html('<a href="' + link + '" target="_blank">' + dataLink + '</a>');
		$('#web-addr-link-lp').html('<a href="' + link + '" target="_blank">Our Website</a>');

		$('.social-item-icon').bind('click', function () {
			var link = $(this).attr('data-link');
			if (link == undefined || link == "") {
				return false;
			}
			window.open(returnValidWebAddress(link), '_blank');
		});
	}
}



function paintBreadCrums(url) {

	callAjaxGETWithTextData(url, function (data) {
		var jsonData = $.parseJSON(data);
		if (jsonData.entity) {
			var entityJson = $.parseJSON(jsonData.entity);
			var htmlContent = '<a target="_blank" class="brd-crm brd-crm-link" href="'
				+ getLocationOrigin()
				+ '/findcompany.do?verticalName='
				+ entityJson[0].breadCrumbProfile
				+ '">'
				+ entityJson[0].breadCrumbProfile + '</a>';

			for (var i = 1; i < entityJson.length; i++) {
				//show entity only if not hidden from bredcrumb
				if (!entityJson[i].hideFromBreadCrumb) {
					htmlContent += '<span class="brd-crm-divider">&gt;&gt;</span>';
					htmlContent += '<a target="_blank" class="brd-crm brd-crm-link" href="'
						+ entityJson[i].breadCrumbUrl
						+ '">'
						+ entityJson[i].breadCrumbProfile + '</a>';
				}
			}

			$('#bread-crum-cont').html(htmlContent);
		}
	}, true, {});
}


function returnValidWebAddress(url) {
	if (url && !url.match(/^http([s]?):\/\/.*/)) {
		url = 'http://' + url;
	}
	return url;
}

function focusOnContact() {
	$('.inc-more').trigger('click');
	$('html, body').animate({
		scrollTop: $('#prof-contact-hdr').offset().top
	}, 1000);
	$('#prof-contact-form input:nth(0)').focus();
}

function fetchCompanyRegions() {
	var url = getLocationOrigin() + '/rest/profile/' + companyProfileName + '/regions';
	callAjaxGET(url, paintCompanyRegions, true);
}

function paintCompanyRegions(data) {
	var response = $.parseJSON(data);
	if (response != undefined) {
		var result = $.parseJSON(response.entity);
		if (result != undefined && result.length > 0) {
			var regionsHtml = "";
			$.each(result, function (i, region) {
				regionsHtml = regionsHtml + '<div class="bd-hr-item-l1 comp-region" data-start=0 data-batch=5 data-openstatus="closed" data-regionid = ' + region.regionId + '>';
				regionsHtml = regionsHtml + '	<div class="bd-hr-item bd-lt-l1 clearfix">';
				regionsHtml = regionsHtml + '    <div class="prf-public-txt bd-hr-txt cursor-pointer region-link" data-profilename="' + region.profileName + '">' + region.region + '</div>';
				regionsHtml = regionsHtml + '	</div>';
				regionsHtml = regionsHtml + '</div>';
				regionsHtml = regionsHtml + '	   <div class="bd-hr-item-l2 hide" id="comp-region-branches-' + region.regionId + '"></div>';
			});
			$("#comp-regions-content").html(regionsHtml);
			$("#comp-hierarchy").show();
		}
	}
}

function fetchBranchesForRegion(regionId) {
	var url = getLocationOrigin() + '/rest/profile/region/' + regionId + '/branches';
	$("#regionid-hidden").val(regionId);
	callAjaxGET(url, paintBranchesForRegion, true);
}

function paintBranchesForRegion(data) {
	var responseJson = $.parseJSON(data);
	var branchesHtml = "";
	var regionId = $("#regionid-hidden").val();
	if (responseJson != undefined) {
		var result = $.parseJSON(responseJson.entity);
		if (result != undefined && result.length > 0) {
			$.each(result, function (i, branch) {
				branchesHtml = branchesHtml + '<div class="bd-hr-item-l2 comp-region-branch" data-openstatus="closed" data-start=0 data-batch=5 data-branchid="' + branch.branchId + '">';
				branchesHtml = branchesHtml + '	<div class="bd-hr-item bd-lt-l2 clearfix">';
				branchesHtml = branchesHtml + '		<div class="prf-public-txt bd-hr-txt cursor-pointer branch-link" data-profilename="' + branch.profileName + '">' + branch.branch + '</div>';
				branchesHtml = branchesHtml + '	</div>';
				branchesHtml = branchesHtml + '</div>';
				branchesHtml = branchesHtml + '		<div class="bd-hr-item-l3 hide" id="comp-branch-individuals-' + branch.branchId + '"></div>';
			});

			$("#region-hierarchy").show();
			if ($("#region-branches").length > 0) {
				$("#region-branches").html(branchesHtml);
			}
			else {
				$("#comp-region-branches-" + regionId).html(branchesHtml).slideDown(200);
			}
		}
	}
}

/**
 * Method to bind the element whose class is provided to fetch individuals under that branch
 * @param bindingClass
 */
function bindClickToFetchBranchIndividuals(bindingClass) {
	$("." + bindingClass).unbind('click');
	$("." + bindingClass).click(function (e) {
		e.preventDefault();
		if ($(this).data("openstatus") == "closed") {
			fetchIndividualsForBranch($(this).data('branchid'));
			$(this).data("openstatus", "open");
		} else {
			$('#comp-branch-individuals-' + $(this).data('branchid')).slideUp(200);
			$(this).data("openstatus", "closed");
		}
	});
}

function fetchIndividualsForBranch(branchId) {
	var start = $('div[data-branchid="' + branchId + '"]').attr('data-start');
	var rows = $('div[data-branchid="' + branchId + '"]').attr('data-batch');
	var url = getLocationOrigin() + '/rest/profile/branch/' + branchId
		+ '/individuals?start=' + start + "&rows=" + rows;
	$("#branchid-hidden").val(branchId);
	callAjaxGET(url, paintIndividualForBranch, true);
}

function paintIndividualForBranch(data) {
	var responseJson = $.parseJSON(data);
	var individualsHtml = "";
	var branchId = parseInt($("#branchid-hidden").val());
	var batchSize = parseInt($('div[data-branchid="' + branchId + '"]').attr('data-batch'));
	var start = parseInt($('div[data-branchid="' + branchId + '"]').attr('data-start'));
	if (responseJson != undefined && responseJson.entity != "") {
		var result = $.parseJSON(responseJson.entity);
		if (result != undefined && result.length > 0) {
			$.each(result, function (i, individual) {
				if (individual.contact_details != undefined) {
					individualsHtml += '<div class="bd-hr-item-l3 comp-individual" data-agentid=' + individual.iden + '>';
					individualsHtml += '	<div class="bd-hr-item bd-lt-l3 clearfix">';
					if (individual.profileImageUrl != undefined && individual.profileImageUrl.trim() != "") {
						individualsHtml += '	<div class="float-left bd-hr-img  comp-individual-prof-image"><img class="hr-ind-img" src="' + individual.profileImageUrlThumbnail + '"/></div>';
					} else {
						individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"></div>';
					}
					individualsHtml += '		<div class="prf-public-txt bd-hr-txt cursor-pointer individual-link" data-profilename="'
						+ individual.profileName + '">' + individual.contact_details.name + '</div>';
					individualsHtml += '	</div>';
					individualsHtml += '</div>';
				}
			});

			var showMoreHtml = "";
			$('div[data-branchid="' + branchId + '"]').attr('data-start', parseInt(start + result.length));
			if (start == 0 && result.length == batchSize) {
				showMoreHtml = '<div class="show-more-btn">Show More</div>';
			} else if (start != 0 && result.length < batchSize) {
				if ($("#branch-individuals").length > 0) {
					$("#branch-individuals").find(".show-more-btn").remove();
				} else {
					$("#comp-branch-individuals-" + branchId).find(".show-more-btn").remove();
				}
			}

			$("#branch-hierarchy").show();
			if ($("#branch-individuals").length > 0) {
				if ($("#branch-individuals").children(".show-more-btn").length > 0) {
					$("#branch-individuals").find(".show-more-btn").before(individualsHtml);
				} else {
					$("#branch-individuals").append(individualsHtml);
				}
				if (showMoreHtml != "") {
					$("#branch-individuals").append(showMoreHtml);
				}
			}
			else {
				if (start == 0) {
					$("#comp-branch-individuals-" + branchId).html(individualsHtml).slideDown(200);
				} else {
					if ($("#comp-branch-individuals-" + branchId).children(".show-more-btn").length > 0) {
						$("#comp-branch-individuals-" + branchId).find(".show-more-btn").before(individualsHtml);
					} else {
						$("#comp-branch-individuals-" + branchId).append(individualsHtml);
					}

				}

				if (showMoreHtml != "") {
					$("#comp-branch-individuals-" + branchId).append(showMoreHtml);
				}
			}
		}
	} else {
		if ($("#branch-individuals").length > 0) {
			$("#branch-individuals").find(".show-more-btn").remove();
		} else {
			$("#comp-branch-individuals-" + branchId).find(".show-more-btn").remove();
		}
	}
}

// Attach onclick event on show more button
$(document).on('click', '.show-more-btn', function (e) {
	var branchId = $(this).parent().prev('div').attr('data-branchid');
	fetchIndividualsForBranch(branchId);
});

function bindClickToFetchIndividualProfile(bindingClass) {
	$("." + bindingClass).click(function (e) {
		e.stopPropagation();
		var agentProfileName = $(this).data("profilename");
		var url = getLocationOrigin() + "/pages/" + agentProfileName;
		window.open(url, "_blank");
	});
}

function fetchIndividualsForRegion(regionId) {
	var start = $('.comp-region[data-regionid="' + regionId + '"]').attr(
		'data-start');
	var rows = $('.comp-region[data-regionid="' + regionId + '"]').attr(
		'data-batch');
	var url = getLocationOrigin() + '/rest/profile/region/' + regionId
		+ '/individuals?start=' + start + "&rows=" + rows;
	$("#regionid-hidden").val(regionId);
	callAjaxGET(url, paintIndividualsForRegion, true);
}

function paintIndividualsForRegion(data) {
	var responseJson = $.parseJSON(data);
	var individualsHtml = "";
	var regionId = $("#regionid-hidden").val();
	if (responseJson != undefined && responseJson.entity != "") {
		var result = $.parseJSON(responseJson.entity);
		if (result != undefined && result.length > 0) {
			$.each(result, function (i, individual) {
				if (individual.contact_details != undefined) {
					individualsHtml += '<div class="bd-hr-item-l2 comp-region-individual" data-agentid=' + individual.iden + '>';
					individualsHtml += '	<div class="bd-hr-item bd-lt-l3 clearfix">';
					if (individual.profileImageUrl != undefined && individual.profileImageUrl.trim() != "") {
						individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"><img class="hr-ind-img" src="' + individual.profileImageUrlThumbnail + '"/></div>';
					} else {
						individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"></div>';
					}
					individualsHtml += '	<div class="prf-public-txt bd-hr-txt cursor-pointer individual-link" data-profilename="'
						+ individual.profileName + '">' + individual.contact_details.name + '</div>';
					individualsHtml += '	</div>';
					individualsHtml += '</div>';
				}
			});
			$("#region-hierarchy").show();
			if ($("#region-branches").length > 0) {
				$("#region-branches").append(individualsHtml);
			}
			else {
				$("#comp-region-branches-" + regionId).append(individualsHtml).slideDown(200);
			}
		}
	}
}

function fetchCompanyIndividuals() {
	var url = getLocationOrigin() + '/rest/profile/' + companyProfileName + '/individuals';
	callAjaxGET(url, paintCompanyIndividuals, true);
}

function paintCompanyIndividuals(data) {
	var response = $.parseJSON(data);
	if (response != undefined && response.entity != "") {
		var result = $.parseJSON(response.entity);
		if (result != undefined && result.length > 0) {
			var compIndividualsHtml = "";
			$.each(result, function (i, compIndividual) {
				if (compIndividual.contact_details != undefined) {
					compIndividualsHtml += '<div class="bd-hr-item-l1 comp-individual" data-agentid=' + compIndividual.iden + '>';
					compIndividualsHtml += '	<div class="bd-hr-item bd-lt-l3 clearfix">';

					if (compIndividual.profileImageUrl != undefined && compIndividual.profileImageUrl.trim() != "") {
						compIndividualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"><img class="hr-ind-img" src="' + compIndividual.profileImageUrlThumbnail + '"/></div>';
					} else {
						compIndividualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"></div>';
					}
					compIndividualsHtml += '		<div class="prf-public-txt bd-hr-txt cursor-pointer individual-link" data-profilename="'
						+ compIndividual.profileName + '">' + compIndividual.contact_details.name + '</div>';
					compIndividualsHtml += '	</div>';
					compIndividualsHtml += '</div>';
				}
			});
			$("#comp-regions-content").append(compIndividualsHtml);
			$("#comp-hierarchy").show();
		}
	}
}

function fetchCompanyBranches() {
	var url = getLocationOrigin() + '/rest/profile/' + companyProfileName + '/branches';
	callAjaxGET(url, paintCompanyBranches, true);
}

function paintCompanyBranches(data) {
	var response = $.parseJSON(data);
	if (response != undefined) {
		var result = $.parseJSON(response.entity);
		if (result != undefined && result.length > 0) {
			var compBranchesHtml = "";
			$.each(result, function (i, branch) {
				compBranchesHtml = compBranchesHtml + '<div class="bd-hr-item-l1 comp-branch" data-start=0 data-batch=5 data-openstatus="closed" data-branchid="' + branch.branchId + '">';
				compBranchesHtml = compBranchesHtml + '	<div class="bd-hr-item bd-lt-l2 clearfix">';
				compBranchesHtml = compBranchesHtml + '		<div class="prf-public-txt bd-hr-txt cursor-pointer branch-link" data-profilename="' + branch.profileName + '">' + branch.branch + '</div>';
				compBranchesHtml = compBranchesHtml + '	</div>';
				compBranchesHtml = compBranchesHtml + '</div>';
				compBranchesHtml = compBranchesHtml + '		<div class="lpsub-2 hide" id="comp-branch-individuals-' + branch.branchId + '"></div>';
			});
			$("#comp-hierarchy").show();
			$("#comp-regions-content").append(compBranchesHtml);
		}
	}
}




function paintReviews(result) {

	//Check if there are more reviews left
	if (reviewsNextBatch == undefined || reviewsNextBatch.length <= numOfRows)
		fetchReviewsScroll(true);
	var profileLevel = $("#profile-fetch-info").attr('profile-level');
	var resultSize = result.length;
	$('.ppl-review-item-last').removeClass('ppl-review-item-last').addClass('ppl-review-item');
	var profileUrl = window.location.href;
	var reviewsHtml = "";
	$.each(result, function (i, reviewItem) {
		var scoreFixVal = 1;
		var date = Date.parse(reviewItem.modifiedOn);
		if (reviewItem.source == "Zillow") {
			date = Date.parse(reviewItem.createdOn);
		}
		profileUrl = buildPublicProfileUrl();
		profileUrl = profileUrl + "/" + reviewItem._id;
		var lastItemClass = "ppl-review-item";
		if (i == resultSize - 1) {
			lastItemClass = "ppl-review-item-last";
		}
		/*var custName = reviewItem.customerFirstName.trim();
		if(reviewItem.customerLastName != undefined){
			custName += ' ' + reviewItem.customerLastName.trim();
		}
		custName = custName || "";
		var custNameArray = custName.split(' ');
		var custDispName = custNameArray[0];
		if(custNameArray[1] != undefined && custNameArray[1].trim() != ""){
			custDispName += ' '+custNameArray[1].substr(0,1).toUpperCase()+'.';
		}*/

		var custName = reviewItem.customerFirstName.trim();

		if (reviewItem.customerLastName != undefined && reviewItem.customerLastName.trim() != "") {
			custName = custName + ' ' + reviewItem.customerLastName.trim();
		}
		var custArray = custName.split(" ");
		var custDispName = custArray[0] + ((custArray[1] != undefined && custArray[1].trim() != "") ? " " + custArray[1].substr(0, 1).toUpperCase() : "");

		reviewsHtml = reviewsHtml +
			'<div class="' + lastItemClass + ' cursor-pointer"' + ' data-rating=' + reviewItem.score + ' data-review="' + escapeHtml(reviewItem.review) + '" data-agentid="' + reviewItem.agentId + '" survey-mongo-id="' + reviewItem._id + '">';
		if(reviewItem.profileImageUrl !=null && reviewItem.profileImageUrl != ""){
		    reviewsHtml +='<div class="ss-reviewer-prof-pic-cont"> <img class="ss-reviewer-prof-pic" src="'+ reviewItem.profileImageUrl +'"></div>';
		}else{
			reviewsHtml +='<div class="ss-reviewer-prof-pic-cont"> <img class="ss-reviewer-prof-pic" src="'+window.location.origin+'/widget/images/person.png"></div>';
		}
		reviewsHtml += '	<div class="ppl-header-wrapper clearfix review-details">';
		reviewsHtml += '    	<div class="float-left ppl-header-right">';
		reviewsHtml += '    	    <div class="st-rating-wrapper maring-0 clearfix review-ratings" data-source="' + reviewItem.source + '" data-rating="' + reviewItem.score + '"></div>';
		reviewsHtml += '		</div>';

		if (reviewItem.source == "verifiedPartner") {
			reviewsHtml += ' <div class="verified-partner-badge  verify-partner-image float-right" title="Click here to know more"></div>';
		} else if (reviewItem.source == "encompass" || reviewItem.source == "DOTLOOP" || reviewItem.source == "API" || reviewItem.source == "FTP" || reviewItem.source == "LONEWOLF") {
			reviewsHtml += ' <div class="verified-badge  verify-image float-right" title="Click here to know more"></div>';
		}
		else if (reviewItem.source == "Zillow") {
			reviewsHtml += ' <div class="zillow-badge  verify-image-zillow float-right" ></div>';
		} else if (reviewItem.source == 'facebook') {
			reviewsHtml += ' <div class="fb-verified-image verify-image-fb float-right" ></div>';
		} else if (reviewItem.source == 'google') {
			reviewsHtml += ' <div class="google-verified-image verify-image-google float-right"></div>';
		} else {
			reviewsHtml += '<div class="unverified-badge  verify-image-ss float-right"></div>'
		}
		reviewsHtml += '		<div class=" ppl-header-left review-detail-profile review-sm-screen" >';
		if (reviewItem.surveyUpdatedDate != null && reviewItem.surveyUpdatedDate != reviewItem.surveyCompletedDate) {
			reviewsHtml += '<div class="ppl-head-2 review-detail-profile float-left"> Survey updated on </div><div class="ppl-head-2 float-left" style="margin-left: 5px;">' + new Date(reviewItem.surveyUpdatedDate).toString("MMMM d, yyyy") + '</div>'
		} else if (reviewItem.surveyCompletedDate != null) {
			reviewsHtml += '<div class="ppl-head-2 review-detail-profile float-left"> Survey completed on </div><div class="ppl-head-2 float-left" style="margin-left: 5px;">' + new Date(reviewItem.surveyCompletedDate).toString("MMMM d, yyyy") + '</div>'
		} else {
			reviewsHtml += '<div class="ppl-head-2 review-detail-profile float-left"> Survey completed on </div><div class="ppl-head-2 float-left" style="margin-left: 5px;">' + new Date(reviewItem.modifiedOn).toString("MMMM d, yyyy") + '</div>'
		}


		reviewsHtml += '			<div class="ppl-head-1 float-left " style="clear:both"><span class="float-left"> Reviewed by<span style="font-weight:600 !important;"> ' + custDispName + '.</span></span>';
		if (profileLevel != 'INDIVIDUAL' && reviewItem.agentName != null && hiddenSection == "false") {
			reviewsHtml += '<span class="float-left" style="margin-left:5px;"> for<a style="color:#236CAF;font-weight: 600 !important;" href="' + reviewItem.completeProfileUrl + '"> ' + reviewItem.agentName + '</a></span>';
		}
		if (profileLevel != 'INDIVIDUAL' && reviewItem.agentName != null && hiddenSection == "true") {
			var agentNameTokens = reviewItem.agentName.split(" ");
			var agentName = agentNameTokens[0];
			if (agentNameTokens[1] != null) {
				agentName = agentName + " " + (agentNameTokens[1]).substring(0, 1);
			}
			reviewsHtml += '<span class="float-left" style="margin-left:5px;"> for ' + agentName + '</span>';
		}
		if (date != null) {
			date = convertUserDateToLocale(date);
			reviewsHtml += '	<span class="float-left" style="margin: 0 5px;"></span>	';
		}


		if (reviewItem.summary != null) {
			reviewsHtml += '<div class="ppl-content" style="clear:both;padding-top:0px !important;">' + reviewItem.summary + '</div>';
		} else {
			if (reviewItem.surveyGeoLocation != null && reviewItem.surveyType) {
				reviewsHtml += '<div class="ppl-content" style="clear:both;padding-top:0px !important;">' + reviewItem.surveyGeoLocation + '<span>' + reviewItem.surveyType + '</span></div>';

			} else {
				if (reviewItem.source != "customer") {
					reviewsHtml += '<div style="clear:both">Completed transaction in';
					if (reviewItem.surveyTransactionDate != null) {
						reviewsHtml += ' <span>' + new Date(reviewItem.surveyTransactionDate).toString("MMMM  yyyy") + '</span>';
					} else {
						reviewsHtml += ' <span>' + new Date(reviewItem.modifiedOn).toString("MMMM  yyyy") + '</span>';
					}

					if (reviewItem.city && reviewItem.state) {
						reviewsHtml += '<span> in ' + reviewItem.city + ', ' + reviewItem.state + '.</span>';
					}
					reviewsHtml += '</div>';
				}
			}
		}
		reviewsHtml += '</div>';
		reviewsHtml += '		</div>';
		reviewsHtml += '	</div>';

		/*<c:if test="${reviewItem.source =='encompass'}">
	<div class='verified-badge  verify-image float-right' title='Click here to know more'></div>
	</c:if>
	<c:if test="${reviewItem.source =='DOTLOOP'}">
	<div class='verified-badge  verify-image float-right' title='Click here to know more'></div>
	</c:if>
		*/

		/*reviewsHtml += '	</div>';
		
		reviewsHtml += '	<div class="ppl-share-wrapper clearfix share-plus-height">';
		reviewsHtml += '		<div class="float-left blue-text ppl-share-shr-txt">Share</div>';
		reviewsHtml += '		<div class="float-left icn-share icn-plus-open"></div>';
		reviewsHtml += '		<div class="float-left clearfix ppl-share-social hide">';
		reviewsHtml += '			<span id ="fb_' + i + '"class="float-left ppl-share-icns icn-fb icn-fb-pp" onclick="getImageandCaption(' + i + ');" title="Facebook" data-link="https://www.facebook.com/dialog/feed?' + reviewItem.faceBookShareUrl + '&link=' +profileUrl.replace("localhost","127.0.0.1")+ '&description=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + ' .&redirect_uri=https://www.facebook.com"></span>';
		reviewsHtml += '            <input type="hidden" id="twttxt_' + i + '" class ="twitterText_loop" value ="' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '"/></input>';
		reviewsHtml += '			<span id ="twitt_' + i + '" class="float-left ppl-share-icns icn-twit icn-twit-pp" onclick="twitterFn(' + i + ');" title="Twitter" data-link="https://twitter.com/intent/tweet?text=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + ' &url='+ profileUrl +'"></span>';	
		reviewsHtml += '			<span class="float-left ppl-share-icns icn-lin icn-lin-pp" title="LinkedIn" data-link="https://www.linkedin.com/shareArticle?mini=true&url=' + profileUrl + '/' + reviewItem._id + '&title=&summary=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) +' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '&source="></span>';
		reviewsHtml += '		</div>';
		reviewsHtml += '		<div class="float-right" style="margin: 0 -5px;">';
		if(reviewItem.source != "Zillow")
			reviewsHtml += '			<div class="report-abuse-txt report-txt prof-report-abuse-txt">Report Abuse</div>';
		reviewsHtml += '		</div>';
		reviewsHtml += '		<div class="float-left icn-share icn-remove icn-rem-size hide"></div>';
		reviewsHtml += '	</div>';
		reviewsHtml += '</div>';*/


		var review = escapeHtml(reviewItem.review);

		if (review.length > 250) {
			reviewsHtml += '<div class="ppl-content review-height"><span class="review-complete-txt">' + reviewItem.review + '';
			if (reviewItem.source == "Zillow") {
				reviewsHtml += '<br><a class="view-zillow-link" href="' + reviewItem.sourceId + '"  target="_blank">View on zillow</a></span>';
			} else if (reviewItem.source == "facebook") {
				reviewsHtml += '<br><a class="view-fb-link" href="' + reviewItem.facebookProfileUrl + '"  target="_blank">View on Facebook</a></span>';
			} else if (reviewItem.source == 'google') {
				reviewsHtml += '<br><a class="view-goo-link" href="' + reviewItem.googleBusinessProfileUrl + '"  target="_blank">View on google</a></span>';
			} else {
				reviewsHtml += '</span>';
			}
			reviewsHtml += '<span class="review-less-text">' + review.substr(0, 250) + '</span><span class="review-more-button">read full review</span>';

			if (reviewItem['reviewReply']) {
				// reviewsHtml += `<div class="review-reply-section">
				// 				<h5><span><b>Replies</b></span></h5>							
				// 				<div class="review-reply-container">`;
				reviewsHtml += `<div class="review-reply-section">							
								<div class="review-reply-container">`;

				reviewItem.reviewReply.forEach(function (v) {
					reviewsHtml += `<div class="review-reply-box" data-reply-id="${v.replyId}">
										<div class="review-reply-box-container">
											<span class="review-reply-owner"><b>${v.replyByName}</b></span>
											<span class="review-reply-text">${v.replyText}</span>
										</div>
									</div>`;
				});
				reviewsHtml += '</div></div>';
			}

		} else {
			reviewsHtml += '<div class="ppl-content review-height"><span>' + review + '</span>';

			if (reviewItem['reviewReply']) {
				// reviewsHtml += `<div class="review-reply-section">
				// 				<h5><span><b>Replies</b></span></h5>							
				// 				<div class="review-reply-container">`;
				reviewsHtml += `<div class="review-reply-section">							
								<div class="review-reply-container">`;

				reviewItem.reviewReply.forEach(function (v) {
					reviewsHtml += `<div class="review-reply-box" data-reply-id="${v.replyId}">
										<div class="review-reply-box-container">
											<span class="review-reply-owner"><b>${v.replyByName}</b></span>
											<span class="review-reply-text">${v.replyText}</span>
										</div>
									</div>`;
				});
				reviewsHtml += '</div></div>';
			}

			if (reviewItem.source == "Zillow") {
				reviewsHtml += '<br><a class="view-zillow-link" href="' + reviewItem.sourceId + '"  target="_blank">View on zillow</a></span>';
			} else if (reviewItem.source == "facebook") {
				reviewsHtml += '<br><a class="view-fb-link" href="' + reviewItem.facebookProfileUrl + '"  target="_blank">View on Facebook</a></span>';
			} else if (reviewItem.source == 'google') {
				reviewsHtml += '<br><a class="view-goo-link" href="' + reviewItem.googleBusinessProfileUrl + '"  target="_blank">View on google</a></span>';
			}
		}

		if (reviewItem.agentName == undefined || reviewItem.agentName == null)
			reviewItem.agentName = "us";

		var reviewSummary = reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review);
		reviewsHtml += '	</div>';
		reviewsHtml += '	<div class="ppl-share-wrapper clearfix share-plus-height" >';
		reviewsHtml += '		<div class="float-left clearfix ppl-share-social ">';
		reviewsHtml += '			<span id ="fb_' + i + '"class="float-left ppl-share-icns icn-fb-rev icn-fb-pp" title="Facebook" data-link="https://www.facebook.com/dialog/share?' + reviewItem.faceBookShareUrl + '&href=' + profileUrl.replace("localhost", "127.0.0.1") + '&quote=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '&redirect_uri=https://www.facebook.com"></span>';
		reviewsHtml += '            <input type="hidden" id="twttxt_' + i + '" class ="twitterText_loop" value ="' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '"/></input>';
		reviewsHtml += '			<span id ="twitt_' + i + '" class="float-left ppl-share-icns icn-twit-rev icn-twit-pp" onclick="twitterFn(' + i + ');" title="Twitter" data-link="https://twitter.com/intent/tweet?text=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + ' &url='+ profileUrl +'"></span>';	
		reviewsHtml += '			<input type="hidden" class="linkedInSummary" value="'+reviewSummary+'" >'
		reviewsHtml += '			<span class="float-left ppl-share-icns icn-lin-rev icn-lin-pp" title="LinkedIn" data-summary="'+reviewSummary+'" data-link="https://www.linkedin.com/shareArticle?mini=true&url=' + profileUrl + '&title=&summary=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) +' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '&reviewid=' + reviewItem._id + '&source="></span>';
		reviewsHtml += '			<span class="float-left ppl-share-icns permalink icn-permalink-rev" title="Permalink" onclick="copyIndividualReviewUrlToClipboard(' + processedPermalink + ')"><input id="permalink_url_' + processedPermalink + '" type="hidden" value="' + profileUrl + '"/></span>';
		reviewsHtml += '		</div>';
		
		var profileLevel = $("#profile-fetch-info").attr("profile-level");
		var curProfIden = parseInt(currentProfileIden);
		if(!(profileLevel == 'INDIVIDUAL' && curProfIden == 26047)){
			if (reviewItem.source != "Zillow")
				reviewsHtml += '		<span class="icn-flag float-right report-abuse-txt prof-report-abuse-txt cursor-pointer  " title="Report Abuse"></span> ';
		}
				
		reviewsHtml += '	</div>';

		processedPermalink++;

		/*if(reviewItem.summary != null && reviewItem.summary.length > 0){
			reviewsHtml += '<div class="ppl-content">'+reviewItem.summary+'</div>';
		}*/



		/*if(reviewItem.source == "Zillow") {
			reviewsHtml += '<br><a class="view-zillow-link" href="'+reviewItem.sourceId+'"  target="_blank">View on zillow</a>';
		}*/
		if (reviewItem.customerLastName != null && reviewItem.customerLastName != "")
			reviewItem.customerLastName = reviewItem.customerLastName.substring(0, 1).toUpperCase() + ".";
		else
			reviewItem.customerLastName = "";


		reviewsHtml += '</div>';
	});
	gplusInvoke();
	if (result.length > 0) {
		$('#reviews-container').show();
	}

	hideLoaderOnPagination($('#prof-review-item'));
	/*if($("#profile-fetch-info").attr("fetch-all-reviews") == "true" && startIndex == 0) {
		$("#prof-review-item").html('');
	}*/

	$("#prof-review-item").append(reviewsHtml);

	$("#prof-reviews-header").parent().show();
	$(".review-ratings").each(function () {
		changeRatingPattern($(this).data("rating"), $(this), false, $(this).data("source"));
	});

	setTimeout(function () {
		$(window).trigger('scroll');
	}, 100);
}

function stringEscape(str) {

	if (str == undefined || str == null) {
		return "";
	}
	var textStr = str.toString();
	return textStr.replace(new RegExp("'", 'g'), "`").replace(new RegExp('"', 'g'), "`");
}

//invokes the google plus js that binds the click events to the popup
function gplusInvoke() {
	$('.g-interactivepost').on('click', function (e) {
		e.stopPropagation();
		var post = $(this).attr('data-prefilltext');
		$(this).attr('data-prefilltext', decodeURIComponent(post));
	});
	var po = document.createElement('script'); po.type = 'text/javascript';
	po.async = true;
	po.src = 'https://apis.google.com/js/client:plusone.js';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(po, s);
};

/*$(document).on('mouseover','.ppl-review-item ',function(e){
	$(this).find('.ppl-share-wrapper').css('visibility','visible');
});
$(document).on('mouseleave','.ppl-review-item ',function(e){
	$(this).find('.ppl-share-wrapper').css('visibility','hidden');;
});*/
/*$(document).on('mouseover','.ppl-review-item-last ',function(e){
	$(this).find('.ppl-share-wrapper').css('visibility','visible');
});
$(document).on('mouseleave','.ppl-review-item-last',function(e){
	$(this).find('.ppl-share-wrapper').css('visibility','hidden');
});*/


$(document).on('click', '.review-more-button', function (e) {
	e.stopPropagation();
	$(this).parent().find('.review-less-text').hide();
	$(this).parent().find('.review-complete-txt').show();
	$(this).hide();
});

$(document).on('click', '#report-abuse-pop-up', function (e) {
	e.stopPropagation();
});

// Report abuse click event.
$(document).on('click', '.prof-report-abuse-txt', function (e) {
	e.stopPropagation();
	var reviewElement = $(this).parent().parent();
	var payload = {
		"surveyMongoId": reviewElement.attr('survey-mongo-id')
	};

	$("#report-abuse-txtbox").val('');
	$('#report-abuse-cus-name').val('');
	$('#report-abuse-cus-email').val('');

	// Unbind click events for button
	$('.rpa-cancel-btn').off('click');
	$('.rpa-report-btn').off('click');

	$('#report-abuse-overlay').show();
	$('.rpa-cancel-btn').on('click', function () {
		$('#report-abuse-overlay').hide();
	});
	$('.rpa-report-btn').on('click', function () {
		var reportText = $("#report-abuse-txtbox").val();
		var cusName = $('#report-abuse-cus-name').val();
		var cusEmail = $('#report-abuse-cus-email').val();

		if (validateReportAbuseForm(reportText, cusName, cusEmail)) {
			showOverlay();
			payload.reportText = reportText;
			payload.reporterName = cusName;
			payload.reporterEmail = cusEmail;
			confirmReportAbuse(payload);
		}
	});
});

$(document).on('click', '.sr-prof-report-abuse-txt', function (e) {
	e.stopPropagation();
	var reviewElement = $('#sr-review-info');
	var payload = {
		"customerEmail": reviewElement.attr('data-customeremail'),
		"agentId": reviewElement.attr('data-agentid'),
		"firstName": reviewElement.attr('data-cust-first-name'),
		"lastName": reviewElement.attr('data-cust-last-name'),
		"agentName": reviewElement.attr('data-agent-name'),
		"review": reviewElement.attr('data-review'),
		"surveyMongoId": reviewElement.attr('data-survey-mongo-id')
	};

	$("#report-abuse-txtbox").val('');
	$('#report-abuse-cus-name').val('');
	$('#report-abuse-cus-email').val('');

	// Unbind click events for button
	$('.rpa-cancel-btn').off('click');
	$('.rpa-report-btn').off('click');

	$('#report-abuse-overlay').show();
	$('.rpa-cancel-btn').on('click', function () {
		$('#report-abuse-overlay').hide();
	});
	$('.rpa-report-btn').on('click', function () {
		var reportText = $("#report-abuse-txtbox").val();
		var cusName = $('#report-abuse-cus-name').val();
		var cusEmail = $('#report-abuse-cus-email').val();

		if (validateReportAbuseForm(reportText, cusName, cusEmail)) {
			showOverlay();
			payload.reportText = reportText;
			payload.reporterName = cusName;
			payload.reporterEmail = cusEmail;
			confirmReportAbuse(payload);
		}
	});
});

$(document).on('click', function (e) {

	if ($('#report-abuse-overlay').is(':visible')) {
		$('#report-abuse-overlay').hide();
		enableBodyScroll();
	}

});

$(document).on('keyup', function (e) {
	if (e.keyCode == 27) {

		if ($('#report-abuse-overlay').is(':visible')) {
			$('#report-abuse-overlay').hide();
			enableBodyScroll();
		}

	}
});

function validateReportAbuseForm(reportText, cusName, cusEmail) {
	// check if custname is empty
	if (cusName == undefined || cusName == "") {
		$('#overlay-toast').html('Please enter valid name!');
		showToast();
		return false;
	}

	// check if custemail is valid
	if (cusEmail == undefined || cusEmail == "" || !emailRegex.test(cusEmail)) {
		$('#overlay-toast').html('Please enter a valid email address!');
		showToast();
		return false;
	}

	// check if report text is empty
	if (reportText == undefined || reportText == "") {
		$('#overlay-toast').html('Please enter why you want to report the review!');
		showToast();
		return false;
	}

	return true;
}

function confirmReportAbuse(payload) {
	callAjaxGetWithPayloadData('/rest/profile/surveyreportabuse', function (status) {
		$('#report-abuse-overlay').hide();

		if (status == 'success') {
			$('#overlay-toast').html('Reported Successfully!');
		} else {
			$('#overlay-toast').html('Failed to report abuse, Please try again later');
		}
		hideOverlay();
		showToast();
	}, payload, true);
}

//window scroll event to listen for reviews scroll
$(window).scroll(function () {
	//check if small screen
	if (getWindowWidth() < 768) {
		//check if reviews are visible. If not, do not proceed
		if (!$('#prof-review-item').is(':visible')) {
			return false;
		}
	}
	if ((window.innerHeight + window.pageYOffset) >= ($('#prof-review-item').offset().top + $('#prof-review-item').height() * 0.75)) {
		if (!isLoaderRunningReviews)
			fetchReviewsScroll(false);
	}
});

/**
 * Function to fetch the reviews on scroll 
 * @param isNextBatch 
 */
function fetchReviewsScroll(isNextBatch) {
	//if pagination is stopped or next batch is not empty
	if (!stopFetchReviewPagination || reviewsNextBatch.length > 0) {
		if (!isNextBatch && reviewsNextBatch != undefined && reviewsNextBatch.length > 0) {
			var reviewsToShow = reviewsNextBatch.slice(0, numOfRows);
			if (reviewsNextBatch.length > numOfRows) {
				reviewsNextBatch = reviewsNextBatch.slice(numOfRows);
			} else {
				reviewsNextBatch = [];
			}
			isLoaderRunningReviews = true;
			showLoaderOnPagination($('#prof-review-item'));
			setTimeout(function () {
				paintReviews(reviewsToShow);
				isLoaderRunningReviews = false;
			}, 500);
		} else {
			if (isFetchReviewAjaxRequestRunning) {
				if (!isNextBatch) {
					setTimeout(function () {
						fetchReviewsScroll(true); //keep checking until the reviews arrives
					}, 200);
				}
				return; // Return if ajax request is still running and not next batch of reviews
			}
			if (stopFetchReviewPagination) return; //Return if pagination is stopped
			startIndex = startIndex + numOfRows;
			var profileLevel = $("#profile-fetch-info").attr("profile-level");
			if (showAllReviews)
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden,
					startIndex, numOfRows, 0, true, isNextBatch);
			else
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden,
					startIndex, numOfRows, minScore, true, isNextBatch);
		}
	} else {
		// fetch zillow reviews
		var profileLevel = $("#profile-fetch-info").attr("profile-level");
		//Zillow fix : Added check to see if social media tokens exist
		if (doFetchZillowReviews && profileJson.socialMediaTokens != undefined
			&& profileJson.socialMediaTokens.zillowToken != undefined)
			fetchZillowReviewsBasedOnProfile(profileLevel, currentProfileIden, isNextBatch);
		else if (doFetchHeirarchyIds && !doStopZillowIdFetch) {
			doFetchHeirarchyIds = false;
			fetchHeirarchyIdsConectedToZillow(profileLevel, currentProfileIden, isNextBatch);
		}
		else if (zillowHierarchyList != undefined) {
			fetchZillowReviewsFromZillowHierarchyMap(profileLevel, currentProfileIden, isNextBatch);
		}
		doFetchZillowReviews = false;
	}
}

function fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden,
	startIndex, numRows, minScore, isAsync, isNextBatch) {

	if (startIndex == 0) {
		stopFetchReviewPagination = false;
		zillowHierarchyList = [];
		zillowHStart = 0;
		zillowHBatchSize = 10;
		curHierarchyLevel = "";
		doStopZillowIdFetch = false;
		doFetchHeirarchyIds = true;
		reviewsNextBatch = [];
		$("#prof-review-item").html('');
	}

	if (currentProfileIden == undefined || currentProfileIden == "") {
		return;
	}
	var url = "/rest/profile/";
	if (profileLevel == 'COMPANY') {
		url += "company/";
	} else if (profileLevel == 'REGION') {
		url += "region/";
	} else if (profileLevel == 'BRANCH') {
		url += "branch/";
	} else if (profileLevel == 'INDIVIDUAL') {
		url += "individual/";
	}
	url += currentProfileIden + "/reviews?start=" + startIndex + "&numRows="
		+ numRows + "&sortCriteria=" + reviewsSortBy;
	if (minScore != undefined) {
		url = url + "&minScore=" + minScore;
	}
	isFetchReviewAjaxRequestRunning = true;
	callAjaxGET(url, function (data) {
		isFetchReviewAjaxRequestRunning = false;
		var responseJson = $.parseJSON(data);
		if (responseJson != undefined) {
			var result = $.parseJSON(responseJson.entity);
			if (result == undefined || result.length < numRows) {
				stopFetchReviewPagination = true; //Stop pagination if reviews fetch are less than the batch size
				//Get zillow reviews if zillow is connected if not get the zillow ids connected in the lower hierarchy
				fetchReviewsScroll(isNextBatch);
			}
			if (result != undefined && result.length > 0) {
				reviewsNextBatch = reviewsNextBatch.concat(result);
				if (isNextBatch) {
					if (reviewsNextBatch.length <= numRows) {
						fetchReviewsScroll(true);
					}
				} else {
					fetchReviewsScroll(false);
				}
			} else {
				if ($("#profile-fetch-info").attr("fetch-all-reviews") == "false"
					&& parseInt($('#prof-hidden-review-count').attr("data-nr-review-count")) > 0) {
					$("#prof-hidden-review-count").show();
				}
			}
		}
	}, isAsync);

}

function fetchZillowReviewsBasedOnProfile(profileLevel, currentProfileIden, isNextBatch) {
	if (currentProfileIden == undefined || currentProfileIden == "" || isZillowReviewsCallRunning) {
		return; //Return if profile id is undefined
	}
	var url = "/rest/profile/";
	if (profileLevel == 'COMPANY') {
		// url += "company/";
		return;
	} else if (profileLevel == 'REGION') {
		// url += "region/";
		return;
	} else if (profileLevel == 'BRANCH') {
		// url += "branch/";
		return;
	} else if (profileLevel == 'INDIVIDUAL') {
		// url += "individual/";
		return;
	}
	url += currentProfileIden + "/zillowreviews";
	isZillowReviewsCallRunning = true;
	callAjaxGET(url, function (data) {
		isZillowReviewsCallRunning = false;
		if (data != undefined && data != "") {
			var responseJson = $.parseJSON(data);
			if (responseJson != undefined) {
				var result = $.parseJSON(responseJson.entity);
				zillowCallBreak = result.zillowCallBreak;
				if (!zillowCallBreak) {
					stopFetchReviewPagination = true; //Stop pagination as zillow reviews are fetch one shot
					if (result != undefined && result.length > 0) {
						reviewsNextBatch = reviewsNextBatch.concat(result);
					}
					if (!isNextBatch)
						fetchReviewsScroll(false);
				}
			}
		}
	}, true);
	if (profileLevel == 'INDIVIDUAL')
		doFetchHeirarchyIds = false;
	if (doFetchHeirarchyIds && !doStopZillowIdFetch) {
		doFetchHeirarchyIds = false;
		fetchHeirarchyIdsConectedToZillow(profileLevel, currentProfileIden, isNextBatch);
	}
}

function fetchReviewsCountBasedOnProfileLevel(profileLevel, iden,
	callbackFunction, minScore, maxScore, notRecommended) {
	if (iden == undefined || iden == "") {
		return;
	}
	if (minScore == undefined) {
		minScore = -1;
	}
	if (maxScore == undefined) {
		maxScore = -1;
	}
	var url = "/rest/profile/";
	if (profileLevel == 'COMPANY') {
		url += "company/";
	} else if (profileLevel == 'REGION') {
		url += "region/";
	} else if (profileLevel == 'BRANCH') {
		url += "branch/";
	} else if (profileLevel == 'INDIVIDUAL') {
		url += "individual/";
	}
	url += iden + '/reviewcount?minScore=' + minScore + '&maxScore=' + maxScore;
	if (notRecommended != undefined && typeof (notRecommended) === "boolean") {
		url += '&notRecommended=' + notRecommended;
	}
	callAjaxGET(url, callbackFunction, true);
}

/**
 * Method 
 * @param data
 */
function paintHiddenReviewsCount(data) {
	var responseJson = $.parseJSON(data);
	if (responseJson != undefined) {
		var reviewsSizeHtml = responseJson.entity;
		if (reviewsSizeHtml > 0) {
			if (reviewsSizeHtml == 1) {
				reviewsSizeHtml = reviewsSizeHtml + ' additional review';
			} else {
				reviewsSizeHtml = reviewsSizeHtml + ' additional reviews';
			}

			$("#prof-hidden-review-count")
				.attr("data-nr-review-count", responseJson.entity)
				.html(reviewsSizeHtml);

			if (showAllReviews == true) {
				$("#prof-hidden-review-count").hide();
			}

			/*$("#prof-hidden-review-count").click(function(){
				$('#prof-review-item').html('');
				$(this).hide();
				startIndex = 0;
				$("#profile-fetch-info").attr("fetch-all-reviews", "true");
				$(window).scrollTop($('#reviews-container').offset().top);
				showAllReviews = true;
				var profileLevel = $("#profile-fetch-info").attr("profile-level");
				doFetchZillowReviews = true;
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, 0 , true);
			});*/
		}
	}
}

$(document).on('click', '#sort-by-feature', function (e) {
	e.stopImmediatePropagation();
	$("#prof-hidden-review-count").show();
	$('#prof-review-item').html('');
	startIndex = 0;
	$("#profile-fetch-info").attr("fetch-all-reviews", "false");
	showAllReviews = false;
	var profileLevel = $("#profile-fetch-info").attr("profile-level");
	reviewsSortBy = 'feature';
	doFetchZillowReviews = true;
	doFetchHeirarchyIds = true;
	fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, minScore, true);
});

$(document).on('click', '#sort-by-date, #prof-hidden-review-count', function (e) {
	e.stopImmediatePropagation();
	$("#prof-hidden-review-count").hide();
	$('#prof-review-item').html('');
	startIndex = 0;
	$("#profile-fetch-info").attr("fetch-all-reviews", "true");
	showAllReviews = true;
	var profileLevel = $("#profile-fetch-info").attr("profile-level");
	reviewsSortBy = 'date';
	doFetchZillowReviews = true;
	doFetchHeirarchyIds = true;
	fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, 0, true);
});

function fetchRegionProfile() {
	var result = profileJson;
	paintProfilePage(result);
	paintIndividualDetails(result);
	fetchBranchesForRegion(result.iden);
	fetchIndividualsForRegion(result.iden);
	if (result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;

	if (showAllReviews == true) {
		fetchReviewsBasedOnProfileLevel('REGION', result.iden, startIndex, numOfRows, 0, true);
	}
	else {
		fetchReviewsBasedOnProfileLevel('REGION', result.iden, startIndex, numOfRows, minScore, true);
	}

	fetchReviewsCountBasedOnProfileLevel('REGION', result.iden, paintHiddenReviewsCount, 0, minScore, true);
	// Commented as zillow reviews are to be fetched after the last batch of social survey reviews fetched, SS-1277
	// fetchZillowReviewsBasedOnProfile('REGION',result.iden);
}

function fetchBranchProfile() {
	var result = profileJson;
	paintProfilePage(result);
	paintIndividualDetails(result);
	fetchIndividualsForBranch(result.iden);
	if (result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;

	if (showAllReviews == true) {
		fetchReviewsBasedOnProfileLevel('BRANCH', result.iden, startIndex, numOfRows, 0, true);
	}
	else {
		fetchReviewsBasedOnProfileLevel('BRANCH', result.iden, startIndex, numOfRows, minScore, true);
	}

	fetchReviewsCountBasedOnProfileLevel('BRANCH', result.iden, paintHiddenReviewsCount, 0, minScore, true);
	// Commented as zillow reviews are to be fetched after the last batch of social survey reviews fetched, SS-1277
	// fetchZillowReviewsBasedOnProfile('BRANCH',result.iden);
}

function paintIndividualDetails(result) {
	var individualDetailsHtml = "";

	// Paint licenses
	var licenses = result.licenses;
	if (licenses != undefined) {
		if (licenses.authorized_in != undefined && licenses.authorized_in.length > 0) {
			individualDetailsHtml = individualDetailsHtml + '<div class="prof-left-row prof-left-auth bord-bot-dc">';
			individualDetailsHtml = individualDetailsHtml + '	<div class="left-auth-wrapper">';
			individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-header lph-dd lph-dd-closed cursor-pointer lph-arrow-closed">Licenses</div>';
			individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-content lph-dd-content">';
			$.each(licenses.authorized_in, function (i, authorizedIn) {
				individualDetailsHtml = individualDetailsHtml + '<div class="lp-auth-row lp-row clearfix">' + authorizedIn + '</div>';
			});
			individualDetailsHtml = individualDetailsHtml + '		</div>';
			individualDetailsHtml = individualDetailsHtml + '	</div>';
			individualDetailsHtml = individualDetailsHtml + '</div>';
		}
	}

	// Paint postions
	var positions = result.positions;
	if (positions != undefined && positions.length > 0) {
		individualDetailsHtml = individualDetailsHtml + '<div class="prof-left-row prof-left-assoc bord-bot-dc">';
		individualDetailsHtml = individualDetailsHtml + '	<div class="left-postions-wrapper">';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-header lph-dd lph-dd-closed lph-dd-open cursor-pointer">Positions</div>';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-content lph-dd-content">';

		for (var i = 0; i < positions.length; i++) {
			individualDetailsHtml += '<div class="postions-content">';

			var positionObj = positions[i];
			individualDetailsHtml = individualDetailsHtml + '<div class="lp-pos-row-1 lp-row clearfix position-align">' + positionObj.name + '</div>';
			if (positionObj.title) {
				individualDetailsHtml = individualDetailsHtml + '<div class="lp-pos-row-2 lp-row clearfix position-align">' + positionObj.title + '</div>';
			}
			if (positionObj.startTime) {
				var startDateDisplay = constructDate(positionObj.startTime.split("-"));
				if (!positionObj.isCurrent && positionObj.endTime) {
					var endDateDisplay = constructDate(positionObj.endTime.split("-"));
					individualDetailsHtml = individualDetailsHtml + '<div class="lp-pos-row-3 lp-row clearfix position-align">' + startDateDisplay + " - " + endDateDisplay + '</div>';
				} else {
					individualDetailsHtml = individualDetailsHtml + '<div class="lp-pos-row-3 lp-row clearfix position-align">' + startDateDisplay + ' - Current</div>';
				}
			}
			individualDetailsHtml += '</div>';
		}
		individualDetailsHtml = individualDetailsHtml + '		</div>';
		individualDetailsHtml = individualDetailsHtml + '	</div>';
		individualDetailsHtml = individualDetailsHtml + '</div>';
	}

	// Paint Associations
	if (result.associations != undefined && result.associations.length > 0) {
		individualDetailsHtml = individualDetailsHtml + '<div class="prof-left-row prof-left-assoc bord-bot-dc">';
		individualDetailsHtml = individualDetailsHtml + '	<div class="left-assoc-wrapper">';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-header lph-dd lph-dd-closed lph-dd-open cursor-pointer">Memberships</div>';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-content lph-dd-content">';
		$.each(result.associations, function (i, associations) {
			individualDetailsHtml = individualDetailsHtml + '<div class="lp-assoc-row lp-row clearfix">' + associations.name + '</div>';
		});
		individualDetailsHtml = individualDetailsHtml + '		</div>';
		individualDetailsHtml = individualDetailsHtml + '	</div>';
		individualDetailsHtml = individualDetailsHtml + '</div>';
	}

	// paint expertise
	if (result.expertise != undefined && result.expertise.length > 0) {
		individualDetailsHtml = individualDetailsHtml + '<div class="prof-left-row prof-left-ach bord-bot-dc">';
		individualDetailsHtml = individualDetailsHtml + '	<div class="left-ach-wrapper">';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-header lph-dd lph-dd-closed cursor-pointer lph-arrow-closed">Specialties</div>';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-content lph-dd-content">';
		for (var i = 0; i < result.expertise.length; i++) {
			individualDetailsHtml = individualDetailsHtml + '<div class="lp-ach-row lp-row clearfix">' + result.expertise[i] + '</div>';
		}
		individualDetailsHtml = individualDetailsHtml + '		</div>';
		individualDetailsHtml = individualDetailsHtml + '	</div>';
		individualDetailsHtml = individualDetailsHtml + '</div>';
	}

	// paint achievements
	if (result.achievements != undefined && result.achievements.length > 0) {
		individualDetailsHtml = individualDetailsHtml + '<div class="prof-left-row prof-left-ach bord-bot-dc">';
		individualDetailsHtml = individualDetailsHtml + '	<div class="left-ach-wrapper">';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-header lph-dd lph-dd-closed cursor-pointer lph-arrow-closed">Achievements</div>';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-content lph-dd-content">';
		$.each(result.achievements, function (i, achievements) {
			individualDetailsHtml = individualDetailsHtml + '<div class="lp-ach-row lp-row clearfix">' + achievements.achievement + '</div>';
		});
		individualDetailsHtml = individualDetailsHtml + '		</div>';
		individualDetailsHtml = individualDetailsHtml + '	</div>';
		individualDetailsHtml = individualDetailsHtml + '</div>';
	}

	// paint hobbies
	if (result.hobbies != undefined && result.hobbies.length > 0) {
		individualDetailsHtml = individualDetailsHtml + '<div class="prof-left-row prof-left-ach bord-bot-dc">';
		individualDetailsHtml = individualDetailsHtml + '	<div class="left-ach-wrapper">';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-header lph-dd lph-dd-closed cursor-pointer lph-arrow-closed">Hobbies</div>';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-content lph-dd-content">';
		for (var i = 0; i < result.hobbies.length; i++) {
			individualDetailsHtml = individualDetailsHtml + '<div class="lp-ach-row lp-row clearfix">' + result.hobbies[i] + '</div>';
		}
		individualDetailsHtml = individualDetailsHtml + '		</div>';
		individualDetailsHtml = individualDetailsHtml + '	</div>';
		individualDetailsHtml = individualDetailsHtml + '</div>';
	}

	$("#individual-details").html(individualDetailsHtml);
	$('.lph-dd').click(function () {
		if ($(this).next('.lph-dd-content').is(':visible')) {
			$(this).next('.lph-dd-content').slideToggle(200);
			$(this).addClass('lph-arrow-closed').removeClass('lph-arrow-open');
		} else {
			$('.lph-dd-content').hide();
			$('.lph-dd').addClass('lph-arrow-closed').removeClass('lph-arrow-open');
			$(this).removeClass('lph-arrow-closed').addClass('lph-arrow-open');
			$(this).next('.lph-dd-content').slideToggle(200);
		}
	});

	$('.lph-dd:nth(0)').trigger('click');
}

function fetchAgentProfile() {
	var result = profileJson;
	paintProfilePage(result);
	paintIndividualDetails(result);
	if (result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;

	if (showAllReviews == true) {
		fetchReviewsBasedOnProfileLevel('INDIVIDUAL', result.iden, startIndex, numOfRows, 0, true);
	}
	else {
		fetchReviewsBasedOnProfileLevel('INDIVIDUAL', result.iden, startIndex, numOfRows, minScore, true);
	}

	fetchReviewsCountBasedOnProfileLevel('INDIVIDUAL', result.iden, paintHiddenReviewsCount, 0, minScore, true);
	// Commented as zillow reviews are to be fetched after the last batch of social survey reviews fetched, SS-1277
	// fetchZillowReviewsBasedOnProfile('INDIVIDUAL',result.iden);
}

function findProList(iden, searchcritrianame) {
	if (iden == undefined || iden == "") {
		return;
	}
	var url = "";
	var profileLevel = $("#profile-fetch-info").attr("profile-level");
	if (profileLevel == 'INDIVIDUAL') {
		initSurveyReview(iden);
	} else {
		url = getLocationOrigin() + "/initfindapro.do?profileLevel=" + profileLevel + "&iden=" + iden + "&searchCriteria=" + searchcritrianame;
		window.open(url, "_blank");
	}

}

function downloadVCard(agentName) {
	if (agentName == undefined || agentName == "") {
		return;
	}
	var url = getLocationOrigin() + "/rest/profile/downloadvcard/" + agentName;
	window.open(url, "_blank");
}

//Watch the scroll position of social posts
$('#prof-posts').scroll(function () {
	var scrollContainer = this;
	if (scrollContainer && $(scrollContainer).is(':visible')) {
		if ((scrollContainer.scrollTop >= ((scrollContainer.scrollHeight) - (scrollContainer.clientHeight / 0.75))) &&
			!isLoaderRunningPublicPosts) {
			if (publicPostsNextBatch.length > 0 || !doStopPublicPostPagination) {
				fetchPublicPostsScroll(false);
			}
		}
	}
});

function fetchPublicPostsScroll(isNextBatch) {
	if (!isNextBatch && publicPostsNextBatch.length > 0) {
		var postsToShow = publicPostsNextBatch.slice(0, publicPostNumRows);
		if (publicPostsNextBatch.length > publicPostNumRows) {
			publicPostsNextBatch = publicPostsNextBatch.slice(publicPostNumRows);
		} else {
			publicPostsNextBatch = [];
		}

		isLoaderRunningPublicPosts = true;
		showLoaderOnPagination($('#prof-posts'));
		setTimeout(function () {
			isLoaderRunningPublicPosts = false;
			paintPublicPosts(postsToShow);
			if (publicPostsNextBatch.length <= publicPostNumRows) {
				fetchPublicPostsScroll(true);
			}
		}, 500);
	} else {
		if (isPublicPostAjaxRequestRunning) {
			if (!isNextBatch) {
				setTimeout(function () {
					fetchPublicPostsScroll(false); //keep checking until the posts arrives	
				}, 200);
			}
			return; // Return if ajax request is still running
		}
		if (doStopPublicPostPagination) return; //If pagination has stopped return the request

		if (publicPostsNextBatch.length <= publicPostNumRows) {
			fetchPublicPosts(true);
		}
	}
}

//Function to paint posts
function fetchPublicPosts(isNextBatch) {

	var profileLevel = $("#profile-fetch-info").attr("profile-level");

	//if recent posts are to be hidden
	if (profileJson.hideSectionsFromProfilePage && ($.inArray("recent_posts", profileJson.hideSectionsFromProfilePage) > -1)) {
		$('#recent-post-container').remove();
		doStopPublicPostPagination = true;
		return;
	}

	var url = getLocationOrigin() + "/rest/profile/";
	if (profileLevel == 'COMPANY') {
		//Fectch the reviews for company
		url += "company/";
	}
	else if (profileLevel == 'REGION') {
		//Fetch the reviews for region
		url += "region/" + companyProfileName + "/";
	}
	else if (profileLevel == 'BRANCH') {
		//Fetch the reviews for branch
		url += "branch/" + companyProfileName + "/";
	}
	else if (profileLevel == 'INDIVIDUAL') {
		//Fetch the reviews for individual
	}

	url += currentProfileName + "/posts?start=" + publicPostStartIndex + "&numRows=" + publicPostNumRows;

	isPublicPostAjaxRequestRunning = true;

	if (!isNextBatch) {
		showLoaderOnPagination($('#prof-posts'));
	}
	callAjaxGET(url, function (data) {
		isPublicPostAjaxRequestRunning = false;

		var posts = $.parseJSON(data);
		posts = $.parseJSON(posts.entity);

		//check if reponse is correct or no reviews are fetched
		if (posts.errorCode != undefined || (publicPostStartIndex == 0 && posts.length <= 0)) {
			$('#recent-post-container').remove();
			doStopPublicPostPagination = true;
			return;
		}

		//Check if request is for next batch
		if (!isNextBatch) {
			paintPublicPosts(posts);
		} else {
			publicPostsNextBatch = publicPostsNextBatch.concat(posts);
		}

		publicPostStartIndex += posts.length;
		if (publicPostStartIndex < publicPostNumRows || posts.length < publicPostNumRows) {
			doStopPublicPostPagination = true;
			return;
		}

		if (publicPostsNextBatch.length <= publicPostNumRows) {
			fetchPublicPosts(true);
		}
	}, true);
}

function paintPublicPosts(posts) {

	var divToPopulate = "";
	var postsLength = posts.length;
	var elementClass;
	$('#prof-posts').children('.tweet-panel-item').removeClass('bord-bot-none');
	$.each(posts, function (i, post) {
		var iconClass = "";
		var href = "javascript:void(0)";
		if (post.source == "google")
			return true;
		else if (post.source == "SocialSurvey")
			iconClass = "icn-ss";
		else if (post.source == "facebook") {
			iconClass = "icn-fb";
			href = "http://www.facebook.com/" + post.postId;
		}
		else if (post.source == "twitter") {
			iconClass = "icn-twit";
			var res = post.postText.split("http");
			href = "http" + res[1];
		}
		else if (post.source == "linkedin")
			iconClass = "icn-lin";
		if (typeof post.postUrl != "undefined") {
			href = post.postUrl;
		}
		var hrefComplet = '<a href=' + href + ' target="_blank">';
		elementClass = "tweet-panel-item bord-bot-dc clearfix";

		if (i >= postsLength - 1) {
			elementClass += " bord-bot-none";
		}
		divToPopulate += '<div class="' + elementClass + '">'
			+ hrefComplet
			+ '<div class="tweet-icn ' + iconClass + ' float-left"></div>'
			+ "</a>"
			+ '<div class="tweet-txt float-left">'
			+ '<div class="tweet-text-main">' + linkify(escapeHtml(post.postText)) + '</div>'
			+ '<div class="tweet-text-link"><em>' + post.postedBy + '</em></div>'
			+ '<div class="tweet-text-time"><em>' + convertUserDateToWeekFormt(new Date(post.timeInMillis)) + '</em></div>'
			+ '	</div>'
			+ '</div>';
	});

	hideLoaderOnPagination($('#prof-posts'));
	if (publicPostStartIndex == 0) {
		if (posts.length > 0) {
			$('#recent-post-container').show();
		} else {
			$('#recent-post-container').remove();
		}
		$('#prof-posts').html('');
	}

	$('#prof-posts').append(divToPopulate);

	if (publicPostStartIndex == 0) {
		$('#prof-posts').perfectScrollbar({
			suppressScrollX: true
		});
	} else {
		$('#prof-posts').perfectScrollbar('update');
	}

	//trigger the scroll to update posts
	setTimeout(function () {
		$('#prof-posts').trigger('scroll');
	}, 100);

}

$('body').on('click', ".branch-link", function (e) {
	e.stopPropagation();
	var branchProfileName = $(this).data("profilename");
	var url = getLocationOrigin() + "/pages/office/" + companyProfileName + "/" + branchProfileName;
	window.open(url, "_blank");
});

$('body').on('click', ".individual-link", function (e) {
	e.stopPropagation();
	var agentProfileName = $(this).data("profilename");
	var url = getLocationOrigin() + "/pages/" + agentProfileName;
	window.open(url, "_blank");
});

$('body').on('click', ".region-link", function (e) {
	e.stopPropagation();
	var regionProfileName = $(this).data("profilename");
	var url = getLocationOrigin() + "/pages/region/" + companyProfileName + "/" + regionProfileName;
	window.open(url, "_blank");
});


$('body').on("click touchstart", ".comp-branch,.comp-region-branch", function (e) {
	e.preventDefault();
	if ($(this).data("openstatus") == "closed") {
		fetchIndividualsForBranch($(this).data('branchid'));
		$(this).data("openstatus", "open");
		$(this).attr("data-openstatus", "open");
	} else {
		$('#comp-branch-individuals-' + $(this).data('branchid')).slideUp(200);
		$(this).data("openstatus", "closed");
		$(this).attr('data-start', 0);
		$(this).attr("data-openstatus", "closed");
	}
});

$('body').on("click touchstart", ".comp-region", function () {
	if ($(this).data("openstatus") == "closed") {
		$('#comp-region-branches-' + $(this).data('regionid')).html("");
		fetchBranchesForRegion($(this).data('regionid'));
		fetchIndividualsForRegion($(this).data('regionid'));
		$(this).data("openstatus", "open");
		$(this).attr("data-openstatus", "open");
	} else {
		$('#comp-region-branches-' + $(this).data('regionid')).slideUp(200);
		$(this).data("openstatus", "closed");
		$(this).attr("data-openstatus", "closed");
	}

});

function constructDate(dateStr) {
	var dateDisplay = "";
	if (typeof dateStr[0] != 'undefined' && dateStr[0] != '0' && typeof dateStr[1] != 'undefined' && dateStr[1] != '0') {
		dateDisplay = monthNames[dateStr[0] - 1] + " " + dateStr[1];
	} else if (typeof dateStr[0] != 'undefined' && dateStr[0] != '0') {
		dateDisplay = monthNames[dateStr[0] - 1];
	} else if (typeof dateStr[1] != 'undefined' && dateStr[1] != '0') {
		dateDisplay = dateStr[1];
	}

	return dateDisplay;
}

//NOTE: picture and caption are deprecated for facebook, facebook no longer supports "picture" and "caption" parameters
function getImageandCaption(index) {
	var pictureandCaptionLink = "";
	var imgid = "";
	var name = "";
	var addrline1 = "";
	var addrline2 = "";
	var fblink = $("#fb_" + index).data('link');
	if (document.getElementById("prof-image") != null && document.getElementById("prof-image").getAttribute("src") != undefined) {
		imgid = document.getElementById("prof-image").getAttribute("src");
	}
	if ($(".prof-name")[0] != undefined && $(".prof-name")[0].innerHTML != undefined) {
		name = $(".prof-name")[0].innerHTML;
	}

	if ($(".prof-addline2")[0] != undefined && $(".prof-addline2")[0].innerHTML != undefined) {
		addrline2 = $(".prof-addline2")[0].innerHTML;
	}

	if ($(".prof-addline1")[0] != undefined && $(".prof-addline1")[0].innerHTML != undefined) {
		addrline1 = $(".prof-addline1")[0].innerHTML;
	}

	pictureandCaptionLink = "&picture=" + imgid + "&caption=" + name + "," + addrline2 + "," + addrline1;
	fblink = fblink.concat(pictureandCaptionLink);
	if (document.getElementById('fb_' + index) != null) {
		document.getElementById('fb_' + index).setAttribute('data-link', fblink);

	}
}

function twitterFn(loop) {

	var twitLink = $("#twitt_" + loop).data('link');
	var String = twitLink.substring(twitLink.indexOf("=") + 1, twitLink
		.lastIndexOf("&"));
	var twitId = 'twttxt_' + loop;
	var twitText = $("#" + twitId).val();
	twitText = decodeURIComponent(twitText);
	var length = twitText.length;
	if (length > 180) {

		var twittStrnDot = "...";
		var substringed = twitText.substring(0, 176);
		var finalString = substringed.concat(twittStrnDot);
		finalString = encodeURIComponent(finalString);
		$("#" + twitId).val(finalString);
		twitLink = twitLink.replace(String, finalString);
		if (document.getElementById('twitt_' + loop) != null) {
			document.getElementById('twitt_' + loop).setAttribute('data-link',
				twitLink);
		}
	}
}

var zillowHierarchyList = [];
var zillowHStart = 0;
var zillowHBatchSize = 10;
var curHierarchyLevel = "";
var doStopZillowIdFetch = false;
var isZillowIdFetchRunning = false;
function fetchHeirarchyIdsConectedToZillow(profileLevel, iden, isNextBatch) {
	if (iden == undefined || iden == "" || isZillowIdFetchRunning) {
		return;
	}

	var newHierarchyLevel = "";
	var url = "/rest/profile/";
	if (profileLevel == 'COMPANY') {
		// url += "company/";
		// newHierarchyLevel = "REGION";
		return;
	} else if (profileLevel == 'REGION') {
		// url += "region/";
		// newHierarchyLevel = "BRANCH";
		return;
	} else if (profileLevel == 'BRANCH') {
		// url += "branch/";
		// newHierarchyLevel = "INDIVIDUAL";
		return;
	} else if (profileLevel == 'INDIVIDUAL') {
		return;
	}
	if (curHierarchyLevel == "") {
		curHierarchyLevel = newHierarchyLevel;
	}
	url += iden + '/fetchhierarchyconnectedtozillow';
	var payload = {
		"start": zillowHStart,
		"numRows": zillowHBatchSize,
		"currentHierarchyLevel": curHierarchyLevel
	};
	isZillowIdFetchRunning = true;
	callAjaxGetWithPayloadData(url, function (data) {
		isZillowIdFetchRunning = false;
		if (data != undefined && data != "") {
			var responseJson = $.parseJSON(data);
			if (responseJson != undefined) {
				// synchronised this increment
				zillowHStart += zillowHBatchSize;
				var result = responseJson;

				var length = result.length;
				if (result == undefined || length == 0) {
					switch (curHierarchyLevel) {
						case "REGION":
							curHierarchyLevel = "BRANCH";
							break;

						case "BRANCH":
							curHierarchyLevel = "INDIVIDUAL";
							break;

						default:
							doStopZillowIdFetch = true;
					}
					zillowHierarchyList = [];
					zillowHStart = 0;
				}

				if (result != undefined && length > 0) {
					zillowHierarchyList = result;
					/*fetchZillowReviewsFromZillowHierarchyMap(profileLevel, iden, isNextBatch);*/
					fetchReviewsScroll(isNextBatch);
				}
			}
		} else {
			zillowHierarchyList = [];
			zillowHStart = 0;
			switch (curHierarchyLevel) {
				case "REGION":
					curHierarchyLevel = "BRANCH";
					if (!isNextBatch)
						fetchHeirarchyIdsConectedToZillow(profileLevel, iden, isNextBatch);
					break;

				case "BRANCH":
					curHierarchyLevel = "INDIVIDUAL";
					if (!isNextBatch)
						fetchHeirarchyIdsConectedToZillow(profileLevel, iden, isNextBatch);
					break;

				default:
					doStopZillowIdFetch = true;
			}
		}
	}, payload, false);
}

function fetchZillowReviewsFromZillowHierarchyMap(profileLevel, iden, isNextBatch) {
	if (isZillowReviewsCallRunning) {
		//If it is not next batch and reviews are still loading check for if reviews are there
		//If there show them otherwise wait for the reviews to load
		if (isNextBatch == undefined)
			isNextBatch = true;
		if (!isNextBatch) {
			setTimeout(function () {
				fetchReviewsScroll(isNextBatch);
			}, 200);
		}
		return; //Return if zillow reviews are still loading
	}
	if (!zillowCallBreak) {
		switch (curHierarchyLevel) {
			case "REGION":
				var regionIds = zillowHierarchyList;
				if (regionIds != undefined && regionIds.length > 0) {
					fetchZillowReviewsBasedOnProfile('REGION', regionIds[0], isNextBatch);
					regionIds.shift();
					return;
				} else {
					if (!doStopZillowIdFetch)
						fetchHeirarchyIdsConectedToZillow(profileLevel, iden, isNextBatch);
				}
				break;

			case "BRANCH":
				var branchIds = zillowHierarchyList;
				if (branchIds != undefined && branchIds.length > 0) {
					fetchZillowReviewsBasedOnProfile('BRANCH', branchIds[0], isNextBatch);
					branchIds.shift();
					return;
				} else {
					if (!doStopZillowIdFetch)
						fetchHeirarchyIdsConectedToZillow(profileLevel, iden, isNextBatch);
				}
				break;

			case "INDIVIDUAL":
				var individualIds = zillowHierarchyList;
				if (individualIds != undefined && individualIds.length > 0) {
					fetchZillowReviewsBasedOnProfile('INDIVIDUAL', individualIds[0], isNextBatch);
					individualIds.shift();
					return;
				} else {
					if (!doStopZillowIdFetch)
						fetchHeirarchyIdsConectedToZillow(profileLevel, iden, isNextBatch);
				}
				break;

			default:
				return;
		}
	}
}


(function () {
	var po = document.createElement('script');
	po.type = 'text/javascript';
	po.async = true;
	po.src = 'https://apis.google.com/js/client:plusone.js';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(po, s);
})();


//Bind events for social share, open, close icons
$('#prof-review-item').on('click', '.icn-plus-open', function () {
	$(this).hide();
	$(this).parent().find('.ppl-share-social,.icn-remove').show();
});

$('#prof-review-item').on('click', '.icn-remove', function () {
	$(this).hide();
	$(this).parent().find('.ppl-share-social').hide();
	$(this).parent().find('.icn-plus-open').show();
});

$('#prof-review-item').on('click', '.ppl-share-icns', function (e) {
	e.stopPropagation();
	var link = $(this).attr('data-link');
	var title = $(this).attr('title');

	if (title == 'LinkedIn') {
		var copyText = $(this).parent().find('.linkedInSummary').val();
		var decodedText = decodeURIComponent(copyText);
		copyToClipboard(decodedText);

		$('#linked-in-cc-popup-continue').off();
		$('#linked-in-cc-popup-continue').click(function () {

			$('#linked-in-cc-popup').hide();
			$('#linked-in-cc-popup-continue').unbind('click');
			enableBodyScroll();

			if (link == undefined || link == "") {
				return false;
			}
			window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
		});

		$('#linked-in-cc-popup').show();
		disableBodyScroll();

	} else {
		if (link == undefined || link == "") {
			return false;
		}
		window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
	}

});

function overlayRevert() {
	$('#overlay-main').hide();
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');

	$('#overlay-continue').unbind('click');

	enableBodyScroll();
	$('.overlay-disable-wrapper').removeClass('pu_arrow_rt');

	$("#overlay-pop-up").removeClass("overlay-disable-wrapper-zillow");
	$('#zillow-popup').hide();
	$('#zillow-popup-body').html('');
}


$('.sr-share-wrapper').on('click', '.ppl-share-icns', function (e) {
	e.stopPropagation();
	var link = $(this).attr('data-link');
	var title = $(this).attr('title');


	if (title == 'LinkedIn') {
		var copyText = $(this).parent().find('.linkedInSummary').val();
		var decodedText = decodeURIComponent(copyText);
		copyToClipboard(decodedText);

		$('#linked-in-cc-popup-continue').off();
		$('#linked-in-cc-popup-continue').click(function () {

			$('#linked-in-cc-popup').hide();
			$('#linked-in-cc-popup-continue').unbind('click');
			enableBodyScroll();

			if (link == undefined || link == "") {
				return false;
			}
			window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
		});

		$('#linked-in-cc-popup').css('z-index', 99999);
		if (window.innerWidth < 768) {
			$('#linked-in-cc-popup').css('top', 70);
		}
		$('#linked-in-cc-popup').show();
		disableBodyScroll();

	} else {
		if (link == undefined || link == "") {
			return false;
		}
		window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
	}

});

/**
 * JS functions to fetch API Key for google maps
 * 
 */

var apikey;


//Function for google map api key
function fetchGoogleMapApi(callBackFunction) {

	$.ajax({
		url: window.location.origin + "/fetchgooglemapapikey.do",
		type: "GET",
		dataType: "html",
		async: true,
		success: function (data) {
			apikey = data;
			if (callBackFunction != undefined)
				callBackFunction();
		},
		error: function (e) {
			redirectErrorpage();
		}
	});
}

function setUpReviewPopupListener() {
	$(document).on('click', '.ppl-review-item, .ppl-review-item-last', function (event) {

		// exceptions
		if ($('.view-zillow-link').is(event.target)) {
			return;
		}

		if ($('.view-fb-link').is(event.target)) {
			return;
		}

		event.stopPropagation();
		var reviewSurveyId = $(this).attr('survey-mongo-id');
		if (reviewSurveyId != undefined && reviewSurveyId != "") {
			loadIndividualReviewPageInPublicProfile(reviewSurveyId);
		}
	});
}

function loadIndividualReviewPageInPublicProfile(mongoSurveyId) {
	if (mongoSurveyId != undefined || mongoSurveyId != "") {

		var publicProfileUrl = buildPublicProfileUrl();
		if (publicProfileUrl != undefined && publicProfileUrl != "") {
			window.location.href = publicProfileUrl + '/' + mongoSurveyId;
		}
	}
}

function buildPublicProfileUrl() {

	var currentPath = window.location.pathname.trim();
	if (currentPath.charAt(0) == "/") {
		currentPath = currentPath.substring(1, currentPath.length);
	}
	if (currentPath.charAt(currentPath.length - 1) == "/") {
		currentPath = currentPath.substring(0, currentPath.length - 1);
	}

	var pathArray = currentPath.split('/');

	if (pathArray != undefined && pathArray[0] == "pages") {
		if (pathArray[1] == "company") {
			return window.location.origin + "/pages/company/" + pathArray[2];
		} else if (pathArray[1] == "region") {
			return window.location.origin + "/pages/region/" + pathArray[2] + "/" + pathArray[3];
		} else if (pathArray[1] == "office") {
			return window.location.origin + "/pages/office/" + pathArray[2] + "/" + pathArray[3];
		} else {
			return window.location.origin + "/pages/" + pathArray[1];
		}
	} else {
		return "";
	}
}

function setUpPopupDismissListeners() {
	$(document).keyup(function (e) {
		if (!$('#single-review-page').hasClass('hide')) {
			if (e.keyCode === 27) {
				$('#dismiss-single-review-popup').trigger('click');
			}
		}
	});

	$(document).mouseup(function (e) {
		if (!$('#single-review-page').hasClass('hide')) {
			var target = e.target;
			var container = $('#single-review-popup');
			if (!container.is(target) && container.parent().is(target) && container.has(target).length == 0) {
				$('#dismiss-single-review-popup').trigger('click');
			}
		}
	});
}


function buildReviewPopupShareData() {

	if ($('#sr-review-info') == undefined || $('#sr-review-info') == '') {
		return;
	}
	var customerFirstName = $('#sr-review-info').data('cust-first-name');
	var customerLastName = $('#sr-review-info').data('cust-last-name');
	var score = parseFloat($('#sr-review-info').data('score'));
	var agentName = $('#sr-review-info').data('agent-name');
	var _id = $('#sr-review-info').data('survey-mongo-id');
	var review = $('#sr-review-info').data('review');
	var faceBookShareUrl = $('#sr-review-info').data('facebookshareurl');

	if (agentName == undefined || agentName == null || agentName == "")
		agentName = "us";


	var profileUrl = window.location.href;
	var scoreFixVal = 1;

	var custDispName = customerFirstName.trim();
	if (customerLastName != undefined && customerLastName.trim() != "") {
		custDispName += ' ' + customerLastName.substr(0, 1).toUpperCase() + '.';
	}

	var socialPostHtml = "";

	// build share URL for facebook
	//$('#fb_post').data( 'link', 'https://www.facebook.com/dialog/share?' + reviewItem.faceBookShareUrl + '&href=' +profileUrl.replace("localhost","127.0.0.1")+ '&quote=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '&redirect_uri=https://www.facebook.com' );

	//$('#twttxt_post').val( reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) );
	//$('#twitt_post').data( 'link', 'https://twitter.com/intent/tweet?text=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + ' &url='+ profileUrl );


	// linkedIn
	//$('#linkedin_post').data( 'link', 'https://www.linkedin.com/shareArticle?mini=true&url=' + profileUrl + '/' + reviewItem._id + '&title=&summary=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) +' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '&reviewid=' + reviewItem._id + '&source=' );
	
	var reviewSummary = score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ( agentName != undefined ? ' for ' + encodeURIComponent(agentName) : '' ) +' at SocialSurvey - ' + encodeURIComponent(review);
	
	socialPostHtml += '         <span id ="fb_post" class="float-left ppl-share-icns sr-icn-fb-rev icn-fb-pp" title="Facebook" data-link="https://www.facebook.com/dialog/share?' + faceBookShareUrl + '&href=' +profileUrl.replace("localhost","127.0.0.1")+ '&quote=' + score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ( agentName != undefined ? ' for ' + encodeURIComponent(agentName) : '' ) + ' at SocialSurvey - ' + encodeURIComponent(review) + '&redirect_uri=https://www.facebook.com"></span>';
	socialPostHtml += '         <input type="hidden" id="twttxt_post" class ="twitterText_loop" value ="' + score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ( agentName != undefined ? ' for ' + encodeURIComponent( agentName) : '' ) + ' at SocialSurvey - ' + encodeURIComponent( review) + '"/></input>';
	socialPostHtml += '			<span id ="twitt_post" class="float-left ppl-share-icns sr-icn-twit-rev icn-twit-pp" onclick="processTwitterTextForSingleReview();" title="Twitter" data-link="https://twitter.com/intent/tweet?text=' + score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ( agentName != undefined ? ' for ' + encodeURIComponent(agentName) : '' ) + ' at SocialSurvey - ' + encodeURIComponent(review) + ' &url='+ profileUrl +'"></span>';	
	socialPostHtml += '			<input type="hidden" class"linkedInSummary" value="'+reviewSummary+'" >'
	socialPostHtml += '			<span class="float-left ppl-share-icns sr-icn-lin-rev icn-lin-pp" title="LinkedIn" data-link="https://www.linkedin.com/shareArticle?mini=true&url=' + profileUrl + '&title=&summary=' + score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ( agentName != undefined ? ' for ' + encodeURIComponent(agentName) : '' ) +' at SocialSurvey - ' + encodeURIComponent(review) + '&reviewid=' + _id + '&source="></span>';
	socialPostHtml += '			<span class="float-left ppl-share-icns permalink sr-icn-permalink-rev" title="Permalink" onclick="copyIndividualReviewUrlToClipboard(-1);"><input id="permalink_url_btn" type="hidden" value="' + profileUrl + '"/></span>';

	$('.sr-share-social').html(socialPostHtml);
}

function processTwitterTextForSingleReview() {

	var twitLink = $("#twitt_post").data('link');
	var String = twitLink.substring(twitLink.indexOf("=") + 1, twitLink
		.lastIndexOf("&"));
	var twitText = $("#twttxt_post").val();
	twitText = decodeURIComponent(twitText);
	var length = twitText.length;
	if (length > 180) {

		var twittStrnDot = "...";
		var substringed = twitText.substring(0, 176);
		var finalString = substringed.concat(twittStrnDot);
		finalString = encodeURIComponent(finalString);
		$("#twttxt_post").val(finalString);
		twitLink = twitLink.replace(String, finalString);
		if (document.getElementById('twitt_post') != null) {
			document.getElementById('twitt_post').setAttribute('data-link',
				twitLink);
		}
	}
}


