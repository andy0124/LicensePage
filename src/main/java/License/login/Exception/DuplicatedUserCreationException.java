package License.login.Exception;

public class DuplicatedUserCreationException extends Exception {
    public DuplicatedUserCreationException() {}
    public DuplicatedUserCreationException(String message) { super(message); }
    public String message(){
        return "동일한 id의 사용자가 존재합니다.";
    }

}
