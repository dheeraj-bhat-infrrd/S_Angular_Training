/**
 * 
 */
var companyProfileName = $("#company-profile-name").val();

function fetchCompanyProfile() {
	var url = window.location.origin +'/rest/profile/'+companyProfileName;
	callAjaxGET(url, paintCompanyProfile, true);
}

function paintCompanyProfile(data) {
	var response= $.parseJSON(data);
	if(response != undefined) {
		var result = $.parseJSON(response.entity);
		var headContentHtml = "";
		if(result != undefined) {
			var contactDetails = result.contact_details;
			if(contactDetails != undefined){
				headContentHtml = headContentHtml +'<div class="prof-name">'+contactDetails.name+'</div>';
	            headContentHtml = headContentHtml +' <div class="prof-address"><div class="prof-addline1">'+result.vertical+'</div>';
	            headContentHtml = headContentHtml +' </div>';
	            headContentHtml = headContentHtml +' <div class="prof-rating clearfix">';
	            headContentHtml = headContentHtml + '	<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp">';
	            headContentHtml = headContentHtml +  '  	<div class="rating-star icn-full-star"></div>';
	            headContentHtml = headContentHtml +  '  	<div class="rating-star icn-full-star"></div>';
	            headContentHtml = headContentHtml +  '  	<div class="rating-star icn-half-star"></div>';
	            headContentHtml = headContentHtml +  '  	<div class="rating-star icn-no-star"></div>';
	            headContentHtml = headContentHtml +  '  	<div class="rating-star icn-no-star"></div>	</div>';
	            headContentHtml = headContentHtml +'	<div class="float-left review-count-left" id="prof-company-review-count"></div>';
	            headContentHtml = headContentHtml +'	</div>';
	            headContentHtml = headContentHtml +'	<div class="prof-btn-wrapper">';
	            headContentHtml = headContentHtml +'		<div class="prof-btn-survey" id="read-write-share-btn">Read Write and Share Reviews</div>';
	            headContentHtml = headContentHtml +'	</div>';            
	            $("#prof-company-head-content").html(headContentHtml);
	            
	            var addressHtml = '<div class="prof-user-addline1">'+contactDetails.address1+'</div>';
	            addressHtml = addressHtml + '<div class="prof-user-addline2">'+contactDetails.address2+'</div>';
	            $("#prof-company-address").html(addressHtml);
	            if(result.logo != undefined) {
	            	$("#prof-company-logo").css("background", "url("+result.logo+") no-repeat center");
	            }
	            if(result.profileImageUrl != undefined) {
	            	 $("#prof-image").css("background", "url("+result.profileImageUrl+") no-repeat center");
	            }
	            
	            var companyIntroHtml = '<div class="main-con-header">About '+ contactDetails.name+'</div>';
	            companyIntroHtml = companyIntroHtml + '<div class="intro-body">'+contactDetails.about_me+'</div>';
	            $("#prof-company-intro").html(companyIntroHtml);
	            
	            var reviewsHeaderHtml = '<span class="ppl-say-txt-st">What people say</span> about '+contactDetails.name;
	            $("#prof-reviews-header").html(reviewsHeaderHtml);
	            
	            var contactInfoHtml = "";
	            var mailIds = contactDetails.mail_ids;
	            
	            if(mailIds != undefined) {
	            	contactInfoHtml =	contactInfoHtml+'<div class="lp-con-row lp-row clearfix">';
	                contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-icn icn-mail"></div>';	            
	                contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-row-item" data-mailid = "'+mailIds.work+'">Contact Us</div></div>';
	            }
	            
	            var webAddresses = contactDetails.web_addresses;
	            if(webAddresses != undefined) {
	            	if(webAddresses.work != undefined) {
	            		contactInfoHtml =	contactInfoHtml+'<div class="lp-con-row lp-row clearfix">';		        
	                    contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-icn icn-web"></div>';		            
	                    contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-row-item blue-text"><a href="'+webAddresses.work+'">Our Website</a></div></div>';		            
	            	}
	            	if(webAddresses.blogs != undefined) {
	                    contactInfoHtml =	contactInfoHtml+'<div class="lp-con-row lp-row clearfix">';		        
	                    contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-icn icn-blog"></div>';		            
	                    contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-row-item blue-text"><a href="'+webAddresses.blogs+'">Our Blogs</a></div></div>';	            
	            	}
	            }
	            
	            var contactNumbers	 = contactDetails.contact_numbers;
	            if(contactNumbers != undefined) {
	            	if(contactNumbers.personal != undefined) {
	            		contactInfoHtml =	contactInfoHtml+'<div class="lp-con-row lp-row clearfix">';		        
		                contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-icn icn-mbl"></div>';		            
		                contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-row-item">'+contactNumbers.personal+'</div></div>';		            
	            	}
	            	if(contactNumbers.work != undefined) {
	            		contactInfoHtml =	contactInfoHtml+'<div class="lp-con-row lp-row clearfix">';		        
	  	                contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-icn icn-phone"></div>';		            
	  	                contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-row-item">'+contactNumbers.work+'</div></div>';		            
	  	               
	            	}
	            	if(contactNumbers.fax != undefined) {
	            		contactInfoHtml =	contactInfoHtml+'<div class="lp-con-row lp-row clearfix">';		        
	            		contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-icn icn-fax"></div>'	;	            
	            		contactInfoHtml =	contactInfoHtml+'	<div class="float-left lp-con-row-item">'+contactNumbers.fax+'</div></div>';
	            	}
	            }
	            $("#prof-contact-information").html(contactInfoHtml);
			}         
		}
		fetchAverageRatings(result.iden);
		fetchCompanyRegions();
		fetchCompanyBranches();
		fetchCompanyIndividuals();
		fetchReviewsForCompany(result.iden);
	}
}

