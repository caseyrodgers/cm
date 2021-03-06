package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_core.client.event.LoadReviewHtmlEvent;
import hotmath.gwt.cm_core.client.event.LoadReviewHtmlHandler;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
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
    
    static int __uniqResourceViewerKey;

    private int _uniqueInstanceKey;

    private ReviewCallback reviewCallback;

    static {
        setupJsniHooks();
    }

    static public interface ReviewCallback {
        void newTopicLoaded(String file, String title);
    }
    
    public ResourceViewerImplReview(ReviewCallback callback) {
        this();
        this.reviewCallback = callback;
    }
    
    public void setRevieweCallback(ReviewCallback callback) {
        this.reviewCallback = callback;
    }
    
    public ResourceViewerImplReview() {
        addStyleName(STYLE_NAME);
        setScrollMode(ScrollMode.AUTO);
        
        
        this._uniqueInstanceKey=__uniqResourceViewerKey++;
        
        CmRpcCore.EVENT_BUS.addHandler(LoadReviewHtmlEvent.TYPE, new LoadReviewHtmlHandler() {
            @Override
            public void loadLesson(String file, int uniqueInstanceKey) {
                if(uniqueInstanceKey == ResourceViewerImplReview.this._uniqueInstanceKey) {
                    System.out.println("Load Lesson: " + uniqueInstanceKey);
                    getResourceItem().setFile(file);
                    getLessonData();
                }
            }
        });
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
    
    
    static ResourceViewerImplReview __lastReviewViewer=null; 
    static public void doResourceLoad(int uniqueInstanceKey, String key, String file) {
        CmRpcCore.EVENT_BUS.fireEvent(new LoadReviewHtmlEvent(file,uniqueInstanceKey));
    }

    static private native void setupJsniHooks() /*-{
        $wnd.doLoadResource_Gwt = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview::doResourceLoad(I Ljava/lang/String;Ljava/lang/String;);
    }-*/;

    public int getUniqueInstanceKey() {
        return this._uniqueInstanceKey;
    }
    @Override
    public ResourceViewerState getInitialMode() {
        return ResourceViewerState.OPTIMIZED;
    }

    @Override
    public String getContainerStyleName() {
        return STYLE_NAME;
    }

    HTML _mainHtmlPanel = new HTML();
    public Widget getResourcePanel() {
        addResource(_mainHtmlPanel, getResourceItem().getTitle());

        getLessonData();
        
        return this;
    }

    public void getLessonData() {
        final InmhItemData resource = getResourceItem();
        final String file = resource.getFile();
        new RetryAction<LessonResult>() {
            @Override   
            public void attempt() {
                GetReviewHtmlAction action = new GetReviewHtmlAction(file,_uniqueInstanceKey);
                if (__isSpanish)
                    action.setSpanish(true);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
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
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_FORCE_GUI_REFRESH));
                // CmMainPanel.__activeInstance.forceLayout();
                
                if(reviewCallback != null) {
                    reviewCallback.newTopicLoaded(result.getItem().getFile(), result.getItem().getTitle());
                }
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


    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                GWindow w = new GWindow(true);
                
                ResourceViewerImplReview review = new ResourceViewerImplReview();
                InmhItemData item = new InmhItemData(CmResourceType.REVIEW, "topics/complementary-angles.html",  "Test");
                review.setResourceItem(item);
                w.setWidget(review.getResourcePanel());
                w.setVisible(true);
            }
        });
    }
}
