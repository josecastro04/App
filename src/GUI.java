import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    private JFrame jFrame;
    private JPanel jPanel;


    // This is the constructor of the class
    public GUI(String frameName, int width, int height){
        this.jFrame =  new JFrame(frameName);
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jFrame.setSize(width, height);
        this.jFrame.setLocationRelativeTo(null);

    }

    // This method is used to add a button to the frame
    private JButton addButton(String nameButton, java.awt.event.ActionListener event){
        JButton button = new JButton(nameButton);
        button.setPreferredSize(new Dimension(200, 30));
        button.addActionListener(event);
        return button;
    }

    // This method is used to configure the login frame
    public void configLogin(GUI createAccFrame, GUI appFrame, User user){
        this.jPanel = new JPanel();

        this.jPanel.setLayout(new BoxLayout(this.jPanel, BoxLayout.Y_AXIS));

        createTextFieldsAndLabels(new String[] {"Email","Password"});

        createButtons(new String[] {"Login", "Create Account"}, new ActionListener[] {
                //
                e -> {
                    if(checkLogin(user)){
                        this.jFrame.dispose();
                        createAccFrame.jFrame.dispose();
                        appFrame.jFrame.setVisible(true);
                    }
                },
                // This event is used to go to the create account frame
                e -> {
                    this.jFrame.setVisible(false);
                    createAccFrame.jFrame.setVisible(true);
                },
        });

        this.jFrame.setVisible(true);

        this.jFrame.add(this.jPanel);

    }

    // This method is used to configure the create account frame
    public void configCreateAcc(GUI loginFrame, GUI appFrame, User user){
        this.jPanel = new JPanel();

        this.jPanel.setLayout(new BoxLayout(this.jPanel, BoxLayout.Y_AXIS));

        createTextFieldsAndLabels(new String[] {"Nome", "Email", "Password"});

        createButtons(new String[] {"Create Account", "Login"}, new ActionListener[]{
                // This event is used to create the user account
                e -> {
                    if(createUser(user)){
                        this.jFrame.dispose();
                        loginFrame.jFrame.dispose();
                        appFrame.jFrame.setVisible(true);
                    }
                },
                // This event is used to go back to the login frame
                e -> {
                    this.jFrame.setVisible(false);
                    loginFrame.jFrame.setVisible(true);
                }
        });

        this.jFrame.add(this.jPanel);
    }

    // This method is used to create the text fields and labels for the login and create account frames
    private void createTextFieldsAndLabels(String[] labelAndTextFieldName){
        for(String s : labelAndTextFieldName){
            JLabel label = new JLabel(s);
            label.setPreferredSize(new Dimension(this.jFrame.getWidth(), 25));
            this.jPanel.add(label);
            if(s.equals("Password")){
                JPasswordField passwordField = new JPasswordField();
                passwordField.setPreferredSize(new Dimension(this.jFrame.getWidth(), 50));
                passwordField.setEchoChar('*');
                this.jPanel.add(passwordField);
                continue;
            }
            JTextField textField = new JTextField();
            textField.setPreferredSize(new Dimension(this.jFrame.getWidth(), 50));
            this.jPanel.add(textField);
        }
    }

    // This method is used to create the buttons
    private void createButtons(String[] buttonsNames, java.awt.event.ActionListener[] events){
        for(int i = 0; i < buttonsNames.length; i++){
            this.jPanel.add(addButton(buttonsNames[i], events[i]));
        }
    }

    // This method is used to check if the user is already registered
    private boolean checkLogin(User user){

        DataBase database = new DataBase();

        ArrayList<JTextField> textFields = getTextFields();
        String[] text = new String[2];
        int i = 0;
        for(JTextField textField : textFields){
            text[i++] = textField.getText();
        }

        if(text[0].isEmpty() || text[1].isEmpty()){
            configErrorFrame(new GUI("Error", 250, 100), "Error when trying to login.");
            database.closeConnection();
            return  false;
        }

        String[] userInfo =  database.searchInfoByEmail(text[0]);
        if(userInfo.length > 0 && userInfo[3].equals(user.hashPassword(text[1]))){
            user.setUserInfo(userInfo);
        }else{
            configErrorFrame(new GUI("Error", 250, 100), "Error when trying to login.");
            database.closeConnection();
            return false;
        }

        database.closeConnection();
        return true;
    }

    //This method is used to create the user
    private boolean createUser(User user){
        ArrayList<JTextField> textFields = getTextFields();
        String[] userInfo = new String[4];
        int i = 0;
        for(JTextField textField : textFields){
            userInfo[i++] = textField.getText();
        }

        userInfo[i - 1] = user.hashPassword(userInfo[i - 1]);

        DataBase database = new DataBase();
        userInfo = database.insertUserInfo(userInfo);
        if(userInfo == null){
            configErrorFrame(new GUI("Error", 250, 100), "Error when trying to create the account.");
            database.closeConnection();
            return false;
        }

        user.setUserInfo(userInfo);

        database.closeConnection();
        return true;

    }

    //This method is used to get the text fields from the frame
    private ArrayList<JTextField> getTextFields(){
        ArrayList<JTextField> textFields = new ArrayList<>();
        Component[] components = this.jPanel.getComponents();

        for (Component component : components){
            if( component instanceof JTextField){
                textFields.add((JTextField) component);
            }
        }

        return textFields;
    }

    //This method is used to configure the error frame
    private void configErrorFrame(GUI errorFrame, String err){
        errorFrame.jFrame.setVisible(true);
        errorFrame.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        errorFrame.jPanel = new JPanel();

        errorFrame.jPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel(err);
        errorFrame.jPanel.add(label, BorderLayout.NORTH);

        errorFrame.createButtons(new String[] {"OK"}, new ActionListener[]{
                e -> errorFrame.jFrame.dispose()

        });

        errorFrame.jFrame.add(errorFrame.jPanel);
    }

    //This method is used to configure the app frame
    public void configApp(User user){

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> fileList = new JList<>(listModel);

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(250, 100));

        JPanel listModelPanel = new JPanel();
        listModelPanel.setLayout(new BorderLayout());

        listModelPanel.add(scrollPane, BorderLayout.CENTER);
        this.jFrame.add(listModelPanel);

        this.jPanel = new JPanel();
        this.jPanel.setLayout(new GridLayout(5, 1));

        createButtons(new String[] {"Upload File","Download File","Remove File", "Load Files", "See content"}, new ActionListener[]{
                //This event is used to upload the file
                e -> {
                    String[] files = readFileNames();

                    Files[] file = new Files[files.length];
                    for(int i = 0; i < files.length; i++){
                        if(!listModel.contains(file)){
                            file[i] = new Files();
                            if(file[i].readFile(files[i])){
                                file[i].setUser_id(user.getUserId());
                                listModel.addElement(file[i].getFile_name());
                            }else{
                                configErrorFrame(new GUI("Error", 250, 100), "Error when trying to upload the file. File: " + file[i].getFile_name());
                            }
                        }
                    }

                    DataBase dataBase = new DataBase();
                    for(Files f : file){
                        dataBase.insertFile(f);
                    }
                    dataBase.closeConnection();
                },
                //This event is used to download the file
                e -> {
                    Files file = new Files();
                    int index = fileList.getSelectedIndex();

                    if (index == -1){
                        configErrorFrame(new GUI("Error", 250, 100), "Select a file to download.");
                        return;
                    }

                    file.setFile_name(listModel.get(index));

                    DataBase dataBase1 = new DataBase();
                    dataBase1.searchFileByFileName(file);
                    dataBase1.closeConnection();

                    if(file.writeFile()) {
                        configErrorFrame(new GUI("Success", 250, 100), "File downloaded successfully.");
                    }else{
                        configErrorFrame(new GUI("Error", 250, 100), "Error when trying to download the file.");
                    }
                },
                //This event is used to remove the file
                e -> {
                    int index = fileList.getSelectedIndex();

                    if (index == -1){
                        configErrorFrame(new GUI("Error", 250, 100), "Select a file to remove.");
                        return;
                    }

                    String fileName = listModel.get(index);
                    listModel.remove(index);

                    DataBase dataBase1 = new DataBase();
                    dataBase1.deleteFile(fileName);
                    dataBase1.closeConnection();
                },
                //This event is used to load the files
                e -> listFilesByUserID(listModel, user.getUserId()),
                //This event is used to see the content of the file
                e -> {
                    int index = fileList.getSelectedIndex();

                    if (index == -1){
                        configErrorFrame(new GUI("Error", 250, 100), "Select a file to see the content.");
                        return;
                    }

                    Files file = new Files();
                    file.setFile_name(listModel.get(index));

                    configSeeContentFrame(new GUI("File Content", 400, 600), file);
                }
        });
        this.jFrame.add(this.jPanel, BorderLayout.EAST);
    }

    //This method is used to list the files from the user
    private void listFilesByUserID(DefaultListModel<String> listModel, int id){
        DataBase dataBase = new DataBase();
        List<String> files = dataBase.searchFilesByUserID(id);
        dataBase.closeConnection();

        listModel.clear();

        for(String file : files){
            listModel.addElement(file);
        }
    }

    private String[] readFileNames(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(null);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);

        if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            String[] fileNames = new String[fileChooser.getSelectedFiles().length];
            for(int i = 0; i < fileChooser.getSelectedFiles().length; i++){
                fileNames[i] = fileChooser.getSelectedFiles()[i].getAbsolutePath();
            }
            return fileNames;
        }
        return new String[]{};
    }

    private void configSeeContentFrame(GUI seeContentFrame, Files file){
        seeContentFrame.jFrame.setVisible(true);
        seeContentFrame.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBar(scrollPane.createVerticalScrollBar());

        DataBase dataBase = new DataBase();
        dataBase.searchFileByFileName(file);
        textArea.setText(new String(file.getContent()));
        dataBase.closeConnection();
        textArea.setEditable(false);

        panel.add(scrollPane);
        seeContentFrame.jFrame.add(panel);
    }
}
