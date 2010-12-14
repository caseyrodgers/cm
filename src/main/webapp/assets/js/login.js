/** validate login attemp.  If both user/pass are
 *  set send to server, otherwise show error and
 *  do not send to server.
 *  
 * @returns {Boolean}
 */
function doLogin() {
    var user = document.getElementById("fld_user").value;
    var pass = document.getElementById("fld_pwd").value;
    if(!user || !pass) {
        showLoginInvalid();
        return false;
    }
    else {
        return true;
    }
}

function showLoginInvalid() {
    showDialog("Please enter both Login Name and Password.", "Login Invalid");
}

var _overlay=null;

function showDialog(msg,title) {
    if(_overlay) {
        _overlay.hide();
    }
    
        var closer = '<div style="text-align: center;margin-top: 25px;">' +
                     '<a class="sexybutton sexysimple" href="#" onclick="closeGeneralDialog();return false;">Close</a></div>';
        var html = '<div style="padding: 10px;">' +  msg + '</div>'
                + closer;
        
        YUI().use('overlay',
                        function(Y) {
                            _overlay = new Y.Overlay(
                                    {   
                                        id:"general-dialog",
                                        width : "360px",
                                        height : "150px",
                                        centered : true,
                                        headerContent : title,
                                        bodyContent : html,
                                        zIndex : 2,
                                        visible:true
                                    });
                            
                           _overlay.render();
        });
    }

    /** Closed from close button */
    function closeGeneralDialog() {
        _overlay.hide();
    }    
