package hotmath.cm.test;

import hotmath.ProblemID;
import hotmath.gwt.shared.server.service.command.GetQuizHtmlCommand;

import java.io.Serializable;

/** Represents a single Question in a test set.
 *
 * Each question is in the form:
 *

<DIV id="question_1" class="question_stepunit">
    <P> The Question Text </P>
    <UL class="question_responses">
        <LI>
            <INPUT onclick="questionGuessChanged(this)" id="question_guess_3"
                type="radio" name="question_1" value="false">
                <LABEL for="question_guess_3">
                    <P>C. 2</P>
                </LABEL>
                <DIV style="display: none">Incorrect</DIV>
        </LI>
        <LI>
            <INPUT onclick="questionGuessChanged(this)" id="question_guess_4"
                type="radio" name="question_1" value="true">
                <LABEL for="question_guess_4">
                    <P>D. 8</P>
                </LABEL>
                <DIV style="display: none">Correct</DIV>
        </LI>
    </UL>
</DIV>
 *
 *
 * Notice the answer is included in HTML.  This will strip that out of HTML and record
 * correct answer.
 *
 * @see GetQuizHtmlCommand setAnswers
 *
 */
public class HaTestSetQuestion implements Serializable{
    String problemIndex;
    String questionHtml;

    volatile IQuestionParser questionParser;

    public HaTestSetQuestion(String guid, String questionHtml) throws Exception {
        this.problemIndex = guid;
        this.questionHtml = questionHtml;
        this.questionParser = new QuestionParser(questionHtml);
    }

    public String getProblemIndex() {
        return problemIndex;
    }

    public void setProblemIndex(String problemIndex) {
        this.problemIndex = problemIndex;
    }

    public String getQuestionHtml() {
        return questionHtml;
    }

    public void setQuestionHtml(String questionHtml) {
        this.questionHtml = questionHtml;
    }

    public String getSolutionHref() {
        String path = new ProblemID(problemIndex).getSolutionPath_Full();
        return path;
    }

    public String getProblemNumber() {
        return new ProblemID(problemIndex).getProblemNumber();
    }

    public String getQuestionHtmlProcessed() throws Exception {
        return questionParser.getProcessedHtml();
    }

    public int getCorrectAnswer() {
        return questionParser.getCorrectAnswer();
    }

    public static interface IQuestionParser {
        void processHtml(String questionHtml) throws Exception;
        String getProcessedHtml();
        int getCorrectAnswer();
    }

   }
