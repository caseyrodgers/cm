package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;
import com.sencha.gxt.widget.core.client.info.Info;

public class WhiteboardTemplateFileUploadDialog extends GWindow {
    
    private SimplePanel panel;


    public WhiteboardTemplateFileUploadDialog() {
        super(false);
        
        setHeadingText("Upload Figure");
        setPixelSize(400,  150);
        
        FramedPanel framedPanel = new FramedPanel();
        setWidget(asWidget());
        
        addCloseButton();
        setVisible(true);
    }
    
    
    @Override
    public Widget asWidget() {
        if (panel == null) {
            panel = new SimplePanel();
       
            final FormPanel form = new FormPanel();
            form.setAction("myurl");
            form.setEncoding(Encoding.MULTIPART);
            form.setMethod(Method.POST);
            panel.add(form);
       
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
                MessageBox box = new MessageBox("File Upload Example", "Your file was uploaded.");
                box.setIcon(MessageBox.ICONS.info());
                box.show();
              }
            });
            addButton(btn);
          }
          return panel;        
    }
    
}
