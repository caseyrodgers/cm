package hotmath.gwt.shared.client.rpc.action;

import com.google.gwt.user.client.rpc.IsSerializable;

import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.rpc.Action;

public class GetProgramDefinitionsAction implements Action<CmArrayList<SubjectModel>>{


    String progId;
    
    public GetProgramDefinitionsAction() {}
    
    public GetProgramDefinitionsAction(String progId) {
        this.progId = progId;
    }

    public String getProgId() {
        return progId;
    }

    public void setProgId(String progId) {
        this.progId = progId;
    }

    @Override
    public String toString() {
        return "GetProgramDefinitionsAction [progId=" + progId + "]";
    }

    
}

