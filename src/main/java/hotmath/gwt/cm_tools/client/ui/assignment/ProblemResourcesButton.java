package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
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
    MyMenuItem lessonsMenu, videoMenu, practiceMenu;
    
    SelectionHandler<MenuItem> _menuItemlistener;
    
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
        
        _menuItemlistener = new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                if(event.getSource() == lessonsMenu) {
                    new ResourceViewerWindow(new LessonResourceView(lessonsMenu.getResource(),_problem));
                }
                else if(event.getSource() instanceof MyMenuItem) {
                    MyMenuItem mi = (MyMenuItem)event.getSource();
                    if(mi.getResource() != null && mi.getResource().getType().equals("video")) {
                        new ResourceViewerWindow(new VideoResourceView(lessonsMenu.getResource(),_problem));
                    }
                }
            }
        };
        lessonsMenu = new MyMenuItem("Lessons",_menuItemlistener);
        videoMenu = new MyMenuItem("Videos",_menuItemlistener );
        practiceMenu = new MyMenuItem("Practice",_menuItemlistener);
        
        menu.add(lessonsMenu);
        menu.add(videoMenu);
        //menu.add(practiceMenu);
        
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
                lessonsMenu.setText("Lesson Text");
                lessonsMenu.setResource(r);
                lessonsMenu.setEnabled(true);
            }
            else if(type.equals("video")){
                Menu subMenu = new Menu();
                for(InmhItemData id: r.getItems()) {
                    subMenu.add(new MyMenuItem(id.getTitle(), _menuItemlistener, r));
                }
                videoMenu.setSubMenu(subMenu);
                videoMenu.setEnabled(true);
            }
            else if(type.equals("practice")) {
//                Menu subMenu = new Menu();
//                for(InmhItemData id: r.getItems()) {
//                    subMenu.add(new MenuItem(id.getTitle(),_menuItemlistener));
//                }
//                practiceMenu.setSubMenu(subMenu);
//                practiceMenu.setEnabled(true);
            }
        }
        
    }

}


class EmptyMenu extends MenuItem {

    public EmptyMenu(String title,  SelectionHandler<MenuItem> listener) {
        super(title,listener);
        Menu subMenu = new Menu();
        subMenu.add(new MenuItem("-- Empty -- "));
        setSubMenu(subMenu);
        setEnabled(false);
    }
    
}


class MyMenuItem extends MenuItem {

    private PrescriptionSessionDataResource resource;

    public MyMenuItem(String title, SelectionHandler<MenuItem> listener) {
        this(title, listener, null);
    }
    public MyMenuItem(String title, SelectionHandler<MenuItem> listener, PrescriptionSessionDataResource r) {
        super(title, listener);
        this.resource = r;
    }
    
    public void setResource(PrescriptionSessionDataResource r) {
        this.resource = r;
    }
    
    public PrescriptionSessionDataResource getResource() {
        return this.resource;
    }
}