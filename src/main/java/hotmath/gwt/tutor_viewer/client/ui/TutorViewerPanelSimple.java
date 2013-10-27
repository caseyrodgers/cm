package hotmath.gwt.tutor_viewer.client.ui;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.CallbackAfterSolutionLoaded;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
        HorizontalPanel toolbar = new HorizontalPanel();
        toolbar.add(new Button("Print", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CmGwtUtils.printWindow();
            }
        }));
        FlowPanel fp = new FlowPanel();
        ScrollPanel sp = new ScrollPanel();
        sp.add(_tutorWrapper);
        fp.add(toolbar);
        fp.add(sp);
        initWidget(fp);
    }

}
