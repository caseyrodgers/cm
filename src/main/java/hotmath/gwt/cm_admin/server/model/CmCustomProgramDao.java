package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CmCustomProgramDao {
    
    public CmCustomProgramDao(){}
    
    /** Return list of lessons that can be used to create a custom program
     * 
     */
    public CmList<CustomLessonModel> getAllLessons(final Connection conn) throws Exception {
        HashMap<String, List<CustomLessonModel>> map = new  HashMap<String, List<CustomLessonModel>>();
        Statement stmt=null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select distinct lesson, file, subject from HA_PROGRAM_LESSONS where subject > '' order by lesson");
            while(rs.next()) {
                CustomLessonModel clm = new CustomLessonModel(rs.getString("lesson"), rs.getString("file"), rs.getString("subject"));
                List<CustomLessonModel> lessons = map.get(clm.getFile());
                if(lessons == null) {
                    lessons = new ArrayList<CustomLessonModel>();
                    map.put(clm.getFile(), lessons);
                }
                lessons.add(clm);
            }
            
            /** Create list of distinct lessons and the highest subject
             *  
             */
            CmList<CustomLessonModel> lessons = new CmArrayList<CustomLessonModel>();
            for(String lesson: map.keySet()) {
                List<CustomLessonModel> ls = map.get(lesson);
                CustomLessonModel use = null;
                for(CustomLessonModel clm: ls) {
                    if(use == null || sl(clm.getSubject()) > sl(use.getSubject())) {
                        use = clm;
                    }
                }
                lessons.add(use);
            }
            return lessons;
        }
        finally {
            SqlUtilities.releaseResources(null,stmt, null);
        }
    }

    
    /** Return list of custom programs defined by this admin 
     * 
     * @param conn
     * @param adminId
     * @return
     * @throws Exception
     */
    public CmList<CustomProgramModel> getCustomPrograms(final Connection conn, Integer adminId) throws Exception {
        Statement stmt=null;
        try {
            CmList<CustomProgramModel> programs = new CmArrayList<CustomProgramModel>();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id, name from HA_CUSTOM_PROGRAM where admin_id = " + adminId  + " order by name");
            while(rs.next()) {
                CustomProgramModel prog = new CustomProgramModel(rs.getString("name"), rs.getInt("id"));
                programs.add(prog);
            }
            return programs;
        }
        finally {
            SqlUtilities.releaseResources(null,stmt, null);
        }
    }
    
    /** Get a singel Custom Program by progId
     * 
     * @param conn
     * @param progId
     * @return
     * @throws Exception
     */
    public CustomProgramModel getCustomProgram(final Connection conn, Integer progId) throws Exception {
        Statement stmt=null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id, name from HA_CUSTOM_PROGRAM where id = " + progId);
            if(!rs.next())
                throw new CmException("Custom Program not found: " + progId);
            return new CustomProgramModel(rs.getString("name"), rs.getInt("id"));
        }
        finally {
            SqlUtilities.releaseResources(null,stmt, null);
        }
    }
    
    public CustomProgramModel getCustomProgram(final Connection conn, Integer adminId, String customProgramName) throws Exception {
        PreparedStatement stmt=null;
        try {
            String sql = "select id, name from HA_CUSTOM_PROGRAM where admin_id = ? and name = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, adminId);
            stmt.setString(2, customProgramName);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next())
                throw new CmException("Custom Program not found for: " + adminId + ", " + customProgramName);
            
            return new CustomProgramModel(rs.getString("name"), rs.getInt("id"));
        }
        finally {
            SqlUtilities.releaseResources(null,stmt, null);
        }
    }
    
    public CmList<CustomProgramModel> deleteCustomProgram(final Connection conn, Integer programId) throws Exception {
        Statement stmt=null;
        try {
            CmList<CustomProgramModel> programs = new CmArrayList<CustomProgramModel>();
            stmt = conn.createStatement();
            stmt.executeUpdate("delete from HA_CUSTOM_PROGRAM where id = " + programId);
            return programs;
        }
        finally {
            SqlUtilities.releaseResources(null,stmt, null);
        }
    }
    
    public CmList<CustomLessonModel> getCustomProgramDefinition(final Connection conn, Integer programId) throws Exception {
        Statement stmt=null;
        try {
            CmList<CustomLessonModel> lessons = new CmArrayList<CustomLessonModel>();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from HA_CUSTOM_PROGRAM_LESSON where program_id = " + programId + " order by id");
            while(rs.next()) {
                lessons.add(new CustomLessonModel(rs.getString("lesson"),rs.getString("file"), rs.getString("subject")));
            }
            return lessons;
        }
        finally {
            SqlUtilities.releaseResources(null,stmt, null);
        }
    }

    public CmList<CustomLessonModel> saveChanges(final Connection conn, Integer progId, String name, List<CustomLessonModel> lessons) throws Exception {
        
        PreparedStatement stmt1=null, stmt2=null;
        try {
            stmt1 = conn.prepareStatement("update HA_CUSTOM_PROGRAM set name = ? where id = ?");
            stmt1.setString(1,name);
            stmt1.setInt(2, progId);
            if(stmt1.executeUpdate() != 1)
                throw new CmException("Could not update Custom Program title: " + progId);
            
            conn.createStatement().executeUpdate("delete from HA_CUSTOM_PROGRAM_LESSON where program_id = " + progId);
            String sql = "insert into HA_CUSTOM_PROGRAM_LESSON(program_id,lesson,file,subject)values(?,?,?,?)";
            stmt2 = conn.prepareStatement(sql);
            stmt2.setInt(1, progId);
            for(CustomLessonModel l: lessons) {
                stmt2.setString(2, l.getLesson());
                stmt2.setString(3, l.getFile());
                stmt2.setString(4, l.getSubject());
                
                if(stmt2.executeUpdate() != 1) {
                    throw new CmException("Could not save new custom program lesson: " + progId + ", " + l);
                }
            }
            return null;
        }
        finally {
            SqlUtilities.releaseResources(null,stmt1, null);
            SqlUtilities.releaseResources(null,stmt2, null);
        }
    }
    
    public CustomProgramModel createNewCustomProgram(final Connection conn, Integer adminId, String name, List<CustomLessonModel> lessons) throws Exception {
        PreparedStatement stmt=null;
        try {
            String sql = "insert into HA_CUSTOM_PROGRAM(admin_id,name)values(?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, adminId);
            stmt.setString(2, name);
            if(stmt.executeUpdate() != 1) {
                throw new CmException("Could not save new custom program lesson: " + adminId + ", " + name);
            }
            ResultSet rs = stmt.getGeneratedKeys();
            if(!rs.next())
                throw new CmException("Could not generate a new custom program id: " + adminId + ", " + name + ", " + lessons);
            int newProgId = rs.getInt(1);
            
            saveChanges(conn, newProgId, name,lessons);
            
            return new CustomProgramModel(name, newProgId);
        }
        finally {
            SqlUtilities.releaseResources(null,stmt, null);
        }
    }
    

    /** return integer representing the level logically of this subject
     * 
     * @param subject
     * @return
     */
    private int sl(String subject) {
       if(subject == null || subject.length() == 0)
           return 0;
       else if(subject.equals("Pre-Alg"))
           return 1;
       else if(subject.equals("Alg 1"))
           return 2;       
       else if(subject.equals("Alg 2"))
           return 3;
       else if(subject.equals("Geom"))
           return 4;
       else
           return -1;
    }
}
