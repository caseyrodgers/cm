package hotmath.assessment;

import hotmath.BookInfoManager;
import hotmath.HotMathException;
import hotmath.ProblemID;
import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpManager;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaTestRunDao.TestRunLesson;
import hotmath.testset.ha.HaUserDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

/**
 * Class to represent an assessment prescription for a given set of INMH items.
 * 
 * With a set of comma separated pids, find the union of all related INMH items.
 * 
 * @author Casey
 * 
 */
public class AssessmentPrescription {

    static final Logger logger = Logger.getLogger(AssessmentPrescription.class);

    protected Connection conn;

    final static public int TOTAL_SESSION_SOLUTIONS = 3;
    final static public int MAX_SESSIONS = 100;
    static final int PID_COUNT = 3;

    InmhAssessment _assessment;

    List<AssessmentPrescriptionSession> _sessions = new ArrayList<AssessmentPrescriptionSession>();
    int missed, totalPrescription;

    HaTestRun testRun;

    protected AssessmentPrescription(final Connection conn) {
        this.conn = conn;
    }

    /**
     * Create an assessment prescription based on comma separated list of
     * problem ids.
     * 
     * A prescription contains one or more: AssessmentPrescriptionSession
     * objects
     * 
     * Each one is a single session with a two tier report:
     * 
     * -- Topic Name 1 -- Solution 1 -- Solution 2 -- Solution 3 -- Topic Name 2
     * -- Solution 4 -- Solution 5
     * 
     * 
     * L == total number of solutions in a session M == missed questions
     * (distinct pid)
     * 
     * To Determine number of sessions:
     * 
     * M == missed, S == sessions f M = 0, S = 0 (You got them all right! Try a
     * harder test) If M = 1, S = 1 If 1 < M < 4, S = 2 If M > 3, S = 3
     * 
     * @param pids
     * @throws Exception
     */
    public AssessmentPrescription(final Connection conn, HaTestRun testRun) throws Exception {
        this(conn, testRun, null);
    }
    
    public AssessmentPrescription(final Connection conn, HaTestRun testRun, ClientEnvironment clientEnvironment) throws Exception {
        this(conn);
        logger.debug("Creating prescription for run: " + testRun);
        this.testRun = testRun;
        
        int uid = testRun.getHaTest().getUser().getUid();
        
        readAssessment();

        missed = _assessment.getPids().length;

        List<InmhItemData> itemsData = _assessment.getInmhItemUnion("review");
        int sumOfWeights = 0;
        for (InmhItemData itemData : itemsData) {
            sumOfWeights += itemData.getWeight();
        }

        totalPrescription = itemsData.size() * TOTAL_SESSION_SOLUTIONS;
        
        if(clientEnvironment == null) {
            clientEnvironment = HaUserDao.getInstance().getLatestClientEnvironment(uid);
        }
        logger.info("Creating prescription for client info: " + clientEnvironment);

        // assign weights to items
        int sessNum = 0;
        for (InmhItemData itemData : itemsData) {

            if (itemData.getInmhItem().getFile().equals(DEFAULT_LESSON)) {
                // no widgets needed for default lesson
                continue;
            }
            
            List<RppWidget> rppWidgets = itemData.getWidgetPool(conn, uid + "/" + testRun.getRunId());
            if (rppWidgets.size() == 0) {
                logger.warn("No RP Widgets found for + '" + itemData.getInmhItem().toString() + "'");
                continue;
            }

            AssessmentPrescriptionSession session = createSession(sessNum, rppWidgets, itemData, true, clientEnvironment);

            // assert that there is at least one
            if (session.getSessionItems().size() == 0) {
                // this session has no items, so it is invalid and will be
                // skipped
                logger.warn("AssessmentPrescriptionSession: session has no items: " + session);
            } else {
                // TOOD: should sessNum be incremented if session not actually
                // added?
                // add this session, and move to next
                _sessions.add(session);
                sessNum++;
            }
        }

        sortLessonsByRanking(conn, _sessions);

        HaTestRunDao.getInstance().addLessonsToTestRun(conn, testRun, _sessions);
        logger.debug("Finished creating prescription for run: " + testRun);
    }

