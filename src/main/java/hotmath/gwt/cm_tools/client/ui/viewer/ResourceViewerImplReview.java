package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ResourceViewerImplReview extends CmResourcePanelImplDefault {

    static final String STYLE_NAME = "resource-viewer-impl-review";

    static boolean __isSpanish;
    TextButton _spanishButton;

    boolean itemHasSpanish;

    public ResourceViewerImplReview() {
        addStyleName(STYLE_NAME);
        
        setScrollMode(ScrollMode.AUTO);
    }
    

    @Override
    public ResourceViewerState getInitialMode() {
        return ResourceViewerState.OPTIMIZED;
    }


    @Override
    public String getContainerStyleName() {
        return STYLE_NAME;
    }

    static HTML _mainHtmlPanel = new HTML();
    public Widget getResourcePanel() {
        addResource(_mainHtmlPanel, getResourceItem().getTitle());

        getLessonData();
        
        return this;
    }

    private void getLessonData() {
        
        final InmhItemData resource = getResourceItem();
        final String file = resource.getFile();
        new RetryAction<LessonResult>() {
            @Override   
            public void attempt() {
                GetReviewHtmlAction action = new GetReviewHtmlAction(file);
                if (__isSpanish)
                    action.setSpanish(true);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(LessonResult result) {
                _mainHtmlPanel.setHTML(result.getLesson());
                
                if (result.getWarning() != null) {
                    CatchupMathTools.showAlert("Lesson Information", result.getWarning());
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
                
                
                CmMainPanel.__lastInstance.forceLayout();
            }
        }.register();
    }

    @Override
    public List<Widget> getContainerTools() {
        List<Widget> tools = new ArrayList<Widget>();

        if (_spanishButton == null) {

            _spanishButton = new TextButton("Spanish");
            _spanishButton.addSelectHandler(new SelectHandler() {
                
                @Override
                public void onSelect(SelectEvent event) {
                    __isSpanish = _spanishButton.getText().equals("Spanish");
                    if (__isSpanish) {
                        _spanishButton.setText("English");
                    }
                    else {
                        _spanishButton.setText("Spanish");
                    }
                    getLessonData();
                }
            });
        }
        tools.add(_spanishButton);
        return tools;
    }

    public Integer getOptimalWidth() {
        return 550;
    }
}
