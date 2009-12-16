package hotmath.testset.ha;

import hotmath.BookInfo;
import hotmath.BookInfoManager;
import hotmath.HotMathException;
import hotmath.HotMathLogger;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A single Hotmath Advance test definition.
 * 
 * Each test definition is made up of:
 * 
 * 1. textcode 2. chapter
 * 
 * Each test has 4 segments.
 * 
 * Each segment is made up of 10 questions
 * 
 * Each question is one from a set of related questions
 * 
 * 
 * each question's related question is taken using the same number (1-n, where
 * is total in related pool).
 * 
 * 
 * 
 * 
 * 
 * @author Casey
 * 
 */
public class HaTestDef {

    static Logger logger = Logger.getLogger(HaTestDef.class.getName());
    static public int PREALG_PROFICENCY = 16;

    String name;
    int indexRelatedPool;
    String textCode;
    String chapter;
    int testDefId;
	String subjectId;
    String progId;
    String stateId;
    HaTestConfig config;
    HaTestDefDao tdDao = new HaTestDefDao();
    
    public HaTestDef() { ; }
    
    public HaTestDef(final Connection conn, String name) throws Exception {
        
    	HaTestDef td = tdDao.getTestDef(conn, name);
    	init(td);
        this.indexRelatedPool = getRelatedPoolIndex();
    }

    public HaTestDef(final Connection conn, Integer id) throws Exception {
    	
    	HaTestDef td = tdDao.getTestDef(conn, id);
    	init(td);
        this.indexRelatedPool = getRelatedPoolIndex();
    }

	private void init(HaTestDef td) {
		this.name = td.getName();
		this.chapter = td.getChapter();
		this.testDefId = td.getTestDefId();
		this.textCode = td.getTextCode();
		this.config = td.getTestConfig();
		this.subjectId = td.getSubjectId();
		this.progId = td.getProgId();
		this.stateId = td.getStateId();
	}
    
    /** Return the default test configuration for this test def
     * 
     * @return
     */
    public HaTestConfig getTestConfig() {
        return config;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   
    // used as internal heading for title
    // subclasses can override and provide a
    // per segment title
    public String getSubTitle(int segment) {
        return "";
    }

    /**
     * Return the page to use as the test page
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
    
    public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getProgId() {
		return progId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}


    int gradeLevel = 0;

    /**
     * Return the grade level for this program or return -1 on error.
     */
    public int getGradeLevel() {
        try {
            return BookInfoManager.getInstance().getBookInfo(this.getTextCode()).getGradeLevel();
        } catch (HotMathException hme) {
            HotMathLogger.logMessage(hme, "Error getting grade level");
        }
        return -1;
    }



    public int getTestDefId() {
        return testDefId;
    }

    public void setTestDefId(int testDefId) {
        this.testDefId = testDefId;
    }

    /**
     * return the index problem to choose from the related questions for this
     * test.
     * 
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

    /**
     * Return a list of ProblemIds that are used to populate this test's
     * segment.
     * 
     *
     * The segmentSlot is the position in the alternate tests to selection questions
     * 
     * @return
     * @throws HotMathException
     */
    List<String> list;
    int _lastSegment;

    public List<String> getTestIdsForSegment(final Connection conn, int segment, HaTestConfig config, int segmentSlot) throws Exception {
        _lastSegment = segment;

        return tdDao.getTestIdsForSegment(conn, segment, textCode, chapter, config, segmentSlot);
    }


    public int getTotalSegmentCount() {
        return config.getSegmentCount();
    }

    /**
     * Return JSON string used to initialize this test or null
     * 
     * @throws Exception
     */
    public String getTestInitJson(HaTest test) throws Exception {
        return null;
    }

}
