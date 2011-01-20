package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;



/** Simple framework breaks here because an element
 *  cannot contain both TEXT and child elements.
 *  
 * @author casey
 *
 */
@Root (name="guess")
public class Guess {
    
    public Guess() { }
    
    public Guess(String guess, String response, String correct) {
        this.guess = guess;
        this.response = response;
        this.correct = correct;
    }
    
    @Text (data=true,required=true)
    String guess;
    
    @Element (required=false)
    String response;
    
    @Attribute (required=false)
    String correct;

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }
    
    
}

