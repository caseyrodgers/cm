package hotmath.gwt.cm_mobile_shared.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EndOfProblemSetDialog extends Composite {

        interface MyUiBinder extends UiBinder<Widget, EndOfProblemSetDialog> {
        }

        private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

        public EndOfProblemSetDialog() {
            initWidget(uiBinder.createAndBindUi(this));


        }

        public void setTutorTitle(String title) {
            this.title.setInnerHTML(title);
        }

        @UiField
        DivElement title;
}
