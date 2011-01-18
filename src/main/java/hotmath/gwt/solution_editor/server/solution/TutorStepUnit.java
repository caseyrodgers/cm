package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root (name="stepunit")
public class TutorStepUnit {
    
    @Element (required=false,data=true)
    String step;
    
    @Element (required=false,data=true)
    String hint;
    
    public TutorStepUnit() {
        
    }
    public TutorStepUnit(StepType type, String text) {
        switch(type) {
           case HINT:
               hint = text;
               break;
               
           case STEP:
               step = text;
               break;
        }
        
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
    
}

