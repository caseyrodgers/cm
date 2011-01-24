package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.gwt.cm_admin.server.model.highlight.CmHighLightManager.HighLightStat;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

/** Update Sections Passed stats
 * 
 * Provide basic routines used to update db
 * 
 * 
 * @author casey
 *
 */
abstract public class HighLightStatImplBase implements HighLightStat {
    
    final static Logger __logger = Logger.getLogger(HighLightStatImplBase.class);
    String statName;
    
    public HighLightStatImplBase(){}
    
    @Override
    public void generateStat(Connection conn) throws Exception {
        GregorianCalendar cal = new GregorianCalendar(2009,0,0);
        getStatsFromDate(conn,cal.getTime());
    }
    
    
    abstract public void getStatsFromDate(final Connection conn,Date fromDate) throws Exception;
    
    @Override
    public CmList<HighlightReportData> getHighLightData(final Connection conn, Date fromDate, Date toDate,int adminId, List<String> uids) {
        CmList<HighlightReportData> list = new CmArrayList<HighlightReportData>();
        return list;
    }
    
    public void writeStatRecord(final Connection conn, int uid, Date runDate, String columnName, int value) throws Exception {
        
        PreparedStatement psCheck=null;
        try {
            String sql = "select 'x' from HA_USER_HIGHLIGHT where uid = ? and highlight_time = ?";
            psCheck = conn.prepareStatement(sql);
            psCheck.setInt(1, uid);
            psCheck.setDate(2, new java.sql.Date(runDate.getTime()));
            ResultSet rs = psCheck.executeQuery();
            if(!rs.first()) {
                /** does not exist, so create
                 * 
                 */
                PreparedStatement psInsert=null;
                try {
                    String sqlInsert = "insert into HA_USER_HIGHLIGHT(uid, highlight_time)values(?,?)";
                    psInsert = conn.prepareStatement(sqlInsert);
                    psInsert.setInt(1, uid);
                    psInsert.setDate(2, new java.sql.Date(runDate.getTime()));
                    if(psInsert.executeUpdate() != 1) {
                        __logger.warn("Could not add new stat record to HA_USER_HIGHLIGHT: " + psInsert);
                    }
                }
                finally {
                    SqlUtilities.releaseResources(null,psInsert,null);
                }
            }
        }
        finally {
            SqlUtilities.releaseResources(null,psCheck,null);
        }
        

        /** update existing record
         * 
         */
        
        PreparedStatement psUpdate=null;
        try {
            String sql = "update HA_USER_HIGHLIGHT set " + columnName + " = ? where uid = ? and highlight_time = ?";
            psUpdate = conn.prepareStatement(sql);
            psUpdate.setInt(1, value);
            psUpdate.setInt(2, uid);
            psUpdate.setDate(3, new java.sql.Date(runDate.getTime()));
            if(psUpdate.executeUpdate() != 1) {
                __logger.warn("Could not update stat record to HA_USER_HIGHLIGHT: " + psUpdate);
            }
        }
        finally {
            SqlUtilities.releaseResources(null,psUpdate,null);
        }
    }

    public String getStatName() {
        return statName;
    }

    public void setStatName(String statName) {
        this.statName = statName;
    }
}
