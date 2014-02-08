package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.StringHolder;
import hotmath.gwt.cm_rpc.client.rpc.GetMessageOfTheDayAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import sb.util.SbFile;

public class GetMessageOfTheDayCommand implements ActionHandler<GetMessageOfTheDayAction, StringHolder>{

    @Override
    public StringHolder execute(Connection conn, GetMessageOfTheDayAction action) throws Exception {
        /**
          There are 2 CM MOTD screens, one that is shown to accounts
          in the first month of their account (MOTD_ADMIN_NEW_USER). 
          And the other to others (MOTD_ADMIN).
          
           The 1-month screen takes precedence.
        */
        
        /**          *  
         *  NEW_USERS and EXISTING_USERS 
         */
        String propFile = CatchupMathProperties.getInstance().getCatchupRuntime() + "/admin_motd.html";
        String messages[] = new SbFile(propFile).getFileContents().toString("\n").split("--NEW USER--");
        
        AccountInfoModel ai = CmAdminDao.getInstance().getAccountInfo(action.getAdminId());
        Date accountCreated = ai.getAccountCreateDate();
        String messageOfTheDay=null;
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.MONTH ,  -1 );
        
        if(accountCreated != null && accountCreated.compareTo(calendar.getTime()) > 0 && messages.length > 1) {
            messageOfTheDay = messages[1];
        }
        else {
            messageOfTheDay = messages[0];
        }
        return new StringHolder(messageOfTheDay);
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetMessageOfTheDayAction.class;
    }
    
}
