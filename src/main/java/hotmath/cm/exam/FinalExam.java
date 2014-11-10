package hotmath.cm.exam;

import hotmath.gwt.shared.client.CmProgram;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

public class FinalExam {

    private static final int[][] intRange60 =
        new int[][]{ {1,4},{5,8},{9,12},{13,16},{17,20},{21,24},{25,28},{29,32},{33,36},{37,40},{41,44},{45,48},{49,52},{53,56},{57,60} };

    private static final int[][] intRange50 =
        new int[][]{ {1,4},{5,7},{8,10},{11,14},{15,17},{18,20},{21,24},{25,27},{28,30},{31,34},{35,37},{38,40},{41,44},{45,47},{48,50} };
    
	static Logger __logger = Logger.getLogger(FinalExam.class);

    private QuizSize quizSize;
    private HaTestDef testDef;
    private List<String> quizIds;

    public FinalExam(CmProgram program, QuizSize quizSize) throws Exception {
        this.quizSize = quizSize;
        this.testDef = HaTestDefDao.getInstance().getTestDef(program.getDefId());

        doCreate();
        __logger.info("Quiz Created for: " + testDef + ": " + quizIds.size());
    }


    private void doCreate() throws Exception {
        List<List<String>> testIds = ExamDao.getInstance().getTestIdsForAllAlternates(testDef, quizSize);

        this.quizIds = extractBalancedIds(testIds, quizSize);
    }

    private List<String> extractBalancedIds(List<List<String>> testIds, QuizSize quizSize2) throws Exception {

        List<String> pids = new ArrayList<String>();
        Balancer balancer = null;
        switch(quizSize2) {
            case SIXTY:
                balancer = new TestBalancer60(testIds);
                break;

            case FOURTY_FIVE:
                balancer = new TestBalancer45(testIds);
                break;

            case THIRTY:
                balancer = new TestBalancer30(testIds);
                break;

            case FIFTEEN:
                balancer = new TestBalancer15(testIds);
                break;

            default:
                throw new Exception("Unknown size: " + quizSize2);
        }

         List<Integer> balancedKeys = balancer.getBalancedKeys();

         for(int k=0;k<balancedKeys.size();k++) {

             int randAlt = SbUtilities.getRandomNumber(testDef.getNumAlternateTests());

             String pid = testIds.get(randAlt).get(balancedKeys.get(k));
             pids.add(pid);
         }
         return pids;
    }

    static final int ALL = -1;

    class TestBalancer60 extends BaseBalancer {

        public TestBalancer60(List<List<String>> testIds) {
            super(testIds);
        }

        @Override
        public List<Integer> getBalancedKeys() {
            List<Integer> l=new ArrayList<Integer>();
            for(int i=0;i<60;i++) {
                l.add(i);
            }
            return l;
        }

        @Override
        public int[][] getRanges() {
            return intRange60;
        }

		@Override
		public int getNumPerRange() {
			return ALL;
		}

    }

    class TestBalancer45 extends BaseBalancer {

        public TestBalancer45(List<List<String>> testIds) {
            super(testIds);
        }

		@Override
		public int getNumPerRange() {
			return 3;
		}

    }

    class TestBalancer30 extends BaseBalancer {
        public TestBalancer30(List<List<String>> testIds) {
            super(testIds);
        }

		@Override
		public int getNumPerRange() {
			return 2;
		}
    }

    class TestBalancer15 extends BaseBalancer {
        public TestBalancer15(List<List<String>> testIds) {
            super(testIds);
        }

		@Override
		public int getNumPerRange() {
			return 1;
		}
    }

    interface Balancer {
        List<Integer> getBalancedKeys();
        int[][] getRanges();
        int getNumPerRange();
    }

    static final int MAX_ATTEMPTS = 10;
    
    abstract class BaseBalancer implements Balancer {
        int NUM_FROM_EACH_RANGE=3;
        protected List<List<String>> testIds;

        public BaseBalancer(List<List<String>> testIds) {
            this.testIds = testIds;
        }

        @Override
        public List<Integer> getBalancedKeys() {

            int[][] ranges = getRanges();
            List<Integer> keys = new ArrayList<Integer>();
            int numPerRange = getNumPerRange();
            for(int r=0;r<ranges.length;r++) {
                int range[] = ranges[r];


                for(int i=0;i<numPerRange;i++) {
                    // attempt to get unique, fail
                    // after MAX_ATTEMPTS.
                    int attempts=0;
                    while(true) {
                        int start=range[0];
                        int end=range[1];
                        int amount = end-start;
                        int rand = SbUtilities.getRandomNumber(amount);
                        int keyToUse = start+rand;

                        if(!keys.contains(keyToUse)) {
                            keys.add(keyToUse);
                            break;
                        }

                        if(attempts++ > MAX_ATTEMPTS) {
                            __logger.error("Too many attempts: " + start + ", " + end);
                        }
                    }
                }
            }
            return keys;
        }

        @Override
        public int[][] getRanges() {
        	int size = (testIds != null) ? testIds.get(0).size() : 0;
        	switch(size) {
        	case 60:
        		return intRange60;
        	case 50:
        		return intRange50;
        	default:
        		return null;
        	}
        }

    }


    public enum QuizSize {
        SIXTY(60), FOURTY_FIVE(45), THIRTY(30), FIFTEEN(15);
        private int count;

        private QuizSize(int count) {
            this.count = count;
        }

        public int getSize() {
            return count;
        }
    }


    public List<String> getPids() {
        return quizIds;
    }

}
