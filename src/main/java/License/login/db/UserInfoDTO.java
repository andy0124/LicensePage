package License.login.db;

import org.dizitart.no2.objects.Id;

public class UserInfoDTO {
    @Id
    private String id;
    private String password;
    private String name;
    private Boolean isAdmin;
    private Integer wrongAccessCount = 0;
    private String salt;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Integer getWrongAccessCount() {
        return wrongAccessCount;
    }

    public void setWrongAccessCount(Integer wrongAccessCount) {
        this.wrongAccessCount = wrongAccessCount;
    }

    public void increaseWrongAccessCount() {
        if (this.wrongAccessCount==null){
            this.wrongAccessCount=1;
        }
        else {
            this.wrongAccessCount = this.wrongAccessCount+1;
        }
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserInfoDTO{");
        sb.append("id='").append(id).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", isAdmin=").append(isAdmin);
        sb.append('}');
        return sb.toString();
    }
}
