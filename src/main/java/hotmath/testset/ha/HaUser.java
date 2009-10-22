package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;


/** Manage the Catchup Math users
 * 
 * @author Casey
 *
 */
public class HaUser extends HaBasicUserImpl {

    Integer aid;
	public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    Integer uid;
	String userName;
	String category;
	Integer gradeLevel;
	String assignedTestName;
	Integer activeTestSegment;
	Integer activeTest;
	Integer activeTestRunId;
	Integer activeTestRunSession;
	String backgroundStyle;
    String testConfigJson;
    boolean isShowWorkRequired;
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
		return (activeTestSegment!=null)?activeTestSegment:0;
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
	    return (activeTestRunId!=null)?activeTestRunId:0;
	}

	public void setActiveTestRunId(Integer activeTestRunId) {
		this.activeTestRunId = activeTestRunId;
	}
	
    
    public int getPassPercentRequired() {
        return passPercentRequired;
    }

    public void setPassPercentRequired(int passPercentRequired) {
        this.passPercentRequired = passPercentRequired;
    }
	
	
	/** Update this user record in db
	 * 
	 *   (only select fields ...)
	 *   
	 * @throws Exception
	 */
	public void update(final Connection conn) throws HotMathException {
		PreparedStatement pstat=null;
		try {
	    	HaTestDef def = HaTestDefFactory.createTestDef(conn, getAssignedTestName());			
			
		    String sql = "update HA_USER set active_test_id = ?, active_run_id = ?, active_segment = ?, active_run_session = ?, " +
		       "test_def_id = ?, gui_background_style = ? " +
	          " where uid = ?";
   	        pstat = conn.prepareStatement(sql);
	        pstat.setInt(1,getActiveTest());
	        pstat.setInt(2,getActiveTestRunId());
	        pstat.setInt(3,getActiveTestSegment());
	        pstat.setInt(4,getActiveTestRunSession());
	        pstat.setInt(5,def.getTestDefId());
	        pstat.setString(6,getBackgroundStyle());
	        pstat.setInt(7,getUid());
	        
	        Logger.getLogger(HaUser.class.getName()).debug("Updating HA_USER: " + pstat.toString());
            
			if(pstat.executeUpdate() == 0)
				throw new HotMathException("Could not update user record: " + getUid());
		}
		catch(Exception e) {
			throw new HotMathException(e, "Error updating user record");
		}
		finally {
			SqlUtilities.releaseResources(null,pstat,null);
		}			
	}
	
	
	/** Lookup and return HaUser user based on uid (PK) 
	 * @param uid THe uid for this user
	 * @param userName The user name to use for lookup.
	 * 
	 * 
	 * If uid is non null it used and userName is ignored.
	 * If uid is null, then userName is used for lookup
	 * 
	 * @return
	 * @throws HotMathException
	 */
	static public HaUser lookUser(final Connection conn,Integer uid,String userName) throws HotMathException {
		PreparedStatement pstat=null;
		ResultSet rs = null;
		try {
			String sql = CmMultiLinePropertyReader.getInstance().getProperty("HA_USER_LOOKUP_USER");
			if(uid != null)
				sql += " where u.uid = ?";
			else 
				sql += " where u.user_name = ?";
			
			pstat = conn.prepareStatement(sql);
			HaUser user = new HaUser();
			
			if(uid != null)
				pstat.setInt(1,uid);
			else
				pstat.setString(1, userName);
 			rs = pstat.executeQuery();
			if(!rs.next())
				throw new HotMathException("No such Catchup Math user id: " + uid);
			
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
			
			return user;
		}
		catch(HotMathException hme) {
			throw hme;
		}
		catch(Exception e) {
			throw new HotMathException(e,"Error looking up Catchup Math user");
		}
		finally {
			SqlUtilities.releaseResources(rs,pstat,null);
		}
	}
	
	
	
	static public HaUser lookupUserByPasscode(String passCode) throws HotMathException {
		Connection conn=null;
	    PreparedStatement pstat=null;
	    ResultSet rs = null;
	    try {
	        String sql = "select uid from HA_USER where user_passcode = ?";
	        conn = HMConnectionPool.getConnection();
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
	        SqlUtilities.releaseResources(rs,pstat,conn);
	    }    				
	}
	
	public static HaUser createUser(String adminId, String userName, String passCode,String testName,String testConfigJson) throws Exception {
	    Connection conn=null;
	    PreparedStatement pstat=null;
	    ResultSet rs = null;
	    try {
	        String sql = "insert into HA_USER(admin_id,user_name,user_passcode,test_def_id,test_config_json)" +
	                     "select ? as admin_id,? as user_name,? as passcode,test_def_id,? as test_config_json " +
	                     "from   HA_TEST_DEF " +
	                     "where  test_name = ?";
	        conn = HMConnectionPool.getConnection();
	        pstat = conn.prepareStatement(sql);
	        
	        pstat.setInt(1,Integer.parseInt(adminId));
	        pstat.setString(2, userName);
	        pstat.setString(3, passCode);
	        pstat.setString(4, testConfigJson);
	        pstat.setString(5, testName);
	        
	        int cnt = pstat.executeUpdate();
	        if(cnt != 1)
	        	throw new HotMathException("Could not register new student");
	        
		    int uid = -1;
		    rs = pstat.getGeneratedKeys();
		    if (rs.next()) {
		    	uid = rs.getInt(1);
		    } else {
		    	throw new HotMathException("Error creating PK for user");
		    }
	        HaUser user = lookUser(conn,uid,null);
	        return user;
	    }
	    catch(HotMathException hme) {
	        throw hme;
	    }
	    catch(Exception e) {
	        throw new HotMathException(e,"Error removing HA student: " + e.getMessage());
	    }
	    finally {
	        SqlUtilities.releaseResources(rs,pstat,conn);
	    }		
	}
	

    @Override
    public Object getUserObject() {
        return this;
    }

    @Override
    public UserType getUserType() {
        return UserType.STUDENT;
    }

    @Override
    public int getUserKey() {
        return this.uid;
    }

    @Override
    public String toString() {
        return "HaUser [activeTest=" + activeTest + ", activeTestRunId=" + activeTestRunId + ", activeTestRunSession="
                + activeTestRunSession + ", activeTestSegment=" + activeTestSegment + ", assignedTestName="
                + assignedTestName + ", backgroundStyle=" + backgroundStyle + ", category=" + category
                + ", gradeLevel=" + gradeLevel + ", isShowWorkRequired=" + isShowWorkRequired
                + ", passPercentRequired=" + passPercentRequired + ", testConfigJson=" + testConfigJson + ", uid="
                + uid + ", userAccountType=" + userAccountType + ", userName=" + userName + "]";
    }
}
