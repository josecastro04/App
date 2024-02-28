import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private byte[] salt = Base64.getDecoder().decode(System.getenv("SALT"));

    public void setUserInfo(String[] info){
        this.userId = Integer.parseInt(info[0]);
        this.name = info[1];
        this.email = info[2];
        this.password = info[3];
    }

    public int getUserId() {
        return this.userId;
    }

    public String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();
            md.update(this.salt);

            byte[] hash = md.digest(password.getBytes());

            StringBuilder string = new StringBuilder();
            for(byte hashBYte : hash){
                String hex = Integer.toHexString(0xff & hashBYte);
                if(hex.length() == 1){
                    string.append('0');
                }
                string.append(hex);
            }
            return string.toString();

        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return "";
    }
}
