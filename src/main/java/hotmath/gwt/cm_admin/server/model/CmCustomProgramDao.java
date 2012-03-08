package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel.Type;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CustomProgramInfoModel;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizId;
import hotmath.gwt.shared.client.model.CustomQuizInfoModel;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import sb.util.SbUtilities;

public class CmCustomProgramDao extends SimpleJdbcDaoSupport {

    private static final Logger LOGGER = Logger.getLogger(CmCustomProgramDao.class);

    static private CmCustomProgramDao __instance;
    static public CmCustomProgramDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (CmCustomProgramDao)SpringManager.getInstance().getBeanFactory().getBean("cmCustomProgramDao");
        }
        return __instance;
    }
    
    private CmCustomProgramDao() {
    }
    

    /**
     * Return complete list of lessons that can be used to create a custom
     * program.
     * 
     * NOTE: HA_PROGRAM_LESSONS is created in PrescriptionReport while creating
     * the HA_PRESCRIPTION_LOG
     * 
     * This table is manually renamed to HA_PRESCRIPTION_LOG_static. (done to
     * allow creation of new lookup table on live server)
     * 
     * Mark each lesson with the lowest level applicable for the lesson.
     * 
     */
    @SuppressWarnings("unchecked")
    public CmList<CustomLessonModel> getAllLessons(final Connection conn) throws Exception {
        
        CmList<CustomLessonModel> list = (CmList<CustomLessonModel>)CmCacheManager.getInstance().retrieveFromCache(CacheName.ALL_CUSTOM_PROGRAM_LESSONS,null);
        if(list != null) {
            return list;
        }
        HashMap<String, List<CustomLessonModel>> map = new HashMap<String, List<CustomLessonModel>>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();

            /** for every entry with a specified subject */
            ResultSet rs = stmt
                    .executeQuery("select distinct lesson, file, subject from HA_PROGRAM_LESSONS_static order by lesson");
            while (rs.next()) {
                CustomLessonModel clm = new CustomLessonModel(rs.getString("lesson"), rs.getString("file"),
                        rs.getString("subject"));

                /**
                 * see if there is a entry for this file already if there is use
                 * it and keep a counter, otherwise create a new entry.
                 */
                List<CustomLessonModel> lessons = map.get(clm.getFile());
                if (lessons == null) {
                    lessons = new ArrayList<CustomLessonModel>();
                    map.put(clm.getFile(), lessons);
                }
                lessons.add(clm);
            }

            //checkForDuplicates(map);

            /**
             * at this point we have a map containing a distinct list of file
             * names as keys and as values a list of lessons linked to the file.
             */

            /**
             * Create list of distinct lessons and the highest subject
             * 
             */
            CmList<CustomLessonModel> lessons = new CmArrayList<CustomLessonModel>();
            for (String lesson : map.keySet()) {
                List<CustomLessonModel> ls = map.get(lesson);
                CustomLessonModel use = null;
                for (CustomLessonModel clm : ls) {

                    /** use the lowest level */
                    if (use == null || getSubjectLevel(clm.getSubject()) < getSubjectLevel(use.getSubject())) {
                        use = clm;
                    }
                }
                lessons.add(use);
            }
            Collections.sort(lessons, new Comparator<CustomLessonModel>() {
                @Override
                public int compare(CustomLessonModel o1, CustomLessonModel o2) {
                    return o1.getLesson().compareTo(o2.getLesson());
                }
            });
            
            CmCacheManager.getInstance().addToCache(CacheName.ALL_CUSTOM_PROGRAM_LESSONS,null,lessons);
            
            return lessons;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }
    
    /** Output a report showing lessons with duplicate titles
     * 
     * @param map
     */
    private void checkForDuplicates(HashMap<String, List<CustomLessonModel>> map) {
        System.out.println("List of files with more than one lesson:");
        for (String f : map.keySet()) {
            f.length();
            List<CustomLessonModel> lc = map.get(f);
            if (lc.size() > 1) {
                String prev = null;
                for (CustomLessonModel c : lc) {
                    if (prev == null) {
                        prev = c.getLesson();
                    } else if (!prev.equals(c.getLesson())) {
                        printLessonInfo(f, lc);
                    }
                }
            }

        }
    }

    private void printLessonInfo(String f, List<CustomLessonModel> lc) {
        System.out.println(f);
        for (CustomLessonModel clm : lc) {
            System.out.println("    " + clm.getLesson() + " (" + clm.getSubject() + ")");
        }
    }

    /** Find the total segment count for the named custom program 
     * 
     * @param conn
     * @param progId
     * @return
     * @throws Exception
     */
    public int getTotalSegmentCount(final Connection conn, int progId) throws Exception {
        return readProgramSegments(conn, progId).size();
    }

    /** return true if named program segment has a valid quiz, false otherwise.
     * 
     * NOTE: segmentNum is 1 based.
     * 
     * @param conn
     * @param programId
     * @param segment
     * @return
     * @throws Exception
     */
    public boolean doesProgramSegmentHaveQuiz(final Connection conn, int programId, int segmentNum) throws Exception {
        assert(segmentNum > 0);
        List<ProgramSegment> segments = readProgramSegments(conn, programId);
        CustomLessonModel quiz = segments.get(segmentNum-1).getQuiz();  // segmentNum is 1 based
        Integer qid = null;
        if(quiz != null)
            qid = quiz.getQuizId();
         
        return qid != null && qid != 1; 
    }
    /**
     * Return list of list representing each program segment.
     * 
     * A segment has a Quiz, followed by N-lessons.
     * 
     * @param conn
     * @param programId
     * @return
     * @throws Exception
     */
    private List<ProgramSegment> readProgramSegments(final Connection conn, int programId) throws Exception {
        PreparedStatement stmt = null;
        try {
            List<ProgramSegment> programSegments = new ArrayList<ProgramSegment>();
            
            CmList<CustomLessonModel> all = getAllLessons(conn);
          

            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CUSTOM_PROGRAM_ITEMS");
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, programId);
            ResultSet rs = stmt.executeQuery();
            
            /** create the first segment */
            ProgramSegment segment = new ProgramSegment();
            programSegments.add(segment);
            
            while (rs.next()) {
                int quizId = rs.getInt("custom_quiz");
                String customQuizName = rs.getString("custom_quiz_name");
                if (quizId > 0) {
                    /** is a quiz */
                	boolean isArchived = (rs.getInt("is_archived") > 0);
                	boolean isAnswersViewable = (rs.getInt("is_answers_viewable") > 0);
                	String archiveDate = rs.getString("archive_date");
                    CustomLessonModel cml = new CustomLessonModel(quizId, customQuizName, isAnswersViewable, false, isArchived, archiveDate);

                    if(segment.getQuiz() == null) {
                        segment.setQuiz(cml);
                    }
                    else {
                        /** create a new segment */
                        segment = new ProgramSegment();
                        segment.setQuiz(cml);
                        programSegments.add(segment);
                    }
                } else {
                    /** is a lesson */
                    String lessonFile =  rs.getString("file");
                    String subject = getSubject(lessonFile, all);
                    segment.getLessons().add(new CustomLessonModel(rs.getString("lesson"),lessonFile, subject));
                }
            }
            
            
//            Collections.sort(segment.getLessons(), new Comparator<CustomLessonModel>() {
//                @Override
//                public int compare(CustomLessonModel o1, CustomLessonModel o2) {
//                    return o1.getLesson().compareTo(o2.getLesson());
//                }
//            });            
            
            
            return programSegments;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }
    
    
    
    /** Return the subject of the lesson found in all
     * 
     * Default == 'Ess';
     * @param lesson
     * @param all
     * @return
     */
    private String getSubject(String lesson, CmList<CustomLessonModel> all) {
        
        for(CustomLessonModel l: all) {
            if(l.getFile().equals(lesson)) {
                return l.getSubject();
            }
        }
        
        return "Ess";
    }
    
    
    /** Composit of CustomLessonModel list of models.
     * 
     * @author casey
     *
     */
    class ProgramSegment {
        CustomLessonModel quiz;
        CmList<CustomLessonModel> lessons = new CmArrayList<CustomLessonModel>();
        
        public ProgramSegment() {}
        public ProgramSegment(CustomLessonModel quiz, CmList<CustomLessonModel> lessons) {
            this.quiz = quiz;
            this.lessons = lessons;
        }

        public CustomLessonModel getQuiz() {
            return quiz;
        }

        public void setQuiz(CustomLessonModel quiz) {
            this.quiz = quiz;
        }

        public CmList<CustomLessonModel> getLessons() {
            return lessons;
        }

        public void setLessons(CmList<CustomLessonModel> lessons) {
            this.lessons = lessons;
        }

    }

    /**
     * Return list of custom programs defined by this admin
     * 
     * @param conn
     * @param adminId
     * @return
     * @throws Exception
     */
    public CmList<CustomProgramModel> getCustomPrograms(final Connection conn, Integer adminId) throws Exception {
        PreparedStatement stmt = null;
        try {
            CmList<CustomProgramModel> programs = new CmArrayList<CustomProgramModel>();
            stmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty(
                    "CUSTOM_PROGRAM_DEFINITIONS_ALL"));
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CustomProgramModel prog = new CustomProgramModel(rs.getString("name"), rs.getInt("id"),
                        rs.getInt("assigned_count"), rs.getInt("inuse_count"), rs.getInt("is_template") != 0,
                        rs.getInt("is_archived") > 0);
                programs.add(prog);
            }
            return programs;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    /**
     * Get a single Custom Program by progId
     * 
     * @param conn
     * @param progId
     * @return
     * @throws Exception
     */
    public CustomProgramModel getCustomProgram(final Connection conn, Integer progId) throws Exception {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty(
                    "CUSTOM_PROGRAM_DEFINITION_BYID"));
            stmt.setInt(1, progId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                throw new CmException("Custom Program not found: " + progId);
            return new CustomProgramModel(rs.getString("name"), rs.getInt("id"), rs.getInt("assigned_count"),
                    rs.getInt("inuse_count"), rs.getInt("is_template") != 0,
                    rs.getInt("is_archived") > 0);
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    /**
     * Get a single Custom Program by progId
     * 
     * @param conn
     * @param progId
     * @return
     * @throws Exception
     */
    public CustomProgramModel archiveCustomProgram(Integer progId) throws Exception {
    	try {
            getSimpleJdbcTemplate().update(
                    CmMultiLinePropertyReader.getInstance().getProperty("ARCHIVE_CUSTOM_PROGRAM_BYID"),
                    new Object[] {progId});
    	}
    	catch (Exception e) {
            logger.error("Error archiving custom prog id: " + progId, e);
            throw new Exception ("Error archiving custom prog id: " + progId, e);
    	}
    	return getCustomProgram(progId);
    }

    /**
     * Get a single Custom Program by Id
     * 
     * @param progId
     * @return
     * @throws Exception
     */
    public CustomProgramModel getCustomProgram(final Integer progId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("CUSTOM_PROGRAM_DEFINITION_BYID");

        CustomProgramModel customProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{progId},
                new RowMapper<CustomProgramModel>() {
                    public CustomProgramModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            return new CustomProgramModel(rs.getString("name"), rs.getInt("id"), rs.getInt("assigned_count"),
                                    rs.getInt("inuse_count"), rs.getInt("is_template") != 0,
                                    rs.getInt("is_archived") > 0);
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Custom Program for Id: %d", progId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return customProg;
    }

    public CustomProgramModel getCustomProgram(final Connection conn, Integer adminId, String customProgramName)
            throws Exception {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty(
                    "CUSTOM_PROGRAM_DEFINITION_BYNAME"));
            stmt.setInt(1, adminId);
            stmt.setString(2, customProgramName);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                throw new CmException("Custom Program not found for: " + adminId + ", " + customProgramName);

            return new CustomProgramModel(rs.getString("name"), rs.getInt("id"), rs.getInt("assigned_count"),
                    rs.getInt("inuse_count"), rs.getInt("is_template") != 0,
                    rs.getInt("is_archived") > 0);
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    public CmList<CustomProgramModel> deleteCustomProgram(final Connection conn, Integer programId) throws Exception {
        Statement stmt = null;
        try {
            CmList<CustomProgramModel> programs = new CmArrayList<CustomProgramModel>();
            stmt = conn.createStatement();
            stmt.executeUpdate("delete from HA_CUSTOM_PROGRAM where id = " + programId);
            return programs;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    /**
     * return list of all lessons assigned to custom program segment.
     * 
     * A new segment is defined when a quiz is added to cp.
     * 
     * @param conn
     * @param programId
     * @return
     * @throws Exception
     */
    public CmList<CustomLessonModel> getCustomProgramLessons(final Connection conn, Integer programId,
            int programSegment) throws Exception {
        PreparedStatement stmt = null;
        try {
            
            
            /** catch old problem of setting to -1
             * 
             */
            if(programSegment == -1)
                programSegment = 1;
            
            
            assert(programSegment > 0);

            
            List<ProgramSegment> testSegments = readProgramSegments(conn, programId);

            if (programSegment > testSegments.size()) {
                LOGGER.warn("Zero lessons for test segment " + programSegment + ", program_id=" + programId);
                return new CmArrayList<CustomLessonModel>();
            }
            
            
            /** return only lessons for this segment 
             * 
             * NOTE: programSegment is 1 based
             */
            CmList<CustomLessonModel> listOfLessons = new CmArrayList<CustomLessonModel>();
            for(CustomLessonModel lm: testSegments.get(programSegment-1).getLessons()) {
                if(lm.getQuizId() == null) {
                    listOfLessons.add(lm);
                }
            }
            return listOfLessons;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    public CmList<CustomLessonModel> getCustomProgramLessonsAllSegments(final Connection conn, Integer programId)
            throws Exception {
        CmList<CustomLessonModel> listAll = new CmArrayList<CustomLessonModel>();
        List<ProgramSegment> testSegments = readProgramSegments(conn, programId);

        for (ProgramSegment segment : testSegments) {
            for (CustomLessonModel model : segment.getLessons()) {
                listAll.add(model);
            }
        }
        return listAll;
    }

    /**
     * Return the quizids of the named segment of the named custom quiz.
     * 
     * @param conn
     * @param custProgId
     * @param segment
     * @return
     * @throws Exception
     */
    public CmList<CustomQuizId> getCustomProgramQuizIds(int programId, int programSegment)
            throws Exception {
        assert(programSegment>0);
        PreparedStatement stmt = null;
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ProgramSegment segment = readProgramSegments(conn, programId).get(programSegment-1);
            CustomLessonModel theQuiz = segment.getQuiz();
            
            Integer quid = theQuiz != null?theQuiz.getQuizId():null;
            if(quid == null || quid == 1) {
                /** dummy, empty quiz */
                return new CmArrayList<CustomQuizId>();
            }
            CmList<CustomQuizId> quizIds = CmQuizzesDao.getInstance().getCustomQuizIds(quid);
            return quizIds;
        }
        finally {
            SqlUtilities.releaseResources(null, stmt, conn);
        }
    }
    
    public List<String> getCustomProgramQuizSegmentPids(int programId, int programSegment) throws Exception {
        List<String> pids = new ArrayList<String>();
        for(CustomQuizId cqi: getCustomProgramQuizIds(programId, programSegment)) {
            pids.add(cqi.getPid());
        }
        return pids;
    }
    

    /**
     * return list of custom program lessons for supplied testId
     * 
     * @param conn
     * @param testId
     * @return
     * @throws Exception
     */
    public CmList<CustomLessonModel> getCustomProgramLessonsForTestId(Integer testId)
            throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            
            CmList<CustomLessonModel> lessons = new CmArrayList<CustomLessonModel>();
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("CUSTOM_PROGRAM_LESSONS_FOR_TEST_ID");
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, testId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                lessons.add(new CustomLessonModel(rs.getString("lesson"), rs.getString("file"), rs.getString("subject")));
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("+++ getCustomProgramLessonsForTestId(): testId: %d, lessons.size(): %d",
                        testId, lessons.size()));
            }
            return lessons;
        } finally {
            SqlUtilities.releaseResources(rs, stmt, conn);
        }
    }

    public int getCustomProgramUsageCount(int cpId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("CUSTOM_PROGRAM_USAGE_COUNT");

    	Integer count = 0;
    	try {
    		count = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{cpId},
    				new RowMapper<Integer>() {
    					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
    						return rs.getInt("count");
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting Custom Program usage count for cpId: %d", cpId), e);
    		throw new Exception("Error getting Custom Program usage count");
    	}
    	return count;
    }

    /**
     * Auto Custom Quiz creation
     * 
     * Check to see if any auto quizzes have been added to custom program
     * lessons. If so, then create each custom lesson based on previous,
     * untested lessons
     * 
     */
    private void makeSureAutoCustomQuizzesCreated(final Connection conn, int adminId, List<CustomLessonModel> lessons)
            throws Exception {
        for (CustomLessonModel lesson : lessons) {
            if (lesson.getCustomProgramType() == Type.QUIZ) {
                if (lesson.getQuizId() == 0) {
                    /** create and assign new custom quiz id */
                    lesson.setQuizId(createAutoCustomQuiz(conn, adminId, lesson, lessons));
                }
            }
        }
    }

    /**
     * Create a new auto custom quiz.
     * 
     * 1. Find previous lessons that have not been quizzed 2. For each lesson
     * take N from each. Where is number of questions per lesson.
     * 
     * Return the new custom quiz id
     * 
     * @param conn
     * @param adminId
     * @param lesson
     * @param lessons
     * @throws Exception
     */
    private int createAutoCustomQuiz(final Connection conn, int adminId, CustomLessonModel lesson,
            List<CustomLessonModel> lessons) throws Exception {
        List<CustomLessonModel> lessonsInCp = new ArrayList<CustomLessonModel>();

        CmList<CustomQuizId> customQuizQuestions = new CmArrayList<CustomQuizId>();

        for (int i = 0; i < lessons.size(); i++) {
            CustomLessonModel li = lessons.get(i);
            if (li == lesson) {
                while (i-- > 0) {
                    CustomLessonModel l2 = lessons.get(i);
                    if (l2.getCustomProgramType() == Type.LESSON) {
                        lessonsInCp.add(l2);
                    } else {
                        break;
                    }
                }

                break;
            }
        }

        if (lessonsInCp.size() == 0) {
            throw new Exception("No lessons to create custom quiz from:  " + lesson);
        }

        /**
         * create a pool from which to choose questions
         * 
         */
        CmQuizzesDao dao = CmQuizzesDao.getInstance();

        int maxFromEachLesson = 2;

        for (CustomLessonModel l : lessonsInCp) {
            CmList<QuizQuestion> questions = dao.getQuestionsFor(conn, l.getFile(), l.getSubject(), false);
            for (int i = 0; i < questions.size() && i < maxFromEachLesson; i++) {
                customQuizQuestions.add(new CustomQuizId(questions.get(i).getPid(), i));
            }
        }

        String cpName = getNextCustomQuizNumberForAdmin(conn, adminId);
        return dao.saveCustomQuiz(conn, adminId, cpName, customQuizQuestions);
    }

    static final String CUSTOM_QUIZ_TEMPLATE = "Auto Custom Quiz: ";
    private String getNextCustomQuizNumberForAdmin(final Connection conn, int adminId) throws Exception {
        CmList<CustomQuizDef> defs = CmQuizzesDao.getInstance().getCustomQuizDefinitions(adminId);
        int max=0;
        for (CustomQuizDef cpm : defs) {
            String quizName = cpm.getQuizName();
            if (quizName.startsWith(CUSTOM_QUIZ_TEMPLATE)) {
                int num = SbUtilities.getInt(quizName.substring(CUSTOM_QUIZ_TEMPLATE.length()));
                if(num > max)
                    max = num;
            }
        }
        

        return CUSTOM_QUIZ_TEMPLATE + (max+1);
    }

    public CmList<CustomLessonModel> saveChanges(final Connection conn, int adminId, Integer progId, String name,
            List<CustomLessonModel> lessons) throws Exception {
        makeSureNameIsValid(conn, name);

        makeSureAutoCustomQuizzesCreated(conn, adminId, lessons);

        PreparedStatement stmt1 = null, stmt2 = null;
        try {
            stmt1 = conn.prepareStatement("update HA_CUSTOM_PROGRAM set name = ? where id = ?");
            stmt1.setString(1, name);
            stmt1.setInt(2, progId);
            if (stmt1.executeUpdate() != 1)
                throw new CmException("Could not update Custom Program title: " + progId);

            conn.createStatement().executeUpdate("delete from HA_CUSTOM_PROGRAM_LESSON where program_id = " + progId);
            String sql = "insert into HA_CUSTOM_PROGRAM_LESSON(program_id,lesson,file,subject,custom_quiz)values(?,?,?,?,?)";
            stmt2 = conn.prepareStatement(sql);
            stmt2.setInt(1, progId);
            for (CustomLessonModel l : lessons) {

                switch (l.getCustomProgramType()) {
                case LESSON:
                    stmt2.setString(2, l.getLesson());
                    stmt2.setString(3, l.getFile());
                    stmt2.setString(4, l.getSubject());
                    stmt2.setInt(5, 0);
                    break;

                case QUIZ:
                    stmt2.setNull(2, Types.VARCHAR);
                    stmt2.setNull(3, Types.VARCHAR);
                    stmt2.setNull(4, Types.VARCHAR);
                    stmt2.setInt(5, l.getQuizId());
                    break;
                }

                if (stmt2.executeUpdate() != 1) {
                    throw new CmException("Could not save new custom program lesson: " + progId + ", " + l);
                }
            }
            return null;
        } finally {
            SqlUtilities.releaseResources(null, stmt1, null);
            SqlUtilities.releaseResources(null, stmt2, null);
        }
    }

    /** Make sure the name can be used ... it is not a system template name */
    private void makeSureNameIsValid(final Connection conn, String name) throws Exception {
        PreparedStatement stmt1 = null;
        try {
            String sql = "select 1 from HA_CUSTOM_PROGRAM where is_template = 1 and name = ?";
            stmt1 = conn.prepareStatement(sql);
            stmt1.setString(1, name);
            ResultSet rs = stmt1.executeQuery();
            if (rs.first())
                throw new CmException("Name is invalid.  Custom program name '" + name + "' is a template name.");
        } finally {
            SqlUtilities.releaseResources(null, stmt1, null);
        }
    }

    public CustomProgramModel createNewCustomProgram(final Connection conn, Integer adminId, String name,
            List<CustomLessonModel> lessons) throws Exception {
        PreparedStatement stmt = null;

        makeSureNameIsValid(conn, name);

        try {
            String sql = "insert into HA_CUSTOM_PROGRAM(admin_id,name)values(?,?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, adminId);
            stmt.setString(2, name);
            if (stmt.executeUpdate() != 1) {
                throw new CmException("Could not save new custom program lesson: " + adminId + ", " + name);
            }
            ResultSet rs = stmt.getGeneratedKeys();
            if (!rs.next())
                throw new CmException("Could not generate a new custom program id: " + adminId + ", " + name + ", "
                        + lessons);
            int newProgId = rs.getInt(1);

            saveChanges(conn, adminId, newProgId, name, lessons);

            return new CustomProgramModel(name, newProgId, 0, 0, false, false);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    /**
     * Get list of students that have this program assigned
     * 
     * @param conn
     * @param adminId
     * @param program
     * @return
     * @throws Exception
     */
    public CustomProgramInfoModel getCustomProgramInfo(final Connection conn, Integer adminId,
            CustomProgramModel program) throws Exception {
        PreparedStatement stmt = null;
        try {
            CustomProgramInfoModel info = new CustomProgramInfoModel();
            info.setProgram(program);

            stmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty(
                    "CUSTOM_PROGRAM_INFO_INUSE"));
            stmt.setInt(1, adminId);
            stmt.setInt(2, program.getProgramId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StudentModelExt sm = new StudentModelExt();
                sm.setAdminUid(adminId);
                sm.setName(rs.getString("user_name"));
                sm.setUid(rs.getInt("uid"));
                info.getAssignedStudents().add(sm);
            }
            info.setLessons(getCustomProgramLessons(conn, program.getProgramId(), 1));
            return info;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }
    
    public CustomQuizInfoModel getCustomQuizInfo(final Connection conn, Integer adminId,
            int quizId) throws Exception {
        PreparedStatement stmt = null;
        try {
            CustomQuizInfoModel info = new CustomQuizInfoModel();
            stmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("CUSTOM_QUIZ_INFO_INUSE"));
            stmt.setInt(1, adminId);
            stmt.setInt(2, quizId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StudentModelExt sm = new StudentModelExt();
                sm.setAdminUid(adminId);
                sm.setName(rs.getString("user_name"));
                sm.setUid(rs.getInt("uid"));
                info.getAssignedStudents().add(sm);
            }
            info.setQuestionCount( CmQuizzesDao.getInstance().getCustomQuizIds(quizId).size());
            return info;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    /**
     * return integer representing the level logically of this subject
     * 
     * TODO: This should not be hard-coded here, use table.
     * 
     *
     * 
     *     -- grade levels provided by Ananthi
           Algebra 2 = 12
           Geometry = 11
           Algebra 1 = 10
           Pre-Algebra = 9
           Essentials = 8
     * 
     * 
     * @param subject
     * @return
     */
    public int getSubjectLevel(String subject) throws Exception {
        if (subject == null || subject.length() == 0)
            return 10; // *no subject equals alg 1 by default
        
        else if (subject.equals("Ess"))
            return 8;
        else if (subject.equals("Pre-Alg"))
            return 9;
        else if (subject.equals("Alg 1"))
            return 10;
        else if (subject.equals("Geom"))
            return 11;
        else if (subject.equals("Alg 2"))
            return 12;
        else
            return 99;
    }
}
