package hotmath.gwt.tutor_viewer.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.CallbackAfterSolutionLoaded;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TutorViewerPanelSimple extends Composite {
    
    TutorWrapperPanel _tutorWrapper;
    String pid;
    public TutorViewerPanelSimple(String pid) {
        this.pid = pid;
        _tutorWrapper = new TutorWrapperPanel(true, false, false, false, new TutorCallbackDefault());
        _tutorWrapper.loadSolution(pid,  pid, false, false,null, new CallbackAfterSolutionLoaded() {
            @Override
            public void solutionLoaded(SolutionInfo solutionInfo) {
                Log.debug("Solution was loaded: " + solutionInfo.getPid());
            }
        });
        ScrollPanel sp = new ScrollPanel();
        sp.add(_tutorWrapper);
        
        initWidget(sp);
        
        
        
    }

}
