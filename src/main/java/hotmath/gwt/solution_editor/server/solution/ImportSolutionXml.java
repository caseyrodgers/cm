package hotmath.gwt.solution_editor.server.solution;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.io.FileUtils;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

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

    private void input(String pid, String file) {
        try {
        	System.out.println("Importing solution '" + pid + "' from '" + file);
            
            String solutionXml = FileUtils.readFileToString(new File(file));
            String sql = "update SOLUTIONS set solutionxml = ? where problemindex = ?";
            PreparedStatement ps=null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, solutionXml);
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
    	
    	String pid = as[1];
    	String file = as[2];
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ImportSolutionXml converter = new ImportSolutionXml(conn);
            converter.input(pid, file);
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
