package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForCustomProgramTestAction;

import java.sql.Connection;
import java.util.List;

public class GetLessonItemsForCustomProgramTestCommand implements ActionHandler<GetLessonItemsForCustomProgramTestAction, CmList<LessonItemModel>>{

    @Override
    public CmList<LessonItemModel> execute(Connection conn, GetLessonItemsForCustomProgramTestAction action) throws Exception {
        
    	List<CustomLessonModel> cmList = CmCustomProgramDao.getInstance().getCustomProgramLessonsForTestId( action.getTestId());
    	
    	CmList<LessonItemModel> list = new CmArrayList<LessonItemModel>();

    	for (CustomLessonModel cm : cmList) {
    		LessonItemModel lm = new LessonItemModel();
    		lm.setFile(cm.getFile());
    		lm.setName(cm.getLesson());
    		lm.setPrescribed("Prescribed");
    		list.add(lm);
    	}
        return list;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetLessonItemsForCustomProgramTestAction.class;
    }

}
