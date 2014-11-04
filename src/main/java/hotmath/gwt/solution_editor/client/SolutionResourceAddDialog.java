package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.solution_editor.client.rpc.AddMathMlResourceAction;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

/**
 * Manage the creating and uploading of
 * solution resources.
 *
 */
public class SolutionResourceAddDialog extends GWindow {

    Callback callback;
    TextArea textArea = new TextArea();
    String pid;
    public SolutionResourceAddDialog(String pid, Callback callback) {
        super(false);
        
        this.pid = pid;
        this.callback = callback;
        setPixelSize(500, 400);
        setHeadingText("Add New Solution Resource");


        textArea.setPixelSize(600,200);
        add(new HTML("<h1>Paste MathML</h1>"));
        add(textArea);

        addButton(new TextButton("Add Resource", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
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

        addButton(new TextButton("Close", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));

        setVisible(true);
    }

    public static interface Callback {
        void resourceAdded();
    }
}
