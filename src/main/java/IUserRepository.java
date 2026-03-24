import java.util.ArrayList;
import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    boolean update(User user);
    void save();
    void load();
    boolean add(User user);
    boolean remove(String login);
    List<String> logins();
}