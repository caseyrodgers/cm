package hotmath.cm.assignment;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.InmhItemData;
import hotmath.assessment.RppWidget;
import hotmath.gwt.cm_admin.server.model.CustomQuizQuestionManager;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.inmh.INeedMoreHelpItem;

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

    int MAX_PIDS=20;
    int MAX_MULTI_CHOICE=5;
    
    final int count[] = new int[1];
    
    final LessonModel lesson;
    
    public AssignmentLessonPidSelector(Connection conn, final String lessonName, String lessonFile, String subject) throws Exception {
        

        lesson = new LessonModel(lessonName, lessonFile);

        InmhItemData itemData = new InmhItemData(new INeedMoreHelpItem("practice", lessonFile, lessonName));
        try {
            List<RppWidget> rpps = itemData.getWidgetPool(conn, "assignment_pid");
            for (RppWidget w : rpps) {
                for (RppWidget ew : AssessmentPrescription.expandProblemSetPids(w)) {
                    String defaultLabel = getDefaultLabel(lessonName, (++count[0]));
                    problemsAll.add(new ProblemDto(0, 0, lesson, defaultLabel, ew.getFile(), 0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        __logger.debug("Read RPPs: " + problemsAll.size());



        /** Read any EPP problems for this lesson
         * 
         */
        String sql = "select * " +
                     " from inmh_link " +
                     " where link_type = 'cmextra' " +
                     " and file = ? " +
                     " order by id desc";
        
        List<ProblemDto> eppProblems = AssignmentDao.getInstance().getJdbcTemplate().query(sql, new Object[] { lessonFile}, new RowMapper<ProblemDto>() {
            @Override
            public ProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                String defaultLabel = getDefaultLabel(lessonName, (++count[0]));
                
                String pid = rs.getString("link_key");
                pid = pid.split(":")[0];  // strip off any grade range
                return new ProblemDto(0, 0, lesson, defaultLabel, pid, 0);
            }
        });
        
        __logger.debug("Read EPPs: " + eppProblems.size());
        
        // don't allow dups between EPP and RPP
        boolean found=false;
        for(ProblemDto p: eppProblems) {
            for(ProblemDto p2: problemsAll) {
                if(p2.getPid().equals(p.getPid())) {
                    found=true;
                    break;
                }
            }
            if(!found) {
                problemsAll.add(p);
            }
        }
        
        
        /** Custom Quiz problems
         * 
         * Add at most MAX_MULTI_CHOICE MC problems.
         * 
         * combine both custom quiz questions and normal quiz questions
         * 
         */
        CmList<QuizQuestion> cqQuestions = CustomQuizQuestionManager.getInstance().getQuestionsFor(conn, lessonFile, 999);
        __logger.debug("Read Custom Quiz MC: " + cqQuestions.size());

        Collection<ProblemDto> quizQuestions=new ArrayList<ProblemDto>();
        for(QuizQuestion qq: cqQuestions) {
            quizQuestions.add(new ProblemDto(0, 0, lesson, "", qq.getPid(), 0));            
        }

        if(quizQuestions.size() <= MAX_MULTI_CHOICE) {
            // only if needed
            quizQuestions.addAll(getQuizProblems(MAX_MULTI_CHOICE, subject, lessonName));
            __logger.debug("Read Quiz MC: " + quizQuestions.size());
        }
        
        
        int numMcProbs=0;
        for(ProblemDto qq: quizQuestions) {
            if(problemsAll.size() >= MAX_PIDS) {
                break;
            }
            
            if(!alreadyContains(problemsAll, qq.getPid())) {
                qq.setLabel(getDefaultLabel(lessonName, (++count[0])));
                problemsAll.add(qq);
                if((++numMcProbs)+1 > MAX_MULTI_CHOICE) {
                    break;
                }
            }
        }
        
        
        AssignmentDao.getInstance().updateProblemTypes(problemsAll);

        /**
         * Try to select only problems with widgets first
         * 
         */
        List<ProblemDto> problemsFiltered = new ArrayList<ProblemDto>();
        for (ProblemDto pt : problemsAll) {
            if (pt.getProblemType() != ProblemType.WHITEBOARD) {
                problemsFiltered.add(pt);
            }
        }
        /**
         * Then add any whiteboard only questions to bottom
         * 
         * Skip MULTI_CHOICE ..
         * 
         */
        for (ProblemDto pt : problemsAll) {
            if (!problemsFiltered.contains(pt)) {
                if(pt.getProblemType() != ProblemType.MULTI_CHOICE) {
                    problemsFiltered.add(pt);
                }
            }
        }

        
        /** Now put them back and disregard any dummy problems
         *  used for custom programs.   Dummy problems are used
         *  to link up custom programs and should not be included.
         */
        problemsAll.clear();
        for (ProblemDto pt : problemsFiltered) {
            if(!pt.getPid().startsWith("dummy")) {
                problemsAll.add(pt);
            }
        }
        
        /**
         * Then make sure they are sorted
         * 
         */
        Collections.sort(problemsAll, new Comparator<ProblemDto>() {
            @Override
            public int compare(ProblemDto o1, ProblemDto o2) {
                String label1 = o1.getLabelWithType();
                String label2 = o2.getLabelWithType();
                return label1.compareTo(label2);
            }
        });
    }


    private boolean alreadyContains(List<ProblemDto> problemsAll2, String pid) {
        for(ProblemDto d: problemsAll2) {
            if(d.getPid().equals(pid)) {
                return true;
            }
        }
        return false;
    }


    /** Return at most numToGet quiz/MC problems.  May return less than requested.
     * 
     * @param numToGet
     * @param subject
     * @param lessonName
     * @return
     * @throws Exception
     */
    private Collection<ProblemDto> getQuizProblems(int numToGet, final String subject, final String lessonName) throws Exception {
        /** Add the MC, quiz questions
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
        if(problems.size() < numToGet) {
            return problems;
        }
        else {
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
