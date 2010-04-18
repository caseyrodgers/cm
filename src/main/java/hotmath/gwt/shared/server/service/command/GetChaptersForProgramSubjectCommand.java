package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetChaptersForProgramSubjectAction;

import java.sql.Connection;

public class GetChaptersForProgramSubjectCommand implements ActionHandler<GetChaptersForProgramSubjectAction, CmList<ChapterModel>>{

	@Override
	public CmList<ChapterModel> execute(Connection conn,GetChaptersForProgramSubjectAction action) throws Exception {
            CmAdminDao cma = new CmAdminDao();
            return cma.getChaptersForProgramSubject(conn, action.getProgId(), action.getSubjId());
	}

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		// TODO Auto-generated method stub
		return GetChaptersForProgramSubjectAction.class;
	}
	

}
