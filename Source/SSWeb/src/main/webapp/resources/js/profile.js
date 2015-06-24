var companyProfileName = $("#company-profile-name").val();
var currentProfileIden = "";
var startIndex = 0;
var numOfRows = 5;
var minScore=0;
var publicPostStartIndex = 0;
var publicPostNumRows = 4;
var currentProfileName;
var doStopPublicPostPagination = false;
var reviewsSortBy = 'default';
var showAllReviews = false;
var monthNames = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
		"Sep", "Oct", "Nov", "Dec" ];
var profileJson;

$(document).ajaxStop(function() {
	adjustImage();
});

function adjustImage(){
    $('.mobile-tabs').children('.mob-icn-active').click();
    var windW = $(window).width();
    if(windW < 768){
        var imgW = $('#prof-image').width();
        $('#prof-image').height(imgW * 0.7);
        // var h2 = $('.prog-img-container').height() - 11;
        var rowW = $('.lp-con-row').width() - 55 - 10;
        $('.lp-con-row-item').width(rowW+'px');
        $('.footer-main-wrapper').hide();
    }else{
        //$('.prof-name-container,#prof-image').height(200);
        $('.lp-con-row-item').width('auto');
        $('.footer-main-wrapper').show();
    }
}


function fetchCompanyProfile() {
	startIndex = 0;
	fetchCompanyProfileCallBack();
	/*var url = window.location.origin +'/rest/profile/'+companyProfileName;
	callAjaxGET(url, fetchCompanyProfileCallBack, true);*/
}

