package hotmath.cm.util.service;

import hotmath.cm.server.model.QuizResultDatabaseAccessor;
import hotmath.cm.server.model.QuizResultsFileSystemAccessor;
import hotmath.cm.test.HaTestSet;
import hotmath.cm.test.HaTestSetQuestion;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaTestRunResult;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.visitors.NodeVisitor;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Save Quiz Results as PDF
 * 
 * NOTE: extends base class
 * 
 * @author bob
 *
 */

public class SaveQuizResultsAsPDF extends QuizResultsAsPDFBase {

	private static Logger LOGGER = Logger.getLogger(SaveQuizResultsAsPDF.class);
	
	private boolean runInSeparateThread = true;
	
	private String assetsPath;
	
	private String webPath;
	

	public SaveQuizResultsAsPDF() {
		setUseDatabaseAccessor(true);
	}

    public void doIt(int runId) throws Exception {

	    SaveQuizResultsRunnable saveRunnable =
	    	new SaveQuizResultsRunnable(runId);

	    if (isRunInSeparateThread()) {
	    	if (LOGGER.isDebugEnabled())
	    		LOGGER.debug("running in separate thread");
            Thread t = new Thread(saveRunnable);
            t.start();
	    }
	    else {
	    	if (LOGGER.isDebugEnabled())
	    		LOGGER.debug("running in same thread");
	    	saveRunnable.run();
	    }

    }

    public void setRunInSeparateThread(boolean runInSeparateThread) {
		this.runInSeparateThread = runInSeparateThread;
	}

	public boolean isRunInSeparateThread() {
		return runInSeparateThread;
	}

	public String getAssetsPath() throws Exception {
		if (assetsPath == null) {
			setAssetsPath(CatchupMathProperties.getInstance().getCatchupHome() + "/src/main/webapp/assets/images/");
		}
		return assetsPath;
	}

	public void setAssetsPath(String assetsPath) {
		this.assetsPath = assetsPath;
	}

