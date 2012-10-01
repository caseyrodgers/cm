package hotmath.gwt.cm_rpc.client.event;

import com.google.gwt.event.shared.GwtEvent;

/** Event fired when the window has changed size
 * 
 * @author casey
 *
 */
public class WindowHasBeenResizedEvent extends GwtEvent<WindowHasBeenResizedHandler> {

        public static Type<WindowHasBeenResizedHandler> TYPE = new Type<WindowHasBeenResizedHandler>();

          @Override
        public Type<WindowHasBeenResizedHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(WindowHasBeenResizedHandler handler) {
            handler.onWindowResized(this);
        }
}
