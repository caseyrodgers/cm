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



alert('install mobile core js');
if (navigator.userAgent.match(/iPhone/i) || navigator.userAgent.match(/iPad/i)) { 
   var viewportmeta = document.querySelector('meta[name="viewport"]'); 
   if (viewportmeta) {
	   console.log('IPad orientation zoom bug fix installed');
	   // disables zoom on orientation change.
       viewportmeta.content = 'width=device-width, minimum-scale=1.0, maximum-scale=1.0'; 
       document.body.addEventListener('gesturestart', function() {viewportmeta.content = 'width=device-width, minimum-scale=0.25, maximum-scale=1.6'; }, false); 
   }
}
