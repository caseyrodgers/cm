package hotmath.gwt.cm_rpc.client.model;


public enum CmProgramType {

    AUTOENROLL("Auto-Enroll"),
    CHAP("Chap"),
    CUSTOM("Custom"),
    CUSTOMQUIZ("Custom Quiz"),
    GRADPREP("Grad Prep"),
    GRADPREPNATIONAL("Grad Prep National"),
    GRADPREPTX("Grad Prep Tx"),
    MATHJAX("MathJax"),
    PROF("Prof"),
    OTHER("Other"),
    ASSIGNMENTSONLY("Assignments Only"),
    UNKNOWN("UnknownType"),
    PLACEME("PlaceMe"),
    NONE("none");

    private final String type;

    CmProgramType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    /** Do a case-insensitive search
     *  for name and return UNKNOWN
     *
     *
     * @param name
     * @return
     */
    static public CmProgramType lookup(String name) throws IllegalArgumentException {
        
        if(name == null) {
            return null;
        }
        
        name = name.replaceAll(" ", "").replaceAll("-", "");

        for (CmProgramType e : values()) {
        	String type=e.toString();
            if (type.equalsIgnoreCase(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No such CmProgramType: " + name);
    }
}
