var _productionMode=false;

/** replacement for YUI events */
var HmEvents = {
		listeners:[],
		eventTutorInitialized: {
	          subscribe:function(callBack) {
	               HmEvents.listeners[HmEvents.listeners.length] = callBack;
              },
              fire:function() {
            	  for(var i=0;i<HmEvents.listeners.length;i++) {
            		  HmEvents.listeners[i]();
            	  }
              }
        }
}

function $get(name) {
	return document.getElementById(name);
}


/** Tutor routines  */
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
	tutorData: null,
    initializeTutor : function(pid, solutionData, showWork, expand) {
        TutorManager.pid = pid;
        TutorManager.currentRealStep = -1;
        TutorManager.currentStepUnit = -1;
        TutorManager.loadTutorData(solutionData);
        TutorManager.analyzeLoadedData();
        
        
        /** hookup any question steps */
        
        HmEvents.eventTutorInitialized.fire();
        
        
        
    },
    showMessage:function(msg) {
    	var tm = $get('tutor_message');
    	tm.innerHTML = msg;
    	setTimeout(function(){tm.innerHTML = '&nbsp;';},5000);
    },
    showNextStep:function() {
        if(TutorManager.currentStepUnit+1 < TutorManager.stepUnits.length) {
            TutorManager.currentStepUnit++;
            showStepUnit(TutorManager.currentStepUnit);
        }
        else {
        	  TutorManager.showMessage('no more steps');
        }
     },
     showPreviousStep:function() {

        if(TutorManager.currentStepUnit<0) {
        	TutorManager.showMessage('No previous step');
            return;
        }
        else {
            while(TutorManager.currentStepUnit > 0) {

                var step = TutorManager.stepUnits[TutorManager.currentStepUnit].ele;
                if(TutorManager.stepUnits[TutorManager.currentStepUnit].realNum != TutorManager.currentRealStep) {
                        TutorManager.currentRealStep = TutorManager.stepUnits[TutorManager.currentStepUnit].realNum;
                    break;
                }
                step.style.display = 'none';
                TutorManager.currentStepUnit--;
            }
            if(TutorManager.currentStepUnit == 0) {
                //. move back one to signal not
                // curent in a step, only problem def
                TutorManager.currentStepUnit = -1;
                // reposition at top
                window.scrollTo(0,0);
            }
            if(TutorManager.currentStepUnit > -1)
                setAsCurrent(TutorManager.stepUnits[TutorManager.currentStepUnit].ele);
            return false;
        }
      },
	loadTutorData : function(solutionData) {
		try {
		    TutorManager.tutorData = eval("(" + solutionData + ")");
		} catch (e) {
			alert(e);
		}
	},

	/** read loaded tutor html and create meta data used
	 *  to drive the solution.
	 *  
	 *  The stepUnits are individual parts of a step.
	 *  
	 *  Stepunit contains these attribute:
	 *  id, steprole, steptype, realstep
	 *  
	 *  steps are the complete steps consisting of two stepUnits.
	 *   
	 *   Return count of stepUnits loaded
	 *   
	 */
	analyzeLoadedData : function() {
		TutorManager.stepUnits = [];
		TutorManager.steps = [];

		/** for each step unit div on page 
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
	
	        var su = new StepUnit(id, stepUnitNum, type, role, realNum, stepUnit);
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
    backToLesson:function() {
    	/** Move back to lesson that called tutor
    	 * 
    	 *  TODO: need gotoLessonForPid(currentPid);
    	 *  */
    	gwt_backToLesson();
    }
}

//StepUnit is a basic unit
function StepUnit(id, stepUnitNum, type, roleType, realNum, ele) {
	this.id = id;
	this.stepUnitNum = stepUnitNum;
	this.type = type;
	this.roleType = roleType;
	this.realNum = realNum;
	this.ele = ele
}

// StepUnit is one or more units that makeup a 
// complete step.  Each Step shares the share 'number'
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

//mark this step unit as current
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

