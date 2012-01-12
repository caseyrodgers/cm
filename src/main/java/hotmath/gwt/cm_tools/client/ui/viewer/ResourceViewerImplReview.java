package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplReview extends CmResourcePanelImplDefault {
    
    
    
    static final String STYLE_NAME = "resource-viewer-impl-review";
    
    static boolean __isSpanish;
    static Button __spanishButton = new Button("Spanish");

    boolean itemHasSpanish;
    
    public ResourceViewerImplReview() {
        addStyleName(STYLE_NAME);
    }
    
    @Override
    public String getContainerStyleName() {
        return STYLE_NAME;
    }
    
    
    Html _mainHtmlPanel = new Html();
    public Widget getResourcePanel() {
        setScrollMode(Scroll.AUTOY);
        addResource(_mainHtmlPanel,getResourceItem().getTitle());
        getLessonData();
        return this;
    }
    
    private void getLessonData() {
        final InmhItemData resource=getResourceItem();
        final String file = resource.getFile();
        new RetryAction<LessonResult>() {
            @Override
            public void attempt() {
                GetReviewHtmlAction action = new GetReviewHtmlAction(file);
                if(__isSpanish)
                    action.setSpanish(true);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            public void oncapture(LessonResult result) {
                _mainHtmlPanel.setHtml(result.getLesson());
                if(result.getWarning() != null) {
                    CatchupMathTools.showAlert("Lesson Information", result.getWarning());
                }

                /** if in English mode, and lesson does not have a Spanish version 
                 *  disable button
                 */
                if(!__isSpanish && !result.isHasSpanish())
                    __spanishButton.setEnabled(false);
                else 
                    __spanishButton.setEnabled(true);
            }
        }.register();
    }
    
    @Override
    public List<Component> getContainerTools() {
        List<Component> tools = new ArrayList<Component>();
        __spanishButton.removeAllListeners();
        __spanishButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                __isSpanish = __spanishButton.getText().equals("Spanish"); 
                if(__isSpanish)
                    __spanishButton.setText("English");
                else
                    __spanishButton.setText("Spanish");
                getLessonData();
            }
        });
        tools.add(__spanishButton);
        return tools;
    }
    
    public Integer getOptimalWidth() {
        return 550;
    }
    
    @Override
    public ResourceViewerState getInitialMode() {
        return ResourceViewerState.OPTIMIZED;
    }
}
