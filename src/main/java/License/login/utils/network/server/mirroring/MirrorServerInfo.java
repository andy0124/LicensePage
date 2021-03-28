package License.login.utils.network.server.mirroring;

import java.util.Objects;

public class MirrorServerInfo {
    /**
     * host 주소
     */
    String hostName;
    /**
     * port
     */
    Integer port;


    public MirrorServerInfo(MirrorServerInfo arg) {
        this.hostName = arg.hostName;
        this.port = arg.port;

    }


    public MirrorServerInfo(String hostName, Integer port) {
        this.hostName = hostName;
        this.port = port;

    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MirrorServerInfo that = (MirrorServerInfo) o;
        return Objects.equals(hostName, that.hostName) &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostName, port);
    }

    @Override
    public String toString() {
        return "MirrorServerInfo{" +
                "hostName='" + hostName + '\'' +
                ", port=" + port +
                '}';
    }
}
