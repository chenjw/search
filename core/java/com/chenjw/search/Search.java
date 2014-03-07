package com.chenjw.search;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.chenjw.search.constants.Constants;

public class Search {

    public static void main(String[] args) throws Exception {

        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
            "/home/chenjw/test/search/index")));
        IndexSearcher searcher = new IndexSearcher(reader);
        // :Post-Release-Update-Version.LUCENE_XY:
        Analyzer analyzer = Constants.CHINESE_ANALYZER;

        String words = "zgyd";
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_47,new String[]{"contents", "pinyin", "pinyinFl"},analyzer);
        
        //QueryParser parser = new QueryParser(Version.LUCENE_47, "contents", analyzer);

        Query query = parser.parse(words);

        TopDocs topDocs = searcher.search(query, null, 100);
 
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("contents"));
        }

        reader.close();
    }

}
