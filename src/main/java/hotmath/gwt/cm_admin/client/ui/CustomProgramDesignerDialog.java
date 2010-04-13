package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction.ActionType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.dnd.ListViewDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class CustomProgramDesignerDialog extends CmWindow {

    CmAdminModel adminModel;
    CustomProgramModel customProgram;
    CmAsyncRequest callback;
    boolean _isEditable;

    public CustomProgramDesignerDialog(CmAdminModel adminModel,CmAsyncRequest callback) {
        this(adminModel,callback, null);
    }
    
    public CustomProgramDesignerDialog(CmAdminModel adminModel, CmAsyncRequest callback, CustomProgramModel program) {
        this.adminModel = adminModel;
        this.callback = callback;
        this.customProgram = program;
        
        setStyleName("custom-prescription-dialog");
        setHeading("Catchup Math Custom Program Designer");

        setModal(true);
        setSize(640, 480);


        buildGui();
        String programName="";
        loadCustomProgramDefinition(customProgram);
        if(customProgram != null) {
            programName = customProgram.getProgramName();
            if(customProgram.getIsTemplate()) {
                _isEditable = true;
                customProgram = new CustomProgramModel();
                programName = "";
            }
            else if(customProgram.getAssignedCount() == 0) {
                _isEditable = true;
            }
        }
        else {
            createNewProgram();
            _programName.setValue(customProgram.getProgramName());
            _isEditable=true;
            programName = "";
        }
        add(createInfoSection(_isEditable, programName), new BorderLayoutData(LayoutRegion.NORTH,50));        
        enableForm(_isEditable);
        
        setVisible(true);
    }

    ListView<CustomLessonModel> _listAll = new ListView<CustomLessonModel>();
    ListView<CustomLessonModel> _listSelected = new ListView<CustomLessonModel>();
    Button _btnClearAll, _btnSave;
    TextField<String> _programName = new TextField<String>();
    private void buildGui() {
        setLayout(new BorderLayout());
        
        String template = "<tpl for=\".\"><div class='x-view-item'><span style='font-size:.5em;width: 5px;' class='{subjectStyleClass}'>&nbsp;</span>&nbsp;{" + "lesson" + "}</div></tpl>";

        
        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new RowLayout(Orientation.HORIZONTAL));
       
        ListStore<CustomLessonModel> storeAll = new ListStore<CustomLessonModel>();
        storeAll.setStoreSorter(new StoreSorter<CustomLessonModel>() {
            @Override
            public int compare(Store<CustomLessonModel> store, CustomLessonModel m1, CustomLessonModel m2,
                    String property) {
                    if (property != null) {
                        String v1 = m1.getLesson();
                        String v2 = m2.getLesson();
                        return comparator.compare(v1, v2);
                    }            
                    return super.compare(store, m1, m2, property);
                }            
        });

        _listAll.setStore(storeAll);
        _listAll.setTemplate(template);

        ListStore<CustomLessonModel> store = new ListStore<CustomLessonModel>();
        _listSelected.setStore(store);
        _listSelected.setTemplate(template);

         
        new ListViewDropTarget(_listAll);
        ListViewDropTarget target = new ListViewDropTarget(_listSelected);
        target.setFeedback(Feedback.INSERT);
        target.setAllowSelfAsSource(true);
        
        RowData data = new RowData(.5, 1);
        data.setMargins(new Margins(5));
        
        lc.add(new MyListContainer(_listAll,"All Available Lessons",true), data);
        lc.add(new MyListContainer(_listSelected,"Lessons in Program",false), data);

        add(lc, new BorderLayoutData(LayoutRegion.CENTER));

        

        _btnClearAll = new Button("Clear All", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                getAllLessonData();
                _listSelected.getStore().removeAll();
            }
        });
        _btnClearAll.setEnabled(false);
        addButton(_btnClearAll);        

        _btnSave = new Button("Save", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                saveChanges(customProgram);
            }
        });
        _btnSave.setEnabled(false);
        addButton(_btnSave);
        
        addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));

        String ledgend = "<div style='position: absolute; top: 2px; left: 0;width: 260px;'>"
                + "<div style='margin-right: 3px;float: left;' class='pre-alg'>Pre-Algebra</div>"
                + "<div style='margin-right: 3px;float: left;' class='alg1'>Algebra 1</div>"
                + "<div style='margin-right: 3px;float: left;' class='alg2'>Algebra 2</div>"
                + "<div style='margin-right: 3px;float: left;' class='geom'>Geometry</div>" + "</div>";

        getButtonBar().setStyleAttribute("position", "relative");
        Html html = new Html(ledgend);
        html.setToolTip("This color indicates highest appropriate level for the lesson.");
        getButtonBar().add(html);

        getAllLessonData();
    }
    
    /** Enable form and prepare for editing.
     * 
     *  initially form is read-only, only after
     *  checking that the program is not 'in-use'
     *  is it enabled.
     */
    private void enableForm(boolean yn) {
        if(yn) {
            new ListViewDragSource(_listAll);
            new ListViewDragSource(_listSelected);
            _btnSave.setEnabled(true);
            _btnClearAll.setEnabled(true);
            _programName.setEnabled(true);
        }
        else {
            
        }
    }

    private LayoutContainer createInfoSection(boolean isEditable, String programName) {
        LayoutContainer lc = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
        
        FormPanel fp = new FormPanel();
        fp.setLabelWidth(90);
        fp.setWidth(340);
        fp.setHeaderVisible(false);
        fp.setBorders(false);
        fp.setFooter(false);
        fp.setFrame(false);
        fp.setBodyBorder(false);
        _programName.setFieldLabel("Program Name");
        _programName.setValue(programName);
        _programName.setEnabled(false);
        fp.add(_programName);
        lc.add(fp);
        
        _programName.focus();
        
        String msg = null;
        if(isEditable)
            msg = "Drag and drop lessons from left side to create and reorder the custom program";
        else {
            if(customProgram.getIsTemplate())
                msg = "<span style='color: red;font-weight: bold'>This program is a system template and cannot be edited.</span>";
            else 
                msg = "<span style='color: red;font-weight: bold'>This program is in use and cannot be edited.</span>";
        }
        Html html = new Html("<p style='padding: 7px 0; width: 250px;'>" + msg + "</p>");
        lc.add(html);
        lc.setScrollMode(Scroll.AUTOX);
        return lc;
    }
    
    /** Create a new empty custom program 
     * 
     */
    private void createNewProgram() {
        _programName.setValue("");
        _listSelected.getStore().removeAll();
        this.customProgram = new CustomProgramModel();
        this.customProgram.setProgramName("");
    }

    
    
    static CmList<CustomLessonModel> __allLessons;
    private void getAllLessonData() {
        if(__allLessons != null) {
            _listAll.getStore().removeAll();
            _listAll.getStore().add(__allLessons);
            return;
        }
        
        new RetryAction<CmList<CustomLessonModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramAction action = new CustomProgramAction(ActionType.GET_ALL_LESSONS);
                setAction(action);
                CmShared.getCmService().execute(action, this);

            }

            @Override
            public void oncapture(CmList<CustomLessonModel> allLessons) {
                CmBusyManager.setBusy(false);                
                __allLessons = allLessons;
                _listAll.getStore().removeAll();
                _listAll.getStore().add(__allLessons);
            }
        }.register();
    }
    
    private void loadCustomProgramDefinition(final CustomProgramModel program) {
        
        _programName.setValue(program.getProgramName());
        
        new RetryAction<CmList<CustomLessonModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramAction action = new CustomProgramAction(ActionType.GET_CUSTOM_PROGRAM);
                action.setProgramId(program.getProgramId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomLessonModel> lessons) {
                
                /** make sure lessons selected are NOT in all
                 * 
                 */
                ListStore<CustomLessonModel> s = _listAll.getStore();
                for(int i=0,t=lessons.size();i<t;i++) {
                    CustomLessonModel clm = lessons.get(i);
                    for(int ii=0,tt=s.getCount();ii<tt;ii++) {
                        CustomLessonModel clm2 = s.getAt(ii);
                        if(clm2 == null)
                            continue;
                        
                        if(clm2.equals(clm)) {
                            s.remove(clm2);
                        }
                    }
                }
                _listSelected.getStore().removeAll();
                _listSelected.getStore().add(lessons);

                CmBusyManager.setBusy(false);
            }
        }.register();
    }
    
    private void saveChanges(final CustomProgramModel program) {
        
        // validate ..
        String pn = _programName.getValue();
        if(pn == null || pn.length() == 0) {
            CatchupMathTools.showAlert("Please enter a custom program name");
            return;
        }
        
        if(_listSelected.getStore().getCount() == 0) {
            CatchupMathTools.showAlert("There are no lessons assigned to this custom program.");
            return;
        }
        
        new RetryAction<CmList<CustomLessonModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramAction action = new CustomProgramAction();
                if(program.getProgramId()==null)
                    action.setAction(ActionType.CREATE);
                else 
                    action.setAction(ActionType.SAVE);
                action.setAdminId(adminModel.getId());
                action.setProgramId(program.getProgramId());
                action.setProgramName(_programName.getValue());
                action.setLessons(_listSelected.getStore().getModels());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);
                String msg = error.getMessage();
                if(msg.indexOf("Duplicate") > -1) {
                    CatchupMathTools.showAlert("Could not save Custom Program: duplicate program name");
                }
                else
                    super.onFailure(error);
            }
            
            @Override
            public void oncapture(CmList<CustomLessonModel> value) {
                Log.info("CustomProgramModel Save complete: " + value);
                CmBusyManager.setBusy(false);
                callback.requestComplete("refresh");
                close();
            }
        }.attempt();
    }
    
    static class LessonNameStoreSorter extends StoreSorter<CustomLessonModel> {
        @Override
        public int compare(Store<CustomLessonModel> store, CustomLessonModel m1, CustomLessonModel m2, String property) {
            if (property != null) {
                String v1 = m1.getLesson();
                String v2 = m2.getLesson();
                return comparator.compare(v1, v2);
            }            
            return super.compare(store, m1, m2, property);
        }
    }
    
    static class MyListContainer extends ContentPanel {
         MyListContainer(ListView<CustomLessonModel> listView, String title, boolean showFilter) {
            super();
            
            setHeading(title);
            setLayout(new FitLayout());
            add(listView);
            
            if(showFilter)
                getHeader().addTool(new MyFilterBox(listView));
        }
    }
    
    static class MyFilterBox extends TextField<String> {
        
        int lastChecked=0;
        MyFilterBox(final ListView<CustomLessonModel>  listView) {
            setWidth(80);
            setEmptyText("--find--");
            setToolTip("Search for a lesson containing text");
            enableEvents(true);
            addListener(Events.KeyUp,new Listener<BaseEvent>() {
                public void handleEvent(BaseEvent be) {
                    String value = getValue();
                    if(value == null || value.length() == 0) {
                        lastChecked=0; // reset
                        return;
                    }
                    
                    int cnt = listView.getStore().getCount();
                    CustomLessonModel lesson = listView.getSelectionModel().getSelectedItem();
                    if(lastChecked+1 > cnt)
                        lastChecked = 0;
                    
                    for(int i=lastChecked;i<cnt;i++) {
                        CustomLessonModel model = listView.getStore().getAt(i);
                        lastChecked=i;
                        if(model.getLesson().toLowerCase().indexOf(value.toLowerCase()) > -1) {
                            if(lesson != null && lesson.getLesson().equals(model.getLesson()))
                                continue;
                            
                            listView.getSelectionModel().select(listView.getStore().getAt(i),false);
                            listView.getElement(i).scrollIntoView();
                            return;
                        }
                    }
                }
            });
        }
    }
}
