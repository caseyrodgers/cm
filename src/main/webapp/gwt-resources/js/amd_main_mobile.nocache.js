require.config({
    urlArgs: "bust=20130616"
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
        require(['whiteboard_v3'], function(x) {
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


function setupFloaterHandler() {
    try {
    	var MIN_VISIBLE = 30;
    	var floatEl = document.getElementById('floater_control');
        sb_windowTools.rightElementOnScreen(floatEl);
        window.onscroll = function() {
            // update the scroll information
            sb_windowTools.updateScrollOffset();
            // update the vertical position of the element
            var element = document.getElementById('floater_control');
            
            var top = sb_windowTools.pageDimensions.verticalOffset();
            element.style.top = top + 'px';
            element.style.display = (top > MIN_VISIBLE)?'block':'none';            
        };
    }
    catch(e) { 
        alert('error setting up floater control: ' + e);
    }	
}

function gwt_setDocumentScaling(allowScaling) {
	
	if(true) {
		return;
	}
	
    var viewport = document.querySelector("meta[name=viewport]");
    if(!viewport) {
       return null;
    }
    var args="";
    if(allowScaling) {
        // make scalable
    	 args = "maximum-scale=10.0, user-scalable=1";    
    }
    else {
        // make non-scalable
    	args = "maximum-scale=1.0, user-scalable=0";
    }
    args = "width:device-width, initial-scale=1.0,  " + args;
    
    try {
        viewport.setAttribute("content", args);
    }
    catch(e) {
        alert("Error forcing document scale: " + e);
    }

    var viewport2 = document.querySelector("meta[name=viewport]");
    alert('viewport: ' + viewport2.getAttribute("content"));
}
//
//
//function checkOrientation() {
//  var viewport = document.querySelector("meta[name=viewport]");
//  if (window.orientation === 90 || window.orientation === -90) {
//    return viewport.setAttribute("content", "width:device-width, initial-scale=1.0, user-scalable=1");
//  } else {
//    return viewport.setAttribute("content", "width:device-width, initial-scale=0.6, user-scalable=1");
//  }
//};
//
//window.onorientationchange = function() {
//  return checkOrientation();
//};
//checkOrientation();