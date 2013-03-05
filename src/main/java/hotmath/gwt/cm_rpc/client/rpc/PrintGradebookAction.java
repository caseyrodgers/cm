package hotmath.gwt.cm_rpc.client.rpc;

import java.util.Date;

import hotmath.gwt.shared.client.rpc.CmWebResource;

public class PrintGradebookAction implements Action<CmWebResource> {

	private int adminId;
    private int groupId;
    private Date fromDate;
    private Date toDate;

    public PrintGradebookAction(){}
    
    public PrintGradebookAction(int aid, int groupId) {
        this.adminId = aid;
        this.groupId = groupId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int aid) {
        this.adminId = aid;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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

	@Override
    public String toString() {
        return "PrintGradebookAction [aid=" + adminId + ", groupId=" + groupId + "]";
    }
}
