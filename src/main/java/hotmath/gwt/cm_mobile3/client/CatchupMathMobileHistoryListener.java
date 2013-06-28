package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.activity.LoginActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonListingActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceResultsActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceReviewActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceTutorActivity;
import hotmath.gwt.cm_mobile3.client.activity.QuizActivity;
import hotmath.gwt.cm_mobile3.client.activity.SearchActivity;
import hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity;
import hotmath.gwt.cm_mobile3.client.activity.WelcomeActivity;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile3.client.event.LoadActiveProgramFlowEvent;
import hotmath.gwt.cm_mobile3.client.view.LoginView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.activity.AssignmentActivity;
import hotmath.gwt.cm_mobile_shared.client.activity.AssignmentListActivity;
import hotmath.gwt.cm_mobile_shared.client.activity.AssignmentProblemActivity;
import hotmath.gwt.cm_mobile_shared.client.activity.AssignmentShowworkActivity;
import hotmath.gwt.cm_mobile_shared.client.activity.PrescriptionLessonResourceVideoActivity;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData.CallbackWhenDataReady;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentListView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentView;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkViewImpl;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

public class CatchupMathMobileHistoryListener implements ValueChangeHandler<String> {

    
    public void onValueChange(final ValueChangeEvent<String> event) {

        String historyToken = event.getValue();

        final TokenParser token = new TokenParser(historyToken);
        final EventBus eb = CatchupMathMobile3.__clientFactory.getEventBus();
        final ClientFactory cf = CatchupMathMobile3.__clientFactory;
        try {
            // always perform any actions here
            ShowWorkActivity.saveWhiteboard();

            String type = token.getType();

            
            if (type.equals("login")) {
                LoginActivity activity = new LoginActivity(eb);
                LoginView view = cf.getLoginView();
                view.setPresenter(activity);
                activity.prepareLogin(view);
                eb.fireEvent(new LoadNewPageEvent((IPage) view));
            }
            else {
                
                SharedData.makeSureUserHasBeenRead(new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        try {
                            onValueChangeRequiresUserLoginData(cf, eb, token, event);
                        }
                        catch(Exception e) {
                            Log.error("Error processing history", e);
                            PopupMessageBox.showError("Request could not be completed due to error: " + e);
                            CmRpcCore.EVENT_BUS.fireEvent(new LoadActiveProgramFlowEvent());
                        }
                    }
                });
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }
    
    private void onValueChangeRequiresUserLoginData(final ClientFactory cf, final EventBus eb, final TokenParser token, final ValueChangeEvent<String> event ) throws Exception{
        
        String type = token.getType();
        
        if(type == null || type.length() == 0) {
          /** if no location info passed in params, then go to default
           *  place as determined by user's current program state
           */
          SharedData.makeSureUserHasBeenRead(new CallbackOnComplete() {
              @Override
              public void isComplete() {
                  
                  /** default place.
                   * 
                   * If assignments only, then go only to assignments.  Otherwise
                   * run user through their program.
                   * 
                   */
                  
                  if(SharedData.getMobileUser().getFlowAction().getPlace() == CmPlace.ASSIGNMENTS_ONLY) {
                      // only show the assignments
                      History.newItem("assignment_list:" + System.currentTimeMillis());
                  }
                  else {
                      // show the current program
                      CmRpcCore.EVENT_BUS.fireEvent(new HandleNextFlowEvent(SharedData.getMobileUser().getFlowAction()));
                  }
              }
          });
          return;
        }
          
        
        // requires login
        if (type.equals("welcome")) {
            WelcomeActivity activity = new WelcomeActivity(cf,eb);
            WelcomeView view = cf.getWelcomeView();
            view.setPresenter(activity);
            activity.prepareView(view);
            eb.fireEvent(new LoadNewPageEvent((IPage) view));
        } else if (type.equals("quiz")) {
            QuizActivity activity = new QuizActivity(eb);
            QuizView view = cf.getQuizView();
            view.setPresenter(activity);
            eb.fireEvent(new LoadNewPageEvent((IPage) view));
        } else if (type.equals("show_work")) {
            String pid = token.getResourceType();
            String title = token.getResourceFile();
            ShowWorkActivity activity = new ShowWorkActivity(eb, pid, title, SharedData.getUserInfo().getRunId());
            ShowWorkView view = cf.getShowWorkView();
            eb.fireEvent(new LoadNewPageEvent((IPage) view));
            view.setPresenter(activity);
        } else if (type.equals("lesson")) {
            PrescriptionLessonActivity activity = new PrescriptionLessonActivity(cf, eb);
            PrescriptionLessonView view = cf.getPrescriptionLessonView();
            view.setPresenter(activity);
            eb.fireEvent(new LoadNewPageEvent((IPage) view));
        } else if (type.equals("listing")) {
            PrescriptionLessonListingActivity activity = new PrescriptionLessonListingActivity(eb);
            PrescriptionLessonListingView view = cf.getPrescriptionLessonListingView();
            view.setPresenter(activity);
            eb.fireEvent(new LoadNewPageEvent((IPage) view));
        } else if (type.equals("end_of_program")) {
            eb.fireEvent(new LoadNewPageEvent((IPage) cf.getEndOfProgramView()));
        } else if (type.equals("search")) {
            SearchActivity search = new SearchActivity(cf,eb);
            cf.getSearchView().setPresenter(search);
            eb.fireEvent(new LoadNewPageEvent((IPage)cf.getSearchView() ));
        } 
        /** is a prescription resource */
        else if (type.equals("resource")) {
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

                if (Controller.peekPage() instanceof ShowWorkViewImpl) {
                    /**
                     * as shortcut, just show current tutor view
                     * 
                     */
                    Controller.navigateBack();
                } else {
                    PrescriptionLessonResourceTutorView view = cf.getPrescriptionLessonResourceTutorView();
                    view.setHeaderTitle("Required " + itemData.getTitle());
                    view.setPresenter(activity);
                    eb.fireEvent(new LoadNewPageEvent((IPage) view));
                }
            } else if (resourceType.equals("cmextra")) {
                PrescriptionLessonResourceTutorActivity activity = new PrescriptionLessonResourceTutorActivity(eb,
                        itemData);
                PrescriptionLessonResourceTutorView view = cf.getPrescriptionLessonResourceTutorView();
                view.setHeaderTitle("Extra Practice " + itemData.getTitle());
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
            
        } else {
            
            /** Assume we now need AssignmentData
             * 
             */
            AssignmentData.readAssData(new CallbackWhenDataReady() {
                @Override
                public void isReady() {
                    onValueChangeRequiresAssignmentData(cf, eb, token, event);
                }
            });
        }
        
    }
    
    

