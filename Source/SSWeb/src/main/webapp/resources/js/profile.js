var companyProfileName = $("#company-profile-name").val();
var currentProfileIden = "";
var startIndex = 0;
var numOfRows = 5;
var minScore=0;
var publicPostStartIndex = 0;
var publicPostNumRows = 5;
var currentProfileName;
var doStopPublicPostPagination = false;
var isPublicPostAjaxRequestRunning = false;
var reviewsSortBy = 'default';
var showAllReviews = false;
var monthNames = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
		"Sep", "Oct", "Nov", "Dec" ];
var profileJson;
var isFetchReviewAjaxRequestRunning = false; //keeps checks of if the ajax request is running to fetch reviews.
var stopFetchReviewPagination = false;
var reviewsNextBatch = []; //Reviews batch to store the next reviews
var publicPostsNextBatch = []; //Posts batch to store the next posts

$(document).ajaxStop(function() {
	adjustImage();
});

//Profile page event binding
$(document).on("click", "#prof-company-review-count",function(){
	if(window.innerWidth < 768){
		$('.icn-star-smile').click();					
	}
	$('html, body').animate({
		scrollTop : $('#reviews-container').offset().top
	},500);
});

//Find a pro
$(document).on('keyup', '#find-pro-form input', function(e) {
	if(e.which == 13)
		submitFindProForm();
});

$(document).on("click", '#find-pro-submit', function(e) {
	e.preventDefault();
	submitFindProForm();
});

function submitFindProForm() {
	$('#find-pro-form').submit();
	showOverlay();
}

// Contact us form validation functions
function validateMessage(elementId) {
	if ($('#'+elementId).val() != "") {
		return true;
	} else {
		showErrorMobileAndWeb('Please enter your message!');
		return false;
	}
}

function validateName(elementId){
	if ($('#'+elementId).val() != "") {
		if (nameRegex.test($('#'+elementId).val()) == true) {
			return true;
		} else {
			showErrorMobileAndWeb('Please enter your valid name!');
			return false;
		}
	} else {
		showErrorMobileAndWeb('Please enter your valid name!');
		return false;
	}
}

function validateContactUsForm() {
	isContactUsFormValid = true;

	var isFocussed = false;
	if($(window).width() < 768){
		isSmallScreen = true;
	}
	
	// Validate form input elements
	if (!validateName('lp-input-name')) {
		isContactUsFormValid = false;
		if (!isFocussed) {
			$('#lp-input-name').focus();
			isFocussed=true;
		}
		return isContactUsFormValid;
	}
	
	if (!validateEmailId('lp-input-email')) {
		isContactUsFormValid = false;
		if (!isFocussed) {
			$('#lp-input-email').focus();
			isFocussed=true;
		}
		return isContactUsFormValid;
	}
	
	if (!validateMessage('lp-input-message')) {
		isContactUsFormValid = false;
		if (!isFocussed) {
			$('#lp-input-message').focus();
			isFocussed=true;
		}
		return isContactUsFormValid;
	}
	
	if (!validateMessage('captcha-text')) {
		isContactUsFormValid = false;
		if (!isFocussed) {
			$('#captcha-text').focus();
			isFocussed=true;
		}
		return isContactUsFormValid;
	}
	
	return isContactUsFormValid;
} 

$(document).on('click touchstart', '.icn-person', function() {
    $('.mob-icn').removeClass('mob-icn-active');
    $(this).addClass('mob-icn-active');
    $('#prof-company-intro').show();
    $('#contact-info').show();
    $('#prof-agent-container').hide();
    $('#reviews-container').hide();
    $('#recent-post-container').hide();
});

$(document).on('click touchstart', '.icn-ppl', function() {
    $('.mob-icn').removeClass('mob-icn-active');
    $(this).addClass('mob-icn-active');
    $('#recent-post-container').show();
    $('#contact-info').hide();
    $('#prof-agent-container').hide();
    $('#prof-company-intro').hide();
    $('#reviews-container').hide();
});

$(document).on('click touchstart', '.icn-star-smile', function() {
    $('.mob-icn').removeClass('mob-icn-active');
    $(this).addClass('mob-icn-active');
    $('#reviews-container').show();
    $('#contact-info').hide();
    $('#prof-agent-container').hide();
    $('#prof-company-intro').hide();
    $('#recent-post-container').hide();
});

$(document).on('click touchstart', '.inc-more', function() {
    $('.mob-icn').removeClass('mob-icn-active');
    $(this).addClass('mob-icn-active');
    $('#prof-agent-container').show();
    $('#prof-company-intro').hide();
    $('#contact-info').hide();
    $('#reviews-container').hide();
    $('#recent-post-container').hide();
});

$(document).on('click','.bd-q-contact-us',function(){
    $('#contact-us-pu-wrapper').show();
    $('body').addClass('body-no-scroll-y');
});

$(document).on('click','.bd-q-btn-cancel',function(){
    $('#contact-us-pu-wrapper').hide();
    $('body').removeClass('body-no-scroll-y');
});

$(document).on('click', '.lp-button', function(event){
	if(validateContactUsForm()){
		url = getLocationOrigin() + "/pages/profile/sendmail.do";
		data = "";
		if($("#agent-profile-name").val() != ""){
			data += "profilename=" + $("#agent-profile-name").val();
			data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
		}
		else if($("#company-profile-name").val() != ""){
			data += "profilename=" + $("#company-profile-name").val();
			data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
		}
		else if($("#region-profile-name").val() != ""){
			data += "profilename=" + $("#region-profile-name").val();
			data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
		}
		else if($("#branch-profile-name").val() != ""){
			data += "profilename=" + $("#branch-profile-name").val();
			data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
		}
		
		data += "&name=" + $('#lp-input-name').val();
		data += "&email=" + $('#lp-input-email').val();
		data += "&message=" + $('#lp-input-message').val();
		data += "&g-recaptcha-response=" + $('#g-recaptcha-response').val();
		//data += "&recaptcha_input=" + $('#captcha-text').val();
		showOverlay();
		callAjaxPostWithPayloadData(url,showMessage,data,true);
	}			
});

