package hotmath.testset.ha;

import hotmath.HotMathException;
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
    
    
    /** Look up existing HaTestDef based on either the name, or the testDefId
     * 
     * 
     * @param conn  The connection to use
     * @param name the name of the test def
     *  
     * @throws HotMathException
     */
     public HaTestDef getTestDef(final Connection conn, String name) throws Exception {
         return new HaTestDef(conn, name);
    }
     
     
     public HaTestDef getTestDef(final Connection conn, int testDefId) throws Exception {
         PreparedStatement ps = null;
         ResultSet rs = null;
         try {
             String sql = "select test_name from HA_TEST_DEF where test_def_id = ?";
             ps = conn.prepareStatement(sql);
             ps.setInt(1,testDefId);
             rs = ps.executeQuery();
             if(!rs.first())
                 throw new Exception("No such test_def_id: " + testDefId);

             return getTestDef(conn, rs.getString("test_name"));
             
         }
         catch (Exception e) {
             logger.error(e);
             throw new CmException(e);
         }
         finally {
             SqlUtilities.releaseResources(rs, ps, null);
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
     public List<String> getTestIdsForSegment(final Connection conn, int segment, String textcode, String chapter, HaTestConfig config)  throws Exception {

         // Use chapter from config if available, otherwise
         // use the default chapter defined for this test_def

         int section = 1; // alternative tests are stored in separate sections
        
         List<String> problemIds = getTestIds(conn, textcode, chapter, section,0,99999,config);

         int cnt = problemIds.size();
         
         // how does the total test break into segments?
         int solsPerSeg = cnt / config.getSegmentCount();
         solsPerSeg = (solsPerSeg < 5) ? cnt : solsPerSeg;

         int segPnEnd = (segment * solsPerSeg);
         int segPnStart = (segPnEnd - (solsPerSeg - 1));

         problemIds = getTestIds(conn, textcode, chapter, section,segPnStart,segPnEnd,config);
         if (problemIds.size() == 0) {
             throw new HotMathException(String.format("No problems for test segment: %s, %s, %n, %n", textcode, chapter,segPnStart, segPnEnd));
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
                 // just use first for now
                 chapter = config.getChapters().get(0);
    
                 sql = "SELECT problemindex " + " FROM   SOLUTIONS s "
                         + "  INNER JOIN BOOK_TOC b on b.textcode = s.booktitle " + " WHERE s.BOOKTITLE = ? "
                         + " and   b.title = ? " + " and   b.level = 2 "
                         + " and   (s.chaptertitle = b.title_number && s.SECTIONTITLE = ?) "
                         + " and  problemnumber between ? and ? ";
             } else {
                 sql = "SELECT problemindex " + " FROM   SOLUTIONS " + " WHERE (SOLUTIONS.BOOKTITLE = ? "
                         + " and  (SOLUTIONS.CHAPTERTITLE = ? && SOLUTIONS.SECTIONTITLE = ?)) "
                         + " and  problemnumber between ? and ? ";
             }
    
             ps = conn.prepareStatement(sql);
    
             ps.setString(1, textcode);
             ps.setString(2, chapter);
             ps.setInt(3, section);
             ps.setInt(4, startProblemNumber);
             ps.setInt(5, endProblemNumber);
    
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
     
     
     /** Create the sub title for this program instance. 
      *  The title should be complete, containing any 
      *  chapter information.
      *  
      * @param conn
      * @param programInfo
      * @return
      * @throws Exception
      */
     public String getSubTitle(final Connection conn, StudentUserProgramModel programInfo) throws Exception {
         // pass along title and title number
         HaTestConfig config = programInfo.getConfig();
         
         String chapter = config.getChapters().size() > 0?config.getChapters().get(0):null;
         if(chapter != null) {
             /** If chapter is specified then add the chapter number
              *  to the title
              *  
              */
             HaTestDefDao tdo = new HaTestDefDao();
             List<String> chapters = tdo.getProgramChapters(tdo.getTestDef(conn,programInfo.getTestDefId()));
             for(int i=0, t=chapters.size();i<t;i++) {
                 if(chapters.get(i).equals(chapter)) {
                     chapter = "#" + (i+1) + " " + chapter;
                     break;
                 }
             }
         }
         return chapter!=null?chapter:"";
     }
}


