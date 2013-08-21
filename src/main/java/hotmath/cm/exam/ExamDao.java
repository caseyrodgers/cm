package hotmath.cm.exam;

import hotmath.cm.exam.FinalExam.QuizSize;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import sb.util.SbUtilities;

public class ExamDao extends SimpleJdbcDaoSupport {

    final static Logger __logger = Logger.getLogger(ExamDao.class);

    static private ExamDao __instance;

    static public ExamDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (ExamDao) SpringManager.getInstance().getBeanFactory().getBean(ExamDao.class.getName());
        }
        return __instance;
    }

    private ExamDao() {/* empty */
    }

    
    /** Return a list of lists where each list represents an alternate quiz
     * 
     * @param testDef
     * @param quizSize
     * @return
     * @throws Exception
     */
    public List<List<String>> getTestIdsForAllAlternates(HaTestDef testDef, QuizSize quizSize) throws Exception {
        List<List<String>> ids = new ArrayList<List<String>>();

        String cacheKey = testDef.getTestDefId() + "_" + quizSize.toString();
        List<List<String>> lists = (List<List<String>>)CmCacheManager.getInstance().retrieveFromCache(CacheName.QUIZ_ALTERNATES, cacheKey);
        if(lists != null) {
            return lists;
        }
        
        int alternates = testDef.getNumAlternateTests();
        for (int a = 0; a < alternates; a++) {
            ids.add(HaTestDefDao.getInstance().getTestIdsBasic(testDef.getTextCode(), "coursetest", a, 0, 60, new HaTestConfig()));
        }
        CmCacheManager.getInstance().addToCache(CacheName.QUIZ_ALTERNATES,cacheKey,ids);
        return ids;
    }
    
    public List<LessonModel> getLessonsForProblem(String pid) {
        String sql = "select lesson, file from HA_PROGRAM_LESSONS_static where pid = ? order by lesson"; 
    
        List<LessonModel> models = getJdbcTemplate().query(sql, new Object[] { pid }, new RowMapper<LessonModel>() {
            @Override
            public LessonModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new LessonModel(rs.getString("lesson"), rs.getString("file"));
            }
        });
        return models;
    }

    public String getAlternateProblem_MultiChoice(HaTestDef testDef, String pid) throws Exception {
        List<List<String>> idLists = ExamDao.getInstance().getTestIdsForAllAlternates(testDef,QuizSize.SIXTY);
        
        for(List<String> ids: idLists) {
            for(int i=0;i<ids.size();i++) {
                String p = ids.get(i);
                if(p.equals(pid)) {
                    int rand = SbUtilities.getRandomNumber(idLists.size());
                    String newPid = idLists.get(rand).get(i);
                    return newPid;
                }
            }
        }
        return null;
    }
}
