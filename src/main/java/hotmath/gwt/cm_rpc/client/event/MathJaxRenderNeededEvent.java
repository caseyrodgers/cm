package hotmath.gwt.cm_rpc.client.event;

import com.google.gwt.event.shared.GwtEvent;

/** Event fired when the window has changed size
 * 
 * @author casey
 *
 */
public class MathJaxRenderNeededEvent extends GwtEvent<MathJaxRenderNeededHandler> {

        public static Type<MathJaxRenderNeededHandler> TYPE = new Type<MathJaxRenderNeededHandler>();

          @Override
        public Type<MathJaxRenderNeededHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(MathJaxRenderNeededHandler handler) {
            handler.renderMathJax();
        }
}
