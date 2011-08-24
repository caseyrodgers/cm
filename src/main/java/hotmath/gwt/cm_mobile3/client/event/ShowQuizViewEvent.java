package hotmath.gwt.cm_mobile3.client.event;

import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;

import com.google.gwt.event.shared.GwtEvent;

public class ShowQuizViewEvent extends GwtEvent<ShowQuizViewHandler>{

    public static Type<ShowQuizViewHandler> TYPE = new Type<ShowQuizViewHandler>();

    QuizHtmlResult quizResult;
    
    public ShowQuizViewEvent() {
    }
    
    public ShowQuizViewEvent(QuizHtmlResult quizResult) {
        this.quizResult = quizResult;
    }
    
    @Override
    public Type<ShowQuizViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowQuizViewHandler handler) {
        handler.showQuizView(quizResult);
    }

}
