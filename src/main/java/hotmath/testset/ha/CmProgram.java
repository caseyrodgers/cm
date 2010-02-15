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
    PREALG_PROF_V1(16, "Pre-Alg_v1", "Prof",false),
    PREALG_PROF(34, "Pre-Alg", "Prof",true), 
    PREALG_CHAP(22, "Pre-Alg", "Chap",true), 
    ALG1_PROF(30, "Alg 1", "Prof",true),
    ALG1_PROF_V1(17, "Alg 1_v1", "Prof",false), 
    ALG2_CHAP(31, "Alg 2", "Chap",true), 
    ALG1_CHAP(32, "Alg 1", "Chap",true), 
    ALG2_PROF(29, "Alg 2", "Prof",true), 
    GEOM_PROF(18, "Geom","Prof",true), 
    CAHSEEHM(28, "", "Grad Prep",true), 
    TAKS(33,"","Grad Prep Tx",true),
    GEOM_CHAP(24, "Geom", "Chap",true),
    AUTO_ENROLL(15,"","Auto-Enroll",true);



    private final int defId;
    private final String subject;
    private final String programId;
    private final boolean isActive;

    CmProgram(int defId, String subject, String programId,boolean isActive) {
        this.defId = defId;
        this.subject = subject;
        this.programId = programId;
        this.isActive = isActive;
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
    
    public boolean isActive() {
        return isActive;
    }    
}