package hotmath.testset.ha;

import java.sql.Connection;
import java.util.List;

public class HaTestDefPlacement extends HaTestDef {
	String TEXTCODE = "placement";
	String PLACEMENT_CHAPTERS[] = {"prealg", "alg1", "geo", "alg2"};

	public HaTestDefPlacement(final Connection conn, String name) throws Exception {
		HaTestDef td = new HaTestDefDao().getTestDef(conn, name);
		super.init(td);
	}

	/** Return set of 4 tests of 7 questions, taken from each subject:
	 * 
	 * 
	 * There will be 4 tests with 7 questions each (prealgptests, alg1, gcgeopractice, alg2).
	 * 
	 */

	@Override
	public List<String> getTestIdsForSegment(final Connection conn, int segment, HaTestConfig config, int testSegmentSlot) throws Exception {
		_lastSegment = segment;

		if(testSegmentSlot == 0)
		    testSegmentSlot++;

		HaTestDefDao dao = new HaTestDefDao();
		return dao.getTestIdsForPlacementSegment(conn, segment, this.TEXTCODE, this.PLACEMENT_CHAPTERS[segment-1], config, testSegmentSlot);
    }

	@Override
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

	@Override
	public int getTotalSegmentCount() {
		return 4;
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
