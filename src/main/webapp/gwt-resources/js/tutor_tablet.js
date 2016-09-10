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
	processMathJax();
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

    initializeTutor : function(tutorNode, pid, jsonConfig, solutionData, stepText,solutionTitle,showWork, shouldExpandSolution, solutionVariableContext, submitButtonText, indicateWidgetStatus, installCustomSteps, whiteboardText) {
    
    	
    	// whiteboardText = null;
    	
    	
        // create a new wrapper to manage this instance of the tutor
        var tutorWrapper = new TutorWrapper(tutorNode, pid, jsonConfig, solutionData, 
                                            stepText,solutionTitle,showWork, shouldExpandSolution, solutionVariableContext, 
                                            submitButtonText, indicateWidgetStatus, installCustomSteps, whiteboardText);
                              
                              
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
    unregisterTutorWrapper: function(idToRemove) {
        for(var i = 0;i < TutorManager.tutorWrappers.length; i++) {
            if(TutorManager.tutorWrappers[i].id == idToRemove) {
                TutorManager.tutorWrappers.splice(i, 1);  // delete it
            }
        }
    },
    setActiveTutorWrapper: function(id) {
       var tutorWrapper = null;
       for(var i = 0;i < TutorManager.tutorWrappers.length; i++) {
            if(TutorManager.tutorWrappers[i].id == id) {
                tutorWrapper = TutorManager.tutorWrappers[i];
            }
        }
        if(!tutorWrapper) {
           alert('tutorWrapper not found in TutorManager: ' + id);
        }
       TutorManager.activeTutorWrapper = tutorWrapper;
    },
    getActiveTutorWrapper: function() {
        return TutorManager.activeTutorWrapper;
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
    	
        if(pid != null) {
       	    var tutorNode = document.createElement('div');
            var jsonConfig = null;
            var stepText = '';
            var solutionTitle = '';
            var showWork = false;
            var shouldExpandSolution=false;
            var solutionVariableContext = null;
            var submitButtonText = '';
            var indicateWidgetStatus=false;
            var installCustomSteps = false;
            
            TutorManager.initializeTutor(tutorNode, pid, jsonConfig, solutionData, stepText, solutionTitle, showWork, shouldExpandSolution, solutionVariableContext,submitButtonText,indicateWidgetStatus,installCustomSteps);    	
        }
        else {
            TutorDynamic.refreshProblem();
        }

        var tutorWrapper = getAtw();
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






/** var value = 'test';
    var result = (MD5(value));
*/     
var MD5=function(s){function L(k,d){return(k<<d)|(k>>>(32-d))}function K(G,k){var I,d,F,H,x;F=(G&2147483648);H=(k&2147483648);I=(G&1073741824);d=(k&1073741824);x=(G&1073741823)+(k&1073741823);if(I&d){return(x^2147483648^F^H)}if(I|d){if(x&1073741824){return(x^3221225472^F^H)}else{return(x^1073741824^F^H)}}else{return(x^F^H)}}function r(d,F,k){return(d&F)|((~d)&k)}function q(d,F,k){return(d&k)|(F&(~k))}function p(d,F,k){return(d^F^k)}function n(d,F,k){return(F^(d|(~k)))}function u(G,F,aa,Z,k,H,I){G=K(G,K(K(r(F,aa,Z),k),I));return K(L(G,H),F)}function f(G,F,aa,Z,k,H,I){G=K(G,K(K(q(F,aa,Z),k),I));return K(L(G,H),F)}function D(G,F,aa,Z,k,H,I){G=K(G,K(K(p(F,aa,Z),k),I));return K(L(G,H),F)}function t(G,F,aa,Z,k,H,I){G=K(G,K(K(n(F,aa,Z),k),I));return K(L(G,H),F)}function e(G){var Z;var F=G.length;var x=F+8;var k=(x-(x%64))/64;var I=(k+1)*16;var aa=Array(I-1);var d=0;var H=0;while(H<F){Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=(aa[Z]| (G.charCodeAt(H)<<d));H++}Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=aa[Z]|(128<<d);aa[I-2]=F<<3;aa[I-1]=F>>>29;return aa}function B(x){var k="",F="",G,d;for(d=0;d<=3;d++){G=(x>>>(d*8))&255;F="0"+G.toString(16);k=k+F.substr(F.length-2,2)}return k}function J(k){k=k.replace(/rn/g,"n");var d="";for(var F=0;F<k.length;F++){var x=k.charCodeAt(F);if(x<128){d+=String.fromCharCode(x)}else{if((x>127)&&(x<2048)){d+=String.fromCharCode((x>>6)|192);d+=String.fromCharCode((x&63)|128)}else{d+=String.fromCharCode((x>>12)|224);d+=String.fromCharCode(((x>>6)&63)|128);d+=String.fromCharCode((x&63)|128)}}}return d}var C=Array();var P,h,E,v,g,Y,X,W,V;var S=7,Q=12,N=17,M=22;var A=5,z=9,y=14,w=20;var o=4,m=11,l=16,j=23;var U=6,T=10,R=15,O=21;s=J(s);C=e(s);Y=1732584193;X=4023233417;W=2562383102;V=271733878;for(P=0;P<C.length;P+=16){h=Y;E=X;v=W;g=V;Y=u(Y,X,W,V,C[P+0],S,3614090360);V=u(V,Y,X,W,C[P+1],Q,3905402710);W=u(W,V,Y,X,C[P+2],N,606105819);X=u(X,W,V,Y,C[P+3],M,3250441966);Y=u(Y,X,W,V,C[P+4],S,4118548399);V=u(V,Y,X,W,C[P+5],Q,1200080426);W=u(W,V,Y,X,C[P+6],N,2821735955);X=u(X,W,V,Y,C[P+7],M,4249261313);Y=u(Y,X,W,V,C[P+8],S,1770035416);V=u(V,Y,X,W,C[P+9],Q,2336552879);W=u(W,V,Y,X,C[P+10],N,4294925233);X=u(X,W,V,Y,C[P+11],M,2304563134);Y=u(Y,X,W,V,C[P+12],S,1804603682);V=u(V,Y,X,W,C[P+13],Q,4254626195);W=u(W,V,Y,X,C[P+14],N,2792965006);X=u(X,W,V,Y,C[P+15],M,1236535329);Y=f(Y,X,W,V,C[P+1],A,4129170786);V=f(V,Y,X,W,C[P+6],z,3225465664);W=f(W,V,Y,X,C[P+11],y,643717713);X=f(X,W,V,Y,C[P+0],w,3921069994);Y=f(Y,X,W,V,C[P+5],A,3593408605);V=f(V,Y,X,W,C[P+10],z,38016083);W=f(W,V,Y,X,C[P+15],y,3634488961);X=f(X,W,V,Y,C[P+4],w,3889429448);Y=f(Y,X,W,V,C[P+9],A,568446438);V=f(V,Y,X,W,C[P+14],z,3275163606);W=f(W,V,Y,X,C[P+3],y,4107603335);X=f(X,W,V,Y,C[P+8],w,1163531501);Y=f(Y,X,W,V,C[P+13],A,2850285829);V=f(V,Y,X,W,C[P+2],z,4243563512);W=f(W,V,Y,X,C[P+7],y,1735328473);X=f(X,W,V,Y,C[P+12],w,2368359562);Y=D(Y,X,W,V,C[P+5],o,4294588738);V=D(V,Y,X,W,C[P+8],m,2272392833);W=D(W,V,Y,X,C[P+11],l,1839030562);X=D(X,W,V,Y,C[P+14],j,4259657740);Y=D(Y,X,W,V,C[P+1],o,2763975236);V=D(V,Y,X,W,C[P+4],m,1272893353);W=D(W,V,Y,X,C[P+7],l,4139469664);X=D(X,W,V,Y,C[P+10],j,3200236656);Y=D(Y,X,W,V,C[P+13],o,681279174);V=D(V,Y,X,W,C[P+0],m,3936430074);W=D(W,V,Y,X,C[P+3],l,3572445317);X=D(X,W,V,Y,C[P+6],j,76029189);Y=D(Y,X,W,V,C[P+9],o,3654602809);V=D(V,Y,X,W,C[P+12],m,3873151461);W=D(W,V,Y,X,C[P+15],l,530742520);X=D(X,W,V,Y,C[P+2],j,3299628645);Y=t(Y,X,W,V,C[P+0],U,4096336452);V=t(V,Y,X,W,C[P+7],T,1126891415);W=t(W,V,Y,X,C[P+14],R,2878612391);X=t(X,W,V,Y,C[P+5],O,4237533241);Y=t(Y,X,W,V,C[P+12],U,1700485571);V=t(V,Y,X,W,C[P+3],T,2399980690);W=t(W,V,Y,X,C[P+10],R,4293915773);X=t(X,W,V,Y,C[P+1],O,2240044497);Y=t(Y,X,W,V,C[P+8],U,1873313359);V=t(V,Y,X,W,C[P+15],T,4264355552);W=t(W,V,Y,X,C[P+6],R,2734768916);X=t(X,W,V,Y,C[P+13],O,1309151649);Y=t(Y,X,W,V,C[P+4],U,4149444226);V=t(V,Y,X,W,C[P+11],T,3174756917);W=t(W,V,Y,X,C[P+2],R,718787259);X=t(X,W,V,Y,C[P+9],O,3951481745);Y=K(Y,h);X=K(X,E);W=K(W,v);V=K(V,g)}var i=B(Y)+B(X)+B(W)+B(V);return i.toLowerCase()};

/** return MD5 fingerprint of variables in context
 *  this can be used to validate against other solutions
 *  
 */
function _makeContextFingerPrint(context) {
	return MD5(String(_getVariablesFromContext(context)));
}

function _getVariablesFromContext(context) {
    var contextObject = eval('(' + context + ')');
    var vars = contextObject._variables;
    if(!vars) {
    	return "NO VARIABLES";
    }
    var varNames = [];
    for(var i=0;i<vars.length;i++) {
    	varNames[varNames.length] = vars[i].name;
    }
    return varNames;
} 



function _stripValuesFromContext(context) {
    var contextObject = eval('(' + context + ')');
    var vars = contextObject._variables;
    if(!vars) {
    	return "NO VARIABLES";
    }
    for(var i=0;i<vars.length;i++) {
    	v = vars[i];
    	v.value = null;
    }
    var contextStr = JSON.stringify(contextObject._variables)
    return contextStr;
}


/** return list of differences between the two contexts
 * 
 */
function __getDiffBetweenContexts(master, other) {
	var c1 = eval('(' + master + ')');
	var c2 = eval('(' + other + ')');
	
	var v1 = c1._variables;
    var v2 = c2._variables;
 
    if(v1 == null) {
    	return 'no variables in master';
    }
    if(v2 == null) {
    	return 'No variables in other';
    }
    
    var varsInMasterNotInOther = __findVarInContextNotInOther(v1, v2);
    var varsInOtherNotInMaster = __findVarInContextNotInOther(v2, v1);
    
    if(varsInMasterNotInOther.length == 0 && varsInOtherNotInMaster.length == 0) {
    	return null;
    }
    else {
        var msg = '';
        if(varsInMasterNotInOther.length > 0) {
            msg = 'Vars in master, but not in other: ' + varsInMasterNotInOther;
            msg += '\n';
        }
        if(varsInOtherNotInMaster.length > 0) {
            msg += 'Vars in other, but not in master: ' + varsInOtherNotInMaster;
        }
        
        return msg;
    }
}



function __findVarInContextNotInOther(v1, v2) {
	var notFound = [];
    for(var i1=0;i1<v1.length;i1++) {
    	var v = v1[i1];
    	
    	var found=false;
    	for(var i2=0;i2<v2.length;i2++) {
    		if(v2[i2].name == v.name) {
    			found=true;
    			break;
    		}
    	}
    	
    	if(!found) {
			if(!(v.name == "tutor_data_record")) {
    		    notFound[notFound.length] = v.name;
			}
    	}
    }	
    
    return notFound;
}