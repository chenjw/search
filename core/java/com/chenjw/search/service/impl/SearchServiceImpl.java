package com.chenjw.search.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.InitializingBean;

import com.chenjw.search.constants.Constants;
import com.chenjw.search.model.SearchHit;
import com.chenjw.search.service.SearchService;

public class SearchServiceImpl implements SearchService, InitializingBean {

    private IndexSearcher searcher;

    private QueryParser   parser;

    private IndexSearcher suggestSearcher;

    public List<SearchHit> search(String word) {
        List<SearchHit> r = new ArrayList<SearchHit>();
        Query query;
        try {
            query = parser.parse(word);

            TopDocs topDocs = searcher.search(query, null, 10);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                SearchHit h = new SearchHit();
                h.setPublicId(doc.get("publicId"));
                h.setName(doc.get("publicName"));
                h.setKeywords(doc.get("keywords"));
                h.setServiceArea(doc.get("serviceArea"));
                r.add(h);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public List<String> suggest(String word) {
        List<String> r = new ArrayList<String>();
        Query query;
        try {
            query = new PrefixQuery(new Term("keywords", word));
            
            TopDocs topDocs = suggestSearcher.search(query, null, 5);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = suggestSearcher.doc(scoreDoc.doc);
                r.add(doc.get("publicName"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
            "/home/chenjw/test/search/index")));
        searcher = new IndexSearcher(reader);

        parser = new MultiFieldQueryParser(Version.LUCENE_47, new String[] { "keywords" },
            Constants.CHINESE_ANALYZER);

        IndexReader suggestReader = DirectoryReader.open(FSDirectory.open(new File(
            "/home/chenjw/test/search/suggest")));
        suggestSearcher = new IndexSearcher(suggestReader);

    }

}
