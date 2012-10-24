/**
 * Main Mobile JS
 *
 *
 *
 *
 */

var _productionMode = false;




/**
 * Process MathJAX asynchronously
 *
 */
HmEvents.eventTutorInitialized.subscribe(function() {
    try {
        MathJax.Hub.Queue([ "Typeset", MathJax.Hub ]);
    } catch (e) {
        alert("MathJAX processing failed: " + e);
    }
});


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
HmEvents.eventTutorInitialized.subscribe(function (x) {
    gwt_solutionHasBeenInitialized();
});







function $get(name) {
    return document.getElementById(name);
}

/** Tutor routines */
function setStepsInfoHelp() {
}
function resetStepsInfo() {
}
function getNextMoveTo() {
}


var TutorManager = {
    currentRealStep : -1,
    currentStepUnit : -1,
    stepUnitsMo : [],
    stepUnits : [],
    steps : [],
    pid : '',
    stepUnit : null,
    tutorData : null,
    solutionTitle: null,
    context: null,
    solutionVariableContext:null,
    initializeTutor : function(pid, jsonConfig, solutionData, stepText,solutionTitle,showWork, expand, solutionVariableContext) {
        TutorManager.pid = pid;
        TutorManager.jsonConfig = jsonConfig;
        TutorManager.currentRealStep = -1;
        TutorManager.currentStepUnit = -1;
        TutorManager.stepText = stepText;
        TutorManager.solutionTitle = solutionTitle;
        TutorManager.solutionData = solutionData;
        TutorManager.solutionVariableContext = solutionVariableContext;

        TutorManager.loadTutorData(solutionData, stepText, solutionVariableContext);
        TutorManager.analyzeLoadedData();

        /* mark next button as active */
        enabledNext(true);

        /** hookup any question steps */
        // HmEvents.eventTutorInitialized.fire();
        TutorManager.context = createNewSolutionMessageContext(pid, jsonConfig);
        TutorDynamic.initializeTutor(TutorManager.context);
    },
    showMessage : function(msg) {
        var tm = $get('tutor_message');
        tm.innerHTML = msg;
        setTimeout(function() {
            tm.innerHTML = '&nbsp;';
        }, 2000);
    },
    removeStep : function(x) {
        TutorManager.stepUnits.split(x,1);
    },
    showNextStep : function() {
        if (TutorManager.currentStepUnit + 1 < TutorManager.stepUnits.length) {
            TutorManager.currentStepUnit++;
            showStepUnit(TutorManager.currentStepUnit);
        } else {
            TutorManager.showMessage('no more steps');
        }
    },
    showPreviousStep : function() {
        if (TutorManager.currentStepUnit < 0) {
            TutorManager.showMessage('no previous step');
            return;
        } else {
            while (TutorManager.currentStepUnit > -1) {

                var step = TutorManager.stepUnits[TutorManager.currentStepUnit].ele;
                if (TutorManager.stepUnits[TutorManager.currentStepUnit].realNum != TutorManager.currentRealStep) {
                    TutorManager.currentRealStep = TutorManager.stepUnits[TutorManager.currentStepUnit].realNum;
                    break;
                }
                step.style.display = 'none';
                TutorManager.currentStepUnit--;
            }
            if (TutorManager.currentStepUnit == 0) {
                // move back one to signal not
                // current in a step, only problem def
                TutorManager.currentStepUnit = -1;
                // reposition at top
                window.scrollTo(0, 0);
            }
            if (TutorManager.currentStepUnit > -1) {
                setAsCurrent(TutorManager.stepUnits[TutorManager.currentStepUnit].ele);
            }

            setButtonState();

            scrollToStep(TutorManager.currentStepUnit);

            return false;
        }
    },
    loadTutorData : function(solutionData, stepText, solutionVariableContext) {
        try {
            TutorManager.tutorData = eval("(" + solutionData + ")");

            processTutorData(TutorManager.tutorData, stepText, solutionVariableContext);

        } catch (e) {
            alert(e);
        }
    },
    resetTutor : function() {
        // gwt_resetTutor();

        var tutor = document.getElementById('tutor_raw_steps_wrapper');
        tutor.innerHTML = TutorManager.stepText;

        TutorManager.currentRealStep = -1;
        TutorManager.currentStepUnit = -1;
        TutorManager.loadTutorData(TutorManager.solutionData,TutorManager.stepText);
        TutorManager.analyzeLoadedData();
        setButtonState();
        TutorDynamic.resetTutor();
    },

    getCurrentStepNumber: function() {
        return TutorManager.currentStepUnit;
    },

    /**
     * read loaded tutor html and create meta data used to drive the solution.
     *
     * The stepUnits are individual parts of a step.
     *
     * Stepunit contains these attribute: id, steprole, steptype, realstep
     *
     * steps are the complete steps consisting of two stepUnits.
     *
     * Return count of stepUnits loaded
     *
     */
    analyzeLoadedData : function() {
        TutorManager.stepUnits = [];
        TutorManager.steps = [];

        /**
         * for each step unit div on page
         *
         */
        var maxStepUnits = 100;
        for ( var s = 0; s < maxStepUnits; s++) {
            var stepUnit = _getStepUnit(s);
            if (stepUnit == null)
                break; // done

            var id = stepUnit.getAttribute("id");
            var stepUnitNum = TutorManager.stepUnits.length;
            var role = stepUnit.getAttribute("steprole");
            var type = stepUnit.getAttribute("steptype");
            var realNum = parseInt(stepUnit.getAttribute("realstep"));

            var su = new StepUnit(id, stepUnitNum, type, role, realNum,
                    stepUnit);
            TutorManager.stepUnits[TutorManager.stepUnits.length] = su;

            // is this realStep already created?
            var myStep = TutorManager.steps[realNum];
            if (myStep == null) {
                myStep = new Step(realNum);
                TutorManager.steps[realNum] = myStep;
            }
            myStep.stepUnits[myStep.stepUnits.length] = su;
        }
        return TutorManager.stepUnits.length;
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
        var cnt = TutorManager.context.probNum;
        var total = TutorManager.context.jsonConfig?TutorManager.context.jsonConfig.limit:0;
        
        gwt_tutorNewProblem(cnt+1, total);
    },

    showWhiteboard: function() {
        gwt_showWhiteboard();
    },

    showStepUnit: function(x) {
          showStepUnit(x);
    }


}

