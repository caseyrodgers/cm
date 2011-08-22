package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import java.util.List;


public class PrescriptionLessonResourceVideoActivity implements PrescriptionLessonResourceVideoView.Presenter {


    List<Integer> testQuestionAnswers;

    private com.google.gwt.event.shared.EventBus eventBus;
    
    InmhItemData resourceItem;

    public PrescriptionLessonResourceVideoActivity(com.google.gwt.event.shared.EventBus eventBus, InmhItemData resourceItem) {
        this.eventBus = eventBus;
        this.resourceItem = resourceItem;
    }

    @Override
    public void setupView(final PrescriptionLessonResourceVideoView view) {
        int randUrl = (int) (Math.floor(Math.random() * urls.length));
        String p[] = urls[randUrl].split("\\|");
        String nonFlashUrl = p[1];
        view.setVideoUrl(nonFlashUrl);
        
        
        view.setVideoTitle(resourceItem.getTitle());
    }
    
    
    String urls[] = {
            "10001|http://www.youtube.com/embed/VZeq9Ho_xrg ",
            "10002|http://www.youtube.com/embed/dIghatl6M6E",
            "10003|http://www.youtube.com/embed/gSCtcQwZt8g",
            "10004|http://www.youtube.com/embed/qH5VyHxO1wE",
            "10005|http://www.youtube.com/embed/dBi1j1_S9_I",
            "10006|http://www.youtube.com/embed/8j7RuBgczMw",
            "10007|http://www.youtube.com/embed/j01d7wTS6rI",
            "10008|http://www.youtube.com/embed/t080PsdLDfU",
            "10009|http://www.youtube.com/embed/i7DQ8Qnh4Y4",
            "10010|http://www.youtube.com/embed/xAbc5XnjRJw",
            "10011|http://www.youtube.com/embed/WDHeRpxJ6n0",
            "10012|http://www.youtube.com/embed/5ppOkANGrtc",
            "10013|http://www.youtube.com/embed/z073dh76pjs",
            "10014|http://www.youtube.com/embed/h2J9XlUb7O4",
            "10015|http://www.youtube.com/embed/85Wu5MA8Mek",
            "10016|http://www.youtube.com/embed/oxlY89JsfQI",
            "10017|http://www.youtube.com/embed/EkYnG6izvW4",
            "10018|http://www.youtube.com/embed/s9g_TrQIBhc",
            "10019|http://www.youtube.com/embed/Cdy8aX1XWX8",
            "10020|http://www.youtube.com/embed/YKvvcBJvm-o",
            "10021|http://www.youtube.com/embed/Cbs-RN1ewR4",
            "10022|http://www.youtube.com/embed/s22ktHvBCnc",
            "10023|http://www.youtube.com/embed/R7w9nGULzow",
            "10024|http://www.youtube.com/embed/a8PD2-klOc8",
            "10025|http://www.youtube.com/embed/WAcPnmSUtM8",
            "10026|http://www.youtube.com/embed/tOv23M72Uy0",
            "10027|http://www.youtube.com/embed/h43E-pY6pRw",
            "10028|http://www.youtube.com/embed/cgASN5Svahc",
            "10029|http://www.youtube.com/embed/ytveRVSB8oY",
            "10030|http://www.youtube.com/embed/vRiy7iRNqAY"
    };
            
}






/*    
10001  http://www.youtube.com/watch?v=VZeq9Ho_xrg 
10002   http://www.youtube.com/watch?v=dIghatl6M6E
10003   http://www.youtube.com/watch?v=gSCtcQwZt8g
10004   http://www.youtube.com/watch?v=qH5VyHxO1wE
10005   http://www.youtube.com/watch?v=dBi1j1_S9_I
10006   http://www.youtube.com/watch?v=8j7RuBgczMw
10007   http://www.youtube.com/watch?v=j01d7wTS6rI
10008   http://www.youtube.com/watch?v=t080PsdLDfU
10009   http://www.youtube.com/watch?v=i7DQ8Qnh4Y4
10010   http://www.youtube.com/watch?v=xAbc5XnjRJw
10011   http://www.youtube.com/watch?v=WDHeRpxJ6n0
10012   http://www.youtube.com/watch?v=5ppOkANGrtc
10013   http://www.youtube.com/watch?v=z073dh76pjs
10014   http://www.youtube.com/watch?v=h2J9XlUb7O4
10015   http://www.youtube.com/watch?v=85Wu5MA8Mek
10016   http://www.youtube.com/watch?v=oxlY89JsfQI
10017   http://www.youtube.com/watch?v=EkYnG6izvW4
10018   http://www.youtube.com/watch?v=s9g_TrQIBhc
10019   http://www.youtube.com/watch?v=Cdy8aX1XWX8
10020   http://www.youtube.com/watch?v=YKvvcBJvm-o
10021   http://www.youtube.com/watch?v=Cbs-RN1ewR4
10022   http://www.youtube.com/watch?v=s22ktHvBCnc
10023   http://www.youtube.com/watch?v=R7w9nGULzow
10024   http://www.youtube.com/watch?v=a8PD2-klOc8
10025   http://www.youtube.com/watch?v=WAcPnmSUtM8
10026   http://www.youtube.com/watch?v=tOv23M72Uy0
10027   http://www.youtube.com/watch?v=h43E-pY6pRw
10028   http://www.youtube.com/watch?v=cgASN5Svahc
10029   http://www.youtube.com/watch?v=ytveRVSB8oY
10030   http://www.youtube.com/watch?v=vRiy7iRNqAY
*/