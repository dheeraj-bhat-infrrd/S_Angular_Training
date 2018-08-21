(function() {

	var bootstrapSourceRef = document.createElement('a');
	bootstrapSourceRef.href = document.currentScript.src;
	var dataContainer =  document.currentScript.getAttribute( "data-container" );
	var currScript = document.currentScript;
	
	var resourcesHost = bootstrapSourceRef.origin;
	var jQuery, $;
	var ssHost = undefined;

	if (resourcesHost.indexOf('socialsurvey') < 0 && resourcesHost.indexOf('localhost') < 0 && resourcesHost.indexOf('127.0.0.1') < 0 ) {
		ssHost = "https://www.socialsurvey.me"
	} else {
		ssHost = resourcesHost;
	}

	function loadCss($, link) {
		var css_link = $('<link>', {
			rel : "stylesheet",
			type : "text/css",
			href : link
		});

		if ($('head').length > 0) {
			css_link.appendTo('head');
		} else {
			css_link.appendTo('body');
		}
	}

	function loadScript(url, onLoadFunction) {
		var script_tag = document.createElement('script');
		script_tag.setAttribute("type", "text/javascript");
		script_tag.setAttribute("src", url);

		// Try to find the head, otherwise default to the documentElement
		(document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);

		if (script_tag.readyState) {
			script_tag.onreadystatechange = function() { // For old versions of IE
				if (this.readyState == 'complete' || this.readyState == 'loaded') {
					onLoadFunction();
				}
			};
		} else {// Other browsers
			if (window.addEventListener) {
				script_tag.addEventListener("load", onLoadFunction, false);
			} else if (window.attachEvent) {
				script_tag.attachEvent("onload", onLoadFunction);
			} else {
				script_tag.onload = onLoadFunction;
			}
		}
	}

	// load JQuery
	if (window.jQuery === undefined || window.jQuery.fn.jquery !== '2.1.1') {
		loadScript(resourcesHost + "/widget/js/jquery-2.1.1.min.js", function() {
			jQuery = $ = window.jQuery.noConflict(true);
			main($);
		});

	} else {

		// The jQuery version on the window is the one we want to use
		jQuery = $ = window.jQuery;
		main($);
	}

	function main($) {

		jQuery(document).ready(function($) {

			loadCss($, resourcesHost + "/widget/css/bootstrap-ss-widget.css");
			loadCss($, resourcesHost + "/widget/css/widget-iso.css");
			loadScript(resourcesHost + "/widget/js/widget-framework.js", function() {
				var companyProfileName = $(currScript).data('company-profile-name');
				var profileName = $(currScript).data('profile-name');
				var profileLevel = $(currScript).data('profile-level');
				var widOuterContainer = undefined;
				if (dataContainer != undefined && dataContainer != "" && dataContainer != null ) {
					widOuterContainer = $('#' + dataContainer);
				} else {
					var tempContainer = document.createElement('div');
					currScript.parentNode.insertBefore(tempContainer, currScript.nextSibling);
					widOuterContainer = $(tempContainer);
				}
				initializeWidget($, widOuterContainer, profileName, profileLevel, companyProfileName, resourcesHost, ssHost);
			});
		});
	}

})();