    /** Requires UserData and AssignmentData
     * 
     * @param cf
     * @param eb
     * @param token
     * @param event
     */
    protected void onValueChangeRequiresAssignmentData(ClientFactory cf, final EventBus eb, TokenParser token, ValueChangeEvent<String> event) {
            String type = token.getType();
            if(type.equals("assignment_list")) {
                AssignmentListActivity activity = new AssignmentListActivity();
                AssignmentListView view = cf.getAssignmentListView();
                view.setPresenter(activity);
                eb.fireEvent(new LoadNewPageEvent(view));
            }
            else if(type.equals("assignment")) {
                int assignKey = Integer.parseInt(token.getTokenPart(1));
                final AssignmentView view = cf.getAssignmentView();
                AssignmentActivity activity = new AssignmentActivity(assignKey);
                view.setPresenter(activity, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        eb.fireEvent(new LoadNewPageEvent(view));
                    }
                });
            }
            else if(type.equals("assignment_problem")) {
                int assignKey = Integer.parseInt(token.getTokenPart(1));
                String pid = token.getTokenPart(2);
                boolean showWork = token.getTokenPart(3).equals("sw"); // show work

                AssignmentProblemActivity activity = new AssignmentProblemActivity(assignKey, pid);
                final AssignmentProblemView view = cf.getAssignmentProblemView();
                view.setPresenter(activity, showWork, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        eb.fireEvent(new LoadNewPageEvent(view));
                    }
                });
            }
            else if(type.equals("assignment_showwork")) {
                int assignKey = Integer.parseInt(token.getTokenPart(1));
                String pid = token.getTokenPart(2);
                AssignmentShowworkActivity activity = new AssignmentShowworkActivity(assignKey, pid);
                AssignmentShowWorkView view = cf.getAssignmentShowworkView();
                view.setPresenter(activity);
                eb.fireEvent(new LoadNewPageEvent(view));
            }
    }

    
    static final String DEFAULT_TYPE = "";


    static public class TokenParser {
        String type;
        String resourceType;
        String resourceFile;
        String resourceConfig;
        int resourceOrdinal;
        String resourceTitle;
        String _parts[];

        public TokenParser(String name) {
            if (name != null && name.length() > 0) {
                _parts = name.split(":");
                type = _parts[0];

                if (_parts.length > 1)
                    resourceType = _parts[1];
                if (_parts.length > 2)
                    resourceFile = _parts[2];
                if (_parts.length > 3)
                    resourceConfig = URL.decode(_parts[3]);
                if (_parts.length > 4)
                    try {
                        resourceOrdinal = Integer.parseInt(_parts[4]);
                    } catch (NumberFormatException e) {
                        Log.debug("error token parsing", e);
                    }
                if (_parts.length > 5)
                    resourceTitle = _parts[5];

            }
            
            else {
                type = DEFAULT_TYPE;
            }
        }
        
        public String getTokenPart(int part) {
            return _parts[part];
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