package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;

import java.io.File;
import java.sql.Connection;
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
    
    
    public CmList<QuizQuestion> getQuestionsFor(final Connection conn, String lessonFile, int gradeLevel) throws Exception {
        

//                  QuizQuestionParsed quizQuestion = getQuestionHtml(conn, num, pid);
//                  quizHtml = quizQuestion.getStatement();
//                  correctAnswer = quizQuestion.getCorrectAnswer();
//              }
//              num++;
//              
//              list.add(new QuizQuestion(questionId,lessonFile, rs.getString("program_name"), pid, quizHtml, correctAnswer));
//          }        
        
        
        CmList<QuizQuestion> retData = new CmArrayList<QuizQuestion>();
        CustomQuizLessonInfo lessonInfo = data.get(lessonFile);
        if(lessonInfo == null) {
            __logger.warn("Could not find lesson info : " + lessonFile);
        }
        else {
            for(String pid: lessonInfo.pids) {
                
                QuizQuestionParsed quizQuestion = CmQuizzesDao.getInstance().getQuestionHtml(conn, 0, pid);
                
                QuizQuestion quizQuesion = new QuizQuestion("ques_id", lessonFile,"Prog Name",pid, quizQuestion.getStatement(), 1);
                retData.add(quizQuesion);
            }
        }
        return retData;
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