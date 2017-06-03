package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;


/** Manage the Catchup Math users
 * 
 * @author Casey
 *
 */
public class HaUser extends HaBasicUserImpl {

    
    int aid;
	public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    int uid;
	String userName;
	String category;
	int gradeLevel;
	String assignedTestName;
	int activeTestSegment;
	int activeTest;
	int activeTestRunId;
	int activeTestRunSession;
	int groupId;
	String backgroundStyle;
    String testConfigJson;
    boolean isShowWorkRequired;
    boolean isDemoUser;
	String  userAccountType;
    int passPercentRequired;
    

    public String getUserAccountType() {
        return userAccountType;
    }

    public void setUserAccountType(String userAccountType) {
        this.userAccountType = userAccountType;
    }

    public HaUser() {
        
    }
    
    public boolean isShowWorkRequired() {
        return isShowWorkRequired;
    }

    public void setShowWorkRequired(boolean isShowWorkRequired) {
        this.isShowWorkRequired = isShowWorkRequired;
    }
    
    /** Return the current active test run session number
     * 
     * @return
     */
	public Integer getActiveTestRunSession() {
        return activeTestRunSession;
    }

    public void setActiveTestRunSession(Integer activeTestRunSession) {
        this.activeTestRunSession = activeTestRunSession;
    }


	public String getAssignedTestName() {
		return assignedTestName;
	}

	public void setAssignedTestName(String assignedTestName) {
		this.assignedTestName = assignedTestName;
	}

	public Integer getActiveTestSegment() {
		return activeTestSegment;
	}

