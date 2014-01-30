package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.util.CmMessageBoxGxt2;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.ui.Hidden;

public class SolutionResourceUploadDialog extends Window {

    String pid;
    Callback callback;
    private CmList<SolutionResource> resources;

    public SolutionResourceUploadDialog(String pid, Callback callback, CmList<SolutionResource> resources) {
        this.pid = pid;
        this.callback = callback;
        this.resources = resources;
        setSize(500, 150);
        setHeading("Upload Solution Resource");
        add(createUploadForm());
        setVisible(true);
    }

    private LayoutContainer createUploadForm() {
        final FileUploadField fileUpload = new FileUploadField();
        final FormPanel form = new FormPanel();
        form.setEncoding(FormPanel.Encoding.MULTIPART);
        form.setMethod(FormPanel.Method.POST);
        final LayoutContainer lc = new LayoutContainer();
        form.addListener(Events.Submit, new Listener<FormEvent>() {
            public void handleEvent(FormEvent be) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                if (be.getResultHtml().equals("OK")) {
                    callback.resourceAdded();
                    hide();
                } else {
                    com.google.gwt.user.client.Window.alert("There was a problem saving resource: "
                            + be.getResultHtml());
                }
            }
        });
        form.setAction("/solution_editor/resourceUpload");
        fileUpload.setAllowBlank(false);
        fileUpload.setFieldLabel("File");
        fileUpload.setAllowBlank(false);
        fileUpload.setFieldLabel("File");
        fileUpload.setAllowBlank(false);
        fileUpload.setBorders(false);
        fileUpload.setName("resourceUpload.field");

        Hidden hidden = new Hidden();
        hidden.setName("resourceUpload.pid");
        hidden.setValue(pid);
        form.add(hidden);

        form.add(fileUpload);
        lc.add(form);
        form.addButton(new com.extjs.gxt.ui.client.widget.button.Button("Upload Resource",
                new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        String fileName = fileUpload.getValue();
                        String p[] = fileName.split("\\\\");  // try ms first
                        if(p.length == 1) {
                            p = fileName.split("/");  // then mac
                        }
                        fileName = p[p.length-1];
                        
                        boolean doesExist=false;
                        for(SolutionResource r: resources) {
                            String file = r.getFile();
                            String path = r.getUrlPath();

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

        return lc;

    }
    
    private void doSubmit(FormPanel form) {
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, "Uploading resource ..."));
        form.submit();
    }

    public interface Callback {
        void resourceAdded();
    }
}
