package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProblemStatementPanel extends LayoutContainer {
    
    String pid;
    public ProblemStatementPanel(String pid) {
        this.pid = pid;
        setId("problem-statement-panel");
        addStyleName("tutoroutput");
        
        setScrollMode(Scroll.AUTO);
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getSolutionProblemStatementHtml(pid, new AsyncCallback() {
            @Override
            public void onSuccess(Object result) {
                String txt = (String)result;
                Html html = new Html();
                html.addStyleName("problem_statement");
                html.setHtml(txt);
                add(html);
                layout();
            }
            @Override
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });
    }
}
