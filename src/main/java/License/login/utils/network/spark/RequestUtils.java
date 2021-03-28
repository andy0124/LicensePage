package License.login.utils.network.spark;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;

public class RequestUtils {

    public static Map<String, String> getAllQueryParams(spark.Request request) {
        Map<String, String> queryMap = new HashMap<>();
        for (String key : request.queryParams()) {
            String value = request.queryParams(key);
            queryMap.put(key, value);
        }
        return queryMap;
    }


    public static Multimap<String, String> getAllQueryParamsArray(spark.Request request) {
        Multimap<String, String> queryMap = HashMultimap.create();
        for (String key : request.queryParams()) {
            String value = request.queryParams(key);
            queryMap.put(key, value);
        }
        return queryMap;
    }
}
