package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmGuiDefinition;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard;
import hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.Widget;


/**
 * add the quiz resource as the default
 * 
 * @TODO: which quiz?
 */
public class QuizCmGuiDefinition implements CmGuiDefinition {
	int testSegment;
	
	public QuizCmGuiDefinition(int segmentNumber) {
	    this.testSegment = segmentNumber;
	}
	
	public String getTitle() {
		return "Quiz Page";
	}

	
	public Widget getWestWidget() {
	    
	    LayoutContainer cp = new LayoutContainer();
	    cp.setStyleName("quiz-page-resource");
	    String html="";
	    if(UserInfo.getInstance().getTestName().indexOf("Auto-Enrollment") > -1) {
	        html = 
	            "<p>Take the quiz to the right.</p>" +
	            "<p>Afterwards, you may get one or two more" +
	            "   quizzes before starting your review and practice. " +
	            "</p>" +
	            "<p>Work out your answers carefully on our whiteboard or on paper.</p>" +
	            "<p>If you log out, your answers will be saved." +
	            "</p> ";
	    }
	    else {
            html = "<h1>HOW TO USE CATCHUP MATH</h1>" +
                   "<p>Take the 10-question quiz to the right.</p>" +
                   "<p>Then, we will provide you with personalized review and practice.</p>" +
   	               "<p>Work out your answers carefully on our whiteboard or on paper.</p>";
	    }
	    cp.add(new Html(html));
	    cp.add(new Html(CatchupMathTools.FEEDBACK_MESSAGE));

	    int currentQuiz = UserInfo.getInstance().getTestSegment();
	    
	    /** this seems like a bug .. maybe switching
	     *  between 1 and 0 base.
	     */
	    if(currentQuiz == 0)
	        currentQuiz = 1;

	    return cp;
	}
	
	QuizPage qp;
	final int FORCE_USE_ACTIVE_SEGMENT = -1;
	public Widget getCenterWidget() {
	        int testSegmentToRead = FORCE_USE_ACTIVE_SEGMENT; 
		    qp = new QuizPage(false, testSegmentToRead, new CmAsyncRequestImplDefault() {
		        
			public void requestComplete(String quizTitle) {
			    
			    /** Create resource container to display 
			     * the tutor/white-board combination 
			     * 
			     */
                CmResourcePanelImplDefault resourcePanel = new CmResourcePanelImplWithWhiteboard() {
                    @Override
                    protected DisplayMode getInitialWhiteboardDisplay() {
                        return DisplayMode.TUTOR;
                    }
                    public Widget getResourcePanel() {
                        return this;
                    }
                    
                    public Integer getOptimalHeight() {
                        return -1;
                    }
                    
                    public Boolean allowClose() {
                        return false;
                    }

                    @Override
                    public List<Component> getContainerTools() {
                    	ArrayList<Component> list2 = new ArrayList<Component>();
                        if(CmShared.getQueryParameter("debug") != null) {
                            list2.add(new Button("Mark Correct", new SelectionListener<ButtonEvent>() {
                                @Override
                                public void componentSelected(ButtonEvent ce) {
                                    qp.markAllAnswersCorrect();
                                }
                            }));
                        }
                        list2.add(new Button("Check Quiz", new SelectionListener<ButtonEvent>() {
                            public void componentSelected(ButtonEvent ce) {
                                ContextController.getInstance().doNext();
                            }
                        }));
                    	List<Component> list = super.getContainerTools();
                    	list2.addAll(list);
                        return list2;
                    }
					@Override
					public Widget getTutorDisplay() {
						return qp;
					}
					@Override
					public void setupShowWorkPanel(ShowWorkPanel whiteboardPanel) {
						whiteboardPanel.setPid("quiz:" + CmMainPanel.getLastQuestionPid());
					}
                };
                resourcePanel.addResource(qp, "Quiz");

                QuizContext qc = (QuizContext)getContext();
                qc.setTitle(quizTitle);

                CmMainPanel.__lastInstance._mainContent.showResource(resourcePanel, quizTitle);

                ContextController.getInstance().setCurrentContext(qc);
			
				if(UserInfo.getInstance().isAutoTestMode()) {
				    qc.doCheckTest();
				}
			}
		});
		
		return null;
	}

	public CmContext getContext() {
		// TODO Auto-generated method stub
		return new QuizContext(this);
	}
}