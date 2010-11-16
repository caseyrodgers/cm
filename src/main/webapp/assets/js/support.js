var browser_issue = "<p>Catchup Math works on virtually all modern browsers. Internet Option settings " +
                    "should be Cookies: Enabled; Security: Medium or Low; Privacy: Medium or Low; " +
                    "Temporary Internet Files: 'check for new pages every time you start the browser' " +
                    " or 'every visit to the page.</p>";

var firewall_issue = "<p>Some firewall services can cause login failures, with no error message being given. " +
                     "If this happens, please ask your firewall service for assistance.</p>";                  

var router_issue = "<p>If you use a router, you may need to specify that catchupmath.com " +
                   "is a permitted website.</p>";

var bookmark_issue = "<p>We may have improved our pages, so if you bookmark a Catchup Math internal page, re-enter " +
                     "catchupmath.com and then reset the bookmark</p>";

var popupblock_issue = "<p>Catchup Math does not display popup advertisements.  Nevertheless, you may need to inform your " +
                       "popup blocker to allow popups from Catchupmath.com.</p>";

function $get(x) {
    return document.getElementById(x);
}


/** hide the JS error banner */
$get("error_alert").style.display = 'none';

$get('browser-tip').onclick=function() {
	showStandardInfoWindow('Browser Information', browser_issue);
}

$get('firewall-tip').onclick=function() {
	showStandardInfoWindow('Firewall Information', firewall_issue);
}

$get('bookmark-tip').onclick=function() {
	showStandardInfoWindow('Bookmark Information', bookmark_issue);
}

$get('router-tip').onclick=function() {
	showStandardInfoWindow('Router Information', router_issue);
}

$get('popupblocker-tip').onclick=function() {
	showStandardInfoWindow('Popup Blocker Information', popupblock_issue);
}


var _overlay;
function showStandardInfoWindow(title, contents) {
    
    var closer = '<div style="text-align: center;margin-top: 20px;">' +
                 '<a class="sexybutton sexysimple" href="#" onclick="closeOverlay();">Close</a></div>';
    var html = contents
            + closer;
    
    if(_overlay) {
        _overlay.destroy();
    }
    YUI().use('overlay',
                    function(Y) {
                        _overlay = new Y.Overlay(
                                {   
                                    id:"information-box",
                                    width : "350px",
                                    height : "200px",
                                    centered : true,
                                    modal:true,
                                    headerContent : "Information",
                                    bodyContent : html,
                                    zIndex : 2,
                                    visible:true
                                });
                        
                       _overlay.render();
    });
}


function closeOverlay() {
    if(_overlay!=null) {
        _overlay.destroy();
        _overlay = null;
    }
}
