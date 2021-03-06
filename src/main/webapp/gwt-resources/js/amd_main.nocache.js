require.config({
	urlArgs: "bust=20141205"
});


/** define highlevel, externalized from GWT requirejs load helper methods
 */
// load all code required to run the whiteboard
function requireJsLoad_whiteboard(funcToCall) {
    alert('REQUIREJS loading whiteboard dependencies: NOT USED, include whiteboard.js');
    funcToCall();
}

function requireJsLoad_calculator(calcContainer, funcToCall) {
    console.log('REQUIREJS loading whiteboard dependencies');
    try {
        require(['calculator/jquery.calculator'], function(x) {
            $('#' + calcContainer).calculator({layout: $.calculator.scientificLayout});
            funcToCall(x);
        });
    }
    catch(ex) {
        alert(ex);
    }
}
console.log('REQUIREJS main loaded');
