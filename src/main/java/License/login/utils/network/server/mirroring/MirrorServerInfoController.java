package License.login.utils.network.server.mirroring;

import License.login.utils.network.client.HttpClientManager;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MirrorServerInfoController {

    HttpClient httpClient = HttpClientManager.getHttpClient();
    private static Logger logger = LoggerFactory.getLogger(MirrorServerInfoController.class);
    private MirrorServerInfo myServerInfo;
    private MirrorServerInfo remoteServerInfo;
    private List<String> hostNames;
    private List<Integer> ports;


    public MirrorServerInfoController(List<String> hosts, List<Integer> ports) {

        this.hostNames = hosts;
        this.ports = ports;

        try {
//            httpClient.start();
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
        if (hostNames.size() > 2) {
            throw new IOException("size of hostnames should be less than or equal to 2.");
        }
        if (hostNames.size() == ports.size()) {
        } else {
            logger.error(hostNames.toString());
            logger.error(ports.toString());
            throw new IOException("size of hostnames, userports and botports are mismatch");
        }
    }

    /**
     * 살아있는 다른 Serssion Server를  찾아본다.
     * 서버 시작전에 호출한다. 그래야 자기자신하고 안겹친다.
     *
     * @return
     */
    public void collectAllServerInfo() {
        if (hostNames.size()==1 || ports.size()==1){
            myServerInfo = new MirrorServerInfo(hostNames.get(0), ports.get(0));
            remoteServerInfo=null;
            return;
        }


        String apiName = "/heartbeat";
        int remoteServerIdx = -1;
        for (int i = 0; i < hostNames.size(); i++) {
            String hostName = hostNames.get(i);
            Integer port = ports.get(i);
            String addr = hostName + ":" + port;
//            logger.info(addr);

            ContentResponse response = null;
            try {
                response = httpClient.newRequest("http://" + addr + apiName)
                        .timeout(500, TimeUnit.MILLISECONDS)
                        .send();

                logger.info(response.toString());
                remoteServerIdx = i;
                break;
            } catch (InterruptedException | TimeoutException | ExecutionException ignore) {

            }

        }

        if (remoteServerIdx < 0) {
            myServerInfo = new MirrorServerInfo(hostNames.get(0), ports.get(0));
            if (hostNames.size() == 2) {
                remoteServerInfo = new MirrorServerInfo(hostNames.get(1), ports.get(1));
            }
        } else {
            int myServerIdx = (remoteServerIdx + 1) % 2;
            myServerInfo = new MirrorServerInfo(hostNames.get(myServerIdx), ports.get(myServerIdx));
            if (hostNames.size() == 2) {
                remoteServerInfo = new MirrorServerInfo(hostNames.get(remoteServerIdx), ports.get(remoteServerIdx));
            }
        }
    }


    public MirrorServerInfo getMyServerInfo() {
        return myServerInfo;
    }

    public MirrorServerInfo getRemoteSeverInfo() {
        return remoteServerInfo;
    }


}