//Show the next available step unit
function showStepUnit(num) {
	if(num < 0)
		return;
	
	try {
		var stepElement = TutorManager.stepUnits[num].ele;
		if (stepElement == null)
			return false;
	
		stepElement.style.display = 'block'; // show it
	
		if (stepElement.getAttribute("steprole") == 'step')
			setAsCurrent(stepElement);
	
		setStepTitle(num, stepElement);
	
		// determine if figure should be displayed.  Only display
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
				if (prevFigureUnit != null && prevFigureUnit.src == figureUnit.src) {
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
	
		TutorManager.currentStepUnit = num;
		TutorManager.currentRealStep = TutorManager.stepUnits[num].realNum;
	
		// Turn off next if no more steps
		if (TutorManager.stepUnits[num + 1] == null) {
			setState("step", false);
		} else {
			setState("step", true);
		}
		// back is on
		setState("back", true);
	
		scrollToStep(num);
	}
	catch(e) {
		alert('Error showing step: ' + e);
	}
	return true;
}

//mark this step unit as current
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
			stepTitle.innerHTML = 'Step ' + (parseInt(stepElement
					.getAttribute('realstep')) + 1);
			stepTitle.className = 'step_title_step';
		} else {
			// assume hint
			stepTitle.innerHTML = 'Hint';
			stepTitle.className = 'step_title_hint';
		}
	}
}

//find the previous figure unit 
//and either return the element
//or return null if not previous
//image exists.
function findPreviousFigureUnit(s) {
	for (p = s - 1; p > -1; p--) {
		fu = _getFigureUnit(p);
		if (fu != null)
			return fu;
	}
	return null;
}

//Set the state of the toolbar buttons
function setState(n, onoff) {
}


/** Attempt to scroll the document so the 
 *  tutor bar rests on the bottom line exposing
 *  the largest portion of solution.
 * @param num
 * @return
 */
function scrollToStep(num) {
    var visibleSize = getViewableSize();
    var scrollXy = getScrollXY();
    var visTop = scrollXy[1];
    var visHeight = visibleSize[1];
    var visBot = visHeight + visTop;  
    var buttonBar = DL_GetElementTop(document.getElementById('scrollTo-button'));
    if(buttonBar < visTop || buttonBar > visBot) {
        var st = Number(buttonBar) - visHeight;
        setTimeout("window.scrollTo(0,10000);",0);
    }     
}    
function hideAllSteps() {
    for(var s=0;s<TutorManager.stepUnits.length;s++) {
        var step = TutorManager.stepUnits[s].ele;        
        if(step == null) 
            return;  // done

        if (step.style.display!="none")  {
            step.style.display="none";
        }
    }
    window.scrollTo(0,0);
}

/** called after control-floater inserted in ControlPanel
 * 
 */
function initializeExternalJs() {
    var divName = 'control-floater';
    new FloatLayer(divName,150,15,10);
    detach(divName);
    alignControlFloater();  
}

function alignControlFloater() {
    alignFloatLayers();
    setTimeout(alignControlFloater,2000);
} 




/** for solution question/hints */
function doQuestionResponseEnd() {
}

function doQuestionResponse(key,yesNo) {
    var questionResponse = TutorManager.tutorData._strings_moArray[key];
    gwt_showMessage(questionResponse);
}


HmEvents.eventTutorInitialized.subscribe(function() {
    var tutor = document.getElementById('tutor_raw_steps_wrapper');
    if(tutor == null)
        return;
    
    var divs = tutor.getElementsByTagName("div");
    var len = divs.length;
    
    /* Mobile does not have onmoueover, 
     * so convert the mouseover to onclick
     * on all hint question_guess elements.
     *  
     */
    for(var d=0;d<len;d++) {
        var div = divs.item(d);
        if(div.className == 'question_guess') {
            var imgs = div.getElementsByTagName("img");
            var mo = imgs.item(0).onmouseout = null;
            imgs.item(0).onclick = function() {this.onmouseover();}
        }
    }
    
});
