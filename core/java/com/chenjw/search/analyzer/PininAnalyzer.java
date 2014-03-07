package com.chenjw.search.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.util.Version;

import com.chenjw.search.constants.Constants;
import com.chenjw.search.filter.PininFilter;

public class PininAnalyzer extends AnalyzerWrapper {

    private Version matchVersion;

    @SuppressWarnings("deprecation")
    public PininAnalyzer(Version matchVersion) {
        this.matchVersion = matchVersion;

    }

    @Override
    protected TokenStreamComponents wrapComponents(String fieldName,
                                                   TokenStreamComponents components) {
        return new org.apache.lucene.analysis.Analyzer.TokenStreamComponents(
            components.getTokenizer(), new PininFilter(matchVersion, components.getTokenStream()));
    }

    @Override
    protected Analyzer getWrappedAnalyzer(String s) {
        return Constants.CHINESE_ANALYZER;
    }

}
