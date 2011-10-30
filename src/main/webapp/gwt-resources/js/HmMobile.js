/**
 * Main Mobile JS
 * 
 * 
 * 
 * 
 */

var _productionMode = false;

/** replacement for YUI events */
var HmEvents = {

		eventTutorInitialized : {
		listeners : [],
		subscribe : function(callBack) {
			var list = HmEvents.eventTutorInitialized.listeners;
			list[list.length] = callBack;
		},
		fire : function() {
			var list = HmEvents.eventTutorInitialized.listeners;
			for ( var i = 0; i < list.length; i++) {
				list[i]();
			}
		}
	},

	eventTutorWidgetComplete : {
		listeners : [],
		subscribe : function(callBack) {
			var list = HmEvents.eventTutorWidgetComplete.listeners;
			list[list.length] = callBack;
		},
		fire : function() {
			var list = HmEvents.eventTutorWidgetComplete.listeners;
			for ( var i = 0; i < list.length; i++) {
				list[i]();
			}
		}
	},
	eventTutorSetComplete : {
		listeners : [],
		subscribe : function(callBack) {
			var list = HmEvents.eventTutorSetComplete.listeners;
			list[list.length] = callBack;
		},
		fire : function() {
			var list = HmEvents.eventTutorSetComplete.listeners;
			for ( var i = 0; i < list.length; i++) {
				list[i]();
			}
		}
	}
	
	
}

