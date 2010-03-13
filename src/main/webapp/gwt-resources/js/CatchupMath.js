// External JS for GWT
//
// called from dynamically loaded question HTML
function questionGuessChanged(o) {
	try {

		var pid = findQuestionGuid(o);

		var questionIndex = -1;
		if (o.id == 'optionSkipped') {
			questionIndex = '-2'; // skipped
		} else {
			// is it correct or wrong?
			var nd = o.parentNode.getElementsByTagName("div").item(0).innerHTML;

			// how to know which index ..
			// .. go to parent, and look for child that matches this
			var parentUl = o.parentNode.parentNode;
			var ndItems = parentUl.getElementsByTagName("input");
			for ( var i = 0; i < ndItems.length; i++) {
				if (ndItems.item(i) == o) {
					questionIndex = i;
					break;
				}
			}
		}
		// call GWT JSNI function defined in QuizPage
		questionGuessChanged_Gwt(nd, "" + questionIndex, pid);
	} catch (e) {
		alert('Error answering question in external JS: ' + e);
	}
}


/** Return count of correct questions */
function getQuizResults_Correct() {

}

/** Return total count of questions defined
 * 
 * @return count of questions
 */
function getTotalQuizQuestions() {
	return 0;
}

/** Search parents looking for closed guid attribute

 return null if not found
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

var _questionObjectPointer;

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
				//cb.style.background = 'red';
				cb.checked = true;
			}
		}
	}
}

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

////////////////////
// / For the tutor viewer
// ///////////////////
// called by GWT, which setups the context for the pid
var _shouldExpandSteps;
function doLoad_Gwt(pid, title, hasShowWork,shouldExpandSteps) {
	// store in var, registered listener will be notified
	// after solution has been fully initialized
	_shouldExpandSteps = shouldExpandSteps;
	var mc = createNewSolutionMessageContext(pid);
	gotoGUID(mc, title);
    if(hasShowWork) {
    	// turn off/hide the ShowWorkFirst button
    	var swf = document.getElementById("show-work-force");
    	if(swf) {
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
////////////////////////
// / End for Tutor /////
// //////////////////////

/** Mark all questions as correct */
window.markAllCorrectAnswers = function() {
	showCorrectAnswers(markCorrectResponse);
}

/** Return the count of correct answers
 * 
 */
