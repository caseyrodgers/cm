package hotmath.testset.ha;

import hotmath.BookInfo;
import hotmath.BookInfoManager;
import hotmath.HotMathException;
import hotmath.HotMathLogger;
import hotmath.ProblemID;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** A single Hotmath Advance test definition.
 * 
 * Each test definition is made up of:
 * 
 * 1. textcode
 * 2. chapter
 * 
 * Each test has 4 segments.
 * 
 * Each segment is made up of 10 questions
 * 
 * Each question is one from a set of related questions
 * 
 * 
 * each question's related question is taken using the same number (1-n, where is total in related pool).
 * 
 *  
 * 
 * 
 * 
 * @author Casey
 *
 */
public class HaTestDef {

	String name;
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	int indexRelatedPool;
	String textCode;
	String chapter;
	int testDefId;
	
	public HaTestDef(String name) throws HotMathException {
		this.name = name;
	 	Connection conn=null;
	    PreparedStatement pstat=null;
	    try {
	        String sql = "select * " +
	                     " from HA_TEST_DEF d " +
	                     " where test_name = ? ";
	        
	        conn = HMConnectionPool.getConnection();
	        pstat = conn.prepareStatement(sql);
	        
	        pstat.setString(1, this.name);
	        
	        ResultSet rs = pstat.executeQuery();
	        if(!rs.first())
	        	throw new Exception("Test definition not found");
	        

	        this.textCode = rs.getString("textcode");
	        this.chapter = rs.getString("chapter");
	        this.testDefId = rs.getInt("test_def_id");
	    }
	    catch(HotMathException hme) {
	        throw hme;
	    }
	    catch(Exception e) {
	        throw new HotMathException(e,"Error getting test definition pids: " + e.getMessage());
	    }
	    finally {
	        SqlUtilities.releaseResources(null,pstat,conn);
	    }    				
		
		indexRelatedPool = getRelatedPoolIndex();
	}
	
	
	// used as internal heading for title
	// subclasses can override and provide a 
	// per segment title
	public String getSubTitle(int segment) {
	    return "";	
	}
	
	public HaTestDef(int testDefId) throws HotMathException {
		this.name = name;
	 	Connection conn=null;
	    PreparedStatement pstat=null;
	    try {
	        String sql = "select * " +
	                     " from HA_TEST_DEF d " +
	                     " where test_def_id = ? ";
	        
	        conn = HMConnectionPool.getConnection();
	        pstat = conn.prepareStatement(sql);
	        
	        pstat.setInt(1, testDefId);
	        
	        ResultSet rs = pstat.executeQuery();
	        if(!rs.first())
	        	throw new Exception("Test definition not found");
	        
            this.name = rs.getString("test_name");
	        this.textCode = rs.getString("textcode");
	        this.chapter = rs.getString("chapter");
	        this.testDefId = rs.getInt("test_def_id");
	    }
	    catch(HotMathException hme) {
	        throw hme;
	    }
	    catch(Exception e) {
	        throw new HotMathException(e,"Error getting test definition pids: " + e.getMessage());
	    }
	    finally {
	        SqlUtilities.releaseResources(null,pstat,conn);
	    }    				
		
		indexRelatedPool = getRelatedPoolIndex();
	}
	
	
	/** Return the page to use as the test page
	 * 
	 * @return
	 */
	public String getTestPage() {
		return "/testsets/testset_assessment.jsp";
	}
	
	
	public String getTextCode() {
		return textCode;
	}


	public void setTextCode(String textCode) {
		this.textCode = textCode;
	}


	public String getChapter() {
		return chapter;
	}


	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	
	int gradeLevel=0;
	/** Return the grade level for this program
	 *  or return -1 on error.
	 * 
	 * 
	 * 
	 */
	public int getGradeLevel() {
		try {
			return BookInfoManager.getInstance().getBookInfo(this.getTextCode()).getGradeLevel();
		}
		catch(HotMathException hme) {
			HotMathLogger.logMessage(hme, "Error getting grade level");
		}
		return -1;
	}


