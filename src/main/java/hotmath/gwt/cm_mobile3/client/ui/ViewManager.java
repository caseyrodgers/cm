package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobileHistoryListener.TokenParser;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceResultsActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceReviewActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceTutorActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceVideoActivity;
import hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile3.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;

/** manages/loads view internally
 *  (ie, not using history)
 *  
 * @author casey
 *
 */
public class ViewManager {
    
    static private ViewManager __instance;
    static public void setInstance(ClientFactory cf) {
        __instance = new ViewManager(cf);
    }
    
    
    static public ViewManager getInstance() {
        return __instance;
    }
    
    ClientFactory cf;
    EventBus eb;
    public ViewManager(ClientFactory cf) {
        this.cf = cf;
        this.eb = cf.getEventBus();
    }
    
    public void loadView(String name) {
        
        try {
        
        final TokenParser token = new TokenParser(name);
        if(token.getType().equals("lesson")) {
            PrescriptionLessonActivity activity = new PrescriptionLessonActivity(cf, eb);
            PrescriptionLessonView view = cf.getPrescriptionLessonView();
            view.setPresenter(activity);
            eb.fireEvent(new LoadNewPageEvent((IPage) view));
        }
        else if(token.getType().equals("show_work")) {
            String pid = token.getResourceType();
            String title = token.getResourceFile();
            ShowWorkActivity activity = new ShowWorkActivity(eb, pid, title, SharedData.getUserInfo().getRunId());
            ShowWorkView view = cf.getShowWorkView();
            view.setPresenter(activity);
            eb.fireEvent(new LoadNewPageEvent((IPage) view));
        }
        else if(token.getType().equals("resource")) {
                InmhItemData itemData = null;

                /**
                 * token the ordinal position of resource in list. We do not
                 * want to have to create new InmhItemData, use the existing on
                 * in prescription data.
                 */
                String resourceType = token.getResourceType();
                String file = token.getResourceFile();
                if (file == null) {
                    int ordinal = token.getResourceOrdinal();
                    itemData = SharedData.findInmhDataInPrescriptionByOrdinal(resourceType, ordinal);
                } else {
                    itemData = SharedData.findInmhDataInPrescriptionByFile(resourceType, file);
                    if (itemData == null) {
                        // not currently loaded
                        itemData = new InmhItemData(resourceType, file, "");
                    }
                }
                itemData.setTitle(token.getResourceTitle());
                if (resourceType.equals("review")) {
                    PrescriptionLessonResourceReviewActivity activity = new PrescriptionLessonResourceReviewActivity(
                            eb, itemData);
                    PrescriptionLessonResourceReviewView view = cf.getPrescriptionLessonResourceReviewView();
                    view.setPresenter(activity);
                    activity.setupView(view);
                    eb.fireEvent(new LoadNewPageEvent((IPage) view));
                } else if (resourceType.equals("video")) {
                    PrescriptionLessonResourceVideoActivity activity = new PrescriptionLessonResourceVideoActivity(eb,
                            itemData);
                    PrescriptionLessonResourceVideoView view = cf.getPrescriptionLessonResourceVideoView();
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage) view));
                } else if (resourceType.equals("practice")) {
                    PrescriptionLessonResourceTutorActivity activity = new PrescriptionLessonResourceTutorActivity(eb,
                            itemData);
                    PrescriptionLessonResourceTutorView view = cf.getPrescriptionLessonResourceTutorView();
                    view.setTitle("Required " + itemData.getTitle());
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage) view));
                } else if (resourceType.equals("cmextra")) {
                    PrescriptionLessonResourceTutorActivity activity = new PrescriptionLessonResourceTutorActivity(eb,
                            itemData);
                    PrescriptionLessonResourceTutorView view = cf.getPrescriptionLessonResourceTutorView();
                    view.setTitle("Extra Practice " + itemData.getTitle());
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage) view));
                } else if (resourceType.equals("results")) {
                    PrescriptionLessonResourceResultsActivity activity = new PrescriptionLessonResourceResultsActivity(
                            eb, itemData);
                    PrescriptionLessonResourceResultsView view = cf.getPrescriptionLessonResourceResultsView();
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage) view));
                } else {
                    PrescriptionLessonResourceActivity activity = new PrescriptionLessonResourceActivity(eb, itemData);
                    PrescriptionLessonResourceView view = cf.getPrescriptionLessonResourceView();
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage) view));
                }                        
        }

        }
        catch(Exception e) {
            Log.error("Error loading view", e);
            MessageBox.showError(e.getMessage());
        }
    }

}
