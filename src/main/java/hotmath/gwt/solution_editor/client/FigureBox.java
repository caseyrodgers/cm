package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.util.CmAlertify.PromptCallback;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;


class FigureBox extends FlowLayoutContainer {
    String figure;
    String pid;
    Callback callback;
    Image figureImage = new Image();

    public FigureBox(String pid, final String figure, Callback callback) {
        this.pid = pid;
        this.figure = figure;
        this.callback = callback;

        addStyleName("FigureBox");

        add(figureImage);
        add(new Button("Figure",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
               CmMessageBox.prompt("Current Figure (cancel for no change)", "Enter Figure (" + FigureBox.this.figure + ")", "", new PromptCallback() {
				
				@Override
				public void promptValue(String value) {
                       setFigure(FigureBox.this.pid,value);
                       FigureBox.this.callback.figureChanged(value);

                       EventBus.getInstance().fireEvent(new CmEvent(EventTypes.SOLUTION_EDITOR_CHANGED));
                   }
               });
            }
        }));

        setFigure(pid, figure);
    }

    protected void setFigure(String pid, String figure) {
        this.figure = figure;
        if(figure == null) {
            figureImage.setVisible(false);
            return;
        }

        String base = "/help/solutions/";
        String ps[] = pid.split("_");
        for (int i=0,t=ps.length;i<t-2;i++) {
            base += "/" + ps[i];
        }
        base += "/" + pid + "/" + figure;
        figureImage.getElement().setAttribute("src", base);
        figureImage.setVisible(true);
    }

    static public interface Callback {
        void figureChanged(String figure);
    }
}