function initStartCmMobile() {}

/**
 * register a listener with Tutor to be notified when last step is reached. This
 * is used to only advance when the solution has actually been viewed.
 * 
 * 
 * gwt_solutionHasBeenViewed is defined GWT source: PrescriptionResourceLessonTutorActivity
 * 
 */
HmEvents.eventTutorLastStep.subscribe(function(x) {
	gwt_solutionHasBeenViewed();
});


HmEvents.eventTutorInitialized.subscribe(function(x) {
	gwt_solutionHasBeenInitialized();
});


function showWhiteboardActive(domEl) {
	var pid = domEl.parentNode.parentNode.getAttribute("pid");
	showWhiteboard_Gwt(pid);
}

function debug(s) {
}

function processMathJax() {
    try {
        if (typeof MathJax != "undefined") {
            MathJax.Hub.Queue(["Typeset", MathJax.Hub]);
        }
    } catch (e) {
        alert('Error processing MathJax: ' + e);
    }
}
