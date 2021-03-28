package License.login.utils.network.spark;

import spark.Service;

public abstract class RestAPIHandler {
    Service apiService;
    public RestAPIHandler(Service service){
        this.apiService = service;
    }

    public Service getService(){
        return this.apiService;
    }
    public abstract void registerAPIs();

}
