package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.rpc.GetCorrelatedTopicsPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class ProblemResourcesButton extends TextButton {
    
    private ProblemDto _problem;
    SelectionHandler<MenuItem> _menuItemlistener;
    
    public ProblemResourcesButton(ProblemDto problem) {
        super("Lesson");
        this._problem = problem;
        
        _menuItemlistener = new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
            	if(event.getSource() instanceof MyMenuItem) {
                    MyMenuItem mi = (MyMenuItem)event.getSource();
                    if(mi.getResource() != null && mi.getResource().getType() == CmResourceType.VIDEO) {
                        new ResourceViewerWindow(new VideoResourceView(mi.getResource(),_problem));
                    }
                    else if(mi.getResource() != null && mi.getResource().getType() == CmResourceType.REVIEW) {
                    	new ResourceViewerWindow(new LessonResourceView(mi.getResource(),_problem));
                    }
                }
            }
        };

        readDataFromServerIfNeeded();
    }

    protected void readDataFromServerIfNeeded() {
        new RetryAction<CmList<PrescriptionSessionResponse>>() {
            @Override
            public void attempt() {
                GetCorrelatedTopicsPrescriptionAction action = new GetCorrelatedTopicsPrescriptionAction(_problem);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<PrescriptionSessionResponse> prescriptions) {
                setupResourceMenus(prescriptions);
            }
        }.register();
    }

    protected void setupResourceMenus(List<PrescriptionSessionResponse> prescriptions) {
        Menu mainMenu = new Menu();
    	if(mainMenu.getWidgetCount() > 0) {
    		return;
    	}
        // for each lesson/topic (one per prescription)
        for(PrescriptionSessionResponse prescription: prescriptions) {

        	MenuItem topicsItem = new MenuItem(prescription.getPrescriptionData().getCurrSession().getTopic());
        	mainMenu.add(topicsItem);

        	Menu topicMenu = new Menu();
        	topicsItem.setSubMenu(topicMenu);
        	
        	PrescriptionData pd = prescription.getPrescriptionData();
        	
        	MyMenuItem lessonTextItem = new MyMenuItem("Text", _menuItemlistener);
        	topicMenu.add(lessonTextItem);

        	MyMenuItem videoItem = new MyMenuItem("Videos", _menuItemlistener);
            videoItem.setEnabled(false);
            topicMenu.add(videoItem);

        	// for each resource in topic
            for(PrescriptionSessionDataResource r: prescription.getPrescriptionData().getCurrSession().getInmhResources()) {
                CmResourceType type = r.getType();
                if(type == CmResourceType.REVIEW) {
                	lessonTextItem.setText("Lesson Text");
                	lessonTextItem.setResource(r);
                	lessonTextItem.setEnabled(true);
                }
                else if(type == CmResourceType.VIDEO){
                    Menu subMenu = new Menu();
                    for(InmhItemData id: r.getItems()) {
                        subMenu.add(new MyMenuItem(id.getTitle(), _menuItemlistener, r));
                    }
                    videoItem.setSubMenu(subMenu);
                    if(r.getItems().size() > 0) {
                    	videoItem.setEnabled(true);
                    }
                }
                else if(type == CmResourceType.PRACTICE) {
//                    Menu subMenu = new Menu();
//                    for(InmhItemData id: r.getItems()) {
//                        subMenu.add(new MenuItem(id.getTitle(),_menuItemlistener));
//                    }
//                    practiceMenu.setSubMenu(subMenu);
//                    practiceMenu.setEnabled(true);
                }
            }
        }
        setMenu(mainMenu);
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