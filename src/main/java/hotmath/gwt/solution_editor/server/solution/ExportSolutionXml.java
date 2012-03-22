package hotmath.gwt.solution_editor.server.solution;

import hotmath.ProblemID;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

import sb.util.SbFile;

/** Convert old style Hotmath XML to new style
 * 
 * @author casey
 *
 */
public class ExportSolutionXml {
    Connection conn;
    public ExportSolutionXml(Connection conn) {
        this.conn = conn;
    }

    private void output(String pid) {
        try {
            System.out.println(pid);
            String xml = new CmSolutionManagerDao().getSolutionXml(conn, pid);
            
            File fileDir = new File("/temp/solution_output/" + new ProblemID(pid).getBook());
            fileDir.mkdirs();
            SbFile file = new SbFile(new File(fileDir, pid + ".xml"));
            file.setFileContents(xml);
            
            file.writeFileOut();
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
 
    static public void main(String as[]) {
        System.out.println("Outputing");
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ExportSolutionXml converter = new ExportSolutionXml(conn);
            
            ResultSet rs = conn.createStatement().executeQuery("select problemindex from SOLUTIONS where booktitle in (select distinct textcode from HA_TEST_DEF)");
            while(rs.next()) {
                converter.output(rs.getString("problemindex"));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        
        System.out.println("Convertions complete");
    }
}
