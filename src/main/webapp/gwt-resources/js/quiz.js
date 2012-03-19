function initializeQuiz() {
    var ts = document.getElementById('testset_div');
    if (ts) {
        var divs = ts.getElementsByTagName("div");
        for ( var i = 0, t = divs.length; i < t; i++) {
            var d = divs[i];
            if (d.className == 'hm_question_def') {
                initializeQuizQuestion(d);
            }
        }
        processMathJax();
    }
}

var uniquer = 1;
function initializeQuizQuestion(question) {
    var tag = 'answer_' + uniquer;
    var alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var answers = question.getElementsByTagName("li");
    for ( var i = 0, t = answers.length; i < t; i++) {
        answer = answers[i];

        var trueOrFalse = answer.getAttribute("correct");

        var elems = answer.getElementsByTagName("div");
        var answerEle = elems[0];

        uniquer++;

        var id = 'answer_id_' + uniquer;

        var answerNum = alphabet.charAt(i);

        /** append the input element */
        var inputElement = "<span class='question-input' style='margin-right: 10px'>"
            + "<input value='"
            + trueOrFalse
            + "' type='radio' name='"
            + tag
            + "' id='"
            + id
            + "' onclick='questionGuessChanged(this)'/>"
            + '&nbsp;'
            + answerNum + '</span>'

            answerEle.innerHTML = inputElement + answerEle.innerHTML;

        if (elems.length > 0) {
            elems[1].style.display = 'none';
        }
    }
}

function hideQuestionResult(question) {
    var answers = question.getElementsByTagName("li");
    for ( var i = 0, t = answers.length; i < t; i++) {
        answer = answers[i];
        var elems = answer.getElementsByTagName("div");
        if (elems.length > 1) {
            elems[1].style.display = 'none';
        }
    }
}

function editQuizQuestion(pid) {
    if (pid) {
        var w = window.open('/solution_editor/SolutionEditor.html?pid=' + pid);
    }
}

/** hide new style of question results
 *
 * TODO: why doesn't IE ajacent child selector work?
 *
 */
function hideQuizQuestionResults(tagName) {
    if (!tagName)
        tagName = 'testset_div';
    var ts = document.getElementById(tagName);
    if (ts) {
        var divs = ts.getElementsByTagName("div");
        for ( var i = 0, t = divs.length; i < t; i++) {
            var d = divs[i];
            if (d.className == 'hm_question_def') {
                hideQuestionResult(d);
            }
        }
        processMathJax();
    }
}

/** Take list of input elements, and mark each as readonly
 *  and select the correct answer as identified in the
 *  answers array.
 *
 * @param questionList
 * @param answers
 */
function prepareCustomQuizForDisplay(questionList, answers) {
    var divs = questionList.getElementsByTagName("input");
    var questionNum = 0;
    var nextCorrect = 0;
    for ( var i = 0, t = divs.length; i < t; i++) {
        if (i > 3 && (i % 4) == 0) {
            questionNum++;
        }
        divs[i].disabled = true;
        var choiceThisQuestion = i - (questionNum * 4);
        if (answers[questionNum] == choiceThisQuestion) {
            divs[i].checked = true;
        }
    }
}
