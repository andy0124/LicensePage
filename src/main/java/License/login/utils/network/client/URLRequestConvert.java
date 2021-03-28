package License.login.utils.network.client;

/**
 *
 */
public class URLRequestConvert {
    /**
     * get방식의 URL호출시 + / , 등의 문자를 %2b %2f 등으로 변환
     * @param str
     * @return
     */
    public static String convert(String str){
        String retStr =str.replace("$","%24");
        retStr =retStr.replace("&","%26");
        retStr =retStr.replace("+","%2b");
        retStr =retStr.replace(",","%2c");
        retStr =retStr.replace("/","%2f");
        retStr =retStr.replace(":","%3a");
        retStr =retStr.replace(";","%3b");
        retStr =retStr.replace("=","%3d");
        retStr =retStr.replace("?","%3f");
        retStr =retStr.replace("@","%40");
        return retStr;
    }
}
