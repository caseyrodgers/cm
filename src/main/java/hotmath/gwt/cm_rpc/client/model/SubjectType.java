package hotmath.gwt.cm_rpc.client.model;

/** Represents all available CM subjects
 * 
 * @author casey
 *
 */
public enum SubjectType {
    
    ESSENTIALS("Essentials", "E", 7),
    PREALGEBRA("Pre-Algebra", "P", 9),
    ALGEBRA1("Algebra 1", "A1", 10),
    GEOMETRY("Geometry", "G", 11),
    ALGEBRA2("Algebra 2","A2", 12),
    COLLEGE_ELE_ALGEBRA("College Elementary Algebra", "CA", 13);
    
    private String label;
    private int level;
    private String key;

    private SubjectType(String label, String key, int level) {
        this.label = label;
        this.key = key;
        this.level = level;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public static SubjectType lookup(String subject) {
        String s = subject.toLowerCase();
        for(SubjectType type: values()) {
            if(type.getLabel().toLowerCase().equals(s)) {
                return type;
            }
        }
        System.out.println("Unknown SubjectType: " + subject);
        return null;
    }

}
