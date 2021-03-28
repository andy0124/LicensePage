package License.login.request_response;

import spark.Request;

import java.lang.reflect.Field;
import java.util.Map;

public class ParseQueryParams {

    /**
     * reflection을 이용하여 queryParam 추출하여 집어넣음
     *
     * @param request
     * @return
     */
    public static QueryParams parse(Request request) {
        QueryParams queryParams = new QueryParams();
        Field[] fields = queryParams.getClass().getDeclaredFields();
        for (Field field : fields) {
            String value = request.queryParams(field.getName());
            if (value == null || value.isEmpty() || value.equals("undefined")) {
                continue;
            } else {
                field.setAccessible(true);
                try {
                    String paramTypeName = field.getType().getSimpleName();

                    switch (paramTypeName) {
                        case "Integer":
                            field.set(queryParams, Integer.parseInt(value));
                            break;
                        case "Long":
                            field.set(queryParams, Long.parseLong(value));
                            break;
                        case "Boolean":
                            field.set(queryParams, Boolean.parseBoolean(value));
                            break;
                        case "String":
                            field.set(queryParams, value);
                            break;
                    }

                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
                }
            }
        }
        return queryParams;
    }

    public static QueryParams parse(Map<String, String> queryParamMap) {
        QueryParams queryParams = new QueryParams();
        Field[] fields = queryParams.getClass().getDeclaredFields();
        for (Field field : fields) {
            String value = queryParamMap.get(field.getName());
            if (value == null || value.isEmpty() || value.equals("undefined")) {
                continue;
            } else {
                field.setAccessible(true);
                try {
                    String paramTypeName = field.getType().getSimpleName();

                    switch (paramTypeName) {
                        case "Integer":
                            field.set(queryParams, Integer.parseInt(value));
                            break;
                        case "Long":
                            field.set(queryParams, Long.parseLong(value));
                            break;
                        case "Boolean":
                            field.set(queryParams, Boolean.parseBoolean(value));
                            break;
                        case "String":
                            field.set(queryParams, value);
                            break;
                    }

                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
                }
            }
        }
        return queryParams;
    }
}
