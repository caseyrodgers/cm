package hotmath.gwt.cm_rpc.client.model;


public enum CmProgramType {

    AUTOENROLL("Auto-Enroll", "AUTOENROLL"),
    CHAP("Chap", "CHAP"),
    CUSTOM("Custom","CUSTOM"),
    CUSTOMQUIZ("Custom Quiz", "CUSTOMQUIZ"),
    GRADPREP("Grad Prep","GRADPREP"),
    GRADPREPNATIONAL("Grad Prep National", "GRADPREPNATIONAL"),
    GRADPREPTX("Grad Prep Tx", "GRADPREPTX"),
    MATHJAX("MathJax", "MATHJAX"),
    PROF("Prof", "PROF"),
    OTHER("Other", "Other"),
    ASSIGNMMENTS_ONLY("Assignments Only", "ASSIGNMENTSONLY"),
    UNKNOWN("UnknownType", "UKNOWN");

    private final String type;
    private final String name;

    CmProgramType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /** Do a case-insensitive search
     *  for name and return UNKNOWN
     *
     *
     * @param name
     * @return
     */
    static public CmProgramType lookup(String name) throws IllegalArgumentException {
        boolean found = false;
        for (CmProgramType e : values()) {
        	String type=e.type;
            if (type.equalsIgnoreCase(name)) {
                return e;
            }
            
        	String ename=e.name;
            if (ename.equalsIgnoreCase(name)) {
                return e;
            }

        }
        throw new IllegalArgumentException("No such CmProgramType: " + name);
    }
}
