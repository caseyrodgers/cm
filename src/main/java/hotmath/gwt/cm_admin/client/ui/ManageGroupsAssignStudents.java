package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAssignAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAssignAction.ActionType;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAssignResponse;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.dnd.ListViewDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

class ManageGroupsAssignStudents extends CmWindow {

	GroupInfoModel gim;
	CheckBox showWorkRequired;
	CheckBox tutoringAllowed;
	CheckBox limitGames;
	CheckBox stopAtProgramEnd;

	CmAdminModel cm;
	CmAsyncRequestImplDefault callback;

	public ManageGroupsAssignStudents(CmAdminModel cm, GroupInfoModel gim, CmAsyncRequestImplDefault callback) {
		this.cm = cm;
		this.gim = gim;
		this.callback = callback;
		setHeading("Assign Students to group '" + gim.getName() + "'");
		setSize(500, 400);
		drawGui();
		setModal(true);
		setResizable(true);

		addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				close();
			}
		}));
		setLayout(new BorderLayout());

		getGroupInfo();

		setVisible(true);
	}

	ListView<StudentModelExt> _listAll = new ListView<StudentModelExt>();
	ListView<StudentModelExt> _listInGroup = new ListView<StudentModelExt>();
	Button _btnSave;

	private void drawGui() {
	    
	    setLayout(new BorderLayout());
	    

		StoreSorter<StudentModelExt> sorter = new StoreSorter<StudentModelExt>() {
            @Override
            public int compare(Store<StudentModelExt> store, StudentModelExt m1, StudentModelExt m2,
                    String property) {
                    if (property != null) {
                        return comparator.compare(m1.getName().toLowerCase(), m2.getName().toLowerCase());
                    }            
                    return super.compare(store, m1, m2, property);
                }            
        };
        
		ListStore<StudentModelExt> storeAll = new ListStore<StudentModelExt>();
		storeAll.setStoreSorter(sorter);
		storeAll.setSortField("name");
		String templateInGroup = "<tpl for=\".\"><div class='x-view-item'>{name}</div></tpl>";
		String templateOther = "<tpl for=\".\"><div class='x-view-item'>{name} <span style='font-size: .7em;color:red'>{group}</span></div></tpl>";
		_listAll.setStore(storeAll);
		_listAll.setTemplate(templateOther);

		ListStore<StudentModelExt> store = new ListStore<StudentModelExt>();
        store.setStoreSorter(sorter);
        store.setSortField("name");
		_listInGroup.setStore(store);
		_listInGroup.setTemplate(templateInGroup);
		
		_listInGroup.setBorders(false);
		_listAll.setBorders(false);
		
		_listAll.addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
	      public void handleEvent(BaseEvent be) {
	    	  StudentModelExt s1 = _listAll.getSelectionModel().getSelectedItem();
	    	  _listAll.getStore().remove(s1);
	    	  _listInGroup.getStore().add(s1);
	      }
	    });
		
		_listInGroup.addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
		      public void handleEvent(BaseEvent be) {
		    	  StudentModelExt s1 = _listInGroup.getSelectionModel().getSelectedItem();
		    	  _listInGroup.getStore().remove(s1);
		    	  _listAll.getStore().add(s1);
		      }
		    });



		new ListViewDropTarget(_listAll);
		ListViewDropTarget target = new ListViewDropTarget(_listInGroup);
		target.setFeedback(Feedback.INSERT);
		target.setAllowSelfAsSource(false);

		new ListViewDragSource(_listAll);
		new ListViewDragSource(_listInGroup);

	    LayoutContainer lc = new LayoutContainer(new BorderLayout());
		lc.add(new MyListContainer(_listAll, "Students Not In Group", false),new BorderLayoutData(LayoutRegion.WEST,270));
		lc.add(new MyListContainer(_listInGroup, "Students In Group", true),new BorderLayoutData(LayoutRegion.CENTER));

		add(lc, new BorderLayoutData(LayoutRegion.CENTER));

		_btnSave = new Button("Save", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				saveChanges();
			}
		});
		addButton(_btnSave);
		
		 String ledgend = "<div style='position: absolute; top: 1px; left: 0;width: 60px;'>"
			 + "Drag and drop users into group."
             + "</div>";
     

     getButtonBar().setStyleAttribute("position", "relative");
     Html html = new Html(ledgend);
     html.setToolTip("This color indicates highest appropriate level for the lesson.");
     getButtonBar().add(html);		
		
	}

	private void saveChanges() {
		new RetryAction<GroupManagerAssignResponse>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(true);
				GroupManagerAssignAction action = new GroupManagerAssignAction(ActionType.SAVE_STUDENTS, gim);
				action.setGroupStudents(_listInGroup.getStore().getModels());
				setAction(action);
				CmShared.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(GroupManagerAssignResponse response) {
				CmBusyManager.setBusy(false);
				callback.requestComplete("OK");
				close();
			}
		}.register();
	}

	private void getGroupInfo() {
		new RetryAction<GroupManagerAssignResponse>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(true);
				GroupManagerAssignAction action = new GroupManagerAssignAction(ActionType.GET_STUDENTS, gim);
				action.setGroupStudents(_listInGroup.getStore().getModels());
				setAction(action);
				CmShared.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(GroupManagerAssignResponse response) {
				CmBusyManager.setBusy(false);
				_listInGroup.getStore().removeAll();
				_listInGroup.getStore().add(response.getInGroup());
				_listAll.getStore().add(response.getNotInGroup());

			}
		}.register();
	}

	static class MyListContainer extends ContentPanel {
		MyListContainer(ListView<StudentModelExt> listView, String title,boolean showFilter) {
			super();

			setHeading(title);
			setLayout(new FitLayout());
			listView.setBorders(false);
			add(listView);
		}
	}

	private void applyChanges() {
	}
}
