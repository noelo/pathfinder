function addAuthToken(url){
  var separator;
  if (!url.includes("_t=") && (jwtToken!="" || jwtToken!=undefined || jwtToken!=null)){
	  if (null!=jwtToken && ""!=jwtToken){
		  if (url.includes("?")){
		  	separator="&";
		  }else{
		  	separator="?";
		  }
		  return url+separator+"_t="+jwtToken;
		}
	}
	return url;
}

function getParameterByName(name, url) {
		  if (!url) url = window.location.href;
		  name = name.replace(/[\[\]]/g, "\\$&");
		  var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
		      results = regex.exec(url);
		  if (!results) return null;
		  if (!results[2]) return '';
		  return decodeURIComponent(results[2].replace(/\+/g, " "));
		}

Utils = {
  SERVER: "",
  chartColors: {
  	"UNKNOWN": "#808080",
		"RED": "#cc0000",
		"AMBER": "#ec7a08",
		"GREEN": "#92d400"
  }
}
