package hotmath.testset.ha;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.List;

/** Class to handle what happens when the end of a given program is reached
 * 
 *
 * Pre-alg prof -> Alg-1 Prof -> Geo-Prof -> Alg-2 Prof -> Grad test
  (Calif) -> Grad test (Texas) -> Grad test (Texas) <loop>

Any chapter except last: Ch<n> -> Ch<n+1>
Last chapter -> Prof test for that same subject

For now, when we do not yet have the Grad test for Texas, The calif
grad test would loop

 * @author casey
 *
 */
public class EndOfProgramHandler {
    
    StudentModel student;
    CmStudentDao dao;
    public EndOfProgramHandler(int userId) throws Exception {
        dao = new CmStudentDao();
        student = dao.getStudentModel(userId);
    }
    
    /** Move this user to the next logical program.  This should 
     * completely move this user and return information identifiing
     * the NEW program assigned.
     * 
     * This will move to the next program in a sequence. Each call will
     * move to the next program in sequence.
     * 
     * Throw Exception if error or program cannot be assigned.
     * 
     * @return
     * @throws Exception
     */
    public StudentUserProgramModel getNextProgram() throws Exception {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            StudentUserProgramModel programCurr = dao.loadProgramInfo(conn,student.getUid());
            
            if(programCurr.getTestDefId() == CmProgram.PREALG_PROF.getDefId()) {
                updateProgram("Alg 1","Prof",null);                
            }
            else if(programCurr.getTestDefId() == CmProgram.ALG1_PROF.getDefId()) {
                updateProgram("Geom","Prof",null);                
            }
            else if(programCurr.getTestDefId() == CmProgram.GEO_PROF.getDefId()) {
                updateProgram("Alg 2", "Prof",null);
            }
            else if(programCurr.getTestDefId() == CmProgram.ALG2_PROF.getDefId() ) {
                updateProgram("","Grad Prep",null);
            }
            else if(programCurr.getTestDefId() == CmProgram.CAHSEEHM.getDefId()) {
                // reset/repeat
                updateProgram("", "Grad Prep",null);
            }
            else {
                // if is a chapter test, then we must find the currently assigned
                // chapter number.  If there are additional chapters, then move to 
                // next in sequence.  Otherwise, move to the associated proficiency test
                
                if(programCurr.getTestDefId() == CmProgram.PREALG_CHAP.getDefId()) {
                    setupChapterTest(conn, programCurr,"Pre-Alg");
                }
                else if(programCurr.getTestDefId() == CmProgram.ALG1_CHAP.getDefId()) {
                    setupChapterTest(conn, programCurr,"Alg 1");
                }
                else {
                    throw new Exception("Unknown program: " + programCurr);
                }
            }

            
            StudentUserProgramModel programNext = dao.loadProgramInfo(conn,student.getUid());
            return programNext;
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }
    
    
    private void setupChapterTest(final Connection conn, StudentUserProgramModel program, String subjId) throws Exception {

        HaTestConfig config = program.getConfig();
        String chapter = config.getChapters().get(0);
        HaTestDefDao dao = new HaTestDefDao(); 
        List<String> chapters = dao.getProgramChapters(dao.getTestDef(conn, program.getTestDefId()));
        
        String nextChapter = getNextChapter(chapters, chapter);
        if(nextChapter != null) {
            // modify the current program to next
            config.getChapters().clear();
            config.getChapters().add(nextChapter);
            
            // assign this new config to the program, by resetting 
            // the entire program.
            updateProgram(subjId, "Chap",nextChapter);
        }
        else {
            updateProgram(subjId, "Prof",null);
        }
        
    }
    
    
    /** Find the next chapter, or return null if end of list
     * 
     * @param chapters
     * @return
     */
    private String getNextChapter(List<String> chapters, String chapter) {
    
        for(int i=0,t=chapters.size();i<t;i++) {
            if(chapters.get(i).equals(chapter)) {
                if(i+1 < t){
                    return chapters.get(i+1);
                }
                else {
                    break;
                }
            }
        }
        
        return null;
    }

    private void updateProgram(String subId,String progId, String chapter) throws Exception {
        student.setProgId(progId);
        student.setSubjId(subId);
        student.setChapter(chapter);
        student.setProgramChanged(true);
        dao.updateStudent(student, true, false, true, false);            
    }

    enum CmProgram {
        PREALG_PROF(16,"Pre-Alg", "Prof"),PREALG_CHAP(22,"Pre-Alg", "Chap"),ALG1_PROF(30,"Alg 1", "Prof"),ALG1_CHAP(32,"Alg 1", "Chap"),ALG2_PROF(29,"Alg 2", "Prof"),GEO_PROF(18,"Geom", "Prof"),CAHSEEHM(28,"", "Grad Prep");
        
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
}
