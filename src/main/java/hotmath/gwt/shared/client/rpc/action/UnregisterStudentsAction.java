package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;

import java.util.List;

public class UnregisterStudentsAction implements Action<StringHolder> {

	private static final long serialVersionUID = 7989547375482529977L;

	List<StudentModel> smList;

    public UnregisterStudentsAction() {}

    public UnregisterStudentsAction(List<StudentModel> smList) {
    	System.out.println("+++ UnregisterStudentsAction(): smList.size(): " + smList.size());
        this.smList = smList;
    }

    @Override
    public String toString() {
        return "UnregsterStudentsAction: smList: " + smList;
    }

    public List<StudentModel> getStudentList() {
        return smList;
    }

    public void setStudentList(List<StudentModel> smList) {
        this.smList = smList;
    }

}
