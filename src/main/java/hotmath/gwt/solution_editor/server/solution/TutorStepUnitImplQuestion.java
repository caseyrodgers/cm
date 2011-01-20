package hotmath.gwt.solution_editor.server.solution;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root (name="stepunit")
public class TutorStepUnitImplQuestion extends TutorStepUnit {
    
    @Element (required=false,data=true)
    Question question;
    
    List<String> figures = new ArrayList<String>();
    
    public TutorStepUnitImplQuestion() {
    }
    
    public TutorStepUnitImplQuestion(Question question) {
        this.question = question;
    }
    
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
    
    @Override
    public String getContentAsString() {
        return toString();
    }
}

