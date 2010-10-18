package hotmath.gwt.cm_activity.server.rpc;

import hotmath.gwt.cm_activity.client.model.WordProblem;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;

import sb.util.SbFile;

public class WordProblemsParser {
    
    public WordProblemsParser(){}
    
    public List<WordProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<WordProblem> problems) {
        this.problems = problems;
    }

    List<WordProblem> problems = new ArrayList<WordProblem>();

    public void addWordProblem(WordProblem wp) {
        problems.add(wp);
    }
    
    static public WordProblemsParser parseXml(File file) throws Exception {
        return parseXml( new SbFile(file).getFileContents().toString("\n"));
    }
    
    static public WordProblemsParser parseXml(String xml) throws Exception {

        try {
            Digester digester = new Digester();
    
            digester.setValidating(false);
            digester.addObjectCreate("data", "hotmath.gwt.cm_activity.server.rpc.WordProblemsParser");
            digester.addObjectCreate("data/item", "hotmath.gwt.cm_activity.client.model.WordProblem");
            digester.addSetNext("data/item", "addWordProblem");
            digester.addSetProperties("data/item/answer"); 
            digester.addBeanPropertySetter("data/item/question");
            digester.addBeanPropertySetter("data/item/explanation");
            digester.addBeanPropertySetter("data/item/answer");
    
            StringReader reader = new StringReader(xml);
    
            Object o = digester.parse(reader);
            if(o == null)
                throw new Exception("Could not create HelpReferenceLinks");
    
            return (WordProblemsParser) o;
        }
        catch(Exception e) {
            throw e;
        }
    }
    
    static public void main(String as[]) {
        try {
            String xml = "<data><item><question>question</question><answer vars='a,b,c'>answer</answer><explaination>explaination</explaination></item></data>";
            WordProblemsParser.parseXml(xml);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
