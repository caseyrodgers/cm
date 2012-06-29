package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.GradeBookDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetGradeBookDataAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;

import java.sql.Connection;

import org.apache.log4j.Logger;

/** Get Student Active Info via CmProgramFlow
 *  
 * @author bob
 *
 */
public class GetGradeBookDataCommand implements ActionHandler<GetGradeBookDataAction, CmList<GradeBookModel>> {

    static final Logger logger = Logger.getLogger(GetActiveInfoForStudentUidCommand.class);

    @Override
    public CmList<GradeBookModel> execute(Connection conn, GetGradeBookDataAction action) throws Exception {
        CmList<GradeBookModel> cmList = new CmArrayList<GradeBookModel>();
        cmList.addAll(GradeBookDao.getInstance().getGradeBookData(action.getUid()));
        return cmList;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetGradeBookDataAction.class;
    }
}





