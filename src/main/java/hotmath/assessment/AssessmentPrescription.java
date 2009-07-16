package hotmath.assessment;

import hotmath.BookInfoManager;
import hotmath.HotMathException;
import hotmath.ProblemID;
import hotmath.gwt.cm_tools.client.ui.NextAction;
import hotmath.gwt.cm_tools.client.ui.NextAction.NextActionName;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpManager;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestRun;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sb.logger.SbLogger;

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

    final static public int TOTAL_SESSION_SOLUTIONS = 3;
    final static public int MAX_SESSIONS = 100;

    InmhAssessment _assessment;
    public InmhAssessment get_assessment() {
		return _assessment;
	}

	public void set_assessment(InmhAssessment _assessment) {
		this._assessment = _assessment;
	}

	List<AssessmentPrescriptionSession> _sessions = new ArrayList<AssessmentPrescriptionSession>();
    int missed, totalPrescription;

    HaTestRun testRun;
    

    /** Return the grade level of this current program
     *
     * This will be determined by the textcode's subject's grade_level
     * found in textcode assigned to the test_def
     * 
     * @return
     */
    public int getGradeLevel() {
        return getTestRun().getHaTest().getTestDef().getGradeLevel();
    }

    public HaTestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(HaTestRun testRun) {
        this.testRun = testRun;
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
        
        this.testRun = testRun;
        _assessment = new InmhAssessment(testRun.getPidList());
        missed = _assessment.getPids().length;


        List<InmhItemData> itemsData = _assessment.getInmhItemUnion("review");
        int sumOfWeights = 0;
        for (InmhItemData id : itemsData) {
            sumOfWeights += id.getWeight();
        }

        int cnt = itemsData.size();
        totalPrescription = itemsData.size() * TOTAL_SESSION_SOLUTIONS;
        
        // assign weights to items
        int sessNum = 0;
        for (InmhItemData id : itemsData) {
            int wi = id.getWeight();

            double numPids = ((double) wi / (double) sumOfWeights) * totalPrescription;
            int numPids2get = (int) Math.ceil(numPids);
            // now choose tham many pids from the pool for this item
            List<ProblemID> workBookPids = id.getWookBookSolutionPool(conn);
            if (workBookPids.size() == 0)
                continue; // nothing to see here.

            AssessmentPrescriptionSession session = new AssessmentPrescriptionSession(this,"Session: " + (sessNum + 1));
            for(ProblemID pid: workBookPids) {

                // subject filter solutions
                int gradeLevel = pid.getGradeLevel();
                if (gradeLevel > getGradeLevel()) {
                    SbLogger.postMessage("AssessmentPrescriptionSession: " + testRun.getRunId() + ", level: " + getGradeLevel() + ", inmh item not included due to higher grade level:  " + pid + ", level: " + gradeLevel);
                    continue;
                }
                    
                    
                List<SessionData> si = session.getSessionItems();

                si.add(new SessionData(id.getInmhItem(), pid.getGUID(), (int) numPids2get, id.getWeight()));
 
                if (si.size() > TOTAL_SESSION_SOLUTIONS-1)
                    break;
            }
            
            // assert that there is at least one
            if(session.getSessionItems().size() == 0) {
                // this session has no items, so it is invalid and will be
                // skipped
                SbLogger.postMessage("AssessmentPrescriptionSession: session has no items: " + session);
            }
            else {
                // add this session, and move to next
               _sessions.add(session);
               sessNum++;
            }
        }
    }

    /** Return all INMH items that are referenced by session data
     * 
     * @param type
     * @param sessionData
     * @return
     * @throws HotMathException
     */
    public List<INeedMoreHelpItem> getInmhItemsFor(String type,
            List<SessionData> sessionData) throws HotMathException {
        List<INeedMoreHelpItem> items = new ArrayList<INeedMoreHelpItem>();
        
        for(SessionData sd: sessionData) {
            INeedMoreHelpItem inmhItems[] = INeedMoreHelpManager.getInstance().getHelpItems(sd.getPid());
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
     * Return the sum of all the weights associated with the the inmh items
     * associated with this assessment.
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

    public AssessmentPrescriptionSession getSession(String name)
            throws HotMathException {
        for (AssessmentPrescriptionSession s : _sessions) {
            if (s.getName().equals(name))
                return s;
        }

        throw new HotMathException("Could not find session: " + name);
    }



    /** Read the appropriate Prescription related inmh items.
     * 
     *  1. all related videos
     *  2. 
     * @return
     * @throws Exception
     */
    public List<INeedMoreHelpItem> readInmhItems() throws Exception {
    	String types[] = {"video","review","lessons","workbook"};
    	// INeedMoreHelpManager.getInstance().getHelpItemsUnion(session.getSessionProblemIds(),types)
    	return null;
    }

    
    
    
    /** Find all solutions in this prescription, then get list of
     *  all INMH items referenced ... then
     *  Return JSON string representing each session status as follows

       {is_ready:false,
       sessions: [{is_ready:false},{is_ready:true}]
       }
             
     * @return
     * @throws Exception
     */
    public String getSessionStatusJson() throws Exception {
        Connection conn=null;
        PreparedStatement pstat=null;
        String json = "";
        try {
            
            // Get list of all solutions currently viewed on this run
            String sql = "select distinct item_file " +
                         " from  HA_TEST_RUN_INMH_USE " +
                         " where  run_id = ? " +
                         " and    item_type = 'solution'";
            conn = HMConnectionPool.getConnection();
            List<String> pids = new ArrayList<String>();
            try {
                pstat = conn.prepareStatement(sql);
                pstat.setInt(1,this.getTestRun().getRunId());
                ResultSet rs = pstat.executeQuery();
                while(rs.next()) {
                    pids.add(rs.getString("item_file"));
                }
            }
            finally {
                pstat.close();
            }

            int cntNotReady=0;
            for(AssessmentPrescriptionSession session: getSessions()) {
                 for(INeedMoreHelpItem item: session.getSessionCategories()) {
                      List<AssessmentPrescription.SessionData> sessionData = session.getSessionDataFor(item.getTitle());
                      boolean found=false;            
                      for(AssessmentPrescription.SessionData sdata: sessionData) {
                          String sdataPid = sdata.getPid();
                          // at least one of these solutions should be been viewed
                          for(String pid: pids) {
                              if(pid.equals(sdataPid)) {
                                  found=true;
                                  break;
                              }
                          }
                      }
                     if(!found) {
                         cntNotReady++;
		     }
                 }

                 json += json.length() > 0?",":"";
		         json += "{is_ready:";
                 if(cntNotReady > 0)
                     json += "false";
                 else
                     json += "true";
                 json += "}";
                 cntNotReady = 0;
            }
            
            json = "{sessions:[" + json + "]}";
            return json;
        }
        catch(HotMathException hme) {
            throw hme;
        }
        catch(Exception e) {
            throw new HotMathException(e,"Error looking up Hotmath Advance test: " + e.getMessage());
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,conn);
        }
    }
    
    
    /** Return the URL used to load this test assessment
     * 
     * @return
     */
    public String getPrescriptionUrl() {
    	return "assessment-prescription-session.jsp?run_id=" +this.testRun.getRunId();        	
    }
    
    
    
    /** Determine the next action to take
     * 
     * @return
     */
    public NextAction getNextAction() {
        return new NextAction(NextActionName.PRESCRIPTION); 
    }
    
    /** Represents a single item in a given session */
    static public class SessionData {
        String pid;
        INeedMoreHelpItem item;
        int numPids, weight;

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

        public SessionData(INeedMoreHelpItem item, String pid, int numPids,
                int weight) {
            this.item = item;
            this.pid = pid;
            this.numPids = numPids;
            this.weight = weight;
        }

        public String getPid() {
            return pid;
        }
        
        
        /** Return the GradeLevel for this pid
         * 
         * @return
         */
        public int getGradeLevel() throws Exception {
            return BookInfoManager.getInstance().getBookInfo(new ProblemID(this.pid).getBook()).getGradeLevel();
            
        }

        public void setPid(String pid) {
            this.pid = pid;
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
                return this.pid.equals(obj);
            else if (obj instanceof SessionData)
                return this.pid.equals(((SessionData) obj).getPid());
            else
                return super.equals(obj);
        }
    }
    
}
