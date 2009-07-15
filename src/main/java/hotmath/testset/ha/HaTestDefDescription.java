package hotmath.testset.ha;

import hotmath.assessment.InmhAssessment;
import hotmath.assessment.InmhItemData;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
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
    List<InmhItemData> lessons;
    List<String> pids;
    
    public HaTestDefDescription(HaTestDef def,List<InmhItemData> lessons) throws Exception {
        this.testDef = def;
        this.lessons = lessons;
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
    public List<InmhItemData> getLessonItems(Integer quizSegment) {
    	
    	if (quizSegment < 1) {
    		throw new IllegalArgumentException(String.format("quizSegment: %d, must be greater than 0", quizSegment));
    	}
        
        int pidsInASegment = pids.size() / testDef.getTotalSegmentCount();
        
        int end = pidsInASegment * quizSegment;
        int start = end - pidsInASegment;
        
        /** Extract subset of pids used in this quiz segment
         * 
         */
        List<String> pidsInThisSegment = new ArrayList<String>();
        for(int i=start;i<end;i++) {
            pidsInThisSegment.add(pids.get(i));
        }
        
        /**  now find each lesson referenced this segments subset of pids
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
    static public HaTestDefDescription getHaTestDefDescription(String testName) throws Exception {
        HaTestDefDescription desc = (HaTestDefDescription)CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST_DEF_DESCRIPTION, testName);
        if(desc != null)
            return desc;
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            
            HaTestDef def = new HaTestDef(conn, testName);
            HaTestConfig config = def.getTestConfig();
            
            HaTestDefDao dao = new HaTestDefDao();
            List<String> pids = dao.getTestIds(conn, def.getTextCode(), def.getChapter(), 1, 0, 9999, config);
            
            InmhAssessment inmhAssessment = new InmhAssessment(pids.toArray( new String[pids.size()] ));
            List<InmhItemData> itemsData = inmhAssessment.getInmhItemUnion("review");
            
            desc = new HaTestDefDescription();
            List<InmhItemData> lessons = new ArrayList<InmhItemData>();
            for(InmhItemData id: itemsData) {
                lessons.add(id);
            }
            
            desc.setPids(pids);
            desc.setTestDef(def);
            desc.setLessonItems(lessons);
            
            CmCacheManager.getInstance().addToCache(CacheName.TEST_DEF_DESCRIPTION, testName,desc);
            
            return desc;
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }
}
