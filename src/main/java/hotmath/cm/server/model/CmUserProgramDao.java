package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CmUserProgramDao {

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
            while (rs.first()) {
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
                supm.setId(rs.getInt("id"));
                supm.setUserId(rs.getInt("user_id"));
                supm.setAdminId(rs.getInt("admin_id"));
                supm.setTestDefId(rs.getInt("test_def_id"));
                supm.setTestName(rs.getString("test_name"));
                int passPercent = rs.getInt("pass_percent");
                supm.setConfig(new HaTestConfig(passPercent, rs.getString("test_config_json")));
            }
            return supm;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }
}
