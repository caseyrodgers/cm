package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;

public class ReportCardWindow extends CmWindow {
    
    public ReportCardWindow(StudentModel student) {
        
        
        createReportRpc(student);
        setSize(800, 600);
        setHeading("Catchup Math Report Card for: " + student.getName());
        
        Button btn = new Button("Close");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
       
        addButton(btn);
    }


    private void createReportRpc(final StudentModel sm) {
        
        CatchupMathTools.setBusy(true);
        List<Integer> studentUids = new ArrayList<Integer>();
        studentUids.add(sm.getUid());

        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new GeneratePdfAction(PdfType.REPORT_CARD,sm.getAdminUid(),studentUids), new AsyncCallback<CmWebResource>() {

            public void onSuccess(CmWebResource webResource) {
                Frame frame = new Frame();
                frame.setWidth("100%");
                frame.setHeight("480px");
                frame.setUrl(webResource.getUrl());
                
                removeAll();
                setLayout(new FitLayout());
                add(frame);
                
                setVisible(true);
                
                CatchupMathTools.setBusy(false);
            }

            public void onFailure(Throwable caught) {
                CatchupMathTools.setBusy(false);
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }
    
}
