package hotmath.gwt.shared.client.rpc.action;

import java.util.Date;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

public class GetStudentActivityAction implements Action<CmList<StudentActivityModel>>{

    StudentModelI student;
    Date fromDate;
    Date toDate;
    
    public GetStudentActivityAction(){}

    
    public GetStudentActivityAction(StudentModelI sm, Date fromDate, Date toDate) {
        this.student = sm;
    	this.fromDate = fromDate;
    	this.toDate = toDate;
    }

    public StudentModelI getStudent() {
        return student;
    }

    public void setStudent(StudentModelI student) {
        this.student = student;
    }

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
