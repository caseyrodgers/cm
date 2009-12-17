package hotmath.testset.ha;

import static hotmath.cm.util.CmCacheManager.CacheName.TEST_DEF;
import hotmath.HotMathException;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/** Provide DAO functionality for HaTestDefs
 * 
 * @author casey
 *
 */
public class HaTestDefDao {
    
    static Logger logger = Logger.getLogger(HaTestDefDao.class);
    
    public List<String> getTestNames() throws CmException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        List<String> names = new ArrayList<String>();
        try {
            String sql = "select test_name from HA_TEST_DEF order by test_def_id";
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                names.add(rs.getString(1));   
            }
            
            return names;
        }
        catch (Exception e) {
            logger.error(e);
            throw new CmException(e);
        }
        finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
    }
    
    
    /** Look up existing HaTestDef based on the name
     * 
     * @param conn  The connection to use
     * @param name the name of the test def
     *  
     * @throws HotMathException
     */
     public HaTestDef getTestDef(final Connection conn, String name) throws Exception {
         // try cache first
         HaTestDef td = (HaTestDef)CmCacheManager.getInstance().retrieveFromCache(TEST_DEF, name);
         if (td != null) {
             return td;
         }
         
         PreparedStatement pstat = null;
         ResultSet rs = null;
         try {
             String sql = "select * from HA_TEST_DEF where test_name = ? ";

             pstat = conn.prepareStatement(sql);

             pstat.setString(1, name);
             rs = pstat.executeQuery();
             if (!rs.first())
                 throw new Exception("Test definition not found");

             return loadRecord(rs);
             
         } catch (HotMathException hme) {
             throw hme;
         } catch (Exception e) {
             throw new HotMathException(e, "Error getting test definition for name: " + name + ", " + e.getMessage());
         } finally {
             SqlUtilities.releaseResources(rs, pstat, null);
         }
    }
     
     
     /** Look up HaTestDef based on the testDefId
      *
      * @param conn  The connection to use
      * @param name the id of the test def
      *  
      * @throws HotMathException
      */
     public HaTestDef getTestDef(final Connection conn, int testDefId) throws Exception {
         // try cache first
         HaTestDef td = (HaTestDef)CmCacheManager.getInstance().retrieveFromCache(TEST_DEF, String.valueOf(testDefId));
         if (td != null) {
        	 return td;
         }
         
         PreparedStatement pstat = null;
         ResultSet rs = null;
         try {
             String sql = "select * from HA_TEST_DEF where test_def_id = ? ";

             pstat = conn.prepareStatement(sql);

             pstat.setInt(1, testDefId);
             rs = pstat.executeQuery();
             if (!rs.first())
                 throw new Exception("Test definition not found");

             return loadRecord(rs);            
             
         } catch (HotMathException hme) {
             throw hme;
         } catch (Exception e) {
             throw new HotMathException(e, "Error getting test definition for id: " + testDefId + ", " + e.getMessage());
         } finally {
             SqlUtilities.releaseResources(rs, pstat, null);
         }
     }
     
     
     
     /**
      * Return list of chapter names for book associated with this program
      * 
      * @return
      * @throws Exception
      */
     public List<String> getProgramChapters(HaTestDef def) throws Exception {
         
         Connection conn = null;
         PreparedStatement pstat = null;
         ResultSet rs = null;
         List<String> chapters = new ArrayList<String>();
         try {
             String sql = "select title " + " from BOOK_TOC t " + " where level = 2 " + " and textcode = ?"
                     + " order by cast(title_number as unsigned) ";

             conn = HMConnectionPool.getConnection();
             pstat = conn.prepareStatement(sql);

             pstat.setString(1, def.textCode);

             rs = pstat.executeQuery();
             while (rs.next()) {
                 chapters.add(rs.getString("title"));
             }
             return chapters;
         } catch (HotMathException hme) {
             throw hme;
         } catch (Exception e) {
             throw new HotMathException(e, "Error getting program chapters: " + e.getMessage());
         } finally {
             SqlUtilities.releaseResources(rs, pstat, conn);
         }
     }
     
     

     /**
      * Each test is made up of 40 questions.
      * 
      * These questions are divided up into 4 segments of 10 questions each.
      * 
      * items are return in normal solution sort order
      * 
      * @TODO: allow setting multiple chapters
      * @TODO: move into factory pattern
      * 
      * @param conn
      * @param conn Active connection passed in
      * @param segment
      * @param textcode
      * @param chapter
      * @return
      * @throws SQLException
      */
     public List<String> getTestIdsForSegment(final Connection conn, int segment, String textcode, String chapter, HaTestConfig config, int segmentSlot)  throws Exception {

         // Use chapter from config if available, otherwise
         // use the default chapter defined for this test_def

                 
         List<String> problemIds = getTestIds(conn, textcode, chapter, segmentSlot,0,99999,config);

         int cnt = problemIds.size();
         
         // how does the total test break into segments?
         int solsPerSeg = (config != null) ? solsPerSeg = cnt / config.getSegmentCount() : 0;
         solsPerSeg = (solsPerSeg < 5) ? cnt : solsPerSeg;

         int segPnEnd = (segment * solsPerSeg);
         int segPnStart = (segPnEnd - (solsPerSeg - 1));

         problemIds = getTestIds(conn, textcode, chapter, segmentSlot,segPnStart,segPnEnd,config);
         if (problemIds.size() == 0) {
             throw new HotMathException(String.format("No problems for test segment: %s, %s, %n, %n, %n", textcode, chapter,segPnStart, segPnEnd, segmentSlot));
         }
         return problemIds;
     }
     
     
     /** Return list of problem ids that match this set
      * 
      * @param conn
      * @param textcode
      * @param chapter
      * @param section
      * @param startProblemNumber
      * @param endProblemNumber
      * @param config
      * @return
      * @throws Exception
      */
     public List<String> getTestIds(final Connection conn, String textcode, String chapter, int section, int startProblemNumber, int endProblemNumber, HaTestConfig config) throws Exception {
         
         PreparedStatement ps=null;
         ResultSet rs=null;
         try {
             String sql = "";
             if (config != null && config.getChapters().size() > 0) {
                 /** Is a chapter program
                  * 
                  */
                 chapter = config.getChapters().get(0);
                 
                 sql = "SELECT problemindex " + " FROM   SOLUTIONS s "
                         + "  INNER JOIN BOOK_TOC b on b.textcode = s.booktitle " + " WHERE s.BOOKTITLE = ? "
                         + " and   b.title = ? " + " and   b.level = 2 "
                         + " and   (s.chaptertitle = b.title_number AND s.SECTIONTITLE = ?) "
                         + " and  problemnumber between ? and ? ";
             } else {
                 
                 
                 sql = "SELECT problemindex " + 
                       " FROM   SOLUTIONS " + 
                       " WHERE (SOLUTIONS.BOOKTITLE = ? " + 
                       " and  (SOLUTIONS.CHAPTERTITLE = ? AND SOLUTIONS.SECTIONTITLE = ?)) " +
                       " and  problemnumber between ? and ? ";
             }
             
             ps = conn.prepareStatement(sql);
             
             ps.setString(1, textcode);
             ps.setString(2, chapter);
             ps.setInt(3, (section+1));  // test_segment_slots are zero based
             ps.setInt(4, startProblemNumber);
             ps.setInt(5, endProblemNumber);
             
             logger.info(ps);
             
             
             rs = ps.executeQuery();
             
             List<String> pids = new ArrayList<String>();
             while (rs.next()) {
                 pids.add(rs.getString(1));
             }
             return pids;
         }
         finally {
             SqlUtilities.releaseResources(null,ps,null);
         }
     }
     

     /** Return the Chapter info for named program or null
      * 
      * @param conn
      * @param programInfo
      * @return The chapter info or null if no chapter
      * @throws Exception
      */
     public ChapterInfo getChapterInfo(final Connection conn, StudentUserProgramModel programInfo) throws Exception {
         // pass along title and title number
         HaTestConfig config = programInfo.getConfig();
         
         String chapter = config.getChapters().size() > 0?config.getChapters().get(0):null;
         if(chapter != null) {
             ChapterInfo ci = new ChapterInfo();
             /** If chapter is specified then add the chapter number
              *  to the title
              *  
              */
             ci.setChapterTitle(chapter);
             HaTestDefDao tdo = new HaTestDefDao();
             List<String> chapters = tdo.getProgramChapters(tdo.getTestDef(conn,programInfo.getTestDefId()));
             for(int i=0, t=chapters.size();i<t;i++) {
                 if(chapters.get(i).equals(chapter)) {
                     ci.setChapterNumber(i+1);
                     return ci;
                 }
                     
             }
         }
         return null;
     }

	private HaTestDef loadRecord(ResultSet rs) throws SQLException, HotMathException {
		HaTestDef testDef = new HaTestDef();
		testDef.name = rs.getString("test_name");
		testDef.textCode = rs.getString("textcode");
		testDef.chapter = rs.getString("chapter");
		testDef.testDefId = rs.getInt("test_def_id");
		testDef.config = new HaTestConfig(rs.getString("test_config_json"));
		testDef.subjectId = rs.getString("subj_id");
		testDef.progId = rs.getString("prog_id");
		testDef.stateId = rs.getString("state_id");
		
		CmCacheManager.getInstance().addToCache(CmCacheManager.CacheName.TEST_DEF, testDef.getName(), testDef);
		CmCacheManager.getInstance().addToCache(CmCacheManager.CacheName.TEST_DEF, String.valueOf(testDef.getTestDefId()), testDef);
		
		return testDef;
	}
}


