package License;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import spark.Request;
import spark.Service;
import org.eclipse.jetty.client.HttpClient;

import java.io.IOException;
import java.lang.Exception;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.google.gson.Gson;


//라이센스 발급 요청하고 mongoDB에 저장하는 class ** {이거 용어 맞게 썼는지 확인}
public class LicenseIssue {

    Service service;
    HttpClient httpClient;
    MongoDBHandler mongoDBHandler;
    HashMap<String,String> url = new HashMap<String, String>();

    /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd\tHH:mm:ss");*/

    /*String wiseichatV3_url = "https://online.license4j.com/e/generate/1593491300798784127481593491456889";
    String ideskV2_url = "https://online.license4j.com/e/generate/1593491511733183579981593491575034";*/


    public LicenseIssue(spark.Service service){
        this.service = service;
        initClient();
        this.mongoDBHandler = new MongoDBHandler();
        //임시 온라인 스토리지 정보
        /*url.put("WiseiChat V3","https://online.license4j.com/e/generate/1596514557596181631791596514707447");
        url.put("iDesk V2","https://online.license4j.com/e/generate/1596514808509730980181596514756087");*/

        //228서버 정보
        url.put("WiseiChat V3","http://127.0.0.1:8080/algas/generate/1597325917197507476541597325866416");
        url.put("iDesk V2","http://127.0.0.1:8080/algas/generate/1597329534971498470221597329502885");

    }



    public void initClient(){
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        this.httpClient = new HttpClient(sslContextFactory);
        this.httpClient.setFollowRedirects(false);
    }



    // 라이센스 발급
    public String issueLicense(Request request) throws Exception{

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //발급 요청 정보
        String product = request.queryParams("product");
        String regname = request.queryParams("regname");
        String comp = request.queryParams("comp");
        String HDid = request.queryParams("HDid");

        Boolean permanent_check = Boolean.valueOf(request.queryParams("permanent_check"));

        String Period = "";
        String Generate = "";

        if(permanent_check){
            Period = "0";

            Date _today = new Date();
            Generate = _today.toString();
        }
        else{
            Date _endDate = dateFormat.parse(request.queryParams("endDate"));
            Date _startDate = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L); // 어제 날짜

            long period_time = (_endDate.getTime() - _startDate.getTime()) / (24*60*60*1000);

            //윈도우가 발급 서버 일때
            /*Generate = dateFormat.format(_startDate) + " 14:59.";// 시작일을 어제 14:59 시작해서 1일을 추가시킴-> 만료일을 맞추기 위해서*/
            //리눅스가 발급 서버 일때
            Generate = dateFormat.format(_startDate) + " 23:59.";

            Period = String.valueOf(period_time + 1);
        }

        //발급
        this.httpClient.start();// 이걸 계속 키고 소멸자때 넣어야 할지 말지 알아보자

        ContentResponse response = this.httpClient.newRequest(url.get(product))
                .method(HttpMethod.POST)
                .param("regname",regname)
                .param("comp",comp)
                .param("HDid",HDid)
                .param("Period",Period)
                .param("Generate",Generate)
                .header(HttpHeader.CONTENT_TYPE,"application/json")
                .timeout(5, TimeUnit.SECONDS)
                .send();

        String license_key = response.getContentAsString();



        this.httpClient.stop();

        //기록저장(비동기로 가자), 발급 실패시 이걸 저장할지 말지 물어보기
        MongoDBHandler mongoDBHandler = new MongoDBHandler();

        String issuedTime = timeFormat.format(new Date());
        String expireDate = "";
        if(permanent_check){
            expireDate = "무기한";
        }else{
            expireDate = request.queryParams("endDate");
        }

        mongoDBHandler.insert_doc(issuedTime, product, expireDate, regname, comp, HDid, license_key);

        //라이센스 키 전달
        return license_key;
    }





    public void registerAPIs(){
        //발급요청 처리 api
        this.service.get("/issuelicense",((request, response) -> {
            return issueLicense(request);
        }));

    }

}
