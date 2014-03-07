package com.chenjw.search.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.chenjw.search.service.IndexService;
import com.chenjw.search.utils.PinyinUtils;
import com.chenjw.search.utils.WordSegmentUtils;
import com.csvreader.CsvReader;

public class IndexServiceImpl implements IndexService {

    private static final Version VERSION = Version.LUCENE_47;
    private static Analyzer      analyzer;

    static {
        //        Map<String, Analyzer> map = new HashMap<String, Analyzer>();
        //        map.put("pinyin", new PininAnalyzer(VERSION));
        //        map.put("pinyinFl", new PininFirstLetterAnalyzer(VERSION));
        //        map.put("keywords", Constants.CHINESE_ANALYZER);
        //        analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(VERSION), map);
        analyzer = new SimpleAnalyzer(VERSION);

    }

    public static void main(String[] args) {
        IndexServiceImpl s = new IndexServiceImpl();
        s.index();
    }

    private void indexSearch() {
        try {
            Directory dir;

            dir = FSDirectory.open(new File("/home/chenjw/test/search/index"));

            IndexWriterConfig iwc = new IndexWriterConfig(VERSION, analyzer);

            iwc.setOpenMode(OpenMode.CREATE);

            //iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            IndexWriter writer = new IndexWriter(dir, iwc);
            File f = new File("/home/chenjw/my_workspace/search/core/resources/测试环境公众号.csv");
            CsvReader csvReader = new CsvReader(new FileInputStream(f), Charset.forName("GBK"));
            while (csvReader.readRecord()) {
                String publicId = csvReader.get(0);
                String publicName = csvReader.get(1);
                String serviceArea = csvReader.get(2);
                String greeting = csvReader.get(3);
                String publicDesc = csvReader.get(4);
                Document doc = new Document();

                doc.add(new LongField("modified", f.lastModified(), Field.Store.YES));
                doc.add(new StringField("publicId", publicId, Field.Store.YES));
                doc.add(new StringField("publicName", publicName, Field.Store.YES));
                doc.add(new StringField("serviceArea", serviceArea, Field.Store.YES));
                Set<String> keywords = new HashSet<String>();
                List<String> words = WordSegmentUtils.chineseSegment(publicName);
                keywords.addAll(words);
                for (String word : words) {
                    // 拼音
                    keywords.add(PinyinUtils.toPinyin(word));
                    // 拼音首字母
                    keywords.add(PinyinUtils.toPinyinFirstLetter(word));
                }
                /// 名称
                keywords.add(PinyinUtils.toPinyin(publicName));
                keywords.add(PinyinUtils.toPinyinFirstLetter(publicName));
                /// 地区
                keywords.add(PinyinUtils.toPinyin(serviceArea));
                keywords.add(PinyinUtils.toPinyinFirstLetter(serviceArea));
                keywords.add(serviceArea);
                ///
                doc.add(new TextField("keywords", StringUtils.join(keywords, " "), Field.Store.YES));
                writer.addDocument(doc);

                System.out.println(csvReader.get(0));
            }
            writer.close();
            //        LineIterator lineIterator = FileUtils.lineIterator(f, "GBK");
            //        while (lineIterator.hasNext()) {
            //            String line = lineIterator.nextLine();
            //            Document doc = new Document();
            //            doc.add(new StringField("path", f.getPath(), Field.Store.YES));
            //            doc.add(new LongField("modified", f.lastModified(), Field.Store.NO));
            //            doc.add(new TextField("pinyin", line, Field.Store.YES));
            //            doc.add(new TextField("pinyinFl", line, Field.Store.YES));
            //            doc.add(new TextField("contents", line, Field.Store.YES));
            //            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
            //                System.out.println("adding " + file);
            //                writer.addDocument(doc);
            //            } else {
            //                System.out.println("updating " + file);
            //                writer.updateDocument(new Term("path", file.getPath()), doc);
            //            }
            //        }
            //        writer.close();

            System.out.println("finished!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void indexSuggest() {
        try {
            Directory dir;

            dir = FSDirectory.open(new File("/home/chenjw/test/search/suggest"));

            IndexWriterConfig iwc = new IndexWriterConfig(VERSION, analyzer);

            iwc.setOpenMode(OpenMode.CREATE);

            //iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            IndexWriter writer = new IndexWriter(dir, iwc);
            File f = new File("/home/chenjw/my_workspace/search/core/resources/测试环境公众号.csv");
            CsvReader csvReader = new CsvReader(new FileInputStream(f), Charset.forName("GBK"));
            while (csvReader.readRecord()) {
                String publicId = csvReader.get(0);
                String publicName = csvReader.get(1);
                String serviceArea = csvReader.get(2);
                String greeting = csvReader.get(3);
                String publicDesc = csvReader.get(4);
                Document doc = new Document();

                doc.add(new LongField("modified", f.lastModified(), Field.Store.YES));
                doc.add(new StringField("publicId", publicId, Field.Store.YES));
                doc.add(new StringField("publicName", publicName, Field.Store.YES));
                doc.add(new StringField("serviceArea", serviceArea, Field.Store.YES));
                Set<String> keywords = new HashSet<String>();
                for (int i = 0; i < publicName.length(); i++) {
                    String str = StringUtils.substring(publicName, i, publicName.length());
                    keywords.add(str);
                    keywords.add(PinyinUtils.toPinyin(str));
                    keywords.add(PinyinUtils.toPinyinFirstLetter(str));
                }

                /// 名称
                ///

                System.out.println(publicName);
                System.out.println(StringUtils.join(keywords, " "));
                doc.add(new TextField("keywords", StringUtils.join(keywords, " "), Field.Store.YES));

                writer.addDocument(doc);

                System.out.println(csvReader.get(0));
            }
            writer.close();
            //        LineIterator lineIterator = FileUtils.lineIterator(f, "GBK");
            //        while (lineIterator.hasNext()) {
            //            String line = lineIterator.nextLine();
            //            Document doc = new Document();
            //            doc.add(new StringField("path", f.getPath(), Field.Store.YES));
            //            doc.add(new LongField("modified", f.lastModified(), Field.Store.NO));
            //            doc.add(new TextField("pinyin", line, Field.Store.YES));
            //            doc.add(new TextField("pinyinFl", line, Field.Store.YES));
            //            doc.add(new TextField("contents", line, Field.Store.YES));
            //            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
            //                System.out.println("adding " + file);
            //                writer.addDocument(doc);
            //            } else {
            //                System.out.println("updating " + file);
            //                writer.updateDocument(new Term("path", file.getPath()), doc);
            //            }
            //        }
            //        writer.close();

            System.out.println("finished!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void index() {
        indexSearch();
        indexSuggest();
    }
}
