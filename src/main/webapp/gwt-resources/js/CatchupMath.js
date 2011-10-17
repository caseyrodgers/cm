var _whiteboardActive = false;
var _questionObjectPointer;

/** provide replacement for missing login_info.js */
var LoginInfo = {
    isValid : function() {
        return true;
    }
}

    function showAskATutorTooltip_Cm() {
        var msg = "<div style='height:100px;padding: 5px;'><p style='margin-bottom: 5px;'>When enabled, this button connects you to an online tutor. Communication is by using an electronic whiteboard and text-chat.</p></div>";
        overlib(msg, FGCLASS, "ol_default_style");
    }

// //////////////////
// / For the tutor viewer
// ///////////////////
// called by GWT to allow proper external setup of tutor
var _shouldExpandSteps;
function doLoad_Gwt(pid, title, jsonConfig, hasShowWork, shouldExpandSteps, solutionHtml, solutionJs) {
    // store in var, registered listener will be notified
    // after solution has been fully initialized
    _shouldExpandSteps = shouldExpandSteps;
    var mc = createNewSolutionMessageContext(pid,jsonConfig);
    mc.solutionData = solutionJs;
    mc.jsonConfig = jsonConfig?eval('(' + jsonConfig + ')'):null;

    var loc = mc.messageLocation;
    if (loc.type != 'solution') {
        alert('MessageContext must be a solution');
    }
    _currentGUID = loc.locationString1;
    var tsw = $get('tutor_raw_steps_wrapper');
    if (!tsw)
        return;

    try {
        if (mc.solutionData == undefined) {
            alert('solutionData not available');
            return;
        }

        var obj = eval('(' + mc.solutionData + ')');
        handleLoadSolutionData(solutionHtml, mc, obj, this.argument);
    } catch (e) {
        alert('CM loadSolutionData catch: ' + e);
    }

    if (hasShowWork) {
        // turn off/hide the ShowWorkFirst button
        var swf = document.getElementById("show-work-force");
        if (swf) {
            swf.style.display = 'none';
        }
    }
}



var _bookMeta = {
    textCode : '',
    isControlled : false
};

// override for tutor
function setBreadCrumbs(crumbs) {
    // empty
}

/**
 * Overridden from tutor5.js
 *
 * Makes sure solution scrolls to bottom of step
 *
 * @param num
 * @return
 */
function scrollToStep(num) {

    // just scroll to bottom, must track the panel that is doing the scrolling
    // @TODO: how to get a better handle on which one? This can easily break
    // if the DOM is changed
    // Perhaps, this needs to call GWT JNSI and have it do the scrolling, or at
    // least provide the source of the scrolling panel ... ?
    var objDiv = document.getElementById("tutor_embedded").parentNode.parentNode;
    objDiv.scrollTop = objDiv.scrollHeight;
}

/**
 * register a listener with Tutor to be notified when last step is reached. This
 * is used to only advance when the solution has actually been completed.
 *
 *
 * solutionHasBeenViewed_Gwt is defined GWT source: PrescriptionCmGuiDefinition
 *
 */
HmEvents.eventTutorLastStep.subscribe(function(x) {
        solutionHasBeenViewed_Gwt(x);
    });

/**
 * Registered listener to be notified after solution is loaded fully If
 * _shouldExpandSteps is true, the move to last step.
 *
 */
HmEvents.eventTutorInitialized.subscribe(function(x) {
        if (_shouldExpandSteps) {
            expandAllSteps();
        }
    });

/**
 * Register a listener with Tutor to be notified when first step is accessed
 */
// HmEvents.eventTutorFirstStep.subscribe( function(x) {
// solutionIsBeingViewed_Gwt(x);
// });
/** Override the Ask a tutor function */
TutorManager.askATutor = function() {
    // do nothing
};

/**
 * overridden from tutor6
 *
 * @return
 */
function isSolutionIsAvailable() {
    return true;
}

/**
 * overridden from tutor6
 *
 * @return
 */
function showNeedToSignup() {
}

function setQuizQuestionActive(pid) {
    setQuizQuestionDisplayAsActive(pid);
}

// //////////////////////
// / End for Tutor /////
// //////////////////////

/**
 * mark the question with guid of pid as being active
 *
 * @param pid
 *            guid of row to select (null selects first)
 * @return
 */
function setQuizQuestionDisplayAsActive(pid) {

    if (true)
        return;

    var testset = document.getElementById("testset_div");
    if (testset == null)
        return;

    var questions = testset.getElementsByTagName("div");
    var activeGuid = null;
    for ( var i = 0, t = questions.length, c = 0; i < t; i++) {
        var d = questions[i];
        if (d.className == 'question_div') {
            if (_whiteboardActive == true
                && (d.getAttribute('guid') == pid || (pid == null && c == 0))) {
                // d.style.background = '#EAEAEA';
                d.style.background = '#EAEAEA url(/gwt-resources/whiteboard_pointer.png) no-repeat top right';
                activeGuid = d.getAttribute('guid');
            } else {
                d.style.background = '';
            }
            c++;
        }
    }
    setQuizActiveQuestion_Gwt(activeGuid);
}

/** called by GWT when the status of the whiteboard changes */
function setWhiteboardIsVisible(wbIsVisible) {
    _whiteboardActive = wbIsVisible;
}

/** called from Flash Input Widget
 *
 * result is JSON string defined as:
 *
 * {
 *     result:'CORRECT/INCORRECT',
 *     input:'THE_INPUT_VALUE',
 *     answer: 'The correct answer',
 *     id: 'pkey'
 *
 * }
 *
 *  */
function flash_quizResult(result) {
    try {
        var resO = eval('(' + result + ')');
        flashInputField_Gwt(resO.result, resO.input, resO.answer, resO.id);
    } catch (e) {
        alert('There was a problem processing Flash Input Field: ' + e);
    }
}

function processMathJax() {
    try {
        if (typeof MathJax != "undefined") {
            MathJax.Hub.Queue([ "Typeset", MathJax.Hub ]);
            //  alert('MathJax log: ' + MathJax.Message.Log());
        }
    } catch (e) {
        alert('Error processing MathJax: ' + e);
    }
}


function get_type(thing) {
    if (thing === null)
        return "[object Null]"; // special case
    return Object.prototype.toString.call(thing);
}
