package hotmath.gwt.cm_admin.server.model.highlight;

import java.sql.Connection;
import java.sql.PreparedStatement;

import hotmath.gwt.cm_admin.server.model.highlight.CmHighLightManager.HighLightStat;
import hotmath.util.sql.SqlUtilities;

public class HighLightStatImplVideosWatched implements HighLightStat {

    
    @Override
    public void generateStat(Connection conn) throws Exception {
        String sql="selecft ..";
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

}
