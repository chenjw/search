package com.chenjw.search.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.chenjw.search.model.SearchHit;
import com.chenjw.search.service.SearchService;
import com.chenjw.search.service.impl.SearchServiceImpl;

public class HttpServer implements InitializingBean {
    private SearchService searchService = new SearchServiceImpl();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start(new HttpRequestHandler() {

            @Override
            public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                                                                                               throws HttpException,
                                                                                               IOException {

                String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
                //get uri  
                String target = request.getRequestLine().getUri();
                if (method.equals("GET") || method.equals("POST")) {
                    if (target.startsWith("/search?")) {
                        response.setStatusCode(HttpStatus.SC_OK);
                        String kvps = StringUtils.substringAfterLast(target, "?");
                        String word = null;
                        for (String kvp : StringUtils.split(kvps, "&")) {
                            if (kvp.startsWith("w=")) {
                                word = StringUtils.substringAfter(kvp, "w=");
                                word = URLDecoder.decode(word, "UTF-8");
                            }
                        }

                        List<SearchHit> r = searchService.search(word);
                        StringEntity entity = new StringEntity(JSON.toJSONString(r, true),Charset.forName("GBK"));
                        response.setEntity(entity);
                    } else if (target.startsWith("/suggest?")) {
                        response.setStatusCode(HttpStatus.SC_OK);
                        String kvps = StringUtils.substringAfterLast(target, "?");
                        String word = null;
                        for (String kvp : StringUtils.split(kvps, "&")) {
                            if (kvp.startsWith("w=")) {
                                word = StringUtils.substringAfter(kvp, "w=");
                                word = URLDecoder.decode(word, "UTF-8");
                            }
                        }

                        List<SearchHit> r = searchService.search(word);
                        StringEntity entity = new StringEntity(JSON.toJSONString(r, true),Charset.forName("GBK"));
                        response.setEntity(entity);
                    }

                } else {
                    throw new MethodNotSupportedException(method + " method not supported");
                }
            }

        }, 8080);
    }

    public static void main(String[] args) throws Exception {
        new HttpServer().afterPropertiesSet();
    }

    public void start(HttpRequestHandler handler, int port) throws IOException {
        Thread t = new RequestListenerThread(handler, port);
        t.setDaemon(false);
        t.start();
    }

    static class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams   params;
        private final HttpService  httpService;

        public RequestListenerThread(HttpRequestHandler handler, int port) throws IOException {
            //   
            this.serversocket = new ServerSocket(port);

            // Set up the HTTP protocol processor  
            HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] {
                    new ResponseDate(), new ResponseServer(), new ResponseContent(),
                    new ResponseConnControl() });

            this.params = new BasicHttpParams();
            this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // Set up request handlers  
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            reqistry.register("*", handler); //WebServiceHandler用来处理webservice请求。  

            this.httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory());
            httpService.setParams(this.params);
            httpService.setHandlerResolver(reqistry); //为http服务设置注册好的请求处理器。  

        }

        @Override
        public void run() {
            System.out.println("Listening on port " + this.serversocket.getLocalPort());
            System.out.println("Thread.interrupted = " + Thread.interrupted());
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection  
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    System.out.println("Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    // Start worker thread  
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    break;
                } catch (IOException e) {
                    System.err.println("I/O error initialising connection thread: "
                                       + e.getMessage());
                    break;
                }
            }
        }
    }

    static class WorkerThread extends Thread {

        private final HttpService          httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        @Override
        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

}