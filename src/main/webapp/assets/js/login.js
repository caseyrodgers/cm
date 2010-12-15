/** validate login attemp.  If both user/pass are
 *  set send to server, otherwise show error and
 *  do not send to server.
 *  
 * @returns {Boolean}
 */
function doLogin() {
    var user = document.getElementById("fld_user").value;
    var pass = document.getElementById("fld_pwd").value;
    if (!user || !pass) {
        showLoginInvalid();
        return false;
    } else {
        return true;
    }
}

function showLoginInvalid() {
    showDialog("<div style='padding-top: 10px;height: 50px'>Please enter both Login Name and Password.</div>", "Login Invalid");
}

var _overlay = null;

function showDialog(msg, title) {
    if (_overlay) {
        _overlay.hide();
    }

    var closer = '<div style="text-align: center;padding-bottom: 10px;">'
            + '<a class="sexybutton sexysimple" href="#" onclick="closeGeneralDialog();return false;">Close</a></div>';
    var html = '<div style="padding: 10px;">' + msg + '</div>' + closer;

    YUI().use('overlay', function(Y) {
        _overlay = new Y.Overlay({
            id : "general-dialog",
            width : "400px",
            centered : true,
            headerContent : title,
            bodyContent : html,
            zIndex : 2,
            visible : true
        });

        _overlay.render();
    });
}

/** Closed from close button */
function closeGeneralDialog() {
    _overlay.hide();
}


var moreInfo = '' +
    '<ul class="more-info">' +
    '<li>' +
    '<ul>' +
    '<p>Students: Your Password will be one of these:</p>' +
    '<li>A self-registration code, e.g., quizme, jones1, or essentials</li>' +
    '<li>Your name and birth date, e.g., smith-robin-0212, if you self-registered</li>' +
    '<li>Your student ID</li>' +
    '<li>A password that your teacher assigned to you</li>' +
    '</ul>' +
    '<li>' +
    '<p>Teachers: Ask your Account Manager for your admin password.</p>' +
    '</li>' +
    '</ul>';

function showMore() {
  showDialog(moreInfo,"More Info");
}