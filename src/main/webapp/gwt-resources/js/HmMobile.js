function setupEducationWorldAdServer() {
	selectEducationWorldAdToShow();
	
	
	HmEvents.eventTutorInitialized.subscribe(function(e) {
		selectEducationWorldAdToShow();
	});
}


function selectEducationWorldAdToShow() {
	console.log('setting up ew ad');
	var lb = document.getElementById("leaderboard");
	if(lb) {
		lb.innerHTML = getEwAdFrame();
		lb.style.display = 'block';
	}
}

var _ZONE_MOBILE=128;
var _ZONE_DESKTOP=24;

function getEwAdFrame() {
	return isMobileSmallScreen()? getEwAdFrameMobile(): getEwAdFrameDesktop();
}

function getEwAdFrameMobile() {
    var randN = Math.floor(Math.random() * 99999999999) + '$1';
    var adHtml = null;
    var adHtml = "<iframe id='a3d1175f' name='a3d1175f' src='http://maxmedia.educationworld.com/www/delivery/afr.php?n=a3d1175f&amp;zoneid=" + _ZONE_MOBILE + "&amp;cb="
                    + randN
                    + "' framespacing='0' frameborder='no' scrolling='no' width='100%' height='50'><a href='http://maxmedia.educationworld.com/www/delivery/ck.php?n=afa0b553&amp;cb="
                    + randN
                    + "' target='_blank'><img src='http://maxmedia.educationworld.com/www/delivery/avw.php?zoneid=" + _ZONE_MOBILE + "&amp;cb="
                    + randN
                    + "&amp;n=afa0b553' border='0' alt='' /></a></iframe>";
    return adHtml;
}


function getEwAdFrameDesktop() {
    var randN = Math.floor(Math.random() * 99999999999) + '$1';
    var adHtml = null;
    var adHtml = "<iframe id='a3d1175f' name='a3d1175f' src='http://maxmedia.educationworld.com/www/delivery/afr.php?n=a3d1175f&amp;zoneid=" + _ZONE_DESKTOP + "&amp;cb="
                    + randN
                    + "' framespacing='0' frameborder='no' scrolling='no' width='728' height='90'><a href='http://maxmedia.educationworld.com/www/delivery/ck.php?n=afa0b553&amp;cb="
                    + randN
                    + "' target='_blank'><img src='http://maxmedia.educationworld.com/www/delivery/avw.php?zoneid=" + _ZONE_DESKTOP + "&amp;cb="
                    + randN
                    + "&amp;n=afa0b553' border='0' alt='' /></a></iframe>";
    return adHtml;
}


// block 79
// unknown 3
// block 160;
// school notes 128;
// HM heder 130,
// big header ew 24



<script type='text/javascript'><!--//<![CDATA[
   var m3_u = (location.protocol=='https:'?'https://maxmedia.educationworld.com/www/delivery/ajs.php':'http://maxmedia.educationworld.com/www/delivery/ajs.php');
   var m3_r = Math.floor(Math.random()*99999999999);
   if (!document.MAX_used) document.MAX_used = ',';
   document.write ("<scr"+"ipt type='text/javascript' src='"+m3_u);
   document.write ("?zoneid=162");
   document.write ('&amp;cb=' + m3_r);
   if (document.MAX_used != ',') document.write ("&amp;exclude=" + document.MAX_used);
   document.write (document.charset ? '&amp;charset='+document.charset : (document.characterSet ? '&amp;charset='+document.characterSet : ''));
   document.write ("&amp;loc=" + escape(window.location));
   if (document.referrer) document.write ("&amp;referer=" + escape(document.referrer));
   if (document.context) document.write ("&context=" + escape(document.context));
   if (document.mmm_fo) document.write ("&amp;mmm_fo=1");
   document.write ("'><\/scr"+"ipt>");
//]]>--></script><noscript><a href='http://maxmedia.educationworld.com/www/delivery/ck.php?n=ab10c14e&amp;cb=INSERT_RANDOM_NUMBER_HERE' target='_blank'><img src='http://maxmedia.educationworld.com/www/delivery/avw.php?zoneid=162&amp;cb=INSERT_RANDOM_NUMBER_HERE&amp;n=ab10c14e' border='0' alt='' /></a></noscript>
