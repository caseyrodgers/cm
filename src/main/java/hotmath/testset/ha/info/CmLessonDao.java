package hotmath.testset.ha.info;

import hotmath.HotMathProperties;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;

import sb.util.SbFile;

/** Lesson data function .. extracting title, finding files .etc..
 * 
 * 
 * @author casey
 *
 */
public class CmLessonDao {
    
    static private CmLessonDao __instance;
    static public CmLessonDao getInstance() {
        if(__instance == null) {
            __instance = new CmLessonDao();
        }
        return __instance;
    }
    
    public String lookupLessonFileFromName(Connection conn, String lesson) throws Exception {
        PreparedStatement p=null;
        try {
            String sql = "select distinct file from HA_PROGRAM_LESSONS_static where lesson = ?";
            p = conn.prepareStatement(sql);
            
            p.setString(1, lesson);
            ResultSet rs = p.executeQuery();
            if(!rs.first()) {
                throw new Exception("No lesson file found: " + lesson);
            }
            else {
                String file = rs.getString("file");
                return file;
            }
        }
        finally {
            SqlUtilities.releaseResources(null,  p, null);
        }
    }


    public String getTopicLessonTitle(String file) throws Exception {
        
        PreparedStatement ps = null;
        Connection conn = HMConnectionPool.getConnection();
        try {
            ps = conn.prepareStatement("select lesson from HA_PROGRAM_LESSONS where file = ? limit 1");
            ps.setString(1, file);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
            }
            
            return extractTitleFromFile(file);
        }
        finally {
            SqlUtilities.releaseResources(null, ps,conn);
        }
    }
    
    static Parser htmlParser = new Parser();  
    public String  extractTitleFromFile(String file) throws Exception {
        
        String base = HotMathProperties.getInstance().getHotMathWebBase() + HotMathProperties.getInstance().getINMHWebHome();
        String htmlContents = new SbFile(new File(base, file)).getFileContents().toString("\n");
        htmlParser.setInputHTML(htmlContents);
        String title = getTitleContent(htmlParser);
        
        return title;
    }
    
    public String getTitleContent(Parser htmlParser) throws Exception {
        
        NodeList list = htmlParser.extractAllNodesThatMatch(new NodeClassFilter(TitleTag.class));
        
        String title=null;
        if (list.size() > 0) {
            title = ((TitleTag) list.elementAt(0)).getTitle();
        }
        return title;
    }
    
}
