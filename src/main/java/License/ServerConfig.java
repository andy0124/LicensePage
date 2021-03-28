package License;

import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
    @Key("Server.ip")
    @DefaultValue("127.0.0.1")
    Integer serverIp();

    @Key("cServer.port")
    @DefaultValue("8080")
    Integer serverPort();
}