function $get(name) {
	return document.getElementById(name);
}

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
	initializeTutor : function(pid, solutionData, showWork, expand) {
		TutorManager.pid = pid;
		TutorManager.currentRealStep = -1;
		TutorManager.currentStepUnit = -1;
		TutorManager.loadTutorData(solutionData);
		TutorManager.analyzeLoadedData();

		/* mark next button as active*/
		enabledNext(true);

		
		
		TutorDynamic.initializeTutor();
		
		
		/** hookup any question steps */
		// HmEvents.eventTutorInitialized.fire();
	},
	showMessage : function(msg) {
		var tm = $get('tutor_message');
		tm.innerHTML = msg;
		setTimeout(function() {
			tm.innerHTML = '&nbsp;';
		}, 2000);
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
			TutorManager.showMessage('No previous step');
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
				//  move back one to signal not
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
	loadTutorData : function(solutionData) {
		try {
			TutorManager.tutorData = eval("(" + solutionData + ")");
		} catch (e) {
			alert(e);
		}
	},

	/**
	 * read loaded tutor html and create meta data used to drive the
	 * solution.
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
		gwt_tutorNewProblem();
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
	var clazz = 'sexybutton ';
	if (!yesNo) {
		clazz += ' disabled';
	}
	$get(btn).className = clazz;
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

		stepElement.style.display = 'block'; // show it

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

		TutorManager.currentStepUnit = num;
		TutorManager.currentRealStep = TutorManager.stepUnits[num].realNum;

		setButtonState();

		scrollToStep(num);
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
		var top = DL_GetElementTop(stb);
		var visibleSize = getViewableSize();
		var scrollXy = getScrollXY();
		var visTop = scrollXy[1];
		var visHeight = visibleSize[1];
		var visBot = visHeight + visTop;

		if (true || top < visTop || top > visBot) {
			// 	        alert('Need to scroll, visibleSize: ' + visibleSize + ' scrollXy: ' + scrollXy + ' visTop: '
			//	          + visTop + ' visHeight: ' + visHeight + ' visBot: ' + visBot + ' buttonBar: '
			//	          + stb);

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
			 * Mobile does not have onmoueover, so convert the mouseover to onclick on
			 * all hint question_guess elements.
			 * 
			 * We rename the onmouseover to onmouseoverDeferred and the call the
			 * deffered event onclick.
			 * 
			 * The event will call doQuestionResponse to be called passing in the proper
			 * key to use to lookup the actual response text. The text is kept in a JSON
			 * array to provide easy embedding of HTML.
			 * 
			 * In the mobile version this call is caught and passed to and question
			 * answer is added to existing question div.
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

/**
 * CM CORE
 * 
 * 
 * 
 * 
 * 
 */

/**
 * core Catchup Math routines
 * 
 */

/**
 * Find list of questions and for each one call func to and pass the question
 * div to supplied func.
 * 
 */
window.showCorrectAnswers = function(func) {
	var td = document.getElementById('testset_div');
	if (td) {
		var testSet = document.getElementById('testset_div')
				.getElementsByTagName('div');
		for ( var q = 0; q < testSet.length; q++) {
			if (testSet[q].className == 'question_wrapper') {
				func(testSet[q]);
			}
		}
	}
}

/**
 * called by generated quiz HTML when a given question is selected
 * 
 */
function setQuizQuestionActive(x) {
	/** empty */
}

/**
 * Search parents looking for guid attribute
 * 
 * return null if not found
 */

function findQuestionGuid(o) {
	while (o) {
		var guid = o.getAttribute('guid');
		if (guid)
			return guid;
		o = o.parentNode;
	}
	return null;
}

/**
 * Find question with problem identifier
 * 
 * @param pid
 * @return
 */
function findQuestionByPid(pid) {
	var all = document.getElementById('testset_div')
			.getElementsByTagName('div');
	try {
		for ( var i = 0; i < all.length; i++) {
			var o = all[i].getAttribute('guid');
			if (o == pid) {
				return all[i];
			}
		}
	} catch (x) {
		alert('Error while setting selected question response: ' + x);
	}
	alert('findQuestionByPid: pid not found: ' + pid);

	return null;
}

/**
 * Return the question number identified by problem number
 * 
 * @param pid
 * @return
 */
function findQuestionNumberByPid(pid) {
	var all = document.getElementById('testset_div')
			.getElementsByTagName('div');
	var num = 0;
	try {
		for ( var i = 0; i < all.length; i++) {
			var o = all[i].getAttribute('guid');
			if (o) {
				if (o == pid) {
					return num;
				} else {
					num++;
				}
			}
		}
	} catch (x) {
		alert('Error while question index: ' + x);
	}
	alert('findQuestionByPid: pid not found: ' + pid);

	return null;
}

// External JS for GWT
//
// TODO: why optionSkipped?
//
// called from dynamically loaded question HTML
function questionGuessChanged(o) {
	try {
		var pid = findQuestionGuid(o);

		var questionIndex = -1;
		if (o.id == 'optionSkipped') {
			questionIndex = '-2'; // skipped
		} else {

			// how to know which index ..
			// .. go to parent, and look for child that matches this
			// TODO: remove dependency on structure
			//
			var parentUl = o.parentNode.parentNode.parentNode;
			var ndItems = parentUl.getElementsByTagName("input");
			for ( var i = 0; i < ndItems.length; i++) {
				if (ndItems.item(i) == o) {
					questionIndex = i;
					break;
				}
			}
		}
		var questionNum = findQuestionNumberByPid(pid);
		// call GWT JSNI function defined in QuizPage
		questionGuessChanged_Gwt("" + questionNum, "" + questionIndex, pid);
	} catch (e) {
		alert('Error answering question in external JS: ' + e);
	}
}

function setSolutionQuestionAnswerIndexByNumber(questionNum, which) {
	var qn = 0;
	showCorrectAnswers(function(ql) {
		var inputList = ql.getElementsByTagName("input");
		if (qn == questionNum) {
			for ( var i = 0, t = inputList.length; i < t; i++) {
				if (i == which) {
					inputList[i].checked = true;
					var ai = inputList[i];
					questionGuessChanged(ai);
				} else {
					inputList[i].checked = false;
				}
			}
		}
		qn++;
	});
}

// call from JSNI when new question has been loaded in order to set
// the selected question answer
window.setSolutionQuestionAnswerIndex = function(pid, which, disabled) {
	ulNode = findQuestionByPid(pid);
	if (ulNode) {
		var inputElements = ulNode.getElementsByTagName("input");
		for ( var i = 0, t = inputElements.length; i < t; i++) {
			var cb = inputElements.item(i);

			// enable or disable control
			cb.disabled = disabled ? true : false;

			if (i == which) {
				// cb.style.background = 'red';
				cb.checked = true;
			}
		}
	}
}

/**
 * Call into GWT to display the requested resource
 * 
 * @param type
 * @param file
 * @return
 */
function doLoadResource(type, file) {
	doLoadResource_Gwt(type, file);
	return false;
}

/** Mark all questions as correct */
window.markAllCorrectAnswers = function() {
	showCorrectAnswers(markCorrectResponse);
}

/**
 * Return the count of correct answers
 * 
 */
window.getQuizResultsCorrect = function() {
	var count = 0;
	showCorrectAnswers(function(ql) {
		var inputList = ql.getElementsByTagName("input");
		for ( var i = 0, t = inputList.length; i < t; i++) {
			var d = inputList[i].parentNode.getElementsByTagName("div");
			if (d[0].innerHTML == 'Correct') {
				if (inputList[i].checked) {
					count++;
				}
			}
		}
	});
	return count;
}

/**
 * Return total count of questions
 * 
 */
window.getQuizQuestionCount = function() {
	var count = 0;
	showCorrectAnswers(function(ql) {
		count++;
	});
	return count;
}

/**
 * Find list of questions and for each one call func to and pass the question
 * div to supplied func.
 * 
 */
window.showCorrectAnswers = function(func) {
	var testSet = document.getElementById('testset_div').getElementsByTagName(
			'div');
	for ( var q = 0; q < testSet.length; q++) {
		if (testSet[q].className == 'question_wrapper') {
			func(testSet[q]);
		}
	}
}

/**
 * Given a list of question guesses, mark the correct one.
 * 
 */
window.markCorrectResponse = function(questionList) {
	var inputList = questionList.getElementsByTagName("input");
	for ( var i = 0, t = inputList.length; i < t; i++) {
		var d = inputList[i].parentNode.getElementsByTagName("div");
		if (d[0].innerHTML == 'Correct') {
			inputList[i].checked = true;
			var ai = inputList[i];
			questionGuessChanged(ai);
			break;
		}
	}
}

function checkQuiz_Gwt() {
	alert('Checking quiz ...');
}

/**
 * Called from GWT to set the quiz question with the appropriate image
 * 
 */
window.setQuizQuestionResult = function(pid, result) {

	var ql = findQuestionByPid(pid);

	var el = getQuestionMarkImage(pid);
	var elT = getQuestionMarkText(pid);
	if (result == 'Correct') {
		el.src = '/gwt-resources/images/check_correct.png';
		elT.innerHTML = 'Correct';
	} else if (result == 'Incorrect') {
		el.src = '/gwt-resources/images/check_incorrect.png';
		elT.innerHTML = 'Incorrect';
	} else {
		el.src = '/gwt-resources/images/check_notanswered.png';
		elT.innerHTML = 'Not answered';
	}
	el.parentNode.style.display = 'block';
}

/** return the question mark image element */
function getQuestionMarkImage(questionIndex) {
	return document.getElementById("response_image_" + questionIndex);
}

function getQuestionMarkText(questionIndex) {
	return document.getElementById("response_text_" + questionIndex);
}

/**
 * dummy, empty implementation
 * 
 * @return
 */
function log() {
	//
}

/**
 * define empty implementation as place holder
 */
InmhButtons = {};

/**
 * DOM UTILS
 * 
 * 
 * 
 * 
 * 
 * 
 */

function DL_GetElementLeft(eElement) {
	if (!eElement && this) // if argument is invalid
	{ // (not specified, is null or is
		// 0)
		eElement = this; // and function is a method
	} // identify the element as the
	// method owner

	var DL_bIE = document.all ? true : false; // initialize var to identify IE

	var nLeftPos = eElement.offsetLeft; // initialize var to store
	// calculations
	var eParElement = eElement.offsetParent; // identify first offset parent
	// element

	while (eParElement != null) { // move up through element
		// hierarchy
		if (DL_bIE) {
			if (eParElement.tagName == "TD") // if parent a table cell,
			// then...
			{
				nLeftPos += eParElement.clientLeft; // append cell border width
				// to calcs
			}
		}

		nLeftPos += eParElement.offsetLeft; // append left offset of parent
		eParElement = eParElement.offsetParent; // and move up the element
		// hierarchy
	} // until no more offset parents
	// exist
	return nLeftPos; // return the number calculated
}

function DL_GetElementTop(eElement) {
	if (!eElement && this) // if argument is invalid
	{ // (not specified, is null or is
		// 0)
		eElement = this; // and function is a method
	} // identify the element as the
	// method owner

	var DL_bIE = document.all ? true : false; // initialize var to identify IE

	var nTopPos = eElement.offsetTop; // initialize var to store
	// calculations
	var eParElement = eElement.offsetParent; // identify first offset parent
	// element

	while (eParElement != null) { // move up through element
		// hierarchy
		if (DL_bIE) {
			if (eParElement.tagName == "TD") // if parent a table cell,
			// then...
			{
				nTopPos += eParElement.clientTop; // append cell border width
				// to calcs
			}
		}

		nTopPos += eParElement.offsetTop; // append top offset of parent
		eParElement = eParElement.offsetParent; // and move up the element
		// hierarchy
	} // until no more offset parents
	// exist
	return nTopPos; // return the number calculated
}

function getViewableSize() {
	var myWidth = 0, myHeight = 0;
	if (typeof (window.innerWidth) == 'number') {
		// Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if (document.documentElement
			&& (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
		// IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if (document.body
			&& (document.body.clientWidth || document.body.clientHeight)) {
		// IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	a = [ myWidth, myHeight ];
	return a;
}

function getScrollXY() {
	var scrOfX = 0, scrOfY = 0;
	if (typeof (window.pageYOffset) == 'number') {
		// Netscape compliant
		scrOfY = window.pageYOffset;
		scrOfX = window.pageXOffset;
	} else if (document.body
			&& (document.body.scrollLeft || document.body.scrollTop)) {
		// DOM compliant
		scrOfY = document.body.scrollTop;
		scrOfX = document.body.scrollLeft;
	} else if (document.documentElement
			&& (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
		// IE6 standards compliant mode
		scrOfY = document.documentElement.scrollTop;
		scrOfX = document.documentElement.scrollLeft;
	}
	return [ scrOfX, scrOfY ];
}

// DHTML Event Handling
function _addEvent(obj, evType, fn, useCapture) {
	if (obj.addEventListener) {
		obj.addEventListener(evType, fn, useCapture);
		return true;
	} else if (obj.attachEvent) {
		var r = obj.attachEvent("on" + evType, fn);
		return r;
	} else {
		alert("Handler could not be attached");
	}
}

function _removeEvent(obj, evType, fn, useCapture) {
	if (obj.removeEventListener) {
		obj.removeEventListener(evType, fn, useCapture);
		return true;
	} else if (obj.detachEvent) {
		var r = obj.detachEvent("on" + evType, fn);
		return r;
	} else {
		alert("Handler could not be removed");
	}
}

function hideDivOnMouseOut(event) {
	var current, related;

	if (window.event) {
		current = this;
		related = window.event.toElement;
	} else {
		current = event.currentTarget;
		related = event.relatedTarget;
	}

	// log('hideHelpDiv related: ' + related.nodeName + ' (' +
	// related.innerHTML.length + ') value: ' + related.nodeValue + ' current: '
	// + current.nodeName + ' (' + current.innerHTML.length + ')');

	if (current != related) {
		// log('current != related');
		if (!contains(current, related)) {
			// log('related is not in current');

			// dojo.lfx.wipeOut(current,200).play();
			// fadeOut s
			current.style.display = 'none';
		}
	}
}

// return true if a contains b
function contains(a, b) {
	// Return true if node a contains node b.
	while (b.parentNode) {
		b = b.parentNode;
		if (b == a) {
			return true;
		}
	}
	return false;
}

function grabComputedStyle(_10, _11) {
	if (document.defaultView && document.defaultView.getComputedStyle) {
		return document.defaultView.getComputedStyle(_10, null)
				.getPropertyValue(_11);
	} else {
		if (_10.currentStyle) {
			return _10.currentStyle[_11];
		} else {
			return null;
		}
	}
}
function grabComputedHeight(_12) {
	var _13 = grabComputedStyle(_12, "height");
	if (_13 != null) {
		if (_13 == "auto") {
			if (_12.offsetHeight) {
				_13 = _12.offsetHeight;
			}
		}
		_13 = parseInt(_13);
	}
	return _13;
}
function grabComputedWidth(_14) {
	var _15 = grabComputedStyle(_14, "width");
	if (_15 != null) {
		if (_15.indexOf("px") != -1) {
			_15 = _15.substring(0, _15.indexOf("px"));
		}
		if (_15 == "auto") {
			if (_14.offsetWidth) {
				_15 = _14.offsetWidth;
			}
		}
	}
	return _15;
}

/**
 * FLOATER
 * 
 * 
 * 
 * 
 * 
 */

// keep element floating in vertical column, using
// anchor (help_icon) as right position
var _leftAnchor = null;
var _leftAnchorElement;
var _maxTop = null;
var _floatDiv = null;
function detach(divName, leftAnchorElement, doNotDetach) {
	_floatDiv = divName;
	_leftAnchorElement = leftAnchorElement;

	lay = document.getElementById(divName);
	_maxTop = DL_GetElementTop($get(divName));
	l = getXCoord(lay);
	t = getYCoord(lay);

	if (!doNotDetach)
		lay.style.position = 'absolute';

	// if function passed in allow it to
	// handle the moving element. This is to
	// get around compound eles linke in YUI
	if (_funcToCallOnRePosition) {
		_funcToCallOnRePosition(lay, l, t);
	} else {
		lay.style.top = t + 'px';
		lay.style.left = l + 'px';
	}
	getFloatLayer(divName).initialize();
}

// ///////////////////////////////////////////////////////////////////

var FloatLayers = new Array();
var FloatLayersByName = new Array();
var _funcToCallOnRePosition;

function addFloatLayer(n, offX, offY, spd) {
	new FloatLayer(n, offX, offY, spd);
}
function getFloatLayer(n) {
	return FloatLayersByName[n];
}

var _isAligning; // stop recursive loop
function alignFloatLayers() {

	if (_isAligning)
		return; // busy

	_isAligning = true;
	var fd = $get(_floatDiv);
	var hil = 0;
	var hiw = 0;
	var hir = 0;
	if (_leftAnchorElement) {
		var hi = _leftAnchorElement;
		hil = DL_GetElementLeft(hi); // get left position
		hiw = grabComputedWidth(hi);
		hir = parseInt(hil) + parseInt(hiw);
	} else {
		hiw = grabComputedWidth($get(_floatDiv));
		hil = getViewableSize();
		hir = hil[0] - hiw;
	}

	fdw = grabComputedWidth(fd);
	fdl = parseInt(hir) - parseInt(fdw);
	_leftAnchor = fdl;
	for ( var i = 0; i < FloatLayers.length; i++)
		FloatLayers[i].align();
	_isAligning = false;
}

function getXCoord(el) {
	x = 0;
	while (el) {
		x += el.offsetLeft;
		el = el.offsetParent;
	}
	return x;
}
function getYCoord(el) {
	y = 0;
	while (el) {
		y += el.offsetTop;
		el = el.offsetParent;
	}
	return y;
}

// ///////////////////////////////////////////////////////////////////

FloatLayer.prototype.setFloatToTop = setTopFloater;
FloatLayer.prototype.setFloatToBottom = setBottomFloater;
FloatLayer.prototype.setFloatToLeft = setLeftFloater;
FloatLayer.prototype.setFloatToRight = setRightFloater;
FloatLayer.prototype.initialize = defineFloater;
FloatLayer.prototype.adjust = adjustFloater;
FloatLayer.prototype.align = alignFloater;

function FloatLayer(n, offX, offY, spd, funcToCallOnRePosition) {
	this.index = FloatLayers.length;
	_funcToCallOnRePosition = funcToCallOnRePosition;

	FloatLayers.push(this);
	FloatLayersByName[n] = this;

	this.name = n;
	this.floatX = 0;
	this.floatY = 0;
	this.tm = null;
	this.steps = spd;
	this.alignHorizontal = (offX >= 0) ? leftFloater : rightFloater;
	this.alignVertical = (offY >= 0) ? topFloater : bottomFloater;
	this.ifloatX = Math.abs(offX);
	this.ifloatY = Math.abs(offY);
}

// ///////////////////////////////////////////////////////////////////

function defineFloater() {
	if (!this.layer)
		this.layer = document.getElementById(this.name);

	this.width = this.layer.offsetWidth;
	this.height = this.layer.offsetHeight;
	this.prevX = this.layer.offsetLeft;
	this.prevY = this.layer.offsetTop;
}
var cnt = 0;
function adjustFloater() {
	this.tm = null;
	// if(this.layer.style.position!='absolute')return;
	var dx = Math.abs(this.floatX - this.prevX);
	var dy = Math.abs(this.floatY - this.prevY);

	if (dx < this.steps / 2)
		cx = (dx >= 1) ? 1 : 0;
	else
		cx = Math.round(dx / this.steps);

	if (dy < this.steps / 2)
		cy = (dy >= 1) ? 1 : 0;
	else
		cy = Math.round(dy / this.steps);
	if (this.floatX > this.prevX)
		this.prevX += cx;
	else if (this.floatX < this.prevX)
		this.prevX -= cx;

	if (this.floatY > this.prevY)
		this.prevY += cy;
	else if (this.floatY < this.prevY)
		this.prevY -= cy;

	if ((_maxTop > 0) && this.prevY < _maxTop) {
		this.prevY = _maxTop;
	}

	if (_funcToCallOnRePosition) {
		cnt++;
		// log('adjustFloater: ' + this.prevY);
		_funcToCallOnRePosition(this.layer, _leftAnchor, this.prevY);
	} else {
		this.layer.style.left = _leftAnchor + 'px'; // this.prevX + 'px';
		this.layer.style.top = this.prevY + 'px';
	}

	if (cx != 0 || cy != 0) {
		log('cx: ' + cx + '   cy: ' + cy);

		if (this.tm == null)
			this.tm = setTimeout('FloatLayers[' + this.index + '].adjust()', 50);
	} else
		alignFloatLayers();
}

function setLeftFloater() {
	this.alignHorizontal = leftFloater;
}
function setRightFloater() {
	this.alignHorizontal = rightFloater;
}
function setTopFloater() {
	this.alignVertical = topFloater;
}
function setBottomFloater() {
	this.alignVertical = bottomFloater;
}

function leftFloater() {
	this.floatX = document.body.scrollLeft + this.ifloatX;
}
function topFloater() {
	var scrollY = getScrollXY()[1];
	this.floatY = scrollY + this.ifloatY;
}
function rightFloater() {
	this.floatX = document.body.scrollLeft + document.body.clientWidth
			- this.ifloatX - this.width;
}
function bottomFloater() {
	this.floatY = document.body.scrollTop + document.body.clientHeight
			- this.ifloatY - this.height;
}

function alignFloater() {
	this.initialize();
	this.alignHorizontal();
	this.alignVertical();
	if (this.prevX != this.floatX || this.prevY != this.floatY) {
		if (this.tm == null)
			this.tm = setTimeout('FloatLayers[' + this.index + '].adjust()', 50);
	}
}

/**
 * tutor_flash_widget.js
 * 
 * 
 * 
 * 
 * 
 */

// version 1.1
var _json;
var HmFlashWidgetFactory = {
	createWidget : function(jsonObj) {

		if (jsonObj.type == 'number_integer') {
			if (jsonObj.format == 'angle_deg') {
				return new HmFlashWidgetImplNumberIntegerAngleDeg(jsonObj);
			} else {
				return new HmFlashWidgetImplNumberInteger(jsonObj);
			}
		} else if (jsonObj.type == 'number_decimal') {
			if (jsonObj.format == 'money') {
				return new HmFlashWidgetImplNumberMoney(jsonObj);
			} else {
				return new HmFlashWidgetImplNumberInteger(jsonObj);
			}
		} else if (jsonObj.type == 'inequality') {
			return new HmFlashWidgetImplInequality(jsonObj);
		} else if (jsonObj.type == 'number_fraction') {
			return new HmFlashWidgetImplNumberIntegerFraction(jsonObj);
		} else if (jsonObj.type == 'number_simple_fraction') {
			return new HmFlashWidgetImplSimpleFraction(jsonObj);
		} else if (jsonObj.type == 'number_rational') {
			return new HmFlashWidgetImplRational(jsonObj);
		} else if (jsonObj.type == 'mChoice') {
			return new HmFlashWidgetImplMulti(jsonObj);
		} else if (jsonObj.type == 'coordinates') {
			return new HmFlashWidgetImplCoord(jsonObj);
		} else if (jsonObj.type == 'number_mixed_fraction') {
			return new HmFlashWidgetImplMixedFraction(jsonObj);
		} else if (jsonObj.type == 'power_form') {
			return new HmFlashWidgetImplPowerForm(jsonObj);
		} else if (jsonObj.type == 'scientific_notation') {
			return new HmFlashWidgetImplSciNotation(jsonObj);
		} else if (jsonObj.type == 'letter') {
			return new HmFlashWidgetImplLetter(jsonObj);
		} else if (jsonObj.type == 'odds') {
			return new HmFlashWidgetImplOdds(jsonObj);
		} else if (jsonObj.type == 'point_slope_form') {
			return new HmFlashWidgetImplPointSlopeForm(jsonObj);
		} else if (jsonObj.type == 'inequality_exact') {
			return new HmFlashWidgetImplInequalityExact(jsonObj);
		} else {
			alert("tutor widget: do not know how to initialize: "
					+ jsonObj.type);
		}
	}
};

/** Called when Flash Tutor widget calls */
function flash_quizResult(x) {
	// enable the Tutor's next button
	// setState('step',true);
}

/** Create question element */
function _createQuestionStep(el) {
	// first child div is the question
	var divs = el.getElementsByTagName('div');

	for ( var d = 0, t = divs.length; d < t; d++) {
		var del = divs[d];
		if (d == 0) {
			// the question text
		} else if ((d % 2) != 0) {
			// a choice
			var html = "<img src='/images/tutor5/hint_question-16x16.gif' onmouseover='showMouseAnswer(this)' onmouseout='nd()'/>";
			del.innerHTML = html + del.innerHTML;
		} else {
			del.style.display = 'none';
		}
	}
}

function showMouseAnswer(el) {
	var imgTarget = el;
	var questionDiv = el.parentNode.parentNode;
	var qc = questionDiv.getAttribute('correct');
	var isCorrect = (qc == 'true' || qc == 'yes');
	var divs = questionDiv.getElementsByTagName("div");

	var divResponse = divs[1]; // second div in list item
	var message = divResponse.innerHTML;
	var stringResourceCorrect = isCorrect ? 'yes' : 'no';
	var pathToImages = '/images';
	if (stringResourceCorrect == 'yes') {
		message = "<img class='question_correct_incorrect' src='"
				+ pathToImages + "/tutor5/question_correct.gif'>" + message;
	} else {
		message = "<img class='question_correct_incorrect' src='"
				+ pathToImages + "/tutor5/question_incorrect.gif'>" + message;
	}
	return overlib(message, FGCLASS, 'ol_question_style');
}

function _setupTutorMultiChoice() {
	var multiChoice = document.getElementsByTagName('div');
	for ( var d = 0, t = multiChoice.length; d < t; d++) {
		if (multiChoice[d].className == 'hm_question_def') {
			var el = multiChoice[d];
			_createQuestionStep(el);
		}
	}
}

/**
 * Register with the HM Solution event system to be notified after the Tutor has
 * been fully initialized
 * 
 */
HmEvents.eventTutorInitialized.subscribe(function(x) {
	try {
		_showTutorWidget();
		_setupTutorMultiChoice();
	} catch (e) {
		alert('error: ' + e);
	}
});

var _enableJsWidgets = true;

function _showTutorWidget() {

	var problemHead = document.getElementById('problem_statement');
	var divs = problemHead.getElementsByTagName('div');
	for ( var w = 0, t = divs.length; w < t; w++) {
		var widgetDiv = divs[w];
		var cn = widgetDiv.getAttribute('id')
		if (cn == 'hm_flash_widget') {
			/** extract embedded JSON */
			var jsonDef = $get('hm_flash_widget_def');
			if (jsonDef) {
				if (_enableJsWidgets) {
					_json = jsonDef.innerHTML;
					var jsonObj = eval('(' + _json + ')');
					HmFlashWidgetFactory.createWidget(jsonObj);
				} else {
					showFlashObject();
					widgetDiv.style.display = 'none';
				}
			} else {
				widgetDiv.innerHTML = _createGuiWrapper().innerHTML;
				var info = document.createElement("div");
				info.innerHTML = _getWidgetNotUsedHtml();
				widgetDiv.appendChild(info);
			}
		}
	}
}

function _getWidgetNotUsedHtml() {

	return "<p>"
			+ "Work out your answer on our whiteboard; your teacher will receive a copy."
			+ " Then, click the arrow buttons to see our step-by-step answer."
			+ "</p>";
}

function showWidgetSubmit(yesNo) {
	var w = $get('widget_submit');
	if (w) {
		w.disabled = yesNo ? false : true;
	}
}

/**
 * Set main widget message.
 * 
 * If null msg then clears any messages and shows the Submit button.
 * 
 * @param msg
 */
function setWidgetMessage(msg) {
	if (!msg) {
		msg = '&nbsp;';
		var img = $get('hm_flash_widget_indicator');
		if (img) {
			img.style.display = 'none';
		}
		showWidgetSubmit(true);

		if ($get('widget_input_simplified')) {
			$get('widget_input_simplified').checked = false;
		}
	}
	var h = $get('hm_flash_widget_head');
	if (h)
		h.innerHTML = msg;
}

/**
 * Copy prototype from one class to another. Allows for JS inheritance.
 */
function copyPrototype(descendant, parent) {
	var sConstructor = parent.toString();
	var aMatch = sConstructor.match(/\s*function (.*)\(/);
	if (aMatch != null) {
		descendant.prototype[aMatch[1]] = parent;
	}
	for ( var m in parent.prototype) {
		descendant.prototype[m] = parent.prototype[m];
	}
};

/**
 * Utility Methods
 * 
 */
var restrictionType_digitsOnly = /[1234567890-]/g;
var restrictionType_digitsOnlyWithSlash = /[1234567890\/]/g;
var restrictionType_digitsOnlyWithColon = /[1234567890:]/g;
var restrictionType_integerOnly = /[0-9\.]/g;
var restrictionType_alphaOnly = /[A-Z]/g;

function restrictCharacters(myfield, e, restrictionType) {
	if (!e)
		var e = window.event
	if (e.keyCode)
		code = e.keyCode;
	else if (e.which)
		code = e.which;
	var character = String.fromCharCode(code);

	// if they pressed esc... remove focus from field...
	if (code == 27) {
		this.blur();
		return false;
	}

	// ignore if they are press other keys
	if (code != 13 && !e.ctrlKey && code != 9 && code != 8 && code != 36
			&& code != 37 && code != 38
			&& (code != 39 || (code == 39 && character == "'")) && code != 40) {
		if (character.match(restrictionType)) {
			return true;
		} else {
			return false;
		}
	}
}
function isASlashCharacter(event) {
	if (!event)
		var event = window.event
	if (event.keyCode)
		code = event.keyCode;
	else if (event.which)
		code = event.which;
	if (code == 47) { // slash
		return true;
	}
	return false;
}

function isABackSpaceCharacter(event) {
	if (!event)
		var event = window.event
	if (event.keyCode)
		code = event.keyCode;
	else if (event.which)
		code = event.which;
	if (code == 8) {
		return true;
	}
	return false;
}

/**
 * Class to define a single flash widget
 * 
 * @param widgetDiv
 * @return
 */
function HmFlashWidget(jsonObj) {
	try {
		this._jsonObj = jsonObj;
		this._widgetAnswer = jsonObj.value;
		this.initializeWidget();
	} catch (e) {
		alert('Widget initialization error: ' + e);
	}
}

/**
 * Class HmFlashWidget base class - Takes a widget and assigns a key listener on
 * field widget_input_field_1-n. - Each key press will call the method
 * widgetKeyPress on the attached listener. - The form submit is assigned to the
 * proper wigetObject handler.
 * 
 * @param widgetDiv
 * 
 */
HmFlashWidget.prototype.initializeWidget = function() {
	/** put value in widget */
	var widgetObj = this;
	this._widgetGui = _createGuiWrapper();
	this._widgetForm = this.drawGui();
	this._widgetForm.onsubmit = function() {
		widgetObj.processWidget(this);
		return false;
	};
	this._widgetGui.appendChild(this._widgetForm);

	/** add the submit button to widget form */

	var submit = "<input type='submit'  id='widget_submit' class='sexybutton sexysimple sexylarge sexyred' disabled='true' style='display: block' value='Check Answer'/>";

	this._widgetForm.innerHTML = this._widgetForm.innerHTML + submit;

	var fields = this._widgetForm.getElementsByTagName("input");
	for ( var f = 0, t = fields.length; f < t; f++) {
		var inputField = fields[f];
		if (inputField.getAttribute("type") == 'text') {
			if (inputField) {
				inputField.onkeypress = function(event) {
					setWidgetMessage(null);
					return widgetObj.processKey(this, event);
				};
			}
		}
	}
	setWidgetMessage('Optional: enter an answer!');
}

HmFlashWidget.prototype.drawGui = function() {
	return this.drawGuiDefault();
}

/**
 * Define GUI for basic widget
 * 
 */
HmFlashWidget.prototype.drawGuiDefault = function() {
	var widget = this.createWidgetForm();

	var html = "<input name='widget_input_field_1' id='widget_input_field_1' type='text'/>";
	widget.innerHTML = html;

	return widget;
}

/**
 * Default processWidget functionality.
 * 
 * This will take the value from single field and matches it with jsonObj.value.
 * 
 * If it is equal, then show correct image else show error image.
 * 
 */
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}

HmFlashWidget.prototype.processWidgetDefault = function() {
	try {
		if (this.processWidgetValidation()) {
			this.markWidgetCorrect();
		} else {
			this.markWidgetIncorrect();
		}
	} catch (msg) {
		setWidgetMessage(msg);
	}
	showWidgetSubmit(false);
}

/**
 * Default process validation method (equality)
 * 
 * This method will be overridden by subclasses to provide widget specific
 * validation.
 * 
 * It must return either true, or false. Any widget specific errors should throw
 * an exception.
 */
HmFlashWidget.prototype.processWidgetValidation = function() {
	var d = $get('widget_input_field_1');
	var ans = d.value;
	return ans == this._widgetAnswer;
}

/**
 * Default functionality is to allow any key
 * 
 */
HmFlashWidget.prototype.processKey = function() {
	// allow all
}

/**
 * Add the head and control box for indicator/submit button
 * 
 */
_createGuiWrapper = function() {

	/**
	 * FIX, this must be specified ...
	 * 
	 */
	var guiWrapper = $get('hm_flash_widget');

	guiWrapper.setAttribute("style", "position: relative;");
	var html = "     <div id='hm_flash_widget_indicator' style='position: absolute;right: 5px;top: 25px;display: none;'>&nbsp;</div>"
			+ "     <div id='hm_flash_widget_head' onclick='showFlashObject();'>&nbsp;</div>";
	guiWrapper.innerHTML = html;
	return guiWrapper;
}

function showFlashObject() {
	var fo = $get('hm_flash_object');
	if (fo) {
		fo.style.display = 'block';
	} else {

	}
}

HmFlashWidget.prototype.markWidgetCorrect = function() {
	setWidgetMessage("Correct!");
	var indicator = $get('hm_flash_widget_indicator');
	indicator.innerHTML = "<img src='/tutor/widget/images/widget_correct.png'/>";
	indicator.style.display = 'block';
}

HmFlashWidget.prototype.markWidgetIncorrect = function() {
	setWidgetMessage("TRY AGAIN!");
	var indicator = $get('hm_flash_widget_indicator');
	indicator.innerHTML = "<img src='/tutor/widget/images/widget_incorrect.png'/>";
	indicator.style.display = 'block';
}

/**
 * Create base widget form object
 * 
 */
HmFlashWidget.prototype.createWidgetForm = function() {
	var widgetForm = document.createElement("form");
	widgetForm
			.setAttribute('styleName', 'width: ' + this._jsonObj.width + 'px');
	widgetForm.setAttribute("id", "hm_widget_form");
	widgetForm.setAttribute("autocomplete", "off");
	return widgetForm;
}

/**
 * Start of widget specific classes
 * 
 * Order is important!
 * 
 */
/**
 * Number integer entry test: cmextras_1_1_1_1_1
 */
function HmFlashWidgetImplNumberInteger(jsonObj) {
	this.HmFlashWidget(jsonObj);
}
HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}
HmFlashWidget.prototype.drawGui = function() {
	return this.drawGuiDefault();
}
HmFlashWidget.prototype.processWidget = function() {
	return this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplNumberInteger, HmFlashWidget);

/**
 * Class for Money entry
 * 
 * test: cmextras_1_1_1_11_1
 */
function HmFlashWidgetImplNumberMoney(jsonObj) {
	this.HmFlashWidget(jsonObj);
}
HmFlashWidget.prototype.drawGui = function() {
	return this.drawGuiDefault();
}
HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplNumberMoney, HmFlashWidget);

/**
 * Define Class for Fraction entry
 * 
 * test: cmextras_1_2_1_56_1
 * 
 */
function HmFlashWidgetImplNumberIntegerFraction(jsonObj) {
	this.HmFlashWidget(jsonObj);
	var p = jsonObj.value.split('/');
	prepareSimpleFraction(jsonObj, this);
}
HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
HmFlashWidget.prototype.drawGui = function() {
	var widget = this.createWidgetForm();

	var html = "<div class='widget-fraction' style='background: white'>"
			+ "<input name='widget_input_field_1' id='widget_input_field_1' type='text'/>"
			+ "<div class='divider'>&nbsp;</div>"
			+ "<input name='widget_input_field_2' id='widget_input_field_2' type='text'/>"
			+ "</div>";

	/**
	 * should a simplified choice been added?
	 * 
	 */
	if (this._jsonObj.format == 'text_simplified') {
		this.hasSimplified = true;

		html += "<div class='simplified_wrapper'>"
				+ "  <label>Simplified?<input id='widget_input_simplified' type='checkbox'/></label>"
				+ "</div>";
	}
	widget.innerHTML = html;

	return widget;
}
copyPrototype(HmFlashWidgetImplNumberIntegerFraction, HmFlashWidget);

/**
 * Define class for Number Simple Fraction
 * 
 * test: genericprealg_3_5_NumberTheory_29_320
 * 
 * inherit all from IntegerFraction
 */
function HmFlashWidgetImplSimpleFraction(jsonObj) {
	this.HmFlashWidget(jsonObj);
	prepareSimpleFraction(jsonObj, this);
}
copyPrototype(HmFlashWidgetImplSimpleFraction, HmFlashWidget);

/** helper method for Fraction setup */
function prepareSimpleFraction(jsonObj, obj) {
	var p = jsonObj.value.split('/');
	obj.numerator = p[0];
	obj.denominator = p[1];
	obj.hasSimplified = false;
	var num = $get('widget_input_field_1');
	var den = $get('widget_input_field_2');
	var simpEl = $get('widget_input_simplified');
	if (simpEl) {
		simpEl.onclick = function() {
			showWidgetSubmit(true);
			num.value = '';
			den.value = '';
		};
	}
}

/**
 * Define class for Integer with Angle Deg
 * 
 */

function HmFlashWidgetImplNumberIntegerAngleDeg(jsonObj) {
	this.HmFlashWidget(jsonObj);
}
HmFlashWidget.prototype.drawGui = function() {
	var widget = this.drawGuiDefault();

	var tag = document.createElement("span");
	tag.innerHTML = "&nbsp;&deg;";
	widget.appendChild(tag);

	return widget;
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplNumberIntegerAngleDeg, HmFlashWidget);

/**
 * Define class for Multi entry
 * 
 * test:
 * 
 */
function HmFlashWidgetImplMulti(jsonObj) {
	this.HmFlashWidget(jsonObj);

	var values = jsonObj.value.split('|');
	this._correct = values[values.length - 1];

	var widgetHolder = document.getElementById("widget_wrapper");

	var items = '';
	/** all but last, which has answer */
	for ( var v = 0, t = values.length - 1; v < t; v++) {
		items += '<input type="radio" name="widget_value" onclick="setWidgetMessage(null)" value="'
				+ values[v] + '"/>';
		items += '<span>' + values[v] + '</span>';
		items += '<br/>';
	}

	widgetHolder.innerHTML = items;

}
HmFlashWidget.prototype.processKey = function(event) {
	// empty
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();

}
HmFlashWidget.prototype.drawGui = function() {
	var widget = this.createWidgetForm();

	var html = "<div id='widget_wrapper'>" + "</div>";

	widget.innerHTML = html;

	return widget;
}
copyPrototype(HmFlashWidgetImplMulti, HmFlashWidget);

/**
 * Define class for Rational
 * 
 * test: cmextras_1_1_1_83_1
 * 
 */
function HmFlashWidgetImplRational(jsonObj) {
	this.isFraction = false;
	this.HmFlashWidget(jsonObj);
	var widgetObj = this;
}

HmFlashWidget.prototype.processKey = function(ele, event) {
	if (!event)
		var event = window.event
	if (event.keyCode)
		code = event.keyCode;
	else if (event.which)
		code = event.which;
	if (isASlashCharacter(event)) {
		if (!this.isFraction) {
			this.configureAsFraction(true);
			this.isFraction = true;
		}
		return false;
	} else if (isABackSpaceCharacter(event)) {
		if (this.isFraction) {
			if (ele.id == 'widget_input_field_2' && ele.value.length < 2) {
				this.configureAsFraction(false);
				this.isFraction = false;
				ele.value = '';
				return false;
			}
		}
		return true;
	} else if (code == 39) {
		return true;
	}

	return restrictCharacters(ele, event, restrictionType_integerOnly);
}

HmFlashWidget.prototype.configureAsFraction = function(yesNo) {
	if (yesNo) {
		$get('widget_input_field_2').style.display = 'inline';
		$get('widget_divider').style.display = 'block';
		$get('widget_input_field_2').focus();
	} else {
		$get('widget_input_field_2').style.display = 'none';
		$get('widget_divider').style.display = 'none';
		$get('widget_input_field_1').focus();
	}
}

/**
 * Builds UI and places measure text to right of main input field
 * 
 */
HmFlashWidget.prototype.drawGui = function() {
	var measure = '';
	var format = '';
	var prefix = '';
	if (this._jsonObj.format) {
		format = this._jsonObj.format;
		var vm = unescape(format).split('|');
		if (vm.length > 1) {
			measure = vm[1];
			prefix = vm[0];
		}
		var mp = format.split('^');
		var raised = null;
		if (mp.length > 1) {
			measure = mp[0];
			raised = mp[1];
		} else {
			measure = unescape(this._jsonObj.format);
		}

		if (measure) {
			measure = measure.split('_')[1];
		}

		if (!measure)
			measure = '';

		format = '';
		if (raised) {
			measure += "<span style='vertical-align: baseline;font-size: 0.8em;position: relative;top: -0.4em;'>"
					+ raised + "</span>";
		}
	}
	var widget = this.createWidgetForm();
	var ms = prefix == '' ? 150 : 125;

	var html = "<div style='height: 75px;'>"
			+ "<div style='float: left;margin-top: 8px;margin-right: 5px;'>"
			+ prefix
			+ "</div>"
			+

			"<div style='float: left;'>"
			+

			"<input id='widget_input_field_1' type='text' style='width: 80px;'/>"
			+ "<sup style='font-weight: bold;'>"
			+ measure
			+ "</sup>"
			+ "<div id='widget_divider' class='divider' style='display: none'>&nbsp;</div>"
			+ "<input id='widget_input_field_2' type='text' style='display: none;width: 80px;'/>"
			+ "</div>" + "</div>";

	if (this._jsonObj.symbols) {
		var symbols = this._jsonObj.symbols;
		if (symbols == 'pi') {
			symbols = '&pi;';
		} else {
			/** square root ? */
			symbols = '&radic;';
		}

		html += ""
				+ "<div class='buttons' style='padding: 5px; background: #DDD;'>"
				+ "    <button onclick='_addButtonTextToWidget(\"" + symbols
				+ "\");return false;'>" + symbols + "</button>" + "</div>";
	}
	widget.innerHTML = html;
	return widget;
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplRational, HmFlashWidget);

/**
 * Define class for Inequality
 * 
 * test: cmextras_1_2_1_43_1
 * 
 */
function HmFlashWidgetImplInequality(jsonObj) {
	this.isFraction = false;
	this.HmFlashWidget(jsonObj);

	var widgetObj = this;
}
HmFlashWidget.prototype.processKey = function(ele, event) {
	return true; // accept all
}

HmFlashWidget.prototype.configureAsFraction = function(yesNo) {
	if (yesNo) {
		$get('widget_input_field_2').style.display = 'inline';
		$get('widget_divider').style.display = 'block';
		$get('widget_input_field_2').focus();
	} else {
		$get('widget_input_field_2').style.display = 'none';
		$get('widget_divider').style.display = 'none';
		$get('widget_input_field_1').focus();
	}
}
HmFlashWidget.prototype.drawGui = function() {
	var widget = this.createWidgetForm();
	var html = "<input id='widget_input_field_1' type='text'/>"
			+ "<div class='buttons' style='margin-top: 10px;'>"
			+ "<button type='button' onclick='_addButtonTextToWidget(\"&lt;\");return false;'>&lt;</button>"
			+ "<button type='button' onclick='_addButtonTextToWidget(\"&le;\");return false;'>&le;</button>"
			+ "<button type='button' onclick='_addButtonTextToWidget(\"&gt;\");return false;'>&gt;</button>"
			+ "<button type='button' onclick='_addButtonTextToWidget(\"&ge;\");return false;'>&ge;</button>"
			+ "<button type='button' onclick='_addButtonTextToWidget(\"=\");return false;'>=</button>"
			+ "<button type='button' onclick='_addButtonTextToWidget(\"&ne;\");return false;'>&ne;</button>"
			+ "</div>";
	widget.innerHTML = html;
	return widget;
}

function _addButtonTextToWidget(ch) {
	var v = $get('widget_input_field_1').value;
	$get('widget_input_field_1').value = v + ch;
	setWidgetMessage(null);
	$get('widget_input_field_1').focus();
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplInequality, HmFlashWidget);

/**
 * Define class for Coord
 * 
 */
function HmFlashWidgetImplCoord(jsonObj) {
	this.HmFlashWidget(jsonObj);
	var inputField = $get('widget_input_field_2');
	inputField.onkeydown = function(event) {
		setWidgetMessage(null);
		return widgetObj.processKey(this, event);
	};
}
HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
	var widget = this.createWidgetForm();
	var html = "<span style='font-size: 40px;font-weight: bold'>&#40;<span>"
			+ "<input style='width: 50px;' id='widget_input_field_1' type='text'/>"
			+ ",&nbsp;"
			+ "<input style='width: 50px;' id='widget_input_field_2' type='text'/>"
			+ "<span style='font-size: 40px;font-weight: bold'>&#41;<span>";

	widget.innerHTML = html;
	return widget;
}
HmFlashWidget.prototype.processWidget = function() {
	var var1 = $get('widget_input_field_1').value;
	var var2 = $get('widget_input_field_2').value;

	var vals = this._widgetAnswer.split('|');
	if (var1 == vals[0] && var2 == vals[1]) {
		this.markWidgetCorrect();
	} else {
		this.markWidgetIncorrect();
	}
	showWidgetSubmit(false);
}
copyPrototype(HmFlashWidgetImplCoord, HmFlashWidget);

/**
 * Define class for number_mixed_fraction: cmextras_1_4_1_1_4
 * 
 */
function HmFlashWidgetImplMixedFraction(jsonObj) {
	this.HmFlashWidget(jsonObj);
	var inputField = $get('widget_input_field_2');
}
HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
	var widget = this.createWidgetForm();
	var html = "<div style='height: 50px;'>"
			+ "<div style='float: left;margin-top: 8px;margin-right: 5px;'>"
			+ "    <input id='widget_input_field_1' type='text' style='width: 30px'/>"
			+ "</div>"
			+ "<div style='float: left;width: 80px;'>"
			+ "<input id='widget_input_field_2' type='text' style='width: 80px;display: block;'/>"
			+ "<div class='divider'>&nbsp;</div>"
			+ "<input id='widget_input_field_2' type='text' style='width: 80px;display: block;'/>"
			+ "</div>" + "</div>";

	widget.innerHTML = html;
	return widget;
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplMixedFraction, HmFlashWidget);

