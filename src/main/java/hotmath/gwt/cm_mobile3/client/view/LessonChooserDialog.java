package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonActivity;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;

public class LessonChooserDialog extends PopupPanel {

    public LessonChooserDialog(final PrescriptionLessonActivity presenter) {
        setStyleName("popup-message");
        setAutoHideEnabled(true);
        setModal(true);
        
        FlowPanel fp = new FlowPanel();
        final ListBox listBox = new ListBox();
        List<String> topics = PrescriptionLessonActivity.getPrescriptionData().getSessionTopics();
        for(String topic: topics) {
            listBox.addItem(topic);
        }
        listBox.setVisibleItemCount(10);
        
        listBox.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                int sessionNum=listBox.getSelectedIndex();
                presenter.moveToLesson(CatchupMathMobile3.__clientFactory.getPrescriptionLessonView(),sessionNum);
                hide();
            }
        });
        
        fp.add(new HTML("<h2>Select a Lesson to Study</h2>"));
        fp.add(listBox);
        fp.add(new HTML("<br/>"));
        fp.add(new Button("Select", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                int sessionNum=listBox.getSelectedIndex();
                presenter.moveToLesson(CatchupMathMobile3.__clientFactory.getPrescriptionLessonView(),sessionNum);
                hide();
            }
        }));
        
        add(fp);
        center();
        
        //int left=0;
        //int top=50 + Window.getScrollTop();
        //popup.setPopupPosition(left, top);
        show();        
    }

}
