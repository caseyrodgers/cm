package hotmath.gwt.cm_rpc.client.rpc;

public class ImportAssignmentAction implements Action<RpcData>{
    
    int aid;
    private int groupToImportInto;
    private int assignmentToImport;
    public ImportAssignmentAction() {}
    
    public ImportAssignmentAction(int aid,int groupToImportInto, int assignmentToImport) {
        this.aid = aid;
        this.groupToImportInto = groupToImportInto;
        this.assignmentToImport = assignmentToImport;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getGroupToImportInto() {
        return groupToImportInto;
    }

    public void setGroupToImportInto(int groupToImportInto) {
        this.groupToImportInto = groupToImportInto;
    }

    public int getAssignmentToImport() {
        return assignmentToImport;
    }

    public void setAssignmentToImport(int assignmentToImport) {
        this.assignmentToImport = assignmentToImport;
    }
}
