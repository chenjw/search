package com.chenjw.search.constants;

import java.io.IOException;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class Constants {
    public static final Analyzer CHINESE_ANALYZER=new PaodingAnalyzer();
    
    public static void main(String[] args) throws IOException{
        String text="中国电信";
        TokenStream ts= CHINESE_ANALYZER.tokenStream("context", text);
        while(ts.incrementToken()){
            CharTermAttribute chars=ts.getAttribute(CharTermAttribute.class);
            System.out.println(chars.toString());
        }
    }
}
