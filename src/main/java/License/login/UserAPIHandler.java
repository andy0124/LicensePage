package License.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import License.login.utils.StringUtils;
import License.login.utils.auth.VerifyAuthToken;

import License.login.utils.network.spark.ErrorHandling;
import License.login.utils.network.spark.RestAPIHandler;

import License.login.db.UserInfoDTO;
import License.login.db.UserInfoManager;
import License.login.utils.AuthConverter;


import License.login.request_response.ParseQueryParams;
import License.login.request_response.QueryParams;
import License.login.utils.ErrorMessages;
import License.login.Exception.NoSuchUserException;
import License.login.Exception.PasswordMismatchException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.*;


public class UserAPIHandler{
    private static Logger logger = LoggerFactory.getLogger(UserAPIHandler.class);
    private VerifyAuthToken verifyAuthToken = new VerifyAuthToken();
    private UserInfoManager userInfoManager;
    private Gson gson = new Gson();
    Service service;


    public UserAPIHandler(Service service, UserInfoManager userInfoManager) {
        this.service = service;
        this.userInfoManager = userInfoManager;
    }

    public void registerAPIs() {
        /**
         jwt 인증토큰 발급
         */

        this.service.get("/user", (request, response) -> getUsers(request, response));
        this.service.put("/user/update", (request, response) -> updateUser(request, response));
        this.service.post("/user/insert", (request, response) -> insertUser(request, response));
        this.service.delete("/user/delete", (request, response) -> deleteUser(request, response));
        this.service.post("/user/login", (request, response) -> login(request, response));
        this.service.put("/user/changePassword", (request, response) -> changePassword(request, response));
        this.service.put("/user/setPassword", (request, response) -> setPassword(request, response));
        this.service.put("/user/clear_wrong_accesscount", (request, response) -> clearWrongAccessCount(request, response));
        ErrorHandling.set405Error(this.service, "/user", "GET");
        ErrorHandling.set405Error(this.service, "/user/update", "PUT");
        ErrorHandling.set405Error(this.service, "/user/insert", "POST");
        ErrorHandling.set405Error(this.service, "/user/delete", "DELETE");
        ErrorHandling.set405Error(this.service, "/user/login", "POST");
        ErrorHandling.set405Error(this.service, "/user/changePassword", "PUT");

    }


    private String getUsers(Request request, Response response) {
        try {
            QueryParams queryParams = ParseQueryParams.parse(request);
            String apiKey = queryParams.getApiKey();
            if (StringUtils.match(apiKey, "f7d8ce90-51ec-4c1c-bbfb-e33307e873da")) {
                return gson.toJson(userInfoManager.getUserInfos());
            } else {
                response.status(409);
                return ErrorMessages.GeneralError;
            }
        } catch (Exception e) {
            response.status(409);
            return ErrorMessages.GeneralError;
        }
    }

    private String insertUser(Request request, Response response) {
        try {
            String bodyStr = request.body();
            UserInfoDTO userInfoDTO = gson.fromJson(bodyStr, UserInfoDTO.class);
            if (StringUtils.isEmpty(userInfoDTO.getId())) {
                response.status(409);
                return "아이디가 비어있습니다.";
            }
            if (userInfoManager.getUserIdList().contains(userInfoDTO.getId())) {
                response.status(409);
                return "해당 아이디가 존재합 니다. 다른 아이디로 생성해 주시기 바랍니다.";
            }
            userInfoManager.upsertUserInfo(userInfoDTO);
            return gson.toJson(userInfoDTO);
        } catch (Exception e) {
            response.status(409);
            return ErrorMessages.GeneralError;
        }
    }

    private String updateUser(Request request, Response response) {
        try {
            String bodyStr = request.body();
            UserInfoDTO userInfoDTO = gson.fromJson(bodyStr, UserInfoDTO.class);
            if (StringUtils.isEmpty(userInfoDTO.getId())) {
                response.status(409);
                return "아이디가 비어있습니다.";
            }
            if (userInfoDTO.getId().equals("root")) {
                response.status(409);
                return "root 계정정보는 이 방법을 통해 수정할 수 없습니다.";
            }
            userInfoManager.upsertUserInfo(userInfoDTO);
            return gson.toJson(userInfoDTO);
        } catch (Exception e) {
            response.status(409);
            return ErrorMessages.GeneralError;
        }
    }

    private String addUser(Request request, Response response) {
        try {
            String bodyStr = request.body();
            UserInfoDTO userInfoDTO = gson.fromJson(bodyStr, UserInfoDTO.class);
            if (StringUtils.isEmpty(userInfoDTO.getId())) {
                response.status(409);
                return "아이디가 비어있습니다.";
            }

            if (userInfoManager.getUserIdList().contains(userInfoDTO.getId())) {
                response.status(401);
                return "해당 아이디가 존재합니다. 다른 아이디로 생성해 주시기 바랍니다.";
            }

            userInfoManager.insertUserInfo(userInfoDTO);
            return gson.toJson(userInfoDTO);
        } catch (Exception e) {

            response.status(409);
            return ErrorMessages.GeneralError;
        }
    }

    private String deleteUser(Request request, Response response) {
        try {
            String bodyStr = request.body();
            logger.info("{}", bodyStr);
            UserInfoDTO userInfoDTO = gson.fromJson(bodyStr, UserInfoDTO.class);
            if (StringUtils.isEmpty(userInfoDTO.getId())) {
                response.status(409);
                return "아이디가 비어있습니다.";
            }
            if(userInfoDTO.getId().equalsIgnoreCase("root")) {
                response.status(401);
                return "root 계정은 삭제할 수 없습니다.";
            }



            userInfoManager.deleteUserInfo(userInfoDTO.getId());
            return gson.toJson(userInfoDTO);
        } catch (NoSuchUserException e) {
            response.type("text/xml");
            response.status(401);
            return "아이디에 해당하는 사용자가 없습니다";
        } catch (Exception e) {
            response.status(409);
            return ErrorMessages.GeneralError;
        }
    }