	public void setActiveTestSegment(Integer assignedCurrentTestSegment) {
		this.activeTestSegment = assignedCurrentTestSegment;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getGradeLevel() {
		return gradeLevel;
	}

	public void setGradeLevel(Integer gradeLevel) {
		this.gradeLevel = gradeLevel;
	}
	
	public String getUserLabel() {
		String un = getUserName();
		return un.substring(0,1).toUpperCase() + un.substring(1).toLowerCase();
	}
	   
    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public void setBackgroundStyle(String backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

	
	
	/** Return the test_id of the current active test for this user
	 * 
	 * @return
	 */
	public Integer getActiveTest() {
	    return activeTest;	
	}
	
	public void setActiveTest(Integer activeTest) {
		this.activeTest = activeTest;
	}
	
	public String getTestConfigJson() {
		return testConfigJson;
	}

	public void setTestConfigJson(String testConfigJson) {
		this.testConfigJson = testConfigJson;
	}
	
	
	public HaTestConfig getTestConfig() throws HotMathException {
		return new HaTestConfig(testConfigJson);
	}
	
	public Integer getActiveTestRunId() {
	    return activeTestRunId;
	}

	public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setActiveTestRunId(int activeTestRunId) {
        this.activeTestRunId = activeTestRunId;
    }

    public void setActiveTestRunId(Integer activeTestRunId) {
		this.activeTestRunId = activeTestRunId!=null?activeTestRunId:0;
	}
	
    
    public int getPassPercentRequired() {
        return passPercentRequired;
    }

    public void setPassPercentRequired(int passPercentRequired) {
        this.passPercentRequired = passPercentRequired;
    }

    public boolean isDemoUser() {
		return isDemoUser;
	}

	public void setDemoUser(boolean isDemoUser) {
		this.isDemoUser = isDemoUser;
	}

	/** Update this user record in db
	 * 
	 *   (only select fields ...)
	 *   
	 * @throws Exception
	 */
	public void update() throws Exception {
	    HaUserDao.getInstance().updateUser(this);
	}
	
	/** Lookup and return HaUser user based on uid (PK), does not use HA_USER cache
	 * @param uid THe uid for this user
	 * @param userName The user name to use for lookup.
	 * 
	 * 
	 * If uid is non null it is used and userName is ignored.
	 * If uid is null, then userName is used for lookup
	 * 
	 * @return
	 * @throws HotMathException
	 */
	static public HaUser lookUser(final Connection conn,Integer uid,String userName) throws HotMathException {
        return lookUser(conn, uid, userName, false);
	}
		
	/** Lookup and return HaUser user based on uid (PK) 
	 * @param uid the uid for this user
	 * @param userName the user name to use for lookup
	 * @param useCache if true use UA_USER cache 
	 * 
	 * If uid is non null it is used and userName is ignored.
	 * If uid is null, then userName is used for lookup
	 * 
	 * @return
	 * @throws HotMathException
	 */
	static public HaUser lookUser(final Connection conn,Integer uid,String userName, Boolean useCache) throws HotMathException {
		PreparedStatement pstat=null;
		ResultSet rs = null;
		try {
			
			HaUser user;
			String cacheKey = (uid != null)?String.valueOf(uid) : userName;

			if (useCache) {
			    /**
			     *  Problem here is possible changes to the USER since last put in cache
			     *  By default cache is not used; client should only use cache if possible staleness is OK
			     *  Currently, life of HA_USER cache is 5 minutes.  
			     */
				
	            user  = (HaUser) CmCacheManager.getInstance().retrieveFromCache(CacheName.HA_USER, cacheKey);
	            if (user != null) return user;
			}
	        
			
			String whereClause="";
			if(uid != null)
				whereClause = " where u.uid = ?";
			else 
			    whereClause = " where u.user_name = ?";
			
			final String fWhereClause = whereClause;
			String sql = CmMultiLinePropertyReader.getInstance().getProperty(
			        "HA_USER_LOOKUP_USER",
			        new String[]{"WHERE_CLAUSE|" + fWhereClause});
			
			pstat = conn.prepareStatement(sql);
			user = new HaUser();
			
			if(uid != null)
				pstat.setInt(1,uid);
			else
				pstat.setString(1, userName);
 			rs = pstat.executeQuery();
			if(!rs.next())
				throw new HotMathException("No such Catchup Math user id: " + uid);
			
			user.setGroupId(rs.getInt("group_id"));
			user.setUid(rs.getInt("uid"));
			user.setUserName(rs.getString("user_name"));
			user.setAssignedTestName(rs.getString("assigned_test_name"));
			user.setActiveTestSegment(rs.getInt("active_segment"));
			user.setActiveTest(rs.getInt("active_test_id"));
			user.setActiveTestRunId(rs.getInt("active_run_id"));
			user.setActiveTestRunSession(rs.getInt("active_run_session"));
			user.setTestConfigJson(rs.getString("test_config_json"));
			user.setBackgroundStyle(rs.getString("gui_background_style"));
			user.setShowWorkRequired(rs.getInt("is_show_work_required")==0?false:true);
			user.setPassPercentRequired(rs.getInt("pass_percent"));
			user.setPassword(rs.getString("user_passcode"));
			user.setAid(rs.getInt("aid"));
			user.setUserAccountType(rs.getString("type"));
			user.setDemoUser((rs.getInt("is_demo")!=0));

			CmCacheManager.getInstance().addToCache(CacheName.HA_USER, cacheKey, user);
	        
			return user;
		}
		catch(HotMathException hme) {
			throw hme;
		}
		catch(Exception e) {
			Logger.getLogger(HaUser.class).error("*** Error looking up Catchup Math user: uid: " + uid, e);
			throw new HotMathException(e,"Error looking up Catchup Math user");
		}
		finally {
			SqlUtilities.releaseResources(rs,pstat,null);
		}
	}
	
	
	
	static public HaUser lookupUserByPasscode(String passCode) throws HotMathException {
		Connection conn=null;
	
	    try {
	        conn = HMConnectionPool.getConnection();
	        return lookupUserByPasscode(conn, passCode);
	    }
	    finally {
	        SqlUtilities.releaseResources(null,null,conn);
	    }    				
	}
	
	static public HaUser lookupUserByPasscode(final Connection conn, String passCode) throws HotMathException {
	    PreparedStatement pstat=null;
	    ResultSet rs = null;
	    try {
	        String sql = "select uid from HA_USER where user_passcode = ?";
	        pstat = conn.prepareStatement(sql);
	        
	        pstat.setString(1, passCode);
	        
	        rs = pstat.executeQuery();
	        if(!rs.first())
	        	throw new Exception("Passcode not found");
	        
	        int userId = rs.getInt("uid");
	        return lookUser(conn,userId,null);
	    }
	    catch(HotMathException hme) {
	        throw hme;
	    }
	    catch(Exception e) {
	        throw new HotMathException(e,"Error getting user information: " + e.getMessage());
	    }
	    finally {
	        SqlUtilities.releaseResources(rs,pstat,null);
	    }    				
	}
	
	public static HaUser createUser(int adminId, String userName, String passCode,String testName,String testConfigJson) throws Exception {
	    
	    // create new user
        StudentModelI student = new StudentModel();
        student.setName(userName);
        
        student.setPasscode(passCode);
        // student.setPassPercent("80%");
        student.setAdminUid(adminId);
        student.setGroupId(1);
        
        StudentProgramModel stdProgram = new StudentProgramModel();
        stdProgram.setProgramType(testName);
        stdProgram.setSubjectId("");
        student.setProgram(stdProgram);
        
        student.getSettings().setTutoringAvailable(false);
        student.getSettings().setShowWorkRequired(false);
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            CmStudentDao csd = CmStudentDao.getInstance();
            student = csd.addStudent(conn, student, false);
            
            return lookUser(conn, student.getUid(), null);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
        
        
	}
	

    @Override
    public Object getUserObject() {
        return this;
    }

    //@Override
    public int getUserKey() {
        return this.uid;
    }

    @Override
    public String toString() {
        return "HaUser [aid=" + aid + ", uid=" + uid + ", userName=" + userName + ", category=" + category + ", gradeLevel=" + gradeLevel
                + ", assignedTestName=" + assignedTestName + ", activeTestSegment=" + activeTestSegment + ", activeTest=" + activeTest + ", activeTestRunId="
                + activeTestRunId + ", activeTestRunSession=" + activeTestRunSession + ", groupId=" + groupId + ", backgroundStyle=" + backgroundStyle
                + ", testConfigJson=" + testConfigJson + ", isShowWorkRequired=" + isShowWorkRequired + ", isDemoUser=" + isDemoUser + ", userAccountType="
                + userAccountType + ", passPercentRequired=" + passPercentRequired + "]";
    }
}