	/** Return list of chapter names for book associated with this program
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> getProgramChapters() throws Exception {
	 	Connection conn=null;
	    PreparedStatement pstat=null;
	    List<String> chapters = new ArrayList<String>();
	    try {
	        String sql = "select title " +
	                     " from BOOK_TOC t " +
	                     " where level = 2 " +
	                     " and textcode = ?" +
	                     " order by cast(title_number as unsigned) ";
	        
	        conn = HMConnectionPool.getConnection();
	        pstat = conn.prepareStatement(sql);
	        
	        pstat.setString(1, this.textCode);
	        
	        ResultSet rs = pstat.executeQuery();
	        while(rs.next()) {
	            chapters.add(rs.getString("title"));
	        }
	        return chapters;
	    }
	    catch(HotMathException hme) {
	        throw hme;
	    }
	    catch(Exception e) {
	        throw new HotMathException(e,"Error getting test definition pids: " + e.getMessage());
	    }
	    finally {
	        SqlUtilities.releaseResources(null,pstat,conn);
	    }
	}
	
	public int getTestDefId() {
		return testDefId;
	}


	public void setTestDefId(int testDefId) {
		this.testDefId = testDefId;
	}


	/** return the index problem to choose from the 
	 *  related questions for this test.
	 * @return
	 */
	private int getRelatedPoolIndex() {
		
		int howManyRelatedForEachQuestion = 2;
		return howManyRelatedForEachQuestion;
	}
	
	public BookInfo getBookInfo() throws HotMathException {
	    return BookInfoManager.getInstance().getBookInfo(textCode);
	}
	
	
	public String getTitle() {
		return name;
	}
	
	/** Return a list of ProblemIds that are used to populate this 
	 *  test's segment.
	 *  
	 * 
	 * 
	 * @return
	 * @throws HotMathException
	 */
	List<String> list;
	int _lastSegment;
	public List<String> getTestIdsForSegment(int segment,HaTestConfig config) throws Exception {
		_lastSegment = segment;
		return getTestIdsForSegment(segment, textCode,chapter,config);
	}
	
	
	
	/** Each test is made up of 40 questions.  
	 *   
	 *   These questions are divided up into 
	 *   4 segments of 10 questions each. 
	 *   
	 *   items are return in normal solution sort order
	 *  
	 *   @TODO: allow setting mulitple chapters
	 *   @TODO: move into factory pattern
	 *   
	 * @param conn
	 * @param segment
	 * @param textcode
	 * @param chapter
	 * @return
	 * @throws SQLException
	 */
	private List<String> getTestIdsForSegment(int segment, String textcode, String chapter,HaTestConfig config) throws Exception {
		
		// Use chapter from config if available, otherwise 
		// use the default chapter defined for this test_def

		
		List<String> pids = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps=null;
		
		
		int section = 1; // alternative tests are stored in separate sections
 
		try {
			conn = HMConnectionPool.getConnection();
			
			String sql = "";
			if(config != null && config.getChapters().size() > 0) {
				// just use first for now
				chapter = config.getChapters().get(0);
				
				sql = "SELECT problemindex " +
				      " FROM   SOLUTIONS s " +
				      "  INNER JOIN BOOK_TOC b on b.textcode = s.booktitle " +
				      " WHERE s.BOOKTITLE = ? " +
				      " and   b.title = ? " +
				      " and   b.level = 2 " +
				      " and   (s.chaptertitle = b.title_number && s.SECTIONTITLE = ?) " +
				      " and  problemnumber between ? and ? ";
			}
			else {
				sql = "SELECT problemindex " +
		        " FROM   SOLUTIONS " +
		        " WHERE (SOLUTIONS.BOOKTITLE = ? " +
		        " and  (SOLUTIONS.CHAPTERTITLE = ? && SOLUTIONS.SECTIONTITLE = ?)) " +
		        " and  problemnumber between ? and ? ";
			}

			ps = conn.prepareStatement(sql);
			
			ps.setString(1, textcode);
			ps.setString(2, chapter);
			ps.setInt(3,section);
			ps.setInt(4,0);
			ps.setInt(5,9999);
			
			// first run through and see how many total solutions there are ..
			

			ResultSet rs1 = ps.executeQuery();
			int cnt = 0;
			while(rs1.next()) {
				cnt++;
			}
			
		        // how does the total break into 4 segments?
			int solsPerSeg = cnt / 4;
                        solsPerSeg = (solsPerSeg < 5)?cnt:solsPerSeg;
			
			int segPnEnd = (segment * solsPerSeg);
			int  segPnStart = (segPnEnd - (solsPerSeg-1));
			
			ps.setInt(4,segPnStart);
			ps.setInt(5,segPnEnd);
			
			
			// execute query and build list of ids in test
			ResultSet rs = ps.executeQuery();
			if(!rs.first()) {
				throw new HotMathException("No problems for test segment: " + ps);
			}
			do {
		        String pid = rs.getString("problemindex");
		        pids.add(pid);
			}while(rs.next());
		}
		finally {
			SqlUtilities.releaseResources(null,ps,conn);
		}
		return pids;
	}
	
	public int getTotalSegmentCount() {
		return HaTest.SEGMENTS_PER_PROGRAM;
	}
	
	
	/** Return JSON string used to initialize this test or null

	 * @throws Exception
	 */
	public String getTestInitJson(HaTest test) throws Exception {
		return null;
	}	
}
