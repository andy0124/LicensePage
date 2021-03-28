package License.login.utils.network.client;

import org.eclipse.jetty.client.HttpClient;

public class HttpClientManager {
    private static HttpClientManager instance;
    private HttpClient httpClient;

    private HttpClientManager() throws Exception {
        this.httpClient = new HttpClient();
        this.httpClient.start();
    }

    static {
        try {
            instance = new HttpClientManager();
        } catch (Exception e) {
            throw new RuntimeException("Exception creating HttpClientManager instance.");
        }
    }

    public static HttpClient getHttpClient() {
        return instance.httpClient;
    }


}
