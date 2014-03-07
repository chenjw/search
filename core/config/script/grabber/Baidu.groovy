

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.chenjw.imagegrab.httpclient.HttpClient
import com.chenjw.imagegrab.spi.Grabber
import com.chenjw.imagegrab.spi.impl.GrabberTemplate
import com.chenjw.imagegrab.ui.DataHandler



public class Baidu extends GrabberTemplate implements Grabber {
    private int pageSize=60;

    /**
     * 邮箱抓取任务接口
     */
    def HttpClient httpClient;

    private class Counter{
        AtomicLong success=new AtomicLong(0);
        AtomicLong processing=new AtomicLong(0);
        AtomicLong processed=new AtomicLong(0);
        boolean finished=false;
        long start=System.currentTimeMillis();
        
    }

    protected void doGrabPage(Counter counter,DataHandler dataHandler,int pageNum){

        String searchWord=dataHandler.getSearchWord();
        String folder=dataHandler.getSource()+"_"+dataHandler.getSearchWord();
        int startNum=pageSize*pageNum+1;
        def url='http://image.baidu.com/i?tn=resultjson_com&ipn=rj&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1393672503661_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=gbk&word='+URLEncoder.encode(searchWord,"GBK")+'&oe=utf-8&rn='+pageSize+'&pn='+startNum+'&146645149949.11734&1063378017322.7675';
        def header=['Referer':'http://image.baidu.com/i?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=index&fr=&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=%E5%B0%8F%E7%A5%A8'];
        String content=httpClient.get('1', url, null, header,null);
        try{
            content=StringUtils.remove(content,'\\\'');
            Map<String,Object> result=JSON.parseObject(content,Map.class);
            JSONArray list=result.get("data");
            for(JSONObject item:list){
                def objURL=item.get("objURL");
                if(objURL==null){
                    continue;
                }
                objURL=uncompile(objURL);
                String fileName=getFileName(objURL);
                // 已存在了
                File f=this.getFileExist(fileName, folder);
                if(f!=null){
                    dataHandler.appendResult("已存在 "+f.getAbsolutePath()+"\n");
                    continue;
                }
                counter.processing.incrementAndGet();
                execute(new Runnable(){
                            public void run(){
                                doProcessImage(counter,dataHandler,objURL,fileName,folder);
                            }
                        });
            }
        }
        catch(Exception e){
            dataHandler.appendResult(content+"\n");
            e.printStackTrace();
        }
        
    }

    private String getFileName(String objURL){
        def fileName=StringUtils.replaceEach(objURL, [
            '/',
            '\\',
            ':',
            '<',
            '>',
            '*',
            '?',
            '"',
            '|'] as String[], [
            '',
            '',
            '',
            '',
            '',
            '',
            '',
            '',
            ''] as String[]);

        if(!fileName.endsWith('.jpg')){
            fileName+='.jpg';
        }
        return fileName;
    }

    private void checkCounter( Counter counter,DataHandler dataHandler){
        if(!counter.finished){
            return;
        }
        if(counter.getProcessed().get()<counter.getProcessing().get()){
            return;
        }
        dataHandler.appendResult("下载完成，共找到 "+counter.success.get()+" 张图片，用时 "+(System.currentTimeMillis()-counter.start)/1000+" 秒\n");
    }
    
    protected void doGrab(DataHandler dataHandler){
        int maxNum=Integer.parseInt(dataHandler.getMaxNum());
        int maxPage=maxNum/pageSize+1;
        Counter counter=new Counter();
        try{
            for(int i=0;i<maxPage;i++){
                doGrabPage(counter,dataHandler,i);
            }
        }
        finally{
            counter.finished=true;
            checkCounter(counter,dataHandler);
        }
    }

    private void doProcessImage(Counter counter,DataHandler dataHandler,String url,String fileName,String folder){
       
        boolean success=false;
        try{
            def header=['Referer':url];
            byte[] bytes=httpClient.getBytes("1", url, null,header, null);
            if(bytes==null || bytes.length<2048){
                return;
            }
            File f=this.saveFile(fileName, folder, bytes);
            dataHandler.appendResult(f.getAbsolutePath()+"\n");
            success=true;
           
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(success){
                counter.success.incrementAndGet();
            }
            counter.processed.incrementAndGet();
            checkCounter(counter,dataHandler);
        }
    }

    public String id(){
        return 'baidu';
    }

    public String name(){
        return '百度搜索';
    }

    def d = [
        'w': 'a',
        'k': 'b',
        'v': 'c',
        '1': 'd',
        'j': 'e',
        'u': 'f',
        '2': 'g',
        'i': 'h',
        't': 'i',
        '3': 'j',
        'h': 'k',
        's': 'l',
        '4': 'm',
        'g': 'n',
        '5': 'o',
        'r': 'p',
        'q': 'q',
        '6': 'r',
        'f': 's',
        'p': 't',
        '7': 'u',
        'e': 'v',
        'o': 'w',
        '8': '1',
        'd': '2',
        'n': '3',
        '9': '4',
        'c': '5',
        'm': '6',
        '0': '7',
        'b': '8',
        'l': '9',
        'a': '0'
    ];

    private String uncompile(String objURL){
        if (objURL==null || objURL.startsWith('http')) {
            return objURL
        }
        objURL=StringUtils.replace(objURL, '_z2C$q', ':')
        objURL=StringUtils.replace(objURL, '_z&e3B', '.')
        objURL=StringUtils.replace(objURL, 'AzdH3F', '/')
        StringBuffer sb=new StringBuffer();
        for(def c:objURL.toCharArray()){
            def a=d[String.valueOf(c)];
            if(a==null){
                sb.append(c);
            }
            else{
                sb.append(a);
            }
        }
        objURL=sb.toString();
        return objURL;
    }
}
