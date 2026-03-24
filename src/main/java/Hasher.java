import org.apache.commons.codec.digest.DigestUtils;

public class Hasher {
    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
