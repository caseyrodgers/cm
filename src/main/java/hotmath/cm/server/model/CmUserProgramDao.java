package hotmath.cm.server.model;

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
	
	static String CURRENT_USER_PROGRAM_SQL =
        "select c.id, c.user_id, c.pass_percent, u.admin_id, c.test_def_id, t.test_name, c.test_config_json, c.create_date " +
        "from CM_USER_PROGRAM c " +
        "JOIN HA_USER u on c.id = u.user_prog_id " +
        "JOIN HA_TEST_DEF t on c.test_def_id = t.test_def_id " +
        "and u.uid = ?";

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

        try {
            StudentUserProgramModel supm = new StudentUserProgramModel();

            ps = conn.prepareStatement(CURRENT_USER_PROGRAM_SQL);

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

    static String ALL_USER_PROGRAM_SQL =
    	"select c.id, c.user_id, c.pass_percent, u.admin_id, c.test_def_id, t.test_name, c.test_config_json, c.create_date " +
        "from CM_USER_PROGRAM c " +
        "JOIN HA_TEST_DEF t on c.test_def_id = t.test_def_id " +
        "where c.user_id = ?";
    
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


        try {
            ps = conn.prepareStatement(ALL_USER_PROGRAM_SQL);

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
}
