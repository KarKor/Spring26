import java.util.ArrayList;
import java.util.List;

public class Authentication {

    private final IUserRepository userRepository;

    public Authentication(IUserRepository userRepository) {
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

    public boolean register(String login, String password){
        List<String> logins=userRepository.logins();
        for(String log : logins){
            System.out.println("This login is already uin use");
            if(login.equals(log)) return false;
        }
        userRepository.add(new User(login,Authentication.hashPassword(password),Role.USER));
        System.out.println("Registered successfully");
        return true;
    }

    public static String hashPassword(String password) {
        return Hasher.hashPassword(password);
    }
}