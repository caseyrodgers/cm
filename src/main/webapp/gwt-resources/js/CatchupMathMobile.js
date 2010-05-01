

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

function findQuestionNumberByPid(pid) {
	var all = document.getElementById('testset_div').getElementsByTagName('div');
	var num=0;
	try {
		for ( var i = 0; i < all.length; i++) {
			var o = all[i].getAttribute('guid');
			if (o) {
				if(o == pid) {
					return num;
				}
				else {
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
			var parentUl = o.parentNode.parentNode;
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


function checkQuiz_Gwt() {
	alert('Checking quiz ...');
}
