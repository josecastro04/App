import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public class Files {
    private int user_id;
    private String file_name;
    private byte[] content = new byte[]{};
    private long size;

    public int getUser_id(){
        return this.user_id;
    }

    public String getFile_name(){
        return this.file_name;
    }

    public byte[] getContent() {
        return this.content;
    }

    public long getSize() {
        return this.size;
    }

    public void setUser_id(int id){
        this.user_id = id;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setContent(byte[] content) {
        this.content = Arrays.copyOf(content, content.length);
    }

    public void setSize(long size) {
        this.size = size;
    }

    //This method is used to read the file
    public boolean readFile(String file_name){
        String[] path = file_name.split("/");
        this.file_name = path[path.length - 2] + "/" + path[path.length - 1];
        try{
            File file = new File(file_name);
            FileInputStream fileInputStream = new FileInputStream(file);
            this.content = fileInputStream.readAllBytes();
            long bytes = file.length();
            if (bytes <= 0){
                fileInputStream.close();
                return false;
            }
            this.size = bytes;
            fileInputStream.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //This method is used to write the file
    public boolean writeFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            String path = fileChooser.getSelectedFile().getAbsolutePath();

            try{
                FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + this.file_name.split("/")[1]);
                fileOutputStream.write(this.content);
                fileOutputStream.close();
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
