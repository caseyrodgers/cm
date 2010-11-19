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
    PREALG_PROF_V1(16, "Pre-Algebra Proficiency (version 1)", "Pre-Alg_v1", "Prof",false),
    PREALG_PROF(34,"Pre-Algebra Proficiency", "Pre-Alg", "Prof",true), 
    PREALG_CHAP(22,"Pre-algebra Chapters", "Pre-Alg", "Chap",true),
    
    ALG1_PROF_V1(17,"Beginning Algebra Proficiency","Alg 1", "Prof",false), 
    ALG1_PROF_V2(30, "Algebra 1 Proficiency (version 1)","Alg 1", "Prof",false),
    ALG1_PROF_V3(35, "Algebra 1 Proficiency (version 2)", "Alg 1", "Prof",false),
    ALG1_PROF(38, "Algebra 1 Proficiency", "Alg 1", "Prof",true),
    
    ALG2_CHAP(31,"Algebra 2 - Chapters", "Alg 2", "Chap",true), 
    ALG1_CHAP(32, "Algebra 1 - Chapters","Alg 1", "Chap",true), 
    ALG2_PROF_V1(29, "Algebra 2 Proficiency (version 1)", "Alg 2", "Prof",false), 
    ALG2_PROF(40, "Algebra 2 Proficiency", "Alg 2", "Prof",true),
    GEOM_PROF_V1(18, "Geometry Proficiency (version 1)", "Geom","Prof",false),
    GEOM_PROF(39, "Geometry Proficiency", "Geom","Prof",true), 
    CAHSEEHM_V1(28, "California State Exit Exam (Version: 1)", "", "Grad Prep",false), 
    CAHSEEHM(43, "California State Exit Exam", "", "Grad Prep",true),
    TAKS(33,"TAKS Exit Level Practice Test","","Grad Prep Tx",true),
    GEOM_CHAP(24,"Geometry - Chapters", "Geom", "Chap",true),
    AUTO_ENROLL(15,"Auto-Enrollment","","Auto-Enroll",true),
    NATIONAL(37,"National Practice Test","","Grad Prep National",true),
    CUSTOM_PROGRAM(36,"Custom Program","Custom","Custom",false),
    ESSENTIALS_V1(41,"Essentials Program (version 1)","Ess","Prof", false),
    ESSENTIALS(42,"Essentials Program","Ess","Prof", true);

    private final int defId;
    private final String subject;
    private final String programType;
    private final boolean isActive;
    private final String title;

    CmProgram(int defId, String title, String subject, String programType,boolean isActive) {
        this.defId = defId;
        this.title = title;
        this.subject = subject;
        this.programType = programType;
        this.isActive = isActive;
    }

    public int getDefId() {
        return defId;
    }

    public String getSubject() {
        return subject;
    }

    public String getProgramType() {
        return programType;
    }
    
    public boolean isActive() {
        return isActive;
    }    
    
    public String getTitle() {
        return title;
    }
}