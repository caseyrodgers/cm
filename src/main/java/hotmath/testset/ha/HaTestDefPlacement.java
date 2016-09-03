package hotmath.testset.ha;

import hotmath.gwt.shared.client.CmProgram;

import java.util.List;

/** Placement HaTestDef that will automatically 
 *  assign a user based on current programs score.
 *  
 * @author casey
 *
 */
public class HaTestDefPlacement extends HaTestDef {
	
    
    public interface PlacementAdapter {
    	String getSubTitle(int segment);
        String[] getSegmentNames();
        int getTotalSegmentCount();
        CmProgram getNextProgram(String testName, int total, int correct);
    }
    

	public HaTestDefPlacement() {}
	
	PlacementAdapter _adapter;
	public HaTestDefPlacement(String name) throws Exception {
		HaTestDef td = HaTestDefDao.getInstance().getTestDef(name);
		super.init(td);
	}

	@Override
	public List<String> getTestIdsForSegment(StudentUserProgramModel program, int segment, HaTestConfig config, int testSegmentSlot) throws Exception {
	    
	    assert(_adapter != null);
	    
		_lastSegment = segment;

		if(testSegmentSlot == 0)
		    testSegmentSlot++;

		HaTestDefDao dao = HaTestDefDao.getInstance();
		
		return dao.getTestIdsForPlacementSegment(segment, program.getTestDef().getTextCode(), getAdapter().getSegmentNames()[segment-1], config, testSegmentSlot);
    }

	
	public CmProgram getNextProblem(String testName, int total, int correct) {
	    return getAdapter().getNextProgram(testName, total, correct);	
	}

	@Override
	public String getSubTitle(int segment) {
	    return getAdapter().getSubTitle(segment);
	}

	@Override
	public int getTotalSegmentCount() {
		return getAdapter().getTotalSegmentCount();
	}
	
	private PlacementAdapter getAdapter() {
	    if(_adapter == null) {
	        if(getTestDefId() == CmProgram.AUTO_ENROLL_COLLEGE.getDefId()) {
	            _adapter = new PlacementAdapterImplAutoEnrollCollege();
	        }
	        else {
	            _adapter = new PlacementAdapterImplAutoEnroll();
	        }
	    }
	    return _adapter;
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
	
	
	
	 /** Return set of 4 tests of 7 questions, taken from each subject:
     * 
     * 
     * There will be 4 tests with 10 questions each (prealgptests, alg1, gcgeopractice, alg2).
     * 
     */
    class PlacementAdapterImplAutoEnroll implements PlacementAdapter {
        @Override
        public String getSubTitle(int segment) {
            String t = "Auto-Enrollment";
            switch(segment) {
                case 0:
                case 1:
                    t = "Essentials " + t;
                    break;
                case 2:
                    t = "Pre-Algebra " + t;
                    break;
                case 3:
                    t = "Algebra 1 " + t;
                    break;
                case 4:
                    t = "Geometry " + t;
                    break;
                case 5:
                    t = "Algebra 2 " + t;
                    break;
            }
            return t;
        }
 
        
        @Override
        public int getTotalSegmentCount() {
            return getSegmentNames().length;            
        }

        @Override
        public String[] getSegmentNames() {
            return new String[]{"ess", "prealg", "alg1", "geo", "alg2"};            
        }
        
        @Override
        public CmProgram getNextProgram(String testName, int total, int correct) {
            String thisTest = testName.toLowerCase();
            CmProgram program=null;
            
            // some trigger, in this case > 1 wrong answers.
            if ((total - correct) > 1) {
                /** Sign user up for the current subject program.
                    map to real Program name.
                */
                if(thisTest.indexOf("essentials") > -1) {
                    
                    if(correct < 9) {
                        program = CmProgram.FOUNDATIONS;
                    }
                    else if(correct < 4) {
                        program = CmProgram.ESSENTIALS;
                    }
                }
                else if (thisTest.indexOf("pre-algebra") > -1) {
                    if(correct < 9) {
                        program = CmProgram.PREALG_PROF;    
                    }
                } else if (thisTest.indexOf("algebra 1") > -1) {
                    if(correct < 9) {
                        program = CmProgram.ALG1_PROF;
                    }
                } else if (thisTest.indexOf("geometry") > -1) {
                    if(correct < 9) {
                        program = CmProgram.GEOM_PROF;
                    }
                } else if (thisTest.indexOf("algebra 2") > -1) {
                    program = CmProgram.ALG2_PROF;
                }
            } else if (thisTest.indexOf("algebra 2") > -1) {
                /**
                 * this means user passed the last test
                 * assign to National as default
                 * 
                 */
                program = CmProgram.ALG2_PROF;
            }
            
            return program;
        }
    }
    
    
    
    class PlacementAdapterImplAutoEnrollCollege implements PlacementAdapter {
        @Override
        public String getSubTitle(int segment) {
            String t = "Auto-Enrollment College";
            switch(segment) {
                case 0:
                case 1:
                    t = "Basic Math " + t;
                    break;
                case 2:
                    t = "Elementary Algebra " + t;
                    break;
            }
            return t;
        }
        
        @Override
        public int getTotalSegmentCount() {
            return getSegmentNames().length;            
        }

        @Override
        public String[] getSegmentNames() {
            return new String[]{"basicmath", "elemalg"};            
        }
        
        @Override
        public CmProgram getNextProgram(String testName, int total, int correct) {
            String thisTest = testName.toLowerCase();
            CmProgram program=null;
            if (thisTest.indexOf("basic math") > -1) {
                if(correct < 9) {
                    program = CmProgram.BASICMATH;    
                }
            }
            else if(thisTest.indexOf("elementary") > -1) {
                if(correct < 10) {   
                    program = CmProgram.ELEMALG;
                }
                else {
                    
                    /** missed zero on last segment
                     * 
                     */
                    program = CmProgram.NONE;
                }
            }
            
            
            return program;
        }
    }
}
