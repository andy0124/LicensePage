package License.login.utils.network.client;




import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpClient {

    public static String sendPost(String ip,int port,String api, String params) throws Exception {
        String targetUrl = "http://" + ip + ":" + port + api;
        URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(params);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) !=null){
            response.append(inputLine);
        }
        in.close();
       return response.toString();

    }


    public static String sendGet(String ip,int port,String api) throws Exception {
        String targetUrl = "http://" + ip + ":" + port + api;
        URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);


        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) !=null){
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    public static void main(String[] args){
        try {
         String s =    sendGet("127.0.0.1",17405,"/project/id/get");
            System.out.println(s);
        } catch (Exception e) {
         e.printStackTrace();
        }



    }
}