window.getQuizResultsCorrect = function() {
	var count = 0;
	showCorrectAnswers( function(ql) {
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

/** Return total count of questions
 * 
 */
window.getQuizQuestionCount = function() {
	var count = 0;
	showCorrectAnswers( function(ql) {
		count++;
	});
	return count;
}

/** Called from GWT to set the quiz question with the appropriate image
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

/** Find list of questions and for each
 *  one call func to and pass the question div
 *  to supplied func.
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

/** Given a list of question guesses, mark the correct
 *  one.  
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



 function showAskATutorTooltip_Cm() {
	 var msg = "<div style='height:100px;padding: 5px;'><p style='margin-bottom: 5px;'>When enabled, this button connects you to an online tutor. Communication is by using an electronic whiteboard and text-chat.</p></div>";
	 overlib(msg,FGCLASS, "ol_default_style");
 }

 


/** Overridden from tutor5.js
 * 
 * Makes sure solution scrolls to bottom of step
 * 
 * @param num
 * @return
 */
function scrollToStep(num) {

	// just scroll to bottom, must track the panel that is doing the scrolling
	// @TODO: how to get a better handle on which one?  This can easily break
        // if the DOM is changed
	// Perhaps, this needs to call GWT JNSI and have it do the scrolling, or at
	// least provide the source of the scrolling panel ... ?
 	var objDiv = document.getElementById("tutor_embedded").parentNode.parentNode;
	objDiv.scrollTop = objDiv.scrollHeight;
} 



  /** 
   * register a listener with Tutor to 
   * be notified when last step is reached.
   * This is used to only advance when the solution 
   * has actually been viewed.
   * 
   * 
   * solutionHasBeenViewed_Gwt is defined GWT source: PrescriptionCmGuiDefinition
   * 
   */
HmEvents.eventTutorLastStep.subscribe( function(x) {
	solutionHasBeenViewed_Gwt(x);
});


/** Registered listener to be notified after solution is loaded fully
 *  If _shouldExpandSteps is true, the move to last step.
 *  
 */
HmEvents.eventTutorInitialized.subscribe( function(x) {
	if(_shouldExpandSteps) {
	    expandAllSteps();
	}
});

/** Register a listener with Tutor to 
 *  be notified when first step is accessed
 */
//HmEvents.eventTutorFirstStep.subscribe( function(x) {
//	solutionIsBeingViewed_Gwt(x);
//});

/** Override the Ask a tutor function */
TutorManager.askATutor = function() {
	// do nothing
};


/** Call into GWT to display the requested resource 
 * 
 * @param type
 * @param file
 * @return
 */
function doLoadResource(type, file) {
    doLoadResource_Gwt(type, file);
    return false;
}


function log() {
	//
}






/** Show Work */


//setup hooks back to the GWT engine
//these are needed due to how Flash is 
//handled when loaded dynamically. The 
//main problem is the ExternalInterface
//from the flash component only works when
//during page load.  It breaks
//when loaded dynamically.


//called by GWT in ShowWorkPanel when reading
//data from server and requesting whiteboard
//to update itself.  
//The id is the unique flash id, the command+data
//is a single request for the whiteboard to do
//something. Such as 'draw', 'clear', or 'load'
//commandData, if used, is JSON.
function updateWhiteboard(id, command, commandData) {

//  only works if single object on page
// @TODO: get browser depend object
var fo = id;  // swfobject.getObjectById("whiteboard-object");

//alert(fo + ', ' + fo.name + ', ' + fo.updateWhiteboard);
if(!fo) {
    alert('Could not find whiteboard flash object');
}
if(fo.updateWhiteboard) {
    // send an array of commands.  Each element in array
    // is a command and an array of data.  For example, one
    // 'draw', but a bunch of draw requests.
	   if(command == 'draw') {
        fo.updateWhiteboard([['draw',[commandData]]]);
	   }
	   else if(command == 'clear') {
		   fo.updateWhiteboard([['clear',[]]]);
	   }
}
else {
   alert('could not find updateWhiteboard: ' + id);
}
}

//register callback in the parent GWT to allow
//GWT to call back.
function setWhiteboardBackground(html) {
 //var wbg = document.getElementById('whiteboard-bg');
 //wbg.innerHTML = html;        
}



/** provide replacement for missing login_info.js */
var LoginInfo = {
		isValid:function() {return true;}
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
  	    // alert("flash result: " + resO.result + ', input: ' + resO.input, ', answer: ' + resO.answer + ', id: ' + resO.id);
	    flashInputField_Gwt(resO.result, resO.input, resO.answer, resO.id);
	}
	catch(e) {
		alert('There was a problem processing Flash Input Field: ' + e);
	}
}


/** overridden from tutor6
 * 
 * @return
 */
function isSolutionIsAvailable() {
	return true;
}


/** overridden from tutor6
 * 
 * @return
 */
function showNeedToSignup() {
}


function setQuizQuestionActive(pid) {
	setQuizQuestionDisplayAsActive(pid);
}

var _whiteboardActive=false;
/** mark the question with guid of pid as being active 
 * 
 * @param pid  guid of row to select (null selects first)
 * @return
 */
function setQuizQuestionDisplayAsActive(pid) {
	
	if(true)
		return;
	
	
	var testset = document.getElementById("testset_div");
	if(testset == null)
		return;
	
	var questions = testset.getElementsByTagName("div");
	var activeGuid=null;
	for ( var i = 0, t = questions.length, c = 0; i < t; i++) {
		var d = questions[i];
		if(d.className == 'question_div') {
			if(_whiteboardActive==true && (d.getAttribute('guid') == pid || (pid == null && c == 0))) {
				// d.style.background = '#EAEAEA';
				d.style.background = '#EAEAEA url(/gwt-resources/whiteboard_pointer.png) no-repeat top right';
				activeGuid=d.getAttribute('guid');
			}
			else {
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
