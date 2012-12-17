package hotmath.gwt.cm_admin.server.model;

import hotmath.ProblemID;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    
    Map<String, CustomQuizLessonInfo> data = new HashMap<String,CustomQuizLessonInfo>();
    
    protected CustomQuizQuestionManager() throws Exception {
        readInFile();
    }
    
    
    /** file is absolute matching of lesson and pids (not ranges)
     * 
     */
    private void readInFile() throws Exception{
        SbFile file = new SbFile(new File(CatchupMathProperties.getInstance().getCatchupRuntime(), "all.custom_quiz"));
        
        String contents = file.getFileContents().toString("\n");
        String lines[] = contents.split("\n");
        
        data.clear();
        for(String line: lines) {
            if(line.startsWith("#")) {
                continue;
            }
            String lesson = SbUtilities.getToken(line, 1, "\t").trim();
            String pid = SbUtilities.getToken(line, 2, "\t").trim();
            
            CustomQuizLessonInfo ci = data.get(lesson);
            if(ci == null) {
                ci = new CustomQuizLessonInfo();
                data.put(lesson,ci);
            }
            ci.pids.add(pid);
        }
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
        CustomQuizLessonInfo lessonInfo = data.get(lessonFile);
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
        return data.containsKey(lesson);
    }

}



class CustomQuizLessonInfo {
    List<String> pids = new ArrayList<String>();
    
    public CustomQuizLessonInfo() {
    }
}