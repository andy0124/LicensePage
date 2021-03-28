package License.login.db;


import License.login.utils.AuthConverter;
import License.login.Exception.DuplicatedUserCreationException;
import License.login.Exception.NoSuchUserException;
import License.login.Exception.PasswordMismatchException;
import License.login.Exception.DatabaseUpsertException;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserInfoManager {
    private Nitrite db;
    private ObjectRepository repository;
    private String dbFilePath;
    private static Logger logger = LoggerFactory.getLogger(UserInfoManager.class);

    public UserInfoManager(String dbPath) {
        this.dbFilePath = dbPath + "/userInfo.db";
        openDB();
        openRepositories();
    }

    private void openDB() {
        db = Nitrite.builder()
                .compressed()
                .filePath((System.getProperty("user.dir") + "/" + dbFilePath).replace("//", "/"))
                .openOrCreate();
    }

    private void openRepositories() {
        repository = db.getRepository(UserInfoDTO.class);
        if (repository.find().toList().size()==0){
            resetRootPassword();
        }
    }

    public void resetRootPassword() {
        UserInfoDTO userInfoDTO = (UserInfoDTO) repository.find(ObjectFilters.eq("id", "root")).firstOrDefault();
        if (userInfoDTO == null) {
            userInfoDTO = new UserInfoDTO();
            userInfoDTO.setId("root");
            userInfoDTO.setName("root");
            userInfoDTO.setAdmin(true);
        }

        String defaultPassword = "admin#12";
        String salt = UUID.randomUUID().toString();
        String encryptedPassword = AuthConverter.SHA256(defaultPassword);
        String saltedPassword = AuthConverter.SHA256(encryptedPassword, salt);
        userInfoDTO.setPassword(saltedPassword);
        userInfoDTO.setSalt(salt);
        userInfoDTO.setWrongAccessCount(0);
        repository.update(userInfoDTO,true);
    }


    public List<UserInfoDTO> getUserInfos() {
        return repository.find().toList();
    }

    public Set<String> getUserIdList() {
        return getUserInfos().stream().map(u -> u.getId()).collect(Collectors.toSet());
    }
    public UserInfoDTO getUserInfoDTO(String id) {
        UserInfoDTO userInfoDTO = (UserInfoDTO) repository.find(ObjectFilters.eq("id", id)).firstOrDefault();
        return userInfoDTO;
    }


    public UserInfoDTO getValidUserInfo(String id, String password) throws NoSuchUserException, PasswordMismatchException {
        UserInfoDTO userInfoDTO = (UserInfoDTO) repository.find(ObjectFilters.eq("id", id)).firstOrDefault();

        if (userInfoDTO == null) {
            throw new NoSuchUserException(id);
        }
        String argPassword = AuthConverter.SHA256(password, userInfoDTO.getSalt());

        if (userInfoDTO.getPassword().equals(argPassword) == false) {
            throw new PasswordMismatchException(id);
        } else {
            return userInfoDTO;
        }
    }


    public void insertUserInfo(UserInfoDTO userInfoDTO) throws DuplicatedUserCreationException {
        if (repository.insert(userInfoDTO).getAffectedCount() == 0) {
            throw new DuplicatedUserCreationException();
        }
    }

    public void upsertUserInfo(UserInfoDTO userInfoDTO) throws DatabaseUpsertException {
        if (repository.update(userInfoDTO, true).getAffectedCount() == 0) {
            throw new DatabaseUpsertException();
        }
    }

    public void updateUserInfo(UserInfoDTO userInfoDTO) throws NoSuchUserException {
        if (repository.update(userInfoDTO).getAffectedCount() == 0) {
            throw new NoSuchUserException();
        }
    }

    public void deleteUserInfo(String id) throws NoSuchUserException {
        if (repository.remove(ObjectFilters.eq("id", id)).getAffectedCount() == 0) {
            throw new NoSuchUserException();
        }
    }

    public void close() {
        db.commit();
        db.close();
    }

    public void increseWrongAccessCount(String userId) {
        UserInfoDTO userInfoDTO = (UserInfoDTO) repository.find(ObjectFilters.eq("id", userId)).firstOrDefault();
        if (userInfoDTO == null) {
            return;
        } else {
            userInfoDTO.increaseWrongAccessCount();
            repository.update(userInfoDTO);
        }
    }

    public boolean checkWrongAccessCount(String userId) throws NoSuchUserException {
        UserInfoDTO userInfoDTO = (UserInfoDTO) repository.find(ObjectFilters.eq("id", userId)).firstOrDefault();
        if (userInfoDTO == null) {
            throw new NoSuchUserException();
        }
        if (userInfoDTO.getWrongAccessCount() >= 5) {
            return true;
        } else {
            return false;
        }
    }

    public void clearWrongAccessCount(String userId) throws NoSuchUserException {
        UserInfoDTO userInfoDTO = (UserInfoDTO) repository.find(ObjectFilters.eq("id", userId)).firstOrDefault();
        if (userInfoDTO == null) {
            throw new NoSuchUserException();
        }
        userInfoDTO.setWrongAccessCount(0);
        repository.update(userInfoDTO);

    }

    public static void main(String[] args) throws NoSuchUserException, PasswordMismatchException, DuplicatedUserCreationException {
        UserInfoManager userInfoManager = new UserInfoManager("knowledges");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId("wykwon");
        userInfoDTO.setPassword(AuthConverter.SHA256("43g90upp"));
        userInfoDTO.setAdmin(false);
        userInfoDTO.setName("권우영");
        userInfoManager.insertUserInfo(userInfoDTO);
        logger.info("{}", userInfoDTO);

        System.out.println(userInfoManager.getUserIdList());
    }


}