    String DEFAULT_LESSON="/hotmath_help/topics/index_hotmath_review_full.html";
    
    /**
     * Return the grade level of this current program
     * 
     * This will be determined by the textcode's subject's grade_level found in
     * textcode assigned to the test_def
     * 
     * @return
     */
    public int getGradeLevel() {
        if (testRun == null)
            return 99;

        return getTestRun().getHaTest().getTestDef().getGradeLevel();
    }

    public HaTestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(HaTestRun testRun) {
        this.testRun = testRun;
    }

    /**
     * Read and set _assessment variable
     * 
     * @throws CmException
     */
    protected void readAssessment() throws CmException {
        _assessment = new InmhAssessment(conn, testRun.getHaTest().getUser().getUid(), testRun.getPidList());
    }

    public InmhAssessment getAssessment() {
        return _assessment;
    }

    private void sortLessonsByRanking(final Connection conn, List<AssessmentPrescriptionSession> sessions)
            throws Exception {
        final Map<String, Integer> sortMap = getLessonRankings(conn);
        Collections.sort(sessions, new Comparator<AssessmentPrescriptionSession>() {
            @Override
            public int compare(AssessmentPrescriptionSession o1, AssessmentPrescriptionSession o2) {
                String l1 = o1.getSessionItems().get(0).getItem().getFile();
                String l2 = o2.getSessionItems().get(0).getItem().getFile();
                int rank1 = getLessonRank(sortMap, l1);
                int rank2 = getLessonRank(sortMap, l2);

                int comp = rank1 - rank2;
                if (comp == 0) {
                    return l1.compareTo(l2);
                }
                return comp;
            }
        });
    }

    static public int getLessonRank(Map<String, Integer> map, String name) {
        Integer rank = map.get(name);
        if (rank == null)
            rank = 999999;
        /** end of list */
        return rank;
    }

    static public Map<String, Integer> getLessonRankings(final Connection conn) throws Exception {
        String key = "lesson_ranking";
        @SuppressWarnings("unchecked")
        Map<String, Integer> map = (Map<String, Integer>) CmCacheManager.getInstance().retrieveFromCache(
                CacheName.LESSON_RANKINGS, key);
        if (map != null) {
            return map;
        }

        PreparedStatement stmt = null;
        try {
            map = new HashMap<String, Integer>();
            stmt = conn.prepareStatement("select * from HA_LESSON_RANK ORDER BY RANK");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("lesson_file"), rs.getInt("rank"));
            }
            CmCacheManager.getInstance().addToCache(CacheName.LESSON_RANKINGS, key, map);
            return map;
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    public AssessmentPrescription(final Connection conn, List<TestRunLesson> lessons, HaTestRun testRun)
            throws CmException {

        logger.debug("Creating AssessmentPrescription from existing lessons: " + testRun.getRunId());
        this.testRun = testRun;
        _assessment = new InmhAssessment(conn, testRun.getHaTest().getUser().getUid(), testRun.getPidList());
        missed = _assessment.getPids().length;

        // create sessions from persistent data
        int sessNum = 0;
        for (TestRunLesson lesson : lessons) {
            logger.debug("Creating AssessmentPrescription for lesson '" + lesson + "': " + testRun.getRunId());
            AssessmentPrescriptionSession session = new AssessmentPrescriptionSession(this);
            for (RppWidget pid : lesson.getPids()) {
                List<SessionData> si = session.getSessionItems();

                /**
                 * @TODO: need to get item (lesson name?)
                 * 
                 */
                INeedMoreHelpItem item = new INeedMoreHelpItem("review", lesson.getFile(), lesson.getLesson());
                si.add(new SessionData(item, pid, 3, 1, pid.getWidgetJsonArgs()));
            }
            if (!_sessions.contains(session)) {
                _sessions.add(session);
                sessNum++;
            }
        }
        
        logger.debug("Finished creating AssessmentPrescription from lessons: " + testRun.getRunId());
    }