/**
 * Define class for Power Form
 * 
 * test: genericprealg_1_6_Operations,ExpressionsandVariables_15_125
 * 
 */
function HmFlashWidgetImplPowerForm(jsonObj) {
	this.HmFlashWidget(jsonObj);
}

HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
	var power = '';
	var format = '';
	var prefix = '';

	var v = this._jsonObj.value.split('^');
	var base = v[0];
	var power = v[1];
	var widget = this.createWidgetForm();
	var html = "<div style='position: relative;height: 40px;'>"
			+ "   <input id='widget_input_field_1' type='text' style='position: absolute; left: 0;width: 87px;'/>"
			+ "   <input id='widget_input_field_2' type='text' style='position: absolute;  top: -10px;left:90px;font-size: .8em;width: 40px;'/>"
			+ "</div>";
	widget.innerHTML = html;

	return widget;
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplPowerForm, HmFlashWidget);

/**
 * type = 'science notation' test = genericprealg_3_8_NumberTheory_1_335
 */
function HmFlashWidgetImplSciNotation(jsonObj) {
	this.HmFlashWidget(jsonObj);

	var val = unescape(jsonObj.value);
	var p1 = val.split('x');
}

HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
	var widget = this.createWidgetForm();
	widget.className = 'science-notation';
	var html = "<div style='position: relative;height: 40px;'>"
			+ "   <input id='widget_input_field_1' type='text' style='position: absolute; left: 0;width: 63px;'/>"
			+ "   <input id='widget_input_field_2' type='text' style='position: absolute;  top: -10px;left:110px;font-size: .5em;width: 40px;'/>"
			+ "   <span style='position: absolute;left: 70px;font-weight: bold;'>x 10</span>"
			+ "</div>";
	widget.innerHTML = html;
	return widget;
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplSciNotation, HmFlashWidget);

