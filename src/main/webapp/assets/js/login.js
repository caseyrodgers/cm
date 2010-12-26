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

var moreInfo = '' +
    '<div class="more-info">' +
    '<h2>Students</h2>' +
    '<p>Your Password will be one of these:</p>' +
    '<ul>' +
    '<li>A self-registration code, e.g., quizme, jones1, or essential</li>' +
    '<li>Your name and birth date, e.g., smith-robin-0212, if you self-registered</li>' +
    '<li>Your student ID</li>' +
    '<li>A password that your teacher assigned to you</li>' +
    '</ul>' +
    '<h2>Teachers</h2>' + 
    '<p>Ask your Account Manager for your admin password.</p>' +
    '</div>';

function showMore() {
  showDialog(moreInfo,"More Info");
}