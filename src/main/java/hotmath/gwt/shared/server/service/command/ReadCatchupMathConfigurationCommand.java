package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.ReadCatchupMathConfigurationAction;
import hotmath.gwt.shared.client.rpc.result.CmProperties;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

public class ReadCatchupMathConfigurationCommand implements ActionHandler<ReadCatchupMathConfigurationAction,CmProperties>{

    @Override
    public CmProperties execute(Connection conn, ReadCatchupMathConfigurationAction action) throws Exception {
    
        CmProperties properties = new CmProperties();
        
        properties.put("host.url", "http://hotmath.kattare.com:8081");
        
        return properties;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ReadCatchupMathConfigurationAction.class;
    }

}
