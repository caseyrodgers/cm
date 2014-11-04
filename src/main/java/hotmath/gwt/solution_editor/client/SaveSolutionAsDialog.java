package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;


public class SaveSolutionAsDialog extends GWindow {
    Callback callback;
    
    static private SaveSolutionAsDialog __instance;
    static public SaveSolutionAsDialog getSharedInstance() {
        __instance = null;
        if(__instance == null)
            __instance = new SaveSolutionAsDialog();
        return __instance;
    }
    private SaveSolutionAsDialog() {
        super(false);
        
        setHeadingText("Save Solution As");
        setPixelSize(350,300);
        setResizable(false);
        
        setModal(true);
        
        FramedPanel frame = new FramedPanel();
        frame.setBodyBorder(false);
        // form.setBorders(false);
        frame.setHeaderVisible(false);
        // form.setLabelWidth(100);
        // form.setFieldWidth(200);
        
        // book.setFieldLabel("Book");
        book.setAllowBlank(false);
        // chap.setFieldLabel("Chapter");
        chap.setAllowBlank(false);
        // sect.setFieldLabel("Section");
        sect.setAllowBlank(false);
        // probSet.setFieldLabel("Problem Set");
        probSet.setAllowBlank(false);
        // probNum.setFieldLabel("Problem Number");
        probNum.setAllowBlank(false);
        // pageNum.setFieldLabel("Page Number");        
        pageNum.setAllowBlank(false);
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(new FieldLabel(book, "Book"));
        flow.add(new FieldLabel(chap, "Chapter"));
        flow.add(new FieldLabel(sect, "Section"));
        flow.add(new FieldLabel(probSet, "Problem Set"));
        flow.add(new FieldLabel(probNum, "Problem Number"));
        flow.add(new FieldLabel(pageNum, "Page Number"));
        
        frame.setWidget(flow);
        
        TextButton save = new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                String newPid = buildNewPid();
                callback.saveSolutionAs(newPid);
                hide();
            }
        });
        
        // FormButtonBinding binding = new FormButtonBinding(form);
        // binding.addButton(save);
        
        addButton(save);
        
        setWidget(frame);
        addCloseButton();
    }

    private void loadPid(String pid) {
        String p[] = pid.split("_");
        book.setValue(p[0]);
        chap.setValue(p[1]);
        sect.setValue(p[2]);
        probSet.setValue(p[3]);
        probNum.setValue(p[4]);
        pageNum.setValue(p[5]);
        
        forceLayout();
    }
    
    private String buildNewPid() {
        String b = book.getValue();
        String c = chap.getValue();
        String s = sect.getValue();
        String ps = probSet.getValue();
        String pn = probNum.getValue();
        String pg = pageNum.getValue();
        return (b + "_" + c + "_" + s + "_" + ps + "_" + pn + "_" + pg);
    }

    public void setCallback(Callback callback, String pidToSaveAs) {
        this.callback = callback;
        loadPid(pidToSaveAs);
        
        setVisible(true);
    }
    
    TextField book = new TextField();
    TextField chap = new TextField();
    TextField sect = new TextField();
    TextField probSet = new TextField();
    TextField probNum = new TextField();
    TextField pageNum = new TextField();
    
    public interface Callback {
        /** called when new pid name */
        void saveSolutionAs(String pid);
    }
}
