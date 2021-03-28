package License.login.Exception;

public class NoSuchUserException extends Exception {
    public NoSuchUserException() {}
    public NoSuchUserException(String message) { super(message); }
    public String message(){
        return "사용자 정보가 없습니다.";
    }

}
