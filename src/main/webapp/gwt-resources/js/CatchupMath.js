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
				cb.style.background = 'red';
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
function doLoad_Gwt(pid) {
	var mc = createNewSolutionMessageContext(pid);
	gotoGUID(mc);
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






/** Overridden from tutor5.js
 * 
 * @param num
 * @return
 */
function scrollToStep(num) {
	
	// just scroll to bottom
	var objDiv = document.getElementById("tutor_embedded").parentNode.parentNode;
	objDiv.scrollTop = objDiv.scrollHeight;

	// alert('Scroll height: ' + objDiv.scrollTop);

} 





//register a listener with Tutor to 
//be notified when last step is reached.
//This is used to only advance when the solution 
//has actually been viewed.
HmEvents.eventTutorLastStep.subscribe( function(x) {
	solutionHasBeenViewed_Gwt(x);
});

/** Override the Ask a tutor function */
TutorManager.askATutor = function() {
	// do nothing
};