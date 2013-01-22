package hotmath.gwt.cm_rpc.client.event;

import com.google.gwt.event.shared.GwtEvent;

/** Global CM Event fired when the tutor widget has been entered correctly
 * 
 * @author casey
 *
 */
public class ShowTutorWidgetCompleteInfoEvent extends GwtEvent<ShowTutorWidgetCompleteInfoHandler> {

        public static Type<ShowTutorWidgetCompleteInfoHandler> TYPE = new Type<ShowTutorWidgetCompleteInfoHandler>();

        
        private ShowTutorWidgetCompleteInfoEvent(){}
        
          @Override
        public Type<ShowTutorWidgetCompleteInfoHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(ShowTutorWidgetCompleteInfoHandler handler) {
            handler.showTutorWidgetCompleteInfo();
        }
}