function fetchAverageRatings(companyId) {
	var url = window.location.origin +'/rest/profile/ratings/company/'+companyId;
	callAjaxGET(url, paintAverageRatings, true);
}

function paintAverageRatings(data) {
	var responseJson = $.parseJSON(data);
	if(responseJson != undefined) {
		var rating = $.parseJSON(responseJson.entity);
		changeRatingPattern(rating,$("#rating-avg-comp"));
	}
}

function changeRatingPattern(rating, ratingParent) {
	var counter = 0;
	ratingParent.children().each(function() {
		$(this).addClass("icn-no-star");
		$(this).removeClass("icn-half-star");
		$(this).removeClass("icn-full-star");

		if (rating >= counter) {
			if (rating - counter >= 1) {
				$(this).removeClass("icn-no-star");
				$(this).addClass("icn-full-star");
			} else if (rating - counter == 0.5) {
				$(this).removeClass("icn-no-star");
				$(this).addClass("icn-half-star");
			}
		}
		counter++;
	});
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
				regionsHtml = regionsHtml+'<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-region" data-openstatus="closed" data-regionid = '+region.regionId+'>';
				regionsHtml = regionsHtml+'	<div class="lp-sub-header clearfix flat-left-bord">';
				regionsHtml = regionsHtml+'    <div class="lp-sub-img icn-company"></div>';
				regionsHtml = regionsHtml+'    <div class="lp-sub-txt">'+region.region+'</div>';
				regionsHtml = regionsHtml+'	   <div class="lpsub-2 hide" id="comp-region-branches-'+region.regionId+'"></div>';
				regionsHtml = regionsHtml+'	</div>';
				regionsHtml = regionsHtml+'</div>';
			});
			$("#comp-regions-content").html(regionsHtml);
			
			$(".comp-region").click(function(){
				if($(this).data("openstatus") == "closed") {
					fetchBranchesForRegion($(this).data('regionid'));
					$(this).data("openstatus","open");
				}else {
					$('#comp-region-branches-'+$(this).data('regionid')).slideUp(200);
					$(this).data("openstatus","closed");
				}
				
			});
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
				branchesHtml = branchesHtml +'<div class="lp-sub lp-sub-l1 bord-left-panel comp-region-branch" data-openstatus="closed" data-branchid="'+branch.branchId+'">';
				branchesHtml = branchesHtml +'	<div class="lp-sub-header clearfix flat-left-bord">';
				branchesHtml = branchesHtml +'		<div class="lp-sub-img icn-rgn"></div>';
				branchesHtml = branchesHtml +'		<div class="lp-sub-txt">'+branch.branch+'</div>';
				branchesHtml = branchesHtml +'		<div class="lpsub-2 hide" id="comp-branch-individuals-'+branch.branchId+'"></div>';
				branchesHtml = branchesHtml +'	</div>';
				branchesHtml = branchesHtml +'</div>' ;
			});
			
			$("#comp-region-branches-"+regionId).html(branchesHtml).slideDown(200);
			
			$(".comp-region-branch").click(function(e){
				e.stopPropagation();
				if($(this).data("openstatus") == "closed") {
					fetchIndividualForBranch($(this).data('branchid'));
					$(this).data("openstatus","open");
				}else {
					$('#comp-branch-individuals-'+$(this).data('branchid')).slideUp(200);
					$(this).data("openstatus","closed");
				}
			});
		}
	}
}

