
require(['CatchupMath_combined'], function(x) {
        console.log('catchup math js loaded');
    });


/** define highlevel, externalized from GWT requirejs load helper methods
 */
// load all code required to run the whiteboard
function requireJsLoad_whiteboard(funcToCall, whiteboardOut) {
    console.log('REQUIREJS loading whiteboard dependencies');
    try {
        require(['whiteboard-min','mathquill/mathquill'], function(x) {
            funcToCall(x);
        });
    }
    catch(ex) {
        alert(ex);
    }
}
console.log('REQUIREJS main loaded');
