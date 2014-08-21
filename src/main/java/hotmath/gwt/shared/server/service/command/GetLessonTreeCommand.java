package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmProgramListingDao;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.rpc.GetLessonTreeAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Provide high level access to program listing data
 * 
 * @author casey
 * 
 */
public class GetLessonTreeCommand implements ActionHandler<GetLessonTreeAction, CmList<LessonModel>> {

    @Override
    public CmList<LessonModel> execute(Connection conn, GetLessonTreeAction action) throws Exception {
        CmList<LessonModel> lessons = new CmArrayList<LessonModel>();

        
        
        ProgramListing pl = new CmProgramListingDao().getProgramListing();
        
        PreparedStatement ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_LESSON_TREE");
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                lessons.add(new LessonModel(rs.getString("lesson"), rs.getString("file"), rs.getString("subject")));
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }

        return lessons;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetLessonTreeAction.class;
    }
}
