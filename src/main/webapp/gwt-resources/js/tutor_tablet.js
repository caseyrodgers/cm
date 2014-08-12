/**
 * Main Mobile JS
 *
 *
 *
 * Problem CM abstraction over HM tutor API
 *
 */

var _productionMode = false;


/**
 * Process MathJAX asynchronously
 *
 */
HmEvents.eventTutorInitialized.subscribe(function(tutorWrapper) {
    try {
        //MathJax.Hub.Queue([ "Typeset", MathJax.Hub ]);
    } catch (e) {
        alert("MathJAX processing failed: " + e);
    }
});


/** get ActiveTutorWrapper */
function getAtw() {
   return TutorManager.getActiveTutorWrapper();
}


/**
 * register a listener with Tutor to be notified when last step is reached. This
 * is used to only advance when the solution has actually been completed.
 *
 *
 */
HmEvents.eventTutorLastStep.subscribe(function (x) {
    gwt_solutionHasBeenViewed(String(x));
});


/**
 * Registered listener to be notified after solution is loaded fully If
 * _shouldExpandSteps is true, the move to last step.
 *
 */
HmEvents.eventTutorInitialized.subscribe(function (tutorWrapper) {
    gwt_solutionHasBeenInitialized(tutorWrapper);
});


/** Tutor routines */
function setStepsInfoHelp() {
}
function resetStepsInfo() {
}
function getNextMoveTo() {
}


