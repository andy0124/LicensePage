package License.login.utils.network.server.bypassing;

public class BypassServerInfo {
    /**
     * host 주소
     */
    String hostName;
    /**
     * port
     */
    Integer port;
    /**
     * 현재 동작여부
     */
    Boolean isActivated;
    /**
     * 동작한다면 초기화된 순서
     */
    Integer initializeOrder;

    public BypassServerInfo(BypassServerInfo arg) {
        this.hostName = arg.hostName;
        this.port = arg.port;
        this.isActivated = arg.isActivated;
        this.initializeOrder = arg.initializeOrder;
    }


    public BypassServerInfo(String hostName, Integer port, boolean isActivated, Integer initializeOrder) {
        this.hostName = hostName;
        this.port = port;
        this.isActivated = isActivated;
        this.initializeOrder = initializeOrder;
    }

    public String getHostName() {
        return hostName;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public Integer getInitializeOrder() {
        return initializeOrder;
    }

    public void setActivated(Boolean activated) {
        isActivated = activated;
    }

    public boolean isMasterMode() {
        return initializeOrder == 0;
    }

    public void setInitializeOrder(Integer initializeOrder) {
        this.initializeOrder = initializeOrder;
    }

    public void decreaseInitializeOrder() {
        this.initializeOrder = this.initializeOrder - 1;
    }

    @Override
    public String toString() {
        return "BypassServerInfo{" +
                "hostName='" + hostName + '\'' +
                ", port=" + port+
                ", isActivated=" + isActivated +
                ", initializeOrder=" + initializeOrder +
                '}';
    }


}