/**
 * type = letter test =
 * 'genericprealg_10_1_LinearEquationsandInequalities_5_900'
 */
function HmFlashWidgetImplLetter(jsonObj) {
	this.HmFlashWidget(jsonObj);

	var val = unescape(jsonObj.value);
	var p1 = val.split('x');
}

HmFlashWidget.prototype.processKey = function(ele, event) {
	return true;
}

HmFlashWidget.prototype.drawGui = function() {
	return this.drawGuiDefault();
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplLetter, HmFlashWidget);

/**
 * Type = Odds test = genericalg1_13_1_DiscreteMathematicsandProbability_13_1200
 */
function HmFlashWidgetImplOdds(jsonObj) {
	this.HmFlashWidget(jsonObj);
}

HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnlyWithColon);
}

HmFlashWidget.prototype.drawGui = function() {
	return this.drawGuiDefault();
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplOdds, HmFlashWidget);

/**
 * Type = point_slope_form test = genericalg1_2_7_GraphingLinearEquations_1_130
 */
function HmFlashWidgetImplPointSlopeForm(jsonObj) {
	this.HmFlashWidget(jsonObj);
}

HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
	var widget = this.createWidgetForm();
	widget.className = 'point-slope-form';
	var html = "<span>y-&nbsp;</span>"
			+ "<input id='widget_input_field_1' type='text' style='width: 30px;display: inline'/>"
			+ "<span>&nbsp;=&nbsp;</span>"
			+ "<input id='widget_input_field_2' type='text' style='width: 30px;display: inline'/>"
			+ "<span>&nbsp;<span style='font-size: 50px;'>(</span>x-&nbsp;</span>"
			+ "<input id='widget_input_field_3' type='text' style='width: 30px;display: inline;'/>"
			+ "<span><span style='font-size: 50px;'>)</span>";

	widget.innerHTML = html;
	return widget;
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplPointSlopeForm, HmFlashWidget);

