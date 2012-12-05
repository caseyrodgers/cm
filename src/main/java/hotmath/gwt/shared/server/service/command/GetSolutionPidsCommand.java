package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionPidsAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;

/** Return the pids associated with named book
 * 
 * @author casey
 *
 */
public class GetSolutionPidsCommand implements ActionHandler<GetSolutionPidsAction, CmList<String>> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionPidsAction.class;
    }

    @Override
    public CmList<String> execute(Connection conn, GetSolutionPidsAction action) throws Exception {
        CmList<String> pids = new CmArrayList<String>();
        pids.addAll(SolutionDao.getInstance().getSolutionPids(action.getBook()));
        return pids;
    }
}