package hotmath.gwt.cm_tools.client.util;


public class GenericVideoPlayerForMona extends GenericVideoPlayer {
    
    String videoPlayerId = "flowPlayer_" + System.currentTimeMillis();
    
    public GenericVideoPlayerForMona() {
        super("/resources/videos/mona_motivational_video.flv", "Motivational Video");
        setSize(330,320);
        setVisible(true);
    }    
}