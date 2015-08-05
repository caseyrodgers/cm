package hotmath.cm.util;

import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;


public class LessonFileExtractor {

    final static Logger __logger = Logger.getLogger(LessonFileExtractor.class);

    public void doIt() {
        Connection conn=null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        long startTime = System.currentTimeMillis();

        File outFile = new File("lesson_all_extract.txt");
        FileWriter fileWriter=null;
        try {
            fileWriter = new FileWriter(outFile);
            conn = HMConnectionPool.getConnection();

            String sql = "select distinct file from HA_PROGRAM_LESSONS order by file";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {

            	String file = rs.getString("file");
                
                String header = "\n\n\n------------------\n" +
                                "file: " + file + "\n" +
                                "-------------------\n\n";
                
                String textOnly=null;
                try {
                    LessonResult result = ActionDispatcher.getInstance().execute(new GetReviewHtmlAction(file));

                    textOnly = stripOffHtmlTagsAndApplyHeaderFooter(result.getLesson());
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                    textOnly = ex.getMessage();
                }
         
                fileWriter.write(header + textOnly);               
            }
        } catch (Exception e) { 
	    	e.printStackTrace();
	    } finally {
		    SqlUtilities.releaseResources(rs, ps, conn);
		    try {
		        fileWriter.close();
		    }
		    catch(Exception ee) {
		        ee.printStackTrace();
		    }
	    }
	    __logger.info("Completed writing extract file: " + outFile.getName());
	}
    
    private String stripOffHtmlTagsAndApplyHeaderFooter(String lesson)  throws Exception {
        Source source=new Source(lesson);
        String renderedText=source.getRenderer().toString();
        return renderedText;
    }

    static public void main(String as[]) {
        try {

            LessonFileExtractor fli = new LessonFileExtractor();
            fli.doIt();
            System.exit(0);
        	
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }    	
}
