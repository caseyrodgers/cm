/** Set focus to user name on page load
 * 
 */ 
YUI().use('event',function(Y) {
  Y.on("domready", function() {
      
    document.getElementById("fld_pwd").value = '';
    //document.getElementById("fld_user").value = '';
    
    document.getElementById('fld_user').focus();
  }, Y, "The DOMContentLoaded event fired.  The DOM is now safe to modify via script.");
});

/**
 * IE9 has String trim() in Standards mode only
 */
if (!String.prototype.trim) {
	String.prototype.trim = function () {  
	    return this.replace(/^\s+|\s+$/g,'');  
	};  
}

/** validate login attempt.  If both user/pass are
 *  set send to server, otherwise show error and
 *  do not send to server.
 *
 * @returns {Boolean}
 */
var _isLoggingIn=false;
function doLogin() {
    if(_isLoggingIn)
        return false;

    var user = document.getElementById("fld_user").value;
    user = user.trim();
    var pass = document.getElementById("fld_pwd").value;
    pass = pass.trim();
    if (!user || user.length==0 || !pass || pass.length==0) {
        showLoginInvalid();
        return false;
    } else {
        // disable the login button to not allow repeats
        _isLoggingIn = true;
        document.getElementById('login_submit').disabled = 'disabled';
        document.getElementById('login_busy').innerHTML = 'Logging you in, please wait...';
        return true;
    }
}


var _overlay;

function showLoggingInDialog(msg, title) {
    if (_overlay) {
        _overlay.hide();
    }
    var closer = '<div style="text-align: center;padding-bottom: 10px;">'
            + '<a class="sexybutton sexysimple" href="#" onclick="closeGeneralDialog();return false;">Close</a></div>';

    closer = '';

    var html = '<div style="padding: 10px;">' + msg + '</div>' + closer;
    var head = title;
    YUI().use('overlay', function(Y) {
        _overlay = new Y.Overlay({
            modal : true,
            width : "320px",
            height: "200px",
            centered : true,
            headerContent : head,
            bodyContent : html,
            zIndex : 2,
            visible : true
        });

        _overlay.render();
    });
}

function showLoginInvalid() {
    showDialog("<div style='padding-top: 10px;height: 50px;z-index:10'>Please enter both Login Name and Password.</div>", "Login Invalid");
}

var moreInfo = '' +
    '<div class="more-info">' +
    '<h2>Students</h2>' +
    '<p>Your Password will be one of these:</p>' +
    '<ul>' +
    '<li>A self-registration code, e.g., quizme, jones1, or essentials</li>' +
    '<li>Your name and birth date, e.g., smith-robin-0212, if you self-registered</li>' +
    '<li>Your student ID</li>' +
    '<li>A password that your teacher/professor assigned to you</li>' +
    '</ul>' +
    '<h2>Teachers</h2>' +
    '<p>Ask your Account Manager for your admin password.</p>' +
    '</div>';

function showMore() {
  showDialog(moreInfo,"More Info");
}

var moreInfoPersonal = '' +
'<div class="more-info">' +
'<p>If you purchased Catchup Math for a school or college course, then use the School/College login method.</p>' +
'<p>If you forgot your personal password, it should be in the original account email you received, unless you changed the password.</p>'
'</div>';

function showMorePersonal() {
showDialog(moreInfoPersonal,"More Info");
}
