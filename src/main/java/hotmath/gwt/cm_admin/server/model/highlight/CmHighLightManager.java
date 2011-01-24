package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/** Manages the generation and re-freshing
 *  of all group related highlight stats.
 *  
 *  Table HA_USER_HIGHLIGHT_DEF contains class
 *  name used to configure any given stat.  That
 *  class name must implement HighLightStat interface.
 *  
 *  This class will be called whenever it is time to 
 *  either generate or refresh the data.
 *  
 *  The HighLightStat class is also responsible for 
 *  persisting data.
 *  
 * @author casey
 *
 */
public class CmHighLightManager {
    final static Logger __logger = Logger.getLogger(CmHighLightManager.class);
    
    List<HighLightStat> stats = new ArrayList<HighLightStat>();
    public CmHighLightManager(final Connection conn) throws Exception {
        Statement st=null;
        try {
            String sql = "select * from HA_USER_HIGHLIGHT_DEF order by load_order";
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
                String className = rs.getString("generator_class");
                className = "hotmath.gwt.cm_admin.server.model.highlight." + className;
                HighLightStat stat = (HighLightStat)getClass().getClassLoader().loadClass(className).newInstance();
                stats.add(stat);
            }
        }
        catch(Exception e) {e.printStackTrace();}
        finally {
            SqlUtilities.releaseResources(null, st, null);
        }
    }
    
    /** Generate stats for all registered HighLightStat objects
     * 
     * @return
     * @throws Exception
     */
    public boolean generateAllStats(final Connection conn) throws Exception {
        __logger.info("Generating user highlight stats");
        try {
            for(HighLightStat stat: stats) {
                __logger.info("Generating stats for: " + stat.getClass().getName());
                stat.generateStat(conn);
                __logger.info("Completed generating stats for: " + stat.getClass().getName());
            }
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            __logger.info("Completed generating user highlight stats");
        }
    }
    
    public List<HighLightStat> getStats() {
        return stats;
    }

    public void setStats(List<HighLightStat> stats) {
        this.stats = stats;
    }

    public interface HighLightStat {
        void generateStat(final Connection conn) throws Exception;
    }
    
    
    static public void main(String as[]) {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            new CmHighLightManager(conn).generateAllStats(conn);
        }
        catch(Exception e) {
            __logger.error("Error running Highlight Manager", e);
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        System.exit(0);
    }
}
