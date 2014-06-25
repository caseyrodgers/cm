package hotmath.gwt.shared.client;

import com.allen_sauer.gwt.log.client.Log;
import com.allen_sauer.gwt.log.client.Logger;

/**
 * Defines all currently defined tests and their related meta data.
 * 
 * defined in HA_TEST_DEF
 * 
 * @TODO: populate from DB?
 * 
 * @author casey
 * 
 */
public enum CmProgram {
    PREALG_PROF_V1(16, "Pre-Algebra Proficiency (version 1)", "Pre-Alg_v1", "Prof",false, 6),
    PREALG_PROF(34,"Pre-Algebra Proficiency", "Pre-Alg", "Prof",true, 6), 
    PREALG_CHAP(22,"Pre-algebra Chapters", "Pre-Alg", "Chap",true, 2),
    
    ALG1_PROF_V1(17,"Beginning Algebra Proficiency","Alg 1", "Prof",false, 6), 
    ALG1_PROF_V2(30, "Algebra 1 Proficiency (version 1)","Alg 1", "Prof",false, 6),
    ALG1_PROF_V3(35, "Algebra 1 Proficiency (version 2)", "Alg 1", "Prof",false, 6),
    ALG1_PROF(38, "Algebra 1 Proficiency", "Alg 1", "Prof",true, 6),
    
    ALG2_CHAP(31,"Algebra 2 - Chapters", "Alg 2", "Chap",true, 2), 
    ALG1_CHAP(32, "Algebra 1 - Chapters","Alg 1", "Chap",true, 2), 
    ALG2_PROF_V1(29, "Algebra 2 Proficiency (version 1)", "Alg 2", "Prof",false, 6), 
    ALG2_PROF(40, "Algebra 2 Proficiency", "Alg 2", "Prof",true, 6),
    GEOM_PROF_V1(18, "Geometry Proficiency (version 1)", "Geom","Prof",false, 6),
    GEOM_PROF(39, "Geometry Proficiency", "Geom","Prof",true, 6), 
    CAHSEEHM_V1(28, "California State Exit Exam (Version: 1)", "", "Grad Prep",false, 8), 
    CAHSEEHM(43, "California State Exit Exam", "", "Grad Prep",true, 8),
    TAKS(33,"TAKS Exit Level Practice Test","","Grad Prep Tx",true, 6),
    GEOM_CHAP(24,"Geometry - Chapters", "Geom", "Chap",true, 2),
    AUTO_ENROLL(15,"Auto-Enrollment","","Auto-Enroll",true, 0),
    NATIONAL_V1(37,"National Practice Test","","Grad Prep National (version 1)",false, 8),
    NATIONAL(44,"National Practice Test","","Grad Prep National",true, 8),
    CUSTOM_PROGRAM(36,"Custom Program","Custom","Custom",false, 0),
    CUSTOM_QUIZ(46,"Custom Quiz","Custom","Custom",false, 0),
    ESSENTIALS_V1(41,"Essentials Program (version 1)","Ess","Prof", false, 6),
    ESSENTIALS(42,"Essentials Program","Ess","Prof", true, 6),
    OTHER_ESS(47,"Essentials, Other", "Ess", "Other",false,1),
    OTHER_PREALG(48,"Pre-Algebra, Other", "Pre-Alg", "Other",false,1),
    OTHER_ALG1(49,"Algebra 1, Other", "Alg 1", "Other", false, 1),
    OTHER_GEOM(50,"Geometry, Other", "Geom", "Other", false, 1),
    OTHER_ALG2(51,"Algebra 2, Other", "Alg 2", "Other", false, 1),
    ASSIGNMENTS_ONLY(53,"Assignments Only", "Assignments Only", "Assignments Only", true, 0),
    ELEMALG(54,"Elementary Algebra", "ElemAlg", "Prof", true, 9),
    ELEMALG_CHAP(55,"Elementary Algebra - Chapters", "ElemAlg", "Chap",true, 2),
    BASICMATH(56,"Basic Math", "BasicMath", "Prof", true, 11),
    AUTO_ENROLL_COLLEGE(57,"Auto-Enrollment, College","","Auto-Enroll, College",true, 0), 
    
    /** Represents no current program assigned */
    NONE(99,"No Program","","none",true,0), 
    FOUNDATIONS(58,"Foundations", "Foundations", "Prof", true, 8);

    private final int     defId;
    private final String  title;
    private final String  subject;
    private final String  programType;
    private final boolean isActive;
    private final int     sectionCount;

    CmProgram(int defId, String title, String subject, String programType, boolean isActive, int sectionCount) {
        this.defId = defId;
        this.title = title;
        this.subject = subject;
        this.programType = programType;
        this.isActive = isActive;
        this.sectionCount = sectionCount;
    }

    public int getDefId() {
        return defId;
    }

    public String getTitle() {
        return title;
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
    
	public int getSectionCount() {
		return sectionCount;
	}

    public static CmProgram lookup(String subjId) {
        for(CmProgram cp: values()) {
            if(cp.name().toLowerCase().equals((subjId.toLowerCase()))) {
                return cp;
            }
        }
        return null;
    }

}