function HmFlashWidgetImplInequalityExact(jsonObj) {
	this.HmFlashWidget(jsonObj);
}
HmFlashWidget.prototype.processKey = function(ele, event) {
	return restrictCharacters(ele, event, restrictionType_digitsOnly);
}
HmFlashWidget.prototype.drawGui = function() {
	return this.drawGuiDefault();
}
HmFlashWidget.prototype.processWidget = function() {
	this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplInequalityExact, HmFlashWidget);

/**
 * tutor_flash_widget_validation.js
 * 
 * 
 * 
 * 
 */

/**
 * tutor flash widget validations
 * 
 * validation functions are assigned to specific widgets defiend in
 * tutor_flash_widget.js.
 * 
 * Each validation method is made an instance method of the assigned class. This
 * makes the instance vars (like _jsonObj) to be available.
 * 
 */

var widget_answer;
var widget_format;
var MESSAGE_CORRECT = 'Correct!';
var MESSAGE_INCORRECT = 'Try Again!';
var ERROR_UNITS = 'Enter correct units!'
var ERROR_SIMPLE_FRACTION = "Not in lowest terms!";
var ERROR_SCI_NOT = "The first number has to be between 1 and 10";
var ERROR_ODDS = "The problem asks for the odds not probability"
var PI_SYM = '\u03c0';
var SQUARE_ROOT_SYM = '\u221A';

