package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 *
 * @author Bob
 *
 */

public class CmUserProgramDao {

    static Logger __logger = Logger.getLogger(CmUserProgramDao.class);

    /**
     * Return the currently configured user program for this user
     * 
     * Return null if no program has been defined or a defined program does not exist.
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public StudentUserProgramModel loadProgramInfoCurrent(final Connection conn, Integer userId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("CURRENT_USER_PROGRAM_SQL");
        try {
            StudentUserProgramModel supm = new StudentUserProgramModel();

            ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.first()) {
                supm = defineUserProgram(rs);
                HaTestDef td = new HaTestDefDao().getTestDef(conn, supm.getTestDefId());
                supm.setTestDef(td);
            }
            return supm;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    public StudentUserProgramModel loadProgramInfo(final Connection conn, Integer userProgId) throws Exception {

        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("LOAD_USER_PROGRAM_SQL");
        try {
            StudentUserProgramModel supm = new StudentUserProgramModel();

            ps = conn.prepareStatement(sql);

            ps.setInt(1, userProgId);
            rs = ps.executeQuery();
            if (rs.first()) {
                supm = defineUserProgram(rs);
                HaTestDef td = new HaTestDefDao().getTestDef(conn, supm.getTestDefId());
                supm.setTestDef(td);
            }
            return supm;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }        
    }

    /**
     * Return all User Programs for the specified User
     * 
     * Return an empty List if no valid programs found
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public List<StudentUserProgramModel> loadProgramInfoAll(final Connection conn, Integer userId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("ALL_USER_PROGRAM_SQL");
        try {
            ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            rs = ps.executeQuery();

            List<StudentUserProgramModel> list = new ArrayList<StudentUserProgramModel>();
            while (rs.next()) {
                StudentUserProgramModel supm = defineUserProgram(rs);
                HaTestDef td = new HaTestDefDao().getTestDef(conn, supm.getTestDefId());
                supm.setTestDef(td);
                list.add(supm);
            }
            return list;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /**
     * Return the program information for the specified test
     * 
     * @param testId
     * @return
     * @throws Exception
     */
    public StudentUserProgramModel loadProgramInfoForTest(final Connection conn, Integer testId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("LOAD_PROGRAM_INFO_FOR_TEST"); 
        try {
            StudentUserProgramModel supm = new StudentUserProgramModel();

            ps = conn.prepareStatement(sql);

            ps.setInt(1, testId);
            rs = ps.executeQuery();
            if (rs.first()) {
                supm = defineUserProgram(rs);
                HaTestDef td = new HaTestDefDao().getTestDef(conn, supm.getTestDefId());
                supm.setTestDef(td);
            }
            return supm;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /** Set the user pass_percent to named value
     * 
     * @param conn
     * @param programId
     * @param passPercent
     * @throws Exception
     */
    public void setProgramPassPercent(final Connection conn, Integer programId, Integer passPercent) throws Exception {
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("UPDATE_PASS_PERCENT_SQL"));

            ps.setInt(1, passPercent);
            ps.setInt(2, programId);

            int cnt = ps.executeUpdate();
            if(cnt != 1)
                __logger.warn("no such program: " + programId);

        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /** Define a StudentUserProgramModel from ResultSet
     * 
     * @param rs
     * @return
     * @throws Exception
     */
    private StudentUserProgramModel defineUserProgram(ResultSet rs) throws Exception {

        StudentUserProgramModel supm = new StudentUserProgramModel();

        supm.setId(rs.getInt("id"));
        supm.setUserId(rs.getInt("user_id"));
        supm.setAdminId(rs.getInt("admin_id"));
        supm.setTestDefId(rs.getInt("test_def_id"));
        supm.setTestName(rs.getString("test_name"));
        int passPercent = rs.getInt("pass_percent");
        supm.setPassPercent(passPercent);
        java.sql.Date dt = rs.getDate("create_date");
        supm.setCreateDate(new Date(dt.getTime()));
        supm.setConfig(new HaTestConfig(passPercent, rs.getString("test_config_json")));

        return supm;
    }

}
