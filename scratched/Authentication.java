import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.impl.UserJsonRepository;

import java.util.List;

public class Authentication {

    private final UserJsonRepository userRepository;

    public Authentication(UserJsonRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String login, String password) {
        User user = userRepository.getUser(login);
        if (user != null) {
            String hashedInput = hashPassword(password);
            if (user.getPassword().equals(hashedInput)) {
                return user.copy();
            }
        }
        return null;
    }

    public User register(String login, String password){
        List<String> logins=userRepository.logins();
        for(String log : logins){
            if(login.equals(log)) {
                System.out.println("This login is already in use");
                return null;
            }
        }
        userRepository.add(new User(login,Authentication.hashPassword(password), Role.USER));
        System.out.println("Registered successfully");
        return new User(login,Authentication.hashPassword(password),Role.USER);
    }

    public static String hashPassword(String password) {
        return Hasher.hashPassword(password);
    }
}