	public String getWebPath() throws Exception {
		if (webPath == null) {
			setWebPath(CatchupMathProperties.getInstance().getSolutionBase());
		}
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	public void setUseDatabaseAccessor(boolean useDatabaseAccessor) {
		if (useDatabaseAccessor == true)
			setQuizResultsAccessor(new QuizResultDatabaseAccessor());
		else
			setQuizResultsAccessor(new QuizResultsFileSystemAccessor());
	}

	class SaveQuizResultsRunnable implements Runnable {

		private int runId;

    	public SaveQuizResultsRunnable(final int runId) {
    		this.runId = runId;
    	}

    	public void run() {
    		Connection conn = null;
    		ByteArrayOutputStream baos = null;

    		try {
    			conn = HMConnectionPool.getConnection();
    			
                HaTestRun testRun = HaTestRunDao.getInstance().lookupTestRun(runId);
                HaTest haTest = testRun.getHaTest();
                
                List<HaTestRunResult> results = testRun.getTestRunResults();

                HaTestSet testSet = new HaTestSet(conn, haTest.getPids());
                List<HaTestSetQuestion> questions = testSet.getQuestions();
                
    			Document document = new Document();
    			baos = new ByteArrayOutputStream();
    			PdfWriter.getInstance(document, baos);
    			document.open();

                for (HaTestSetQuestion question : questions) {
                    formatQuestion(question, results, document);
                }
                
                document.close();
                
                byte[] quizPDFbytes = baos.toByteArray();
                getQuizResultsAccessor().save(runId, quizPDFbytes);
    		}
    		catch (Exception e) {
    			LOGGER.error("*** Exception generating / saving Quiz results as PDF ***", e);
    		}
    		finally {
    			try { baos.close(); }
    			catch (Exception e) {}
    			SqlUtilities.releaseResources(null, null, conn);
    		}
    	}

        Parser parser = new Parser();
        Node root = null;
        boolean haveNewQuestion = false;
        boolean haveNewAnswer = false;
        int questionNumber = 0;
        int answerNumber = 0;
        int choiceNumber = -1;
        int paddingCount = 0;
        String[] answers = {"a. ", "b. ", "c. ", "d. ", "e. ", "f. ", "g. "};

		private void formatQuestion(HaTestSetQuestion question, final List<HaTestRunResult> results, final Document document) throws Exception {

			final String pid = question.getProblemIndex();
            final int selectedAnswer = getUserSelection(pid, results);
            final boolean isCorrect = selectedAnswer == question.getCorrectAnswer();
			
	        NodeVisitor visitor = new  NodeVisitor() {
	            public void visitTag(org.htmlparser.Tag tag) {
	                if(root == null)
	                    root = tag;
	                
	                String tn = tag.getTagName().toLowerCase();
	                String cl = tag.getAttribute("class");
	                try {
	                if(tn.equals("div") && cl != null && cl.equalsIgnoreCase("hm_question_def")) {
	                    haveNewQuestion = true;
	                    questionNumber++;
	                    answerNumber = 0;
	                    paddingCount = 0;
	                }
	                else if (tag.getTagName().equalsIgnoreCase("p")) {
	                	if (haveNewQuestion) {
	                		NodeList list = tag.getChildren();
	                		SimpleNodeIterator iter = list.elements();
	                		Paragraph p = new Paragraph();
	                		if (questionNumber > 1) {
	                			p.add(Chunk.NEWLINE);
	                		}
	                		p.add(new Chunk(String.format("%d. ", questionNumber), FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, new Color(0, 0, 0))));
	                		while (iter.hasMoreNodes()) {
	                			Node node = iter.nextNode();
	                			if (node instanceof TextNode) {
	                				String text = ((TextNode)node).getText();

                                    List<Chunk> chunks = processText(text);
                                    for(Chunk c : chunks) {
	                				    p.add(c);
                                    }
	                			}
	                			else if (node instanceof ImageTag) {
	                				ImageTag imgTag = (ImageTag)node;
	                				String width = imgTag.getAttribute("width");
	                				int height = Integer.valueOf(imgTag.getAttribute("height"));
	                				String cssClass = imgTag.getAttribute("class");
	                				String src = getWebPath() + imgTag.getAttribute("src");
	                				if (LOGGER.isDebugEnabled())
	                					LOGGER.debug(String.format("width: %s, height: %d, class: %s, src: %s",
	                						width, height, (cssClass!=null)?cssClass:"NULL", src));
	                				try {
	                					Image img = Image.getInstance(src);
	                					img.scalePercent(80);
	                					img.setAlignment(Image.TEXTWRAP);
	                					float vertOffset = 0;
	                					if (cssClass.toLowerCase().indexOf("middle") >= 0 ||
	                						cssClass.toLowerCase().indexOf("figure") >= 0) {
	                						vertOffset = -Float.valueOf(height)/3;
	                					}
	                					else if (cssClass.toLowerCase().indexOf("bottom") >= 0) {
		                					vertOffset = -2;
		                				}
	                					else if (cssClass.toLowerCase().indexOf("top") >= 0) {
		                					vertOffset = -4;
		                				}

    	                				Chunk c = new Chunk(img, 0, vertOffset);
    	                				p.add(c);
    	                				if (LOGGER.isDebugEnabled())
    	                					LOGGER.debug(String.format("vertOffset: %f", vertOffset));
	                				}
	                				catch (Exception e) {
	                					p.add(" (image '" + src + "' not availalble) " ); 
	                				}
	                			}
	                		}
                			document.add(p);
                			document.add(Chunk.NEWLINE);
	                		haveNewQuestion = false;
	                	}
	                	if (haveNewAnswer) {
	                		NodeList list = tag.getChildren();
	                		SimpleNodeIterator iter = list.elements();
	                		Paragraph p = new Paragraph();
	                		String srcBase = getAssetsPath();
	                		String src;
	                		float vertOffset = 0;
	                		if (answerNumber == selectedAnswer) {
	                			src = srcBase + ((isCorrect) ? "green-check-25x22.png" : "red-x-25x25.png");
	                			vertOffset = (isCorrect) ? -2 : -8;
	                		}
	                		else {
	                			src = srcBase + "blank-25x25.png";
	                		}
	                		Chunk m = new Chunk(Image.getInstance(src), 0, vertOffset);
	                		while (iter.hasMoreNodes()) {
	                			Node node = iter.nextNode();
	                			if (node instanceof TextNode) {
	                				String text = ((TextNode)node).getText();
	                				text = text.replaceAll("&ndash;", "-");
	                				text = text.replaceAll("&minus;", "-");
	                				if (LOGGER.isDebugEnabled())
	                					LOGGER.debug("text, paddingCount: " + text + ", " + paddingCount);
	                				while(paddingCount>0) {
	                					p.add(Chunk.NEWLINE);
	                					paddingCount = paddingCount - 2;
	                				}
	                				paddingCount = 0;
	    	                		p.add(m);
	                				StringBuilder sb = new StringBuilder(answers[answerNumber++]);
	                				sb.append(text);
	                				Chunk c = new Chunk(sb.toString(), FontFactory.getFont(FontFactory.COURIER, 12, Font.NORMAL, new Color(0, 0, 0)));
	    	                		p.add(c);
	                			}
	                			else if (node instanceof ImageTag) {
	                				ImageTag imgTag = (ImageTag)node;
	                				String width = imgTag.getAttribute("width");
	                				int height = Integer.valueOf(imgTag.getAttribute("height"));
	                				String cssClass = imgTag.getAttribute("class");
	                				src = getWebPath() + imgTag.getAttribute("src");
	                				if (LOGGER.isDebugEnabled())
	                					LOGGER.debug(String.format("width: %s, height: %d, class: %s, src: %s",
	                						width, height, (cssClass!=null)?cssClass:"NULL", src));
	                				try {
	                					Image img = Image.getInstance(src);
	                					img.scalePercent(80);
	                					img.setAlignment(Image.TEXTWRAP);
	                					vertOffset = 0;
	                					if (cssClass.toLowerCase().indexOf("middle") >= 0 ||
	                						cssClass.toLowerCase().indexOf("figure") >= 0) {
	                						vertOffset = -Float.valueOf(height)/3;
	                					}
	                					else if (cssClass.toLowerCase().indexOf("bottom") >= 0) {
		                					vertOffset = -2;
		                				}
	                					else if (cssClass.toLowerCase().indexOf("top") >= 0) {
		                					vertOffset = -4;
		                				}
    	                				paddingCount = 0;
    	                				while(height > 30) {
    	                					p.add(Chunk.NEWLINE);
    	                					height = height - 20;
    	                					paddingCount++;
    	                				}
    	                				Chunk t = new Chunk(answers[answerNumber++], FontFactory.getFont(FontFactory.COURIER, 12, Font.NORMAL, new Color(0, 0, 0)));
    	                				Chunk c = new Chunk(img, 0, vertOffset);
    	    	                		p.add(m);
    	    	                		p.add(t);
    	                				p.add(c);
    	                				p.add(Chunk.NEWLINE);
    	                				if (LOGGER.isDebugEnabled())
    	                					LOGGER.debug(String.format("vertOffset: %f, paddingCount: %d", vertOffset, paddingCount));
	                				}
	                				catch (Exception e) {
	                					p.add(" (image '" + src + "' not availalble) " ); 
	                				}
	                			}
	                		}
	            			document.add(p);
	            			document.add(Chunk.NEWLINE);
	                		haveNewAnswer = false;;
	                	}
	                	
	                }
	                else if(tag.getTagName().equalsIgnoreCase("ul")) {
	                    choiceNumber = 0;
	                }
	                else if(tag.getTagName().equalsIgnoreCase("li")) {
	                	haveNewAnswer = true;
	                }
	                }
	                catch (Exception e) {
	                	e.printStackTrace();
	                }
	            }

	        };

	        parser.setInputHTML(question.getQuestionHtml());
	        parser.visitAllNodesWith(visitor);
			
		}
    }
	
	private List<Chunk> processText(String text) {
		List<Chunk> chunks = new ArrayList<Chunk>();
		text = text.replaceAll("&ndash;", "-");
		text = text.replaceAll("&minus;", "-");
		text = text.replaceAll("&quot;", "\"");
		
		if ("?".equals(text)) text = " " + text;
		Chunk c = new Chunk(text, FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, new Color(0, 0, 0)));
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("text: " + text);
		
		chunks.add(c);
		
		c = new Chunk("\00B7", FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, new Color(0, 0, 0)));
		chunks.add(c);
		chunks.add(c);
		chunks.add(c);

		return chunks;
	}

	/** Return the users selection for the question for guid.  Return 
     * -1 if user has not made a selection.
     */
    private int getUserSelection(String guid, List<HaTestRunResult> results) {
        for(HaTestRunResult r: results) {
            if(r.getPid().equals(guid)) {
                if(r.isAnswered()) {
                    return r.getResponseIndex();
                }
            }
        }
        return -1;
    }

}

