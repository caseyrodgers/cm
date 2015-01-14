package hotmath.gwt.cm_core.client.model;

public  enum SearchAllowMode {

    ENABLED_EXCEPT_TESTS("Enabled, Except Tests", 0),
    ENABLED_ALWAYS("Enabled Always", 1),
    DISABLED_ALWAYS("Disabled Always", 2);

    int level;
    String desc;
    private SearchAllowMode(String desc, int level) {
        this.desc = desc;
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public String getDesc() {
        return this.desc;
    }

    public static SearchAllowMode lookup(int level) {
        try {
            SearchAllowMode m = values()[level];
            return m;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return ENABLED_EXCEPT_TESTS; // default
    }

    public static SearchAllowMode lookup(String name) {
        try {
            SearchAllowMode m = valueOf(name);
            if(m != null) {
                return m;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return ENABLED_EXCEPT_TESTS; // default
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

