package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_qa.server.CmQaDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQaItemProblemAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

import sb.mail.SbMailManager;

public class SaveQaItemProblemCommand implements ActionHandler<SaveQaItemProblemAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveQaItemProblemAction action) throws Exception {
        boolean saved = new CmQaDao().saveQaItemProblem(conn, action.getUserName(), action.getItem(), action.getProblem());
        
        
        /** send email message with contents */
        String subject="CM QA Program: " + action.getItem() + " (" + action.getUserName() + ")";
        String message = action.getProblem();
        
        try {
            String asTo[] = {"casey@hotmath.com","qa@hotmath.com"};
            SbMailManager.getInstance().sendMessage(subject, message,asTo,"admin@hotmath.com");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        return new RpcData("status=" + (saved?"OK":"Not Saved"));
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveQaItemProblemAction.class;
    }    
}