function fetchIndividualForBranch(branchId) {
	var url=window.location.origin +'/rest/profile/branch/'+branchId+'/individuals';
	$("#branchid-hidden").val(branchId);
	callAjaxGET(url, paintIndividualForBranch, true);
}

function paintIndividualForBranch(data) {
	var responseJson = $.parseJSON(data);
	var individualsHtml = "";
	var branchId = $("#branchid-hidden").val();
	if(responseJson != undefined && responseJson.entity != "") {
		var result = $.parseJSON(responseJson.entity);
		if(result != undefined && result.length > 0) {
			$.each(result,function(i,individual) {
				if(individual.contact_details != undefined){
					individualsHtml=  individualsHtml+'<div class="lp-sub lp-sub-l3 bord-left-panel">';
					individualsHtml=  individualsHtml+'		<div class="lp-sub-header clearfix flat-left-bord">';
					individualsHtml=  individualsHtml+'    		<div class="lp-sub-img lp-pers-img individual-prof-image" data-imageurl = "'+individual.profileImageUrl+'"></div>';
					individualsHtml=  individualsHtml+'    		<div class="lp-sub-txt">'+individual.contact_details.name+'</div>';
					individualsHtml=  individualsHtml+'		</div>';
					individualsHtml=  individualsHtml+'</div>';
				}
			});
			$("#comp-branch-individuals-"+branchId).html(individualsHtml).slideDown(200);
			paintProfileImage("individual-prof-image");
		}
	}
}

function paintProfileImage(imgDivClass) {
	$("."+imgDivClass).each(function(){
		$(this).css("background", "url("+$(this).data('imageurl')+") no-repeat center");
	});
}

function fetchCompanyIndividuals() {
	var url = window.location.origin +'/rest/profile/'+companyProfileName+'/individuals';
	callAjaxGET(url, paintCompanyIndividuals, true);
}

function paintCompanyIndividuals(data) {
	var response= $.parseJSON(data);
	if(response != undefined) {
		var result = $.parseJSON(response.entity);
		if(result != undefined && result.length > 0) {
			var compIndividualsHtml = "";
			$.each(result,function(i, compIndividual) {
				if(compIndividual.contact_details != undefined){
					compIndividualsHtml = compIndividualsHtml+'<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-individual" data-agentid = '+compIndividual.iden+'>';
					compIndividualsHtml = compIndividualsHtml+'	<div class="lp-sub-header clearfix flat-left-bord">';
					compIndividualsHtml = compIndividualsHtml+'    <div class="lp-sub-img lp-pers-img comp-individual-prof-image" data-imageurl = "'+compIndividual.profileImageUrl+'"></div>';
					compIndividualsHtml = compIndividualsHtml+'    <div class="lp-sub-txt">'+compIndividual.contact_details.name+'</div>';
					compIndividualsHtml = compIndividualsHtml+'	</div>';
					compIndividualsHtml = compIndividualsHtml+'</div>';
				}
			});
			$("#comp-regions-content").append(compIndividualsHtml);
			paintProfileImage("comp-individual-prof-image");
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
				compBranchesHtml = compBranchesHtml +'<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-branch" data-openstatus="closed" data-branchid="'+branch.branchId+'">';
				compBranchesHtml = compBranchesHtml +'	<div class="lp-sub-header clearfix flat-left-bord">';
				compBranchesHtml = compBranchesHtml +'		<div class="lp-sub-img icn-rgn"></div>';
				compBranchesHtml = compBranchesHtml +'		<div class="lp-sub-txt">'+branch.branch+'</div>';
				compBranchesHtml = compBranchesHtml +'		<div class="lpsub-2 hide" id="comp-branch-individuals-'+branch.branchId+'"></div>';
				compBranchesHtml = compBranchesHtml +'	</div>';
				compBranchesHtml = compBranchesHtml +'</div>' ;
			});
			
			$("#comp-regions-content").append(compBranchesHtml);
			$(".comp-branch").click(function(e){
				e.stopPropagation();
				if($(this).data("openstatus") == "closed") {
					fetchIndividualForBranch($(this).data('branchid'));
					$(this).data("openstatus","open");
				}else {
					$('#comp-branch-individuals-'+$(this).data('branchid')).slideUp(200);
					$(this).data("openstatus","closed");
				}
			});
		}
	}
}
function fetchReviewsForCompany(companyId) {
	var url = window.location.origin +'/rest/profile/reviews/company/'+companyId;
	callAjaxGET(url, paintReviewsForCompany, true);
}

