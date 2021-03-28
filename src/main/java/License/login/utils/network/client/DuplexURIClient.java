package License.login.utils.network.client;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * uri api를 1개~n개까지 입력한후 접속가능한쪽에 전달하는 클라이언트
 * <p>
 * host, port, api가 합쳐진 uri를 입력으로 사용
 * <p>
 * 전체적으로 한바퀴 돌고나서도 없으면 Exception반환.
 * 즉. 등록한 모든 대상이 반응이 없다면 예외 반환함
 */
public class DuplexURIClient {
    private static Logger logger = LoggerFactory.getLogger(DuplexURIClient.class);
    private Queue<String> uriList;
    private int timeoutSec;
    private HttpClient httpClient = HttpClientManager.getHttpClient();
    private DuplexMode duplexMode;

    public DuplexURIClient(List<String> uriList, DuplexMode duplexMode, int timeoutSec) {
        this.duplexMode = duplexMode;
        this.uriList = new LinkedList(uriList);
        this.timeoutSec = timeoutSec;
    }

    public String get(Map<String, String> params) throws IOException {
        return get(params, 0);
    }


    private String get(Map<String, String> params, int count) throws IOException {
        if (count >= uriList.size()) {
            throw new IOException("connection fail");
        }
        String uri = uriList.peek();
        return get(uri, params, count);
    }

    private String get(String uri, Map<String, String> params, int count) throws IOException {
        Request httpRequest = httpClient.newRequest(uri)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .method(HttpMethod.GET);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        try {
            ContentResponse response = httpRequest.send();
            return response.getContentAsString();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            reorderHostAndPorts();
            return get(params, count + 1);
        }
    }

    public String post(Map<String, String> params, String body) throws IOException {
        String uri = uriList.peek();
        return post(uri, params, body, 0);
    }

    private String post(Map<String, String> params, String body, int count) throws IOException {
        if (duplexMode==DuplexMode.SINGLE && count >= uriList.size()) {
            throw new IOException("connection fail");
        }

        String uri = uriList.peek();
        return post(uri, params, body, count);
    }

    private String post(String uri, Map<String, String> params, String body, int count) throws IOException {

        Request httpRequest = httpClient.newRequest(uri)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .method(HttpMethod.POST)
                .content(new StringContentProvider(body), "application/json");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        try {
            ContentResponse response = httpRequest.send();
            return response.getContentAsString();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            reorderHostAndPorts();
            return post(params, body, count + 1);
        }

    }

    public String put(Map<String, String> params, String body) throws IOException {

        String uri = uriList.peek();
        return put(uri, params, body, 0);
    }

    private String put(Map<String, String> params, String body, int count) throws IOException {
        if (duplexMode==DuplexMode.SINGLE && count >= uriList.size()) {
            throw new IOException("connection fail");
        }
        String uri = uriList.peek();
        return put(uri, params, body, count);
    }

    private String put(String uri, Map<String, String> params, String body, int count) throws IOException {

        Request httpRequest = httpClient.newRequest(uri)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .method(HttpMethod.PUT)
                .content(new StringContentProvider(body), "application/json");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        try {
            ContentResponse response = httpRequest.send();
            return response.getContentAsString();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            reorderHostAndPorts();
            return put(params, body, count + 1);
        }

    }

    public String delete(Map<String, String> params, String body) throws IOException {
        String uri = uriList.peek();
        return delete(uri, params, body, 0);
    }

    public String delete(Map<String, String> params, String body, int count) throws IOException {
        if (duplexMode==DuplexMode.SINGLE && count >= uriList.size()) {
            throw new IOException("connection fail");
        }
        String uri = uriList.peek();
        return delete(uri, params, body, count);
    }

    private String delete(String uri, Map<String, String> params, String body, int count) throws IOException {
        Request httpRequest = httpClient.newRequest(uri)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .method(HttpMethod.DELETE)
                .content(new StringContentProvider(body), "application/json");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        try {
            ContentResponse response = httpRequest.send();
            return response.getContentAsString();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            reorderHostAndPorts();
            return delete(params, body, count);
        }

    }

    private void reorderHostAndPorts() {
        String uri = uriList.poll();
        uriList.offer(uri);
    }


}
