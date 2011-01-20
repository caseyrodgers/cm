package hotmath.gwt.solution_editor.server.solution;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;



@Root (name="question")
public class Question {
    
    @ElementList (inline=true,required=false)
    List<Guess> guess = new ArrayList<Guess>();

    
    public Question() {
    }


    public List<Guess> getGuess() {
        return guess;
    }


    public void setGuess(List<Guess> guess) {
        this.guess = guess;
    }
    
}

