package License;

import License.login.UserAPIHandler;
import spark.Service;
import java.io.IOException;
import License.login.db.UserInfoManager;
import License.login.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LicensePageServer {
    private Service service;
    /*int portnum = 8080;*/
    int portnum = 8500;
    LicenseIssue licenseIssue;
    LicenseRecord licenseRecord;
    UserAPIHandler userAPIHandler;
    private UserInfoManager userInfoManager = new UserInfoManager("userinfo");

    public void start(){
        initService();
        /*initLicenseIssueAPI();
        initLicenseRecordAPI();
        initLoginAPI();*/

        (new LicenseIssue(this.service)).registerAPIs();
        (new LicenseRecord(this.service)).registerAPIs();
        (new UserAPIHandler(this.service,userInfoManager)).registerAPIs();
    }

    private void initService(){
        this.service = Service.ignite().port(portnum);
        this.service.staticFileLocation("/static");
        this.service.init();

        // enable CORS
        this.service.options("/*", (request, response) -> {
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
        this.service.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        this.service.before((request, response) -> {
            response.type("application/json");
        });
    }

    private void resetRootPassword() {
        userInfoManager.resetRootPassword();
    }

    /*public void initLicenseIssueAPI(){
        this.licenseIssue = new LicenseIssue(this.service);
        this.licenseIssue.registerAPIs();
    }

    public void initLicenseRecordAPI(){
        this.licenseRecord = new LicenseRecord(this.service);
        this.licenseRecord.registerAPIs();
    }

    public void initLoginAPI(){
        this.userAPIHandler = new UserAPIHandler(this.service,userInfoManager);
        this.userAPIHandler.registerAPIs();
    }*/






    public static void main(String[] args) throws IOException{
        LicensePageServer licensePageServer = new LicensePageServer();

        if (args.length == 1) {
            if (StringUtils.match(args[0].toLowerCase(), "reset")) {
                licensePageServer.resetRootPassword();
                /*logger.warn("reset root password as admin#12");*/
            }
        }

        licensePageServer.start();
    }

}
