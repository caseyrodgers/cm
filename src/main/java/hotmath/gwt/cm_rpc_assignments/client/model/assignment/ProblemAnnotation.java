package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class ProblemAnnotation implements Response {
    
    private int assignKey;
    private String pid;

    public ProblemAnnotation(){}
    
    public ProblemAnnotation(int assignKey, String pid) {
        this.assignKey = assignKey;
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    
    public int getAssignKey() {
        return assignKey;
    }
    
    @Override
    public boolean equals(Object obj) {
        ProblemAnnotation pa = (ProblemAnnotation)obj;
        return pa.getPid().equals(pid) && pa.getAssignKey() == assignKey; 
    }

}
