public class Main {
    public static void main(String[] args) {
        User user = new User();

        GUI loginFrame = new GUI("Login", 300, 250);
        GUI createAccFrame = new GUI("Create Account", 300, 250);
        GUI appFrame =  new GUI("App", 500, 500);
        loginFrame.configLogin(createAccFrame, appFrame, user);
        createAccFrame.configCreateAcc(loginFrame, appFrame, user);
        appFrame.configApp(user);
    }
}
