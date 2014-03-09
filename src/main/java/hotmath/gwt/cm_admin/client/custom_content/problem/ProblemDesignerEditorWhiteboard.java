package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WhiteboardModel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.WhiteboardTemplatesResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardTemplatesAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction.SaveType;
import hotmath.gwt.cm_rpc.client.rpc.SaveStaticWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SetupWhiteboardForEditingAction;
import hotmath.gwt.cm_rpc.client.rpc.SetupWhiteboardForEditingAction.SetupType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyTextButton;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.cm_tools.client.util.WhiteboardTemplatesManager;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Cookies;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class ProblemDesignerEditorWhiteboard extends GWindow {

    private String whiteboardId;
    private SolutionInfo solution;
    private BorderLayoutContainer _main;
    private CallbackOnComplete callback;
    private int _countChanges=1;
    protected String _pidEdit;
    private CKEditorPanel _ckEditorPanel;

    public ProblemDesignerEditorWhiteboard(SolutionInfo solution, String whiteboardId, CallbackOnComplete callbackIn) {
        super(false);
        this.callback = callbackIn;
        this.solution = solution;
        this.whiteboardId = whiteboardId;
        setPixelSize(800, 650);
        setResizable(false);

        setHeadingText("Edit Problem Definition: " + solution.getPid());
        _main = new BorderLayoutContainer();
        setWidget(_main);

        buildUi();

        addBeforeHideHandler(new BeforeHideHandler() {
            @Override
            public void onBeforeHide(BeforeHideEvent event) {
                /** 
                 * always save for now
                 * TODO: add tracking of editor and whiteboard commands for dirty.
                 */
                
                if (_countChanges > 0) { 
                    callback.isComplete();
                }
            }
        });
        
        addButton(new MyTextButton("Save",  new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSave();
            }
        }, "Save any changes to whiteboard"));
        
        addButton(new TextButton("Cancel",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        setVisible(true);
    }

    protected void doSave() {
        
        CmBusyManager.setBusy(true);
        
        final String editorText = _ckEditorPanel.getEditorValue();
        
        new RetryAction<CmList<Response>>() {
            @Override
            public void attempt() {
                
                MultiActionRequestAction mAction = new MultiActionRequestAction(); 
                mAction.getActions().add(new SaveCustomProblemAction(solution.getPid(), SaveType.PROBLEM_STATEMENT_TEXT, editorText));                
                mAction.getActions().add(new SetupWhiteboardForEditingAction(SetupType.SAVE, solution.getPid(), _pidEdit));
                setAction(mAction);
                CmShared.getCmService().execute(mAction, this);
            }

            @Override
            public void oncapture( CmList<Response> result) {
                CmBusyManager.setBusy(false);
                Log.info("Whiteboard changes commited: " + result);
                hide();
            }

        }.register();   
        
    }

    private void buildUi() {
        final ShowWorkPanel2Callback callBack = new ShowWorkPanelCallbackDefault() {
            @Override
            public void windowResized() {
            }

            @Override
            public void showWorkIsReady(ShowWorkPanel2 showWork) {
                //WhiteboardModel whiteBoard = solution.getWhiteboards().size() > 0 ? solution.getWhiteboards().get(0) : new WhiteboardModel();
                //showWork.loadWhiteboard(whiteBoard.getCommands());

                setupWhiteboardForEditing(showWork, solution.getPid());
            }

            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                _countChanges++;
                return new SaveStaticWhiteboardDataAction(_pidEdit, UserInfoBase.getInstance().getUid(), solution.getPid(), commandType, data);
            }
            
            
             @Override
            public void saveWhiteboardAsTemplate(final ShowWorkPanel2 showWorkPanel2) {
                 String tmplName = Cookies.getCookie("wb_template");
                 final PromptMessageBox mb = new PromptMessageBox("Save As Template", "Template Name");
                 
                 
                 CmMessageBox.confirm("Save",  "Save as template?",  new ConfirmCallback() {
                    @Override
                    public void confirmed(boolean yesNo) {
                        showWorkPanel2.saveAsTemplate(UserInfoBase.getInstance().getUid(),  new CallbackOnComplete() {
                            @Override
                            public void isComplete() {
                                // silent
                            }
                        });
                    }
                });
                 
             }
             
             
             @Override
            public void manageTemplates(ShowWorkPanel2 showWorkPanel2) {
                 new WhiteboardTemplatesManager(showWorkPanel2);
            }
             
        };
        
        BorderLayoutContainer bCon = new BorderLayoutContainer();

        _ckEditorPanel = new CKEditorPanel(jsni_getProblemStatementHtml());
        bCon.setCenterWidget(new ShowWorkPanel2(callBack, true, true, "wb_ps-1", 300, getWidget()));
        bCon.setNorthWidget(_ckEditorPanel, new BorderLayoutData(300));
        
        
        _main.setCenterWidget(bCon);
    }
    
    
    native private String jsni_getProblemStatementHtml() /*-{
        var m = $wnd.$('.cm_problem_text');
        if(m.length == 0) {
            return '';
        }
        return m.html();
    }-*/;
    
    
    native private String jsni_setProblemStatementHtml(String text) /*-{
        var m = $wnd.$('.cm_problem_text');
        if(m.length == 0) {
            alert('no cm_problem_text element');
            return;
        }
        m.html(text);
    }-*/;
    
    protected void setupWhiteboardForEditing(final ShowWorkPanel2 showWork, final String pid) {
        
        CmBusyManager.setBusy(true);
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SetupWhiteboardForEditingAction action = new SetupWhiteboardForEditingAction(SetupType.CREATE, pid);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData result) {
                CmBusyManager.setBusy(false);
                _pidEdit = result.getDataAsString("pid_edit");
                WhiteboardModel whiteBoard = solution.getWhiteboards().size() > 0 ? solution.getWhiteboards().get(0) : new WhiteboardModel();
                showWork.loadWhiteboard(whiteBoard.getCommands());
            }

        }.register();
    }

    protected void loadWhiteboardTemplates(final ShowWorkPanel2 showWork) {
        new RetryAction<WhiteboardTemplatesResponse>() {
            @Override
            public void attempt() {
                GetWhiteboardTemplatesAction action = new GetWhiteboardTemplatesAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(WhiteboardTemplatesResponse templates) {
                showWork.setWhiteboardTemplates(templates.getJsonRepresentation());
                /** jsni_setWhiteboardTemplatesAux */
            }

        }.register();
    }

}