    private String login(Request request, Response response) {
        String userId = "";
        try {
            String authorization = request.headers("Authorization");
            logger.info("Authorization : {}", authorization);
            if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                String base64Credentials = authorization.substring("Basic".length()).trim();
                IdPassword idPassword = AuthConverter.splitUserPwd(AuthConverter.base64Decoder(base64Credentials));
                userId = idPassword.getId();
                if (userInfoManager.checkWrongAccessCount(userId)) {
                    response.status(409);
                    response.type("text/xml");
                    return ErrorMessages.WrongPasswordCount;
                }

                UserInfoDTO userInfoDTO = userInfoManager.getValidUserInfo(idPassword.getId(), idPassword.getPassword());
                response.status(200);
                logger.info("User {} tried to log in. ", userInfoDTO.getId());
                logger.info("{}", userInfoDTO);
                int level = 1;
                if (userInfoDTO.getAdmin() == true) {
                    level = 0;
                }
                userInfoManager.clearWrongAccessCount(userInfoDTO.getId());
                return verifyAuthToken.createToken(userInfoDTO.getId(), level);
            } else {
                response.status(409);
                return "로그인 정보가 없습니다.";
            }
        } catch (NoSuchUserException e) {
            response.status(409);
            response.type("text/xml");

            return "등록되지 않은 아이디 이거나, 아이디 또는 비밀번호를 잘못 입력하셨습니다.";
        } catch (PasswordMismatchException e) {
            response.status(409);
            response.type("text/xml");
            userInfoManager.increseWrongAccessCount(userId);
            logger.info("the number of wrong login "+ userInfoManager.getUserInfoDTO(userId).getWrongAccessCount());
            return "등록되지 않은 아이디 이거나, 아이디 또는 비밀번호를 잘못 입력하셨습니다.";
        } catch (Exception e) {
            response.status(409);
            return ErrorMessages.GeneralError;
        }

    }

    private String changePassword(Request request, Response response) {
        try {
            String authorization = request.headers("Authorization");
            logger.info(authorization);
            String jsonStr = AuthConverter.base64Decoder(authorization.replace("Bearer ", ""));
            logger.info(jsonStr);
            JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
            String userId = jsonObject.get("userId").getAsString();
            String password = jsonObject.get("password").getAsString();
            String newPassword = jsonObject.get("newPassword").getAsString();


            UserInfoDTO userInfoDTO = userInfoManager.getValidUserInfo(userId, password);

            String salt = UUID.randomUUID().toString();
            userInfoDTO.setSalt(salt);
            String saltedPassword = AuthConverter.SHA256(newPassword, salt);
            userInfoDTO.setPassword(saltedPassword);
            //logger.info("userId={}, password={}, newPassword={}", userId, password, saltedPassword);
            userInfoDTO.setWrongAccessCount(0);
            userInfoManager.updateUserInfo(userInfoDTO);
            response.type("text/xml");
            return "OK";
        } catch (NoSuchUserException e) {
            response.status(401);
            response.type("text/xml");
            return "아이디가 존재하지 않습니다.";
        } catch (PasswordMismatchException e) {
            response.status(401);
            response.type("text/xml");
            return "패스워드가 일치하지 않습니다.";
        } catch (Exception e) {
            response.status(401);
            response.type("text/xml");
            return "패스워드 변경이 실패하였습니다.";
        }
    }

    private String setPassword(Request request, Response response) {
        try {
            String authorization = request.headers("Authorization");
            logger.info("Authorization : {}", authorization);
            String jsonStr = AuthConverter.base64Decoder(authorization.replace("Bearer ", ""));
            JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
            String userId = jsonObject.get("userId").getAsString();
            String password = jsonObject.get("password").getAsString();
            UserInfoDTO userInfoDTO = userInfoManager.getUserInfoDTO(userId);

            String salt = UUID.randomUUID().toString();
            userInfoDTO.setSalt(salt);
            String saltedPassword = AuthConverter.SHA256(password, salt);
            userInfoDTO.setPassword(saltedPassword);
            userInfoDTO.setWrongAccessCount(0);
            logger.info("{}", userInfoDTO);
            userInfoManager.updateUserInfo(userInfoDTO);
            response.type("text/xml");
            return "OK";
        } catch (NoSuchUserException e) {
            response.status(401);
            response.type("text/xml");
            return "아이디가 존재하지 않습니다.";
        } catch (Exception e) {
            response.status(401);
            response.type("text/xml");
            return "패스워드 변경이 실패하였습니다.";
        }
    }



    private String clearWrongAccessCount(Request request, Response response) {
        try {
            QueryParams queryParams = ParseQueryParams.parse(request);
            String apiKey = queryParams.getApiKey();
            String userId = queryParams.getUserId();
            if (StringUtils.match(apiKey, "f7d8ce90-51ec-4c1c-bbfb-e33307e873da")) {
                response.status(409);
                return ErrorMessages.GeneralError;
            }

            if (StringUtils.isEmpty(userId)) {
                response.status(409);
                return "아이디가 비어있습니다.";
            }

            userInfoManager.clearWrongAccessCount(userId);
            return "OK";
        } catch (Exception e) {
            response.status(409);
            return ErrorMessages.GeneralError;
        }
    }

}
