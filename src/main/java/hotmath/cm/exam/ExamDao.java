package hotmath.cm.exam;

import hotmath.cm.exam.FinalExam.QuizSize;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

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

    public List<List<String>> getTestIdsForAllAlternates(HaTestDef testDef, QuizSize quizSize) throws Exception {

        StudentUserProgramModel up = new StudentUserProgramModel();
        up.setTestDef(testDef);

        List<List<String>> ids = new ArrayList<List<String>>();

        int alternates = testDef.getNumAlternateTests();
        for (int a = 0; a < alternates; a++) {
            ids.add(HaTestDefDao.getInstance().getTestIdsBasic(testDef.getTextCode(), "coursetest", a, 0, 60, new HaTestConfig()));
        }

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
}
