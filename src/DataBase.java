import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private Connection connection;
    private String url = System.getenv("URL");
    private String user = System.getenv("USER");
    private String password = System.getenv("PASSWORD");

    public DataBase() {
        try{
            this.connection = DriverManager.getConnection(this.url + "user=" + this.user +"&password=" + this.password);
            Class.forName("com.mysql.cj.jdbc.Driver");
            if(connection != null){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try{
            this.connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public String[] insertUserInfo(String[] info){
        try{

            for(int i = 0; i < info.length - 1; i++){
                if(info[i].equals("")){
                    return new String[]{};
                }
            }
            PreparedStatement statement = this.connection.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ? ,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, info[0]);
            statement.setString(2, info[1]);
            statement.setString(3, info[2]);

            int linesAffected = statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            System.arraycopy(info, 0, info, 1, 3);

            if(rs.next()){
                if(linesAffected > 0){
                     info[0] = rs.getString(1);
                     rs.close();
                     statement.close();
                     return info;
                }
            }
            rs.close();
            statement.close();

            return new String[]{};

        }catch (SQLException e){
            e.printStackTrace();
        }
        return new String[]{};
    }

    public String[] searchInfoByEmail(String email){
        try{
            PreparedStatement statement = this.connection.prepareStatement("SELECT user_id, name, email, password from users where email = ?");
            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();

            String[] info = new String[4];

            while(rs.next()){
                info[0] = String.valueOf(rs.getInt("user_id"));
                info[1] = rs.getString("name");
                info[2] = rs.getString("email");
                info[3] = rs.getString("password");
            }

            statement.close();
            rs.close();

            return info;

        }catch (SQLException e){
            e.printStackTrace();
        }

        return new String[]{};
    }

    public boolean insertFile(Files file){
        try{
            if(file.getSize() <= 0){
                return false;
            }

            PreparedStatement statement = this.connection.prepareStatement("INSERT INTO files (users_id, file_name, content, size) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, file.getUser_id());
            statement.setString(2, file.getFile_name());
            statement.setBytes(3, file.getContent());
            statement.setLong(4, file.getSize());

            int linesAffected = statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()){
                if(linesAffected > 0){
                    rs.close();
                    statement.close();
                    return true;
                }
            }

            rs.close();
            statement.close();
            return false;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void searchFileByFileName(Files file){
        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT content, size from files where file_name = ?");
            statement.setString(1, file.getFile_name());

            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                file.setContent(rs.getBytes(1));
                file.setSize(rs.getLong(2));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<String> searchFilesByUserID(int userID){
        try{
            List<String> listModel = new ArrayList<>();
            PreparedStatement statement = this.connection.prepareStatement("SELECT file_name from files where users_id = ?");
            statement.setInt(1, userID);

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                listModel.add(rs.getString(1));
            }

            return listModel;
        }catch (Exception e){
            e.printStackTrace();
        }

        return new ArrayList<String>();
    }

    public boolean deleteFile(String fileName){
        try{
            PreparedStatement statement = this.connection.prepareStatement("delete from files where file_name = ?");
            statement.setString(1, fileName);

            boolean executed = statement.execute();

            statement.close();

            return executed;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
