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
 * a list of all possible Lesson names associated with 
 * the test.
 * 
 * Is stored in CmCacheManager.TEST_DEF_DESCRIPTION for efficiency.
 * 
 * @author casey
 *
 */
public class HaTestDefDescription {
    
    HaTestDef testDef;
    List<String> lessonNames;
    
    public HaTestDefDescription(HaTestDef def, List<String> lessonNames) throws Exception {
        this.testDef = def;
        this.lessonNames = lessonNames;
    }
    
    
    public HaTestDef getTestDef() {
        return testDef;
    }


    public void setTestDef(HaTestDef testDef) {
        this.testDef = testDef;
    }


    public List<String> getLessonNames() {
        return lessonNames;
    }


    public void setLessonNames(List<String> lessonNames) {
        this.lessonNames = lessonNames;
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
            HaTestConfig config = new HaTestConfig(null);
            
            HaTestDefDao dao = new HaTestDefDao();
            List<String> pids = dao.getTestIds(conn, def.getTextCode(), def.getChapter(), 1, 0, 9999, config);
            
            InmhAssessment inmhAssessment = new InmhAssessment(pids.toArray( new String[pids.size()] ));
            List<InmhItemData> itemsData = inmhAssessment.getInmhItemUnion("review");
            
            List<String> lessons = new ArrayList<String>();
            for(InmhItemData id: itemsData) {
                lessons.add(id.getInmhItem().getTitle());
            }
            
            desc = new HaTestDefDescription(def, lessons);
            CmCacheManager.getInstance().addToCache(CacheName.TEST_DEF_DESCRIPTION, testName,desc);
            
            return desc;
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }
}
