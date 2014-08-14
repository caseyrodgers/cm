package hotmath.gwt.cm_admin.server.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools_2.client.rpc.action.GetChaptersForProgramSubjectAction;

import java.sql.Connection;

public class GetChaptersForProgramSubjectCommand implements ActionHandler<GetChaptersForProgramSubjectAction, CmList<ChapterModel>>{

	@Override
	public CmList<ChapterModel> execute(Connection conn,GetChaptersForProgramSubjectAction action) throws Exception {
            CmAdminDao cma = CmAdminDao.getInstance();
            CmList<ChapterModel> list = new CmArrayList<ChapterModel>();
            list.addAll(cma.getChaptersForProgramSubject(action.getProgId(), action.getSubjId()));
            return list;
	}

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return GetChaptersForProgramSubjectAction.class;
	}
}
