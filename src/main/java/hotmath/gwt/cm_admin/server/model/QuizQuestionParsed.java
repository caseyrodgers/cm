package hotmath.gwt.cm_admin.server.model;

import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;


/** Extract the correct answer from quiz question.
 * 
 *  The answer is embedded like:
 *    UL
 *        LI
 *            ...HTML...
 *            <div style='display: none'>Incorrect|Correct</div>
 *        LI
 *        ..etc..
 *
 *  Ugly but true.
 *  
 *  TODO: use the new quiz format defined by SolutionEditor
 *  
 * @author casey
 *
 */
public class QuizQuestionParsed {

    String _html;
    Parser _parser;
    DOMFragmentParser _parserBalancer;
    StringWriter _writerBalancer;
    Logger _logger = Logger.getLogger(QuizQuestionParsed.class);
    String statement;

    public QuizQuestionParsed(String statement) throws Exception {
        this.statement = statement;
        // setup HTML parser to remove elements
        _parser = new Parser();
        _parser.setFeedback(Parser.STDOUT);
        _parserBalancer = new DOMFragmentParser();
            
        extractCorrectAnswer(statement);
    }
    
    public int getCorrectAnswer() {
        return correctNumber;
    }
    
    int thisResult=0;
    int correctNumber=-1;
    private void extractCorrectAnswer(String html) throws Exception {
        String validatorHead = "<span class='pp'>";
        String validatorTail = "</span>";

        
        html = validatorHead + html + validatorTail;
        _parser.setInputHTML(html);
        NodeVisitor visitor = new NodeVisitor() {
            public void visitTag(Tag tag) {
                String name = tag.getTagName();
                if (name.equalsIgnoreCase("div")) {
                    String style = tag.getAttribute("style");
                    if(style != null && style.equals("display: none")) {
                        String text = tag.getFirstChild().getText();
                        if(text.equalsIgnoreCase("incorrect") || text.equalsIgnoreCase("correct")) {
                            if(text.equalsIgnoreCase("correct")) {
                                correctNumber=thisResult;
                            }
                            thisResult++;
                        }
                    }
                }
            }
        };
        _parser.visitAllNodesWith(visitor);
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}