function paintReviewsForCompany(data) {
	var responseJson = $.parseJSON(data);
	if(responseJson != undefined) {
		var result = $.parseJSON(responseJson.entity);
		var reviewsHtml = "";
		if(result != undefined && result.length > 0) {
			var reviewsSizeHtml = result.length;
			if(result.length == 1) {
				reviewsSizeHtml = reviewsSizeHtml +' Review';
			}else {
				reviewsSizeHtml = reviewsSizeHtml +' Reviews';
			}
			$("#prof-company-review-count").html(reviewsSizeHtml);
			$("#prof-company-review-count").click(function(){
				$(window).scrollTop($('#reviews-container').offset().top);
			});
			
			$.each(result,function(i, reviewItem) {
				var d = Date.parse(reviewItem.updatedOn);
				reviewsHtml=  reviewsHtml+'<div class="ppl-review-item">';
				reviewsHtml=  reviewsHtml+'	<div class="ppl-header-wrapper clearfix">';
				reviewsHtml=  reviewsHtml+'		<div class="float-left ppl-header-left">';    
				reviewsHtml=  reviewsHtml+'			<div class="ppl-head-1">'+reviewItem.customerEmail+'</div>';
				//reviewsHtml=  reviewsHtml+'			<div class="ppl-head-2">12<sup>th</sup>Sept 2014</div>';    
				reviewsHtml=  reviewsHtml+'			<div class="ppl-head-2">'+d.getDate() +" "+ d.getMonthName()+" "+d.getFullYear()+'</div>'; 
				reviewsHtml=  reviewsHtml+'    </div>';
				reviewsHtml=  reviewsHtml+'    <div class="float-right ppl-header-right">';
				reviewsHtml=  reviewsHtml+'        <div class="st-rating-wrapper maring-0 clearfix review-ratings" data-rating="'+reviewItem.score+'">';
				reviewsHtml=  reviewsHtml+'           <div class="rating-star icn-full-star"></div>';
				reviewsHtml=  reviewsHtml+'           <div class="rating-star icn-full-star"></div>';
				reviewsHtml=  reviewsHtml+'           <div class="rating-star icn-half-star"></div>';
				reviewsHtml=  reviewsHtml+'           <div class="rating-star icn-no-star"></div>';
				reviewsHtml=  reviewsHtml+'           <div class="rating-star icn-no-star"></div>';
				reviewsHtml=  reviewsHtml+'       </div>';
				reviewsHtml=  reviewsHtml+'   </div>';
				reviewsHtml=  reviewsHtml+'	</div>';
				reviewsHtml=  reviewsHtml+'	<div class="ppl-content">'+reviewItem.review +'</div>';
				reviewsHtml=  reviewsHtml+'		<div class="ppl-share-wrapper clearfix">';
				reviewsHtml=  reviewsHtml+'    		<div class="float-left blue-text ppl-share-shr-txt">Share</div>';
				reviewsHtml=  reviewsHtml+'    		<div class="float-left icn-share icn-plus-open"></div>';
				reviewsHtml=  reviewsHtml+'    		<div class="float-left clearfix ppl-share-social hide">';
				reviewsHtml=  reviewsHtml+'        	<div class="float-left ppl-share-icns icn-fb"></div>';
				reviewsHtml=  reviewsHtml+'        	<div class="float-left ppl-share-icns icn-twit"></div>';
				reviewsHtml=  reviewsHtml+'        	<div class="float-left ppl-share-icns icn-lin"></div>';
				reviewsHtml=  reviewsHtml+'       	<div class="float-left ppl-share-icns icn-yelp"></div>';
				reviewsHtml=  reviewsHtml+'    	</div>';
				reviewsHtml=  reviewsHtml+'   <div class="float-left icn-share icn-remove icn-rem-size hide"></div>';
				reviewsHtml=  reviewsHtml+'	</div>';
				reviewsHtml=  reviewsHtml+'</div>';
			});
			
			$("#prof-review-item").html(reviewsHtml);
			
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
	}
}