function initStartCmMobile() {}




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
