package hotmath.gwt.cm_rpc.client.rpc.cm2;

public class QuizAnswer {

    private String pid;
    private int choice;

    public QuizAnswer(String pid, int choice) {
        this.pid = pid;
        this.choice = choice;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

}
