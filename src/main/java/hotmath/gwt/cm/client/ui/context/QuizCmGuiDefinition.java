package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmGuiDefinition;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.util.UserInfo;

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
	            "<p>Most of the quiz questions require pencil " +
	            "   and paper. Please don't guess." +
	            "</p>" +
	            "<p>If you log out, your answers will be saved." +
	            "</p> ";
	    }
	    else {
            html = "<h1>HOW TO USE CATCHUP MATH</h1>" +
                   "<p>Take the 10-question quiz to the right.</p>" +
                   "<p>Then, we will provide you with personalized review and practice.</p>" +
                   "<p>Most of the quiz questions require pencil and paper. Please don't guess.</p> ";
	    }
	    cp.add(new Html(html));
	    cp.add(new Html(CatchupMathTools.FEEDBACK_MESSAGE));
		return cp;
	}
	
	QuizPage qp;
	public Widget getCenterWidget() {
		    qp = new QuizPage(new CmAsyncRequest() {
			public void requestComplete(String quizTitle) {
			    
                CmResourcePanelImplDefault resourcePanel = new CmResourcePanelImplDefault() {
                    public Widget getResourcePanel() {
                        return this;
                    }
                    public Integer getOptimalHeight() {
                        return -1;
                    }
                    
                    public Boolean allowClose() {
                        return false;
                    }
                    
                    public ResourceViewerState getInitialMode() {
                        return ResourceViewerState.OPTIMIZED;
                    }
                    
                    public String getContainerStyleName() {
                        return "quiz-cm-gui-definition-resource";
                        
                    }
                    @Override
                    public List<Component> getContainerTools() {
                        List<Component> list = new ArrayList<Component>();
                        if(CmShared.getQueryParameter("debug") != null) {
                            list.add(new Button("Mark Correct", new SelectionListener<ButtonEvent>() {
                                @Override
                                public void componentSelected(ButtonEvent ce) {
                                    QuizPage.markAllCorrectAnswers();
                                }
                            }));
                        }
                        list.add(new Button("Check Quiz", new SelectionListener<ButtonEvent>() {
                            public void componentSelected(ButtonEvent ce) {
                                ContextController.getInstance().doNext();
                            }
                        }));
                            
                        return list;
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
			public void requestFailed(int code, String text) {
			}
		});
		
		return null;
	}

	public CmContext getContext() {
		// TODO Auto-generated method stub
		return new QuizContext(this);
	}
}