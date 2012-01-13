package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

import sb.client.SbTesterFrameGeneric;
import sb.util.SbException;
import sb.util.SbTestImpl;

/** checks for existence of mobile videos for 
 *  all prescriptions.
 *  
 * @author casey
 *
 */
public class VideoChecker implements SbTestImpl {
    
    public VideoChecker() {}
    
    public void runTests() {
        
        String sql = "select i.file,l.lesson,link_key, count(*) " +
                     " from inmh_link i " +
                     " JOIN HA_PROGRAM_LESSONS_static l on l.file = i.file " +
                     " where link_type = 'video' " +
                     " and subject > '' " +
                     " group by subject, i.file,l.lesson,link_key ";
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ResultSet rs  = conn.createStatement().executeQuery(sql);
            while(rs.next()) {
                if(!doesExistAsMobileVersion(rs.getString("link_key"))) {
                    System.out.print("DOES NOT EXIST: " + rs.getString("lesson") + ", " + rs.getString("link_key"));
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null, null,conn);
        }
    }
    
    private boolean doesExistAsMobileVersion(String videoKey) throws Exception {
        
        String base = CatchupMathProperties.getInstance().getSolutionBase();
        
        String videoPath = "";
        String flvPath="";
        String mp4Path="";
        String ogvPath="";
        
        if(videoKey.indexOf("youtube") > -1 || videoKey.indexOf("mathtv") > -1) {
            videoPath = videoKey.substring(0, videoKey.indexOf(".flv"));

            String p[] =videoPath.split("/");

            flvPath = videoPath + ".flv";
            mp4Path = "/help/flvs/tw/" + p[p.length-1]  + ".mp4";
            ogvPath = "/help/flvs/tw/" + p[p.length-1]  + ".ogv";
        }
        else {
            videoPath = "/help/flvs/tw/" + videoKey;
            
            flvPath = videoPath + ".flv";
            
            mp4Path = "/help/flvs/tw/" + videoKey + ".mp4";
            ogvPath = "/help/flvs/tw/" + videoKey + ".ogv";
        }        
        videoPath = base + videoPath;
        
        /** should be a .ogg and a .mp4 and a .flv
         * 
         */
        
        if(!new File(base, flvPath).exists()) {
            System.out.println(videoKey + ": " + ".flv does not exist");
            return false;
        }
        if(!new File(base, mp4Path).exists()) {
            System.out.println(videoKey + ": " + ".mp4 does not exist");
            return false;
        }
        if(!new File(base, ogvPath).exists()) {
            System.out.println(videoKey + ": " + ".ogv does not exist");
            return false;
        }
        
        return true;
        
    }

    @Override
    public void doTest(Object objLogger, String sFromGUI) throws SbException {
        runTests();
    }
    
    
    
    public static void main(String as[]) {
        try {
            System.out.println("Video Checker: look for missing videos requried for mobile");
            new VideoChecker().runTests();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
