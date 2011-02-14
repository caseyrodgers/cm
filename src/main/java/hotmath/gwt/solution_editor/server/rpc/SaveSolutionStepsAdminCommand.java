package hotmath.gwt.solution_editor.server.rpc;

import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.SaveSolutionStepsAdminAction;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;

import java.sql.Connection;

public class SaveSolutionStepsAdminCommand implements ActionHandler<SaveSolutionStepsAdminAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveSolutionStepsAdminAction action) throws Exception {
        SolutionDef def = new SolutionDef(action.getPid());
        TutorSolution ts = new TutorSolution("sm", def, action.getStatement(), action.getSteps());
        new CmSolutionManagerDao().saveSolutionXml(conn, action.getPid(), ts.toXml());
        RpcData rdata = new RpcData("status=OK");
        return rdata;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SaveSolutionStepsAdminAction.class;
    }

}
    