package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.io.StringReader;
import java.sql.Connection;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;

public class GetAssignmentWhiteboardDataCommand implements ActionHandler<GetAssignmentWhiteboardDataAction, AssignmentWhiteboardData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentWhiteboardDataAction.class;
    }

    @Override
    public AssignmentWhiteboardData execute(Connection conn, GetAssignmentWhiteboardDataAction action) throws Exception {
        CmList<WhiteboardCommand> cList = new CmArrayList<WhiteboardCommand>();
        cList.addAll(AssignmentDao.getInstance().getWhiteboardData(action.getUid(),action.getAssignId(),action.getPid()));
        
        StudentProblemDto studentProblem = AssignmentDao.getInstance().getStudentProblem(action.getUid(), action.getAssignId(), action.getPid());
        
        String problemStatement = "";
//        try {
//            GetSolutionAction saction = new GetSolutionAction(0, 0, action.getPid());
//            SolutionInfo info = new GetSolutionCommand().execute(conn, saction);
//            problemStatement = extractProblemStatement(info.getHtml());
//        }
//        catch(Exception e) {
//            problemStatement = "ERROR: " + e.getMessage();
//        }
        //String problemStatement = "THE TEST STATEMENT";
        AssignmentWhiteboardData data = new AssignmentWhiteboardData(studentProblem, cList, problemStatement);
        
        return data;
    }

    private String extractProblemStatement(String html) {
        
        SAXBuilder parser = new SAXBuilder();
        try {
            //get the dom-document
            Document doc = parser.build(new StringReader(html));
            Filter f = new Filter() {
                
                @Override
                public boolean matches(Object obj) {
                    if(obj instanceof Element) {
                        //((Element)obj).getAt
                    }
                    return false;
                }
            };
            Iterator list = doc.getDescendants(f);
            while(list.hasNext()) {
                System.out.println("TEST: " + list.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
