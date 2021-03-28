package License.login.utils.network.spark;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import spark.Service;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static License.login.utils.network.spark.ServerUtil.*;

public class DuplexProxyServer {
    /**
     * proxyPath에 따른 proxyServer의 자료들
     */
    private static Map<String, Queue<String>> proxyServerMap = new ConcurrentHashMap<>();




    /**
     * @param service             - instance of Service for which proxy has to be enabled
     * @param proxyServers         - URL of the servers to proxy requests to (duplicated)
     * @param proxyPath           - local path for which requests have to be forwarded; null = '/'
     */


    public static void enableProxy(Service service, String proxyPath, String... proxyServers ) {

        String path = formatPath(proxyPath);
        String pathFilter = path.endsWith("/") ? proxyPath + "*" : proxyPath;

        Queue<String> proxyServerQueue = new LinkedList();
        for (String proxyServer : proxyServers) {
            proxyServerQueue.add(proxyServer);
        }
        proxyServerMap.put(pathFilter, proxyServerQueue);

        service.get(pathFilter, (req, res) -> {
            for (int i = 0; i < proxyServers.length; i++) {
                String activeProxyServer = proxyServerMap.get(pathFilter).peek();
                Request request = Request.Get(ServerUtil.url(req, activeProxyServer, path));
                ServerUtil.addHeader(request, req);
                try {
                    HttpResponse response = go(request);
                    mapHeaders(response, res);
                    mapStatus(response, res);
                    extractResponse(response, res.raw());
                    return res.raw();
                } catch (Exception e) {
                    reorderProxyServer(pathFilter);
                }
            }
            res.status(404);
            return "404 Not found";
        });

        service.post(pathFilter, (req, res) -> {
            for (int i = 0; i < proxyServers.length; i++) {
                String activeProxyServer = proxyServerMap.get(pathFilter).peek();
                Request request = Request.Post(ServerUtil.url(req, activeProxyServer, path));
                ServerUtil.addHeader(request, req);
                ServerUtil.addBody(request,req);
                try {
                    HttpResponse response = go(request);
                    mapHeaders(response, res);
                    mapStatus(response, res);
                    extractResponse(response, res.raw());
                    return res.raw();
                } catch (Exception e) {
                    reorderProxyServer(pathFilter);
                }
            }
            res.status(404);
            return "404 Not found";
        });

        service.put(pathFilter, (req, res) -> {
            for (int i = 0; i < proxyServers.length; i++) {
                String activeProxyServer = proxyServerMap.get(pathFilter).peek();
                Request request = Request.Put(ServerUtil.url(req, activeProxyServer, path));
                ServerUtil.addHeader(request, req);
                ServerUtil.addBody(request,req);
                try {
                    HttpResponse response = go(request);
                    mapHeaders(response, res);
                    mapStatus(response, res);
                    extractResponse(response, res.raw());
                    return res.raw();
                } catch (Exception e) {
                    reorderProxyServer(pathFilter);
                }
            }
            res.status(404);
            return "404 Not found";
        });

        service.delete(pathFilter, (req, res) -> {
            for (int i = 0; i < proxyServers.length; i++) {
                String activeProxyServer = proxyServerMap.get(pathFilter).peek();
                Request request = Request.Delete(ServerUtil.url(req, activeProxyServer, path));
                ServerUtil.addHeader(request, req);
                try {
                    HttpResponse response = go(request);
                    mapHeaders(response, res);
                    mapStatus(response, res);
                    extractResponse(response, res.raw());
                    return res.raw();
                } catch (Exception e) {
                    reorderProxyServer(pathFilter);
                }
            }
            res.status(404);
            return "404 Not found";
        });

        service.options(pathFilter, (req, res) -> {
            for (int i = 0; i < proxyServers.length; i++) {
                String activeProxyServer = proxyServerMap.get(pathFilter).peek();
                Request request = Request.Options(ServerUtil.url(req, activeProxyServer, path));
                ServerUtil.addHeader(request, req);
                try {
                    HttpResponse response = go(request);
                    mapHeaders(response, res);
                    mapStatus(response, res);
                    extractResponse(response, res.raw());
                    return res.raw();
                } catch (Exception e) {
                    reorderProxyServer(pathFilter);
                }
            }
            res.status(404);
            return "404 Not found";
        });


        service.head(pathFilter, (req, res) -> {
            for (int i = 0; i < proxyServers.length; i++) {
                String activeProxyServer = proxyServerMap.get(pathFilter).peek();
                Request request = Request.Head(ServerUtil.url(req, activeProxyServer, path));
                ServerUtil.addHeader(request, req);
                try {
                    HttpResponse response = go(request);
                    mapHeaders(response, res);
                    mapStatus(response, res);
                    extractResponse(response, res.raw());
                    return res.raw();
                } catch (Exception e) {
                    reorderProxyServer(pathFilter);
                }
            }
            res.status(404);
            return "404 Not found";
        });

    }
    private static synchronized void reorderProxyServer(String pathFilter) {
        Queue<String> pathFilterQueue = proxyServerMap.get(pathFilter);
        String newPathFilter = pathFilterQueue.poll();
        pathFilterQueue.offer(newPathFilter);
        proxyServerMap.put(pathFilter, pathFilterQueue);

    }

    private void changeProxyServers() {
//        String tmp = proxyServer1;
//        proxyServer2 = tmp;
//        proxyServer1 = proxyServer2;
    }
}