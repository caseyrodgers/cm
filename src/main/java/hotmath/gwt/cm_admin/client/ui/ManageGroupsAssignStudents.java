package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAssignAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAssignAction.ActionType;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAssignResponse;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;



public class ManageGroupsAssignStudents extends GWindow {

	GroupInfoModel gim;
	CheckBox showWorkRequired;
	CheckBox tutoringAllowed;
	CheckBox limitGames;
	CheckBox stopAtProgramEnd;

	CmAdminModel cm;
	CmAsyncRequestImplDefault callback;

	public ManageGroupsAssignStudents(CmAdminModel cm, GroupInfoModel gim, CmAsyncRequestImplDefault callback) {
	    super(false);
	    
		this.cm = cm;
		this.gim = gim;
		this.callback = callback;
		setHeadingText("Assign Students to group '" + gim.getGroupName() + "'");
		setPixelSize(500, 400);
		drawGui();
		setModal(true);
		setResizable(true);

		addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
				close();
			}
		}));

		getGroupInfo();

		setVisible(true);
	}

	ListView<StudentModelI, String> _listAll;
	ListView<StudentModelI, String> _listInGroup;
	TextButton _btnSave;
	
	StudentGridProperties __propsStudentGridProperties = GWT.create(StudentGridProperties.class);

	private void drawGui() {
	    

	    //ListView<StudentModelI, String> _listInGroup;
	    
//		StoreSorter<StudentModelI> sorter = new StoreSorter<StudentModelI>() {
//            @Override
//            public int compare(Store<StudentModelI> store, StudentModelI m1, StudentModelI m2,
//                    String property) {
//                    if (property != null) {
//                        return comparator.compare(m1.getName().toLowerCase(), m2.getName().toLowerCase());
//                    }            
//                    return super.compare(store, m1, m2, property);
//                }            
//        };
        
		ListStore<StudentModelI> storeAll = new ListStore<StudentModelI>(__propsStudentGridProperties.id());
		
		// storeAll.setStoreSorter(sorter);
		//storeAll.setSortField("name");
		String templateInGroup = "<tpl for=\".\"><div class='x-view-item'>{name}</div></tpl>";
		String templateOther = "<tpl for=\".\"><div class='x-view-item'>{name} <span style='font-size: .7em;color:red'>{group}</span></div></tpl>";
		
		_listAll = new ListView<StudentModelI, String>(storeAll,__propsStudentGridProperties.name());
		_listAll.setCell(new AbstractCell<String>(){
            @Override
            public void render(Context context, String value, SafeHtmlBuilder sb) {
                StudentModelI sm = _listAll.getStore().get(context.getIndex());
                sb.append(SafeHtmlUtils.fromString(value + " [" + sm.getGroup() + "]"));
            }
        });
		_listAll.setStore(storeAll);
		//_listAll.setTemplate(templateOther);

		ListStore<StudentModelI> store = new ListStore<StudentModelI>(__propsStudentGridProperties.id());
        //store.setStoreSorter(sorter);
        //store.setSortField("name");
		
		_listInGroup = new ListView<StudentModelI, String>(store,__propsStudentGridProperties.name());
		//_listInGroup.setTemplate(templateInGroup);
		
		_listInGroup.setBorders(false);
		_listAll.setBorders(false);
		
		_listAll.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                moveSelectedInto();
	      }
	    },DoubleClickEvent.getType());
		
		_listInGroup.addHandler(new DoubleClickHandler() {
		    @Override
		    public void onDoubleClick(DoubleClickEvent event) {
		        moveSelectedOut();
		      }
		    },DoubleClickEvent.getType());


		new ListViewDropTarget<StudentModelI>(_listAll);
		ListViewDropTarget<StudentModelI> target = new ListViewDropTarget<StudentModelI>(_listInGroup);
		target.setFeedback(Feedback.INSERT);
		target.setAllowSelfAsSource(false);

		new ListViewDragSource<StudentModelI>(_listAll);
		new ListViewDragSource<StudentModelI>(_listInGroup);

	    BorderLayoutContainer bordCont = new BorderLayoutContainer();
	    
	    BorderLayoutData data = new BorderLayoutData(250);
	    data.setCollapsible(true);
	    MyListContainer listConTo = new MyListContainer(_listInGroup, "Students In Group", true);
	    listConTo.addTool(new TextButton("Remove", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                moveSelectedOut();
            }
        }));
	    
	    MyListContainer listConAll = new MyListContainer(_listAll, "Students Not In Group [current]", false);
	    listConAll.addTool(new TextButton("Add", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                moveSelectedInto();
            }
        }));
	    
	    bordCont.setCenterWidget(listConTo,data);
	    bordCont.setWestWidget(listConAll,data);
	    

	    
	    setWidget(bordCont);

		_btnSave = new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
				saveChanges();
			}
		});
		getButtonBar().add(new Label("Drag and drop users into group."));    
		addButton(_btnSave);
	}
	
	private void moveSelectedInto() {
	    StudentModelI selected = _listAll.getSelectionModel().getSelectedItem();
	    if(selected != null) {
	        _listAll.getStore().remove(selected);
	        _listInGroup.getStore().add(selected);
	    }
	    
	}
	
	private void moveSelectedOut() {
        StudentModelI s1 = _listInGroup.getSelectionModel().getSelectedItem();
        _listInGroup.getStore().remove(s1);
        _listAll.getStore().add(s1);
	}

	private void saveChanges() {
		new RetryAction<GroupManagerAssignResponse>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(true);
				GroupManagerAssignAction action = new GroupManagerAssignAction(ActionType.SAVE_STUDENTS, gim);
				CmList<StudentModelI> cList = new CmArrayList<StudentModelI>();
				cList.addAll(_listInGroup.getStore().getAll());
				action.setGroupStudents(cList);
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
				GroupInfoModel myGif = new GroupInfoModel(cm.getUid(),gim.getId(),gim.getGroupName(),0, true, false);
				GroupManagerAssignAction action = new GroupManagerAssignAction(ActionType.GET_STUDENTS, myGif);
                CmList<StudentModelI> cList = new CmArrayList<StudentModelI>();
                cList.addAll(_listInGroup.getStore().getAll());

				action.setGroupStudents(cList);
				setAction(action);
				CmShared.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(GroupManagerAssignResponse response) {
				CmBusyManager.setBusy(false);
				_listInGroup.getStore().clear();
				_listInGroup.getStore().addAll(response.getInGroup());
				_listAll.getStore().addAll(response.getNotInGroup());

			}
		}.register();
	}

	static class MyListContainer extends ContentPanel {
		MyListContainer(ListView<StudentModelI, String> listView, String title,boolean showFilter) {
			super();

			setHeadingText(title);
			listView.setBorders(false);
			setWidget(listView);
		}
	}

	private void applyChanges() {
	}
}
