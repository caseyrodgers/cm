package hotmath.testset.ha;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
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

	private CmStudentDao dao = new CmStudentDao();

	StudentModelI student;
    
    public EndOfProgramHandler() throws Exception {
    }
    
    public void loadStudent(final Connection conn, int userId) throws Exception {
        student = dao.getStudentModelBase(conn, userId, false);    	
    }
    
    /** Move this user to the next logical program.  This should 
     * completely move this user and return information identifying
     * the NEW program assigned.
     * 
     * This will move to the next program in a sequence. Each call will
     * move to the next program in sequence.
     * 
     * Throw Exception if error or program cannot be assigned.
     * 
     * @param conn
     * 
     * @return
     * @throws Exception
     */
    public StudentUserProgramModel getNextProgram(final Connection conn) throws Exception {
    	CmUserProgramDao upDao = new CmUserProgramDao();

    	StudentUserProgramModel programCurr = upDao.loadProgramInfoCurrent(conn,student.getUid());

    	if(programCurr.getTestDefId() == CmProgram.ESSENTIALS.getDefId()
    			|| programCurr.getTestDefId() == CmProgram.ESSENTIALS_V1.getDefId()){
    		updateProgram(conn, CmProgram.PREALG_PROF.getSubject(),CmProgram.PREALG_PROF.getProgramType(),null);                
    	}
    	else if(programCurr.getTestDefId() == CmProgram.PREALG_PROF.getDefId() 
    			|| programCurr.getTestDefId() == CmProgram.PREALG_PROF_V1.getDefId()) {
    		updateProgram(conn, CmProgram.ALG1_PROF.getSubject(),CmProgram.ALG1_PROF.getProgramType(),null);                
    	}
    	else if(programCurr.getTestDefId() == CmProgram.ALG1_PROF.getDefId() 
    			|| programCurr.getTestDefId() == CmProgram.ALG1_PROF_V1.getDefId() 
    			|| programCurr.getTestDefId() == CmProgram.ALG1_PROF_V2.getDefId()
    			|| programCurr.getTestDefId() == CmProgram.ALG1_PROF_V3.getDefId()) {
    		updateProgram(conn, CmProgram.GEOM_PROF.getSubject(),CmProgram.GEOM_PROF.getProgramType(),null);                
    	}
    	else if(programCurr.getTestDefId() == CmProgram.GEOM_PROF.getDefId()
    			|| programCurr.getTestDefId() == CmProgram.GEOM_PROF_V1.getDefId()) {
    		updateProgram(conn, CmProgram.ALG2_PROF.getSubject(), CmProgram.ALG2_PROF.getProgramType(),null);
    	}
    	else if(programCurr.getTestDefId() == CmProgram.ALG2_PROF.getDefId() || programCurr.getTestDefId() == CmProgram.ALG2_PROF_V1.getDefId()) {
    		updateProgram(conn, CmProgram.CAHSEEHM.getSubject(),CmProgram.CAHSEEHM.getProgramType(),null);
    	}
    	else if(programCurr.getTestDefId() == CmProgram.NATIONAL.getDefId() || programCurr.getTestDefId() == CmProgram.NATIONAL_V1.getDefId()) {
    		// reset/repeat
    		updateProgram(conn, CmProgram.ALG2_PROF.getSubject(), CmProgram.ALG2_PROF.getProgramType(),null);
    	}
    	else if( programCurr.getTestDefId() == CmProgram.CAHSEEHM.getDefId() 
    	        || programCurr.getTestDefId() == CmProgram.CAHSEEHM_V1.getDefId() ) {
    		// reset/repeat
    		updateProgram(conn, CmProgram.ALG2_PROF.getSubject(), CmProgram.ALG2_PROF.getProgramType(),null);
    	}  
    	
    	else if(programCurr.getTestDefId() == CmProgram.TAKS.getDefId()) {
    		// reset/repeat
    		updateProgram(conn, CmProgram.ALG2_PROF.getSubject(), CmProgram.ALG2_PROF.getProgramType(),null);
    	}            
    	else {
    		// if is a chapter test, then we must find the currently assigned
    		// chapter number.  If there are additional chapters, then move to 
    		// next in sequence.  Otherwise, move to the associated proficiency test

    		if(programCurr.getTestDefId() == CmProgram.PREALG_CHAP.getDefId()) {
    			setupChapterTest(conn, programCurr,CmProgram.PREALG_CHAP.getSubject());
    		}
    		else if(programCurr.getTestDefId() == CmProgram.ALG1_CHAP.getDefId()) {
    			setupChapterTest(conn, programCurr,CmProgram.ALG1_CHAP.getSubject());
    		}
    		else if(programCurr.getTestDefId() == CmProgram.ALG2_CHAP.getDefId()) {
    			setupChapterTest(conn, programCurr,CmProgram.ALG2_CHAP.getSubject());
    		}
    		else if(programCurr.getTestDefId() == CmProgram.GEOM_CHAP.getDefId()) {
    			setupChapterTest(conn, programCurr,CmProgram.GEOM_CHAP.getSubject());
    		}                
    		else {
    			throw new Exception("Unknown program: " + 
    					programCurr);
    		}
    	}

    	StudentUserProgramModel programNext = upDao.loadProgramInfoCurrent(conn, student.getUid());
    	return programNext;
    }
    
    
    /** Move to next chapter or if at end of chap list move to associated prof test
     * 
     * @param conn
     * @param program
     * @param subjId
     * @throws Exception
     */
    private void setupChapterTest(final Connection conn, StudentUserProgramModel program, String subjId) throws Exception {

        HaTestConfig config = program.getConfig();
        String chapter = config.getChapters().get(0);
        HaTestDefDao dao = new HaTestDefDao(); 
        List<String> chapters = dao.getProgramChapters(conn, dao.getTestDef(conn, program.getTestDefId()));
        
        String nextChapter = getNextChapter(chapters, chapter);
        if(nextChapter != null) {
            // modify the current program to next
            config.getChapters().clear();
            config.getChapters().add(nextChapter);
            
            // assign this new config to the program, by resetting 
            // the entire program.
            updateProgram(conn, subjId, "Chap", nextChapter);
        }
        else {
            updateProgram(conn, subjId, "Prof", null);
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

    private void updateProgram(final Connection conn, String subId, String progId, String chapter) throws Exception {
        student.getProgram().setProgramType(progId);
        student.getProgram().setSubjectId(subId);
        student.setChapter(chapter);
        student.setProgramChanged(true);
        
        dao.updateStudent(conn, student, true, false, true, false, false);
    }

}
