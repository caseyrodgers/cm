package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardAsTemplateAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class CreateTemplateFromClipboardImage extends GWindow {
    
    private ShowWorkPanel2 showWork;
    private CallbackOnComplete callback;

    public CreateTemplateFromClipboardImage(ShowWorkPanel2 showWork, CallbackOnComplete callback) {
        super(false);
        
        this.showWork = showWork;
        this.callback = callback;
        
        setHeadingText("Create Whiteboard Template");
        setPixelSize(540, 380);
        setResizable(false);
        setMaximizable(false);
        setModal(true);

        String html="";
        html += "<applet id='clipboard_image' " +
                " archive='/gwt-resources/screenshot/clipboard_image.jar'" +
                " code='de.christophlinder.supa.SupaApplet'" + 
                " width='100%' " +
                " height='100%'>" +
                " <!--param name='clickforpaste' value='true'-->" +
                " <param name='imagecodec' value='png'>" +
                " <param name='encoding' value='base64'>" +
                " <param name='trace' value='false'>" +
                " <param name='previewscaler' value='fit to canvas'>" +
                " <!--param name='backgroundcolor' value='#FF0000'-->" +
                " <!--param name='trace' value='true'-->" +
                " <param name='pasteonload' value='true'>" +
                " Applets disabled :(" +
                " </applet>";
        
        HTML htmlWidget = new HTML(html);
        
       
        BorderLayoutContainer bCont = new BorderLayoutContainer();
        FramedPanel framed = new FramedPanel();
        framed.setBorders(false);
        framed.setHeaderVisible(false);
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(new HTML("<div style='position: absolute;right: 10px;top: 0;width: 220px;height: 4em;'>Copy an image into your clipboard and then paste it into the preview area.</div>"));
        framed.setWidget(flow);
        bCont.setNorthWidget(framed, new BorderLayoutData(50));
        bCont.setCenterWidget(htmlWidget);

        setWidget(bCont);
        
        
        addTool(new TextButton("Paste Clipboard Image", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                jsni_getClipboardImage();
            }
        }));
        
        addButton(new TextButton("Create Template", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                createTemplateFromImage();
            }
        }));
        
        addCloseButton();
        
        setVisible(true);
    }

    protected void saveClipboardImage() {
        final String dataUrl = jsni_getClipboardImageData();
        if(dataUrl == null) {
            CmMessageBox.showAlert("Image is null");
            return;
        }
        
        new RetryAction<RpcData>() {

            @Override
            public void attempt() {
                SaveWhiteboardAsTemplateAction action = new SaveWhiteboardAsTemplateAction(UserInfoBase.getInstance().getUid(), dataUrl);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(RpcData value) {
                hide();
                callback.isComplete();
            }
        }.register();
        
    }

    native protected String jsni_getClipboardImageData() /*-{
        try {
            return $wnd.clipboard_image.getEncodedString();
        }
        catch(e) {
            alert(e);
        }
    }-*/;
    
    native protected void jsni_getClipboardImage() /*-{
        try {
            $wnd.clipboard_image.pasteFromClipboard();
        }
        catch(e) {
            alert(e);
        }
    }-*/;

    protected void createTemplateFromImage() {
        saveClipboardImage();
    }

    static public void doTest() {
        new CreateTemplateFromClipboardImage(null,  new CallbackOnComplete() {
            @Override
            public void isComplete() {
                System.out.println("Done");
            }
        });
    }

}
