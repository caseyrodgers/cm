package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.user.client.rpc.AsyncCallback;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.rpc.AddMathMlResourceAction;

/**
 * Manage the creating and uploading of
 * solution resources.
 *
 */
public class SolutionResourceAddDialog extends Window {

    Callback callback;
    TextArea textArea = new TextArea();
    String pid;
    public SolutionResourceAddDialog(String pid, Callback callback) {
        this.pid = pid;
        this.callback = callback;
        setSize(500, 400);
        setHeading("Add New Solution Resource");


        textArea.setSize(600,200);
        add(new Html("<h1>Paste MathML</h1>"));
        add(textArea);

        addButton(new Button("Add Resource", new SelectionListener<ButtonEvent>() {
               @Override
               public void componentSelected(ButtonEvent buttonEvent) {

                   String file= "resource_" + System.currentTimeMillis();
                   String mathMl = textArea.getValue();

                   AddMathMlResourceAction action = new AddMathMlResourceAction(SolutionResourceAddDialog.this.pid, file, mathMl);
                   SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
                       public void onFailure(Throwable caught) {
                           com.google.gwt.user.client.Window.alert(caught.getMessage());
                           caught.printStackTrace();
                       }

                       public void onSuccess(RpcData result) {
                           SolutionResourceAddDialog.this.callback.resourceAdded();
                           hide();
                       }
                   });

               }}));

        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                hide();
            }
        }));

        setVisible(true);
    }

    public static interface Callback {
        void resourceAdded();
    }
}
