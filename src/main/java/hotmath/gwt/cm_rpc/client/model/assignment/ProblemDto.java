package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ProblemDto extends BaseDto implements Response {

    static public enum ProblemType {
        INPUT_WIDGET, MULTI_CHOICE, WHITEBOARD, UNKNOWN
    };

    public ProblemDto() {
    }

    String label, pid, lesson;
    ProblemType problemType = ProblemType.UNKNOWN;
    int assignKey;

    public ProblemDto(int id, String lesson, String label, String pid, ProblemType problemType, int assignKey) {
        super(id, label);
        this.label = label;
        this.lesson = lesson;
        this.pid = pid;
        this.problemType = problemType;
        this.assignKey = assignKey;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns only the pid portion. The value stored in SOLUTIONXML
     * 
     * @return
     */
    public String getPidOnly() {
        return getPid().split("\\$")[0].trim();
    }

    /**
     * Returns the full PID, with any problem number identifier (ie,
     * test_test$2)
     * 
     * @return
     */
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "ProblemDto [label=" + label + ", pid=" + pid + ", lesson=" + lesson + ", problemType=" + problemType
                + ", assignKey=" + assignKey + "]";
    }

    /** Return the type tag for problem type
     * 
     * TODO: move to ProblemType
     * @return
     */
    public String getProblemTypeName() {

        String typeTag = "";
        switch (getProblemType()) {
        case INPUT_WIDGET:
            typeTag = "(iw)";
            break;

        case MULTI_CHOICE:
            typeTag = "(mc)";
            break;

        case WHITEBOARD:
            typeTag = "(w)";
            break;

        case UNKNOWN:
        default:
            typeTag = "(u)";
            break;
        }

        return typeTag;
    }

    public String getLabelWithType() {
        return getProblemTypeName() + " " + getLabel();
    }
}