    /**
     * Create a single prescription session based on passed in data
     * 
     * if client supports flash and there are RPAs.
     *    return only RPAs
     *     
     * if client does not support Flash
     *    return only dynamic RPPs
     *    
     * Otherwise, 
     *    return all RPPs
     * 
     * @param sessNum
     * @param workBookPids
     * @param itemData
     * @return
     * @throws Exception
     */
    public AssessmentPrescriptionSession createSession(int sessNum, List<RppWidget> rppWidgets, InmhItemData itemData,
            boolean filter,ClientEnvironment clientEnvironment) throws Exception {
        AssessmentPrescriptionSession session = new AssessmentPrescriptionSession(this);
        List<SessionData> sessionItems = session.getSessionItems();

        
        /**
         * if ANY widgets are RPA then only show the RPA widgets
         * 
         */
        boolean hasRPA = false;
        for (RppWidget rpp : rppWidgets) {
            if (!rpp.isSolution()) {
                hasRPA = true;
                break;
            }
        }
        if (hasRPA && clientEnvironment.isFlashEnabled()) {
            /**
             * only show RPA widgets (filtered)
             * 
             */
            for (RppWidget rpp : rppWidgets) {
                if (!rpp.isSolution()) {
                    sessionItems.add(new SessionData(itemData.getInmhItem(), rpp, (int) PID_COUNT, itemData.getWeight(), rpp.getWidgetJsonArgs()));
                }
            }            
        } else {
            /**
             * show only RPP widgets (filtered)
             * 
             * If CM Mobile (flashNoEnabled)
             * then only use dynamic solutions
             * if available.
             * 
             */
            List<SessionData> filteredData = filterRppsByGradeLevel(getGradeLevel(), rppWidgets, itemData);
            
            /** two passes
             * 
             * first to only choose dynamic solutions if 
             * flash not available
             * 
             * fl
             * 
             */
            boolean allowFlashWidgetsOnServer = SbUtilities.getBoolean(CatchupMathProperties.getInstance().getProperty("prescription.allow_rpa", "true"));
            boolean allowDynamicProblemSets =  !allowFlashWidgetsOnServer || !clientEnvironment.isFlashEnabled(); 
            if(allowDynamicProblemSets) {
                for(SessionData sd: filteredData) {
                    RppWidget rpp = sd.getRpp();
                    if (rpp.isDynamicSolution()) {
                        sessionItems.add(new SessionData(itemData.getInmhItem(), rpp, (int) PID_COUNT, itemData.getWeight(), rpp.getWidgetJsonArgs()));
                    }
                }
            }
            
            
            /** second pass to fill in missing
             *  with static RPPs
             *  
             */
            if(sessionItems.size() == 0) {
                for(SessionData sd: filteredData) {
                    RppWidget rpp = sd.getRpp();
                    if(!rpp.isDynamicSolution()) {
                        sessionItems.add(sd);
                        if(sessionItems.size() >= PID_COUNT) {
                            break;
                        }
                    }
                }
            }
        }
        return session;
    }

    protected int getHighestGradeLevel(List<RppWidget> rpps) throws Exception {
        int heighestLevel = 0;
        for (RppWidget w : rpps) {
            if (w.isSolution()) {
                int level = new ProblemID(w.getFile()).getGradeLevel();
                if (level > heighestLevel) {
                    heighestLevel = level;
                }
            }
        }
        return heighestLevel;
    }

    
    protected int getLowestGradeLevel(List<RppWidget> rpps) throws Exception {
        int lowestLevel = 99999;
        for (RppWidget w : rpps) {
            if (w.isSolution()) {
                int level=0;
                level = new ProblemID(w.getFile()).getGradeLevel();
                
                if (level < lowestLevel) {
                    lowestLevel = level;
                }
            }
        }
        return lowestLevel;
    }

    
    
