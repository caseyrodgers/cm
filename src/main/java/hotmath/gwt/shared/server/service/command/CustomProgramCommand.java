package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.server.service.ActionHandler;

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
                CmList<CustomLessonModel> list = (CmList<CustomLessonModel>)CmCacheManager.getInstance()
                    .retrieveFromCache(CacheName.CUSTOM_PROGRAM_LESSONS, CacheName.CUSTOM_PROGRAM_LESSONS);
                if(list == null) {
                    list = new CmCustomProgramDao().getAllLessons(conn);
                    CmCacheManager.getInstance().addToCache(CacheName.CUSTOM_PROGRAM_LESSONS, CacheName.CUSTOM_PROGRAM_LESSONS, list);
                }
                return list;
                
            case GET_CUSTOM_PROGRAM:
                return new CmCustomProgramDao().getCustomProgramDefinition(conn, action.getProgramId());
                
            case SAVE:
                return new CmCustomProgramDao().saveChanges(conn, action.getProgramId(), action.getProgramName(), action.getLessons());
                  
            case CREATE:                
                CustomProgramModel newProgram = new CmCustomProgramDao().createNewCustomProgram(conn, action.getAdminId(), action.getProgramName(), action.getLessons());
                return null;
                
            default:
                throw new IllegalArgumentException("Unknown ActionType: "  + action);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return CustomProgramAction.class;
    }

}
