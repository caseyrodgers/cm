package hotmath.gwt.solution_editor.server.solution;

import hotmath.ProblemID;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import sb.util.SbFile;
import sb.util.SbStringList;

/** Convert old style Hotmath XML to new style
 * 
 * @author casey
 *
 */
public class ImportSolutionXml {
    Connection conn;
    public ImportSolutionXml(Connection conn) {
        this.conn = conn;
    }

    private void input(String dir, String pid) {
        try {
            System.out.println("Importing: " + pid);
            
            ProblemID p = new ProblemID(pid);
            
            File dirFile = new File(dir + "/" + p.getBook());
            
            SbFile file = new SbFile(new File(dirFile, pid + ".xml"));
            SbStringList sbl = file.getFileContents();
            String xml = sbl.toString("\n");
            
            String sql = "update SOLUTIONS set solutionxml = ? where problemindex = ?";
            PreparedStatement ps=null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, xml);
                ps.setString(2, pid);   
                
                if(ps.executeUpdate() != 1) {
                    throw new Exception("Could not update: " + pid);
                }
            }
            finally {
                SqlUtilities.releaseResources(null,  ps, null);
            }
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
 
    static public void main(String as[]) {
        System.out.println("Importing ... ");
        
        String dir = "/temp/solution_input";
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ImportSolutionXml converter = new ImportSolutionXml(conn);
            
            //ResultSet rs = conn.createStatement().executeQuery("select problemindex from SOLUTIONS where booktitle in (select distinct textcode from HA_TEST_DEF)");
            ResultSet rs = conn.createStatement().executeQuery("select problemindex from SOLUTIONS where booktitle = 'placement'");
            while(rs.next()) {
                converter.input(dir, rs.getString("problemindex"));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        
        System.out.println("Import complete");
    }
}
