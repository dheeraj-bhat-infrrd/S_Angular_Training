/**
 * Methods for rendering java-script widget and saving its configuration on SocialSurvey
 * 
 */

// global vars
var lightFont = "#ffffff";
var darkFont = "#555";

// java script widget
var socialSurveyJavascriptWidget = {

	widgetDetails : undefined,

	setup : function() {
		showOverlay();
		this.initialize();
		hideOverlay();
	},

	initialize : function() {

		this.loadConfiguration();

		// initialize data-independent drop down
		autoAppendTextDropdown("#st-dd-wrapper-widget-font-theme", "st-dd-item font-theme-option-item cursor-pointer widget-select-bx", [ "Light", "Dark" ]);
		autoAppendTextDropdown("#st-dd-wrapper-widget-embedded-font-theme", "st-dd-item embedded-font-theme-option-item cursor-pointer widget-select-bx", [ "Light", "Dark" ]);
		autoAppendTextDropdown("#st-dd-wrapper-df-rev-ordr", "st-dd-item df-rev-ordr-option-item cursor-pointer widget-select-bx", [ "Newest First", "Highest Rated First", "Oldest First", "Lowest Rated First", "Featured Reviews" ]);

		// populate widget configuration in input elements
		this.populateConfiguration();

		// event handlers
		this.onClicks();

		// help properly close drop down inputs
		setupWidgetDropdownHandler();

		// show widget preview
		this.displayPreview();

		// show widget code
		this.generateCodeForUse();

	},

	onClicks : function() {

		// ~~~~~~~~~~~~ on clicks

		$('.font-theme-option-item').on('click', function(event) {
			$('#widget-font-theme').val($(this).html());

			if ($(this).html() === "Light") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.fontTheme = lightFont;
			} else {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.fontTheme = darkFont;
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$('.embedded-font-theme-option-item').on('click', function(event) {
			$('#widget-embedded-font-theme').val($(this).html());

			if ($(this).html() === "Light") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.embeddedFontTheme = lightFont;
			} else {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.embeddedFontTheme = darkFont;
			}

			socialSurveyJavascriptWidget.displayPreview();

		});

		$('.df-rev-ordr-option-item').on('click', function(event) {
			$('#widget-df-rev-ordr').val($(this).html());

			if ("Newest First" == $(this).html()) {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder = "newestFirst";
			} else if ("Oldest First" == $(this).html()) {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder = "oldestFirst";
			} else if ("Highest Rated First" == $(this).html()) {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder = "highestRatingFirst";
			} else if ("Lowest Rated First" == $(this).html()) {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder = "lowestRatingFirst";
			}else if ("Featured Reviews" == $(this).html()) {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder = "feature";
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#button-one-name").on('change', function() {
			if ($("#button-one-name").val() != undefined && $("#button-one-name").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonOneName = $("#button-one-name").val();
			} else {
				$("#overlay-toast").html("Button one name must be provided");
				showToast();
			}

			if ($("#button-one-name").val().length > 15 && bonf == false) {
				bonf = true;
				$("#overlay-toast").html("A longer name could stretch the button");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#button-two-name").on('change', function() {
			if ($("#button-two-name").val() != undefined && $("#button-two-name").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonTwoName = $("#button-two-name").val();
			} else {
				$("#overlay-toast").html("Button two name must be provided");
				showToast();
			}

			if ($("#button-two-name").val().length > 15 && btnf == false) {
				btnf = true;
				$("#overlay-toast").html("A longer name could stretch the button");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#button-one-link").on('change', function() {
			if ($("#button-one-link").val() != undefined && $("#button-one-link").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonOneLink = $("#button-one-link").val();
			} else {
				$("#overlay-toast").html("Button one link must be provided");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#button-two-link").on('change', function() {
			if ($("#button-two-link").val() != undefined && $("#button-two-link").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonTwoLink = $("#button-two-link").val();
			} else {
				$("#overlay-toast").html("Button two link must be provided");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#btn-one-opc").on('change', function() {
			if ($("#btn-one-opc").val() != undefined && $("#btn-one-opc").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonOneOpacity = $("#btn-one-opc").val();
			} else {
				$("#overlay-toast").html("Button one opacity must be provided");
				showToast();
			}

			if ((isNaN($("#btn-one-opc").val()) || $("#btn-one-opc").val() > 1 || $("#btn-one-opc").val() < 0) && boof == false) {
				boof = true;
				$("#overlay-toast").html("opacity should be a number between zero and one");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#btn-two-opc").on('change', function() {
			if ($("#btn-two-opc").val() != undefined && $("#btn-two-opc").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonTwoOpacity = $("#btn-two-opc").val();
			} else {
				$("#overlay-toast").html("Button two opacity must be provided");
				showToast();
			}

			if ((isNaN($("#btn-two-opc").val()) || $("#btn-two-opc").val() > 1 || $("#btn-two-opc").val() < 0) && btof == false) {
				btof = true;
				$("#overlay-toast").html("opacity should be a number between zero and one");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#rvw-ldr-opc").on('change', function() {
			if ($("#rvw-ldr-opc").val() != undefined && $("#rvw-ldr-opc").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewLoaderOpacity = $("#rvw-ldr-opc").val();
			} else {
				$("#overlay-toast").html("Review loader opacity must be provided");
				showToast();
			}

			if ((isNaN($("#rvw-ldr-opc").val()) || $("#rvw-ldr-opc").val() > 1 || $("#rvw-ldr-opc").val() < 0) && rlof == false) {
				rlof = true;
				$("#overlay-toast").html("opacity should be a number between zero and one");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#ld-mr-alt").on('change', function() {
			if ($("#ld-mr-alt").val() != undefined && $("#ld-mr-alt").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewLoaderName = $("#ld-mr-alt").val();
			} else {
				$("#overlay-toast").html("Review loader name must be provided");
				showToast();
			}

			if ($("#ld-mr-alt").val().length == 35 && lmaf == false) {
				lmaf = true;
				$("#overlay-toast").html("A longer name could stretch the button");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#init-rvw-cnt").on('change', function() {
			if ($("#init-rvw-cnt").val() != undefined && $("#init-rvw-cnt").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.initialNumberOfReviews = $("#init-rvw-cnt").val();
			} else {
				$("#overlay-toast").html("Initial Review count must be provided");
				showToast();
			}

			if (isNaN($("#init-rvw-cnt").val()) && ircf == false) {
				ircf = true;
				$("#overlay-toast").html("count should be a number less than 1000");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#onld-rvw-cnt").on('change', function() {
			if ($("#onld-rvw-cnt").val() != undefined && $("#onld-rvw-cnt").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.maxReviewsOnLoadMore = $("#onld-rvw-cnt").val();
			} else {
				$("#overlay-toast").html("On laod Review count must be provided");
				showToast();
			}

			if (isNaN($("#init-rvw-cnt").val()) && orcf == false) {
				orcf = true;
				$("#overlay-toast").html("count should be a number less than 1000");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});
		
		$("#onld-btn-size").on('change', function() {
			if ($("#onld-btn-size").val() != undefined && $("#onld-btn-size").val() != "") {
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.maxWidgetBtnSize = $("#onld-btn-size").val();
			} else {
				$("#overlay-toast").html("Contact and review button sizes must be provided");
				showToast();
			}

			if (isNaN($("#init-rvw-cnt").val()) && orcf == false) {
				orcf = true;
				$("#overlay-toast").html("size should be a number less than 1000");
				showToast();
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$("#flt-ss-chk-box").on('click', function() {
			var revSources = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources;
			if ($("#flt-ss-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-ss-chk-box").removeClass('bd-check-img-checked')
				if (revSources != undefined && revSources != null && revSources != "") {
					if (revSources.indexOf('SocialSurey') == -1) {
						revSources = revSources.trim() + ",SocialSurvey";
						socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources;
					}
				} else {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "SocialSurvey";
				}
			} else {
				$("#flt-ss-chk-box").addClass('bd-check-img-checked')
				if (revSources != undefined && revSources != null && revSources != "") {
					revSources = revSources.replace("SocialSurvey,", "");
					revSources = revSources.replace(",SocialSurvey,", ",");
					revSources = revSources.replace(",SocialSurvey", "");
					if (revSources.length > 12 && revSources.substring(revSources.length - 12, revSources.length) == 'SocialSurvey') {
						revSources = revSources.substring(0, revSources.length - 12);
					}
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources;
				} else {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "SocialSurvey Verified,Zillow";
				}
			}
			socialSurveyJavascriptWidget.displayPreview();
		});

		$("#flt-ssv-chk-box").on('click', function() {
			var revSources = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources;
			if ($("#flt-ssv-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-ssv-chk-box").removeClass('bd-check-img-checked')
				if (revSources != undefined && revSources != null && revSources != "") {
					if (revSources.indexOf('SocialSurey Verified') == -1) {
						revSources = revSources.trim() + ",SocialSurvey Verified";
						socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources;
					}
				} else {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "SocialSurvey Verified";
				}
			} else {
				$("#flt-ssv-chk-box").addClass('bd-check-img-checked')
				if (revSources != undefined && revSources != null && revSources != "") {
					revSources = revSources.replace("SocialSurvey Verified,", "");
					revSources = revSources.replace(",SocialSurvey Verified", "");
					revSources = revSources.replace("SocialSurvey Verified", "");
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources;
				} else {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "SocialSurvey,Zillow";
				}
			}
			socialSurveyJavascriptWidget.displayPreview();
		});

		$("#flt-zw-chk-box").on('click', function() {
			var revSources = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources;
			if ($("#flt-zw-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-zw-chk-box").removeClass('bd-check-img-checked')
				if (revSources != undefined && revSources != null && revSources != "") {
					if (revSources.indexOf('Zillow') == -1) {
						revSources = revSources.trim() + ",Zillow";
						socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources;
					}
				} else {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "Zillow";
				}
			} else {
				$("#flt-zw-chk-box").addClass('bd-check-img-checked')
				if (revSources != undefined && revSources != null && revSources != "") {
					revSources = revSources.replace("Zillow,", "");
					revSources = revSources.replace(",Zillow", "");
					revSources = revSources.replace("Zillow", "");
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources;
				} else {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "SocialSurvey,SocialSurvey Verified";
				}
			}
			socialSurveyJavascriptWidget.displayPreview();
		});

		/*
		 * $("#flt-fb-chk-box").on('click', function() { var revSources = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources; if ($("#flt-fb-chk-box").hasClass('bd-check-img-checked')) { $("#flt-fb-chk-box").removeClass('bd-check-img-checked') if (revSources != undefined && revSources != null && revSources != "") { if (revSources.indexOf('Facebook') == -1) { revSources = revSources.trim() + ",Facebook"; socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources; } } else { socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "Facebook"; } } else { $("#flt-fb-chk-box").addClass('bd-check-img-checked') if (revSources != undefined && revSources != null && revSources != "") { revSources = revSources.replace("Facebook,", ""); revSources = revSources.replace(",Facebook", ""); revSources = revSources.replace("Facebook", ""); socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources; } } socialSurveyJavascriptWidget.displayPreview(); });
		 * 
		 * $("#flt-ln-chk-box").on('click', function() { var revSources = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources; if ($("#flt-ln-chk-box").hasClass('bd-check-img-checked')) { $("#flt-ln-chk-box").removeClass('bd-check-img-checked') if (revSources != undefined && revSources != null && revSources != "") { if (revSources.indexOf('LinkedIn') == -1) { revSources = revSources.trim() + ",LinkedIn"; socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources; } } else { socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "LinkedIn"; } } else { $("#flt-ln-chk-box").addClass('bd-check-img-checked') if (revSources != undefined && revSources != null && revSources != "") { revSources = revSources.replace("LinkedIn,", ""); revSources = revSources.replace(",LinkedIn", ""); revSources = revSources.replace("LinkedIn", ""); socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources; } } socialSurveyJavascriptWidget.displayPreview(); });
		 * 
		 * $("#flt-gl-chk-box").on('click', function() { var revSources = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources; if ($("#flt-gl-chk-box").hasClass('bd-check-img-checked')) { $("#flt-gl-chk-box").removeClass('bd-check-img-checked') if (revSources != undefined && revSources != null && revSources != "") { if (revSources.indexOf('Google') == -1) { revSources = revSources.trim() + ",Google"; socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources; } } else { socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "Google"; } } else { $("#flt-gl-chk-box").addClass('bd-check-img-checked') if (revSources != undefined && revSources != null && revSources != "") { revSources = revSources.replace("Google,", ""); revSources = revSources.replace(",Google", ""); revSources = revSources.replace("Google", ""); socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = revSources; } } socialSurveyJavascriptWidget.displayPreview(); });
		 */

		$('#widget-filter-select-all').on('click', function() {

			if ($("#flt-ss-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-ss-chk-box").removeClass('bd-check-img-checked');
			}

			if ($("#flt-ssv-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-ssv-chk-box").removeClass('bd-check-img-checked');
			}

			if ($("#flt-zw-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-zw-chk-box").removeClass('bd-check-img-checked');
			}

			if ($("#flt-fb-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-fb-chk-box").removeClass('bd-check-img-checked');
			}

			if ($("#flt-ln-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-ln-chk-box").removeClass('bd-check-img-checked');
			}

			if ($("#flt-gl-chk-box").hasClass('bd-check-img-checked')) {
				$("#flt-gl-chk-box").removeClass('bd-check-img-checked');
			}

			// socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "SocialSurvey,SocialSurvey Verified,Zillow,Facebook,LinkedIn,Google";
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources = "SocialSurvey,SocialSurvey Verified,Zillow";
			socialSurveyJavascriptWidget.displayPreview();

		});
		
		$('#enable-mob-view-chk-box').on('click', function() {
			if ($('#enable-mob-view-chk-box').hasClass("bd-check-img-checked")) {
				$('#enable-mob-view-chk-box').removeClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.enableMobView = "true";
			} else {
				$('#enable-mob-view-chk-box').addClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.enableMobView = "false";
			}
			socialSurveyJavascriptWidget.displayPreview();
		});

		$('#hide-bg-initly-chk-box').on('click', function() {
			if ($('#hide-bg-initly-chk-box').hasClass("bd-check-img-checked")) {
				$('#hide-bg-initly-chk-box').removeClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideBarGraph = "true";
			} else {
				$('#hide-bg-initly-chk-box').addClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideBarGraph = "false";
			}
			socialSurveyJavascriptWidget.displayPreview();
		});

		$('#hide-ot-initly-chk-box').on('click', function() {
			if ($('#hide-ot-initly-chk-box').hasClass("bd-check-img-checked")) {
				$('#hide-ot-initly-chk-box').removeClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideOptions = "true";
			} else {
				$('#hide-ot-initly-chk-box').addClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideOptions = "false";
			}
			socialSurveyJavascriptWidget.displayPreview();

		});
		
		$('#hide-con-btn-chk-box').on('click', function() {
			if ($('#hide-con-btn-chk-box').hasClass("bd-check-img-checked")) {
				$('#hide-con-btn-chk-box').removeClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideContactBtn = "true";
			} else {
				$('#hide-con-btn-chk-box').addClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideContactBtn = "false";
			}
			socialSurveyJavascriptWidget.displayPreview();

		});
		
		$('#hide-rev-btn-chk-box').on('click', function() {
			if ($('#hide-rev-btn-chk-box').hasClass("bd-check-img-checked")) {
				$('#hide-rev-btn-chk-box').removeClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideReviewBtn = "true";
			} else {
				$('#hide-rev-btn-chk-box').addClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideReviewBtn = "false";
			}
			socialSurveyJavascriptWidget.displayPreview();

		});

		$('#allw-mdst-brndng-chk-box').on('click', function() {
			if ($('#allw-mdst-brndng-chk-box').hasClass("bd-check-img-checked")) {
				$('#allw-mdst-brndng-chk-box').removeClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.allowModestBranding = "true";
			} else {
				$('#allw-mdst-brndng-chk-box').addClass("bd-check-img-checked")
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.allowModestBranding = "false";
			}
			socialSurveyJavascriptWidget.displayPreview();
		});

		$('#title-tag-text').on("change", function() {
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoTitle = $('#title-tag-text').val();
			// socialSurveyJavascriptWidget.displayPreview();
		});

		$('#kw-tag-text').on("change", function() {
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoKeywords = $('#kw-tag-text').val();
			// socialSurveyJavascriptWidget.displayPreview();
		});

		$('#dsc-tg-txt').on("change", function() {
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoDescription = $('#dsc-tg-txt').val();
			// socialSurveyJavascriptWidget.displayPreview();
		});

		$('#st-dd-wrapper-conf-history').on('click', '.conf-history-option-item', function(event) {
			event.stopPropagation();
			var elementHistory = $(this);
			createGenericConfirmPopup("Rollback Widget Configuration", "Widget template will be reset to the configuration saved at this check point.<br/> " + $(this).html() + "<br/> Do you want to Continue?", "Yes", "No");
			$('#overlay-continue').on('click', {}, function(event) {
				socialSurveyJavascriptWidget.applyHistory(parseInt(elementHistory.find('.widget-hist-opt-message')[0].id.split('-')[1]));
				overlayRevert();
				$('#overlay-continue').off('click');
				$('#overlay-cancel').off('click');
			});
			$('#overlay-cancel').on('click', function() {
				$('#overlay-continue').off('click');
				$('#overlay-cancel').off('click');
				overlayRevert();
			});
			// continue processing other events
			socialSurveyJavascriptWidget.dropdownHandler(event);

		});

		var lockHierarchy = undefined;

		var lockHistory = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.lockHistory;

		if (lockHistory != undefined && lockHistory != null && lockHistory.length > 0 && lockHistory[lockHistory.length - 1].action == "lock") {
			lockHierarchy = "";
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.lockLowerHierarchy = "true";
		} else {
			lockHierarchy = "bd-check-img-checked";
		}

		var overrideLockBtn = undefined;
		if (wProfileLevel == 'INDIVIDUAL') {
			overrideLockBtn = '';
		} else {
			overrideLockBtn = '<div style="padding: 3% 3% 1% 3%;text-align:  left;"><div id="ovrde-save-chk-box" class="float-left bd-check-img bd-check-img-checked" style="height: 40px;"></div><div class="widget-conf-txt">Override Lower Hierarchy</div></div>' + '<div style="padding: 0% 3% 3% 3%;text-align:  left;"><div id="lock-save-chk-box" class="float-left bd-check-img ' + lockHierarchy + '" style="height: 40px;"></div><div class="widget-conf-txt">Override And Lock Lower Hierarchy</div></div>';
		}

		$('#widget-conf-save').on('click', function(event) {
			event.stopPropagation();
			createGenericConfirmPopup("Save Widget Configuration", 'Tag this change with a short message<br/><br/> <input type="text" class="st-item-row-txt widget-commit-bx" placeholder="message"><div class="widget-message-error hide"></div>' + overrideLockBtn, "Submit", "Cancel");
			$('#overlay-continue').on('click', function() {
				if (socialSurveyJavascriptWidget.validateSaveResetMessage()) {
					socialSurveyJavascriptWidget.saveConfiguration($('.widget-commit-bx').val());

					overlayRevert();
					$('#overlay-continue').off('click');
					$('#overlay-cancel').off('click');
				}
			});
			$('#overlay-cancel').on('click', function() {
				$('#overlay-continue').off('click');
				$('#overlay-cancel').off('click');
				overlayRevert();
			});
			// continue processing other events
			socialSurveyJavascriptWidget.dropdownHandler(event);
		})

		/*
		 * $('#widget-conf-reset').on('click', function(event) { event.stopPropagation(); createGenericConfirmPopup("Reset Widget Configuration", 'Tag this change with a short message<br/><br/> <input type="text" class="st-item-row-txt widget-commit-bx" placeholder="message"><div class="widget-message-error hide"></div>', "Submit", "Cancel"); $('#overlay-continue').on('click', function() { if (socialSurveyJavascriptWidget.validateSaveResetMessage()) { socialSurveyJavascriptWidget.resetConfiguration($('.widget-commit-bx').val());
		 * 
		 * overlayRevert(); $('#overlay-continue').off('click'); $('#overlay-cancel').off('click'); } }); $('#overlay-cancel').on('click', function() { $('#overlay-continue').off('click'); $('#overlay-cancel').off('click'); overlayRevert(); }); // continue processing other events socialSurveyJavascriptWidget.dropdownHandler(event); })
		 */

		$('#widget-conf-reset').on('click', function(event) {
			event.stopPropagation();
			socialSurveyJavascriptWidget.resetConfiguration();

			// continue processing other events
			socialSurveyJavascriptWidget.dropdownHandler(event);
		})

		$("#widget-conf-reset, #widget-conf-save").on("keyup", ".widget-commit-bx", function() {
			if ($(this).val() != undefined && $(this).val().length == 35) {
				$('#overlay-toast').html("A short and precise message is preferred");
				showToast();
			}
		});

		$('#widget-reload-conf').on('click', function() {
			showMainContent('./shownewwidget.do');
		});

		$("#overlay-pop-up").off('click', "#ovrde-save-chk-box").on('click', "#ovrde-save-chk-box", function() {

			if (!$("#lock-save-chk-box").hasClass('bd-check-img-checked')) {
				$("#lock-save-chk-box").addClass('bd-check-img-checked');
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.lockLowerHierarchy = "false";
			}

			if ($("#ovrde-save-chk-box").hasClass('bd-check-img-checked')) {
				$("#ovrde-save-chk-box").removeClass('bd-check-img-checked');
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.overrideLowerHierarchy = "true";
			} else {
				$("#ovrde-save-chk-box").addClass('bd-check-img-checked');
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.overrideLowerHierarchy = "false";
			}

		});

		$("#overlay-pop-up").off('click', "#lock-save-chk-box").on('click', "#lock-save-chk-box", function() {

			if (!$("#ovrde-save-chk-box").hasClass('bd-check-img-checked')) {
				$("#ovrde-save-chk-box").addClass('bd-check-img-checked');
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.overrideLowerHierarchy = "false";
			}

			if ($("#lock-save-chk-box").hasClass('bd-check-img-checked')) {
				$("#lock-save-chk-box").removeClass('bd-check-img-checked');
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.lockLowerHierarchy = "true";
			} else {
				$("#lock-save-chk-box").addClass('bd-check-img-checked');
				socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.lockLowerHierarchy = "false";
			}

		});

		// click corrections
		$('#st-dd-wrapper-conf-history').on('click', '.widget-hist-opt-message', function(e) {
			e.stopPropagation();
			$(this).parent().trigger('click');
		})

		$('#widget-conf-history').on('click', '.widget-hist-opt-message', function(e) {
			e.stopPropagation();
			$(this).parent().trigger('click');
		})

		$('#st-dd-wrapper-conf-history').on('click', '.widget-hist-opt-date', function(e) {
			e.stopPropagation();
			$(this).parent().trigger('click');
		})

		$('#widget-conf-history').on('click', '.widget-hist-opt-date', function(e) {
			e.stopPropagation();
			$(this).parent().trigger('click');
		})

	},

	loadConfiguration : function() {

		var payload = {
			"profileName" : wProfileName,
			"profileLevel" : wProfileLevel,
			"companyProfileName" : wCompanyProfileName,
			"hideHistory" : false
		};

		callAjaxGetWithPayloadJsonpData($, './rest/widget/getwidgetdetails', this.getWidgetConfigCallback, function() {
		}, payload, false);
	},

	getWidgetConfigCallback : function(data) {
		socialSurveyJavascriptWidget.widgetDetails = data;
	},

	populateConfiguration : function() {

		// initialize and load the font picker
		initFontPicker($('#fontSelect'), socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.font, function(font) {
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.font = font.trim();
			socialSurveyJavascriptWidget.displayPreview();
		});

		// initialize and load color pickers
		initSpectrum($("#widget-bg-clr"), socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.backgroundColor, false, function(color) {
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.backgroundColor = color.toHexString();
			socialSurveyJavascriptWidget.displayPreview();
		});

		initSpectrum($("#widget-fg-clr"), socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.foregroundColor, false, function(color) {
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.foregroundColor = color.toHexString();
			socialSurveyJavascriptWidget.displayPreview();

		});

		if (socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.barGraphColor == undefined || socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.barGraphColor == null || socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.barGraphColor == "null") {
			initSpectrum($("#widget-bargraph-clr"), false, true, function(color) {
				if (color == null || color == undefined || color.toHexString() == "") {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.barGraphColor = null;
				} else {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.barGraphColor = color.toHexString();
				}
				socialSurveyJavascriptWidget.displayPreview();

			});
		} else {
			initSpectrum($("#widget-bargraph-clr"), socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.barGraphColor, true, function(color) {
				if (color == null || color == undefined || color.toHexString() == "") {
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.barGraphColor = null;
				}else{
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.barGraphColor = color.toHexString();
				}
				socialSurveyJavascriptWidget.displayPreview();

			});
		}

		initSpectrum($("#widget-rating-str-clr"), socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.ratingAndStarColor, false, function(color) {
			socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.ratingAndStarColor = color.toHexString();
			socialSurveyJavascriptWidget.displayPreview();

		});

		if (socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.fontTheme == lightFont) {
			$('#widget-font-theme').val("Light");
		} else {
			$('#widget-font-theme').val("Dark");
		}

		if (socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.embeddedFontTheme == lightFont) {
			$('#widget-embedded-font-theme').val("Light");
		} else {
			$('#widget-embedded-font-theme').val("Dark");
		}

		if ("newestFirst" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder) {
			$('#widget-df-rev-ordr').val("Newest First");
		} else if ("oldestFirst" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder) {
			$('#widget-df-rev-ordr').val("Oldest First");
		} else if ("highestRatingFirst" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder) {
			$('#widget-df-rev-ordr').val("Highest Rated First");
		} else if ("lowestRatingFirst" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSortOrder) {
			$('#widget-df-rev-ordr').val("Lowest Rated First");
		}

		$('#button-one-name').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonOneName);

		$('#button-one-link').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonOneLink);
		$('#btn-one-opc').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonOneOpacity);

		$('#button-two-name').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonTwoName);
		$('#button-two-link').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonTwoLink);
		$('#btn-two-opc').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.buttonTwoOpacity);

		$('#ld-mr-alt').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewLoaderName);
		$('#rvw-ldr-opc').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewLoaderOpacity);

		$('#init-rvw-cnt').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.initialNumberOfReviews);
		$('#onld-rvw-cnt').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.maxReviewsOnLoadMore);
		$('#onld-btn-size').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.maxWidgetBtnSize);

		if (socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoTitle != undefined && socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoTitle != null && socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoTitle != "null") {
			$('#title-tag-text').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoTitle);
		}

		if (socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoKeywords != undefined && socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoKeywords != null && socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoKeywords != "null") {
			$('#kw-tag-text').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoKeywords);
		}

		if (socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoDescription != undefined && socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoDescription != null && socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoDescription != "null") {
			$('#dsc-tg-txt').val(socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.seoDescription);
		}

		if ("false" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.enableMobView) {
			$('#enable-mob-view-chk-box').addClass("bd-check-img-checked");
		} else {
			$('#enable-mob-view-chk-box').removeClass("bd-check-img-checked");
		}
		
		if ("false" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideBarGraph) {
			$('#hide-bg-initly-chk-box').addClass("bd-check-img-checked");
		} else {
			$('#hide-bg-initly-chk-box').removeClass("bd-check-img-checked");
		}

		if ("false" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideOptions) {
			$('#hide-ot-initly-chk-box').addClass("bd-check-img-checked");
		} else {
			$('#hide-ot-initly-chk-box').removeClass("bd-check-img-checked");
		}

		if ("false" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.allowModestBranding) {
			$('#allw-mdst-brndng-chk-box').addClass("bd-check-img-checked");
		} else {
			$('#allw-mdst-brndng-chk-box').removeClass("bd-check-img-checked");
		}

		if ("false" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideContactBtn) {
			$('#hide-con-btn-chk-box').addClass("bd-check-img-checked");
		} else {
			$('#hide-con-btn-chk-box').removeClass("bd-check-img-checked");
		}
		
		if ("false" == socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.hideReviewBtn) {
			$('#hide-rev-btn-chk-box').addClass("bd-check-img-checked");
		} else {
			$('#hide-rev-btn-chk-box').removeClass("bd-check-img-checked");
		}
		
		var revsrs = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.reviewSources;
		reviewSourcesList = [];

		if (revsrs != undefined && revsrs != null && revsrs != "null") {
			var reviewSourcesList = revsrs.split(",");
		}

		if (reviewSourcesList.indexOf("Social Survey") == -1) {
			$('#flt-ss-chk-box').removeClass("bd-check-img-checked");
		} else {
			$('#flt-ss-chk-box').addClass("bd-check-img-checked");
		}

		if (reviewSourcesList.indexOf("Social Survey Verified") == -1) {
			$('#flt-ssv-chk-box').removeClass("bd-check-img-checked");
		} else {
			$('#flt-ssv-chk-box').addClass("bd-check-img-checked");
		}

		if (reviewSourcesList.indexOf("Zillow") == -1) {
			$('#flt-zw-chk-box').removeClass("bd-check-img-checked");
		} else {
			$('#flt-zw-chk-box').addClass("bd-check-img-checked");
		}

		/*
		 * if (reviewSourcesList.indexOf("Facebook") == -1) { $('#flt-fb-chk-box').removeClass("bd-check-img-checked"); } else { $('#flt-fb-chk-box').addClass("bd-check-img-checked"); }
		 * 
		 * if (reviewSourcesList.indexOf("LinkedIn") == -1) { $('#flt-ln-chk-box').removeClass("bd-check-img-checked"); } else { $('#flt-ln-chk-box').addClass("bd-check-img-checked"); }
		 * 
		 * if (reviewSourcesList.indexOf("Google") == -1) { $('#flt-gl-chk-box').removeClass("bd-check-img-checked"); } else { $('#flt-gl-chk-box').addClass("bd-check-img-checked"); }
		 */
		var history = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.history;
		var historyDivList = [];

		$('#st-dd-wrapper-conf-history div').remove();

		if (history == undefined || history == null || history == "null" || history.length <= 0) {
			$("#widget-conf-history").html(this.configHistoryDivCreator(0, "No History Found", new Date().toLocaleString()));
		} else {

			$("#widget-conf-history").html(this.configHistoryDivCreator(0, "Apply a History", (history.length > 10 ? 10 : history.length) + " found"));

			var i, n;
			for (i = 1, n = history.length - 1; i <= 10; i++, n--) {
				if (history[n] == undefined) {
					break;
				} else {
					historyDivList.push(this.configHistoryDivCreator(n, history[n].requestMessage, new Date(history[n].timestamp).toLocaleString()));
				}
			}
		}

		// load history
		autoAppendTextDropdown("#st-dd-wrapper-conf-history", "st-dd-item conf-history-option-item cursor-pointer widget-select-bx widget-hist-adj", historyDivList, true);

		if (socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.lockFlag != 0) {
			$(".widget-code").addClass("disable");
		}

	},

	configHistoryDivCreator : function(id, message, timestamp) {
		return '<div id="\hist-' + id + '\" class="widget-hist-opt-message">' + message + '</div><div class="widget-hist-opt-date">' + timestamp + '</div>';
	},

	displayPreview : function() {
		drawWidgetFramework($, $('#basic-widget-view'), socialSurveyJavascriptWidget.widgetDetails, wProfileName, wProfileLevel, wCompanyProfileName, resourcesUrl, wAppBaseUrl.substring(0, wAppBaseUrl.length - 1));
	},

	applyHistory : function(historyIndex) {
		showOverlay();
		var history = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.history;
		if (history === undefined || history == null || history == "null" || history[historyIndex] === undefined) {
			$('#overlay-toast').html("History record not found");
			showToast();
			return;
		} else {
			for (var n = history.length - 1; n >= historyIndex; n--) {
				var historyX = history[n];

				Object.keys(historyX.changes).forEach(function(key) {
					var value = historyX.changes[key];
					socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration[key] = value;
				});

			}
		}
		this.populateConfiguration();
		this.displayPreview();
		hideOverlay();
	},

	validateConfiguration : function() {

		if ($('#button-one-name').val() == "" || $('#button-one-name').val() == undefined) {

			this.showValidationError("Button one name must be provided");
			return false;

		} else if ($('#button-one-link').val() == "" || $('#button-one-link').val() == undefined) {

			this.showValidationError("Button one link must be provided");
			return false;

		} else if ($('#btn-one-opc').val() == "" || $('#btn-one-opc').val() == undefined) {

			this.showValidationError("Button one opacity must be provided");
			return false;

		} else if ($('#button-two-name').val() == "" || $('#button-two-name').val() == undefined) {

			this.showValidationError("Button two name must be provided");
			return false;

		} else if ($('#button-two-link').val() == "" || $('#button-two-link').val() == undefined) {

			this.showValidationError("Button two link must be provided");
			return false;

		} else if ($('#btn-two-opc').val() == "" || $('#btn-two-opc').val() == undefined) {

			this.showValidationError("Button two opacity must be provided");
			return false;

		} else if ($('#init-rvw-cnt').val() == "" || $('#init-rvw-cnt').val() == undefined) {

			this.showValidationError("Initial review count must be provided");
			return false;

		} else if ($('#onld-rvw-cnt').val() == "" || $('#onld-rvw-cnt').val() == undefined) {

			this.showValidationError("On load review count must be provided");
			return false;

		} else if ($('#onld-btn-size').val() == "" || $('#onld-btn-size').val() == undefined) {

			this.showValidationError("Widget button size must be provided");
			return false;

		}else if ($('#ld-mr-alt').val() == "" || $('#ld-mr-alt').val() == undefined) {

			this.showValidationError("Review loader name must be provided");
			return false;

		} else if ($('#rvw-ldr-opc').val() == "" || $('#rvw-ldr-opc').val() == undefined) {

			this.showValidationError("Review loader opacity must be provided");
			return false;

		} else if (isNaN($('#btn-one-opc').val()) || $('#btn-one-opc').val() < 0 || $('#btn-one-opc').val() > 1) {

			this.showValidationError("button one opacity should be a decimal number between 0 and 1");
			return false;

		} else if (isNaN($('#btn-two-opc').val()) || $('#btn-two-opc').val() < 0 || $('#btn-two-opc').val() > 1) {

			this.showValidationError("button two opacity should be a decimal number between 0 and 1");
			return false;

		} else if (isNaN($('#rvw-ldr-opc').val()) || $('#rvw-ldr-opc').val() < 0 || $('#rvw-ldr-opc').val() > 1) {

			this.showValidationError("Review Loader opacity should be a decimal number between 0 and 1");
			return false;

		} else if (isNaN($('#init-rvw-cnt').val())) {

			this.showValidationError("Initial review count must be a number less than 1000");
			return false;

		} else if (isNaN($('#onld-rvw-cnt').val())) {

			this.showValidationError("On load review count must be a number less than 1000");
			return false;

		} else if (isNaN($('#onld-btn-size').val())) {

			this.showValidationError("Button size must be a number less than 1000");
			return false;

		}else if ($('#button-one-name').val().length > 15) {

			this.showValidationError("Button one name must not have more than 15 characters.");
			return false;

		} else if ($('#button-two-name').val().length > 15) {

			this.showValidationError("Button two name must not have more than 15 characters.");
			return false;

		}

		this.hideValidationError();
		return true;
	},

	showValidationError : function(message) {
		$('.widget-error').text(message);
		$('.widget-error').removeClass('hide');
		shakeElement('.widget-error', "right", 2, 5, 300);
	},

	hideValidationError : function(element) {
		$('.widget-error').text("");
		$('.widget-error').addClass('hide');
	},

	saveConfiguration : function(message) {
		showOverlay();
		if (this.validateConfiguration()) {
			var payload = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration;
			delete payload.history;
			payload.requestMessage = message;
			callAjaxPostWithPayloadData('./savewidgetconfiguration.do', this.saveWidgetConfigCallback, payload, false);
		}
		hideOverlay();
	},

	resetConfiguration : function() {
		showOverlay();
		callAjaxGET('./getdefaultwidgetconfiguration.do', this.getDefaultWidgetConfigCallback, false);
		this.populateConfiguration();
		this.displayPreview();
		hideOverlay();
	},

	getDefaultWidgetConfigCallback : function(data) {
		data = $.parseJSON(data);
		data.history = socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration.history;
		socialSurveyJavascriptWidget.widgetDetails.widgetConfiguration = data;
	},

	validateSaveResetMessage : function() {

		if ($('.widget-commit-bx') == undefined || $('.widget-commit-bx').val() == undefined || $('.widget-commit-bx').val() == '') {
			this.showSaveResetValidationError("A short message must be provided");
			return false;
		}

		this.hideSaveResetValidationError();
		return true;
	},

	showSaveResetValidationError : function(message) {
		$('.widget-message-error').text(message);
		$('.widget-message-error').removeClass('hide');
		shakeElement('.widget-message-error', "right", 2, 5, 300);
	},

	hideSaveResetValidationError : function(element) {
		$('.widget-message-error').text("");
		$('.widget-message-error').addClass('hide');
	},

	saveWidgetConfigCallback : function(data) {

		if (data.charAt(0) === '"' && data.charAt(data.length - 1) === '"') {
			data = data.substr(1, data.length - 2);
		}

		$('#overlay-toast').html(data);
		showToast();
	},

	generateCodeForUse : function() {

		var bodyJs = "";
		var bodyJsCust = "";
		var bodyJsi = "";

		bodyJs = decodeURIComponent(widgetPlaceAndForget);
		bodyJs = bodyJs.replace(/\\n/g, "\n");
		bodyJs = bodyJs.replace(/\\"/g, '"');
		bodyJs = bodyJs.replace(/\+/g, ' ');
		bodyJs = bodyJs.replace("%s", wCompanyProfileName);
		bodyJs = bodyJs.replace("%s", wProfileName);
		bodyJs = bodyJs.replace("%s", wProfileLevel);
		bodyJs = bodyJs.replace("%s", resourcesUrl);

		$("#widget-js-code-area").html(bodyJs);
		$('#overlay-continue-js').click(function() {
			copyWidgetToClipboard("widget-js-code-area");
			$('#overlay-continue-js').unbind('click');
		});

		bodyJsCust = decodeURIComponent(widgetCustomContainer);
		bodyJsCust = bodyJsCust.replace(/\\n/g, "\n");
		bodyJsCust = bodyJsCust.replace(/\\"/g, '"');
		bodyJsCust = bodyJsCust.replace(/\+/g, ' ');
		bodyJsCust = bodyJsCust.replace("%s", wCompanyProfileName);
		bodyJsCust = bodyJsCust.replace("%s", wProfileName);
		bodyJsCust = bodyJsCust.replace("%s", wProfileLevel);
		bodyJsCust = bodyJsCust.replace("%s", resourcesUrl);

		$("#widget-js-cust-code-area").html(bodyJsCust);
		$('#overlay-continue-js-cust').click(function() {
			copyWidgetToClipboard("widget-js-cust-code-area");
			$('#overlay-continue-js-cust').unbind('click');
		});
		
		bodyJsi = decodeURIComponent(widgetJavascriptIframe);
		bodyJsi = bodyJsi.replace(/\\n/g, "\n");
		bodyJsi = bodyJsi.replace(/\\"/g, '"');
		bodyJsi = bodyJsi.replace(/\+/g, ' ');
		bodyJsi = bodyJsi.replace("%s", wCompanyProfileName);
		bodyJsi = bodyJsi.replace("%s", wProfileName);
		bodyJsi = bodyJsi.replace("%s", wProfileLevel);
		bodyJsi = bodyJsi.replace("%s", resourcesUrl);

		$("#widget-jsi-code-area").html(bodyJsi);
		$('#overlay-continue-jsi').click(function() {
			copyWidgetToClipboard("widget-jsi-code-area");
			$('#overlay-continue-jsi').unbind('click');
		});

	},

	dropdownHandler : function(e) {

		if ($(e.target).is('#widget-font-theme') || $(e.target).is('#st-dd-wrapper-widget-font-theme')) {
			$('#st-dd-wrapper-widget-font-theme').slideToggle(200);
		} else if ($('#st-dd-wrapper-widget-font-theme').css('display') != 'none') {
			$('#st-dd-wrapper-widget-font-theme').slideToggle(200);
		}

		if ($(e.target).is('#widget-embedded-font-theme') || $(e.target).is('#st-dd-wrapper-widget-embedded-font-theme')) {
			$('#st-dd-wrapper-widget-embedded-font-theme').slideToggle(200);
		} else if ($('#st-dd-wrapper-widget-embedded-font-theme').css('display') != 'none') {
			$('#st-dd-wrapper-widget-embedded-font-theme').slideToggle(200);
		}

		if ($(e.target).is('#widget-df-rev-ordr') || $(e.target).is('#st-dd-wrapper-df-rev-ordr')) {
			$('#st-dd-wrapper-df-rev-ordr').slideToggle(200);
		} else if ($('#st-dd-wrapper-df-rev-ordr').css('display') != 'none') {
			$('#st-dd-wrapper-df-rev-ordr').slideToggle(200);
		}

		if ($(e.target).is('#widget-conf-history') || $(e.target).is('#st-dd-wrappert-conf-history')) {
			$('#st-dd-wrapper-conf-history').slideToggle(200);
		} else if ($('#st-dd-wrapper-conf-history').css('display') != 'none') {
			$('#st-dd-wrapper-conf-history').slideToggle(200);
		}

	}
};