package hotmath.testset.ha;

/**
 * Defines all currently defined tests and their related meta data.
 * 
 * @TODO: populate from DB?
 * 
 * @author casey
 * 
 */
public enum CmProgram {
    PREALG_PROF_V1(16, "Pre-Alg_v1", "Prof"),
    PREALG_PROF(34, "Pre-Alg", "Prof"), 
    PREALG_CHAP(22, "Pre-Alg", "Chap"), 
    ALG1_PROF(30, "Alg 1", "Prof"),
    ALG1_PROF_V1(17, "Alg 1_v1", "Prof"), 
    ALG2_CHAP(31, "Alg 2", "Chap"), 
    ALG1_CHAP(32, "Alg 1", "Chap"), 
    ALG2_PROF(29, "Alg 2", "Prof"), 
    GEOM_PROF(18, "Geom","Prof"), 
    CAHSEEHM(28, "", "Grad Prep"), 
    TAKS(33,"","Grad Prep Tx"),
    GEOM_CHAP(24, "Geom", "Chap"),
    AUTO_ENROLL(15,"","Auto-Enroll");

    private final int defId;
    private final String subject;
    private final String programId;

    CmProgram(int defId, String subject, String programId) {
        this.defId = defId;
        this.subject = subject;
        this.programId = programId;
    }

    public int getDefId() {
        return defId;
    }

    public String getSubject() {
        return subject;
    }

    public String getProgramId() {
        return programId;
    }
}