function regExpMatch(reg, str) {
	var re = new RegExp(reg);
	if (str.match(re)) {
		return true;
	} else {
		return false;
	}
}
function regExpReplace(reg, str, repstr) {
	var re = new RegExp(reg, "g");
	return str.replace(re, repstr);
}
var revSymb = {};
revSymb['<'] = '>';
revSymb['>'] = '<';
revSymb['='] = '=';
revSymb['!='] = '!=';
revSymb['<='] = '>=';
revSymb['>='] = '<=';

// unicode:
// less-than-or-equals-to = \u2264
// greater-than-or-equals-to = \u2265
// not-equals-to = \u2260

function splitAtInEq(str) {
	var syArr = [ "<=", "<", "&lt;", "\u2264", "&le;", ">=", ">", "&gt;",
			"\u2265", "&ge;", "=", "&eq;", "\u2260", "&ne;" ];
	var ieq = null;
	for ( var i = 0; i < syArr.length; i++) {
		if (str.indexOf(syArr[i]) != -1) {
			ieq = syArr[i];
			break;
		}
	}
	var exp = str.split(ieq);
	if (exp.length > 2) {
		var texp0 = exp.pop();
		var texp1 = exp.join("");
		exp = [ texp0, texp1 ];
	}
	var _ieq = ieq;
	ieq = ieq == '&lt;' ? '<' : ieq == "\u2264" ? '<=' : ieq == '&le;' ? '<='
			: ieq;
	ieq = ieq == '&gt;' ? '>' : ieq == "\u2265" ? '>=' : ieq == '&ge;' ? '>='
			: ieq;
	ieq = ieq == "&eq;" ? '=' : ieq;
	ieq = ieq == "&ne;" || ieq == "\u2260" ? '!=' : ieq;
	return {
		ueq : _ieq,
		syeq : ieq,
		lexp : exp[0],
		rexp : exp[1]
	};
}
function applyUniqueVarX(str) {
	if (!str) {
		return str;
	}

	var inputValue = str
	if (regExpMatch('\\*[a-z()]|[a-z()]\\*', inputValue)) {
		inputValue = (regExpReplace('[a-z]', inputValue, 'x'))
	} else if (regExpMatch('[0-9]\\([a-z]\\)', inputValue)) {
		inputValue = (regExpReplace('\\([', inputValue, '*('))
	} else if (regExpMatch('[a-z]\\([0-9]', inputValue)) {
		inputValue = (regExpReplace('\\(', inputValue, '*('))
	} else if (regExpMatch('[0-9][a-z]', inputValue)) {
		inputValue = (regExpReplace('[a-z]', inputValue, '*x'))
	}
	return inputValue
}
function applyUniqueVarXForEval(str) {
	if (!str)
		return str;

	var inputValue = str
	if (regExpMatch('\\*[a-z()]|[a-z()]\\*', inputValue)) {
		inputValue = (regExpReplace('[a-z]', inputValue, 'x'))
	} else if (regExpMatch('[0-9]\\([a-z]\\)', inputValue)) {
		inputValue = (regExpReplace('\\([', inputValue, '*('))
	} else if (regExpMatch('[a-z]\\([0-9]', inputValue)) {
		inputValue = (regExpReplace('\\(', inputValue, '*('))
	} else if (regExpMatch('[0-9][a-z]', inputValue)) {
		inputValue = (regExpReplace('[a-z]', inputValue, '*x'))
	}
	return inputValue
}
function cm_unescape(str) {
	return unescape(decodeURI(str));
}
// unicode used:
// divide = \u00f7
// subscript_minus = \u208B (verify correct?)---->\u2013
// plus_minus = \u00B1
// asterisk = \u002A (verify correct?)----->correct
function toMathFormat(_str) {
	var str = _str;
	var opStr = '/\u00f7*+-\u2013<=>][^\u00B1()\u002A';
	var hasPiSymb = str.indexOf(PI_SYM) > -1;
	if (hasPiSymb) {
		// str = str.split("\u03a0").join("pi");
		if (regExpMatch('\\*\\(?\u03c0|\u03c0\\)?\\*', str)) {
			str = regExpReplace('\u03a0', str, 'Math.PI')
		} else {
			if (regExpMatch('[0-9)]\u03c0', str)) {
				str = regExpReplace('\u03c0', str, '*Math.PI')
			}
			if (regExpMatch('\u03c0[(0-9]', str)) {
				str = regExpReplace('\u03c0', str, 'Math.PI*')
			}
		}
	}
	var hasSqrtSymb = str.indexOf(SQUARE_ROOT_SYM);
	var strL = str.length;
	var lstr, rstr, uchar, fstr;
	fstr = "";
	var closeB = ")"
	if (hasSqrtSymb > -1) {
		lstr = str.substring(0, hasSqrtSymb);
		rstr = str.substring(hasSqrtSymb + 1);
		if (rstr.length < 1) {
			return lstr;
		}
		if (lstr.length < 1) {
			fstr = "Math.sqrt("
		} else {
			if (lstr.charAt(lstr.length - 1) == '*') {
				if (rstr.charAt(0) == "(") {
					fstr = lstr + "Math.sqrt"
					closeB = ""
				} else {
					fstr = lstr + "Math.sqrt("
				}
			} else {
				if (rstr.charAt(0) == "(") {
					fstr = lstr + "*Math.sqrt"
					closeB = ""
				} else {
					fstr = lstr + "*Math.sqrt("
				}
			}
		}
		// alert('fstr '+fstr+":"+closeB)
		var end = false;
		for ( var i = 0; i < rstr.length; i++) {
			uchar = rstr.charAt(i);
			if (opStr.indexOf(uchar) != -1 && i != 0) {
				fstr += closeB + rstr.substring(i);
				end = true;
				break;
			} else {
				fstr += uchar;
			}
		}
		if (!end) {
			fstr += closeB;
		}
		// alert('fstr '+fstr)
		return fstr;
	}
	return str;
}
// utility functions ends

