package hotmath.cm.test;

import hotmath.ProblemID;
import hotmath.gwt.shared.server.service.command.GetQuizHtmlCommand;

import java.io.Serializable;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

import sb.util.SbUtilities;

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
    
    volatile QuestionParser questionParser;
    
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
    
    
    /** Provide HTML parsing for the Question.
     * 
     * This will find the correct answers stored in the
     * nested divs with Correct/Incorrect values.  Then 
     * we remove the answers from the HTML and make value
     * answer available via getCorrectAnswer method.
     * 
     *
     */
    static class QuestionParser implements Serializable{
        static Parser __parser;
        Node root;
        int correctAnswer=-1;
        String questionHtml;
        String processedHtml;
        
        public QuestionParser(String questionHtml) throws Exception {
            this.questionHtml = questionHtml;
            if(__parser == null) {
                __parser = new Parser();
                __parser.setFeedback(Parser.STDOUT);
            }
            
            processHtml();
        }
        
        public void processHtml() throws Exception {
            __parser.setInputHTML(questionHtml);
            __parser.visitAllNodesWith(new NodeVisitor() {
                int visited=0;
                @Override
                public void visitTag(Tag tag) {
                    if(root == null)
                        root = tag;
                    
                    if(tag.getTagName().equalsIgnoreCase("DIV")) {
                        if(tag.getAttribute("id") == null && tag.getAttribute("class") == null) {
                            /** is the answer div */
                            String text = tag.toPlainTextString();
                            if(text.indexOf("Correct") > -1) {
                                correctAnswer=visited;
                            }
                            visited++;
                        }
                    }
                }
            });
            
            /** remove the Correct and Incorrect divs ..
             *  @TODO: why cannot this be done in the Visitor
             */
            processedHtml = SbUtilities.replaceSubString(questionHtml,">Correct<","><");
            processedHtml = SbUtilities.replaceSubString(processedHtml,">Incorrect<","><");
        }

        public int getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(int correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getProcessedHtml() {
            return processedHtml;
        }

        public void setProcessedHtml(String processedHtml) {
            this.processedHtml = processedHtml;
        }
    }
}
