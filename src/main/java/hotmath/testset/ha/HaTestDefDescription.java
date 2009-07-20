package hotmath.testset.ha;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.assessment.InmhItemData;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/** Class to describe a single HaTestDef. Provides 
 * a list of all possible Lesson Items associated with 
 * the test.
 * 
 * Is stored in CmCacheManager.TEST_DEF_DESCRIPTION for efficiency.
 * 
 * @author casey
 *
 */
public class HaTestDefDescription {
    
    HaTestDef testDef;
    Integer segment;
    List<InmhItemData> lessons;
    List<String> pids;
    int pidsInASegment;
    
    public HaTestDefDescription(int pidsInASegment, HaTestDef def,Integer segment, List<InmhItemData> lessons) throws Exception {
        this.testDef = def;
        this.segment = segment;
        this.lessons = lessons;
        
        this.pidsInASegment = pidsInASegment;        
    }
    
    public HaTestDefDescription() {
    }
    
    public HaTestDef getTestDef() {
        return testDef;
    }


    public void setTestDef(HaTestDef testDef) {
        this.testDef = testDef;
    }


    /** Returns all lessons for entire program
     * 
     * @return
     */
    public List<InmhItemData> getLessonItems() {
        return lessons;
    }


    public void setLessonItems(List<InmhItemData> lessonItems) {
        this.lessons = lessonItems;
    }

    
    /** Return list of lesson items defined for the
     *  named quiz segment.  This will be a subset
     *  of the total lessons defined for this program.
     *  
     *  quizSegment is one based. quizSegment < 1
     *  will result in an IllegalArgumentException.
     *  
     *  
     * @return
     */
    public List<InmhItemData> getLessonItems2(Integer quizSegment) {
    	
    	if (quizSegment < 1) {
    		throw new IllegalArgumentException(String.format("quizSegment: %d, must be greater than 0", quizSegment));
    	}
        
        int end = pidsInASegment * quizSegment;
        int start = end - pidsInASegment;
        
        /** Extract subset of pids used in this quiz segment
         * 
         */
        List<String> pidsInThisSegment = new ArrayList<String>();
        for(int i=start;i<end;i++) {
            pidsInThisSegment.add(pids.get(i));
        }
        
        /**  now find each lesson that references this segment's subset of pids
         * 
         */
        List<InmhItemData> quizSegmentLessons = new ArrayList<InmhItemData>();
        for(InmhItemData id: lessons) {
            
            // is this item referenced by at least one quiz segment pid
            boolean found=false;
            for(String p: pidsInThisSegment) {
                // for each pid in this test segment
                for(String itemPid: id.getPids()) {
                    // check to see if this pid is referenced by this item
                    if(p.equals(itemPid)) {
                        quizSegmentLessons.add(id);
                        found=true;
                        break;
                    }
                }
                
                if(found)
                    break;
            }
        }
        return quizSegmentLessons;
    }
    
    /** Return list of all pids in the program
     * 
     * @return
     */
    public List<String> getPids() {
        return pids;
    }

    public void setPids(List<String> pids) {
        this.pids = pids;
    }

    /** Return list of Lesson names based on this testName
     * 
     * @param testName
     * @return
     * @throws Exception
     */
     static public HaTestDefDescription getHaTestDefDescription(String testName, Integer quizSegment) throws Exception {

        String keyName = testName + "_" + quizSegment;
        HaTestDefDescription desc = (HaTestDefDescription)CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST_DEF_DESCRIPTION, keyName);
        if(desc != null)
            return desc;
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            
            HaTestDef def = new HaTestDef(conn, testName);
            HaTestConfig config = def.getTestConfig();
            
            HaTestDefDao dao = new HaTestDefDao();
            

            int totalPidsInProgram = dao.getTestIds(conn, def.getTextCode(), def.getChapter(), 1, 0, 99999, config).size();
            int pidsInASegment = totalPidsInProgram / def.getTotalSegmentCount();
            
            int end = pidsInASegment * quizSegment;
            int start = end - pidsInASegment;            
            
            /** Get list of all pids that are in this individal quiz in this program
             * 
             */
            List<String> pids = dao.getTestIds(conn, def.getTextCode(), def.getChapter(), 1, start, end, config);
            

            /** Create a dummy TestRun
             * 
             */
            HaTestRun testRun = new HaTestRun();
            HaTest test = new HaTest();
            test.setTestDef(def);
            testRun.setHaTest(test);
            /** put all pids as results in this test run
             * 
             */
            for(String p: pids) {
                HaTestRunResult r = new HaTestRunResult();
                r.setPid(p);
                testRun.getTestRunResults().add(r);
            }
            
            
            /** Create a new prescription based on this test run 
             * 
             */
            AssessmentPrescription pres = new AssessmentPrescription(conn, testRun);
            
            
            List<AssessmentPrescriptionSession> sessions = pres.getSessions();
            
            desc = new HaTestDefDescription();
            
            List<InmhItemData> lessons = new ArrayList<InmhItemData>();
            
            /** For each session in prescription (aka, lesson in program)
             * 
             *  Track both the lesson and each test pid that references it
             */
            for(AssessmentPrescriptionSession session: sessions) {
                
                /** Dummy lessonData used as holder of information
                 *  that will be used when accessing lessons belonging
                 *  to a single session.
                 * 
                 */
                InmhItemData lessonData = new InmhItemData();
                INeedMoreHelpItem item = new INeedMoreHelpItem();
                item.setTitle(session.getTopic());
                lessonData.setINeedMoreHelpItem(item);
                /** For each pid that references this session, add it 
                 *  to tracking object.
                 */
                String file=null;
                for(SessionData sessionData: session.getSessionItems()){
                    
                    /** All pids refernce the same Session title
                     * 
                     * @TODO: should this be inserted into session as getFile()?
                     */
                    if(file == null) {
                        file = sessionData.getItem().getFile();
                    }
                    
                    lessonData.getPids().add(sessionData.getPid());                    
                }
                
                item.setFile(file);
                
                /** Add this object to list of lesson names for this
                 *  complete assessment (program).
                 */
                lessons.add(lessonData);
            }
            
            desc.setPids(pids);
            desc.setTestDef(def);
            desc.setLessonItems(lessons);
            
            CmCacheManager.getInstance().addToCache(CacheName.TEST_DEF_DESCRIPTION, keyName,desc);
            
            return desc;
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }
}
