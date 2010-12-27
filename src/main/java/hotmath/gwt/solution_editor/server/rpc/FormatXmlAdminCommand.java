package hotmath.gwt.solution_editor.server.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.FormatXmlAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionAdminResponse;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.util.HtmlCleanser;

import java.sql.Connection;

public class FormatXmlAdminCommand implements ActionHandler<FormatXmlAdminAction,SolutionAdminResponse>{
    
    public FormatXmlAdminCommand() {}
    

    @Override
    public SolutionAdminResponse execute(Connection conn, FormatXmlAdminAction action) throws Exception {
        // TODO Auto-generated method stub
        String xml = action.getXml();
        
        String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                      "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" " +
                      "\"http://www.w3.org/Math/DTD/mathml2/mathml2.dtd\">";

        xml = head + "<format_wrapper>"+xml + "</format_wrapper>";
        
       //  xml = HtmlCleanser.getInstance().cleanseHtml(xml);
        String formatedXml = new CmSolutionManagerDao().formatXml(xml);
        
        formatedXml = formatedXml.substring(formatedXml.indexOf("<format_wrapper>")+16);
        formatedXml = formatedXml.substring(0, formatedXml.indexOf("</format_wrapper>"));
        return new SolutionAdminResponse(formatedXml.trim());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return FormatXmlAdminAction.class;
    }
}
