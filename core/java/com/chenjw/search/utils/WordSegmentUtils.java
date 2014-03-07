package com.chenjw.search.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.chenjw.search.constants.Constants;

public class WordSegmentUtils {

    /**
     * 中文分词
     * 
     * @param text
     * @return
     */
    public static List<String> chineseSegment(String text) {
        List<String> r=new ArrayList<String>();
        try {
            TokenStream ts = Constants.CHINESE_ANALYZER.tokenStream(null, text);
            ts.reset();
            while (ts.incrementToken()) {
                CharTermAttribute chars = ts.getAttribute(CharTermAttribute.class);
                r.add(chars.toString());
            }
            ts.close();
        } catch (IOException e) {
           e.printStackTrace();
        }
        return r;
    }
    
    public static void main(String[] args) throws IOException{
        String text="中国电信";
        List<String> r=chineseSegment(text);
        for(String s:r){
            System.out.println(s);
        }
    }

}