var TutorManager = {

    tutorWrappers : [],
    activeTutorWrapper : null,

    initializeTutor : function(tutorNode, pid, jsonConfig, solutionData, stepText,solutionTitle,showWork, shouldExpandSolution, solutionVariableContext, submitButtonText, indicateWidgetStatus, installCustomSteps) {
    
        // create a new wrapper to manage this instance of the tutor
        var tutorWrapper = new TutorWrapper(tutorNode, pid, jsonConfig, solutionData, 
                                            stepText,solutionTitle,showWork, shouldExpandSolution, solutionVariableContext, 
                                            submitButtonText, indicateWidgetStatus, installCustomSteps);
                              
                              
        TutorManager.activeTutorWrapper = tutorWrapper;              
        TutorManager.tutorWrappers[TutorManager.tutorWrappers.length] = tutorWrapper;
         
        tutorWrapper.loadTutorData();

        
        if(submitButtonText) {
            // call global tutor configuration
            // defined in tutor_widget.js
            var config = {'submitButtonText': submitButtonText, 'indicateWidgetStatus': indicateWidgetStatus};
            setTutorConfiguration(config);
        }
        
        
        
        /** hookup any question steps */
        // HmEvents.eventTutorInitialized.fire();


        tutorWrapper.initializeTutor(tutorWrapper.context);
        
        if(shouldExpandSolution) {
        	var maxStepUnits = 100;
    		var stepCount = 0;
            for ( stepCount = 0; stepCount < maxStepUnits; stepCount++) {
                var stepUnit = _getStepUnit(stepCount);
                if (stepUnit == null)
                    break; // done
                
                stepUnit.style.display = 'block';
            }
        }
        
    },
    
    unregisterTutorWrapper: function(twToRemove) {
        for(var i = 0;i < TutorManager.tutorWrappers.length; i++) {
            if(TutorManager.tutorWrappers[i] == twToRemove) {
                TutorManager.tutorWrappers[i] = null;
            }
        }
    },
    
    getActiveTutorWrapper: function() {
        return TutorManager.activeTutorWrapper;
    },
    
    setActiveTutorWrapper: function(tutorWrapper) {
       TutorManager.activeTutorManager = tutorWrapper;
    },
    
    showMessage : function(msg) {
        var tm = $get('tutor_message');
        tm.innerHTML = msg;
        setTimeout(function() {
            tm.innerHTML = '&nbsp;';
        }, 2000);
    },
    removeStep : function(x) {
        getAtw().stepUnits.split(x,1);
    },
    showNextStep : function() {
        var tutorWrapper = getAtw();
        if (tutorWrapper.currentStepUnit + 1 < tutorWrapper.stepUnits.length) {
            tutorWrapper.currentStepUnit++;
            tutorWrapper.showStepUnit(tutorWrapper.currentStepUnit);
            
            tutorWrapper.scrollToStep(tutorWrapper.currentStepUnit);
            
        } else {
            TutorManager.showMessage('No more steps');
        }
    },
    showPreviousStep : function() {
        var tutorWrapper = getAtw();
        if (tutorWrapper.currentStepUnit < 0) {
            TutorManager.showMessage('No previous step');
            return;
        } else {
            while (tutorWrapper.currentStepUnit > -1) {

                var step = tutorWrapper.stepUnits[tutorWrapper.currentStepUnit].ele;
                if (tutorWrapper.stepUnits[tutorWrapper.currentStepUnit].realNum != tutorWrapper.currentRealStep) {
                    tutorWrapper.currentRealStep = tutorWrapper.stepUnits[tutorWrapper.currentStepUnit].realNum;
                    break;
                }
                step.style.display = 'none';
                tutorWrapper.currentStepUnit--;
            }
            if (tutorWrapper.currentStepUnit == 0) {
                // move back one to signal not
                // current in a step, only problem def
                tutorWrapper.currentStepUnit = -1;
                // reposition at top
                window.scrollTo(0, 0);
            }
            if (tutorWrapper.currentStepUnit > -1) {
                setAsCurrent(tutorWrapper.stepUnits[tutorWrapper.currentStepUnit].ele);
            }

            tutorWrapper.setButtonState();

            tutorWrapper.scrollToStep(tutorWrapper.currentStepUnit);

            return false;
        }
    },
    resetTutor : function() {
        // gwt_resetTutor();
        
        var tutorWrapper = getAtw();

        var tutor = $get('tutor_raw_steps_wrapper');
        if(tutor) {
            tutor.innerHTML = tutorWrapper.stepText;
        }

        tutorWrapper.currentRealStep = -1;
        tutorWrapper.currentStepUnit = -1;
        tutorWrapper.loadTutorData();
        
        if(tutorWrapper.isVisible) {
            tutorWrapper.setButtonState();
        }
        
        tutorWrapper.resetTutor();
    },

    getCurrentStepNumber: function() {
        return getAtw().currentStepUnit;
    },

    
    backToLesson : function() {
        /**
         * Move back to lesson that called tutor
         *
         * TODO: need gotoLessonForPid(currentPid);
         */
        gwt_backToLesson();
    },
    newProblem : function() {
        var tutorWrapper = getAtw();
        
        var cnt = tutorWrapper.context.probNum;
        var total = tutorWrapper.context.jsonConfig?tutorWrapper.context.jsonConfig.limit:0;
        
        gwt_tutorNewProblem(cnt+1, total);
    },

    showWhiteboard: function() {
        gwt_showWhiteboard();
    },

    showStepUnit: function(x) {
          showStepUnit(x);
    },
    
    
    generateContext: function(pid, solutionData, jsonConfig) {
        var tutorWrapper = getAtw();
        if(pid != null) {
            tutorWrapper.initializeTutor(pid, jsonConfig, solutionData, '','',false, false, null);
        }
        else {
            TutorDynamic.refreshProblem();
        }
        
        var myContext = getTutorVariableContextJson(tutorWrapper.tutorData._variables);
        return myContext;
    }
    
}





// StepUnit is a basic unit
function StepUnit(id, stepUnitNum, type, roleType, realNum, ele) {
    this.id = id;
    this.stepUnitNum = stepUnitNum;
    this.type = type;
    this.roleType = roleType;
    this.realNum = realNum;
    this.ele = ele
}

// StepUnit is one or more units that makeup a
// complete step. Each Step shares the share 'number'
// and theme of the step.
function Step(realNum) {
    this.realNum = realNum;
    this.stepUnits = new Array();
}

function _getStepUnit(s) {
    return _getElement("stepunit", s);
}

function _getHintUnit(s) {
    return _getElement("hintunit", s);
}

