package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowPrescriptionLessonViewEvent extends GwtEvent<ShowPrescriptionLessonViewHandler>{

    public static Type<ShowPrescriptionLessonViewHandler> TYPE = new Type<ShowPrescriptionLessonViewHandler>();

    public ShowPrescriptionLessonViewEvent() {
    }
    
    @Override
    public Type<ShowPrescriptionLessonViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowPrescriptionLessonViewHandler handler) {
        handler.showPrescriptionLesson();
    }

}
