package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmAlertify.PromptCallback;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;


import hotmath.gwt.solution_editor.client.rpc.ReplaceTextSolutionsAction;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class SearchReplaceDialog extends GWindow {
    
    private List<SolutionSearchModel> resultSet;
    public SearchReplaceDialog() {
        super(false);
        setResizable(false);
        setHeadingText("Search and Replace");
        addButton(new TextButton("Do Replace", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                doReplace();
            }
        }));
        addCloseButton();

        
        drawForm();
        setPixelSize(400,  200);
    }

    protected void doReplace() {
        
        final String searchFor = _searchFor.getCurrentValue();
        final String replaceWith = _replaceWith.getCurrentValue();
        if(searchFor == null || searchFor.length() == 0 || replaceWith == null || replaceWith.length() == 0) {
            CmMessageBox.showAlert("Both 'Search For' and 'Replace With' must be specified");
            return;
        }
        
        CmMessageBox.confirm("Search/Replace",  "Are you sure you want to replace '" + searchFor + "' with '" + replaceWith + "' in all " + resultSet.size() + " solution(s)?", new ConfirmCallback() {
            
            @Override
            public void confirmed(boolean yesNo) {
                if(yesNo) {
                    CmList<SolutionSearchModel> probs = new CmArrayList<SolutionSearchModel>();
                    probs.addAll(resultSet);
                    ReplaceTextSolutionsAction action = new ReplaceTextSolutionsAction(probs, searchFor, replaceWith);
                    SolutionEditor.getCmService().execute(action, new AsyncCallback<RpcData>() {
                        @Override
                        public void onSuccess(RpcData result) {
                            int replaced = result.getDataAsInt("replaced");
                            String message = "Updated " + replaced + " solution(s)";
                           
                            String errors = result.getDataAsString("errors");
                            if(errors != null && errors.length() > 0)  {
                               message += "<br/>There were errors: \n" + errors;
                            }
               
                            CmMessageBox.showAlert(message);
                            SolutionEditor.__instance._stepEditorViewer.loadSolution(SolutionEditor.__pidToLoad);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            com.google.gwt.user.client.Window.alert("Error: " + caught);
                        }

                    });
                }
            }
        });
    }

    public void showDialog(List<SolutionSearchModel> res) {
        this.resultSet = res;
        
        setHeadingText("Search Replace: " + res.size() + " solution(s)");
        show();
    }

    TextArea _searchFor = new TextArea();
    TextArea _replaceWith = new TextArea();
    private void drawForm() {
        
        FramedPanel framedPanel = new FramedPanel();
        framedPanel.setHeaderVisible(false);
        VerticalPanel vp = new VerticalPanel();
        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        
        HTML infoText = new HTML("<p>This will do an exact match search and replace in current result set.</p>");
        vlc.add(infoText);
        vlc.add(new MyFieldLabel(_searchFor,  "Search For",  100,250));
        vlc.add(new MyFieldLabel(_replaceWith,  "Replace With",  100,250));
     
        vp.add(vlc);
        framedPanel.setWidget(vp);
        setWidget(framedPanel);
    }

}