function _getFigureUnit(s) {
    return _getElement("figure", s);
}

// find the previous figure unit
// and either return the element
// or return null if not previous
// image exists.
function findPreviousFigureUnit(s) {
    for (p = s - 1; p > -1; p--) {
        fu = _getFigureUnit(p);
        if (fu != null)
            return fu;
    }
    return null;
}

// mark this step unit as current
function setAsNotCurrent(ele) {
    ele.style.backgroundColor = '#E2E2E2';
}

/**
 * Return requested element
 *
 * first look for name='tag', if that fails used id
 *
 */
function _getElement(tag, num) {
    var step = tag + "-" + num;
	return $get(step);    
}



// mark this step unit as current
function setAsCurrent(ele) {
    ele.style.backgroundColor = '#F1F1F1';
}


// find the previous figure unit
// and either return the element
// or return null if not previous
// image exists.
function findPreviousFigureUnit(s) {
    for (p = s - 1; p > -1; p--) {
        fu = _getFigureUnit(p);
        if (fu != null)
            return fu;
    }
    return null;
}


/**
 * Attempt to scroll the document so the tutor bar rests on the bottom line
 * exposing the largest portion of solution.
 *
 *
 * gwt_scrollToBottomOfScrollPanel();
 *
 * @param num
 * @return
 */

function scrollToStep(num) {
    getAtw().scrollToStep(num);
}

function hideAllSteps() {

    var tutorWrapper = getAtw();

    for ( var s = 0; s < tutorWrapper.stepUnits.length; s++) {
        var step = tutorWrapper.stepUnits[s].ele;
        if (step == null)
            return; // done

        if (step.style.display != "none") {
            step.style.display = "none";
        }
    }
    window.scrollTo(0, 0);
}

/**
 * called after control-floater inserted in ControlPanel
 *
 */
function initializeExternalJs() {
    var divName = 'control-floater';
    new FloatLayer(divName, 150, 15, 10);
    detach(divName);
    alignControlFloater();
}

function alignControlFloater() {
    alignFloatLayers();
    setTimeout(alignControlFloater, 2000);
}

/** for solution question/hints */
function doQuestionResponseEnd() {
}

var _activeQuestion;

function doQuestionResponse(key, yesNo) {

    var tutorWrapper = getAtw();
    
    var questionResponse = tutorWrapper.tutorData._strings_moArray[key];

    if (_activeQuestion) {
        var node = document.createElement('div');
        node.className = 'questionResponseAnswer';
        node.innerHTML = questionResponse;

        _activeQuestion.parentNode.appendChild(node);

    } else {
        gwt_showMessage(questionResponse);
    }
}

HmEvents.eventTutorInitialized
        .subscribe(function(tutorWrapper) {

            var tutor = document.getElementById('tutor_raw_steps_wrapper');
            if (tutor == null)
                return;

            var divs = tutor.getElementsByTagName("div");
            var len = divs.length;

            /*
             * Mobile does not have onmoueover, so convert the mouseover to
             * onclick on all hint question_guess elements.
             *
             * We rename the onmouseover to onmouseoverDeferred and the call the
             * deffered event onclick.
             *
             * The event will call doQuestionResponse to be called passing in
             * the proper key to use to lookup the actual response text. The
             * text is kept in a JSON array to provide easy embedding of HTML.
             *
             * In the mobile version this call is caught and passed to and
             * question answer is added to existing question div.
             *
             */
            for ( var d = 0; d < len; d++) {
                var div = divs.item(d);
                if (div.className == 'question_guess') {
                    var imgs = div.getElementsByTagName("img");
                    var imgEl = imgs.item(0);
                    var mo = imgEl.onmouseout = null;
                    imgEl.onmouseoverDeferred = imgEl.onmouseover;
                    imgEl.onmouseover = null;
                    imgEl.onclick = function(x) {
                        var event = (x) ? x : window.event;
                        var target = event.srcElement ? event.srcElement
                                : event.target;
                        _activeQuestion = target;
                        if (!target.onmouseoverDeferred) {
                            alert('error: no deferred move event'); // fail loud
                            return;
                        }
                        target.onclick = null; // only allow one trigger
                        target.onmouseoverDeferred();
                    }
                }
            }

        });


