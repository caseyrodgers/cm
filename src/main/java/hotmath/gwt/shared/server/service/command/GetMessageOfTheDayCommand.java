package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetMessageOfTheDayAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StringHolder;

import java.sql.Connection;

import sb.util.SbFile;

public class GetMessageOfTheDayCommand implements ActionHandler<GetMessageOfTheDayAction, StringHolder>{

    @Override
    public StringHolder execute(Connection conn, GetMessageOfTheDayAction action) throws Exception {
        String messageOfTheDay = new SbFile(CatchupMathProperties.getInstance().getCatchupRuntime() + "/admin_motd.html").getFileContents().toString("\n");
        return new StringHolder(messageOfTheDay);
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetMessageOfTheDayAction.class;
    }

}
