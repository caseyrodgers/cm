package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemLinkedLessonAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class CustomProblemPropertiesDialog extends GWindow {

    private static CustomProblemPropertiesDialog __instance;

    public static Component getInstance(CustomProblemModel problem) {
        __instance = null;
        if (__instance == null) {
            __instance = new CustomProblemPropertiesDialog();
        }
        __instance.setSolution(problem);
        
        __instance.setVisible(true);
        return __instance;
    }

    private CustomProblemModel _customProblem;

    private void setSolution(CustomProblemModel problem) {
        _customProblem = problem;
        _lessonsPanel.setSolution(problem.getPid());
        _comments.setValue(problem.getComments());
    }

    CustomProblemLinkedLessonsPanel _lessonsPanel;
    TextArea _comments;
    
    public CustomProblemPropertiesDialog() {
        super(false);
        setHeadingText("Setup Custom Problem properties");

        setPixelSize(450, 400);

        drawGui();
        
        addButton(new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSave();
            }

        }));
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
    }


    private void doSave() {
        saveLessonsToServer(_customProblem.getPid(), _comments.getValue(), _lessonsPanel._grid.getStore().getAll());
    }
    

    private void saveLessonsToServer(final String pid, final String comments, final List<LessonModel> lessons) {

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SaveCustomProblemLinkedLessonAction action = new SaveCustomProblemLinkedLessonAction(UserInfoBase.getInstance().getUid(), pid, comments, new CmArrayList<LessonModel>(lessons));
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                Log.info("Custom Problem linked to lessons: " + value);
            }
        }.register();

    }

    
    private void drawGui() {
        BorderLayoutContainer bl = new BorderLayoutContainer();
        _comments = new TextArea();
        FramedPanel fp1 = new FramedPanel();
        fp1.setWidget(_comments);
        fp1.setHeadingText("Comments");
        fp1.setBorders(false);
        
        bl.setNorthWidget(fp1, new BorderLayoutData(120));

        _lessonsPanel = new CustomProblemLinkedLessonsPanel();
        bl.setCenterWidget(_lessonsPanel);
        
        setWidget(bl);
    }
}
