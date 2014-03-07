package com.chenjw.search;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.Version;

import com.chenjw.search.analyzer.PininAnalyzer;
import com.chenjw.search.analyzer.PininFirstLetterAnalyzer;
import com.chenjw.search.constants.Constants;
import com.chenjw.search.filter.PininFilter;
import com.chenjw.search.filter.PininFirstLetterFilter;

public class Index {
    private static final Version VERSION  = Version.LUCENE_47;
    private static Analyzer     analyzer;
  
    static{
        Map<String,Analyzer> map=new HashMap<String,Analyzer>();
        map.put("pinyin", new PininAnalyzer(VERSION));
        map.put("pinyinFl", new PininFirstLetterAnalyzer(VERSION));
        map.put("contents", Constants.CHINESE_ANALYZER);
        analyzer= new PerFieldAnalyzerWrapper(Constants.CHINESE_ANALYZER,map);

    }
    public static void main(String[] args) throws IOException,
                                          BadHanyuPinyinOutputFormatCombination {
        Directory dir = FSDirectory.open(new File("/home/chenjw/test/search/index"));

        testAnalyzer(analyzer);
        IndexWriterConfig iwc = new IndexWriterConfig(VERSION, analyzer);

        iwc.setOpenMode(OpenMode.CREATE);

        //iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

        IndexWriter writer = new IndexWriter(dir, iwc);
        indexDocs(writer, new File("/home/chenjw/test/search/doc"));

        writer.close();

        System.out.println("finished!");

    }

    private static TokenStream toPintinFirstLetterTokenStream(String key, String value) throws IOException {
        TokenStream ts = analyzer.tokenStream(key, value);
        ts = new PininFirstLetterFilter(VERSION, ts);
        ts.reset();
        return ts;
    }
    
    private static TokenStream toPintinTokenStream(String key, String value) throws IOException {
        TokenStream ts = analyzer.tokenStream(key, value);
        ts = new PininFilter(VERSION, ts);
        ts.reset();
        return ts;
    }

    private static void testAnalyzer(Analyzer analyzer) throws IOException,
                                                       BadHanyuPinyinOutputFormatCombination {
        TokenStream ts = analyzer.tokenStream("pinyinFl", "中国移动");
        ts.reset();
        while (ts.incrementToken()) {
            Iterator<AttributeImpl> iterator = ts.getAttributeImplsIterator();
            while (iterator.hasNext()) {
                AttributeImpl attr = iterator.next();
                if (attr instanceof CharTermAttribute) {
                    CharTermAttribute a = (CharTermAttribute) attr;
                    String text = a.toString();
                    //String pintin = toPinyin(text);
                    System.out.println(text);
                }
            }
        }
        ts.close();
    }

    static void indexDocs(IndexWriter writer, File file) throws IOException {
        Iterator<File> iterator = FileUtils.iterateFiles(file, new String[] { "txt" }, true);
        while (iterator.hasNext()) {
            File f = iterator.next();
            if (f.isDirectory()) {
                continue;
            }
            LineIterator lineIterator = FileUtils.lineIterator(f, "GBK");
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                Document doc = new Document();
                doc.add(new StringField("path", f.getPath(), Field.Store.YES));
                doc.add(new LongField("modified", f.lastModified(), Field.Store.NO));
                doc.add(new TextField("pinyin",line,Field.Store.YES ));
                doc.add(new TextField("pinyinFl",line,Field.Store.YES ));
                doc.add(new TextField("contents",line,Field.Store.YES ));
                if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                    System.out.println("adding " + file);
                    writer.addDocument(doc);
                } else {
                    System.out.println("updating " + file);
                    writer.updateDocument(new Term("path", file.getPath()), doc);
                }
            }
        }
    }
}
