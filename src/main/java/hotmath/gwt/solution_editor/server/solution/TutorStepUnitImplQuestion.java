package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Root;



@Root (name="stepunit")
public class TutorStepUnitImplQuestion extends TutorStepUnit {
    String value;
    String pid;
    public TutorStepUnitImplQuestion(String pid, String value) {
        this.pid = pid;
        this.value = value;
    }
    
    @Override
    public String getContentAsString() {
        return this.value;
    }
    
    @Override
    public Role getRole() {
        // TODO Auto-generated method stub
        return Role.HINT;
    }
}

