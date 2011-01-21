package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root (name="stepunit")
public class TutorStepUnitImplHint extends TutorStepUnit {
    
    @Element (required=false,data=true)
    String hint;

    public TutorStepUnitImplHint() {}
    
    public TutorStepUnitImplHint(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
    
    @Override
    public String getContentAsString() {
        return hint;
    }
    
    
    @Override
    public Role getRole() {
        // TODO Auto-generated method stub
        return Role.HINT;
    }
}

