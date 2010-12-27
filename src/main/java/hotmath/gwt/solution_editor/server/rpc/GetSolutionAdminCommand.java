package hotmath.gwt.solution_editor.server.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionAdminResponse;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;

import java.sql.Connection;

public class GetSolutionAdminCommand implements ActionHandler<GetSolutionAdminAction, SolutionAdminResponse>{

    @Override
    public SolutionAdminResponse execute(Connection conn, GetSolutionAdminAction action) throws Exception {
        CmSolutionManagerDao dao = new CmSolutionManagerDao();
        switch(action.getType()) {
        case FORMAT:
               return new SolutionAdminResponse(dao.formatXml(action.getSolutionXml()));
        case GET:
               return new SolutionAdminResponse(new CmSolutionManagerDao().getSolutionXml(conn, action.getPid()));
        case CREATE:
            String newPid = new CmSolutionManagerDao().createNewSolution(conn);
            SolutionAdminResponse sar = new SolutionAdminResponse();
            sar.setPid(newPid);
            return sar;
        }
        throw new CmException("Unknown action: " + action);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionAdminAction.class;
    }
}
