package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CmTreeModel;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapterAll;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.List;  

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;

import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
  
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
  
public class ProgramDetailsPanel extends CmWindow {  

    TreeStore<ModelData> store;
    TreeLoader<ModelData> loader;
    
    CmAdminModel cmAdminMdl;

	TreePanel<ModelData> tree;
	
    public ProgramDetailsPanel(CmAdminModel cmAdminMdl) {
    	this.cmAdminMdl = cmAdminMdl;
    	
    	buildTree();
    	
        setVisible(true);
    }

    private void buildTree() {

    	LayoutContainer lc = new LayoutContainer();
    	lc.setLayout(new FlowLayout());
    	add(lc);
    	setWidth(355);

    	store = new TreeStore<ModelData>();
        tree = new TreePanel<ModelData>(store);  
    	tree.setWidth(290);  
    	tree.setDisplayProperty("name");
    	tree.setAutoHeight(true);

    	tree.addListener(Events.BeforeExpand, new Listener<TreePanelEvent<ModelData>>() {
			@Override
			public void handleEvent(TreePanelEvent<ModelData> te) {
				CmTreeModel m = (CmTreeModel) te.getItem();

				if (m.loadChildrenAsynchronously()) {
					getLessonItemsRPC(m);
				}
			}
    	});
    	
    	getProgramListingRPC();

    	ButtonBar buttonBar = new ButtonBar();  

    	buttonBar.add(new Button("Expand All", new SelectionListener<ButtonEvent>() {  
    		public void componentSelected(ButtonEvent ce) {  
    			tree.expandAll();  
    		}  
    	}));  
    	buttonBar.add(new Button("Collapse All", new SelectionListener<ButtonEvent>() {  
    		public void componentSelected(ButtonEvent ce) {  
    			tree.collapseAll();  
    		}  
    	}));  

    	ContentPanel cp = new ContentPanel();
    	cp.setScrollMode(Scroll.AUTO);
    	cp.setHeaderVisible(true);
    	cp.setLayout(new FitLayout());  
    	cp.add(tree);  
    	cp.setSize(340, 385);

    	lc.add(buttonBar, new FlowData(10));
    	lc.add(cp);

    	setModal(true);
    	setHeading("Catchup Math Programs");    	
    	setHeight(500);
    	setResizable(false);

    }

    private void getLessonItemsRPC(final CmTreeModel section) {
    	new RetryAction <CmList<ProgramLesson>> () {

			@Override
			public void attempt() {
				GetProgramLessonsAction action = new GetProgramLessonsAction(section.getId(), section.getNumber());
				setAction(action);
		        CmShared.getCmService().execute(action, this);				
			}

			@Override
			public void oncapture(CmList<ProgramLesson> lessons) {
				section.remove(0);
				for (ProgramLesson p : lessons) {
					CmTreeModel m = new CmTreeModel();
					m.setName(p.getName());
					section.add(m);
				}
				// tree.fireEvent(Events.Refresh); // TODO: doesn't work...
			}
    		
    	}.register();
    	
    }

    private void getProgramListingRPC() {

		new RetryAction<ProgramListing>() {
		    @Override
		    public void attempt() {
		        GetProgramListingAction action = new GetProgramListingAction(cmAdminMdl.getId());
		        setAction(action);
		        CmShared.getCmService().execute(action, this);
		    }
            public void oncapture(ProgramListing pl) {
                
            	List <ProgramType> progTypeList = pl.getProgramTypes();
            	CmTreeModel ctMdl = new CmTreeModel();

            	for (ProgramType pt : progTypeList) {
            		CmTreeModel ctm0 = new CmTreeModel(pt.getType());
            		ctMdl.add(ctm0);

            		for (ProgramSubject ps : pt.getProgramSubjects()) {

            			CmTreeModel ctm1 = new CmTreeModel(ps.getName());
            			ctm1.setId(ps.getTestDefId());
            			ctm0.add(ctm1);

            			for (ProgramChapter pc : ps.getChapters()) {

            				if (pc instanceof ProgramChapterAll) {
            					for (ProgramSection s : pc.getSections()) {
            						
            						CmTreeModel ctm2 = new CmTreeModel(s.getName());
            						ctm2.setId(ctm1.getId());
            						ctm2.setNumber(s.getNumber());
            						ctm1.add(ctm2);
            						
            						// add place holder Lesson Item to Section (will be loaded asynchronously)
            						CmTreeModel ctm3 = new CmTreeModel("Lessons");
            						ctm2.add(ctm3);
            						ctm2.setLoadChildrenAsynchronously(true);
            					}
            				}
            				else {
            					CmTreeModel ctm2 = new CmTreeModel(String.valueOf(pc.getNumber()) + " " + pc.getName());
            					ctm1.add(ctm2);

            					for (ProgramSection s : pc.getSections()) {
            						CmTreeModel ctm3 = new CmTreeModel(s.getName());
            						ctm3.setId(ctm1.getId());
            						ctm3.setNumber(s.getNumber());
            						ctm2.add(ctm3);
            						
            						// add place holder Lesson Item to Section (will be loaded asynchronously)
            						CmTreeModel ctm4 = new CmTreeModel("Lessons");
            						ctm3.add(ctm4);            						
            						ctm3.setLoadChildrenAsynchronously(true);
            					}
            				}
            			}
            		}
            	}
            	
                store.add(ctMdl.getChildren(), true);
                tree.enable();
            }
        }.register();
	}

}  