package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
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
        new RetryAction<CmWebResource>() {
            @Override
            public void attempt() {
                CatchupMathTools.setBusy(true);
                CmServiceAsync s = CmShared.getCmService();
                setAction(action);
                s.execute(action,this);                
            }
            public void oncapture(CmWebResource webResource) {
                CatchupMathTools.setBusy(false);
                
                Frame frame = new Frame();
                frame.setWidth("100%");
                frame.setHeight("480px");
                frame.setUrl(webResource.getUrl());
                
                removeAll();
                setLayout(new FitLayout());
                add(frame);
                
                setVisible(true);
            }
        }.register();
    }
}
