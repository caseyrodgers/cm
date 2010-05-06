package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CmTreeModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramExt;
import hotmath.gwt.cm_tools.client.model.SubjectModel;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapterAll;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;

import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.List;  

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;  
import com.extjs.gxt.ui.client.util.IconHelper;  
import com.extjs.gxt.ui.client.widget.ContentPanel;  
import com.extjs.gxt.ui.client.widget.LayoutContainer;  
import com.extjs.gxt.ui.client.widget.button.ToolButton;  
import com.extjs.gxt.ui.client.widget.layout.FitLayout;  
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;  
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;  
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;  
import com.google.gwt.user.client.Element;  
import com.google.gwt.user.client.rpc.AsyncCallback;  
import com.google.gwt.user.client.ui.AbstractImagePrototype;  
  
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;  
import com.extjs.gxt.ui.client.event.ButtonEvent;  
import com.extjs.gxt.ui.client.event.SelectionListener;  
import com.extjs.gxt.ui.client.store.TreeStore;  
import com.extjs.gxt.ui.client.widget.LayoutContainer;  
import com.extjs.gxt.ui.client.widget.button.Button;  
import com.extjs.gxt.ui.client.widget.button.ButtonBar;  
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.FlowData;  
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;  
import com.google.gwt.user.client.Element;  
  
public class ProgramDetailsPanel extends CmWindow {  

    TreeStore<ModelData> store;
    
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
            		//StudyProgramExt spe = new StudyProgramExt(pt.getType(), " ", " ", 0, 0, 0, 0, " ");
            		CmTreeModel ctm0 = new CmTreeModel(pt.getType());
            		ctMdl.add(ctm0);

            		for (ProgramSubject ps : pt.getProgramSubjects()) {

            			CmTreeModel ctm1 = new CmTreeModel(ps.getName());
            			ctm0.add(ctm1);

            			for (ProgramChapter pc : ps.getChapters()) {

            				if (pc instanceof ProgramChapterAll) {
            					// TODO: don't load leaf node data now - load asynchronously into tree or separate window
            					for (ProgramLesson l : pc.getLessons()) {
            						//LessonItemModel lim = new LessonItemModel();
            						//lim.setName(l.getName());
            						CmTreeModel ctm = new CmTreeModel(l.getName());
            						ctm1.add(ctm);
            					}
            				}
            				else {
            					//ChapterModel cm = new ChapterModel(String.valueOf(pc.getNumber()), pc.getName());
            					CmTreeModel ctm2 = new CmTreeModel(String.valueOf(pc.getNumber()) + " " + pc.getName());
            					ctm1.add(ctm2);

            					// TODO: don't load leaf node data now - load asynchronously into tree or separate window
            					for (ProgramLesson l : pc.getLessons()) {
            						//LessonItemModel lim = new LessonItemModel();
            						//lim.setName(l.getName());
            						CmTreeModel ctm = new CmTreeModel(l.getName());
            						ctm2.add(ctm);
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