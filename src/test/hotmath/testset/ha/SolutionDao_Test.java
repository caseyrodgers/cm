package hotmath.testset.ha;

import hotmath.cm.util.CompressHelper;
import hotmath.gwt.cm.server.CmDbTestCase;

import hotmath.spring.SpringManager;
import hotmath.testset.ha.SolutionDao;

public class SolutionDao_Test extends CmDbTestCase {

    SolutionDao dao;
    SpringManager manager;

    static String _contextJson =
    		"{\"_variables\":[{\"name\":\"_ans_e\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"NTQ5MA==\"}," +
            "{\"name\":\"_l_op\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"MzY2\"}," +
            "{\"name\":\"_r_op\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"MTU=\"}," +
            "{\"name\":\"n\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"MQ==\"}," +
            "{\"name\":\"_n\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"NQ==\"}," +
            "{\"name\":\"mr\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"MzY2\"}," +
            "{\"name\":\"_ml\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"MTgzMA==\"}," +
            "{\"name\":\"m_1\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"MzY2LCA3MzIsIDEwOTgsIDE0NjQsIDE4MzAsIDIxOTYsIDI1NjIsIDI5MjgsIDMyOTQsIDM2NjAsIDQwMjYsIDQzOTIsIDQ3NTgsIDUxMjQsIDxiPjU0OTA8L2I+\"}," +
            "{\"name\":\"m_2\",\"init\":\"\",\"type\":\"PROVIDED\", \"value\":\"MTUsIDMwLCA0NSwgNjAsIDc1LCA5MCwgMTA1LCAxMjAsIDEzNSwgMTUwLCAxNjUsIDE4MCwgMTk1LCAyMTAsIDIyNSwgMjQwLCAyNTUsIDI3MCwgMjg1LCAzMDAsIDMxNSwgMzMwLCAzNDUsIDM2MCwgMzc1LCAzOTAsIDQwNSwgNDIwLCA0MzUsIDQ1MCwgNDY1LCA0ODAsIDQ5NSwgNTEwLCA1MjUsIDU0MCwgNTU1LCA1NzAsIDU4NSwgNjAwLCA2MTUsIDYzMCwgNjQ1LCA2NjAsIDY3NSwgNjkwLCA3MDUsIDcyMCwgNzM1LCA3NTAsIDc2NSwgNzgwLCA3OTUsIDgxMCwgODI1LCA4NDAsIDg1NSwgODcwLCA4ODUsIDkwMCwgOTE1LCA5MzAsIDk0NSwgOTYwLCA5NzUsIDk5MCwgMTAwNSwgMTAyMCwgMTAzNSwgMTA1MCwgMTA2NSwgMTA4MCwgMTA5NSwgMTExMCwgMTEyNSwgMTE0MCwgMTE1NSwgMTE3MCwgMTE4NSwgMTIwMCwgMTIxNSwgMTIzMCwgMTI0NSwgMTI2MCwgMTI3NSwgMTI5MCwgMTMwNSwgMTMyMCwgMTMzNSwgMTM1MCwgMTM2NSwgMTM4MCwgMTM5NSwgMTQxMCwgMTQyNSwgMTQ0MCwgMTQ1NSwgMTQ3MCwgMTQ4NSwgMTUwMCwgMTUxNSwgMTUzMCwgMTU0NSwgMTU2MCwgMTU3NSwgMTU5MCwgMTYwNSwgMTYyMCwgMTYzNSwgMTY1MCwgMTY2NSwgMTY4MCwgMTY5NSwgMTcxMCwgMTcyNSwgMTc0MCwgMTc1NSwgMTc3MCwgMTc4NSwgMTgwMCwgMTgxNSwgMTgzMCwgMTg0NSwgMTg2MCwgMTg3NSwgMTg5MCwgMTkwNSwgMTkyMCwgMTkzNSwgMTk1MCwgMTk2NSwgMTk4MCwgMTk5NSwgMjAxMCwgMjAyNSwgMjA0MCwgMjA1NSwgMjA3MCwgMjA4NSwgMjEwMCwgMjExNSwgMjEzMCwgMjE0NSwgMjE2MCwgMjE3NSwgMjE5MCwgMjIwNSwgMjIyMCwgMjIzNSwgMjI1MCwgMjI2NSwgMjI4MCwgMjI5NSwgMjMxMCwgMjMyNSwgMjM0MCwgMjM1NSwgMjM3MCwgMjM4NSwgMjQwMCwgMjQxNSwgMjQzMCwgMjQ0NSwgMjQ2MCwgMjQ3NSwgMjQ5MCwgMjUwNSwgMjUyMCwgMjUzNSwgMjU1MCwgMjU2NSwgMjU4MCwgMjU5NSwgMjYxMCwgMjYyNSwgMjY0MCwgMjY1NSwgMjY3MCwgMjY4NSwgMjcwMCwgMjcxNSwgMjczMCwgMjc0NSwgMjc2MCwgMjc3NSwgMjc5MCwgMjgwNSwgMjgyMCwgMjgzNSwgMjg1MCwgMjg2NSwgMjg4MCwgMjg5NSwgMjkxMCwgMjkyNSwgMjk0MCwgMjk1NSwgMjk3MCwgMjk4NSwgMzAwMCwgMzAxNSwgMzAzMCwgMzA0NSwgMzA2MCwgMzA3NSwgMzA5MCwgMzEwNSwgMzEyMCwgMzEzNSwgMzE1MCwgMzE2NSwgMzE4MCwgMzE5NSwgMzIxMCwgMzIyNSwgMzI0MCwgMzI1NSwgMzI3MCwgMzI4NSwgMzMwMCwgMzMxNSwgMzMzMCwgMzM0NSwgMzM2MCwgMzM3NSwgMzM5MCwgMzQwNSwgMzQyMCwgMzQzNSwgMzQ1MCwgMzQ2NSwgMzQ4MCwgMzQ5NSwgMzUxMCwgMzUyNSwgMzU0MCwgMzU1NSwgMzU3MCwgMzU4NSwgMzYwMCwgMzYxNSwgMzYzMCwgMzY0NSwgMzY2MCwgMzY3NSwgMzY5MCwgMzcwNSwgMzcyMCwgMzczNSwgMzc1MCwgMzc2NSwgMzc4MCwgMzc5NSwgMzgxMCwgMzgyNSwgMzg0MCwgMzg1NSwgMzg3MCwgMzg4NSwgMzkwMCwgMzkxNSwgMzkzMCwgMzk0NSwgMzk2MCwgMzk3NSwgMzk5MCwgNDAwNSwgNDAyMCwgNDAzNSwgNDA1MCwgNDA2NSwgNDA4MCwgNDA5NSwgNDExMCwgNDEyNSwgNDE0MCwgNDE1NSwgNDE3MCwgNDE4NSwgNDIwMCwgNDIxNSwgNDIzMCwgNDI0NSwgNDI2MCwgNDI3NSwgNDI5MCwgNDMwNSwgNDMyMCwgNDMzNSwgNDM1MCwgNDM2NSwgNDM4MCwgNDM5NSwgNDQxMCwgNDQyNSwgNDQ0MCwgNDQ1NSwgNDQ3MCwgNDQ4NSwgNDUwMCwgNDUxNSwgNDUzMCwgNDU0NSwgNDU2MCwgNDU3NSwgNDU5MCwgNDYwNSwgNDYyMCwgNDYzNSwgNDY1MCwgNDY2NSwgNDY4MCwgNDY5NSwgNDcxMCwgNDcyNSwgNDc0MCwgNDc1NSwgNDc3MCwgNDc4NSwgNDgwMCwgNDgxNSwgNDgzMCwgNDg0NSwgNDg2MCwgNDg3NSwgNDg5MCwgNDkwNSwgNDkyMCwgNDkzNSwgNDk1MCwgNDk2NSwgNDk4MCwgNDk5NSwgNTAxMCwgNTAyNSwgNTA0MCwgNTA1NSwgNTA3MCwgNTA4NSwgNTEwMCwgNTExNSwgNTEzMCwgNTE0NSwgNTE2MCwgNTE3NSwgNTE5MCwgNTIwNSwgNTIyMCwgNTIzNSwgNTI1MCwgNTI2NSwgNTI4MCwgNTI5NSwgNTMxMCwgNTMyNSwgNTM0MCwgNTM1NSwgNTM3MCwgNTM4NSwgNTQwMCwgNTQxNSwgNTQzMCwgNTQ0NSwgNTQ2MCwgNTQ3NSwgPGI+NTQ5MDwvYj4=\"}]}";
    		
    public SolutionDao_Test(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        manager = SpringManager.getInstance();
        dao = (SolutionDao)manager.getBeanFactory().getBean("hotmath.testset.ha.SolutionDao");
    }

    public void testSaveSolutionContextCompressed() throws Exception {
        int runId = 1;
        String pid = "compression_test";
        int problemNumber = 1;
        dao.saveSolutionContextCompressed(runId, pid, problemNumber, _contextJson);
        assertTrue(true);
    }

    public void testgetSolutionContextCompressed() throws Exception {
        int runId = 1;
        String pid = "compression_test";
        String solnCtx = dao.getSolutionContextCompressedString(runId, pid);
        assertTrue(solnCtx.equals(_contextJson));
    }

}
