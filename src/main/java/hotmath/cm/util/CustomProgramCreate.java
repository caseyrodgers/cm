package hotmath.cm.util;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/** Create custom problem (SP) from 
 *  external source.
 *  
 *  To facilitate the use of external text files.
 *  
 * @author casey
 *
 */
public class CustomProgramCreate {
    
    private String file;
    private String name;
    private int adminId;
    private String subject;
    private int loadOrder;
    private int gradeLevel;

    
    static private Logger __logger = Logger.getLogger(CustomProgramCreate.class);

    public CustomProgramCreate() {
        
    }
    
    public CustomProgramCreate(String file, String name, String subject, int adminId, int loadOrder, int gradeLevel) {
        this.file = file;
        this.name = name;
        this.subject = subject;
        this.adminId = adminId;
        this.loadOrder = loadOrder;
        this.gradeLevel = gradeLevel;
    }
    

    private void createCustomProgram() throws Exception {
        __logger.info("Creating custom program: " + file + ", " + name + ", " + adminId);
        Connection conn=null;
        PreparedStatement ps=null;
        try {
            
            conn = HMConnectionPool.getConnection();
            String sql = 
                    "select lesson " +
                            "from HA_PROGRAM_LESSONS_static " +
                            "where file = ? " +
                            "limit 1 ";
            ps = conn.prepareStatement(sql);
            
            /** Read lines into list */
            BufferedReader r = new BufferedReader(new FileReader(this.file));
            String line=null;
            List<CustomLessonModel> lessons = new ArrayList<CustomLessonModel>();
            int num=0;
            while((line = r.readLine()) != null) {

                num++;
                
                if(line.startsWith("#")) {
                    continue; // comment
                }
                String lessonFile = line.trim();
                
                ps.setString(1,  lessonFile);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    String lesson = rs.getString("lesson");
                    CustomLessonModel m = new CustomLessonModel(lesson, lessonFile, this.subject);
                    lessons.add(m);
                }
                else {
                    __logger.warn("line=" + num + ", Could not lookup lesson title for: " + lessonFile);
                }
            }
            
            int cid = createOrClearOutProgram(conn, this.name, adminId, loadOrder, gradeLevel);
            CmCustomProgramDao.getInstance().updateCustomProgram(conn, adminId, cid, this.name, lessons, false);    
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        __logger.info("Custom program created!");
    }


    /** If program exists, remove all lessons
     *  if does not exist, create new one.
     *  
     * @param conn
     * @param gradeLevel2 
     * @param loadOrder 
     * @param name2
     * @param adminId2
     * @return
     */
    private int createOrClearOutProgram(Connection conn, String name, int adminId, int loadOrder, int gradeLevel) throws Exception {
        CustomProgramModel cp = CmCustomProgramDao.getInstance().getCustomProgram(conn, adminId, name);
        if(cp == null) {
            cp = CmCustomProgramDao.getInstance().createNewCustomProgram(conn, adminId, name, new ArrayList<CustomLessonModel>(), loadOrder, gradeLevel);
        }
        return cp.getProgramId();
    }

    static public void main(String args[]) {
        try {
            
            
            List<String> lines = (List<String>)FileUtils.readLines(new File(args[0]));
            for(String l: lines) {
                if(l.length() == 0 || l.startsWith("#")) {
                    continue;
                }
                
                String as[] = l.split(",");
                if(as.length != 6) {
                    __logger.error("Must have 5 fields");
                    continue;
                }
                
                String file = as[0].trim();
                String name = as[1].trim();
                String subject = as[2].trim();
                int adminId = Integer.parseInt(as[3].trim());
                int loadOrder = Integer.parseInt(as[4].trim());
                int gradeLevel = Integer.parseInt(as[5].trim());
                
                CustomProgramCreate cp = new CustomProgramCreate(file, name, subject, adminId, loadOrder, gradeLevel);
                cp.createCustomProgram();
            }
            
            __logger.info("Custom Program creation success!");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        System.exit(0);
    }
}
