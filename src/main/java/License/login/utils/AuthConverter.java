package License.login.utils;

import License.login.IdPassword;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthConverter {

    public static String base64Decoder(String code) {
        byte[] decodeBytes = Base64.getDecoder().decode(code);
        return new String(decodeBytes);
    }

    public static String base64Encoder(String str) {
        byte[] targetStr = str.getBytes();
        byte[] encodeBytes = Base64.getEncoder().encode(targetStr);
        return new String(encodeBytes);
    }

    public static IdPassword splitUserPwd(String code) {
        String[] idPwd = code.split(":");
        if (idPwd.length == 1) {
            return new IdPassword(code.replace(":",""), "");
        } else {
            return new IdPassword(idPwd[0], idPwd[1]);
        }
    }

    public static String SHA256(String key, String salt) {
        String newStr = key+salt;
        String SHA = "";
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(newStr.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }


    public static String SHA256(String key) {
        String SHA = "";
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(key.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }

    public static void main(String[] args) {
        AuthConverter cv = new AuthConverter();
        String encodeSHA256 = cv.SHA256("12345678");
        System.out.println(encodeSHA256);
    }
}
