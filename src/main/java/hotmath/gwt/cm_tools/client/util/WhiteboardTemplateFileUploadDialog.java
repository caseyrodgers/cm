package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_tools.client.teacher.TeacherManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;
import com.sencha.gxt.widget.core.client.info.Info;

public class WhiteboardTemplateFileUploadDialog extends GWindow {
    
    private SimplePanel panel;
    private CallbackOnComplete callback;


    public WhiteboardTemplateFileUploadDialog(CallbackOnComplete callbackOnComplete) {
        super(false);

        this.callback = callbackOnComplete;
        
        setHeadingText("Upload Figure");
        setPixelSize(400,  130);
        setResizable(false);
        setMaximizable(false);
        setWidget(asWidget());
        
        addCloseButton();
        setVisible(true);
    }
    
    
    @Override
    public Widget asWidget() {
        if (panel == null) {
            panel = new SimplePanel();
            
            FramedPanel framedPanel = new FramedPanel();
       
            final FormPanel form = new FormPanel();
            form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
                @Override
                public void onSubmitComplete(SubmitCompleteEvent event) {
                    if(event.getResults().indexOf("Exception") > -1) {
                        CmMessageBox.showAlert("Upload Error", "File could not be uploaded.  You can only upload 'png', gif or 'jpg' files.");
                    }
                    else {
                        CmMessageBox.showAlert("Upload Complete", "File Uploaded successfully: ");
                        hide();
                        callback.isComplete();
                    }
                }
            });
            
            TeacherIdentity currTeacher = TeacherManager.getTeacher();
            if(currTeacher == null || currTeacher.isUnknown()) {
                CmMessageBox.showAlert("You must select a teacher first");
            }
            else {
                StringBuffer sb = new StringBuffer("/cm_admin/figureUpload");
                sb.append("?aid=").append(UserInfoBase.getInstance().getUid());
                form.setAction(sb.toString());

                form.setEncoding(Encoding.MULTIPART);
                form.setMethod(Method.POST);
            }
            framedPanel.setWidget(form);
       
            VerticalLayoutContainer p = new VerticalLayoutContainer();
            form.add(p, new MarginData(10));
       
            final FileUploadField file = new FileUploadField();
            file.addChangeHandler(new ChangeHandler() {
              @Override
              public void onChange(ChangeEvent event) {
                Info.display("File Changed", "You selected " + file.getValue());
              }
            });
            file.setName("uploadedfile");
            file.setAllowBlank(false);
       
            p.add(new FieldLabel(file, "File"), new VerticalLayoutData(-18, -1));
       
            TextButton btn = new TextButton("Submit");
            btn.addSelectHandler(new SelectHandler() {
       
              @Override
              public void onSelect(SelectEvent event) {
                if (!form.isValid()) {
                  return;
                }
                // normally would submit the form but for example no server set up to
                // handle the post
                // panel.submit();
                
                form.submit();
              }
            });
            
            framedPanel.setBodyBorder(false);
            framedPanel.setHeaderVisible(false);
            panel.setWidget(framedPanel);
            
            addButton(btn);
          }
          return panel;        
    }
    
}
