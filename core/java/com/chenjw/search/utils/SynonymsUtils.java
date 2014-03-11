package com.chenjw.search.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import com.chenjw.search.constants.Constants;

public class SynonymsUtils {
    private static final String          SYNONYMS_PATH = Constants.dicPath + "synonym/";
    private static Map<String, String[]> SYNONYMS_MAP  = new HashMap<String, String[]>();
    static {
        readDics();
    }

    private static void readDics() {
        // 读取字典
        Iterator<File> fi=FileUtils.iterateFiles(new File(SYNONYMS_PATH), new String[] { "dic" }, true);
        while(fi.hasNext()){
            File f=fi.next();
            try {
                readDic(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    
    private static void readDic(File f) throws IOException{
        LineIterator li=FileUtils.lineIterator(f, "UTF-8");
        while(li.hasNext()){
            String line=li.nextLine();
            if(line==null){
                continue;
            }
            line=line.trim();
            // 去掉注释
            if(line.startsWith("#")){
                continue;
            }
            if(line.contains("=>")){
                String shortWords=StringUtils.substringBefore(line, "=>").trim();
                String forWords=StringUtils.substringAfter(line, "=>").trim();
                String[] strs=SYNONYMS_MAP.get(forWords);
                if(strs==null){
                    strs=new String[]{shortWords};
                }
                else{
                    String[] newStrs=Arrays.copyOf(strs, strs.length+1);
                    newStrs[strs.length]=shortWords;
                    strs=newStrs;
                }
                SYNONYMS_MAP.put(forWords, strs);
            }
            
        }
    }

    /**
     * 根据一个词找到的同义词
     * 
     * @param text
     * @return
     */
    public static String[] getSynonyms(String text) {
        return SYNONYMS_MAP.get(text);
    }
    
    public static void main(String[] args){
        System.out.println(SynonymsUtils.getSynonyms("12306")[0]);
    }
}
