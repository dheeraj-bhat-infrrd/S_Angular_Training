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
	            headContentHtml = headContentHtml + '	<div class="st-rating-wrapper maring-0 clearfix float-left">';
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
	            	$("#prof-company-logo").css("background", "url("+result.logo+")");
	            }
	            if(result.profileImageUrl != undefined) {
	            	 $("#prof-image").css("background", "url("+result.profileImageUrl+")");
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
		fetchCompanyRegions();
		fetchReviewsForCompany(result.iden);
		fetchAverageRatings(result.iden);
	}
}

function fetchAverageRatings(companyId) {
	var url = window.location.origin +'/rest/profile/ratings/company/'+companyId;
	callAjaxGET(url, paintAverageRatings, true);
}

function paintAverageRatings(data) {
	var responseJson = $.parseJSON(data);
	//console.log(responseJson);
}


function fetchCompanyRegions() {
	var url = window.location.origin +'/rest/profile/'+companyProfileName+'/regions';
	callAjaxGET(url, paintCompanyRegions, true);
}

function paintCompanyRegions(data) {
	var response= $.parseJSON(data);
	if(response != undefined) {
		var result = $.parseJSON(response.entity);
		console.log(result);
		if(result != undefined && result.length > 0) {
			var regionsHtml = "";
			$.each(result,function(i, region) {
				regionsHtml = regionsHtml+'<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-region" data-regionid = '+region.regionId+' id="comp-region-id-"'+region.regionId+'>';
				regionsHtml = regionsHtml+'	<div class="lp-sub-header clearfix flat-left-bord">';
				regionsHtml = regionsHtml+'    <div class="lp-sub-img icn-company"></div>';
				regionsHtml = regionsHtml+'    <div class="lp-sub-txt">'+region.region+'</div>';
				regionsHtml = regionsHtml+'	</div>';
				regionsHtml = regionsHtml+'</div>';
			});
			$("#comp-regions-content").html(regionsHtml);
		}
	}
}

function fetchCompanyIndividuals() {
	var url = window.location.origin +'/rest/profile/'+companyProfileName+'/individuals';
	callAjaxGET(url, paintCompanyIndividuals, true);
}

function paintCompanyIndividuals() {
	var response= $.parseJSON(data);
	/*if(response != undefined) {
		console.log(response);
	}*/
}

function fetchCompanyBranches() {
	var url = window.location.origin +'/rest/profile/'+companyProfileName+'/branches';
	callAjaxGET(url, paintCompanyBranches, true);
}

function paintCompanyBranches(data) {
	var response= $.parseJSON(data);
	/*if(response != undefined) {
		console.log(response);
	}*/
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
			$.each(result,function(i, reviewItem) {
				reviewsHtml=  reviewsHtml+'<div class="ppl-review-item">';
				reviewsHtml=  reviewsHtml+'	<div class="ppl-header-wrapper clearfix">';
				reviewsHtml=  reviewsHtml+'		<div class="float-left ppl-header-left">';    
				reviewsHtml=  reviewsHtml+'			<div class="ppl-head-1">'+reviewItem.customerEmail+'</div>';        
				reviewsHtml=  reviewsHtml+'			<div class="ppl-head-2">12<sup>th</sup>Sept 2014</div>';        
				reviewsHtml=  reviewsHtml+'    </div>';
				reviewsHtml=  reviewsHtml+'    <div class="float-right ppl-header-right">';
				reviewsHtml=  reviewsHtml+'        <div class="st-rating-wrapper maring-0 clearfix">';
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
		}
	}
}