function setButtonState() {
    setState('step',
            TutorManager.currentStepUnit < (TutorManager.stepUnits.length - 1));
    setState('back', TutorManager.currentStepUnit > -1);
}

function enabledPrevious(yesNo) {
    enabledButton('steps_prev', yesNo);
}

function enabledNext(yesNo) {
    enabledButton('steps_next', yesNo);
}

function enabledButton(btn, yesNo) {
    var clazz = 'sexybutton sexy_cm_silver ';
    if (!yesNo) {
        clazz += ' disabled';
    }
    var b = $get(btn);
    if(b) {
       b.className = clazz;
    }
    else {
        alert('required button with id [' + btn + '] not found');
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
 */
function _getElement(tag, num) {
    var step = tag + "-" + num;
    return document.getElementById(step);
}

// Show the next available step unit
function showStepUnit(num) {
    if (num < 0)
        return;

    try {
        var stepElement = TutorManager.stepUnits[num].ele;
        if (stepElement == null)
            return false;

        // use animation library to show step
        // appear added as method during intiialize
        // of scripty2
        stepElement.style.display = 'block'; // show it
        //stepElement.appear();

        if (stepElement.getAttribute("steprole") == 'step')
            setAsCurrent(stepElement);

        setStepTitle(num, stepElement);

        // determine if figure should be displayed. Only display
        // if is the first one or different than the previous.
        var figureUnit = _getFigureUnit(num);
        if (figureUnit != null) {
            if (num == 0) {
                figureUnit.style.display = "block";
            } else {
                // only display image if it is not
                // the same as the previously displayed image.
                // find the first previous image.
                var prevFigureUnit = findPreviousFigureUnit(num);
                if (prevFigureUnit != null
                        && prevFigureUnit.src == figureUnit.src) {
                    // skip it
                    figureUnit.style.display = "none";
                } else {
                    // image is different, so show it
                    figureUnit.style.display = "block";
                }
            }
        }

        // make sure all previous hints are invisible
        // stepunits of role == 'hint'
        for (i = num - 1; i > -1; i--) {
            if (TutorManager.stepUnits[i].roleType == 'hint') {
                TutorManager.stepUnits[i].ele.style.display = 'none';
            } else {
                // set as not-current
                setAsNotCurrent(TutorManager.stepUnits[i].ele);
            }
        }

        // make sure all future step unitsare hidden
        for ( var i = num + 1; i < TutorManager.stepUnits.length; i++) {
            if (TutorManager.stepUnits[i].roleType == 'hint') {
                TutorManager.stepUnits[i].ele.style.display = 'none';
            } else {
                // set as not-current
                setAsNotCurrent(TutorManager.stepUnits[i].ele);
            }

        }

        TutorManager.currentStepUnit = num;
        TutorManager.currentRealStep = TutorManager.stepUnits[num].realNum;

        setButtonState();

        scrollToStep(num);

        HmEvents.eventTutorChangeStep.fire(num);

    } catch (e) {
        alert('Error showing step: ' + e);
    }
    return true;
}

// mark this step unit as current
function setAsCurrent(ele) {
    ele.style.backgroundColor = '#F1F1F1';
}

function setStepTitle(num, stepElement) {
    // put title up.
    // put up step number if not a hint
    // otherwise, show 'Hint'
    stepTitle = document.getElementById('step_title-' + num);
    if (stepTitle) {
        var sr = stepElement.getAttribute("steprole");
        if (sr && sr == 'step') {
            stepTitle.innerHTML = 'Step '
                    + (parseInt(stepElement.getAttribute('realstep')) + 1);
            stepTitle.className = 'step_title_step';
        } else {
            // assume hint
            stepTitle.innerHTML = 'Hint';
            stepTitle.className = 'step_title_hint';
        }
    }
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

// Set the state of the toolbar buttons
function setState(n, onoff) {
    if (n == 'step') {
        enabledNext(onoff);

        if (!onoff) {
            TutorDynamic.endOfSolutionReached();
        }
    } else if (n == 'back') {
        enabledPrevious(onoff);
    }
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
    var stb = document.getElementById('scrollTo-button');
    if (stb) {
        var top = _getElementTop(stb);
        var visibleSize = getViewableSize();
        var scrollXy = getScrollXY();
        var visTop = scrollXy[1];
        var visHeight = visibleSize[1];
        var visBot = visHeight + visTop;

        if (true || top < visTop || top > visBot) {
            // alert('Need to scroll, visibleSize: ' + visibleSize + ' scrollXy:
            // ' + scrollXy + ' visTop: '
            // + visTop + ' visHeight: ' + visHeight + ' visBot: ' + visBot + '
            // buttonBar: '
            // + stb);

            gwt_scrollToBottomOfScrollPanel(top - visHeight);
        }
    }
}

function hideAllSteps() {
    for ( var s = 0; s < TutorManager.stepUnits.length; s++) {
        var step = TutorManager.stepUnits[s].ele;
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
    var questionResponse = TutorManager.tutorData._strings_moArray[key];

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
        .subscribe(function() {

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

function createNewSolutionMessageContext(pid, jsonConfig) {
    var loc = new SolutionMessageLocation('solution', pid);
    var mc = new MessageContext(loc);
    if (jsonConfig) {
        try {
            mc.jsonConfig = eval('(' + jsonConfig + ')');
        } catch (e) {
            alert('could not process solution ' + pid + ' config: '
                    + jsonConfig)
        }
    }
    return mc;
}

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
    TutorManager.resetTutor();
}



function resetTutor() {
    TutorManager.resetTutor();
}



function gotoStepUnit(x) {
    TutorManager.showStepUnit(x);
}

function getCurrentStepUnitNumber() {
    return TutorManager.currentStepUnit;
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

         TutorManager.steps.split(x,1);
         TutorManager.stepUnits.split(su,2);

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

         TutorManager.steps.splice(x,1);
         TutorManager.stepUnits.splice(su,2);

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












