package hotmath.gwt.cm_mobile_shared.server.rpc;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileLessonInfoAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.MobileLessonInfo;

public class GetMobileLessonInfoCommand_Test extends CmDbTestCase {
    
    public GetMobileLessonInfoCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        String file = "topics/absolute-value.html";
        
        GetMobileLessonInfoAction action = new GetMobileLessonInfoAction(file);
        MobileLessonInfo lesson = new GetMobileLessonInfoCommand().execute(conn, action);
        assertNotNull(lesson.getPresData());
    }
}
