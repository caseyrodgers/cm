/** core Catchup Math routines
 * 
 */

/** Find list of questions and for each
 *  one call func to and pass the question div
 *  to supplied func.
 *
 */
window.showCorrectAnswers = function(func) {
    var td = document.getElementById('testset_div');
    if(td) {
    	var testSet = document.getElementById('testset_div').getElementsByTagName('div');
    	for ( var q = 0; q < testSet.length; q++) {
    		if (testSet[q].className == 'question_wrapper') {
    			func(testSet[q]);
    		}
    	}
    }
}

/** called by generated quiz HTML when a given question is selected/activated.
 * 
 */
function setQuizQuestionActive(x) {
	/** empty */
}

/** Search parents looking for guid attribute

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


/** Find question with problem identifier 
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
			
			o = all[i].getAttribute('pid');
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

/** Return the question number identified by problem number
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
			var parentUl = o.parentNode.parentNode.parentNode.parentNode;
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

//call from JSNI when new question has been loaded in order to set
//the selected question answer
window.setSolutionQuestionAnswerIndex = function(pid, which, disabled) {
	var ulNode = findQuestionByPid(pid);
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


/** Mark all questions as correct */
window.markAllCorrectAnswers = function() {
	showCorrectAnswers(markCorrectResponse);
}

/** Return the count of correct answers
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

/** Return total count of questions
 * 
 */
window.getQuizQuestionCount = function() {
	var count = 0;
	showCorrectAnswers(function(ql) {
		count++;
	});
	return count;
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

function checkQuiz_Gwt() {
	alert('Checking quiz ...');
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


/** dummy, empty implementation 
 * 
 * @return
 */
function log() {
	//
}

/** define empty implementation as place holder
 */
InmhButtons = {};




function replaceAll(txt, replace, with_this) {
	return txt.replace(new RegExp(replace, 'g'), with_this);
}
