package hotmath.cm.exam;

import hotmath.cm.exam.FinalExam.QuizSize;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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

}
