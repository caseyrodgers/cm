package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionLessonResourceVideoViewImpl extends AbstractPagePanel implements
        PrescriptionLessonResourceVideoView {

    Presenter presenter;

    public PrescriptionLessonResourceVideoViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    interface MyUiBinder extends UiBinder<Widget, PrescriptionLessonResourceVideoViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @Override
    public String getTitle() {
        return "Video Review";
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        presenter.setupView(this);
    }

    @Override
    public String getBackButtonText() {
        return "Back";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }

    @UiField
    HTMLPanel mainPanel;

    @Override
    public void setVideoUrl(String nonFlashVideoUrl) {
        String width = "390px";
        int height = 390;

        String html = "<div class='video_wrapper'>" + "  <object id='video_tag' width='" + width + "' height='"
                + height + "'>" + "    <param name='movie' value='" + nonFlashVideoUrl + "' />"
                + "    <param name='allowFullScreen' value='true' />" + ""
                + "    <param name='allowscriptaccess' value='always' />" + "    <embed src='" + nonFlashVideoUrl
                + "' type='application/x-shockwave-flash' "
                + "            allowscriptaccess='always' allowfullscreen='true' width='" + width + "' height='"
                + height + "'></embed>" + "  </object>" + "</div>";

        String newHtml = "<iframe class='youtube-player' type='text/html' width='640' height='385' src='"
                + nonFlashVideoUrl + "' frameborder='0'></iframe>";

        // html +=
        // "<button onclick='alert(document.getElementById(\"video_tag\").innerHTML);'>source</button>";
        mainPanel.clear();
        mainPanel.add(new HTML(newHtml));
    }

    @Override
    public void setVideoTitle(String title) {
        videoTitle.setInnerHTML(title);
    }

    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                mainPanel.clear();
                return true;
            }
        };
    }

    @UiField
    HeadingElement videoTitle;
}
