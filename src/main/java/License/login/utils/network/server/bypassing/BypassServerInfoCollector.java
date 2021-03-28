package License.login.utils.network.server.bypassing;

import License.login.utils.network.client.HttpClientManager;
import License.login.utils.network.spark.ServerUtil;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BypassServerInfoCollector {

    private HttpClient httpClient = HttpClientManager.getHttpClient();
    private static Logger logger = LoggerFactory.getLogger(BypassServerInfoCollector.class);
    private List<BypassServerInfo> allSessionServerInfoList;
    private List<String> hostNames;
    private List<Integer> ports;
    private int numActiveSessions = 0;


    public BypassServerInfoCollector(List<String> hosts, List<Integer> ports) {
        this.hostNames = hosts;
        this.ports = ports;

        try {
            checkValidationOfConfig();
            collectAllServerInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * config가 맞는지 여부 확인
     *
     * @return
     */
    public void checkValidationOfConfig() throws IOException {
        if (hostNames.size()>2){
            throw new IOException("size of hostnames should be less than or equal to 2.");
        }
        if (hostNames.size() == ports.size()) {
        } else {
            logger.error("size of hostnames {}, ports {} are mismatched", hostNames, ports);
            throw new IOException("size of hostnames, userports and botports are mismatch");
        }
    }

    /**
     * 살아있는 다른 Serssion Server를  찾아본다.ChatMessageQueueServer
     * 서버 시작전에 호출한다. 그래야 자기자신하고 안겹친다.
     *
     * @return
     */
    public void collectAllServerInfo() {
        allSessionServerInfoList = new ArrayList<>();
        String apiName = "/get_initialize_order";

        for (int i = 0; i < hostNames.size(); i++) {
            String hostName = hostNames.get(i);
            Integer port = ports.get(i);

            String addr = hostName + ":" + port.toString();
//            logger.info(addr);
            try {
                ContentResponse response = httpClient.newRequest("http://" + addr + apiName)
                        .timeout(500, TimeUnit.MILLISECONDS)
                        .send();

                String responseStr = response.getContentAsString();
//                logger.info(responseStr);
                Integer initialize_order = Integer.parseInt(responseStr);
                allSessionServerInfoList.add(new BypassServerInfo(hostName, port, true, initialize_order));
                numActiveSessions++;
            } catch (Exception e) {
                logger.debug("user port http://"+hostName+":"+port+ " is not activated. ");
                allSessionServerInfoList.add(new BypassServerInfo(hostName, port, false, null));
            }
        }
//        logger.info(allSessionServerInfoList.toString());
    }

    public BypassServerInfo selectMySessionServerInfo() {
        Set<String> hostNames = ServerUtil.getHostAddress();
        for (BypassServerInfo sessionServerInfo : allSessionServerInfoList) {
            // hostname이 같고, 사용안되면 쓴다
            if (hostNames.contains(sessionServerInfo.hostName) && !sessionServerInfo.isActivated) {
                BypassServerInfo mySessionServerInfo = new BypassServerInfo(sessionServerInfo);
                mySessionServerInfo.setActivated(true);
                mySessionServerInfo.setInitializeOrder(numActiveSessions);
                return mySessionServerInfo;
            }
        }
        return null;
    }

    public BypassServerInfo getMasterSessionSeverInfo() {
        for (BypassServerInfo statusOfSessionServer : allSessionServerInfoList) {
            if (statusOfSessionServer.isActivated()) {
                if (statusOfSessionServer.initializeOrder == 0) {
                    return statusOfSessionServer;
                }
            }
        }
        return null;
    }

}
