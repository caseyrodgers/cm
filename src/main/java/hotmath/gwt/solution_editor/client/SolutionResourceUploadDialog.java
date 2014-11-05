package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.SubmitHandler;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;

public class SolutionResourceUploadDialog extends GWindow {

    String pid;
    Callback callback;
    private CmList<SolutionResource> resources;

    public SolutionResourceUploadDialog(String pid, Callback callback, CmList<SolutionResource> resources) {
        super(false);
        
        this.pid = pid;
        this.callback = callback;
        this.resources = resources;
        setPixelSize(300, 150);
        setHeadingText("Upload Solution Resource");
        setWidget(createUploadForm());
        setVisible(true);
    }

    private Widget createUploadForm() {
        final FileUploadField fileUpload = new FileUploadField();
        final FormPanel form = new FormPanel();
        form.setEncoding(FormPanel.Encoding.MULTIPART);
        form.setMethod(FormPanel.Method.POST);
        
        form.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                if (!event.isCanceled()) {
                    callback.resourceAdded();
                    hide();
                } else {
                    CmMessageBox.showAlert("There was a problem saving resource: " + event);
                }
            }
        });
        form.setAction("/solution_editor/resourceUpload");
        // fileUpload.setFieldLabel("File");
        fileUpload.setAllowBlank(false);
        fileUpload.setName("resourceUpload.field");

        FlowLayoutContainer flow = new FlowLayoutContainer();
        Hidden hidden = new Hidden();
        hidden.setName("resourceUpload.pid");
        hidden.setValue(pid);
        flow.add(hidden);
        flow.add(fileUpload);
        form.setWidget(flow);
        
        FramedPanel frame = new FramedPanel();
        frame.setWidget(form);
        frame.addButton(new TextButton("Upload Resource",
                new SelectHandler() {
                    @Override
                    public void onSelect(SelectEvent event) {
                        String fileName = fileUpload.getValue();
                        String p[] = fileName.split("\\\\");  // try ms first
                        if(p.length == 1) {
                            p = fileName.split("/");  // then mac
                        }
                        fileName = p[p.length-1];
                        
                        boolean doesExist=false;
                        for(SolutionResource r: resources) {
                            String file = r.getFile();
                            if(fileName.equals(file)) {
                                doesExist = true;
                                break;
                            }
                        }
                        
                        boolean confirm=true;
                        if(doesExist) {
                            confirm = com.google.gwt.user.client.Window.confirm("This resource already exists, do you want overwrite?");
                        }

                        if(confirm) {
                            doSubmit(form);
                        }
                    }
                }));

        return frame;
    }
    
    private void doSubmit(FormPanel form) {
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, "Uploading resource ..."));
        form.submit();
    }

    public interface Callback {
        void resourceAdded();
    }
}
