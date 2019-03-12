	<head>
		<title>Pathfinder</title>

		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />

    <link rel="stylesheet" href="https://rawgit.com/RedHatBrand/Overpass/master/webfonts/overpass-webfont/overpass.css"/>
		<link rel="stylesheet" href="assets/css/bootstrap-3.3.7.min.css" />
		<link rel="stylesheet" href="assets/css/main.css" />

	  <script src="assets/js/jquery-3.3.1.min.js"></script>
	  <script>
        	function getParameterByName(name, url) {
        		  if (!url) url = window.location.href;
        		  name = name.replace(/[\[\]]/g, "\\$&");
        		  var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        		      results = regex.exec(url);
        		  if (!results) return null;
        		  if (!results[2]) return '';
        		  return decodeURIComponent(results[2].replace(/\+/g, " "));
        		}

	  	var jwtToken = "<%=session.getAttribute("x-access-token")!=null?session.getAttribute("x-access-token"):""%>";
	  	var customerId=getParameterByName("customerId");
	  	var applicationId=getParameterByName("applicationId");
	  	var assessmentId=getParameterByName("assessmentId");
	  </script>
	</head>
