package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class ProblemResourcesButton extends TextButton {
    
    private ProblemDto _problem;
    EmptyMenu lessonsMenu, videoMenu, practiceMenu;
    
    public ProblemResourcesButton(ProblemDto problem) {
        super("Resources");
        this._problem = problem;
        
        addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                readDataFromServerIfNeeded();
            }
        });
        Menu menu = new Menu();
        
        
        
        SelectionHandler<MenuItem> listener = new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                CmMessageBox.showAlert("Show Resource: " + _problem.getLesson());
            }
        };
        
        lessonsMenu = new EmptyMenu("Lessons");
        videoMenu = new EmptyMenu("Videos");
        practiceMenu = new EmptyMenu("Practice");
        
        menu.add(lessonsMenu);
        menu.add(videoMenu);
        menu.add(practiceMenu);
        
        setMenu(menu);
    }

    protected void readDataFromServerIfNeeded() {
        new RetryAction<PrescriptionSessionResponse>() {
            @Override
            public void attempt() {
                
                GetTopicPrescriptionAction action = new GetTopicPrescriptionAction(_problem.getLesson().getLessonFile());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(PrescriptionSessionResponse prescription) {
                setupResourceMenus(prescription);
            }
        }.register();
    }

    protected void setupResourceMenus(PrescriptionSessionResponse prescription) {
        
        lessonsMenu.setEnabled(false);
        videoMenu.setEnabled(false);
        practiceMenu.setEnabled(false);
        
        for(PrescriptionSessionDataResource r: prescription.getPrescriptionData().getCurrSession().getInmhResources()) {
            String type = r.getType();
            if(type.equals("review")) {
                lessonsMenu.setSubMenu(null);
                lessonsMenu.setText("Lesson Text");
                lessonsMenu.setEnabled(true);
            }
            else if(type.equals("video")){
                Menu subMenu = new Menu();
                for(InmhItemData id: r.getItems()) {
                    subMenu.add(new MenuItem(id.getTitle()));
                }
                videoMenu.setSubMenu(subMenu);
                videoMenu.setEnabled(true);
            }
            else if(type.equals("practice")) {
                Menu subMenu = new Menu();
                for(InmhItemData id: r.getItems()) {
                    subMenu.add(new MenuItem(id.getTitle()));
                }
                practiceMenu.setSubMenu(subMenu);
                practiceMenu.setEnabled(true);
            }
        }
        
    }

}


class EmptyMenu extends MenuItem {
    
    public EmptyMenu(String title) {
        super(title);
        Menu subMenu = new Menu();
        subMenu.add(new MenuItem("-- Empty -- "));
        setSubMenu(subMenu);
        setEnabled(false);
    }
    
}