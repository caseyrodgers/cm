package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;

import java.sql.Connection;

/** Provide RPC services for creating and managing 
 *  a custom program
 *   
 * @author casey
 *
 */
public class CustomProgramCommand implements ActionHandler<CustomProgramAction, CmList<CustomLessonModel>>{

    @SuppressWarnings("unchecked")
    @Override
    public CmList<CustomLessonModel> execute(Connection conn, CustomProgramAction action) throws Exception {
        switch(action.getAction()) {
            case GET_ALL_LESSONS:
                return CmCustomProgramDao.getInstance().getAllLessons(conn);
                
            case GET_CUSTOM_PROGRAM:
                return CmCustomProgramDao.getInstance().getCustomProgramLessonsAllSegments(conn, action.getProgramId());
                
            case SAVE:
                return CmCustomProgramDao.getInstance().saveChanges(conn, action.getAdminId(),action.getProgramId(), action.getProgramName(), action.getLessons());
                  
            case CREATE:                
                CustomProgramModel newProgram = CmCustomProgramDao.getInstance().createNewCustomProgram(conn, action.getAdminId(), action.getProgramName(), action.getLessons());
                return new CmArrayList<CustomLessonModel>();
                
            case COPY:
                CmCustomProgramDao.getInstance().copyCustomProblem(conn, action.getAdminId(), action.getProgramId(), action.getDestAdminId());
                return new CmArrayList<CustomLessonModel>();
                
            default:
                throw new IllegalArgumentException("Unknown ActionType: "  + action);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CustomProgramAction.class;
    }

}