/**
 * Each validation function has access to this._jsonObj which is the JSON
 * configuration object (ie, this._jsonObj.value)
 * 
 */
// VALIDATION FUNCTIONS START
// /////////////////////////////////////////////////////////////////////
// -! type=number_integer
function validateNumberInteger() {
	var expectedValue = cm_unescape(this._jsonObj.value);
	var format = this._jsonObj.format;

	var inputValue = $get('widget_input_field_1').value;
	if (inputValue == expectedValue) {
		return true;
	} else {
		if (format == "money") {
			if (Number(inputValue) == Number(expectedValue.substring(1))) {
				throw (ERROR_UNITS);
			} else {
				return false;
			}
		} else {
			return false
		}
	}
}
HmFlashWidgetImplNumberInteger.prototype.processWidgetValidation = validateNumberInteger;

// -! type=number_decimal format=money
function validateNumberMoney() {
	var inputValue = $get('widget_input_field_1').value;
	var expectedValue = cm_unescape(this._jsonObj.value);

	var numVal = 0;
	var hasUnits = false;
	if (inputValue.substring(0, 1) == '$') {
		numVal = Number(inputValue.substring(1));
		hasUnits = true;
	} else
		numVal = Number(inputValue);

	/** strip off leading $ */
	var numExpect = Number(expectedValue.substring(1));
	if (numVal == numExpect) {
		if (!hasUnits)
			throw (ERROR_UNITS);
		return true;
	} else {
		return false;
	}
}
HmFlashWidgetImplNumberMoney.prototype.processWidgetValidation = validateNumberMoney;

// -! type=number_decimal
function validateNumberDecimal() {
	var inputValue = $get('widget_input_field_1').value;
	var expectedValue = cm_unescape(this._jsonObj.value);
	if (inputValue == expectedValue) {
		return true;
	} else {
		if (widget_format == "money") {
			if (Number(inputValue) == Number(expectedValue.substring(1))) {
				throw Exception(ERROR_UNITS);
			} else {
				return false;
			}
		} else {
			return false
		}
	}
}

// -! type=number_simple_fraction
function validateNumberSimpleFraction() {
	var expectedValue = cm_unescape(this._jsonObj.value);
	var simplified = $get('widget_input_simplified');

	/**
	 * isSimplified field avaialbe and checked?
	 * 
	 */
	var isSimplified = simplified && simplified.checked;
	var ans_isSimplified = expectedValue.split("|")[1] == 'simplified'
	if (expectedValue.indexOf("[") > -1) {
		var splitVal = expectedValue.split("]")
		var ewhole = splitVal[0].split("[")[1];
		splitVal = splitVal[1].split("/")
		var enumero = splitVal[0]
		var eden = splitVal[1]
		expectedValue = ((ewhole * eden) + Number(enumero)) + "/" + eden;
	}
	var eAns = eval(expectedValue);
	var inputValue = $get('widget_input_field_1').value;
	var isFrac = $get('widget_input_field_2').value != "";
	var fld3 = $get('widget_input_field_3');
	var isMixed = (fld3 != null && fld3.value != "");
	// alert($get('widget_input_field_2').value)
	isFrac = isMixed ? false : isFrac
	var num, den, whole
	if (isFrac) {
		num = inputValue
		den = $get('widget_input_field_2').value
		inputValue = num + "/" + den
	}
	if (isMixed) {
		num = inputValue;
		den = $get('widget_input_field_2').value;
		whole = fld3.value;
		inputValue = ((whole * den) + Number(num)) + "/" + den
	}

	if (inputValue == expectedValue) {
		return true;
	} else {
		if (eAns == inputValue) {
			return true
		}
		if (isSimplified && ans_isSimplified) {
			return true;
		}
		if (eval(inputValue) == eval(expectedValue)) {
			throw (ERROR_SIMPLE_FRACTION);
		} else {
			return false
		}
	}
}
HmFlashWidgetImplSimpleFraction.prototype.processWidgetValidation = validateNumberSimpleFraction;

// -! type=number_fraction
/**
 * TODO: need widget for this
 * 
 * widget_input_simplified is avaiable on text_simple.
 * 
 */
function validateNumberFraction() {
	var simplified = $get('widget_input_simplified');

	/**
	 * isSimplified field avaialbe and checked?
	 * 
	 */
	var isSimplified = simplified && simplified.checked;

	var expectedValue = cm_unescape(this._jsonObj.value);

	if (expectedValue.indexOf("[") > -1) {
		var splitVal = expectedValue.split("]")
		var ewhole = splitVal[0].split("[")[1];
		splitVal = splitVal[1].split("/")
		var enumero = splitVal[0]
		var eden = splitVal[1]
		expectedValue = ((ewhole * eden) + Number(enumero)) + "/" + eden;
	}
	var inputValue = $get('widget_input_field_1').value;
	var isFrac = $get('widget_input_field_2').value != "";
	var fld3 = $get('widget_input_field_3');
	var isMixed = false;
	if (fld3)
		isMixed = fld3.value != "";

	isFrac = isMixed ? false : isFrac
	var num, den, whole
	if (isFrac) {
		num = inputValue
		den = $get('widget_input_field_2').value
		inputValue = num + "/" + den
	}
	if (isMixed) {
		num = inputValue;
		den = $get('widget_input_field_2').value;
		whole = fld3.value;
		inputValue = ((whole * den) + Number(num)) + "/" + den
	}

	if (inputValue == expectedValue) {
		return true;
	} else {
		if (eval(inputValue) == eval(expectedValue)) {
			return true;
		} else {
			return false;
		}
	}
}
HmFlashWidgetImplNumberIntegerFraction.prototype.processWidgetValidation = validateNumberFraction;

