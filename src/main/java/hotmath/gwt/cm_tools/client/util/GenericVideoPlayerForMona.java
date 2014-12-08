package hotmath.gwt.cm_tools.client.util;


public class GenericVideoPlayerForMona extends GenericVideoPlayer {
    
    String videoPlayerId = "flowPlayer_" + System.currentTimeMillis();
    
    public GenericVideoPlayerForMona(MonaVideo video) {
        super(video.getFile(),video.getTitle());
        setPixelSize(330,320);
        setModal(true);
        
        setVisible(true);
    }
    
    
    static public enum MonaVideo {
        MOTIVATIONAL("http://catchupmath.com/assets/videos/mona_motivational_video.flv", "Motivational Video"),
        PASS_QUIZ("http://catchupmath.com/assets/videos/mona_congrats.flv","Congratulations" );
        
        private String title;
        private String file;
        
        MonaVideo(String file, String t) {
            this.title = t;
            this.file = file;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

    }
}