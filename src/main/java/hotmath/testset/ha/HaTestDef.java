package hotmath.testset.ha;

import hotmath.BookInfo;
import hotmath.BookInfoManager;
import hotmath.HotMathException;
import hotmath.HotMathLogger;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A single Catchup Math test source definition.
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
 * each question's related question is taken using the same number (1-n, where
 * is total in related pool).
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
    int numAlternateTests;
    String chapter;
    int testDefId;
    String subjectId;
    String progId;
    String stateId;
    HaTestConfig config;

    public HaTestDef() {
        this.indexRelatedPool = getRelatedPoolIndex();
    }

    protected void init(HaTestDef td) {
        this.name = td.getName();
        this.chapter = td.getChapter();
        this.testDefId = td.getTestDefId();
        this.textCode = td.getTextCode();
        this.config = td.getTestConfig();
        this.subjectId = td.getSubjectId();
        this.progId = td.getProgId();
        this.stateId = td.getStateId();
        this.numAlternateTests = td.getNumAlternateTests();
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("HaTestDef[ name=").append(name);
    	sb.append(", textCode=").append(textCode);
    	sb.append(", chapter= ").append(chapter);
    	sb.append(", subjectId= ").append(subjectId);
    	sb.append(", progId=").append(progId);
    	sb.append(", testDefId=").append(testDefId);
    	sb.append(", stateId=").append(stateId);
    	sb.append(", indexRelatedPool=").append(indexRelatedPool);
    	sb.append(", numAlternateTests=").append(numAlternateTests);
    	sb.append(", config=").append(config.toString()).append(" ]");
    	return sb.toString();
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
    final static int HIGHEST_LEVEL = 99;  /* can include any problem */


    /**
     * Return the grade level for this program or return -1 on error.
     */
    public int getGradeLevel() {
        try {
            String textCode = this.getTextCode();
            if (textCode == null || textCode.length() == 0) {
                return HIGHEST_LEVEL;
            } else {
                return BookInfoManager.getInstance().getBookInfo(textCode).getGradeLevel();
            }
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

    public List<String> getTestIdsForSegment(final Connection conn, StudentUserProgramModel userProgram, int segment, HaTestConfig config, int segmentSlot) throws Exception {
        _lastSegment = segment;
        String chap = "";
        if (config.getChapters().size() > 0) {
            chap = config.getChapters().get(0);
        } else {
            chap = chapter;
        }
        
        if(userProgram.getCustomProgramId() > 0) {
            return new CmCustomProgramDao().getCustomProgramQuizSegmentPids(conn, userProgram.getCustomProgramId(), segment);
        }
        else {
            return new HaTestDefDao().getTestIdsForSegment(conn, userProgram, segment, textCode, chap, config, segmentSlot);
        }
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


    /** Return the number of alternate tests defined
     *  for this text.
     * 
     * @return
     */
    public int getNumAlternateTests() {
        return this.numAlternateTests;
    }
}
