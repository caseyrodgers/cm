package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonActivity;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class LessonChooserDialog extends PopupPanel {
    PrescriptionLessonActivity presenter;

    public LessonChooserDialog(final PrescriptionLessonActivity presenter) {
        this.presenter = presenter;
        setStyleName("popup-message");
        setAutoHideEnabled(true);
        setModal(true);
        
        FlowPanel fp = new FlowPanel();
        TouchClickHandler<String> touchHandler = new TouchClickHandler<String>() {
            @Override
            public void touchClick(TouchClickEvent<String> event) {
                loadLesson(((MyGenericTextTag2)event.getTarget()).sessionNum);
            }
        };
        GenericContainerTag listItems = new GenericContainerTag("ul");
        List<String> topics = PrescriptionLessonActivity.getPrescriptionData().getSessionTopics();
        for(int sessNum=0,t=topics.size();sessNum<t;sessNum++) {
            MyGenericTextTag2 tt = new MyGenericTextTag2(topics.get(sessNum),sessNum,touchHandler);
            listItems.add(tt);
        }
        fp.add(new HTML("<h2>Select a Lesson to Study</h2>"));
        fp.add(listItems);
        setTitle("The Popup Panels Title");
        add(fp);
        center();
        
        //int left=0;
        //int top=50 + Window.getScrollTop();
        //popup.setPopupPosition(left, top);
        show();        
    }
    
    
    private void loadLesson(int sessionNum) {
        presenter.moveToLesson(CatchupMathMobile3.__clientFactory.getPrescriptionLessonView(),
                sessionNum);
        hide();
    }

}


class MyGenericTextTag2 extends GenericTextTag<String> {
    int sessionNum;
    public MyGenericTextTag2(String name,int sessionNum, TouchClickHandler<String> touchHandler) {
        super("li");
        this.sessionNum = sessionNum;
        addStyleName("group");
        addHandler(touchHandler);
        getElement().setInnerHTML("<span>" + name + "</span>");
    }
}