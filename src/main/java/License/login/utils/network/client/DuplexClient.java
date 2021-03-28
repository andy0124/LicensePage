package License.login.utils.network.client;

import License.login.utils.auth.VerifyAuthToken;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DuplexClient {
    private static Logger logger = LoggerFactory.getLogger(DuplexClient.class);
    private List<String> originalHostList;
    private List<Integer> originalPortList;
    private Queue<String> hostQueue;
    private Queue<Integer> portQueue;
    private int timeoutSec;
    private HttpClient httpClient;
    private DuplexMode duplexMode;


    public DuplexClient(List<String> hostQueue, List<Integer> portQueue, DuplexMode duplexMode, int timeoutSec) {
        this.duplexMode = duplexMode;
        this.originalHostList = hostQueue;
        this.originalPortList = portQueue;
        this.hostQueue = new LinkedList(hostQueue);
        this.portQueue = new LinkedList(portQueue);
        this.timeoutSec = timeoutSec;
        this.httpClient = HttpClientManager.getHttpClient();
    }

    public String get(String apiName, Map<String, String> params) throws IOException {
        return get(apiName, params, 0);
    }


    private String get(String apiName, Map<String, String> params, int count) throws IOException {
        if (duplexMode == DuplexMode.SINGLE && count >= hostQueue.size()) {
            throw new IOException("connection fail");
        }

        String host = hostQueue.peek();
        int port = portQueue.peek();
        return get(apiName, params, host, port, count);
    }

    private String get(String apiName, Map<String, String> params, String host, Integer port, int count) throws IOException {
        String systemToken = VerifyAuthToken.createToken("system",0);
        Request httpRequest = httpClient.newRequest("http://" + host + ":" + port + apiName)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .header("Authorization", "Bearer "+systemToken)
                .method(HttpMethod.GET);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        try {
            /*
            기존 코드는 response의 body사이즈가 2mb를 넘을 경우 문제가 됨(jetty의 response의 기본 buffer가 2mb)
            /knowledge/get?type=proejct api의 경우 프로젝트 전체의 정보를 받아오기 때문에 프로젝트가 많을경우 2mb를 넘으므로 에러 발생
            따라서 2mb 이상의 데이터도 읽을 수 있도록 아래와 같이 변경
            현재는 get만 아래와 같이 처리함. post도 문제될 경우 변경 필요
            ContentResponse response = httpRequest.send();
            return response.getContentAsString();
            */

            InputStreamResponseListener listener = new InputStreamResponseListener();
            httpRequest.send(listener);
            Response response = listener.get(timeoutSec, TimeUnit.SECONDS);
            StringBuffer stringBuffer = new StringBuffer();
            if(response.getStatus()==200){
                byte[] buffer = new byte[256];
                try (InputStream input = listener.getInputStream()){
                    while(true){
                        int read = input.read(buffer);
                        if(read<0){
                            break;
                        }
                        stringBuffer.append(new String(buffer, 0, read));
                    }
                }
            }else{
                response.abort(new InterruptedException());
            }
            return stringBuffer.toString();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            reorderHostAndPorts();
            return get(apiName, params, count + 1);
        }
    }

    public String post(String apiName, Map<String, String> params, String body) throws IOException {
//        logger.debug("apiName={} param={}",apiName,params);
        return post(apiName, params, body, 0);
    }

    public String post(String apiName, Map<String, String> params, String body, int count) throws IOException {
        if (duplexMode == DuplexMode.SINGLE && count >= hostQueue.size()) {
            throw new IOException("connection fail");
        }

        String host = hostQueue.peek();
        int port = portQueue.peek();
        return post(apiName, params, body, host, port, count);
    }

    private String post(String apiName, Map<String, String> params, String body, String host, Integer port, int count) throws IOException {
        String systemToken = VerifyAuthToken.createToken("system",0);
        Request httpRequest = httpClient.newRequest("http://" + host + ":" + port + apiName)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .method(HttpMethod.POST)
                .header("Authorization", "Bearer "+systemToken)
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
            return post(apiName, params, body, count + 1);
        }
    }

    public String put(String apiName, Map<String, String> params, String body) throws IOException {
        return put(apiName, params, body, 0);
    }

    public String put(String apiName, Map<String, String> params, String body, int count) throws IOException {
        if (duplexMode == DuplexMode.SINGLE && count >= hostQueue.size()) {
            throw new IOException("connection fail");
        }

        String host = hostQueue.peek();
        int port = portQueue.peek();
        return put(apiName, params, body, host, port, count);
    }


    private String put(String apiName, Map<String, String> params, String body, String host, Integer port, int count) throws IOException {
        String systemToken = VerifyAuthToken.createToken("system",0);
        Request httpRequest = httpClient.newRequest("http://" + host + ":" + port + apiName)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .method(HttpMethod.PUT)
                .header("Authorization", "Bearer "+systemToken)
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
            return put(apiName, params, body, count + 1);
        }

    }

    public String delete(String apiName, Map<String, String> params, String body) throws IOException {
        return delete(apiName, params, body, 0);
    }

    public String delete(String apiName, Map<String, String> params, String body, int count) throws IOException {
        if (duplexMode == DuplexMode.SINGLE && count >= hostQueue.size()) {
            throw new IOException("connection fail");
        }

        String host = hostQueue.peek();
        int port = portQueue.peek();
        return delete(apiName, params, body, host, port, count);
    }


    private String delete(String apiName, Map<String, String> params, String body, String host, Integer port, int count) throws IOException {
        String systemToken = VerifyAuthToken.createToken("system",0);
        Request httpRequest = httpClient.newRequest("http://" + host + ":" + port + apiName)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .method(HttpMethod.DELETE)
                .header("Authorization", "Bearer "+systemToken)
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
            return delete(apiName, params, body, count + 1);
        }

    }

    public void nonBlockGet(String apiName, Map<String, String> params) throws IOException {
        nonBlockGet(apiName, params, 0);
    }

    /**
     * @param params
     * @param sendCount 몇번쨰 전송인지 확인 (2번 이상은 보내지 말것 )
     */
    private void nonBlockGet(String apiName, Map<String, String> params, int sendCount) throws RuntimeException {

        if (sendCount >= hostQueue.size()) {
            return;
        }

        String host = hostQueue.peek();
        int port = portQueue.peek();
        String systemToken = VerifyAuthToken.createToken("system",0);
        Request httpRequest = httpClient.newRequest("http://" + host + ":" + port + apiName)
                .timeout(timeoutSec, TimeUnit.SECONDS)
                .header("Authorization", "Bearer "+systemToken)
                .method(HttpMethod.GET);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        httpRequest.onRequestFailure((request, throwable) -> {
            reorderHostAndPorts();
            nonBlockGet(apiName, params, sendCount + 1);
        });
        httpRequest.send(result -> { /* NOOP */ });
    }

    public void nonBlockPost(String apiName, Map<String, String> params, String body) throws RuntimeException {
        nonBlockPost(apiName, params, body, 0);
    }

    public void nonBlockPost(String apiName, Map<String, String> params, String body, int sendCount) throws RuntimeException {
        if (sendCount >= hostQueue.size()) {
            return;
        }
        String host = hostQueue.peek();
        int port = portQueue.peek();
        String systemToken = VerifyAuthToken.createToken("system",0);
        Request httpRequest = httpClient.newRequest("http://" + host + ":" + port + apiName)
                .method(HttpMethod.POST)
                .header("Authorization", "Bearer "+systemToken)
                .content(new StringContentProvider(body), "application/json");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        httpRequest.onRequestFailure((request, throwable) -> {
            reorderHostAndPorts();
            nonBlockPost(apiName, params, body, sendCount + 1);
        });

        httpRequest.send(result -> { /* NOOP */ });
    }

    public void nonBlockPut(String apiName, Map<String, String> params, String body) throws RuntimeException {
        nonBlockPut(apiName, params, body, 0);
    }

    public void nonBlockPut(String apiName, Map<String, String> params, String body, int sendCount) throws RuntimeException {

        if (sendCount >= hostQueue.size()) {
            return;
        }
        String host = hostQueue.peek();
        int port = portQueue.peek();
        String systemToken = VerifyAuthToken.createToken("system",0);
        Request httpRequest = httpClient.newRequest("http://" + host + ":" + port + apiName)
                .method(HttpMethod.POST)
                .header("Authorization", "Bearer "+systemToken)
                .content(new StringContentProvider(body), "application/json");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        httpRequest.onRequestFailure((request, throwable) -> {
            reorderHostAndPorts();
            nonBlockPut(apiName, params, body, sendCount + 1);
        });

        httpRequest.send(result -> { /* NOOP */ });
    }

    public void nonBlockDelete(String apiName, Map<String, String> params, String body) throws RuntimeException {
        nonBlockDelete(apiName, params, body, 0);
    }

    public void nonBlockDelete(String apiName, Map<String, String> params, String body, int sendCount) throws RuntimeException {
        if (sendCount >= hostQueue.size()) {
            return;
        }
        String host = hostQueue.peek();
        int port = portQueue.peek();
        String systemToken = VerifyAuthToken.createToken("system",0);
        Request httpRequest = httpClient.newRequest("http://" + host + ":" + port + apiName)
                .method(HttpMethod.POST)
                .header("Authorization", "Bearer "+systemToken)
                .content(new StringContentProvider(body), "application/json");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpRequest = httpRequest.param(URLRequestConvert.convert(entry.getKey()), URLRequestConvert.convert(entry.getValue()));
            }
        }
        httpRequest.onRequestFailure((request, throwable) -> {
            reorderHostAndPorts();
            nonBlockDelete(apiName, params, body, sendCount + 1);
        });

        httpRequest.send(result -> { /* NOOP */ });
    }

    private void reorderHostAndPorts() {
        String host = hostQueue.poll();
        Integer port = portQueue.poll();
        hostQueue.offer(host);
        portQueue.offer(port);
//        if (hostQueue.size() == 0 || portQueue.size() == 0) {
//            hostQueue = new LinkedList(originalHostList);
//            portQueue = new LinkedList(originalPortList);
//            throw new RuntimeException("Unable to get a response from the requesting server.");
//        }
    }


    public static void main(String[] args) {
        String systemToken = VerifyAuthToken.createToken("system",0);
        System.out.println(systemToken);
    }
}
