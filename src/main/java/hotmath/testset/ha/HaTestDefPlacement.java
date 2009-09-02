package hotmath.testset.ha;

import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HaTestDefPlacement extends HaTestDef {
	String TEXTCODE = "placement";
	String PLACEMENT_CHAPTERS[] = {"prealg","alg1", "geo", "alg2"};
	
	public HaTestDefPlacement(final Connection conn, String name) throws Exception {
		super(conn, name);
	}

	/** Return set of 4 tests of 7 questions, taken from each subject:
	 * 
	 * 
	 * There will be 4 tests with 7 questions each (prealgptests, alg1, gcgeopractice, alg2).
	 * 
	 */
	
	@Override
	public List<String> getTestIdsForSegment(Connection conn, int segment, HaTestConfig config, int testSegmentSlot) throws Exception {
		_lastSegment = segment;
		PreparedStatement ps=null;
		ResultSet rs = null;
		
		if(testSegmentSlot == 0)
		    testSegmentSlot++;

		try {
			// Create list of 7 random solutions from
			// each text group that is listed in the
			// placement test.
			List<String> list = new ArrayList<String>();
			String sql = "select problemindex " +
					     " from SOLUTIONS " + 
			             " where booktitle = ? " +
			             "  and chaptertitle = ? " +
			             "  and sectiontitle = ? " + 
			             "  order by problemnumber";
			ps = conn.prepareStatement(sql);
			
			ps.setString(1,TEXTCODE);
			ps.setString(2, this.PLACEMENT_CHAPTERS[segment-1]);
			ps.setInt(3, testSegmentSlot);
			
			rs = ps.executeQuery();
			if(!rs.first())
				throw new Exception("could not initialize HaTestDefPlacement: no rows found to initialize");
			do {
				list.add(rs.getString("problemindex"));
			}while(rs.next());
			return list;
		}
		finally {
			SqlUtilities.releaseResources(rs,ps,null);
		}		
	}
	
	public String getSubTitle(int segment) {
		String t = "Auto-Enrollment";
		switch(segment) {
		    case 0:
		    case 1:
		        t = "Pre-Algebra " + t;
		        break;
		    case 2:
		    	t = "Algebra 1 " + t;
		    	break;
		    case 3:
		    	t = "Geometry " + t;
		    	break;
		    case 4:
		    	t = "Algebra 2 " + t;
		    	break;
		}
		return t;
	}
	
	public int getTotalSegmentCount() {
		return 4;
	}
	
	
	public String getTestPage() {
		return super.getTestPage();
	}
	
	
	/** Return JSON string used to initialize this test
	 *  
	 *  Not all tests will have init code.  If they do
	 *  it should be in the form:
	 *  
	 *  {
	 *      count_correct:0,
	 *      pid:12_23_32_23_32
	 *  }
	 *  
	 *  use getActionTestSegment as the number of current correct answers
	 *  
	 *  
	 * @return  JSON string defined above
	 * @throws Exception
	 * 
	 */
	@Override
	public String getTestInitJson(HaTest test) throws Exception {
		return super.getTestInitJson(test);
	}
}
