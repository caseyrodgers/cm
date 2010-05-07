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
	initializeTutor : function(pid, solutionData, showWork, expand) {
		TutorManager.pid = pid;
		TutorManager.loadTutorData(solutionData);
		TutorManager.analyzeLoadedData();
	},
	
	showNextStep:function() {
		if(TutorManager.currentStepUnit+1 < TutorManager.stepUnits.length) {
		    TutorManager.currentStepUnit++;
		    showStepUnit(TutorManager.currentStepUnit);
		}
		else {
			alert('No more steps');
		}
	},
	
	showPreviousStep:function() {
		
		if(TutorManager.currentStepUnit<0) {
			alert('No previous step');
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
		    setAsCurrent(TutorManager.stepUnits[TutorManager.currentStepUnit].ele);
		    return false;			
		}
	},


	loadTutorData : function(solutionData) {
		try {
			var js = solutionData;
			eval("var tutorData = (" + js + ")");
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
    var buttonBar = getElementTop(document.getElementById('scrollTo-button'));
    if(buttonBar < visTop || buttonBar > visBot) {
        var st = Number(buttonBar) - visHeight;
        window.scrollTo(0,(st+25) );
    }     
}    



function getElementTop(eElement) {
    if (!eElement && this)                    // if argument is invalid
    {                                         // (not specified, is null or is 0)
        eElement = this;                       // and function is a method
    }                                         // identify the element as the method owner

    var DL_bIE = document.all ? true : false; // initialize var to identify IE
    
    var nTopPos = eElement.offsetTop;       // initialize var to store calculations 
    var eParElement = eElement.offsetParent;  // identify first offset parent element                            

    while (eParElement != null)                 
    {                                         // move up through element hierarchy
        if(DL_bIE)
        {
            if(eParElement.tagName == "TD")     // if parent a table cell, then...
            {
                nTopPos += eParElement.clientTop; // append cell border width to calcs
            }
        }

        nTopPos += eParElement.offsetTop;    // append top offset of parent
        eParElement = eParElement.offsetParent; // and move up the element hierarchy
    }                                         // until no more offset parents exist
    return nTopPos;                          // return the number calculated
}

function getViewableSize() {
    var myWidth = 0, myHeight = 0;
    if( typeof( window.innerWidth ) == 'number' ) {
        //Non-IE
        myWidth = window.innerWidth;
        myHeight = window.innerHeight;
    } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
        //IE 6+ in 'standards compliant mode'
        myWidth = document.documentElement.clientWidth;
        myHeight = document.documentElement.clientHeight;
    } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
        //IE 4 compatible
        myWidth = document.body.clientWidth;
        myHeight = document.body.clientHeight;
    }
    a = [myWidth, myHeight];
    return  a;
}

function getScrollXY() {
    var scrOfX = 0, scrOfY = 0;
    if( typeof( window.pageYOffset ) == 'number' ) {
        //Netscape compliant
        scrOfY = window.pageYOffset;
        scrOfX = window.pageXOffset;
    } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
        //DOM compliant
        scrOfY = document.body.scrollTop;
        scrOfX = document.body.scrollLeft;
    } else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
        //IE6 standards compliant mode
        scrOfY = document.documentElement.scrollTop;
        scrOfX = document.documentElement.scrollLeft;
    }
    return [ scrOfX, scrOfY ];
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