function showMessage(data){
	var jsonData = JSON.parse(data);
	if(jsonData["success"] == 1){
		showInfoMobileAndWeb(jsonData["message"]);
		$(".reg-cap-reload").click();
		
		// resetting contact form and captcha
		$('#prof-contact-form')[0].reset();
		var recaptchaframe = $('.g-recaptcha iframe');
        var recaptchaSoure = recaptchaframe[0].src;
        recaptchaframe[0].src = '';
        setInterval(function () { recaptchaframe[0].src = recaptchaSoure; }, 500);
	}
	else{
		showErrorMobileAndWeb(jsonData["message"]);
		$(".reg-cap-reload").click();
	}
}

function adjustImage(){
    var windW = $(window).width();
    if(windW < 768){
        var imgW = $('#prof-image').width();
        $('#prof-image').height(imgW * 0.7);
        $('.footer-main-wrapper').hide();
    }else{
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
	if(result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	fetchReviewsCountBasedOnProfileLevel('COMPANY',result.iden,paintHiddenReviewsCount,0,minScore, true);
	fetchReviewsBasedOnProfileLevel('COMPANY', result.iden,startIndex,numOfRows,minScore , true);	
	fetchZillowReviewsBasedOnProfile('COMPANY',result.iden);
}

function paintProfilePage(result) {
	if(result != undefined && result != "") {
		currentProfileIden = result.iden;
		currentProfileName = result.profileName;
		var contactDetails = result.contact_details;
		var profileLevel = $("#profile-fetch-info").attr("profile-level");
		
		// paint public  posts
		fetchPublicPosts();
		
		var breadCrumUrl = '/rest/breadcrumb/';
		
		if (profileLevel == 'INDIVIDUAL') {
			breadCrumUrl += 'individual/' + profileJson.iden;
		} else if (profileLevel == 'BRANCH') {
			breadCrumUrl += '/branch/' + profileJson.iden;
		} else if (profileLevel == 'REGION') {
			breadCrumUrl += '/region/' + profileJson.iden;
		}
		
		if(profileLevel != 'COMPANY'){
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
            	address=address.replace(/,/g,"");
            	
            	if (apikey == undefined) {
            		fetchGoogleMapApi();
            	}
            	$("#prof-company-logo").html('<iframe src="https://www.google.com/maps/embed/v1/place?key='+apikey+'&q='+address+'"></iframe>');
            }
		}
		
		var dataLink = $('.web-address-link').attr('data-link');
		var link = returnValidWebAddress(dataLink);
		$('#web-address-txt').html('<a href="'+link+'" target="_blank">'+dataLink+'</a>');
		$('#web-addr-link-lp').html('<a href="'+link+'" target="_blank">Our Website</a>');
		
		$('.social-item-icon').bind('click', function() {
    		var link = $(this).attr('data-link');
    		if (link == undefined || link == "") {
    			return false;
    		}
    		window.open(returnValidWebAddress(link), '_blank');
    	});
	}
}



function paintBreadCrums(url) {
	
	callAjaxGETWithTextData(url, function(data) {
		var jsonData = $.parseJSON(data);
		if(jsonData.entity) {
			var entityJson = $.parseJSON(jsonData.entity);
			var htmlContent = '<a target="_blank" class="brd-crm brd-crm-link" href="'
					+ getLocationOrigin()
					+ '/findcompany.do?verticalName='
					+ entityJson[0].breadCrumbProfile
					+ '">'
					+ entityJson[0].breadCrumbProfile + '</a>';
			
			for(var i=1; i<entityJson.length; i++) {
				htmlContent += '<span class="brd-crm-divider">&gt;&gt;</span>';
				htmlContent += '<a target="_blank" class="brd-crm brd-crm-link" href="'
						+ entityJson[i].breadCrumbUrl
						+ '">'
						+ entityJson[i].breadCrumbProfile + '</a>';
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
	var url = getLocationOrigin() +'/rest/profile/'+companyProfileName+'/regions';
	callAjaxGET(url, paintCompanyRegions, true);
}

function paintCompanyRegions(data) {
	var response= $.parseJSON(data);
	if(response != undefined) {
		var result = $.parseJSON(response.entity);
		if(result != undefined && result.length > 0) {
			var regionsHtml = "";
			$.each(result,function(i, region) {
				regionsHtml = regionsHtml+'<div class="bd-hr-item-l1 comp-region" data-start=0 data-batch=5 data-openstatus="closed" data-regionid = '+region.regionId+'>';
				regionsHtml = regionsHtml+'	<div class="bd-hr-item bd-lt-l1 clearfix">';
				regionsHtml = regionsHtml+'    <div class="prf-public-txt bd-hr-txt cursor-pointer region-link" data-profilename="'+region.profileName+'">'+region.region+'</div>';
				regionsHtml = regionsHtml+'	</div>';
				regionsHtml = regionsHtml+'</div>';
				regionsHtml = regionsHtml+'	   <div class="bd-hr-item-l2 hide" id="comp-region-branches-'+region.regionId+'"></div>';
			});
			$("#comp-regions-content").html(regionsHtml);
			$("#comp-hierarchy").show();
		}
	}
}

function fetchBranchesForRegion(regionId) {
	var url = getLocationOrigin() +'/rest/profile/region/'+regionId+'/branches';
	$("#regionid-hidden").val(regionId);
	callAjaxGET(url, paintBranchesForRegion, true);
}

function paintBranchesForRegion(data) {
	var responseJson = $.parseJSON(data);
	var branchesHtml = "";
	var regionId = $("#regionid-hidden").val();
	if(responseJson != undefined) {
		var result = $.parseJSON(responseJson.entity);
		if(result != undefined && result.length > 0) {
			$.each(result,function(i,branch) {
				branchesHtml = branchesHtml +'<div class="bd-hr-item-l2 comp-region-branch" data-openstatus="closed" data-start=0 data-batch=5 data-branchid="'+branch.branchId+'">';
				branchesHtml = branchesHtml +'	<div class="bd-hr-item bd-lt-l2 clearfix">';
				branchesHtml = branchesHtml +'		<div class="prf-public-txt bd-hr-txt cursor-pointer branch-link" data-profilename="'+branch.profileName+'">'+branch.branch+'</div>';
				branchesHtml = branchesHtml +'	</div>';
				branchesHtml = branchesHtml +'</div>' ;
				branchesHtml = branchesHtml +'		<div class="bd-hr-item-l3 hide" id="comp-branch-individuals-'+branch.branchId+'"></div>';
			});
			
			$("#region-hierarchy").show();
			if($("#region-branches").length > 0) {
				$("#region-branches").html(branchesHtml);
			}
			else {
				$("#comp-region-branches-"+regionId).html(branchesHtml).slideDown(200);
			}
		}
	}
}

/**
 * Method to bind the element whose class is provided to fetch individuals under that branch
 * @param bindingClass
 */
function bindClickToFetchBranchIndividuals(bindingClass) {
	$("."+bindingClass).unbind('click');
	$("."+bindingClass).click(function(e){
		e.preventDefault();
		if($(this).data("openstatus") == "closed") {
			fetchIndividualsForBranch($(this).data('branchid'));
			$(this).data("openstatus","open");
		}else {
			$('#comp-branch-individuals-'+$(this).data('branchid')).slideUp(200);
			$(this).data("openstatus","closed");
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
			$.each(result, function(i, individual) {
				if (individual.contact_details != undefined) {
					individualsHtml += '<div class="bd-hr-item-l3 comp-individual" data-agentid=' + individual.iden + '>';
					individualsHtml += '	<div class="bd-hr-item bd-lt-l3 clearfix">';
					if (individual.profileImageUrl != undefined && individual.profileImageUrl.trim() != "") {
						individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"><img class="hr-ind-img" src="'+individual.profileImageUrl+'"/></div>';
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
			$('div[data-branchid="' + branchId + '"]').attr('data-start',parseInt(start + result.length));
			if( start == 0 && result.length == batchSize ) {
				showMoreHtml = '<div class="show-more-btn">Show More</div>';
			} else if (start != 0 && result.length < batchSize) {
				if($("#branch-individuals").length > 0) {
					$("#branch-individuals").find(".show-more-btn").remove();
				}else {
					$("#comp-branch-individuals-"+branchId).find(".show-more-btn").remove();
				}
			}
			
			$("#branch-hierarchy").show();
			if($("#branch-individuals").length > 0) {
				if($("#branch-individuals").children(".show-more-btn").length > 0){
					$("#branch-individuals").find(".show-more-btn").before(individualsHtml);	
				} else {
					$("#branch-individuals").append(individualsHtml);
				}
				if(showMoreHtml != "") {
					$("#branch-individuals").append(showMoreHtml);	
				}
			}
			else {
				if(start == 0) {
					$("#comp-branch-individuals-"+branchId).html(individualsHtml).slideDown(200);
				} else {
					if($("#comp-branch-individuals-"+branchId).children(".show-more-btn").length > 0){
						$("#comp-branch-individuals-"+branchId).find(".show-more-btn").before(individualsHtml);	
					} else {
						$("#comp-branch-individuals-"+branchId).append(individualsHtml);
					}
					
				}
				
				if(showMoreHtml != "") {
					$("#comp-branch-individuals-"+branchId).append(showMoreHtml);	
				}
			}
		}
	} else {
		if($("#branch-individuals").length > 0) {
			$("#branch-individuals").find(".show-more-btn").remove();
		}else {
			$("#comp-branch-individuals-"+branchId).find(".show-more-btn").remove();
		}
	}
}

// Attach onclick event on show more button
$(document).on('click', '.show-more-btn', function(e) {
	var branchId = $(this).parent().prev('div').attr('data-branchid');
	fetchIndividualsForBranch(branchId);
});

function bindClickToFetchIndividualProfile(bindingClass) {
	$("."+bindingClass).click(function(e){
		e.stopPropagation();
		var agentProfileName = $(this).data("profilename");
		//var url = window.location.origin +"/pages/"+agentProfileName;
		var url = getLocationOrigin() +"/pages/"+agentProfileName;
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
				$.each(result, function(i, individual) {
					if (individual.contact_details != undefined){
						individualsHtml += '<div class="bd-hr-item-l2 comp-region-individual" data-agentid=' + individual.iden + '>';
						individualsHtml += '	<div class="bd-hr-item bd-lt-l3 clearfix">';
						if (individual.profileImageUrl != undefined && individual.profileImageUrl.trim() != "") {
							individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"><img class="hr-ind-img" src="'+individual.profileImageUrl+'"/></div>';
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
				if($("#region-branches").length > 0) {
					$("#region-branches").append(individualsHtml);
				}
				else {
					$("#comp-region-branches-"+regionId).append(individualsHtml).slideDown(200);
				}
		}
	}
}

function fetchCompanyIndividuals() {
	var url = getLocationOrigin() +'/rest/profile/'+companyProfileName+'/individuals';
	callAjaxGET(url, paintCompanyIndividuals, true);
}

function paintCompanyIndividuals(data) {
	var response= $.parseJSON(data);
	if (response != undefined && response.entity != "") {
		var result = $.parseJSON(response.entity);
		if (result != undefined && result.length > 0) {
			var compIndividualsHtml = "";
			$.each(result, function(i, compIndividual) {
				if (compIndividual.contact_details != undefined) {
					compIndividualsHtml += '<div class="bd-hr-item-l1 comp-individual" data-agentid=' + compIndividual.iden + '>';
					compIndividualsHtml += '	<div class="bd-hr-item bd-lt-l3 clearfix">';
					
					if (compIndividual.profileImageUrl != undefined && compIndividual.profileImageUrl.trim() != "") {
						compIndividualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"><img class="hr-ind-img" src="'+compIndividual.profileImageUrl+'"/></div>';
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
	var url = getLocationOrigin() +'/rest/profile/'+companyProfileName+'/branches';
	callAjaxGET(url, paintCompanyBranches, true);
}

function paintCompanyBranches(data) {
	var response = $.parseJSON(data);
	if(response != undefined) {
		var result = $.parseJSON(response.entity);
		if(result != undefined && result.length > 0) {
			var compBranchesHtml = "";
			$.each(result,function(i,branch) {
				compBranchesHtml = compBranchesHtml +'<div class="bd-hr-item-l1 comp-branch" data-start=0 data-batch=5 data-openstatus="closed" data-branchid="'+branch.branchId+'">';
				compBranchesHtml = compBranchesHtml +'	<div class="bd-hr-item bd-lt-l2 clearfix">';
				compBranchesHtml = compBranchesHtml +'		<div class="prf-public-txt bd-hr-txt cursor-pointer branch-link" data-profilename="'+branch.profileName+'">'+branch.branch+'</div>';
				compBranchesHtml = compBranchesHtml +'	</div>';
				compBranchesHtml = compBranchesHtml +'</div>' ;
				compBranchesHtml = compBranchesHtml +'		<div class="lpsub-2 hide" id="comp-branch-individuals-'+branch.branchId+'"></div>';
			});
			$("#comp-hierarchy").show();
			$("#comp-regions-content").append(compBranchesHtml);
		}
	}
}

function paintReviews(result){
	
	//Check if there are more reviews left
	if(reviewsNextBatch == undefined || reviewsNextBatch.length <= numOfRows)
		fetchReviewsScroll(true);
	
	var resultSize = result.length;
	$('.ppl-review-item-last').removeClass('ppl-review-item-last').addClass('ppl-review-item');
	
	var reviewsHtml = "";
	$.each(result, function(i, reviewItem) {
		var scoreFixVal = 1;
		/*if (reviewItem.score % 1 == 0) {
			scoreFixVal = 0;
		}*/
		var date = Date.parse(reviewItem.modifiedOn);
		var lastItemClass = "ppl-review-item";
		if (i == resultSize - 1) {
			lastItemClass = "ppl-review-item-last";
        }
		var custName = reviewItem.customerFirstName;
		if(reviewItem.customerLastName != undefined){
			custName += ' ' + reviewItem.customerLastName;
		}
		var custNameArray = custName.split(' ');
		var custDispName = custNameArray[0];
		if(custNameArray[1] != undefined && custNameArray[1].trim() != ""){
			custDispName += ' '+custNameArray[1].substr(0,1).toUpperCase()+'.';
		}
		reviewsHtml = reviewsHtml +
			'<div class="' + lastItemClass + '" data-cust-first-name=' + reviewItem.customerFirstName
				+ ' data-cust-last-name=' + reviewItem.customerLastName + ' data-agent-name=' + reviewItem.agentName
				+ ' data-rating=' + reviewItem.score + ' data-review="' + reviewItem.review + '" data-customeremail="'
				+ reviewItem.customerEmail + '" data-agentid="' + reviewItem.agentId + '" survey-mongo-id="' + reviewItem._id + '">';
		reviewsHtml += '	<div class="ppl-header-wrapper clearfix">';
		reviewsHtml += '		<div class="float-left ppl-header-left">';    
		reviewsHtml += '			<div class="ppl-head-1">'+custDispName+'</div>';
		if (date != null) {
			date = convertUserDateToLocale(date);
			reviewsHtml += '		<div class="ppl-head-2">' + date.toString("MMMM d, yyyy") + '</div>'; 
		}
		
		reviewsHtml += '		</div>';
		reviewsHtml += '    	<div class="float-right ppl-header-right">';
		reviewsHtml += '    	    <div class="st-rating-wrapper maring-0 clearfix review-ratings" data-source="'+reviewItem.source+'" data-rating="'+reviewItem.score+'"></div>';
		reviewsHtml += '		</div>';
		reviewsHtml += '	</div>';
		
		if (reviewItem.review.length > 250) {
			reviewsHtml += '<div class="ppl-content"><span class="review-complete-txt">'+reviewItem.review+'</span><span class="review-less-text">' + reviewItem.review.substr(0,250) + '</span><span class="review-more-button">More</span>';
		} else {
			reviewsHtml += '<div class="ppl-content">'+reviewItem.review;
		}
		if(reviewItem.source == "Zillow") {
			reviewsHtml += '<a class="view-zillow-link" href="'+reviewItem.sourceId+'"  target="_blank">View on zillow</a>';
		}
		if(reviewItem.customerLastName != null && reviewItem.customerLastName != "")
			reviewItem.customerLastName = reviewItem.customerLastName.substring( 0, 1 ).toUpperCase() + ".";
		else
			reviewItem.customerLastName = "";
		if(reviewItem.agentName == undefined || reviewItem.agentName == null)
			reviewItem.agentName = "us";
		reviewsHtml += '	</div>';
		
		reviewsHtml += '	<div class="ppl-share-wrapper clearfix">';
		reviewsHtml += '		<div class="float-left blue-text ppl-share-shr-txt">Share</div>';
		reviewsHtml += '		<div class="float-left icn-share icn-plus-open"></div>';
		reviewsHtml += '		<div class="float-left clearfix ppl-share-social hide">';
		reviewsHtml += '			<span id ="fb_' + i + '"class="float-left ppl-share-icns icn-fb icn-fb-pp" onclick="getImageandCaption(' + i + ');" title="Facebook" data-link="https://www.facebook.com/dialog/feed?' + reviewItem.faceBookShareUrl + '&link=' + reviewItem.completeProfileUrl + '&description=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + custDispName + ' for ' + reviewItem.agentName + ' at SocialSurvey - ' + reviewItem.review + ' .&redirect_uri=https://www.facebook.com"></span>';
		reviewsHtml += '            <input type="hidden" id="twttxt_' + i + '" class ="twitterText_loop" value ="' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + custDispName + ' for ' + reviewItem.agentName + ' at SocialSurvey - ' + reviewItem.review + '"/></input>';
		reviewsHtml += '			<span id ="twitt_' + i + '" class="float-left ppl-share-icns icn-twit icn-twit-pp" onclick="twitterFn(' + i + ');" title="Twitter" data-link="https://twitter.com/intent/tweet?text=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + custDispName + ' for ' + reviewItem.agentName + ' at SocialSurvey - ' + reviewItem.review + ' &url='+ reviewItem.completeProfileUrl +'"></span>';	
		reviewsHtml += '			<span class="float-left ppl-share-icns icn-lin icn-lin-pp" title="LinkedIn" data-link="https://www.linkedin.com/shareArticle?mini=true&url=' + reviewItem.completeProfileUrl + '&title=&summary=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + custDispName + ' for ' + reviewItem.agentName +' at SocialSurvey - ' + reviewItem.review + '&source="></span>';
		reviewsHtml += '			<span class="float-left" title="Google+"> <button class="g-interactivepost float-left ppl-share-icns icn-gplus" data-contenturl="' + reviewItem.completeProfileUrl + '" data-clientid="' + reviewItem.googleApi + '"data-cookiepolicy="single_host_origin" data-prefilltext="' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + custDispName + ' for ' + reviewItem.agentName + ' at SocialSurvey - ' + reviewItem.review + '" data-calltoactionlabel="USE"'+''+'data-calltoactionurl=" ' + reviewItem.completeProfileUrl + '"> <span class="icon">&nbsp;</span> <span class="label">share</span> </button> </span>';
		reviewsHtml += '		</div>';
		reviewsHtml += '		<div class="float-right" style="margin: 0 -5px;">';
		if(reviewItem.source != "Zillow")
			reviewsHtml += '			<div class="report-abuse-txt report-txt prof-report-abuse-txt">Report Abuse</div>';
		reviewsHtml += '		</div>';
		reviewsHtml += '		<div class="float-left icn-share icn-remove icn-rem-size hide"></div>';
		reviewsHtml += '	</div>';
		reviewsHtml += '</div>';
	});
	
	if(result.length > 0){
		$('#reviews-container').show();
	}
	
	if($("#profile-fetch-info").attr("fetch-all-reviews") == "true" && startIndex == 0) {
		$("#prof-review-item").html(reviewsHtml);
	}else {
		$("#prof-review-item").append(reviewsHtml);
	}
	
	$("#prof-reviews-header").parent().show();
	$(".review-ratings").each(function() {
		changeRatingPattern($(this).data("rating"), $(this), false, $(this).data("source"));
	});
	$('.icn-plus-open').click(function(){
        $(this).hide();
        $(this).parent().find('.ppl-share-social,.icn-remove').show();
    });
    
    $('.icn-remove').click(function(){
        $(this).hide();
        $(this).parent().find('.ppl-share-social').hide();
        $(this).parent().find('.icn-plus-open').show();
    });
    
	$('.ppl-share-icns').bind('click', function() {
		var link = $(this).attr('data-link');
		var title = $(this).attr('title');
		if (link == undefined || link == "") {
			return false;
		}
		window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
	});
}

$(document).on('click','.review-more-button',function(){
	$(this).parent().find('.review-less-text').hide();
	$(this).parent().find('.review-complete-txt').show();
	$(this).hide();
});

// Report abuse click event.
$(document).on('click', '.prof-report-abuse-txt', function(e) {
	var reviewElement = $(this).parent().parent().parent();
	var payload = {
		"customerEmail" : reviewElement.attr('data-customeremail'),
		"agentId" : reviewElement.attr('data-agentid'),
		"firstName" : reviewElement.attr('data-cust-first-name'),
		"lastName" : reviewElement.attr('data-cust-last-name'),
		"agentName" : reviewElement.attr('data-agent-name'),
		"review" : reviewElement.attr('data-review'),
		"surveyMongoId" : reviewElement.attr('survey-mongo-id')
	};
	$("#report-abuse-txtbox").val('');
	$('#report-abuse-cus-name').val('');
	$('#report-abuse-cus-email').val('');
	
	// Unbind click events for button
	$('.rpa-cancel-btn').off('click');
	$('.rpa-report-btn').off('click');
	
	$('#report-abuse-overlay').show();
	$('.rpa-cancel-btn').on('click', function() {
		$('#report-abuse-overlay').hide();
	});
	$('.rpa-report-btn').on('click', function() {
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

function validateReportAbuseForm(reportText, cusName, cusEmail) {
	// check if custname is empty
	if(cusName == undefined || cusName == ""){
		$('#overlay-toast').html('Please enter valid name!');
		showToast();
		return false;
	}
	
	// check if custemail is valid
	if(cusEmail == undefined || cusEmail == "" || !emailRegex.test(cusEmail)){
		$('#overlay-toast').html('Please enter a valid email address!');
		showToast();
		return false;
	}
	
	// check if report text is empty
	if(reportText == undefined || reportText == ""){
		$('#overlay-toast').html('Please enter why you want to report the review!');
		showToast();
		return false;
	}
	
	return true;
}

function confirmReportAbuse(payload) {
	callAjaxGetWithPayloadData('/rest/profile/surveyreportabuse', function(status) {
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

$(document).scroll(function(){
	if ((window.innerHeight + window.pageYOffset) >= ($('#prof-review-item').offset().top + $('#prof-review-item').height()) ){
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
		if (isFetchReviewAjaxRequestRunning)
			return; // Return if ajax request is still running
		if(!isNextBatch && reviewsNextBatch != undefined && reviewsNextBatch.length > 0) {
			var reviewsToShow = reviewsNextBatch.slice(0, numOfRows);
			if(reviewsNextBatch.length > numOfRows) {
				reviewsNextBatch = reviewsNextBatch.slice(numOfRows);
			} else {
				reviewsNextBatch = [];
			}
			paintReviews(reviewsToShow);
		} else {
			if(stopFetchReviewPagination) return; //Return if pagination is stopped
			startIndex = startIndex + numOfRows;
			var profileLevel = $("#profile-fetch-info").attr("profile-level");
			if (showAllReviews)
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden,
						startIndex, numOfRows, 0, true, isNextBatch);
			else
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden,
						startIndex, numOfRows, minScore, true, isNextBatch);
		}
	}
}

function fetchZillowReviewsBasedOnProfile(profileLevel, currentProfileIden){
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
	url += currentProfileIden + "/zillowreviews";
	callAjaxGET(url, fetchZillowReviewsCallBack, true);
}

function fetchZillowReviewsCallBack(data) {
	
}

function fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden,
		startIndex, numRows, minScore , isAsync, isNextBatch) {
	
	if(startIndex == 0) {
		stopFetchReviewPagination = false;
		reviewsNextBatch = [];
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
	callAjaxGET(url, function(data) {
		isFetchReviewAjaxRequestRunning = false;
		var responseJson = $.parseJSON(data);
		if (responseJson != undefined) {
			var result = $.parseJSON(responseJson.entity);
			if(result == undefined || result.length < numRows) {
				stopFetchReviewPagination = true; //Stop pagination if reviews fetch are less than the batch size
			}
			if (result != undefined && result.length > 0) {
				if(isNextBatch) {
					reviewsNextBatch = reviewsNextBatch.concat(result);
					if(reviewsNextBatch.length <= numRows) {
						fetchReviewsScroll(true);
					}
				} else {
					paintReviews(result);				
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
	if(notRecommended != undefined && typeof(notRecommended) === "boolean") {
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
	if(responseJson != undefined) {
		var reviewsSizeHtml = responseJson.entity;
		if(reviewsSizeHtml > 0) {
			if(reviewsSizeHtml == 1) {
				reviewsSizeHtml = reviewsSizeHtml +' additional review not recommended';
			}else {
				reviewsSizeHtml = reviewsSizeHtml +' additional reviews not recommended';
			}
			
			$("#prof-hidden-review-count")
			.attr("data-nr-review-count", responseJson.entity)
			.html(reviewsSizeHtml);
			
			$("#prof-hidden-review-count").click(function(){
				$('#prof-review-item').html('');
				$(this).hide();
				startIndex = 0;
				$("#profile-fetch-info").attr("fetch-all-reviews", "true");
				$(window).scrollTop($('#reviews-container').offset().top);
				showAllReviews = true;
				var profileLevel = $("#profile-fetch-info").attr("profile-level");
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, 0 , true);
			});
		}
	}
}

$(document).on('click', '#sort-by-feature',function(e){
	e.stopImmediatePropagation();
	$("#prof-hidden-review-count").show();
	$('#prof-review-item').html('');
	startIndex = 0;
	$("#profile-fetch-info").attr("fetch-all-reviews","false");
	showAllReviews = false;
	var profileLevel = $("#profile-fetch-info").attr("profile-level");
	reviewsSortBy = 'feature';
	fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, minScore , true);
});

$(document).on('click', '#sort-by-date',function(e){
	e.stopImmediatePropagation();
	$("#prof-hidden-review-count").hide();
	$('#prof-review-item').html('');
	startIndex = 0;
	$("#profile-fetch-info").attr("fetch-all-reviews","true");
	showAllReviews = true;
	var profileLevel = $("#profile-fetch-info").attr("profile-level");
	reviewsSortBy = 'date';
	fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, 0 , true);
});

function fetchRegionProfile() {
	var result = profileJson;
	paintProfilePage(result);
	paintIndividualDetails(result);
	fetchBranchesForRegion(result.iden);
	fetchIndividualsForRegion(result.iden);
	if(result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;
	fetchReviewsBasedOnProfileLevel('REGION', result.iden,startIndex,numOfRows,minScore , true);
	fetchReviewsCountBasedOnProfileLevel('REGION',result.iden, paintHiddenReviewsCount, 0, minScore, true);
	fetchZillowReviewsBasedOnProfile('REGION',result.iden);
}

function fetchBranchProfile() {
	var result = profileJson;
	paintProfilePage(result);
	paintIndividualDetails(result);
	fetchIndividualsForBranch(result.iden);
	if(result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;
	fetchReviewsBasedOnProfileLevel('BRANCH', result.iden, startIndex, numOfRows, minScore);
	fetchReviewsCountBasedOnProfileLevel('BRANCH',result.iden, paintHiddenReviewsCount, 0, minScore, true);
	fetchZillowReviewsBasedOnProfile('BRANCH',result.iden);
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
			$.each(licenses.authorized_in, function(i, authorizedIn) {
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
			individualDetailsHtml = individualDetailsHtml + '<div class="lp-pos-row-1 lp-row clearfix">' + positionObj.name + '</div>';
			if (positionObj.title) {
				individualDetailsHtml = individualDetailsHtml + '<div class="lp-pos-row-2 lp-row clearfix">' + positionObj.title + '</div>';
			}
			if (positionObj.startTime) {
				var startDateDisplay = constructDate(positionObj.startTime.split("-"));
				if (!positionObj.isCurrent && positionObj.endTime) {
					var endDateDisplay = constructDate(positionObj.endTime.split("-"));
					individualDetailsHtml = individualDetailsHtml + '<div class="lp-pos-row-3 lp-row clearfix">' + startDateDisplay + " - " + endDateDisplay + '</div>';
				} else {
					individualDetailsHtml = individualDetailsHtml + '<div class="lp-pos-row-3 lp-row clearfix">' + startDateDisplay + ' - Current</div>';
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
		$.each(result.associations, function(i, associations) {
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
		$.each(result.achievements, function(i, achievements) {
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
	$('.lph-dd').click(function() {
		if($(this).next('.lph-dd-content').is(':visible')){
			$(this).next('.lph-dd-content').slideToggle(200);
			$(this).addClass('lph-arrow-closed').removeClass('lph-arrow-open');
		}else{
			$('.lph-dd-content').hide();
			$('.lph-dd').addClass('lph-arrow-closed').removeClass('lph-arrow-open');
			$(this).removeClass('lph-arrow-closed').addClass('lph-arrow-open');
			$(this).next('.lph-dd-content').slideToggle(200);
		}
	});

	$('.lph-dd:nth(0)').trigger('click');
}

function fetchAgentProfile(){
	var result = profileJson;
	paintProfilePage(result);
	paintIndividualDetails(result);
	if(result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;
	fetchReviewsBasedOnProfileLevel('INDIVIDUAL', result.iden, startIndex, numOfRows, minScore , true);
	fetchReviewsCountBasedOnProfileLevel('INDIVIDUAL',result.iden, paintHiddenReviewsCount, 0, minScore, true);
	fetchZillowReviewsBasedOnProfile('INDIVIDUAL',result.iden);
}

function findProList(iden,searchcritrianame){
	if(iden == undefined || iden == ""){
		return;
	}
	var url = "";
	var profileLevel = $("#profile-fetch-info").attr("profile-level");
	if(profileLevel == 'INDIVIDUAL'){
		initSurveyReview(iden);
	}else {
		url = getLocationOrigin() +"/initfindapro.do?profileLevel="+profileLevel+"&iden="+iden+"&searchCriteria="+searchcritrianame;
		 window.open(url, "_blank");
	}
	
}

function downloadVCard(agentName){
	if(agentName == undefined || agentName == ""){
		return;
	}
	var url = getLocationOrigin() + "/rest/profile/downloadvcard/"+agentName;
	window.open(url, "_blank");
}

$('#prof-posts').on('scroll',function(){
	var scrollContainer = this;
	if (scrollContainer.scrollTop === scrollContainer.scrollHeight
				- scrollContainer.clientHeight) {
		if(publicPostsNextBatch.length > 0) {
			paintPublicPosts(publicPostsNextBatch.slice(0, publicPostNumRows));
			if(publicPostsNextBatch.length > publicPostNumRows) {
				publicPostsNextBatch = publicPostsNextBatch.slice(publicPostNumRows);
			} else {
				publicPostsNextBatch = [];
			}
			if(publicPostsNextBatch.length <= publicPostNumRows) {
				fetchPublicPosts(true);
			}
		} else {
			fetchPublicPosts(false);
		}
	}
});

//Function to paint posts
function fetchPublicPosts(isNextBatch) {
	
	if(isPublicPostAjaxRequestRunning) return; // Return if request is running
	
	if(doStopPublicPostPagination) return; //If pagination has stopped return the request
	
	var profileLevel = $("#profile-fetch-info").attr("profile-level");
	
	var url = getLocationOrigin() + "/rest/profile/";
	if(profileLevel == 'COMPANY'){
		//Fectch the reviews for company
		url += "company/"+currentProfileName+"/posts?start="+publicPostStartIndex+"&numRows="+publicPostNumRows;
	}
	else if(profileLevel == 'REGION'){
		//Fetch the reviews for region
		url += "region/"+companyProfileName+"/"+currentProfileName+"/posts?start="+publicPostStartIndex+"&numRows="+publicPostNumRows;
	}
	else if(profileLevel == 'BRANCH') {
		//Fetch the reviews for branch
		url += "branch/"+companyProfileName+"/"+currentProfileName+"/posts?start="+publicPostStartIndex+"&numRows="+publicPostNumRows;
	}
	else if(profileLevel == 'INDIVIDUAL'){
		//Fetch the reviews for individual
		url += currentProfileName+"/posts?start="+publicPostStartIndex+"&numRows="+publicPostNumRows;
	}
	isPublicPostAjaxRequestRunning = true;
	callAjaxGET(url, function(data) {
		isPublicPostAjaxRequestRunning = false;
		var posts = $.parseJSON(data);
		posts = $.parseJSON(posts.entity);
		
		//Check if request is for next batch
		if(isNextBatch) {
			publicPostsNextBatch = publicPostsNextBatch.concat(posts);
		} else {
			paintPublicPosts(posts);
		}
		publicPostStartIndex += posts.length;
		if (publicPostStartIndex < publicPostNumRows || posts.length < publicPostNumRows){
			doStopPublicPostPagination = true;
		}
		
		if(publicPostsNextBatch.length <= publicPostNumRows) {
			fetchPublicPosts(true);
		}
	}, true);
}

function paintPublicPosts(posts) {
	
	var divToPopulate = "";
	$.each(posts, function(i, post) {
		var iconClass = "";
		var href="javascript:void(0)";
		if (post.source == "google")
			iconClass = "icn-gplus";
		else if (post.source == "SocialSurvey")
			iconClass = "icn-ss";
		else if (post.source == "facebook"){
			iconClass = "icn-fb";
			href="http://www.facebook.com/"+post.postId;
		}
		else if (post.source == "twitter"){
			iconClass = "icn-twit";
			var res = post.postText.split("http");
			href="http"+res[1];
		}
		else if (post.source == "linkedin")
			iconClass = "icn-lin";
		if(typeof post.postUrl!=  "undefined" ){
			 href= post.postUrl;
		}
		var hrefComplet='<a href='+href+' target="_blank">';
		divToPopulate += '<div class="tweet-panel-item bord-bot-dc clearfix">'
			+ hrefComplet
			+ '<div class="tweet-icn '+ iconClass +' float-left"></div>'
			+ "</a>"
			+ '<div class="tweet-txt float-left">'
				+ '<div class="tweet-text-main">' + linkify(post.postText) + '</div>'
				+ '<div class="tweet-text-link"><em>' + post.postedBy + '</em></div>'
				+ '<div class="tweet-text-time"><em>' + convertUserDateToWeekFormt(new Date(post.timeInMillis)) + '</em></div>'
			+ '	</div>'
		+ '</div>';
	});
	

	if (publicPostStartIndex == 0) {
		if (posts.length > 0) {
			$('#recent-post-container').show();
		} else {
			$('#recent-post-container').remove();
		}
		$('#prof-posts').html(divToPopulate);
		$('#prof-posts').perfectScrollbar();
	} else {
		$('#prof-posts').append(divToPopulate);
		$('#prof-posts').perfectScrollbar('update');
	}
}

$('body').on('click',".branch-link",function(e) {
	e.stopPropagation();
	var branchProfileName = $(this).data("profilename");
	var url = getLocationOrigin() +"/pages/office/"+companyProfileName+"/"+branchProfileName;
	window.open(url, "_blank");
});

$('body').on('click',".individual-link",function(e) {
	e.stopPropagation();
	var agentProfileName = $(this).data("profilename");
	var url = getLocationOrigin() +"/pages/"+agentProfileName;
	window.open(url, "_blank");
});

$('body').on('click',".region-link",function(e) {
	e.stopPropagation();
	var regionProfileName = $(this).data("profilename");
	var url = getLocationOrigin() +"/pages/region/"+companyProfileName+"/"+regionProfileName;
	window.open(url, "_blank");
});


$('body').on("click touchstart",".comp-branch,.comp-region-branch",function(e){
	e.preventDefault();
	if($(this).data("openstatus") == "closed") {
		fetchIndividualsForBranch($(this).data('branchid'));
		$(this).data("openstatus","open");
		$(this).attr("data-openstatus","open");
	}else {
		$('#comp-branch-individuals-'+$(this).data('branchid')).slideUp(200);
		$(this).data("openstatus","closed");
		$(this).attr('data-start',0);
		$(this).attr("data-openstatus","closed");
	}
});

$('body').on("click touchstart",".comp-region",function(){
	if($(this).data("openstatus") == "closed") {
		$('#comp-region-branches-'+$(this).data('regionid')).html("");
		fetchBranchesForRegion($(this).data('regionid'));
		fetchIndividualsForRegion($(this).data('regionid'));
		$(this).data("openstatus","open");
		$(this).attr("data-openstatus","open");
	}else {
		$('#comp-region-branches-'+$(this).data('regionid')).slideUp(200);
		$(this).data("openstatus","closed");
		$(this).attr("data-openstatus","closed");
	}
	
});

function constructDate(dateStr) {
	var dateDisplay = "";
	if (typeof dateStr[0] != 'undefined' && dateStr[0] != '0' && typeof dateStr[1] != 'undefined'  && dateStr[1] != '0') {
		dateDisplay = monthNames[dateStr[0] - 1] + " " + dateStr[1];
	} else if (typeof dateStr[0] != 'undefined' && dateStr[0] != '0') {
		dateDisplay = monthNames[dateStr[0] - 1];
	} else if (typeof dateStr[1] != 'undefined'  && dateStr[1] != '0') {
		dateDisplay = dateStr[1];
	}
	
	return dateDisplay;
}

function getImageandCaption(index)
{
	var pictureandCaptionLink = "";
	var imgid="";
	var name = "";
	var addrline1 = "";
	var addrline2="";
	var fblink = $("#fb_"+index).data('link');
	if(document.getElementById("prof-image")!= null && document.getElementById("prof-image").getAttribute("src")!= undefined)
		{
		imgid = document.getElementById("prof-image").getAttribute("src");
		
		}
	if($(".prof-name")[0] != undefined && $(".prof-name")[0].innerHTML != undefined )
		{
		name= $(".prof-name")[0].innerHTML;
	
		}
	
	if($(".prof-addline2")[0] != undefined && $(".prof-addline2")[0].innerHTML != undefined )
	{
		addrline2= $(".prof-addline2")[0].innerHTML;
		
	}
	
	if($(".prof-addline1")[0] != undefined && $(".prof-addline1")[0].innerHTML != undefined )
	{
		addrline1= $(".prof-addline1")[0].innerHTML;
	
	}

	 pictureandCaptionLink = "&picture="+imgid+"&caption="+name+","+addrline2+","+addrline1;
	fblink = fblink.concat(pictureandCaptionLink);
	if(document.getElementById('fb_'+index) != null)
		{
		document.getElementById('fb_'+index).setAttribute('data-link',fblink);
		
		}


}

function twitterFn(loop) {

	var twitLink = $("#twitt_" + loop).data('link');
	var String = twitLink.substring(twitLink.indexOf("=") + 1, twitLink
			.lastIndexOf("&"));
	var twitId = 'twttxt_' + loop;
	var twitText = $("#" + twitId).val();
	var length = twitText.length;
	if (length > 109) {

		var twittStrnDot = "...";
		var substringed = twitText.substring(0, 105);
		var finalString = substringed.concat(twittStrnDot);
		$("#" + twitId).val(finalString);
		twitLink = twitLink.replace(String, finalString);
		if (document.getElementById('twitt_' + loop) != null) {
			document.getElementById('twitt_' + loop).setAttribute('data-link',
					twitLink);
		}
	}
}

(function() {
	var po = document.createElement('script');
	po.type = 'text/javascript';
	po.async = true;
	po.src = 'https://apis.google.com/js/client:plusone.js';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(po, s);
})();