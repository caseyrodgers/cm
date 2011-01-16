package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.solution_editor.client.rpc.GetMathMlResourceAction;
import hotmath.gwt.solution_editor.client.rpc.MathMlResource;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SolutionResourcePanel extends LayoutContainer {
    
    SolutionResource resource;
    String pid;
    public SolutionResourcePanel(final SolutionResource sr, String pid) {
        resource = sr;
        this.pid = pid;
        
        setLayout(new BorderLayout());
        addStyleName("solution-resource-panel");
        
        String html = "<h2>" + sr.getFile() + "</h2>";
        if(sr.getUrlPath() != null) {
            html += "<img src='" + sr.getUrlPath() + "?rand=" + System.currentTimeMillis() + "'/>";
        }
        else if(sr.getDisplay() != null) {
            html += sr.getDisplay();
        }
        
        add(new Html(html), new BorderLayoutData(LayoutRegion.CENTER));

        ToolBar tb = new ToolBar();
        tb.add(new Button("URL", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                String url = "<img src='" + sr.getUrlPath() + "' class='solution_resource'/>";
                new ShowValueWindow(url);
            }
        }));
        tb.add(new Button("MathML", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                showMathMl();
            }
        }));
        
        add(tb, new BorderLayoutData(LayoutRegion.SOUTH,25));
    }
    
    
    private void showMathMl() {
            
            GetMathMlResourceAction action = null;
            if(pid != null) {
                action = new GetMathMlResourceAction(pid,resource.getFile());
            }
            else {
                action = new GetMathMlResourceAction(resource.getFile());
            }
            
            EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, "Reading MathML for: " + resource.getFile()));
            SolutionEditor.getCmService().execute(action, new AsyncCallback<MathMlResource>() {
                public void onSuccess(MathMlResource resource) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                    String mathMl = resource.getMathMl();
                    if(!mathMl.startsWith("<math")) {
                        mathMl = "<math>\n" + mathMl + "\n</math>";
                    }
                    new ShowValueWindow(mathMl);
                }
                @Override
                public void onFailure(Throwable arg0) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STATUS_MESSAGE, ""));
                    arg0.printStackTrace();
                    com.google.gwt.user.client.Window.alert(arg0.getLocalizedMessage());
                }
            });                
    }
    
    
    public SolutionResource getResource() {
        return resource;
    }
}


class ShowValueWindow extends Window {
    public ShowValueWindow(String text) {
        
        setLayout(new FitLayout());
        setSize(600,400);
        setScrollMode(Scroll.AUTO);
        setModal(true);
        setHeading("Value Display");
        
        final TextArea ta = new TextArea();
        ta.setValue(text);
        ta.setReadOnly(true);
        add(ta);
        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        
        setVisible(true);

        /** delay setting focus */
        new Timer() {
            @Override
            public void run() {
                ta.focus();
                ta.selectAll();
            }
        }.schedule(500);

    }
}