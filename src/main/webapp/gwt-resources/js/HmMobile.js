function setupEducationWorldAdServer() {
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

