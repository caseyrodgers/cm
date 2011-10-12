package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;



@Root (name="stepunit")
public class TutorStepUnitImplStep extends TutorStepUnit {
    
    
    @Element
    @Convert(StepConverter.class)
    private String step;

    
    public TutorStepUnitImplStep() {
    }
    
    public TutorStepUnitImplStep(String text) {
        this.step = text;
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

    @Override
    public Role getRole() {
        // TODO Auto-generated method stub
        return Role.STEP;
    }
}
