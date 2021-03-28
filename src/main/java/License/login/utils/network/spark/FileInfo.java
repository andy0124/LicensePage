package License.login.utils.network.spark;

public class FileInfo {
    public Integer chunkIndex;
    public String contentType;
    public String fileName;
    public String relativePath;
    public Integer totalFileSize;
    public Integer totalChunks;
    public String uploadUid;

    @Override
    public String toString() {
        return "FileInfo{" +
                "chunkIndex=" + chunkIndex +
                ", contentType='" + contentType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", relativePath='" + relativePath + '\'' +
                ", totalFileSize=" + totalFileSize +
                ", totalChunks=" + totalChunks +
                ", uploadUid='" + uploadUid + '\'' +
                '}';
    }
}
