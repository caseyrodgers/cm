require.config({
    urlArgs: "bust=20130208"
});


require(['CatchupMathMobile3_combined'], function(x) {
        console.log('CatchupMathMobile3_combined loaded');

        setupFloaterControl();
        
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
        require(['whiteboard', 'mathquill/mathquill'], function(x) {
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


function setupFloaterControl() {

    try {
    	var floatEl = document.getElementById('floater_control');
        sb_windowTools.rightElementOnScreen(floatEl);
        window.onscroll = function() {
            // update the scroll information
            sb_windowTools.updateScrollOffset();
            // update the vertical position of the element
            var element = document.getElementById('floater_control');
            
            var top = sb_windowTools.pageDimensions.verticalOffset();
            element.style.top = top + 'px';
            element.style.display = (top > 30)?'block':'none';            
        };
        
        var element = document.getElementById('main-content');
        var hammertime = Hammer(element).on("pinch", function(event) {

        	// ask gwt if pinch zoom should be allowed
        	if(window.gwt_fireBrowserResizedEvent()) {
        	    event.gesture.srcEvent.preventDefault();
        	}
            
        });
        
        
    }
    catch(e) { 
        alert('error setting up floater control: ' + e);
    }	
}