function MessageContext(mLocation, collabPointer, message) {
    this.messageLocation = mLocation;
    this.collabPointer = collabPointer;
    this.message = message;
}

MessageContext.prototype.toString = function() {
    return this.messageLocation + ", " + this.collabPointer;
}

function SolutionMessageLocation(type, str1, int1) {
    this.type = type;
    this.locationString1 = str1;
    this.locationInt1 = int1;
    this.complete = false;
}

// override from tutor.js
// Contact server and load tutor_data.js in
// current tutor's directory. Load as JSON
// data and populate Tutor data structures
function gotoGUID(messageContext, callAfter) {
    var tutorWrapper = getAtw();
    tutorWrapper.resetTutor();
}



function resetTutor() {
    var tutorWrapper = getAtw();
    tutorWrapper.resetTutor();
}



function gotoStepUnit(x) {
    var tutorWrapper = getAtw();
    tutorWrapper.showStepUnit(x);
}


function getCurrentStepUnitNumber() {
    return getAtw().currentStepUnit;
}

function deleteStep(x) {

     var su = 0;
     if(x > 0) {
         su = x*2;
     }

     // completely remove from DOM
     var hintUnit = $get('stepunit-' + su);
     var stepUnit = $get('stepunit-' + (su+1));

     if(hintUnit == null || stepUnit == null) {
         alert('Delete Step: step ' + x + ' not found');
         return false;
     }
     else {
         hintUnit.parentNode.removeChild(hintUnit);
         stepUnit.parentNode.removeChild(stepUnit);

         getAtw().steps.split(x,1);
         getAtw().stepUnits.split(su,2);

         return true;
     }
}



function deleteStep(x) {
     var su = 0;
     if(x > 0) {
         su = x*2;
     }

     // completely remove from DOM
     var hintUnit = $get('stepunit-' + su);
     var stepUnit = $get('stepunit-' + (su+1));

     if(hintUnit == null || stepUnit == null) {
         alert('Delete Step: step ' + x + ' not found');
         return false;
     }
     else {
         hintUnit.parentNode.removeChild(hintUnit);
         stepUnit.parentNode.removeChild(stepUnit);

         
         var tutorWrapper = getAtw();

         tutorWrapper.steps.splice(x,1);
         tutorWrapper.stepUnits.splice(su,2);

         return true;
     }
}




function _getElementTop(eElement)
{
    if (!eElement && this)                    // if argument is invalid
    {                                         // (not specified, is null or is
                                                // 0)
        eElement = this;                       // and function is a method
    }                                         // identify the element as the
                                                // method owner

    var DL_bIE = document.all ? true : false; // initialize var to identify IE

    var nTopPos = eElement.offsetTop;       // initialize var to store
                                            // calculations
    var eParElement = eElement.offsetParent;  // identify first offset parent
                                                // element

    while (eParElement != null)
    {                                         // move up through element
                                                // hierarchy
        if(DL_bIE)
        {
            if(eParElement.tagName == "TD")     // if parent a table cell,
                                                // then...
            {
                nTopPos += eParElement.clientTop; // append cell border width
                                                    // to calcs
            }
        }

        nTopPos += eParElement.offsetTop;    // append top offset of parent
        eParElement = eParElement.offsetParent; // and move up the element
                                                // hierarchy
    }                                         // until no more offset parents
                                                // exist
    return nTopPos;                          // return the number calculated
}








//External JS glue code for GWT to collect
// answers to multiple choice questions
// set TutorPanelWrapper
//
// called from dynamically loaded question HTML
function tutor_questionGuessChanged(o) {
    var id = o.getAttribute('id');
    var selection = o.getAttribute('option_number');
    var value = o.getAttribute('value');
    
    gwt_tutorQuestionGuessChanged(id, selection, value);
}












