package License.login.Exception;

public class DatabaseUpsertException extends Exception {
    public DatabaseUpsertException() {}
    public DatabaseUpsertException(String message) { super(message); }
    public String message(){
        return "데이터 생성 및 업데이트에 문제가 발생했습니다.";
    }
}
