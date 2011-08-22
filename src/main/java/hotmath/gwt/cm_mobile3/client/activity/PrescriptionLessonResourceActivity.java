package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceView;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import java.util.List;


public class PrescriptionLessonResourceActivity implements PrescriptionLessonResourceView.Presenter {


    List<Integer> testQuestionAnswers;

    private com.google.gwt.event.shared.EventBus eventBus;
    
    InmhItemData resourceItem;

    public PrescriptionLessonResourceActivity(com.google.gwt.event.shared.EventBus eventBus, InmhItemData resourceItem) {
        this.eventBus = eventBus;
        this.resourceItem = resourceItem;
    }

    @Override
    public void setupView(PrescriptionLessonResourceView view) {
        view.setResourceTitle(resourceItem.getTitle());
        
        
        view.setResourceWidget(ResourceFactory.createViewer(resourceItem).asWidget());
    }
}
