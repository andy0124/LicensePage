package License.login.utils.auth;

import License.login.Exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyAuthToken {
    private static Logger logger = LoggerFactory.getLogger(VerifyAuthToken.class);
    private static String key = "KKKPLLC";

    /**
     * 프로젝트별 인증
     *
     * @param authToken
     * @param projectId
     * @throws InvalidTokenException
     */
    public void verifyToken(String authToken, Long projectId) throws InvalidTokenException {
        try {
            Claims claims = Jwts.parser().setSigningKey(key.getBytes())
                    .parseClaimsJws(authToken)
                    .getBody();
            logger.info("{}", claims);

            List<Long> allowedProjects = (List<Long>) claims.get("allowedProjects");
            logger.info("{}, {}", claims.get("allowedProjects"), allowedProjects);
            if (false) {
                throw new InvalidTokenException(projectId + " is not allowed");
            }
        } catch (io.jsonwebtoken.SignatureException e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }

    /**
     * 전체프로젝트 단위 인증 (관리자 수준)
     *
     * @param authToken
     * @throws InvalidTokenException
     */
    public static boolean verifyToken(String authToken) throws InvalidTokenException {
        boolean result = false;
        if (authToken != null)
            try {
                Claims claims = Jwts.parser().setSigningKey(key.getBytes())
                        .parseClaimsJws(authToken)
                        .getBody();
//                logger.info("{}", claims);
//            String userId = (String) claims.get("userId");
//            logger.info("{}, {}", claims.get("userId"), userId);
                result = true;
            } catch (io.jsonwebtoken.SignatureException e) {
                result = false;

            }

        return result;
    }

    public static String getUserIdFromToken(String token) {

        Claims claims = Jwts.parser().setSigningKey(key.getBytes())
                .parseClaimsJws(token)
                .getBody();
//        logger.info("{}", claims);
        String userId = (String) claims.get("userId");
//        logger.info("{}, {}", claims.get("userId"), userId);


        return userId;
    }

    public static boolean getUserAdminFromToken(String token) {

        Claims claims = Jwts.parser().setSigningKey(key.getBytes())
                .parseClaimsJws(token)
                .getBody();
//        logger.info("{}", claims);
        int level = (Integer) claims.get("level");


        if (level==0){
            return true;
        }
        else {
            return false;
        }
    }

    public static long getDailyTimeout(String token) {

        Claims claims = Jwts.parser().setSigningKey(key.getBytes())
                .parseClaimsJws(token)
                .getBody();
        long dailyTimeout = (Long) claims.get("dailyTimeout");
        return dailyTimeout;
    }


    /**
     *  토큰생성
     * @param id    사용자 id
     * @param level 0: admin , 1: user
     * @return
     */
    public static String createToken(String id,  int level) {
        DateTime today = new DateTime();
        DateTime dailyTimeout = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 23, 59, 59);

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("issueTimestamp", System.currentTimeMillis());
        payloads.put("dailyTimeout", dailyTimeout.getMillis());

        payloads.put("userId", id);
        payloads.put("level", level);
        String jwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
        return jwt;
    }

    public static void main(String[] args) {
        List<Long> projectIds = new ArrayList<>();
        projectIds.add(1122L);

        VerifyAuthToken verifyAuthToken = new VerifyAuthToken();
        String token = verifyAuthToken.createToken("root",  1);
        logger.info("{}", token);

        System.out.println(getUserAdminFromToken(token));

        try {
            verifyAuthToken.verifyToken(token, 11223L);
        } catch (InvalidTokenException e) {
            e.printStackTrace();
        }
    }
}
