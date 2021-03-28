package License.login.utils.network.spark;

import spark.Service;

public class ErrorHandling {

    public static void set405Error(Service service, String apiName, String method) {
        String returnMessage = "405 Method Not Allowed";
        service.head(apiName, (request, response) -> {
            response.status(405);
            return returnMessage;
        });
        service.options(apiName, (request, response) -> {
            response.status(405);
            return returnMessage;
        });

        service.patch(apiName, (request, response) -> {
            response.status(405);
            return returnMessage;
        });


        switch (method) {
            case "GET":
                service.post(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });

                service.put(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });

                service.delete(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });


                break;

            case "POST":
                service.get(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });


                service.put(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });

                service.delete(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });
                break;


            case "PUT":
                service.get(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });

                service.post(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });

                service.delete(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });
                break;


            case "DELETE":
                service.get(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });

                service.post(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });

                service.put(apiName, (request, response) -> {
                    response.status(405);
                    return returnMessage;
                });

                break;

        }
    }
}
