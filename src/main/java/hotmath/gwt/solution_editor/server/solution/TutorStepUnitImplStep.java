package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root (name="stepunit")
public class TutorStepUnitImplStep extends TutorStepUnit {
    
    @Element (required=false,data=true)
    String step;
    
    public TutorStepUnitImplStep() {
    }
    
    public TutorStepUnitImplStep(String text) {
        step = text;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
    
    @Override
    public String getContentAsString() {
        return step;
    }
}

