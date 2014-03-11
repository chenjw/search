package com.chenjw.search.constants;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Constants {

    public static final Version LUCENE_VERSION = Version.LUCENE_47;
    public static String dicPath="dic/";
    
    public static final Analyzer CHINESE_ANALYZER=new IKAnalyzer();
    //public static final Analyzer CHINESE_ANALYZER=new MMSegAnalyzer();
    public static void main(String[] args) throws IOException{
        String text="bbb aaa";
        TokenStream ts= CHINESE_ANALYZER.tokenStream("context", text);
        
        ts.reset();
        while(ts.incrementToken()){
            CharTermAttribute chars=ts.getAttribute(CharTermAttribute.class);
            System.out.println("--  "+chars.toString());
        }
        ts.close();
    }
}
