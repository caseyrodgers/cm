require(['CatchupMathMobile3_combined'], function(x) {
        console.log('CatchupMathMobile3_combined loaded');
    });


/** define high level, externalized from GWT requirejs load helper methods
 * 
 * TODO: THESE ARE DUPS in amd_main.js, generalize
 * 
 */
// load all code required to run the whiteboard
function requireJsLoad_whiteboard(funcToCall) {
    console.log('REQUIREJS loading whiteboard dependencies');
    try {
        require(['whiteboard-min', 'mathquill/mathquill'], function(x) {
            funcToCall(x);
        });
    }
    catch(ex) {
        alert(ex);
    }
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