    /**
     * implement the grade_level filter.
     * 
     * create list of possible PIDS looking at grade level.
     * 
     * favor exact match 2. do not consider higher grade levels 3. choose top
     * three.
     * 
     * 
     * do this is possible two passes. First pass looks for three that have the
     * exact grade_level match. If that fails, then select the rest from
     */
    private List<SessionData> filterRppsByGradeLevel(int testDefGradeLevel, List<RppWidget> rppWidgets,
            InmhItemData itemData) throws Exception {

        List<SessionData> session = new ArrayList<AssessmentPrescription.SessionData>();

        
//        /** create a sorted list with dynamic solutions first.  That way they will be 
//         *  added to the prescription before 'raw' PIDS.
//         */
//        Collections.sort(rppWidgets, new Comparator<RppWidget>() {
//            @Override
//            public int compare(RppWidget o1, RppWidget o2) {
//                return o1.getWidgetJsonArgs()!=null?0:1;
//            }
//        });

        
        List<RppWidget> maybeList = new ArrayList<RppWidget>();
        for (RppWidget rpp : rppWidgets) {
            if(rpp.isFlashRequired())
                continue;
            ProblemID pid = new ProblemID(rpp.getFile());

            int pidGradeLevel = pid.getGradeLevel();
            if (pidGradeLevel == testDefGradeLevel) {
                logger.debug("adding exact grade level match or dynamnic: " + rpp);
                session.add(new SessionData(itemData.getInmhItem(), rpp, PID_COUNT, itemData.getWeight()));

            } else if (pidGradeLevel < testDefGradeLevel) {
                /**
                 * might be used
                 */
                maybeList.add(rpp);
            } else if (pidGradeLevel > testDefGradeLevel) {
                /**
                 * completely filtered out
                 * 
                 */
                logger.debug("AssessmentPrescriptionSession: " + itemData.getInmhItem() + testRun.getRunId()
                        + ", level: " + testDefGradeLevel + ", inmh item not included due to higher grade level:  "
                        + pid + ", level: " + pid.getGradeLevel());
                continue;
            }
        }

        if (maybeList.size() > 0) {
            logger.debug("finding best match from: " + maybeList.size());
            /**
             * take first three from maybe list
             * 
             */
            try {
                for (int i = 0; i < maybeList.size(); i++) {
                    RppWidget rpp = maybeList.get(i);
                    session.add(new SessionData(itemData.getInmhItem(), rpp, PID_COUNT, itemData.getWeight()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return session;
    }

    /**
     * Return all INMH items that are referenced by session data
     * 
     * @param type
     * @param sessionData
     * @return
     * @throws HotMathException
     */
    public List<INeedMoreHelpItem> getInmhItemsFor(final Connection conn, int runId, String type,
            List<SessionData> sessionData) throws HotMathException {
        List<INeedMoreHelpItem> items = new ArrayList<INeedMoreHelpItem>();

        for (SessionData sd : sessionData) {
            INeedMoreHelpItem inmhItems[] = INeedMoreHelpManager.getInstance().getHelpItems(conn, "runId=" + runId, sd.getRpp().getFile());
            for (INeedMoreHelpItem inmhItem : inmhItems) {
                if (inmhItem.getType().equals(type)) {
                    items.add(inmhItem);
                }
            }
        }
        return items;
    }

    public HaTest getTest() {
        return getTestRun().getHaTest();
    }

    /**
     * Return the sum of all the weights for the inmh items associated with this
     * assessment.
     * 
     * @return
     */
    public int getSumOfWeights() {
        int sum = 0;
        for (InmhItemData id : _assessment.getInmhItemUnion(null)) {
            sum += id.getWeight();
        }
        return sum;
    }

    public int getCountMissed() {
        return missed;
    }

    /**
     * Return list of all sessions defined for this prescription
     * 
     * @return
     */
    public List<AssessmentPrescriptionSession> getSessions() {
        return _sessions;
    }

    /**
     * Find all solutions in this prescription, then get list of all INMH items
     * referenced ... then Return JSON string representing each session status
     * as follows
     * 
     * {is_ready:false, sessions: [{is_ready:false},{is_ready:true}] }
     * 
     * @return
     * @throws Exception
     */
    public String getSessionStatusJson() throws Exception {
        Connection conn = null;
        PreparedStatement pstat = null;
        String json = "";
        try {

            // Get list of all solutions currently viewed on this run
            String sql = "select distinct item_file " + " from  HA_TEST_RUN_INMH_USE " + " where  run_id = ? "
                    + " and    item_type = 'solution'";
            conn = HMConnectionPool.getConnection();
            List<String> pids = new ArrayList<String>();
            try {
                pstat = conn.prepareStatement(sql);
                pstat.setInt(1, this.getTestRun().getRunId());
                ResultSet rs = pstat.executeQuery();
                while (rs.next()) {
                    pids.add(rs.getString("item_file"));
                }
            } finally {
                pstat.close();
            }

            int cntNotReady = 0;
            for (AssessmentPrescriptionSession session : getSessions()) {
                for (INeedMoreHelpItem item : session.getSessionCategories()) {
                    List<AssessmentPrescription.SessionData> sessionData = session.getSessionDataFor(item.getTitle());
                    boolean found = false;
                    for (AssessmentPrescription.SessionData sdata : sessionData) {
                        String sdataPid = sdata.getRpp().getFile();
                        // at least one of these solutions should be been viewed
                        for (String pid : pids) {
                            if (pid.equals(sdataPid)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        cntNotReady++;
                    }
                }

                json += json.length() > 0 ? "," : "";
                json += "{is_ready:";
                if (cntNotReady > 0)
                    json += "false";
                else
                    json += "true";
                json += "}";
                cntNotReady = 0;
            }

            json = "{sessions:[" + json + "]}";
            return json;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error looking up Hotmath Advance test: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }

    
    
    /** return true if this prescription requires Flash 
     *  on the client.
     *  
     */
    public boolean dependsOnFlash() {
        for(AssessmentPrescriptionSession s: getSessions()) {
            for(SessionData item: s.getSessionItems()) {
                
                if(item.getRpp().getFile().indexOf(".swf") > -1) {
                    return true;
                }
                    
            }
        }
        
        return false;
    }
    
    /**
     * Return the URL used to load this test assessment
     * 
     * @return
     */
    public String getPrescriptionUrl() {
        return "assessment-prescription-session.jsp?run_id=" + this.testRun.getRunId();
    }

    /**
     * Determine the next action to take
     * 
     * @return
     */
    public CmProgramFlowAction getNextAction() throws Exception {
        return new CmProgramFlowAction(CmPlace.PRESCRIPTION);
    }

    /**
     * Check to see if this prescription is equaled to requested
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AssessmentPrescription) {
            AssessmentPrescription ap1 = (AssessmentPrescription) obj;

            boolean isMatch = false;
            if (ap1.getSessions().size() == getSessions().size()) {
                for (int i = 0, t = ap1.getSessions().size(); i < t; i++) {
                    AssessmentPrescriptionSession s1 = ap1.getSessions().get(i);
                    AssessmentPrescriptionSession s2 = getSessions().get(i);
                    if (!s1.getTopic().equals(s2.getTopic())) {
                        return false;
                    }
                }
                isMatch = true;
            }
            return isMatch;
        } else
            return equals(obj);
    }

    /** Represents a single item in a given session */
    static public class SessionData {
        RppWidget rpp;
        INeedMoreHelpItem item;
        int numPids, weight;
        String widgetArgs;

        public SessionData(INeedMoreHelpItem item, RppWidget rpp, int numPids, int weight) {
            this.item = item;
            this.rpp = rpp;
            this.numPids = numPids;
            this.weight = weight;
        }

        public SessionData(INeedMoreHelpItem item, RppWidget rpp, int numPids, int weight, String widgetArgs) {
            this(item, rpp, numPids, weight);
            this.widgetArgs = widgetArgs;
        }

        public String getWidgetArgs() {
            return widgetArgs;
        }

        public void setWidgetArgs(String widgetArgs) {
            this.widgetArgs = widgetArgs;
        }

        public int getNumPids() {
            return numPids;
        }

        public void setNumPids(int numPids) {
            this.numPids = numPids;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public RppWidget getRpp() {
            return rpp;
        }

        /**
         * Return the GradeLevel for this pid
         * 
         * @return
         */
        public int getGradeLevel() throws Exception {
            return BookInfoManager.getInstance().getBookInfo(new ProblemID(this.getRpp().getFile()).getBook()).getGradeLevel();

        }

        public void setRpp(RppWidget rpp) {
            this.rpp = rpp;
        }

        public INeedMoreHelpItem getItem() {
            return item;
        }

        public void setItem(INeedMoreHelpItem item) {
            this.item = item;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof String)
                return this.getRpp().getFile().equals(obj);
            else if (obj instanceof SessionData)
                return this.getRpp().getFile().equals(((SessionData) obj).getRpp().getFile());
            else
                return super.equals(obj);
        }
    }

}
