package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;

import java.util.List;

public class UnregisterStudentsAction implements Action<StringHolder> {

	private static final long serialVersionUID = 7989547375482529977L;

	List<StudentModelI> smList;

    public UnregisterStudentsAction() {}

    public UnregisterStudentsAction(List<StudentModelI> smList) {
        this.smList = smList;
    }


    public List<StudentModelI> getStudentList() {
        return smList;
    }

    public void setStudentList(List<StudentModelI> smList) {
        this.smList = smList;
    }

    @Override
    public String toString() {
        return "UnregisterStudentsAction [smList=" + smList + "]";
    }    
}
