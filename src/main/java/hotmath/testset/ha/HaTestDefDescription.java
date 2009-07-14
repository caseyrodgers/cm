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
    List<LessonItem> lessonItems;
    
    public HaTestDefDescription(HaTestDef def, List<LessonItem> lessonItems) throws Exception {
        this.testDef = def;
        this.lessonItems = lessonItems;
    }
    
    public HaTestDefDescription() {
    }
    
    public HaTestDef getTestDef() {
        return testDef;
    }


    public void setTestDef(HaTestDef testDef) {
        this.testDef = testDef;
    }


    public List<LessonItem> getLessonItems() {
        return lessonItems;
    }


    public void setLessonNames(List<LessonItem> lessonItems) {
        this.lessonItems = lessonItems;
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
            
            desc = new HaTestDefDescription();
            List<LessonItem> lessons = new ArrayList<LessonItem>();
            for(InmhItemData id: itemsData) {
                lessons.add(desc.new LessonItem(id.getInmhItem().getTitle(), id.getInmhItem().getFile()));
            }
            
            desc.setTestDef(def);
            desc.setLessonNames(lessons);
            
            CmCacheManager.getInstance().addToCache(CacheName.TEST_DEF_DESCRIPTION, testName,desc);
            
            return desc;
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }
    
    public class LessonItem {
    	private String name;
		private String file;
		
		public LessonItem(String name, String file) {
			this.name = name;
			this.file = file;
		}
    	
    	public String getName() {
			return name;
		}
    	
		public void setName(String name) {
			this.name = name;
		}
		
		public String getFile() {
			return file;
		}
		
		public void setFile(String file) {
			this.file = file;
		}
    }
}
