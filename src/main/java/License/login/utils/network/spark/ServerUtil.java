package License.login.utils.network.spark;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class ServerUtil {
    private static Logger logger = LoggerFactory.getLogger(ServerUtil.class);

    public static Set<String> getHostAddress() {
        Set<String> retSet = new HashSet<>();
        try {
            Enumeration<NetworkInterface> nienum = NetworkInterface.getNetworkInterfaces();
            while (nienum.hasMoreElements()) {
                NetworkInterface ni = nienum.nextElement();
                Enumeration<InetAddress> kk = ni.getInetAddresses();
                while (kk.hasMoreElements()) {
                    InetAddress inetAddress = kk.nextElement();
                    retSet.add(inetAddress.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return retSet;
    }

    /**
     * 자기 자신의 ip address를 가져오는데 ipv4중에서 외부에서 식별가능한 주소를 가져온다.
     *
     * @return
     */
    public static String getUniqueIpAddress() {
        String localAddress = null;
        try {
            Enumeration<NetworkInterface> nienum = NetworkInterface.getNetworkInterfaces();
            while (nienum.hasMoreElements()) {
                NetworkInterface ni = nienum.nextElement();
                if (ni.isLoopback() || ni.isPointToPoint()) {
                    continue;
                }
                Enumeration<InetAddress> kk = ni.getInetAddresses();
                while (kk.hasMoreElements()) {
                    InetAddress inetAddress = kk.nextElement();
                    if (inetAddress.isLinkLocalAddress()) {
                        continue;
                    }
                    if (inetAddress.isSiteLocalAddress()) {
                        localAddress = inetAddress.getHostAddress();
                        continue;
                    }

                    return inetAddress.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return localAddress;
    }

    public static Service initService(int port, String staticFileLocation, int threadPoolSize) {
        Service service = Service.ignite().port(port).threadPool(threadPoolSize);
        if (staticFileLocation != null) {
            service.staticFiles.externalLocation(staticFileLocation);
        }
        service.init();
        // enable CORS
        service.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
        service.before((request, response) -> {
            String accessControlAllowOrigin = request.headers("Access-Control-Allow-Origin");
            logger.info(accessControlAllowOrigin);
            if (accessControlAllowOrigin == null) {
                response.header("Access-Control-Allow-Origin", "*");
            }
        });

        enableCORS(service);
        return service;

    }

    public static Service initService(int port) {
        Service service = Service.ignite().port(port).threadPool(1000);
        service.init();
        // enable CORS
        service.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
        service.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

//        enableCORS(service);
        return service;

    }

    public static void enableCORS(Service service) {
        // enable CORS
        service.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            logger.info("accessControlRequestMethod =" + accessControlRequestMethod);
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        service.after((request, response) -> {
            response.raw().setHeader("Access-Control-Allow-Origin", "*");
        });
    }


    static String formatPath(String proxyPath) {
        if (proxyPath == null)
            proxyPath = "/";
        /*if (!proxyPath.endsWith("/"))
            proxyPath += "/";*/
        return proxyPath;
    }

    static String url(spark.Request req, String proxyServer, String proxyPath) {
        String proxyUrl = (proxyPath.equals(req.pathInfo())) ? proxyServer : proxyServer + "/" + req.pathInfo().replace(proxyPath, "");
        return (req.queryString() == null) ? proxyUrl : (proxyUrl + "?" + req.queryString());
    }

    static void addHeader(Request request, spark.Request req) {
        for (String header : req.headers()) {
            if (!header.equals("Content-Length"))
                request.setHeader(header, req.headers(header));
        }
    }

    static void addBody(Request request, spark.Request req) {
        request.bodyByteArray(req.bodyAsBytes());
    }

    static HttpResponse go(Request request) throws ClientProtocolException, IOException {
        return request.execute().returnResponse();
    }

    static void mapHeaders(HttpResponse response, spark.Response res) {
        for (Header header : response.getAllHeaders()) {
            res.header(header.getName(), header.getValue());
        }
    }

    static void mapStatus(HttpResponse response, spark.Response res) {
        res.status(response.getStatusLine().getStatusCode());
    }

    /*
     * This had issues with binary data
     * private static String result(HttpResponse response) throws ParseException,
     * IOException { HttpEntity entity = response.getEntity(); return entity == null
     * ? "" : EntityUtils.toString(entity); }
     */

    static void extractResponse(HttpResponse httpResponse, HttpServletResponse raw) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null)
            return;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        entity.writeTo(baos);
        byte[] bytes = baos.toByteArray();
        raw.getOutputStream().write(bytes);
        raw.getOutputStream().flush();
        raw.getOutputStream().close();
    }

    public static void main(String[] args) {
        System.out.println(ServerUtil.getUniqueIpAddress());
    }
}