function fetchCompanyProfileCallBack(data) {
	var result = profileJson;
	
	paintProfilePage(result);
	paintIndividualDetails(result);
	//fetchAverageRatings(result.iden);
	paintAverageRatings();
	fetchCompanyRegions();
	fetchCompanyBranches();
	fetchCompanyIndividuals();
	minScore = 0;
	if(result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	fetchReviewsCountForCompany(result.iden, paintAllReviewsCount);
	$("#profile-fetch-info").attr("fetch-all-reviews","false");
	fetchReviewsForCompany(result.iden,startIndex,numOfRows,minScore);	
}

function paintProfilePage(result) {
	//TODO : Remove all the js iteration for profileJson
	if(result != undefined && result != "") {
		currentProfileIden = result.iden;
		currentProfileName = result.profileName;
		var contactDetails = result.contact_details;
		var profileLevel = $("#profile-fetch-info").attr("profile-level");
		
		// paint public  posts
		paintPublicPosts();
		
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
					if (addressData.name && addressData.name != "") {
						address += addressData.name;
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
    	
    	$("#read-write-share-btn").click(function(e){
        	e.stopPropagation();
        	findProList(result.iden,result.contact_details.name);
        });
        
        $('#mob-review-btn').click(function(e) {
        	e.stopPropagation();
        	findProList(result.iden,result.contact_details.name);
        });
	}
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

function fetchAverageRatings(companyId) {
	var url = window.location.origin +'/rest/profile/company/'+companyId+'/ratings';
	callAjaxGET(url, paintAverageRatings, true);
}

function paintAverageRatings() {
	/*var responseJson = $.parseJSON(data);
	if(responseJson != undefined) {*/
		var rating = $('input[name="averageRatings"]').val();
		changeRatingPattern(rating,$("#rating-avg-comp"),true);
		//$('#prof-schema-agent-rating').html(parseFloat(rating).toFixed(2));
}

function fetchCompanyRegions() {
	var url = window.location.origin +'/rest/profile/'+companyProfileName+'/regions';
	callAjaxGET(url, paintCompanyRegions, true);
}

function paintCompanyRegions(data) {
	var response= $.parseJSON(data);
	if(response != undefined) {
		var result = $.parseJSON(response.entity);
		if(result != undefined && result.length > 0) {
			var regionsHtml = "";
			$.each(result,function(i, region) {
				regionsHtml = regionsHtml+'<div class="bd-hr-item-l1 comp-region" data-openstatus="closed" data-regionid = '+region.regionId+'>';
				regionsHtml = regionsHtml+'	<div class="bd-hr-item bd-lt-l1 clearfix">';
				regionsHtml = regionsHtml+'    <div class="bd-hr-txt cursor-pointer region-link" data-profilename="'+region.profileName+'">'+region.region+'</div>';
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
	var url = window.location.origin +'/rest/profile/region/'+regionId+'/branches';
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
				branchesHtml = branchesHtml +'<div class="bd-hr-item-l2 comp-region-branch" data-openstatus="closed" data-branchid="'+branch.branchId+'">';
				branchesHtml = branchesHtml +'	<div class="bd-hr-item bd-lt-l2 clearfix">';
				branchesHtml = branchesHtml +'		<div class="bd-hr-txt cursor-pointer branch-link" data-profilename="'+branch.profileName+'">'+branch.branch+'</div>';
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
			
			//bindClickToFetchBranchIndividuals("comp-region-branch");
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
	var url=window.location.origin +'/rest/profile/branch/'+branchId+'/individuals';
	$("#branchid-hidden").val(branchId);
	callAjaxGET(url, paintIndividualForBranch, true);
}

function paintIndividualForBranch(data) {
	var responseJson = $.parseJSON(data);
	var individualsHtml = "";
	var branchId = $("#branchid-hidden").val();
	if (responseJson != undefined && responseJson.entity != "") {
		var result = $.parseJSON(responseJson.entity);
		if (result != undefined && result.length > 0) {
			$.each(result, function(i, individual) {
				if (individual.contact_details != undefined) {
					individualsHtml += '<div class="bd-hr-item-l3 comp-individual" data-agentid=' + individual.iden + '>';
					individualsHtml += '	<div class="bd-hr-item bd-lt-l3 clearfix">';
					if (individual.profileImageUrl != undefined) {
						individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"'
							+ 'style="background: url(' + individual.profileImageUrl + ') 50% 50% / 100% no-repeat;"></div>';
					} else {
						individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"></div>';
					}
					individualsHtml += '		<div class="bd-hr-txt cursor-pointer individual-link" data-profilename="'
						+ individual.profileName + '">' + individual.contact_details.name + '</div>';
					individualsHtml += '	</div>';
					individualsHtml += '</div>';
				}
			});
			$("#branch-hierarchy").show();
			if($("#branch-individuals").length > 0) {
				$("#branch-individuals").html(individualsHtml);
			}
			else {
				$("#comp-branch-individuals-"+branchId).html(individualsHtml).slideDown(200);
			}
			
			// paintProfileImage("comp-individual-prof-image");
			// bindClickToFetchIndividualProfile("branch-individual");
		}
	}
}

function bindClickToFetchIndividualProfile(bindingClass) {
	$("."+bindingClass).click(function(e){
		e.stopPropagation();
		var agentProfileName = $(this).data("profilename");
		var url = window.location.origin +"/pages/"+agentProfileName;
		window.open(url, "_blank");				
	});
}

function fetchIndividualsForRegion(regionId) {
	var url = window.location.origin +'/rest/profile/region/'+regionId+'/individuals';
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
						if (individual.profileImageUrl != undefined) {
							individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"'
								+ 'style="background: url(' + individual.profileImageUrl + ') 50% 50% / 100% no-repeat;"></div>';
						} else {
							individualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"></div>';
						}
						individualsHtml += '	<div class="bd-hr-txt cursor-pointer individual-link" data-profilename="'
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
				
				// paintProfileImage("comp-individual-prof-image");
				// bindClickToFetchIndividualProfile("region-individual");
		}
	}
}


function paintProfileImage(imgDivClass) {
	$("." + imgDivClass).each(function() {
		var imageUrl = $(this).attr('data-imageurl');
		if (imageUrl != undefined && imageUrl != "") {
			console.log(imageUrl);
			$(this).css("background", "url(" + imageUrl + ") 50% 50% / 100% no-repeat;");
		}
	});
}

function fetchCompanyIndividuals() {
	var url = window.location.origin +'/rest/profile/'+companyProfileName+'/individuals';
	callAjaxGET(url, paintCompanyIndividuals, true);
}

function paintCompanyIndividuals(data) {
	var response= $.parseJSON(data);
	if (response != undefined) {
		var result = $.parseJSON(response.entity);
		if (result != undefined && result.length > 0) {
			var compIndividualsHtml = "";
			$.each(result, function(i, compIndividual) {
				if (compIndividual.contact_details != undefined) {
					compIndividualsHtml += '<div class="bd-hr-item-l1 comp-individual" data-agentid=' + compIndividual.iden + '>';
					compIndividualsHtml += '	<div class="bd-hr-item bd-lt-l3 clearfix">';
					
					if (compIndividual.profileImageUrl != undefined) {
						compIndividualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"'
							+ 'style="background: url(' + compIndividual.profileImageUrl + ') 50% 50% / 100% no-repeat;"></div>';
					} else {
						compIndividualsHtml += '	<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image"></div>';
					}
					compIndividualsHtml += '		<div class="bd-hr-txt cursor-pointer individual-link" data-profilename="'
						+ compIndividual.profileName + '">' + compIndividual.contact_details.name + '</div>';
					compIndividualsHtml += '	</div>';
					compIndividualsHtml += '</div>';
				}
			});
			$("#comp-regions-content").append(compIndividualsHtml);
			$("#comp-hierarchy").show();
			
			// paintProfileImage("comp-individual-prof-image");
			// bindClickToFetchIndividualProfile("comp-individual");
		}
	}
}

function fetchCompanyBranches() {
	var url = window.location.origin +'/rest/profile/'+companyProfileName+'/branches';
	callAjaxGET(url, paintCompanyBranches, true);
}

function paintCompanyBranches(data) {
	var response = $.parseJSON(data);
	if(response != undefined) {
		var result = $.parseJSON(response.entity);
		if(result != undefined && result.length > 0) {
			var compBranchesHtml = "";
			$.each(result,function(i,branch) {
				compBranchesHtml = compBranchesHtml +'<div class="bd-hr-item-l1 comp-branch" data-openstatus="closed" data-branchid="'+branch.branchId+'">';
				compBranchesHtml = compBranchesHtml +'	<div class="bd-hr-item bd-lt-l2 clearfix">';
				compBranchesHtml = compBranchesHtml +'		<div class="bd-hr-txt cursor-pointer branch-link" data-profilename="'+branch.profileName+'">'+branch.branch+'</div>';
				compBranchesHtml = compBranchesHtml +'	</div>';
				compBranchesHtml = compBranchesHtml +'</div>' ;
				compBranchesHtml = compBranchesHtml +'		<div class="lpsub-2 hide" id="comp-branch-individuals-'+branch.branchId+'"></div>';
			});
			$("#comp-hierarchy").show();
			$("#comp-regions-content").append(compBranchesHtml);
			//bindClickToFetchBranchIndividuals("comp-branch");
		}
	}
}

function fetchReviewsForCompany(companyId,start,numRows,minScore) {
	if(companyId == undefined || companyId == ""){
		return;
	}
	var url = window.location.origin +'/rest/profile/company/'+companyId+'/reviews?start='+start+"&numRows="+numRows+"&sortCriteria="+reviewsSortBy;
	if(minScore != undefined) {
		url = url +"&minScore="+minScore;
	}
	callAjaxGET(url, fetchReviewsForCompanyCallBack, false);
}

function fetchReviewsForCompanyCallBack(data) {
	var responseJson = $.parseJSON(data);
	if(responseJson != undefined) {
		var result = $.parseJSON(responseJson.entity);
		if(result != undefined && result.length > 0) {
			paintReviews(result);
		}
		else {
			/**
			 * calling method to populate count of hidden reviews, min score becomes the upper limit for score here
			 */
			if(minScore > 0){
				fetchReviewsCountForCompany(currentProfileIden,paintHiddenReviewsCount,0,minScore);
			}		
		}
	}
}

function paintReviews(result){
	var resultSize = result.length;
	$('.ppl-review-item-last').removeClass('ppl-review-item-last').addClass('ppl-review-item');
	
	var reviewsHtml = "";
	$.each(result, function(i, reviewItem) {
		var date = Date.parse(reviewItem.modifiedOn);
		var lastItemClass = "ppl-review-item";
		if (i == resultSize - 1) {
			lastItemClass = "ppl-review-item-last";
        }
		
		reviewsHtml = reviewsHtml +
			'<div class="' + lastItemClass + '" data-cust-first-name=' + reviewItem.customerFirstName
				+ ' data-cust-last-name=' + reviewItem.customerLastName + ' data-agent-name=' + reviewItem.agentName
				+ ' data-rating=' + reviewItem.score + ' data-review="' + reviewItem.review + '" data-customeremail="'
				+ reviewItem.customerEmail + '" data-agentid="' + reviewItem.agentId + '">';
		reviewsHtml += '	<div class="ppl-header-wrapper clearfix">';
		reviewsHtml += '		<div class="float-left ppl-header-left">';    
		reviewsHtml += '			<div class="ppl-head-1">'+reviewItem.customerFirstName+' '+reviewItem.customerLastName+'</div>';
		if (date != null) {
			date = convertUTCToUserDate(date);
			reviewsHtml += '		<div class="ppl-head-2">' + date.getMonthName() + " " + date.getDate() + ", " + date.getFullYear() + '</div>'; 
		}
		
		reviewsHtml += '		</div>';
		reviewsHtml += '    	<div class="float-right ppl-header-right">';
		reviewsHtml += '    	    <div class="st-rating-wrapper maring-0 clearfix review-ratings" data-rating="'+reviewItem.score+'"></div>';
		reviewsHtml += '		</div>';
		reviewsHtml += '	</div>';
		
		if (reviewItem.review.length > 250) {
			reviewsHtml += '<div class="ppl-content"><span class="review-complete-txt">'+reviewItem.review+'</span><span class="review-less-text">' + reviewItem.review.substr(0,250) + '</span><span class="review-more-button">More</span></div>';			
		} else {
			reviewsHtml += '<div class="ppl-content">'+reviewItem.review+'</div>';
		}
		
		reviewsHtml += '	<div class="ppl-share-wrapper clearfix">';
		reviewsHtml += '		<div class="float-left blue-text ppl-share-shr-txt">Share</div>';
		reviewsHtml += '		<div class="float-left icn-share icn-plus-open"></div>';
		reviewsHtml += '		<div class="float-left clearfix ppl-share-social hide">';
		reviewsHtml += '			<a href="https://www.facebook.com/sharer/sharer.php?u=' + reviewItem.completeProfileUrl + '" target="_blank"><span class="float-left ppl-share-icns icn-fb icn-fb-pp" title="Facebook"></span></a>';
		reviewsHtml += '			<a href="https://twitter.com/home?status=' + reviewItem.completeProfileUrl + '" target="_blank"><span class="float-left ppl-share-icns icn-twit icn-twit-pp" title="Twitter"></span></a>';
		reviewsHtml += '			<a href="https://www.linkedin.com/shareArticle?mini=true&url=' + reviewItem.completeProfileUrl + '&title=&summary=' + reviewItem.score + '-star response from ' + reviewItem.customerFirstName+' '+reviewItem.customerLastName + ' for ' + reviewItem.agentName +' at SocialSurvey - ' + reviewItem.review + '&source=" target="_blank"><span class="float-left ppl-share-icns icn-lin icn-lin-pp" title="LinkedIn"></span></a>';
		reviewsHtml += '			<a href="https://plus.google.com/share?url=' + reviewItem.completeProfileUrl + '" target="_blank"<span class="float-left ppl-share-icns icn-gplus" title="Google+"></span></a>';
		
		if (reviewItem.yelpProfileUrl != null && reviewItem.yelpProfileUrl != "") {
			reviewsHtml += '		<a href="' + reviewItem.yelpProfileUrl + '" target="_blank"><span class="float-left ppl-share-icns icn-yelp" title="Yelp"></span></a>';
		}
		if (reviewItem.zillowProfileUrl != null && reviewItem.zillowProfileUrl != "") {
			reviewsHtml += '		<a href="' + reviewItem.zillowProfileUrl + '" target="_blank"><span class="float-left ppl-share-icns icn-zillow" title="Zillow"></span></a>';
		}
		if (reviewItem.lendingTreeProfileUrl != null && reviewItem.lendingTreeProfileUrl != "") {
			reviewsHtml += '		<a href="' + reviewItem.lendingTreeProfileUrl + '" target="_blank"><span class="float-left ppl-share-icns icn-lendingtree" title="LendingTree"></span></a>';
		}
		reviewsHtml += '		</div>';
		reviewsHtml += '		<div class="float-right" style="margin: 0 -5px;">';
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
		changeRatingPattern($(this).data("rating"), $(this));
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
}

$(document).on('click','.review-more-button',function(){
	$(this).parent().find('.review-less-text').hide();
	$(this).parent().find('.review-complete-txt').show();
	$(this).hide();
});

//Report abuse click event.
$(document).on('click', '.prof-report-abuse-txt', function(e) {
	
	var firstName = $(this).parent().parent().parent().parent().attr('data-cust-first-name');
	var lastName = $(this).parent().parent().parent().parent().attr('data-cust-last-name');
	var agentName = $(this).parent().parent().parent().parent().attr('data-agent-name');
	var customerEmail = $(this).parent().parent().parent().parent().attr('data-customeremail');
	var agentId = $(this).parent().parent().parent().parent().attr('data-agentid');
	var review = $(this).parent().parent().parent().parent().attr('data-review');
	var payload = {
			"customerEmail" : customerEmail,
			"agentId" : agentId,
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName,
			"review" : review
	};
	$("#report-abuse-txtbox").val('');
	$('#report-abuse-cus-name').val('');
	$('#report-abuse-cus-email').val('');
	
	//Unbind click events for button
	$('.rpa-cancel-btn').off('click');
	$('.rpa-report-btn').off('click');
	
	
	$('#report-abuse-overlay').show();
	
	$('.rpa-cancel-btn').on('click',function(){
		$('#report-abuse-overlay').hide();
	});
	$('.rpa-report-btn').on('click',function(){
		var reportText = $("#report-abuse-txtbox").val();
		var cusName = $('#report-abuse-cus-name').val();
		var cusEmail = $('#report-abuse-cus-email').val();
		payload.reportText = reportText;
		payload.reporterName = cusName;
		payload.reporterEmail = cusEmail;
		confirmReportAbuse(payload);
	});
});

function confirmReportAbuse(payload) {
	callAjaxGetWithPayloadData('/rest/profile/surveyreportabuse', function() {
		$('#report-abuse-overlay').hide();
		$('#overlay-toast').html('Reported Successfully!');
		showToast();
	}, payload, true);
}

$(document).scroll(function(){
	var totalReviews = parseInt($("#profile-fetch-info").attr("total-reviews"));
	if ((window.innerHeight + window.pageYOffset) >= (document.body.offsetHeight) && startIndex <= totalReviews){
		startIndex = startIndex + numOfRows;
		$("#profile-fetch-info").attr("fetch-all-reviews","false");
		var profileLevel = $("#profile-fetch-info").attr("profile-level");
		if(showAllReviews)
			fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, 0);
		else
			fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, minScore);
	}
});

function fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, minScore) {
	if (profileLevel == 'COMPANY') {
		fetchReviewsForCompany(currentProfileIden, startIndex, numOfRows, minScore);
	}
	else if (profileLevel == 'REGION') {
		fetchReviewsForRegion(currentProfileIden, startIndex, numOfRows, minScore);
	}
	else if (profileLevel == 'BRANCH') {
		fetchReviewsForBranch(currentProfileIden, startIndex, numOfRows, minScore);
	}
	else if (profileLevel == 'INDIVIDUAL') {
		fetchReviewsForAgent(currentProfileIden, startIndex, numOfRows, minScore);
	}
}

function fetchReviewsCountForCompany(companyId,callBackFunction,minScore,maxScore) {
	if(minScore == undefined){
		minScore = -1;
	}
	if(maxScore == undefined){
		maxScore = -1;
	}
	var url = window.location.origin +'/rest/profile/company/'+companyId+'/reviewcount?minScore='+minScore+'&maxScore='+maxScore;
	callAjaxGET(url, callBackFunction, true);
}

function paintAllReviewsCount(data) {
	var responseJson = $.parseJSON(data);
	if(responseJson != undefined) {
		var reviewsSizeHtml = responseJson.entity;
		$("#profile-fetch-info").attr("total-reviews",responseJson.entity);
		reviewsSizeHtml = reviewsSizeHtml +' Review(s)';
		//$("#prof-company-review-count").html(reviewsSizeHtml);
		//$("#prof-schema-reviews").html(reviewsSizeHtml);
		/*if(responseJson.entity > 0){
			$("#prof-company-review-count").click(function(){
				if(window.innerWidth < 768){
					$('.icn-star-smile').click();					
				}
				$('html, body').animate({
					scrollTop : $('#reviews-container').offset().top
				},500);
			});
		}*/
	}
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
			
			$("#prof-hidden-review-count").html(reviewsSizeHtml);
			
			if($("#profile-fetch-info").attr("fetch-all-reviews") == "true"){
				$("#prof-hidden-review-count").show();
			}
			
			$('#prof-reviews-sort').show();
			$("#prof-hidden-review-count").click(function(){
				$('#prof-review-item').html('');
				$(this).hide();
				startIndex = 0;
				$("#profile-fetch-info").attr("fetch-all-reviews", "true");
				$(window).scrollTop($('#reviews-container').offset().top);
				showAllReviews = true;
				var profileLevel = $("#profile-fetch-info").attr("profile-level");
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, 0);
			});

			$('#sort-by-feature').on('click',function(e){
				e.stopImmediatePropagation();
				$("#prof-hidden-review-count").show();
				$('#prof-review-item').html('');
				startIndex = 0;
				$("#profile-fetch-info").attr("fetch-all-reviews","false");
				showAllReviews = false;
				var profileLevel = $("#profile-fetch-info").attr("profile-level");
				reviewsSortBy = 'feature';
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, minScore);
			});

			$('#sort-by-date').on('click',function(e){
				e.stopImmediatePropagation();
				$("#prof-hidden-review-count").hide();
				$('#prof-review-item').html('');
				startIndex = 0;
				$("#profile-fetch-info").attr("fetch-all-reviews","true");
				
				var profileLevel = $("#profile-fetch-info").attr("profile-level");
				reviewsSortBy = 'date';
				fetchReviewsBasedOnProfileLevel(profileLevel, currentProfileIden, startIndex, numOfRows, 0);
			});
		}
	}
}

