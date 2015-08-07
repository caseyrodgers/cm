package hotmath.search;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_core.client.model.SearchSuggestion;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.RAMDirectory;

import sb.client.SbTesterFrameGeneric;
import sb.util.SbException;
import sb.util.SbTestImpl;

public class SearchSuggest {

    SpellChecker _spellChecker;
    EnglishAnalyzer _analyzer;
    int MAX_SUGGETIONS = 5;

    static Logger __logger = Logger.getLogger(SearchSuggest.class);
    
    public SearchSuggest() throws Exception {

        __logger.info("Initializing");
        String rtPathStr = CatchupMathProperties.getInstance().getCatchupRuntime();
        
        // Path wordFile = FileSystems.getDefault().getPath(rtPathStr, "spellcheck_words.txt");
        Path wordFile = FileSystems.getDefault().getPath(rtPathStr, "spellcheck_phrases.txt");

        RAMDirectory spellCheckDir = new RAMDirectory();
        
        _spellChecker = new SpellChecker(spellCheckDir);

        _analyzer = new EnglishAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(_analyzer);
        _spellChecker.indexDictionary(new PlainTextDictionary(wordFile), config, true);
        
        __logger.info("Complete");
    }


    public List<SearchSuggestion> getSuggestions(String suggestFor) throws IOException {
        String[] suggestions = _spellChecker.suggestSimilar(suggestFor, MAX_SUGGETIONS);
        List<SearchSuggestion> suggestList = new ArrayList<SearchSuggestion>();
        for(int i=0;i<suggestions.length;i++) {
            suggestList.add(  new SearchSuggestion(suggestions[i],null));
        }
        return suggestList;
    }
    
    
    static public void main(String as[]) {
        try {
            new SbTesterFrameGeneric(new SbTestImpl() {
                SearchSuggest _suggest = new SearchSuggest();
                
                @Override
                public void doTest(Object objLogger, String sFromGUI) throws SbException {
                    try {
                        System.out.println("----");
                        System.out.println(_suggest.getSuggestions(sFromGUI));
                        System.out.println("----");
                    }
                    catch(Exception e) {
                        throw new SbException(e, "Error");
                    }
                }
            });
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}