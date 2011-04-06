package hotmath.gwt.cm_qa.server;

import hotmath.gwt.cm_rpc.client.model.QaEntryModel;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

public class CmQaDao {
    
    public CmList<QaEntryModel> getQaItems(final Connection conn, String category) throws Exception {
        
        PreparedStatement ps=null;
        try {
            String sql = "select category, item, description, verified_time " +
                         "from QA_ITEM i " +
                         " where i.category like ? " +
                         "order by category, item";
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, category.equals("all")?"%":category);
            ResultSet rs = ps.executeQuery();
            
            CmList<QaEntryModel> items = new CmArrayList<QaEntryModel>();
            while(rs.next()) {
                boolean verified = rs.getDate("verified_time") != null;
                items.add(new QaEntryModel(rs.getString("item"), rs.getString("description"), verified));
            }
            return items;
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }
    
    
    public boolean saveQaItem(final Connection conn, String userName, String item, boolean verified) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "update QA_ITEM set verified_by = ?, verified_time = ? where item = ?"; 
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, userName);
            if(verified) {
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            }
            else {
                ps.setNull(2, Types.DATE);
            }
            ps.setString(3, item);
            
            int cnt = ps.executeUpdate();
            
            return cnt == 1;
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }
}
