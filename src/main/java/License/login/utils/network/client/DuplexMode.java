package License.login.utils.network.client;

/**
 * 서버에 접속할때 응답하지 않는 서버에 대한 대응
 * <p>
 * INFINITE: 응답할때까지 무한반복
 * SINGLE: 등록된 서버에 한번씩만 반복하고 예외발생
 */
public enum DuplexMode {
    INFINITE, SINGLE, MIRROR
}