function fetchRegionProfile(regionProfileName) {
	fetchRegionProfileCallBack();
	/*var url = window.location.origin +"/rest/profile/"+companyProfileName+"/region/"+regionProfileName;
	callAjaxGET(url, fetchRegionProfileCallBack, true);*/
}

function fetchRegionProfileCallBack(data) {
	var result = profileJson;

	paintProfilePage(result);
	paintIndividualDetails(result);
	//fetchAverageRatingsForRegion(result.iden);
	paintAverageRatings();
	fetchBranchesForRegion(result.iden);
	fetchIndividualsForRegion(result.iden);
	if(result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;
	fetchReviewsForRegion(result.iden,startIndex,numOfRows,minScore);
	//fetchReviewsCountForRegion(result.iden, paintAllReviewsCount);
}

function fetchAverageRatingsForRegion(regionId){
	var url = window.location.origin +"/rest/profile/region/"+regionId+"/ratings";
	callAjaxGET(url, paintAverageRatings, true);
}

function fetchReviewsForRegion(regionId,start,numRows,minScore) {
	if(regionId == undefined || regionId == ""){
		return;
	}
	var url = window.location.origin +"/rest/profile/region/"+regionId+"/reviews?start="+start+"&numRows="+numRows+"&sortCriteria="+reviewsSortBy;
	if(minScore != undefined) {
		url = url +"&minScore="+minScore;
	}
	callAjaxGET(url, fetchReviewsForRegionCallBack, false);
}

function fetchReviewsForRegionCallBack(data) {
	var responseJson = $.parseJSON(data);
	if(responseJson != undefined) {
		var result = $.parseJSON(responseJson.entity);
		if(result != undefined && result.length > 0) {
			paintReviews(result);
		}
		else {
			/**
			 * calling method to populate count of hidden reviews, min score becomes the upper limit for score here
			 */
			if(minScore > 0){
				fetchReviewsCountForRegion(currentProfileIden,paintHiddenReviewsCount,0,minScore);
			}		
		}
	}
}

function fetchReviewsCountForRegion(regionId,callBackFunction,minScore,maxScore) {
	if(minScore == undefined){
		minScore = -1;
	}
	if(maxScore == undefined){
		maxScore = -1;
	}
	var url = window.location.origin +'/rest/profile/region/'+regionId+'/reviewcount?minScore='+minScore+'&maxScore='+maxScore;
	callAjaxGET(url, callBackFunction, true);
}

function fetchAverageRatingsForBranch(branchId){
	var url = window.location.origin +"/rest/profile/branch/"+branchId+"/ratings";
	callAjaxGET(url, paintAverageRatings, true);
}

function fetchReviewsForBranch(branchId,start,numRows,minScore){
	if(branchId == undefined || branchId == ""){
		return;
	}
	var url = window.location.origin +"/rest/profile/branch/"+branchId+"/reviews?start="+start+"&numRows="+numRows+"&sortCriteria="+reviewsSortBy;
	if(minScore != undefined) {
		url = url +"&minScore="+minScore;
	}
	callAjaxGET(url, fetchReviewsForBranchCallBack, false);
}

function fetchReviewsForBranchCallBack(data) {
	var responseJson = $.parseJSON(data);
	if(responseJson != undefined) {
		var result = $.parseJSON(responseJson.entity);
		if(result != undefined && result.length > 0) {
			paintReviews(result);
		}
		else {
			/**
			 * calling method to populate count of hidden reviews, min score becomes the upper limit for score here
			 */
			if(minScore > 0){
				fetchReviewsCountForBranch(currentProfileIden,paintHiddenReviewsCount,0,minScore);
			}		
		}
	}
}

function fetchReviewsCountForBranch(branchId,callBackFunction,maxScore) {
	if(minScore == undefined){
		minScore = -1;
	}
	if(maxScore == undefined){
		maxScore = -1;
	}
	var url = window.location.origin +'/rest/profile/branch/'+branchId+'/reviewcount?minScore='+minScore+'&maxScore='+maxScore;;
	callAjaxGET(url, callBackFunction, true);
}

function fetchBranchProfile(branchProfileName) {
	fetchBranchProfileCallBack();
	/*var url = window.location.origin +"/rest/profile/"+companyProfileName+"/branch/"+branchProfileName;
	callAjaxGET(url, fetchBranchProfileCallBack, true);*/
}

function fetchBranchProfileCallBack(data) {
	var result = profileJson;
	
	paintProfilePage(result);
	paintIndividualDetails(result);
	//fetchAverageRatingsForBranch(result.iden);
	paintAverageRatings();
	fetchIndividualsForBranch(result.iden);
	if(result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;
	fetchReviewsForBranch(result.iden,startIndex,numOfRows,minScore);
	fetchReviewsCountForBranch(result.iden, paintAllReviewsCount);
}

function fetchAverageRatingsForAgent(agentId){
	var url = window.location.origin+"/rest/profile/individual/"+agentId+"/ratings";
	callAjaxGET(url, paintAverageRatings, true);
}

function fetchReviewsCountForAgent(agentId,callBackFunction,minScore,maxScore) {
	if(minScore == undefined){
		minScore = -1;
	}
	if(maxScore == undefined){
		maxScore = -1;
	}
	var url = window.location.origin +'/rest/profile/individual/'+agentId+'/reviewcount?minScore='+minScore+'&maxScore='+maxScore;
	callAjaxGET(url, callBackFunction, true);
}

function fetchReviewsForAgent(agentId,start,numRows,minScore){
	if(agentId == undefined || agentId == ""){
		return;
	}
	var url = window.location.origin +"/rest/profile/individual/"+agentId+"/reviews?start="+start+"&numRows="+numRows+"&sortCriteria="+reviewsSortBy;
	if(minScore != undefined) {
		url = url +"&minScore="+minScore;
	}
	callAjaxGET(url, fetchReviewsForAgentCallBack, false);
}

function fetchReviewsForAgentCallBack(data) {
	var responseJson = $.parseJSON(data);
	if(responseJson != undefined) {
		var result = $.parseJSON(responseJson.entity);
		if(result != undefined && result.length > 0) {
			paintReviews(result);
		}
		else {
			//hideReviewsHeader();
			/**
			 * calling method to populate count of hidden reviews, min score becomes the upper limit for score here
			 */
			if(minScore > 0){
				fetchReviewsCountForAgent(currentProfileIden,paintHiddenReviewsCount,0,minScore);
			}		
		}
	}
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
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-header lph-dd lph-dd-closed cursor-pointer lph-arrow-closed">Specialities</div>';
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
	
	// paint disclaimer
	if (result.disclaimer != undefined && result.disclaimer != "") {
		individualDetailsHtml = individualDetailsHtml + '<div class="prof-left-row prof-left-ach bord-bot-dc">';
		individualDetailsHtml = individualDetailsHtml + '	<div class="left-ach-wrapper">';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-header lph-dd lph-dd-closed cursor-pointer lph-arrow-closed">Disclaimer</div>';
		individualDetailsHtml = individualDetailsHtml + '		<div class="left-panel-content lph-dd-content">';
		individualDetailsHtml = individualDetailsHtml + '<div class="lp-ach-row lp-row clearfix">' + result.disclaimer + '</div>';
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

function fetchAgentProfile(agentProfileName){
	fetchAgentProfileCallBack();
	/*var url = window.location.origin+"/rest/profile/individual/"+agentProfileName;
	callAjaxGET(url, fetchAgentProfileCallBack, true);*/
}

function fetchAgentProfileCallBack(data) {
	var result = profileJson;
	paintProfilePage(result);
	paintIndividualDetails(result);
	//fetchAverageRatingsForAgent(result.iden);
	paintAverageRatings();
	if(result.survey_settings != undefined && result.survey_settings.show_survey_above_score != undefined) {
		minScore = result.survey_settings.show_survey_above_score;
	}
	startIndex = 0;
	fetchReviewsForAgent(result.iden,startIndex,numOfRows,minScore);
	fetchReviewsCountForAgent(result.iden, paintAllReviewsCount);
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
		 url = window.location.origin +"/initfindapro.do?profileLevel="+profileLevel+"&iden="+iden+"&searchCriteria="+searchcritrianame;
		 window.open(url, "_blank");
	}
	
}

function downloadVCard(agentName){
	if(agentName == undefined || agentName == ""){
		return;
	}
	var url = window.location.origin + "/rest/profile/downloadvcard/"+agentName;
	//callAjaxGET(url, afterDownloadVCard, true);
	window.open(url, "_blank");
}

function afterDownloadVCard(data){
	console.log("V Card download complete");
}

//Function to paint posts
function paintPublicPosts() {
	
	var profileLevel = $("#profile-fetch-info").attr("profile-level");
	
	var url = window.location.origin + "/rest/profile/";
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
	callAjaxGET(url, callBackPaintPublicPosts, true);
}

function callBackPaintPublicPosts(data) {
	var posts = $.parseJSON(data);
	posts = $.parseJSON(posts.entity);
	
	var divToPopulate = "";
	$.each(posts, function(i, post) {
		var iconClass = "";
		if (post.source == "google")
			iconClass = "icn-gplus";
		else if (post.source == "SocialSurvey")
			iconClass = "icn-ss";
		else if (post.source == "facebook")
			iconClass = "icn-fb";
		else if (post.source == "twitter")
			iconClass = "icn-twit";
		else if (post.source == "linkedin")
			iconClass = "icn-lin";
		
		divToPopulate += '<div class="tweet-panel-item bord-bot-dc clearfix">'
			+ '<div class="tweet-icn '+ iconClass +' float-left"></div>'
			+ '<div class="tweet-txt float-left">'
				+ '<div class="tweet-text-main">' + post.postText + '</div>'
				+ '<div class="tweet-text-link"><em>' + post.postedBy + '</em></div>'
				+ '<div class="tweet-text-time"><em>' + new Date(post.timeInMillis).toUTCString() + '</em></div>'
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
	
	publicPostStartIndex += posts.length;
	if (publicPostStartIndex < publicPostNumRows || posts.length < publicPostNumRows){
		doStopPublicPostPagination = true;
	}
	
	$('#prof-posts').on('scroll',function(){
		var scrollContainer = this;
		if (scrollContainer.scrollTop === scrollContainer.scrollHeight
					- scrollContainer.clientHeight) {
			if (!doStopPublicPostPagination) {
				paintPublicPosts();					
			}
		}
	});
}


$('body').on('click',".branch-link",function(e) {
	e.stopPropagation();
	var branchProfileName = $(this).data("profilename");
	var url = window.location.origin +"/pages/office/"+companyProfileName+"/"+branchProfileName;
	window.open(url, "_blank");
});

$('body').on('click',".individual-link",function(e) {
	e.stopPropagation();
	var agentProfileName = $(this).data("profilename");
	var url = window.location.origin +"/pages/"+agentProfileName;
	window.open(url, "_blank");
});

$('body').on('click',".region-link",function(e) {
	e.stopPropagation();
	var regionProfileName = $(this).data("profilename");
	var url = window.location.origin +"/pages/region/"+companyProfileName+"/"+regionProfileName;
	window.open(url, "_blank");
});


$('body').on("click",".comp-branch,.comp-region-branch",function(e){
	e.preventDefault();
	if($(this).data("openstatus") == "closed") {
		fetchIndividualsForBranch($(this).data('branchid'));
		$(this).data("openstatus","open");
	}else {
		$('#comp-branch-individuals-'+$(this).data('branchid')).slideUp(200);
		$(this).data("openstatus","closed");
	}
});

$('body').on("click",".comp-region",function(){
	if($(this).data("openstatus") == "closed") {
		$('#comp-region-branches-'+$(this).data('regionid')).html("");
		fetchBranchesForRegion($(this).data('regionid'));
		fetchIndividualsForRegion($(this).data('regionid'));
		$(this).data("openstatus","open");
	}else {
		$('#comp-region-branches-'+$(this).data('regionid')).slideUp(200);
		$(this).data("openstatus","closed");
	}
	
});

function constructDate(dateStr) {
	var dateDisplay = "";
	if (typeof dateStr[0] != 'undefined' && typeof dateStr[1] != 'undefined') {
		dateDisplay = monthNames[dateStr[0] - 1] + " " + dateStr[1];
	} else if (typeof dateStr[0] != 'undefined') {
		dateDisplay = monthNames[dateStr[0] - 1];
	} else if (typeof dateStr[1] != 'undefined') {
		dateDisplay = dateStr[1];
	}
	
	return dateDisplay;
}