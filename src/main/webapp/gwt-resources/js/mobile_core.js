var MobileCheck = {
	    Android: function() {
	        return navigator.userAgent.match(/Android/i);
	    },
	    BlackBerry: function() {
	        return navigator.userAgent.match(/BlackBerry/i);
	    },
	    iOS: function() {
	        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
	    },
	    Opera: function() {
	        return navigator.userAgent.match(/Opera Mini/i);
	    },
	    Windows: function() {
	        return navigator.userAgent.match(/IEMobile/i);
	    },
	    any: function() {
	        return (MobileCheck.Android() || MobileCheck.BlackBerry() || MobileCheck.iOS() || MobileCheck.Opera() || MobileCheck.Windows());
	    }
};


function isIPadOrIPhone() {
	var check = MobileCheck.any();
	return check != null && MobileCheck.any();
}

function redirectIfMobile() {
	if(isIPadOrIPhone()) {
		document.location = '/not-available-on-mobile.html';
	}
}

