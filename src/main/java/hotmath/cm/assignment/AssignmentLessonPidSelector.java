package hotmath.cm.assignment;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.InmhItemData;
import hotmath.assessment.RppWidget;
import hotmath.gwt.cm_admin.server.model.CustomQuizQuestionManager;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;



public class AssignmentLessonPidSelector {

    static Logger __logger = Logger.getLogger(AssignmentLessonPidSelector.class);

    List<ProblemDto> problemsAll = new ArrayList<ProblemDto>();

    int MAX_PIDS = 20;
    int MAX_MULTI_CHOICE = 5;

    final int count[] = new int[1];

    final LessonModel lesson;

    public AssignmentLessonPidSelector(Connection conn, final String lessonName, String lessonFile, String subject) throws Exception {

        lesson = new LessonModel(lessonName, lessonFile);

        InmhItemData itemData = new InmhItemData(new INeedMoreHelpItem(CmResourceType.PRACTICE.label(), lessonFile, lessonName));
        try {
            List<RppWidget> rpps = itemData.getWidgetPool(conn, "assignment_pid");
            for (RppWidget w : rpps) {
                for (RppWidget ew : AssessmentPrescription.expandProblemSetPids(w)) {
                    problemsAll.add(new ProblemDto(0, 0, lesson, "", ew.getFile(), 0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        __logger.debug("Read RPPs: " + problemsAll.size());

        /**
         * Read any EPP problems for this lesson
         * 
         */
        String sql = "select * " + " from inmh_link " + " where link_type = 'cmextra' " + " and file = ? " + " order by id desc";

        List<ProblemDto> eppProblems = AssignmentDao.getInstance().getJdbcTemplate().query(sql, new Object[] { lessonFile }, new RowMapper<ProblemDto>() {
            @Override
            public ProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                String pid = rs.getString("link_key");
                pid = pid.split(":")[0]; // strip off any grade range
                return new ProblemDto(0, 0, lesson, "", pid, 0);
            }
        });

        __logger.debug("Read EPPs: " + eppProblems.size());

        for (ProblemDto p : eppProblems) {
            if (alreadyContains(problemsAll, p.getPid())) {
                problemsAll.add(p);
            }
        }

        /**
         * Custom Quiz problems
         * 
         * Add at most MAX_MULTI_CHOICE MC problems.
         * 
         * combine both custom quiz questions and normal quiz questions
         * 
         */
        CmList<QuizQuestion> cqQuestions = CustomQuizQuestionManager.getInstance().getQuestionsFor(conn, lessonFile, 999);
        __logger.debug("Read Custom Quiz MC: " + cqQuestions.size());

        Collection<ProblemDto> quizQuestions = new ArrayList<ProblemDto>();
        for (QuizQuestion qq : cqQuestions) {
            quizQuestions.add(new ProblemDto(0, 0, lesson, "", qq.getPid(), 0));
        }
        if (quizQuestions.size() <= MAX_MULTI_CHOICE) {
            // only if needed
            quizQuestions.addAll(getQuizProblems(MAX_MULTI_CHOICE, subject, lessonName));
            __logger.debug("Read Quiz MC: " + quizQuestions.size());
        }

        int numMcProbs = 0;
        for (ProblemDto qq : quizQuestions) {

            /**
             * if already have enough
             * 
             */
            if (problemsAll.size() >= MAX_PIDS) {
                break;
            }

            /**
             * Dummy problems are used to link up custom programs and should not
             * be included.
             */
            if (qq.getPid().startsWith("dummy")) {
                continue;
            }

            /**
             * If this pid does not already exist add it only take
             * MAX_MULTI_CHOICE
             */
            if (!alreadyContains(problemsAll, qq.getPid())) {
                problemsAll.add(qq);
                if ((++numMcProbs) + 1 > MAX_MULTI_CHOICE) {
                    break;
                }
            }
        }

        
        AssignmentDao.getInstance().updateProblemTypes(problemsAll);
        
        
        /** Sort so all Widget problems come first, then whiteboard then MC
         * 
         */
        Collections.sort(problemsAll, new Comparator<ProblemDto>() {
            @Override
            public int compare(ProblemDto o1, ProblemDto o2) {
                ProblemType p1 = o1.getProblemType();
                ProblemType p2 = o2.getProblemType();
                if(p1 == p2) {
                    return 0;
                }
                
                if(p1 == ProblemType.INPUT_WIDGET && p2 != ProblemType.INPUT_WIDGET) {
                    return -1;
                }
                else if(p2 == ProblemType.INPUT_WIDGET && p1 != ProblemType.INPUT_WIDGET) {
                    return 1;
                }
                else if(p1 == ProblemType.WHITEBOARD && p2 == ProblemType.MULTI_CHOICE) {
                    return -1;
                }
                else if(p2 == ProblemType.WHITEBOARD && p1 == ProblemType.MULTI_CHOICE) {
                    return 1;
                }
                else if(p1 == ProblemType.UNKNOWN) {
                    return 1;
                }
                else if(p2 == ProblemType.UNKNOWN) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });



        /**
         * Add the label to each
         * 
         */
        int pCount = 0;
        for (ProblemDto p : problemsAll) {
            p.setLabel(getDefaultLabel(lessonName, (++pCount)));
        }

        

        /** Add any custom problem associated with this lesson 
         * 
         */
        List<ProblemDto> customProblems = CustomProblemDao.getInstance().getCustomProblemsLinkedToLesson(lessonFile);
        AssignmentDao.getInstance().updateProblemTypes(customProblems);
        pCount=0;
        for (ProblemDto p : customProblems) {
            p.setLabel(getDefaultLabel(lessonName + " [cust]", (++pCount)));
        }
        problemsAll.addAll(customProblems);
    }

    private boolean alreadyContains(List<ProblemDto> problemsAll2, String pid) {
        for (ProblemDto d : problemsAll2) {
            if (d.getPid().equals(pid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return at most numToGet quiz/MC problems. May return less than requested.
     * 
     * @param numToGet
     * @param subject
     * @param lessonName
     * @return
     * @throws Exception
     */
    private Collection<ProblemDto> getQuizProblems(int numToGet, final String subject, final String lessonName) throws Exception {
        /**
         * Add the MC, quiz questions
         * 
         */
        String sql = "";
        if (subject != null) {
            sql = "select * from HA_PROGRAM_LESSONS_static where lesson = ? and subject = ? order by rand()";
        } else {
            sql = "select * from HA_PROGRAM_LESSONS_static where lesson = ? OR subject = ? order by rand()"; // get
            // all
        }
        List<ProblemDto> problems = AssignmentDao.getInstance().getJdbcTemplate().query(sql, new Object[] { lessonName, subject }, new RowMapper<ProblemDto>() {
            @Override
            public ProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                String defaultLabel = getDefaultLabel(lessonName, (++count[0]));
                return new ProblemDto(0, 0, lesson, defaultLabel, rs.getString("pid"), 0);
            }
        });
        if (problems.size() < numToGet) {
            return problems;
        } else {
            return problems.subList(0, numToGet);
        }
    }

    public List<ProblemDto> getProblems() {
        return problemsAll;
    }

    private String getDefaultLabel(String lesson, int i) {
        String filler = "";
        if (i < 10) {
            filler = " ";
        }
        return lesson + ": " + filler + i;
    }

}
