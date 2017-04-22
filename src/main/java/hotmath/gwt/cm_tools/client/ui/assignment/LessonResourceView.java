package hotmath.gwt.cm_tools.client.ui.assignment;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.rpc.RetryAction;


public class LessonResourceView extends ContentPanel implements ResourceView  {

    private PrescriptionSessionDataResource resource;
    private ProblemDto problem;
    ToggleButton _spanishButton;
    
    public LessonResourceView(PrescriptionSessionDataResource resource, ProblemDto problem) {
        
        this.resource = resource;
        this.problem = problem;
        
        addStyleName("lesson-resource-view");
        
        _spanishButton = new ToggleButton("Spanish/English");
        _spanishButton.addSelectHandler( new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(__isSpanish) {
                    __isSpanish=false;
                }
                else {
                    __isSpanish=true;
                }
                getData();
            }
        });
        addTool(_spanishButton);
        setWidget(new DefaultGxtLoadingPanel());
        
        getData();
    }

    @Override
    public String getResourceTitle() {
        return this.resource.getItems().get(0).getTitle();
    }
    
    @Override
    public Widget asWidget() {
        return this;
    }

    
    static boolean __isSpanish;
    private void getData() {
        new RetryAction<LessonResult>() {
            @Override   
            public void attempt() {
                GetReviewHtmlAction action = new GetReviewHtmlAction(resource.getItems().get(0).getFile());
                if (__isSpanish)
                    action.setSpanish(true);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            public void oncapture(LessonResult result) {
                
                FlowLayoutContainer flowContainer = new FlowLayoutContainer();
                flowContainer.add(new HTML(result.getLesson()));
                flowContainer.setScrollMode(ScrollMode.AUTO);
                
                if (result.getWarning() != null) {
                    CmMessageBox.showAlert("Lesson Information", result.getWarning());
                }

                /**
                 * if in English mode, and lesson does not have a Spanish
                 * version disable button
                 */
                if(_spanishButton != null) {
                    if (!__isSpanish && !result.isHasSpanish()) {
                        _spanishButton.setEnabled(false);
                    }
                    else {
                        _spanishButton.setEnabled(true);
                    }
                }
                setWidget(flowContainer);
                forceLayout();
                
                CmRpc.jsni_processMathJax();
            }
        }.register();
    }        
}

