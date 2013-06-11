package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.event.ShowPrescriptionLessonViewEvent;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
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

    String _videoTitle="";
    @Override
    public String getViewTitle() {
        return "Video Lesson";
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
    
    @UiField
    Element videoCompleteWrapper, videoWrapper;
    
    @UiField
    SexyButton resetButton, returnButton;
    

    public void setVideoUrlWithOutExtension(String videoUrl) {
        
        setupViewForVideoViewing();
        
        Log.debug("Showing video: " + videoUrl);
        
        String mp4File = videoUrl + ".mp4";
        String oggFile = videoUrl + ".ogv";
        String videoHtml = 
                "<video class='cm_video' controls='controls' autoplay='autoplay'> " +
                    "<source src='" + mp4File + "' type='video/mp4' />" +
                    "<source src='" + oggFile + "' type='video/ogg' />" +
                "</video>";
        
        videoContainer.setInnerHTML(videoHtml);
        videoReadyLabel.setInnerText("Touch the arrow once it appears.");
        setupVideoListeners(videoContainer, this);
    }
    
    private void gwt_videoIsReady() {
        this.videoReadyLabel.setInnerHTML("");   
    }

    private void gwt_videoIsComplete() {
        videoWrapper.setAttribute("style",  "display: none");
        videoCompleteWrapper.setAttribute("style",  "display: block");
    }

    
    private native void setupVideoListeners(Element videoC, PrescriptionLessonResourceVideoViewImpl instance) /*-{
    
        var videos = videoC.getElementsByTagName('video');
        if(videos.length > 0) {
            var videoEl = videos[0]; // just first
            var autoPlay = function() {
                videoEl.play();
                instance.@hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoViewImpl::gwt_videoIsReady()();                
            }
            var videoEnded = function() {
                     instance.@hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoViewImpl::gwt_videoIsComplete()();
            }
            videoEl.addEventListener('canplaythrough',autoPlay,false);
            videoEl.addEventListener('ended',videoEnded,false);
         }        
    }-*/;
    
     private native void showVideo(Element videoC) /*-{
         try {
            var videoEl = videoC.getElementsByTagName('video')[0];
            if(videoEl.paused) {
                videoEl.play();
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
     
     private void setupViewForVideoViewing() {
         videoCompleteWrapper.setAttribute("style",  "display: none");
         videoWrapper.setAttribute("style",  "display: block");
     }
     
     private void resetVideo(Element videoC) {
         setupViewForVideoViewing();
         jsni_resetVideo(videoC);
     }
     
     private native void jsni_resetVideo(Element videoC) /*-{
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
    
    
    @UiHandler("returnButton")
    protected void handleReturn(ClickEvent ce) {
        presenter.getEventBus().fireEvent(new ShowPrescriptionLessonViewEvent());
    }
    
    @UiHandler("resetButton")
    protected void handleReset(ClickEvent ce) {
        resetVideo(videoContainer);        
    }
    
    @UiField
    Element videoContainer;
    
    @UiField
    Element videoReadyLabel, videoTitle;
    
    }
