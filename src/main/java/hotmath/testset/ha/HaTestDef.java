package hotmath.testset.ha;

import hotmath.BookInfo;
import hotmath.BookInfoManager;
import hotmath.HotMathException;
import hotmath.HotMathLogger;
import hotmath.cm.util.CmCacheManager;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import static hotmath.cm.util.CmCacheManager.CacheName.TEST_DEF;

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
    HaTestConfig config;
    
    
    public HaTestDef(final Connection conn, String name) throws Exception {
        
        // try cache first
        HaTestDef td = (HaTestDef)CmCacheManager.getInstance().retrieveFromCache(TEST_DEF, name);
        if (td != null) {
            this.name = td.getName();
            this.chapter = td.getChapter();
            this.testDefId = td.getTestDefId();
            this.textCode = td.getTextCode();
            this.config = td.getTestConfig();
            return;
        }
        
        
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = "select * " + " from HA_TEST_DEF d " + " where test_name = ? ";

            pstat = conn.prepareStatement(sql);

            pstat.setString(1, name);
            rs = pstat.executeQuery();
            if (!rs.first())
                throw new Exception("Test definition not found");

            HaTestDef testDef = this;             
            testDef.name = name;
            testDef.textCode = rs.getString("textcode");
            testDef.chapter = rs.getString("chapter");
            testDef.testDefId = rs.getInt("test_def_id");
            testDef.config = new HaTestConfig(rs.getString("test_config_json"));
            
            CmCacheManager.getInstance().addToCache(CmCacheManager.CacheName.TEST_DEF, testDef.getName(), testDef);
            
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error getting test definition: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }        
        
        this.indexRelatedPool = getRelatedPoolIndex();
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
     * 
     * @return
     * @throws HotMathException
     */
    List<String> list;
    int _lastSegment;

    public List<String> getTestIdsForSegment(final Connection conn, int segment, HaTestConfig config) throws Exception {
        _lastSegment = segment;

        HaTestDefDao dao = new HaTestDefDao();
        return dao.getTestIdsForSegment(conn, segment, textCode, chapter, config);
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
