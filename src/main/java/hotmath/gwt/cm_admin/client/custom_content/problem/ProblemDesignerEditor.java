package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.WhiteboardTemplatesResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardTemplatesAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.ResizeEndEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Allows editing of a solution step unit.
 * 
 * Contains a rich text area and optional whiteboard
 * 
 * @author casey
 *
 */
public class ProblemDesignerEditor extends GWindow {

    private String whiteboardId;
    private SolutionInfo solution;
    private BorderLayoutContainer _main;
    private EditorCallback callback;
    private int _countChanges=1;
    protected String _pidEdit;
    private CKEditorPanel _ckEditorPanel;
    private ToggleButton _showWhiteboardToggle;
    private String editorText;
    private AreaData areaData;
    
    public interface EditorCallback {
        void editingComplete(String pidEdit,String textPartPlusWhiteboardJson);
    }

    
    
    public ProblemDesignerEditor(SolutionInfo solution, String editorText, String whiteboardId, EditorCallback callbackIn) {
        super(false);
        this.callback = callbackIn;
        this.solution = solution;
        this.editorText = editorText;
        this.whiteboardId = whiteboardId;
        this.areaData = extractAreaData(editorText);
        setPixelSize(730, 500);
        setResizable(true);
        setMaximizable(true);

        setHeadingText("Edit Problem Definition: " + solution.getPid());
        _main = new BorderLayoutContainer();
        setWidget(_main);

        
        _showWhiteboardToggle = new ToggleButton("Show Whiteboard");
        _showWhiteboardToggle.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showWhiteboardEditor(_showWhiteboardToggle.getValue());
            }
        });
        addTool(_showWhiteboardToggle);
        
        buildUi();

        addButton(new MyTextButton("Save",  new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSave();
            }
        }, "Save any changes"));
        
        addButton(new TextButton("Cancel",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        

        
        addBeforeHideHandler(new BeforeHideHandler() {
            @Override
            public void onBeforeHide(BeforeHideEvent event) {
                if(_ckEditorPanel != null) {
                    _ckEditorPanel.destroyEditor();
                }
            }
        });
        
        
        addMaximizeHandler(new MaximizeHandler() {
            @Override
            public void onMaximize(MaximizeEvent event) {
                resizeWhiteboard();
            }
        });
        
        setVisible(true);
    }

    @Override
    protected void onEndResize(ResizeEndEvent re) {
        super.onEndResize(re);
        resizeWhiteboard();
    }

    private void resizeWhiteboard() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                int height = _wbWrapper.getOffsetHeight();
                _showWorkPanel.resizeWhiteboard(height);
            }
        });

    }
    
    protected void doSave() {
        String wbJson = _showWorkPanel != null?_showWorkPanel.getWhiteboardCommandsAsJson():this.areaData.wbJson;
        
        String wbJsonHtml = wbJson!=null?"<div class='wb_json'>" + wbJson + "</div>":"";
        
        String textPlusWhiteboardPlusWidget = 
                "<div class='step_part'>" + _ckEditorPanel.getEditorValue() + 
                wbJsonHtml +
                "</div>";
        
        textPlusWhiteboardPlusWidget += this.areaData.widgetHtml;
        callback.editingComplete(_pidEdit,textPlusWhiteboardPlusWidget);
        hide();
    }

    static int _cnt;
    private void showWhiteboardEditor(boolean yesNo) {
        String textValue="";
        if(_ckEditorPanel != null) {
            textValue = _ckEditorPanel.getEditorValue();
            _ckEditorPanel.destroyEditor();
            _ckEditorPanel = null;
        }
        else {
            textValue = this.areaData.textPart;
        }
        
        _ckEditorPanel = new CKEditorPanel("ps_editor",120, textValue, new CallbackOnComplete() {
            
            @Override
            public void isComplete() {
                setEditorHeight();
            }
        });
        if(yesNo) {

            if(_showWorkPanel == null) {
                _showWorkPanel = new ShowWorkPanel2(whiteboardCallBack, true, true, "wb_ps-1", 280, getWidget());
            }
            BorderLayoutContainer bCon = new BorderLayoutContainer();
            
            _wbWrapper = new SimplePanel();
            _wbWrapper.setWidget(_showWorkPanel);
            bCon.setCenterWidget(_wbWrapper);
            bCon.setNorthWidget(_ckEditorPanel, new BorderLayoutData(175));
            _main.setCenterWidget(bCon);
        } 
        else
        {
            SimplePanel sp = new SimplePanel();
            sp.setWidget(_ckEditorPanel);
            _main.setCenterWidget(sp);            
        }
        
        _main.forceLayout();

        _showWhiteboardToggle.setValue(yesNo);
        
        // _ckEditorPanel.resizeEditor(200);
    }
    
    SimplePanel _wbWrapper;
    
    final ShowWorkPanel2Callback whiteboardCallBack = new ShowWorkPanelCallbackDefault() {
        @Override
        public void windowResized() {
        }

        @Override
        public void showWorkIsReady(ShowWorkPanel2 showWork) {
            showWork.loadWhiteboardFromJson(areaData.wbJson);
        }

        @Override
        public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
            _countChanges++;
            return null;
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
    private ShowWorkPanel2 _showWorkPanel;
    
    private void buildUi() {
        
        showWhiteboardEditor(true);
        
        addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                setEditorHeight();
            }
        });
    }
    
    
    private void setEditorHeight() {
//        int h = _showWorkPanel.getParent().getElement().getClientHeight();
//        //_showWorkPanel.resizeWhiteboard();
    }

    
    native private String jsni_setProblemStatementHtml(String text) /*-{
        var m = $wnd.$('.cm_problem_text');
        if(m.length == 0) {
            alert('no cm_problem_text element');
            return;
        }
        m.html(text);
    }-*/;

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
    
    
    static public void doTest() {
        SolutionInfo si = new SolutionInfo("custom_44_140314_set1_1_1", "This is the <b>HTML</b>", "", false);
        final ProblemDesignerEditor wb = new ProblemDesignerEditor(si,  si.getHtml(), "wb_id", new EditorCallback() {
            @Override
            public void editingComplete(String pidEdit, String textPlusWhiteboard) {
            }
        });
    }
    
    private AreaData extractAreaData(String text) {
        String checkFor = "<div class='wb_json'>";
        int p = text.indexOf(checkFor);
        String wbJson = null;
        String textPart = null;
        if(p > -1) {
            textPart = text.substring(0, p);
            wbJson = text.substring(p);
            wbJson = wbJson.substring(checkFor.length(), wbJson.indexOf("</div>"));
        }
        else {
            textPart = text;
        }
        
        checkFor="<div id='hm_flash_widget'>";
        p = textPart.indexOf(checkFor);
        String widgetHtml="";
        if(p > -1) {
            widgetHtml = text.substring(p);
            textPart = text.substring(0, p);
            //widgetHtml = wbJson.substring(checkFor.length(), wbJson.indexOf("</div></div>"));
        }
        
        AreaData aData = new AreaData(textPart,wbJson, widgetHtml);
        return aData;
    }

    class AreaData {
        String textPart;
        String wbJson;
        String widgetHtml;
        
        public AreaData(String textPart, String whiteboardJson, String widgetHtml) {
            this.textPart = textPart;
            this.wbJson = whiteboardJson;
            this.widgetHtml = widgetHtml;
        }
        
    }    

}
