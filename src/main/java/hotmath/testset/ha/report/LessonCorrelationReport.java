package hotmath.testset.ha.report;

import hotmath.HotMathProperties;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;

import sb.util.SbFile;



/** Checks all CM Programs for anomalies.  
 * 
 * Writes to table HA_PRESCRIPTION_LOG
 * 
 * Also populates the HA_PROGRAM_LESSON table which 
 * contains all the ACTIVE lessons currently in play.
 * 
 * 
 * @author casey
 *
 */
public class LessonCorrelationReport {

    static Logger __logger = Logger.getLogger(PrescriptionReport.class);
    
    public LessonCorrelationReport(final Connection conn) throws Exception {
        
        PreparedStatement ps=null;
        try {
        
            
            ps = conn.prepareStatement("select * from inmh_standard where topic <> '' order by topic");
            ResultSet rs = ps.executeQuery();
                     
            System.out.println("--Start of Lesson Correlation with State Standards");
            while(rs.next()) {
                
                String topic = rs.getString("topic");
                String standard = rs.getString("standard_name");
                
                String title = topic;
                title = getTitle(conn, topic);

                System.out.println(title + "\t" + standard);
            }
            System.out.println("--End of Lesson Correlation with State Standards");

        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
    }
    
    private String getTitle(final Connection conn, String topic) throws Exception {
        try {
            String filePath = HotMathProperties.getInstance().getHotMathWebBase();
            
            String topicFile = new File(filePath + "/hotmath_help/topics", topic).getAbsolutePath();
            String content = new SbFile(topicFile).getFileContents().toString("\n");
            
            Parser htmlParser = new Parser();
            
            htmlParser.setInputHTML(content);
            return getTitleContent(htmlParser);
        }
        catch(Exception e) {
            return topic + " (file not found)";
        }
    }
    
    protected String getTitleContent(Parser htmlParser) throws Exception {
        NodeList list = htmlParser.extractAllNodesThatMatch(new NodeClassFilter(TitleTag.class));
        if (list.size() == 0) {
            throw new Exception("No title found");
        } else {
            return ((TitleTag) list.elementAt(0)).getTitle();
        }
    }
    
    public static void main(String[] args) {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();    
            new LessonCorrelationReport(conn);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        
        System.exit(0);
    }

}