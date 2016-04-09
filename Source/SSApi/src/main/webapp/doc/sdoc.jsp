<!DOCTYPE html>
<html>
<head>
<title>Social Survey - API Documentation</title>
<link href='https://fonts.googleapis.com/css?family=Droid+Sans:400,700'
	rel='stylesheet' type='text/css' />
<link href='css/reset.css' media='screen' rel='stylesheet'
	type='text/css' />
<link href='css/screen.css' media='screen' rel='stylesheet'
	type='text/css' />
<link href='css/reset.css' media='print' rel='stylesheet'
	type='text/css' />
<link href='css/screen.css' media='print' rel='stylesheet'
	type='text/css' />
<link href="images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
<link
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.css"
	rel="stylesheet" />
<link
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.css"
	rel="stylesheet" />
<link
	href="//netdna.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.css"
	rel="stylesheet" />
<link href="css/style-common-1.1.css" rel="stylesheet" />
<script type="text/javascript" src="lib/shred.bundle.js"></script>
<script src='lib/jquery-1.8.0.min.js' type='text/javascript'></script>
<script src='lib/jquery.slideto.min.js' type='text/javascript'></script>
<script src='lib/jquery.wiggle.min.js' type='text/javascript'></script>
<script src='lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
<script src='lib/handlebars-1.0.0.js' type='text/javascript'></script>
<script src='lib/underscore-min.js' type='text/javascript'></script>
<script src='lib/backbone-min.js' type='text/javascript'></script>
<script src='lib/swagger.js' type='text/javascript'></script>
<script src='swagger-ui.js' type='text/javascript'></script>
<script src='lib/highlight.7.3.pack.js' type='text/javascript'></script>

<!-- enabling this will enable oauth2 implicit scope support -->
<script src='lib/swagger-oauth.js' type='text/javascript'></script>
<script type="text/javascript">
	$(function() {
		var apiUrl = window.location.protocol + "//" + window.location.host;

		if (window.location.pathname.indexOf('/api') > 0) {
			apiUrl += window.location.pathname.substring(0,
					window.location.pathname.indexOf('/api'))
		}

		apiUrl += "/api-docs";
		log('API URL: ' + apiUrl);

		window.swaggerUi = new SwaggerUi({
			url : apiUrl,
			dom_id : "swagger-ui-container",
			supportedSubmitMethods : [ 'get', 'post', 'put', 'delete' ],
			onComplete : function(swaggerApi, swaggerUi) {
				log("Loaded SwaggerUI");

				if (typeof initOAuth == "function") {

					initOAuth({
						clientId : "your-client-id",
						realm : "your-realms",
						appName : "your-app-name"
					});

				}
				$('pre code').each(function(i, e) {
					hljs.highlightBlock(e)
				});
			},
			onFailure : function(data) {
				log("Unable to Load SwaggerUI");
			},
			docExpansion : "none"
		});

		$('#input_apiKey').change(
				function() {
					var key = $('#input_apiKey')[0].value;
					log("key: " + key);
					if (key && key.trim() != "") {
						log("added key " + key);
						window.authorizations
								.add("key", new ApiKeyAuthorization("api_key",
										key, "query"));
					}
				})
		window.swaggerUi.load();
	});
</script>
</head>

<body class="swagger-section">
	<!-- Header Section -->
	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo"></div>
		</div>
	</div>
	<div id="message-bar" class="swagger-ui-wrap">&nbsp;</div>
	<div id="swagger-ui-container" class="swagger-ui-wrap"></div>
</body>
</html>
