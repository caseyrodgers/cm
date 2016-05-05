package hotmath.gwt.cm_admin.server.model;

import hotmath.ProblemID;
import hotmath.SolutionManager;
import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.solution.Solution;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sb.util.SbFile;
import sb.util.SbUtilities;

public class CustomQuizQuestionManager {
    
    static Logger __logger = Logger.getLogger(CustomQuizQuestionManager.class);
    static CustomQuizQuestionManager __instance;

    public static CustomQuizQuestionManager getInstance() {
        if(__instance == null) {
            try {
                __instance = new CustomQuizQuestionManager();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return __instance;
    }
    
    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {
            @Override
            public void flush() {
                __instance = null;
            }
        });
    }
    
    
    Map<String, CustomQuizLessonInfo> _dataMap = new HashMap<String,CustomQuizLessonInfo>();
    
    protected CustomQuizQuestionManager() throws Exception {
        readInTable();
    }

    private void readInTable() throws Exception {
    	Connection conn=null;
    	PreparedStatement ps=null;
    	try {
    		conn = HMConnectionPool.getConnection();
    		
    		String sql = "select problemindex from SOLUTIONS where problemindex like ? and active = 1";
            ps = conn.prepareStatement(sql);
            
    		ResultSet rs = conn.createStatement().executeQuery("select * from CUSTOM_QUIZ_LESSON_PIDS order by lesson, pid");
    		while(rs.next()) {
    			String lesson = rs.getString("lesson");
    			String pid = rs.getString("pid");
    			//String activePidSearch = getActivePid(pid);
                //ps.setString(1,  activePidSearch);
                //ResultSet  rsPid = ps.executeQuery();
                if(pid != null) {
                    /** only active problems
                     * 
                     */
                    String activePid = pid; // rsPid.getString("problemindex");
                    
                    CustomQuizLessonInfo ci = _dataMap.get(lesson);
                    if(ci == null) {
                        ci = new CustomQuizLessonInfo();
                        _dataMap.put(lesson,ci);
                        
                        System.out.println("adding pid: " + lesson + ". " + activePid);
                       
                    }
                    ci.pids.add(activePid);
                }
                else {
                    __logger.warn("could not find active problem for pid: " + pid);
                }
    		}
		} 
    	finally {
    		SqlUtilities.releaseResources(null, null, conn);
    	}

    }
    
    /** replace the page with wild, which will return the latest (active)
     * 
     * @param pid
     * @return
     */
    private String getActivePid(String pid) {
        String p[] = pid.split("\\_");
        p[5] = "%";
        
        String sout = "";
        for(String p2: p) {
            if(sout.length() > 0) {
                sout += "_";
            }
            sout += p2;
        }
        return sout;
    }


    /** Get all active questions for this lesson.
     * 
     *  Return all active lesson
     * 
     * @param conn
     * @param lessonFile
     * @param gradeLevel
     * @return
     * @throws Exception
     */
    public CmList<QuizQuestion> getQuestionsFor(final Connection conn, String lessonFile, int gradeLevel) throws Exception {
        
        CmList<QuizQuestion> retData = new CmArrayList<QuizQuestion>();
        CustomQuizLessonInfo lessonInfo = _dataMap.get(lessonFile);
        if(lessonInfo == null) {
            __logger.warn("Could not find lesson info : " + lessonFile);
        }
        else {
            for(String pid: lessonInfo.pids) {
                List<String> activePids = getActivePids(conn, pid);
                
                for(String activePid: activePids) {
                    QuizQuestionParsed quizQuestion = CmQuizzesDao.getInstance().getQuestionHtml(conn, 0, activePid);
                
                    QuizQuestion quizQuesion = new QuizQuestion("ques_id", lessonFile,"Prog Name",activePid, quizQuestion.getStatement(), 1);
                    retData.add(quizQuesion);
                }
            }
        }
        return retData;
    }

    public List<LessonModel> getLessonsInCustomQuiz(final Connection conn, String pid) throws Exception {
    	List<LessonModel> matches = new ArrayList<LessonModel>(); 
        Set<String> keySet = _dataMap.keySet();
        for(String lesson: keySet) {
        	CustomQuizLessonInfo lessonInfo = _dataMap.get(lesson);
        	for(String lessonPid: lessonInfo.pids) {
        		if(lessonPid.equals(pid)) {
        			matches.add(new LessonModel(AssignmentDao.getInstance().getLessonTitle(conn, lesson), lesson));
        			break;
        		}
        	}
        }
        return matches;
    }
    
    public List<LessonModel> getLessonsInCustomQuizList(final Connection conn, List<String> pidList) throws Exception {
    	List<LessonModel> matches = new ArrayList<LessonModel>(); 
        Set<String> keySet = _dataMap.keySet();
        for(String lesson: keySet) {
        	CustomQuizLessonInfo lessonInfo = _dataMap.get(lesson);
        	for(String lessonPid: lessonInfo.pids) {
        		if(pidList.contains(lessonPid)) {
        			matches.add(new LessonModel(AssignmentDao.getInstance().getLessonTitle(conn, lesson), lesson));
        		}
        	}
        }
        return matches;
    }

    private List<String> getActivePids(Connection conn, String pid) throws Exception {
       PreparedStatement ps=null;
       List<String> pids = new ArrayList<String>();
       try {
           ProblemID pidO = new ProblemID(pid);
           String pid1 = pidO.getBook() + "\\_" + pidO.getChapter() + "\\_" + pidO.getSection() + "\\_" + pidO.getProblemSet() + "\\_" + pidO.getProblemNumber() + "\\_%";
           String sql = "select problemindex from SOLUTIONS where problemindex like ? and active = 1 order by pagenumber";
           ps = conn.prepareStatement(sql);
           ps.setString(1, pid1);
           ResultSet rs = ps.executeQuery();
           while(rs.next()) {
               pids.add(rs.getString("problemindex"));
           }
       }
       finally {
           SqlUtilities.releaseResources(null,  ps, null);
       }
       
       if(pids.size() == 0) {
           __logger.error("NO active pids found for '" + pid + "'");
       }
       return pids;
    }


    public boolean isDefined(String lesson) {
        return _dataMap.containsKey(lesson);
    }
    
    
    public static void main(String[] args) {
    	Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
			CmList<QuizQuestion> pids = CustomQuizQuestionManager.getInstance().getQuestionsFor(conn,  "topics/adding-and-subtracting-fractions.html",1);
			for(QuizQuestion p: pids) {
				System.out.println(p.getPid());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
			System.exit(0);
		}
	}

}




class CustomQuizLessonInfo {
    List<String> pids = new ArrayList<String>();
    
    public CustomQuizLessonInfo() {
    }
}