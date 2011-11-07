package hotmath.cm.test;

import hotmath.cm.test.HaTestSetQuestion.IQuestionParser;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

import sb.util.SbUtilities;


/** Provide HTML parsing for the Question.
 * 
 * This will find the correct answers stored in the
 * nested divs with Correct/Incorrect values.  Then 
 * we remove the answers from the HTML and make value
 * answer available via getCorrectAnswer method.
 * 
 * Provide path for both old style, and new
 * Solution Editor friendly format. 
 * 
 *
 */
public class QuestionParser implements IQuestionParser,Serializable{
    static Parser __parser;
    Node root;
    int correctAnswer=-1;
    String questionHtml;
    String processedHtml;
    
    static Logger LOGGER = Logger.getLogger(QuestionParser.class);
    
    public QuestionParser(String questionHtml) throws Exception {
        this.questionHtml = questionHtml;
        if(__parser == null) {
            __parser = new Parser();
            __parser.setFeedback(Parser.STDOUT);
        }
        
        processHtml(questionHtml);
        
        if(correctAnswer == -1)
            System.out.println("no correct answer found");
    }
    
    
    public void processHtml(String questionHtml) throws Exception {
        __parser.setInputHTML(questionHtml);
        __parser.visitAllNodesWith(new NodeVisitor() {
            int visited=0;
            @Override
            public void visitTag(Tag tag) {
                if(root == null)
                    root = tag;
                
                if(tag.getTagName().equals("LI")) {
                    String text = tag.getAttribute("correct");
                    if(text != null) {
                        if(text.toLowerCase().indexOf("yes") > -1) {
                            correctAnswer=visited;
                        }
                    }
                    else {
                        System.out.println("No correct answer found in solution xml");
                    }
                }
            }
        });
        
        if(correctAnswer == -1) {
            /** old school */

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
            
            


        }
        
        if(correctAnswer == -1) {
            LOGGER.error("No correct answer found for question: " + questionHtml);
        }
        
        /** remove the Correct and Incorrect divs ..
         *  @TODO: why can't this be done in the Visitor
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
