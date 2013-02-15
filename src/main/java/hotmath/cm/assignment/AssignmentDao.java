package hotmath.cm.assignment;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.InmhItemData;
import hotmath.assessment.RppWidget;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.PropertyLoadFileException;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.model.AssignmentStatus;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.LessonDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentLessonDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class AssignmentDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(AssignmentDao.class);

    static private AssignmentDao __instance;

    static public AssignmentDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (AssignmentDao) SpringManager.getInstance().getBeanFactory().getBean(AssignmentDao.class.getName());
        }
        return __instance;
    }

    private AssignmentDao() {
        /** empty */
    }

    public int saveAssignment(final int aid, final Assignment ass) {
        /**
         * insert or update the new Assignment record and save the key
         * 
         * make sure to set the assignKey[0] variable to assign_key
         */

        if (ass.getStatus() == null) {
            ass.setStatus("Open"); // default status
        }

        final int assignKey[] = new int[1];
        if (ass.getAssignKey() == 0) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            /**
             * is new
             * 
             */
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "insert into CM_ASSIGNMENT(aid,group_id,name,due_date,comments,last_modified,status,is_draft_mode)values(?,?,?,?,?,?,?,?)";
                    PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, aid);
                    ps.setInt(2, ass.getGroupId());
                    ps.setString(3, ass.getAssignmentName());
                    ps.setDate(4, new java.sql.Date(ass.getDueDate().getTime()));
                    ps.setString(5, ass.getComments());
                    ps.setDate(6, new Date(System.currentTimeMillis()));
                    ps.setString(7, ass.getStatus());
                    ps.setInt(8, ass.isDraftMode() ? 1 : 0);
                    return ps;
                }
            }, keyHolder);
            assignKey[0] = keyHolder.getKey().intValue();
        } else {
            /**
             * is update
             * 
             */
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "update CM_ASSIGNMENT set aid = ?, name = ?, due_date = ?, comments = ?, last_modified = now(), status = ?, is_draft_mode = ? where assign_key = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, aid);
                    ps.setString(2, ass.getAssignmentName());
                    ps.setDate(3, new java.sql.Date(ass.getDueDate().getTime()));
                    ps.setString(4, ass.getComments());
                    ps.setString(5, ass.getStatus());
                    ps.setInt(6, ass.isDraftMode() ? 1 : 0);
                    ps.setInt(7, ass.getAssignKey());

                    return ps;
                }
            });
            assignKey[0] = ass.getAssignKey();
        }
        final int assKey = assignKey[0];

        deleteAssignmentPids(assKey);

        /**
         * save the PIDS contained in this Assignment
         * 
         */
        if (ass.getPids() != null && ass.getPids().size() > 0) {
            String sqlPids = "insert into CM_ASSIGNMENT_PIDS(assign_key, pid, label, lesson)values(?,?,?,?)";
            getJdbcTemplate().batchUpdate(sqlPids, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, assKey);
                    ProblemDto p = ass.getPids().get(i);

                    String pidParts[] = p.getPid().split("\\$");
                    String fullPid = pidParts[0];
                    if (pidParts.length > 1) {
                        try {
                            fullPid = fullPid + "$" + SolutionDao.getInstance().getGlobalSolutionContextNewest(pidParts[0], pidParts[1]);
                        } catch (Exception e) {
                            __logger.error("Error getting global context name", e);
                        }
                    }
                    ps.setString(2, fullPid);
                    ps.setString(3, p.getLabel());
                    ps.setString(4, p.getLesson());
                }

                @Override
                public int getBatchSize() {
                    return ass.getPids().size();
                }
            });
        }

        return assKey;

    }

    /**
     * Fetch a persisted Assignment from DB
     * 
     * @param assKey
     * @return
     */
    public Assignment getAssignment(int assKey) throws AssignmentNotFoundException {

        String sql = "select * from CM_ASSIGNMENT where assign_key = ?";
        Assignment assignment = null;
        try {
            assignment = getJdbcTemplate().queryForObject(sql, new Object[] { assKey }, new RowMapper<Assignment>() {
                @Override
                public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {

                    Date dueDate = rs.getDate("due_date");
                    return new Assignment(rs.getInt("is_draft_mode") == 1, rs.getInt("assign_key"), rs.getInt("group_id"), rs.getString("name"), rs
                            .getString("comments"), dueDate, null, null, rs.getString("status"));
                }
            });
        } catch (Exception e) {
            throw new AssignmentNotFoundException(assKey, e);
        }

        /**
         * now read all pids assigned to this Assignment
         * 
         */
        final int count[] = new int[1];
        sql = "select * from CM_ASSIGNMENT_PIDS where assign_key = ? order by id";
        List<ProblemDto> pids = getJdbcTemplate().query(sql, new Object[] { assKey }, new RowMapper<ProblemDto>() {
            @Override
            public ProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                String pid = rs.getString("pid");
                return new ProblemDto(0, rs.getString("lesson"), rs.getString("label"), pid, null, 0);
            }
        });
        CmList<ProblemDto> cmPids = new CmArrayList<ProblemDto>();
        cmPids.addAll(pids);
        assignment.setPids(cmPids);

        updateProblemTypes(assignment.getPids());

        return assignment;
    }

    /**
     * Set the appropriate problem types for all problems in assignment. THis is
     * dynamic because the type can be changed by the author at any time.
     * 
     * 
     * @param assignment
     */
    private void updateProblemTypes(final List<ProblemDto> problems) {

        if (problems.size() == 0) {
            return;
        }

        String list = "";
        for (ProblemDto problem : problems) {
            if (list.length() > 0) {
                list += ",";
            }

            list += "'" + problem.getPidOnly() + "'";
        }

        String sql = "select problemindex, solutionxml from SOLUTIONS where problemindex in (" + list + ")";
        getJdbcTemplate().query(sql, new Object[] {}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String pid = rs.getString("problemindex");
                String xml = rs.getString("solutionxml");

                boolean found = false;
                for (ProblemDto problem : problems) {
                    if (problem.getPidOnly().equals(pid)) {
                        problem.setProblemType(determineProblemType(xml));
                        found = true;
                    }
                }
                assert (found);
                return "OK";
            }
        });

    }

    public AssignmentLessonData getAssignmentLessonData() {

        final AssignmentLessonData data = new AssignmentLessonData();

        /**
         * get list of each prof program *
         * 
         */
        String sql = "select * from HA_TEST_DEF where is_active  = 1 and prog_id = 'Prof' order by load_order";
        List<String> pids = getJdbcTemplate().query(sql, new Object[] {}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                data.getSubjects().add(new SubjectDto(0, rs.getInt("test_def_id"), rs.getString("test_name"), rs.getString("test_name")));
                return "";
            }
        });

        return data;

    }

    /**
     * Return list of problems associated with named lesson.
     * 
     * At most MAX_PROBLEMS (12) are returned
     * 
     * Makes sure that solutions with widgets are returned first
     * 
     * 
     * @param conn
     * @param lesson
     * @param gradeLevel
     * @return
     */
    public List<ProblemDto> getLessonProblemsFor(Connection conn, final String lessonName, String lessonFile, String subject) {

        int MAX_PROBLEMS = 12;

        final int count[] = new int[1];

        String sql = "select * from HA_PROGRAM_LESSONS_static where lesson = ? and subject = ? order by id";

        InmhItemData itemData = new InmhItemData(new INeedMoreHelpItem("practice", lessonFile, lessonName));
        List<ProblemDto> problemsAll = new ArrayList<ProblemDto>();
        try {
            List<RppWidget> rpps = itemData.getWidgetPool(conn, "assignment_pid");
            for (RppWidget w : rpps) {
                for (RppWidget ew : AssessmentPrescription.expandProblemSetPids(w)) {
                    String defaultLabel = getDefaultLabel(lessonName, (++count[0]));
                    problemsAll.add(new ProblemDto(0, lessonName, defaultLabel, ew.getFile(), null, 0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProblemDto> problems = getJdbcTemplate().query(sql, new Object[] { lessonName, subject }, new RowMapper<ProblemDto>() {
            @Override
            public ProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {

                String defaultLabel = getDefaultLabel(lessonName, (++count[0]));

                return new ProblemDto(0, rs.getString("lesson"), defaultLabel, rs.getString("pid"), null, 0);
            }
        });

        problemsAll.addAll(problems);

        updateProblemTypes(problemsAll);

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
         */
        for (ProblemDto pt : problemsAll) {
            if (!problemsFiltered.contains(pt)) {
                problemsFiltered.add(pt);
            }
        }

        return problemsFiltered;
    }

    private String getDefaultLabel(String lesson, int i) {
        return lesson + ": #" + i;
    }

    public List<Assignment> getAssignments(int aid2, int groupId) throws PropertyLoadFileException {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENTS_FOR_GROUP");

        List<Assignment> problems = getJdbcTemplate().query(sql, new Object[] { groupId }, new RowMapper<Assignment>() {
            @Override
            public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {

                // create a psudo name
                String comments = rs.getString("comments");

                Date dueDate = rs.getDate("due_date");

                String assignmentName = _createAssignmentName(dueDate, comments);

                Assignment ass = new Assignment(rs.getInt("is_draft_mode") == 1, rs.getInt("assign_key"), rs.getInt("group_id"), assignmentName, rs
                        .getString("comments"), dueDate, null, null, rs.getString("status"));

                ass.setProblemCount(rs.getInt("problem_count"));
                return ass;
            }
        });
        return problems;
    }

    /**
     * delete the PIDS contained in this Assignment
     * 
     */
    private void deleteAssignmentPids(final int assKey) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from CM_ASSIGNMENT_PIDS where assign_key = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, assKey);
                return ps;
            }
        });
    }

    public void deleteAssignment(final int assKey) {

        deleteAssignmentPids(assKey);

        /**
         * Delete the assignment record
         * 
         */
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from CM_ASSIGNMENT where assign_key = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, assKey);
                return ps;
            }
        });
    }

    public CmList<StudentAssignment> getAssignmentGradeBook(final int assignKey) throws Exception {
        CmList<StudentAssignment> stuAssignments = new CmArrayList<StudentAssignment>();
        Assignment assignment = getAssignment(assignKey);

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_GRADE_BOOK_DATA_3");

        /**
         * get assignment problem status list for all users
         */
        final Map<Integer, String> nameMap = new HashMap<Integer, String>();
        List<StudentProblemDto> problemStatuses = getJdbcTemplate().query(sql, new Object[] { assignKey, assignKey, assignKey },
                new RowMapper<StudentProblemDto>() {
                    @Override
                    public StudentProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Integer uid = rs.getInt("uid");
                        if (!nameMap.containsKey(uid)) {
                            nameMap.put(uid, rs.getString("user_name"));
                        }

                        ProblemDto probDto = new ProblemDto(rs.getInt("problem_id"), rs.getString("lesson"), rs.getString("label"), rs.getString("pid"), null,
                                0);

                        boolean hasShowWork = rs.getInt("has_show_work") != 0;
                        boolean hasShowWorkAdmin = rs.getInt("has_show_work_admin") != 0;
                        StudentProblemDto prob = new StudentProblemDto(uid, probDto, rs.getString("status"), hasShowWork, hasShowWorkAdmin);

                        prob.setIsGraded((rs.getInt("is_graded") > 0) ? "Yes" : "No");

                        return prob;
                    }
                });

        /**
         * create student assignments for all users
         */
        int uid = 0;
        CmList<StudentProblemDto> probList = null;
        Map<Integer, StudentAssignment> stuAssignMap = new HashMap<Integer, StudentAssignment>();
        for (StudentProblemDto probDto : problemStatuses) {
            if (probDto.getUid() != uid) {
                probList = new CmArrayList<StudentProblemDto>();
                uid = probDto.getUid();
                StudentAssignment stuAssignment = new StudentAssignment(uid, assignment, probList);
                stuAssignment.setStudentName(nameMap.get(uid));
                stuAssignments.add(stuAssignment);
                stuAssignMap.put(uid, stuAssignment);
            }
            probList.add(probDto);
        }

        /**
         * add lesson status for each user/lesson add assignment status
         */
        uid = 0;
        String lessonName = "";
        int completed = 0;
        int pending = 0;
        int count = 0;
        int viewed = 0;
        int totGraded = 0;
        int totCompleted = 0;
        int totPending = 0;
        int totCount = 0;
        int totCorrect = 0;
        int totIncorrect = 0;
        int totViewed = 0;

        CmList<StudentLessonDto> lessonList = null;
        StudentLessonDto lessonStatus = null;

        for (StudentProblemDto probDto : problemStatuses) {

            if (probDto.getUid() != uid) {
                if (lessonStatus != null) {
                    lessonStatus.setStatus(getLessonStatus(count, completed, pending, viewed));
                    stuAssignMap.get(uid).setHomeworkStatus(getHomeworkStatus(totCount, totCompleted, totPending, totGraded, totViewed));
                    stuAssignMap.get(uid).setHomeworkGrade(getHomeworkGrade(totCount, totCorrect, totIncorrect));
                }
                lessonName = "";
                totCount = 0;
                totCorrect = 0;
                totIncorrect = 0;
                totCompleted = 0;
                totPending = 0;
                totGraded = 0;
                totViewed = 0;
                uid = probDto.getUid();
                lessonList = new CmArrayList<StudentLessonDto>();
                stuAssignMap.get(uid).setLessonStatuses(lessonList);
            }

            if (!lessonName.equals(probDto.getProblem().getLesson())) {
                if (lessonName.trim().length() > 0) {
                    if (lessonStatus != null) {
                        lessonStatus.setStatus(getLessonStatus(count, completed, pending, viewed));
                    }
                }
                completed = 0;
                pending = 0;
                count = 0;
                viewed = 0;
                lessonName = probDto.getProblem().getLesson();
                lessonStatus = new StudentLessonDto(uid, lessonName, null);
                lessonList.add(lessonStatus);
            }

            count++;
            totCount++;
            String probStatus = probDto.getStatus().trim();
            if ("not viewed".equalsIgnoreCase(probStatus))
                continue;

            // TODO: is "answered" an actual status?
            if ("answered".equalsIgnoreCase(probStatus) || "correct".equalsIgnoreCase(probStatus) || "incorrect".equalsIgnoreCase(probStatus)) {
                completed++;
                totCompleted++;
                totGraded += ("yes".equalsIgnoreCase(probDto.getIsGraded())) ? 1 : 0;
                if (probStatus.toLowerCase().indexOf("orrect") > 0)
                    probDto.setIsGraded("Yes");
                totCorrect += ("correct".equalsIgnoreCase(probStatus)) ? 1 : 0;
                totIncorrect += ("incorrect".equalsIgnoreCase(probStatus)) ? 1 : 0;
                continue;
            }
            if ("pending".equalsIgnoreCase(probStatus)) {
                pending++;
                totPending++;
            } else if ("viewed".equalsIgnoreCase(probStatus)) {
                viewed++;
                totViewed++;
            }
        }

        if (lessonStatus != null) {
            lessonStatus.setStatus(getLessonStatus(count, completed, pending, viewed));
        }
        if (stuAssignMap.size() > 0) {
            stuAssignMap.get(uid).setHomeworkStatus(getHomeworkStatus(totCount, totCompleted, totPending, totGraded, totViewed));
            stuAssignMap.get(uid).setHomeworkGrade(getHomeworkGrade(totCount, totCorrect, totIncorrect));
        }

        if (__logger.isDebugEnabled())
            __logger.debug("getAssignmentGradeBook(): stuAssignments.size(): " + stuAssignments.size());

        /**
         * Grade was not being set, so I generalized it a bit.
         * 
         * Also, really just want an aggregate .. values for each lesson is too
         * much.
         * 
         * 
         */
        for (StudentAssignment sa : stuAssignments) {
            setStudentDetailStatus(sa);

            for (StudentProblemDto sd : sa.getAssigmentStatuses()) {

                /**
                 * Make sure the problem type is set for each student problem.
                 * The information is shared from the assignment base list
                 * because problem type is determined at runtime.
                 * 
                 */
                ProblemDto studentProblem = sd.getProblem();
                String pid = studentProblem.getPidOnly();
                for (ProblemDto pd : assignment.getPids()) {
                    if (pd.getPidOnly().equals(pid)) {
                        studentProblem.setProblemType(pd.getProblemType());
                    }
                }
            }

        }

        return stuAssignments;
    }

    private void setStudentDetailStatus(StudentAssignment sa) {
        int correct = 0, inCorrect = 0, complete = 0, pending = 0, accepted = 0, viewed = 0;
        for (StudentProblemDto spd : sa.getAssigmentStatuses()) {

            if (spd.isCorrect()) {
                correct++;
            } else {
                inCorrect++;
            }

            String s = spd.getStatus().toLowerCase();
            if (s.equals("correct") || s.equals("incorrect")) {
                complete++;
            }

            if (s.equals("viewed")) {
                viewed++;
            }

            if (s.equals("pending")) {
                pending++;
            }

        }
        sa.setHomeworkGrade(getHomeworkGrade(sa.getAssigmentStatuses().size(), correct, inCorrect));
        sa.setStudentDetailStatus(getLessonStatus(sa.getAssigmentStatuses().size(), complete, pending, viewed));
    }

    private String getLessonStatus(int count, int completed, int pending, int viewed) {

        String ret;
        viewed = (completed + pending + viewed); // all viewed

        if (pending != 0 && viewed != 0) {
            ret = String.format("%d of %d completed, %d pending, %d viewed", completed, count, pending, viewed);
        } else if (pending != 0) {
            ret = String.format("%d of %d completed, %d pending", completed, count, pending);
        } else if (viewed != 0) {
            ret = String.format("%d of %d completed, %d viewed", completed, count, viewed);
        } else {
            ret = String.format("%d of %d completed", completed, count);
        }

        return ret;
    }

    private String getHomeworkStatus(int totCount, int totCompleted, int totPending, int totAccepted, int totViewed) {
        String status = "Not Started";
        if (totAccepted > 0 && totAccepted == totCount) {
            status = "Graded";
        }
        // TODO: should this include totViewed?
        else if ((totCompleted + totPending + totViewed) > 0) {
            status = ((totCompleted + totPending) < totCount) ? "In Progress" : "Ready to Grade";
        }
        return status;
    }

    private String getHomeworkGrade(int totCount, double totCorrect, int totIncorrect) {
        String grade = "-";
        if ((totCorrect + totIncorrect) == totCount) {
            int percent = Math.round(((float) totCorrect / (float) totCount) * 100.0f);
            grade = String.format("%d%s", percent, "%");
        }
        return grade;
    }

    public List<StudentAssignment> getAssignmentWorkForStudent(int userId) throws Exception {

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_WORK_FOR_STUDENT");
        String stuName = null;

        List<StudentProblemDto> problemStatuses = getJdbcTemplate().query(sql, new Object[] { userId, userId, userId }, new RowMapper<StudentProblemDto>() {
            @Override
            public StudentProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                StudentProblemDto prob = new StudentProblemDto();
                Integer uid = rs.getInt("uid");
                prob.setUid(uid);
                ProblemDto probDto = new ProblemDto(rs.getInt("problem_id"), rs.getString("lesson"), rs.getString("label"), rs.getString("pid"), null, rs
                        .getInt("assign_key"));
                prob.setProblem(probDto);
                prob.setStatus(rs.getString("status"));
                prob.setIsGraded((rs.getInt("is_graded") > 0) ? "Yes" : "No");
                return prob;
            }
        });

        /**
         * create student/assignments for all assignments
         */
        CmList<StudentAssignment> stuAssignments = new CmArrayList<StudentAssignment>();

        int lastAssignKey = 0;
        CmList<StudentProblemDto> probList = null;
        Map<Integer, StudentAssignment> stuAssignMap = new HashMap<Integer, StudentAssignment>();
        for (StudentProblemDto probDto : problemStatuses) {
            if (probDto.getProblem().getAssignKey() != lastAssignKey) {
                probList = new CmArrayList<StudentProblemDto>();
                lastAssignKey = probDto.getProblem().getAssignKey();
                Assignment assignment = getAssignment(lastAssignKey);
                StudentAssignment stuAssignment = new StudentAssignment(probDto.getUid(), assignment, probList);
                stuAssignment.setStudentName(stuName);
                stuAssignments.add(stuAssignment);
                stuAssignMap.put(lastAssignKey, stuAssignment);
            }
            probList.add(probDto);
        }

        /**
         * add lesson status for each lesson add assignment status
         */
        String lessonName = "";
        int completed = 0;
        int pending = 0;
        int count = 0;
        int viewed = 0;
        int totGraded = 0;
        int totCompleted = 0;
        int totPending = 0;
        int totCount = 0;
        int totCorrect = 0;
        int totIncorrect = 0;
        int totViewed = 0;
        lastAssignKey = 0;
        CmList<StudentLessonDto> lessonList = null;
        StudentLessonDto lessonStatus = null;

        for (StudentProblemDto probDto : problemStatuses) {

            if (probDto.getProblem().getAssignKey() != lastAssignKey) {
                if (lessonStatus != null) {
                    lessonStatus.setStatus(getLessonStatus(count, completed, pending, viewed));

                    StudentAssignment sa = stuAssignMap.get(lastAssignKey);
                    sa.setProblemCount(totCount);
                    sa.setProblemPendingCount(totPending);
                    sa.setProblemCompletedCount(totCompleted);
                    sa.setHomeworkStatus(getHomeworkStatus(totCount, totCompleted, totPending, totGraded, totViewed));
                    sa.setHomeworkGrade(getHomeworkGrade(totCount, totCorrect, totIncorrect));
                }
                lessonName = "";
                totCount = 0;
                totCorrect = 0;
                totIncorrect = 0;
                totCompleted = 0;
                totPending = 0;
                totGraded = 0;
                totViewed = 0;
                lastAssignKey = probDto.getProblem().getAssignKey();
                lessonList = new CmArrayList<StudentLessonDto>();
                stuAssignMap.get(lastAssignKey).setLessonStatuses(lessonList);
            }

            if (!lessonName.equals(probDto.getProblem().getLesson())) {
                if (lessonName.trim().length() > 0) {
                    if (lessonStatus != null) {
                        lessonStatus.setStatus(getLessonStatus(count, completed, pending, viewed));
                    }
                }
                completed = 0;
                pending = 0;
                count = 0;
                viewed = 0;
                lessonName = probDto.getProblem().getLesson();
                lessonStatus = new StudentLessonDto(probDto.getUid(), lessonName, null);
                lessonList.add(lessonStatus);
            }

            count++;
            totCount++;
            String probStatus = probDto.getStatus().trim();
            if ("not viewed".equalsIgnoreCase(probStatus))
                continue;
            if ("answered".equalsIgnoreCase(probStatus) || "correct".equalsIgnoreCase(probStatus) || "incorrect".equalsIgnoreCase(probStatus)) {
                completed++;
                totCompleted++;
                totGraded += ("yes".equalsIgnoreCase(probDto.getIsGraded())) ? 1 : 0;
                /*
                 * if (probStatus.toLowerCase().indexOf("orrect") > 0)
                 * probDto.setIsGraded("Yes");
                 */
                totCorrect += ("correct".equalsIgnoreCase(probStatus)) ? 1 : 0;
                totIncorrect += ("incorrect".equalsIgnoreCase(probStatus)) ? 1 : 0;
                continue;
            }
            if ("pending".equalsIgnoreCase(probStatus)) {
                pending++;
                totPending++;
            } else if ("viewed".equalsIgnoreCase(probStatus)) {
                viewed++;
                totViewed++;
            }
        }

        if (lessonStatus != null) {
            lessonStatus.setStatus(getLessonStatus(count, completed, pending, viewed));
        }
        if (stuAssignMap.size() > 0) {
            StudentAssignment sa = stuAssignMap.get(lastAssignKey);
            sa.setProblemCount(totCount);
            sa.setProblemPendingCount(totPending);
            sa.setProblemCompletedCount(totCompleted);
            sa.setHomeworkStatus(getHomeworkStatus(totCount, totCompleted, totPending, totGraded, totViewed));
            sa.setHomeworkGrade(getHomeworkGrade(totCount, totCorrect, totIncorrect));
        }

        if (__logger.isDebugEnabled())
            __logger.debug("getAssignmentGradeBook(): stuAssignments.size(): " + stuAssignments.size());

        return stuAssignments;
    }

    /**
     * Assign students to assignment. Return messages indicating each error s
     * 
     * @param assignKey
     * @param students
     * @return
     */
    public AssignmentInfo assignStudents(final int assignKey, List<StudentDto> students) {

        int assignCount = 0, errorCount = 0;
        AssignmentInfo assignmentInfo = new AssignmentInfo();
        String messages = null;
        for (final StudentDto s : students) {

            try {
                getJdbcTemplate().update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        String sql = "insert into CM_ASSIGNMENT_USERS(assign_key, uid)values(?,?)";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, assignKey);
                        ps.setInt(2, s.getUid());
                        return ps;
                    }
                });

                ++assignCount;
            } catch (Exception e) {
                errorCount++;
                if (messages == null) {
                    messages = "";
                }
                messages += "Error assigning student (" + s.getName() + "): " + e.getMessage() + "\n";
            }
        }
        assignmentInfo.setAssigned(assignCount);
        assignmentInfo.setErrors(errorCount);
        assignmentInfo.setMessage(messages);
        return assignmentInfo;
    }

    public void unassignStudents(final int assKey, final CmList<StudentAssignment> students) {
        getJdbcTemplate().batchUpdate("delete from CM_ASSIGNMENT_USERS where assign_key = ? and uid = ?", new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, assKey);
                ps.setInt(2, students.get(i).getUid());
            }

            @Override
            public int getBatchSize() {
                return students.size();
            }
        });

    }

    public List<GroupDto> getAssignmentGroups(int aid) {
        List<GroupDto> groups = new ArrayList<GroupDto>();
        try {
            List<GroupInfoModel> groupModels = CmAdminDao.getInstance().getActiveGroups(aid);

            List<Integer> inList = new ArrayList<Integer>();
            for (GroupInfoModel gm : groupModels) {
                inList.add(gm.getId());
            }

            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_GROUP_INFO");
            sql = QueryHelper.createInListSQL(sql, inList);
            groups.addAll(getJdbcTemplate().query(sql, new Object[] {}, new RowMapper<GroupDto>() {
                @Override
                public GroupDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String label = rs.getString("name") + " [" + rs.getInt("student_count") + ", " + rs.getInt("assignment_count") + "]";
                    return new GroupDto(rs.getInt("group_id"), label);
                }
            }));

        } catch (Exception e) {
            __logger.error("Error getting assignments", e);
        }
        return groups;

    }

    public int getNumberOfIncompleteAssignments(int uid) {

        __logger.debug("Getting number of of assignments for '" + uid + "'");

        String sql = "select count(*) as cnt " + " from HA_USER u " + " JOIN CM_GROUP g on g.id = u.group_id "
                + " JOIN CM_ASSIGNMENT a on a.group_id = u.group_id " + " where u.uid = ? and a.status = 'Open' ";

        List<Integer> cnt = getJdbcTemplate().query(sql, new Object[] { uid }, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("cnt");
            }
        });

        __logger.warn("Assignments for '" + uid + "': " + cnt.get(0));
        return cnt.get(0);

    }

    public List<Assignment> getAssignmentsForUser(int uid) {

        /**
         * Sort so active/not-expired assignments are on top
         * 
         */
        String sql = "select  due_date < now() as is_expired, a.* " + "from   HA_USER u " + " join CM_GROUP g " + " on g.id = u.group_id "
                + " join CM_ASSIGNMENT a " + " on a.group_id = u.group_id " + " where  u.uid = ? " + " and a.is_draft_mode = 0 "
                + " order by is_expired, a.due_date desc ";

        List<Assignment> problems = getJdbcTemplate().query(sql, new Object[] { uid }, new RowMapper<Assignment>() {
            @Override
            public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {

                // create a pseudo name
                String comments = rs.getString("comments");
                String assignmentName = _createAssignmentName(rs.getDate("due_date"), comments);
                Date dueDate = rs.getDate("due_date");

                Assignment ass = new Assignment(rs.getInt("is_draft_mode") == 1, rs.getInt("assign_key"), rs.getInt("group_id"), assignmentName, rs
                        .getString("comments"), dueDate, null, null, rs.getString("status"));
                return ass;
            }
        });
        return problems;
    }

    private String _createAssignmentName(Date dueDate, String comments) {
        return "Due Date: " + dueDate + (comments != null ? " - " + comments : "");
    }

    /**
     * Return the assignment for a given student
     * 
     * @param uid
     * @param assignKey
     * @return
     * @throws Exception
     */
    public StudentAssignment getStudentAssignment(final int uid, int assignKey) throws Exception {

        StudentAssignment studentAssignment = new StudentAssignment();

        Assignment assignment = getAssignment(assignKey);
        studentAssignment.setAssignment(assignment);

        /**
         * Read list of assignment problem statues for this user
         * 
         * 
         */
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_STUDENT_ASSIGNMENT");

        final List<StudentProblemDto> problemStatuses = getJdbcTemplate().query(sql, new Object[] { assignKey, uid, assignKey, uid, assignKey, uid },
                new RowMapper<StudentProblemDto>() {
                    @Override
                    public StudentProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ProblemDto dummy = new ProblemDto(0, null, null, rs.getString("pid"), null, 0);
                        boolean hasShowWork = rs.getInt("has_show_work") != 0;
                        boolean hasShowWorkAdmin = rs.getInt("has_show_work_admin") != 0;
                        StudentProblemDto prob = new StudentProblemDto(uid, dummy, rs.getString("status"), hasShowWork, hasShowWorkAdmin);
                        return prob;
                    }
                });

        boolean isComplete = true;
        for (StudentProblemDto prob : problemStatuses) {

            /**
             * get last input value for this problem
             * 
             */
            sql = "Select * from CM_ASSIGNMENT_PID_ANSWERS where assign_key = ? and user_id = ? and pid = ? order by id desc limit 1";
            List<Boolean> pidAnswers = getJdbcTemplate().query(sql, new Object[] { assignKey, uid, prob.getPid() }, new RowMapper<Boolean>() {
                @Override
                public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("correct") == 0 ? false : true;
                }
            });

            /**
             * If there is an answer, then update the status accordingly.
             * 
             * TODO: remove status from PID_STATUS? is it needed?
             */
            if (pidAnswers.size() > 0) {
                prob.setStatus(pidAnswers.get(0) ? "Correct" : "Incorrect");
            }

            if (!prob.isComplete() && isComplete) {
                isComplete = false;
            }
        }

        /**
         * Make sure there is a status for each
         * 
         */
        CmList<StudentProblemDto> allStatus = new CmArrayList<StudentProblemDto>();

        for (ProblemDto p : assignment.getPids()) {
            boolean found = false;
            for (StudentProblemDto s : problemStatuses) {
                if (s.getProblem().getPid().equals(p.getPid())) {
                    s.setProblem(p); // replace with real problem
                    allStatus.add(s);
                    found = true;
                    break;
                }
            }
            if (!found) {
                StudentProblemDto spd = new StudentProblemDto();
                spd.setProblem(p);
                spd.setUid(uid);
                spd.setStatus("Not Viewed");
                allStatus.add(spd);
            }
        }

        studentAssignment.setAssigmentStatuses(allStatus);
        return studentAssignment;
    }

    /**
     * Get Assignment data for specified assignment and student UIDs
     * 
     * @param assignKey
     * @param userIds
     * @return
     * @throws Exception
     */
    public CmList<StudentAssignment> getAssignmentForStudents(int assignKey, List<Integer> userIds) throws Exception {

        CmList<StudentAssignment> stuAssignments = new CmArrayList<StudentAssignment>();
        Assignment assignment = getAssignment(assignKey);

        String sqlTemplate = CmMultiLinePropertyReader.getInstance().getProperty("GET_GRADE_BOOK_DATA_2");

        String sql = QueryHelper.createInListSQL(sqlTemplate, userIds);
        if (__logger.isDebugEnabled())
            __logger.debug("+++ getStudentsWithActivityInDateRange(): sql: " + sql);

        /**
         * get assignment problem status list for all users
         */
        final Map<Integer, String> nameMap = new HashMap<Integer, String>();
        List<StudentProblemDto> problemStatuses = getJdbcTemplate().query(sql, new Object[] { assignKey, assignKey, assignKey },
                new RowMapper<StudentProblemDto>() {
                    @Override
                    public StudentProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Integer uid = rs.getInt("uid");

                        if (!nameMap.containsKey(uid)) {
                            nameMap.put(uid, rs.getString("user_name"));
                        }
                        ProblemDto dummy = new ProblemDto(rs.getInt("problem_id"), rs.getString("lesson"), rs.getString("label"), rs.getString("pid"), null, 0);

                        boolean hasShowWork = rs.getInt("has_show_work") != 0;
                        boolean hasShowWorkAdmin = rs.getInt("has_show_work_admin") != 0;
                        StudentProblemDto prob = new StudentProblemDto(uid, dummy, rs.getString("status"), hasShowWork, hasShowWorkAdmin);

                        return prob;
                    }
                });

        /**
         * create student assignments for all users
         */
        int uid = 0;
        CmList<StudentProblemDto> probList = null;
        for (StudentProblemDto probDto : problemStatuses) {
            if (probDto.getUid() != uid) {
                probList = new CmArrayList<StudentProblemDto>();
                uid = probDto.getUid();
                StudentAssignment stuAssignment = new StudentAssignment(uid, assignment, probList);
                stuAssignment.setStudentName(nameMap.get(uid));
                stuAssignments.add(stuAssignment);
            }
            probList.add(probDto);
        }

        return stuAssignments;
    }

    /**
     * Make sure there is a status record for this assignment PID
     * 
     * @param assignKey
     * @param uid
     * @param pid
     */
    public void makeSurePidStatusExists(final int assignKey, final int uid, final String pid) {
        try {
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "insert into CM_ASSIGNMENT_PID_STATUS(assign_key, uid, pid, status,modify_datetime)values(?,?,?,'Viewed',now())";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, assignKey);
                    ps.setInt(2, uid);
                    ps.setString(3, pid);

                    return ps;
                }
            });
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().indexOf("duplicate") > -1) {
                // duplicate record, already been viewed
                // silent
            } else {
                __logger.error("Error setting assignment pid status", e);
            }
        }
    }

    /**
     * Return whiteboard data associated with this named assignment/pid
     * 
     * @param uid
     * @param assignId
     * @param pid
     * @return
     */
    public List<WhiteboardCommand> getWhiteboardData(int uid, int assignId, String pid) {
        String sql = "select * from CM_ASSIGNMENT_PID_WHITEBOARD where user_id = ? and assign_key = ? and pid = ? and command_data is not null order by insert_time_mills";
        return getJdbcTemplate().query(sql, new Object[] { uid, assignId, pid }, new RowMapper<WhiteboardCommand>() {
            @Override
            public WhiteboardCommand mapRow(ResultSet rs, int rowNum) throws SQLException {
                WhiteboardCommand wCommand = new WhiteboardCommand(rs.getString("command"), rs.getString("command_data"), rs.getInt("is_admin") != 0 ? true
                        : false);
                return wCommand;
            }
        });
    }

    public void saveAssignmentProblemStatus(final int uid, final int assignKey, final String pid, final String status) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "update CM_ASSIGNMENT_PID_STATUS set modify_datetime = now(), status = ? where uid = ? and assign_key = ? and pid = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, status);
                ps.setInt(2, uid);
                ps.setInt(3, assignKey);
                ps.setString(4, pid);

                return ps;
            }
        });
    }

    public void saveTutorInputWidgetAnswer(final int uid, final int assignKey, final String pid, final String value, final boolean correct) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into CM_ASSIGNMENT_PID_ANSWERS(assign_key, user_id, pid, answer, correct, create_datetime)values(?,?,?,?,?,now()) ";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, assignKey);
                ps.setInt(2, uid);
                ps.setString(3, pid);
                ps.setString(4, clearUpString(value));
                ps.setInt(5, correct ? 1 : 0);
                return ps;
            }
        });
    }

    private String clearUpString(String value) {
        if (value != null) {
            value = value.trim().replace("\\n", "");
        }
        return value;
    }

    public void saveWhiteboardData(final Integer uid, final int assignKey, final String pid, CommandType commandType, final String commandData,
            final boolean isAdmin) {
        if (commandType == CommandType.CLEAR) {
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "delete from CM_ASSIGNMENT_PID_WHITEBOARD where user_id = ? and assign_key = ? and pid = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, uid);
                    ps.setInt(2, assignKey);
                    ps.setString(3, pid);
                    return ps;
                }
            });
        } else {

            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "insert into CM_ASSIGNMENT_PID_WHITEBOARD(user_id, pid, command, command_data, insert_time_mills, assign_key, is_admin) "
                            + " values(?,?,?,?,?,?, ?) ";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, uid);
                    ps.setString(2, pid);
                    ps.setString(3, "draw");
                    ps.setString(4, commandData);
                    ps.setLong(5, System.currentTimeMillis());
                    ps.setInt(6, assignKey);
                    ps.setInt(7, isAdmin ? 1 : 0);

                    return ps;
                }
            });
        }
    }

    /**
     * Update a Student's Assignment status
     * 
     * @param studentAssignment
     * @return
     */
    public int[] updateStudentAssignmentStatus(StudentAssignment studentAssignment) {
        List<StudentProblemDto> list = studentAssignment.getAssigmentStatuses();
        StringBuilder sb = new StringBuilder();
        for (StudentProblemDto sp : list) {
            sb.append(String.format("label: %s, status: %s\n", sp.getPidLabel(), sp.getStatus()));
        }
        __logger.debug("problem-status: " + sb.toString());

        List<Object[]> batch = new ArrayList<Object[]>();
        for (StudentProblemDto sp : studentAssignment.getAssigmentStatuses()) {
            Object[] values = new Object[] { sp.getStatus(), sp.getIsGraded().equalsIgnoreCase("YES") ? 1 : 0,
                    studentAssignment.getAssignment().getAssignKey(), sp.getPid(), studentAssignment.getUid() };
            batch.add(values);
        }
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(this.getDataSource());
        int[] updateCounts = template.batchUpdate("update CM_ASSIGNMENT_PID_STATUS set status = ?, is_graded = ? where assign_key = ? and pid = ? and uid = ?",
                batch);

        // TODO: also update CM_ASSIGNMENT_PID_ANSWERS

        return updateCounts;
    }

    public Collection<? extends LessonDto> getAvailableLessons() {
        String sql = "select distinct lesson, file, subject from HA_PROGRAM_LESSONS_static where subject > '' order by lesson, subject";
        List<LessonDto> problems = getJdbcTemplate().query(sql, new Object[] {}, new RowMapper<LessonDto>() {
            @Override
            public LessonDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new LessonDto(0, 0, rs.getString("subject"), rs.getString("lesson") + " (" + rs.getString("subject") + ")", rs.getString("file"));
            }
        });
        return problems;
    }

    public String getAssignmentLastWidgetInputValue(int uid, int assignKey, String pid) {
        String sql = "select * from  CM_ASSIGNMENT_PID_ANSWERS where user_id = ? and assign_key = ? and pid = ? order by id desc";
        List<String> values = getJdbcTemplate().query(sql, new Object[] { uid, assignKey, pid }, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("answer");
            }
        });

        String value = null;
        if (values.size() > 0) {
            value = values.get(0);
        }
        return value;
    }

    public void closeAssignment(final int uid, final int assignKey) {
        int cnt = getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "update CM_ASSIGNMENT set status = 'Closed' where assign_key = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, assignKey);
                return ps;
            }
        });

        if (cnt != 1) {
            __logger.debug("Assignment not closed: " + uid + ", " + assignKey);
        }
    }

    /**
     * Save the solution context for this assignment problem.
     * 
     * First make sure to remove any existing variables, then insert.
     * 
     * @param uid
     * @param assignKey
     * @param pid
     * @param variables
     */
    public void saveAssignmentSolutionContext(final int uid, final int assignKey, final String pid, final String variables) {

        /** Delete first */
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from CM_ASSIGNMENT_PID_CONTEXT where uid = ? and assign_key = ? and pid = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, uid);
                ps.setInt(2, assignKey);
                ps.setString(3, pid);
                return ps;
            }
        });

        /** Then always insert */
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into CM_ASSIGNMENT_PID_CONTEXT(uid, assign_key, pid, variables, time_viewed)values(?,?,?,?,now())";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, uid);
                ps.setInt(2, assignKey);
                ps.setString(3, pid);
                ps.setString(4, variables);

                return ps;
            }
        });
    }

    public String getSolutionContext(int uid, int assignKey, String pid) {
        String sql = "select * from  CM_ASSIGNMENT_PID_CONTEXT where uid = ? and assign_key = ? and pid = ?";
        List<String> values = getJdbcTemplate().query(sql, new Object[] { uid, assignKey, pid }, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("variables");
            }
        });

        String value = null;
        if (values.size() > 0) {
            value = values.get(0);
        }
        return value;
    }

    /**
     * Given the a problem PID, determine the type of problem
     * 
     * Meaning what type of input is required
     * 
     * @param defaultLabel
     * @return
     */
    static public ProblemType determineProblemType(String html) {
        try {
            if (html.indexOf("hm_flash_widget") > -1 || html.indexOf("hotmath:flash") > -1) {
                return ProblemType.INPUT_WIDGET;
            } else if (html.indexOf("hm_question_def") > -1) {
                return ProblemType.MULTI_CHOICE;
            } else {
                return ProblemType.WHITEBOARD;
            }
        } catch (Exception e) {
            __logger.error("Error determining problem type: " + html, e);
        }

        return ProblemType.UNKNOWN;
    }

    /**
     * Make a copy of Assignment pointed to by assKey
     * 
     * @param assKey
     */
    public String copyAssignment(int aid, int assKey) throws Exception {
        Assignment assignmentCopy = getAssignment(assKey);
        assignmentCopy.setAssignKey(0);
        String copyTag = " - copy:  " + new java.util.Date();
        assignmentCopy.setAssignmentName(assignmentCopy.getAssignmentName() + copyTag);
        assignmentCopy.setComments(assignmentCopy.getComments() + copyTag);
        assignmentCopy.setDraftMode(true);

        saveAssignment(aid, assignmentCopy);

        return assignmentCopy.getAssignmentName();
    }

    /** Extract current use stats for named assignment
     * 
     * @param aid
     * @param assKey
     * @return
     */
    public AssignmentStatus getAssignmentStatus(int aid, int assKey) {
        String sql = "select count(*) from CM_ASSIGNMENT_PID_STATUS where assign_key = ?";
        List<Integer> cnt = getJdbcTemplate().query(sql,new Object[]{assKey},new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });
        
        return new AssignmentStatus(cnt.get(0) > 0);
    }
}
