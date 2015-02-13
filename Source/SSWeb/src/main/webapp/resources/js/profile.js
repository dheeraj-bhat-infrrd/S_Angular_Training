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
	            headContentHtml = headContentHtml +'	<div class="float-left review-count-left">'+250 +'Reviews</div>';
	            headContentHtml = headContentHtml +'	</div>';
	            headContentHtml = headContentHtml +'	<div class="prof-btn-wrapper">';
	            headContentHtml = headContentHtml +'		<div class="prof-btn-survey" id="read-write-share-btn">Read Write and Share Reviews</div>';
	            headContentHtml = headContentHtml +'	</div>';            
	            $("#prof-company-head-content").html(headContentHtml);
	            
	            var addressHtml = '<div class="prof-user-addline1">'+contactDetails.address1+'</div>';
	            addressHtml = addressHtml + '<div class="prof-user-addline2">'+contactDetails.address2+'</div>';
	            $("#prof-company-address").html(addressHtml);
	            console.log("logo : "+result.logo);
	            if(result.logo != undefined) {
	            	$("#prof-company-logo").css("background", "url("+result.logo+")");
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
	}
}

function fetchCompanyRegions(start,numOfrows) {
	var url = window.location.origin +'/rest/profile/'+companyProfileName+'/regions';
	callAjaxGET(url, paintCompanyRegions, true);
}

function paintCompanyRegions(data) {
	var response= $.parseJSON(data);
	if(response != undefined) {
		console.log(response);
	}
}