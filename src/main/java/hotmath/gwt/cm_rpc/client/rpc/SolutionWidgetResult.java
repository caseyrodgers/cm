package hotmath.gwt.cm_rpc.client.rpc;

public class SolutionWidgetResult implements Response {
    
    private String value;
    private boolean correct;

    public SolutionWidgetResult(){}
    
    public SolutionWidgetResult(String value, boolean correct) {
        this.value = value;
        this.correct = correct;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @Override
    public String toString() {
        return "SolutionWidgetResult [value=" + value + ", correct=" + correct + "]";
    }

}
