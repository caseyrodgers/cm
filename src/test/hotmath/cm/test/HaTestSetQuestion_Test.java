package hotmath.cm.test;

import junit.framework.TestCase;

public class HaTestSetQuestion_Test extends TestCase {
    
    public HaTestSetQuestion_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        
       String html = "<div class=\"hm_question_def\"> " +
       "  <div> " +
       "    <h1>Test Question 1</h1> " +
       "  <ul> " +
       "    <li correct=\"no\"> " +
       "      <div> " +
       "          <h1>Answer 1</h1> " +
       "      </div> " +
       "      <div>&nbsp;</div> " +
       "    </li> " +
       "    <li correct=\"no\"> " +
       "      <div>Answer 2</div> " +
       "      <div>&nbsp;</div> " +
       "    </li> " +
       "    <li correct=\"no\"> " +
       "      <div>Answer 3</div> " +
       "      <div>&nbsp;</div> " +
       "    </li> " +
       "    <li correct=\"yes\"> " +
       "      <div>Answer 4</div> " +
       "      <div>&nbsp;</div> " +
       "    </li> " +
       "  </ul> " +
       "</div> ";
       
       String pid="";
       
       HaTestSetQuestion tq = new HaTestSetQuestion(pid, html);
       assertTrue(tq.getQuestionHtmlProcessed().length() > 0);
    }

}
