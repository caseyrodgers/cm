package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.CmWebResource;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;

public class PdfWindow extends CmWindow {

    Action<CmWebResource> action;
    Integer aid;
    public PdfWindow(Integer aid,  String title, Action<CmWebResource> action) {
        this.action = action;
        this.aid = aid;
        createPdfRpc();
        setSize(800, 600);
        setHeading(title);
        
        Button btn = new Button("Close");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
       
        addButton(btn);
    }


    private void createPdfRpc() {
        
        CatchupMathTools.setBusy(true);

        CmServiceAsync s = CmShared.getCmService();
        s.execute(action, new AsyncCallback<CmWebResource>() {

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
