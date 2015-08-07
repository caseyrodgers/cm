package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_core.client.model.SearchSuggestion;
import hotmath.gwt.cm_rpc.client.model.SpellCheckResults;
import hotmath.gwt.cm_rpc.client.rpc.SpellCheckAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.RAMDirectory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class SpellCheckCommand implements ActionHandler<SpellCheckAction, SpellCheckResults> {

    private static final int MAX_SUGGETIONS = 50;
    private SpellChecker _spellChecker;
    private EnglishAnalyzer _analyzer;

    public SpellCheckCommand() throws Exception {
        String rtPathStr = CatchupMathProperties.getInstance().getCatchupRuntime();

        // Path wordFile = FileSystems.getDefault().getPath(rtPathStr,
        // "spellcheck_words.txt");
        Path wordFile = FileSystems.getDefault().getPath(rtPathStr, "spellcheck_words.txt");

        RAMDirectory spellCheckDir = new RAMDirectory();

        _spellChecker = new SpellChecker(spellCheckDir);

        _analyzer = new EnglishAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(_analyzer);
        _spellChecker.indexDictionary(new PlainTextDictionary(wordFile), config, true);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SpellCheckAction.class;
    }

    @Override
    public SpellCheckResults execute(Connection conn, SpellCheckAction action) throws Exception {
        
        Source source=new Source(action.getText());
        String renderedText=source.getRenderer().toString();

        String noVariablesText = renderedText.replaceAll("\\$.*[^a-zA-Z0-9]",  "");
        
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://service.afterthedeadline.com/checkDocument");
        List nameValuePairs = new ArrayList(1);
        nameValuePairs.add(new BasicNameValuePair("key", "se2"));
        nameValuePairs.add(new BasicNameValuePair("data", noVariablesText));
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer results = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            results.append(line);
        }

        SAXBuilder builder = new SAXBuilder(false);
        SpellCheckResults spellResults = new SpellCheckResults();
        try {
            Document document = (Document) builder.build(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + results.toString()));
            Element el = document.getRootElement();
            List errors = el.getChildren("error");
            for(int i=0;i<errors.size();i++) {
                Element errorEl = (Element)errors.get(i);
                if(errorEl.getChildText("description").equals("Spelling")) {
                      
                    String word = errorEl.getChild("string").getValue();
                    boolean isDup=false;
                    for(SearchSuggestion sp: spellResults.getCmList()) {
                        
                        if(sp.getWord().equals(word)) {
                            isDup=true; // skip
                            break;
                        }
                        
                    }
                    
                    if(!isDup) {
                        List<String> options = new ArrayList<String>();
                        List suggestions = errorEl.getChildren("suggestions");
                        for(int j=0;j<suggestions.size();j++) {
                            Element sugEl = (Element)suggestions.get(j);
                                            
                            List opts = sugEl.getChildren("option");
                            for(int o=0;o<opts.size();o++) {
                                String sug = sugEl.getValue();
                                options.add(sug.trim());
                            }
                        }
                        spellResults.getCmList().add(new SearchSuggestion(word, options)); 
                    }
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        
        return spellResults;
    }

}
