package hotmath.gwt.cm_rpc_assignments.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class PidStats implements Response {
    
    private String pid;
    private int cntCorrect;
    private int cntIncorrect;
    private int cntPending;
    private int cntUnanswered;

    public PidStats() {}
    
    public PidStats(String pid, int cntCorrect, int cntIncorrect, int cntUnanswered, int cntPending) {
        this.pid = pid;
        this.cntCorrect = cntCorrect;
        this.cntIncorrect = cntIncorrect;
        this.cntUnanswered = cntUnanswered;
        this.cntPending = cntPending;
    }

    public int getCntPending() {
        return cntPending;
    }

    public void setCntPending(int cntPending) {
        this.cntPending = cntPending;
    }

    public int getCntUnanswered() {
        return cntUnanswered;
    }

    public void setCntUnanswered(int cntUnanswered) {
        this.cntUnanswered = cntUnanswered;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getCntCorrect() {
        return cntCorrect;
    }

    public void setCntCorrect(int cntCorrect) {
        this.cntCorrect = cntCorrect;
    }

    public int getCntIncorrect() {
        return cntIncorrect;
    }

    public void setCntIncorrect(int cntIncorrect) {
        this.cntIncorrect = cntIncorrect;
    }
    
}
