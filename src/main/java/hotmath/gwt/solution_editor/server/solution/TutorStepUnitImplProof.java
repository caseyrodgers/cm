package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Root;



@Root (name="stepunit")
public class TutorStepUnitImplProof extends TutorStepUnit {
    
    String text;
    public TutorStepUnitImplProof() {
    }
    
    public TutorStepUnitImplProof(String value) {
        this.text = value;
    }
    
    @Override
    public String getContentAsString() {
        return text;
    }
    

    @Override
    public Role getRole() {
        // TODO Auto-generated method stub
        return Role.STEP;
    }    
}

