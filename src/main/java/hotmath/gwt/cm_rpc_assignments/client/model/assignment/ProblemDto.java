package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class ProblemDto extends BaseDto implements Response {

    static public enum ProblemType {
        INPUT_WIDGET, MULTI_CHOICE, WHITEBOARD, UNKNOWN
    };

    String label, pid;
    ProblemType problemType = ProblemType.UNKNOWN;
    int assignKey;
    private int ordinalNumber;

    public ProblemDto() {
    }


    public ProblemDto(int ordinalNumber, int id, String label, String pid, int assignKey) {
        super(id, label);
        this.ordinalNumber = ordinalNumber;
        this.label = label;
        this.pid = pid;
        this.assignKey = assignKey;
    }

    public ProblemDto(String pid, String fullPath) {
        setPid(pid);
        setLabel(fullPath);
        setName(fullPath);
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

    public String getLabel() {
        return label;
    }
    
    /** Return label, but strip off any trailing number portion
     * 
     * @return
     */
    public String getLabelWithoutNumber() {
    	return label.split(":")[0];
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

    /**
     * 
     * TODO: move to ProblemType
     * 
     * @return the type tag for problem type
     */
    public String getProblemTypeName() {
        String typeTag = "";
        switch (getProblemType()) {
        case INPUT_WIDGET:
            typeTag = "";
            break;

        case MULTI_CHOICE:
            typeTag = "(MC)";
            break;

        case WHITEBOARD:
            typeTag = "(WB)";
            break;

        case UNKNOWN:
        default:
            typeTag = "(u)";
            break;
        }

        return typeTag;
    }

    public String getLabelWithType() {
        return getLabel() + " " + getProblemTypeName();
    }

    public int getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(int ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }


    @Override
    public String toString() {
        return "ProblemDto [label=" + label + ", pid=" + pid + ", problemType=" + problemType + ", assignKey=" + assignKey
                + ", ordinalNumber=" + ordinalNumber + "]";
    }

    public boolean equals(Object obj) {
        if (obj instanceof ProblemDto && getPid() != null) {
            return ((ProblemDto) obj).getPid().equals(getPid());
        } else {
            return super.equals(obj);
        }
    }
}