package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

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
                    System.out.println(rs.getString("link_key"));
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


        File flvPath=null;
        File mp4Path=null;
        File ogvPath=null;
        
        
        if(!isANumber(videoKey) && !videoKey.startsWith("/")) {
            
            /** must be mathtv
             * MOVE TO CORRECT PLACE!
             */
            videoKey = "/help/flvs/mathtv/" + videoKey + ".flv";
        }


        if(videoKey.indexOf("youtube") > -1 || videoKey.indexOf("mathtv") > -1) {
            String videoPath = videoKey.substring(0, videoKey.indexOf(".flv"));

            String p[] =videoPath.split("/");

            flvPath = new File(base, videoPath + ".flv");
            mp4Path = new File(base, videoPath + ".mp4");
            ogvPath = new File(base, videoPath  + ".ogv");
        }
        else {
            String videoPath = "/help/flvs/tw/" + videoKey;

            flvPath = new File(base, videoPath + ".flv");

            mp4Path = new File(base, "/help/flvs/tw/" + videoKey + ".mp4");
            ogvPath = new File(base, "/help/flvs/tw/" + videoKey + ".ogv");
        }

        /** should be a .ogg and a .mp4 and a .flv
         *
         */

        if(!flvPath.exists()) {
            System.out.println(flvPath + ": " + ".flv does not exist");
            return false;
        }
        if(!mp4Path.exists()) {
            System.out.println(mp4Path + ": " + ".mp4 does not exist");
            return false;
        }
        if(!ogvPath.exists()) {
            System.out.println(ogvPath + ": " + ".ogv does not exist");
            return false;
        }

        return true;

    }
    
    private boolean isANumber(String x) {
        try {
            Integer.parseInt(x);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
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

        System.exit(0);
    }
}
