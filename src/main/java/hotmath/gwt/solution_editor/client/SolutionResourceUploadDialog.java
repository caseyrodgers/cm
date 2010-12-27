package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;

public class SolutionResourceUploadDialog extends Window {

    String pid;
    Callback callback;

    public SolutionResourceUploadDialog(String pid, Callback callback) {
        this.pid = pid;
        this.callback = callback;
        setSize(500, 150);
        setHeading("Upload Solution Resource");
        add(createUploadForm());
        setVisible(true);
    }

    private LayoutContainer createUploadForm() {
        FileUploadField fileUpload = new FileUploadField();
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
                        EventBus.getInstance().fireEvent(
                                new CmEvent(EventTypes.STATUS_MESSAGE, "Uploading resource ..."));
                        form.submit();
                    }
                }));

        return lc;

    }

    public interface Callback {
        void resourceAdded();
    }
}
