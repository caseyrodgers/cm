package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview.ReviewCallback;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


public class AccContentPanelImplLesson extends AccContentPanel {
    
    public AccContentPanelImplLesson(PrescriptionSessionDataResource resource, TopicExplorerCallback callback) {
        super(resource, callback);
    }

    TextButton _back;
    public List<Widget> getContainerTools(CmResourcePanel viewer) {
        List<Widget> tools = super.getContainerTools(viewer);
        _back = new TextButton("Back");
        _back.setEnabled(false);
        _back.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                goBack();
            }
        });
        tools.add(0, _back);
        return tools;
    }
    
    protected void goBack() {
        int whichToDel=_history.size()-1;
        _history.remove(whichToDel);
        
        int whichToLoad = _history.size()-1;
        String file = _history.get(whichToLoad);
        _history.remove(whichToLoad);
        
        _viewer.setResourceItem(new InmhItemData(CmResourceType.REVIEW, file, ""));
        _viewer.getLessonData();
        
        if(_history.size() < 2) {
            _back.setEnabled(false);
            forceLayout();
         }
    }

    List<String> _history = new ArrayList<String>();
    private ResourceViewerImplReview _viewer;
    @Override
    protected void showResource(CmResourcePanel viewer, String title, boolean b) {
        super.showResource(viewer, title, b);
        this._viewer = (ResourceViewerImplReview)viewer;
        this._viewer.setRevieweCallback(new ReviewCallback() {
            @Override
            public void newTopicLoaded(String file, String title) {
                _history.add(file);
                _back.setEnabled(_history.size() > 1);
            }
        });
    }
    
}
