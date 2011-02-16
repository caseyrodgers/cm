package hotmath.gwt.solution_editor.server.rpc;

import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.solution_editor.client.rpc.SaveSolutionStepsAdminAction;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;

import java.sql.Connection;

public class SaveSolutionStepsAdminCommand implements ActionHandler<SaveSolutionStepsAdminAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveSolutionStepsAdminAction action) throws Exception {
        SolutionDef def = new SolutionDef(action.getPid());
        TutorSolution ts = new TutorSolution("sm", def, action.getStatement(), action.getSteps());
        
        /** make sure it has not been changed since read
         * 
         */
        String md5Now = new CmSolutionManagerDao().getSolutionMd5(conn, action.getPid());
        if(!md5Now.equals(action.getMd5OnRead())) {
            if(!action.isOverrideDirty()) {
                throw new CmException("Solution has been modified since read: " + action.getPid());
            }
        }
        
        new CmSolutionManagerDao().saveSolutionXml(conn, action.getPid(), ts.toXml());
        String md5 = new CmSolutionManagerDao().getSolutionMd5(conn, action.getPid());
        RpcData rdata = new RpcData("status=OK,md5=" + md5);
        return rdata;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SaveSolutionStepsAdminAction.class;
    }

}
    