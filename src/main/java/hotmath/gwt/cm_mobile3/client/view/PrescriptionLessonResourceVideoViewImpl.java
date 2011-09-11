package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
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

    public void setVideoUrlWithOutExtension(String videoUrl) {
        int width = 340;
        int height = 274;
        
        String mp4File = videoUrl + ".mp4";
        String oggFile = videoUrl + ".ogv";
        String videoHtml = 
                "<video width='" + width + "' height='" + height + "' controls='controls' autoplay='autoplay'> " +
                    "<source src='" + mp4File + "' type='video/mp4' />" +
                    "<source src='" + oggFile + "' type='video/ogg' />" +
                "</video>";
        
        videoContainer.setInnerHTML(videoHtml);
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                showVideo(videoContainer);
            }
        });
        
    }
    
     private native void showVideo(Element videoC) /*-{
         try {
            var con = videoC.getElementsByTagName('video');
            if(con.length > 0) {
                var videoEl = con[0];
                if(videoEl.paused) {
                    videoEl.play();
                }
             }
         }
         catch(e) {
            alert('could not start video: ' + e);
         }
            
     }-*/;
     

     private native void stopVideo(Element videoC) /*-{
         try {
            var con = videoC.getElementsByTagName('video');
            if(con.length > 0) {
                var videoEl = con[0];
                videoEl.pause();
            }
            
            videoC.innerHTML = '';
         }
         catch(e) {
            alert('could not stop video: ' + e);
         }
     }-*/;
     
     private native void resetVideo(Element videoC) /*-{
         try {
            var con = videoC.getElementsByTagName('video');
            if(con.length > 0) {
                var videoEl = con[0];
                videoEl.currentTime = 0;
                videoEl.play();
            }
         }
         catch(e) {
            alert('could not stop video: ' + e);
         }
     }-*/;
          

    public void setVideoUrlFlash(String nonFlashVideoUrl) {
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
                stopVideo(videoContainer);
                return true;
            }
        };
    }
    
    
    @UiField
    Element videoTitle, videoContainer;
}
