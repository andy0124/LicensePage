package License;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spark.Service;
import org.eclipse.jetty.client.HttpClient;

import java.util.HashMap;

import com.google.gson.Gson;



public class LicenseRecord {

    Service service;
    HttpClient httpClient;
    MongoDBHandler mongoDBHandler;
    HashMap<String,String> url = new HashMap<String, String>();

    public LicenseRecord(spark.Service service){
        this.service = service;
        this.mongoDBHandler = new MongoDBHandler();
    }

    public void registerAPIs(){

        this.service.get("/getRecord",((request, response) -> {
            return new Gson().toJson(mongoDBHandler.get_all_doc());
        }));

        this.service.get("/getExcel",(((request, response) -> {
            XSSFWorkbook xssfWorkbook = this.mongoDBHandler.downloadExcel();

            response.raw().setContentType("application/octet-stream");
            //response.raw().setHeader("Content-Disposition", "attachment; filename=tt.xlsx");
            xssfWorkbook.write(response.raw().getOutputStream());
            return response.raw();
        })));

    }
}
