package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.activity.LoginActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonListingActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceResultsActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceReviewActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceTutorActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceVideoActivity;
import hotmath.gwt.cm_mobile3.client.activity.QuizActivity;
import hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity;
import hotmath.gwt.cm_mobile3.client.activity.WelcomeActivity;
import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile3.client.view.LoginView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile3.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

public class CatchupMathMobileHistoryListener implements ValueChangeHandler<String> {
    public void onValueChange(ValueChangeEvent<String> event) {
        
        String historyToken = event.getValue();

        final TokenParser token = new TokenParser(historyToken);
        EventBus eb = CatchupMathMobile3.__clientFactory.getEventBus();
        ClientFactory cf = CatchupMathMobile3.__clientFactory;
        
        try {
            final String type = token.getType();
            
            if(type == null || type.equals("login")) {
                LoginActivity activity = new LoginActivity(eb);
                LoginView view = cf.getLoginView();
                view.setPresenter(activity);
                activity.prepareLogin(view);
                eb.fireEvent(new LoadNewPageEvent((IPage)view));            
            }
            else if (type.equals("welcome") ) {
                WelcomeActivity activity = new WelcomeActivity(eb);
                WelcomeView view = cf.getWelcomeView();
                view.setPresenter(activity);
                activity.prepareView(view);
                eb.fireEvent(new LoadNewPageEvent((IPage)view));            
            }
            else if(type.equals("quiz")) {
                QuizActivity activity = new QuizActivity(eb);
                QuizView view = cf.getQuizView();
                view.setPresenter(activity);
                eb.fireEvent(new LoadNewPageEvent((IPage)view));
            }
            else if(type.equals("show_work")) {
                String pid = token.getResourceType();
                ShowWorkActivity activity = new ShowWorkActivity(eb, pid,SharedData.getUserInfo().getRunId());
                ShowWorkView view = cf.getShowWorkView();
                view.setPresenter(activity);
                eb.fireEvent(new LoadNewPageEvent((IPage)view));
            }
            else if(type.equals("lesson")) {
                PrescriptionLessonActivity activity = new PrescriptionLessonActivity(cf,eb);
                PrescriptionLessonView view = cf.getPrescriptionLessonView();
                view.setPresenter(activity);
                eb.fireEvent(new LoadNewPageEvent((IPage)view));
            }    
            else if(type.equals("listing")) {
                PrescriptionLessonListingActivity activity = new PrescriptionLessonListingActivity(eb);
                PrescriptionLessonListingView view = cf.getPrescriptionLessonListingView();
                view.setPresenter(activity);
                eb.fireEvent(new LoadNewPageEvent((IPage)view));
            }
            else if(type.equals("end_of_program")) {
                eb.fireEvent(new LoadNewPageEvent((IPage)cf.getEndOfProgramView()));
            }
            else if(type.equals("resource")) {
                
                /** token the ordinal position of resource in list.  We do
                 *  not want to have to create new InmhItemData, use the existing
                 *  on in prescription data.
                 */
                int ordinal = token.getResourceOrdinal();
                

                String resourceType = token.getResourceType();
                InmhItemData itemData = SharedData.findInmhDataInPrescriptionByOrdinal(resourceType, ordinal);
                itemData.setTitle(token.getResourceTitle());
                if(resourceType.equals("review")) {
                    PrescriptionLessonResourceReviewActivity activity = new PrescriptionLessonResourceReviewActivity(eb, itemData);
                    PrescriptionLessonResourceReviewView view = cf.getPrescriptionLessonResourceReviewView();
                    view.setPresenter(activity);
                    activity.setupView(view);
                    eb.fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(resourceType.equals("video")) {
                    PrescriptionLessonResourceVideoActivity activity = new PrescriptionLessonResourceVideoActivity(eb, itemData);
                    PrescriptionLessonResourceVideoView view = cf.getPrescriptionLessonResourceVideoView();
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(resourceType.equals("practice")) {
                    PrescriptionLessonResourceTutorActivity activity = new PrescriptionLessonResourceTutorActivity(eb, itemData);
                    PrescriptionLessonResourceTutorView view = cf.getPrescriptionLessonResourceTutorView();
                    view.setTitle("Required Practice Problems");
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(resourceType.equals("cmextra")) {
                    PrescriptionLessonResourceTutorActivity activity = new PrescriptionLessonResourceTutorActivity(eb, itemData);
                    PrescriptionLessonResourceTutorView view = cf.getPrescriptionLessonResourceTutorView();
                    view.setTitle("Extra Practice Problems");
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else if(resourceType.equals("results")) {
                    PrescriptionLessonResourceResultsActivity activity = new PrescriptionLessonResourceResultsActivity(eb, itemData);
                    PrescriptionLessonResourceResultsView view = cf.getPrescriptionLessonResourceResultsView();
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage)view));
                }
                else {
                    PrescriptionLessonResourceActivity activity = new PrescriptionLessonResourceActivity(eb, itemData);
                    PrescriptionLessonResourceView view = cf.getPrescriptionLessonResourceView();
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage)view));
                }
            }
            else {
                Log.error("NOT IMPLEMENTED: " + token.getHistoryTag());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }
    
    
    
    static public class TokenParser {
        String type;
        String resourceType;
        String resourceFile;
        String resourceConfig;
        int resourceOrdinal;
        String resourceTitle;
        
        public TokenParser(String name) {
            if(name != null) {
                String p[] = name.split(":");
                type = p[0];
                
                if(p.length > 1)
                    resourceType = p[1];
                if(p.length > 2) 
                    resourceFile = p[2];
                if(p.length > 3) 
                    resourceConfig = URL.decode(p[3]);
                if(p.length > 4)
                   try {
                       resourceOrdinal = Integer.parseInt(p[4]);
                   }
                    catch(NumberFormatException e){
                        Log.debug("error token parsing", e);
                    }
                if(p.length > 5)
                    resourceTitle = p[5];
                
            }
        }

        public int getResourceOrdinal() {
            return resourceOrdinal;
        }

        public void setResourceOrdinal(int resourceOrdinal) {
            this.resourceOrdinal = resourceOrdinal;
        }

        public void setResourceTitle(String resourceTitle) {
            this.resourceTitle = resourceTitle;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getResourceFile() {
            return resourceFile;
        }

        public void setResourceFile(String resourceFile) {
            this.resourceFile = resourceFile;
        }

        public String getResourceConfig() {
            return resourceConfig;
        }

        public void setResourceConfig(String resourceConfig) {
            this.resourceConfig = resourceConfig;
        }
        
        public String getResourceTitle() {
            return resourceTitle;
        }
        
        public String getHistoryTag() {
            return type + ":" + resourceType + ":" + resourceFile + ":" + resourceConfig; 
        }
    }
}