// -! type=number_rational
function validateNumberRational() {
	var expectedValue = cm_unescape(this._jsonObj.value);
	if (expectedValue.indexOf("[") > -1) {
		var splitVal = expectedValue.split("]")
		var ewhole = splitVal[0].split("[")[1];
		splitVal = splitVal[1].split("/")
		var enumero = splitVal[0]
		var eden = splitVal[1]
		expectedValue = ((ewhole * eden) + Number(enumero)) + "/" + eden;
	}
	var inputValue = $get('widget_input_field_1').value;
	var isFrac = $get('widget_input_field_2').value != "";
	var fld3 = $get('widget_input_field_3');
	var isMixed = fld3 != null && fld3.value != "";
	// alert($get('widget_input_field_2').value)
	isFrac = isMixed ? false : isFrac
	var num, den, whole
	if (isFrac) {
		num = inputValue
		den = $get('widget_input_field_2').value
		inputValue = num + "/" + den;

	}
	if (isMixed) {
		num = inputValue;
		den = $get('widget_input_field_2').value;
		whole = fld3.value;
		inputValue = ((whole * den) + Number(num)) + "/" + den
	}
	// alert(inputValue+":"+num+":"+den)\
	//
	// Regular expressions do not support unicode chars
	// unicode:
	// pi = \u03a0
	// sroot = \u221A

	var isExpression_input = regExpMatch('[a-z\u221A\u03c0]', inputValue)
	var isExpression_ans = regExpMatch('[a-z\u221A\u03c0]', expectedValue)

	try {
		if (isExpression_input) {
			var x = 2
			inputValue = applyUniqueVarX(inputValue)
			inputValue = toMathFormat(inputValue)
			inputValue = eval(inputValue);
		} else {
			inputValue = eval(inputValue)
		}
		if (isExpression_ans) {
			var x = 2
			expectedValue = expectedValue.replace("pi", PI_SYM)
			expectedValue = expectedValue.replace("sqrt", SQUARE_ROOT_SYM)
			expectedValue = applyUniqueVarX(expectedValue)
			expectedValue = toMathFormat(expectedValue)
			// alert(expectedValue)
			expectedValue = eval(expectedValue);
		} else {
			expectedValue = eval(expectedValue)
		}
		// alert(expectedValue+":"+inputValue)
		if (inputValue == expectedValue) {
			return true;
		} else {
			return false;
		}
	} catch (ex) {
		// report error?
		// alert(ex);
		return false;
	}
}
HmFlashWidgetImplRational.prototype.processWidgetValidation = validateNumberRational;

// -! type=number_mixed_fraction
function validateNumberMixedFraction() {
	var expectedValue = cm_unescape(this._jsonObj.value);

	if (expectedValue.indexOf("[") > -1) {
		var splitVal = expectedValue.split("]")
		var ewhole = splitVal[0].split("[")[1];
		splitVal = splitVal[1].split("/")
		var enumero = splitVal[0]
		var eden = splitVal[1]
		expectedValue = ((ewhole * eden) + Number(enumero)) + "/" + eden;
	}
	var inputValue = $get('widget_input_field_1').value;
	var isFrac = $get('widget_input_field_2') != undefined;
	var isMixed = $get('widget_input_field_3') != undefined;
	var num, den, whole
	if (isFrac) {
		num = inputValue
		den = $get('widget_input_field_2').value
		inputValue = num + "/" + den
	}
	if (isMixed) {
		/**
		 * todo: If a mixed fraction, then inputValue here would always be a
		 * string, ie. 1/5 ... because it is also a isFrac.
		 */
		whole = $get('widget_input_field_1').value;
		num = $get('widget_input_field_2').value;
		den = $get('widget_input_field_3').value;
		inputValue = ((Number(whole) * Number(den)) + Number(num)) + "/" + den
	}

	if (inputValue == expectedValue) {
		return true;
	} else {
		return false;
	}
}
HmFlashWidgetImplMixedFraction.prototype.processWidgetValidation = validateNumberMixedFraction;

// -! type=mChoice
function validateMChoice() {
	var expectedValue = cm_unescape(this._jsonObj.value).split("|");
	expectedValue = eval(expectedValue[expectedValue.length - 1]) - 1;
	var inputControls = $get('hm_widget_form');
	var inputValue = null;
	for ( var i = 0, l = inputControls.length; i < l; i++) {
		if (inputControls[i].checked) {
			inputValue = i;
			break;
		}
	}
	if (inputValue == expectedValue) {
		return true;
	} else {
		return false;
	}
}
HmFlashWidgetImplMulti.prototype.processWidgetValidation = validateMChoice;

// -! type=coordinates
function validateCoordinates() {
	widget_answer = cm_unescape(this._jsonObj.value);
	var expectedValue = widget_answer.indexOf(",") > -1 ? widget_answer
			.split(",") : widget_answer.split("|");
	var inputValue = [ $get('widget_input_field_1').value,
			$get('widget_input_field_2').value ]
	if (inputValue[0] == expectedValue[0] && inputValue[1] == expectedValue[1]) {
		return true;
	} else {
		return false;
	}
}
// -! type=string&letter
// ignore case
function validateString() {
	var expectedValue = cm_unescape(this._jsonObj.value);
	var inputValue = $get('widget_input_field_1').value
	if (inputValue.toLowerCase() == expectedValue.toLowerCase()) {
		return true;
	} else {
		return false;
	}
}
HmFlashWidgetImplLetter.prototype.processWidgetValidation = validateString;

// -! type=inequality
function validateInequality() {
	var expectedValue = cm_unescape(this._jsonObj.value);
	var inputValue = $get('widget_input_field_1').value
	var inIEq = splitAtInEq(inputValue);
	var outIEq = splitAtInEq(expectedValue);
	var olex = applyUniqueVarX(outIEq.lexp);
	var ilex = applyUniqueVarX(inIEq.lexp);
	var orex = applyUniqueVarX(outIEq.rexp);
	var irex = applyUniqueVarX(inIEq.rexp);
	if (outIEq.syeq == inIEq.syeq) {
		if (olex == ilex) {
			if (orex == irex) {
				return true;
			}
		} else {
			//
			var x = 2;
			var ole = eval(olex);
			var ile = eval(ilex);
			var ore = eval(orex);
			var ire = eval(irex);
			if (ole == ile) {
				if (ore == ire) {
					return true;
				}
			}
		}
	} else if (revSymb[outIEq.syeq] == inIEq.syeq) {
		if (orex == ilex) {
			if (olex == irex) {
				return true;
			}
		} else {
			var x = 2;
			var ole = eval(olex);
			var ile = eval(ilex);
			var ore = eval(orex);
			var ire = eval(irex);
			if (ore == ile) {
				if (ole == ire) {
					return true;
				}
			}
		}
	}
	return false;
}
HmFlashWidgetImplInequality.prototype.processWidgetValidation = validateInequality;

// -! type=inequality_exact
function validateInequalityExact() {
	var expectedValue = cm_unescape(this._jsonObj.value);
	var inputValue = $get('widget_input_field_1').value
	inputValue = inptValue.replace(/[*()]/g, "");
	if (inputValue == expectedValue) {
		return true;
	} else {
		return false;
	}
}
HmFlashWidgetImplInequalityExact.prototype.processWidgetValidation = validateInequalityExact;

// -! type=power
function validatePower() {
	var expectedValue = cm_unescape(this._jsonObj.value).split("^");
	var inputValue = [ $get('widget_input_field_1').value,
			$get('widget_input_field_2').value ];
	var x = 2;
	var obex = applyUniqueVarX(expectedValue[0]);
	var ibex = applyUniqueVarX(inputValue[0]);
	var oeex = applyUniqueVarX(expectedValue[1]);
	var ieex = applyUniqueVarX(inputValue[1]);
	var obe = eval(obex);
	var ibe = eval(ibex);
	var oee = eval(oeex);
	var iee = eval(ieex);
	if (obe == ibe && oee == iee) {
		return true;
	} else {
		return false;
	}
}
HmFlashWidgetImplPowerForm.prototype.processWidgetValidation = validatePower;

// -! type=scientific_notation
function validateScientificNotation() {
	var fld1 = $get('widget_input_field_1');
	var fld2 = $get('widget_input_field_2');
	widget_answer = cm_unescape(this._jsonObj.value);

	var expectedValue = widget_answer.indexOf("x10^") ? widget_answer
			.split("x10^") : widget_answer.split("|");
	var inputValue = [ $get('widget_input_field_1').value,
			$get('widget_input_field_2').value ];
	if (inputValue[0] < 1 || inputValue[0] > 9) {
		throw (ERROR_SCI_NOT);
	} else {
		if (inputValue[0] == expectedValue[0]
				&& inputValue[1] == expectedValue[1]) {
			return true;
		} else {
			return false;
		}
	}
}
HmFlashWidgetImplSciNotation.prototype.processWidgetValidation = validateScientificNotation;

// -! type=point_slope
function validtePointSlope() {
	var expectedValue = cm_unescape(this._jsonObj.value);
	var yval = $get('widget_input_field_1').value
	var slope = $get('widget_input_field_2').value
	var xval = $get('widget_input_field_3').value
	var inputValue = yval + "|" + slope + "|" + xval;
	if (inputValue == expectedValue) {
		return true;
	} else {
		return false;
	}
}
HmFlashWidgetImplPointSlopeForm.prototype.processWidgetValidation = validtePointSlope;

// -! type=odds
function validateOdds() {
	var expectedValue = cm_unescape(this._jsonObj.value);
	var inputValue = $get('widget_input_field_1').value;
	var ans = {};
	var inp = {};
	if (expectedValue.indexOf("/") != -1) {
		ans = expectedValue.split("/");
	} else if (expectedValue.indexOf(":") != -1) {
		ans = expectedValue.split(":");
	} else if (expectedValue.indexOf("to") != -1) {
		ans = expectedValue.split("to");
	}
	var prob = eval(ans[0]) + eval(ans[1]);
	if (inputValue.indexOf("/") != -1) {
		inp = inputValue.split("/");
	} else if (inputValue.indexOf(":") != -1) {
		inp = inputValue.split(":");
	} else if (inputValue.indexOf("to") != -1) {
		inp = inputValue.split("to");
	}
	if (inputValue == expectedValue) {
		return true;
	} else {
		if (eval(ans[0]) == eval(inp[0]) && eval(ans[1]) == eval(inp[1])) {
			return true;
		} else if (eval(ans[0]) == eval(inp[0]) && (eval(inp[1]) == prob)) {
			throw Exception(ERROR_ODDS)
		} else {
			return false;
		}
	}
}
HmFlashWidgetImplOdds.prototype.processWidgetValidation = validateOdds;
