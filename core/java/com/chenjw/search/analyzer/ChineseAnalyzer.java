/**
 * IK 中文分词  版本 5.0.1
 * IK Analyzer release 5.0.1
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 */
package com.chenjw.search.analyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 中文分词
 * 
 * @author chenjw
 * @version $Id: ChineseAnalyzer.java, v 0.1 2014年3月9日 下午8:28:27 chenjw Exp $
 */
public class ChineseAnalyzer extends IKAnalyzer{
	
    
    static{
        String testinput = "aaa fooaaa baraaa bazaaa GB gib gigabytegigabytes";
        Version ver=Version.LUCENE_47;
        String synfile="synonyms.txt";
        Map<String,String> filterargs=new HashMap<String, String>();
        filterargs.put("luceneMatchVersion",ver.toString());
        filterargs.put("synonyms", synfile);
        filterargs.put("ignoreCase", "true");
        filterargs.put("format", "solr");
        filterargs.put("expand", "false");
        SynonymFilterFactory factory= new SynonymFilterFactory(filterargs);

        try {
            factory.inform(new FilesystemResourceLoader());
        } catch (IOException e) {
            //logger.error("", e);
        }
    }
}
