package hotmath.gwt.cm_rpc.client.model;


public enum CmProgramType {

    PROF("Prof", "PROF"), CHAP("Chap", "CHAP"), GRADPREP("Grad Prep", "GRADPREP"), GRADPREPTX("Grad Prep Tx",
                                                                                              "GRADPREPTX"), GRADPREPNATIONAL("Grad Prep National", "GRADPREPNATIONAL"), CUSTOM("Custom", "CUSTOM"), CUSTOMQUIZ(
                                                                                                                                                                                                                "Custom Quiz", "CUSTOMQUIZ"), AUTOENROLL("Auth-Enroll", "AUTOENROLL"),UNKNOWN("UnknownType", "UKNOWN");

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
            if (e.toString().equalsIgnoreCase(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No such CmProgramType: " + name);
    }
}
